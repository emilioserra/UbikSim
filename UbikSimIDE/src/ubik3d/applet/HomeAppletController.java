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
package ubik3d.applet;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;

import javax.jnlp.BasicService;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;

import ubik3d.io.HomeFileRecorder;
import ubik3d.model.Home;
import ubik3d.model.HomeApplication;
import ubik3d.model.InterruptedRecorderException;
import ubik3d.model.RecorderException;
import ubik3d.plugin.PluginManager;
import ubik3d.swing.FileContentManager;
import ubik3d.viewcontroller.ContentManager;
import ubik3d.viewcontroller.HomeController;
import ubik3d.viewcontroller.HomeView;
import ubik3d.viewcontroller.ThreadedTaskController;
import ubik3d.viewcontroller.UserPreferencesController;
import ubik3d.viewcontroller.ViewFactory;


/**
 * Home applet pane controller.
 * @author Emmanuel Puybaret
 */
public class HomeAppletController extends HomeController {
  private final Home home;
  private final HomeApplication application;
  private final ViewFactory viewFactory;
  private final ContentManager contentManager;

  public HomeAppletController(Home home, 
                              HomeApplication application, 
                              ViewFactory viewFactory,
                              ContentManager  contentManager,
                              PluginManager   pluginManager,
                              boolean newHomeEnabled, 
                              boolean openEnabled, 
                              boolean saveEnabled, 
                              boolean saveAsEnabled) {
    super(home, application, viewFactory, contentManager, pluginManager);
    this.home = home;
    this.application = application;
    this.viewFactory = viewFactory;
    this.contentManager = contentManager;
    
    HomeView view = getView();
    view.setEnabled(HomeView.ActionType.EXIT, false);
    view.setEnabled(HomeView.ActionType.NEW_HOME, newHomeEnabled);
    view.setEnabled(HomeView.ActionType.OPEN, openEnabled);
    view.setEnabled(HomeView.ActionType.SAVE, saveEnabled);
    view.setEnabled(HomeView.ActionType.SAVE_AS, saveAsEnabled);
    
    // By default disabled Print to PDF, Export to SVG, Export to OBJ and Create photo actions 
    view.setEnabled(HomeView.ActionType.PRINT_TO_PDF, false);
    view.setEnabled(HomeView.ActionType.EXPORT_TO_SVG, false);
    view.setEnabled(HomeView.ActionType.EXPORT_TO_OBJ, false);
    view.setEnabled(HomeView.ActionType.CREATE_PHOTO, false);
    
    view.setEnabled(HomeView.ActionType.DETACH_3D_VIEW, false);
  }
  
  /**
   * Creates a new home after saving and deleting the current home.
   */
  @Override
  public void newHome() {
    close(new Runnable() {
        public void run() {
          HomeAppletController.super.newHome();
        }
      });
  }

  /**
   * Opens a home after saving and deleting the current home.
   */
  @Override
  public void open() {
    close(new Runnable() {
      public void run() {
        HomeAppletController.super.open();
      }
    });
  }
  
  /**
   * Displays Sweet Home user guide in a navigator window.
   */
  @Override
  public void help() {
    try { 
      // Lookup the javax.jnlp.BasicService object 
      final BasicService service = (BasicService)ServiceManager.lookup("javax.jnlp.BasicService");
      String helpIndex = this.application.getUserPreferences().getLocalizedString(HomeAppletController.class, "helpIndex");
      service.showDocument(new URL(helpIndex)); 
    } catch (UnavailableServiceException ex) {
      // Too bad : service is unavailable             
    } catch (MalformedURLException ex) {
      ex.printStackTrace();
    } 
  }

  /**
   * Controls the export of home to a SH3D file.
   */
  public void exportToSH3D() {
    final String sh3dName = new FileContentManager(this.application.getUserPreferences()).showSaveDialog(getView(),
        this.application.getUserPreferences().getLocalizedString(HomeAppletController.class, "exportToSH3DDialog.title"), 
        ContentManager.ContentType.SWEET_HOME_3D, home.getName());    
    if (sh3dName != null) {
      // Export home in a threaded task
      Callable<Void> exportToObjTask = new Callable<Void>() {
            public Void call() throws RecorderException {
              new HomeFileRecorder(9).writeHome(home, sh3dName);
              return null;
            }
          };
      ThreadedTaskController.ExceptionHandler exceptionHandler = 
          new ThreadedTaskController.ExceptionHandler() {
            public void handleException(Exception ex) {
              if (!(ex instanceof InterruptedRecorderException)) {
                if (ex instanceof RecorderException) {
                  String message = application.getUserPreferences().getLocalizedString(
                      HomeAppletController.class, "exportToSH3DError", sh3dName);
                  getView().showError(message);
                } else {
                  ex.printStackTrace();
                }
              }
            }
          };
      new ThreadedTaskController(exportToObjTask, 
          this.application.getUserPreferences().getLocalizedString(HomeAppletController.class, "exportToSH3DMessage"), exceptionHandler, 
          this.application.getUserPreferences(), viewFactory).executeTask(getView());
    }
  }
  
  @Override
  public void editPreferences() {
    new UserPreferencesController(this.application.getUserPreferences(), 
        this.viewFactory, this.contentManager) {
      @Override
      public boolean isPropertyEditable(UserPreferencesController.Property property) {
        // No auto recovery with applet
        return property != UserPreferencesController.Property.AUTO_SAVE_DELAY_FOR_RECOVERY
            && property != UserPreferencesController.Property.AUTO_SAVE_FOR_RECOVERY_ENABLED;
      }
    }.displayView(getView());
  }
}

