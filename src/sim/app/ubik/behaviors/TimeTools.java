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

import sim.app.ubik.Ubik;
import sim.engine.SimState;

/**
 *UBISKSIM CODE
 * Clase con métodos de utilidad referentes al tiempo.
 */
public class TimeTools {
  /**
     * Obtener tiempo actual en días, horas, minutos y segundos (usado en el ECHO)
     */
    public  static String getTimeOfSimulation(SimState state){
        double totalSeconds= ((Ubik) state).schedule.getTime();
        int days = (int) (totalSeconds / (24*60*60));
        int totalSecondsToday = (int) ( totalSeconds % (24*60*60));
        int hours = totalSecondsToday / (60*60);
        int minutes = (totalSecondsToday % (60*60)) / (60);
        int seconds = ((totalSecondsToday % (60*60)) % (60));
         return "[day " + days + ", " + hours + ":" + minutes + ":" + seconds +"]";
        }

      /**
     * Obtener tiempo actual en minutos que han pasado de un día
     */
    public  static Double getCurrentMinuteForThisDay(SimState state){
        double minutes=(((Ubik) state).schedule.getTime() / 60 ); //cada step es un segundo
        Double d= minutes % 1440; //1440 son los minutos de un día
        return d;
    }

    /**Pasa de hora a minutos*/
    public static int toMinutes(int hour,int minute){
        return hour*60 + minute;
    }


}
