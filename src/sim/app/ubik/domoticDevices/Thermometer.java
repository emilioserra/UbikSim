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
import ocp.service.ContextItemString;
import ocp.service.ContextService;
import sim.app.ubik.Ubik;
import sim.app.ubik.ocp.OCPProxyProducer;
import sim.engine.SimState;
import ubik3d.model.HomePieceOfFurniture;


public class Thermometer extends FixedDomoticDevice {
    private int readPeriod = 60;    // Steps
    private int cont = 0;
    protected OCPProxyProducer ocp;
    protected float lastTemperature;
    protected float temperature;

    public Thermometer(int floor, HomePieceOfFurniture device3DModel, Ubik ubik) {
        super(floor, device3DModel, ubik);
        ocp = ubik.createOCPProxyProducer("UBIK.Thermometer-"+getId());
        lastTemperature = temperature = 30.0f;
    }

    @Override
    public void createInOCP(ContextService cs) {
    	super.createInOCP(cs);
        ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), String.valueOf(id));
        cei.addContextItem(new ContextItemFloat("temperature", temperature));
        cei.addContextItem(new ContextItemString("state", "On"));
        cs.setContextItems(cei);
    }

    @Override
	public void step(SimState state) {
        cont++;
        if(cont % readPeriod == 0 && lastTemperature != temperature) {
            lastTemperature = temperature;
            if(ocp != null) {
                ocp.getContextService().setContextItem(getClass().getSimpleName(), getId(), "temperature", temperature);
            }
            cont = 0;
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
}
