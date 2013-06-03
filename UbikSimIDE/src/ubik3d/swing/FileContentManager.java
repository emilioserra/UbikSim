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
package ubik3d.swing;

import java.awt.FileDialog;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import ubik3d.model.Content;
import ubik3d.model.RecorderException;
import ubik3d.model.UserPreferences;
import ubik3d.tools.OperatingSystem;
import ubik3d.tools.URLContent;
import ubik3d.viewcontroller.ContentManager;
import ubik3d.viewcontroller.View;


/**
 * Content manager for files with Swing file choosers.
 * @author Emmanuel Puybaret
 */
public class FileContentManager implements ContentManager {
  private static final String OBJ_EXTENSION = ".obj";
  /**
   * Supported OBJ filter.
   */
  private static final FileFilter [] OBJ_FILTER = {
      new FileFilter() {
        @Override
        public boolean accept(File file) {
          // Accept directories and .obj files
          return file.isDirectory()
              || file.getName().toLowerCase().endsWith(OBJ_EXTENSION);
        }
        
        @Override
        public String getDescription() {
          return "OBJ - Wavefront";
        }
      }};
  /**
   * Supported 3D model file filters.
   */
  private static final FileFilter [] MODEL_FILTERS = {
     OBJ_FILTER [0],
     new FileFilter() {
       @Override
       public boolean accept(File file) {
         // Accept directories and LWS files
         return file.isDirectory()
                || file.getName().toLowerCase().endsWith(".lws");
       }
   
       @Override
       public String getDescription() {
         return "LWS - LightWave Scene";
       }
     },
     new FileFilter() {
       @Override
       public boolean accept(File file) {
         // Accept directories and 3DS files
         return file.isDirectory()
                || file.getName().toLowerCase().endsWith(".3ds");
       }
   
       @Override
       public String getDescription() {
         return "3DS - 3D Studio";
       }
     },
     new FileFilter() {
       @Override
       public boolean accept(File file) {
         // Accept directories and 3DS files
         return file.isDirectory()
                || file.getName().toLowerCase().endsWith(".dae");
       }
   
       @Override
       public String getDescription() {
         return "DAE - Collada";
       }
     },
     new FileFilter() {
       @Override
       public boolean accept(File file) {
         // Accept directories and ZIP files
         return file.isDirectory()
                || file.getName().toLowerCase().endsWith(".zip");
       }
   
       @Override
       public String getDescription() {
         return "ZIP";
       }
     }};
  private static final String PNG_EXTENSION = ".png";
  /**
   * Supported OBJ filter.
   */
  private static final FileFilter [] PNG_FILTER = {
      new FileFilter() {
        @Override
        public boolean accept(File file) {
          // Accept directories and .png files
          return file.isDirectory()
              || file.getName().toLowerCase().endsWith(PNG_EXTENSION);
        }
        
        @Override
        public String getDescription() {
          return "PNG";
        }
      }};
  /**
   * Supported image file filters.
   */
  private static final FileFilter [] IMAGE_FILTERS = {
      new FileFilter() {
        @Override
        public boolean accept(File file) {
          // Accept directories and .ubiksim files
          return file.isDirectory()
                 || file.getName().toLowerCase().endsWith(".bmp")
                 || file.getName().toLowerCase().endsWith(".wbmp");
        }
    
        @Override
        public String getDescription() {
          return "BMP";
        }
      },
      new FileFilter() {
        @Override
        public boolean accept(File file) {
          // Accept directories and GIF files
          return file.isDirectory()
                 || file.getName().toLowerCase().endsWith(".gif");
        }
    
        @Override
        public String getDescription() {
          return "GIF";
        }
      },
      new FileFilter() {
        @Override
        public boolean accept(File file) {
          // Accept directories and JPEG files
          return file.isDirectory()
                 || file.getName().toLowerCase().endsWith(".jpg")
                 || file.getName().toLowerCase().endsWith(".jpeg");
        }
    
        @Override
        public String getDescription() {
          return "JPEG";
        }
      }, 
      PNG_FILTER [0]};
  private static final String MOV_EXTENSION = ".mov";
  /**
   * Supported MOV filter.
   */
  private static final FileFilter [] MOV_FILTER = {
      new FileFilter() {
        @Override
        public boolean accept(File file) {
          // Accept directories and .mov files
          return file.isDirectory()
              || file.getName().toLowerCase().endsWith(MOV_EXTENSION);
        }
        
        @Override
        public String getDescription() {
          return "MOV";
        }
      }};
  private static final String PDF_EXTENSION = ".pdf";
  /**
   * Supported PDF filter.
   */
  private static final FileFilter [] PDF_FILTER = {
      new FileFilter() {
        @Override
        public boolean accept(File file) {
          // Accept directories and .pdf files
          return file.isDirectory()
              || file.getName().toLowerCase().endsWith(PDF_EXTENSION);
        }
        
        @Override
        public String getDescription() {
          return "PDF";
        }
      }};
  private static final String SVG_EXTENSION = ".svg";
  /**
   * Supported SVG filter.
   */
  private static final FileFilter [] SVG_FILTER = {
      new FileFilter() {
        @Override
        public boolean accept(File file) {
          // Accept directories and .obj files
          return file.isDirectory()
              || file.getName().toLowerCase().endsWith(SVG_EXTENSION);
        }
        
        @Override
        public String getDescription() {
          return "SVG - Scalable Vector Graphics";
        }
      }};
  
