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

import com.eteks.ubikeditor.plugin.UbikSimPlugin;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.swing.undo.UndoableEditSupport;
import ubik3d.model.Home;
import ubik3d.model.HomeApplication;
import ubik3d.model.UserPreferences;
import ubik3d.plugin.Plugin;
import ubik3d.plugin.PluginManager;

/**
 * This class extends PluginManager to add the UbikSimPlugin in the getPlugin() method.
 * 
 */
public class UbikPluginManager extends PluginManager {

    /**
     * Reads application plug-ins from resources in the given plug-in folder.
     */
    public UbikPluginManager(File pluginFolder) {
        this(new File[]{pluginFolder});
    }

    /**
     * Reads application plug-ins from resources in the given plug-in folders.
     *
     * @since 3.0
     */
    public UbikPluginManager(File[] pluginFolders) {
        super(pluginFolders);
    }

    /**
     * Reads application plug-ins from resources in the given URLs.
     */
    public UbikPluginManager(URL[] pluginUrls) {
        super(pluginUrls);
    }

    /**
     * Returns a plugin list.
     * This methods adds UbikSimPlugin to the list crated by PluginManager
     * 
     * @param application
     * @param home
     * @param preferences
     * @param undoSupport
     * @return 
     */
    @Override
    public List<Plugin> getPlugins(final HomeApplication application,
            final Home home,
            UserPreferences preferences,
            UndoableEditSupport undoSupport) {
        List<Plugin> l = super.getPlugins(application, home, preferences, undoSupport);
        List<Plugin> list = new ArrayList<Plugin>(l);
        if (list == null) {
            list = new ArrayList<Plugin>();
        }
        
        // Creates and Adds UbikSimPlugin to the list
        Plugin ubikSimPlugin = new UbikSimPlugin();
        ubikSimPlugin.setUserPreferences(preferences);
        ubikSimPlugin.setHome(home);
        list.add(ubikSimPlugin);
        
        return list;
    }
}