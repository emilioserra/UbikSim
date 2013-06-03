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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import ubik3d.model.Content;


/**
 * URL content for files, images...
 * @author Emmanuel Puybaret
 */
public class URLContent implements Content {
  private static final long serialVersionUID = 1L;

  private URL url;
  
  public URLContent(URL url) {
    this.url = url;
  }

  /**
   * Returns the URL of this content.
   */
  public URL getURL() {
    return this.url;
  }

  /**
   * Returns an InputStream on the URL content. 
   * @throws IOException if URL stream can't be opened. 
   */
  public InputStream openStream() throws IOException {
    return this.url.openStream();
  }
  
  /**
   * Returns <code>true</code> if the URL stored by this content 
   * references an entry in a JAR.
   */
  public boolean isJAREntry() {
    return "jar".equals(this.url.getProtocol());
  }
  
  /**
   * Returns the URL base of a JAR entry.
   * @throws IllegalStateException if the URL of this content 
   *                    doesn't reference an entry in a JAR.
   */
  public URL getJAREntryURL() {
    if (!isJAREntry()) {
      throw new IllegalStateException("Content isn't a JAR entry");
    }
    try {
      String file = this.url.getFile();
      return new URL(file.substring(0, file.indexOf('!')));
    } catch (MalformedURLException ex) {
      throw new IllegalStateException("Invalid URL base for JAR entry", ex);
    }
  }

  /**
   * Returns the name of a JAR entry. 
   * If the JAR entry in the URL given at creation time was encoded in application/x-www-form-urlencoded format,
   * this method will return it unchanged and not decoded.
   * @throws IllegalStateException if the URL of this content 
   *                    doesn't reference an entry in a JAR URL.
   */
  public String getJAREntryName() {
    if (!isJAREntry()) {
      throw new IllegalStateException("Content isn't a JAR entry");
    }
    String file = this.url.getFile();
    return file.substring(file.indexOf('!') + 2);
  }
  
  /**
   * Returns <code>true</code> if the object in parameter is an URL content
   * that references the same URL as this object.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (obj instanceof URLContent) {
      URLContent urlContent = (URLContent)obj;
      return urlContent.url == this.url
          || urlContent.url.equals(this.url);
    } else {
      return false;
    }
  }
  
  @Override
  public int hashCode() {
    return this.url.hashCode();
  }
}
