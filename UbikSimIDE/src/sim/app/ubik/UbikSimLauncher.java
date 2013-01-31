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
package sim.app.ubik;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.app.ubik.utils.Configuration;
import sim.display.Console;
import sim.engine.MakesSimState;
import sim.engine.SimState;
import ubik3d.io.HomeFileRecorder;
import ubik3d.model.Home;
import ubik3d.model.RecorderException;


public class UbikSimLauncher {

    private Configuration configuration;
    private int mode;       // Por defecto 3D
    private int cellSize;   // Por defecto 10 cm
    private List<Home> homes;   // Cada una describe una planta
    private long seed;       // Por defecto random
    
    private static Ubik ubik;

    private String ipOCP;
    private boolean useOCP;
    private int cameraMode;
    
    public UbikSimLauncher() {
        configuration = new Configuration();
    }

    public boolean is0D(){
        return mode==0;
    }
      public void launch() {
          launch(true,-1);
      }
    
    /**
     * 
     * @param launchIf0D true, simulation is executed without GUI and control is not returned from this method
     * with false, method launch0D must be executed after initialized everything with this method.
     * @param seed <0 is not used, >0 is used independently of the config.pros
     */
    public void launch(boolean launchIf0D, long seedForced) {
        initConfigVars();
        if(seedForced>0) seed=seedForced;
        
        if(ipOCP != null){
            ubik = new Ubik(seed, homes, ipOCP, cellSize, mode, useOCP);
        } else {
            System.out.println("UBIK sin OCP");
            ubik = new Ubik(seed, homes, cellSize, mode);
        }
        
        if(mode == 0){
        	if(launchIf0D) launch0D();
        }
        else {
        	UbikSimWithUI vid = new UbikSimWithUI(ubik);                                
	        Console c = new Console(vid);	        
	        c.setVisible(true);	                 
                /*LOOK OUT!!! the console of mason put its own random seed, to avoid this, the method setRandom
                 * in Console has been created. Code as follows:
                 *  public void setRandom(long seed, MersenneTwisterFast random) {
                        this.randomField.setValue(Long.toString(seed));       
                        simulation.state.setRandom(random);      
                        randomSeed=(int) seed;
                    }
                 */
               if (!configuration.getProperty(Configuration.SEED).equalsIgnoreCase("random")){
                   // c.setRandom(seed, ubik.random);
                }
                          
        }        
    }

    private void initConfigVars() {
        if (configuration.getProperty(Configuration.MODE) == null) {
            mode = 3;   // Por defecto 3D
        } else if (configuration.getProperty(Configuration.MODE).equalsIgnoreCase("0D")) {
            mode = 0;
        } else if (configuration.getProperty(Configuration.MODE).equalsIgnoreCase("2D")) {
            mode = 2;
        } else if (configuration.getProperty(Configuration.MODE).equalsIgnoreCase("3D")) {
            mode = 3;
        }
        if (configuration.getProperty(Configuration.CELL_SIZE) == null) {
            cellSize = 10;
        } else {
            cellSize = Integer.parseInt(configuration.getProperty(Configuration.CELL_SIZE));
        }
        if (configuration.getProperty(Configuration.FLOORS) != null) {
            homes = new ArrayList();
            HomeFileRecorder h = new HomeFileRecorder();
            Home home;
            String allNames = configuration.getProperty(Configuration.FLOORS);
            String[] names = allNames.split(",");
            System.out.println("names="+names[0]+", "+names.length);
            for (String n : names) {
                try {
                    home = h.readHome(n);
                    homes.add(home);
                } catch (RecorderException ex) {
                    Logger.getLogger(UbikSimLauncher.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            System.out.println("ERROR: NO floor's files are found");
        }
        if (configuration.getProperty(Configuration.SEED) != null) {
            if (configuration.getProperty(Configuration.SEED).equalsIgnoreCase("random")) {         
                seed = System.currentTimeMillis();
            } else {
                seed = Integer.parseInt(configuration.getProperty(Configuration.SEED));
               
            }
        } else {
            seed = 0;
        }
        if(configuration.getProperty(Configuration.OCP) != null)
        	if(configuration.getProperty(Configuration.OCP).equalsIgnoreCase("on"))
        		useOCP = true;
        	else
        		useOCP = false;
        if(configuration.getProperty(Configuration.IP_OCP) != null)
            ipOCP = configuration.getProperty(Configuration.IP_OCP);        
        
        cameraMode = 0;
        if(configuration.getProperty(Configuration.CAMERA_MODE) != null) {
        	cameraMode = Integer.parseInt(configuration.getProperty(Configuration.CAMERA_MODE));
        	System.out.print("Camera Mode: "+cameraMode);
        	 if(cameraMode != 0)
        		 cameraMode = 1;
        }
        
        
        System.out.println("Parametros:");
        System.out.println("mode="+mode);
        System.out.println("cellSize="+cellSize);
        System.out.println("floors="+homes.get(0).getName());
        System.out.println("seed="+seed);
        System.out.println("ipOCP="+ipOCP);
        //System.out.println("ocp="+useOCP);
        //System.out.println("mobile uri = "+Ubik.uri);
        System.out.println("cameraMode="+cameraMode);
    }

    public static void main(String args[]) {
        UbikSimLauncher usl = new UbikSimLauncher();
        usl.launch();
    }
    
    public static Ubik getUbik() {
    	return ubik;
    }

    public void launch0D() {
        SimState.doLoop(new MakesSimState()
            {
            @Override
			public SimState newInstance(long seed, String[] args)
                {
                try
                    {
                    return ubik;
                    }
                catch (Exception e)
                    {
                    throw new RuntimeException("Exception occurred while trying to construct the simulation: " + e);
                    }
                }
            @Override
			public Class simulationClass() { return Ubik.class; }
            }, new String[0]);
    }
}
