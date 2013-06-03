/*
 * Contact, UbikSimIDE has been developed by:
 * 
 * Juan A. Botía , juanbot@um.es
 * Pablo Campillo, pablocampillo@um.es
 * Francisco Campuzano, fjcampuzano@um.es
 * Emilio Serrano, emilioserra@um.es 
 * 
 * This file is part of UbikSimIDE and a modified version (on 10/02/2011) of 
 * Sweet Home 3D version 3.3, Copyright (c) 2005-2011 Emmanuel PUYBARET / eTeks.
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
package ubik3d.swing;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;

import ubik3d.tools.OperatingSystem;


/**
 * A tool bar where all components are maintained unfocusable.
 * Under Mac OS X 10.5 and superior, it also uses segmented buttons and groups them.
 * @author Emmanuel Puybaret
 */
public class UnfocusableToolBar extends JToolBar {
  /**
   * Creates an unfocusable toolbar.
   */
  public UnfocusableToolBar() {
    // Update toolBar buttons when component orientation changes 
    // and when buttons are added or removed to it  
    addPropertyChangeListener("componentOrientation", 
        new PropertyChangeListener () {
          public void propertyChange(PropertyChangeEvent evt) {
            updateToolBarButtons();
          }
        });
    addContainerListener(new ContainerListener() {
        public void componentAdded(ContainerEvent ev) {
          updateToolBarButtons();
        }
        
        public void componentRemoved(ContainerEvent ev) {}
      });
  }

  /**
   * Ensures that all the children of this tool bar aren't focusable. 
   * Under Mac OS X 10.5, it also uses segmented buttons and groups them depending
   * on toolbar orientation and whether a button is after or before a separator.
   */
  private void updateToolBarButtons() {
    // Retrieve component orientation because Mac OS X 10.5 miserably doesn't it take into account 
    ComponentOrientation orientation = getComponentOrientation();
    Component previousComponent = null;
    for (int i = 0, n = getComponentCount(); i < n; i++) {        
      JComponent component = (JComponent)getComponentAtIndex(i); 
      // Remove focusable property on buttons
      component.setFocusable(false);
      
      if (!(component instanceof AbstractButton)) {
        previousComponent = null;
        continue;
      }          
      if (OperatingSystem.isMacOSXLeopardOrSuperior()) {
        Component nextComponent;
        if (i < n - 1) {
          nextComponent = getComponentAtIndex(i + 1);
        } else {
          nextComponent = null;
        }
        component.putClientProperty("JButton.buttonType", "segmentedTextured");
        if (previousComponent == null
            && !(nextComponent instanceof AbstractButton)) {
          component.putClientProperty("JButton.segmentPosition", "only");
        } else if (previousComponent == null) {
          component.putClientProperty("JButton.segmentPosition", 
              orientation.isLeftToRight() 
                ? "first"
                : "last");
        } else if (!(nextComponent instanceof AbstractButton)) {
          component.putClientProperty("JButton.segmentPosition",
              orientation.isLeftToRight() 
                ? "last"
                : "first");
        } else {
          component.putClientProperty("JButton.segmentPosition", "middle");
        }
        previousComponent = component;
      }
    }
  }
}
