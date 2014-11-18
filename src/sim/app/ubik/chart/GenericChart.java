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

package sim.app.ubik.chart;

import javax.swing.JFrame;
import sim.display.Controller;


import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.util.MutableDouble;


/**
 * 
 * Generic class to create a chart frame.
 */
public class GenericChart extends GUIState{
    /*El esquema 1 es para articulos*/
    public static int colorScheme = 1;
    public int timestepsToRedraw = 1;
    public Display2D display;
    public JFrame displayFrame;
  
    org.jfree.data.xy.XYSeries series;    // the data series we'll add to
    sim.util.media.chart.TimeSeriesChartGenerator chart;  // the charting facility
    Thread timer = null;
    
    protected String title;
    protected String xlabel;
    protected String ylabel;
    protected MutableDouble ox;
    protected MutableDouble oy;

    
  /**
   * 
   * @param state
   * @param title Title of ghe chart
   * @param objectinfo Object with information of x and y (methodx and y)
   * @param x Double to be read in chart, don't change this reference! only its value (attribute val)
   * @param y Double to be read in chart, don't change this reference! only its value (attribute val)
   * @param xlabel label for x
   * @param ylabel label for y
   */
    public GenericChart(SimState state, MutableDouble  x, MutableDouble  y, String title, String xlabel, String ylabel) {
        super(state);
        this.title=title;
        this.xlabel=xlabel;
        this.ylabel=ylabel;
        ox=x;
        oy=y;        
        this.init(null);
        this.start();
    }
    
    
    
   
        public void startTimer(final long milliseconds)
       {
       if (timer == null)
           timer= sim.util.gui.Utilities.doLater(milliseconds, new Runnable()
              {
              @Override
			public void run()
                  {
                  if (chart!=null) chart.update(state.schedule.getSteps(), true);                      
                  timer = null;  // reset the timer
                  }
              });
       }

            @Override
	public void start() //cuando se pulsa play
    {
        //super.start();
    
        
        
               
        chart.removeAllSeries();
        series = new org.jfree.data.xy.XYSeries(
            "Put a unique name for this series here so JFreeChart can hash with it",
            false);
        chart.addSeries(series, null);
        state.schedule.scheduleRepeating( new Steppable()
            {
            @Override
			public void step(SimState state)
               {
               // at this stage we're adding data to our chart.  We
               // need an X value and a Y value.  Typically the X
               // value is the schedule's timestamp.  The Y value
               // is whatever data you're extracting from your 
               // simulation.  For purposes of illustration, let's
               // extract the number of steps from the schedule and
               // run it through a sin wave.
            
                  
               double x = ox.doubleValue();
               double y = oy.doubleValue();

               
               // now add the data
               if (x >= Schedule.EPOCH && x < Schedule.AFTER_SIMULATION)
                                series.add(x, y, false);  // don't update automatically
                                startTimer(1000);  // once a second (1000 milliseconds)

               }
           });

    }
            
       @Override
	public void init(Controller c) {
        super.init(c);

        
       chart = new sim.util.media.chart.TimeSeriesChartGenerator();
       chart.setTitle(title);
       chart.setRangeAxisLabel(xlabel);
       chart.setDomainAxisLabel(ylabel);
       JFrame frame = chart.createFrame(this);
       // perhaps you might move the chart to where you like.
       frame.setVisible(true);
       frame.pack();
     
     
      //  display.attach(warningsPortrayal, "Warnings");
    }

    @Override
	public void quit() {
        super.quit();

        if (displayFrame != null) {
            displayFrame.dispose();
        }
        displayFrame = null;
        display = null;
    }
}
