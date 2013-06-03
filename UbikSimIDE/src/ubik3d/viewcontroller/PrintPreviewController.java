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
package ubik3d.viewcontroller;

import ubik3d.model.Home;
import ubik3d.model.UserPreferences;

/**
 * A MVC controller for home print preview view.
 * @author Emmanuel Puybaret
 */
public class PrintPreviewController implements Controller {
  private final Home            home;
  private final UserPreferences preferences;
  private final HomeController  homeController;
  private final ViewFactory     viewFactory;
  private DialogView            printPreviewView;

  /**
   * Creates the controller of print preview with undo support.
   */
  public PrintPreviewController(Home home,
                                UserPreferences preferences,
                                HomeController homeController,
                                ViewFactory viewFactory) {
    this.home = home;
    this.preferences = preferences;
    this.homeController = homeController;
    this.viewFactory = viewFactory;
  }

  /**
   * Returns the view associated with this controller.
   */
  public DialogView getView() {
    // Create view lazily only once it's needed
    if (this.printPreviewView == null) {
      this.printPreviewView = this.viewFactory.createPrintPreviewView(this.home, 
          this.preferences, this.homeController, this);
    }
    return this.printPreviewView;
  }
  
  /**
   * Displays the view controlled by this controller.
   */
  public void displayView(View parentView) {
    getView().displayView(parentView);
  }
}
