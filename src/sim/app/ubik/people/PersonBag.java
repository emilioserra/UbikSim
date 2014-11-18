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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class PersonBag {

	private int interObjectSpace = 20;
	public enum objectPosition{Elevated(1),Pushed(2);
            private int pos;
 
            objectPosition(int pos){
                this.pos = pos;
            }
 
            public int getPos(){
                return this.pos;
            }
        };
	protected Map<Portable,PersonBag.objectPosition> objects; //value puede ser objectPosition.Elevated o objectPosition.Pushed

	protected Person person;
                
	public PersonBag(Person person) {
		this.person = person;
	}

	public void addPortableObject(Portable p) {
		if (objects == null) {
			objects = new HashMap<Portable,PersonBag.objectPosition>();
		}
		float elevation = getTopElevation();
                p.moveTo(person.getPerson3DModel().getX(), person.getPerson3DModel().getY());
		p.elevate(elevation);
		objects.put(p,PersonBag.objectPosition.Elevated);
	}
        
        public void addPortableObject(Portable p,PersonBag.objectPosition position) {
		if (objects == null) {
			objects = new HashMap<Portable,PersonBag.objectPosition>();
		}
                if (position.equals(PersonBag.objectPosition.Elevated)){
                    float elevation = getTopElevation();
                    p.moveTo(person.getPerson3DModel().getX(), person.getPerson3DModel().getY());
                    p.elevate(elevation);
                    objects.put(p,PersonBag.objectPosition.Elevated);
                }
                else if (position.equals(PersonBag.objectPosition.Pushed)){
                    float angle=person.getPerson3DModel().getAngle();
                    p.getVisualObject().setAngle(angle);
                    p.moveTo(person.getPerson3DModel().getX()- (int) Math.round(person.getPerson3DModel().getDepth()*Math.sin(person.getPerson3DModel().getAngle())), person.getPerson3DModel().getY()+ (int) Math.round(person.getPerson3DModel().getDepth()*Math.cos(person.getPerson3DModel().getAngle())));                    
                    objects.put(p,PersonBag.objectPosition.Pushed);
                }
	}

	public void removePortableObject(Portable p) {
		objects.remove(p);
		relocateObjects();
	}

        public void removeAllPortableObjects() {
            objects.clear();
	}
        
	private void relocateObjects() {
		float elevation = person.getPerson3DModel().getElevation()
				+ interObjectSpace;
		for (Portable p : objects.keySet()) {
			p.elevate(elevation);
			elevation += p.getVisualObject().getElevation() + interObjectSpace;
		}
	}

	private float getTopElevation() {
		float result = person.getPerson3DModel().getHeight() + interObjectSpace;
		for (Portable p : objects.keySet()) {
			result += p.getVisualObject().getHeight() + interObjectSpace;
		}
		return result;
	}

	public void updatePosition() {
		if (objects != null) {
                        int cont=0;
			for (Portable p : objects.keySet()) {
                            if (objects.get(p).equals(PersonBag.objectPosition.Elevated)){
                                p.moveTo(person.getPerson3DModel().getX(), person.getPerson3DModel().getY());
                            }
                            else if (objects.get(p).equals(PersonBag.objectPosition.Pushed)){
                                p.getVisualObject().setAngle(person.getPerson3DModel().getAngle());
                                p.moveTo(person.getPerson3DModel().getX()- (int) Math.round(person.getPerson3DModel().getDepth()*Math.sin(person.getPerson3DModel().getAngle())), person.getPerson3DModel().getY()+ (int) Math.round(person.getPerson3DModel().getDepth()*Math.cos(person.getPerson3DModel().getAngle())));
                            }
                           
			}
		}
	}

	public List<Portable> getObjects() {
		return new LinkedList<Portable>(objects.keySet());
	}
        
        public boolean contains(Portable p) {
            if(objects == null)
                return false;
            return objects.containsKey(p);
        }
}