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

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import ubik3d.io.DefaultHomeInputStream;
import ubik3d.j3d.Component3DManager;
import ubik3d.j3d.ModelManager;
import ubik3d.j3d.TextureManager;
import ubik3d.model.BackgroundImage;
import ubik3d.model.CatalogPieceOfFurniture;
import ubik3d.model.CatalogTexture;
import ubik3d.model.FurnitureCatalog;
import ubik3d.model.Home;
import ubik3d.model.InterruptedRecorderException;
import ubik3d.model.RecorderException;
import ubik3d.model.UserPreferences;
import ubik3d.swing.HomeComponent3D;
import ubik3d.swing.ThreadedTaskPanel;
import ubik3d.tools.OperatingSystem;
import ubik3d.viewcontroller.BackgroundImageWizardController;
import ubik3d.viewcontroller.CompassController;
import ubik3d.viewcontroller.DialogView;
import ubik3d.viewcontroller.FurnitureCatalogController;
import ubik3d.viewcontroller.FurnitureController;
import ubik3d.viewcontroller.HelpController;
import ubik3d.viewcontroller.HelpView;
import ubik3d.viewcontroller.Home3DAttributesController;
import ubik3d.viewcontroller.HomeController;
import ubik3d.viewcontroller.HomeController3D;
import ubik3d.viewcontroller.HomeFurnitureController;
import ubik3d.viewcontroller.HomeView;
import ubik3d.viewcontroller.ImportedFurnitureWizardController;
import ubik3d.viewcontroller.ImportedFurnitureWizardStepsView;
import ubik3d.viewcontroller.ImportedTextureWizardController;
import ubik3d.viewcontroller.LabelController;
import ubik3d.viewcontroller.PageSetupController;
import ubik3d.viewcontroller.PhotoController;
import ubik3d.viewcontroller.PlanController;
import ubik3d.viewcontroller.PlanView;
import ubik3d.viewcontroller.PrintPreviewController;
import ubik3d.viewcontroller.RoomController;
import ubik3d.viewcontroller.TextureChoiceController;
import ubik3d.viewcontroller.TextureChoiceView;
import ubik3d.viewcontroller.ThreadedTaskController;
import ubik3d.viewcontroller.ThreadedTaskView;
import ubik3d.viewcontroller.UserPreferencesController;
import ubik3d.viewcontroller.VideoController;
import ubik3d.viewcontroller.View;
import ubik3d.viewcontroller.ViewFactory;
import ubik3d.viewcontroller.WallController;
import ubik3d.viewcontroller.WizardController;


/**
 * Helper for {@link SweetHome3DViewer SweetHome3DViewer}. This class is public 
 * because it's loaded by applet viewer class loader as a start point.
 * @author Emmanuel Puybaret
 */
public final class ViewerHelper {
  private static final String HOME_URL_PARAMETER     = "homeURL";
  private static final String IGNORE_CACHE_PARAMETER = "ignoreCache";
  private static final String NAVIGATION_PANEL       = "navigationPanel";
  