  private final UserPreferences           preferences;
  private final String                    sweetHome3DFileExtension;
  private final String                    languageLibraryFileExtension;
  private final String                    furnitureLibraryFileExtension;
  private final String                    texturesLibraryFileExtension;
  private final String                    pluginFileExtension;
  private File                            currentDirectory;
  private Map<ContentType, FileFilter []> fileFilters;
  private Map<ContentType, String>        defaultFileExtensions;

  public FileContentManager(final UserPreferences preferences) {  
    this.preferences = preferences;
    this.sweetHome3DFileExtension = preferences.getLocalizedString(FileContentManager.class, "homeExtension");
    this.languageLibraryFileExtension = preferences.getLocalizedString(FileContentManager.class, "languageLibraryExtension");
    this.furnitureLibraryFileExtension = preferences.getLocalizedString(FileContentManager.class, "furnitureLibraryExtension");
    this.texturesLibraryFileExtension = preferences.getLocalizedString(FileContentManager.class, "texturesLibraryExtension");
    this.pluginFileExtension = preferences.getLocalizedString(FileContentManager.class, "pluginExtension");
    
    // Fill file filters map
    this.fileFilters = new HashMap<ContentType, FileFilter[]>();
    this.fileFilters.put(ContentType.MODEL, MODEL_FILTERS);
    this.fileFilters.put(ContentType.IMAGE, IMAGE_FILTERS);
    this.fileFilters.put(ContentType.MOV, MOV_FILTER);
    this.fileFilters.put(ContentType.PNG, PNG_FILTER);
    this.fileFilters.put(ContentType.PDF, PDF_FILTER);
    this.fileFilters.put(ContentType.SVG, SVG_FILTER);
    this.fileFilters.put(ContentType.OBJ, OBJ_FILTER);
    this.fileFilters.put(ContentType.SWEET_HOME_3D, new FileFilter [] {
        new FileFilter() {
          @Override
          public boolean accept(File file) {
            // Accept directories and .ubiksim files
            return file.isDirectory()
                || file.getName().toLowerCase().endsWith(sweetHome3DFileExtension);
          }
          
          @Override
          public String getDescription() {
            return preferences.getLocalizedString(FileContentManager.class, "homeDescription");
          }
        }
      });
    this.fileFilters.put(ContentType.LANGUAGE_LIBRARY, new FileFilter [] {
        new FileFilter() {
          @Override
          public boolean accept(File file) {
            // Accept directories and .sh3f files
            return file.isDirectory()
                || file.getName().toLowerCase().endsWith(languageLibraryFileExtension);
          }
         
          @Override
          public String getDescription() {
            return preferences.getLocalizedString(FileContentManager.class, "languageLibraryDescription");
          }
        }
      });
    this.fileFilters.put(ContentType.FURNITURE_LIBRARY, new FileFilter [] {
        new FileFilter() {
          @Override
          public boolean accept(File file) {
            // Accept directories and .sh3f files
            return file.isDirectory()
                || file.getName().toLowerCase().endsWith(furnitureLibraryFileExtension);
          }
          
          @Override
          public String getDescription() {
            return preferences.getLocalizedString(FileContentManager.class, "furnitureLibraryDescription");
          }
        }
      });
    this.fileFilters.put(ContentType.TEXTURES_LIBRARY, new FileFilter [] {
        new FileFilter() {
          @Override
          public boolean accept(File file) {
            // Accept directories and .sh3f files
            return file.isDirectory()
                || file.getName().toLowerCase().endsWith(texturesLibraryFileExtension);
          }
         
          @Override
          public String getDescription() {
            return preferences.getLocalizedString(FileContentManager.class, "texturesLibraryDescription");
          }
        }
      });
    this.fileFilters.put(ContentType.PLUGIN, new FileFilter [] {
       new FileFilter() {
         @Override
         public boolean accept(File file) {
           // Accept directories and .sh3f files
           return file.isDirectory()
               || file.getName().toLowerCase().endsWith(pluginFileExtension);
         }
         
         @Override
         public String getDescription() {
           return preferences.getLocalizedString(FileContentManager.class, "pluginDescription");
         }
       }
     });

    // Fill file default extension map
    this.defaultFileExtensions = new HashMap<ContentType, String>();
    this.defaultFileExtensions.put(ContentType.SWEET_HOME_3D, sweetHome3DFileExtension);
    this.defaultFileExtensions.put(ContentType.LANGUAGE_LIBRARY, languageLibraryFileExtension);
    this.defaultFileExtensions.put(ContentType.FURNITURE_LIBRARY, furnitureLibraryFileExtension);
    this.defaultFileExtensions.put(ContentType.TEXTURES_LIBRARY, texturesLibraryFileExtension);
    this.defaultFileExtensions.put(ContentType.PLUGIN, pluginFileExtension);
    this.defaultFileExtensions.put(ContentType.PNG, PNG_EXTENSION);
    this.defaultFileExtensions.put(ContentType.MOV, MOV_EXTENSION);
    this.defaultFileExtensions.put(ContentType.PDF, PDF_EXTENSION);
    this.defaultFileExtensions.put(ContentType.SVG, SVG_EXTENSION);
    this.defaultFileExtensions.put(ContentType.OBJ, OBJ_EXTENSION);
  }
  
