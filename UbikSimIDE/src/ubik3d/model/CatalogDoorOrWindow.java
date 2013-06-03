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

import java.math.BigDecimal;

/**
 * A door or a window of the catalog.
 * @author Emmanuel Puybaret
 * @since  1.7
 */
public class CatalogDoorOrWindow extends CatalogPieceOfFurniture implements DoorOrWindow {
  private static final long serialVersionUID = 1L;

  private final float   wallThickness;
  private final float   wallDistance;
  private final Sash [] sashes;

  /**
   * Creates an unmodifiable catalog door or window of the default catalog.
   * @param id    the id of the new door or window, or <code>null</code>
   * @param name  the name of the new door or window
   * @param description the description of the new door or window 
   * @param icon content of the icon of the new door or window
   * @param model content of the 3D model of the new door or window
   * @param width  the width in centimeters of the new door or window
   * @param depth  the depth in centimeters of the new door or window
   * @param height  the height in centimeters of the new door or window
   * @param elevation  the elevation in centimeters of the new door or window
   * @param movable if <code>true</code>, the new door or window is movable
   * @param wallThickness a value in percentage of the depth of the new door or window
   * @param wallDistance a distance in percentage of the depth of the new door or window
   * @param sashes the sashes attached to the new door or window
   * @param modelRotation the rotation 3 by 3 matrix applied to the door or window model
   * @param creator the creator of the model
   * @param resizable if <code>true</code>, the size of the new door or window may be edited
   * @param price the price of the new door or window, or <code>null</code> 
   * @param valueAddedTaxPercentage the Value Added Tax percentage applied to the 
   *             price of the new door or window or <code>null</code> 
   */
  public CatalogDoorOrWindow(String id, String metadata, String rol, String name, String description, Content icon, Content model, float amount, 
                             float width, float depth, float height, float elevation, boolean movable, 
                             float wallThickness, float wallDistance, Sash [] sashes,
                             float [][] modelRotation, String creator,
                             boolean resizable, BigDecimal price, BigDecimal valueAddedTaxPercentage) {
    this(id, metadata, rol, name, description, icon, null, model, amount, width, depth, height, elevation, movable,   
        wallThickness, wallDistance, sashes, modelRotation, creator, resizable, price, valueAddedTaxPercentage);
  }
         
  /**
   * Creates an unmodifiable catalog door or window of the default catalog.
   * @param id    the id of the new door or window, or <code>null</code>
   * @param name  the name of the new door or window
   * @param description the description of the new door or window 
   * @param icon content of the icon of the new door or window
   * @param planIcon content of the icon of the new piece displayed in plan
   * @param model content of the 3D model of the new door or window
   * @param width  the width in centimeters of the new door or window
   * @param depth  the depth in centimeters of the new door or window
   * @param height  the height in centimeters of the new door or window
   * @param elevation  the elevation in centimeters of the new door or window
   * @param movable if <code>true</code>, the new door or window is movable
   * @param wallThickness a value in percentage of the depth of the new door or window
   * @param wallDistance a distance in percentage of the depth of the new door or window
   * @param sashes the sashes attached to the new door or window
   * @param modelRotation the rotation 3 by 3 matrix applied to the door or window model
   * @param creator the creator of the model
   * @param resizable if <code>true</code>, the size of the new door or window may be edited
   * @param price the price of the new door or window, or <code>null</code> 
   * @param valueAddedTaxPercentage the Value Added Tax percentage applied to the 
   *             price of the new door or window or <code>null</code>
   * @since 2.2 
   */
  public CatalogDoorOrWindow(String id, String metadata, String rol, String name, String description, 
                             Content icon, Content planIcon, Content model, float amount,
                             float width, float depth, float height, float elevation, boolean movable, 
                             float wallThickness, float wallDistance, Sash [] sashes,
                             float [][] modelRotation, String creator,
                             boolean resizable, BigDecimal price, BigDecimal valueAddedTaxPercentage) {
    this(id, metadata, rol, name, description, icon, planIcon, model, amount, width, depth, height, elevation, movable,   
        wallThickness, wallDistance, sashes,
        modelRotation, creator, resizable, true, true, price, valueAddedTaxPercentage);
  }
         
