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
package sim.app.ubik.displays;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import annas.graph.DefaultArc;
import annas.graph.Graph;

import sim.app.ubik.graph.Node;
import sim.app.ubik.Ubik;
import ubik3d.model.Home;
import ubik3d.model.Wall;


public class ShowNavGraph {
	protected Ubik ubik;
	
	protected List<Wall> walls;
	protected Color color;
	
	protected boolean init = false;
	
	public ShowNavGraph(Ubik ubik) {
		this.ubik = ubik;
		walls = new ArrayList<Wall>();
		color = Color.red;
	}
	
	public void showNavGraph(boolean show) {
		if(!init) {
			createVisualGraph();
			init = true;
		}
		
		if(show) {
			addScenario();
		} else {
			removeScenario();
		}
	}
	
	private void createVisualGraph() {
		Graph<Node, DefaultArc<Node>> graph = ubik.getBuilding().getBuildingToGraph().getRoadMap().getGraph();
		
		for(Node node: graph.getNodeMap()) {
			for(DefaultArc<Node> arc: graph.getArc(node)) {
				Node tail = arc.getTail();
				Node head = arc.getHead();
				Wall wall = new Wall(tail.getPosition().x*ubik.getCellSize(),
						tail.getPosition().y*ubik.getCellSize(),
						head.getPosition().x*ubik.getCellSize(),
						head.getPosition().y*ubik.getCellSize(),
						5, 30);
				
				walls.add(wall);				
			}
		}
		
		updateWallsColor();
	}
	
	public void setColor(Color color) {
		this.color = color;
		updateWallsColor();
	}
	
	private void addScenario() {
		Home home = ubik.getHomes().get(0);
		for(Wall wall: walls) {
			home.addWall(wall);
		}
	}
	
	private void removeScenario() {
		Home home = ubik.getHomes().get(0);
		for(Wall wall: walls) {
			home.deleteWall(wall);
		}
	}
	
	private void updateWallsColor() {
		for(Wall wall: walls) {
			wall.setRightSideColor(color.getRGB());
			wall.setLeftSideColor(color.getRGB());
		}
	}

	public void clear() {
		walls.clear();
		walls = null;
	}
}
