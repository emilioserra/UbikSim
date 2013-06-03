/*
 * Contact, UbikSimIDE has been developed by:
 * 
 * Juan A. Botía , juanbot@um.es
 * Pablo Campillo, pablocampillo@um.es
 * Francisco Campuzano, fjcampuzano@um.es
 * Emilio Serrano, emilioserra@um.es 
 * 
 * This file is part of UbikSimIDE and a modified version (on 10/02/2011) of 
 * Sweet Home 3D version 3.3, Copyright (c) 2005-2011 Emmanuel PUYBARET / eTeks.
 * 
 *     UbikSimIDE is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 * 
 *     UbikSimIDE is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 * 
 *     You should have received a copy of the GNU General Public License
 *     along with UbikSimIDE.  If not, see <http://www.gnu.org/licenses/>
 */
package ubik3d.viewcontroller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ubik3d.model.CatalogPieceOfFurniture;
import ubik3d.model.CollectionEvent;
import ubik3d.model.CollectionListener;
import ubik3d.model.FurnitureCatalog;
import ubik3d.model.SelectionEvent;
import ubik3d.model.SelectionListener;
import ubik3d.model.UserPreferences;


/**
 * A MVC controller for the furniture catalog.
 * @author Emmanuel Puybaret
 */
public class FurnitureCatalogController implements Controller {
  private final FurnitureCatalog        catalog;
  private final UserPreferences         preferences;
  private final ViewFactory             viewFactory;
  private final ContentManager          contentManager;
  private final List<SelectionListener> selectionListeners;
  private List<CatalogPieceOfFurniture> selectedFurniture;
  private View                          catalogView;


  /**
   * Creates a controller of the furniture catalog view.
   * @param catalog the furniture catalog of the application
   * @param viewFactory a factory able to create the furniture view managed by this controller
   */
  public FurnitureCatalogController(FurnitureCatalog catalog,
                                    ViewFactory viewFactory) {
    this(catalog, null, viewFactory, null);
  }

  /**
   * Creates a controller of the furniture catalog view.
   * @param catalog the furniture catalog of the application
   * @param preferences application user preferences
   * @param viewFactory a factory able to create the furniture view managed by this controller
   * @param contentManager content manager for furniture import
   */
  public FurnitureCatalogController(FurnitureCatalog catalog, 
                                    UserPreferences preferences,
                                    ViewFactory     viewFactory, 
                                    ContentManager  contentManager) {
    this.catalog = catalog;
    this.preferences = preferences;
    this.viewFactory = viewFactory;
    this.contentManager = contentManager;
    this.selectionListeners = new ArrayList<SelectionListener>();
    this.selectedFurniture  = Collections.emptyList();
    
    this.catalog.addFurnitureListener(new FurnitureCatalogChangeListener(this));
    if (preferences != null) {
      preferences.addPropertyChangeListener(UserPreferences.Property.FURNITURE_CATALOG_VIEWED_IN_TREE, 
          new FurnitureCatalogViewChangeListener(this));
    }
  }

  /**
   * Furniture catalog listener that deselects a piece removed from catalog.  
   */
  private static class FurnitureCatalogChangeListener implements CollectionListener<CatalogPieceOfFurniture> {
    private WeakReference<FurnitureCatalogController> furnitureCatalogController;
    
    public FurnitureCatalogChangeListener(FurnitureCatalogController furnitureCatalogController) {
      this.furnitureCatalogController = new WeakReference<FurnitureCatalogController>(furnitureCatalogController);
    }
    
    public void collectionChanged(CollectionEvent<CatalogPieceOfFurniture> ev) {
      // If controller was garbage collected, remove this listener from catalog
      final FurnitureCatalogController controller = this.furnitureCatalogController.get();
      if (controller == null) {
        ((FurnitureCatalog)ev.getSource()).removeFurnitureListener(this);
      } else if (ev.getType() == CollectionEvent.Type.DELETE) {
        controller.deselectPieceOfFurniture(ev.getItem());
      }
    }
  }

  /**
   * Preferences listener that reset view when furniture catalog view should change.  
   */
  private static class FurnitureCatalogViewChangeListener implements PropertyChangeListener {
    private WeakReference<FurnitureCatalogController> controller;

    public FurnitureCatalogViewChangeListener(FurnitureCatalogController controller) {
      this.controller = new WeakReference<FurnitureCatalogController>(controller);
    }
    
    public void propertyChange(PropertyChangeEvent ev) {
      // If home pane was garbage collected, remove this listener from preferences
      FurnitureCatalogController controller = this.controller.get();
      if (controller == null) {
        ((UserPreferences)ev.getSource()).removePropertyChangeListener(
            UserPreferences.Property.FURNITURE_CATALOG_VIEWED_IN_TREE, this);
      } else {
        // Forgot current view and create a new one at next getView call 
        controller.catalogView = null;
      }
    }
  }

  /**
   * Returns the view associated with this controller.
   */
  public View getView() {
    // Create view lazily only once it's needed
    if (this.catalogView == null) {
      this.catalogView = viewFactory.createFurnitureCatalogView(this.catalog, this.preferences, this);
    }
    return this.catalogView;
  }


