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
package sim.app.ubik.view;

import java.util.ArrayList;
import java.util.List;

import sim.app.ubik.Ubik;
import sim.app.ubik.building.OfficeFloor;
import sim.app.ubik.people.Person;

public class ViewManagerController {
	protected Ubik ubik;
	protected List<Person> people;
	protected List<OfficeFloor> floors;	
	
	public ViewManagerController(Ubik ubik) {
		this.ubik = ubik;
		
		floors = new ArrayList<OfficeFloor>();
		people = new ArrayList<Person>();
		
		for(OfficeFloor of: ubik.getBuilding().getFloors()) {
			floors.add(of);						
			for(Person p: of.getPersonHandler().getPersons()) {
				people.add(p);
			}
		}
		
		new ViewManagerFrame(this);
	}

	public Ubik getUbik() {
		return ubik;
	}

	public List<Person> getPeople() {
		return people;
	}

	public List<OfficeFloor> getFloors() {
		return floors;
	}
}
