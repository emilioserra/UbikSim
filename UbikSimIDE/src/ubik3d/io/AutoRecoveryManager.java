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
package ubik3d.io;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import ubik3d.model.CollectionEvent;
import ubik3d.model.CollectionListener;
import ubik3d.model.Home;
import ubik3d.model.HomeApplication;
import ubik3d.model.HomeRecorder;
import ubik3d.model.InterruptedRecorderException;
import ubik3d.model.RecorderException;
import ubik3d.model.UserPreferences;
import ubik3d.model.UserPreferences.Property;
import ubik3d.tools.OperatingSystem;


/**
 * Manager able to automatically save open homes in recovery folder with a timer.
 * The delay between two automatic save operations is specified by 
 * {@link UserPreferences#getAutoSaveDelayForRecovery() auto save delay for recovery}
 * property.
 * @author Emmanuel Puybaret
 */
public class AutoRecoveryManager {
  private static final int    MINIMUM_DELAY_BETWEEN_AUTO_SAVE_OPERATIONS = 30000;
  private static final String RECOVERY_SUB_FOLDER      = "recovery";
  private static final String RECOVERED_FILE_EXTENSION = ".recovered";

  private final HomeApplication             application;
  private final List<Home>                  recoveredHomes      = new ArrayList<Home>();
  // The auto saved files and their locked output streams are handled 
  // only in autoSaveForRecoveryExecutor single thread executor
  private final Map<Home, File>             autoSavedFiles      = new HashMap<Home, File>();
  private final Map<File, FileOutputStream> lockedOutputStreams = new HashMap<File, FileOutputStream>();
  private final ExecutorService             autoSaveForRecoveryExecutor;
  private Timer                             timer;
  private long                              lastAutoSaveTime;

