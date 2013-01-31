/*
 * Contact, UbikSimIDE has been developed by:
 * 
 * Juan A. Botía , juanbot@um.es
 * Pablo Campillo, pablocampillo@um.es
 * Francisco Campuzano, fjcampuzano@um.es
 * Emilio Serrano, emilioserra@um.es 
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
package sim.app.ubik.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loads the Ubik class parameters for simulating a scenario.
 * Once created the class, use get methods to get the properties values.
 * 
 */
public class Configuration {
	public static String MODE = "mode";
	public static String CELL_SIZE = "cellSize";
	public static String FLOORS = "floors";
	public static String SEED = "seed";
	public static String IP_OCP = "ipOCP";
	public static String OCP = "ocp";
	public static String mobileIP = "mobileIP";
	public static String mobilePort = "mobilePort";
	public static String CAMERA_MODE = "cameraMode";
	public static String KEYBOARD_CONTROLED = "keyboardControlled";
	public static String INITIAL_DATE = "initialDate";

	private static String CIPB_CONFIGFILE = "config.props";
	protected Properties properties;

	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd hh:mm:ss");

	/**
	 * Loads the properties from the default file config.props
	 */
	public Configuration() {
		this(CIPB_CONFIGFILE);
	}
	
	/**
	 * Loads the properties from the given file path.
	 * 
	 * @param filePath
	 */
	public Configuration(String filePath) {
		try {
			properties = new Properties();
			// Para mostrar de donde leemos el fichero
			File f = new File(filePath);			
			if(f.exists()) {
				FileInputStream fis = new FileInputStream(f);
				properties.load(fis);
				fis.close();
			} else {
				System.err.println("Configuration: The file "+f.getAbsolutePath()+" doesn't exist.");
			}
		} catch (IOException ex) {
			Logger.getLogger(Configuration.class.getName()).log(Level.SEVERE,
					null, ex);
		}
	}

	/**
	 * obtiene una propiedad de la configuracion
	 * 
	 * @param key
	 *            clave
	 * @return valor o devuelve null si no se encuentra la clave
	 */
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	// No devuelve nulos
	public String getProperty2(String key) {
		String s = properties.getProperty(key);
		if (s == null) {
			return "";
		} else {
			return s;
		}
	}

	public void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}

	public boolean containsProperty(String key) {
		return properties.contains(key);
	}

	public Set<String> getPropertyNames() {
		return properties.stringPropertyNames();
	}

	public int getCellSize() {
		int result = 10;
		if (getProperty(Configuration.CELL_SIZE) != null) {
			result = Integer.parseInt(getProperty(Configuration.CELL_SIZE));
		}
		return result;
	}

	public long getSeed() {
		long seed = 0;
		if (getProperty(Configuration.SEED) != null) {
			if (getProperty(Configuration.SEED).equalsIgnoreCase("random")) {
				seed = System.currentTimeMillis();
			} else {
				seed = Integer.parseInt(getProperty(Configuration.SEED));

			}
		}
		return seed;
	}

	public boolean isOCP() {
		boolean useOCP = false;
		if (getProperty(Configuration.OCP) != null) {
			String ocp = getProperty(Configuration.OCP);
			if (ocp.equalsIgnoreCase("on") || ocp.equalsIgnoreCase("yes"))
				useOCP = true;
		}
		return useOCP;
	}

	public String getIpOCP() {
		String ipOCP = "127.0.0.1";
		if (getProperty(Configuration.IP_OCP) != null)
			ipOCP = getProperty(Configuration.IP_OCP);
		return ipOCP;
	}

	public String getPathScenario() {
		if (getProperty(Configuration.FLOORS) != null) {
			String allNames = getProperty(Configuration.FLOORS);
			String[] names = allNames.split(",");
			for (String n : names) {
				return n;
			}
		}
		return "";
	}

	public Date getInitialDate() {
		Date date = new Date();
		if (getProperty(Configuration.FLOORS) != null) {
			try {
				date = dateFormat.parse(getProperty(Configuration.INITIAL_DATE));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return date;
	}
}
