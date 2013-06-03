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

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.jnlp.DownloadService;
import javax.jnlp.DownloadServiceListener;
import javax.jnlp.ServiceManager;
import javax.jnlp.UnavailableServiceException;

/**
 * This bootstrap class loads lazy resource parts specified in the JNLP file
 * with the System property <code>ubik3d.lazyParts</code>, 
 * then launches Sweet Home 3D application class.
 * @author Emmanuel Puybaret
 */
public class SweetHome3DJavaWebStartBootstrap {
  public static void main(String [] args) throws ClassNotFoundException, 
        NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    try { 
      // Lookup the javax.jnlp.DownloadService object 
      DownloadService service = (DownloadService)ServiceManager.lookup("javax.jnlp.DownloadService");
      DownloadServiceListener progressWindow = service.getDefaultProgressWindow();
      // Search jars lazily downloaded 
      String lazyParts = System.getProperty("ubik3d.lazyParts", "");
      List<String> lazyPartsToDownload = new ArrayList<String>(); 
      for (String lazyPart : lazyParts.split("\\s")) {
        if (!service.isPartCached(lazyPart)) {
          lazyPartsToDownload.add(lazyPart);
        }
      }      
      try {
        if (lazyPartsToDownload.size() > 0) {
          service.loadPart(lazyPartsToDownload.toArray(new String [lazyPartsToDownload.size()]), progressWindow);
        }
      } catch (IOException ex) {
        ex.printStackTrace();
        System.exit(1);
      }        
    } catch (UnavailableServiceException ex) {
      // Sweet Home 3D isn't launched from Java Web Start
    }
    
    // Call application class main method with reflection
    String applicationClassName = "ubik3d.SweetHome3DBootstrap";
    Class<?> applicationClass = Class.forName(applicationClassName);
    Method applicationClassMain = 
        applicationClass.getMethod("main", Array.newInstance(String.class, 0).getClass());
    applicationClassMain.invoke(null, new Object [] {args});
  }
}