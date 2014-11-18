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
package sim.app.ubik.people;

import java.awt.Color;
import java.io.*;
import java.util.*;
import java.util.Properties;
import java.util.logging.Logger;

import ocp.service.ContextEntityItems;
import ocp.service.ContextItemFloat;
import ocp.service.ContextItemString;
import ocp.service.ContextService;
import sim.engine.*;
import sim.util.*;
import sim.portrayal.Oriented2D;
import ubik3d.model.HomePieceOfFurniture;
import sim.app.ubik.MovementTools;
import sim.app.ubik.Ubik;
import org.rosuda.JRI.REXP;
import sim.app.ubik.R.RInterface;
import sim.app.ubik.behaviors.Automaton;
import sim.app.ubik.behaviors.PositionTools;
import sim.app.ubik.people.PersonBag.objectPosition;
import sim.app.ubik.representation.Label;
import ubik3d.model.Camera;

abstract public class Person extends Observable implements Steppable, Stoppable, Oriented2D {

    private static int IDgenerator = 1;
    protected int id;					// Identificador único (utilizado principalmente para OCP)
    protected String name;
    protected Ubik ubik;
    protected int bodyRadio = 15;  			// Radio del cuerpo de la persona en centimetros (utilizado para colisiones)
    protected HomePieceOfFurniture person3DModel;	// Modelo 3D de la persona
    protected MutableInt2D lastPosition;
    protected MutableInt2D position;
    protected int cellSize;				// Tamaño de la celda de epacial en centimetros
    protected PersonBag objectsCarried;			// Objetos que transporta la persona (que implementen la interfaz Portable)
    protected int floor;   				// Planta que ocupa la persona
    protected double angle;     			// Orientación de la persona en radians
    protected double speed;                             // meters/seconds
    protected int behavior;
    protected boolean isMoving; 			// La persona se esta moviendo, aunque no se desplace (para sensor de presencia).
    public Automaton automaton; 			// Automata de comportamiento
    protected Stoppable stoppable;			//
    protected boolean stopped = false;
    protected KeyboardControlledByPerson keyControlPerson;	// Objeto que controla la persona. Si es distinto de null, 
    // no se realiza comportamiento automático.
    protected float weigth;							// Peso de la persona
    protected List<Person.WeightItem> weightModel;					// Modelo espacial de la distribución del peso de la persona.

    protected String state;					// Estado de la persona
    protected Label label;
    /**
     * Activar visión subjetica
     */
    protected boolean subjectiveView;

    public static void clearIDGenerator() {
        IDgenerator = 0;
    }

    /**
     * Turno de ejecución de MASON. Definir las acciones que desa hacer en un
     * turno.
     *
     * @param state Estado de la simulación (de tipo Ubik).
     */
    @Override
    public void step(SimState state) {
        // si keyControlPerson es distinto de un se cede el control al teclado.
        if (keyControlPerson != null) {
            keyControlPerson.step(state);
        }
    }

    public String toString() {
        return this.getName();
    }

