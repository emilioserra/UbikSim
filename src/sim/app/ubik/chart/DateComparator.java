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

import java.util.Comparator;
import java.util.Date;
import java.util.Calendar;



public class DateComparator implements Comparator{

   private static Calendar calendario = Calendar.getInstance();


   private static int getMonth(String month){
        if (month.equals("Jan")){
            return Calendar.JANUARY;
        }
        else if (month.equals("Feb")){
            return Calendar.FEBRUARY;
        }
        else if (month.equals("Mar")){
            return Calendar.MARCH;
        }
        else if (month.equals("Apr")){
            return Calendar.APRIL;
        }
        else if (month.equals("May")){
            return Calendar.MAY;
        }
        else if (month.equals("Jun")){
            return Calendar.JUNE;
        }
        else if (month.equals("Jul")){
            return Calendar.JULY;
        }
        else if (month.equals("Aug")){
            return Calendar.AUGUST;
        }
        else if (month.equals("Sep")){
            return Calendar.SEPTEMBER;
        }
        else if (month.equals("Oct")){
            return Calendar.OCTOBER;
        }
        else if (month.equals("Nov")){
            return Calendar.NOVEMBER;
        }
        else if (month.equals("Dec")){
            return Calendar.DECEMBER;
        }
        else {
            return 0;
        }
    }

    private static Date getDate(String cadena){
        calendario.set(Integer.parseInt(cadena.substring(19,23)),getMonth(cadena.substring(3,6)),Integer.parseInt(cadena.substring(6,8)),Integer.parseInt(cadena.substring(8,10)),Integer.parseInt(cadena.substring(11,13)),Integer.parseInt(cadena.substring(14,16)));
        return calendario.getTime();
    }

    @Override
	public int compare(Object obj1, Object obj2) {
        String s1 = (String)obj1;
        String s2 = (String)obj2;
        Date d1 = getDate(s1.substring(s1.indexOf(",")+1));
        Date d2 = getDate(s2.substring(s2.indexOf(",")+1));
        return d1.compareTo(d2);
    }
    
}
