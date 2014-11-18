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

import java.util.ArrayList;
import java.util.List;
import sim.app.ubik.Ubik;
import sim.app.ubik.building.rooms.Room;
import sim.app.ubik.people.Person;
import sim.engine.SimState;
import sim.util.Int2D;
import sim.util.MutableInt2D;
import ubik3d.model.HomePieceOfFurniture;

/**
 * @deprecated Use pathfinder class instead
 * Moverse a una posición o una habitación generando una ruta
* @todo ¿Por qué la ruta a veces devuelve nulo?, hay una comparación en createNewTransitions que por ahora devuelve un estado WaitingForRoute, pero eso
 * habría que arreglarlo. A veces no se devuelve ruta alguna, por ejemplo si vas a habitación 056, primer baño, desde los despachos de abajo
 * @todo Hacer que cuando se de una habitación y el centro este ocupado, se devuelva la posición libre más alejada de la puerta, así
 * se pueden tener reuniones.
 * @todo habriá que hacer un estado aun más simple que simpleMove, simpleStep, porque si se mueve por un pasillo no se le puede interrumpir (lo hace
 * del tirón sin dar control al autómata).
  * @author Juan A. Botía, Pablo Campillo, Francisco Campuzano, and Emilio Serrano
 */
public class Move extends Automaton{
    /**
     * Sólo se genera transiciones una vez. Autómata en cadena.
     */    
    protected boolean transitionsGenerated;
    protected int x=-1;
    protected int y=-1;
    protected String roomName=null;
    protected Room room=null;
    protected MutableInt2D relativePos=null;
    protected HomePieceOfFurniture objectToGoAndLook;
    /**
     * a false, move no termina aunque no consiga generar una ruta. se imprime mensaje de error waiting for route
     * a true, si no se consigue generar ruta, move termina
     */
    public static boolean FINISHIFPATHNOTGENERATED=false;
    
   
   

    /**
     * Ver constructor de autómata, aquí se añade el destino en forma de x,y
     * @todo: NO FUNCIONA MUY BIEN. EN EL ESCENARIO DE QUICK START NO PODÍA DAR UN PAR DE PASOS. HE 
     * TENIDO QUE USAR SIMPLEMOVE
     * @param personImplementingAutomaton
     * @param priority
     * @param duration
     * @param name
     * @param x
     * @param y
     */
    public  Move(Person personImplementingAutomaton, int priority, int duration, String name,  int x, int y){
        super(personImplementingAutomaton,priority,duration,name);
        this.x=x;
        this.y=y;        
    }
    
    /*
     *  
     * Activando "relative" se entiende que las coordenadas x e y dadas en el constructor
     * se deben sumar a la posición  de la persona en el momento en el que el automata tome el control.
     *
     */
    
    public  Move(Person personImplementingAutomaton, int priority, int duration, String name,  int x, int y, boolean relative){
        this(personImplementingAutomaton,priority,duration,name,x,y);    
        if(relative==true){
            relativePos= new MutableInt2D();
            relativePos.x=x;
            relativePos.y=y;

        }
    }

    /**
     * Moverse a cierta distancia de un objeto, se situa delante del objeto (ángulo 0) a una distancia en centimetros pasada como parámetro
     * Además, se genera una transición a "LookAtObject" al final.
     * @param personImplementingAutomaton
     * @param priority
     * @param duration
     * @param name
     * @param object3d
     */
     public  Move(Person personImplementingAutomaton, int priority, int duration, String name,    HomePieceOfFurniture object3d, int distance){
        super(personImplementingAutomaton,priority,duration,name);
        MutableInt2D m = PositionTools.getApproximationPoint(object3d,this.personImplementingAutomaton.getUbik().getCellSize(),distance,0,true);
       objectToGoAndLook =object3d;
        this.x=m.getX();
        this.y=m.getY();
     }

   
    /**
     *  Ver constructor de autómata, aquí se añade el destino en forma de nombre de habitación
     * @param personImplementingAutomaton
     * @param priority
     * @param duration
     * @param name
     * @param roomName
     */

   public  Move(Person personImplementingAutomaton, int priority, int duration, String name,  String roomName){
        super(personImplementingAutomaton,priority,duration,name);
        this.roomName=roomName;
        this.room= PositionTools.getRoom(personImplementingAutomaton, roomName);
    }

    public  Move(Person personImplementingAutomaton, int priority, int duration, String name,  Room room){
        super(personImplementingAutomaton,priority,duration,name);
        this.roomName=room.getName();
        this.room=room;
    }
    /**
     * No se vuelve a un estado por defecto
     * @param simState
     * @return
     */
    @Override
    public Automaton getDefaultState(SimState simState) {
       return null;
    }
    /**
     * Se generan los estados para ir a la habitación sólo una vez.
     * Estos estados son instancias de SimpleMove para cada elemento de la ruta calculada.      
     * Si la ruta no se calcula (a veces devuelve null) se devuelve un estado WaitingForRoute
     * @param simState
     * @return
     */
    @Override
    public ArrayList<Automaton> createNewTransitions(SimState simState) {

        if(transitionsGenerated ) return null; //solo se genera la ruta una vez
        //recuperar posición destino si se pasó nombre de habitación
        loadDestiny();          
        if(isFinished(simState)) return null;// nada si ya esta en destino         
        ArrayList<Automaton> states=null;
        //sólo se genera un grupo de transiciones a seguir de una en una (misma prioridad).
        if(!transitionsGenerated){
            states= loadPath(simState);                             
        }
        //si se dio una ruta, ya no se vuelven a generar transiciones
       
        return states;
    }

