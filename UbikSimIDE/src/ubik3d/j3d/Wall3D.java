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

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Geometry;
import javax.media.j3d.Group;
import javax.media.j3d.Node;
import javax.media.j3d.RenderingAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Texture;
import javax.media.j3d.TextureAttributes;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Point3f;
import javax.vecmath.TexCoord2f;

import ubik3d.model.Home;
import ubik3d.model.HomeEnvironment;
import ubik3d.model.HomeFurnitureGroup;
import ubik3d.model.HomePieceOfFurniture;
import ubik3d.model.HomeTexture;
import ubik3d.model.Wall;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;

/**
 * Root of wall branch.
 */
public class Wall3D extends Object3DBranch {
  private static final TextureAttributes MODULATE_TEXTURE_ATTRIBUTES = new TextureAttributes();
  
  static {
    MODULATE_TEXTURE_ATTRIBUTES.setTextureMode(TextureAttributes.MODULATE);
  }
  
  private static final int LEFT_WALL_SIDE  = 0;
  private static final int RIGHT_WALL_SIDE = 1;
  
  private final Home home;

  /**
   * Creates the 3D wall matching the given home <code>wall</code>.
   */
  public Wall3D(Wall wall, Home home) {
    this(wall, home, false, false);
  }
  
  /**
   * Creates the 3D wall matching the given home <code>wall</code>.
   */
  public Wall3D(Wall wall, Home home, boolean ignoreDrawingMode, 
                boolean waitTextureLoadingEnd) {
    setUserData(wall);
    this.home = home;

    // Allow wall branch to be removed from its parent
    setCapability(BranchGroup.ALLOW_DETACH);
    // Allow to read branch shape children
    setCapability(Group.ALLOW_CHILDREN_READ);
    
    // Add wall left and right empty shapes to branch
    addChild(createWallPartShape(false));
    addChild(createWallPartShape(false));
    if (!ignoreDrawingMode) {
      // Add wall left and right empty outline shapes to branch
      addChild(createWallPartShape(true));
      addChild(createWallPartShape(true));
    }
    // Set wall shape geometry and appearance
    updateWallGeometry();
    updateWallAppearance(waitTextureLoadingEnd);
  }

  /**
   * Returns a new wall part shape with no geometry  
   * and a default appearance with a white material.
   */
  private Node createWallPartShape(boolean outline) {
    Shape3D wallShape = new Shape3D();
    // Allow wall shape to change its geometry
    wallShape.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
    wallShape.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
    wallShape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);

    Appearance wallAppearance = new Appearance();
    wallShape.setAppearance(wallAppearance);
    wallAppearance.setCapability(Appearance.ALLOW_TRANSPARENCY_ATTRIBUTES_READ);
    TransparencyAttributes transparencyAttributes = new TransparencyAttributes();
    transparencyAttributes.setCapability(TransparencyAttributes.ALLOW_VALUE_WRITE);
    transparencyAttributes.setCapability(TransparencyAttributes.ALLOW_MODE_WRITE);
    wallAppearance.setTransparencyAttributes(transparencyAttributes);
    wallAppearance.setCapability(Appearance.ALLOW_RENDERING_ATTRIBUTES_READ);
    RenderingAttributes renderingAttributes = new RenderingAttributes();
    renderingAttributes.setCapability(RenderingAttributes.ALLOW_VISIBLE_WRITE);
    wallAppearance.setRenderingAttributes(renderingAttributes);
    
    if (outline) {
      wallAppearance.setColoringAttributes(Object3DBranch.OUTLINE_COLORING_ATTRIBUTES);
      wallAppearance.setPolygonAttributes(Object3DBranch.OUTLINE_POLYGON_ATTRIBUTES);
      wallAppearance.setLineAttributes(Object3DBranch.OUTLINE_LINE_ATTRIBUTES);
    } else {
      wallAppearance.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
      wallAppearance.setMaterial(DEFAULT_MATERIAL);      
      wallAppearance.setCapability(Appearance.ALLOW_TEXTURE_WRITE);
      wallAppearance.setCapability(Appearance.ALLOW_TEXTURE_READ);
      // Mix texture and wall color
      wallAppearance.setTextureAttributes(MODULATE_TEXTURE_ATTRIBUTES);
    }
    
