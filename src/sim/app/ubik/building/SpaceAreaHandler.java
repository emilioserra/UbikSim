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

import sim.app.ubik.building.rooms.Room;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import sim.app.ubik.MovementTools;

import sim.app.ubik.Ubik;
import sim.app.ubik.building.connectionSpace.Door;
import sim.app.ubik.building.rooms.BathRoom;
import sim.app.ubik.building.rooms.BedRoom;
import sim.app.ubik.building.rooms.Corridor;
import sim.app.ubik.building.rooms.GeriatricBathRoom;
import sim.app.ubik.building.rooms.Kitchen;
import sim.app.ubik.building.rooms.Lab;
import sim.app.ubik.building.rooms.LivingRoom;
import sim.app.ubik.building.rooms.MaintenanceRoom;
import sim.app.ubik.building.rooms.MeetingRoom;
import sim.app.ubik.building.rooms.Office;
import sim.app.ubik.graph.NodeGraph;
import sim.app.ubik.graph.NodeGraphCollection;
import sim.app.ubik.ocp.OCPProxyProducer;
import sim.app.ubik.utils.ElementsHandler;
import sim.field.grid.SparseGrid2D;
import sim.util.Bag;
import sim.util.Int2D;
import ubik3d.model.Home;
import ubik3d.model.HomePieceOfFurniture;

public class SpaceAreaHandler extends ElementsHandler {

    protected int cellSize;
    private String file = "handlers/space_areas.txt";
    protected List<SpaceArea> spaceAreas;
    protected Map<String, SpaceArea> spaceAreasById;
    protected Map<String, SpaceArea> spaceAreasByName;

    protected OCPProxyProducer ocp;
private static final Logger LOG = Logger.getLogger(SpaceAreaHandler.class.getName());
    public SpaceAreaHandler(Ubik ubik, int floor, int GRID_WIDTH, int GRID_HEIGHT) {
        super(ubik, floor, GRID_WIDTH, GRID_HEIGHT);
        this.cellSize = ubik.getCellSize();

        this.spaceAreas = new ArrayList<SpaceArea>();
        this.spaceAreasById = new HashMap<String, SpaceArea>();
        this.spaceAreasByName = new HashMap<String, SpaceArea>();

        this.ocp = ubik.createOCPProxyProducer("UBIK.SpaceAreaHandler");

        initRols(file);
    }

    public void add(SpaceArea p) {
        spaceAreas.add(p);
        spaceAreasById.put(p.getId(), p);
        spaceAreasByName.put(p.getName(), p);
    }

    public List<SpaceArea> getInstancesOf(Class clase) {
        List<SpaceArea> result = new ArrayList<SpaceArea>();
        for (SpaceArea p : spaceAreas) {
            if (clase.isInstance(p)) {
                result.add(p);
            }
        }
        return result;
    }

    public void clear() {
        spaceAreas.clear();
        spaceAreasById.clear();
        spaceAreasByName.clear();

        grid.clear();
    }

    /**
     * Genera las puertas de la casa, eliminando el muro y la habitación
     * Comprueba en qué habitaciones se encuentran los puntos de acceso y añade
     * la puerta a dichas habitaciones.
     *
     * @param home
     */
    private void generateDoors(Home home) {
        for (HomePieceOfFurniture h : home.getFurniture()) {
            if (h.getRol() != null && h.getRol().equals("Door")) {
                LOG.config(h.getName());
                h.setDepth(h.getDepth() + cellSize);
                Door d = new Door(floor, ubik, h);
                if (ocp != null) {
                    d.createInOCP(ocp.getContextService());
                }

                Int2D[] positions = d.getPositions();
                for (Int2D p : positions) {
                    grid.removeObjectsAtLocation(p);
                    grid.setObjectLocation(new Cell(d, p), p);
                }

                // Añadimos la puerta a cada habitación que conecta
                for (Int2D p : d.getAccessPoints()) {
                    Cell cell = (Cell) grid.getObjectsAtLocation(p).get(0);
                    if (cell.getValue() instanceof Room) {
                        ((Room) cell.getValue()).add(d);
                    }
                }
            }
        }
    }
    /**
     * Genera un pasillo o una habitacion dependiendo de si tiene nombre o no
     * Marca todo el area correspondiente a la habitación o pasillo con este
     * Añade a la lista de habitaciones o pasillos
     *
     * @param room
     */
    

    
    private void generateCorridorsAndRooms(Home home) {
        LOG.info("Generating rooms and hallways:");
        for (ubik3d.model.Room room : home.getRooms()) {
            if (rols.contains(room.getType())) {
                Room r = createRoom(floor, room);
                if (r != null) {
                    LOG.config("Room created: " + r.getName());
                    for (Int2D p : r.getPositions()) {
                        grid.setObjectLocation(new Cell(r, p), p);
                    }
                    if (ocp != null) {
                        r.createInOCP(ocp.getContextService());
                    }
                    add(r);
                }
            }
        }
    }

