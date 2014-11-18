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

import java.util.List;
import java.util.logging.Logger;


import sim.app.ubik.Ubik;
import sim.app.ubik.behaviors.PositionTools;
import sim.app.ubik.people.Person;
import sim.app.ubik.utils.GenericLogger;
import sim.engine.SimState;


public class MonitorServiceImpl implements GenericLoggerService {
	protected long maxStepToStop = 60 * 5; // 15 mintues the building should be
    private static final Logger LOG = Logger.getLogger(MonitorServiceImpl.class.getName());
											// empty!
	protected GenericLogger genericLogger;
	protected Ubik ubik;
	protected List<Person> people;

	public MonitorServiceImpl(Ubik u, long maxStepToStop) {		
		this.ubik = u;
		this.maxStepToStop = maxStepToStop;
		
		people = ubik.getBuilding().getFloor(0).getPersonHandler().getPersons();
		String[] logHeadings = new String[people.size()];
		System.out
				.print(ubik.seed()+" --------------------------------------------------->");
		for (int i = 0; i < people.size(); i++) {
			logHeadings[i] = people.get(i).getName();
			System.out.print(logHeadings[i] + " ");
		}
		System.out.println();
		
		genericLogger = new GenericLogger(logHeadings);
	}

	@Override
	public void step(SimState ss) {
		if (ubik.schedule.getSteps() == maxStepToStop
				|| !PositionTools.existAnyPersonInSpace(ubik)) {
			System.out.println("Experiment ended!");
			ubik.kill();
			return;
		}

		double toLog[] = new double[people.size()];
		System.out
				.print(" --------------------------------------------------->");
		for (int i = 0; i < people.size(); i++) {
                        LOG.info("REPLACE COMMENTS IN THIS CLASS WITH THE DATA TO BE LOGGED");
			/**toLog[i] = ((TestPerson) people.get(i))
					.getDistanceLeftToReachDestiny();
			System.out.print(toLog[i] + " ");*/
		}
		System.out.println();
		genericLogger.addStep(toLog);
	}

	@Override
	public void stop() {
	}
	
	@Override
	public void register() {
		ubik.schedule.scheduleRepeating(this, 1);
	}

	@Override
	public GenericLogger getGenericLogger() {
		return genericLogger;
	}

	public Ubik getUbik() {
		return ubik;
	}

	public long getMaxStepToStop() {
		return maxStepToStop;
	}
}
