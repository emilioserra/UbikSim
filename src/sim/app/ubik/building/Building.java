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

import sim.app.ubik.building.connectionSpace.Stairs;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.app.ubik.Ubik;

import sim.util.Int2D;

import ubik3d.model.Home;
import java.util.List;
import sim.app.ubik.graph.BuildingToGraph;



public class Building {
    //sugerencias de colores para los espacios especiales y obstaculos
    /**
     * La documentación de la clase ValueGridPortrayal2D enseña a usar el cuarto parametro del constructor de color para indicar transparencia. Cuando no se quiere poner nada se usa transparencia 
     * total (0) lo que hace que sea mucho más eficiente.
     */

    protected OfficeFloor floors[];
    
    protected List<Stairs> stairs;
    protected int nstairs;

    protected Ubik  ubik;

    BuildingToGraph btg;
    private static final Logger LOG = Logger.getLogger(Building.class.getName());

    public Building(List<Home> homes, Ubik simulation, int cellSize){
    	LOG.config("Building("+homes+","+simulation+","+cellSize+")");
        this.ubik=simulation;
        this.floors= new OfficeFloor[homes.size()];

        for(int i = 0; i<floors.length; i++){
            floors[i]= new OfficeFloor(homes.get(i), i, simulation);
        }
        
        for(int i=0;i<numberOfFloors();i++){
            buildStairs(floors[i].getHome());
       }
    }

    public void createEntities() {
    	for(int i = 0; i<floors.length; i++){
            floors[i].createEntities();
        }
    	
    	btg = new BuildingToGraph(ubik);
    }
    
    public void clearHomes(){       
     for(int i=0;i<numberOfFloors();i++){
           try {
                floors[i].clear();
            } catch (InterruptedException ex) {
                Logger.getLogger(Building.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //floors[0].printHome();
    }

    public OfficeFloor getFloor(int floor){
        if(floor>= floors.length || floor<0) return null;
        return floors[floor];
    }

    private void buildStairs(Home home) {
        /*
        nstairs=0;
        stairs=new ArrayList();
        List l=home.getFurniture();
        ListIterator i=l.listIterator();
        while(i.hasNext()){
            HomePieceOfFurniture h=(HomePieceOfFurniture)i.next();
            if (h.getName().equals("Staircase")){
                stairs.add(new Stairs((int)h.getX(),(int)h.getY(),nstairs++));
                Cell c=(Cell)this.floors[0].cellsGrid.getObjectsAtLocation((int)h.getX()/this.floors[0].cellSize, (int)h.getY()/this.floors[0].cellSize).get(0);
                c.fixStairs();
            }
        }*/
    }
    
     public int numberOfStairs() {       
        return this.nstairs;
    }

    public int numberOfFloors() {
        return this.floors.length;
    }
     
     /**
      * Devuelve la escalera
      * @param index
      * @return
      */
     public Stairs getStairs(int index){
         return stairs.get(index);
     }
          
     /**
      * Devuelve el ancho x largo de la escalera
      */
      
     public Int2D getStairsSize(){
         Int2D size = new Int2D(   Stairs.getWidth() , Stairs.getHeight());
         return size;
     }
    
     public OfficeFloor [] getFloors(){
         return this.floors;
     }

     public BuildingToGraph getBuildingToGraph() {
         return btg;
     }
}
