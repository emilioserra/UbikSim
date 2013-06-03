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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;

import ubik3d.model.Home;
import ubik3d.model.HomeRecorder;
import ubik3d.model.InterruptedRecorderException;
import ubik3d.model.RecorderException;
import ubik3d.tools.OperatingSystem;


/**
 * Recorder that stores homes in files with {@link DefaultHomeOutputStream} and
 * {@link DefaultHomeInputStream}.
 * @author Emmanuel Puybaret
 */
public class HomeFileRecorder implements HomeRecorder {
  private final int     compressionLevel;
  private final boolean includeOnlyTemporaryContent;

  /**
   * Creates a home recorder able to write and read homes in uncompressed files. 
   */
  public HomeFileRecorder() {
    this(0);
  }

  /**
   * Creates a home recorder able to write and read homes in files compressed 
   * at a level from 0 to 9. 
   * @param compressionLevel 0 (uncompressed) to 9 (compressed).
   */
  public HomeFileRecorder(int compressionLevel) {
    this(compressionLevel, false);
  }

  /**
   * Creates a home recorder able to write and read homes in files compressed 
   * at a level from 0 to 9. 
   * @param compressionLevel 0-9
   * @param includeOnlyTemporaryContent if <code>true</code>, only content instances of 
   *            <code>TemporaryURLContent</code> class referenced by the saved home 
   *            will be written. If <code>false</code>, all the content instances 
   *            referenced by the saved home will be written in the zip stream.  
   */
  public HomeFileRecorder(int     compressionLevel, 
                          boolean includeOnlyTemporaryContent) {
    this.compressionLevel = compressionLevel;
    this.includeOnlyTemporaryContent = includeOnlyTemporaryContent;    
  }

  /**
   * Writes home data.
   * @throws RecorderException if a problem occurred while writing home.
   */
  public void writeHome(Home home, String name) throws RecorderException {
    File homeFile = new File(name);
    if (homeFile.exists()
        && !homeFile.canWrite()) {
      throw new RecorderException("Can't write over file " + name);
    }
    
    DefaultHomeOutputStream homeOut = null;
    File tempFile = null;
    try {
      // Open a stream on a temporary file 
      tempFile = OperatingSystem.createTemporaryFile("save", ".sweethome3d");
      homeOut = new DefaultHomeOutputStream(new FileOutputStream(tempFile), 
          this.compressionLevel, this.includeOnlyTemporaryContent);
      // Write home with HomeOuputStream
      homeOut.writeHome(home);
    } catch (InterruptedIOException ex) {
      throw new InterruptedRecorderException("Save " + name + " interrupted");
    } catch (IOException ex) {
      throw new RecorderException("Can't save home " + name, ex);
    } finally {
      try {
        if (homeOut != null) {
          homeOut.close();
        }
      } catch (IOException ex) {
        throw new RecorderException("Can't close temporary file " + name, ex);
      }
    }

    // Open destination file
    OutputStream out;
    try {
      out = new FileOutputStream(homeFile);
    } catch (FileNotFoundException ex) {
      if (tempFile != null) {
        tempFile.delete();
      }
      throw new RecorderException("Can't save file " + name, ex);
    }
    
    // Copy temporary file to home file
    // Overwriting home file will ensure that its rights are kept
    byte [] buffer = new byte [8192];
    InputStream in = null;
    try {
      in = new FileInputStream(tempFile);          
      int size; 
      while ((size = in.read(buffer)) != -1) {
        out.write(buffer, 0, size);
      }
    } catch (IOException ex) { 
      throw new RecorderException("Can't copy file " + tempFile + " to " + name);
    } finally {
      try {
        if (out != null) {          
          out.close();
        }
      } catch (IOException ex) {
        throw new RecorderException("Can't close file " + name, ex);
      }
      try {
        if (in != null) {          
          in.close();
          tempFile.delete();
        }
      } catch (IOException ex) {
        // Forget exception
      }
    }
  }
  
  /**
   * Returns a home instance read from its file <code>name</code>.
   * @throws RecorderException if a problem occurred while reading home, 
   *   or if file <code>name</code> doesn't exist.
   */
  public Home readHome(String name) throws RecorderException {
    DefaultHomeInputStream in = null;
    try {
      // Open a stream on file
      in = new DefaultHomeInputStream(new FileInputStream(name));
      // Read home with HomeInputStream
      Home home = in.readHome();
      return home;
    } catch (InterruptedIOException ex) {
      throw new InterruptedRecorderException("Read " + name + " interrupted");
    } catch (IOException ex) {
      throw new RecorderException("Can't read home from " + name, ex);
    } catch (ClassNotFoundException ex) {
      throw new RecorderException("Missing classes to read home from " + name, ex);
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException ex) {
        throw new RecorderException("Can't close file " + name, ex);
      }
    }
  }

  /**
   * Returns <code>true</code> if the file <code>name</code> exists.
   */
  public boolean exists(String name) throws RecorderException {
    return new File(name).exists();
  }
}
