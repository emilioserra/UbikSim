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
package ubik3d.swing;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.util.List;

import ubik3d.model.Home;
import ubik3d.model.Selectable;


/**
 * A transferable class that manages the transfer of a list of items in a home.
 * @author Emmanuel Puybaret
 */
public class HomeTransferableList implements Transferable {
  public final static DataFlavor HOME_FLAVOR;
  
  static {
    try {
      // Create HomeTransferableList data flavor
      String homeFlavorMimeType = 
        DataFlavor.javaJVMLocalObjectMimeType
        + ";class=" + HomeTransferableList.class.getName();
      HOME_FLAVOR = new DataFlavor(homeFlavorMimeType);
    } catch (ClassNotFoundException ex) {
      throw new RuntimeException(ex);
    }
  }
  
  // Stores a copy of the transfered items
  private final List<Selectable> transferedItems;

  /**
   * Creates a transferable list of a copy of <code>items</code>.
   */
  public HomeTransferableList(List<? extends Selectable> items) {
    this.transferedItems = Home.duplicate(items);
  }

  /**
   * Returns a copy of the transfered items list.
   */
  public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
    if (flavor.equals(HOME_FLAVOR)) {
      return Home.duplicate(this.transferedItems);
    } else {
      throw new UnsupportedFlavorException(flavor);
    }
  }

  /**
   * Returns the {@link #HOME_FLAVOR data flavor} of the transfered data 
   * of this transferable object.
   */
  public DataFlavor [] getTransferDataFlavors() {
    return new DataFlavor [] {HOME_FLAVOR};
  }

  /**
   * Returns <code>true</code> if <code>flavor</code> is 
   * {@link #HOME_FLAVOR HOME_FLAVOR}.
   */
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return HOME_FLAVOR.equals(flavor);
  }
}