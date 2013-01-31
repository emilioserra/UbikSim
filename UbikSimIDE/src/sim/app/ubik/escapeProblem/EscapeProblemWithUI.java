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

package sim.app.ubik.escapeProblem;


import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.Inspector;
import sim.portrayal.Portrayal;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.network.NetworkPortrayal2D;
import sim.portrayal.simple.OrientedPortrayal2D;
import sim.portrayal.simple.OvalPortrayal2D;
import sim.util.media.chart.ChartGenerator;


public class EscapeProblemWithUI extends GUIState {
    /*El esquema 1 es para articulos*/
    public static int colorScheme = 1;
    public int timestepsToRedraw = 1;
    public Display2D display;
    public JFrame displayFrame;
    SparseGridPortrayal2D yardPortrayal = new SparseGridPortrayal2D();
    NetworkPortrayal2D warningsPortrayal = new NetworkPortrayal2D();
    org.jfree.data.xy.XYSeries series;    // the data series we'll add to
    sim.util.media.chart.TimeSeriesChartGenerator chart;  // the charting facility
Thread timer = null;

    public static void main(String[] args) {
        EscapeProblemWithUI epui = new EscapeProblemWithUI();
        Console c = new Console(epui);
        c.setVisible(true);
    }

    
    
    public void startTimer(final long milliseconds)
       {
       if (timer == null)
           timer= sim.util.gui.Utilities.doLater(milliseconds, new Runnable()
              {
              @Override
			public void run()
                  {
                  if (chart!=null) chart.update(ChartGenerator.FORCE_KEY, false);
                  timer = null;  // reset the timer
                  }
              });
       }

    public EscapeProblemWithUI() {
        super(new EscapeProblem(System.currentTimeMillis()));
    } //se pasa la semilla aleatorioa

    public EscapeProblemWithUI(SimState state) {
        super(state);
    }


    /*Inspector del modelo, se inspecciona ubik*/
    @Override
	public Object getSimulationInspectedObject() {
        return state;
    }
    /*obtener inspector, si el inspector modifica propiedades del modelo ser recomienda volatil a false*/
    @Override
	public Inspector getInspector() {
        Inspector i = super.getInspector();
        i.setVolatile(true);
        return i;
    }

    public static String getName() {
        return "EscapeProblem";
    }

    @Override
	public void start() //cuando se pulsa play
    {
        super.start();
        setupPortrayals();
        
        
               
        chart.removeAllSeries();
        series = new org.jfree.data.xy.XYSeries(
            "Put a unique name for this series here so JFreeChart can hash with it",
            false);
        chart.addSeries(series, null);
        scheduleImmediateRepeat(true, new Steppable()
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
               
               double x = state.schedule.time(); 
               double y =  ((EscapeProblem) state).getScapingAgents();

               
               // now add the data
               if (x >= Schedule.EPOCH && x < Schedule.AFTER_SIMULATION)
                                 series.add(x, y, false);  // don't update automatically
                                startTimer(1000);  // once a second (1000 milliseconds)

               }
           });

    }

    @Override
	public void load(SimState state)//cuando se carga simulación desde checkpoint
    {
        super.load(state);
        setupPortrayals();
    }

    
    
    public void setupPortrayals() {
        EscapeProblem ep = (EscapeProblem) state;

        // tell the portrayals what to portray and how to portray them
        yardPortrayal.setField(ep.yard);
        yardPortrayal.setPortrayalForAll(getAgentsPortrayal());
        
        
      /*  warningsPortrayal.setField(new SpatialNetwork2D(ep.yard, ep.warnings));
        warningsPortrayal.setPortrayalForAll(new SimpleEdgePortrayal2D());*/

        
        
        // reschedule the displayer
        display.reset();
        display.setBackdrop(Color.black);
        if(colorScheme==1)         display.setBackdrop(Color.white);
        
    
        
        // redraw the display
        display.repaint();
    }

    @Override
	public void init(Controller c) {
        super.init(c);

        
       chart = new sim.util.media.chart.TimeSeriesChartGenerator();
       chart.setTitle("Escaping Agents");
       chart.setRangeAxisLabel("Agents");
       chart.setDomainAxisLabel("Time");
       JFrame frame = chart.createFrame(this);
       // perhaps you might move the chart to where you like.
       frame.setVisible(true);
       frame.pack();
       c.registerFrame(frame);
       //frame.setLocationRelativeTo(null);
       
       

        // make the displayer
        display = new Display2D(600, 600, this, timestepsToRedraw);
        // turn off clipping
        display.setClipping(true);

        displayFrame = display.createFrame();
        displayFrame.setTitle("World");
        c.registerFrame(displayFrame);   // register the frame so it appears in the "Display" list
        displayFrame.setVisible(true);
        display.attach(yardPortrayal, "Yard");
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
    
    
    private Portrayal getAgentsPortrayal() {
             EscapeProblem ep = (EscapeProblem) state;
            OrientedPortrayal2D orientedpForAgents = null;


            OvalPortrayal2D ovalp = new OvalPortrayal2D();
            ovalp.paint = new Color(0,0,0,0); //transparente
                                         
            orientedpForAgents = new OrientedPortrayal2D(ovalp){
                @Override
				public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
                    Agent a = (Agent) object;
                    if(a.getEscaping()){
                          if(colorScheme==1) paint = new Color(0, 0, 0);//black
                          else  paint = new Color(255, 140, 0);//naranja
                    }
                    else paint = new Color(163,163,163);//gris            
                    super.draw(object, graphics, info);
                }
            };
            
         
            
              orientedpForAgents.setShape(1);//con 1 es el triangulo de netlogo

        

        /**TODO: el problema de usar  oriented es que va encima de otra representación básica, me habría gustado dejar la flecha sola,
         * y comprobar la posición del objeto para ponerla del color del obstaculo en cada paso sería poco eficiente
         * Si cargas mal una imagen se muestra solo la orientacion
         */
  

        return orientedpForAgents;
    }

}
