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
 * A sash (moving part) of a door or a window. 
 * @author Emmanuel Puybaret
 * @since  1.7
 */
public class Sash implements Serializable {
  private static final long serialVersionUID = 1L;
  
  private final float xAxis;
  private final float yAxis;
  private final float width;
  private final float startAngle;
  private final float endAngle;
  
  /**
   * Creates a window sash.
   */
  public Sash(float xAxis, float yAxis, 
              float width, 
              float startAngle,
              float endAngle) {
    this.xAxis = xAxis;
    this.yAxis = yAxis;
    this.width = width;
    this.startAngle = startAngle;
    this.endAngle = endAngle;
  }

  /**
   * Returns the abscissa of the axis around which this sash turns, relatively to
   * the top left corner of the window or the door.
   * @return a value in percentage of the width of the door or the window.
   */
  public float getXAxis() {
    return this.xAxis;
  }
  
  /**
   * Returns the ordinate of the axis around which this sash turns, relatively to
   * the top left corner of the window or the door.
   * @return a value in percentage of the depth of the door or the window.
   */
  public float getYAxis() {
    return this.yAxis;
  }
  
  /**
   * Returns the width of this sash.
   * @return a value in percentage of the width of the door or the window.
   */
  public float getWidth() {
    return this.width;
  }
  
  /**
   * Returns the opening start angle of this sash.
   * @return an angle in radians.
   */
  public float getStartAngle() {
    return this.startAngle;
  }    

  /**
   * Returns the opening end angle of this sash.
   * @return an angle in radians.
   */
  public float getEndAngle() {
    return this.endAngle;
  }    
}