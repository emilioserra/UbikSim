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
package ubik3d.model;

/**
 * A door or a window in {@linkplain Home home}.
 * @author Emmanuel Puybaret
 * @since  1.7
 */
public class HomeDoorOrWindow extends HomePieceOfFurniture implements DoorOrWindow {
  private static final long serialVersionUID = 1L;

  private final float   wallThickness;
  private final float   wallDistance;
  private final Sash [] sashes;
  private boolean boundToWall;

  /**
   * Creates a home door or window from an existing one.
   * @param doorOrWindow the door or window from which data are copied
   */
  public HomeDoorOrWindow(DoorOrWindow doorOrWindow) {
    super(doorOrWindow);
    this.wallThickness = doorOrWindow.getWallThickness();
    this.wallDistance = doorOrWindow.getWallDistance();
    this.sashes = doorOrWindow.getSashes();
  }

  /**
   * Returns the default thickness of the wall in which this door or window should be placed.
   * @return a value in percentage of the depth of the door or the window.
   */
  public float getWallThickness() {
    return this.wallThickness;
  }
  
  /**
   * Returns the default distance that should lie at the back side of this door or window.
   * @return a distance in percentage of the depth of the door or the window.
   */
  public float getWallDistance() {
    return this.wallDistance;
  }
    
  /**
   * Returns a copy of the sashes attached to this door or window.
   * If no sash is defined an empty array is returned. 
   */
  public Sash [] getSashes() {
    if (this.sashes.length == 0) {
      return this.sashes;
    } else {
      return this.sashes.clone();
    }
  }

  /**
   * Returns <code>true</code> if the location and the size of this door or window 
   * were bound to a wall, last time they were updated. 
   */
  public boolean isBoundToWall() {
    return this.boundToWall;
  }
  
  /**
   * Sets whether the location and the size of this door or window 
   * were bound to a wall, last time they were updated. 
   */
  public void setBoundToWall(boolean boundToWall) {
    this.boundToWall = boundToWall;
  }

  /**
   * Sets the abscissa of this door or window and 
   * resets its {@link #isBoundToWall() boundToWall} flag if the abscissa changed. 
   */
  @Override
  public void setX(float x) {
    if (getX() != x) {
      this.boundToWall = false;
    }
    super.setX(x);
  }

  /**
   * Sets the ordinate of this door or window and 
   * resets its {@link #isBoundToWall() boundToWall} flag if the ordinate changed. 
   */
  @Override
  public void setY(float y) {
    if (getY() != y) {
      this.boundToWall = false;
    }
    super.setY(y);
  }

  /**
   * Sets the angle of this door or window and 
   * resets its {@link #isBoundToWall() boundToWall} flag if the angle changed. 
   */
  @Override
  public void setAngle(float angle) {
    if (getAngle() != angle) {
      this.boundToWall = false;
    }
    super.setAngle(angle);
  }
  
  /**
   * Sets the depth of this door or window and 
   * resets its {@link #isBoundToWall() boundToWall} flag if the depth changed. 
   */
  @Override
  public void setDepth(float depth) {
    if (getDepth() != depth) {
      this.boundToWall = false;
    }
    super.setDepth(depth);
  }
  
  /**
   * Returns always <code>true</code>.
   */
  @Override
  public boolean isDoorOrWindow() {
    return true;
  }

  /**
   * Returns a clone of this door or window.
   */
  @Override
  public HomeDoorOrWindow clone() {
    HomeDoorOrWindow clone = (HomeDoorOrWindow)super.clone();
    clone.boundToWall = false;
    return clone;    
  }
}
