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
package com.eteks.ubikeditor.plugin;

import sim.app.ubik.Ubik;
import sim.app.ubik.UbikSimWithUI;
import sim.app.ubik.utils.Configuration;
import sim.display.Console;
import ubik3d.plugin.Plugin;
import ubik3d.plugin.PluginAction;

public class UbikSimPlugin extends Plugin {

    private static Plugin plugin;

    @Override
    public PluginAction[] getActions() {
        setPlugin(this);
        // TODO Auto-generated method stub
        return new PluginAction[]{new SimulationAction()};
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static void setPlugin(Plugin plugin) {
        UbikSimPlugin.plugin = plugin;

    }

    public class SimulationAction extends PluginAction {

        public SimulationAction() {
            putPropertyValue(Property.NAME, "Simulation");
            putPropertyValue(Property.MENU, "Tools");
            // Enables the action by default
            setEnabled(true);
        }

        @Override
        public boolean isEnabled() {
            return getHome() != null && getHome().getRooms() != null && getHome().getRooms().size() > 0;
        }

        @Override
        public void execute() {
            Configuration configuration = new Configuration();
            String home = getHome().getName();
            configuration.setPathScenario(home);

            Ubik ubik = new Ubik(configuration);
            UbikSimWithUI vid = new UbikSimWithUI(ubik);
            Console c = new Console(vid);
            c.setIncrementSeedOnStop(false);
            c.setVisible(true);
        }
    }
}