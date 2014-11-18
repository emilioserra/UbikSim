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

import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import sim.app.ubik.building.OfficeFloor;
import sim.app.ubik.view.model.FloorTableModel;
import sim.app.ubik.view.model.PeopleTableModel;

public class ViewManagerFrame extends JFrame {
	protected ViewManagerController vmc;

	protected JTabbedPane tabbedPane;

	protected JPanel mainPanel;

	protected JTable floorsTable;
	protected JTable peopleTable;

	protected JList paneList;

	public ViewManagerFrame(ViewManagerController vmc) {
		this.vmc = vmc;
		this.setTitle("Visualization Manager");

		createSwing();

		setExtendedState(MAXIMIZED_BOTH);
		setVisible(true);
	}

	public void createSwing() {

		JPanel tabPanel = createTabPane();
		mainPanel = createMainPane();

		JSplitPane splitPane1 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				tabPanel, mainPanel);
		splitPane1.setOneTouchExpandable(true);
		splitPane1.setDividerLocation(200);

		JPanel miniViewPane = createListPane();

		JSplitPane splitPane2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				splitPane1, miniViewPane);
		splitPane2.setOneTouchExpandable(true);
		splitPane2.setDividerLocation(1000);

		add(splitPane2);
	}

	public JPanel createTabPane() {
		tabbedPane = new JTabbedPane();
		tabbedPane.setMinimumSize(new Dimension(200, 500));
		floorsTable = new JTable();
		floorsTable.setModel(new FloorTableModel(vmc.getFloors()));
		tabbedPane.addTab("Floors", floorsTable);

		peopleTable = new JTable();
		peopleTable.setModel(new PeopleTableModel(vmc.getPeople()));
		tabbedPane.addTab("People", peopleTable);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(tabbedPane, BorderLayout.CENTER);
		panel.setBorder(BorderFactory.createTitledBorder("Elements"));
		return panel;
	}

	public JPanel createMainPane() {
		JPanel panel = new JPanel();
		panel.setMinimumSize(new Dimension(500, 500));
		panel.setBorder(BorderFactory.createTitledBorder("Main View"));
		return panel;
	}

	public JPanel createListPane() {		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setMinimumSize(new Dimension(200, 500));
		paneList = new JList();
		paneList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		for(OfficeFloor of: vmc.getFloors()) {
			paneList.add(new Floor3DViewPanel(of));
		}
		scrollPane.add(paneList);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(BorderFactory.createTitledBorder("Mini Views"));
		panel.add(scrollPane);
		
		return panel;
	}
}
