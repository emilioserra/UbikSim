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
import ubik3d.model.HomePieceOfFurniture;
import java.util.ArrayList;
import ocp.service.ContextEntityItems;
import ocp.service.ContextItemRelation;
import ocp.service.ContextItemString;
import sim.app.ubik.Ubik;
import sim.app.ubik.building.SpaceArea;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.portrayal.Oriented2D;


public class PresenceActuator extends FixedDomoticDevice implements Steppable, Stoppable, Oriented2D{

    private static final String iconOn = "/com/eteks/sweethome3d/io/resources/pictures/Hospital/sphere.png";
    private static final String iconOff = "/com/eteks/sweethome3d/io/resources/pictures/Hospital/souris.png";
    private static final String modelLightOff = "/com/eteks/sweethome3d/io/resources/pictures/Hospital/souris.obj";
    private static final String modelLigthOn = "/com/eteks/sweethome3d/io/resources/pictures/Hospital/sphere.obj";

    protected static boolean activedPresenceActuators = false;
    private ArrayList<HomePieceOfFurniture> listLigths;


    //State of the current light in any room
    private static boolean actualStatusLight = false;

    //Id current sensor has detected the person
    private static String actualIdPresenceSensor = "";


    public PresenceActuator(int floor, HomePieceOfFurniture w, Ubik ubik) {
        super(floor, w, ubik);
    }

    /**
     * Id arrives presence sensor that has changed and what will be the state
       of the light to change the actuator
     * @param idPresenceSensor
     * @param statusLight
     */
    public static void changeIconLight(String idPresenceSensor, boolean statusLight){
        /*
        setActualIdPresenceSensor(idPresenceSensor);
        setActualStatusLight(statusLight);
        
        //Activate the actuators
        setActivedThePresenceActuators(true);*/
    }

    /**
     *
     * @return
     */
    public boolean getActualStatusLight(){
        return actualStatusLight;
    }


    /**
     *
     * @return
     */
    public String getActualIdPresenceSensor(){
        return actualIdPresenceSensor;
    }


    /**
     *
     * @param statusLight
     */
    public static void setActualStatusLight(boolean statusLight){
        actualStatusLight = statusLight;
    }


    /**
     *
     * @param idPresenceSensor
     */
    public static void setActualIdPresenceSensor(String idPresenceSensor){
        actualIdPresenceSensor = idPresenceSensor;
    }


    /**
     *
     * @param state
     */
    @Override
	public void step(SimState state) {
        /*
        String idActual = getActualIdPresenceSensor();
        ListIterator it = listLigths.listIterator();
        while(it.hasNext()){
            HomePieceOfFurniture h = (HomePieceOfFurniture)it.next(); 
            String s = h.getOcpId();
            String lightRoom = h.getOcpId().substring(5, h.getOcpId().length());
            if(lightRoom.equals(idActual.substring(2, idActual.length()))){
                if(getActualStatusLight() == false){
                    if(ubik.get() == 2){
                        h.setName("BombillaOff");
                        h.setIcon(iconOff);
                        break;
                    }
                    else if(of.getVisorMode() == 3){
                        h.setName("BombillaOff");
                        h.setModel(modelLightOff);
                        break;
                    }
                    }
                else{
                    if(of.getVisorMode() == 2){
                        h.setName("BombillaOn");
                        h.setIcon(iconOn);
                        break;
                    }
                    else if(of.getVisorMode() == 3){
                        h.setName("BombillaOn");
                        h.setModel(modelLigthOn);
                        break;
                    }
                 }
            }
        setActivedThePresenceActuators(false);
        }
         *
         */
   }
        

     /**
        In Mason, when an event begins to repeat on schedule, it returns a Stoppable.
        stop calling that agent ceases to have the turn to run
     * @param stoppable
     */

    @Override
	public void stop() {

    }

    @Override
    public void createInOCP(ContextService cs) {
        ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), String.valueOf(id));
        SpaceArea sa = ubik.getBuilding().getFloor(floor).getSpaceAreaHandler().getSpaceArea(position.x, position.y);
        if(sa != null)
            cei.addContextItem(new ContextItemRelation("locatedIn", sa.getClass().getName(), String.valueOf(sa.getId())));
        //cei.addContextItem(new ContextItemRelation("bluetoothDetectedBy", "", ""));
        cei.addContextItem(new ContextItemString("state", "Activo"));
        cs.setContextItems(cei);
    }
}
