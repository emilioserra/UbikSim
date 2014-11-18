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

import javax.swing.JFrame;
import javax.swing.WindowConstants;
import sim.app.ubik.Ubik;
import sim.app.ubik.UbikSimWithUI;
import sim.app.ubik.behaviors.Automaton;
import sim.app.ubik.people.PersonHandler;
import sim.display.Console;
import sim.display.Controller;


/**
 * Start the sim with a GUI, displays show 3d and 2d views
 *
 * @author Emilio Serrano, emilioserra [at] dit.upm.es
 */
public class SimExampleWithGUI extends UbikSimWithUI {

    protected static SimExample simExample;
    protected static int demoCode = 3; //1 for escape in MapExample, 2 for pathfinding in primeraPlantaUMU_DIIC, 3 for UbikSim 1.0 example

    public static void setDemoCode(int demoCode) {
        SimExampleWithGUI.demoCode = demoCode;
    }

    public SimExampleWithGUI(Ubik ubik) {
        super(ubik);
    }

    /**
     * Method called after pressing pause (the building variables are
     * instantiated) but before executing simulation. Any JFrame can be
     * registered to be shown in the display menu
     */
    @Override
    public void start() {
        super.start();
  
        if (demoCode == 1) {
            Automaton.setEcho(false);
            //add more people
            PersonHandler ph = simExample.getBuilding().getFloor(0).getPersonHandler();
            ph.addPersons(100, true, ph.getPersons().get(0));
            //change their name
            ph.changeNameOfAgents("a");
        }

    }

    /**
     * Method to finish the simulation
     */
    @Override
    public void finish() {
        super.finish();
  
    }

    @Override
    public void init(final Controller c) {
        super.init(c);

    }

    /**
     * Executing simulation with GUI, it delegates to SimExample, simulation
     * without GUI
     *
     * @param args
     */
    public static void main(String[] args) {
        //simExample = new SimExample(System.currentTimeMillis());
        simExample = new SimExample(1);
        
        switch (demoCode) {//scenario depeding on demo code
            case 1:
                simExample.setPathScenario("./environments/mapExample.ubiksim");
                break;
            case 2:
                simExample.setPathScenario("./environments/primeraPlantaUMU_DIIC.ubiksim");
                break;
                
            case 3:
                simExample.setPathScenario("./environments/twoRooms.ubiksim");
                break;
            default:
                //the file in config.pros will be used
        }
        
        
        SimExampleWithGUI vid = new SimExampleWithGUI(simExample);
        Console c = new Console(vid);
        c.setIncrementSeedOnStop(true);
        c.setVisible(true);
        c.setSize(500, 650);
        c.setIconImage(getLocoIcon().getImage());
       
        
        

    }

}
