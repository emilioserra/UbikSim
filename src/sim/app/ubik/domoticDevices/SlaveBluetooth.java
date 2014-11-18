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

import ocp.service.ContextService;
import sim.app.ubik.Ubik;
import sim.engine.SimState;
import ubik3d.model.HomePieceOfFurniture;


public class SlaveBluetooth extends PortableDomoticDevice {

    public SlaveBluetooth(int floor, HomePieceOfFurniture device3DModel, Ubik ubik) {
        super(floor, device3DModel, ubik);
    }

    @Override
	public void step(SimState state) {
    }

    @Override
	public void stop() {
    }

    @Override
	public void moveTo(float x, float y) {
		setPosition((int)x/ubik.getCellSize(), (int)y/ubik.getCellSize());
	}

	@Override
	public void elevate(float elevation) {
		getDevice3DModel().setElevation(elevation);
	}

    @Override
    public void createInOCP(ContextService cs) {
    	super.createInOCP(cs);
    }
}
