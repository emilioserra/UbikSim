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

package ubiksimdist;


import sim.app.ubik.Ubik;
import sim.app.ubik.behaviors.Automaton;
import sim.app.ubik.people.PersonHandler;
import sim.app.ubik.people.Worker;


/**
 * @author Emilio Serrano, Ph.d.; eserrano@gsi.dit.upm.es
 */
public class SimExample extends Ubik {
    
     static int maxTimeForExecution=1500;
     
     private int globalTargetDemo1=1;//fix global exit for 

    public void setGlobalTargetDemo1(int val) {       
        this.globalTargetDemo1 = val;
        String[]  s= {"Exit1", "Exit2", "Exit3", "Exit4"};
        Worker.setGlobalGoal(s[val]);
    }

    public int getGlobalTargetDemo1() {
        return globalTargetDemo1;
    }
    
     public Object domGlobalTargetDemo1() { return new String[]  {"Exit1", "Exit2", "Exit3", "Exit4"}; }



     /**    
    * Object with information about execution and, if needed,
      * to finish the execution
      */     

  
    
    /**
     * Passing a random seed
     * @param seed 
     */
    public SimExample(long seed)   {
        super(seed);
        
    }
    
      /**
     * Passing a random seed and time to make EscapeMonitorAgent to finish simulation
     * This time must be less than maxTimeForExecution
     * @param seed 
     */
    public SimExample(long seed, int timeForSim)   {
        super(seed);

        
    }
    

    /**
     * Using seed from config.pros file
     */
     public SimExample() {         
           super();
           setSeed(getSeedFromFile());         
    }
     
     /**
      * 
     * Adding things before running simulation.   
     * Method called after pressing pause (the building variables are instantiated) but before executing simulation.
 
      */
   public void start() {               
        super.start();      


        
        

   }
    
   
   /**
 * Default execution without GUI. It executed the simulation for maxTimeForExecution steps.
 * @param args 
 */
    public static void main(String []args) {
       
       SimExample state = new SimExample(System.currentTimeMillis());
      
       state.start();
        do{
                if (!state.schedule.step(state)) break;
        }while(state.schedule.getSteps() < maxTimeForExecution);//
        state.finish();     
      
     
    }
    
 
 


}
