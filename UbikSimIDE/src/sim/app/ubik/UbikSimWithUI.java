/*
 * Contact, UbikSimIDE has been developed by:
 * 
 * Juan A. Botía , juanbot@um.es
 * Pablo Campillo, pablocampillo@um.es
 * Francisco Campuzano, fjcampuzano@um.es
 * Emilio Serrano, emilioserra@um.es 
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
package sim.app.ubik;

import sim.app.ubik.batch.Experiment;
import sim.app.ubik.displays.UbikSimDisplay2D;
import sim.app.ubik.displays.UbikSimDisplay3D;
import sim.display.*;
import sim.engine.SimState;
import sim.portrayal.Inspector;


public class UbikSimWithUI extends GUIState {

	public UbikSimDisplay3D ubikSimDisplay3D;
	public UbikSimDisplay2D ubikSimDisplay2D;
	
	public UbikSimWithUI(Ubik ubik) {
		super(ubik);		
	}

	public Object getSimulationInspectedObject() {
		return state;
	}

	public Inspector getInspector() {
		Inspector i = super.getInspector();
		i.setVolatile(true);
		return i;
	}

	public static String getName() {
		return "UbikSim";
	}

	private boolean initFrames = false;
	@Override
	public void start() {
		System.out.println("UbikSimWithUI.start()");
		super.start();
		//setupPortrayals();
		if(!initFrames) { 
			initFrames();
			initFrames = true;
		} else {
			ubikSimDisplay2D.reset(this);
			ubikSimDisplay3D.reset(this);
		}
	}

	@Override
	public void load(SimState state) {
		System.out.println("UbikSimWithUI.load()");
		super.load(state);
		//setupPortrayals();
	}

	@Override
	public void finish() {
		System.out.println("UbikSimWithUI.finish()");
		super.finish();		
		//clearFrames();
		//state = new Ubik(((Ubik)state).getSeedFromFile());
	}
	
	@Override
	public void init(Controller c) {
		System.out.println("UbikSimWithUI.init()");
		super.init(c);
	}

	@Override
	public void quit() {
		System.out.println("UbikSimWithUI.quit()");
		super.quit();
		clearFrames();
	}

	private void initFrames() {
		ubikSimDisplay3D = new UbikSimDisplay3D(this);
		controller.registerFrame(ubikSimDisplay3D);
		
		ubikSimDisplay2D = new UbikSimDisplay2D(this);
		controller.registerFrame(ubikSimDisplay2D);
	}
	
	private void clearFrames() {
		if(ubikSimDisplay2D != null) {
			controller.unregisterFrame(ubikSimDisplay2D);
			ubikSimDisplay2D.dispose();
		}
		if(ubikSimDisplay3D != null) {
			controller.unregisterFrame(ubikSimDisplay3D);
			ubikSimDisplay3D.dispose();
		}
	}
	
	public static void main(String [] args) {
		Ubik ubik = new Experiment(0);
		UbikSimWithUI vid = new UbikSimWithUI(ubik);
        Console c = new Console(vid);	
        c.setIncrementSeedOnStop(false);
        c.setVisible(true);	
	}
}
