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
package sim.app.ubik.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import sim.app.ubik.Ubik;
import sim.app.ubik.building.SpaceArea;
import sim.app.ubik.building.connectionSpace.ConnectionSpaceInABuilding;
import sim.util.Int2D;


public class NodeCollection {
	// Agrupa los nodos por el SpaceArea donde se encuentra (Room, Corridor,...)

	private HashMap<String, List<Node>> nodeList;
	private List<Node> allNodes;

	public NodeCollection() {
		nodeList = new HashMap<String, List<Node>>();
		allNodes = new ArrayList<Node>();
	}

	public void addNode(Node node) {
		if (node.getSpaceArea().getName() == null) {
			addNode("Default", node);
		} else {
			SpaceArea sa = null;
			sa = node.getSpaceArea();
			addNode(sa.getName(), node);
		}
	}

	private void addNode(String roomName, Node node) {
		List<Node> list = nodeList.get(roomName);
		if (list == null) {
			list = new ArrayList<Node>();
			nodeList.put(roomName, list);
		}
		list.add(node);
		allNodes.add(node);
	}

	public void clear() {
		for (List<Node> list : nodeList.values()) {
			list.clear();
		}
		nodeList.clear();
		allNodes.clear();
	}

	/**
	 * Devuelve los nodos que son accessPoints a la puerta
	 * 
	 * @param door
	 * @return
	 */
	public List<NodeAccessPoint> getAccesPoints(ConnectionSpaceInABuilding csiab) {
		List<NodeAccessPoint> result = new ArrayList<NodeAccessPoint>();
		for (List<Node> list : nodeList.values()) {
			for (Node n : list) {
				if (n instanceof NodeAccessPoint) {
					NodeAccessPoint nap = (NodeAccessPoint) n;
					if (nap.getConnectionSpaces() == csiab) {
						result.add(nap);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Devuelve el nodo más próximo a una posición
	 * 
	 * @param position
	 * @return
	 */
	public Node getNodeByPosition(Ubik ubik, Int2D position) {
		Node result = null;
		for (Node n : sortNodesByDistanceTo(position, allNodes)) {
			boolean areObstacles = ubik
					.getBuilding()
					.getFloor(0)
					.getSpaceAreaHandler()
					.isObstacles(position.x, position.y, n.getPosition().x,
							n.getPosition().y, 40 / ubik.getCellSize());
			if (!areObstacles) {
				return n;
			}
		}
		return result;
	}

	/**
	 * Returns the nearest Node which is located in the same space area that the
	 * given position.
	 * 
	 * @param ubik
	 * @param position
	 * @return
	 */
	public Node getNearestNodeInSpaceArea(Ubik ubik, Int2D position) {
		SpaceArea sa = ubik.getBuilding().getFloor(0).getSpaceAreaHandler()
				.getSpaceArea(position.x, position.y);
		if (sa == null) {
			return getNodeByPosition(ubik, position);
		}
		Node result = null;
		double minD = Double.MAX_VALUE;

		if (sa instanceof ConnectionSpaceInABuilding) {
			Int2D point = getNearestPoint(position,
					((ConnectionSpaceInABuilding) sa).getAccessPoints());
			return getNearestNodeInSpaceArea(ubik, point);
		}

		for (Node n : nodeList.get(sa.getName())) {
			double d = n.getPosition().distance(position);
			boolean areObstacles = ubik
					.getBuilding()
					.getFloor(0)
					.getSpaceAreaHandler()
					.isObstacles(position.x, position.y, n.getPosition().x,
							n.getPosition().y, 40 / ubik.getCellSize());
			if (!areObstacles && d < minD) {
				result = n;
				minD = d;
			}
		}
		if (result == null) {
			return getNodeByPosition(ubik, position);
		} else
			return result;
	}

	private Int2D getNearestPoint(Int2D position, Int2D[] points) {
		Int2D result = null;
		double minD = Double.MAX_VALUE;

		for (Int2D v : points) {
			double d = v.distance(position);
			if (d < minD) {
				result = v;
				minD = d;
			}
		}

		return result;
	}

	private List<Node> sortNodesByDistanceTo(Int2D position, List<Node> nodes) {
		List<Node> result = new ArrayList<Node>();
		for (Node n : nodes) {
			if (result.isEmpty())
				result.add(n);
			else {
				for (int i = 0; i < result.size(); i++) {
					Node sortedNode = result.get(i);
					double actualDist = sortedNode.getPosition().distance(
							position);
					double newDist = n.getPosition().distance(position);
					if (newDist < actualDist) {
						result.add(i, n);
						break;
					}
				}
			}
		}
		return result;
	}
}
