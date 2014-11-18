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

import java.util.ArrayList;
import java.util.List;
import ocp.service.ContextEntityItems;
import ocp.service.ContextItemString;
import ocp.service.ContextService;
import sim.app.ubik.Ubik;
import sim.app.ubik.people.Person;
import sim.engine.SimState;
import sim.util.MutableInt2D;
import ubik3d.model.HomePieceOfFurniture;


public class PresenceSensor extends FixedDomoticDevice {

    private int readPeriod = 5;    // Steps
    private int cont = 0;
    private boolean activity;
    private List<MutableInt2D> positions;

    public PresenceSensor(int floor, HomePieceOfFurniture device3DModel, Ubik ubik) {
        super(floor, device3DModel, ubik);
        //Room where is the PresenceSensor
        ocp = ubik.createOCPProxyProducer("UBIK.PrsenceSensor-" + getId());
        lastActivity = activity = false;
        positions = new ArrayList();
    }

    private boolean lastActivity;
    /**
     * Comprueba si hay alguna persona dentro de la misma habitación donde se encuentra el sensor
     * @param state
     */    
    @Override
	public void step(SimState state) {
        cont++;
        if (cont % readPeriod == 0) {
            activity = thereAnyActivity();
            
            if (ocp != null) {
                //ocp.getContextService().setContextItem(getClass().getSimpleName(), getId(), "activity", activity);
                if (activity) {
                    //System.out.println(getClass().getSimpleName()+"-"+getId()+" state = On");
                    ocp.getContextService().setContextItem(getClass().getSimpleName(), getId(), "state", "On");
                } else {
                    //System.out.println(getClass().getSimpleName()+"-"+getId()+" state = Off");
                    ocp.getContextService().setContextItem(getClass().getSimpleName(), getId(), "state", "Off");
                }
            }
            
            	
            
            cont = 1;
        }        
        if(lastActivity != activity) {
        	lastActivity = activity;
        	//System.out.println(getName()+"+"+getId()+" state "+(activity ? "On" : "Off"));
        }
    }

    private boolean thereAnyActivity() {
        List<MutableInt2D> newPos = new ArrayList();
        boolean result = false;
        for (Person p : ubik.getBuilding().getFloor(floor).getPersonHandler().getPersons()) {
            if (spaceArea.contains(p.getPosition().getX(), p.getPosition().getY())) {
                   if (p.isMoving()){   //Comprobamos si la persona se esta moviendo
                       return true;
                   }
                   else {               //Si esta quieta, anotamos su posicion para comprobar si se ha desplazado                	   
                        newPos.add(new MutableInt2D(p.getPosition().getX(),p.getPosition().getY()));
                   }
            }
        }
        if (newPos.size() != positions.size()) {
            result = true;
        } else if (newPos.size() > 0) {
            for (MutableInt2D mi1 : newPos) {
                if (!positions.contains(mi1)) {
                    result = true;
                    break;
                }
            }
        }
        positions.clear();
        positions.addAll(newPos);
        return result;
    }

    /**
     * In Mason, when an event begins to repeat on schedule, it returns a Stoppable.
     * Stop calling that agent ceases to have the turn to run.
     * @param stoppable
     */
    @Override
	public void stop() {
        setActivated(false);
    }

    @Override
    public double orientation2D() {
        return 0;
    }

    /**
     *
     * @return
     */
    public String getIdSensorProducer() {
        return name;
    }

    public String getIdLocationProducer() {
        return name;
    }

    @Override
    public void createInOCP(ContextService cs) {
    	super.createInOCP(cs);
    	
        ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), getId());
        // cei.addContextItem(new ContextItemBoolean("activity", activity));
        //cei.addContextItem(new ContextItemRelation("bluetoothDetectedBy", "", ""));
        cei.addContextItem(new ContextItemString("state", "Off"));
        cs.setContextItems(cei);
    }
}
