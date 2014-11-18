/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sim.app.ubik.behaviors.pathfinderDemos;

import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;
import sim.app.ubik.behaviors.PositionTools;
import sim.app.ubik.people.Person;

/**
 * @deprecated 
 * Map for pathfidning purposes. It does not generate an extra map. Use booleanPathfindingMap for more eficiency
 * Check example class PathfinderDemo 
 * @author Emilio Serrano, Ph.d.; eserrano [at] gsi.dit.upm.es
 */
public class PathfindingMap implements TileBasedMap {


    private Person p;
    private int percetionRange;
    
    /**Map depends on person floor and percetion*/
    public PathfindingMap(Person p, int percetionRange){
        this.p=p;
       this.percetionRange= percetionRange;
    }

    
    /**
     * Considered if its wall of if there are a person when its in perception range
     * @param ctx
     * @param x
     * @param y
     * @return 
     */
    @Override
    public boolean blocked(PathFindingContext ctx, int x, int y) {
       if(PositionTools.isWall(p, x, y)) return true;
       if(PositionTools.getDistance(p.getPosition().x, p.getPosition().y, x, y)<percetionRange){
           return PositionTools.getPerson(p, x, y)!=null;
        }
       return false;
    }

    @Override
    public float getCost(PathFindingContext ctx, int x, int y) {
        return 1.0f;
    }

    @Override
    public int getHeightInTiles() {
        return p.getUbik().getBuilding().getFloor(p.getFloor()).getSpaceAreaHandler().getGrid().getHeight();
    }

    @Override
    public int getWidthInTiles() {
        return p.getUbik().getBuilding().getFloor(p.getFloor()).getSpaceAreaHandler().getGrid().getWidth();
    }

    @Override
    public void pathFinderVisited(int x, int y) {}

}
