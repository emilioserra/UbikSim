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

import java.util.ArrayList;
import java.util.List;

import sim.app.ubik.utils.GenericLogger;

public class SimBatch {
	protected int numberOfExperiments = 10;
	protected List<GenericLogger> listOfResults;
	
	public SimBatch() {
		listOfResults = new ArrayList<GenericLogger>();
	}
	
	public SimBatch(int numberOfExperiments) {
		this();
		this.numberOfExperiments = numberOfExperiments;
	}

	public void run() {
		
		for (int i = 0; i < numberOfExperiments; i++) {
			Experiment experiment = new Experiment(i*1000);
			experiment.launch0D(experiment, new String[0]);
			GenericLogger gl1 = experiment.getGenericLogger();
			listOfResults.add(gl1);
			System.out.println("EXPERIMENT " + i + " RESULTS ");
			System.out.println(gl1.toString());
		}

		GenericLogger gl2 = GenericLogger.getMean(listOfResults);
		System.out.println("EXPERIMENTS RESULTS , MEAN");
		System.out.println(gl2.toString());

		GenericLogger.getStandardDeviation(listOfResults);
		System.out.println("EXPERIMENTS RESULTS , STANDARD DEVIATION");
		System.out.println(gl2.toString());
		
		System.exit(0);
	}	
	
	public static void main(String[] args) {
		SimBatch simBatch;
		if(args.length > 0) {
			simBatch = new SimBatch(Integer.parseInt(args[0]));
		} else {
			simBatch = new SimBatch();
		}
		simBatch.run();
	}
}
