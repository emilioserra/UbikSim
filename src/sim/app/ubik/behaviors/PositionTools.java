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

package sim.app.ubik.behaviors;

import annas.graph.DefaultArc;
import annas.graph.GraphPath;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.j3d.BranchGroup;
import javax.swing.JOptionPane;
import javax.vecmath.Vector3f;
import sim.app.ubik.Ubik;
import sim.app.ubik.building.SpaceArea;
import sim.app.ubik.building.connectionSpace.ConnectionSpaceInABuilding;
import sim.app.ubik.building.rooms.Room;
import sim.app.ubik.graph.Node;
import sim.app.ubik.graph.NodeGraph;
import sim.app.ubik.graph.NodeGraphCollection;
import sim.app.ubik.people.Person;
import sim.app.ubik.representation.Representation;
import sim.util.Bag;
import sim.util.Int2D;
import sim.util.MutableDouble2D;
import sim.util.MutableInt2D;
import ubik3d.j3d.ModelManager;
import ubik3d.model.CatalogPieceOfFurniture;
import ubik3d.model.Content;
import ubik3d.model.HomePieceOfFurniture;
import ubik3d.tools.ResourceURLContent;

/**
 * Clase con métodos de la antigua MovementTools. Básicamente calculo de
 * distancias y de destinos para que puedan ser llamados desde cualquier
 * comportamiento, canal o interacción. PersonHandler también contiene métodos
 * de utilidad tales como coger personas en un radio.
 * 
 */
public class PositionTools {
    
    private static final Logger LOG = Logger.getLogger(PositionTools.class.getName());

	/**
	 * True si la posición origen esta en un sitio vecino al sitio que se pasa
	 * como parametro. Se pasa el grado de vecinidad, con 1 solo vecinos
	 * directos o m isma posicion, con 2 también indirectos El calculo de
	 * vecinidad es restar las coordenadas, ponerlas en valor absoluto, y
	 * calcular el máximo de estos valores. Un movimiento diagonal supone un
	 * avance de posición x e y, por lo que la vecinidad mínima es todos los
	 * posibles pasos diagonales, y los que quedan en vertical u horizontal.
	 * 
	 * @return
	 */
	public static boolean isNeighboring(int xor, int yor, int xdest, int ydest,
			int degree) {
		int distance = getDistance(xor, yor, xdest, ydest);
		return (distance <= degree);
	}

	/**
	 * La distancia se calcula teniendo en cuenta que se pueden dar pasos
	 * diagonales
	 * 
	 * @param xor
	 * @param yor
	 * @param xdest
	 * @param ydest
	 * @return
	 */
	public static int getDistance(int xor, int yor, int xdest, int ydest) {
		return (int) Math.round(Math.sqrt((xor - xdest) * (xor - xdest)
				+ (yor - ydest) * (yor - ydest)));
	}

	public static int getDistanceForFourMovements(int xor, int yor, int xdest,
			int ydest) {
		return (Math.abs(xor - xdest) + Math.abs(yor - ydest));
	}

	public static int getDistance(Person p1, Person p2) {
		return getDistance(p1.getPosition().getX(), p1.getPosition().getY(), p2
				.getPosition().getX(), p2.getPosition().getY());
	}

	/**
	 * Ver cual habitación es más cercana
	 * 
	 * @param p
	 * @param r1
	 * @param r2
	 * @return
	 */
	public static Room closer(Person p, String room1, String room2) {
		int x = p.getPosition().x;
		int y = p.getPosition().y;
		Room r1 = (Room) (p.getUbik()).getBuilding().getFloor(p.getFloor())
				.getSpaceAreaHandler().getSpaceAreaByName(room1);
		Room r2 = (Room) (p.getUbik()).getBuilding().getFloor(p.getFloor())
				.getSpaceAreaHandler().getSpaceAreaByName(room2);
		int d1 = getDistance(x, y, r1.getCenter().x, r1.getCenter().y);
		int d2 = getDistance(x, y, r2.getCenter().x, r2.getCenter().y);
		if (d1 < d2)
			return r1;
		else
			return r2;
	}
	
	/**
	 * Se le pasa lista de salidas (nombres de habitaciones en el plano);
	 * 
	 * @param exits
	 * @return Nombre de salida más cercana
	 */
	public static Room closestRoom(Person p, ArrayList<String> rooms) {
		String[] array = new String[0];
		return closestRoom(p, rooms.toArray(array));
	}

