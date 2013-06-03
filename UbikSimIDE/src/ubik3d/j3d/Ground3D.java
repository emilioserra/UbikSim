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
package ubik3d.j3d;

import java.awt.Color;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;

import ubik3d.model.Home;
import ubik3d.model.HomeTexture;
import ubik3d.model.Room;

import com.sun.j3d.utils.geometry.GeometryInfo;

/**
 * Root of a the 3D ground.
 * @author Emmanuel Puybaret
 */
public class Ground3D extends Object3DBranch {
  private final float originX;
  private final float originY;
  private final float width;
  private final float depth;

  /**
   * Creates a 3D ground for the given <code>home</code>.
   */
  public Ground3D(Home home,
                  float groundOriginX,
                  float groundOriginY,
                  float groundWidth,
                  float groundDepth, 
                  boolean waitTextureLoadingEnd) {
    setUserData(home);
    this.originX = groundOriginX;
    this.originY = groundOriginY;
    this.width = groundWidth;
    this.depth = groundDepth;

    // Use coloring attributes for ground to avoid ground lighting
    ColoringAttributes groundColoringAttributes = new ColoringAttributes();
    groundColoringAttributes.setCapability(ColoringAttributes.ALLOW_COLOR_WRITE);
    
    Appearance groundAppearance = new Appearance();
    groundAppearance.setColoringAttributes(groundColoringAttributes);
    groundAppearance.setCapability(Appearance.ALLOW_COLORING_ATTRIBUTES_READ);
    groundAppearance.setCapability(Appearance.ALLOW_TEXTURE_WRITE);

    final Shape3D groundShape = new Shape3D();
    groundShape.setAppearance(groundAppearance);
    groundShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    groundShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
    groundShape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
    
    setCapability(ALLOW_CHILDREN_READ);
    
    addChild(groundShape);

    update(waitTextureLoadingEnd);    
  }
  
  /**
   * Updates ground coloring and texture attributes from home ground color and texture.
   */
  @Override
  public void update() {
    update(false);
  }
  
