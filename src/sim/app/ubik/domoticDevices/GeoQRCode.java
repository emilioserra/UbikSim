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
import ocp.service.ContextItemFloat;
import ocp.service.ContextItemInt;
import ocp.service.ContextItemString;
import ocp.service.ContextService;
import sim.app.ubik.Ubik;
import sim.engine.SimState;
import ubik3d.model.HomePieceOfFurniture;


public class GeoQRCode extends FixedDomoticDevice {

	protected float angle;
	
	/**
	 * @param floor
	 * @param device3dModel
	 * @param ubik
	 */
	public GeoQRCode(int floor, HomePieceOfFurniture device3dModel, Ubik ubik) {
		super(floor, device3dModel, ubik);
		angle = (float)(device3DModel.getAngle() + Math.PI/2);
		/*if(angle < 0)
			angle = (float) (2*Math.PI + angle);*/
	}

	/* (non-Javadoc)
	 * @see sim.engine.Steppable#step(sim.engine.SimState)
	 */
	@Override
	public void step(SimState arg0) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void createInOCP ( ContextService cs) {
		super.createInOCP(cs);
		ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), getId()); 
		cei.addContextItem(new ContextItemInt("x", (int) Math.round(device3DModel.getX())));
		cei.addContextItem(new ContextItemInt("y", (int) Math.round(device3DModel.getY())));
		cei.addContextItem(new ContextItemFloat("angle", angle));
		cei.addContextItem(new ContextItemInt("floor", 1));
		cei.addContextItem(new ContextItemString("name", getName()));
		cs.setContextItems (cei) ;
	}

	/* (non-Javadoc)
	 * @see sim.engine.Stoppable#stop()
	 */
	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}
	
	public float getAngle() {
		return angle;
	}

	public String toString() {
		return getName()+":"+getFloor();
	}
}
