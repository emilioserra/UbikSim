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

package sim.app.ubik.building.temperature;

import sim.app.ubik.Ubik;
import sim.app.ubik.building.SpaceAreaHandler;
import sim.field.grid.SparseGrid2D;


public class TemperatureHandler {
    private Ubik ubik;
    private int floor;
    protected int cellSize;
    protected SparseGrid2D grid;

    protected int ambientTemperature;

    /**
     *
     * @param ubik
     * @param floor
     * @param GRID_WIDTH
     * @param GRID_HEIGHT
     * @param at
     */
    public TemperatureHandler(Ubik ubik, int floor, int GRID_WIDTH, int GRID_HEIGHT, int at) {
        this.ubik = ubik;
        this.floor = floor;
        this.grid = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
        this.cellSize = ubik.getCellSize();

        this.ambientTemperature = at;
    }

    public void clear() {
        grid.clear();
    }

    public void generateTemperatureAgents(int cellSize) {
        SpaceAreaHandler sah = ubik.getBuilding().getFloor(floor).getSpaceAreaHandler();
        for(int i = 0; i < grid.getWidth(); i++) {
            for(int j = 0; j < grid.getHeight(); j++) {
                if(!sah.isWall(i, j)) {

                }
            }
        }
    }
}
