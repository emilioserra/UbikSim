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
package ubik3d.viewcontroller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.Vector;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import javax.swing.undo.UndoableEditSupport;

import ubik3d.model.Home;
import ubik3d.model.HomeTexture;
import ubik3d.model.Room;
import ubik3d.model.Selectable;
import ubik3d.model.UserPreferences;

/**
 * A MVC controller for room view.
 * @author Emmanuel Puybaret
 */
public class RoomController implements Controller {
  /**
   * The properties that may be edited by the view associated to this
   * controller.
   */
  public enum Property {
    NAME, TYPE, AREA_VISIBLE, FLOOR_VISIBLE, FLOOR_COLOR, FLOOR_PAINT, FLOOR_SHININESS, CEILING_VISIBLE, CEILING_COLOR, CEILING_PAINT, CEILING_SHININESS
  }

  /**
   * The possible values for {@linkplain #getFloorPaint() room paint type}.
   */
  public enum RoomPaint {
    COLORED, TEXTURED
  }

  public String []                    defaultRooms = {"Room", "Garage", "Garden", "Office", "BathRoom", "Kitchen",
      "LivingRoom", "Lab", "MeetingRoom", "Corridor", "BedRoom", "GeriatricBathRoom", "MaintenanceRoom"};
  public Vector<String>                 roomTypes;

  private final Home                  home;
  private final UserPreferences       preferences;
  private final ViewFactory           viewFactory;
  private final ContentManager        contentManager;
  private final UndoableEditSupport   undoSupport;
  private TextureChoiceController     floorTextureController;
  private TextureChoiceController     ceilingTextureController;
  private final PropertyChangeSupport propertyChangeSupport;
  private DialogView                  roomView;

  private String                      name;
  private String                      type;
  private Boolean                     areaVisible;
  private Boolean                     floorVisible;
  private Integer                     floorColor;
  private RoomPaint                   floorPaint;
  private Float                       floorShininess;
  private Boolean                     ceilingVisible;
  private Integer                     ceilingColor;
  private RoomPaint                   ceilingPaint;
  private Float                       ceilingShininess;

  /**
   * Creates the controller of room view with undo support.
   */
  public RoomController(final Home home, UserPreferences preferences, ViewFactory viewFactory,
                        ContentManager contentManager, UndoableEditSupport undoSupport) {
    this.home = home;
    this.preferences = preferences;
    this.viewFactory = viewFactory;
    this.contentManager = contentManager;
    this.undoSupport = undoSupport;
    this.propertyChangeSupport = new PropertyChangeSupport(this);

    roomTypes = new Vector<String>();    
    for (String roomName : defaultRooms) {
      roomTypes.add(roomName);
    }
    updateProperties();
  }

