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
package ubik3d;

import ubik3d.model.Home;
import ubik3d.model.HomeApplication;
import ubik3d.plugin.PluginManager;
import ubik3d.viewcontroller.ContentManager;
import ubik3d.viewcontroller.Controller;
import ubik3d.viewcontroller.HomeController;
import ubik3d.viewcontroller.View;
import ubik3d.viewcontroller.ViewFactory;

/**
 * Home frame pane controller.
 * @author Emmanuel Puybaret
 */
public class HomeFrameController implements Controller {
  private final Home            home;
  private final HomeApplication application;
  private final ViewFactory     viewFactory;
  private final ContentManager  contentManager;
  private final PluginManager   pluginManager;
  private View                  homeFrameView;

  private HomeController        homeController;
  
  public HomeFrameController(Home home, HomeApplication application, 
                             ViewFactory viewFactory,
                             ContentManager contentManager, 
                             PluginManager pluginManager) {
    this.home = home;
    this.application = application;
    this.viewFactory = viewFactory;
    this.contentManager = contentManager;
    this.pluginManager = pluginManager;
  }

  /**
   * Returns the view associated with this controller.
   */
  public View getView() {
    // Create view lazily only once it's needed
    if (this.homeFrameView == null) {
      this.homeFrameView = new HomeFramePane(this.home, this.application, this.contentManager, this);
    }
    return this.homeFrameView;
  }
  
  /**
   * Returns the home controller managed by this controller.
   */
  public HomeController getHomeController() {
    // Create sub controller lazily only once it's needed
    if (this.homeController == null) {
      this.homeController = new HomeController(
          this.home, this.application, this.viewFactory, this.contentManager, this.pluginManager);
    }
    return this.homeController;
  }
  
  /**
   * Displays the view controlled by this controller.
   */
  public void displayView() {
    ((HomeFramePane)getView()).displayView();
  }
}