	public static Room closestRoom(Person p, String[] rooms) {
		if (rooms == null || rooms.length == 0)
			return null;
		String resultName = rooms[0];
		for (int i = 1; i < rooms.length; i++) {
			resultName = PositionTools.closer(p, resultName, rooms[i])
					.getName();
		}
		Room result = (Room) (p.getUbik()).getBuilding().getFloor(p.getFloor())
				.getSpaceAreaHandler().getSpaceAreaByName(resultName);
		return result;
	}

	/**
	 * Comprueba si la persona está en la habitación
	 * 
	 * @param p
	 * @param r
	 * @return devuelve true si la persona se haya en la habitación
	 */
	public static boolean isInRoom(Person p, Room r) {
		int x = p.getPosition().x;
		int y = p.getPosition().y;
		return r.contains(x, y);
	}

	/**
	 * Devuelve la habitación con el nombre dado
	 * 
	 * @param p
	 * @param roomName
	 * @return Devuelve la habitación con el nombre dado
	 */
	public static Room getRoom(Person p, String roomName) {

		return (Room) (p.getUbik()).getBuilding().getFloor(p.getFloor())
				.getSpaceAreaHandler().getSpaceAreaByName(roomName);
	}

	/**
	 * Lista de habitaciones. Se omiten las que no tengan nombre
	 * 
	 * @param p
	 * @return
	 */
	public static ArrayList<Room> getRooms(Person p) {
		List<SpaceArea> list = p.getUbik().getBuilding().getFloor(p.getFloor())
				.getSpaceAreaHandler().getInstancesOf(Room.class);

		ArrayList<Room> result = new ArrayList<Room>();
		for (SpaceArea sa : list) {
			Room r = (Room) sa;
			if (r != null && r.getName() != null)
				result.add(r);
		}
		return result;
	}
    
        /**
         * Get room in position given
         * @param p
         * @param x
         * @param y
         * @return 
         */
        public static Room getRoom(Person p, int x, int y){
            int cs= p.getUbik().getCellSize();
            MutableInt2D mi=PositionTools.pointMasonToPoint3D(cs, x, y);
            return p.getUbik().getBuilding().getFloor(p.getFloor()).getRoom(mi.x, mi.y);
        }

	/**
	 * Posicion aleatoria en habitación aleatoria
	 * 
	 * @param p
	 * @return
	 */
	public static Int2D getRandomPositionInRandomRoom(Person p) {
		List<Room> rooms = getRooms(p);
		Room r = rooms.get(p.getUbik().random.nextInt(rooms.size()));
		return getRandomPositionInRoom(p, r);
	}

	public static Int2D getRandomPositionInRoom(Person p, Room r) {
		Int2D[] positions = r.getPositions();
                Int2D position;
		do{
                     position = positions[p.getUbik().random.nextInt(positions.length)];
                }while(isObstacle(p,position.x,position.y) || isWall(p,position.x, position.y));
                return position;
             
	}

	/**
	 * Room de una persona
	 * 
	 * @param x
	 * @param y
	 * 
	 * @return
	 */
	public static Room getRoom(Person p) {
		return (Room) (p.getUbik()).getBuilding().getFloor(p.getFloor())
				.getSpaceAreaHandler()
				.getSpaceArea(p.getPosition().x, p.getPosition().y, Room.class);
	}

	/**
	 * Devuelve el spacio (normalmente de tipo Room) en el que se encuentra la
	 * persona.
	 * 
	 * @param p
	 * @return
	 */
	public static SpaceArea getSpaceArea(Person p) {
		return p.getUbik().getBuilding().getFloor(p.getFloor())
				.getSpaceAreaHandler()
				.getSpaceArea(p.getPosition().getX(), p.getPosition().getY());
	}

	/**
	 * Devuelve la lista de personas que hay en el simulador
	 * 
	 * @param p
	 * @param r
	 * @return Devuelve la lista de personas que hay en el simulador
	 */
	public static List<Person> getPersons(Person p) {
		List<Person> list = (p.getUbik()).getBuilding().getFloor(p.getFloor())
				.getPersonHandler().getPersons();
		return list;
	}

