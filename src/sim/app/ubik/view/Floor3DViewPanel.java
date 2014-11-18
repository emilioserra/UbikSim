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
package sim.app.ubik.view;


import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import sim.app.ubik.building.OfficeFloor;
import ubik3d.io.FileUserPreferences;
import ubik3d.swing.FileContentManager;
import ubik3d.swing.HomeComponent3D;
import ubik3d.swing.SwingViewFactory;
import ubik3d.viewcontroller.HomeController3D;

public class Floor3DViewPanel extends JPanel {
	protected OfficeFloor floor;
	
	protected HomeController3D c3d;
	protected HomeComponent3D homeComponent3D;
	
	public Floor3DViewPanel(OfficeFloor floor) {
		this.floor = floor;
		
		FileUserPreferences u = new FileUserPreferences();
		c3d = new HomeController3D(floor.getHome(), u,
				new SwingViewFactory(), new FileContentManager(u), null);			

		c3d.viewFromTop();
		c3d.moveCamera(500);
		c3d.elevateCamera(500);
		
		homeComponent3D = new HomeComponent3D(floor.getHome(), u, c3d);
		// Add tab key to input map to change camera
		InputMap inputMap = homeComponent3D
				.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke("SPACE"), "changeCamera");
			
		add("Center", homeComponent3D);	        
	}	
	
	public String toString() {
		return String.valueOf(floor);
	}
}