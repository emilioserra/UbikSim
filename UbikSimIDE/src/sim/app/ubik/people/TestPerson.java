/*
 * Contact, UbikSimIDE has been developed by:
 * 
 * Juan A. Botía , juanbot@um.es
 * Pablo Campillo, pablocampillo@um.es
 * Francisco Campuzano, fjcampuzano@um.es
 * Emilio Serrano, emilioserra@um.es 
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

package sim.app.ubik.people;

import sim.app.ubik.Ubik;
import sim.app.ubik.behaviors.AutomatonTestPerson;
import sim.engine.SimState;
import ubik3d.model.HomePieceOfFurniture;

public class TestPerson extends Person {
	


	public TestPerson(int floor, HomePieceOfFurniture w, Ubik ubik) {
		super(floor, w, ubik);

	}
	
	
	public void step(SimState state) {
	    if(automaton==null) automaton = new AutomatonTestPerson(this);
            automaton.nextState(state);
	}

	
}
