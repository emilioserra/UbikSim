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

package sim.app.ubik.utils;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


public abstract class Slider extends JPanel
                        implements ActionListener,
                                   WindowListener,
                                   ChangeListener {

    protected JSlider simSpeed;

    public Slider(int MIN,int MAX, int INIT) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));


        //Create the slider.
        simSpeed = new JSlider(SwingConstants.HORIZONTAL,
                                              MIN, MAX, INIT);


        simSpeed.addChangeListener(this);

        //Turn on labels at major tick marks.

        simSpeed.setMajorTickSpacing(1);
        simSpeed.setMinorTickSpacing(1);
        
        simSpeed.setBorder(
                BorderFactory.createEmptyBorder(0,0,10,0));
        Font font = new Font("Serif", Font.ITALIC, 15);
        simSpeed.setFont(font);

        //Put everything together.
        
        add(simSpeed);
        setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
    }

    /** Add a listener for window events. */
    void addWindowListener(Window w) {
        w.addWindowListener(this);
    }

    @Override
	public void windowIconified(WindowEvent e) {}
    @Override
	public void windowDeiconified(WindowEvent e) {}
    @Override
	public void windowOpened(WindowEvent e) {}
    @Override
	public void windowClosing(WindowEvent e) {}
    @Override
	public void windowClosed(WindowEvent e) {}
    @Override
	public void windowActivated(WindowEvent e) {}
    @Override
	public void windowDeactivated(WindowEvent e) {}
    @Override
	public void actionPerformed(ActionEvent e) {}

    @Override
	public abstract void stateChanged(ChangeEvent e);

}
