/*
 * UbikSim2 has been developed by:
 * 
 * Juan A. Botía , juanbot[at] um.es
 * Pablo Campillo, pablocampillo[at] um.es
 * Francisco Campuzano, fjcampuzano[at] um.es
 * Emilio Serrano, emilioserra [at] dit.upm.es
 * 
 * This file is part of UbikSimIDE.
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
package sim.app.ubik.building;

import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import ocp.service.ContextEntityItems;
import ocp.service.ContextItemString;
import ocp.service.ContextService;
import sim.app.ubik.Ubik;
import sim.app.ubik.building.connectionSpace.ConnectionSpaceInABuilding;
import sim.app.ubik.graph.NodeGraph;
import sim.app.ubik.utils.ElementsHandler;
import sim.field.grid.SparseGrid2D;
import sim.util.Double2D;
import sim.util.Int2D;


public abstract class SpaceArea {

    private static int IDgenerator = 1;
    protected int floor;
    protected Ubik ubik;
    protected SparseGrid2D grid;
    protected Int2D[] points;      //puntos que delimitan la habitacion
    protected String name;
    protected String id;
    protected Int2D[] positionCache;
    protected Shape shapeCache;     //silueta de la habitacion
    protected Int2D centerCache; // Centro de del cuadrado que abarca la silueta
    protected ElementsHandler elementsHandler;
    
    // Puntos de la habitacion para poder desplazarse por esta en línea recta
    // sin toparse. Se usan para generar el grafo del edificio.
    protected List<NodeGraph> centers;

    public SpaceArea(int floor, String name, Ubik ubik, float[][] points) {
        this.floor = floor;
        this.name = name;
        this.ubik = ubik;
        this.elementsHandler = ubik.getBuilding().getFloor(floor).getHandler(this);
        this.grid = elementsHandler.getGrid();
        this.points = new Int2D[points.length];
        for (int i = 0; i < points.length; i++) {
            this.points[i] = new Int2D(Math.round(points[i][0] / ubik.getCellSize()),
                    Math.round(points[i][1] / ubik.getCellSize()));
        }
        this.id = String.valueOf(IDgenerator++);
        this.centers = new ArrayList();
    }

    /**
     * Metodo que calcula el contorno de la habitacion
     * @return
     */
    public Shape getShape() {
        if (this.shapeCache == null) {
            GeneralPath roomShape = new GeneralPath();
            roomShape.moveTo(this.points[0].getX(), this.points[0].getY());
            for (int i = 1; i < this.points.length; i++) {
                roomShape.lineTo(this.points[i].getX(), this.points[i].getY());
            }
            roomShape.closePath();
            // Cache roomShape
            this.shapeCache = roomShape;
        }
        return this.shapeCache;
    }

    /**
     * Devuelve las coordenadas del centro de la superficie
     * @return
     */
    public Int2D getCenter() {
        if (centerCache == null) {
            Rectangle2D rectangle2D = getShape().getBounds2D();
            centerCache = new Int2D((int) rectangle2D.getCenterX(), (int) rectangle2D.getCenterY());
        }
        return centerCache;
    }

    /**
     * Devuelve true si la coordenada x, y se encuentra dentro de la superficie.
     * @param x
     * @param y
     * @return
     */
    public boolean contains(int x, int y) {
        return getShape().contains(x, y);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void addCenter(NodeGraph point) {
        centers.add(point);
    }

    /**
     * Devuelve los puntos de la habitación que pertenecen al grafo de navegación
     * 
     * @return 
     */
    public List<Int2D> getCenters() {
        List<Int2D> result = new ArrayList<Int2D>();
        for(NodeGraph ng : centers)
            result.add(ng.getPosition());
        return result;
    }
    
    public List<NodeGraph> getNodeGraphs() {
        return centers;
    }

    /**
     * Dado un ángulo (en radianes), devuelve la coordenada inmediatamente
     * limítrofe con el area,
     * @param angle
     * @return
     */
    public Int2D getBoundPoint(float angle) {
        int x = getCenter().getX();
        int y = getCenter().getY();
        int resultX = x;
        int resultY = y;
        int distance = 1;
        while (contains(resultX, resultY)) {
            resultX = x + (int) Math.round(distance * Math.cos(angle));
            resultY = y + (int) Math.round(distance * Math.sin(angle));
            distance++;
        }
        return new Int2D(resultX, resultY);
    }

    /**
     * Obtiene un array con todas las posiciones en la que esta la puerta
     * @return
     */
    public Int2D[] getPositions() {
        if (positionCache == null) {
            ArrayList<Int2D> list = new ArrayList();
            Rectangle2D rectangle2D = getShape().getBounds2D();
            for (int i = (int) rectangle2D.getMinX(); i < (int) rectangle2D.getMaxX(); i++) {
                for (int j = (int) rectangle2D.getMinY(); j < (int) rectangle2D.getMaxY(); j++) {
                    if (contains(i, j)) {
                        list.add(new Int2D(i, j));
                    }
                }
            }
            positionCache = new Int2D[list.size()];
            for (int i = 0; i < list.size(); i++) {
                positionCache[i] = list.get(i);
            }
        }
        return positionCache;
    }

    public SparseGrid2D getGrid() {
        return grid;
    }

    public void createInOCP(ContextService cs) {
    	ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), String.valueOf(getId()));
        cei.addContextItem(new ContextItemString("ubikName", getName()));
        cs.setContextItems(cei);
    }
    
    public Int2D mediatriz(Int2D p1, Int2D p2, Int2D q1, Int2D q2) {
        Int2D result = null;
        double t = 0;
        double s = 0;

        double vx = p2.y - p1.y;
        double vy = p2.x - p1.x;
        double wx = q2.y - q1.y;
        double wy = q2.x - q1.x;

        // Si los segmentos son paralelos devuelve null
        if (Math.round(vx / wx) == Math.round(vy / wy)) {
            return null;
        }

        Double2D p1m = puntoMedio(new Double2D(p1.x, p1.y), new Double2D(p2.x, p2.y));

        Double2D p2m = puntoMedio(new Double2D(q1.x, q1.y), new Double2D(q2.x, q2.y));

        s = (vx * (p2m.y - p1m.y) - p2m.x + p1m.x) / (wx - wy * vx);

        double x2 = p2m.x + s * wx;
        double y2 = p2m.y + s * wy;

        t = (p2m.x + s * wx - p1m.x) / vx;

        double x1 = p1m.x + t * vx;
        double y1 = p1m.y + t * vy;

        return new Int2D((int) Math.round(x2), (int) Math.round(y2));
    }

    public Double2D puntoMedio(Double2D p1, Double2D p2) {
        return new Double2D((p1.x + p2.x) / 2.0, (p1.y + p2.y) / 2.0);
    }

    public int getFloor() {
    	return floor;
    }
    
    public abstract void add(ConnectionSpaceInABuilding d);

    public abstract List<ConnectionSpaceInABuilding> getConnectionSpace();
}