    /**
     * Localiza objetos
     * Nodes are not needed anymore with the use of A*
     * @deprecated
     * @param room
     */
    private void generateNodesOfAreas(Home home) {
        LOG.info("Generating rooms and hallways:");
        for (HomePieceOfFurniture h : home.getFurniture()) {
            if (h.getRol() != null && h.getRol().equals("NodeGraph")) {
                h.setVisible(false);                        //Desactivada visualización de nodos al simular
                int x = Math.round(h.getX() / cellSize);
                int y = Math.round(h.getY() / cellSize);
                Cell cell = (Cell) grid.getObjectsAtLocation(x, y).get(0);
                if (cell.getValue() instanceof SpaceArea) {
                    SpaceArea sa = (SpaceArea) cell.getValue();
                    NodeGraph ng = new NodeGraph(h.getName(), x, y, sa);
                    NodeGraphCollection.getInstance().addNodeGraph(ng);
                    sa.addCenter(ng);
                }
            }
        }
    }

    /**
     * Machaca todo el area correspondiente a el muro
     */
    private void generateFloorWalls(Home home) {
        for (ubik3d.model.Wall wall : home.getWalls()) {
            if (wall.getThickness() < cellSize) {
                wall.setThickness(cellSize * 1.5f);
            }
            Wall w = new Wall(floor, "Wall", ubik, wall);
            Int2D[] positions = w.getPositions();
            for (Int2D p : positions) {
                grid.removeObjectsAtLocation(p);
                grid.setObjectLocation(new Cell(w, p), p);
            }
        }
    }

    public void generateSpaceAreas(Home home, int floor, int cellSize) {
        LOG.info("Generating spaces:");        
        this.generateCorridorsAndRooms(home);
        LOG.info("Generating walls:");        
        this.generateFloorWalls(home);
        LOG.info("\nGenerating doors:\n");
        this.generateDoors(home);
        this.generateNodesOfAreas(home);
    }

    protected Room createRoom(int floor, ubik3d.model.Room room) {
        Room result = null;
        if (room.getType().equals("Room")) {
            result = new Room(floor, room.getName(), ubik, room.getPoints());
        } else if (room.getType().equals("Corridor")) {
            result = new Corridor(floor, room.getName(), ubik, room.getPoints());
        } else if (room.getType().equals("Office")) {
            result = new Office(floor, room.getName(), ubik, room.getPoints());
        } else if (room.getType().equals("BathRoom")) {
            result = new BathRoom(floor, room.getName(), ubik, room.getPoints());
        } else if (room.getType().equals("Kitchen")) {
            result = new Kitchen(floor, room.getName(), ubik, room.getPoints());
        } else if (room.getType().equals("LivingRoom")) {
            result = new LivingRoom(floor, room.getName(), ubik, room.getPoints());
        } else if (room.getType().equals("Lab")) {
            result = new Lab(floor, room.getName(), ubik, room.getPoints());
        } else if (room.getType().equals("MeetingRoom")) {
            result = new MeetingRoom(floor, room.getName(), ubik, room.getPoints());
        } else if (room.getType().equals("BedRoom")) {
            result = new BedRoom(floor, room.getName(), ubik, room.getPoints());
        } else if (room.getType().equals("MaintenanceRoom")) {
            result = new MaintenanceRoom(floor, room.getName(), ubik, room.getPoints());
        } else if (room.getType().equals("GeriatricBathRoom")) {
            result = new GeriatricBathRoom(floor, room.getName(), ubik, room.getPoints());
        }
        return result;
    }

    public List<SpaceArea> getSpaceAreas() {
        return spaceAreas;
    }

    @Override
    public SparseGrid2D getGrid() {
        return grid;
    }

    public boolean isWall(int x, int y) {
        Bag objects = grid.getObjectsAtLocation(x, y);
        if (objects == null) {
            return false;
        }
        Cell cell = (Cell) objects.get(0);
        if (cell.getValue() instanceof Wall) {
            return true;
        } else {
            return false;
        }

    }

    public SpaceArea getSpaceArea(int x, int y) {
        if (x < grid.getWidth() && y < grid.getHeight()) {
            Bag b = grid.getObjectsAtLocation(x, y);
            if (b != null && b.size() > 0) {
                Cell cell = (Cell) b.get(0);
                if (cell.getValue() instanceof SpaceArea) {
                    return (SpaceArea) cell.getValue();
                }
            }
        }
        return null;
    }

