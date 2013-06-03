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
package ubik3d.plugin;

import javax.swing.undo.UndoableEditSupport;

import ubik3d.model.Home;
import ubik3d.model.UserPreferences;


/**
 * The super class of a plug-in.
 * Subclasses should implement {@link #getActions() getActions} method
 * to return the actions that will be available to user.
 * This class should be packed in a JAR file with a family of properties file named 
 * <code>ApplicationPlugin.properties</code> at its root or in one of its subdirectories. 
 * This file describes a plug-in with the following keys (all of them are mandatory):
 * <ul><li>The <code>name</code> key specifies the name of the plug-in.</li>
 *     <li>The <code>class</code> key specifies the fully qualified class name
 *     of the plug-in.</li>
 *     <li>The <code>description</code> key specifies the description of 
 *     the plug-in.</li>
 *     <li>The <code>version</code> key specifies the version of the plug-in.
 *     <li>The <code>license</code> key specifies the license under which
 *     the plug-in is distributed.</li>
 *     <li>The <code>provider</code> key specifies the provider, the developer
 *     and/or the editor of the plug-in.</li>    
 *     <li>The <code>applicationMinimumVersion</code> key specifies the 
 *     minimum application version under which this plug-in may work. Note that
 *     only the first two groups of digits will be used for the comparison
 *     with current JVM version, and that plug-ins were available from
 *     version 1.5.</li>
 *     <li>The <code>javaMinimumVersion</code> key specifies the 
 *     minimum Java version under which this plug-in may work. Note that
 *     only the first two groups of digits will be used for the comparison
 *     with current JVM version.</li></ul>    
 * <br>For example, a plug-in class named <code>com.mycompany.mypackage.MyPlugin</code> 
 * will become a plug-in if it's packed in a JAR file with the following 
 * <code>ApplicationPlugin.properties</code> file: 
 * <pre> name=My plug-in
 * class=com.mycompany.mypackage.MyPlugin
 * description=This plug-in rocks!
 * version=1.0
 * license=GNU GPL
 * provider=MyCompany
 * applicationMinimumVersion=1.5
 * javaMinimumVersion=1.5</pre>
 * @author Emmanuel Puybaret
 */
public abstract class Plugin {
  private ClassLoader         pluginClassLoader;
  private String              name;
  private String              description;
  private String              version;
  private String              license;
  private String              provider;
  private UserPreferences     userPreferences;
  private Home                home;
  private UndoableEditSupport undoableEditSupport;

  /**
   * Sets the class loader used to load this plug-in.
   */
  final void setPluginClassLoader(ClassLoader pluginClassLoader) {
    this.pluginClassLoader = pluginClassLoader;
  }

  /**
   * Returns the class loader used to load this plug-in.
   */
  public final ClassLoader getPluginClassLoader() {
    return this.pluginClassLoader;
  }

  /**
   * Sets the name of this plug-in.
   */
  final void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the name of this plug-in.
   */
  public final String getName() {
    return this.name;
  }
  
  /**
   * Sets the description of this plug-in.
   */
  final void setDescription(String description) {
    this.description = description;
  }

  /**
   * Returns the description of this plug-in.
   */
  public final String getDescription() {
    return this.description;
  }
  
  /**
   * Sets the version of this plug-in.
   */
  final void setVersion(String version) {
    this.version = version;
  }

  /**
   * Returns the version of this plug-in.
   */
  public final String getVersion() {
    return this.version;
  }
  
  /**
   * Sets the license of this plug-in.
   */
  final void setLicense(String license) {
    this.license = license;
  }

  /**
   * Returns the license of this plug-in.
   */
  public final String getLicense() {
    return this.license;
  }
  
  /**
   * Sets the provider of this plug-in.
   */
  final void setProvider(String provider) {
    this.provider = provider;
  }

  /**
   * Returns the provider of this plug-in.
   */
  public String getProvider() {
    return this.provider;
  }
  
  /**
   * Sets the user preferences of the current application.
   */
  final void setUserPreferences(UserPreferences userPreferences) {
    this.userPreferences = userPreferences;    
  }
  
  /**
   * Returns the user preferences of the current application.
   */
  public final UserPreferences getUserPreferences() {
    return this.userPreferences;
  }

  /**
   * Sets the home associated to this plug-in instance.
   */
  final void setHome(Home home) {
    this.home = home;
  }

  /**
   * Returns the home associated to this plug-in instance.
   */
  public final Home getHome() {
    return this.home;
  }
  
  /**
   * Sets the undoable edit support that records undoable modifications made on a home. 
   */
  final void setUndoableEditSupport(UndoableEditSupport undoableEditSupport) {
    this.undoableEditSupport = undoableEditSupport;
  }

  /**
   * Returns the undoable edit support that records undoable modifications made on a home. 
   */
  public final UndoableEditSupport getUndoableEditSupport() {
    return this.undoableEditSupport;
  }
 
  /**
   * This method will be called when the home referenced by this plug-in will be deleted.
   * Subclasses may override it to free resources associated to this plug-in.
   */
  public void destroy() {    
  }
  
  /**
   * Returns the actions available on this plug-in. 
   * These actions may define the properties defined by 
   * {@link PluginAction.Property PluginAction.Property} enumeration.
   */
  public abstract PluginAction [] getActions();
}
