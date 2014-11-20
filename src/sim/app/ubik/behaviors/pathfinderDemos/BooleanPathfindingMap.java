
package sim.app.ubik.behaviors.pathfinderDemos;

import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;
import sim.app.ubik.behaviors.PositionTools;
import sim.app.ubik.people.Person;

/**
 * Map for pathfidning purposes. Uses a boolean map with the osbtacles for efficiency purposes
 * The cell size, in config.props, also has an important impact in the efficiency (it makes graph smaller)
 * Check example class AStarTest 
 * @author Emilio Serrano, Ph.d.; eserrano [at] gsi.dit.upm.es
 */
public class BooleanPathfindingMap implements TileBasedMap {


    private Person p;
    private int percetionRange;    
    /**
     * Map is static, one for all instances since blocked obstacles are shared
     */
    private static boolean[][] bmap=null;//true if blocked, false if not
    private static int WIDTH;
    private static int HEIGHT;
    private String mapPath;
    
    /**Map depends on person floor and percetion*/
    public BooleanPathfindingMap(Person p, int percetionRange){
       this.p=p;
       this.percetionRange= percetionRange;
       if(bmap==null || !p.getUbik().getPathScenario().equals(mapPath)){
           mapPath=p.getUbik().getPathScenario();
           generateBmap();
       }
       
    }

    
    /**
     * Considered if its wall of if there is  a person in its in perception range (so calling it when finding a mobile object, generates a path considering this)
     * @param ctx
     * @param x
     * @param y
     * @return 
     */
    @Override
    public boolean blocked(PathFindingContext ctx, int x, int y) {
         if(x>=WIDTH ||x<0 ||y<0 || y>= HEIGHT) return true;
       if(bmap[x][y]) return true;
       if(PositionTools.getDistance(p.getPosition().x, p.getPosition().y, x, y)<percetionRange){
           if(PositionTools.getPerson(p, x, y)!=null) return true;
        }
       return false;
    }

    @Override
    public float getCost(PathFindingContext ctx, int x, int y) {
        return 1.0f;
    }

    @Override
    public int getHeightInTiles() {
        return HEIGHT;
    }

    @Override
    public int getWidthInTiles() {
        return WIDTH;
    }

    @Override
    public void pathFinderVisited(int x, int y) {}

    
    /**
     * generates the boolean map of obstacles: true if obstacle.
     * Called only once, map shared for all agents
     */
    private void generateBmap() {        
        HEIGHT = p.getUbik().getBuilding().getFloor(p.getFloor()).getSpaceAreaHandler().getGrid().getHeight();
        WIDTH=  p.getUbik().getBuilding().getFloor(p.getFloor()).getSpaceAreaHandler().getGrid().getWidth();
        bmap= new boolean[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH ; i++) {
            for (int j = 0; j < HEIGHT; j++) {
               if(PositionTools.isWall(p, i, j)) bmap[i][j]=true;
               else bmap[i][j]=false;
            }
            
        }
    }

}
