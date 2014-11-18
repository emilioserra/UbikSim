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

package sim.app.ubik.building.rooms;

import ocp.service.ContextEntityItems;
import ocp.service.ContextItemInt;
import ocp.service.ContextItemString;
import ocp.service.ContextService;
import sim.app.ubik.Ubik;


public class Office extends Room {
    public Office(int floor, String name, Ubik ubik, float[][] pos) {
        super(floor, name, ubik, pos);
    }

    @Override
    public void createInOCP(ContextService cs) {
        System.out.println("Creando en ocp: "+getClass().getSimpleName()+"-"+id);
        ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), String.valueOf(id));
        cei.addContextItem(new ContextItemInt("centerX", getCenter().getX()));
        cei.addContextItem(new ContextItemInt("centerY", getCenter().getY()));
        cei.addContextItem(new ContextItemString("name", getName()));
        cs.setContextItems(cei);
    }
}
