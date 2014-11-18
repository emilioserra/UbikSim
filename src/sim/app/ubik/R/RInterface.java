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
package sim.app.ubik.R;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;

public class RInterface {
    
    private static RInterface rinterface = null;
    private static Rengine re;
    
    private RInterface(){
        //Connect to R
        System.out.println("connection to RInterface");
        String newargs[] = {"--vanilla"};
        re = new Rengine(newargs, false, null);
        re.eval("require(actuar)");
        re.eval("require(VGAM)");
    }
     
    /**
     * Rengine must be initialized once
     * @return 
     */
    public static RInterface getInstance() {
        if(rinterface == null){
            rinterface = new RInterface();
        }
        return rinterface;
    }
    
    /**
     * Process a R command and return its result, it is possible to define a package which could be required to execute the command
     * @param command 
     * @param package_name
     */
    public  REXP processCommand(String command, String package_name){

        try {
            
            //Load a package which could be required
            if (package_name!=null){
                re.eval("require("+package_name+")");
            }
            
            //Evaluation of the command in R
            REXP eval = re.eval(command);
            
            return eval;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Process a R command and return its result
     * @param command 
     */
    public  REXP processCommand(String command){

        try {
                        
            //Evaluation of the command in R
            REXP eval = re.eval(command);
            
            return eval;
            
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    
}
