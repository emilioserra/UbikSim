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

package sim.app.ubik.domoticDevices;

import java.util.Observable;
import ocp.service.ContextEntityItems;
import ocp.service.ContextItemString;
import ocp.service.ContextService;
import sim.app.ubik.Ubik;
import sim.app.ubik.ocp.OCPProxyProducer;
import sim.app.ubik.utils.ElementsHandler;
import sim.engine.Schedule;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.field.grid.SparseGrid2D;
import sim.portrayal.Oriented2D;
import sim.util.MutableInt2D;
import ubik3d.model.HomePieceOfFurniture;


public abstract class DomoticDevice extends Observable implements Steppable, Stoppable, Oriented2D {
    private static int IDgenerator = 1;
    protected String id;

    protected String name;
    protected int floor;
    protected Ubik ubik;
    
    protected boolean activated = true;
    
    protected double angle;
    protected MutableInt2D position;
    protected int cellSize;
    protected ElementsHandler elementsHandler;
    protected SparseGrid2D grid;

    protected HomePieceOfFurniture device3DModel;

    // OCP service
    protected OCPProxyProducer ocp;
    

    public DomoticDevice(int floor, HomePieceOfFurniture device3DModel, Ubik ubik){
        this.floor = floor;
        this.device3DModel = device3DModel;
        this.ubik = ubik;

        this.position = new MutableInt2D();
        this.position.x = Math.round(device3DModel.getX()/ubik.getCellSize());
        this.position.y = Math.round(device3DModel.getY()/ubik.getCellSize());
        
        this.id = String.valueOf(IDgenerator++);
        this.name = device3DModel.getName();
        this.cellSize = ubik.getCellSize();

        angle = device3DModel.getAngle()-Math.PI/2.0;

        this.elementsHandler = ubik.getBuilding().getFloor(floor).getHandler(this);
        this.grid = elementsHandler.getGrid();
        this.grid.setObjectLocation(this, position.x, position.y);
    }

    public void fixStopable(Schedule schedule) {
        schedule.scheduleRepeating(this);
    }

    @Override
	public double orientation2D() {
        return this.angle;
    }

    public MutableInt2D getPosition(){
        return position;
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public HomePieceOfFurniture getDevice3DModel() {
        return device3DModel;
    }

    public int getFloor() {
        return floor;
    }

    public void setPosition(int x, int y) {
        this.position.x = x;
        this.position.y = y;
        device3DModel.setX((this.position.x * cellSize) + (cellSize / 2));
        device3DModel.setY((this.position.y * cellSize) + (cellSize / 2));
        grid.setObjectLocation(this, position.x, position.y);
    }

    public String getName() {
        return name;
    }
    
    public String getId() {
        return id;
    }

    public void createInOCP(ContextService cs) {
    	ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), getId());
        cei.addContextItem(new ContextItemString("ubikName", getName()));
        cs.setContextItems(cei);
    }

    public Ubik getUbik() {
        return ubik;
    }    
}
