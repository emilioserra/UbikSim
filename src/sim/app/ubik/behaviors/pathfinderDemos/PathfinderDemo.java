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

import org.newdawn.slick.util.pathfinding.AStarPathFinder;
import org.newdawn.slick.util.pathfinding.Path;
import org.newdawn.slick.util.pathfinding.PathFindingContext;
import org.newdawn.slick.util.pathfinding.TileBasedMap;
/**
 * Class based on code given in http://stackoverflow.com/questions/9742039/a-pathfinding-java-slick2d-library about the use of Slick2D
 * @author Emilio Serrano, Ph.d.; eserrano [at] gsi.dit.upm.es
 */

public class PathfinderDemo {

    private static final int MAX_PATH_LENGTH = 100;

    private static final int START_X = 1;
    private static final int START_Y = 1;

    private static final int GOAL_X = 1;
    private static final int GOAL_Y = 6;

    public static void main(String[] args) {

        SimpleMap map = new SimpleMap();

        /** documentation at: http://slick.ninjacave.com/javadoc/org/newdawn/slick/util/pathfinding/AStarPathFinder.html */
        AStarPathFinder pathFinder = new AStarPathFinder(map, MAX_PATH_LENGTH, false);
        Path path = pathFinder.findPath(null, START_X, START_Y, GOAL_X, GOAL_Y);

        int length = path.getLength();
        System.out.println("Found path of length: " + length + ".");

        for(int i = 0; i < length; i++) {
            System.out.println("Move to: " + path.getX(i) + "," + path.getY(i) + ".");
        }

    }

}
//Documentation at: http://slick.ninjacave.com/javadoc/org/newdawn/slick/tiled/TiledMap.html

class SimpleMap implements TileBasedMap {
    private static final int WIDTH = 10;
    private static final int HEIGHT = 10;

    private static final int[][] MAP = {
        {1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,1,1,1,1},
        {1,0,1,1,1,0,1,1,1,1},
        {1,0,1,1,1,0,0,0,1,1},
        {1,0,0,0,1,1,1,0,1,1},
        {1,1,1,0,1,1,1,0,0,0},
        {1,0,1,0,0,0,0,0,1,0},
        {1,0,1,1,1,1,1,1,1,0},
        {1,0,0,0,0,0,0,0,0,0},
        {1,1,1,1,1,1,1,1,1,0}
    };

    @Override
    //0 is blocked
    public boolean blocked(PathFindingContext ctx, int x, int y) {
        return MAP[y][x] != 0;
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

}
