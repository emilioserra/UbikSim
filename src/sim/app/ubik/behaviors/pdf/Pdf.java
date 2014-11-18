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
package sim.app.ubik.behaviors.pdf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import org.rosuda.JRI.REXP;
import sim.app.ubik.R.RInterface;
import sim.engine.SimState;
import sim.util.MutableInt2D;
import sim.app.ubik.behaviors.TimeTools;

/**
 * Clase con las funciones de probabilidad y las comprobaciones necesarias para generar transiciones.
 * Se puede extender esta clase y modificar el chequeo que permite nuevas transiciones.
     * @todo las funciones son un poco raras. Por ejemplo, toilet gnera la lista  [3, 2, 37, 11
     * que, entiendo,significa que se va al baño tras 3 minutos desde la última vez, tras, 2, tras 37..
 */
public abstract class Pdf {
    /**
     * Nombre de la función de distribución
     */
 protected String name;
 
        /**
     * nombre de fichero con la función de probabilidad en R para rellenar los tiempos.
     * Un posible contenido para un fichero es: rgamma(5000,5,0.1)
     */
    @Deprecated
    protected String probabilityDistributionFileName;
    /**Enteros con los tiempos en los que una transición a un
     * estado concreto se añadirá a la lista de tareas pendientes del autómata.*/
    @Deprecated
    protected LinkedList<Integer> timesForTransitions;
   /** Función de probabilidad para comportamiento monónono o no monótono. Por defecto no monotono. */
    protected boolean monotonous=false;
    /** Entero con la última transición para  comportamiento monónono. */
     protected double lastTransition;
     /** Entero con la próxima transición.*/
     protected int nextTransition;
     /** Entero con el momento en minutos en el que se puede iniciar una transición de comportamiento  monótono */
     protected double timeOfInitOfTransition;
     /** Espacio de tiempo en el que se puede dar la transición en un monótono, minutos */
     protected double slotOfTimeForTransition;
     /**Los monotonos se ejecutan una vez al día, esta bandera lo controla*/
     protected boolean monotonousExectutedToday=true;


     /**Imprimir evolución de la función por pantalla*/
 protected static boolean ECHO = false;
 /**Intervalo para permitir transición. Guarda dos minutos del día en los que se puede hacer una transición*/
 protected MutableInt2D timeInterval=null;


 /**
  * Constructor para pdf no monotonas
  * @param name
  */
    public  Pdf(String name){
         this.name=name;
    }

    /**
     * Constructor para monotonas
     * @param timeOfInitOfTransition  Entero con el minuto en el que se puede iniciar una transición de comportamiento  monótono
     * @param slotOfTimeForTransition Minutos en el que se puede dar la transición en un monótono.
     */
    public  Pdf(String name, int timeOfInitOfTransition, int slotOfTimeForTransition){
        this.name=name;
        monotonous=true;
        this.timeOfInitOfTransition= timeOfInitOfTransition;
        this.slotOfTimeForTransition= slotOfTimeForTransition;
    }


 /**
  * Constructor para pdf no monotonas
  * @param name
  * @param probabilityDistributionFileName
  * @deprecated Replaced by {@link #Pdf(String)} 
  */
 @Deprecated
    public  Pdf(String name,String probabilityDistributionFileName){
         timesForTransitions = generateTimesForTransitions(probabilityDistributionFileName);
         this.name=name;
         if(ECHO) System.out.println(toString() + ", times generated " + timesForTransitions.toString());

    }



public static void setEcho(boolean e){
    Pdf.ECHO=e;
}
    /**
     * Constructor para monotonas
     * @param timeOfInitOfTransition  Entero con el minuto en el que se puede iniciar una transición de comportamiento  monótono
     * @param slotOfTimeForTransition Minutos en el que se puede dar la transición en un monótono.
     * @deprecated Replaced by {@link #Pdf(String,int,int)} 
     */
@Deprecated
    public  Pdf(String name,String probabilityDistributionFileName, int timeOfInitOfTransition, int slotOfTimeForTransition){

        timesForTransitions = generateTimesForTransitions(probabilityDistributionFileName);
        this.name=name;
        monotonous=true;
        this.timeOfInitOfTransition= timeOfInitOfTransition;
        this.slotOfTimeForTransition= slotOfTimeForTransition;
        if(ECHO) System.out.println(toString() + ", times generated " + timesForTransitions.toString());
    }

