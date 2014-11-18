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
package sim.app.ubik.representation;

import java.awt.Color;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.app.ubik.Ubik;
import sim.app.ubik.people.Person;
import sim.app.ubik.people.Portable;
import sim.util.Int2D;
import ubik3d.model.HomePieceOfFurniture;

/**
 *
 * Clase pensada para observar el estado de una persona
 * (sim.app.ubik.people.Person.getState()) y en función del estado representarlo
 * con objetos 3D sobre su cabeza.
 *
 * Por defecto existen los estados StatePersonRepresentation.STATE_X donde X =
 * {0,...,9}. Cuando un personaje modifica su estado, p.e. person.setState("0"),
 * notifica a esta clase a través del método notify(). Si el estado se reconoce
 * se mostrará el objeto 3D asociado al estado sobre el personaje. En el caso
 * por defecto será un número correspondiente al estado sobre el personaje.
 *
 * Se pueden añadir objetos 3D asociados a estados utilizando el método estático
 * addState().
 *
 * Todas las clases que hereden de Person disponen al menos de este
 * comportamiento. En el método PersonHandler.createPerson(...) se añade este
 * observador a la persona.
 *
 *
 *
 */
public class PersonStateRepresentation extends Representation {

    public static final String STATE_0 = "0";
    public static final String STATE_1 = "1";
    public static final String STATE_2 = "2";
    public static final String STATE_3 = "3";
    public static final String STATE_4 = "4";
    public static final String STATE_5 = "5";
    public static final String STATE_6 = "6";
    public static final String STATE_7 = "7";
    public static final String STATE_8 = "8";
    public static final String STATE_9 = "9";
    public static final String STATE_DOT = ".";
    // Pares nombre del estado y item Portable (para situarlo sobre el personaje) que contiene el objeto 3D
    private static Map<String, ItemStateRepresentationPerson> globalRepresentations = new HashMap<String, ItemStateRepresentationPerson>();

    public static void initDefaultStates(Ubik ubik) {
        addState(STATE_0, "Numbers/0.obj");
        addState(STATE_1, "Numbers/1.obj");
        addState(STATE_2, "Numbers/2.obj");
        addState(STATE_3, "Numbers/3.obj");
        addState(STATE_4, "Numbers/4.obj");
        addState(STATE_5, "Numbers/5.obj");
        addState(STATE_6, "Numbers/6.obj");
        addState(STATE_7, "Numbers/7.obj");
        addState(STATE_8, "Numbers/8.obj");
        addState(STATE_9, "Numbers/9.obj");
        addState(STATE_DOT, "Numbers/dot.obj");
    }
    protected ItemStateRepresentationPerson actualStateRepresentation;

    public PersonStateRepresentation(Ubik ubik, Person person) {
        super(ubik, null);
        if (person.getState() != null && globalRepresentations.get(person.getState()) != null) {
            actualStateRepresentation = (ItemStateRepresentationPerson) globalRepresentations.get(person.getState()).clone();
            // Si existe representación
            if (actualStateRepresentation != null) {
                actualModel = actualStateRepresentation.getVisualObject();
                resizeToMaxDimension(maxValueOfAnyDimension);
                actualModel.setVisible(true);
                person.addObjectToBag(actualStateRepresentation);
                actualStateRepresentation.moveTo(person.getPerson3DModel().getX(), person.getPerson3DModel().getY());
                ubik.getHomes().get(person.getFloor()).addPieceOfFurniture(actualModel);
            }
        }
    }

