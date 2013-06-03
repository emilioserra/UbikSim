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
package ubik3d.tools;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import ubik3d.model.Home;

import com.apple.eio.FileManager;

/**
 * Tools used to test current user operating system.
 * @author Emmanuel Puybaret
 */
public class OperatingSystem {
  private static final String EDITOR_SUB_FOLDER; 
  private static final String APPLICATION_SUB_FOLDER;
  private static final String TEMPORARY_SUB_FOLDER;
  private static final String TEMPORARY_SESSION_SUB_FOLDER;
  
  static {
    // Retrieve sub folders where is stored application data
    ResourceBundle resource = ResourceBundle.getBundle(OperatingSystem.class.getName());
    if (OperatingSystem.isMacOSX()) {
      EDITOR_SUB_FOLDER = resource.getString("editorSubFolder.Mac OS X");
      APPLICATION_SUB_FOLDER = resource.getString("applicationSubFolder.Mac OS X");
    } else if (OperatingSystem.isWindows()) {
      EDITOR_SUB_FOLDER = resource.getString("editorSubFolder.Windows");
      APPLICATION_SUB_FOLDER = resource.getString("applicationSubFolder.Windows");
    } else {
      EDITOR_SUB_FOLDER = resource.getString("editorSubFolder");
      APPLICATION_SUB_FOLDER = resource.getString("applicationSubFolder");
    }
    
    String temporarySubFolder;
    try {
      temporarySubFolder = resource.getString("temporarySubFolder");
      if (temporarySubFolder.trim().length() == 0) {
        temporarySubFolder = null;
      }
    } catch (MissingResourceException ex) {
      temporarySubFolder = "work";
    }
    temporarySubFolder = System.getProperty(
        "ubik3d.tools.temporarySubFolder", temporarySubFolder);
    TEMPORARY_SUB_FOLDER = temporarySubFolder;
    TEMPORARY_SESSION_SUB_FOLDER = UUID.randomUUID().toString();
  }
 
  // This class contains only static methods
  private OperatingSystem() {    
  }

  /**
   * Returns <code>true</code> if current operating is Linux.
   */
  public static boolean isLinux() {
    return System.getProperty("os.name").startsWith("Linux");
  }

  /**
   * Returns <code>true</code> if current operating is Windows.
   */
  public static boolean isWindows() {
    return System.getProperty("os.name").startsWith("Windows");
  }

  /**
   * Returns <code>true</code> if current operating is Mac OS X.
   */
  public static boolean isMacOSX() {
    return System.getProperty("os.name").startsWith("Mac OS X");
  }

  /**
   * Returns <code>true</code> if current operating is Mac OS X 10.5 
   * or superior.
   */
  public static boolean isMacOSXLeopardOrSuperior() {
    // Just need to test is OS version is different of 10.4 because Sweet Home 3D
    // isn't supported under Mac OS X versions previous to 10.4
    return isMacOSX()
        && !System.getProperty("os.version").startsWith("10.4");
  }

  /**
   * Returns a temporary file that will be deleted when JVM will exit.
   * @throws IOException if the file couldn't be created
   */
  public static File createTemporaryFile(String prefix, String suffix) throws IOException {
    File temporaryFolder;
    try {
      temporaryFolder = getDefaultTemporaryFolder(true);
    } catch (IOException ex) {
      // In case creating default temporary folder failed, use default temporary files folder
      temporaryFolder = null;
    }
    File temporaryFile = File.createTempFile(prefix, suffix, temporaryFolder);
    temporaryFile.deleteOnExit();
    return temporaryFile;
  }

  /**
   * Deletes all the temporary files created with {@link #createTemporaryFile(String, String) createTemporaryFile}.
   */
  public static void deleteTemporaryFiles() {
    try {
      File temporaryFolder = getDefaultTemporaryFolder(false);
      if (temporaryFolder != null) {
        for (File temporaryFile : temporaryFolder.listFiles()) {
          temporaryFile.delete();
        }
        temporaryFolder.delete();
      }
    } catch (IOException ex) {
      // Ignore temporary folder that can't be found
    }
  }

