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
package sim.app.ubik.building.connectionSpace;

import java.util.List;
import ocp.service.ContextService;
import sim.app.ubik.Ubik;

/**
 * Al principio se planteo darle orientación, actualmente todas las escaleras se muestran con orientación derecha. Esto es, se entra por la izquierda.
 */
public class Stairs extends ConnectionSpaceInABuilding {

    public static int STAIRS_WIDTH = 2;
    /*Longitud de tramo de escaleras, por ahora fijado a 10*/
    public static int STAIRS_LONG = 10;
    protected static int grid_width;
    protected static int grid_height;

    public Stairs(int floor, String name, Ubik ubik, float angle, float[][] points) {
        super(floor, name, ubik, angle, points);
    }

    public static int getWidth() {
        return grid_width;

    }

    public static int getHeight() {
        return grid_height;
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

	@Override
	public void createAccessPoints() {
	}
}
