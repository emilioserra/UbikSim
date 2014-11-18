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
package sim.app.ubik;

import ec.util.MersenneTwisterFast;
import sim.engine.*;

import sim.util.*;

/**
 * Clase singleton
 */
public class MovementTools {

    /**
     * Devuelve a partir de una dirección (x,y=[-1,0,1]) un valor en radianes
     * -1 Si la dirección de avance no es correcta
     */

    protected int[][] degrees = new int[3][3];
    protected Int2D[] directions = new Int2D[8];
    protected Int2D[] fourDirections = new Int2D[4];
    protected Int2D[] advanceToTheRight = new Int2D[3];
    protected static MovementTools mt;

    private MovementTools(){
        fixDegrees();
        fixDirections();
        fixFourDirections();
        fixAdvanceToTheRight();
    }
    
  
 
    public static MovementTools getInstance() {
        if(mt==null) mt = new MovementTools();
        return mt;
 
    }

    private void fixAdvanceToTheRight() {
        advanceToTheRight[0] = new Int2D(1,0);//derecha
        advanceToTheRight[1] = new Int2D(1,-1);//subir, derecha        
        advanceToTheRight[2] = new Int2D(1,1);//bajar, derecha

    }
 

    


    private void fixDegrees(){
        //grados de una circnferencia en un array
        /*
         el 0,0 es la esquina inferior derecha de la circunferencia, así sumandole 1 a los valores de un vector director se obtiene el grado que le corresponde a una dirección
         * si la dirección va en formato x,y donde x e y son valores entre -1 y 1. Es decir, la dirección es un vector director:
         * */

        degrees[0][0]=225;
        degrees[0][1]=180;
        degrees[0][2]=135;
        degrees[1][0]= 270;
        degrees[1][1]=-1;  //centro de la circunferencia
        degrees[1][2]=90;
        degrees[2][0]=315;
        degrees[2][1]= 0;
        degrees[2][2]=45;
    }

    /**
     * Direcciones, en el sentido de las agujas del reloj. Se suma a una posición para moverte.
     */
    private void fixDirections(){
         directions[0] = new Int2D(0,-1);//subir
         directions[1] = new Int2D(1,-1);//subir, derecha
         directions[2] = new Int2D(1,0);//derecha
         directions[3] = new Int2D(1,1);//bajar, derecha
         directions[4] = new Int2D(0,1);//bajar
         directions[5] = new Int2D(-1,1);//bajar,izquierda
         directions[6] = new Int2D(-1,0);//izquierda
         directions[7] = new Int2D(-1,-1);//subir izquierda             
    }

    
    private void fixFourDirections(){
         fourDirections[0] = new Int2D(0,-1);//subir        
         fourDirections[1] = new Int2D(1,0);//derecha        
         fourDirections[2] = new Int2D(0,1);//bajar         
         fourDirections[3] = new Int2D(-1,0);//izquierda
         
    }
    
    public double directionToRadians(MutableInt2D m){
       return Math.toRadians(degrees[m.getX()+1][m.getY()+1]);
    }

    public double directionToRadians(Int2D m){
       return Math.toRadians(degrees[m.getX()+1][m.getY()+1]);
    }

    /**Devuelve el siguiente entero aleatorio entre dos valores, incluidos estos valores
    USA EL GENERADOR DE ALEATORIOS DE LA SIMULACION PARA QUE SE PUEDA REPRODUCIR*/
    public int randomBetweenValues(SimState state, int low, int upper) {
        return (low + state.random.nextInt(upper - low + 1));
    }
   
        /**Devuelve el siguiente entero aleatorio entre dos valores, incluidos estos valores*/ 
    public int randomBetweenValues(MersenneTwisterFast random, int low, int upper) {
        return (low + random.nextInt(upper - low + 1));
    }

    
    
    public double directionInRadians(int xOr, int yOr, int xDes, int yDes) {
        Int2D v = new Int2D((xDes - xOr),(yDes - yOr));
        double angle;
        if(v.getX() == 0) {
            if(v.getY() > 0) {
                angle = Math.PI/2.0;
            } else {
                angle = 3.0*Math.PI/2.0;
            }
        } else {
            angle = Math.atan(v.getY()/v.getX());
            if(xDes - xOr < 0)
                angle+=Math.PI;
        }
        angle = Math.atan2(v.getY(),v.getX());
        return angle;
    }

