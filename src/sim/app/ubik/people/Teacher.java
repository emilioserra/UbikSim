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
 * Teacher person example: uses the pathfinding to reach 5 random positions
 * at the environment before finishing and get in red. The 2D display can be
 * used to fixed new positions to be reached. You can use it in any environment with an 
 * object teacher, but it has been tested with primeraPlantaUMU_DIIC.ubiksim.
 * Moreover, selecting teachers at the 2D view, new goals can be added to make the agent
 * go there.
 *
 * @author Emilio Serrano, Ph.d.; eserrano [at] gsi.dit.upm.es
 */
public class Teacher extends Person {

    private int numberOfGoalsToEnd = 5;
    private List<Int2D> goals;
    private Int2D currentGoal=null;
    private static final Logger LOG = Logger.getLogger(Teacher.class.getName());
    private Pathfinder pf;

    private List<String> roomList;
    private String newGoal;

    public Teacher(int floor, HomePieceOfFurniture person3DModel, Ubik ubik) {
        super(floor, person3DModel, ubik);

    }

    public void step(SimState state) {
        super.step(state);

      
        
        if (pf == null) {//generate pathfidner and goals in first step
            pf = new Pathfinder(this);
            //pf = new PathfinderThread(this);
            generateGoals(numberOfGoalsToEnd);
        }

        if (goals.isEmpty()) {//if no remaining goals, put in red and do not follow       
            return;
        }
        
        
        if (currentGoal == null || pf.isInGoal()) {//if no current goal or it is in a goal, remove current goal and replace it
            goals.remove(currentGoal);
            currentGoal=null;
            if (!goals.isEmpty()) {
                currentGoal = goals.get(0);
                pf.setGoalAndGeneratePath(currentGoal);
            }
            else{this.setColor(Color.BLUE);}//blue to say that agent has accomplished all goals
        } 
        else{ 
            pf.step(state);//take steps to the goal (step can regenerate paths) 
        }
        
        

    }

    
    /**
     * Generate random goals
     * @param numberOfGoalsToEnd 
     */
    
    private void generateGoals(int numberOfGoalsToEnd) {
        goals = new ArrayList();
        for (int i = 0; i < numberOfGoalsToEnd; i++) {
            goals.add(PositionTools.getRandomPositionInRandomRoom(this));
        }    
    }

    /**
     * This allow checking a list of rooms selecting an agent in the 2D view.
     * See inspectors in MASON documentation
     
     * @return
     */
    public List<String> getRooms() {
        if (roomList == null) {
            this.roomList = new ArrayList<String>();
            for (Room r : PositionTools.getRooms(this)) {
                roomList.add(r.getName());
            }

        }
        return roomList;
    }

    /**
     * This allow adding a goal for an agent after selecting it in the 2D view,
     * See inspectors in MASON documentation
     *
     * @return
     */
    public void setNewGoal(String room) {
        if (!roomList.contains(room)) {
            return;
        }
        Int2D pos = PositionTools.getRandomPositionInRoom(this, PositionTools.getRoom(this, room));
        goals.add(pos);
        this.setColor(Color.MAGENTA);
        this.newGoal = room;
    }

    
    
    public String getNewGoal() {
        return newGoal;
    }
}