 /**
     * Comprueba, según sea una función monotóna o no, si es momento para generar una nueva transición. Los autómatas
     * pueden llamar a este método desde "newTransitions" y comprobar si es momento de incluir cierta transición
     * en la lista de pendientes.
     *
     * @param state
     * @return
     */
    public boolean isTimeForNewTransition(SimState state){
        //si no esta en el intervalo de tiempo fijado (si se fijo) no es momento de generar transición.
        if(!isInTimeInterval( TimeTools.getCurrentMinuteForThisDay(state))) return false;
        //1 step por segundo, currentTime lleva minutos de ejecución del día
        double minutes=TimeTools.getCurrentMinuteForThisDay(state);
        //primer minuto del día, se fija que no se ha ejecutado el comportamiento monotono ni no monotono en el día
        if(minutes==0 && monotonousExectutedToday) {
            monotonousExectutedToday=false;
            lastTransition=0;
            if(ECHO)  System.out.println(toString() + ", New Day!");
        }
        if( isMonotonous() ) return isTimeForNewTransitionInMonotonous(state, minutes);
        if( !isMonotonous()) return isTimeForNewTransitionInNonMonotonousSimState(state,minutes);
        return false;
    }

    /**La función de comportamientos no monotonos produce tiempos a los que hay que sumar el momento de la última transición. Se compara
     * con la lista para saber si es el momento de iniciar una nueva transición. Se le pasa el número de minutos de ejecución en el día.
     */
    protected boolean isTimeForNewTransitionInNonMonotonousSimState(SimState state, double minutes){
        if( (nextTransition + lastTransition) == minutes ){
          nextTransition=generateNextTransition();
          //almacenar el  momento de la última transición
          lastTransition = minutes;
          if(ECHO) System.out.println(toString() + ", time for new transition in minute " + minutes + ", " + TimeTools.getTimeOfSimulation(state));
          return true;
        }


        else return false;
   }

         /**La función de comportamientos  monotonos produce tiempos a los que hay que sumar el tiempo en el que se puede iniciar la transición.
          * Además, devuelve true si se ha pasado el tiempo en el que se debía iniciar la actividad y no se produjo
         */
     protected boolean isTimeForNewTransitionInMonotonous( SimState state, double minutes){
        if(!monotonousExectutedToday && ( ((nextTransition + timeOfInitOfTransition) == minutes) || (timeOfInitOfTransition+slotOfTimeForTransition == minutes) )  ){
            nextTransition=generateNextTransition();
            //marcar que ya ha habido una transición en el día
            monotonousExectutedToday=true;
            if(ECHO) System.out.println(toString() + ", time for new transition in " + minutes +  ", " + TimeTools.getTimeOfSimulation(state));
            return true;
        }
        return false;
    }


    /**
     * Comprueba, según sea una función monotóna o no, si es momento para generar una nueva transición. Los autómatas
     * pueden llamar a este método desde "newTransitions" y comprobar si es momento de incluir cierta transición
     * en la lista de pendientes.
     *
     * @param state
     * @deprecated Replaced by {@link #isTimeForNewTransition(SimState)} 
     * @return
     */
    @Deprecated
    public boolean timeForNewTransition(SimState state){
        //si no esta en el intervalo de tiempo fijado (si se fijo) no es momento de generar transición.
        if(!isInTimeInterval( TimeTools.getCurrentMinuteForThisDay(state))) return false;
        //1 step por segundo, currentTime lleva minutos de ejecución del día
        double minutes=TimeTools.getCurrentMinuteForThisDay(state);
        //primer minuto del día, se fija que no se ha ejecutado el comportamiento monotono ni no monotono en el día
        if(minutes==0 && monotonousExectutedToday) {
            monotonousExectutedToday=false;
            lastTransition=0;
            if(ECHO)  System.out.println(toString() + ", New Day!");
        }
        if( isMonotonous() ) return timeForNewTransitionInMonotonous(state, minutes);
        if( !isMonotonous()) return timeForNewTransitionInNonMonotonousSimState(state,minutes);
        return false;
    }

    /**La función de comportamientos no monotonos produce tiempos a los que hay que sumar el momento de la última transición. Se compara
     * con la lista para saber si es el momento de iniciar una nueva transición. Se le pasa el número de minutos de ejecución en el día.
     * @deprecated Replaced by {@link #isTimeForNewTransitionInNonMonotonousSimState(SimState,double)} 
     */
    @Deprecated
    protected boolean timeForNewTransitionInNonMonotonousSimState(SimState state, double minutes){
        if( (timesForTransitions.getFirst() + lastTransition) == minutes ){
          timesForTransitions.removeFirst();
          //almacenar el  momento de la última transición
          lastTransition = minutes;
          if(ECHO) System.out.println(toString() + ", time for new transition in minute " + minutes + ", " + TimeTools.getTimeOfSimulation(state));
          return true;
        }


        else return false;
   }

