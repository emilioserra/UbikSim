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
package sim.app.ubik.graph;

import sim.app.ubik.building.SpaceArea;
import sim.util.Int2D;


public class Node {
    private static int idCreator = 0;

    protected Int2D position;
    protected int id;
    protected SpaceArea spaceArea;

    Node(int xCenter, int yCenter, SpaceArea sa) {
        this.position = new Int2D(xCenter, yCenter);
        this.id = idCreator++;
        this.spaceArea = sa;
    }

    public String getNameId() {
        return spaceArea.getName() + "-" + id;
    }

    public int getId() {
        return id;
    }

    public static int getIdCreator() {
        return idCreator;
    }

    public SpaceArea getSpaceArea() {
        return spaceArea;
    }

    public Int2D getPosition() {
        return position;
    }

    @Override
    public String toString() {
        return getNameId()+" "+getPosition();
    }


}
