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
import annas.graph.Graph;
import annas.graph.GraphPath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import sim.app.ubik.Ubik;
import sim.util.Int2D;


public class RoadMap {

    int[][] matriz;
    HashMap nodos;
    HashMap aristas;
    HashMap aristasComoNodos;
    HashMap nodosEspeciales; //Usado para mobiliario    
    LinkedList[][] rutasCompletas; //Vector de caminos con las rutas posibles
    protected Graph<Node, DefaultArc<Node>> graph;
    protected NodeCollection nodeCollection;
    protected Dijkstra<Node, DefaultArc<Node>> dijkstra;
    GraphPath<Node, DefaultArc<Node>>[][] routes; //Vector de caminos con las rutas posibles
    ArrayList<Node> nodes;
    
    protected Ubik ubik;

    public void clear() {
    	ubik = null;
		matriz = null;
		nodos.clear();
		aristas.clear();
		aristasComoNodos.clear();
		nodosEspeciales.clear();
		rutasCompletas = null;
		graph.resetArcs();
		graph = null;
		dijkstra.reset();
		dijkstra = null;
		routes = null;
		nodes.clear();
	}
    
    public RoadMap(Ubik ubik, Graph<Node, DefaultArc<Node>> graph, NodeCollection nodes) {
    	this.ubik = ubik;
        this.graph = graph;
        this.nodes = graph.getNodeMap();
        this.nodeCollection = nodes;

        dijkstra = new Dijkstra<Node, DefaultArc<Node>>(graph);
        routes = new GraphPath[this.nodes.size()][this.nodes.size()];
        //createAllRoutes();
    }

