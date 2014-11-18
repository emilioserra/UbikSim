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

import DistLib.beta;
import DistLib.uniform;

/** This class generates Beta random deviates
 *
 */
public class BetaPdf extends Pdf{
    
    /** Shape 1 parameter of Beta distribution.*/
    private double shape1;
    /** Shape 2 parameter of Beta distribution.*/
    private double shape2;
    
    /**
  * Constructor para pdf no monotonas
  * @param name
  * @param shape1 Shape parameter of Beta distribution.
  * @param shape2 Shape parameter of Beta distribution.
  */
    public  BetaPdf(String name, double shape1, double shape2){
         super(name);
         this.shape1=shape1;
         this.shape2=shape2;
         double valor=beta.random(shape1, shape2, new uniform());
         this.nextTransition=toMinute(valor);
         if(ECHO) System.out.println(toString() + ", first time generated " + this.nextTransition);
    }
    
    /**
     * Constructor para monotonas
     * @param timeOfInitOfTransition  Entero con el minuto en el que se puede iniciar una transición de comportamiento  monótono
     * @param slotOfTimeForTransition Minutos en el que se puede dar la transición en un monótono.
     * @param shape1 Shape parameter of Beta distribution.
     * @param shape2 Shape parameter of Beta distribution.
     */
    public  BetaPdf(String name, int timeOfInitOfTransition, int slotOfTimeForTransition, double shape1, double shape2){
        super(name,timeOfInitOfTransition,slotOfTimeForTransition);
        double valor=beta.random(shape1, shape2, new uniform());
        this.nextTransition=toMinute(valor);
        if(ECHO) System.out.println(toString() + ", first time generated " + this.nextTransition);
    }
    
    /**
     * Generates a new random value.
     * @return 
     */
    public int generateNextTransition(){
        double valor=beta.random(shape1, shape2, new uniform());
        return toMinute(valor);
    }
      
}
