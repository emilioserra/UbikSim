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

import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import ubik3d.model.CollectionEvent;
import ubik3d.model.CollectionListener;
import ubik3d.model.Home;
import ubik3d.model.UserPreferences;
import ubik3d.swing.HomePane;
import ubik3d.swing.ResourceAction;
import ubik3d.viewcontroller.HomeController;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

/**
 * Configuration class that accesses to Mac OS X specifics.
 * Do not invoke methods of this class without checking first if 
 * <code>os.name</code> System property is <code>Mac OS X</code>.
 * This class requires some classes of <code>com.apple.eawt</code> package  
 * to compile.
 * @author Emmanuel Puybaret
 */
class MacOSXConfiguration {
  /**
   * Binds <code>homeApplication</code> to Mac OS X application menu.
   */
  public static void bindToApplicationMenu(final SweetHome3D homeApplication) {
    Application macosxApplication = Application.getApplication();
    // Create a default controller for an empty home and disable unrelated actions
    final HomeController defaultController = 
        homeApplication.createHomeFrameController(homeApplication.createHome()).getHomeController();
    final HomePane defaultHomeView = (HomePane)defaultController.getView();
    for (HomePane.ActionType action : HomePane.ActionType.values()) {
      switch (action) {
        case ABOUT :
        case NEW_HOME :
        case OPEN :
        case DELETE_RECENT_HOMES :
        case HELP :
          break;
        default :
          defaultHomeView.setEnabled(action, false);
      }
    }

    final JMenuBar defaultMenuBar = defaultHomeView.getJMenuBar();
    JFrame frame;
    try {
      if (UIManager.getLookAndFeel().getClass().getName().equals(UIManager.getSystemLookAndFeelClassName())) {
        macosxApplication.setDefaultMenuBar(defaultMenuBar);
        addWindowMenu(null, defaultMenuBar, homeApplication, true);
      }
      frame = null;
    } catch (NoSuchMethodError ex) {
      // Create default frame if setDefaultMenuBar isn't available
      frame = new JFrame();
      final JFrame defaultFrame = frame;
      EventQueue.invokeLater(new Runnable() {
        public void run() {
          // Create a default undecorated frame out of sight 
          // and attach the application menu bar of empty view to it
          defaultFrame.setLocation(-10, 0);
          defaultFrame.setUndecorated(true);
          defaultFrame.setVisible(true);
          defaultFrame.setJMenuBar(defaultMenuBar);
          defaultFrame.setContentPane(defaultHomeView);
          addWindowMenu(defaultFrame, defaultMenuBar, homeApplication, true);
        }
      });
    } 

    final JFrame defaultFrame = frame;
    // Add a listener to Mac OS X application that will call
    // controller methods of the active frame
    macosxApplication.addApplicationListener(new ApplicationAdapter() {      
      @Override
      public void handleQuit(ApplicationEvent ev) { 
        handleAction(new Runnable() {
            public void run() {
              defaultController.exit();
            }
          });
        if (homeApplication.getHomes().isEmpty()) {
          System.exit(0);
        }
      }
      
      @Override
      public void handleAbout(ApplicationEvent ev) {
        handleAction(new Runnable() {
            public void run() {
              defaultController.about();
            }
          });
        ev.setHandled(true);
      }

      @Override
      public void handlePreferences(ApplicationEvent ev) {
        handleAction(new Runnable() {
            public void run() {
              defaultController.editPreferences();
            }
          });
      }
      
      private void handleAction(Runnable runnable) {
        Frame activeFrame = null;
        for (Frame frame : Frame.getFrames()) {
          if (frame.isActive()) {
            activeFrame = frame;
            break;
          }
        }
        if (defaultFrame != null) {
          // Move default frame to center to display dialogs at center
          defaultFrame.setLocationRelativeTo(null);
        }
        
        runnable.run();
        
        // Activate previous frame again
        if (activeFrame != null) {
          activeFrame.toFront();
        }
        if (defaultFrame != null) {
          // Move default frame out of user view
          defaultFrame.setLocation(-10, 0);
        }
      }

      @Override
      public void handleOpenFile(ApplicationEvent ev) {
        // handleOpenFile is called when user opens a document
        // associated with a Java Web Start application
        // Just call main with -open file arguments as JNLP specifies 
        homeApplication.run(new String [] {"-open", ev.getFilename()});
      }
      
      @Override
      public void handleReOpenApplication(ApplicationEvent ev) {
        // handleReOpenApplication is called when user launches 
        // the application when it's already open
        homeApplication.run(new String [0]);
      }
    });
    macosxApplication.setEnabledAboutMenu(true);
    macosxApplication.setEnabledPreferencesMenu(true);
    
    homeApplication.addHomesListener(new CollectionListener<Home>() {
      public void collectionChanged(CollectionEvent<Home> ev) {
        if (ev.getType() == CollectionEvent.Type.ADD) {
          // Add Mac OS X Window menu on new homes
          JFrame homeFrame = homeApplication.getHomeFrame(ev.getItem());
          MacOSXConfiguration.addWindowMenu(
              homeFrame, homeFrame.getJMenuBar(), homeApplication, false);
        }
      };
    });
    
    // Set application icon if program wasn't launch from bundle
    if (!"true".equalsIgnoreCase(System.getProperty("sweethome3d.bundle", "false"))) {
      try {
        String iconPath = homeApplication.getUserPreferences().getLocalizedString(HomePane.class, "about.icon");
        Image icon = ImageIO.read(HomePane.class.getResource(iconPath));
        macosxApplication.setDockIconImage(icon);
      } catch (NoSuchMethodError ex) {
        // Ignore icon change if setDockIconImage isn't available
      } catch (IOException ex) {
      }
    }
  }
  
