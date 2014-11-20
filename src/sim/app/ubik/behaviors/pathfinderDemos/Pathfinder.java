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
package sim.app.ubik.behaviors.pathfinderDemos;

import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;
import sim.app.ubik.behaviors.PositionTools;
import sim.app.ubik.people.Person;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;
import ubik3d.model.HomePieceOfFurniture;

/**
 * This is a demo of how to use pathfinding on UbikSim based on the used of
 * Slick2D library and the BooleanPathfindingMap class.
 * 
 * A goal can be sead, and step makes the agent take a step.
 *
 * It uses a basic A*, the cell size (parameter of config.prop) is important to
 * adjust speed and algorithm efficiency (bigger cells make the algorithm faster
 * since nodes are reduced)
 *
 * Consider parameters of AStarPathFinder when navigation faults are found. The diagonal movements
 * can make agents go through  walls corners (using the diagonal movement); and the max number of steps
 * can be not large enough if cell size is small.
 * 
 * @author Emilio Serrano, Ph.d.; eserrano [at] gsi.dit.upm.es
 */
public class Pathfinder implements Steppable {
    
    protected AStarPathFinder pathfinder;
    protected Path path;
    protected BooleanPathfindingMap pmap;
    
    
    /**
     * Index for the path followed
     */
    protected int lastStepInPath;
    
    protected Int2D goal;
    protected Person p;
    protected static final Logger LOG = Logger.getLogger(Pathfinder.class.getName());
    
    /**
     * Used to add marks in the 3D environment
     */
    protected HomePieceOfFurniture marker;
    
    
    public Pathfinder(Person p) {        
        this.p = p;
        pmap = new BooleanPathfindingMap(p, 10);//perception of 10, the person can see 10 positions ahead to detect mobile obstacles
        pathfinder = new AStarPathFinder(pmap, 10000, true);//plan with no more than 1000 steps,diagonal movement allowed        
        Logger.getLogger(Pathfinder.class.getName()).setLevel(Level.WARNING);//change to info for more details about pathfinding

    }

    /**
     * Make the agent take a step in the path generated. If the mobile obstacles such as people do not let the agent advance, a new path is recalculated
     * and the next step will try to follow it.
     * 
     * It does not count tries of getting the goal (so an agent can keep trying to calculate a path to reach an unreachele goal)
     * @param state
     */
    @Override
    public void step(SimState state) {
        if (goal == null) {
            LOG.info(p.getName() + " DOESN'T have a path or goal defined, use setGoal");
            return;
        }
        if(goal!=null && path==null){//try getting path again, the agent could be sorrounded by mobile agents
           setGoalAndGeneratePath(goal);
           if(path==null) return; //no path found
        }
        Int2D nextPosition = new Int2D(path.getX(lastStepInPath), path.getY(lastStepInPath));  //get next step in path      
        boolean moved = p.setPosition(nextPosition.x, nextPosition.y);//false if movement could no be finished (another person in that position)
        if (moved) {
            lastStepInPath++;            
            
        } else {//generate path again without changing the goal the perception is considered to avoid close mobile obstacles               
            LOG.info(p.getName() + " found a mobile obstacle, recalculating");
            setGoalAndGeneratePath(goal);
        }        
        
    }

    /**
     * Set goal an calculate path.
     * The agent color is change to red if no path was found and a marker inserted in the display. This can be removed with p.setColor
     * A blue marker is inserted when path found, red marker when not found
     * Consider that if the agent is surrounded or there is no path without obstacles, path will be null and false returned.
     * @param goal
     * @return false if path was not generated (no path far the given goal)
     */
    public boolean setGoalAndGeneratePath(Int2D goal) {
        this.goal = goal;
        path = pathfinder.findPath(null, p.getPosition().x, p.getPosition().y, goal.x, goal.y); //get path 
        lastStepInPath = 1;        
        if(marker!=null){
            PositionTools.removeMarkerInDisplay(p, marker);
            marker=null;
        }
        if (path != null) {
            LOG.info(p.getName() + " generated a path from (" + p.getPosition().x + "," + p.getPosition().y + ") to (" + goal.x + "," + goal.y + ") in " + path.getLength() + " steps.");
            marker= PositionTools.insertMakerInDisplay(p, goal.x,goal.y, Color.BLUE);
        } else {
            LOG.info(p.getName() + " DIDN'T find a reachable path from (" + p.getPosition().x + "," + p.getPosition().y + ") to (" + goal.x + "," + goal.y + ")");            
            p.setColor(Color.RED);
            marker= PositionTools.insertMakerInDisplay(p, goal.x,goal.y, Color.RED);
            
        }        
        
        return (path != null);
    }
    
    /**
     * true if agent is in goal fixed

     * @return 
     */
    public boolean isInGoal() {
        return (p.getPosition().x == goal.x && p.getPosition().y == goal.y);
    }
    
    /**
     * Check if reachable. It can be interesting to make sure that tha path is reachable before fixing a goal 
     * @return 
     */
    public boolean checkIfReachable(){
        return (pathfinder.findPath(null, p.getPosition().x, p.getPosition().y, goal.x, goal.y))!=null;
    }
    
    
}