	/**
	 * Devuelve la lista de personas que hay en una habitación
	 * 
	 * @param p
	 * @param r
	 * @return Devuelve la lista de personas que hay en la habitación
	 */
	public static List<Person> getPersons(Person p, Room r) {
		List<Person> list = getPersons(p);
		List<Person> roomList = new LinkedList();
		for (Person person : list) {
			int x = person.getPosition().x;
			int y = person.getPosition().y;
			if (r.contains(x, y)) {
				roomList.add(person);
			}
		}
		return roomList;
	}

	/**
	 * Devuelve una persona con el nombre dado name
	 * 
	 * @param person
	 * @param name
	 * @return Devuelve una persona con el nombre dado name
	 */
	public static Person getPerson(Person person, String name) {
		List<Person> list = getPersons(person);
		for (Person p : list) {
			if (p.getName().equals(name))
				return p;
		}
		return null;
	}
    /**
     * Return first person in position or null if there is a person in that position
     * @param x
     * @param y
     * @return
     */
    

        
        public static Person getPerson(Person p, int x, int y){ 
            try{
            Bag b= (p.getUbik()).getBuilding().getFloor(p.getFloor()).getPersonHandler().getGrid().getObjectsAtLocation(x, y);        
            if(b==null) return null;
            if(((Person) b.get(0)).isStopped()) LOG.severe("There is an agent stopped and in the environment, use getOutOfSpace if want to remove it, agent: " + ((Person) b.get(0)).getName() );
            return (Person) b.get(0);
            }
            catch(Exception e){                         
                e.printStackTrace();
                return null;
            }
            
      
                    
        }

	/**
	 * Devuelve una lista de personas que estén a menos de distace (en cm),
	 * distance * tamaño celda de la persona person.
	 * 
	 * @param person
	 * @param distance
	 * @return Devuelve una lista de personas que estén a menos de distace (en
	 *         cm) de la persona person.
	 */
	public static ArrayList<Person> getPersons(Person person, int distance) {

		int distanceWithCell = distance * person.getUbik().getCellSize();
		List<Person> persons = (person.getUbik()).getBuilding()
				.getFloor(person.getFloor()).getPersonHandler().getPersons();
		ArrayList<Person> result = new ArrayList<Person>();
		for (Person paux : persons) {
			if (PositionTools.isNeighboring(person.getPosition().x,
					person.getPosition().y, paux.getPosition().x,
					paux.getPosition().y, distanceWithCell))
				result.add(paux);
		}
		return result;
		// return
		// (person.getUbik()).getBuilding().getFloor(person.getFloor()).getPersonHandler().getPersons(person.getPosition().x,
		// person.getPosition().y, distance*person.getUbik().getCellSize());
	}

	/**
         *See the pathfinder demos instead
	 * Devuelve la lista de NodeGraph que se encuentran en un spacio dado.
	 * 
         * @deprecated        
	 * @param sa
	 * @return
	 */
        
	public static List<NodeGraph> getNodeGraphInSpaceArea(SpaceArea sa) {
		return NodeGraphCollection.getInstance().getNodeGraphInSpaceArea(sa);
	}

	/**
	 * Devuelve el NodeGraph con nombre dado.
	 * @deprecated 
	 * @param nodeGraphName
	 * @return
	 */
	public static NodeGraph getNodeGraph(String nodeGraphName) {
		return NodeGraphCollection.getInstance().getNodeGraph(nodeGraphName);
	}

	/**
	 * See the pathfinder demos instead
         * @deprecated 
         * Devuelve la lista de coordenadas que marcan el camino de menor peso desde
	 * el punto ori a dest.	   
	 * @param p
	 * @param ori
	 * @param dest
	 * @return
	 */
	public static List<Int2D> getRoute(Person p, Int2D ori, Int2D dest) {
		return p.getUbik().getBuilding().getBuildingToGraph().getRoadMap()
				.getRoute(ori, dest);
	}

	/**
         * @deprecated 
         * See the pathfinder demos instead
	 * Devuelve la distancia física entre el punto ori y dest
	 * 
	 * @param p
	 * @param ori
	 * @param dest
	 * @return
	 */
	public static float getDistanceRoute(Person p, Int2D ori, Int2D dest) {
		float result = -1;
		GraphPath<Node, DefaultArc<Node>> graphPath = p.getUbik().getBuilding()
				.getBuildingToGraph().getRoadMap().getGraphPath(ori, dest);
		if (graphPath != null) {
			Node n1 = null;
			Node n2 = null;
			boolean init = false;
			Iterator<Node> it = graphPath.getIterator();
			while (it.hasNext()) {
				if (!init) {
					n1 = it.next();
					init = true;
					continue;
				}
				n2 = it.next();
				result += getDistance(n1.getPosition().getX(), n1.getPosition()
						.getY(), n2.getPosition().getX(), n2.getPosition()
						.getY());
				n1 = n2;
			}
		}
		return result;
	}

