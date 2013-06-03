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

import java.util.EventObject;

/**
 * Type of event notified when an item is added or deleted from a list.
 * <code>T</code> is the type of item stored in the collection.
 * @author Emmanuel Puybaret
 */
public class CollectionEvent<T> extends EventObject {
  /**
   * The type of change in the collection.
   */
  public enum Type {ADD, DELETE}

  private final T    item;
  private final int  index;
  private final Type type;

  /**
   * Creates an event for an item that has no index. 
   */
  public CollectionEvent(Object source, T item, Type type) {
    this(source, item, -1, type);
  }

  /**
   * Creates an event for an item with its index. 
   */
  public CollectionEvent(Object source, T item, int index, Type type) {
    super(source);
    this.item = item;
    this.index = index;
    this.type =  type;
  }
  
  /**
   * Returns the added or deleted item.
   */
  public T getItem() {
    return this.item;
  }

  /**
   * Returns the index of the item in collection or -1 if this index is unknown.
   */
  public int getIndex() {
    return this.index;
  }

  /**
   * Returns the type of event. 
   */
  public Type getType() {
    return this.type;
  }
}