  /**
   * Returns the default folder used to store temporary files created in the program.
   */
  private synchronized static File getDefaultTemporaryFolder(boolean create) throws IOException {
    if (TEMPORARY_SUB_FOLDER != null) {
      File temporaryFolder = new File(getDefaultApplicationFolder(), TEMPORARY_SUB_FOLDER);
      final String versionPrefix = Home.CURRENT_VERSION + "-";
      final File sessionTemporaryFolder = new File(temporaryFolder, 
          versionPrefix + TEMPORARY_SESSION_SUB_FOLDER);      
      if (!sessionTemporaryFolder.exists()) {
        // Retrieve existing folders working with same Sweet Home 3D version in temporary folder
        final File [] siblingTemporaryFolders = temporaryFolder.listFiles(new FileFilter() {
            public boolean accept(File file) {
              return file.isDirectory() 
                  && file.getName().startsWith(versionPrefix);
            }
          });
        
        // Create temporary folder  
        if (!sessionTemporaryFolder.mkdirs()) {
          throw new IOException("Can't create temporary folder " + sessionTemporaryFolder);
        } else {
          sessionTemporaryFolder.deleteOnExit();
        }
        
        // Launch a timer that updates modification date of the temporary folder each minute
        final long updateDelay = 60000;
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
              // Ensure modification date is always growing in case system time was adjusted
              sessionTemporaryFolder.setLastModified(Math.max(System.currentTimeMillis(),
                  sessionTemporaryFolder.lastModified() + updateDelay));
            }
          }, updateDelay, updateDelay);
        
        if (siblingTemporaryFolders != null
            && siblingTemporaryFolders.length > 0) {
          // Launch a timer that will delete in 10 min temporary folders older than a week 
          final long deleteDelay = 10 * 60000;
          final long age = 7 * 24 * 3600000;
          new Timer(true).schedule(new TimerTask() {
              @Override
              public void run() {
                long now = System.currentTimeMillis();
                for (File siblingTemporaryFolder : siblingTemporaryFolders) {
                  if (siblingTemporaryFolder.exists()
                      && now - siblingTemporaryFolder.lastModified() > age) {
                    File [] temporaryFiles = siblingTemporaryFolder.listFiles();
                    for (File temporaryFile : temporaryFiles) {
                      temporaryFile.delete();
                    }
                    siblingTemporaryFolder.delete();
                  }
                }
              }
            }, deleteDelay);
        }
      }
      return sessionTemporaryFolder;
    } else {
      return null;
    }
  }
  
  /**
   * Returns default application folder. 
   */
  public static File getDefaultApplicationFolder() throws IOException {
    File userApplicationFolder; 
    if (isMacOSX()) {
      userApplicationFolder = new File(MacOSXFileManager.getApplicationSupportFolder());
    } else if (isWindows()) {
      userApplicationFolder = new File(System.getProperty("user.home"), "Application Data");
      // If user Application Data directory doesn't exist, use user home
      if (!userApplicationFolder.exists()) {
        userApplicationFolder = new File(System.getProperty("user.home"));
      }
    } else { 
      // Unix
      userApplicationFolder = new File(System.getProperty("user.home"));
    }
    return new File(userApplicationFolder, 
        EDITOR_SUB_FOLDER + File.separator + APPLICATION_SUB_FOLDER);
  }

  /**
   * File manager class that accesses to Mac OS X specifics.
   * Do not invoke methods of this class without checking first if 
   * <code>os.name</code> System property is <code>Mac OS X</code>.
   * This class requires some classes of <code>com.apple.eio</code> package  
   * to compile.
   */
  private static class MacOSXFileManager {
    public static String getApplicationSupportFolder() throws IOException {
      // Find application support folder (0x61737570) for user domain (-32763)
      return FileManager.findFolder((short)-32763, 0x61737570);
    }
  }
}
