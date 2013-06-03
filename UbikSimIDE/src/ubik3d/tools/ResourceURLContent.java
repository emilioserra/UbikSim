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

import java.net.MalformedURLException;
import java.net.URL;


/**
 * URL content read from a class resource.
 * @author Emmanuel Puybaret
 */
public class ResourceURLContent extends URLContent {
  private static final long serialVersionUID = 1L;

  private boolean multiPartResource;
  
  /**
   * Creates a content for <code>resourceName</code> relative to <code>resourceClass</code>.
   * @param resourceClass the class relative to the resource name to load
   * @param resourceName  the name of the resource
   * @throws IllegalArgumentException if the resource doesn't match a valid resource.
   */
  public ResourceURLContent(Class<?> resourceClass, 
                            String resourceName) {
    this(resourceClass, resourceName, false);
  }

  /**
   * Creates a content for <code>resourceName</code> relative to <code>resourceClass</code>.
   * @param resourceClass the class relative to the resource name to load
   * @param resourceName  the name of the resource
   * @param multiPartResource  if <code>true</code> then the resource is a multi part resource 
   *           stored in a directory with other required resources
   * @throws IllegalArgumentException if the resource doesn't match a valid resource.
   */
  public ResourceURLContent(Class<?> resourceClass,
                            String resourceName, 
                            boolean multiPartResource) {
    super(getClassResource(resourceClass, resourceName));
    if (getURL() == null) {
      throw new IllegalArgumentException("Unknown resource " + resourceName);
    }
    this.multiPartResource = multiPartResource;
  }
  
  /**
   * Creates a content for <code>resourceName</code> relative to <code>resourceClassLoader</code>.
   * <code>resourceName</code> is absolute and shouldn't start with a slash.
   * @param resourceClassLoader the class loader used to load the given resource name
   * @param resourceName  the name of the resource
   * @throws IllegalArgumentException if the resource doesn't match a valid resource.
   */
  public ResourceURLContent(ClassLoader resourceClassLoader, 
                            String resourceName) {
    super(resourceClassLoader.getResource(resourceName));
    if (getURL() == null) {
      throw new IllegalArgumentException("Unknown resource " + resourceName);
    }
  }

  private static final boolean isJava1dot5dot0_16 = 
      System.getProperty("java.version").startsWith("1.5.0_16"); 
  
  /**
   * Returns the URL of the given resource relative to <code>resourceClass</code>.
   */
  private static URL getClassResource(Class<?> resourceClass,
                                      String resourceName) {
    URL defaultUrl = resourceClass.getResource(resourceName);
    // Fix for bug #6746185
    // http://bugs.sun.com/view_bug.do?bug_id=6746185
    if (isJava1dot5dot0_16
        && defaultUrl != null
        && "jar".equalsIgnoreCase(defaultUrl.getProtocol())) {
      String defaultUrlExternalForm = defaultUrl.toExternalForm();
      if (defaultUrl.toExternalForm().indexOf("!/") == -1) {
        String fixedUrl = "jar:" 
          + resourceClass.getProtectionDomain().getCodeSource().getLocation().toExternalForm() 
          + "!/" + defaultUrl.getPath();
        
        if (!fixedUrl.equals(defaultUrlExternalForm)) {
          try {
            return new URL(fixedUrl);
          } catch (MalformedURLException ex) {
            // Too bad: keep defaultUrl
          } 
        }
      }
    }
    return defaultUrl;
  }

  /**
   * Creates a content for <code>resourceUrl</code>. 
   * @param url  the URL of the resource
   */
  public ResourceURLContent(URL url, boolean multiPartResource) {
    super(url);
    this.multiPartResource = multiPartResource;
  }

  /**
   * Returns <code>true</code> if the resource is a multi part resource stored 
   * in a directory with other required resources.
   */
  public boolean isMultiPartResource() {
    return this.multiPartResource;
  }
}
