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

package sim.app.ubik.building.connectionSpace;

import sim.app.ubik.building.OfficeFloor;
import sim.util.MutableInt2D;


public class Window {
    protected OfficeFloor of;
    protected static int width;
    protected MutableInt2D initialPos;    
    protected boolean horizontal;  
    private int hmark; //marca de horizontal, 1 si es horizontal
    private int vmark; //marca de vertical, 1 si es vertical
    

 public Window(int widthWindow, boolean horizontal, MutableInt2D initialPos, int specialValueIn, OfficeFloor floor){
        this.of=floor;
        width = widthWindow;
        this.initialPos = initialPos;
        this.horizontal = horizontal;
        if(horizontal) hmark=1;
        else vmark=1;
        this.fixInitialPos();
        markCellsAsWindow();
     
 }
     /**
     * Ajusta la posición inicial de puerta restandole la mitad de la anchura de la ventana*/
    private void fixInitialPos(){
       this.initialPos = new MutableInt2D(initialPos.getX() - (hmark * Window.width/2), initialPos.getY() - (vmark * Window.width/2));
    }

    private void markCellsAsWindow() {
        /*
            for (int i = 0; i < this.width; i++) {
                //se usa la marca de horizontal o vertical para saber que sumar en cada paso
                Cell c = (Cell) of.cellsGrid.getObjectsAtLocation(initialPos.getX() + (i*hmark), initialPos.getY() + (i*vmark)).get(0);
                c.fixOpenedWindow();
            }
         *
         */
              
    }
}