	/**
	 * Obtener angulo en radianes para mirar de un punto origen a un punto
	 * destino. Se le deben pasar angulos en formato Ubik3D o en formato MASOn,
	 * pero ambos iguales Devolverá un angulo en 3d o mason según formato.
	 * 
	 * En el editor, norte es 180 grados, este 270, sur 0, y oeste 90
	 * 
	 * @param xOr
	 * @param yOr
	 * @param xDes
	 * @param yDes
	 * @return
	 */
	public static double angleToLookInRadians(double xOr, double yOr,
			double xDes, double yDes) {
		MutableDouble2D v = new MutableDouble2D((xDes - xOr), (yDes - yOr));
		double angle;
		if (v.getX() == 0) {
			if (v.getY() > 0) {
				angle = Math.PI / 2.0;
			} else {
				angle = 3.0 * Math.PI / 2.0;
			}
		} else {
			angle = Math.atan(v.getY() / v.getX());
			if (xDes - xOr < 0)
				angle += Math.PI;
		}
		angle = Math.atan2(v.getY(), v.getX());
		return angle;
	}

	/**
	 * Devuelve angulo para que una persona mire (formaton Mason) para
	 * p.setAngle(result)
	 * 
	 * @param p
	 * @param object3d
	 * @return
	 */
	public static double angleToLookInRadians(Person p,
			HomePieceOfFurniture object3d) {
		int cellSize = p.getUbik().getCellSize();
		MutableInt2D destiny = point3DToPointMason(cellSize, object3d.getX(),
				object3d.getY());
		return angleToLookInRadians(p.getPosition().x, p.getPosition().y,
				destiny.x, destiny.y);
	}

	/**
	 * Punto de aproximación a un objeto
	 * 
	 * @param objectInMap
	 *            modelo 3d del objeto
	 * @param approxRadio
	 *            distancia a la que te quedas (centimetros, 100 es un metro)
	 * @param approxAngle
	 *            angulo para aproximación (radianes)
	 * @param isLeft
	 *            a la izquierda del ángulo del objeto o a su derecha. Ejemplo:
	 *            (currentQR.getDevice3DModel(),cellSize,100,0,true), se pone
	 *            delante de un código QR Devuelve un punto formato Mason
	 * @return
	 */
	public static MutableInt2D getApproximationPoint(
			HomePieceOfFurniture object3d, int cellSize, double approxRadio,
			double approxAngle, boolean isLeft) {

		double angle = angleHome3DtoAngleMason(object3d.getAngle());
		MutableInt2D objectInMap = point3DToPointMason(cellSize,
				object3d.getX(), object3d.getY());
		if (!isLeft)
			angle = angle - approxAngle;
		else
			angle = angle + approxAngle;

		MutableDouble2D p = new MutableDouble2D(object3d.getX() + approxRadio
				* Math.cos(angle), object3d.getY() + approxRadio
				* Math.sin(angle));

		return point3DToPointMason(cellSize, (float) p.getX(), (float) p.getY());
	}

	/**
	 * Arregla desfase entre punto de un objeto sobre el grid y un objeto 3d
	 * sobre el plano
	 * 
	 * @param angle3D
	 * @return
	 */
	public static float angleHome3DtoAngleMason(float angle3D) {
		return angle3D + (float) Math.PI / 2;
	}

	/**
	 * Arregla desfase entre punto de un objeto sobre el grid y un objeto 3d
	 * sobre el plano
	 * 
	 * @param angleMason
	 * @return
	 */
	public static float angleMasonToAngle3D(float angleMason) {
		return angleMason - (float) Math.PI / 2;
	}

	/**
	 * Arregla desfase entre punto de un objeto sobre el grid y un objeto 3d
	 * sobre el plano
	 * 
	 * @param cellSize
	 * @param x
	 * @param y
	 * @return
	 */

	public static MutableInt2D point3DToPointMason(int cellSize, float x,
			float y) {
		MutableInt2D point = new MutableInt2D(Math.round(x / cellSize),
				Math.round(y / cellSize));
		return point;
	}