  /**
   * Adds the selection <code>listener</code> in parameter to this controller.
   */
  public void addSelectionListener(SelectionListener listener) {
    this.selectionListeners.add(listener);
  }

  /**
   * Removes the selection <code>listener</code> in parameter from this controller.
   */
  public void removeSelectionListener(SelectionListener listener) {
    this.selectionListeners.remove(listener);
  }
  
  /**
   * Returns an unmodifiable list of the selected furniture in catalog.
   */
  public List<CatalogPieceOfFurniture> getSelectedFurniture() {
    return Collections.unmodifiableList(this.selectedFurniture);
  }
  
  /**
   * Updates the selected furniture in catalog and notifies listeners selection change.
   */
  public void setSelectedFurniture(List<CatalogPieceOfFurniture> selectedFurniture) {
    this.selectedFurniture = new ArrayList<CatalogPieceOfFurniture>(selectedFurniture);
    if (!this.selectionListeners.isEmpty()) {
      SelectionEvent selectionEvent = new SelectionEvent(this, getSelectedFurniture());
      // Work on a copy of selectionListeners to ensure a listener 
      // can modify safely listeners list
      SelectionListener [] listeners = this.selectionListeners.
        toArray(new SelectionListener [this.selectionListeners.size()]);
      for (SelectionListener listener : listeners) {
        listener.selectionChanged(selectionEvent);
      }
    }
  }

  /**
   * Removes <code>piece</code> from selected furniture.
   */
  private void deselectPieceOfFurniture(CatalogPieceOfFurniture piece) {
    int pieceSelectionIndex = this.selectedFurniture.indexOf(piece);
    if (pieceSelectionIndex != -1) {
      List<CatalogPieceOfFurniture> selectedItems = 
          new ArrayList<CatalogPieceOfFurniture>(getSelectedFurniture());
      selectedItems.remove(pieceSelectionIndex);
      setSelectedFurniture(selectedItems);
    }
  }

  /**
   * Displays the wizard that helps to change the selected piece of furniture. 
   */
  public void modifySelectedFurniture() {
    if (this.preferences != null) {
      if (this.selectedFurniture.size() > 0) {
        CatalogPieceOfFurniture piece = this.selectedFurniture.get(0);
        if (piece.isModifiable()) {
          AddedFurnitureSelector addedFurnitureListener = new AddedFurnitureSelector();
          this.preferences.getFurnitureCatalog().addFurnitureListener(addedFurnitureListener);
          new ImportedFurnitureWizardController(piece, this.preferences, 
              this.viewFactory, this.contentManager).displayView(getView());
          addedFurnitureListener.selectAddedFurniture();
          this.preferences.getFurnitureCatalog().removeFurnitureListener(addedFurnitureListener);
        }
      }
    }
  }

  /**
   * Listener that keeps track of the furniture added to catalog.
   */
  private class AddedFurnitureSelector implements CollectionListener<CatalogPieceOfFurniture> {
    private List<CatalogPieceOfFurniture> addedFurniture = new ArrayList<CatalogPieceOfFurniture>();

    public void collectionChanged(CollectionEvent<CatalogPieceOfFurniture> ev) {
      if (ev.getType() == CollectionEvent.Type.ADD) {
        this.addedFurniture.add(ev.getItem());
      }
    }
    
    public void selectAddedFurniture() {
      if (this.addedFurniture.size() > 0) {
        setSelectedFurniture(this.addedFurniture);
      }
    }
  }

  /**
   * Displays the wizard that helps to import furniture to catalog. 
   */
  public void importFurniture() {
    if (this.preferences != null) {
      AddedFurnitureSelector addedFurnitureListener = new AddedFurnitureSelector();
      this.preferences.getFurnitureCatalog().addFurnitureListener(addedFurnitureListener);
      new ImportedFurnitureWizardController(this.preferences, 
          this.viewFactory, this.contentManager).displayView(getView());
      addedFurnitureListener.selectAddedFurniture();
      this.preferences.getFurnitureCatalog().removeFurnitureListener(addedFurnitureListener);
    }
  }

  /**
   * Displays the wizard that helps to import furniture to catalog. 
   */
  private void importFurniture(String modelName) {
    if (this.preferences != null) {
      new ImportedFurnitureWizardController(modelName, this.preferences, 
          this.viewFactory, this.contentManager).displayView(getView());
    }
  }

  /**
   * Deletes selected catalog furniture. 
   */
  public void deleteSelection() {
    for (CatalogPieceOfFurniture piece : this.selectedFurniture) {
      if (piece.isModifiable()) {
        this.catalog.delete(piece);
      }
    }
  }

  /**
   * Adds dropped files to catalog.
   */
  public void dropFiles(List<String> importableModels) {
    AddedFurnitureSelector addedFurnitureListener = new AddedFurnitureSelector();
    this.preferences.getFurnitureCatalog().addFurnitureListener(addedFurnitureListener);
    // Import furniture
    for (String model : importableModels) {
      importFurniture(model);
    }
    addedFurnitureListener.selectAddedFurniture();
    this.preferences.getFurnitureCatalog().removeFurnitureListener(addedFurnitureListener);
  }
}
