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

import java.util.logging.Logger;
import sim.app.ubik.building.rooms.Corridor;
import sim.app.ubik.building.rooms.Room;
import sim.app.ubik.building.connectionSpace.Door;
import sim.app.ubik.Ubik;
import sim.app.ubik.domoticDevices.*;

import sim.util.Int2D;

import ubik3d.model.Home;
import sim.app.ubik.building.connectionSpace.Stairs;
import sim.app.ubik.building.temperature.TemperatureHandler;
import sim.app.ubik.furniture.FurnitureHandler;

import sim.app.ubik.people.Person;
import sim.app.ubik.people.PersonHandler;
import sim.app.ubik.utils.ElementsHandler;

/**
 * La planta contiene pasillos y habtaciones. Estas a su vez puertas y ventanas. Las entidades superiores determinan las posiciones (las habitaciones las de puertas y ventanas por ejemplo) pero son las entidades inferiores las que deben dibujarse marcando en cells el espacio especial
 * 
 */
public class OfficeFloor {
    
        private boolean clearExecutedOnce=false;

    public static int cellSize;
    protected int floor;
    /*Variables de configuración del espacio con valores por defecto*/
    public static int GRID_HEIGHT = 100;
    public static int GRID_WIDTH = 100;
    public static Ubik ubik;
    protected static int ignitionTemperature = 100;    

    private PersonHandler personHandler;    // Gestor y Creador de personas
    private DeviceHandler deviceHandler;    // Gestor y Creador de dispositivos
    private SpaceAreaHandler spaceAreaHandler;  //
    private FurnitureHandler furnitureHandler;
    private TemperatureHandler temperatureHandler;

    private Home home;
    private String nameHome;
    private static final Logger LOG = Logger.getLogger(OfficeFloor.class.getName());
    
   

    public OfficeFloor(Home home, int floor, Ubik simulation) {
    	LOG.config("OfficeFloor("+floor+")");
        this.home = home;
        this.nameHome = home.getName().substring(home.getName().lastIndexOf("\\")+1,home.getName().lastIndexOf("."));

        OfficeFloor.cellSize = simulation.getCellSize();
        this.floor = floor;
        OfficeFloor.ubik = simulation;

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = 0;
        int maxY = 0;
        for (ubik3d.model.Room r:home.getRooms()) {   //Calculo del tamaño de los grids
            float[][] points = r.getPoints();
            for (int j = 0; j < points.length; j++) {
                if (points[j][0] < minX) {
                    minX = (int) points[j][0];
                }
                if (points[j][0] > maxX) {
                    maxX = (int) points[j][0];
                }
                if (points[j][1] < minY) {
                    minY = (int) points[j][1];
                }
                if (points[j][1] > maxY) {
                    maxY = (int) points[j][1];
                }
            }
        }
        
        GRID_WIDTH = maxX / cellSize;
        GRID_HEIGHT = maxY / cellSize;   //Tenemos el tamaño del grid, lo creamos

        LOG.info("Creating floor "+floor+" with dimensions: "+GRID_WIDTH + ", " + GRID_HEIGHT);
        spaceAreaHandler = new SpaceAreaHandler(ubik, floor, GRID_WIDTH, GRID_HEIGHT);
        personHandler = new PersonHandler(ubik, floor, GRID_WIDTH, GRID_HEIGHT);
        furnitureHandler = new FurnitureHandler(ubik, floor, GRID_WIDTH, GRID_HEIGHT);
        deviceHandler = new DeviceHandler(ubik, floor, GRID_WIDTH, GRID_HEIGHT);        
        //temperatureHandler = new TemperatureHandler(ubik, floor, GRID_WIDTH, GRID_HEIGHT, 30);
    }