	/**
	 * Arregla desfase entre punto de un objeto sobre el grid y un objeto 3d
	 * sobre el plano
	 * 
	 * @param cellSize
	 * @param x
	 * @param y
	 * @return
	 */

	public static MutableInt2D pointMasonToPoint3D(int cellSize, float x,
			float y) {
		MutableInt2D point = new MutableInt2D((int) x * cellSize, (int) y
				* cellSize);
		return point;
	}

	/**
	 * Comprobar si en una posición cabe la persona para un movimiento, una
	 * posición
	 * 
	 * @param p
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean isObstacle(Person p, int x, int y) {
		return p.getUbik().getBuilding().getFloor(p.getFloor())
				.isObstacle(p, x, y);
	}
        
        public static boolean isWall(Person p, int x, int y) {
		return p.getUbik().getBuilding().getFloor(p.getFloor()).isWall(x, y);
	}

	/**
	 * Ver si una persona que tiene turno de ejecución está en el espacio.
	 * 
	 * @param p
	 * @return
	 */
	public static boolean isInSpace(Person p) {
		return !(p.getUbik().getBuilding().getFloor(p.getFloor())
				.getPersonHandler().getGrid().getObjectIndex(p) == -1);
	}

	/**
	 * Checks if there are any person in the scenario
	 * 
	 * @param ubik
	 */
	public static boolean existAnyPersonInSpace(Ubik ubik) {
		for(Person p: ubik.getBuilding().getFloor(0)
				.getPersonHandler().getPersons())
			if(isInSpace(p))
				return true;
		return false;
	}
	/**
	 * Sacar a una persona del grid, seguirá recibiendo turno en step de
	 * ejecución
	 * 
	 * @param p
	 * @param x
	 * @param y
	 * @return
	 */
	public static void getOutOfSpace(Person p) {
		p.getUbik().getBuilding().getFloor(p.getFloor()).getHome()
				.deletePieceOfFurniture(p.getPerson3DModel());
		p.getUbik().getBuilding().getFloor(p.getFloor()).getPersonHandler()
				.getGrid().remove(p);
	}

	/**
	 * Método pensado para añadir de nuevo en el plano a agentes que hayan
	 * salido con getOutOfSpace. Falso si por obstaculos no se puede añadir.
	 * 
	 * @param p
	 * @param x
	 * @param y
	 */
	public static boolean putInSpace(Person p, int x, int y) {
		if (isObstacle(p, x, y)) {
			return false;
		} else {
			p.setPosition(x, y);
			/*MutableInt2D p3d = PositionTools.pointMasonToPoint3D(p.getUbik()
					.getCellSize(), x, y);
			// System.out.println("Putting person " + p.getName() +
			// " in space [" + x + "," + y + "], 3D space " + p3d.x + "," +
			// p3d.y);
			// grid y position of agent, MASON coordenades
			p.getUbik().getBuilding().getFloor(p.getFloor()).getPersonHandler()
					.getGrid().setObjectLocation(p, x, y);
			p.setPosition(x, y);
			// 3D model, 3D coordenates
			p.getPerson3DModel().setX(p3d.x);
			p.getPerson3DModel().setY(p3d.y);
			p.getPerson3DModel().setVisible(true);*/
			p.getUbik().getBuilding().getFloor(p.getFloor()).getHome()
					.addPieceOfFurniture(p.getPerson3DModel());
			return true;
		}
	}

	/**
	 * Returns an instance of a person given her name.
	 * 
	 * @param ubik
	 * @param name
	 * @return
	 */
	public static Person getPerson(Ubik ubik, String name) {
		Person person = ubik.getBuilding().getFloor(0).getPersonHandler()
				.getPersonByName(name);
		return person;
	}
        
      
	
	/**
	 * Returns the distance of the shortest path beteewn the given two points.
	 *  
	 * @param ubik
	 * @param ori
	 * @param dest
	 * @return
	 */
	public static float getPathDistance(Ubik ubik, Int2D ori, Int2D dest) {
		List<Int2D> path = ubik.getBuilding().getBuildingToGraph().getRoadMap().getRoute(ori, dest);
		if(path == null)
			return Float.MAX_VALUE;
		
		Int2D actualPoint = ori;
		float result = 0.0f;
		for(int index = 0; index < path.size(); index++) {
			result += actualPoint.distance(path.get(index));
			actualPoint = path.get(index);
		}
		
		return result;
	}
	