  /**
   * Adds Mac OS X standard Window menu to frame. 
   */
  private static void addWindowMenu(final JFrame frame, 
                                    final JMenuBar menuBar, 
                                    final SweetHome3D homeApplication,
                                    boolean defaultFrame) {
    UserPreferences preferences = homeApplication.getUserPreferences();
    final JMenu windowMenu = new JMenu(
        new ResourceAction(preferences, MacOSXConfiguration.class, "WINDOW_MENU", true));
    // Add Window menu before Help menu
    menuBar.add(windowMenu, menuBar.getComponentCount() - 1);
    windowMenu.add(new JMenuItem(
        new ResourceAction(preferences, MacOSXConfiguration.class, "MINIMIZE", !defaultFrame) {
            @Override
            public void actionPerformed(ActionEvent ev) {
              frame.setState(Frame.ICONIFIED);
            }
          }));
    windowMenu.add(new JMenuItem(
        new ResourceAction(preferences, MacOSXConfiguration.class, "ZOOM", !defaultFrame) {
            @Override
            public void actionPerformed(ActionEvent ev) {
              if ((frame.getExtendedState() & Frame.MAXIMIZED_BOTH) != 0) {
                frame.setExtendedState(frame.getExtendedState() & ~Frame.MAXIMIZED_BOTH);
              } else {
                frame.setExtendedState(frame.getExtendedState() | Frame.MAXIMIZED_BOTH);
              }
            }
          }));
    windowMenu.addSeparator();
    windowMenu.add(new JMenuItem(
        new ResourceAction(preferences, MacOSXConfiguration.class, "BRING_ALL_TO_FRONT", !defaultFrame) {
            @Override
            public void actionPerformed(ActionEvent ev) {
              // Avoid blinking while bringing other windows to front
              frame.setAlwaysOnTop(true);
              for (Home home : homeApplication.getHomes()) {
                JFrame applicationFrame = homeApplication.getHomeFrame(home);
                if (applicationFrame != frame
                    && applicationFrame.getState() != Frame.ICONIFIED) {
                  applicationFrame.setFocusableWindowState(false);
                  applicationFrame.toFront();
                  applicationFrame.setFocusableWindowState(true);
                }
              }
              frame.setAlwaysOnTop(false);
            }
          }));
    
    windowMenu.addMenuListener(new MenuListener() {
        public void menuSelected(MenuEvent ev) {
          boolean firstMenuItem = true;
          // Fill menu dynamically with a menu item for the frame of each application home
          for (Home home : homeApplication.getHomes()) {
            final JFrame applicationFrame = homeApplication.getHomeFrame(home);
            JCheckBoxMenuItem windowMenuItem = new JCheckBoxMenuItem(
                new AbstractAction(applicationFrame.getTitle()) {
                    public void actionPerformed(ActionEvent ev) {
                      applicationFrame.toFront();
                    }
                  });
              
            if (frame == applicationFrame) {
              windowMenuItem.setSelected(true);
            }
            if (firstMenuItem) {
              windowMenu.addSeparator();
              firstMenuItem = false;
            }
            windowMenu.add(windowMenuItem);
          }
        }

        public void menuDeselected(MenuEvent ev) {
          // Remove dynamically filled part of menu
          for (int i = windowMenu.getMenuComponentCount() - 1; i >= 4; i--) {
            windowMenu.remove(i);
          }
        }

        public void menuCanceled(MenuEvent ev) {
          menuDeselected(ev);
        }
      });
  }
}