  /**
   * Creates an unmodifiable catalog door or window of the default catalog.
   * @param id    the id of the new door or window, or <code>null</code>
   * @param name  the name of the new door or window
   * @param description the description of the new door or window 
   * @param icon content of the icon of the new door or window
   * @param planIcon content of the icon of the new piece displayed in plan
   * @param model content of the 3D model of the new door or window
   * @param width  the width in centimeters of the new door or window
   * @param depth  the depth in centimeters of the new door or window
   * @param height  the height in centimeters of the new door or window
   * @param elevation  the elevation in centimeters of the new door or window
   * @param movable if <code>true</code>, the new door or window is movable
   * @param wallThickness a value in percentage of the depth of the new door or window
   * @param wallDistance a distance in percentage of the depth of the new door or window
   * @param sashes the sashes attached to the new door or window
   * @param modelRotation the rotation 3 by 3 matrix applied to the door or window model
   * @param creator the creator of the model
   * @param resizable if <code>true</code>, the size of the new door or window may be edited
   * @param deformable if <code>true</code>, the width, depth and height of the new piece may 
   *            change independently from each other
   * @param texturable if <code>false</code> this piece should always keep the same color or texture.
   * @param price the price of the new door or window, or <code>null</code> 
   * @param valueAddedTaxPercentage the Value Added Tax percentage applied to the 
   *             price of the new door or window or <code>null</code>
   * @since 3.0 
   */
  public CatalogDoorOrWindow(String id, String metadata, String rol,  String name, String description, 
                             Content icon, Content planIcon, Content model, float amount,
                             float width, float depth, float height, float elevation, boolean movable, 
                             float wallThickness, float wallDistance, Sash [] sashes,
                             float [][] modelRotation, String creator,
                             boolean resizable, boolean deformable, boolean texturable,
                             BigDecimal price, BigDecimal valueAddedTaxPercentage) {
    super(id, metadata, rol, name, description, icon, null, model, amount, width, depth, height, elevation, movable,   
        modelRotation, creator, resizable, deformable, texturable, price, valueAddedTaxPercentage);
    this.wallThickness = wallThickness;
    this.wallDistance = wallDistance;
    this.sashes = sashes;
  }
         
  /**
   * Creates a modifiable catalog door or window with all its values.
   * @param name  the name of the new door or window
   * @param icon content of the icon of the new door or window
   * @param model content of the 3D model of the new door or window
   * @param width  the width in centimeters of the new door or window
   * @param depth  the depth in centimeters of the new door or window
   * @param height  the height in centimeters of the new door or window
   * @param elevation  the elevation in centimeters of the new door or window
   * @param movable if <code>true</code>, the new door or window is movable
   * @param wallThickness a value in percentage of the depth of the new door or window
   * @param wallDistance a distance in percentage of the depth of the new door or window
   * @param sashes the sashes attached to the new door or window
   * @param color the color of the door or window as RGB code or <code>null</code> 
   *        if door or window color is unchanged
   * @param modelRotation the rotation 3 by 3 matrix applied to the door or window model
   * @param backFaceShown <code>true</code> if back face should be shown
   * @param iconYaw the yaw angle used to create the door or window icon
   * @param proportional if <code>true</code>, size proportions will be kept
   */
  public CatalogDoorOrWindow(String metadata, String rol, String name, Content icon, Content model, float amount,
                             float width, float depth, float height,
                             float elevation, boolean movable, 
                             float wallThickness, float wallDistance, Sash [] sashes, 
                             Integer color, float [][] modelRotation, boolean backFaceShown, 
                             float iconYaw, boolean proportional) {
    super(metadata, rol, name, icon, model, amount, width, depth, height, elevation, movable,   
        color, modelRotation, backFaceShown, iconYaw, proportional);
    this.wallThickness = wallThickness;
    this.wallDistance = wallDistance;
    this.sashes = sashes;
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
   * Returns always <code>true</code>.
   */
  @Override
  public boolean isDoorOrWindow() {
    return true;
  }
}
