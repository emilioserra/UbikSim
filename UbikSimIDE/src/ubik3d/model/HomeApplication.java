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
import java.util.Collections;
import java.util.List;

/**
 * Application managing a list of homes displayed at screen.
 * @author Emmanuel Puybaret
 */
public abstract class HomeApplication {
  private List<Home> homes = new ArrayList<Home>();
  private final CollectionChangeSupport<Home> homesChangeSupport = 
                             new CollectionChangeSupport<Home>(this);

  /**
   * Adds the home <code>listener</code> in parameter to this application.
   */
  public void addHomesListener(CollectionListener<Home> listener) {
    this.homesChangeSupport.addCollectionListener(listener);
  }
  
  /**
   * Removes the home <code>listener</code> in parameter from this application.
   */
  public void removeHomesListener(CollectionListener<Home> listener) {
    this.homesChangeSupport.removeCollectionListener(listener);
  } 
  
  /**
   * Returns a new home.
   * @return a new home with wall heights equal to the one in user preferences.
   * @since 2.2
   */
  public Home createHome() {
    return new Home(getUserPreferences().getNewWallHeight());
  }

  /**
   * Returns an unmodifiable collection of the homes of this application.
   */
  public List<Home> getHomes() {
    return Collections.unmodifiableList(this.homes);
  }

  /**
   * Adds a given <code>home</code> to the homes list of this application.
   * Once the <code>home</code> is added, home listeners added 
   * to this application will receive a
   * {@link CollectionListener#collectionChanged(CollectionEvent) collectionChanged}
   * notification, with an {@link CollectionEvent#getType() event type} 
   * equal to {@link CollectionEvent.Type#ADD ADD}. 
   */
  public void addHome(Home home) {
    this.homes = new ArrayList<Home>(this.homes);
    this.homes.add(home);
    this.homesChangeSupport.fireCollectionChanged(home, this.homes.size() - 1, CollectionEvent.Type.ADD);
  }

  /**
   * Removes a given <code>home</code> from the homes list  of this application.
   * Once the <code>home</code> is removed, home listeners added 
   * to this application will receive a
   * {@link CollectionListener#collectionChanged(CollectionEvent) collectionChanged}
   * notification, with an {@link CollectionEvent#getType() event type} 
   * equal to {@link CollectionEvent.Type#DELETE DELETE}.
   */
  public void deleteHome(Home home) {
    int index = this.homes.indexOf(home);
    if (index != -1) {
      this.homes = new ArrayList<Home>(this.homes);
      this.homes.remove(index);
      this.homesChangeSupport.fireCollectionChanged(home, index, CollectionEvent.Type.DELETE);
    }
  }

  /**
   * Returns the default recorder able to write and read homes.
   */
  public abstract HomeRecorder getHomeRecorder();
  
  /**
   * Returns a recorder of a given <code>type</code> able to write and read homes.
   * Subclasses may override this method to return a recorder matching <code>type</code>. 
   * @param type  a hint for the application to choose the returned recorder.
   * @return the default recorder able to write and read homes.
   * @since 1.8
   */
  public HomeRecorder getHomeRecorder(HomeRecorder.Type type) {
    return getHomeRecorder();
  }
  
  /**
   * Returns user preferences.
   */
  public abstract UserPreferences getUserPreferences();
  
  /**
   * Returns the name of this application. Default implementation returns <i>Sweet Home 3D</i>. 
   * @since 1.6
   */
  public String getName() {
    return "Sweet Home 3D";
  }
  
  /**
   * Returns information about the version of this application.
   * Default implementation returns an empty string.
   * @since 1.6
   */
  public String getVersion() {
    return "";
  }
}