  /**
   * Updates ground coloring and texture attributes from home ground color and texture.
   */
  private void update(boolean waitTextureLoadingEnd) {
    Home home = (Home)getUserData();
    Shape3D groundShape = (Shape3D)getChild(0);
    int currentGeometriesCount = groundShape.numGeometries();
    
    Color3f groundColor = new Color3f(new Color(home.getEnvironment().getGroundColor()));
    final Appearance groundAppearance = groundShape.getAppearance();
    groundAppearance.getColoringAttributes().setColor(groundColor);
    HomeTexture groundTexture = home.getEnvironment().getGroundTexture();
    if (groundTexture != null) {
      final TextureManager imageManager = TextureManager.getInstance();
      imageManager.loadTexture(groundTexture.getImage(), waitTextureLoadingEnd,
          new TextureManager.TextureObserver() {
              public void textureUpdated(Texture texture) {
                groundAppearance.setTexture(texture);
              }
            });
    } else {
      groundAppearance.setTexture(null);
    }
    
    // Create ground geometry
    List<Point3f> coords = new ArrayList<Point3f>();
    List<Integer> stripCounts = new ArrayList<Integer>();
    // First add the coordinates of the ground rectangle
    coords.add(new Point3f(this.originX, 0, this.originY)); 
    coords.add(new Point3f(this.originX, 0, this.originY + this.depth));
    coords.add(new Point3f(this.originX + this.width, 0, this.originY + this.depth));
    coords.add(new Point3f(this.originX + this.width, 0, this.originY));
    // Compute ground texture coordinates if necessary
    List<TexCoord2f> textureCoords = new ArrayList<TexCoord2f>();
    if (groundTexture != null) {
      textureCoords.add(new TexCoord2f(0, 0));
      textureCoords.add(new TexCoord2f(0, -this.depth / groundTexture.getHeight()));
      textureCoords.add(new TexCoord2f(this.width / groundTexture.getWidth(), -this.depth / groundTexture.getHeight()));
      textureCoords.add(new TexCoord2f(this.width / groundTexture.getWidth(), 0));
    }
    stripCounts.add(4);

    // Compute the union of the rooms
    Area roomsArea = new Area();
    for (Room room : home.getRooms()) {
      if (room.isFloorVisible()) {
        float [][] points = room.getPoints();
        if (points.length > 2) {
          roomsArea.add(new Area(getShape(points)));
        }
      }
    }

    // Retrieve points
    List<float [][]> roomParts = new ArrayList<float [][]>();
    List<float []>   roomPart  = new ArrayList<float[]>();
    float [] previousRoomPoint = null;
    for (PathIterator it = roomsArea.getPathIterator(null); !it.isDone(); ) {
      float [] roomPoint = new float[2];
      if (it.currentSegment(roomPoint) == PathIterator.SEG_CLOSE) {
        if (roomPart.get(0) [0] == previousRoomPoint [0] 
            && roomPart.get(0) [1] == previousRoomPoint [1]) {
          roomPart.remove(roomPart.size() - 1);
        }
        if (roomPart.size() > 2) {
          roomParts.add(roomPart.toArray(new float [roomPart.size()][]));
        }
        roomPart.clear();
        previousRoomPoint = null;
      } else {
        if (previousRoomPoint == null
            || roomPoint [0] != previousRoomPoint [0] 
            || roomPoint [1] != previousRoomPoint [1]) {
          roomPart.add(roomPoint);
        }
        previousRoomPoint = roomPoint;
      }
      it.next();
    }
    
    for (float [][] points : roomParts) {
      if (!new Room(points).isClockwise()) {
        // Define a hole in ground
        for (float [] point : points) {
          coords.add(new Point3f(point [0], 0, point [1]));
          if (groundTexture != null) {
            textureCoords.add(new TexCoord2f((point [0] - this.originX) / groundTexture.getWidth(), 
                (this.originY - point [1]) / groundTexture.getHeight()));
          }
        }
        stripCounts.add(points.length);
      } else {
        // Define an inner surface in ground
        Point3f [] innerSurfaceCoords = new Point3f [points.length];
        TexCoord2f [] innerSurfaceTextureCoords = groundTexture != null 
            ? new TexCoord2f [points.length]
            : null;
        for (int i = 0, j = points.length - 1; i < points.length; i++, j--) {
          float [] point = points [i];
          innerSurfaceCoords [j] = new Point3f(point [0], 0, point [1]);
          if (groundTexture != null) {
            innerSurfaceTextureCoords [j] = new TexCoord2f((point [0] - this.originX) / groundTexture.getWidth(), 
                (this.originY - point [1]) / groundTexture.getHeight());
          }
        }
        GeometryInfo geometryInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        geometryInfo.setCoordinates (innerSurfaceCoords);
        if (groundTexture != null) {
          geometryInfo.setTextureCoordinateParams(1, 2);
          geometryInfo.setTextureCoordinates(0, innerSurfaceTextureCoords);
        }
        geometryInfo.setStripCounts(new int [] {points.length});
        geometryInfo.setContourCounts(new int [] {1});
        groundShape.addGeometry(geometryInfo.getIndexedGeometryArray());
      }
    }

    GeometryInfo geometryInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
    geometryInfo.setCoordinates (coords.toArray(new Point3f [coords.size()]));
    int [] stripCountsArray = new int [stripCounts.size()];
    for (int i = 0; i < stripCountsArray.length; i++) {
      stripCountsArray [i] = stripCounts.get(i);
    }
    geometryInfo.setStripCounts(stripCountsArray);
    geometryInfo.setContourCounts(new int [] {stripCountsArray.length});

    if (groundTexture != null) {
      geometryInfo.setTextureCoordinateParams(1, 2);
      geometryInfo.setTextureCoordinates(0, textureCoords.toArray(new TexCoord2f [textureCoords.size()]));
    }    
    groundShape.addGeometry(geometryInfo.getIndexedGeometryArray());
    
    // Remove old geometries
    for (int i = currentGeometriesCount - 1; i >= 0; i--) {
      groundShape.removeGeometry(i);
    }
  }
}