  /**
   * Returns the texture controller of the room floor.
   */
  public TextureChoiceController getFloorTextureController() {
    // Create sub controller lazily only once it's needed
    if (this.floorTextureController == null) {
      this.floorTextureController = new TextureChoiceController(this.preferences.getLocalizedString(
          RoomController.class, "floorTextureTitle"), this.preferences, this.viewFactory, this.contentManager);
      this.floorTextureController.addPropertyChangeListener(TextureChoiceController.Property.TEXTURE,
          new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent ev) {
              setFloorPaint(RoomPaint.TEXTURED);
            }
          });
    }
    return this.floorTextureController;
  }

  /**
   * Returns the texture controller of the room ceiling.
   */
  public TextureChoiceController getCeilingTextureController() {
    // Create sub controller lazily only once it's needed
    if (this.ceilingTextureController == null) {
      this.ceilingTextureController = new TextureChoiceController(this.preferences.getLocalizedString(
          RoomController.class, "ceilingTextureTitle"), this.preferences, this.viewFactory, this.contentManager);
      this.ceilingTextureController.addPropertyChangeListener(TextureChoiceController.Property.TEXTURE,
          new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent ev) {
              setCeilingPaint(RoomPaint.TEXTURED);
            }
          });
    }
    return this.ceilingTextureController;
  }

  /**
   * Returns the view associated with this controller.
   */
  public DialogView getView() {
    // Create view lazily only once it's needed
    if (this.roomView == null) {
      this.roomView = this.viewFactory.createRoomView(this.preferences, this);
    }
    return this.roomView;
  }

  /**
   * Displays the view controlled by this controller.
   */
  public void displayView(View parentView) {
    getView().displayView(parentView);
  }

  /**
   * Adds the property change <code>listener</code> in parameter to this
   * controller.
   */
  public void addPropertyChangeListener(Property property, PropertyChangeListener listener) {
    this.propertyChangeSupport.addPropertyChangeListener(property.name(), listener);
  }

  /**
   * Removes the property change <code>listener</code> in parameter from this
   * controller.
   */
  public void removePropertyChangeListener(Property property, PropertyChangeListener listener) {
    this.propertyChangeSupport.removePropertyChangeListener(property.name(), listener);
  }

  /**
   * Updates edited properties from selected rooms in the home edited by this
   * controller.
   */
  protected void updateProperties() {
    List<Room> selectedRooms = Home.getRoomsSubList(this.home.getSelectedItems());
    if (selectedRooms.isEmpty()) {
      setAreaVisible(null); // Nothing to edit
      setFloorColor(null);
      getFloorTextureController().setTexture(null);
      setFloorPaint(null);
      setFloorShininess(null);
      setCeilingColor(null);
      getCeilingTextureController().setTexture(null);
      setCeilingPaint(null);
      setCeilingShininess(null);
    } else {
      // Search the common properties among selected rooms
      Room firstRoom = selectedRooms.get(0);

      String type = firstRoom.getType();
      if (type != null) {
        for (int i = 1; i < selectedRooms.size(); i++) {
          if (!type.equals(selectedRooms.get(i).getType())) {
            type = null;
            break;
          }
        }
      }
      setType(type);

      String name = firstRoom.getName();
      if (name != null) {
        for (int i = 1; i < selectedRooms.size(); i++) {
          if (!name.equals(selectedRooms.get(i).getName())) {
            name = null;
            break;
          }
        }
      }
      setName(name);

      // Search the common areaVisible value among rooms
      Boolean areaVisible = firstRoom.isAreaVisible();
      for (int i = 1; i < selectedRooms.size(); i++) {
        if (areaVisible != selectedRooms.get(i).isAreaVisible()) {
          areaVisible = null;
          break;
        }
      }
      setAreaVisible(areaVisible);

      // Search the common floorVisible value among rooms
      Boolean floorVisible = firstRoom.isFloorVisible();
      for (int i = 1; i < selectedRooms.size(); i++) {
        if (floorVisible != selectedRooms.get(i).isFloorVisible()) {
          floorVisible = null;
          break;
        }
      }
      setFloorVisible(floorVisible);

      // Search the common floor color among rooms
      Integer floorColor = firstRoom.getFloorColor();
      if (floorColor != null) {
        for (int i = 1; i < selectedRooms.size(); i++) {
          if (!floorColor.equals(selectedRooms.get(i).getFloorColor())) {
            floorColor = null;
            break;
          }
        }
      }
      setFloorColor(floorColor);

      // Search the common floor texture among rooms
      HomeTexture floorTexture = firstRoom.getFloorTexture();
      if (floorTexture != null) {
        for (int i = 1; i < selectedRooms.size(); i++) {
          if (!floorTexture.equals(selectedRooms.get(i).getFloorTexture())) {
            floorTexture = null;
            break;
          }
        }
      }
      getFloorTextureController().setTexture(floorTexture);

      if (floorColor != null) {
        setFloorPaint(RoomPaint.COLORED);
      } else if (floorTexture != null) {
        setFloorPaint(RoomPaint.TEXTURED);
      } else {
        setFloorPaint(null);
      }

      // Search the common floor shininess among rooms
      Float floorShininess = firstRoom.getFloorShininess();
      for (int i = 1; i < selectedRooms.size(); i++) {
        if (!floorShininess.equals(selectedRooms.get(i).getFloorShininess())) {
          floorShininess = null;
          break;
        }
      }
      setFloorShininess(floorShininess);

      // Search the common ceilingVisible value among rooms
      Boolean ceilingVisible = firstRoom.isCeilingVisible();
      for (int i = 1; i < selectedRooms.size(); i++) {
        if (ceilingVisible != selectedRooms.get(i).isCeilingVisible()) {
          ceilingVisible = null;
          break;
        }
      }
      setCeilingVisible(ceilingVisible);

      // Search the common ceiling color among rooms
      Integer ceilingColor = firstRoom.getCeilingColor();
      if (ceilingColor != null) {
        for (int i = 1; i < selectedRooms.size(); i++) {
          if (!ceilingColor.equals(selectedRooms.get(i).getCeilingColor())) {
            ceilingColor = null;
            break;
          }
        }
      }
      setCeilingColor(ceilingColor);

      // Search the common ceiling texture among rooms
      HomeTexture ceilingTexture = firstRoom.getCeilingTexture();
      if (ceilingTexture != null) {
        for (int i = 1; i < selectedRooms.size(); i++) {
          if (!ceilingTexture.equals(selectedRooms.get(i).getCeilingTexture())) {
            ceilingTexture = null;
            break;
          }
        }
      }
      getCeilingTextureController().setTexture(ceilingTexture);

      if (ceilingColor != null) {
        setCeilingPaint(RoomPaint.COLORED);
      } else if (ceilingTexture != null) {
        setCeilingPaint(RoomPaint.TEXTURED);
      } else {
        setCeilingPaint(null);
      }

      // Search the common ceiling shininess among rooms
      Float ceilingShininess = firstRoom.getCeilingShininess();
      for (int i = 1; i < selectedRooms.size(); i++) {
        if (!ceilingShininess.equals(selectedRooms.get(i).getCeilingShininess())) {
          ceilingShininess = null;
          break;
        }
      }
      setCeilingShininess(ceilingShininess);
    }
  }

  /**
   * Sets the edited type.
   */
  public void setType(String type) {
    if (type != null && !type.equals(this.type)) {
      String oldType = this.type;
      this.type = type;
      if(!roomTypes.contains(type))
        roomTypes.add(type);
      this.propertyChangeSupport.firePropertyChange(Property.TYPE.name(), oldType, type);
    }
  }

  public Vector<String> getRoomTypes() {
    return this.roomTypes;
  }

  /**
   * Sets the edited name.
   */
  public void setName(String name) {
    if (name != this.name) {
      String oldName = this.name;
      this.name = name;
      this.propertyChangeSupport.firePropertyChange(Property.NAME.name(), oldName, name);
    }
  }

  /**
   * Returns the edited name.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Returns the edited name.
   */
  public String getType() {
    return this.type;
  }

  /**
   * Sets whether room area is visible or not.
   */
  public void setAreaVisible(Boolean areaVisible) {
    if (areaVisible != this.areaVisible) {
      Boolean oldAreaVisible = this.areaVisible;
      this.areaVisible = areaVisible;
      this.propertyChangeSupport.firePropertyChange(Property.AREA_VISIBLE.name(), oldAreaVisible, areaVisible);
    }
  }

  /**
   * Returns whether room area is visible or not.
   */
  public Boolean getAreaVisible() {
    return this.areaVisible;
  }

  /**
   * Sets whether room floor is visible or not.
   */
  public void setFloorVisible(Boolean floorVisible) {
    if (floorVisible != this.floorVisible) {
      Boolean oldFloorVisible = this.floorVisible;
      this.floorVisible = floorVisible;
      this.propertyChangeSupport.firePropertyChange(Property.FLOOR_VISIBLE.name(), oldFloorVisible, floorVisible);
    }
  }

  /**
   * Returns whether room floor is visible or not.
   */
  public Boolean getFloorVisible() {
    return this.floorVisible;
  }

  /**
   * Sets the edited color of the floor.
   */
  public void setFloorColor(Integer floorColor) {
    if (floorColor != this.floorColor) {
      Integer oldFloorColor = this.floorColor;
      this.floorColor = floorColor;
      this.propertyChangeSupport.firePropertyChange(Property.FLOOR_COLOR.name(), oldFloorColor, floorColor);

      setFloorPaint(RoomPaint.COLORED);
    }
  }

  /**
   * Returns the edited color of the floor.
   */
  public Integer getFloorColor() {
    return this.floorColor;
  }

  /**
   * Sets whether the floor is colored, textured or unknown painted.
   */
  public void setFloorPaint(RoomPaint floorPaint) {
    if (floorPaint != this.floorPaint) {
      RoomPaint oldFloorPaint = this.floorPaint;
      this.floorPaint = floorPaint;
      this.propertyChangeSupport.firePropertyChange(Property.FLOOR_PAINT.name(), oldFloorPaint, floorPaint);
    }
  }

  /**
   * Returns whether the floor is colored, textured or unknown painted.
   */
  public RoomPaint getFloorPaint() {
    return this.floorPaint;
  }

  /**
   * Sets the edited shininess of the floor.
   */
  public void setFloorShininess(Float floorShininess) {
    if (floorShininess != this.floorShininess) {
      Float oldFloorShininess = this.floorShininess;
      this.floorShininess = floorShininess;
      this.propertyChangeSupport.firePropertyChange(Property.FLOOR_SHININESS.name(), oldFloorShininess, floorShininess);
    }
  }

  /**
   * Returns the edited shininess of the floor.
   */
  public Float getFloorShininess() {
    return this.floorShininess;
  }

  /**
   * Sets whether room ceiling is visible or not.
   */
  public void setCeilingVisible(Boolean ceilingCeilingVisible) {
    if (ceilingCeilingVisible != this.ceilingVisible) {
      Boolean oldCeilingVisible = this.ceilingVisible;
      this.ceilingVisible = ceilingCeilingVisible;
      this.propertyChangeSupport.firePropertyChange(Property.CEILING_VISIBLE.name(), oldCeilingVisible,
          ceilingCeilingVisible);
    }
  }

  /**
   * Returns whether room ceiling is ceilingCeilingVisible or not.
   */
  public Boolean getCeilingVisible() {
    return this.ceilingVisible;
  }

  /**
   * Sets the edited color of the ceiling.
   */
  public void setCeilingColor(Integer ceilingColor) {
    if (ceilingColor != this.ceilingColor) {
      Integer oldCeilingColor = this.ceilingColor;
      this.ceilingColor = ceilingColor;
      this.propertyChangeSupport.firePropertyChange(Property.CEILING_COLOR.name(), oldCeilingColor, ceilingColor);

      setCeilingPaint(RoomPaint.COLORED);
    }
  }

  /**
   * Returns the edited color of the ceiling.
   */
  public Integer getCeilingColor() {
    return this.ceilingColor;
  }

  /**
   * Sets whether the ceiling is colored, textured or unknown painted.
   */
  public void setCeilingPaint(RoomPaint ceilingPaint) {
    if (ceilingPaint != this.ceilingPaint) {
      RoomPaint oldCeilingPaint = this.ceilingPaint;
      this.ceilingPaint = ceilingPaint;
      this.propertyChangeSupport.firePropertyChange(Property.CEILING_PAINT.name(), oldCeilingPaint, ceilingPaint);
    }
  }

  /**
   * Returns whether the ceiling is colored, textured or unknown painted.
   */
  public RoomPaint getCeilingPaint() {
    return this.ceilingPaint;
  }

  /**
   * Sets the edited shininess of the ceiling.
   */
  public void setCeilingShininess(Float ceilingShininess) {
    if (ceilingShininess != this.ceilingShininess) {
      Float oldCeilingShininess = this.ceilingShininess;
      this.ceilingShininess = ceilingShininess;
      this.propertyChangeSupport.firePropertyChange(Property.CEILING_SHININESS.name(), oldCeilingShininess,
          ceilingShininess);
    }
  }

  /**
   * Returns the edited shininess of the ceiling.
   */
  public Float getCeilingShininess() {
    return this.ceilingShininess;
  }

  /**
   * Controls the modification of selected rooms in edited home.
   */
  public void modifyRooms() {
    List<Selectable> oldSelection = this.home.getSelectedItems();
    List<Room> selectedRooms = Home.getRoomsSubList(oldSelection);
    if (!selectedRooms.isEmpty()) {
      String name = getName();
      String type = getType();
      Boolean areaVisible = getAreaVisible();
      Boolean floorVisible = getFloorVisible();
      Integer floorColor = getFloorPaint() == RoomPaint.COLORED
          ? getFloorColor()
          : null;
      HomeTexture floorTexture = getFloorPaint() == RoomPaint.TEXTURED
          ? getFloorTextureController().getTexture()
          : null;
      Float floorShininess = getFloorShininess();
      Boolean ceilingVisible = getCeilingVisible();
      Integer ceilingColor = getCeilingPaint() == RoomPaint.COLORED
          ? getCeilingColor()
          : null;
      HomeTexture ceilingTexture = getCeilingPaint() == RoomPaint.TEXTURED
          ? getCeilingTextureController().getTexture()
          : null;
      Float ceilingShininess = getCeilingShininess();

      // Create an array of modified rooms with their current properties values
      ModifiedRoom [] modifiedRooms = new ModifiedRoom [selectedRooms.size()];
      for (int i = 0; i < modifiedRooms.length; i++) {
        modifiedRooms [i] = new ModifiedRoom(selectedRooms.get(i));
      }
      // Apply modification
      doModifyRooms(modifiedRooms, name, type, areaVisible, floorVisible, floorColor, floorTexture, floorShininess,
          ceilingVisible, ceilingColor, ceilingTexture, ceilingShininess);
      if (this.undoSupport != null) {
        UndoableEdit undoableEdit = new RoomsModificationUndoableEdit(this.home, this.preferences, oldSelection,
            modifiedRooms, name, type, areaVisible, floorColor, floorTexture, floorVisible, floorShininess,
            ceilingColor, ceilingTexture, ceilingVisible, ceilingShininess);
        this.undoSupport.postEdit(undoableEdit);
      }
    }
  }

  /**
   * Undoable edit for rooms modification. This class isn't anonymous to avoid
   * being bound to controller and its view.
   */
  private static class RoomsModificationUndoableEdit extends AbstractUndoableEdit {
    private final Home             home;
    private final UserPreferences  preferences;
    private final List<Selectable> oldSelection;
    private final ModifiedRoom []  modifiedRooms;
    private final String           type;
    private final String           name;
    private final Boolean          areaVisible;
    private final Integer          floorColor;
    private final HomeTexture      floorTexture;
    private final Boolean          floorVisible;
    private final Float            floorShininess;
    private final Integer          ceilingColor;
    private final HomeTexture      ceilingTexture;
    private final Boolean          ceilingVisible;
    private final Float            ceilingShininess;

    private RoomsModificationUndoableEdit(Home home, UserPreferences preferences, List<Selectable> oldSelection,
                                          ModifiedRoom [] modifiedRooms, String name, String type, Boolean areaVisible,
                                          Integer floorColor, HomeTexture floorTexture, Boolean floorVisible,
                                          Float floorShininess, Integer ceilingColor, HomeTexture ceilingTexture,
                                          Boolean ceilingVisible, Float ceilingShininess) {
      this.home = home;
      this.preferences = preferences;
      this.oldSelection = oldSelection;
      this.modifiedRooms = modifiedRooms;
      this.type = type;
      this.name = name;
      this.areaVisible = areaVisible;
      this.floorColor = floorColor;
      this.floorTexture = floorTexture;
      this.floorVisible = floorVisible;
      this.floorShininess = floorShininess;
      this.ceilingColor = ceilingColor;
      this.ceilingTexture = ceilingTexture;
      this.ceilingVisible = ceilingVisible;
      this.ceilingShininess = ceilingShininess;
    }

    @Override
    public void undo() throws CannotUndoException {
      super.undo();
      undoModifyRooms(this.modifiedRooms);
      this.home.setSelectedItems(this.oldSelection);
    }

    @Override
    public void redo() throws CannotRedoException {
      super.redo();
      doModifyRooms(this.modifiedRooms, this.name, this.type, this.areaVisible, this.floorVisible, this.floorColor,
          this.floorTexture, this.floorShininess, this.ceilingVisible, this.ceilingColor, this.ceilingTexture,
          this.ceilingShininess);
      this.home.setSelectedItems(this.oldSelection);
    }

    @Override
    public String getPresentationName() {
      return this.preferences.getLocalizedString(RoomController.class, "undoModifyRoomsName");
    }
  }

  /**
   * Modifies rooms properties with the values in parameter.
   */
  private static void doModifyRooms(ModifiedRoom [] modifiedRooms, String name, String type, Boolean areaVisible,
                                    Boolean floorVisible, Integer floorColor, HomeTexture floorTexture,
                                    Float floorShininess, Boolean ceilingVisible, Integer ceilingColor,
                                    HomeTexture ceilingTexture, Float ceilingShininess) {
    for (ModifiedRoom modifiedRoom : modifiedRooms) {
      Room room = modifiedRoom.getRoom();
      if (type != null) {
        room.setType(type);
      }
      if (name != null) {
        room.setName(name);
      }
      if (areaVisible != null) {
        room.setAreaVisible(areaVisible);
      }
      if (floorVisible != null) {
        room.setFloorVisible(floorVisible);
      }
      if (floorTexture != null) {
        room.setFloorTexture(floorTexture);
        room.setFloorColor(null);
      } else if (floorColor != null) {
        room.setFloorColor(floorColor);
        room.setFloorTexture(null);
      }
      if (floorShininess != null) {
        room.setFloorShininess(floorShininess);
      }
      if (ceilingVisible != null) {
        room.setCeilingVisible(ceilingVisible);
      }
      if (ceilingTexture != null) {
        room.setCeilingTexture(ceilingTexture);
        room.setCeilingColor(null);
      } else if (ceilingColor != null) {
        room.setCeilingColor(ceilingColor);
        room.setCeilingTexture(null);
      }
      if (ceilingShininess != null) {
        room.setCeilingShininess(ceilingShininess);
      }
    }
  }

  /**
   * Restores room properties from the values stored in
   * <code>modifiedRooms</code>.
   */
  private static void undoModifyRooms(ModifiedRoom [] modifiedRooms) {
    for (ModifiedRoom modifiedRoom : modifiedRooms) {
      modifiedRoom.reset();
    }
  }

  /**
   * Stores the current properties values of a modified room.
   */
  private static final class ModifiedRoom {
    private final Room        room;
    private final String      type;
    private final String      name;
    private final boolean     areaVisible;
    private final boolean     floorVisible;
    private final Integer     floorColor;
    private final HomeTexture floorTexture;
    private final float       floorShininess;
    private final boolean     ceilingVisible;
    private final Integer     ceilingColor;
    private final HomeTexture ceilingTexture;
    private final float       ceilingShininess;

    public ModifiedRoom(Room room) {
      this.room = room;
      this.type = room.getType();
      this.name = room.getName();
      this.areaVisible = room.isAreaVisible();
      this.floorVisible = room.isFloorVisible();
      this.floorColor = room.getFloorColor();
      this.floorTexture = room.getFloorTexture();
      this.floorShininess = room.getFloorShininess();
      this.ceilingVisible = room.isCeilingVisible();
      this.ceilingColor = room.getCeilingColor();
      this.ceilingTexture = room.getCeilingTexture();
      this.ceilingShininess = room.getCeilingShininess();
    }

    public Room getRoom() {
      return this.room;
    }

    public void reset() {
      this.room.setType(this.type);
      this.room.setName(this.name);
      this.room.setAreaVisible(this.areaVisible);
      this.room.setFloorVisible(this.floorVisible);
      this.room.setFloorColor(this.floorColor);
      this.room.setFloorTexture(this.floorTexture);
      this.room.setFloorShininess(this.floorShininess);
      this.room.setCeilingVisible(this.ceilingVisible);
      this.room.setCeilingColor(this.ceilingColor);
      this.room.setCeilingTexture(this.ceilingTexture);
      this.room.setCeilingShininess(this.ceilingShininess);
    }
  }
}
