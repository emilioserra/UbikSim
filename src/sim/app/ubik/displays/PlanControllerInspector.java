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

import java.util.List;

import javax.swing.undo.UndoableEditSupport;

import sim.app.ubik.Ubik;
import sim.app.ubik.building.SpaceArea;
import sim.app.ubik.building.connectionSpace.ConnectionSpaceInABuilding;
import sim.app.ubik.building.connectionSpace.Door;
import sim.app.ubik.building.rooms.Room;
import sim.app.ubik.domoticDevices.DomoticDevice;
import sim.app.ubik.furniture.Furniture;
import sim.app.ubik.people.Person;
import sim.display.GUIState;
import sim.portrayal.Inspector;
import sim.portrayal.SimpleInspector;
import sim.util.Bag;
import ubik3d.model.Home;
import ubik3d.model.HomePieceOfFurniture;
import ubik3d.model.UserPreferences;
import ubik3d.viewcontroller.ContentManager;
import ubik3d.viewcontroller.PlanController;
import ubik3d.viewcontroller.ViewFactory;

public class PlanControllerInspector extends PlanController {

	private Home home;
	private UserPreferences preferences;
	private ViewFactory viewFactory;
	private ContentManager contentManager;
	private UndoableEditSupport undoSupport;
	private GUIState guiState;

	public PlanControllerInspector(GUIState guiState, Home home,
			UserPreferences preferences, ViewFactory viewFactory,
			ContentManager contentManager, UndoableEditSupport undoSupport) {
		super(home, preferences, viewFactory, contentManager, undoSupport);
		this.guiState = guiState;
		this.home = home;
		this.preferences = preferences;
		this.viewFactory = viewFactory;
		this.contentManager = contentManager;
		this.undoSupport = undoSupport;
	}

	@Override
	public void modifySelectedWalls() {
		/*
		 * if (!Home.getWallsSubList(home.getSelectedItems()).isEmpty()) { new
		 * WallController(this.home, this.preferences, this.viewFactory,
		 * this.contentManager, this.undoSupport) .displayView(getView()); }
		 */
	}

	/**
	 * Controls the modification of the selected rooms.
	 */
	@Override
	public void modifySelectedRooms() {
		Bag inspectors = new Bag();
		Bag names = new Bag();

		List<ubik3d.model.Room> list = Home.getRoomsSubList(this.home
				.getSelectedItems());
		if (!list.isEmpty()) {
			for (ubik3d.model.Room room : list) {
				if (room.getName() != null && !room.equals("")) {
					Object obj = getObject(room);
					if (obj != null) {
						Inspector inspector = new SimpleInspector(obj, guiState,
								"Properties");
						inspectors.add(inspector);
						names.add(room.getName());
					}
				}
			}
			guiState.controller.setInspectors(inspectors, names);
		}
	}

	/**
	 * Controls the modification of the selected labels.
	 */
	@Override
	public void modifySelectedLabels() {
	}

	/**
	 * Controls the modification of the compass.
	 */
	@Override
	public void modifyCompass() {
	}

	/**
	 * Controls the modification of selected furniture.
	 */
	@Override
	public void modifySelectedFurniture() {
		Bag inspectors = new Bag();
		Bag names = new Bag();

		List<HomePieceOfFurniture> list = Home.getFurnitureSubList(this.home
				.getSelectedItems());
		if (!list.isEmpty()) {
			for (HomePieceOfFurniture hpof : list) {
				Object obj = getObject(hpof);
				if (obj != null) {
					Inspector inspector = new SimpleInspector(obj, guiState,
							"Properties");
					inspectors.add(inspector);
					names.add(hpof.getName());
				}
			}
			guiState.controller.setInspectors(inspectors, names);
		}
	}

	public Object getObject(ubik3d.model.Room room) {
		Ubik ubik = (Ubik) guiState.state;

		for (SpaceArea sa : ubik.getBuilding().getFloor(0)
				.getSpaceAreaHandler().getSpaceAreas()) {
			if (sa instanceof Room) {
				Room r = (Room) sa;
				if (room.getName().equals(r.getName()))
					return r;
			}
		}
		return null;
	}

	public Object getObject(HomePieceOfFurniture hpof) {
		Ubik ubik = (Ubik) guiState.state;

		for (Person p : ubik.getBuilding().getFloor(0).getPersonHandler()
				.getPersons()) {
			if (p.getPerson3DModel().equals(hpof))
				return p;
		}

		for (DomoticDevice dd : ubik.getBuilding().getFloor(0)
				.getDeviceHandler().getDomoticDevice()) {
			if (dd.getDevice3DModel().equals(hpof))
				return dd;
		}

		for (Furniture f : ubik.getBuilding().getFloor(0).getFurnitureHandler()
				.getFurnitures()) {
			if (f.getFurniture3DModel().equals(hpof)) {
				return f;
			}
		}

		if (hpof.isDoorOrWindow()) {
			for (SpaceArea sa : ubik.getBuilding().getFloor(0)
					.getSpaceAreaHandler().getSpaceAreas()) {
				for (ConnectionSpaceInABuilding csib : sa.getConnectionSpace()) {
					if (csib instanceof Door) {
						Door door = (Door) csib;
						if (door.getModel().equals(hpof))
							return door;
					}
				}
			}
		}

		return null;
	}

}
