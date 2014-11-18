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

package sim.app.ubik.building;

import java.util.List;
import ocp.service.ContextService;
import sim.app.ubik.Ubik;
import sim.app.ubik.building.connectionSpace.ConnectionSpaceInABuilding;


public class Wall extends SpaceArea {
    protected ubik3d.model.Wall wall;

    public Wall(int floor, String name, Ubik ubik, ubik3d.model.Wall wall) {
        super(floor, name, ubik, wall.getPoints());
        this.wall = wall;
    }

    @Override
    public void createInOCP(ContextService cs) {
    }

    @Override
    public void add(ConnectionSpaceInABuilding d) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ConnectionSpaceInABuilding> getConnectionSpace() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public float getHeight() {
        return wall.getHeight();
    }
}
