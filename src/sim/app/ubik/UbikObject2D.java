/**
 * 
 */
package sim.app.ubik;

import java.awt.Shape;

/**
 * Interfaz que debe implementar toda clase que represente algun objeto en el mapa
 * @author rpax
 *
 */
public interface UbikObject2D {
	/**
	 * 
	 * @return the shape representing this object
	 */
	public Shape getShape();
	/**
	 * 
	 * @return the approximation radio of this object.
	 */
	public int getApproximationRadio();
}