    /**
     * Cada vez que se modifica el estado de la persona se llama a este método.
     */
    @Override
    public void update(Observable o, Object arg) {
        Person p = (Person) o;

        // Sólo nos interesa cuando cambia de estado
        if (actualStateRepresentation != null && actualStateRepresentation.equals(p.getState())) {
            return;
        }

        if (actualStateRepresentation != null) {
            p.removeObjectFromBag(actualStateRepresentation);
            ubik.getHomes().get(p.getFloor()).deletePieceOfFurniture(actualStateRepresentation.getVisualObject());
        }

        if (p.getState() == null || p.getState().equals("")) {
            return;
        }
        // Se devuelve una copia del objeto para que no sea el mismo para todas las personas.
        if (globalRepresentations.get(p.getState()) != null) {
            actualStateRepresentation = (ItemStateRepresentationPerson) globalRepresentations.get(p.getState()).clone();
        }

        // Si existe representación
        if (actualStateRepresentation != null) {
            actualModel = actualStateRepresentation.getVisualObject();
            resizeToMaxDimension(maxValueOfAnyDimension);
            actualModel.setVisible(true);
            p.addObjectToBag(actualStateRepresentation);
            actualStateRepresentation.moveTo(p.getPerson3DModel().getX(), p.getPerson3DModel().getY());
            ubik.getHomes().get(p.getFloor()).addPieceOfFurniture(actualModel);
        }
    }

    /**
     * Devuelve el nombre de los estados representables.
     *
     * @return
     */
    public static Set<String> getStates() {
        return globalRepresentations.keySet();
    }

    /**
     * Cambia el color del objeto que representa el estado stateName.
     *
     * @param stateName
     * @param color
     */
    public static void setColor(String stateName, Color color) {
        ItemStateRepresentationPerson isrp = globalRepresentations.get(stateName);
        if (isrp != null) {
            isrp.getVisualObject().setColor(color.getRGB());
        }
    }

    /**
     * Añade un representación nueva para un estado dado.
     *
     * @param name
     * @param pathOfObject
     * @throws MalformedURLException
     */
    public static void addState(String name, String pathOfObject) {
        HomePieceOfFurniture hpof;
        try {
            hpof = Representation.createModel(name, PersonStateRepresentation.class, pathOfObject);
            globalRepresentations.put(name, new ItemStateRepresentationPerson(name, hpof));
        } catch (IOException ex) {
            Logger.getLogger(PersonStateRepresentation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Representa un objeto que lleva la persona (Portable) pero en este caso no
     * es un objeto físico sino referente al estado de la misma.
     */
    public static class ItemStateRepresentationPerson implements Portable, Cloneable {

        private String name;
        private HomePieceOfFurniture model;
        private Int2D displacement;

        /**
         * Construye una representación del estado como objeto portable.
         *
         * @param name
         * @param hpof
         * @param displacement Si se desea que el objeto no se situe sobre la
         * persona sino con cierto desplazamiento (en cm).
         */
        public ItemStateRepresentationPerson(String name, HomePieceOfFurniture hpof, Int2D displacement) {
            this.name = name;
            this.model = hpof;
            this.displacement = displacement;
        }

        public ItemStateRepresentationPerson(String name, HomePieceOfFurniture hpof) {
            this(name, hpof, new Int2D(0, 0));
        }

        /**
         * Método de la interfaz portable para devolver la representación del
         * objeto.
         */
        @Override
        public HomePieceOfFurniture getVisualObject() {
            return this.model;
        }

        /**
         * Método de la interfaz portable. Cada vez que se desplaza la persona
         * se llama a este método para que se desplace también los objetos que
         * lleva consigo.
         */
        @Override
        public void moveTo(float x, float y) {
            if (model != null) {
                model.setX(x + displacement.x);
                model.setY(y + displacement.y);
            }
        }

        /**
         * Método de la interfaz portable. Cada vez que se reordena la pila de
         * objetos sobre la cabeza de la persona.
         */
        @Override
        public void elevate(float elevation) {
            model.setElevation(elevation);
        }

        public Object clone() {
            ItemStateRepresentationPerson irp = new ItemStateRepresentationPerson(this.name, this.model.clone(), new Int2D(this.displacement.x, this.displacement.y));
            return irp;
        }

        public String getName() {
            return name;
        }
    }
}