    /**
     * generar dirección desede cierta posición hasta cierta otra.
     */
    public Int2D generateDirectionToLocation(int xor, int yor, int xdest, int ydest) {
        int bestx = (xdest - xor);//si es 0, xfinal se queda asi

        if (bestx != 0) {
            bestx /= Math.abs(bestx);//1 o -1, osea, el signo de la diferencia entre el destino y el origen

        }
        int besty = (ydest - yor);
        if (besty != 0) {
            besty /= Math.abs(besty);//1 o -1, osea, el signo de la diferencia entre el destino y el origen

        }
        return new Int2D(bestx, besty);

    }
    
 public Int2D generateDirectionToLocation(Int2D origin, Int2D destiny) {
     return generateDirectionToLocation(origin.x, origin.y, destiny.x, destiny.y); 
 }

 public Int2D generateDirectionToLocation(MutableInt2D origin, Int2D destiny) {
     return generateDirectionToLocation(origin.x, origin.y, destiny.x, destiny.y);
 }
    /**
     * Genera una dirección aleatoria (de 8 posible) La dirección es un valor de -1 a 1 que se suma a cada coordenada para conseguir la nueva posicion.
     * No se considera nueva dirección la misma dirección que se estaba siguiendo ni quedarse quieto.
          
     */
    public Int2D generateRandomDirection(SimState state, Int2D lastDirection) {
      

        Int2D direction;
        do{
         direction = this.directions[this.randomBetweenValues(state, 0, 7)];
        }while(direction.equals(lastDirection));
        return direction;
    }
    
    public Int2D generateRandomDirection(SimState state) {      
        return this.directions[this.randomBetweenValues(state, 0, 7)];
    }
    
  
    


        /**
     * Genera una dirección aleatoria (de 4 posible) La dirección es un valor de -1 a 1 que se suma a cada coordenada para conseguir la nueva posicion.
     * Si lastDirection es null se genera una de las 4 posibles direcciones.
     * No se considera nueva dirección la misma dirección que se estaba siguiendo ni quedarse quieto.
     */
    public Int2D generateRandomDirectionBetweeenFour(SimState state, Int2D lastDirection) {

        Int2D direction;
         int indexDirection;
        do{
          indexDirection=this.randomBetweenValues(state, 0, 3);                  
         direction = this.fourDirections[indexDirection];
         //las direcciones arriba, abajo, izquierda y derecha son las posiciones pares del array y la 0.
        }while(direction.equals(lastDirection));
        return direction;

    }
    
    public Int2D generateRandomDirectionBetweeenFour(SimState state) {
        return fourDirections[this.randomBetweenValues(state, 0, 3)];       
    }
    
        
    public Int2D generateRandomDirectionBetweeenFour(MersenneTwisterFast random) {
        return fourDirections[this.randomBetweenValues(random, 0, 3)];      
    }
    
    public Int2D[] getFourDirections(){
        return this.fourDirections;
    }
     public Int2D[] getAllDirections(){
        return this.directions;
    }
      
        /**
         * Genera un array con todas las direcciones, empezando por la pasada como parámetro y seguido por las 7 direcciones restantes a la pasada.
         * Se ordenan por las que produzcan un avance al objetivo (al menos una coordenada se reduce).
         * Por ejemplo, si la primera posición es bajar (0,1) , las 2 siguiente posiciones llevarán de manera aleatoria bajar a la izquierda (-1,1) y bajar a la derecha (1,1). Las posiciones 3 y 4 llevarán aleatoriamente izquierda y derecha. La 5,6 restrocesos diagonales y la 7 retroceso
         * 4, 5 llevarán subir izquierda y subir derecha. Finalmente 6 llevará retroceder.
         * @param state
         * @param lastDirection
         * @return
         */
      public Int2D[] generateAdvanceDirection(SimState state, Int2D lastDirection){          
          int i=0;           
          //calculo que posición tiene la direccion pasada en el array de posiciones
          while(!(directions[i].x == lastDirection.x && directions[i].y==lastDirection.y)) i++;                 
          //random vale -1 o 1.
          Int2D[] result = new Int2D[8];
           result[0] = lastDirection;          
          //las direcciones de avance estan a 1 o -1 en el array, modulo 8 por si estoy en un extremo del array                    
          int[] indexs;          
          //System.out.println(i);                    
          indexs=indexDirection(state,i,1);
          result[1] =  this.directions[indexs[0]];
          result[2] =  this.directions[indexs[1]];
          //las direcciones de movimiento lateral al actual estan a -2 o +2
          indexs=indexDirection(state,i,2);
          result[3] = this.directions[indexs[0]];
          result[4] = this.directions[indexs[1]];
          //retrocesos laterales -3 o +3
           indexs=indexDirection(state,i,3);
          result[5] =  this.directions[indexs[0]];
          result[6] = this.directions[indexs[1]];
          //retroceso a -4 modulo 8    
           indexs=indexDirection(state,i,4);
          result[7] = this.directions[indexs[0]];
          
          return result;
          }
      /**
       * Devuelve un array con las 3 posiciones para avanzar hacia la derecha.
       * Util para el paso de planta a escaleras.
       * @return
       */
      public Int2D[] generateAdvanceToTheRightDirections(){          
         return advanceToTheRight;
          
          
      }
      
