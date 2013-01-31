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


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;


public class LoggerAgent implements Steppable, Stoppable {
    private int finalStep=3000;
    private Stoppable stoppable;
    private PrintWriter output;
    private String date;

    private ArrayList buildingParameters;
    private long seed;
    public static int skipSteps = 49;
    private int counterSteps = 0;

    public LoggerAgent() {
        output = null;
    }

    public static void fixSkipSteps(int steps) {
        skipSteps = steps;
    }

    public LoggerAgent(long seed) {
        this();
      

        this.seed = seed;
    }

    @Override
	public void step(SimState state) {

        int step = (int) ((EscapeProblem) state).schedule.getSteps();

        /**
         * Se pueden saltar pasos en la captura. En este método al final se llama a un metodo de checkFInal
         * que da true cuando todos los agentes han salido o muertos. Pero como la simulación sigue a causa del fuego,
         * no da error leer cada intervalo razonable de steps.
         */
        if (skipSteps != 0) {
            if (counterSteps > 0) {
                counterSteps--;
                return;
            } else {
                counterSteps = skipSteps;
            }
        }


        createLogs(state, step);

        if (checkIfFinishLogging(state)) {
            output.close();
    
            output = null;
            this.stop();
        }




    }

 


    private void createLogs(SimState state, int step) {
        EscapeProblem ep = (EscapeProblem) state;
        String line;

        if (output == null) {
            try {
                //guardo marca de tiempo para repetir en varios ficheros de log
                date = this.getDate();
                createInitialLogFile(state);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        //line = "Step" + "\t" +"SavedAgents" + "\t" + "DiedAgents" + "\t" + "ExposedAgents" + "\t" + "Fires";
        line = step + "\t" + ep.getScapingAgents();
  
        output.println(line);
    }

    private String getDate() {
        Calendar calendario = Calendar.getInstance();
        String r = calendario.getTime().toString();
        r = r.replace(':', '.');
        return r;
    }

 
/**
 * Termino si he llegado al final de los steps a registrar o si todos los agentes estan huyendo
 * @param state
 * @return
 */
    public boolean checkIfFinishLogging(SimState state) {
              int step = (int) ((EscapeProblem) state).schedule.getSteps();
              if(step>= this.finalStep) return true;
               EscapeProblem ep = (EscapeProblem) state;
               return (ep.getScapingAgents() == ep.getNumAgents());

    }

    @Override
	public void stop() {
        stoppable.stop();
    }

    /**
     * En Mason, cuando un evento se pone a repetir en schedule, se devuelve un Stoppable. Llamando a stop ese agente deja de tener el turno para ejecutarse.
     * En el caso de trabajadores, el fuego con intensidad suficiente puede "pararlos"
     * @param stoppable
     */
    public void fixStoppable(Stoppable stoppable) {
        this.stoppable = stoppable;
    }

    private void createInitialLogFile(SimState state) throws IOException {
            EscapeProblem ep = (EscapeProblem) state;
        output = new PrintWriter(new FileWriter("LogEscapeProblem INFOSIMULATION " + " " + date + ".txt"));

        String line = "LogEscapeProblem, date: " + getDate();
        output.println(line);

        if (seed != 0) {
            line = "Random seed " + seed;
            output.println(line);
        }

        line = "Number of agents = " + ep.getNumAgents();
        output.println(line);
   
     

        output.close();



        output = new PrintWriter(new FileWriter("LogEscapeProblem OUTPUTSIMULATION " +" " + date + ".txt"));
        line = "Step" + "\t" + "EscapingAgents";

        output.println(line);

    }
}