  /**
   * Creates a manager able to automatically recover <code>application</code> homes.
   * As this constructor adds some listeners on <code>application</code> instance and its preferences,
   * it should be invoke only from the same thread where application is modified or at program startup. 
   */
  public AutoRecoveryManager(HomeApplication application) throws RecorderException {
    this.application = application;
    this.autoSaveForRecoveryExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        public Thread newThread(Runnable runnable) {
          Thread thread = new Thread(runnable);
          thread.setPriority(Thread.MIN_PRIORITY);
          return thread;
        }
      });
    
    readRecoveredHomes();
    
    // Interrupt auto saving when program stops
    Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
          autoSaveForRecoveryExecutor.shutdownNow();
        }
      });
    
    // Remove auto saved files when a home is closed
    application.addHomesListener(new CollectionListener<Home>() {
        public void collectionChanged(CollectionEvent<Home> ev) {
          if (ev.getType() == CollectionEvent.Type.DELETE) {
            final Home home = ev.getItem();
            autoSaveForRecoveryExecutor.submit(new Runnable() {
                public void run() {
                  try {
                    final File homeFile = autoSavedFiles.get(home);
                    if (homeFile != null) {
                      freeLockedFile(homeFile);
                      homeFile.delete();
                      autoSavedFiles.remove(home);
                    }
                  } catch (RecorderException ex) {
                  }
                }
              });
          }
        }
      });
    
    // Add a listener on auto save delay that will run auto save timer
    application.getUserPreferences().addPropertyChangeListener(Property.AUTO_SAVE_DELAY_FOR_RECOVERY, new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent ev) {
          restartTimer();
        }
      });
    restartTimer();
  }

  /**
   * Reads the homes to recover.
   */
  private void readRecoveredHomes() throws RecorderException {
    File [] recoveredFiles = getRecoveryFolder().listFiles(new FileFilter() {
        public boolean accept(File file) {
          return file.isFile()
              && file.getName().endsWith(RECOVERED_FILE_EXTENSION);
        }
      });
    if (recoveredFiles != null) {
      Arrays.sort(recoveredFiles, new Comparator<File>() {
          public int compare(File f1, File f2) {
            if (f1.lastModified() < f2.lastModified()) {
              return 1;
            } else {
              return -1;
            }
          }
        });
      for (final File file : recoveredFiles) {
        if (!isFileLocked(file)) {
          try {
            final Home home = this.application.getHomeRecorder().readHome(file.getPath());
            // Recovered homes are the ones with a name different from the file path 
            if (home.getName() == null 
                || !file.equals(new File(home.getName()))) {
              home.setRecovered(true);
              // Delete recovered file once home isn't recovered anymore
              home.addPropertyChangeListener(Home.Property.RECOVERED, new PropertyChangeListener() {
                  public void propertyChange(PropertyChangeEvent evt) {
                    if (!home.isRecovered()) {
                      file.delete();
                    }
                  }
                });
              this.recoveredHomes.add(home);
            }
          } catch (RecorderException ex) {
            if (recoveredFiles.length > 1) {
              // Let's give a chance to other files
              ex.printStackTrace();
            } else {
              throw ex; 
            }
          }
        }
      }
    }
  }

  /**
   * Returns <code>true</code> if the given file is locked or can't be accessed.
   */
  private boolean isFileLocked(final File file) {
    FileOutputStream out = null;
    try {
      // Check file lock is free
      out = new FileOutputStream(file, true); 
      return out.getChannel().tryLock() == null;
    } catch (IOException ex) {
      // Forget this file
      return true;
    } finally {
      if (out != null) {
        try {
          out.close();
        } catch (IOException ex) {
          return true;
        }
      }
    }
  }

  /**
   * Opens recovered homes and adds them to application. 
   */
  public void openRecoveredHomes() {
    for (Home recoveredHome : this.recoveredHomes) {
      boolean recoveredHomeOpen = false;
      for (Home home : this.application.getHomes()) {
        // If recovered home matches an opened home, open it as a new home
        if (home.getName() != null
            && home.getName().equals(recoveredHome.getName())) {
          recoveredHome.setName(null);
          this.application.addHome(recoveredHome);
          recoveredHomeOpen = true;
          break;
        }
      }
      if (!recoveredHomeOpen) {
        this.application.addHome(recoveredHome);
      }
    }
    // Clear the list to avoid open twice the recovered homes
    this.recoveredHomes.clear();
  }
  
  /**
   * Restarts the timer that regularly saves application homes. 
   */
  private void restartTimer() {
    if (this.timer != null) {
      this.timer.cancel();
      this.timer = null;
    }
    int autoSaveDelayForRecovery = this.application.getUserPreferences().getAutoSaveDelayForRecovery();
    if (autoSaveDelayForRecovery > 0) {
      this.timer = new Timer("autoSaveTimer", true);
      TimerTask task = new TimerTask() {
        @Override
        public void run() {
          if (System.currentTimeMillis() - lastAutoSaveTime > MINIMUM_DELAY_BETWEEN_AUTO_SAVE_OPERATIONS) {
            cloneAndSaveHomes();
          }
        }
      };
      this.timer.scheduleAtFixedRate(task, autoSaveDelayForRecovery, autoSaveDelayForRecovery);
    }
  }

  /**
   * Clones application homes and saves them in automatic save executor.
   */
  private void cloneAndSaveHomes() {
    try {
      EventQueue.invokeAndWait(new Runnable() {
          public void run() {
            // Handle and clone application homes in Event Dispatch Thread
            for (final Home home : application.getHomes()) {
              final Home autoSavedHome = home.clone();
              final HomeRecorder homeRecorder = application.getHomeRecorder();
              autoSaveForRecoveryExecutor.submit(new Runnable() {
                public void run() {
                  try {
                    // Save home clone in an other thread
                    saveHome(home, autoSavedHome, homeRecorder);
                  } catch (RecorderException ex) {
                    ex.printStackTrace();
                  }
                }
              });
            }
          }
        });
    } catch (InvocationTargetException ex) {
      throw new RuntimeException(ex);
    } catch (InterruptedException ex) {
      // Ignore saving in case of interruption
    }
  }

  /**
   * Saves the given <code>home</code> in recovery folder.
   * Must be run only from auto save thread.
   */
  private void saveHome(Home home, Home autoSavedHome, HomeRecorder homeRecorder) throws RecorderException {
    File autoSavedHomeFile = this.autoSavedFiles.get(home);
    if (autoSavedHomeFile == null) {
      File recoveredFilesFolder = getRecoveryFolder();
      if (!recoveredFilesFolder.exists()) {
        if (!recoveredFilesFolder.mkdirs()) {
          throw new RecorderException("Can't create folder " + recoveredFilesFolder + " to store recovered files");
        }
      }
      // Find a unique file for home in recovered files sub folder
      if (autoSavedHome.getName() != null) {
        String homeFile = new File(autoSavedHome.getName()).getName();
        autoSavedHomeFile = new File(recoveredFilesFolder, homeFile + RECOVERED_FILE_EXTENSION);
        if (autoSavedHomeFile.exists()) {
          autoSavedHomeFile = new File(recoveredFilesFolder, 
              UUID.randomUUID() + "-" + homeFile + RECOVERED_FILE_EXTENSION);
        }
      } else {
        autoSavedHomeFile = new File(recoveredFilesFolder,
            UUID.randomUUID() + RECOVERED_FILE_EXTENSION);
      }
    }
    freeLockedFile(autoSavedHomeFile);        
    if (autoSavedHome.isModified()) {
      this.autoSavedFiles.put(home, autoSavedHomeFile);
      try {
        // Save home and lock the saved file to avoid possible auto recovery processes to read it 
        homeRecorder.writeHome(autoSavedHome, autoSavedHomeFile.getPath());
        
        FileOutputStream lockedOutputStream = null;
        try {
          lockedOutputStream = new FileOutputStream(autoSavedHomeFile, true);
          lockedOutputStream.getChannel().lock();
          this.lockedOutputStreams.put(autoSavedHomeFile, lockedOutputStream);
        } catch (OverlappingFileLockException ex) {
          // Don't try to race with other processes that acquired a lock on the file 
        } catch (IOException ex) {
          if (lockedOutputStream != null) {
            try {
              lockedOutputStream.close();
            } catch (IOException ex1) {
              // Forget it
            }
          }
          throw new RecorderException("Can't lock saved home", ex);            
        }
      } catch (InterruptedRecorderException ex) {
        // Forget exception that probably happen because of shutdown hook management
      } 
    } else {
      autoSavedHomeFile.delete();
      this.autoSavedFiles.remove(home);
    }
    this.lastAutoSaveTime = Math.max(this.lastAutoSaveTime, System.currentTimeMillis());
  }

  /**
   * Frees the given <code>file</code> if it's locked.
   * Must be run only from auto save thread.
   */
  private void freeLockedFile(File file) throws RecorderException {
    FileOutputStream lockedOutputStream = this.lockedOutputStreams.get(file);
    if (lockedOutputStream != null) {
      // Close stream and free its associated lock
      try {
        lockedOutputStream.close();
        this.lockedOutputStreams.remove(file);
      } catch (IOException ex) {
        throw new RecorderException("Can't close locked stream", ex);
      }
    }
  }

  /**
   * Returns the folder where recovered files are stored.
   */
  private File getRecoveryFolder() throws RecorderException {
    try {
      UserPreferences userPreferences = this.application.getUserPreferences();
      return new File(userPreferences instanceof FileUserPreferences
          ? ((FileUserPreferences)userPreferences).getApplicationFolder()
          : OperatingSystem.getDefaultApplicationFolder(), RECOVERY_SUB_FOLDER);
    } catch (IOException ex) {
      throw new RecorderException("Can't retrieve recovered files folder", ex);
    }
  }
}