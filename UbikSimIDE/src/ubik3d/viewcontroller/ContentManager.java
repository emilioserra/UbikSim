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
package ubik3d.viewcontroller;

import ubik3d.model.Content;
import ubik3d.model.RecorderException;

/**
 * Content manager.
 * @author Emmanuel Puybaret
 */
public interface ContentManager {
  public enum ContentType {SWEET_HOME_3D, MODEL, IMAGE, SVG, OBJ, PNG, MOV, PDF, LANGUAGE_LIBRARY, TEXTURES_LIBRARY, FURNITURE_LIBRARY, PLUGIN, USER_DEFINED};

  /**
   * Returns a {@link Content content} object that references a given content name.
   */
  public abstract Content getContent(String contentName) throws RecorderException;

  /**
   * Returns a human readable string for a given content name.
   */
  public abstract String getPresentationName(String contentName,
                                             ContentType contentType);

  /**
   * Returns <code>true</code> if the content name in parameter is accepted
   * for <code>contentType</code>.
   */
  public abstract boolean isAcceptable(String contentName,
                                       ContentType contentType);

  /**
   * Returns the content name chosen by user with an open content dialog.
   * @return the chosen content name or <code>null</code> if user canceled its choice.
   */
  public abstract String showOpenDialog(View parentView,
                                        String dialogTitle,
                                        ContentType contentType);

  /**
   * Returns the content name chosen by user with a save content dialog.
   * If the returned name already exists, this method should have confirmed 
   * if the user wants to overwrite it before return. 
   * @return the chosen content name or <code>null</code> if user canceled its choice.
   */
  public abstract String showSaveDialog(View parentView,
                                        String dialogTitle,
                                        ContentType contentType,
                                        String name);
}