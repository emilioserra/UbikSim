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

import java.util.ArrayList;
import java.util.List;
import ocp.service.ContextEntityItems;
import ocp.service.ContextItemString;
import ocp.service.ContextService;
import sim.app.ubik.Ubik;
import sim.engine.SimState;
import ubik3d.model.HomePieceOfFurniture;


public class PC extends FixedDomoticDevice {

    protected List<ServicePC> servicios;

    public PC(int floor, HomePieceOfFurniture device3DModel, Ubik ubik) {
        super(floor, device3DModel, ubik);
        servicios = new ArrayList<ServicePC>();
        servicios.add(new MasterBluetooth(this));
    }

    @Override
    public void createInOCP(ContextService cs) {
    	super.createInOCP(cs);
        ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), String.valueOf(id));
        cei.addContextItem(new ContextItemString("state", "Activo"));
        cs.setContextItems(cei);

        for(ServicePC spc:servicios) {
            spc.createInOCP(cs);
        }
    }

    @Override
	public void step(SimState state) {     
        for(ServicePC spc:servicios) {
            spc.step(state);
        }
    }

    @Override
	public void stop() {
        for(ServicePC spc:servicios) {
            spc.stop();
        }
    }

    public List<ServicePC> getServicios() {
        return servicios;
    }
}
