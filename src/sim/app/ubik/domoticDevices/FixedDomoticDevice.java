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
import ocp.service.ContextService;
import sim.app.ubik.Ubik;
import sim.app.ubik.building.SpaceArea;
import ubik3d.model.HomePieceOfFurniture;



public abstract class FixedDomoticDevice extends DomoticDevice {
    protected SpaceArea spaceArea;

    public FixedDomoticDevice(int floor, HomePieceOfFurniture device3DModel, Ubik ubik) {
        super(floor, device3DModel, ubik);
        spaceArea = ubik.getBuilding().getFloor(floor).getSpaceAreaHandler().getSpaceArea(position.x, position.y);
    }

    public SpaceArea getSpaceArea() {
        return spaceArea;
    }
    
    @Override
	public void createInOCP(ContextService cs) {
    	super.createInOCP(cs);
    	ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), getId());
        if (spaceArea != null) {
            cei.addContextItem(new ContextItemRelation("locatedIn", spaceArea.getClass().getSimpleName(), spaceArea.getId()));
        }
        cs.setContextItems(cei);
    }

}