  public ViewerHelper(final JApplet applet) {
    // Create default user preferences with no catalog
    final UserPreferences preferences = new UserPreferences() {
        @Override
        public void addLanguageLibrary(String languageLibraryName) throws RecorderException {
          throw new UnsupportedOperationException();
        }
  
        @Override
        public boolean languageLibraryExists(String languageLibraryName) throws RecorderException {
          throw new UnsupportedOperationException();
        }

        @Override
        public void addFurnitureLibrary(String furnitureLibraryName) throws RecorderException {
          throw new UnsupportedOperationException();
        }
  
        @Override
        public boolean furnitureLibraryExists(String furnitureLibraryName) throws RecorderException {
          throw new UnsupportedOperationException();
        }
  
        @Override
        public boolean texturesLibraryExists(String name) throws RecorderException {
          throw new UnsupportedOperationException();
        }

        @Override
        public void addTexturesLibrary(String name) throws RecorderException {
          throw new UnsupportedOperationException();
        }

        @Override
        public void write() throws RecorderException {
          throw new UnsupportedOperationException();
        }
        
        @Override
        public boolean isNavigationPanelVisible() {
          return "true".equalsIgnoreCase(applet.getParameter(NAVIGATION_PANEL));
        }
      };
    
    // Create a view factory able to instantiate only a 3D view and a threaded task view
    final ViewFactory viewFactory = new ViewFactory() {
        public View createBackgroundImageWizardStepsView(BackgroundImage backgroundImage, UserPreferences preferences,
                                                         BackgroundImageWizardController backgroundImageWizardController) {
          throw new UnsupportedOperationException();
        }

        public View createFurnitureCatalogView(FurnitureCatalog catalog, UserPreferences preferences,
                                               FurnitureCatalogController furnitureCatalogController) {
          throw new UnsupportedOperationException();
        }

        public View createFurnitureView(Home home, UserPreferences preferences, FurnitureController furnitureController) {
          throw new UnsupportedOperationException();
        }

        public HelpView createHelpView(UserPreferences preferences, HelpController helpController) {
          throw new UnsupportedOperationException();
        }

        public DialogView createHome3DAttributesView(UserPreferences preferences,
                                                     Home3DAttributesController home3DAttributesController) {
          throw new UnsupportedOperationException();
        }

        public DialogView createHomeFurnitureView(UserPreferences preferences,
                                                  HomeFurnitureController homeFurnitureController) {
          throw new UnsupportedOperationException();
        }

        public HomeView createHomeView(Home home, UserPreferences preferences, HomeController homeController) {
          throw new UnsupportedOperationException();
        }

        public ImportedFurnitureWizardStepsView createImportedFurnitureWizardStepsView(CatalogPieceOfFurniture piece,
                    String modelName, boolean importHomePiece, UserPreferences preferences,
                    ImportedFurnitureWizardController importedFurnitureWizardController) {
          throw new UnsupportedOperationException();
        }

        public View createImportedTextureWizardStepsView(CatalogTexture texture, String textureName,
                                                         UserPreferences preferences,
                                                         ImportedTextureWizardController importedTextureWizardController) {
          throw new UnsupportedOperationException();
        }

        public DialogView createLabelView(boolean modification, UserPreferences preferences,
                                          LabelController labelController) {
          throw new UnsupportedOperationException();
        }

        public DialogView createPageSetupView(UserPreferences preferences, PageSetupController pageSetupController) {
          throw new UnsupportedOperationException();
        }

        public PlanView createPlanView(Home home, UserPreferences preferences, PlanController planController) {
          throw new UnsupportedOperationException();
        }

        public DialogView createPrintPreviewView(Home home, UserPreferences preferences, HomeController homeController,
                                                 PrintPreviewController printPreviewController) {
          throw new UnsupportedOperationException();
        }

        public DialogView createRoomView(UserPreferences preferences, RoomController roomController) {
          throw new UnsupportedOperationException();
        }

        public TextureChoiceView createTextureChoiceView(UserPreferences preferences,
                                                         TextureChoiceController textureChoiceController) {
          throw new UnsupportedOperationException();
        }

        public ThreadedTaskView createThreadedTaskView(String taskMessage, UserPreferences preferences,
                                                       ThreadedTaskController controller) {
          return new ThreadedTaskPanel(taskMessage, preferences, controller) {
              private boolean taskRunning;
  
              @Override
              public void setTaskRunning(boolean taskRunning, View executingView) {
                if (taskRunning && !this.taskRunning) {
                  // Display task panel directly at applet center if it's empty 
                  this.taskRunning = taskRunning;
                  JPanel contentPane = new JPanel(new GridBagLayout());
                  contentPane.add(this, new GridBagConstraints());
                  applet.setContentPane(contentPane);
                  applet.getRootPane().revalidate();
                } 
              }
            };
        }

        public DialogView createUserPreferencesView(UserPreferences preferences,
                                                    UserPreferencesController userPreferencesController) {
          throw new UnsupportedOperationException();
        }

        public View createView3D(final Home home, UserPreferences preferences, final HomeController3D controller) {
          HomeComponent3D homeComponent3D = new HomeComponent3D(home, preferences, controller);
          // Add tab key to input map to change camera
          InputMap inputMap = homeComponent3D.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
          inputMap.put(KeyStroke.getKeyStroke("SPACE"), "changeCamera");
          ActionMap actionMap = homeComponent3D.getActionMap();
          actionMap.put("changeCamera", new AbstractAction() {
              public void actionPerformed(ActionEvent ev) {
                if (home.getCamera() == home.getTopCamera()) {
                  controller.viewFromObserver();
                } else {
                  controller.viewFromTop();
                }
              }
            });
          return homeComponent3D;
        }

        public DialogView createWallView(UserPreferences preferences, WallController wallController) {
          throw new UnsupportedOperationException();
        }

        public DialogView createWizardView(UserPreferences preferences, WizardController wizardController) {
          throw new UnsupportedOperationException();
        }

        public DialogView createPhotoView(Home home, UserPreferences preferences, PhotoController photoController) {
          throw new UnsupportedOperationException();
        }

        public DialogView createVideoView(Home home, UserPreferences preferences, VideoController videoController) {
          throw new UnsupportedOperationException();
        }

        public DialogView createCompassView(UserPreferences preferences, CompassController compassController) {
          throw new UnsupportedOperationException();
        }
      };

    // Force offscreen in 3D view under Plugin 2 and Mac OS X
    System.setProperty("ubik3d.j3d.useOffScreen3DView", 
        String.valueOf(OperatingSystem.isMacOSX()            
            && applet.getAppletContext() != null
            && applet.getAppletContext().getClass().getName().startsWith("sun.plugin2.applet.Plugin2Manager")));

    initLookAndFeel();

    addComponent3DRenderingErrorObserver(applet.getRootPane(), preferences);

    // Retrieve displayed home 
    String homeUrlParameter = applet.getParameter(HOME_URL_PARAMETER);
    if (homeUrlParameter == null) {
      homeUrlParameter = "default.sh3d";
    }
    // Retrieve ignoreCache parameter value
    String ignoreCacheParameter = applet.getParameter(IGNORE_CACHE_PARAMETER);
    final boolean ignoreCache = ignoreCacheParameter != null 
        && "true".equalsIgnoreCase(ignoreCacheParameter);
    try {
      final URL homeUrl = new URL(applet.getDocumentBase(), homeUrlParameter);
      // Read home in a threaded task
      Callable<Void> openTask = new Callable<Void>() {
            public Void call() throws RecorderException {
              // Read home with application recorder
              Home openedHome = readHome(homeUrl, ignoreCache);
              displayHome(applet.getRootPane(), openedHome, preferences, viewFactory);
              return null;
            }
          };
      ThreadedTaskController.ExceptionHandler exceptionHandler = 
          new ThreadedTaskController.ExceptionHandler() {
            public void handleException(Exception ex) {
              if (!(ex instanceof InterruptedRecorderException)) {
                if (ex instanceof RecorderException) {
                  showError(applet.getRootPane(), 
                      preferences.getLocalizedString(ViewerHelper.class, "openError", homeUrl));
                } else {
                  ex.printStackTrace();
                }
              }
            }
          };
      new ThreadedTaskController(openTask, 
          preferences.getLocalizedString(ViewerHelper.class, "openMessage"), exceptionHandler, 
          null, viewFactory).executeTask(null);
    } catch (MalformedURLException ex) {
      showError(applet.getRootPane(), 
          preferences.getLocalizedString(ViewerHelper.class, "openError", homeUrlParameter));
      return;
    } 
  }
  
