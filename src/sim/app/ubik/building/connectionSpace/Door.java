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
package sim.app.ubik.building.connectionSpace;

import java.awt.Color;
import java.util.List;
import ocp.service.ContextEntityItems;
import ocp.service.ContextItemInt;
import ocp.service.ContextService;
import sim.app.ubik.Ubik;
import sim.util.Int2D;
import sim.util.MutableInt2D;
import ubik3d.model.HomePieceOfFurniture;

/**
 * La clase door no se representa en el mapa,eso se hace mediante el grid de Cell.
 * Sirve para que los agentes puedan localizar puertas y operar con ellas. 
 * Hay un grid dedicado a este tipo de objetos para que sea fácil localizar puertas cercanas en todo momento.
 */
public class Door extends ConnectionSpaceInABuilding {
    
    protected int doorWidth;
    protected MutableInt2D initialPos;
    protected boolean horizontal;
    protected Int2D goInDirection;

    private HomePieceOfFurniture door;

    public Door(int floor, Ubik ubik, HomePieceOfFurniture w) {
        super(floor, w.getName(), ubik, w.getAngle(), w.getPoints());
        this.door = w;
        open();
        if(door.getMetadata() != null)
        	if(door.getMetadata().indexOf("locked") >= 0)
        		lock();
        	else if(door.getMetadata().indexOf("closed") >= 0)
        		close();
    }

    @Override
    public String toString(){
        return getName()+getId();
    }

    /**
     * Abrir la puerta
     */
    @Override
    public void open() {
        door.setColor(Color.GREEN.getRGB());
        opened = true;
    }

    @Override
    public void close() {
    	door.setColor(Color.YELLOW.getRGB());
        opened = false;
    }
    
    @Override
    public void lock() {
        if(isOpened())
            close();
        locked = true;
        door.setColor(Color.RED.getRGB());
    }

    @Override
    public void unlock() {       
        locked = false;
        close();
    }

    /**
     * Dirección (un par de valores entre -1 y 0 que le sumas a la direccion actual) para ir dentro una vez estas en un umbral de la puerta
     * @param goIn si esta a false da la direccion para ir fuera
     * @return
     *
    public Int2D getDirectionToGoIn(boolean goIn) {
        if (!goIn) {
            return getDirecctionToEnter(spaceArea);
        }
        return getDirecctionToExit(spaceArea);
    }
*/
    /*
    public Int2D getDirecctionToExit(SpaceArea area) {
        Int2D p1 = getPositionToExit(area);
        Int2D p2 = getPositionToEnter(area);
        double angle = p1.getAngleOfVector(p2);
        int x = (int)Math.round(Math.cos(angle));
        int y = (int)Math.round(Math.sin(angle));
        Int2D result = new Int2D(x, y);
        return result;
    }

    public Int2D getDirecctionToEnter(SpaceArea area) {
        Int2D p1 = getPositionToEnter(area);
        Int2D p2 = getPositionToExit(area);
        double angle = p1.getAngleOfVector(p2);
        int x = (int)Math.round(Math.cos(angle));
        int y = (int)Math.round(Math.sin(angle));
        Int2D result = new Int2D(x, y);
        return result;
    }
     */
    @Override
    public void createAccessPoints() {
        accessPoints = new Int2D[2];
        accessPoints[0] = getBoundPoint(getAngle());
        accessPoints[1] = getBoundPoint((float)(getAngle()+Math.PI));
    }

    @Override
    public void createInOCP(ContextService cs) {
    	super.createInOCP(cs);
        ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), String.valueOf(id));
        cei.addContextItem(new ContextItemInt("centerX", getCenter().getX()));
        cei.addContextItem(new ContextItemInt("centerY", getCenter().getY()));
        cs.setContextItems(cei);
    }

    @Override
    public void add(ConnectionSpaceInABuilding d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ConnectionSpaceInABuilding> getConnectionSpace() {
        return null;
    }

    /*
    public Int2D getPositionToExit(SpaceArea area) {
        Int2D result = null;
        result = getNearestAccessPointFrom(area.getCenter().getX(), area.getCenter().getY());
        return result;
    }

    public Int2D getPositionToEnter(SpaceArea area) {
        Int2D result = null;
        double maxDistance = 0.0;
        for(int i = 0; i < accessPoints.length; i++) {
            double distance = accessPoints[i].distance(area.getCenter());
            if(distance > maxDistance) {
                maxDistance = distance;
                result = accessPoints[i];
            }
        }
        return result;
    }
     */
    public HomePieceOfFurniture getModel() {
    	return door;
    }
}