  /**
   * Returns a {@link URLContent URL content} object that references 
   * the given file path.
   */
  public Content getContent(String contentName) throws RecorderException {
    try {
      return new URLContent(new File(contentName).toURI().toURL());
    } catch (IOException ex) {
      throw new RecorderException("Couldn't access to content " + contentName);
    }
  }
  
  /**
   * Returns the file name of the file path in parameter.
   */
  public String getPresentationName(String contentName, 
                                    ContentType contentType) {
    switch (contentType) {
      case SWEET_HOME_3D :
        return new File(contentName).getName();
      default :
        String fileName = new File(contentName).getName();
        int pointIndex = fileName.lastIndexOf('.');
        if (pointIndex != -1) {
          fileName = fileName.substring(0, pointIndex);
        }
        return fileName;
    }    
  }
  
  /**
   * Returns the file filters available for a given content type.
   * This method may be overridden to add some file filters to existing content types
   * or to define the filters of a user defined content type.
   */
  protected FileFilter [] getFileFilter(ContentType contentType) {
    if (contentType == ContentType.USER_DEFINED) {
      throw new IllegalArgumentException("Unknown user defined content type");
    } else {
      return this.fileFilters.get(contentType);
    }
  }
  
  /**
   * Returns the default file extension of a given content type. 
   * If not <code>null</code> this extension will be appended automatically 
   * to the file name chosen by user in save dialog.
   * This method may be overridden to change the default file extension of an existing content type
   * or to define the default file extension of a user defined content type.
   */
  public String getDefaultFileExtension(ContentType contentType) {
    if (contentType == ContentType.USER_DEFINED) {
      return null;
    } else {
      return this.defaultFileExtensions.get(contentType);
    }
  }
  
