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
package ubik3d.j3d;

import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Geometry;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;

import ubik3d.model.Home;
import ubik3d.model.HomeTexture;
import ubik3d.model.Room;
import ubik3d.model.Wall;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;

/**
 * Root of room branch.
 */
public class Room3D extends Object3DBranch {
  private static final TextureAttributes MODULATE_TEXTURE_ATTRIBUTES = new TextureAttributes();
  
  static {
    MODULATE_TEXTURE_ATTRIBUTES.setTextureMode(TextureAttributes.MODULATE);
  }
  
  private static final int FLOOR_PART  = 0;
  private static final int CEILING_PART = 1;
  
  private final Home home;

  /**
   * Creates the 3D room matching the given home <code>room</code>.
   */
  public Room3D(Room room, Home home) {
    this(room, home, false, false, false);
  }

  /**
   * Creates the 3D room matching the given home <code>room</code>.
   */
  public Room3D(Room room, Home home,
                boolean ignoreCeilingPart, 
                boolean ignoreInvisiblePart,
                boolean waitTextureLoadingEnd) {
    setUserData(room);
    this.home = home;

    // Allow room branch to be removed from its parent
    setCapability(BranchGroup.ALLOW_DETACH);
    // Allow to read branch shape children
    setCapability(Group.ALLOW_CHILDREN_READ);
    
    // Add room floor and cellar empty shapes to branch
    addChild(createRoomPartShape());
    addChild(createRoomPartShape());
    // Set room shape geometry and appearance
    updateRoomGeometry();
    updateRoomAppearance(waitTextureLoadingEnd);
    
    if (ignoreCeilingPart
        || (ignoreInvisiblePart
            && !room.isCeilingVisible())) {
      removeChild(CEILING_PART);
    }
    if (ignoreInvisiblePart
        && !room.isFloorVisible()) {
      removeChild(FLOOR_PART);
    }
  }

  /**
   * Returns a new room part shape with no geometry  
   * and a default appearance with a white material.
   */
  private Node createRoomPartShape() {
    Shape3D roomShape = new Shape3D();
    // Allow room shape to change its geometry
    roomShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    roomShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
    roomShape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);

