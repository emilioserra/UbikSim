/*
 * Contact, UbikSimIDE has been developed by:
 * 
 * Juan A. Bot�a , juanbot@um.es
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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import ubik3d.io.DefaultHomeInputStream;
import ubik3d.io.DefaultHomeOutputStream;
import ubik3d.model.Home;
import ubik3d.model.HomeRecorder;
import ubik3d.model.InterruptedRecorderException;
import ubik3d.model.RecorderException;


/**
 * Recorder that stores homes on a HTTP server.
 * @author Emmanuel Puybaret
 */
public class HomeAppletRecorder implements HomeRecorder {
  private final String writeHomeURL;
  private final String readHomeURL;
  private final String listHomesURL;
  private final boolean includeOnlyTemporaryContent;

  /**
   * Creates a recorder that will use the URLs in parameter to write, read and list homes.
   * @see SweetHome3DApplet
   */
  public HomeAppletRecorder(String writeHomeURL, 
                            String readHomeURL,
                            String listHomesURL) {
    this(writeHomeURL, readHomeURL, listHomesURL, true);
  }
  
  /**
   * Creates a recorder that will use the URLs in parameter to write, read and list homes.
   * @see SweetHome3DApplet
   */
  public HomeAppletRecorder(String writeHomeURL, 
                            String readHomeURL,
                            String listHomesURL,
                            boolean includeOnlyTemporaryContent) {
    this.writeHomeURL = writeHomeURL;
    this.readHomeURL = readHomeURL;
    this.listHomesURL = listHomesURL;
    this.includeOnlyTemporaryContent = includeOnlyTemporaryContent;
  }
  
  /**
   * Posts home data to the server URL returned by <code>getHomeSaveURL</code>.
   * @throws RecorderException if a problem occurred while writing home.
   */
  public void writeHome(Home home, String name) throws RecorderException {
    HttpURLConnection connection = null;
    try {
      // Open a stream to server 
      connection = (HttpURLConnection)new URL(this.writeHomeURL).openConnection();
      connection.setRequestMethod("POST");
      String multiPartBoundary = "---------#@&$!d3emohteews!$&@#---------";
      connection.setRequestProperty("Content-Type", "multipart/form-data; charset=UTF-8; boundary=" + multiPartBoundary);
      connection.setDoOutput(true);
      connection.setDoInput(true);
      connection.setUseCaches(false);
      
      // Post home part
      OutputStream out = connection.getOutputStream();
      out.write(("--" + multiPartBoundary + "\r\n").getBytes("UTF-8"));
      out.write(("Content-Disposition: form-data; name=\"home\"; filename=\"" 
          + name.replace('\"', '\'') + "\"\r\n").getBytes("UTF-8"));
      out.write(("Content-Type: application/octet-stream\r\n\r\n").getBytes("UTF-8"));
      out.flush();
      DefaultHomeOutputStream homeOut = new DefaultHomeOutputStream(out, 9, this.includeOnlyTemporaryContent);
      // Write home with HomeOuputStream
      homeOut.writeHome(home);
      homeOut.flush();
      
      // Post last boundary
      out.write(("\r\n--" + multiPartBoundary + "--\r\n").getBytes("UTF-8"));
      out.close();
      
      // Read response
      InputStream in = connection.getInputStream();
      int read = in.read();
      in.close();
      if (read != '1') {
        throw new RecorderException("Saving home " + name + " failed");
      } 
    } catch (InterruptedIOException ex) {
      throw new InterruptedRecorderException("Save " + name + " interrupted");
    } catch (IOException ex) {
      throw new RecorderException("Can't save home " + name, ex);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  /**
   * Returns a home instance read from its file <code>name</code>.
   * @throws RecorderException if a problem occurred while reading home, 
   *   or if file <code>name</code> doesn't exist.
   */
  public Home readHome(String name) throws RecorderException {
    URLConnection connection = null;
    DefaultHomeInputStream in = null;
    try {
      // Replace % sequence by %% except %s before formating readHomeURL with home name 
      String readHomeURL = String.format(this.readHomeURL.replaceAll("(%[^s])", "%$1"), 
          URLEncoder.encode(name, "UTF-8"));
      // Open a home input stream to server
      connection = new URL(readHomeURL).openConnection();
      connection.setRequestProperty("Content-Type", "charset=UTF-8");
      connection.setUseCaches(false);
      in = new DefaultHomeInputStream(connection.getInputStream());
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
   * Returns <code>true</code> if the home <code>name</code> exists.
   */
  public boolean exists(String name) throws RecorderException {
    String [] availableHomes = getAvailableHomes();
    for (String home : availableHomes) {
      if (home.equals(name)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the available homes on server.
   */
  public String [] getAvailableHomes() throws RecorderException {
    URLConnection connection = null;
    InputStream in = null;
    try {
      // Open a stream to server 
      connection = new URL(this.listHomesURL).openConnection();
      connection.setUseCaches(false);
      in = connection.getInputStream();
      String contentEncoding = connection.getContentEncoding();
      if (contentEncoding == null) {
        contentEncoding = "UTF-8";
      }
      Reader reader = new InputStreamReader(in, contentEncoding);
      StringWriter homes = new StringWriter();
      for (int c; (c = reader.read()) != -1; ) {
        homes.write(c);
      }
      String [] availableHomes = homes.toString().split("\n");
      if (availableHomes.length == 1 && availableHomes [0].length() == 0) {
        return new String [0];
      } else {
        return availableHomes;
      }
    } catch (IOException ex) {
      throw new RecorderException("Can't read homes from server", ex);
    } finally {
      try {
        if (in != null) {
          in.close();
        }
      } catch (IOException ex) {
        throw new RecorderException("Can't close coonection", ex);
      }
    }
  }
}