    /**
     * En caso de pausa, habrá que descartar las rutas establecidas y volver a generarlas por si la persona ha cambiado de posición.
     * @param simState
     */
   @Override
   public  void interrupt(SimState simState){
    this.clearPendingTransitions();
    transitionsGenerated=false;
   }

/**
 * Destino redefinido para comprobar si se ha llegado al estado final.
 * Lanza excepción si acaa sin alcanzar el destino
 * @param state
 * @return
 */
   @Override
    public boolean isFinished(SimState state){        
        
       boolean destinyReached= destinyAchieved(state);
        //if(destinyReached) ((MuseumVisitorAutomaton) this.automatonFahter).testPositionsQR();
       
        if(!FINISHIFPATHNOTGENERATED && !destinyReached &&  !isFinishedBecauseOfTime(state) && super.isFinished(state) && this.transitionsPlanned()==0)  throw new RuntimeException("Move does not reach the final point");      
        if(destinyReached) {
            if(this.objectToGoAndLook!=null)  personImplementingAutomaton.setAngle(PositionTools.angleToLookInRadians(personImplementingAutomaton, objectToGoAndLook));
            return true;
        }     

        return super.isFinished(state);
        
        
    }
/**
 * Llegar al destino, si se dio posiciones serán esas
 * si se dio habitación basta estar en ella (se suelen quedar en la puerta
 * con esta opción)
 * @param simState
 * @return 
 */
    private boolean destinyAchieved(SimState simState){         
        MutableInt2D m= personImplementingAutomaton.getPosition();
        if(this.roomName==null){//destino clavado, no vale con estar en habitación
            if(m.x==x && m.y==y) {               
                return true;
            }
        }
        else{           
           if(room.contains(m.x, m.y)) return true;
        }
        return false;
    }

    public Room getRoom(){
        return room;
    }
    
    /**
     * Método para poder reiniciar el estado. Las variables deben permitir otra ejecución.
     * IMPORTANTE, PARA USAR ESTADOS EN FSM DEBEN REDEFINIR RESTART, VER QUICK START DE MANUAL
     * Los fallos derivados de una mala implementación del método restart son particularmente difíciles de detectar ya que se manifiestan no la primera vez que se hace una transición a un estado, sino en las sucesivas transiciones a este (cuando no se ha ejecutado el restart adecuadamente por el inteprete del FSM).
     * @param simstate 
     */
    @Override
    public void restart(SimState simstate){
        super.restart(simstate);
        transitionsGenerated=false;
    
  
    }

    /**
     * Carga el destino del movimiento. Puede ser una habitación o relativo: movimientos relativos
     * a la posición actual del agente
     */
    private void loadDestiny() {
         if(x==-1 && y==-1){
            room= PositionTools.getRoom(personImplementingAutomaton, roomName);           
            Int2D destiny = room.getCenter();
            x=destiny.x;
            y=destiny.y;

        }        
       if(relativePos!=null){//si tengo x,y, y relativo activado, actualizo               
                x= relativePos.getX() + personImplementingAutomaton.getPosition().x;
                y= relativePos.getY() + personImplementingAutomaton.getPosition().y;                         
       }
    }

    /**
     * Calcula ruta, devuelve null si no lo consigue
     * También se encarga de marcar si las transiciones se han generado o no.
     * @return 
     */
    private ArrayList<Automaton> loadPath(SimState simState) {
            ArrayList<Automaton> states = new ArrayList<Automaton>();
            Int2D origin = new Int2D(personImplementingAutomaton.getPosition().x, personImplementingAutomaton.getPosition().y);
            Int2D destiny= new Int2D(x,y);
            List<Int2D> route = ((Ubik) simState).getBuilding().getBuildingToGraph().getRoadMap().getRoute(origin, destiny);
           
            
            //crea estados a los que transitar para cada destino de la ruta
            if(route==null){// a veces la ruta devuelve nulo, genero un no hacer nada 1, y devuelvo. No pongo transitionsGenerated a true                            
                if(FINISHIFPATHNOTGENERATED){//si no se dio ruta y quiero terminar sin reintentos, marco el final    
                    this.setFinished(true); //marco final de automata
                    return null;
                } //devuelvo la ruta vacía                   
                states.add(new DoNothing(personImplementingAutomaton, 0, 1, "WaitingForRoute from " + origin.toString() + " to " +destiny.toString()));
                System.err.println(personImplementingAutomaton.getName()  +  " WaitingForRoute from " + origin.toString() + " to " +destiny.toString() );                  
                return states;
            }
            
            if(route!=null) route.add(destiny);
            for(Int2D newDestiny : route){
                states.add(new SimpleMove(personImplementingAutomaton, "SimpleMove", newDestiny.x,  newDestiny.y));                
            }
            transitionsGenerated=true;
            return states;
    }

}
