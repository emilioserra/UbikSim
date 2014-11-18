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

public class Etiqueta extends Panel {
    private String texto = "";
    private Font font = null;
    private FontMetrics metrica = null;
    private Color color = Color.black;
    private int ancho = 0;
    private int alto = 0;
    private int sombra = 0;
    private boolean borde = false;
   
    // Declaramos las variables que indican como aparecera el texto en
    // pantalla, para ello las hacemos "public" para que sean visibles
    // a otras clases, "static" para que la compartan todos los objetos
    // de la clase y "final" porque son valores constantes, que no van
    // a variar de ninguna de las maneras
    public static final int TextoNormal = 0;
    public static final int TextoResaltado = 1;
    public static final int TextoHundido = 2;
      
    // Constructor basico, con una cadena vacia
    public Etiqueta() { 
        this( "" ); 
        }
   

    // Constructor de conveniencia al que le pasamos una cadena de texto
    // que se presentara con los valores de defecto
    public Etiqueta( String Texto ) { 
        setText( Texto ); 
        }
   

    // Constructor de conveniencia al que le pasamos todos los datos
    // para que pinte el texto como nostros queremos
    // Tiene la particularidad de que lo podemos utilizar como plantilla
    // para crear otros objetos que sean iguales a uno que ya tengamos,
    // porque le pasamos este nuestro objeto y nos devuelve otro con
    // las mismas caracteristicas
    // Solo fijamos las caracteristicas, no indicamos el texto a
    // presentar
    public Etiqueta( Etiqueta plantilla ) { 
        copiaPlantilla( plantilla ); 
        }
   

    // Constructor de conveniencia completo, en el que le decimos
    // el texto que debe aparecer y las caracteristicas con que
    // debe presentarlo en pantalla
    public Etiqueta( String Texto,Etiqueta plantilla ) {
        copiaPlantilla( plantilla );
        setText( Texto );
        }
   

    // Metodo que pasa los valores de las caracteristicas con que
    // queremos pintar, a los atributos del objeto Texto que
    // vamos a hacer aparecer en pantalla, crea un objeto a partir
    // de otro
    public void copiaPlantilla( Etiqueta plantilla ) {
        texto = plantilla.texto;
        font = plantilla.font;
        metrica = plantilla.metrica;
        color = plantilla.color;
        ancho = plantilla.ancho;
        sombra = plantilla.sombra;
        borde = plantilla.borde;
        }
   

    // Nos aseguramos de que se seleccione una fuente para pintar los
    // caracteres. Si no se indica una, la creamos nosotros de la 
    // forma mas sencilla posible
    public void checkFont() {
        if( font == null ) 
            font = new Font( "Helvetica",Font.PLAIN,12 );
        }


    public void setText( String Texto ) { 
        texto = Texto; 
        }

    // Fija la cadena de texto que se va a presentar, asegurandose
    // de que se indica con que font de caracteres queremos hacerlo,
    // luego copia el texto en un miembro local y hace que el panel
    // se repinte
    public void updateText( String Texto ) {
        checkFont();
        texto = Texto;
        repaint();
        }
   

    // Fijamos una nueva fuente de caracteres para pintar el texto
    public void setFont( String nombre,int estilo,int tam ) {
        font = new Font( nombre,estilo,tam );  
        }
   

    // Fijamos el ancho del rectangulo en donde vamos a inscribir el
    // texto
    public void setAncho( int Ancho ) { 
        ancho = Ancho; 
        }


    // Fijamos la altura del rectangulo en donde vamos a inscribir el
    // texto
    public void setAlto( int Alto ) { 
        alto = Alto; 
        }


    // Fijamos el color con que queremos presentar el texto en pantalla
    public void setColor( Color cColor ) { 
        color = cColor; 
        }
   

    // Fijamos los pixels de desplazamiento que habra respecto de los
    // dos texto que se escriben para dar el efecto de sombra.
    // Para conseguir este efecto, simplemente pintamos el texto con el
    // color seleccionado para la sombra y luego lo volvemos a pintar
    // con el color del texto y desplazado tantos pixels como indique
    // este paramentro
    public void setSombra( int Sombra ) { 
        sombra = Sombra; 
        }


    // Fija el tamano del borde del rectangulo que circunscribe al
    // texto que estamos presentando en pantalla
    public void setBorde( boolean Borde ) { 
        borde = Borde; 
        } 
   

