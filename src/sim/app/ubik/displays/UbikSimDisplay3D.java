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
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import sim.app.ubik.Ubik;
import sim.app.ubik.clock.RelojDigital;
import sim.app.ubik.people.KeyboardControlledByPerson;
import sim.app.ubik.people.Person;
import sim.display.Console;
import sim.display.GUIState;
import ubik3d.io.FileUserPreferences;
import ubik3d.model.Camera;
import ubik3d.swing.FileContentManager;
import ubik3d.swing.HomeComponent3D;
import ubik3d.swing.SwingViewFactory;
import ubik3d.viewcontroller.HomeController3D;

/**
 * Class for displaying 3D simulation. It extends JFrame and creates: 1. 3D
 * visualization of the scenario. 2. A control panel to display the date of the
 * simulation and to change the perspective of the view.
 * 
 */
public class UbikSimDisplay3D extends JFrame {

	private Ubik ubik;
	private GUIState guiState;
	
	private Camera viewFromTopCamera;
	
	private RelojDigital clock;
	private String[] viewModeOptions = { ViewPerspective.VIEW_FROM_TOP, ViewPerspective.VIEW_FROM_OBSERVER };
	private JComboBox viewMode;
	private JComboBox keyControledPeople;
	private JCheckBox showGraph;
	private HomeController3D c3d;
	private HomeComponent3D homeComponent3D;
	private KeyboardControlledByPerson keyboardControlledByPerson;	
	
	private ShowNavGraph showNavGraph;
        private static final Logger LOG = Logger.getLogger(UbikSimDisplay3D.class.getName());
	
	private List<ViewPerspectiveListener> perspectiveListeners;
	
	public UbikSimDisplay3D(GUIState guiState) {
		this.guiState = guiState;
		this.ubik = (Ubik) guiState.state;
		setTitle("UbikSim3D");
	}

	@Override
	public void dispose() {
		super.dispose();
		unregisterListeners();
	}

	public void reset(GUIState guiState) {
		this.guiState = guiState;
		this.ubik = (Ubik) guiState.state;
				
		if(showGraph != null)
			showNavGraph.clear();
		
		unregisterListeners();
		
		getContentPane().removeAll();
		
		init();
	}
	
	private void init() {
		this.perspectiveListeners = new ArrayList<ViewPerspectiveListener>();
		
		FileUserPreferences fileUserPreferences = new FileUserPreferences();
		c3d = new HomeController3D(ubik.getHomes().get(0), fileUserPreferences,
				new SwingViewFactory(), new FileContentManager(
						fileUserPreferences), null);

		homeComponent3D = new HomeComponent3D(ubik.getHomes().get(0),
				fileUserPreferences, c3d);
		// Add tab key to input map to change camera
		InputMap inputMap = homeComponent3D
				.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(KeyStroke.getKeyStroke("SPACE"), "changeCamera");

		Panel controlPanel = new Panel(new GridLayout(1, 4));

		// Clock Panel (1,1)
		JPanel clockPanel = new JPanel();
		clockPanel
				.setBorder(BorderFactory.createTitledBorder("Simulated Date"));
		clock = new RelojDigital();
		clockPanel.add(clock);
		controlPanel.add(clockPanel);

		// Perspective Panel (1,2)
		JPanel perspectivePanel = new JPanel(new GridLayout(1, 2));
		JLabel viewLabel = new JLabel("Perspective: ");
		perspectivePanel.add(viewLabel);
		viewMode = new JComboBox(viewModeOptions);
		viewMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String option = (String) viewMode.getSelectedItem();
				LOG.info(option);
				if (option.equals(ViewPerspective.VIEW_FROM_TOP)) {					
					setViewFromTop();
				} else if (option.equals(ViewPerspective.VIEW_FROM_OBSERVER)) {
					setViewFromObserver();
				}
				notifyAllViewPerspectiveListeners(new ViewPerspective((String)viewMode.getSelectedItem()));
			}
		});
		viewMode.setSelectedItem(ViewPerspective.VIEW_FROM_TOP);
		perspectivePanel.add(viewMode);
		controlPanel.add(perspectivePanel);

		// Key Controlled Person (1,3)
		JPanel keyPanel = new JPanel();
		keyPanel.setBorder(BorderFactory.createTitledBorder("Keyboard Controlled By Person"));
		
		keyboardControlledByPerson = KeyboardControlledByPerson.getInstance();
		keyboardControlledByPerson.init(0, new ViewPerspective((String)viewMode.getSelectedItem()), homeComponent3D, ubik);
		addViewPerspectiveListener(keyboardControlledByPerson);
		
		keyControledPeople = new JComboBox();
		keyControledPeople.setModel(new PeopleComboBox(ubik));
		keyControledPeople.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object item = keyControledPeople.getSelectedItem();
				if (item != null) {
					Person person = (Person) item;
					keyboardControlledByPerson.setControlledPerson(person);
				} else {
					keyboardControlledByPerson.setControlledPerson(null);
				}
			}
		});

		keyPanel.add(keyControledPeople);
		controlPanel.add(keyPanel);

		// Show graph (1,4)
		showNavGraph = new ShowNavGraph(ubik);
		showGraph = new JCheckBox("Show Nav. Graph");
		showGraph.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(showGraph.isSelected())
					((Console)guiState.controller).pressPause();
				showNavGraph.showNavGraph(showGraph.isSelected());
			}
		});
		controlPanel.add(showGraph);
		
		getContentPane().add("South", controlPanel);
		getContentPane().add("Center", homeComponent3D);

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

	public void setViewFromTop() {
		if(viewFromTopCamera == null) {
			c3d.viewFromTop();
			c3d.moveCamera(500);
			c3d.elevateCamera(500);
		} else {
			c3d.goToCamera(viewFromTopCamera);
		}
	}

	public void setViewFromObserver() {
		viewFromTopCamera = ubik.getHomes().get(0).getCamera().clone();
		c3d.viewFromObserver();
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			if (homeComponent3D == null) {
				init();
			} else
				registerListeners();
		} else {
			unregisterListeners();
		}
		super.setVisible(visible);
	}
	
	public void addViewPerspectiveListener(ViewPerspectiveListener vpl) {
		perspectiveListeners.add(vpl);
	}
	
	public void removeViewPerspectiveListener(ViewPerspectiveListener vpl) {
		perspectiveListeners.remove(vpl);
	}
	
	private void notifyAllViewPerspectiveListeners(ViewPerspective vp) {
		for(ViewPerspectiveListener l: perspectiveListeners) {
			l.notifyViewPerspectiveChanged(vp);
		}
	}
}