    public Person(int floor, HomePieceOfFurniture person3DModel, Ubik ubik) {
        this.person3DModel = person3DModel;
        this.floor = floor;
        this.cellSize = ubik.getCellSize();
        this.position = new MutableInt2D();
        this.lastPosition = new MutableInt2D();
        this.name = person3DModel.getName();
        this.ubik = ubik;
        this.isMoving = false;

        bodyRadio /= cellSize;  // Escalamos al tamaño de la celda

        this.id = IDgenerator++;

        angle = person3DModel.getAngle();
        speed = 0.5;  // velocidad de 1 metro/segundo

        objectsCarried = new PersonBag(this);
        setPosition((int) (person3DModel.getX() / cellSize), (int) (person3DModel.getY() / cellSize));

        // Lee las properties del campo metadata de UbikEditor (name=value)
        if (person3DModel.getMetadata() != null) {
            Properties properties = new Properties();

            StringReader sr = new StringReader(person3DModel.getMetadata());
            try {
                properties.load(sr);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String value = properties.getProperty("weight");
            if (value != null) {
                weigth = Float.parseFloat(value);
            }
        } else {
            // Si no se especifica nada en el Metadata se 
            // inicializa con los valores por defecto
            weigth = 70.0f;
        }

    }

    /**
     * En Mason, cuando un evento se pone a repetir en schedule, se devuelve un
     * Stoppable. Llamando a stop ese agente deja de tener el turno para
     * ejecutarse. En el caso de trabajadores, el fuego con intensidad
     * suficiente puede "pararlos"
     *
     * @param stoppable
     */
    public void fixStopable(Schedule schedule) {
        stoppable = schedule.scheduleRepeating(this);
    }

    @Override
    public void stop() {
        person3DModel.setVisible(false);
        ubik.getBuilding().getFloor(floor).getPersonHandler().remove(this);
        if (stoppable != null) {
            stopped = true;
            stoppable.stop();   
            LOG.info(name + " has been stopped");
            
        }
    }
    private static final Logger LOG = Logger.getLogger(Person.class.getName());
    

    @Override
    public double orientation2D() {
        return this.angle;
    }

    /**
     * Da de alta la entidad Person en OCP
     *
     * @param cs
     */
    public void createInOCP(ContextService cs) {
        ContextEntityItems cei = new ContextEntityItems(getClass().getSimpleName(), String.valueOf(getId()));
        cei.addContextItem(new ContextItemString("ubikName", getName()));
        cei.addContextItem(new ContextItemString("name", name));
        cei.addContextItem(new ContextItemString("state", "Normal"));
        cei.addContextItem(new ContextItemFloat("weight", weigth));
        cs.setContextItems(cei);
    }

    public int getBehavior() {
        return behavior;
    }

    public HomePieceOfFurniture getPerson3DModel() {
        return person3DModel;
    }

    public MutableInt2D getPosition() {
        return position;
    }

    public void setAngle(double angle) {
        this.angle = angle;
        setChanged();
        person3DModel.setAngle((float) (angle - Math.PI / 2.0));
        notifyObservers(this);
    }

    public int getSpeedInCell() {
        return (int) Math.round((speed * 100 / cellSize)/*
         * ubik.getSpeed()
         */);
    }
    // distancia acumulativa.
    // Si el desplazamiento es menor que el tamaño de la celda
    // se almazena y la próxima vez se suma ambos desplazamientos.
    // Necesario para celdas muy grandes y/o velocidades muy lentas.
    double distance = 0.0;

    /**
     * Dada la velocidad de la persona, su orientación (angle) y considerando
     * que cada step es un segundo. Desplaza a la persona hasta el punto
     * indicado comprobando si hay obstáculos de por medio.
     *
     * @return true si se pudo realizar el movimiento y false si hay un
     * obstáculo.
     */
    public boolean move() {
        double lastDist = distance;
        distance += getSpeedInCell();
        if (distance >= cellSize) {
            for (int i = 1; i <= distance; i++) {
                int x = (int) Math.round(position.x + i * Math.cos(angle));
                int y = (int) Math.round(position.y + i * Math.sin(angle));

                if (PositionTools.isObstacle(this, x, y)) {
                    //System.out.println("OBSTACULO!!!");
                    distance = lastDist;
                    return false;
                }
            }
            int x = (int) Math.round(position.x + distance * Math.cos(angle));
            int y = (int) Math.round(position.y + distance * Math.sin(angle));
            distance = 0;
            return setPosition(x, y);
        }
        return true;
    }

    /**
     * Mueve a la persona hacia el punto x,y en línea recta. Para ello, la
     * orienta dada su posición y la destino, y luego llama al método move.
     *
     * @param x
     * @param y
     * @return true si pudo realizar el movimiento.
     */
    public boolean move(int x, int y) {
        setAngle(MovementTools.getInstance().directionInRadians(position.x, position.y, x, y));
        double distance = getSpeedInCell();
        // Si el punto está muy cerca, directamente se sitúa en el punto.
        if (position.distance(x, y) < distance) {
            return setPosition(x, y);
        }
        return move();
    }

    /**
     * Teletransporta a la persona al punto x,y indicado.
     *
     * @param x
     * @param y
     * @return true si la posición no está ocupada.
     */
    public boolean setPosition(int x, int y) {
        if (ubik.getBuilding().getFloor(floor).isObstacle(this, x, y)) {
            return false;
        }
        this.lastPosition.x = this.position.x;
        this.lastPosition.y = this.position.y;
        this.position.x = x;
        this.position.y = y;
        this.angle = MovementTools.getInstance().directionInRadians(lastPosition.x, lastPosition.y, position.x, position.y);
        person3DModel.setAngle((float) (angle - Math.PI / 2.0));
        person3DModel.setX(this.position.x * cellSize);
        person3DModel.setY(this.position.y * cellSize);
        ubik.getBuilding().getFloor(0).getPersonHandler().getGrid().setObjectLocation(this, position.x, position.y);
        objectsCarried.updatePosition();
        setChanged();
        notifyObservers(this);
        if (subjectiveView) {
            changeCameraView();
        }
        return true;
    }

    /**
     *
     * @param b
     */
    public void setSubjectiveView(boolean b) {
        this.subjectiveView = b;
        if (b) {
            this.getPerson3DModel().setVisible(false);
        } // Hacemos invisible a la persona
        else {
            this.getPerson3DModel().setVisible(true);

        }
    }

    /**
     * Cambiar la visión de la camara a la persona
     *
     * @param x
     * @param y
     */
    public void changeCameraView() {

        Camera camera = ubik.getHomes().get(0).getCamera();
        camera.setX(getPerson3DModel().getX());
        camera.setY(getPerson3DModel().getY());
        camera.setZ(getPerson3DModel().getHeight());
        camera.setYaw(getPerson3DModel().getAngle());
        //System.out.println(1);
        camera.setPitch((float) 0.15);
    }

    public boolean setPosition(MutableInt2D position) {
        return setPosition(position.x, position.y);
    }

    /**
     * Añade un objeto Portable a la persona. Dicho objeto se sitúa encima de la
     * cabeza.
     *
     * @param p
     */
    public void addObjectToBag(Portable p) {
        objectsCarried.addPortableObject(p);
    }

    /**
     * Añade un objeto Portable a la persona. Incluye un parámetro para indicar
     * si se sitúa encima de la cabeza o delante
     *
     * @param p
     * @param elevation
     */
    public void addObjectToBag(Portable p, objectPosition position) {
        objectsCarried.addPortableObject(p, position);
    }

    /**
     * Elimina un objeto Portable a la persona. Dicho objeto se quita de encima
     * de la cabeza.
     *
     * @param p
     */
    public void removeObjectFromBag(Portable p) {
        objectsCarried.removePortableObject(p);
    }

    /**
     * Devuelve true si el objeto Portable p lo transporta la persona.
     *
     * @param p
     * @return
     */
    public boolean contains(Portable p) {
        return objectsCarried.contains(p);
    }

    /**
     * Devuelve el radio que se considera el volumen de la persona
     *
     * @return
     */
    public int getBodyRadio() {
        return bodyRadio;
    }

    public int getId() {
        return id;
    }

    public boolean isObstacle(Person p) {
        if (MovementTools.getInstance().getDistance(position.x, position.y, p.getPosition().x, p.getPosition().y) <= bodyRadio + p.getBodyRadio()) {
            return true;
        }
        return false;
    }

    /**
     * Devuelve true si el punto x,y se encuentra dentro del espacio de la
     * persona
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isBodyRadioIn(int x, int y) {
        if (MovementTools.getInstance().getDistance(position.x, position.y, x, y) <= bodyRadio) {
            return true;
        }
        return false;
    }

    public int getFloor() {
        return floor;
    }

    public PersonBag getObjectsCarried() {
        return objectsCarried;
    }

    public String getName() {
        return name;
    }

    public void setName(String s) {
        name = s;
    }

    public double getAngle() {
        return angle;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean estado) {
        isMoving = estado;
        setChanged();
        notifyObservers(this);
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double s) {
        this.speed = s;
        setChanged();
        notifyObservers(this);
    }

    public void setKeyControlPerson(KeyboardControlledByPerson p) {
        this.keyControlPerson = p;
    }

    public KeyboardControlledByPerson getKeyControlPerson() {
        return this.keyControlPerson;
    }

    @Deprecated
    protected LinkedList<Integer> generateTimes(String filename) {

        LinkedList<Integer> l = new LinkedList<Integer>();

        try {
            //Get function from file
            BufferedReader bf = new BufferedReader(new FileReader(filename));
            String function = bf.readLine();

            if (function.equals("0")) {
                l = new LinkedList();
                l.add(-1);
                return l;
            }

            //Connect to R
            REXP eval = RInterface.getInstance().processCommand(function);

            //To Integer List
            for (double d : eval.asDoubleArray()) {
                int i = (int) d;
                if (i == 0) {
                    i = 1;
                }
                if (i < 1440) {
                    l.add(i);
                }

            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return l;
    }

    @Deprecated
    protected void readTimes(String nombreFichero, LinkedList lista) {
        BufferedReader bf;
        String linea = "";
        String aux = "";
        String tiempo = "";
        try {
            bf = new BufferedReader(new FileReader(nombreFichero));
            try {
                linea = bf.readLine();
                aux = linea;
                while ((linea) != null) {
                    tiempo = aux.substring(aux.indexOf("]") + 1, aux.indexOf(".")).trim();
                    if (tiempo.equals("0")) {
                        tiempo = "1";
                    }
                    lista.add(Integer.parseInt(tiempo));
                    aux = aux.substring(aux.indexOf(".") + 1);

                    while (aux.contains(".")) {
                        tiempo = aux.substring(aux.indexOf(" ") + 1, aux.indexOf(".")).trim();
                        if (tiempo.equals("0")) {
                            tiempo = "1";
                        }
                        lista.add(Integer.parseInt(tiempo));
                        aux = aux.substring(aux.indexOf(".") + 1);
                    }

                    linea = bf.readLine();
                    aux = linea;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public Ubik getUbik() {
        return ubik;
    }

    public boolean isStopped() {
        return stopped;
    }

    public float getWeight() {
        return weigth;
    }

    /**
     * Devuelve una lista de WeightItem Lista de cooredenadas y valor entre 0.0
     * y 1.0 indicando como se distribuye el peso.
     *
     * La suma total de todas las properciones de peso debe ser 1.
     *
     * @return lista de puntos con la distribuci�n del peso
     */
    public List<Person.WeightItem> getWeightModel() {
        // Modelo de distribuci�n del peso
        int diff = Math.round(10/*
                 * cm
                 */ / cellSize);
        weightModel = new ArrayList<Person.WeightItem>();
        weightModel.add(new Person.WeightItem(
                new Int2D((int) Math.round(getPosition().getX() + diff * Math.cos(angle)),
                        (int) Math.round(getPosition().getY() + diff * Math.sin(angle))), 0.25f));
        weightModel.add(new Person.WeightItem(
                new Int2D((int) Math.round(getPosition().getX() + diff * Math.cos(angle)),
                        getPosition().getY()), 0.25f));
        weightModel.add(new Person.WeightItem(
                new Int2D((int) Math.round(getPosition().getX() - diff * Math.cos(angle)),
                        (int) Math.round(getPosition().getY() + diff * Math.sin(angle))), 0.25f));
        weightModel.add(new Person.WeightItem(
                new Int2D((int) Math.round(getPosition().getX() - diff * Math.cos(angle)),
                        getPosition().getY()), 0.25f));
        return weightModel;
    }

    /**
     * Clase para representar qué properción (percent) del peso total se
     * distribuye en un punto (position) de la persona.
     *
     */
    public class WeightItem {

        private Int2D position;
        private float percent;

        /**
         * Crea un punto donde recae una proporción de peso de la persona.
         *
         * @param p Posición donde recae el peso
         * @param percent Properción de peso en dicho punto (valor entre 0 y 1)
         */
        public WeightItem(Int2D p, float percent) {
            this.position = p;
            this.percent = percent;
        }

        public Int2D getPosition() {
            return position;
        }

        public float getPercent() {
            return percent;
        }
    }

    /**
     * Cambia el color del objeto que representa la persona. Para que tenga su
     * aspecto original, pasar null como parámetro.
     *
     * @param c Color de la persona
     */
    public void setColor(Color c) {
        if (c == null) {
            this.getPerson3DModel().setColor(null);
        } else {
            this.person3DModel.setColor(c.getRGB());
        }
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
        setChanged();
        notifyObservers(this);
    }

    public float getWeigth() {
        return weigth;
    }

    public void setWeigth(float weigth) {
        this.weigth = weigth;
        setChanged();
        notifyObservers(this);
    }

    public Automaton getAutomaton() {
        return automaton;
    }

    public void setAutomaton(Automaton automaton) {
        this.automaton = automaton;
    }

    /**
     * A label is added to the subject
     *
     * @param label
     */
    public void addLabel(String label) {
        setLabel(new Label(label));
    }

    /**
     * The label is removed
     *
     * @param label
     */
    public void deleteLabel() {
        this.label = null;
    }

    /**
     * This method changes the label
     *
     * @param label
     */
    public void setLabel(Label label) {
        this.label = label;
        setChanged();
        notifyObservers(this);
    }

    /**
     * This method changes the color of the label
     *
     * @param label
     */
    public void setLabelColor(int color) {
        this.label.setColor(color);
    }

    /**
     * This method changes the font size of the label
     *
     * @param label
     */
    public void setLabelFontSize(float size) {
        this.label.setFontSize(size);
    }

    /**
     *
     * @return A portable object which represents a label
     */
    public Label getLabel() {
        return label;
    }

}
