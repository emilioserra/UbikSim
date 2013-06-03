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

import java.io.Serializable;

/**
 * A light source of a {@linkplain Light light}. 
 * @author Emmanuel Puybaret
 */
public class LightSource implements Serializable {
  private static final long serialVersionUID = 1L;
  
  private final float x;
  private final float y;
  private final float z;
  private final int   color;
  private final Float diameter;

  /**
   * Creates a new light source.
   */
  public LightSource(float x, float y, float z, int color) {
    this(x, y, z, color, null);
  }

  /**
   * Creates a new light source.
   * @since 3.0
   */
  public LightSource(float x, float y, float z, int color, Float diameter) {
    this.x = x;
    this.y = y;
    this.z = z;
    this.color = color;
    this.diameter = diameter;
  }

  /**
   * Returns the abscissa of this source.
   */
  public float getX() {
    return this.x;
  }
  
  /**
   * Returns the ordinate of this source.
   */
  public float getY() {
    return this.y;
  }
  
  /**
   * Returns the elevation of this source.
   */
  public float getZ() {
    return this.z;
  }
  
  /**
   * Returns the RGB color code of this source.
   */
  public int getColor() {
    return this.color;
  }
  
  /**
   * Returns the diameter of this source or <code>null</code> if it's not defined.
   * @since 3.0
   */
  public Float getDiameter() {
    return this.diameter;
  }
}
