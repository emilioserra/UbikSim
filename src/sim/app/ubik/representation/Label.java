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

import java.io.IOException;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import sim.app.ubik.people.Portable;
import ubik3d.model.HomePieceOfFurniture;

/**
* This class represents a portable label composed by characters.
* 
* 
*/
public class Label implements Portable{
    //A list which contains 3D models of the characters
    private LinkedList<HomePieceOfFurniture> models;
    
    /**
     * Constructor
     * @param label A string containing the label to create 
     */
    public Label(String label) {        
        models=new LinkedList<HomePieceOfFurniture>();
        for (int i=0;i<label.length();i++){
            try{
                models.add(Representation.createModel(label.charAt(i)+"", Label.class, "Numbers/"+label.charAt(i)+".obj"));
            } catch (IOException ex) {
                Logger.getLogger(Label.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }    
    
    /**
     *
     * @return A list which contains 3D models of the characters
     */
    public LinkedList<HomePieceOfFurniture> getModels(){
        return models;
    }

    /**
     * This method elevates the portable label
     * @param elevation 
     */
    @Override
    public void elevate(float elevation) {
        ListIterator it=models.listIterator();
        while (it.hasNext()){
            HomePieceOfFurniture hpf= (HomePieceOfFurniture) it.next();
            hpf.setElevation(elevation);
        }
    }

    /**
     * This method is not supported
     * @return 
     */
    @Override
    public HomePieceOfFurniture getVisualObject() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * This method moves all the characters of the label
     * @param x
     * @param y 
     */
    @Override
    public void moveTo(float x, float y) {
        int numberChars=models.size();
        HomePieceOfFurniture hpf=(HomePieceOfFurniture)models.get(numberChars/2);
        hpf.setX(x);
        hpf.setY(y);
        
        float lastPosition=x;
        HomePieceOfFurniture lastPiece=hpf;
        for (int i=numberChars/2+1;i<models.size();i++){
            hpf=(HomePieceOfFurniture)models.get(i);
            hpf.setX(lastPosition+lastPiece.getWidth());
            lastPosition=lastPosition+lastPiece.getWidth();
            lastPiece=hpf;
            hpf.setY(y);
        }
        lastPosition=x;
        for (int i=(numberChars/2)-1;i>=0;i--){
            hpf=(HomePieceOfFurniture)models.get(i);
            hpf.setX(lastPosition-lastPiece.getWidth());
            lastPosition=lastPosition-lastPiece.getWidth();
            lastPiece=hpf;
            hpf.setY(y);
        }
    }
    
    /**
     * This method changes the color of the label
     * @param color 
     */
    public void setColor(int color){
        ListIterator it=models.listIterator();
        while (it.hasNext()){
            HomePieceOfFurniture hpf= (HomePieceOfFurniture) it.next();
            hpf.setColor(color);
        }
    }
    
    /**
     * This method changes the font size of the label
     * @param size 
     */
    public void setFontSize(float size){
        ListIterator it=models.listIterator();
        while (it.hasNext()){
            HomePieceOfFurniture hpf= (HomePieceOfFurniture) it.next();
            Representation.resizeToMaxDimension(hpf, size);
       }
    }
        
}
