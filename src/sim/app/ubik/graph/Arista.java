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


public class Arista{
        int unionX;
        int unionY;
        int id;
        String nameUnion;
        String conexion1;
        String conexion2;
        int distancia;

        Arista(int id, String conexion1, String conexion2, int unionX, int unionY, String nameUnion, int distancia){
            this.id = id;
            this.conexion1 = conexion1;
            this.conexion2 = conexion2;
            this.unionX = unionX;
            this.unionY = unionY;
            this.nameUnion = nameUnion;
            this.distancia=distancia;
        }
    }
