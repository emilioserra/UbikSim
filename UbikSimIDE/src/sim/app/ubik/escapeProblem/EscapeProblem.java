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

import sim.engine.SimState;
import sim.engine.Stoppable;
import sim.field.grid.SparseGrid2D;
import sim.field.network.Network;
import sim.util.Int2D;
import sim.util.MutableInt2D;


public class EscapeProblem extends SimState {
    public static final boolean log=false;
    public static int GRID_HEIGHT = 100;
    public static int GRID_WIDTH = 100;
    public SparseGrid2D yard = new SparseGrid2D(GRID_WIDTH, GRID_HEIGHT);
    /*El espacio de trabajo  de un trabajador serán al menos los 8 que le rodean . luego el máximo de trabajadores por planta es relativo a estos valores*/
    public int numAgents = 100;
    /*Steps extra a esperar para cada comportamiento*/
    public Network warnings = new Network(true);
    public Agent agents[];

    public int getNumAgents() {
        return numAgents;
    }

    public void setNumAgents(int val) {
        if (val > 0) {
            numAgents = val;
        }
    }

    public EscapeProblem(long seed) {
        super(seed);
    }

    @Override
	public void start() {
        super.start();

        yard.clear();
        agents = new Agent[numAgents];
        warnings.clear();
        Agent.scapingAgents=0;


        // add some students to the yard

        for (int i = 0; i < numAgents; i++) {
            /*Por ahora el tipo de trabajado y la forma es aleatoria, se considera forma física de 0 (trabajadores estáticos)*/
            int nextFitness = MovementTools.getInstance().randomBetweenValues(random, Agent.maxFitness / 2, Agent.maxFitness);
            MutableInt2D nextDirection = new MutableInt2D(MovementTools.getInstance().randomBetweenValues(random, -1, 1), MovementTools.getInstance().randomBetweenValues(random, -1, 1));
            if (nextFitness == 0) {
                System.out.println("Agent with a fitness 0, he will not be able to walk");
            }
            Agent agent = new Agent(nextFitness, nextDirection);
            agents[i] = agent;
            Stoppable s = schedule.scheduleRepeating(agent);
            agent.fixStoppable(s);
            warnings.addNode(agent);



            int counter = 10000000;
            Int2D nextPosition;
            do {
                nextPosition = new Int2D(MovementTools.getInstance().randomBetweenValues(random, 0, EscapeProblem.GRID_WIDTH - 1), MovementTools.getInstance().randomBetweenValues(random, 0, EscapeProblem.GRID_HEIGHT - 1));
                counter--;
            } while (!validPositionToMove(nextPosition) && counter != 0);
            if (counter == 0) {
                System.err.println("TOO MUCH AGENTS FOR THIS SPACE");
                System.exit(0);
            }
            this.yard.setObjectLocation(agent, nextPosition.x, nextPosition.y);

        }//fin for creacion agentes
        
        
        
                    
            //uno empieza a huir aleatoriamente
            int nagente =MovementTools.getInstance().randomBetweenValues(random, 0, agents.length-1);
            agents[nagente].setEscaping(true);
            
            if(log){
                LoggerAgent la = new LoggerAgent();
                Stoppable s2 = schedule.scheduleRepeating(la);
                la.fixStoppable(s2);
        
            }



    }
    
                public double getProbabilitiesForEscapeByOtherAgent() {
        return Agent.probabilitiesForEscapeByOtherAgent;
    }

    public void setProbabilitiesForEscapeByOtherAgent(double val) {
        if (val > 0 || val <1) {
            Agent.probabilitiesForEscapeByOtherAgent = val;
            
        }
    }
        
                
    public int getPerceptionOfAgents(){
        return Agent.perceptionOfAgents;
    }
    
    public void setPerceptionOfAgents(int p) {
          Agent.perceptionOfAgents=p;
        
    }
   

    public static void main(String[] args) {
        doLoop(EscapeProblem.class, args);
        System.exit(0);
    }

    protected boolean validPositionToMove(Int2D pos) {

        return (yard.getObjectsAtLocation(pos.x, pos.y) == null);
    }

    public  int getScapingAgents() {
        return Agent.scapingAgents;
    }
}


