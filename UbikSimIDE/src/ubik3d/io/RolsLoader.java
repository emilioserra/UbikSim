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
package ubik3d.io;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase encargada de cargar los rols desde los ficheros localizados en las
 * rutas de filePaths.
 * Está implementada siguiendo el patrón Singleton.
 * Utilizar el método estático getInstace() para obtener la instancia.
 *
 * @author Juan A. Botía, Pablo Campillo, Francisco Campuzano, and Emilio
 * Serrano
 */
public class RolsLoader {

    private static RolsLoader rolsLoader = null;
    private Map<String, RolCategory> rols;

    public static RolsLoader getInstance() {
        if (rolsLoader == null) {
            rolsLoader = new RolsLoader();
        }
        return rolsLoader;
    }

    private String actualRol;
    private String actualCategory;
    
    private RolsLoader() {
        rols = new HashMap<String, RolCategory>();
        readRols();
    }

    /**
     * Dado el nombre de una categoría devuelve la lista de rols
     * @param categoryName
     * @return 
     */
    public List<String> getRols(String categoryName) {
        System.out.println("getRols("+categoryName+")");
        List<String> r =  rols.get(categoryName).getRols();
        Collections.sort(r);
        for(String s: r) {
            System.out.print(s+", ");
        }
        System.out.println("");
        return r;
    }
    
    /**
     * Devuelve la lista de categorías
     * @return 
     */
    public List<String> getCagegories() {
        List<String> result = new ArrayList<String>(rols.keySet());
        Collections.sort(result);
        return result;
    }
    
    /**
     * Devuelve la categoría dado el nombre del rol
     * @param rolName
     * @return 
     */
    public String getCagegoryOf(String rolName) {
        for(String category: rols.keySet()) {
            if(getRols(category).contains(rolName))
                return category;
        }
        return null;
    }

    /**
     * Establece la categoría y rol actual para después, si se 
     * aceptan los cambios se almacena el rol en el fichero.
     * 
     * @param categoryName
     * @param rol 
     */
    public void setActual(String categoryName, String rol) {
        System.out.println("setActual("+categoryName+", "+rol+")");
        this.actualCategory = categoryName;
        this.actualRol = rol;
    }
    
    /**
     * Añade el rol a la categoría que se especificó con setActual(category, rol)
     * y lo guarda en el fichero.
     */
    public void commit() {
        if(actualCategory != null && actualRol != null)
            rols.get(actualCategory).add(actualRol);
    }

    public void readRols() {
        System.out.println("readRols()");
        rols.clear();
        RolCategory rc = new RolCategory("handlers/persons.txt");
        rols.put(rc.getName(), rc);
        rc = new RolCategory("handlers/devices.txt");
        rols.put(rc.getName(), rc);
        rc = new RolCategory("handlers/furnitures.txt");
        rols.put(rc.getName(), rc);
    }

    /** 
     * Representa la categoría de un rol
     */
    private class RolCategory {

        private String filePath;
        private List<String> rols;
        private String name;

        public RolCategory(String filePath) {
            this.filePath = filePath;
            rols = new ArrayList<String>();

            int firstIndex = filePath.lastIndexOf("/")+1;
            int lastIndex = filePath.indexOf(".txt");
            name = filePath.substring(firstIndex, lastIndex);
            
            readRols();
        }

        public void add(String rolName) {
            if(!rols.contains(rolName)) {
                rols.add(rolName);
                writeRols();
            }
        }

        public List<String> getRols() {
            return rols;
        }

        public String getName() {
            return name;
        }

        /**
         * Lee los rols del fichero
         */
        public void readRols() {
            readRolsFromFile(filePath);
        }

        /**
         * Machaca el fichero de los roles con los actuales
         */
        public void writeRols() {
            writeRolsToFile();
        }

        /**
         * Lee los rols que contiene el fichero en la ruta rolsFile
         * @param rolsFile 
         */
        private void readRolsFromFile(String rolsFile) {
            try {
                FileReader fr = new FileReader(rolsFile);
                BufferedReader br = new BufferedReader(fr);
                String s;
                while ((s = br.readLine()) != null) {
                    if (!s.equals("")) {
                        rols.add(s);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void writeRolsToFile() {
            try {
                FileWriter fw = new FileWriter(filePath, false);
                BufferedWriter bw = new BufferedWriter(fw);
                String fileText = getString();
                System.out.println("File \n"+fileText);
                bw.write(fileText);
                bw.flush();
                bw.close();
                fw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private String getString() {
            String result = "";
            for (String rol : rols) {
                result += rol + "\n";
            }
            return result;
        }
    }
}