      /**
       * Dadas dos direcciones, devuelve true si la primera contraria a la segunda o produce un retroceso respecto a esta.
       * 
       */
      public boolean isOnlyABackDirection(SimState state, Int2D d1, Int2D d2 ){
          Int2D[] advanceDirections = this.generateAdvanceDirection(state, d1);
          //si es igual a una de las 3 primeras direcciones de avance es true.
         // System.out.println("first direction " + advanceDirections[0]);
          for(int i=advanceDirections.length-1;i>4;i--){
            //  System.out.println("Considering back direction " + i + " igual a " +  advanceDirections[i]);
              if(d2.equals(advanceDirections[i])) return true;
          }
          return false;
      }
      
       public boolean isNotAnAdvanceDirection(SimState state, Int2D d1, Int2D d2 ){
          Int2D[] advanceDirections = this.generateAdvanceDirection(state, d1);
          //si es igual a una de las 3 primeras direcciones de avance es true.
         // System.out.println("first direction " + advanceDirections[0]);
          for(int i=advanceDirections.length-1;i>2;i--){
            //  System.out.println("Considering back direction " + i + " igual a " +  advanceDirections[i]);
              if(d2.equals(advanceDirections[i])) return true;
          }
          return false;
      }
      
      
      
   
          
    
    /**
     * 
     * @param state
     * @param originalIndex indice del que se parte
     * @param gradoDeAvance con 1 sería direcciones de avance alternativas, con 2 laterales, con 3 retroceder en diagonal, con 4 sería retroceder
     * @return
     */
   
      private int[] indexDirection(SimState state, int originalIndex, int gradoDeAvance){
          int random = randomBetweenValues(state, 0, 1);                       
          if(random==0) random=-1;                
          //random vale -1 o 1, así se genera aleatoriamente una de las dos direccoines
          int index1 = (originalIndex + gradoDeAvance*random + 8) %8 ;          
          int index2 = (originalIndex - gradoDeAvance*random + 8) %8 ;
          int[] result = {index1,index2};
          return result;
  
          
      }
      
      /**
       * Dada una serie de vectores directores en un array de Int2D devuelve la suma de todos ellos
       * @return
       */
      public MutableInt2D sumOfDirections(Int2D vectors[]){
          MutableInt2D r = new MutableInt2D(0,0);
          for(int i=0;i<vectors.length;i++){
              Int2D v = vectors[i];
              //System.out.println("vector a sumar " + v);
              r.x += v.x;
              r.y +=v.y;
              
          }
          //saco el vector unitario dividiendo por el modulo
          if(r.x!=0 )r.x /= Math.abs(r.x); 
          if(r.y!=0 )r.y /= Math.abs(r.y);
          //System.out.println("SUMA DE VECTORES: " + r.x + " " + r.y);
          return r;
      }
      
      
      public Int2D inverseDirection(Int2D direction){
          return new Int2D(direction.x * -1 , direction.y * -1);
          
      }
    
      
              /**
     * True si la posición origen esta en un sitio vecino al sitio que se pasa como parametro.
     * Se pasa el grado de vecinidad, con 1 solo vecinos directos o m isma posicion, con 2 también indirectos
     *El calculo de vecinidad es restar las coordenadas, ponerlas en valor absoluto, y calcular el máximo de estos valores.
     * Un movimiento diagonal supone un avance de posición x e y, por lo que la vecinidad mínima es todos los posibles pasos diagonales, y los que quedan
               * en vertical u horizontal.
     * @return
     */
    public boolean isNeighboring( int xor, int yor, int xdest, int ydest, int degree) {    
       int distance = this.getDistance(xor, yor, xdest, ydest);
        return (distance <= degree);
    }
    
    /**
     * La distancia se calcula teniendo en cuenta que se pueden dar pasos diagonales
     * @param xor
     * @param yor
     * @param xdest
     * @param ydest
     * @return
     */
    public int getDistance( int xor, int yor, int xdest, int ydest){
        return   (int)Math.round(Math.sqrt((xor - xdest)*(xor - xdest) + (yor - ydest)*(yor - ydest)));
    }
    
        public int getDistanceForFourMovements( int xor, int yor, int xdest, int ydest){
        return   (Math.abs(xor - xdest) + Math.abs(yor - ydest));
    }
    


}