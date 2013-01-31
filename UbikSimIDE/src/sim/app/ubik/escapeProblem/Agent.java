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
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.portrayal.Oriented2D;
import sim.util.Bag;
import sim.util.Int2D;
import sim.util.IntBag;
import sim.util.MutableInt2D;


public class Agent implements Steppable, Stoppable, Oriented2D {

    static int maxFitness = 10;
    int stepsCounterForMovement=0;
 protected    static int scapingAgents=0;
    private int fitness = 10;
    private boolean escaping = false;
    private Stoppable stoppable;
    protected static double probabilitiesForEscapeByOtherAgent = 0.1;
    public MutableInt2D nowDirection;
    protected static int perceptionOfAgents = 4;

    public Agent(int nextFitness, MutableInt2D direction) {
        this.setFitness(nextFitness);
        nowDirection = direction;//dirección actual, la representada en el display
    //System.out.println(nowDirection);
    }

    public int getMaxFinessForWorkers() {
        return Agent.maxFitness;
    }

    public void fixStoppable(Stoppable stoppable) {
        this.stoppable = stoppable;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int val) {
        if (val >= 0 && val <= maxFitness) {
            fitness = val;
        } else {
            fitness = maxFitness;

        }
    }

    public void setEscaping(boolean b) {
        if(b && !getEscaping()) { 
            this.escaping = true;
            Agent.scapingAgents++;
        }
        if(!b && getEscaping()) {
            Agent.scapingAgents--;
            this.escaping = false;
            
        }
    }

    public boolean getEscaping() {
        return escaping;
    }

    public Object domFitness() {
        return new sim.util.Interval(0, maxFitness);
    }

    @Override
	public double orientation2D() {
        return MovementTools.getInstance().directionToRadians(this.nowDirection);
    }

    @Override
	public void step(SimState state) {
        MutableInt2D suggestedDirection = checkIfAgentsAreScaping(state);
        if (suggestedDirection != null) {
            this.setEscaping(true);
            this.nowDirection = suggestedDirection;
        }
        if(!fitnessPermitMove()) return;    
        /*si escapo doy un paso en la dirección almacenada si puedo (no hay gente ocupandola) si no paso a dirección de avance.
         * si no puedo quedo quieto. ¡cuidado. espacio torodial!.*/

        if (escaping) {
            /*Si un agente no puede moverse en una dirección inicia la huida en una de avance alternativa, al final se cubren*/
            makeAStep(state);

        }


    }

    
    public boolean fitnessPermitMove() {

        //***********************************************
        /*MOVIENDOSE SI LA FORMA FISICA LO PERMITE*/
        if (stepsCounterForMovement <= 0 && fitness != 0) {//si la forma es 0 el empleado no se mueve
            /*si la forma es igual a la máxima se mueve en cada paso (0 steps extra),
            si es 1 ncesitará maxfiness-fitness steps extra*/
            stepsCounterForMovement = Agent.maxFitness - fitness;
            return true;
        //continuo para moverme
        } else {
            stepsCounterForMovement--;
            return false;
        }
    //***********************************************

    }
        
        
    @Override
	public void stop() {
        this.stoppable.stop();
    }

    /**
     * Devuelve la direccion de un agente por el que se inicia una huida si se cumple probabilidad.
     * 
     * que indujo la huida.
     * null si no se inicia huida.
     * @param state
     * @return
     */
    private MutableInt2D checkIfAgentsAreScaping(SimState state) {

        EscapeProblem ep = (EscapeProblem) state;

        Int2D location = ep.yard.getObjectLocation(this);

        // System.out.println(location);
        // if(location==null) return null;


        //espacio torodial
        Bag agentsBag = ep.yard.getNeighborsMaxDistance(location.x, location.y, Agent.perceptionOfAgents, true, new Bag(), new IntBag(), new IntBag());


        if (agentsBag.numObjs > 1) {//si hay trabajadores a parte de el propio                         
        int directions[] = new int[8]; //se lleva la cuenta de los agentes que se ven en cada direccion
            
        boolean escape=false;
            for (int i = 0; i < agentsBag.numObjs; i++) {
                Agent a2 = (Agent) agentsBag.get(i);
                 //incremento la direccion
                 if(a2.getEscaping()) directions[ MovementTools.getInstance().getIndexDirection(a2.nowDirection)]++;
                //si el trabajador esta huyendo, no soy yo y se cumple probabilidad de que huya
                if (!a2.equals(this) && a2.getEscaping() && state.random.nextBoolean(Agent.probabilitiesForEscapeByOtherAgent)) {
                    escape = true;
                    //saco las direcciones a partir de la del agente que huye
                    //Int2D[] directions = MovementTools.getInstance().generateAdvanceDirection(state, new Int2D(a2.nowDirection.x, a2.nowDirection.y));
                    //de 0 a 2 cojo una direccion de avance a la direccion a la que esta avanzando el agente que ha inducido mi huida
                    //int index = MovementTools.getInstance().randomBetweenValues(state, 0, 2);
                    
                   // return new MutableInt2D(directions[index].x, directions[index].y);
                    
                }



            }
            if(escape) return getMostRepeatedDirection(state,directions);
        

        }
        return null;


    }
    
        /**
     * Da la direccion más repetida y si hay varias da una aleatoriamente
     * @param state
     * @param directions
     * @return
     */
    MutableInt2D getMostRepeatedDirection(SimState state, int[] directions) {
        int max=-1;
      int index=0;
      int maxOcurrences=0;
     
 
        for(int i=0;i< directions.length;i++){
            if(directions[i]> max) {
                 max= directions[i];
                 index=i;
                 maxOcurrences=1;
                
            }
            //se aumenta ocurrencias del maximo.
            if(directions[i]== max) {
               maxOcurrences++;
            }
            
            
        }
      //index es el numero de direccion más repetida
      if(maxOcurrences==1) return new MutableInt2D(MovementTools.getInstance().directions[index]);
      else{//aleatoriamente cojo uno de los indices con el numero de ocurrencias máximas
          
          do{
             index = MovementTools.getInstance().randomBetweenValues(state,0, directions.length-1);
          }while(directions[index]!=max);
          return new MutableInt2D(MovementTools.getInstance().directions[index]);
          
      }
       
          
    }


    private void makeAStep(SimState state) {
        EscapeProblem ep = (EscapeProblem) state;
        Int2D location = ep.yard.getObjectLocation(this);
        Int2D[] directions = MovementTools.getInstance().generateAdvanceDirection(state, new Int2D(nowDirection.x, nowDirection.y));
        //System.out.println(directions[0]);
        for (int i = 0; i < 2; i++) {

            int x = ep.yard.tx(directions[i].x + location.x);
            int y = ep.yard.ty(directions[i].y + location.y);
            Int2D newPostion = new Int2D(x, y);
            if (ep.validPositionToMove(newPostion)) {
                //System.out.println("direccion tomada " + directions[i]);
                ep.yard.setObjectLocation(this, newPostion);

                /*Se cambio la dirección si i no es 0*/
                if (i != 0) {
                    this.nowDirection.x = directions[i].x;
                    this.nowDirection.y = directions[i].y;
                }
                //ya se dio el paso
                return;
            }


        }

    
    }

}
