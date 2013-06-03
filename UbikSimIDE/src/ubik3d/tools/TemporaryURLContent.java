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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import ubik3d.model.Content;


/**
 * URL content for files, images...
 * @author Emmanuel Puybaret
 */
public class TemporaryURLContent extends URLContent {
  private static final long serialVersionUID = 1L;

  public TemporaryURLContent(URL temporaryUrl) {
    super(temporaryUrl);
  }

  /**
   * Returns a {@link URLContent URL content} object that references a temporary copy of 
   * a given <code>content</code>.
   */
  public static TemporaryURLContent copyToTemporaryURLContent(Content content) throws IOException {
    String extension = ".tmp";
    if (content instanceof URLContent) {
      String file = ((URLContent)content).getURL().getFile();
      int lastIndex = file.lastIndexOf('.');
      if (lastIndex > 0) {
        extension = file.substring(lastIndex);
      }
    }
    File tempFile = OperatingSystem.createTemporaryFile("temp", extension);
    InputStream tempIn = null;
    OutputStream tempOut = null;
    try {
      tempIn = content.openStream();
      tempOut = new FileOutputStream(tempFile);
      byte [] buffer = new byte [8192];
      int size; 
      while ((size = tempIn.read(buffer)) != -1) {
        tempOut.write(buffer, 0, size);
      }
    } finally {
      if (tempIn != null) {
        tempIn.close();
      }
      if (tempOut != null) {
        tempOut.close();
      }
    }
    return new TemporaryURLContent(tempFile.toURI().toURL());
  }
}