    /**
     * Imprime por pantalla la casa en ASCII
     */
    public void printHome() {
        for (int y = 0; y <= GRID_HEIGHT; y++) {
            for (int x = 0; x <= GRID_WIDTH; x++) {
                if (spaceAreaHandler.getGrid().getObjectsAtLocation(x, y) != null) {
                    Cell cell = (Cell)spaceAreaHandler.getGrid().getObjectsAtLocation(x, y).get(0);
                    SpaceArea sa = (SpaceArea)cell.getValue();
                    
                    if (spaceAreaHandler.isAccessPoint(x, y) != null) {
                        System.out.print("@");
                    } else if (sa instanceof Wall) {
                        System.out.print("*");
                    } else if (sa instanceof Door) {
                        System.out.print("P");
                    } else if (sa instanceof Stairs) {
                        System.out.print("E");
                    } /*else if (c.isDesk()) {
                        System.out.print("M");
                    }*/ else if (sa instanceof Corridor) {
                        System.out.print("C");
                    } else if (sa instanceof Room) {
                        if(sa.getName() != null)
                            System.out.print(sa.getName().substring(0, 1));
                        else
                            System.out.print("X");
                    } else {
                        System.out.println(" ");
                    }
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println("");
        }
        System.out.println(spaceAreaHandler.getInstancesOf(Room.class).size() + " habitaciones y " + spaceAreaHandler.getInstancesOf(Corridor.class).size() + " pasillos");
    }

    public void createEntities() {
        
        spaceAreaHandler.generateSpaceAreas(home, floor, cellSize);
        //temperatureHandler.generateTemperatureAgents(cellSize);
        personHandler.generatePeople(floor, cellSize);        
        furnitureHandler.generateFurniture(floor, cellSize);
        deviceHandler.generateDevices(floor, cellSize);
        
        
    }
    
    public void clear() throws InterruptedException {
    	LOG.severe("OfficeFloor.clear()");
       
        spaceAreaHandler.clear();
        deviceHandler.clear();
        personHandler.clear();
        furnitureHandler.clear();
        
        //temperatureHandler.clear();
        
        /**
         * Look out!! it does not work if its called more than once for each simulation
         *
        if(!this.clearExecutedOnce){
            
            clearExecutedOnce=true; 
            extraClear();
        }*/
    }
        

    /**
     * Compara si dos posiciones pertenecen al mismo pasillo. Si una no es un pasillo siempre es false.
     * @param pos1
     * @param pos2
     * @return
     */
    public boolean sameCorridor(Int2D pos1, Int2D pos2) {
        SpaceArea sa1 = spaceAreaHandler.getSpaceArea(pos1.x, pos1.y);
        SpaceArea sa2 = spaceAreaHandler.getSpaceArea(pos2.x, pos2.y);
        if(sa1 != null && sa2 != null && sa1 == sa2)
            return true;
        return false;
    }

    public boolean isWall(int x, int y) {
        return spaceAreaHandler.isWall(x, y);
    }

    public Corridor getCorridor(int x, int y) {
        SpaceArea sa = spaceAreaHandler.getSpaceArea(x, y);
        if(sa != null && sa instanceof Corridor)
            return (Corridor)sa;
        return null;
    }

    /**
     * Devuelve la habitación a la que pertence una posición del grid de celdas, por ejemplo para (10,10) devolvería la habitación (0,0) en el array de habitaicones.
     * Suponiendo que una habitación tiene más de 10 de ancho y largo.
     * @param x
     * @param y
     * @return
     */
    public Room getRoom(int x, int y) {
        SpaceArea sa = spaceAreaHandler.getSpaceArea(x, y);
        if(sa != null && sa instanceof Room)
            return (Room)sa;
        return null;
    }

    public boolean isCorridor(int x, int y) {
        SpaceArea sa1 = spaceAreaHandler.getSpaceArea(x, y);
        if(sa1 != null && sa1 instanceof Corridor)
            return true;
        return false;
    }

    public boolean isInDoor(int x, int y) {
        SpaceArea sa1 = spaceAreaHandler.getSpaceArea(x, y);
        if(sa1 != null && sa1 instanceof Door)
            return true;
        return false;
    }

    public boolean isObstacle(Person p, int x, int y) {
        SpaceArea sa1 = spaceAreaHandler.getSpaceArea(x, y);
        if(sa1 != null && sa1 instanceof Wall)
            return true;
        Door door = (Door) spaceAreaHandler.getSpaceArea(x, y, Door.class);
        if(door != null && door.isLocked()) {
        	return true;
        }
        Person person = personHandler.getPersonIn(x, y);
        if(person != null && person != p)
            return true;
        return false;
    }

    public Home getHome() {
        return home;
    }

    public ElementsHandler getHandler(Object o) {
    	if(personHandler.canHandler(o))
    		return personHandler;
    	if(furnitureHandler.canHandler(o))
    		return furnitureHandler;
    	if(deviceHandler.canHandler(o))
    		return deviceHandler;
    	if(spaceAreaHandler.canHandler(o))
    		return spaceAreaHandler;
    	
    	return null;
    }
    
    public PersonHandler getPersonHandler() {
        return personHandler;
    }

    public FurnitureHandler getFurnitureHandler() {
        return furnitureHandler;
    }

    public DeviceHandler getDeviceHandler() {
        return deviceHandler;
    }

    public SpaceAreaHandler getSpaceAreaHandler() {
        return spaceAreaHandler;
    }

    public TemperatureHandler getTemperatureHandler() {
        return temperatureHandler;
    }
    
    

    private void extraClear() {
           
        
        spaceAreaHandler.generateSpaceAreas(home, floor, cellSize);
        //temperatureHandler.generateTemperatureAgents(cellSize);
        //printHome();
        personHandler.generatePeople(floor, cellSize);        
        furnitureHandler.generateFurniture(floor, cellSize);
        deviceHandler.generateDevices(floor, cellSize);
        
        
    }
    
    
    
}
