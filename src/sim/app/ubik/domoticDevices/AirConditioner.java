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

import java.awt.Color;
import ocp.service.ContextEntityItems;
import ocp.service.ContextItemFloat;
import ocp.service.ContextItemRelation;
import ocp.service.ContextItemString;
import ocp.service.ContextService;
import sim.app.ubik.Ubik;
import sim.app.ubik.ocp.domoticDevice.OCPProxyAirConditioner;
import sim.engine.SimState;
import ubik3d.model.HomePieceOfFurniture;


public class AirConditioner extends FixedDomoticDevice {
    private ContextService cs;
    private boolean working;
    private float temperature;

    private int timeWorking = 60;

    private OCPProxyAirConditioner proxyAirConditioner;

    public AirConditioner(int floor, HomePieceOfFurniture device3DModel, Ubik ubik) {
        super(floor,device3DModel,ubik);
        this.working = false;
        this.temperature = 25;
        if(ubik.isIpOCP() != null)
            proxyAirConditioner = new OCPProxyAirConditioner(getId(), "localhost", this);
    }

    @Override
    public void createInOCP(ContextService cs) {
    	super.createInOCP(cs);
        ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), getId());
        if(spaceArea != null) {
            cei.addContextItem(new ContextItemRelation("locatedIn", spaceArea.getClass().getSimpleName(), spaceArea.getId()));
        }
        //cei.addContextItem(new ContextItemRelation("bluetoothDetectedBy", "", ""));
        if(working) {
            cei.addContextItem(new ContextItemString("state", "On"));
            device3DModel.setColor(Color.RED.getRGB());
        } else {
            cei.addContextItem(new ContextItemString("state", "Off"));
            device3DModel.setColor(Color.GREEN.getRGB());
        }
        cei.addContextItem(new ContextItemFloat("temperature", temperature));
        cs.setContextItems(cei);
        System.out.println(cei);
        //cs.register(getClass().getSimpleName(), getId(), this);
    }

    @Override
	public void step(SimState state) {
        if(timeWorking == 0)
            working = false;
        if(working) {
            timeWorking--;
        }
    }

    @Override
	public void stop() {
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public boolean isWorking() {
        return working;
    }

    public void setWorking(boolean working) {
        //SpaceArea sa = ubik.getBuilding().getFloor(floor).getSpaceAreaHandler().getSpaceArea(position.x, position.y);
        //ubik.getBuilding().getFloor(floor).getHome().getRooms().get(0).setFloorColor(Color.RED.getRGB());
        this.working = working;
        if(working) {
            device3DModel.setColor(Color.RED.getRGB());
            timeWorking = 60;
        } else {
            device3DModel.setColor(Color.GREEN.getRGB());
        }
    }
}
