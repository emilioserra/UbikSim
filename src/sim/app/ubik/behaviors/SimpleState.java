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

/**
 * Estados que no tienen un autómata asociado. Es decir, autómatas más bajos de la jerarquía.
 * En ellos getDefaultState y createNewTransitions devuelven null. Por otro lado, nextState
 * debe ser redefinido.
 */
public abstract class  SimpleState extends Automaton{

    public SimpleState(Person personImplementingAutomaton, int priority, int duration, String name){
        super(personImplementingAutomaton,priority, duration, name );
    }

    @Override
    public Automaton getDefaultState(SimState simState) {
       return null;

    }

    @Override
    public ArrayList<Automaton> createNewTransitions(SimState simState) {
        return null;
    }

    /**
     * Se obliga a implementar un nextState ya que en automaton depende del autómata subordinado.
     * @param state
     */
    @Override
    public abstract void nextState(SimState state);

}