         /**La función de comportamientos  monotonos produce tiempos a los que hay que sumar el tiempo en el que se puede iniciar la transición.
          * Además, devuelve true si se ha pasado el tiempo en el que se debía iniciar la actividad y no se produjo
          * @deprecated Replaced by {@link #isTimeForNewTransitionInMonotonous(SimState,double)} 
         */
    @Deprecated
     protected boolean timeForNewTransitionInMonotonous( SimState state, double minutes){
        if(!monotonousExectutedToday && ( ((timesForTransitions.getFirst() + timeOfInitOfTransition) == minutes) || (timeOfInitOfTransition+slotOfTimeForTransition == minutes) )  ){
            timesForTransitions.removeFirst();
            //marcar que ya ha habido una transición en el día
            monotonousExectutedToday=true;
            if(ECHO) System.out.println(toString() + ", time for new transition in " + minutes +  ", " + TimeTools.getTimeOfSimulation(state));
            return true;
        }
        return false;
    }


    public boolean isMonotonous(){
        return monotonous;
    }

    /**
     * Fija un intervalo para que sea posible dar una transición. Si alguno se pasa a -1 se desactiva intervalo.
     * También borra de la lista de tiempos generados los instantes que no entren dentro del intervalo especificado.
     * @param hourA hora  inicio
     * @param minA minuto  inicio
     * @param hourB hora fin
     * @param minB minuto fin
     */
    @Deprecated
    public void setTimeInterval(int hourA,int minA, int hourB, int minB){
        int a= hourA*60+minA;
        int b= hourB*60+minB;
        if(a>0 && b>0){
            this.timeInterval=new MutableInt2D();
            timeInterval.setX(a);
            timeInterval.setY(b);
            //borrar los tiempos generados que no esten en el intervalo especificado
            ArrayList<Integer> timesToBeRemoved= new ArrayList<Integer>();
            for(Integer time: timesForTransitions){
                if(!isInTimeInterval(time)) timesToBeRemoved.add(time);
            }
            timesForTransitions.removeAll(timesToBeRemoved);
             if(ECHO) System.out.println(toString() + ", times generated after removing the times out of the time interval fixed " + timesForTransitions.toString());
        }
        else {
               timeInterval=null;
        }

    }

    /**
     * Comprobar si estas en el intervalo pasado como parámetro
     * @param state
     * @return
     */
    private boolean isInTimeInterval(double min){

        if(timeInterval==null) return true;
        //System.out.println("current: " + min + " " + this.timeInterval.x +  " to " +  this.timeInterval.y );
        if(min> this.timeInterval.x && min<this.timeInterval.y){
            return true;
        }

        else return false;

    }


   public String toString(){
        if( isMonotonous() ) return name + ".PDF.monotonous" ;
        else  return  name + ".PDF.nonMonotonous. "  ;
    }   
   
   /**
    * Convierte los valores obtenidos de las PDFs a minutos.
    * @param n
    * @return 1 si el valor es menor que 1 minuto.
    */
   protected int toMinute(double n){
       int i=(int)n;
       if (i==0){
           return 1;
       }
       else {
           return i;
       }
   }
   
   /**
    * Generates a new transition value using the appropiate Pdf.
    * @return 
    */
   protected abstract int generateNextTransition();
   
       /**
        * Cargar una lista con tiempos a los que lanzar transiciones
     * @param filename
     * @return
     */
   @Deprecated
    private LinkedList<Integer> generateTimesForTransitions(String filename) {

        LinkedList <Integer> l = new LinkedList<Integer>();

        try {
            //Get function from file
            BufferedReader bf = new BufferedReader(new FileReader(filename));
            String function = bf.readLine();


            if (function.equals("0")){
                l=new LinkedList();
                l.add(-1);
                return l;
            }
          
            REXP eval = RInterface.getInstance().processCommand(function);

            //To Integer List
            for (double d: eval.asDoubleArray()) {
                int i=(int)d;
                if (i==0){
                    i=1;
                }
                if (i<1440){
                    l.add(i);
                }

            }

        } catch (IOException ex) {
            System.err.println(ex.getStackTrace());
        }

        return l;
    }


}
