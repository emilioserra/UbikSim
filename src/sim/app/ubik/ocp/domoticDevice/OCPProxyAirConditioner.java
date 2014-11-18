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
package sim.app.ubik.ocp.domoticDevice;

import ocp.service.ContextEntityItems;
import ocp.service.ContextItem;
import org.osgi.framework.BundleContext;
import sim.app.ubik.domoticDevices.AirConditioner;
import sim.app.ubik.ocp.OCPProxyConsumer;


public class OCPProxyAirConditioner extends OCPProxyConsumer {

    protected AirConditioner airConditioner;

    public OCPProxyAirConditioner(BundleContext bundleContext, String id, AirConditioner airConditioner) {
        super(bundleContext, id);
        this.airConditioner = airConditioner;
    }

    public OCPProxyAirConditioner(String id, String url, AirConditioner airConditioner) {
        super(id, url);
        this.airConditioner = airConditioner;
        cs.register(airConditioner.getClass().getSimpleName(), airConditioner.getId(), this);
    }

    @Override
	public void notifyContextChange(ContextEntityItems cei) {
        if (cei.getLocalId().equals(airConditioner.getId())) {
            ContextItem ci = cei.getContextItem("state");
            if (ci != null) {
                String state = ci.getValueString();
                if (state.equals("On")) {
                    airConditioner.setWorking(true);
                } else {
                    airConditioner.setWorking(false);
                }
            }
        }
    }

    @Override
	public void notifyNewContext(ContextEntityItems cei) {
    }
}
