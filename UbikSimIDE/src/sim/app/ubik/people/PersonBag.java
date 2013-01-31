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
package sim.app.ubik.people;

import java.util.ArrayList;
import java.util.List;


public class PersonBag {

	private int interObjectSpace = 20;
	protected List<Portable> objects;
        protected List<String> positions; //"ELEVATED","PUSHED"
	protected Person person;
                
	public PersonBag(Person person) {
		this.person = person;
	}

	public void addPortableObject(Portable p) {
		if (objects == null) {
			objects = new ArrayList<Portable>();
		}

		float elevation = getTopElevation();
                p.moveTo(person.getPerson3DModel().getX(), person.getPerson3DModel().getY());
		p.elevate(elevation);
		objects.add(p);
	}
        
        public void addPortableObject(Portable p,String position) {
		if (objects == null) {
			objects = new ArrayList<Portable>();
                        positions = new ArrayList<String>();
		}
                if (position.equals("ELEVATED")){
                    float elevation = getTopElevation();
                    p.moveTo(person.getPerson3DModel().getX(), person.getPerson3DModel().getY());
                    p.elevate(elevation);
                    objects.add(p);
                    positions.add("ELEVATED");
                }
                else if (position.equals("PUSHED")){
                    float angle=person.getPerson3DModel().getAngle();
                    p.getVisualObject().setAngle(angle);
                    p.moveTo(person.getPerson3DModel().getX()- (int) Math.round(person.getPerson3DModel().getDepth()*Math.sin(person.getPerson3DModel().getAngle())), person.getPerson3DModel().getY()+ (int) Math.round(person.getPerson3DModel().getDepth()*Math.cos(person.getPerson3DModel().getAngle())));                    objects.add(p);
                    positions.add("PUSHED");
                }
	}

	public void removePortableObject(Portable p) {
		objects.remove(p);
		relocateObjects();
	}

	private void relocateObjects() {
		float elevation = person.getPerson3DModel().getElevation()
				+ interObjectSpace;
		for (Portable p : objects) {
			p.elevate(elevation);
			elevation += p.getVisualObject().getElevation() + interObjectSpace;
		}
	}

	private float getTopElevation() {
		float result = person.getPerson3DModel().getHeight() + interObjectSpace;
		for (Portable p : objects) {
			result += p.getVisualObject().getHeight() + interObjectSpace;
		}
		return result;
	}

	public void updatePosition() {
		if (objects != null) {
                        int cont=0;
			for (Portable p : objects) {
                            if (positions!=null){
                                if (positions.get(cont).equals("ELEVATED")){
                                    p.moveTo(person.getPerson3DModel().getX(), person.getPerson3DModel().getY());
                                }
                                else if (positions.get(cont).equals("PUSHED")){
                                    p.getVisualObject().setAngle(person.getPerson3DModel().getAngle());
                                    p.moveTo(person.getPerson3DModel().getX()- (int) Math.round(person.getPerson3DModel().getDepth()*Math.sin(person.getPerson3DModel().getAngle())), person.getPerson3DModel().getY()+ (int) Math.round(person.getPerson3DModel().getDepth()*Math.cos(person.getPerson3DModel().getAngle())));
                                }
                            }
                            else{
                                p.moveTo(person.getPerson3DModel().getX(), person.getPerson3DModel().getY());
                            }
			}
		}
	}

	public List<Portable> getObjects() {
		return objects;
	}
        
        public boolean contains(Portable p) {
            if(objects == null)
                return false;
            return objects.contains(p);
        }
}