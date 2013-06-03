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
 * Homes recorder.
 * @author Emmanuel Puybaret
 */
public interface HomeRecorder {
  /**
   * Recorder type used as a hint to select a home recorder.
   * @since 1.8
   */
  public enum Type {
    /**
     * The default recorder type.
     */
    DEFAULT, 
    /**
     * A recorder type able to compress home data.
     */
    COMPRESSED}
  
  /**
   * Writes <code>home</code> data.
   * @param home  the home to write.
   * @param name  the name of the resource in which the home will be written. 
   */
  public void writeHome(Home home, String name) throws RecorderException;
  
  /**
   * Returns a home instance read from its <code>name</code>.
   * @param name  the name of the resource from which the home will be read. 
   */
  public Home readHome(String name) throws RecorderException;

  /**
   * Returns <code>true</code> if the home with a given <code>name</code>
   * exists.
   * @param name the name of the resource to check
   */
  public boolean exists(String name) throws RecorderException;
}
