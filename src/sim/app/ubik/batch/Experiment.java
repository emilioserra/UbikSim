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
package sim.app.ubik.batch;

import sim.app.ubik.Ubik;
import sim.app.ubik.behaviors.Automaton;
import sim.app.ubik.behaviors.PositionTools;
import sim.app.ubik.people.Person;
import sim.app.ubik.utils.GenericLogger;

public class Experiment extends Ubik {

	int numberOfAgents = 2;
	
	GenericLogger gl;
	
	MonitorServiceImpl ms;

	public Experiment(long seed) {
		super(seed);
	}

	@Override
	public void start() {
		super.start();
		
		
		Person pattern = PositionTools.getPerson(this, "Bob");
		System.out.println("Pattern: " + pattern.getName());
		getBuilding().getFloor(0).getPersonHandler()
				.addPersons(getNumberOfAgents(), true, pattern);
		getBuilding().getFloor(0).getPersonHandler()
				.changeNameOfAgents("Bob");

		ms = new MonitorServiceImpl(this, 60*15);
		ms.register();		
	}

	public GenericLogger getGenericLogger() {
		if(ms != null)
			return ms.getGenericLogger();
		return null;
	}

	public int getNumberOfAgents() {
		return numberOfAgents;
	}

	public void setNumberOfAgents(int numberOfAgents) {
		this.numberOfAgents = numberOfAgents;
	}
	
	
}