    Appearance roomAppearance = new Appearance();
    roomShape.setAppearance(roomAppearance);
    roomAppearance.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_READ);
    RenderingAttributes renderingAttributes = new RenderingAttributes();
    renderingAttributes.setCapability(RenderingAttributes.ALLOW_VISIBLE_WRITE);
    roomAppearance.setRenderingAttributes(renderingAttributes);
    roomAppearance.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
    roomAppearance.setMaterial(DEFAULT_MATERIAL);      
    roomAppearance.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
    roomAppearance.setCapability(Appearance.ALLOW_TEXTURE_READ);
    // Mix texture and room color
    roomAppearance.setTextureAttributes(MODULATE_TEXTURE_ATTRIBUTES);
    
    return roomShape;
  }

  @Override
  public void update() {
    updateRoomGeometry();
    updateRoomAppearance(false);
  }
  
  /**
   * Sets the 3D geometry of this room shapes that matches its 2D geometry.  
   */
  private void updateRoomGeometry() {
    updateRoomPartGeometry(FLOOR_PART, ((Room)getUserData()).getFloorTexture());
    updateRoomPartGeometry(CEILING_PART, ((Room)getUserData()).getCeilingTexture());
  }
  
  private void updateRoomPartGeometry(int roomPart, HomeTexture texture) {
    Shape3D roomShape = (Shape3D)getChild(roomPart);
    int currentGeometriesCount = roomShape.numGeometries();
    for (Geometry roomGeometry : createRoomGeometries(roomPart, texture)) {
      roomShape.addGeometry(roomGeometry);
    }
    for (int i = currentGeometriesCount - 1; i >= 0; i--) {
      roomShape.removeGeometry(i);
    }
  }
  
  /**
   * Returns room geometry computed from its points.
   */
  private Geometry [] createRoomGeometries(int roomPart, HomeTexture texture) {
    Room room = (Room)getUserData();
    float [][] points = room.getPoints();
    if ((roomPart == FLOOR_PART && room.isFloorVisible()
         || roomPart == CEILING_PART && room.isCeilingVisible())
        && points.length > 2) {
      // If room isn't singular retrieve all the points of its different polygons 
      List<float [][]> roomPoints = new ArrayList<float[][]>();
      Map<Integer, List<float [][]>> roomHoles = new HashMap<Integer, List<float [][]>>();
      List<Room> homeRooms = this.home.getRooms();
      if (!room.isSingular() || homeRooms.get(homeRooms.size() - 1) != room) {        
        Area roomArea = new Area(getShape(points));
        if (homeRooms.contains(room)) {
          // Remove other rooms surface that may overlap the current room
          for (int i = homeRooms.size() - 1; i > 0 && homeRooms.get(i) != room; i--) {
            Room otherRoom = homeRooms.get(i);
            if (roomPart == FLOOR_PART && otherRoom.isFloorVisible()
                || roomPart == CEILING_PART && otherRoom.isCeilingVisible()) {
              roomArea.subtract(new Area(getShape(otherRoom.getPoints())));
            }
          }
        }
        // Retrieve the points of the different polygons 
        // and reverse their points order if necessary
        List<float []> currentPathPoints = new ArrayList<float[]>();
        roomPoints = new ArrayList<float[][]>();
        float [] previousRoomPoint = null;
        int i = 0;
        for (PathIterator it = roomArea.getPathIterator(null); !it.isDone(); ) {
          float [] roomPoint = new float[2];
          switch (it.currentSegment(roomPoint)) {
            case PathIterator.SEG_MOVETO :
            case PathIterator.SEG_LINETO : 
              if (previousRoomPoint == null
                  || roomPoint [0] != previousRoomPoint [0] 
                  || roomPoint [1] != previousRoomPoint [1]) {
                currentPathPoints.add(roomPoint);
              }
              previousRoomPoint = roomPoint;
              break;
            case PathIterator.SEG_CLOSE :
              if (currentPathPoints.get(0) [0] == previousRoomPoint [0] 
                  && currentPathPoints.get(0) [1] == previousRoomPoint [1]) {
                currentPathPoints.remove(currentPathPoints.size() - 1);
              }
              if (currentPathPoints.size() > 2) {
                float [][] pathPoints = 
                    currentPathPoints.toArray(new float [currentPathPoints.size()][]);
                Room subRoom = new Room(pathPoints);
                if (subRoom.getArea() > 0) {
                  boolean pathPointsClockwise = subRoom.isClockwise();
                  if (pathPointsClockwise) {
                    // Store counter clockwise points as holes
                    if (roomPart != CEILING_PART) {
                      pathPoints = getReversedArray(pathPoints);
                    }
                    List<float [][]> holes = roomHoles.get(i);
                    if (holes == null) {
                      holes = new ArrayList<float [][]>(1);
                      roomHoles.put(i, holes);
                    }
                    holes.add(pathPoints);
                  } else {
                    if (roomPart == CEILING_PART) {
                      pathPoints = getReversedArray(pathPoints);
                    }
                    roomPoints.add(pathPoints);
                    i++;
                  }
                }
              }
              currentPathPoints.clear();
              previousRoomPoint = null;
              break;
          }
          it.next();        
        }
      } else {
        boolean clockwise = room.isClockwise();
        if (clockwise && roomPart == FLOOR_PART
            || !clockwise && roomPart == CEILING_PART) {
          // Reverse points order if they are in the good order
          points = getReversedArray(points);
        }
        roomPoints = Arrays.asList(new float [][][] {points});
      }
      
      Geometry [] geometries = new Geometry [roomPoints.size()];
      for (int i = 0; i < geometries.length; i++) {
        float [][] geometryPoints = roomPoints.get(i);
        List<float [][]> geometryHoles = roomHoles.get(i);
        if (geometryHoles == null) {
          geometryHoles = Collections.emptyList();
        }
        int vertexCount = geometryPoints.length;
        int [] stripCounts = new int [1 + geometryHoles.size()];
        int j = 0;
        stripCounts [j++] = geometryPoints.length;
        for (float [][] geometryHole : geometryHoles) {
          vertexCount += geometryHole.length;
          stripCounts [j++] = geometryHole.length;
        }
        // Compute room coordinates
        Point3f [] coords = new Point3f [vertexCount];
        for (j = 0; j < geometryPoints.length; j++) {
          float y = roomPart == FLOOR_PART 
              ? 0 
              : getRoomHeightAt(geometryPoints [j][0], geometryPoints [j][1]);
          coords [j] = new Point3f(geometryPoints [j][0], y, geometryPoints [j][1]);
        }
        for (float [][] geometryHole : geometryHoles) {
          for (int k = 0; k < geometryHole.length; k++, j++) {
            float y = roomPart == FLOOR_PART 
                ? 0 
                : getRoomHeightAt(geometryHole [k][0], geometryHole [k][1]);
            coords [j] = new Point3f(geometryHole [k][0], y, geometryHole [k][1]);
          }
        }
        GeometryInfo geometryInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
        geometryInfo.setCoordinates (coords);
        geometryInfo.setStripCounts(stripCounts);
        geometryInfo.setContourCounts(new int [] {stripCounts.length});
        // Compute room texture coordinates
        if (texture != null) {
          TexCoord2f [] textureCoords = new TexCoord2f [vertexCount];
          for (j = 0; j < geometryPoints.length; j++) {
            textureCoords [j] = new TexCoord2f(geometryPoints [j][0] / texture.getWidth(), 
                roomPart == FLOOR_PART 
                    ? -geometryPoints [j][1] / texture.getHeight()
                    : geometryPoints [j][1] / texture.getHeight());
          }
          for (float [][] geometryHole : geometryHoles) {
            for (int k = 0; k < geometryHole.length; k++, j++) {
              textureCoords [j] = new TexCoord2f(geometryHole [k][0] / texture.getWidth(), 
                  roomPart == FLOOR_PART 
                      ? -geometryHole [k][1] / texture.getHeight()
                      : geometryHole [k][1] / texture.getHeight());
            }
          }
          geometryInfo.setTextureCoordinateParams(1, 2);
          geometryInfo.setTextureCoordinates(0, textureCoords);
        }
        
        // Generate normals
        new NormalGenerator(0).generateNormals(geometryInfo);
        geometries [i] = geometryInfo.getIndexedGeometryArray();
      }
      return geometries;
    } else {
      return new Geometry [0];
    }
  }
  
  /**
   * Returns an array that cites <code>points</code> in reverse order.
   */
  private float [][] getReversedArray(float [][] points) {
    List<float []> pointList = Arrays.asList(points);
    Collections.reverse(pointList);
    points = pointList.toArray(points);
    return points;
  }
  
  /**
   * Returns the room height at the given point. 
   */
  private float getRoomHeightAt(float x, float y) {
    double smallestDistance = Float.POSITIVE_INFINITY;
    float roomHeight = this.home.getWallHeight();
    // Search the closest wall point to x, y
    for (Wall wall : this.home.getWalls()) {
      Float wallHeightAtStart = wall.getHeight();
      float [][] points = wall.getPoints();
      for (int i = 0; i < points.length; i++) {
        double distanceToWallPoint = Point2D.distanceSq(points [i][0], points [i][1], x, y);
        if (distanceToWallPoint < smallestDistance) {
          smallestDistance = distanceToWallPoint; 
          if (i == 0 || i == 3) { // Wall start
            roomHeight = wallHeightAtStart != null 
                ? wallHeightAtStart 
                : this.home.getWallHeight();
          } else { // Wall end
            roomHeight = wall.isTrapezoidal() 
                ? wall.getHeightAtEnd() 
                : (wallHeightAtStart != null ? wallHeightAtStart : this.home.getWallHeight());
          }
        }
      }
    }
    return roomHeight;
  }

  /**
   * Sets room appearance with its color, texture.
   */
  private void updateRoomAppearance(boolean waitTextureLoadingEnd) {
    Room room = (Room)getUserData();
    updateRoomPartAppearance(((Shape3D)getChild(FLOOR_PART)).getAppearance(), 
        room.getFloorTexture(), waitTextureLoadingEnd, room.getFloorColor(), room.getFloorShininess(), room.isFloorVisible());
    updateRoomPartAppearance(((Shape3D)getChild(CEILING_PART)).getAppearance(), 
        room.getCeilingTexture(), waitTextureLoadingEnd, room.getCeilingColor(), room.getCeilingShininess(), room.isCeilingVisible());
  }
  
  /**
   * Sets room part appearance with its color, texture and visibility.
   */
  private void updateRoomPartAppearance(final Appearance roomPartAppearance, 
                                        final HomeTexture roomPartTexture,
                                        boolean waitTextureLoadingEnd,
                                        Integer roomPartColor,
                                        float shininess,
                                        boolean visible) {
    if (roomPartTexture == null) {
      roomPartAppearance.setMaterial(getMaterial(roomPartColor, roomPartColor, shininess));
      roomPartAppearance.setTexture(null);
    } else {
      // Update material and texture of room part
      roomPartAppearance.setMaterial(getMaterial(DEFAULT_COLOR, DEFAULT_AMBIENT_COLOR, shininess));
      final TextureManager textureManager = TextureManager.getInstance();
      textureManager.loadTexture(roomPartTexture.getImage(), waitTextureLoadingEnd,
          new TextureManager.TextureObserver() {
              public void textureUpdated(Texture texture) {
                roomPartAppearance.setTexture(texture);
              }
            });
    }
    // Update room part visibility
    RenderingAttributes renderingAttributes = roomPartAppearance.getRenderingAttributes();
    renderingAttributes.setVisible(visible);
  }
}