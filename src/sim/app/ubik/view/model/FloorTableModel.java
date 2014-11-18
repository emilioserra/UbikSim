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
package sim.app.ubik.view.model;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import sim.app.ubik.building.OfficeFloor;

public class FloorTableModel extends AbstractTableModel {

	public List<OfficeFloor> floors;
	
	public FloorTableModel(List<OfficeFloor> floors) {
		this.floors = floors;
	}

	@Override
	public String getColumnName(int col) {
		switch(col) {
		case 0:
			return "name";
		case 1:
			return "number";
		}
        return "";
    }
	
	@Override
	public int getColumnCount() {		
		return 1;
	}

	@Override
	public int getRowCount() {
		return floors.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {		
		switch(columnIndex) {
		case 0:
			return floors.get(rowIndex).getHome().getName();
		case 1:
			return rowIndex;
		}
        return "";		
	}
}
