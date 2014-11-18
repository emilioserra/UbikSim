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
package sim.app.ubik.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sim.app.ubik.Ubik;
import sim.app.ubik.ocp.OCPProxyProducer;
import sim.field.grid.SparseGrid2D;

public class ElementsHandler {
	protected Ubik ubik;
	protected int floor;
	protected SparseGrid2D grid;// SparseGrid2D can efficiently (O(1)) tell you
								// the location of an object (ObjectGrid2D no)
	protected OCPProxyProducer ocp;
	protected List<String> rols;

	public ElementsHandler(Ubik ubik2, int floor2, int GRID_WIDTH,
			int GRID_HEIGHT) {
		this.ubik = ubik2;
		this.floor = floor2;
		this.grid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
	}

	protected void initRols(String rolsFile) {
		try {
			BufferedReader bf = new BufferedReader(new FileReader(rolsFile));
			String s;
			rols = new ArrayList<String>();
			while ((s = bf.readLine()) != null) {
				rols.add(s);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
	}

	public SparseGrid2D getGrid() {
		return grid;
	}

	public boolean canHandler(Object o) {
		String className = o.getClass().getSimpleName();
		return rols.contains(className);
	}

	public Ubik getUbik() {
		return ubik;
	}

	public int getFloor() {
		return floor;
	}

	public OCPProxyProducer getOCP() {
		return ocp;
	}

	public List<String> getRols() {
		return rols;
	}
}