	/**
	 * Return the closest ConnectionSpaceInABuilding which belongs to the given spaceArea.
	 * 
	 * @param person
	 * @param sa
	 * @return
	 */
	public static float getPathDistanceToSpaceArea(Person person, SpaceArea spaceArea) {
		Int2D personPosition = new Int2D(person.getPosition().x,person.getPosition().y);
		float minDistance = Float.MAX_VALUE;
		for(ConnectionSpaceInABuilding csb: spaceArea.getConnectionSpace()) {
			float dist = getPathDistance(person.getUbik(), personPosition, csb.getCenter());
			if(dist < minDistance) {
				minDistance = dist;
			}
		}
		return minDistance;
	}
	
	public static SpaceArea getTheClosestSpaceAreaContainsString(Person person, String name) {
		List<SpaceArea> areas = person.getUbik().getBuilding().getFloor(0).getSpaceAreaHandler().getSpaceAreasWithNameContains(name);
		SpaceArea result = null;
		Int2D personPosition = new Int2D(person.getPosition().x,person.getPosition().y);
		float minDistance = Float.MAX_VALUE;
		for(SpaceArea sa: areas) {			
			float dist = getPathDistanceToSpaceArea(person, sa);
			System.out.println(sa.getName()+" distance "+dist);
			if(dist < minDistance) {
				minDistance = dist;
				result = sa;
				System.out.println("actual result = "+result.getName());
			}		
		}
		return result;
	}	
        
        
        
        /**
     * Insert a colour figure  in the map for debugging purposes (is not an obstacle)
     * The same idea can be used to insert any 3d object and change its size 
     */
    public static HomePieceOfFurniture insertMakerInDisplay(Person p, int x, int y, Color color){

              
            
  
            //object 3d imported, it must be in the same folder than the second parameter (inside src and build)
            HomePieceOfFurniture hpof=null;
          
            try {
                URL url = Thread.currentThread().getContextClassLoader().getResource("ubik3d/io/resources/pictures/Ubik/cylinder.obj");    //3D model
                String name= p.getName() + " marker";             
                Content modelContent= new ResourceURLContent(url,false);
                final BranchGroup modelNode = ModelManager.getInstance().loadModel(modelContent);
                Vector3f modelSize = ModelManager.getInstance().getSize(modelNode);        
                CatalogPieceOfFurniture piece = new CatalogPieceOfFurniture(
				"", 		/* Metadata */
				"",             /* Rol */
				name, 		/* Name */
				null,	/* Icon */
				modelContent, 	/* Model */
				0, 		/* Amount */
				5.0f, 		/* Width */
				0.9f, 		/* Depth */
				7.8f, 		/* Height */
				160,		/* Elevation */
			        true, 		/* Movable */
			        null, 		/* Color */
			        new float [] [] { {1, 0, 0}, {0, 1, 0}, {0, 0, 1}}, /* ModelRotation */
			        false, 							/* isBackFaceShown */
			        (float) Math.PI / 8,			/* IconYaw */
			        true); 							/* isProportional*/
        
                hpof = new HomePieceOfFurniture(piece);
                hpof.setWidth(modelSize.x);
                hpof.setDepth(modelSize.z);
                hpof.setHeight(modelSize.y);
                hpof.setNameVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(PositionTools.class.getName()).log(Level.SEVERE, null, ex);
            }           
            
            
            int cellSize=p.getUbik().getCellSize(); 
            MutableInt2D point3D = PositionTools.pointMasonToPoint3D(cellSize,x, y);//the positions in the 3d display are not the same than in the MASON grid
            hpof.setHeight(1);     
            hpof.setWidth(1*cellSize);
            hpof.setDepth(1*cellSize);
            hpof.setX(point3D.x);
            hpof.setY(point3D.y);
            
            p.getUbik().getBuilding().getFloor(p.getFloor()).getHome().addPieceOfFurniture(hpof);
            hpof.setColor(color.getRGB());            
            return hpof;
        
    }
    
    
    /**
     * remove a marker or other element inserted in the display (is not an obstacle)
     * @param p
     * @param homeFurniture 
     */
    public static void removeMarkerInDisplay(Person p, HomePieceOfFurniture homeFurniture){
          p.getUbik().getBuilding().getFloor(p.getFloor()).getHome().deletePieceOfFurniture(homeFurniture);
    }
          
          
    
}
