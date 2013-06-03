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
package ubik3d.j3d;

import ubik3d.model.Home;
import ubik3d.model.HomePieceOfFurniture;
import ubik3d.model.Room;
import ubik3d.model.Selectable;
import ubik3d.model.Wall;
import ubik3d.viewcontroller.Object3DFactory;

/**
 * A factory able to create instances of {@link Object3DBranch Object3DBranch} class.
 * @author Emmanuel Puybaret
 */
public class Object3DBranchFactory implements Object3DFactory {
  /**
   * Returns the 3D object matching a given <code>item</code>.
   */
  public Object createObject3D(Home home, Selectable item, boolean waitForLoading) {
    if (item instanceof HomePieceOfFurniture) {
      return new HomePieceOfFurniture3D((HomePieceOfFurniture)item, home, true, waitForLoading);
    } else if (item instanceof Wall) {
      return new Wall3D((Wall)item, home, true, waitForLoading);
    } else if (item instanceof Room) {
      return new Room3D((Room)item, home, false, false, waitForLoading);
    } else {
      throw new IllegalArgumentException("Can't create 3D object for an item of class " + item.getClass());
    }  
  }
}
