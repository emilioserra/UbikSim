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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import sim.app.ubik.MovementTools;
import sim.app.ubik.Ubik;
import sim.app.ubik.ocp.OCPProxyProducer;
import sim.app.ubik.utils.ElementsHandler;
import sim.util.Int2D;
import ubik3d.model.Home;
import ubik3d.model.HomePieceOfFurniture;


public class DeviceHandler extends ElementsHandler {
	private String file = "handlers/devices.txt";
	protected List<DomoticDevice> domoticDevices;
	protected OCPProxyProducer ocp;
        private static final Logger LOG = Logger.getLogger(DeviceHandler.class.getName());

	public DeviceHandler(Ubik ubik, int floor, int GRID_WIDTH, int GRID_HEIGHT) {
		super(ubik, floor, GRID_WIDTH, GRID_HEIGHT);

		domoticDevices = new ArrayList<DomoticDevice>();

		this.ocp = ubik.createOCPProxyProducer("UBIK.DeviceHandler");
		initRols(file);
	}

	public void add(DomoticDevice p) {
		domoticDevices.add(p);
	}

	public List<DomoticDevice> getDevicesInstanceOf(Class clase) {
		List<DomoticDevice> result = new ArrayList();
		for (DomoticDevice p : domoticDevices) {
			if (clase.isInstance(p)) {
				result.add(p);
			}
		}
		return result;
	}


	public DomoticDevice getDeviceByName(String name) {		
		for (DomoticDevice p : domoticDevices) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}


	public DomoticDevice getNearestDevice(Int2D pos, Class clase) {
		double minDistance = Double.MAX_VALUE;
		DomoticDevice result = null;
		for (DomoticDevice p : domoticDevices) {
			if (clase.isInstance(p)) {
				if (result == null) {
					result = p;
					minDistance = MovementTools.getInstance().getDistance(
							pos.getX(), pos.getY(),
							result.getPosition().getX(),
							result.getPosition().getY());
				} else {
					double d = MovementTools.getInstance().getDistance(
							p.getPosition().getX(), p.getPosition().getY(),
							pos.getX(), pos.getY());
					if (d < minDistance) {
						minDistance = d;
						result = p;
					}
				}
			}
		}
		return result;
	}

	public void clear() {
		domoticDevices.clear();
		grid.clear();
	}

	public void generateDevices(int floor, int cellSize) {
		LOG.info("Generating devices: ");
		Home home = ubik.getBuilding().getFloor(floor).getHome();
		for (HomePieceOfFurniture hpof : home.getFurniture()) {
			if (rols.contains(hpof.getRol())) {
				LOG.config("Generating devices: " + hpof.getRol()
						+ ", " + hpof.getName());
				if (hpof.getAmount() <= 1) {
					DomoticDevice p = createDomoticDevice(floor, hpof, ubik);
					if (p != null) {
						domoticDevices.add(p);
					}
				} else {
					home.deletePieceOfFurniture(hpof);

					for (int i = 0; i < hpof.getAmount(); i++) {
						HomePieceOfFurniture piece = new HomePieceOfFurniture(
								hpof);
						DomoticDevice p = createDomoticDevice(floor, piece,
								ubik);
						if (p != null) {
							home.addPieceOfFurniture(piece);
							domoticDevices.add(p);
						}
					}
				}
			}
		}
	}

	protected DomoticDevice createDomoticDevice(int floor,
			HomePieceOfFurniture piece, Ubik ubik) {
		System.out.println("sim.app.ubik.domoticDevices." + piece.getRol());
		DomoticDevice result = null;
		try {
			for (Constructor<?> c : Class.forName("sim.app.ubik.domoticDevices." + piece.getRol()).getConstructors()) {
				System.out.println(c);
				try {
					result = (DomoticDevice) c.newInstance(floor, piece, ubik);
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
			    break;
			}
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		if (result != null) {
			result.fixStopable(ubik.schedule);
			System.out.println("Dispositivo creado!!!!!!!!!!!");
			if (ocp != null) {
				result.createInOCP(ocp.getContextService());
			}
		}

		return result;
	}

	/*
	 * protected void initUbikDevicesRols() { ubikDevicesRols = new
	 * ArrayList<String>(); ubikDevicesRols.add("Actuator");
	 * ubikDevicesRols.add("PC"); ubikDevicesRols.add("SlaveBluetooth");
	 * ubikDevicesRols.add("Mobile"); ubikDevicesRols.add("RFIDAntenna");
	 * ubikDevicesRols.add("RFIDTag"); ubikDevicesRols.add("PresenceSensor");
	 * ubikDevicesRols.add("AirCond"); ubikDevicesRols.add("Thermometer");
	 * ubikDevicesRols.add("RQTag"); ubikDevicesRols.add("DoorSensor");
	 * ubikDevicesRols.add("PressureSensor");
	 * 
	 * }
	 */

	public List<DomoticDevice> getDomoticDevice() {
		return domoticDevices;
	}
	
	
	public DomoticDevice getDomoticDeviceByName(String name) {
		for(DomoticDevice dd: domoticDevices)
			if(dd.getName().equals(name)) {
				return dd;
			}
		return null;
	}

}