  /**
   * Clears all the resources used by the applet.
   * This method is called when an applet is destroyed.  
   */
  public void destroy() {
    // Collect deleted objects (seems to be required under Mac OS X when the applet is being reloaded)
    System.gc();
    // Stop managers threads
    TextureManager.getInstance().clear();
    ModelManager.getInstance().clear();
  }
  
  private void initLookAndFeel() {
    try {
      // Apply current system look and feel
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      // Enable applets to update their content while window resizing
      Toolkit.getDefaultToolkit().setDynamicLayout(true);
    } catch (Exception ex) {
      // Too bad keep current look and feel
    }
  }

  /**
   * Sets the rendering error listener bound to Java 3D 
   * to avoid default System exit in case of error during 3D rendering. 
   */
  private void addComponent3DRenderingErrorObserver(final JRootPane rootPane,
                                                    final UserPreferences preferences) {
    // Instead of adding a RenderingErrorListener directly to VirtualUniverse, 
    // we add it through Canvas3DManager, because offscreen rendering needs to check 
    // rendering errors with its own RenderingErrorListener
    Component3DManager.getInstance().setRenderingErrorObserver(
        new Component3DManager.RenderingErrorObserver() {
          public void errorOccured(int errorCode, String errorMessage) {
            System.err.print("Error in Java 3D : " + errorCode + " " + errorMessage);
            EventQueue.invokeLater(new Runnable() {
                public void run() {
                  String message = preferences.getLocalizedString(
                      ViewerHelper.class, "3DErrorMessage");
                  showError(rootPane, message);
                }
              });
          }
        });
  }

  /**
   * Shows the given text in a label.
   */
  private static void showError(final JRootPane rootPane, String text) {
    JLabel label = new JLabel(text, SwingConstants.CENTER);
    rootPane.setContentPane(label);
    rootPane.revalidate();
  }

  /**
   * Reads a home from its URL.
   */
  private Home readHome(URL homeUrl, boolean ignoreCache) throws RecorderException {
    URLConnection connection = null;
    DefaultHomeInputStream in = null;
    try {
      // Open a home input stream to server 
      connection = homeUrl.openConnection();
      connection.setRequestProperty("Content-Type", "charset=UTF-8");
      connection.setUseCaches(!ignoreCache);
      in = new DefaultHomeInputStream(connection.getInputStream());
      // Read home with HomeInputStream
      Home home = in.readHome();
      return home;
    } catch (InterruptedIOException ex) {
      throw new InterruptedRecorderException("Read " + homeUrl + " interrupted");
    } catch (IOException ex) {
      throw new RecorderException("Can't read home from " + homeUrl, ex);
    } catch (ClassNotFoundException ex) {
      throw new RecorderException("Missing classes to read home from " + homeUrl, ex);
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException ex) {
        throw new RecorderException("Can't close stream", ex);
      }
    }
  }
  
  /**
   * Displays the given <code>home</code> in the main pane of <code>rootPane</code>. 
   */
  private void displayHome(final JRootPane rootPane, final Home home, 
                           final UserPreferences preferences, 
                           final ViewFactory viewFactory) {
    EventQueue.invokeLater(new Runnable() {
        public void run() {
          HomeController3D controller = 
              new HomeController3D(home, preferences, viewFactory, null, null);
          rootPane.setContentPane((JComponent)controller.getView());
          rootPane.revalidate();
        }
      });
  }
}