    return wallShape;
  }

  @Override
  public void update() {
    updateWallGeometry();
    updateWallAppearance(false);
  }
  
  /**
   * Sets the 3D geometry of this wall shapes that matches its 2D geometry.  
   */
  private void updateWallGeometry() {
    updateWallSideGeometry(LEFT_WALL_SIDE, ((Wall)getUserData()).getLeftSideTexture());
    updateWallSideGeometry(RIGHT_WALL_SIDE, ((Wall)getUserData()).getRightSideTexture());
  }
  
  private void updateWallSideGeometry(int wallSide, HomeTexture texture) {
    Shape3D wallFilledShape = (Shape3D)getChild(wallSide);
    Shape3D wallOutlineShape = numChildren() > 2 
        ? (Shape3D)getChild(wallSide + 2)
        : null; 
    int currentGeometriesCount = wallFilledShape.numGeometries();
    for (Geometry wallGeometry : createWallGeometries(wallSide, texture)) {
      if (wallGeometry != null) {
        wallFilledShape.addGeometry(wallGeometry);
        if (wallOutlineShape != null) {
          wallOutlineShape.addGeometry(wallGeometry);
        }
      }
    }
    for (int i = currentGeometriesCount - 1; i >= 0; i--) {
      wallFilledShape.removeGeometry(i);
      if (wallOutlineShape != null) {
        wallOutlineShape.removeGeometry(i);
      }
    }
  }
  
  /**
   * Returns <code>wall</code> geometries computed with windows or doors 
   * that intersect wall.
   */
  private Geometry [] createWallGeometries(int wallSide, HomeTexture texture) {
    float [][] wallSidePoints = getWallSidePoints(wallSide);
    float [] textureReferencePoint = wallSide == LEFT_WALL_SIDE
        ? wallSidePoints [0]
        : wallSidePoints [wallSidePoints.length - 1];
    Shape wallShape = getShape(wallSidePoints);
    Area wallArea = new Area(wallShape);
    float wallHeightAtStart = getWallHeightAtStart();
    float wallHeightAtEnd = getWallHeightAtEnd();
    float maxWallHeight = Math.max(wallHeightAtStart, wallHeightAtEnd);
    
    // Compute wall angles and top line factors
    Wall wall = (Wall)getUserData();
    double wallYawAngle = Math.atan2(wall.getYEnd() - wall.getYStart(), wall.getXEnd() - wall.getXStart()); 
    double cosWallYawAngle = Math.cos(wallYawAngle);
    double sinWallYawAngle = Math.sin(wallYawAngle);
    double wallXStartWithZeroYaw = cosWallYawAngle * wall.getXStart() + sinWallYawAngle * wall.getYStart();
    double wallXEndWithZeroYaw = cosWallYawAngle * wall.getXEnd() + sinWallYawAngle * wall.getYEnd();
    boolean roundWall = wall.getArcExtent() != null && wall.getArcExtent() != 0; 
    double topLineAlpha;
    double topLineBeta;
    if (wallHeightAtStart == wallHeightAtEnd) {
      topLineAlpha = 0;
      topLineBeta = wallHeightAtStart;
    } else {
      topLineAlpha = (wallHeightAtEnd - wallHeightAtStart) / (wallXEndWithZeroYaw - wallXStartWithZeroYaw);
      topLineBeta = wallHeightAtStart - topLineAlpha * wallXStartWithZeroYaw;
    }
    
    // Search which doors or windows intersect with this wall side
    List<DoorOrWindowArea> windowIntersections = new ArrayList<DoorOrWindowArea>();
    for (HomePieceOfFurniture piece : getVisibleDoorsAndWindows(this.home.getFurniture())) {
      if (piece.getElevation() < maxWallHeight) {
        Shape pieceShape = getShape(piece.getPoints());
        Area pieceArea = new Area(pieceShape);
        Area intersectionArea = new Area(wallShape);
        intersectionArea.intersect(pieceArea);
        if (!intersectionArea.isEmpty()) {
          windowIntersections.add(new DoorOrWindowArea(intersectionArea, Arrays.asList(new HomePieceOfFurniture [] {piece})));
          // Remove from wall area the piece shape
          wallArea.subtract(pieceArea);
        }
      }
    }
    // Refine intersections in case some doors or windows are superimposed
    if (windowIntersections.size() > 1) {
      // Search superimposed windows
      for (int windowIndex = 0; windowIndex < windowIntersections.size(); windowIndex++) {
        DoorOrWindowArea windowIntersection = windowIntersections.get(windowIndex);
        List<DoorOrWindowArea> otherWindowIntersections = new ArrayList<DoorOrWindowArea>();
        int otherWindowIndex = 0;
        for (DoorOrWindowArea otherWindowIntersection : windowIntersections) {          
          if (windowIntersection.getArea().isEmpty()) {
            break;
          } else if (otherWindowIndex > windowIndex) { // Avoid search twice the intersection between two items
            Area windowsIntersectionArea = new Area(otherWindowIntersection.getArea());
            windowsIntersectionArea.intersect(windowIntersection.getArea());
            if (!windowsIntersectionArea.isEmpty()) {
              // Remove intersection from wall area              
              otherWindowIntersection.getArea().subtract(windowsIntersectionArea);              
              windowIntersection.getArea().subtract(windowsIntersectionArea);
              // Create a new area for the intersection 
              List<HomePieceOfFurniture> doorsOrWindows = new ArrayList<HomePieceOfFurniture>(windowIntersection.getDoorsOrWindows());
              doorsOrWindows.addAll(otherWindowIntersection.getDoorsOrWindows());
              otherWindowIntersections.add(new DoorOrWindowArea(windowsIntersectionArea, doorsOrWindows));
            }
          }
          otherWindowIndex++;
        }
        windowIntersections.addAll(otherWindowIntersections);
      }
    }    
    List<Geometry> wallGeometries = new ArrayList<Geometry>();
    List<float[]> wallPoints = new ArrayList<float[]>(4);
    // Generate geometry for each wall part that doesn't contain a window
    float [] previousWallPoint = null;
    for (PathIterator it = wallArea.getPathIterator(null, 0.1f); !it.isDone(); ) {
      float [] wallPoint = new float[2];
      if (it.currentSegment(wallPoint) == PathIterator.SEG_CLOSE) {
        if (wallPoints.size() > 2) {
          // Remove last point if it's equal to first point
          if (Arrays.equals(wallPoints.get(0), wallPoints.get(wallPoints.size() - 1))) {
            wallPoints.remove(wallPoints.size() - 1);
          }
          if (wallPoints.size() > 2) {
            float [][] wallPartPoints = wallPoints.toArray(new float[wallPoints.size()][]);
            // Compute geometry for vertical part
            wallGeometries.add(createWallVerticalPartGeometry(wall, wallPartPoints, 0, 
                cosWallYawAngle, sinWallYawAngle, topLineAlpha, topLineBeta, texture, 
                textureReferencePoint, wallSide));
            // Compute geometry for bottom part
            wallGeometries.add(createWallHorizontalPartGeometry(wallPartPoints, 0, true, roundWall));
            // Compute geometry for top part
            wallGeometries.add(createWallTopPartGeometry(wallPartPoints, 
                cosWallYawAngle, sinWallYawAngle, topLineAlpha, topLineBeta, roundWall));
          }
        }
        wallPoints.clear();
        previousWallPoint = null;
      } else if (previousWallPoint == null
                 || !Arrays.equals(wallPoint, previousWallPoint)) {
        wallPoints.add(wallPoint);
        previousWallPoint = wallPoint;
      }
      it.next();
    }
    
    // Generate geometry for each wall part above and below a window
    previousWallPoint = null;
    for (DoorOrWindowArea windowIntersection : windowIntersections) {
      if (!windowIntersection.getArea().isEmpty()) {
        for (PathIterator it = windowIntersection.getArea().getPathIterator(null, 0.1f); !it.isDone(); ) {
          float [] wallPoint = new float[2];
          if (it.currentSegment(wallPoint) == PathIterator.SEG_CLOSE) {
            // Remove last point if it's equal to first point
            if (Arrays.equals(wallPoints.get(0), wallPoints.get(wallPoints.size() - 1))) {
              wallPoints.remove(wallPoints.size() - 1);
            }

            if (wallPoints.size() > 2) {
              float [][] wallPartPoints = wallPoints.toArray(new float[wallPoints.size()][]);
              List<HomePieceOfFurniture> doorsOrWindows = windowIntersection.getDoorsOrWindows();
              if (doorsOrWindows.size() > 1) {
                // Sort superimposed doors and windows by elevation and height
                Collections.sort(doorsOrWindows, 
                    new Comparator<HomePieceOfFurniture>() {
                      public int compare(HomePieceOfFurniture piece1, HomePieceOfFurniture piece2) {
                        if (piece1.getElevation() < piece2.getElevation()) {
                          return -1;
                        } else if (piece1.getElevation() > piece2.getElevation()) {
                          return 1;
                        } else {
                          return 0;
                        }
                      }
                    });
              }
              HomePieceOfFurniture lowestDoorOrWindow = doorsOrWindows.get(0);            
              // Generate geometry for wall part below window
              if (lowestDoorOrWindow.getElevation() > 0) {
                wallGeometries.add(createWallVerticalPartGeometry(wall, wallPartPoints, 0, 
                    cosWallYawAngle, sinWallYawAngle, 0, lowestDoorOrWindow.getElevation(), texture, 
                    textureReferencePoint, wallSide));
                wallGeometries.add(createWallHorizontalPartGeometry(wallPartPoints, 0, true, roundWall));
                wallGeometries.add(createWallHorizontalPartGeometry(wallPartPoints, 
                    lowestDoorOrWindow.getElevation(), false, roundWall));
              }
              
              // Generate geometry for wall parts between superimposed windows
              for (int i = 0; i < doorsOrWindows.size() - 1; ) {
                HomePieceOfFurniture lowerDoorOrWindow = doorsOrWindows.get(i);            
                HomePieceOfFurniture higherDoorOrWindow = doorsOrWindows.get(++i);
                // Ignore higher windows smaller than lower window
                while (lowerDoorOrWindow.getElevation() + lowerDoorOrWindow.getHeight() >= higherDoorOrWindow.getElevation() + higherDoorOrWindow.getHeight()
                    && ++i < doorsOrWindows.size()) {
                  higherDoorOrWindow = doorsOrWindows.get(i);
                }
                if (i < doorsOrWindows.size()
                    && lowerDoorOrWindow.getElevation() + lowerDoorOrWindow.getHeight() < higherDoorOrWindow.getElevation()) {
                  wallGeometries.add(createWallVerticalPartGeometry(wall, wallPartPoints, lowerDoorOrWindow.getElevation() + lowerDoorOrWindow.getHeight(), 
                      cosWallYawAngle, sinWallYawAngle, 0, higherDoorOrWindow.getElevation(), texture, textureReferencePoint, wallSide));
                  wallGeometries.add(createWallHorizontalPartGeometry(wallPartPoints, 
                      lowerDoorOrWindow.getElevation() + lowerDoorOrWindow.getHeight(), true, roundWall));
                  wallGeometries.add(createWallHorizontalPartGeometry(wallPartPoints, higherDoorOrWindow.getElevation(), false, roundWall));
                }
              }
                
              HomePieceOfFurniture highestDoorOrWindow = doorsOrWindows.get(doorsOrWindows.size() - 1);            
              for (int i = doorsOrWindows.size() - 2; i >= 0; i--) {
                HomePieceOfFurniture doorOrWindow = doorsOrWindows.get(i);            
                if (doorOrWindow.getElevation() + doorOrWindow.getHeight() > highestDoorOrWindow.getElevation() + highestDoorOrWindow.getHeight()) {
                  highestDoorOrWindow = doorOrWindow;
                }
              }
              float doorOrWindowTop = highestDoorOrWindow.getElevation() + highestDoorOrWindow.getHeight();
              // Compute the minimum vertical position of wallPartPoints
              double minTopY = maxWallHeight;
              for (int i = 0; i < wallPartPoints.length; i++) {
                double xTopPointWithZeroYaw = cosWallYawAngle * wallPartPoints[i][0] + sinWallYawAngle * wallPartPoints[i][1];
                minTopY = Math.min(minTopY, topLineAlpha * xTopPointWithZeroYaw + topLineBeta);
              }            
              // Generate geometry for wall part above window
              if (doorOrWindowTop < minTopY) {
                wallGeometries.add(createWallVerticalPartGeometry(wall, wallPartPoints, doorOrWindowTop, 
                    cosWallYawAngle, sinWallYawAngle, topLineAlpha, topLineBeta, texture, textureReferencePoint, wallSide));
                wallGeometries.add(createWallHorizontalPartGeometry(
                    wallPartPoints, doorOrWindowTop, true, roundWall));
                wallGeometries.add(createWallTopPartGeometry(wallPartPoints, 
                    cosWallYawAngle, sinWallYawAngle, topLineAlpha, topLineBeta, roundWall));
              }
            }
            wallPoints.clear();
            previousWallPoint = null;
          } else if (previousWallPoint == null
                     || !Arrays.equals(wallPoint, previousWallPoint)) {
            wallPoints.add(wallPoint);
            previousWallPoint = wallPoint;
          }
          it.next();
        }
      } 
    }
    return wallGeometries.toArray(new Geometry [wallGeometries.size()]);
  }

  /**
   * Returns all the visible doors and windows in the given <code>furniture</code>.  
   */
  private List<HomePieceOfFurniture> getVisibleDoorsAndWindows(List<HomePieceOfFurniture> furniture) {
    List<HomePieceOfFurniture> visibleDoorsAndWindows = new ArrayList<HomePieceOfFurniture>(furniture.size());
    for (HomePieceOfFurniture piece : furniture) {
      if (piece.isVisible()) {
        if (piece instanceof HomeFurnitureGroup) {
          visibleDoorsAndWindows.addAll(getVisibleDoorsAndWindows(((HomeFurnitureGroup)piece).getFurniture()));
        } else if (piece.isDoorOrWindow()) {
          visibleDoorsAndWindows.add(piece);
        }
      }
    }
    return visibleDoorsAndWindows;
  }

  /**
   * Returns the points of one of the side of this wall. 
   */
  private float [][] getWallSidePoints(int wallSide) {
    Wall wall = (Wall)getUserData();
    float [][] wallPoints = wall.getPoints();
    
    if (wallSide == LEFT_WALL_SIDE) {
      for (int i = wallPoints.length / 2; i < wallPoints.length; i++) {
        wallPoints [i][0] = (wallPoints [i][0] + wallPoints [wallPoints.length - i - 1][0]) / 2;
        wallPoints [i][1] = (wallPoints [i][1] + wallPoints [wallPoints.length - i - 1][1]) / 2;
      }
    } else { // RIGHT_WALL_SIDE
      for (int i = 0, n = wallPoints.length / 2; i < n; i++) {
        wallPoints [i][0] = (wallPoints [i][0] + wallPoints [wallPoints.length - i - 1][0]) / 2;
        wallPoints [i][1] = (wallPoints [i][1] + wallPoints [wallPoints.length - i - 1][1]) / 2;
      }
    }
    return wallPoints;
  }

  /**
   * Returns the vertical rectangles that join each point of <code>points</code>
   * and spread from <code>yMin</code> to a top line (y = ax + b) described by <code>topLineAlpha</code>
   * and <code>topLineBeta</code> factors in a vertical plan that is rotated around
   * vertical axis matching <code>cosWallYawAngle</code> and <code>sinWallYawAngle</code>. 
   */
  private Geometry createWallVerticalPartGeometry(Wall wall, 
                                                  float [][] points, float yMin, 
                                                  double cosWallYawAngle, double sinWallYawAngle, 
                                                  double topLineAlpha, double topLineBeta, 
                                                  HomeTexture texture,
                                                  float [] textureReferencePoint,
                                                  int wallSide) {
    // Compute wall coordinates
    Point3f [] bottom = new Point3f [points.length];
    Point3f [] top    = new Point3f [points.length];
    double  [] distanceSqToWallMiddle = new double [points.length];
    Float   [] pointUCoordinates = new Float [points.length];
    float xStart = wall.getXStart();
    float yStart = wall.getYStart();
    float xEnd = wall.getXEnd();
    float yEnd = wall.getYEnd();
    Float arcExtent = wall.getArcExtent();
    float [] arcCircleCenter = null;
    float arcCircleRadius = 0;
    float referencePointAngle = 0;
    if (arcExtent != null && arcExtent != 0) {
      arcCircleCenter = new float [] {wall.getXArcCircleCenter(), wall.getYArcCircleCenter()};
      arcCircleRadius = (float)Point2D.distance(arcCircleCenter [0], arcCircleCenter [1], 
          xStart, yStart);
      referencePointAngle = (float)Math.atan2(textureReferencePoint [1] - arcCircleCenter [1], 
          textureReferencePoint [0] - arcCircleCenter [0]);
    }
    for (int i = 0; i < points.length; i++) {
      bottom [i] = new Point3f(points [i][0], yMin, points [i][1]);
      if (arcCircleCenter == null) {
        distanceSqToWallMiddle [i] = Line2D.ptLineDistSq(xStart, yStart, xEnd, yEnd, bottom [i].x, bottom [i].z);
      } else {
        distanceSqToWallMiddle [i] = arcCircleRadius 
            - Point2D.distance(arcCircleCenter [0], arcCircleCenter [1], bottom [i].x, bottom [i].z);
        distanceSqToWallMiddle [i] *= distanceSqToWallMiddle [i];
      }
      // Compute vertical top point 
      double xTopPointWithZeroYaw = cosWallYawAngle * points [i][0] + sinWallYawAngle * points [i][1];
      float topY = (float)(topLineAlpha * xTopPointWithZeroYaw + topLineBeta);
      top [i] = new Point3f(points [i][0], topY, points [i][1]);
    }
    // Search which rectangles should be ignored
    int rectanglesCount = 0;
    boolean [] usedRectangle = new boolean [points.length]; 
    for (int i = 0; i < points.length - 1; i++) {
      usedRectangle [i] = distanceSqToWallMiddle [i] > 0.001f
          || distanceSqToWallMiddle [i + 1] > 0.001f;
      if (usedRectangle [i]) {
        rectanglesCount++;
      } 
    }
    usedRectangle [usedRectangle.length - 1] =  distanceSqToWallMiddle [0] > 0.001f
        || distanceSqToWallMiddle [points.length - 1] > 0.001f;
    if (usedRectangle [usedRectangle.length - 1]) {
      rectanglesCount++;
    }
    if (rectanglesCount == 0) {
      return null;
    }
    
    Point3f [] coords = new Point3f [rectanglesCount * 4];
    int j = 0;
    for (int i = 0; i < points.length - 1; i++) {
      if (usedRectangle [i]) {
        coords [j++] = bottom [i];
        coords [j++] = bottom [i + 1];
        coords [j++] = top [i + 1];
        coords [j++] = top [i];
      }
    }
    if (usedRectangle [usedRectangle.length - 1]) {
      coords [j++] = bottom [points.length - 1];
      coords [j++] = bottom [0];
      coords [j++] = top [0];
      coords [j++] = top [points.length - 1];
    }
    
    GeometryInfo geometryInfo = new GeometryInfo(GeometryInfo.QUAD_ARRAY);
    geometryInfo.setCoordinates (coords);
    
    // Compute wall texture coordinates
    if (texture != null) {
      float halfThicknessSq = (wall.getThickness() * wall.getThickness()) / 4;
      TexCoord2f [] textureCoords = new TexCoord2f [rectanglesCount * 4];
      float yMinTextureCoords = yMin / texture.getHeight();
      TexCoord2f firstTextureCoords = new TexCoord2f(0, yMinTextureCoords);
      j = 0;
      // Tolerate more error with round walls since arc points are approximative
      float epsilon = arcCircleCenter == null 
          ? 0.0001f 
          : halfThicknessSq / 4;
      for (int index = 0; index < points.length; index++) {
        int nextIndex = (index + 1) % points.length;
        if (usedRectangle [index]) {
          if (Math.abs(distanceSqToWallMiddle [index] - halfThicknessSq) < epsilon
              && Math.abs(distanceSqToWallMiddle [nextIndex] - halfThicknessSq) < epsilon) {
            // Compute texture coordinates of wall part parallel to wall middle
            // according to textureReferencePoint
            float firstHorizontalTextureCoords;
            float secondHorizontalTextureCoords;
            if (arcCircleCenter == null) {
              firstHorizontalTextureCoords = (float)Point2D.distance(textureReferencePoint[0], textureReferencePoint[1], 
                  points [index][0], points [index][1]) / texture.getWidth();
              secondHorizontalTextureCoords = (float)Point2D.distance(textureReferencePoint[0], textureReferencePoint[1], 
                  points [nextIndex][0], points [nextIndex][1]) / texture.getWidth();
            } else {
              if (pointUCoordinates [index] == null) {
                float pointAngle = (float)Math.atan2(points [index][1] - arcCircleCenter [1], points [index][0] - arcCircleCenter [0]);
                pointAngle = adjustAngleOnReferencePointAngle(pointAngle, referencePointAngle, arcExtent);
                pointUCoordinates [index] = (pointAngle - referencePointAngle) * arcCircleRadius / texture.getWidth();
              }
              if (pointUCoordinates [nextIndex] == null) {
                float pointAngle = (float)Math.atan2(points [nextIndex][1] - arcCircleCenter [1], points [nextIndex][0] - arcCircleCenter [0]);
                pointAngle = adjustAngleOnReferencePointAngle(pointAngle, referencePointAngle, arcExtent);
                pointUCoordinates [nextIndex] = (pointAngle - referencePointAngle) * arcCircleRadius / texture.getWidth();
              }
              
              firstHorizontalTextureCoords = pointUCoordinates [index];
              secondHorizontalTextureCoords = pointUCoordinates [nextIndex];
            }
            textureCoords [j++] = new TexCoord2f(firstHorizontalTextureCoords, yMinTextureCoords);
            textureCoords [j++] = new TexCoord2f(secondHorizontalTextureCoords, yMinTextureCoords);
            textureCoords [j++] = new TexCoord2f(secondHorizontalTextureCoords, top [nextIndex].y / texture.getHeight());
            textureCoords [j++] = new TexCoord2f(firstHorizontalTextureCoords, top [index].y / texture.getHeight());
          } else {
            float horizontalTextureCoords = (float)Point2D.distance(points [index][0], points [index][1], 
                points [nextIndex][0], points [nextIndex][1]) / texture.getWidth();
            textureCoords [j++] = firstTextureCoords;
            textureCoords [j++] = new TexCoord2f(horizontalTextureCoords, yMinTextureCoords);
            textureCoords [j++] = new TexCoord2f(horizontalTextureCoords, top [nextIndex].y / texture.getHeight());
            textureCoords [j++] = new TexCoord2f(0, top [index].y / texture.getHeight());
          }
        }
      }
      geometryInfo.setTextureCoordinateParams(1, 2);
      geometryInfo.setTextureCoordinates(0, textureCoords);
    }
    
    // Generate normals
    NormalGenerator normalGenerator = new NormalGenerator();
    if (arcCircleCenter == null) {
      normalGenerator.setCreaseAngle(0);
    }
    normalGenerator.generateNormals(geometryInfo);
    return geometryInfo.getIndexedGeometryArray();
  }

  /**
   * Returns <code>pointAngle</code> plus or minus 2 PI to ensure <code>pointAngle</code> value 
   * will be greater or lower than <code>referencePointAngle</code> depending on <code>arcExtent</code> direction.
   */
  private float adjustAngleOnReferencePointAngle(float pointAngle, float referencePointAngle, float arcExtent) {
    if (arcExtent > 0) {
      if ((referencePointAngle > 0 
          && (pointAngle < 0
              || referencePointAngle > pointAngle))
        || (referencePointAngle < 0 
            && pointAngle < 0 
            && referencePointAngle > pointAngle)) {
        pointAngle += 2 * (float)Math.PI;
      }
    } else {
      if ((referencePointAngle < 0 
            && (pointAngle > 0
                || referencePointAngle < pointAngle))
          || (referencePointAngle > 0 
              && pointAngle > 0 
              && referencePointAngle < pointAngle)) {
        pointAngle -= 2 * (float)Math.PI;
      }
    }
    return pointAngle;
  }

  /**
   * Returns the geometry of an horizontal part of a wall at <code>y</code>.
   */
  private Geometry createWallHorizontalPartGeometry(float [][] points, float y, 
                                                    boolean reverseOrder, boolean roundWall) {
    Point3f [] coords = new Point3f [points.length];
    for (int i = 0; i < points.length; i++) {
      coords [i] = new Point3f(points [i][0], y, points [i][1]);
    }
    GeometryInfo geometryInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
    geometryInfo.setCoordinates (coords);
    geometryInfo.setStripCounts(new int [] {coords.length});
    if (reverseOrder) {
      geometryInfo.reverse();
    }
    // Generate normals
    NormalGenerator normalGenerator = new NormalGenerator();
    if (roundWall) {
      normalGenerator.setCreaseAngle(0);
    }
    normalGenerator.generateNormals(geometryInfo);
    return geometryInfo.getIndexedGeometryArray ();
  }
  
  /**
   * Returns the geometry of the top part of a wall.
   */
  private Geometry createWallTopPartGeometry(float [][] points, 
                                             double cosWallYawAngle, double sinWallYawAngle, 
                                             double topLineAlpha, double topLineBeta, 
                                             boolean roundWall) {
    Point3f [] coords = new Point3f [points.length];
    for (int i = 0; i < points.length; i++) {
      double xTopPointWithZeroYaw = cosWallYawAngle * points [i][0] + sinWallYawAngle * points [i][1];
      float topY = (float)(topLineAlpha * xTopPointWithZeroYaw + topLineBeta);
      coords [i] = new Point3f(points [i][0], topY, points [i][1]);
    }
    GeometryInfo geometryInfo = new GeometryInfo(GeometryInfo.POLYGON_ARRAY);
    geometryInfo.setCoordinates (coords);
    geometryInfo.setStripCounts(new int [] {coords.length});
    // Generate normals
    NormalGenerator normalGenerator = new NormalGenerator();
    if (roundWall) {
      normalGenerator.setCreaseAngle(0);
    }
    normalGenerator.generateNormals(geometryInfo);
    return geometryInfo.getIndexedGeometryArray ();
  }
  
  /**
   * Returns the height at the start of the wall managed by this 3D object.
   */
  private float getWallHeightAtStart() {
    Float wallHeight = ((Wall)getUserData()).getHeight();      
    if (wallHeight != null) {
      return wallHeight;
    } else {
      // If wall height isn't set, use home wall height
      return this.home.getWallHeight();
    }
  }
  
  /**
   * Returns the height at the end of the wall managed by this 3D object.
   */
  private float getWallHeightAtEnd() {
    Wall wall = (Wall)getUserData();      
    if (wall.isTrapezoidal()) {
      return wall.getHeightAtEnd();
    } else {
      // If the wall isn't trapezoidal, use same height as at wall start
      return getWallHeightAtStart();
    }
  }
  
  /**
   * Sets wall appearance with its color, texture and transparency.
   */
  private void updateWallAppearance(boolean waitTextureLoadingEnd) {
    Wall wall = (Wall)getUserData();
    updateFilledWallSideAppearance(((Shape3D)getChild(LEFT_WALL_SIDE)).getAppearance(), 
        wall.getLeftSideTexture(), waitTextureLoadingEnd, wall.getLeftSideColor(), wall.getLeftSideShininess());
    updateFilledWallSideAppearance(((Shape3D)getChild(RIGHT_WALL_SIDE)).getAppearance(), 
        wall.getRightSideTexture(), waitTextureLoadingEnd, wall.getRightSideColor(), wall.getRightSideShininess());
    if (numChildren() > 2) {
      updateOutlineWallSideAppearance(((Shape3D)getChild(LEFT_WALL_SIDE + 2)).getAppearance());
      updateOutlineWallSideAppearance(((Shape3D)getChild(RIGHT_WALL_SIDE + 2)).getAppearance());
    }
  }
  
  /**
   * Sets filled wall side appearance with its color, texture, transparency and visibility.
   */
  private void updateFilledWallSideAppearance(final Appearance wallSideAppearance, 
                                              final HomeTexture wallSideTexture,
                                              boolean waitTextureLoadingEnd,
                                              Integer wallSideColor, 
                                              float shininess) {
    if (wallSideTexture == null) {
      wallSideAppearance.setMaterial(getMaterial(wallSideColor, wallSideColor, shininess));
      wallSideAppearance.setTexture(null);
    } else {
      // Update material and texture of wall side
      wallSideAppearance.setMaterial(getMaterial(DEFAULT_COLOR, DEFAULT_AMBIENT_COLOR, shininess));
      final TextureManager textureManager = TextureManager.getInstance();
      textureManager.loadTexture(wallSideTexture.getImage(), waitTextureLoadingEnd,
          new TextureManager.TextureObserver() {
              public void textureUpdated(Texture texture) {
                wallSideAppearance.setTexture(texture);
              }
            });
    }
    // Update wall side transparency
    float wallsAlpha = this.home.getEnvironment().getWallsAlpha();
    TransparencyAttributes transparencyAttributes = wallSideAppearance.getTransparencyAttributes();
    transparencyAttributes.setTransparency(wallsAlpha);
    // If walls alpha is equal to zero, turn off transparency to get better results 
    transparencyAttributes.setTransparencyMode(wallsAlpha == 0 
        ? TransparencyAttributes.NONE 
        : TransparencyAttributes.NICEST);      
    // Update wall side visibility
    RenderingAttributes renderingAttributes = wallSideAppearance.getRenderingAttributes();
    HomeEnvironment.DrawingMode drawingMode = this.home.getEnvironment().getDrawingMode();
    renderingAttributes.setVisible(drawingMode == null
        || drawingMode == HomeEnvironment.DrawingMode.FILL 
        || drawingMode == HomeEnvironment.DrawingMode.FILL_AND_OUTLINE);
  }
  
  /**
   * Sets outline wall side visibility.
   */
  private void updateOutlineWallSideAppearance(final Appearance wallSideAppearance) {
    // Update wall side visibility
    RenderingAttributes renderingAttributes = wallSideAppearance.getRenderingAttributes();
    HomeEnvironment.DrawingMode drawingMode = this.home.getEnvironment().getDrawingMode();
    renderingAttributes.setVisible(drawingMode == HomeEnvironment.DrawingMode.OUTLINE 
        || drawingMode == HomeEnvironment.DrawingMode.FILL_AND_OUTLINE);
  }

  /**
   * An area used to compute holes in walls. 
   */
  private static class DoorOrWindowArea {
    private final Area area;
    private final List<HomePieceOfFurniture> doorsOrWindows;
    
    public DoorOrWindowArea(Area area, List<HomePieceOfFurniture> doorsOrWindows) {
      this.area = area;
      this.doorsOrWindows = doorsOrWindows;      
    }
    
    public Area getArea() {
      return this.area;
    }
    
    public List<HomePieceOfFurniture> getDoorsOrWindows() {
      return this.doorsOrWindows;
    }
  }
}