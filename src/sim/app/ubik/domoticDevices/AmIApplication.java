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

import sim.app.ubik.building.connectionSpace.Door;
import sim.engine.SimState;
import sim.util.Int2D;


public abstract class AmIApplication {

    public abstract Door getSuggestedDoor(SimState state, DomoticDevice domotic);

    public abstract Int2D getSuggestedDirection(SimState state, DomoticDevice domotic);

    /**
     * Este método es consultado por dispositivos domóticos que una vez activos pueden mostrarse inactivos.
     * Devuelve true para no oculta que esta encendido desde cuando se activa.
     * @return
     */
    public boolean activatedDomoticDeviceSinceFirstMoment() {
        return true;
    }

    /**
     * Calcula el vector director que lleva al fuego desde un dispositivo domotico
     * pasado como parámetro en base a la suma de vectores directores de sensores activados.
     * Null si no hay sensores activados.
     * @param state
     * @param domotic
     * @return
     */
    public Int2D fireDirection(SimState state, DomoticDevice domotic) {
        /*
        Ubik ubik = (Ubik) state;
        Bag as = ubik.getBuilding().getFloor(domotic.floor).getActivatedSensors();
        Int2D directionsToActivatedSensors[] = new Int2D[as.numObjs];
        if (as.numObjs == 0) {
            return null;
        }
        for (int i = 0; i < as.numObjs; i++) {
            directionsToActivatedSensors[i] = MovementTools.getInstance().generateDirectionToLocation(domotic.position, ((Sensor) as.get(i)).position);
        }
        MutableInt2D r = MovementTools.getInstance().sumOfDirections(directionsToActivatedSensors);
        Int2D lastFireDirection = new Int2D(r.x, r.y);
        return lastFireDirection;
         */
        return new Int2D(0,0);
    }
}
