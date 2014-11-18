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

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Observable;
import sim.app.ubik.Ubik;
import sim.app.ubik.people.Person;
import sim.app.ubik.people.PersonBag.objectPosition;
import ubik3d.model.HomePieceOfFurniture;

/**
* This class manages the 3D representation of a Label. The label appears over a 3D model in the simulation.
* 
* 
*/

public class PersonLabelRepresentation extends Representation{
    
    //The label object to represent
    protected Label actualLabelRepresentation;
    
    /**
     * Constructor
     * @param ubik The simulation where the label is introduced
     * @param person The subject which is owner of the subject
     */
    public PersonLabelRepresentation(Ubik ubik, Person person) {
        super(ubik, null);
        if (person.getLabel() != null) {
            this.actualLabelRepresentation=person.getLabel();
            LinkedList<HomePieceOfFurniture> l=actualLabelRepresentation.getModels();
            
            person.addObjectToBag(actualLabelRepresentation,objectPosition.Elevated);
            modelsPlacement(l, person);
        }
    }

    /**
     * This method updates the 3D model representation of the label when the subject label changes.
     * @param o This observable object represents the person which is owner of the subject
     * @param arg 
     */
    @Override
    public void update(Observable o, Object arg) {
        Person p = (Person) o;

        // We are only interested in cases where the label changes
        if (actualLabelRepresentation != null && actualLabelRepresentation.equals(p.getLabel())) {
            return;
        }

        //If the label changes, the previous label representation is deleted
        if (actualLabelRepresentation != null) {
            p.removeObjectFromBag(actualLabelRepresentation);
            LinkedList<HomePieceOfFurniture> l=actualLabelRepresentation.getModels();
            ListIterator it=l.listIterator();
            while (it.hasNext()){
                HomePieceOfFurniture hpf=(HomePieceOfFurniture)it.next();
                ubik.getHomes().get(p.getFloor()).deletePieceOfFurniture(hpf);
            }            
        }

        //If the label is empty, no changes are done
        if (p.getLabel() == null || p.getLabel().equals("")) {
            return;
        }

        //The label representation is placed over the 3D model of the subject
        if (actualLabelRepresentation != null) {
            LinkedList<HomePieceOfFurniture> l=actualLabelRepresentation.getModels();
            p.addObjectToBag(actualLabelRepresentation,objectPosition.Elevated);
            modelsPlacement(l, p);
        }
    }

    /**
     * This method redistributes the 3D characters of the label when the label representation is added to a subject
     * @param l A list of 3D characters
     * @param person The subject which is owner of the label
     */
    private void modelsPlacement(LinkedList<HomePieceOfFurniture> l, Person person){
        int numberChars=l.size();
        HomePieceOfFurniture hpf=(HomePieceOfFurniture)l.get(numberChars/2);
        resizeToMaxDimension(hpf,maxValueOfAnyDimension/l.size());
        hpf.setX(person.getPerson3DModel().getX());
        hpf.setY(person.getPerson3DModel().getY());
        hpf.setVisible(true);
        ubik.getHomes().get(person.getFloor()).addPieceOfFurniture(hpf);
        
        float lastPosition=person.getPerson3DModel().getX();
        HomePieceOfFurniture lastPiece=hpf;
        for (int i=numberChars/2+1;i<l.size();i++){
            hpf=(HomePieceOfFurniture)l.get(i);
            resizeToMaxDimension(hpf,maxValueOfAnyDimension/l.size());
            hpf.setX(lastPosition+lastPiece.getWidth());
            lastPosition=lastPosition+lastPiece.getWidth();
            lastPiece=hpf;
            hpf.setY(person.getPerson3DModel().getY());
            hpf.setVisible(true);
            ubik.getHomes().get(person.getFloor()).addPieceOfFurniture(hpf);
        }
        lastPosition=person.getPerson3DModel().getX();
        for (int i=(numberChars/2)-1;i>=0;i--){
            hpf=(HomePieceOfFurniture)l.get(i);
            resizeToMaxDimension(hpf,maxValueOfAnyDimension/l.size());
            hpf.setVisible(true);
            hpf.setX(lastPosition-lastPiece.getWidth());
            lastPosition=lastPosition-lastPiece.getWidth();
            lastPiece=hpf;
            hpf.setY(person.getPerson3DModel().getY());
            ubik.getHomes().get(person.getFloor()).addPieceOfFurniture(hpf);
        }
        
    }
   
    
}
