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

package sim.app.ubik.building.temperature;

import sim.app.ubik.Ubik;
import sim.engine.SimState;
import sim.util.Int2D;
import sim.util.IntBag;


public class AmbientTemperatureAgent extends TemperatureAgent {    
    protected IntBag posX = new IntBag();
    protected IntBag posY = new IntBag();

    protected int updateStep = 60;

    public AmbientTemperatureAgent(Int2D position,int floor, Ubik ubik) {
        super(position,floor,ubik);
        temperature = th.ambientTemperature;
    }

    @Override
	public void step(SimState state) {
        
    }

    @Override
	public void stop() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void updateTemperature() {
        float mean = 0;
        float cont = 0;
        th.grid.getNeighborsMaxDistance(position.x, position.y, 1, false, posX, posY);
        for(int i = 0; i < posX.numObjs; i++) {
            Object o = th.grid.getObjectsAtLocation(posX.objs[i], posY.objs[i]);
            if(o instanceof TemperatureAgent) {
                mean += ((TemperatureAgent)o).temperature;
                cont ++;
            }
        }
        temperature = mean/cont;
    }

}