    public Object getSpaceArea(int x, int y, Class clase) {
        if (x < grid.getWidth() && y < grid.getHeight()) {
            Bag b = grid.getObjectsAtLocation(x, y);
            if (b != null && b.size() > 0) {
                Cell cell = (Cell) b.get(0);
                if (clase.isInstance(cell.getValue())) {
                    return cell.getValue();
                }
            }
        }
        return null;
    }

    public SpaceArea getSpaceAreaByName(String name) {
        SpaceArea result = null;
        if (name != null) {
            result = spaceAreasByName.get(name);
        }
        return result;
    }

    public SpaceArea getSpaceAreaById(String id) {
        SpaceArea result = null;
        if (id != null) {
            result = spaceAreasByName.get(id);
        }
        return result;
    }

    public boolean isObstacles(int xOri, int yOri, int xDest, int yDest) {
        return isObstaclesWithHeight(xOri, yOri, xDest, yDest, 0);
    }

    /**
     *
     * @param xOri
     * @param yOri
     * @param xDest
     * @param yDest
     * @param height Altura a partir de la cual se considera obstaculo
     * @return
     */
    public boolean isObstaclesWithHeight(int xOri, int yOri, int xDest, int yDest, int height) {
        double alpha = MovementTools.getInstance().directionInRadians(xOri, yOri, xDest, yDest);
        int x = xOri;
        int y = yOri;
        int distance = 0;
        while (MovementTools.getInstance().getDistance(x, y, xDest, yDest) > 1.5) {
            if (isObstacle(x, y, height)) {
                return true;
            }
            distance += 1.0;
            x = (int) Math.round(xOri + distance * Math.cos(alpha));
            y = (int) Math.round(yOri + distance * Math.sin(alpha));
            //System.out.println("Actual1: "+x+", "+y+ " -> "+xDest+" "+yDest);
        }
        return false;
    }

    public boolean isObstacles(int xOri, int yOri, int xDest, int yDest, int width) {
        double alpha = MovementTools.getInstance().directionInRadians(xOri, yOri, xDest, yDest);
        double alpha1 = alpha + Math.PI / 2.0;
        double alpha2 = alpha - Math.PI / 2.0;
        int x = xOri;
        int y = yOri;
        double dist = 0.0;
        while (MovementTools.getInstance().getDistance(x, y, xDest, yDest) > 1.5) {

            int xD = (int) Math.round(x + (width / 2.0) * Math.cos(alpha1));
            int yD = (int) Math.round(y + (width / 2.0) * Math.sin(alpha1));
            if (isObstacles(x, y, xD, yD)) {
                return true;
            }
            xD = (int) Math.round(x + (width / 2.0) * Math.cos(alpha2));
            yD = (int) Math.round(y + (width / 2.0) * Math.sin(alpha2));
            if (isObstacles(x, y, xD, yD)) {
                return true;
            }

            dist += 1.0;
            x = (int) Math.round(xOri + dist * Math.cos(alpha));
            y = (int) Math.round(yOri + dist * Math.sin(alpha));

        }
        return false;
    }

    /**
     *
     * @param x
     * @param y
     * @param height Más alto es un obstaculo
     * @return
     */
    public boolean isObstacle(int x, int y, int height) {
        if (grid.getObjectsAtLocation(x, y) == null || grid.getObjectsAtLocation(x, y).isEmpty()) {
            return true;
        }

        Cell cell = (Cell) grid.getObjectsAtLocation(x, y).get(0);
        if (cell != null) {
            if (cell.getValue() instanceof Wall) {
                Wall w = (Wall) cell.getValue();
                if (w.getHeight() > height) {
                    return true;
                }
                return false;
            }
            if (cell.getValue() instanceof Door) {
                if (((Door) cell.getValue()).isLocked()) {
                    return true;
                }
            }
        }
        return false;
    }

    public Door isAccessPoint(int x, int y) {
        SpaceArea sa = getSpaceArea(x, y);
        if (sa != null && sa instanceof Door) {
            Door door = (Door) sa;
            if (door.isInAccessPoint(x, y)) {
                return door;
            }
        }
        return null;
    }

    /**
     * Returns a list of SpaceArea which contain the SpaceArea whose name
     * contains the specified string (name).
     *
     * @param name the string to search for
     * @return
     */
    public List<SpaceArea> getSpaceAreasWithNameContains(String name) {
        List<SpaceArea> result = new ArrayList<SpaceArea>();
        for (SpaceArea sa : getSpaceAreas()) {
            if (sa.getName().contains(name)) {
                result.add(sa);
            }
        }
        return result;
    }
}
