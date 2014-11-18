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
import sim.app.ubik.furniture.Furniture;
import sim.app.ubik.people.Person;
import sim.engine.SimState;
import ubik3d.model.HomePieceOfFurniture;

public class PressureSensor extends FixedDomoticDevice {

	private static final String ON = "on";
	private static final String OFF = "off";
	
	private String TAG = getName()+"("+getClass().getSimpleName()+"-"+getId()+")";
	
	private Furniture furniture;
	private String lastState = OFF;
	
	public PressureSensor(int floor, HomePieceOfFurniture device3dModel, Ubik ubik) {
		super(floor, device3dModel, ubik);
		ocp = ubik.createOCPProxyProducer("UBIK.PressureSensor-" + getId());
		furniture = ubik.getBuilding().getFloor(floor).getFurnitureHandler().getFurniture(position.x, position.y);
	}

	@Override
	public void createInOCP(ContextService cs) {
		super.createInOCP(cs);
		System.out.println("Generado en OCP");
		ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), getId());
		if (furniture != null) {
			System.out.println(getClass().getSimpleName()+"-"+getId()+" locatedIn "+furniture.getClass().getSimpleName()+"-"+furniture.getId());
            cei.addContextItem(new ContextItemRelation("locatedIn", furniture.getClass().getSimpleName(), furniture.getId()));
        }
        // cei.addContextItem(new ContextItemBoolean("activity", activity));
        //cei.addContextItem(new ContextItemRelation("bluetoothDetectedBy", "", ""));
        cei.addContextItem(new ContextItemString("state", OFF));
        cs.setContextItems(cei);
	}

	@Override
	public void step(SimState state) {
		if(furniture != null) {
			for(Person p: ubik.getBuilding().getFloor(floor).getPersonHandler().getPersons()) {
				if(furniture.contains(p.getPosition().x, p.getPosition().y)) {
					if(lastState.equals(OFF)) {	
						if(ocp != null) {
							ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), getId());
							cei.addContextItem(new ContextItemString("state", ON));
							ocp.getContextService().setContextItems(cei);
						}
                                                
						lastState = ON;
						System.out.println(TAG+": "+ON);
						return;
					} else {
						return;
					}
				}
			}
			if(lastState.equals(ON)) {
				if(ocp != null) {
					ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), getId());
					cei.addContextItem(new ContextItemString("state", OFF));
					ocp.getContextService().setContextItems(cei);
				}
                                
				lastState = OFF;
				System.out.println(TAG+": "+OFF);
			}
		}
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

}