    // Funciones de recuperacion, equivalentes a las del grupo 'set',
    // utilizadas para recuperar los atributos actueles fijados para
    // el texto que actualmente se presenta en pantalla
    public String getText() { 
        return( texto ); 
        } 

    @Override
	public Font getFont() { 
        return( font ); 
        }

    public int getAncho() { 
        return( ancho ); 
        }

    public Color getColor() { 
        return( color ); 
        }

    public int getSombra() { 
        return( sombra ); 
        }

    public boolean getBorde() { 
        return( borde ); 
        }
   

    // Este metodo se llama automaticamente cuando el objeto aparece
    // en pantalla por primera vez o cuando se ve expuesto de nuevo,
    // tras haber estado tapado por otra ventana
    @Override
	public void paint( Graphics g ) { 
        update( g ); 
        }
   

    // Este metodo es llamado por el propio objeto para mostrar
    // en pantalla algo que haya cambiado o porque alguien lo llame
    // directamente, como en el caso de update
    @Override
	public void update( Graphics g ) {
       // Borramos todo el area que va a ocupar el texto
       Color color = g.getColor();      
       g.clearRect( 0,0,bounds().width,bounds().height );
       // Si va a llevar borde el rectangulo que delimita el espacio en
       // donde se va a pintar el texto, se lo ponemos
       if( borde )
           {
           g.setColor( Color.lightGray );
           g.draw3DRect( 0,0,bounds().width-1,bounds().height-1,false );
           }
       drawText( g );      
       g.setColor( color );
       }


    // Este es el metodo encargado de pintar realmente el texto en la
    // pantalla
    private void drawText( Graphics g ) {   
        // Si no hay texto que pintar, nos vamos
        if( texto == null ) 
            return;
      
        // Convertimos el texto en un array de caracteres
        char caracteres[] = texto.toCharArray();
        int Longitud = texto.length();
        Color colorant = g.getColor();
      
        // Fijamos la fuente con que queremos que aparezca el texto y
        // recogemos la informacion de esa fuente, porque necesitamos
        // conocer el ancho de cada uno de los caracteres
        g.setFont( font );
        FontMetrics metrica = getFontMetrics( font );
        int stringWidth = metrica.charsWidth( caracteres,0,Longitud );

        // Controlamos el efecto que queremos darle al texto, si
        // resaltado o hundido (hacia abajo), para conseguirlo
        // pintamos dos cadenas en diferente color
        if( sombra == TextoResaltado )
            {
            g.setColor( Color.white );
            g.drawChars( caracteres,0,Longitud,1,metrica.getHeight()-3 );
            }
        else if( sombra == TextoHundido )
            {
            g.setColor( Color.white );
            g.drawChars( caracteres,0,Longitud,3,metrica.getHeight()-1 );
            }
        // Aplicamos el color mas oscuro con un ligero desplazamiento
        // y recuperamos el color antiguo con que estabamos trabajando
        g.setColor( color );      
        g.drawChars( caracteres,0,Longitud,2,metrica.getHeight()-2 );
        g.setColor( colorant );
        }
   

    // Este metodo devuelve el tamno minimo deseable para el rectangulo
    // donde vamos a pintar el texto. Es importante porque nuestro
    // objeto no deja de ser mas que un Componente en el Layout, y el
    // gestor del layout a la hora de reconstruirlo, lo llama para
    // saber cual es el minimo tamano posible
    @Override
	public Dimension minimumSize() {  
        if( alto == 0 )
            {
            if( font != null )
                alto = font.getSize();
            else
                alto = 12;
            }
        if( ancho == 0 ) 
            ancho = 100;

        return( new Dimension( ancho+3,alto+3 ) );
        }
   
    // Con este metodo pasa lo mismo, el manejador del Layout lo llama
    // para saber cual es el tamano que ha fijado el creador del 
    // componente e intentar respetarlo (siempre que pueda)
    @Override
	public Dimension preferredSize() { 
        return( minimumSize() ); 
        }

    // Creamos un bordecito entre el texto y el borde del rectangulo
    // que lo circunscribe
    @Override
	public Insets insets() { 
        return( new Insets( 3,3,3,3 ) ); 
        }   
    }

//------------------------------------------ Final del fichero Etiqueta.java
