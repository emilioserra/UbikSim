/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.ubik.people;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import sim.app.ubik.Ubik;
import sim.app.ubik.behaviors.PositionTools;
import sim.app.ubik.behaviors.pathfinderDemos.Pathfinder;
import sim.app.ubik.behaviors.pathfinderDemos.PathfinderThread;
import sim.app.ubik.building.rooms.Room;
import sim.engine.SimState;
import sim.util.Int2D;
import ubik3d.model.HomePieceOfFurniture;

/**
 * Worker person example: uses the pathfinding (as a thread) to reach a random exit (assumed rooms with names
 * "Exit1" to "Exit4"). This goal can be changed online. You can use it in any environment
 * exits named as described, or change the exits name at this class according to your environment. 
 * Moreover, the environment has to include an object Worker. It has been tested with mapExample.ubiksim.
 * Agents get out after reaching a goal
 *
 * @author Emilio Serrano, Ph.d.; eserrano [at] gsi.dit.upm.es
 */
public class Worker extends  Person{
    private static final Logger LOG = Logger.getLogger(Worker.class.getName());
    private static String globalGoal=null; //goal for all agents     
    private static String exits[] = {"Exit1", "Exit2", "Exit3", "Exit4"}; //list of poosible goals
    
    
           
    private String localGoal=null;//local goal if no global is fixed
    private Pathfinder pf;



    public Worker(int floor, HomePieceOfFurniture person3DModel, Ubik ubik) {
        super(floor, person3DModel, ubik);
       
    }

    public void step(SimState state) {
        super.step(state);

        
      
        
        if (pf == null) {//generate pathfidner and goals in first step                       
            pf = new PathfinderThread(this);
            this.localGoal=exits[state.random.nextInt(exits.length)];//random exit            
            pf.setGoalAndGeneratePath(PositionTools.getRoom(this,localGoal).getCenter());
            
        }  
        
        if(globalGoal!=null && !globalGoal.equals(localGoal) ){//if globalGoal fixed and it does not match the current goal
            pf.setGoalAndGeneratePath(PositionTools.getRoom(this,globalGoal).getCenter());
            localGoal = globalGoal;//this allow not entering this condition again if global goal is not rechanged
        }
                
     

        if(pf.isInGoal()){
           this.stop();//stop agent and make it get out of the simulation       
           PositionTools.getOutOfSpace(this);
           LOG.info(name + " has leave the building using " + localGoal);
           return;
        }
      
        
         pf.step(state);//take steps to the goal (step can regenerate paths) 
        
        

    }

    public static void setGlobalGoal(String globalGoal) {
        Worker.globalGoal = globalGoal;
        
    }
    
    


  
}
