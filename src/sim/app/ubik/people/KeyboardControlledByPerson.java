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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import sim.app.ubik.Ubik;
import sim.app.ubik.building.connectionSpace.ConnectionSpaceInABuilding;
import sim.app.ubik.building.connectionSpace.Door;
import sim.app.ubik.building.rooms.Room;
import sim.app.ubik.displays.ViewPerspective;
import sim.app.ubik.displays.ViewPerspectiveListener;
import sim.app.ubik.representation.PersonStateRepresentation;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.Int2D;
import ubik3d.model.Home;
import ubik3d.swing.HomeComponent3D;


public class KeyboardControlledByPerson implements Steppable, KeyListener,
		ViewPerspectiveListener {
	private KeyEvent lastKeyEvent;
	private Home home;
	private Ubik ubik;
	private int floor;
	private ViewPerspective viewPerspective;

	private Person controlledPerson;
	private boolean init;

	private static KeyboardControlledByPerson keyboardControlledByPerson;

	public static KeyboardControlledByPerson getInstance() {
		if (keyboardControlledByPerson == null) {
			keyboardControlledByPerson = new KeyboardControlledByPerson();
		}
		return keyboardControlledByPerson;
	}

	private KeyboardControlledByPerson() {
	}

	public void init(int floor, ViewPerspective viewPerspective,
			HomeComponent3D hc3d, Ubik ubik) {
		if (!init) {
			this.floor = floor;
			this.viewPerspective = viewPerspective;
			this.ubik = ubik;
			home = ubik.getHomes().get(0);
			hc3d.addKeyListener(this);
		}
		init = true;
	}

	@Override
	public void notifyViewPerspectiveChanged(ViewPerspective vp) {
		viewPerspective = vp;
		if (isViewFromObserver()) {
			setup(controlledPerson);
		} else {
			if(controlledPerson != null)
				controlledPerson.getPerson3DModel().setVisible(true);
		}
	}

	public void setControlledPerson(Person person) {
		if (this.controlledPerson != null) {
			controlledPerson.setKeyControlPerson(null);
			controlledPerson.getPerson3DModel().setVisible(true);
		}
		setup(person);
	}

	private void setup(Person person) {
		controlledPerson = person;
		if (controlledPerson != null) {
			controlledPerson.setKeyControlPerson(this);
			if (isViewFromObserver()) {
				home.getCamera().setX(person.getPerson3DModel().getX());
				home.getCamera().setY(person.getPerson3DModel().getY());
				home.getCamera().setZ(person.getPerson3DModel().getHeight());
				home.getCamera().setYaw(person.getPerson3DModel().getAngle());
				person.getPerson3DModel().setVisible(false);
			}
		}
	}

	private boolean isViewFromObserver() {
		return viewPerspective.getPerspective().equals(
				ViewPerspective.VIEW_FROM_OBSERVER);
	}

	@Override
	public void step(SimState state) {
		if (controlledPerson == null)
			return;
		if (lastKeyEvent != null) {
			switch (lastKeyEvent.getKeyChar()) {
			case 'u':
				// to simulate the person is moving
				controlledPerson.setMoving(!controlledPerson.isMoving);
				break;
			case '-':
				// decrease person speed
				controlledPerson.setSpeed(controlledPerson.getSpeed() / 2.0);
				break;
			case '+':
				// increase person speed
				controlledPerson.setSpeed(controlledPerson.getSpeed() * 2.0);
				break;
			case 'j':
				// move the person to left
				controlledPerson.setAngle(controlledPerson.getAngle() - Math.PI
						/ 8.0);
				controlledPerson.getPerson3DModel()
						.setAngle(
								getNormalAngle((float) (controlledPerson
										.getAngle() - Math.PI / 2.0)));
				if (isViewFromObserver())
					home.getCamera().setYaw(
							controlledPerson.getPerson3DModel().getAngle());
				break;
			case 'l':
				// move the person to right
				controlledPerson.setAngle(controlledPerson.getAngle() + Math.PI
						/ 8.0);
				controlledPerson.getPerson3DModel()
						.setAngle(
								getNormalAngle((float) (controlledPerson
										.getAngle() - Math.PI / 2.0)));
				if (isViewFromObserver())
					home.getCamera().setYaw(
							controlledPerson.getPerson3DModel().getAngle());
				break;
			case 'i':
				// the person goes ahead depending his speed
				controlledPerson.move();
				if (isViewFromObserver()) {
					home.getCamera().setX(
							controlledPerson.getPerson3DModel().getX());
					home.getCamera().setY(
							controlledPerson.getPerson3DModel().getY());
				}
				break;
			case 'k':
				// the person turns back
				controlledPerson
						.setAngle(controlledPerson.getAngle() - Math.PI);
				controlledPerson.getPerson3DModel()
						.setAngle(
								getNormalAngle((float) (controlledPerson
										.getAngle() - Math.PI / 2.0)));
				if (isViewFromObserver())
					home.getCamera().setYaw(
							controlledPerson.getPerson3DModel().getAngle());
				break;
			case 'o':
				// open/close a door
				int x = controlledPerson.getPosition().x;
				int y = controlledPerson.getPosition().y;
				Room room = (Room) ubik.getBuilding().getFloor(floor)
						.getSpaceAreaHandler().getSpaceArea(x, y, Room.class);
				if (room != null) {
					ConnectionSpaceInABuilding cs = room
							.getConnectionSpaceNearerTo(x, y);
					if (cs != null) {
						if (cs instanceof Door) {
							Door door = (Door) cs;
							if (cs.getCenter().distance(new Int2D(x, y))
									* ubik.getCellSize() < 100) {
								if (door.isOpened()) {
									door.close();
								} else {
									door.open();
								}
							}
						}
					} else {
						System.out.println("A door has not been found!");
					}
				} else {
					System.out.println("A room has not been found!");
				}
				break;
			case '0':
				controlledPerson.setState(PersonStateRepresentation.STATE_0);
				break;
			case '1':
				controlledPerson.setState(PersonStateRepresentation.STATE_1);
				break;
			case '2':
				controlledPerson.setState(PersonStateRepresentation.STATE_2);
				break;
			case '3':
				controlledPerson.setState(PersonStateRepresentation.STATE_3);
				break;
			case '4':
				controlledPerson.setState(PersonStateRepresentation.STATE_4);
				break;
			case '5':
				controlledPerson.setState(PersonStateRepresentation.STATE_5);
				break;
			case '6':
				controlledPerson.setState(PersonStateRepresentation.STATE_6);
				break;
			case '7':
				controlledPerson.setState(PersonStateRepresentation.STATE_7);
				break;
			case '8':
				controlledPerson.setState(PersonStateRepresentation.STATE_8);
				break;
			case '9':
				controlledPerson.setState(PersonStateRepresentation.STATE_9);
				break;
			case 'z':
				controlledPerson.setState("cooking");
				break;
			}
		}
		lastKeyEvent = null;
	}

	private float getNormalAngle(float angle) {
		float result = angle;
		if (result > 2 * Math.PI) {
			return (float) (result - 2 * Math.PI);
		} else if (result < 0) {
			return (float) (2 * Math.PI + result);
		}
		return result;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		lastKeyEvent = e;
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	public Person getControlledPerson() {
		return controlledPerson;
	}
}
