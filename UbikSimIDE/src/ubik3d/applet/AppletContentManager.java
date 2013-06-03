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
package ubik3d.applet;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ubik3d.model.HomeRecorder;
import ubik3d.model.RecorderException;
import ubik3d.model.UserPreferences;
import ubik3d.swing.FileContentManager;
import ubik3d.viewcontroller.View;


/**
 * Content manager for Sweet Home 3D files stored on server.
 * @author Emmanuel Puybaret
 */
public class AppletContentManager extends FileContentManager {
  private final HomeRecorder recorder;
  private final UserPreferences preferences;

  public AppletContentManager(HomeRecorder recorder, UserPreferences preferences) {
    super(preferences);
    this.recorder = recorder;
    this.preferences = preferences;  
  }
  
  /**
   * Returns the name of the content in parameter.
   */
  @Override
  public String getPresentationName(String contentName, 
                                    ContentType contentType) {
    if (contentType == ContentType.SWEET_HOME_3D) {
      return contentName;
    } else {
      return super.getPresentationName(contentName, contentType);
    }    
  }
  
  /**
   * Returns <code>true</code> if the content name in parameter is accepted
   * for <code>contentType</code>.
   */
  @Override
  public boolean isAcceptable(String contentName, 
                              ContentType contentType) {
    if (contentType == ContentType.SWEET_HOME_3D) {
      return true;
    } else {
      return contentType != ContentType.PLUGIN 
          && super.isAcceptable(contentName, contentType);
    }    
  }
  
  /**
   * Returns the name chosen by user with an open dialog.
   * @return the name or <code>null</code> if user canceled its choice.
   */
  @Override
  public String showOpenDialog(View        parentView,
                               String      dialogTitle,
                               ContentType contentType) {
    if (contentType == ContentType.SWEET_HOME_3D) {
      String [] availableHomes = null;
      if (this.recorder instanceof HomeAppletRecorder) {
        try {
          availableHomes = ((HomeAppletRecorder)this.recorder).getAvailableHomes();
        } catch (RecorderException ex) {
          String errorMessage = this.preferences.getLocalizedString(
              AppletContentManager.class, "showOpenDialog.availableHomesError");
          showError(parentView, errorMessage);
          return null;
        }
      }    
      
      if (availableHomes != null && availableHomes.length == 0) {
        String message = this.preferences.getLocalizedString(
            AppletContentManager.class, "showOpenDialog.noAvailableHomes");
        JOptionPane.showMessageDialog(SwingUtilities.getRootPane((JComponent)parentView), 
            message, getFileDialogTitle(false), JOptionPane.INFORMATION_MESSAGE);
        return null;
      } else {
        String message = this.preferences.getLocalizedString(
            AppletContentManager.class, "showOpenDialog.message");
        return (String)JOptionPane.showInputDialog(SwingUtilities.getRootPane((JComponent)parentView), 
            message, getFileDialogTitle(false), JOptionPane.QUESTION_MESSAGE, null, availableHomes, null);
      }
    } else {
      return super.showOpenDialog(parentView, dialogTitle, contentType);
    }
  }
  
  /**
   * Returns the name chosen by user with a save dialog.
   * If this name already exists, the user will be prompted whether 
   * he wants to overwrite this existing name. 
   * @return the chosen name or <code>null</code> if user canceled its choice.
   */
  @Override
  public String showSaveDialog(View        parentView,
                               String      dialogTitle,
                               ContentType contentType,
                               String      name) {
    if (contentType == ContentType.SWEET_HOME_3D) {
      String message = this.preferences.getLocalizedString(
          AppletContentManager.class, "showSaveDialog.message");
      String savedName = (String)JOptionPane.showInputDialog(SwingUtilities.getRootPane((JComponent)parentView), 
          message, getFileDialogTitle(true), JOptionPane.QUESTION_MESSAGE, null, null, name);
      if (savedName == null) {
        return null;
      }
      savedName = savedName.trim();
  
      try {
        // If the name exists, prompt user if he wants to overwrite it
        if (this.recorder.exists(savedName)
            && !confirmOverwrite(parentView, savedName)) {
          return showSaveDialog(parentView, dialogTitle, contentType, savedName);
        // If name is empty, prompt user again
        } else if (savedName.length() == 0) {
          return showSaveDialog(parentView, dialogTitle, contentType, savedName);
        }
        return savedName;
      } catch (RecorderException ex) {
        String errorMessage = this.preferences.getLocalizedString(
            AppletContentManager.class, "showSaveDialog.checkHomeError");
        showError(parentView, errorMessage);
        return null;
      }
    } else {
      return super.showSaveDialog(parentView, dialogTitle, contentType, name);
    }
  }
  
  /**
   * Shows the given <code>message</code> in an error message dialog. 
   */
  private void showError(View parentView, String message) {
    String title = this.preferences.getLocalizedString(
        AppletContentManager.class, "showError.title");
    JOptionPane.showMessageDialog(SwingUtilities.getRootPane((JComponent)parentView), 
        message, title, JOptionPane.ERROR_MESSAGE);    
  }
}