    public void createAllRoutes() {
        routes = new GraphPath[nodes.size()][nodes.size()];

        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                if (i != j) {
                    GraphPath<Node, DefaultArc<Node>> route = dijkstra.execute(nodes.get(i), nodes.get(j));
                    if (route != null) {
                        routes[i][j] = route;
                    }
                }
            }
        }
    }

    /**
     * Dadas dos coordenadas (origen y destino) devuelve el camino del grafo
     * que une ambas coordenadas con menor peso.
     * @param ori
     * @param dest
     * @return 
     */
    public GraphPath<Node, DefaultArc<Node>> getGraphPath(Int2D ori, Int2D dest) {
        // Obtener los nodos del grafo más proximos al origen y destino
        Node nOri = nodeCollection.getNodeByPosition(ubik, ori);
        Node nDest = nodeCollection.getNodeByPosition(ubik, dest);
        int p = nodes.indexOf(nOri);
        int q = nodes.indexOf(nDest);
        if(p < 0 || q < 0)
        	return null;
        //System.out.println("Origen ("+p+"): "+nOri+"\nDestino ("+q+"): "+nDest);
        if (routes[p][q] == null) {
            GraphPath<Node, DefaultArc<Node>> route = dijkstra.execute(nOri, nDest);
            //System.out.println("Route: "+route);
            if (route != null) {
                routes[p][q] = route;
            }
        }
        return routes[p][q];
    }
    
    /**
     * Dadas dos coordenadas (origen y destino) devuelve una lista de coordenadas
     * que forman el camino hasta el destino (se incluye también el destino).
     *
     * @param ori
     * @param dest
     * @return
     */
    public List<Int2D> getRoute(Int2D ori, Int2D dest) {
        List<Int2D> result = null;
        GraphPath<Node, DefaultArc<Node>> route = getGraphPath(ori, dest);
        if (route != null) {
                result = new ArrayList(convert(route));
        }
        return result;
    }

    // dado un camino de un grafo, devuelve la lista de coordenadas de los nodos
    // que forman el camino
    private List<Int2D> convert(GraphPath<Node, DefaultArc<Node>> route) {
        List<Int2D> result = new ArrayList();
        Iterator it = route.getIterator();
        while (it.hasNext()) {
            Node node = (Node) it.next();
            result.add(node.getPosition());
        }
        return result;
    }

    /*
     *
    public LinkedList[][] getRoutes() {
    return rutas;
    }

    public void createAllRoutes() {

    rutas = new LinkedList[nodos.size()][nodos.size()];

    Iterator it = nodos.values().iterator();
    while (it.hasNext()) {
    Node n = (Node) it.next();
    createRoutes(n.nombreNodo);
    }

    //Ahora crear las rutas metiendo los pasos entre ellas
    crearRutasCompletas(); //deberia poner las aristas como nodos y punto
    }
     *
    public Int2D getNodoEspecial(String nodo) {
    return (Int2D) this.nodosEspeciales.get(nodo);
    }

    public void createRoutes(String origen) {

    int idNodo = ((Node) nodos.get(origen)).id;
    //System.out.println(idNodo);

    int distancia[] = new int[nodos.size()]; //Usaremos un vector para guardar las distancias del nodo salida al resto
    boolean visto[] = new boolean[nodos.size()]; //vector de boleanos para controlar los vertices de los que ya tenemos la distancia mínima

    //System.out.println(nodos.size());

    //Primer camino
    LinkedList camino = new LinkedList();
    camino.add((Node) nodos.get(origen));
    rutas[idNodo][idNodo] = camino;
    List sucesores = cogerSucesores(idNodo);
    for (int i = 0; i < sucesores.size(); i++) {
    nuevoCamino(idNodo, idNodo, (Node) sucesores.get(i));
    }

    for (int i = 0; i < distancia.length; i++) {
    distancia[i] = matriz[idNodo][i];
    }

    distancia[idNodo] = 0;
    visto[idNodo] = true;
    int index = idNodo;

    while (!todosVistos(visto)) {
    index = cogerMinimoNoVisto(idNodo, visto, distancia);

    visto[index] = true;
    sucesores = cogerSucesores(index);
    for (int i = 0; i < sucesores.size(); i++) {
    int w = ((Node) sucesores.get(i)).id;
    nuevoCamino(idNodo, index, (Node) sucesores.get(i));

    if (distancia[w] > distancia[index] + matriz[index][w]) {
    distancia[w] = distancia[index] + matriz[index][w];
    }
    }
    }
    }

    public boolean todosVistos(boolean[] visto) {
    for (int i = 0; i < visto.length; i++) {
    if (visto[i] == false) {
    return false;
    }
    }
    return true;
    }

    public int cogerMinimoNoVisto(int idNodo, boolean[] visto, int[] distancia) {
    int menorValor = INFINITO;
    int menorIndex = -1;

    for (int i = 0; i < distancia.length; i++) {
    if ((distancia[i] < menorValor) && (visto[i] == false)) {
    menorValor = distancia[i];
    menorIndex = i;
    }
    }
    return menorIndex;
    }

    public List cogerSucesores(int index) {
    LinkedList sucesores = new LinkedList();
    for (int i = 0; i < matriz.length; i++) {
    if ((matriz[index][i] != INFINITO) && (index != i)) {
    Node n1 = buscaNodo(i);
    if (n1 != null) {
    sucesores.add(n1);
    } else {
    //System.out.println("Algo va mal con indice" + index);
    System.exit(0);
    }
    }
    }
    return sucesores;
    }

    public Node buscaNodo(int index) {

    Iterator it = nodos.values().iterator();
    while (it.hasNext()) {
    Node n = (Node) it.next();
    if (n.id == index) {
    return n;
    }
    }
    return null;
    }

    public void nuevoCamino(int idNodo, int index, Node sucesor) {
    if (rutas[idNodo][sucesor.id] == null) { //Si ya está, tiene la más corta
    LinkedList camino = (LinkedList) rutas[idNodo][index];
    LinkedList camino2 = (LinkedList) camino.clone();
    camino2.add(sucesor);
    rutas[idNodo][sucesor.id] = camino2;
    }
    }

    public String traduce(MutableInt2D centro) {
    Iterator it = nodos.values().iterator();
    while (it.hasNext()) {
    Node n = (Node) it.next();
    if (n.centroX == centro.x && n.centroY == centro.y) {
    return n.nombreNodo;
    }
    }
    return null;
    }

    public String traduceArista(MutableInt2D centro) {
    Iterator it = aristas.values().iterator();
    while (it.hasNext()) {
    Arista a = (Arista) it.next();
    if (a.unionX == centro.x && a.unionY == centro.y) {
    return a.nameUnion;
    }
    }
    return null;
    }

    public Int2D getLocation(String habitacion) {
    Node n = (Node) nodos.get(habitacion);
    if (n != null) {
    return new Int2D(n.centroX, n.centroY);
    } else {
    return (Int2D) nodosEspeciales.get(habitacion);
    }
    }

    public void crearRutasCompletas() {
    rutasCompletas = new LinkedList[nodos.size()][nodos.size()]; //El tamaño de la matriz es el mismo, cambia la long del camino

    for (int i = 0; i < rutas.length; i++) {
    for (int j = 0; j < rutas[i].length; j++) {
    LinkedList rutaSinAristas = rutas[i][j];
    LinkedList rutaConAristas = new LinkedList();
    for (int k = 0; k < (rutaSinAristas.size() - 1); k++) { //El ultimo no lleva conexion
    Node n = (Node) rutaSinAristas.get(k);
    Node n1 = (Node) rutaSinAristas.get(k + 1);
    Node nAux = buscaArista(n.nombreNodo, n1.nombreNodo);
    rutaConAristas.add(n);
    rutaConAristas.add(nAux);
    }
    rutaConAristas.add(rutaSinAristas.getLast());//Se añade el último
    rutasCompletas[i][j] = rutaConAristas;
    }
    }
    }

    public Node buscaArista(String conexion1, String conexion2) {
    Iterator it = aristas.values().iterator();
    while (it.hasNext()) {
    Arista a = (Arista) it.next();
    if (((a.conexion1.compareTo(conexion1) == 0) && (a.conexion2.compareTo(conexion2) == 0))
    || ((a.conexion1.compareTo(conexion2) == 0) && (a.conexion2.compareTo(conexion1) == 0))) {
    return ((Node) aristasComoNodos.get(a.nameUnion));

    }
    }
    return null;
    }

    public void imprime() {

    Iterator it = nodos.values().iterator();
    while (it.hasNext()) {
    Node n = (Node) it.next();
    System.out.println("Número de caminos " + rutas[n.id].length);
    int num = 0;
    for (int i = 0; i < rutas[n.id].length; i++) {
    LinkedList l = (LinkedList) rutas[n.id][i];
    System.out.println("ruta para " + n.id + " con " + i);
    if (l != null) {
    Iterator it2 = l.iterator();
    System.out.print("Num:-> [" + num + "] -");
    while (it2.hasNext()) {
    System.out.print(((Node) it2.next()).nombreNodo + " - ");
    }
    System.out.println();
    num++;
    } else {
    System.out.println("SIN CAMINO PREPARADO");
    }
    }
    }
    }

    public void imprimeCompletas() {

    Iterator it = nodos.values().iterator();
    while (it.hasNext()) {
    Node n = (Node) it.next();
    System.out.println("Número de caminos " + rutasCompletas[n.id].length);
    int num = 0;
    for (int i = 0; i < rutasCompletas[n.id].length; i++) {
    LinkedList l = (LinkedList) rutasCompletas[n.id][i];
    System.out.println("ruta para " + n.id + " con " + i);
    if (l != null) {
    Iterator it2 = l.iterator();
    System.out.print("Num:-> [" + num + "] -");
    while (it2.hasNext()) {
    System.out.print(((Node) it2.next()).nombreNodo + " - ");
    }
    System.out.println();
    num++;
    } else {
    System.out.println("SIN CAMINO PREPARADO");
    }
    }
    }
    }*/
    public Graph<Node, DefaultArc<Node>> getGraph() {
        return graph;
    }
}
