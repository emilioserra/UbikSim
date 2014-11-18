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

package sim.app.ubik.behaviors;

import java.util.ArrayList;
import sim.app.ubik.people.Person;
import sim.engine.SimState;



public class DoNothing extends SimpleState {
    /**
     * Esta clase es un estado de comportamiento. Como no tiene autómata subordinado se extiende SimpleState.
     * Se le puede pasar prioridad, duración  y nombre. El nombre sirve para implementar varios estados con una misma clase.

     * @param personImplementingAutomaton
     * @param name
     */
    public DoNothing(Person personImplementingAutomaton, int priority, int duration,  String name){
        super(personImplementingAutomaton,priority,duration,name);

    }
  /**
   * No hace nada a parte de esperar a que se acabe la duración (con -1 nunca acaba, en cuyo caso debe tener una prioridad baja
   * para que se tomen otros estados).
   * @param state
   */
    @Override
    public void nextState(SimState state){

    }



}
