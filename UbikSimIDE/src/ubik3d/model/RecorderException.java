/*
 * Contact, UbikSimIDE has been developed by:
 * 
 * Juan A. Botía , juanbot@um.es
 * Pablo Campillo, pablocampillo@um.es
 * Francisco Campuzano, fjcampuzano@um.es
 * Emilio Serrano, emilioserra@um.es 
 * 
 * This file is part of UbikSimIDE and a modified version (on 10/02/2011) of 
 * Sweet Home 3D version 3.3, Copyright (c) 2005-2011 Emmanuel PUYBARET / eTeks.
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
package ubik3d.model;

/**
 * Exception thrown by methods that access to data in IO layer.
 * @author Emmanuel Puybaret
 */
public class RecorderException extends Exception {
  private static final long serialVersionUID = 1L;

  /**
   * Creates a default <code>RecorderException</code>.
   */
  public RecorderException() {
    super();
  }

  /**
   * Creates a <code>RecorderException</code> from its message.
   */
  public RecorderException(String message) {
    super(message);
  }

  /**
   * Creates a <code>RecorderException</code> with its message 
   * and the internal cause that initiated this exception.
   */
  public RecorderException(String message, Throwable cause) {
    super(message, cause);
  }
}
