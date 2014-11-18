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

import ocp.service.ContextService;
import sim.app.ubik.MovementTools;
import sim.app.ubik.Ubik;
import sim.app.ubik.building.connectionSpace.Door;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.portrayal.Oriented2D;
import sim.util.Int2D;
import ubik3d.model.HomePieceOfFurniture;

/**
 * El actuador puede estar activo o inactivo. Dentro de que este activo, se pueden dar tres estados: muestra que hay emergencia pero no da indicaciones, muestra emergencia pero no da idicaciones 
 * o finge que esta inactivo para que los agentes no se alarmen.
 */
public class Actuator extends DomoticDevice implements Steppable, Stoppable, Oriented2D {

    public Actuator(int floor, HomePieceOfFurniture device3dModel, Ubik ubik) {
		super(floor, device3dModel, ubik);
		// TODO Auto-generated constructor stub
	}
    
	private Stoppable stoppable;
    protected static int totalInstancesOfActuators = 0;
    protected static int activedActuatorsCounter = 0;
    /*Los actuadores estan todos activos o todos desactivos*/
    protected static boolean activedActuators = false;
    protected static boolean showSuggestedDoorsInLabel = false;
    protected static Ubik ubik;
    /**
     * Le permite a un actuador concreto fingir que esta inactivo.
     */
    protected boolean showActivated = true;

    public void setShowActivated(boolean b) {
        this.showActivated = b;
    }

    public boolean getShowActivated() {
        return showActivated;
    }

    public static void setShowSuggestedDoorsInLabel(boolean b) {
        showSuggestedDoorsInLabel = b;
    }

    public static boolean getShowSuggestedDoorsInLabel() {
        return showSuggestedDoorsInLabel;
    }
    /**Dirección mostrada, aunque los agentes leen puertas directamente. Si la dirección es 0,0 (dirección nula)
    entonces se ha dado la alarma pero no el panel no da sugerencias de que camino seguir.*/
    public Int2D showedDirection = new Int2D(0, 0);
    public Int2D fireDirection = new Int2D(0, 0);
    public Door suggestedDoor;
    protected AmIApplication AmI;

    /**Se fijan valores estaticos, la simulación (para recuperar el edificio y plantas)
    public Actuator(int floor, Int2D position, AmIApplication AmI) {
        //super(floor, position);
        this.AmI = AmI;
        this.setShowActivated(AmI.activatedDomoticDeviceSinceFirstMoment());
    }*/

    @Override
	public void createInOCP(ContextService cs) {
    	super.createInOCP(cs);
    }
    public static void fixStaticValues(Ubik simulation) {
        Actuator.ubik = simulation;
    }

    /**
     * Devuelve los contadores activos (totales - quemados) siempre y cuando esten activados todos los actuadores.
     * @return
     */
    public static int getActivedActuators() {
        return activedActuatorsCounter;
    }

    public static boolean isActivedTheActuators() {
        return activedActuators;
    }

    public Int2D getShowedDirection() {
        return this.showedDirection;
    }

    public Door getSuggestedDoor() {
        return suggestedDoor;

    }

    public Int2D getFireDirection() {
        return null;//AmI.fireDirection(ubik, this);
    }

    /**
     * El número total de actuadores se conoce (nº de actuadores x numero de plantas), una vez se activan se generan todos estos agentes. Según se queman
    se van desactivando.

     */
    public static void setActivedTheActuators(boolean actived) {
        /*
        activedActuators = actived;
        if (actived) {
            Actuator.totalInstancesOfActuators = ubik.getFloors() * OfficeFloor.numDomoticDevices;
            Actuator.activedActuatorsCounter = totalInstancesOfActuators;
        }
        //al desactivar actuadores en edificio se reduce el contador, no hay que ponerlo a 0
        /*Se delega en el edificio para planificar todos los sensores o pararlos, la parad 
        disminuye los contadores activos

        ubik.building.initActuators(actived);
        */
    }

    public static void clearStaticValues() {
        totalInstancesOfActuators = 0;
        activedActuatorsCounter = 0;
        activedActuators = false;

    }

    public void fixStoppable(Stoppable stoppable) {
        this.stoppable = stoppable;
    }

    /**
     * En Mason, cuando un evento se pone a repetir en schedule, se devuelve un Stoppable. Llamando a stop ese agente deja de tener el turno para ejecutarse.
     *
     * @param stoppable
     */
    @Override
	public void stop() {
        //System.out.println("fire ends its mission, position ("  +  position.x + "," + position.y + ")" );
        if (stoppable != null) {
            activedActuatorsCounter--;
            stoppable.stop();
        }

    }

    @Override
	public void step(SimState state) {
     
        /*
        //delega en la aplicacion AmI
        considerStopMe(state);
        fireDirection = AmI.fireDirection(state, this);
        if (fireDirection != null) {
            suggestedDoor = AmI.getSuggestedDoor(state, this);
            showedDirection = AmI.getSuggestedDirection(state, this);
        }
*/
        // testSimpleFleeingApplication(state);

    }

    /**
     * El actuador puede pararse totalmente. Cuando no hay trabajadores se para pues ya no hay utilidad.
     * @param state
     */
    private void considerStopMe(SimState state) {
        /*
        Ubik ubik = (Ubik) state;
        if (ubik.getPersonHandler().getPersons().size() == 0) {
            this.stop();
        }*/
    }

    @Override
	public double orientation2D() {
        if (showedDirection == null) {
            return 0;
        }
        return MovementTools.getInstance().directionToRadians(this.showedDirection);
    }
}
