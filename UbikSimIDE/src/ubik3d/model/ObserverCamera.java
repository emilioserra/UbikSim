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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;

/**
 * Observer camera characteristics in home.
 * @author Emmanuel Puybaret
 */
public class ObserverCamera extends Camera implements Selectable {
  private static final long serialVersionUID = 1L;

  private transient Shape shapeCache;
  private transient Shape rectangleShapeCache;
  
  /**
   * Creates a camera at given location and angle.
   */
  public ObserverCamera(float x, float y, float z, float yaw, float pitch, float fieldOfView) {
    super(x, y, z, yaw, pitch, fieldOfView);
  }

  /**
   * Sets the yaw angle in radians of this camera.
   */
  @Override
  public void setYaw(float yaw) {
    super.setYaw(yaw);
    this.shapeCache = null;
    this.rectangleShapeCache = null;
  }
  
  /**
   * Sets the abscissa of this camera.
   */
  @Override
  public void setX(float x) {
    super.setX(x);
    this.shapeCache = null;
    this.rectangleShapeCache = null;
  }
  
  /**
   * Sets the ordinate of this camera.
   */
  @Override
  public void setY(float y) {
    super.setY(y);
    this.shapeCache = null;
    this.rectangleShapeCache = null;
  }
  
  /**
   * Sets the elevation of this camera.
   */
  @Override
  public void setZ(float z) {
    super.setZ(z);
    this.shapeCache = null;
    this.rectangleShapeCache = null;
  }
  
  /**
   * Returns the width of this observer camera according to
   * human proportions with an eyes elevation at z. 
   */
  public float getWidth() {
    // Adult width is 4 times the distance between head and eyes location
    float width = getZ() * 4 / 14;
    return Math.min(Math.max(width, 20), 62.5f);
  }
  
  /**
   * Returns the depth of this observer camera according to
   * human proportions with an eyes elevation at z. 
   */
  public float getDepth() {
    // Adult depth is equal to the 2 / 5 of its width 
    float depth = getZ() * 8 / 70;
    return Math.min(Math.max(depth, 8), 25);
  }
  
  /**
   * Returns the height of this observer camera according to
   * human proportions with an eyes elevation at z. 
   */
  public float getHeight() {
    // Eyes are 14 / 15 of an adult height
    return getZ() * 15 / 14;
  }
  
  /**
   * Returns the points of each corner of the rectangle surrounding this camera.
   * @return an array of the 4 (x,y) coordinates of the camera corners.
   */
  public float [][] getPoints() {
    float [][] piecePoints = new float[4][2];
    PathIterator it = getRectangleShape().getPathIterator(null);
    for (int i = 0; i < piecePoints.length; i++) {
      it.currentSegment(piecePoints [i]);
      it.next();
    }
    return piecePoints;
  }
  
  /**
   * Returns <code>true</code> if this camera intersects
   * with the horizontal rectangle which opposite corners are at points
   * (<code>x0</code>, <code>y0</code>) and (<code>x1</code>, <code>y1</code>).
   */
  public boolean intersectsRectangle(float x0, float y0, 
                                     float x1, float y1) {
    Rectangle2D rectangle = new Rectangle2D.Float(x0, y0, 0, 0);
    rectangle.add(x1, y1);
    return getShape().intersects(rectangle);
  }
  
  /**
   * Returns <code>true</code> if this camera contains 
   * the point at (<code>x</code>, <code>y</code>)
   * with a given <code>margin</code>.
   */
  public boolean containsPoint(float x, float y, float margin) {
    if (margin == 0) {
      return getShape().contains(x, y);
    } else {
      return getShape().intersects(x - margin, y - margin, 2 * margin, 2 * margin);
    }
  }

  /**
   * Returns the ellipse shape matching this camera.
   */
  private Shape getShape() {
    if (this.shapeCache == null) {
      // Create the ellipse that matches piece bounds
      Ellipse2D cameraEllipse = new Ellipse2D.Float(
          getX() - getWidth() / 2, getY() - getDepth() / 2,
          getWidth(), getDepth());
      // Apply rotation to the rectangle
      AffineTransform rotation = new AffineTransform();
      rotation.setToRotation(getYaw(), getX(), getY());
      PathIterator it = cameraEllipse.getPathIterator(rotation);
      GeneralPath pieceShape = new GeneralPath();
      pieceShape.append(it, false);
      // Cache shape
      this.shapeCache = pieceShape;
    }
    return this.shapeCache;
  }

  /**
   * Returns the rectangle shape matching this camera.
   */
  private Shape getRectangleShape() {
    if (this.rectangleShapeCache == null) {
      // Create the ellipse that matches piece bounds
      Rectangle2D cameraRectangle = new Rectangle2D.Float(
          getX() - getWidth() / 2, getY() - getDepth() / 2,
          getWidth(), getDepth());
      // Apply rotation to the rectangle
      AffineTransform rotation = new AffineTransform();
      rotation.setToRotation(getYaw(), getX(), getY());
      PathIterator it = cameraRectangle.getPathIterator(rotation);
      GeneralPath cameraRectangleShape = new GeneralPath();
      cameraRectangleShape.append(it, false);
      // Cache shape
      this.rectangleShapeCache = cameraRectangleShape;
    }
    return this.rectangleShapeCache;
  }

  /**
   * Moves this camera of (<code>dx</code>, <code>dy</code>) units.
   */
  public void move(float dx, float dy) {
    setX(getX() + dx);
    setY(getY() + dy);
  }
  
  /**
   * Returns a clone of this camera.
   */
  @Override
  public ObserverCamera clone() {
    return (ObserverCamera)super.clone();
  }
}
