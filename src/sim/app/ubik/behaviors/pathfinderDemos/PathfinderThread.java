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
import sim.app.ubik.behaviors.PositionTools;
import static sim.app.ubik.behaviors.pathfinderDemos.Pathfinder.LOG;
import sim.app.ubik.people.Person;
import sim.engine.SimState;
import sim.util.Int2D;

/**
 * Extends Pathfinder, a thread is used to calculate the path. The step does not
 * follow if the thread is calculating (letting other agents work) It affects
 * simulation reproducibility
 *
 * @author Emilio Serrano, Ph.d.; eserrano [at] gsi.dit.upm.es
 */
public class PathfinderThread extends Pathfinder {

    private Thread pfThread;//trhead for calculating path

    public PathfinderThread(Person p) {
        super(p);
    }

    @Override
    public void step(SimState state) {
        if (pfThread.isAlive()) {
            return;
        }
      super.step(state);
    }

    /**
     * Set goal an calculate path
     *
     * @param goal
     * @return Always true, since the thread does not block execution, it cannot
     * be know if there will be a path at the moment of finishing the method. Markers 
     * displaying goal are not used.
     */
    @Override
    public boolean setGoalAndGeneratePath(Int2D goalLocal) {

        this.goal = goalLocal;
        final Int2D goal = goalLocal;
        Runnable run = new Runnable() {
            public void run() {
                
                path = pathfinder.findPath(null, p.getPosition().x, p.getPosition().y, goal.x, goal.y); //get path 
                lastStepInPath = 1;
                /*if (marker != null) {
                    PositionTools.removeMarkerInDisplay(p, marker);
                    marker = null;
                }*/
                if (path != null) {
                    LOG.info(p.getName() + " generated a path from (" + p.getPosition().x + "," + p.getPosition().y + ") to (" + goal.x + "," + goal.y + ") in " + path.getLength() + " steps.");
                   // marker = PositionTools.insertMakerInDisplay(p, goal.x, goal.y, Color.BLUE);
                } else {
                    LOG.info(p.getName() + " DIDN'T find a reachable path from (" + p.getPosition().x + "," + p.getPosition().y + ") to (" + goal.x + "," + goal.y + ")");
                    p.setColor(Color.RED);
                   // marker = PositionTools.insertMakerInDisplay(p, goal.x, goal.y, Color.RED);

                }
            }
        };
        pfThread = new Thread(run);        
        pfThread.start();
        return true;
    }
    
    
    

}
