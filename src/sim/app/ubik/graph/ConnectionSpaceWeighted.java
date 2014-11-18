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

import annas.graph.WeightedInterface;

/**
 * Representa el arco que atraviesa una puerta. Por ello, dependiendo si está
 * o no cerrada con llave, su peso puede ser muy alto.
 * 
 */
public class ConnectionSpaceWeighted implements WeightedInterface {

    private double connectionSpaceWight = 1000;

    private NodeAccessPoint tail;
    private NodeAccessPoint head;

    public ConnectionSpaceWeighted(NodeAccessPoint tail, NodeAccessPoint head) {
        this.tail = tail;
        this.head = head;
    }

    @Override
	public Double evaluate() {
        if(tail.getConnectionSpaces().isLocked()) {
            return Double.MAX_VALUE;
        } else {
            return connectionSpaceWight;
        }
    }
}
