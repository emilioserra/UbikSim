/*
 * UbikSim2 has been developed by:
 * 
 * Juan A. Botía , juanbot[at] um.es
 * Pablo Campillo, pablocampillo[at] um.es
 * Francisco Campuzano, fjcampuzano[at] um.es
 * Emilio Serrano, emilioserra [at] dit.upm.es
 * 
 * This file is part of UbikSimIDE.
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
package sim.app.ubik;

import java.io.File;
import java.io.IOException;
import ubik3d.SweetHome3D;
import ubik3d.io.FileUserPreferences;
import ubik3d.model.UserPreferences;
import ubik3d.plugin.PluginManager;
import ubik3d.plugin.UbikPluginManager;

/**
 * UbikEditor extends SweetHome3d in order to redefine getPluginManager().
 * This method creates an instance of UbikPluginManager instead of PluginManager.
 * 
 */
public class UbikEditor extends SweetHome3D {

    private static final String APPLICATION_PLUGINS_SUB_FOLDER = "plugins";
    protected boolean pluginManagerInitialized = false;
    protected PluginManager pluginManager;

    public UbikEditor() {
        super();
        getUserPreferences().setLanguage("en");
    }

    @Override
    protected PluginManager getPluginManager() {
        if (!this.pluginManagerInitialized) {
            try {
                UserPreferences userPreferences = getUserPreferences();
                if (userPreferences instanceof FileUserPreferences) {
                    File[] applicationPluginsFolders = ((FileUserPreferences) userPreferences).getApplicationSubfolders(APPLICATION_PLUGINS_SUB_FOLDER);
                    // Create the plug-in manager that will search plug-in files in plugins folders
                    // And will add the Simulation plugin (com.eteks.ubikeditor.plugin).
                    this.pluginManager = new UbikPluginManager(applicationPluginsFolders);
                }
            } catch (IOException ex) {
            }
            this.pluginManagerInitialized = true;
        }
        return this.pluginManager;
    }

    public static void main(String[] args) {
        UbikEditor ue = new UbikEditor();
        ue.init(args);
    }
}
