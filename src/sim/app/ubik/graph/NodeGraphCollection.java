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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import sim.app.ubik.building.SpaceArea;


public class NodeGraphCollection {
    private static NodeGraphCollection nodeGraphCollection;
    
    private Map<String, NodeGraph> nodeGraphs;
    
    public static NodeGraphCollection getInstance() {
        if(nodeGraphCollection == null)
            nodeGraphCollection = new NodeGraphCollection();
        return nodeGraphCollection;
    }
    
    private NodeGraphCollection() {
        nodeGraphs = new HashMap<String, NodeGraph>();
    }
    
    public void addNodeGraph(NodeGraph node) {
        if(node.getNameId() != null && !node.getNameId().equals(""))
            nodeGraphs.put(node.getNameId(), node);
    }
    
    public NodeGraph getNodeGraph(String name) {
        return nodeGraphs.get(name);
    }
    
    /**
     * Dado un espacio devuelve la lista de NodeGraph que se encuentran en su interior.
     * @param space
     * @return 
     */
    public List<NodeGraph> getNodeGraphInSpaceArea(SpaceArea space) {        
        return space.getNodeGraphs();
    }
}
