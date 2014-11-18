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
package sim.app.ubik.displays;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import sim.app.ubik.Ubik;
import sim.app.ubik.clock.RelojDigital;
import sim.display.GUIState;
import ubik3d.io.FileUserPreferences;
import ubik3d.swing.FileContentManager;
import ubik3d.swing.PlanComponent;
import ubik3d.swing.SwingViewFactory;
import ubik3d.viewcontroller.PlanController;

/**
 * Class for displaying 3D simulation. It extends JFrame and creates:
 * 1. 3D visualization of the scenario.
 * 2. A control panel to display the date of the simulation and to change the perspective of the view.
 * 
 */
public class UbikSimDisplay2D extends JFrame {
	
	private Ubik ubik;
	private GUIState guiState;
	
	private PlanComponent planComponent;
	private PlanController planController;
	
	private RelojDigital clock;
	
	public UbikSimDisplay2D(GUIState guiState) {
		this.guiState = guiState;
		this.ubik = (Ubik) guiState.state;
		setTitle("UbikSim2D");
	}
	
	@Override
	public void dispose() {
		super.dispose();
		unregisterListeners();
	}
	
	public void reset(GUIState guiState) {
		this.guiState = guiState;
		this.ubik = (Ubik) guiState.state;
				
		unregisterListeners();
		
		getContentPane().removeAll();
		
		init();
	}
	
	private void init() {
		FileUserPreferences fileUserPreferences = new FileUserPreferences();
		planController = new PlanControllerInspector(guiState, ubik.getHomes().get(0),
				fileUserPreferences, new SwingViewFactory(),
				new FileContentManager(fileUserPreferences), null);

		planComponent = new PlanComponent(ubik.getHomes().get(0),
				fileUserPreferences, planController);
		planComponent.setScale((-ubik.getBuilding().getFloor(0).cellSize + 110)
				/ (Math.max((float) ubik.getBuilding().getFloor(0).GRID_WIDTH,
						(float) ubik.getBuilding().getFloor(0).GRID_HEIGHT)));
		
		JScrollPane scrolledPlanComponent = new JScrollPane(planComponent);		

		JPanel controlPanel = new JPanel(new GridLayout(1, 2));
		
		JPanel clockPanel = new JPanel();
		clockPanel.setBorder(BorderFactory.createTitledBorder("Simulated Date"));
		clock = new RelojDigital();
		clockPanel.add(clock);
		controlPanel.add(clockPanel);
		
		JPanel scalePanel = new JPanel(new GridLayout(1, 2));
		scalePanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
		JButton decreaseScaleButton = new JButton("-");
		decreaseScaleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				planComponent.setScale(planComponent.getScale()/2.0f);
			}
		});
		JButton increaseScaleButton = new JButton("+");
		increaseScaleButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				planComponent.setScale(planComponent.getScale()*2.0f);
			}
		});
		scalePanel.add(decreaseScaleButton);
		scalePanel.add(increaseScaleButton);
		controlPanel.add(scalePanel);
		
		getContentPane().add("South", controlPanel);
		getContentPane().add("Center", scrolledPlanComponent);

		pack();
		setSize(600, 500);
		
		registerListeners();
	}
		
	private void unregisterListeners() {
		ubik.getClock().removeTimeListener(clock);
	}
	
	private void registerListeners() {
		ubik.getClock().addTimeListener(clock);
	}
	
	@Override
	public void setVisible(boolean visible) {
		if(visible) {
			if(planComponent == null) {
				init();
			} else
				registerListeners();
		} else {
			unregisterListeners();
		}
		super.setVisible(visible);
	}
}
