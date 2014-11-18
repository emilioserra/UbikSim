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
package sim.app.ubik.people;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import sim.app.ubik.MovementTools;
import sim.app.ubik.Ubik;
import sim.app.ubik.behaviors.PositionTools;
import sim.app.ubik.representation.PersonLabelRepresentation;
import sim.app.ubik.representation.PersonStateRepresentation;
import sim.app.ubik.utils.Configuration;
import sim.app.ubik.utils.ElementsHandler;
import sim.util.Bag;
import sim.util.Int2D;
import ubik3d.model.Home;
import ubik3d.model.HomePieceOfFurniture;


public class PersonHandler extends ElementsHandler {

	private String file = "handlers/persons.txt";
	private static int NUM_PERSONS = 20; // Si hay menos de 20 personas se
											// buscan en la lista, si no en el
											// grid.
	protected List<Person> persons;
	protected String keyboardControledPerson = null;
	protected Configuration configuration;
        
       private static final Logger LOG = Logger.getLogger(PersonHandler.class.getName());

	public PersonHandler(Ubik ubik, int floor, int GRID_WIDTH, int GRID_HEIGHT) {
		super(ubik, floor, GRID_WIDTH, GRID_HEIGHT);

		persons = new ArrayList<Person>();
		initRols(file);

		this.ocp = ubik.createOCPProxyProducer("UBIK.PersonHandler");

		// Se genera las representaciones de los estados de las personas por
		// defecto
		PersonStateRepresentation.initDefaultStates(ubik);
	}

	public void add(Person p) {
		persons.add(p);
		grid.setObjectLocation(p, p.getPosition().getX(), p.getPosition()
				.getY());
	}

	public List<Person> getPersonsInstanceOf(Class clase) {
		List<Person> result = new ArrayList();
		for (Person p : persons) {
			if (clase.isInstance(p)) {
				result.add(p);
			}
		}
		return result;
	}

	public void clear() {
		persons.clear();
		grid.clear();
		Person.clearIDGenerator();
	}
    

	public void generatePeople(int floor, int cellSize) {
		LOG.info("\nGenerating people:\n");
		Home home = ubik.getBuilding().getFloor(floor).getHome();
		for (HomePieceOfFurniture hpof : home.getFurniture()) {
			Person p = null;
			if (rols.contains(hpof.getRol())) {
				if (hpof.getAmount() <= 1) {
					LOG.config("ubik " + hpof.getRol() + ","
							+ hpof.getName());
					p = createPerson(floor, hpof, ubik);
					if (p != null) {
						LOG.config("Person created: " + p.getId());
						add(p);
					}
				} else {
					home.deletePieceOfFurniture(hpof);
					addPersons((int) hpof.getAmount(), true, hpof);
				}
			}
		}
	}