  /**
   * Returns <code>true</code> if the file path in parameter is accepted
   * for <code>contentType</code>.
   */
  public boolean isAcceptable(String contentName, 
                              ContentType contentType) {
    for (FileFilter filter : getFileFilter(contentType)) {
      if (filter.accept(new File(contentName))) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Returns the file path chosen by user with an open file dialog.
   * @return the file path or <code>null</code> if user canceled its choice.
   */
  public String showOpenDialog(View        parentView,
                               String      dialogTitle,
                               ContentType contentType) {
    // Use native file dialog under Mac OS X
    if (OperatingSystem.isMacOSX()) {
      return showFileDialog(parentView, dialogTitle, contentType, null, false);
    } else {
      return showFileChooser(parentView, dialogTitle, contentType, null, false);
    }
  }
  
  /**
   * Returns the file path chosen by user with a save file dialog.
   * If this file already exists, the user will be prompted whether 
   * he wants to overwrite this existing file. 
   * @return the chosen file path or <code>null</code> if user canceled its choice.
   */
  public String showSaveDialog(View        parentView,
                               String      dialogTitle,
                               ContentType contentType,
                               String      name) {
    String defaultExtension = getDefaultFileExtension(contentType);
    if (name != null) {
      // If name has an extension, remove it and build a name that matches contentType
      int extensionIndex = name.lastIndexOf('.');
      if (extensionIndex != -1) {
        name = name.substring(0, extensionIndex);
        if (defaultExtension != null) {
          name += defaultExtension;
        }
      }
    }
    
    String savedName;
    // Use native file dialog under Mac OS X    
    if (OperatingSystem.isMacOSX()) {
      savedName = showFileDialog(parentView, dialogTitle, contentType, name, true);
    } else {
      savedName = showFileChooser(parentView, dialogTitle, contentType, name, true);
    }
    
    boolean addedExtension = false;
    if (savedName != null) {
      if (defaultExtension != null) {
        if (!savedName.toLowerCase().endsWith(defaultExtension)) {
          savedName += defaultExtension;
          addedExtension = true;
        }
      }

      // If no extension was added to file under Mac OS X, 
      // FileDialog already asks to user if he wants to overwrite savedName
      if (OperatingSystem.isMacOSX()
          && !addedExtension) {
        return savedName;
      }
      // If the file exists, prompt user if he wants to overwrite it
      File savedFile = new File(savedName);
      if (savedFile.exists()
          && !confirmOverwrite(parentView, savedFile.getName())) {
        return showSaveDialog(parentView, dialogTitle, contentType, savedName);
      }
    }
    return savedName;
  }
  
  /**
   * Displays an AWT open file dialog.
   */
  private String showFileDialog(View               parentView,
                                String             dialogTitle,
                                final ContentType  contentType,
                                String             name, 
                                boolean            save) {
    FileDialog fileDialog = new FileDialog(
        JOptionPane.getFrameForComponent((JComponent)parentView));

    // Set selected file
    if (save && name != null) {
      fileDialog.setFile(new File(name).getName());
    }
    
    // Set supported files filter 
    fileDialog.setFilenameFilter(new FilenameFilter() {
        public boolean accept(File dir, String name) {          
          return isAcceptable(new File(dir, name).toString(), contentType);
        }
      });
    // Update current directory
    if (this.currentDirectory != null) {
      fileDialog.setDirectory(this.currentDirectory.toString());
    }
    if (save) {
      fileDialog.setMode(FileDialog.SAVE);
    } else {
      fileDialog.setMode(FileDialog.LOAD);
    }

    if (dialogTitle == null) {
      dialogTitle = getFileDialogTitle(save);
    }
    fileDialog.setTitle(dialogTitle);
    
    fileDialog.setVisible(true);
    String selectedFile = fileDialog.getFile();
    // If user chose a file
    if (selectedFile != null) {
      // Retrieve current directory for future calls
      this.currentDirectory = new File(fileDialog.getDirectory());
      // Return selected file
      return this.currentDirectory + File.separator + selectedFile;
    } else {
      return null;
    }
  }

  /**
   * Displays a Swing open file chooser.
   */
  private String showFileChooser(View          parentView,
                                 String        dialogTitle,
                                 ContentType   contentType,
                                 String        name,
                                 boolean       save) {
    JFileChooser fileChooser = new JFileChooser();
    // Set selected file
    if (save && name != null) {
      fileChooser.setSelectedFile(new File(name));
    }    
    // Set supported files filter 
    FileFilter acceptAllFileFilter = fileChooser.getAcceptAllFileFilter();
    fileChooser.addChoosableFileFilter(acceptAllFileFilter);
    FileFilter [] contentFileFilters = getFileFilter(contentType);
    for (FileFilter filter : contentFileFilters) {
      fileChooser.addChoosableFileFilter(filter);
    }
    // If there's only one file filter, select it 
    if (contentFileFilters.length == 1) {
      fileChooser.setFileFilter(contentFileFilters [0]);
    } else {
      fileChooser.setFileFilter(acceptAllFileFilter);
    }
    
    // Update current directory
    if (this.currentDirectory != null) {
      fileChooser.setCurrentDirectory(this.currentDirectory);
    }
    
    if (dialogTitle == null) {
      dialogTitle = getFileDialogTitle(save);
    }
    fileChooser.setDialogTitle(dialogTitle);
    
    int option;
    if (save) {
      option = fileChooser.showSaveDialog((JComponent)parentView);
    } else {
      option = fileChooser.showOpenDialog((JComponent)parentView);
    }    
    if (option == JFileChooser.APPROVE_OPTION) {
      // Retrieve current directory for future calls
      this.currentDirectory = fileChooser.getCurrentDirectory();
      // Return selected file
      return fileChooser.getSelectedFile().toString();
    } else {
      return null;
    }
  }

  /**
   * Returns default file dialog title.
   */
  protected String getFileDialogTitle(boolean save) {
    if (save) {
      return this.preferences.getLocalizedString(FileContentManager.class, "saveDialog.title");
    } else {
      return this.preferences.getLocalizedString(FileContentManager.class, "openDialog.title");
    }
  }
    
  /**
   * Displays a dialog that let user choose whether he wants to overwrite 
   * file <code>fileName</code> or not.
   * @return <code>true</code> if user confirmed to overwrite.
   */
  protected boolean confirmOverwrite(View parentView, String fileName) {
    // Retrieve displayed text in buttons and message
    String message = this.preferences.getLocalizedString(FileContentManager.class, "confirmOverwrite.message", fileName);
    String title = this.preferences.getLocalizedString(FileContentManager.class, "confirmOverwrite.title");
    String replace = this.preferences.getLocalizedString(FileContentManager.class, "confirmOverwrite.overwrite");
    String cancel = this.preferences.getLocalizedString(FileContentManager.class, "confirmOverwrite.cancel");
    
    return JOptionPane.showOptionDialog(SwingUtilities.getRootPane((JComponent)parentView), 
        message, title, JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
        null, new Object [] {replace, cancel}, cancel) == JOptionPane.OK_OPTION;
  }
}
