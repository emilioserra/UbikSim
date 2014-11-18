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
package sim.app.ubik.domoticDevices;

import java.util.Iterator;
import java.util.LinkedList;
import ocp.service.ContextEntityItems;
import ocp.service.ContextItemString;
import ocp.service.ContextService;
import sim.app.ubik.Ubik;
import sim.app.ubik.building.OfficeFloor;
import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;
import ubik3d.model.HomePieceOfFurniture;


public class RFIDAntenna extends FixedDomoticDevice {

    private OfficeFloor of;
    private LinkedList<Int2D> posiciones = new LinkedList<Int2D>();
    protected static int activedAntenasCounter = 0;
    private int perceptionOfFireByAtenas = 6;
    private String sLastPerson = "";

    public RFIDAntenna(int floor, HomePieceOfFurniture w, Ubik ubik) {
        super(floor, w, ubik);
        this.activated = true;
        posiciones = this.getPosiciones(position.x, position.y);
    }

    @Override
    public void step(SimState state) {
        for (int i = 0; i < posiciones.size(); i++) {

            int x = posiciones.get(i).x;
            int y = posiciones.get(i).y;

            SparseGrid2D grid = ubik.getBuilding().getFloor(floor).getDeviceHandler().getGrid();
            System.out.println("Soy una antena situada en " + position.getX() + "," + position.getY());
            for (Int2D p : posiciones) {
                for (Object o : grid.getObjectsAtLocation(p.x, p.y)) {
                    if (o instanceof RFIDTag) {
                        RFIDTag sb = (RFIDTag) o;
                        System.out.println("Dispositivo " + sb.getName() + " detectado en " + getSpaceArea().getName());
                    }
                }
            }
        }
    }

    /**
     * En Mason, cuando un evento se pone a repetir en schedule, se devuelve un Stoppable.
     * Llamando a stop ese agente deja de tener el turno para ejecutarse.
     * @param stoppable
     */
    @Override
	public void stop() {
    }

    public LinkedList<Int2D> getPosiciones(int x, int y) {

        LinkedList<Int2D> directions = new LinkedList<Int2D>();
        directions = getDirections(directions, perceptionOfFireByAtenas);

        Iterator it = directions.iterator();
        while (it.hasNext()) {
            Int2D posicion = (Int2D) it.next();
            posiciones.add(new Int2D(posicion.x + x, posicion.y + y));
        }
        return posiciones;
    }

    public LinkedList getDirections(LinkedList direcciones, int percepcion) {
        for (int i = 0; i <= percepcion; i++) {
            for (int j = 0; j <= percepcion; j++) {
                if (!(i == 0 && j == 0)) {
                    direcciones.add(new Int2D(i, j));
                }
                //[0,0] - [0,1] - [1,0] - [1,1]
                if (j != 0) {
                    direcciones.add(new Int2D(i, -j));
                }
                if (i != 0) {
                    direcciones.add(new Int2D(-i, j));
                }
                if ((i != 0) && (j != 0)) {
                    direcciones.add(new Int2D(-i, -j));
                }
                //[0,-1], [1, -1], [-1,0], [-1,1], [-1,-1]
            }
        }
        //Mi propia posicion también la añado
        direcciones.add(new Int2D(0, 0));

        /*Iterator it = direcciones.iterator();
        while (it.hasNext()){
        Int2D k = (Int2D)it.next();
        System.out.println("[" + k.x + "," + k.y + "]");
        }*/
        return direcciones;
    }

    @Override
    public void createInOCP(ContextService cs) {
    	super.createInOCP(cs);
        ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), String.valueOf(id));
        cei.addContextItem(new ContextItemString("state", "Activo"));
        cs.setContextItems(cei);
    }
}
