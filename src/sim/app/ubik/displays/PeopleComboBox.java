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

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

import sim.app.ubik.Ubik;
import sim.app.ubik.people.Person;

public class PeopleComboBox implements ComboBoxModel {
	private Ubik ubik;
	
	private Person selected = null;
	
	public PeopleComboBox(Ubik ubik) {
		this.ubik = ubik;
	}
	

	public int getSize() {
		return ubik.getBuilding().getFloor(0).getPersonHandler().getPersons().size()+1;
	}


	public Person getElementAt(int index) {
		if(index == 0)
			return null;
		return ubik.getBuilding().getFloor(0).getPersonHandler().getPersons().get(index-1);
	}


	public void addListDataListener(ListDataListener l) {
	}


	public void removeListDataListener(ListDataListener l) {
	}


	public void setSelectedItem(Object anItem) {
		selected = (Person) anItem;
	}


	public Object getSelectedItem() {
		return selected;
	}

}
