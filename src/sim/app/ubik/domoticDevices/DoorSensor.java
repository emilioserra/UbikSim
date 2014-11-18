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

import ocp.service.ContextEntityItems;
import ocp.service.ContextItemRelation;
import ocp.service.ContextItemString;
import ocp.service.ContextService;
import sim.app.ubik.Ubik;
import sim.app.ubik.building.connectionSpace.Door;
import sim.engine.SimState;
import ubik3d.model.HomePieceOfFurniture;

public class DoorSensor extends DomoticDevice {

	private static final String OPENED = "opened";
	private static final String CLOSED = "closed";
	
	private Door door;
	private boolean lastDoorState;
	private String TAG = getName()+"("+getClass().getSimpleName()+"-"+getId()+")";
	
	public DoorSensor(int floor, HomePieceOfFurniture device3dModel, Ubik ubik) {
		super(floor, device3dModel, ubik);
		door = (Door) ubik.getBuilding().getFloor(floor).getSpaceAreaHandler().getSpaceArea(position.x, position.y, Door.class);
		System.out.println(getClass().getSimpleName()+"-"+getId()+" locatedIn "+door.getClass().getSimpleName()+"-"+door.getId());
		lastDoorState = door.isOpened();
		ocp = ubik.createOCPProxyProducer("UBIK.DoorSensor-" + getId());
		System.out.println("***************************DoorSensor: asociado a puerta "+door.getName());
	}
	
	@Override
	public void createInOCP(ContextService cs) {
		super.createInOCP(cs);
		ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), getId());

        if (door != null) {
            cei.addContextItem(new ContextItemRelation("locatedIn", door.getClass().getSimpleName(), door.getId()));
        }
        // cei.addContextItem(new ContextItemBoolean("activity", activity));
        // cei.addContextItem(new ContextItemRelation("bluetoothDetectedBy", "", ""));
        cei.addContextItem(new ContextItemString("state", OPENED));
        cs.setContextItems(cei);
	}

	@Override
	public void step(SimState state) {
		if(lastDoorState != door.isOpened()) {
			lastDoorState = door.isOpened();
			System.out.println(getName()+" abierta: "+lastDoorState);
			if (door != null) {
				System.out.println("ocp = "+ocp);
				if(ocp != null) {
					ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), getId());
					if(lastDoorState) {						
						cei.addContextItem(new ContextItemString("state", OPENED));
					} else {
						cei.addContextItem(new ContextItemString("state", CLOSED));
					}
					System.out.println(cei);
		            ocp.getContextService().setContextItems(cei);
				}
	        }
		}
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
	}

}