	/**
	 * Create a person invoking the constructor associated to its class. Note
	 * that if an exception is thrown from the invoked constructor, an
	 * InvocationTargetException trace from this method is shown as output.
	 * 
	 * @param floor
	 * @param piece
	 * @param ubik
	 * @return
	 */
	public Person createPerson(int floor, HomePieceOfFurniture piece, Ubik ubik) {
		Person result = null;
		try {
			for (Constructor c : Class.forName(
					"sim.app.ubik.people." + piece.getRol()).getConstructors()) {
				try {
					result = (Person) c.newInstance(floor, piece, ubik);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					System.err
							.println("An exception was thrown at constructor of the class "
									+ ("sim.app.ubik.people." + piece.getRol())
									+ ". Please, revise this constructor.");
					e.printStackTrace();
					continue;
				}
				break;
			}
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        if (result != null) {
            result.fixStopable(ubik.schedule);
            if (ocp != null) {
                result.createInOCP(ocp.getContextService());
            }
            // Añadimos una clase que representa el estado de la persona
            result.addObserver(new PersonStateRepresentation(ubik, result));
            // A new class which represents the label of the person is added
            result.addObserver(new PersonLabelRepresentation(ubik, result));
        }

		return result;
	}

	public void remove(Person person) {
		if (person != null) {
			grid.remove(this);
			persons.remove(person);
		}
	}

	public List<Person> getPersons() {
		return persons;
	}

	public Person getPersonByName(String name) {
		for (Person p : persons)
			if (p.getName().equals(name))
				return p;
		return null;
	}

	public Person getPerson(int x, int y) {
		if (x < grid.getWidth() && y < grid.getHeight()) {
			for (Object o : grid.getObjectsAtLocation(x, y)) {
				if (o instanceof Person) {
					return (Person) o;
				}
			}
		}
		return null;
	}

	public List<Person> getPersons(int x, int y, int radio) {
		List<Person> result = new ArrayList();
		if (persons.size() < NUM_PERSONS) {
			for (Person p : persons) {
				if (MovementTools.getInstance().getDistance(p.getPosition().x,
						p.getPosition().y, x, y) <= radio) {
					result.add(p);
				}
			}
		} else {
			Bag b = grid.getNeighborsMaxDistance(x, y, radio, false, null,
					null, null);
			for (int i = 0; i < b.size(); i++) {
				Person p = (Person) b.get(i);
				result.add(p);
			}
		}
		return result;
	}

	public Person getPersonIn(int x, int y) {
		if (persons.size() < NUM_PERSONS) {
			for (Person p : persons) {
				if (p.isBodyRadioIn(x, y)) {
					return p;
				}
			}
		} else {
			Bag b = grid.getNeighborsMaxDistance(x, y,
					100 / ubik.getCellSize(), false, null, null, null);
			for (int i = 0; i < b.size(); i++) {
				Person p = (Person) b.get(i);
				if (p.isBodyRadioIn(x, y)) {
					return p;
				}
			}
		}
		return null;
	}

	/**
	 * Remove the last n persons registered. They are also removed from the
	 * schedule of MASON.
	 * 
	 * @param n
	 */
	public void removePersons(int n) {
		for (int i = 0; i < n; i++) {
			Person p = (getPersons().get(getPersons().size() - 1));
			p.stop();// this remove it from the list too
		}
	}

	/**
	 * Create n agents cloning the first agent registered or using a 3d model
	 * passed as parameter
	 * 
	 * @param n
	 * @param randomPosition
	 *            Put a random position for the agent or not putting it in the
	 *            space at all. Only rooms with name are considered
	 * @param HomePieceOfFurniture
	 *            optional, 3dmodel
	 * 
	 */
	public void addPersons(int n, boolean randomPosition,
			HomePieceOfFurniture hof) {
		Person prototype = getPersons().get(0);
		for (int i = 0; i < n; i++) {
			Person pcreated;
			if (hof == null)
				pcreated = createPerson(prototype.getFloor(),
						new HomePieceOfFurniture(prototype.getPerson3DModel()),
						ubik);
			else
				pcreated = createPerson(prototype.getFloor(),
						new HomePieceOfFurniture(hof), ubik);
			add(pcreated);
			if (!randomPosition)
				PositionTools.getOutOfSpace(pcreated);
			else
				addPersonInRandomPosition(pcreated);
		}
	}

	/**
	 * Create n agents cloning the given pattern
	 * 
	 * @param n
	 * @param randomPosition
	 *            Put a random position for the agent or not putting it in the
	 *            space at all. Only rooms with name are considered.
	 * @param pattern person who will be cloned
	 * 
	 */
	public void addPersons(int n, boolean randomPosition,
			Person pattern) {
		for (int i = 0; i < n; i++) {
				Person pcreated = createPerson(pattern.getFloor(),
						new HomePieceOfFurniture(pattern.getPerson3DModel()),
						ubik);
			add(pcreated);
			if (!randomPosition)
				PositionTools.getOutOfSpace(pcreated);
			else
				addPersonInRandomPosition(pcreated);
		}
	}
	
	/**
	 * Put a random position for the agent or not putting it in the space at
	 * all. Only rooms with name are considered
	 * 
	 * @param pcreated
	 */
	public void addPersonInRandomPosition(Person pcreated) {
		Int2D pos;
		do {
			pos = PositionTools.getRandomPositionInRandomRoom(pcreated);
		} while (PositionTools.isObstacle(pcreated, pos.x, pos.y));
		PositionTools.putInSpace(pcreated, pos.x, pos.y);
	}

	
	/**
	 * Change name of agents for "name + index" 
	 * @param name
	 */
	public void changeNameOfAgents(String name) {
		int i = 1;
		for (Person p : getPersons()) {
			p.setName(name + i);
			i++;
		}
	}
}
