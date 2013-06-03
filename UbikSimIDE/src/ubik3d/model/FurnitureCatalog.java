/*
 * Contact, UbikSimIDE has been developed by:
 * 
 * Juan A. Bot�a , juanbot@um.es
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
package ubik3d.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Furniture catalog.
 * @author Emmanuel Puybaret
 */
public abstract class FurnitureCatalog {
  private List<FurnitureCategory>       categories = new ArrayList<FurnitureCategory>();
  private boolean                       sorted;
  private final CollectionChangeSupport<CatalogPieceOfFurniture> furnitureChangeSupport = 
                             new CollectionChangeSupport<CatalogPieceOfFurniture>(this);

  /**
   * Returns the categories list sorted by name.
   * @return an unmodifiable list of categories.
   */
  public List<FurnitureCategory> getCategories() {
    checkCategoriesSorted();
    return Collections.unmodifiableList(this.categories);
  }

  /**
   * Checks categories are sorted.
   */
  private void checkCategoriesSorted() {
    if (!this.sorted) {
      Collections.sort(this.categories);
      this.sorted = true;
    }
  }

  /**
   * Returns the count of categories in this catalog.
   */
  public int getCategoriesCount() {
    return this.categories.size();
  }

  /**
   * Returns the category at a given <code>index</code>.
   */
  public FurnitureCategory getCategory(int index) {
    checkCategoriesSorted();
    return this.categories.get(index);
  }

  /**
   * Adds the furniture <code>listener</code> in parameter to this catalog.
   */
  public void addFurnitureListener(CollectionListener<CatalogPieceOfFurniture> listener) {
    this.furnitureChangeSupport.addCollectionListener(listener);
  }

  /**
   * Removes the furniture <code>listener</code> in parameter from this catalog.
   */
  public void removeFurnitureListener(CollectionListener<CatalogPieceOfFurniture> listener) {
    this.furnitureChangeSupport.removeCollectionListener(listener);
  }

  /**
   * Adds a category.
   * @param category the category to add.
   * @throws IllegalHomonymException if a category with same name as the one in
   *           parameter already exists in this catalog.
   */
  private void add(FurnitureCategory category) {
    if (this.categories.contains(category)) {
      throw new IllegalHomonymException(
          category.getName() + " already exists in catalog");
    }
    this.categories.add(category);
    this.sorted = false;
  }

  /**
   * Adds <code>piece</code> of a given <code>category</code> to this catalog.
   * Once the <code>piece</code> is added, furniture listeners added to this catalog will receive a
   * {@link CollectionListener#collectionChanged(CollectionEvent) collectionChanged}
   * notification.
   * @param category the category of the piece.
   * @param piece    a piece of furniture.
   */
  public void add(FurnitureCategory category, CatalogPieceOfFurniture piece) {
    int index = this.categories.indexOf(category);
    // If category doesn't exist yet, add it to categories
    if (index == -1) {
      category = new FurnitureCategory(category.getName());
      add(category);
    } else {
      category = this.categories.get(index);
    }    
    // Add current piece of furniture to category list
    category.add(piece);
    
    this.furnitureChangeSupport.fireCollectionChanged(piece, 
        Collections.binarySearch(category.getFurniture(), piece), CollectionEvent.Type.ADD);
  }

  /**
   * Deletes the <code>piece</code> from this catalog.
   * If then piece category is empty, it will be removed from the categories of this catalog. 
   * Once the <code>piece</code> is deleted, furniture listeners added to this catalog will receive a
   * {@link CollectionListener#collectionChanged(CollectionEvent) collectionChanged}
   * notification.
   * @param piece a piece of furniture in that category.
   */
  public void delete(CatalogPieceOfFurniture piece) {
    FurnitureCategory category = piece.getCategory();
    // Remove piece from its category
    if (category != null) {
      int pieceIndex = Collections.binarySearch(category.getFurniture(), piece);
      if (pieceIndex >= 0) {
        category.delete(piece);
        
        if (category.getFurnitureCount() == 0) {
          //  Make a copy of the list to avoid conflicts in the list returned by getCategories
          this.categories = new ArrayList<FurnitureCategory>(this.categories);
          this.categories.remove(category);
        }
        
        this.furnitureChangeSupport.fireCollectionChanged(piece, pieceIndex, CollectionEvent.Type.DELETE);
        return;
      }
    }

    throw new IllegalArgumentException("catalog doesn't contain piece " + piece.getName());
  }
}
