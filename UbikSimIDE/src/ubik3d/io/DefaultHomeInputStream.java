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
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.ZipInputStream;

import ubik3d.model.Home;
import ubik3d.tools.OperatingSystem;
import ubik3d.tools.URLContent;


/**
 * An <code>InputStream</code> filter that reads a home from a stream 
 * at .sh3d file format. 
 * @see DefaultHomeOutputStream
 */
public class DefaultHomeInputStream extends FilterInputStream {
  private File tempFile;

  /**
   * Creates a home input stream filter able to read a home and its content
   * from <code>in</code>.
   */
  public DefaultHomeInputStream(InputStream in) throws IOException {
    super(in);
  }

  /**
   * Throws an <code>InterruptedRecorderException</code> exception 
   * if current thread is interrupted. The interrupted status of the current thread 
   * is cleared when an exception is thrown.
   */
  private static void checkCurrentThreadIsntInterrupted() throws InterruptedIOException {
    if (Thread.interrupted()) {
      throw new InterruptedIOException();
    }
  }
  
  /**
   * Reads home from a zipped stream.
   */
  public Home readHome() throws IOException, ClassNotFoundException {
    // Copy home stream in a temporary file 
    this.tempFile = OperatingSystem.createTemporaryFile("open", ".sweethome3d");
    checkCurrentThreadIsntInterrupted();
    OutputStream tempOut = null;
    try {
      tempOut = new FileOutputStream(this.tempFile);
      byte [] buffer = new byte [8192];
      int size; 
      while ((size = this.in.read(buffer)) != -1) {
        tempOut.write(buffer, 0, size);
      }
    } finally {
      if (tempOut != null) {
        tempOut.close();
      }
    }
    
    ZipInputStream zipIn = null;
    try {
      // Open a zip input from temp file
      zipIn = new ZipInputStream(new FileInputStream(this.tempFile));
      // Read home in first entry
      zipIn.getNextEntry();
      checkCurrentThreadIsntInterrupted();
      // Use an ObjectInputStream that replaces temporary URLs of Content objects 
      // by URLs relative to file 
      ObjectInputStream objectStream = new HomeObjectInputStream(zipIn);
      return (Home)objectStream.readObject();
    } finally {
      if (zipIn != null) {
        zipIn.close();
      }
    }
  }

  /**
   * <code>ObjectInputStream</code> that replaces temporary <code>URLContent</code> 
   * objects by <code>URLContent</code> objects that points to file.
   */
  private class HomeObjectInputStream extends ObjectInputStream {
    public HomeObjectInputStream(InputStream in) throws IOException {
      super(in);
      enableResolveObject(true);
    }

    @Override
    protected Object resolveObject(Object obj) throws IOException {
      if (obj instanceof URLContent) {
        URL tmpURL = ((URLContent)obj).getURL();
        String url = tmpURL.toString();
        if (url.startsWith("jar:file:temp!/")) {
          // Replace "temp" in URL by current temporary file
          URL fileURL = new URL("jar:file:" + tempFile.toString() + url.substring(url.indexOf('!')));
          return new HomeURLContent(fileURL);
        } else {
          return obj;
        }
      } else {
        return obj;
      }
    }
  }
}