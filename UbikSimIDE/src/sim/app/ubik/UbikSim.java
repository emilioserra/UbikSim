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


import sim.display.*;
import sim.engine.SimState;
import sim.engine.Steppable;


public class UbikSim extends GUIState {
	
    public UbikSim(Ubik ubik) {
        super(ubik);
    }
    
    @Override
	public void init(Controller c) {
        super.init(c);

        /*series = new ArrayList();
        chart = new sim.util.media.chart.TimeSeriesChartGenerator();
        chart.setTitle("Tiempos de ejecución");
        chart.setRangeAxisLabel("Tiempo Promedio de ejecutar step (Segundos)");
        chart.setDomainAxisLabel("Time");
        JFrame frame = chart.createFrame(this);
        // perhaps you might move the chart to where you like.
        frame.setVisible(true); //Sustituye a frame.show();
        frame.pack();
        c.registerFrame(frame);
        // the console automatically moves itself to the right of all
        // of its registered frames -- you might wish to rearrange the
        // location of all the windows, including the console, at this
        // point in time....*/
    }
    private boolean init = true;

    @Override
	public void start() {
        super.start();

        //chart.removeAllSeries();

        scheduleImmediateRepeat(true, new Steppable() {

            @Override
			public void step(SimState state) {
            }
        });
    }
}
