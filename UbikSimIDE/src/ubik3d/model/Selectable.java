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
 * An object that is selectable in home.
 * @author Emmanuel Puybaret
 */
public interface Selectable extends Cloneable {
  /**
   * Returns the points of the shape surrounding this object.
   * @return an array of the (x,y) coordinates of the rectangle.
   */
  public abstract float [][] getPoints();

  /**
   * Returns <code>true</code> if this object intersects
   * with the horizontal rectangle which opposite corners are at points
   * (<code>x0</code>, <code>y0</code>) and (<code>x1</code>, <code>y1</code>).
   */
  public abstract boolean intersectsRectangle(float x0, float y0,
                                              float x1, float y1);

  /**
   * Returns <code>true</code> if this object contains the point at 
   * (<code>x</code>, <code>y</code>) with a given <code>margin</code>.
   */
  public abstract boolean containsPoint(float x, float y,
                                        float margin);

  /**
   * Moves this object of (<code>dx</code>, <code>dy</code>) units.
   */
  public abstract void move(float dx, float dy);
  
  /**
   * Returns a clone of this object.
   */
  public abstract Selectable clone();
}