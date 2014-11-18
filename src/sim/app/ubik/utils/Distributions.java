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
package sim.app.ubik.utils;

import ec.util.MersenneTwisterFast;

public class Distributions {


    /*MersenneTwisterFast rand;

	public Distributions(MersenneTwisterFast r) {
		rand = r;
	}*/

    public Distributions() {}
    

	// rate parameter lambda is the expected number of "events" that occur per
	// unit time
	// the return value represents the number of events that "did" happen
	// according to poisson
	public int nextPoisson(double lambda, MersenneTwisterFast rand) {
		double elambda = Math.exp(-1 * lambda);
		double product = 1.0;
		int count = 0;
		int result = 0;
		while (product >= elambda) {
			product *= rand.nextDouble();
			result = count;
			count++; // keep result one behind
		}
		return result;
	}

	// Returns a value according to an exponential distribution with rate
	// parameter lambda.
	public int nextExponential(double lambda, MersenneTwisterFast rand) {
		double randx = rand.nextDouble();
        //log es el log neperiano (logaritmo natural con base e)
		//return -1 * lambda * Math.log(randx);
        return (new Double((-Math.log(randx)) / lambda)).intValue(); //T = -ln U / lambda
	}

}
