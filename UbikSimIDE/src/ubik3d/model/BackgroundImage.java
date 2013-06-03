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

import java.awt.geom.Point2D;
import java.io.Serializable;

/**
 * The image displayed in background of the plan.
 * @author Emmanuel Puybaret
 */
public class BackgroundImage implements Serializable {
  private static final long serialVersionUID = 1L;
  
  private final Content image;
  private final float   scaleDistance;
  private final float   scaleDistanceXStart;
  private final float   scaleDistanceYStart;
  private final float   scaleDistanceXEnd;
  private final float   scaleDistanceYEnd;
  private final float   xOrigin;
  private final float   yOrigin;
  private final boolean invisible; 
  
  /**
   * Creates a visible background image.
   */
  public BackgroundImage(Content image, float scaleDistance, 
                         float scaleDistanceXStart, float scaleDistanceYStart, 
                         float scaleDistanceXEnd, float scaleDistanceYEnd, 
                         float xOrigin, float yOrigin) {
    this(image, scaleDistance, scaleDistanceXStart, 
        scaleDistanceYStart, scaleDistanceXEnd, scaleDistanceYEnd, xOrigin, yOrigin, true);
  }

  /**
   * Creates a background image.
   * @since 1.8
   */
  public BackgroundImage(Content image, float scaleDistance, 
                         float scaleDistanceXStart, float scaleDistanceYStart, 
                         float scaleDistanceXEnd, float scaleDistanceYEnd, 
                         float xOrigin, float yOrigin, boolean visible) {
    this.image = image;
    this.scaleDistance = scaleDistance;
    this.scaleDistanceXStart = scaleDistanceXStart;
    this.scaleDistanceYStart = scaleDistanceYStart;
    this.scaleDistanceXEnd = scaleDistanceXEnd;
    this.scaleDistanceYEnd = scaleDistanceYEnd;
    this.xOrigin = xOrigin;
    this.yOrigin = yOrigin;
    // Use an invisible field instead of a visible field to get a default false value
    // for images created before version 1.8
    this.invisible = !visible;
  }

  /**
   * Returns the image content of this background image.
   */
  public Content getImage() {
    return this.image;
  }
  
  /**
   * Returns the distance used to compute the scale of this image.
   */
  public float getScaleDistance() {
    return this.scaleDistance;
  }

  /**
   * Returns the abcissa of the start point used to compute 
   * the scale of this image.
   */
  public float getScaleDistanceXStart() {
    return this.scaleDistanceXStart;
  }

  /**
   * Returns the ordinate of the start point used to compute 
   * the scale of this image.
   */
  public float getScaleDistanceYStart() {
    return this.scaleDistanceYStart;
  }

  /**
   * Returns the abcissa of the end point used to compute 
   * the scale of this image.
   */
  public float getScaleDistanceXEnd() {
    return this.scaleDistanceXEnd;
  }

  /**
   * Returns the ordinate of the end point used to compute 
   * the scale of this image.
   */
  public float getScaleDistanceYEnd() {
    return this.scaleDistanceYEnd;
  }

  /**
   * Returns the scale of this image.
   */
  public float getScale() {
    return getScale(this.scaleDistance,
        this.scaleDistanceXStart, this.scaleDistanceYStart, 
        this.scaleDistanceXEnd, this.scaleDistanceYEnd);
  }
  
  /**
   * Returns the scale equal to <code>scaleDistance</code> divided
   * by the distance between the points 
   * (<code>scaleDistanceXStart</code>, <code>scaleDistanceYStart</code>)
   * and (<code>scaleDistanceXEnd</code>, <code>scaleDistanceYEnd</code>).
   */
  public static float getScale(float scaleDistance, 
                               float scaleDistanceXStart, float scaleDistanceYStart,
                               float scaleDistanceXEnd, float scaleDistanceYEnd) {
    return (float)(scaleDistance 
        / Point2D.distance(scaleDistanceXStart, scaleDistanceYStart, 
                           scaleDistanceXEnd, scaleDistanceYEnd));
  }
  
  /**
   * Returns the origin abscissa of this image.
   */
  public float getXOrigin() {
    return this.xOrigin;
  }
  
  /**
   * Returns the origin ordinate of this image.
   */
  public float getYOrigin() {
    return this.yOrigin;
  }

  /**
   * Returns <code>true</code> if this image is visible in plan.
   * @since 1.8
   */
  public boolean isVisible() {
    return !this.invisible;
  }
}
