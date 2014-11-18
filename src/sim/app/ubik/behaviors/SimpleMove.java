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
package sim.app.ubik.behaviors;

import java.util.logging.Logger;
import sim.app.ubik.Ubik;
import sim.app.ubik.people.Person;
import sim.engine.SimState;
import sim.util.Double2D;
import sim.util.MutableInt2D;

/**
  * @deprecated Use pathfinder class instead
 * Moverse a un punto en línea recta, no calcula rutas para esquivar muros.
 * @todo Algo falla en este sistema para evitar obstaculos, en huidas masivas se atascan y no salen Además habría que comentarlo.
  * @author Juan A. Botía, Pablo Campillo, Francisco Campuzano, and Emilio Serrano
 */
public class SimpleMove extends SimpleState {
    private static final Logger LOG = Logger.getLogger(SimpleMove.class.getName());

	protected int x;
    protected int y;
    protected MutableInt2D relativePos=null;
    
    protected float minDistance = 0;
    protected int numOfTries = -1;
    
    /**
     *
     * 
     * Esta clase es un estado de comportamiento. Como no tiene autómata subordinado se extiende SimpleState
     * Mueve a un punto sin calcular rutas.
     * @param personImplementingAutomaton
     * @param name
     * @param x X destino
     * @param y Y destino
     */
    public SimpleMove(Person personImplementingAutomaton,  String name, int x, int y){
        super(personImplementingAutomaton,1,-1,name);
        this.x=x;
        this.y=y;
    }
    
    
     /**  
     * Activando "relative" se entiende que las coordenadas x e y dadas en el constructor
     * se deben sumar a la posición  de la persona en el momento en el que el automata tome el control.
     *
     */
    
    public  SimpleMove(Person personImplementingAutomaton,  String name,  int x, int y, boolean relative){
        this(personImplementingAutomaton,name,x,y);    
        if(relative==true){
            relativePos= new MutableInt2D();
            relativePos.x=x;
            relativePos.y=y;
        }
    }
    
    /**
    *
    * 
    * Esta clase es un estado de comportamiento. Como no tiene autómata subordinado se extiende SimpleState
    * Mueve a un punto sin calcular rutas.
    * @param personImplementingAutomaton
    * @param name
    * @param x X destino
    * @param y Y destino
    * @param minDistance distacia mm�nima que se considera que se ha llegado al detino
    */
   public SimpleMove(Person personImplementingAutomaton,  String name, int x, int y, float minDistance){
       this(personImplementingAutomaton,"SimpleMove",x,y);
       this.minDistance = minDistance;
   }
   
   /**
   * 
   * Esta clase es un estado de comportamiento. Como no tiene autómata subordinado se extiende SimpleState
   * Mueve a un punto sin calcular rutas.
   * @param personImplementingAutomaton
   * @param name
   * @param x X destino
   * @param y Y destino
   * @param numOfTries numero de intentos m�ximos para conseguir el objetivo
   */
  public SimpleMove(Person personImplementingAutomaton,  String name, int x, int y, int numOfTries){
      this(personImplementingAutomaton,"SimpleMove",x,y);
      this.numOfTries = numOfTries;
  }
    
    /**
     * Movimiento simple al destino. Se comprueba si hay obstaculo. Si lo hay, se cambia
     * angulo de la persona para moverse.
     *
     * 
     */
    public void nextState(SimState state) {
        //if(ECHO) System.out.println(personImplementingAutomaton.getName() + ", " +  toString() + " automaton step ");
        if(relativePos!=null){//si tengo x,y, y relativo activado, actualizo               
                x= relativePos.getX() + personImplementingAutomaton.getPosition().x;
                y= relativePos.getY() + personImplementingAutomaton.getPosition().y;                                
        }                  
        if(!personImplementingAutomaton.move(x, y)) {
        	  numOfTries--;
            float f = ((Ubik) state).random.nextFloat();
            if(f < 0.5)
                personImplementingAutomaton.setAngle(personImplementingAutomaton.getAngle()+Math.PI/2.0);
            else
                personImplementingAutomaton.setAngle(personImplementingAutomaton.getAngle()-Math.PI/2.0);
            personImplementingAutomaton.move();
        }
    }




    /**
     * Se redefine la condición de finalización del autómata para terminar si el destino ha sido alcanzado
     * (a parted de por duración o por influencia externa).
     * Llama a destino alcanzado que tiene en cuenta: (1) la distancia mínima al destino para considerar que se ha llegado (si se uso
     * el constructor al que se pasa minDistance), (2) el número de intentos para llegar al destino (si se uso constructor adecuado).
     * 
     * @param state
     * @return
     */
    @Override
    public boolean isFinished(SimState state){
        if(super.isFinished(state)) return true;
        return destinyAchieved(state);
    }

    /**
     * Comprueba si se ha llegado al destino.
     *  Ver isFinished
     * @param simState
     * @return
     */
    private boolean destinyAchieved(SimState simState){
        MutableInt2D m= personImplementingAutomaton.getPosition();
        if(m.distance(new Double2D(x,y)) <= minDistance) 
        	return true;
        else if(numOfTries == 0) {
                LOG.info(name + "Destiny is not achieved, but the number of tries has been exeeded");
        	return true;
        }
        return false;
    }
    /**
     * Se amplia toString con  el destino
     * @return 
     */
    public String toString(){
        int cs=this.personImplementingAutomaton.getUbik().getCellSize();
        return super.toString() + ", simple move to (" + x  +"," + y + ") [editor format: " + x*cs + "," + y*cs +"]" ;
    }

}


