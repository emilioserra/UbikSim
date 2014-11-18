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
package sim.app.ubik.behaviors.pdf;

import flanagan.analysis.Stat;

/** This class generates Pareto random deviates
 *
 */
public class ParetoPdf extends Pdf {
    
     /** Shape parameter of Pareto distribution.*/
    private double shape;
    /** Scale parameter of Pareto distribution.*/
    private double scale;
    
      /**
  * Constructor para pdf no monotonas
  * @param name
  * @param shape Shape parameter of Pareto distribution.
  * @param scale Scale parameter of Pareto distribution.
  */
    public  ParetoPdf(String name, double shape, double scale){
         super(name);
         this.shape=shape;
         this.scale=scale;
         double [] valores=Stat.paretoRand(shape,scale,1);
         this.nextTransition=toMinute(valores[0]);
         if(ECHO) System.out.println(toString() + ", first time generated " + this.nextTransition);
    }
    
    /**
     * Constructor para monotonas
     * @param timeOfInitOfTransition  Entero con el minuto en el que se puede iniciar una transición de comportamiento  monótono
     * @param slotOfTimeForTransition Minutos en el que se puede dar la transición en un monótono.
     * @param shape Shape parameter of Pareto distribution.
     * @param scale Scale parameter of Pareto distribution.
     */
    public  ParetoPdf(String name, int timeOfInitOfTransition, int slotOfTimeForTransition, double shape, double scale){
        super(name,timeOfInitOfTransition,slotOfTimeForTransition);
        double [] valores=Stat.paretoRand(shape,scale,1);
        this.nextTransition=toMinute(valores[0]);
        if(ECHO) System.out.println(toString() + ", first time generated " + this.nextTransition);
    }
    
    /**
     * Generates a new random value.
     * @return 
     */
    public int generateNextTransition(){
        double [] valores=Stat.paretoRand(shape,scale,1);
        return toMinute(valores[0]);
    }
}
