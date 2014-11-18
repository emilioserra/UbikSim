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
package sim.app.ubik.clock;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;

public class UbikClock implements Steppable, Stoppable, TimeSubject {

    //private RelojDigital reloj;
    protected Stoppable stoppable;
    private int segundos = 0;
    private int minutos = 0;
    private int horas = 0;
    private int dias = 1;
    private int meses = 1;
    private int anyos = 2012;
    private long timestamp=new Long("1262300400000") + dias * 84600000 + horas * 3600000 + minutos * 60000 + segundos * 1000;
    private Map<Integer, List<TimeListener>> puntualListeners;
    private Map<Integer, List<TimeListener>> dailyListeners;
    private List<TimeListener> secondListeners;

    public UbikClock() {
        puntualListeners = new HashMap<Integer, List<TimeListener>>();
        dailyListeners = new HashMap<Integer, List<TimeListener>>();
        secondListeners = new LinkedList<TimeListener>();
    }
    
    @Override
    public void step(SimState state) {
        segundos++;
        if (segundos == 60) {
            segundos = 0;
            minutos++;
        }
        if (minutos == 60) {
            minutos = 0;
            horas++;
        }
        if (horas == 24) {
            horas = 0;
            dias++;
        }
        
        timestamp+=1000;
        
        notify(anyos,meses,dias,horas,minutos,segundos);
        notify(timestamp);
        
        List<TimeListener> list = dailyListeners.get(horas *60 + minutos);
        if ((list != null)&&(segundos==0)) {
            for (TimeListener tl : list) {
                tl.time(anyos,meses,dias, horas, minutos, segundos);
            }
        }
        list = puntualListeners.get(dias * 24 * 60 + horas * 60 + minutos);
        if ((list != null)&&(segundos==0)) {
            for (TimeListener tl : list) {
                tl.time(anyos,meses,dias, horas, minutos, segundos);
            }
        }
  
    }

    @Override
    public void stop() {
    	for(List<TimeListener> listeners: puntualListeners.values()) {
    		listeners.clear();
    	}
    	puntualListeners = null;
    	for(List<TimeListener> listeners: dailyListeners.values()) {
    		listeners.clear();
    	}
    	dailyListeners = null;
    	secondListeners.clear();
    	stoppable = null;
    	
    }

    public void fixStoppable(Schedule schedule) {
        schedule.scheduleRepeating(this);
    }

    /**
     * Añade un listener que va a ser invocado en momentos puntuales (día, hora y minuto)
     * @param tl
     * @param day
     * @param hour
     * @param min 
     */
    public void addPuntualTimeListener(TimeListener tl, int day, int hour, int min) {
        int time = day * 24 * 60 + hour * 60 + min;
        List<TimeListener> list = puntualListeners.get(time);
        if (list == null) {
            list = new ArrayList<TimeListener>();
        }
        list.add(tl);
        puntualListeners.put(time, list);
    }

    /**
     * Añade un listener que va a ser invocado en momentos puntuales (hora y minuto)
     * @param tl
     * @param hour
     * @param min 
     */
    public void addDailyTimeListener(TimeListener tl, int hour, int min) {
        int time = hour * 60 + min;
        List<TimeListener> list = dailyListeners.get(time);
        if (list == null) {
            list = new ArrayList<TimeListener>();
        }
        list.add(tl);
        dailyListeners.put(time, list);
    }
    
    /**
     * Añade un TimeListener que va a ser invocado cada segundo
     * @param listener 
     */
    public void addTimeListener(TimeListener listener) {
        secondListeners.add(listener);
    }

    public Date getDate() {
        return new Date(timestamp);
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Notifica a todos los listeners con valores individuales
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param min
     * @param sec 
     */
    public void notify(int year, int month, int day, int hour, int min, int sec) {        
        for (TimeListener tl : secondListeners) {
            tl.time(year,month,day, hour, min, sec);
        }
    }

    /**
     * Notifica a todos los listeners con un valor de timestamp
     * @param time 
     */
    public void notify(long time) {
        for (TimeListener tl : secondListeners) {
            tl.time(new Long("1262300400000") + dias * 84600000 + horas * 3600000 + minutos * 60000 + segundos * 1000);
        }
    }

    /**
     * Borra un listener
     * @param listener 
     */
    public void removeTimeListener(TimeListener listener) {
        secondListeners.remove(listener);
    }
    
    public void setDate(long date) {
    	timestamp = date;
    }
    
    public void setDate(int year,int month, int day,int hour,int minute, int second) {
    	this.anyos=year;
        this.meses=month;
        this.dias=day;
        this.horas=hour;
        this.minutos=minute;
        this.segundos=second;
        Date d=new Date(year,month,day,hour,minute, second);
        this.timestamp=d.getTime();
    }
}