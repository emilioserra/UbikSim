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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.PathFinder;
import sim.app.ubik.Ubik;
import sim.app.ubik.behaviors.PositionTools;
import sim.app.ubik.building.rooms.Room;
import sim.app.ubik.people.Person;

/**
 * Generate a pathfinder inform about a UbikSim scenario to check if all rooms are reachable
 * @author Emilio Serrano, Ph.d.; eserrano [at] gsi.dit.upm.es
 */
public class PathfinderChecker {

    
    private String inform;
    private static final Logger LOG = Logger.getLogger(PathfinderChecker.class.getName());
    private int maxSteps=10000;

    public String getInform() {
        return inform;
    }
    public PathfinderChecker(String pathScenario) {
        Ubik u= new Ubik(1);        
        u.setPathScenario(pathScenario);      
        u.init();
   
        String s= "Scenario checked: " + pathScenario + "\n";
        int np= (u.getBuilding().getFloor(0).getPersonHandler().getPersons()).size();
        if(np==0) {
            s+="Add some person for a thorough inform \n";
            return;
        }
        s+= "Persons included: " + np + "\n";
        
        Person p=(u.getBuilding().getFloor(0).getPersonHandler().getPersons()).get(0);
        
        List<Room> lr= PositionTools.getRooms(p);
        s+= "List of Rooms included: \n";
        for(Room r: lr){
            s+="\t" + r.getName()+ "\n";
        }
        
    
        BooleanPathfindingMap pmap = new BooleanPathfindingMap(p, 10);//perception of 10, the person can see 10 positions ahead to detect mobile obstacles
        AStarPathFinder pathfinder = new AStarPathFinder(pmap, maxSteps, true);//plan with no more than 1000 steps,diagonal movement allowed  
        List<String> nrr=new ArrayList<String>(); //non reachable rooms
        for(Room r1: lr){
            for(Room r2:lr){
                if(lr.indexOf(r1)<lr.indexOf(r2)){//if r1 reaches r2, r2 also reaches r1
                    //check reachability                        
                    Path path = pathfinder.findPath(null, r1.getCenter().x, r1.getCenter().y, r2.getCenter().x, r2.getCenter().y); //get path 
                    LOG.info("Reachability " + r1.getName() +  " to " + r2.getName() +  " checked");
                    if(path==null) nrr.add(r1.getName() + " <-> " + r2.getName());
                }
            }
        }
        if(nrr.isEmpty()) s+="All rooms are reachable!";                    
        else{
            s+= "Not reachable rooms with  " + maxSteps + " steps: \n";
            for(String nonreachable: nrr){
                s+="\t" + nonreachable + "\n";
            }
        }
        inform=s;
        
        
        
        
    }

}
