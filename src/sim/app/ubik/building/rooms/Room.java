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
package sim.app.ubik.building.rooms;

import ocp.service.ContextService;
import sim.app.ubik.building.connectionSpace.Door;
import java.util.ArrayList;
import java.util.List;
import ocp.service.ContextEntityItems;
import ocp.service.ContextItemInt;
import sim.app.ubik.Ubik;
import sim.app.ubik.building.SpaceArea;
import sim.app.ubik.building.connectionSpace.ConnectionSpaceInABuilding;
import sim.app.ubik.furniture.Furniture;

/**
 * Las habitaciones grandes (entre pasillos) también estan numeradas con x e y.  
 */
public class Room extends SpaceArea {

    protected List<ConnectionSpaceInABuilding> doors;
    protected List<Furniture> furniture;

    public Room(int floor, String name, Ubik ubik, float[][] pos) {
        super(floor, name, ubik, pos);
        this.doors = new ArrayList();
    }

    /**
     * Null si no hay puertas o la posición no esta en la habitacion. La habitación más cercana a una posición de una sala
     * @param x
     * @param y
     * @return
     */
    public ConnectionSpaceInABuilding getConnectionSpaceNearerTo(int x, int y) {
        ConnectionSpaceInABuilding result = null;
        if (!contains(x, y)) {
            return null;
        }
        int mindistance = Integer.MAX_VALUE;
        for (ConnectionSpaceInABuilding csiab : getConnectionSpace()) {
            int distance = (int) Math.round(csiab.getCenter().distance(x, y));
            if (distance < mindistance) {
                result = csiab;
                mindistance = distance;
            }
        }
        return result;
    }

    /**
     * Devuelve la puerta más cercana a una posición si no esta en el array de excepciones pasado como parámetro. Se usa para agentes
     * o actuadores que descartan ciertas puertas.
     * Null si no hay puertas o la posición no esta en la habitacion. La habitación más cercana a una posición de una sala
     * @param x
     * @param y
     * @return
     */
    public Door getNearerDoorWithExceptions(int x, int y, List<Door> doorExceptions) {
        Door result = null;
        if (!contains(x, y)) {
            return null;
        }
        int mindistance = (int) Math.round(getShape().getBounds2D().getWidth() + getShape().getBounds2D().getHeight()); //esta distancia es mayor a la máxima        
        for (ConnectionSpaceInABuilding csiab: doors) {
            Door d = (Door) csiab;
            int distance = (int) Math.round(d.getCenter().distance(x, y));
            if (distance < mindistance) {
                if (doorExceptions != null) {
                    if (!doorExceptions.contains(d)) {
                        result = d;
                        mindistance = distance;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void add(ConnectionSpaceInABuilding d) {
        doors.add(d);
    }

    public void addFurniture(Furniture f) {
        if(furniture == null)
            furniture = new ArrayList<Furniture>();
        furniture.add(f);
    }

    @Override
    public void createInOCP(ContextService cs) {
        System.out.println("Creando en ocp: "+getClass().getSimpleName()+"-"+id);
        ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), String.valueOf(id));
        cei.addContextItem(new ContextItemInt("centerX", getCenter().getX()));
        cei.addContextItem(new ContextItemInt("centerY", getCenter().getY()));
        cs.setContextItems(cei);
    }

    @Override
    public List<ConnectionSpaceInABuilding> getConnectionSpace() {
        return doors;
    }

    public List<Furniture> getFurniture() {
        return furniture;
    }
}
