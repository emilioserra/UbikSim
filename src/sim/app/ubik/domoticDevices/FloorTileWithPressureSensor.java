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

import java.awt.Color;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;

import ocp.service.ContextEntityItems;
import ocp.service.ContextItemFloat;
import ocp.service.ContextService;
import sim.app.ubik.Ubik;
import sim.app.ubik.people.Person;
import sim.engine.SimState;
import sim.util.Int2D;
import ubik3d.model.HomePieceOfFurniture;

/**

 * Clase que representa una baldosa con un sensor de presion
 * Comprueba las personas que pueden estar sobre ella y
 * en que propercion (pueden tener solo un pie).
 * 
 * Con el atributo iluminate a true las baldosas se iluminan cuando detectan peso.
 *
 */
public class FloorTileWithPressureSensor extends FixedDomoticDevice {
	
	private float weightDetected; 
	private int width;
	private int depth;
	
	private int radio;
	
	private boolean iluminate;
	
	public FloorTileWithPressureSensor(int floor,
			HomePieceOfFurniture device3dModel, Ubik ubik) {
		super(floor, device3dModel, ubik);
		this.width = Math.round(device3dModel.getWidth() / ubik.getCellSize());
		this.depth = Math.round(device3dModel.getDepth() / ubik.getCellSize());
		
		radio = Math.max(width, depth) + 20/cellSize;
		
		ocp = ubik.createOCPProxyProducer("UBIK.FloorTileWithPressureSensor-" + getId());
		
		Properties properties = new Properties();
		StringReader sr = new StringReader(device3dModel.getMetadata());		
		try {
			properties.load(sr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String value = properties.getProperty("indicator");
		if(value != null) {
			if(value.equals("false")) {				
				iluminate = false;
			} else {
				iluminate = true;
			}
		}
		
	}

	@Override
	public void step(SimState state) {
		float weight = 0;
		List<Person> people = ubik.getBuilding().getFloor(getFloor()).getPersonHandler().getPersons(getPosition().getX(), getPosition().getY(), radio);
		for(Person p: people) {
			for(Person.WeightItem item: p.getWeightModel()) {
				if(contains(item.getPosition())) {
					weight += (item.getPercent()*p.getWeight());
				}
			}
		}
		if(weight != weightDetected) {
			if(ocp != null) {
				ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), getId());
				cei.addContextItem(new ContextItemFloat("weightDetected", weight));
				ocp.getContextService().setContextItems(cei);
			}
		}
		
		weightDetected = weight;
		
		if(iluminate) {
			if(weightDetected > 0) {
				device3DModel.setColor(Color.GREEN.getRGB());
			} else {
				device3DModel.setColor(null);
			}
		}
	}
	
	private boolean contains(Int2D p) {
		int xMax = getPosition().getX() + width/2;
		int xMin = getPosition().getX() - width/2;
		int yMax = getPosition().getY() + depth/2;
		int yMin = getPosition().getY() - depth/2;
		
		if(p.getX() <= xMax && p.getY() <= yMax && p.getX() > xMin && p.getY() > yMin)
			return true;
		return false;
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void createInOCP(ContextService cs) {
		super.createInOCP(cs);
		ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), getId());
        
		cei.addContextItem(new ContextItemFloat("weightDetected", weightDetected));
		cei.addContextItem(new ContextItemFloat("width", device3DModel.getWidth()));
		cei.addContextItem(new ContextItemFloat("depth", device3DModel.getDepth()));

        cs.setContextItems(cei);
	}
}
