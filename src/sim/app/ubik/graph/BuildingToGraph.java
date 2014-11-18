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

import annas.graph.DefaultArc;
import annas.graph.DefaultWeight;
import annas.graph.UndirectedGraph;
import java.util.ArrayList;
import sim.util.Int2D;
import java.util.List;
import sim.app.ubik.Ubik;
import sim.app.ubik.building.SpaceArea;
import sim.app.ubik.building.connectionSpace.ConnectionSpaceInABuilding;
import sim.app.ubik.building.rooms.Room;
import sim.app.ubik.furniture.Furniture;

/**
 * Introduce un esquema abstracto en forma de grafo implícito a partir del edificio.
 */
public class BuildingToGraph {

    protected Ubik ubik;
    protected NodeCollection nodes;
    private UndirectedGraph<Node, DefaultArc<Node>> graph;
    private RoadMap roadMap;

    public BuildingToGraph(Ubik ubik) {
        this.ubik = ubik;
        this.nodes = new NodeCollection();

        graph = new UndirectedGraph<Node, DefaultArc<Node>>();

        for (int i = 0; i < ubik.getBuilding().numberOfFloors(); i++) {
            createRooms(i);
            createConnectionSpace(i);
        }
        roadMap = new RoadMap(ubik, graph, nodes);

        // TODO Comentado Pablo
        //roadMap.createAllRoutes();

        //r.imprime();
        //r.imprimeCompletas();
    }

    public void clear() {
    	ubik = null;
    	nodes.clear();
    	graph.resetArcs();
    	graph = null;
    	roadMap.clear();
    	roadMap = null;
    }
    
    /**
     * Crea las habitaciones y pasillos
     * @param floor
     */
    public void createRooms(int floor) {        
        for (SpaceArea sa : ubik.getBuilding().getFloor(floor).getSpaceAreaHandler().getSpaceAreas()) {
            List<Node> nodesRoom = new ArrayList();
            
            // Primero añadimos el nodo que corresponde con el centro de la habitación
            // si este cae dentro de la habitación
            Int2D centro = sa.getCenter();
            if (sa.contains(centro.x, centro.y)) {
                Node n = new Node(centro.getX(), centro.getY(), sa);
                nodes.addNode(n);
                nodesRoom.add(n);
                graph.addNode(n);
            }

            // Creamos los nodos introducidos por el editor
            for (Node n : sa.getNodeGraphs()) {
                nodes.addNode(n);
                nodesRoom.add(n);
                graph.addNode(n);                
            }

            // Creamos los nodos de los muebles si es una habitacion
            if(sa instanceof Room)
                if(((Room)sa).getFurniture() != null) {
                    for(Furniture f:((Room)sa).getFurniture()) {
                        Node n = new NodeFurniture(f.getCenter().getX(), f.getCenter().getY(), (Room)sa, f);
                        nodes.addNode(n);
                        nodesRoom.add(n);
                        graph.addNode(n);
                    }
                }

            // Añadimos los puntos de acceso a la puerta que pertenecen puerta
            for (ConnectionSpaceInABuilding csiab : sa.getConnectionSpace()) {
                for (Int2D p : csiab.getAccessPoints()) {
                    if (sa.contains(p.x, p.y)) {
                        Node n = new NodeAccessPoint(p.getX(), p.getY(), sa, csiab);
                        nodes.addNode(n);
                        nodesRoom.add(n);
                        graph.addNode(n);
                    }
                }
            }

            // Se crean aristas entre todos los nodos mientras no haya obstáculos
            for (int i = 0; i < nodesRoom.size() - 1; i++) {
                Node n1 = nodesRoom.get(i);
                for (int j = i + 1; j < nodesRoom.size(); j++) {
                    Node n2 = nodesRoom.get(j);
                    if (!ubik.getBuilding().getFloor(floor).getSpaceAreaHandler().isObstacles(
                            n1.getPosition().getX(), n1.getPosition().getY(), n2.getPosition().getX(), n2.getPosition().getY(), 40 / ubik.getCellSize())) {
                        double distancia = n1.getPosition().distance(n2.getPosition());
                        graph.addArc(n1, n2, new DefaultWeight(distancia));
                        //graph.addArc(n2, n1, new DefaultWeight(distancia));
                    }
                }
            }
        }
    }

    public void createConnectionSpace(int floor) {
        List<ConnectionSpaceInABuilding> visitadas = new ArrayList();
        for (SpaceArea sa : ubik.getBuilding().getFloor(floor).getSpaceAreaHandler().getInstancesOf(Room.class)) {
            Room r = (Room)sa;
            for (ConnectionSpaceInABuilding d : r.getConnectionSpace()) {
                if (!visitadas.contains(d)) {
                    visitadas.add(d);
                    // Obtener los nodos que son puntos de acceso a la puerta.
                    List<NodeAccessPoint> points = nodes.getAccesPoints(d);
                    if (points.size() == 2) {
                        NodeAccessPoint nap1 = points.get(0);
                        NodeAccessPoint nap2 = points.get(1);
                        graph.addArc(nap1, nap2, new ConnectionSpaceWeighted(nap1, nap2));
                        //graph.addArc(nap2, nap1, new DoorWeighted(nap2, nap1));
                    }
                }
            }
        }
    }

    public RoadMap getRoadMap() {
        return roadMap;
    }
}
