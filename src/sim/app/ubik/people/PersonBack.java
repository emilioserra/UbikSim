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

import ocp.service.ContextService;
import sim.engine.*;
import sim.util.*;
import sim.portrayal.Oriented2D;
import sim.field.grid.SparseGrid2D;
import ubik3d.model.HomePieceOfFurniture;
import sim.app.ubik.MovementTools;
import sim.app.ubik.Ubik;


abstract public class PersonBack extends Person implements Steppable, Stoppable, Oriented2D {

    private static int IDgenerator = 1;
    protected int id;
    protected String name;
    protected Ubik ubik;
    protected int bodyRadio = 15;  // centimetros
    protected HomePieceOfFurniture person3DModel;
    protected MutableInt2D lastPosition;
    protected MutableInt2D position;
    protected int cellSize;
    protected SparseGrid2D grid;
    protected PersonBag objectsCarried;
    protected int floor;   //planta que ocupa la persona
    protected double angle;     // radians
    protected double speed;     // meters/seconds
    protected int behavior;
    
    protected KeyboardControlledByPerson keyControlPerson;

    @Override
	public void step(SimState state) {
    	if(keyControlPerson != null) {
    		System.out.println("keyControlPerson.step()");
    		keyControlPerson.step(state);
    	}
    }
    
    public PersonBack(int floor, HomePieceOfFurniture person3DModel, Ubik ubik) {
    	super(floor, person3DModel, ubik);
        this.person3DModel = person3DModel;
        this.floor = floor;
        this.grid = ubik.getBuilding().getFloor(floor).getPersonHandler().getGrid();
        this.cellSize = ubik.getCellSize();
        this.position = new MutableInt2D();
        this.lastPosition = new MutableInt2D();
        this.name = person3DModel.getName();
        this.ubik = ubik;

        bodyRadio /= cellSize;

        this.id = IDgenerator++;

        angle = person3DModel.getAngle();
        speed = 0.5;  // velocidad de 1 metro/segundo

        objectsCarried = new PersonBag(this);
        setPosition((int) (person3DModel.getX() / cellSize), (int) (person3DModel.getY() / cellSize));
    }

    /**
     * En Mason, cuando un evento se pone a repetir en schedule, se devuelve un Stoppable. Llamando a stop ese agente deja de tener el turno para ejecutarse.
     * En el caso de trabajadores, el fuego con intensidad suficiente puede "pararlos"
     * @param stoppable
     */
    @Override
	public void fixStopable(Schedule schedule) {
        schedule.scheduleRepeating(this);
    }

    @Override
	public void stop() {
    }

    @Override
	public double orientation2D() {
        return this.angle;
    }

    @Override
	public abstract void createInOCP(ContextService cs);

    @Override
	public int getBehavior() {
        return behavior;
    }

    @Override
	public HomePieceOfFurniture getPerson3DModel() {
        return person3DModel;
    }

    @Override
	public MutableInt2D getPosition() {
        return position;
    }

    @Override
	public void setAngle(double angle) {
        this.angle = angle;
    }

    @Override
	public int getSpeedInCell() {    	
        return (int) Math.round((speed * 100 / cellSize)*ubik.getSpeed());
    }

    double distance = 0.0;
    @Override
	public boolean move() {
    	double lastDist = distance;
        distance += getSpeedInCell();
        if(distance >= cellSize) {
        	for (int i = 1; i <= distance; i++) {
        		int x = (int) Math.round(position.x + i * Math.cos(angle));
        		int y = (int) Math.round(position.y + i * Math.sin(angle));

        		if (ubik.getBuilding().getFloor(floor).isObstacle(this, x, y)) {
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

    @Override
	public boolean move(int x, int y) {
        setAngle(MovementTools.getInstance().directionInRadians(position.x, position.y, x, y));
        double distance = getSpeedInCell();
        if (position.distance(x, y) < distance) {
            return setPosition(x, y);
        }
        return move();
    }

    @Override
	public boolean setPosition(int x, int y) {
        if (ubik.getBuilding().getFloor(floor).isObstacle(this, x, y)) {
            //System.out.println("OBSTACULO!!!");
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
        objectsCarried.updatePosition();
        grid.setObjectLocation(this, position.x, position.y);
        return true;
    }

    @Override
	public boolean setPosition(MutableInt2D position) {
        return setPosition(position.x, position.y);
    }

    @Override
	public void addObjectToBag(Portable p) {
        objectsCarried.addPortableObject(p);
    }

    @Override
	public void removeObjectFromBag(Portable p) {
        objectsCarried.removePortableObject(p);
    }

    @Override
	public int getBodyRadio() {
        return bodyRadio;
    }

    @Override
	public int getId() {
        return id;
    }

    public boolean isObstacle(PersonBack p) {
        if (MovementTools.getInstance().getDistance(position.x, position.y, p.getPosition().x, p.getPosition().y) <= bodyRadio + p.getBodyRadio()) {
            return true;
        }
        return false;
    }

    @Override
	public boolean isBodyRadioIn(int x, int y) {
        if (MovementTools.getInstance().getDistance(position.x, position.y, x, y) <= bodyRadio) {
            return true;
        }
        return false;
    }

    @Override
	public int getFloor() {
        return floor;
    }

    @Override
	public PersonBag getObjectsCarried() {
        return objectsCarried;
    }

    @Override
	public String getName() {
        return name;
    }

    @Override
	public double getAngle() {
        return angle;
    }
    
    @Override
	public double getSpeed() {
    	return speed;
    }

    @Override
	public void setSpeed(double s) {
    	this.speed = s;
    }
    
    @Override
	public void setKeyControlPerson(KeyboardControlledByPerson p) {
    	this.keyControlPerson = p;
    }
    
    @Override
	public KeyboardControlledByPerson getKeyControlPerson() {
    	return this.keyControlPerson;
    }
}
