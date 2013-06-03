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

import java.util.ArrayList;
import java.util.List;

/**
 * A helper class for {@link CollectionListener CollectionListener} management.
 * <code>T</code> is the type of item stored in the collection.
 * @author Emmanuel Puybaret
 */
public class CollectionChangeSupport<T> {
  private final Object                      source;
  private final List<CollectionListener<T>> collectionListeners;
  
  /**
   * Creates a collection change support.
   */
  public CollectionChangeSupport(Object source) {
    this.source = source;
    this.collectionListeners = new ArrayList<CollectionListener<T>>(5);
  }
  
  /**
   * Adds the <code>listener</code> in parameter to the list of listeners that may be notified.
   */
  public void addCollectionListener(CollectionListener<T> listener) {
    this.collectionListeners.add(listener);
  }

  /**
   * Removes the <code>listener</code> in parameter to the list of listeners that may be notified.
   */
  public void removeCollectionListener(CollectionListener<T> listener) {
    this.collectionListeners.remove(listener);
  }

  /**
   * Fires a collection event about <code>item</code>.
   */
  public void fireCollectionChanged(T item, CollectionEvent.Type eventType) {
    fireCollectionChanged(item, -1, eventType);
  }

  /**
   * Fires a collection event about <code>item</code> at a given <code>index</code>.
   */
  @SuppressWarnings("unchecked")
  public void fireCollectionChanged(T item, int index, 
                                    CollectionEvent.Type eventType) {
    if (!this.collectionListeners.isEmpty()) {
      CollectionEvent<T> furnitureEvent = 
          new CollectionEvent<T>(this.source, item, index, eventType);
      // Work on a copy of collectionListeners to ensure a listener 
      // can modify safely listeners list
      CollectionListener<T> [] listeners = this.collectionListeners.
        toArray(new CollectionListener [this.collectionListeners.size()]);
      for (CollectionListener<T> listener : listeners) {
        listener.collectionChanged(furnitureEvent);
      }
    }
  }
}
