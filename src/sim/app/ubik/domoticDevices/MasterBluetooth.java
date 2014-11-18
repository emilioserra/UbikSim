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
import ocp.service.ContextItemDatetime;
import ocp.service.ContextItemFloat;
import ocp.service.ContextItemRelation;
import ocp.service.ContextItemString;
import ocp.service.ContextProducer;
import ocp.service.ContextService;
import sim.app.ubik.building.SpaceAreaHandler;
import sim.app.ubik.ocp.OCPProxyProducer;
import sim.engine.SimState;
import sim.field.grid.SparseGrid2D;
import sim.util.Int2D;


public class MasterBluetooth extends ServicePC implements ContextProducer {

    private static int idCreator = 0;
    private List<Int2D> posiciones;
    protected static int activedAntenasCounter = 0;
    //TODO cambiar el atributo perception!!
    private int perceptionOfBluetooth = 300; // 700cm = 7m
    private String sLastPerson = "";
    protected OCPProxyProducer ocp;
    protected int id;
    SparseGrid2D gridOfDevices;
    DeviceHandler deviceHandler;
    private String name = null;
    private static final String signalArea = "/ubik3d/io/resources/pictures/Ubik/cylinder.obj";
    //private HomePieceOfFurniture signalAreaPiece;

    public MasterBluetooth(FixedDomoticDevice runOnDevice) {
        super(runOnDevice);
        this.id = idCreator++;

        this.name = runOnDevice.getName();

        this.deviceHandler = runOnDevice.getUbik().getBuilding().getFloor(runOnDevice.getFloor()).getDeviceHandler();
        this.gridOfDevices = deviceHandler.getGrid();

        perceptionOfBluetooth = Math.round(perceptionOfBluetooth / runOnDevice.getUbik().getCellSize());
        //posiciones = getPositionsRangeBluetooth(runOnDevice.getPosition().x, runOnDevice.getPosition().y);
        this.ocp = runOnDevice.getUbik().createOCPProxyProducer("MasterBluetooth" + "-" + getId());

        
        /*signalAreaPiece = new HomePieceOfFurniture(
                "",
                signalArea,
                perceptionOfBluetooth * runOnDevice.getUbik().getCellSize() * 2,
                perceptionOfBluetooth * runOnDevice.getUbik().getCellSize() * 2,
                10,
                runOnDevice.getPosition().x * runOnDevice.getUbik().getCellSize(),
                runOnDevice.getPosition().y * runOnDevice.getUbik().getCellSize());
        signalAreaPiece.setColor(Color.RED.getRGB());
        runOnDevice.getUbik().getBuilding().getFloor(runOnDevice.getFloor()).getHome().addPieceOfFurniture(signalAreaPiece);*/

    }

    // Este es el método que comprueba la existencia de un usuario dentro del radio de acción de la antena
    @Override
	public void step(SimState state) {
        //boolean encontrado = false;
        //LinkedList<String> auxPerson = new LinkedList<String>();
        for (Object m : deviceHandler.getDevicesInstanceOf(Mobile.class)) {
            Mobile mobile = (Mobile) m;
            double distance = mobile.getPosition().distance(runOnDevice.getPosition());
            if (distance < perceptionOfBluetooth) {
//                System.out.println("\tDispositivo " + mobile.getId() + " de " + mobile.getOwner().getName() + " detectado a " + ((int) distance) + " celdas desde " + runOnDevice.getName());
                if (ocp != null) {
                    ContextEntityItems contextItems = new ContextEntityItems("MasterBluetoothEvents", "" + 1);
//                System.out.println("Identificador MasterBluetoothEvents en Antena Bluetooth----->"+1);

                    contextItems.addContextItem(new ContextItemFloat("distanceDetection", (float) distance));
                    contextItems.addContextItem(new ContextItemString("antenaName", runOnDevice.getName()));
                    contextItems.addContextItem(new ContextItemRelation("antennaBluetooth", "MasterBluetooth", "" + this.id));
                    contextItems.addContextItem(new ContextItemRelation("mobileDevice", "Mobile", "" + mobile.getId()));
//                contextItems.addContextItem(new ContextItemRelation("mobileDevice", "Mobile", "" +  mobile.getOwner().getName()));
                    contextItems.addContextItem(new ContextItemDatetime("timestamp", runOnDevice.getUbik().getClock().getDate()));

                    ocp.getContextService().setContextItems(contextItems);
//                    ContextEntityItems cei = new ContextEntityItems(mobile.getClass().getSimpleName(), String.valueOf(mobile.getId()));
//                    cei.addContextItem(new ContextItemRelation("bluetoothDetectedBy", getClass().getSimpleName(), ""+id));
//                    //cei.addContextItem(new ContextItemRelation("bluetoothDetectedBy", "", ""));
//                    cei.addContextItem(new ContextItemInt("distance", (int) Math.round(distance)));
//                    ocp.getContextService().setContextItems(cei);


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

    private List<Int2D> getPositionsRangeBluetooth(int x, int y) {
        SpaceAreaHandler sah = runOnDevice.getUbik().getBuilding().getFloor(runOnDevice.getFloor()).getSpaceAreaHandler();

        Int2D ori = new Int2D(x, y);
        List<Int2D> direcciones = new ArrayList<Int2D>();
//        System.out.println("Posicion Antena:" + x + "-" + y + " percepcion--->" + perceptionOfBluetooth);
        for (int i = x - perceptionOfBluetooth; i <= x + perceptionOfBluetooth; i++) {
            for (int j = y - perceptionOfBluetooth; j <= y + perceptionOfBluetooth; j++) {
                Int2D act = new Int2D(i, j);
                if (i >= 0 && i < gridOfDevices.getWidth() && j >= 0 && j < gridOfDevices.getHeight()
                        && ori.distance(act) < perceptionOfBluetooth && !sah.isObstacle(i, j,0)) {
                    direcciones.add(new Int2D(i, j));
                }
            }
        }
        return direcciones;
    }

    @Override
	public void createInOCP(ContextService cs) {
        ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), String.valueOf(id));
        cei.addContextItem(new ContextItemRelation("bluetoothSetTo", runOnDevice.getClass().getSimpleName(), "" + runOnDevice.getId()));
        //cei.addContextItem(new ContextItemRelation("bluetoothDetectedBy", "", ""));
        cei.addContextItem(new ContextItemString("state", "Active"));
        cs.setContextItems(cei);
    }

    @Override
	public String getId() {
        return getClass().getName() + "-" + id;
    }

    @Override
	public void activate(ContextService cs) {
    	if(ocp != null)
    		ocp.activate(cs);
    }

    @Override
	public void deactivate() {
    	if(ocp != null)
    		ocp.deactivate();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
