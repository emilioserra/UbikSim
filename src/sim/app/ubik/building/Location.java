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
package sim.app.ubik.building;

/**
 * DEPRECATED
 */
public class Location {
    protected int x;
    protected int y;
    protected int f;
    
    
    
    /**TODO: habrá que considerar escalera, por ejemplo que getFloor de -1 y tengas ya que ver
     * o un getFloor que de planta escalera o ascensor*/
 
        public Location(int x, int y, int f){
        this.x=x;
        this.y=y;
        this.f=f;
    }
   
    
    public void setLocation(int x, int y, int f){
         this.x=x;
        this.y=y;
        this.f=f;
    }
    
    public int getxX(){
        return x;
    }
    public int getY(){
        return y;        
    }
    public int getFloor(){
        return f;
    }
}
