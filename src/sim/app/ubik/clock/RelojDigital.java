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

import java.awt.*;

public class RelojDigital extends Panel implements TimeListener {
    private Etiqueta horaActual;
    private Color fondo;    

    public RelojDigital() { 
        // Cargamos el parametro que nos indica el color de fondo que
        // queremos para el reloj, si no se especifica o no se indica
        // correctamente, fijamos uno por defecto
        // Lo adecuado, en caso de presentar el applet sobre una pagina
        // de un navegador es que este parametro coincida con el que
        // se especifique para el color de Background de esa pagina
        String Fondo = null;
        if( Fondo == null )
             fondo = Color.lightGray;
        else
            {
            try {
                fondo = new Color( Integer.parseInt( Fondo,16 ) );
            } catch( NumberFormatException e ) {
                fondo = Color.lightGray;
                }
            }
        setBackground( fondo );

        // Creamos un objeto Etiqueta, para que nuestro reloj aparezca
        // con un texto decente y bonito
        horaActual = new Etiqueta();
        horaActual.setFont( "Helvetica",Font.PLAIN,18 );
        horaActual.setColor( new Color( 128,0,0 ) );
        horaActual.setAlto( 25 );
        horaActual.setAncho( 175 );
        horaActual.setSombra( Etiqueta.TextoHundido );
        horaActual.setBorde( true );
        add( horaActual );

        }   

    @Override
	public void paint( Graphics g ) {
        g.setColor( fondo );
        g.fillRect( 0,0,size().width,size().height );
        }

    public Etiqueta getHoraActual() {
        return horaActual;
    }

    public void setHoraActual(String hora) {
        horaActual.setText(hora);
    }

    public void updateHoraActual(String hora) {
        horaActual.updateText(hora);
    }

    public String getHora() {
        return horaActual.getText();
    }

	@Override
	public void time(int year, int month, int day, int hour, int min, int sec) {
		String hora = "Dia " + day + " - ";
        if (hour < 10) {
            hora += "0";
        }
        hora += hour + ":";
        if (min < 10) {
            hora += "0";
        }
        hora += min + ":";
        if (sec < 10) {
            hora += "0";
        }
        hora += sec;
		setHoraActual(hora);
        updateHoraActual(hora);
	}

	@Override
	public void time(long time) {
		// TODO Auto-generated method stub
		
	}

}
      
