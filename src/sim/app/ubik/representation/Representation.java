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
import java.util.Observer;
import javax.media.j3d.BranchGroup;
import javax.vecmath.Vector3f;
import sim.app.ubik.Ubik;
import ubik3d.j3d.ModelManager;
import ubik3d.model.CatalogPieceOfFurniture;
import ubik3d.model.Content;
import ubik3d.model.HomePieceOfFurniture;
import ubik3d.tools.ResourceURLContent;

/**
* Esta clase sirve para representar a una entidad a través de un objeto gráfico.
* Para ello, la clase debe obvservar a la entidad en cuestión que además debe ser observable.
* Cada modificación de la entidad debe notificar a esta clase que cambiará las propiedades gráficas
* según se desee.
* 
* 
*/
public abstract class Representation implements Observer {
    protected HomePieceOfFurniture actualModel;
    protected Ubik ubik;
    
    protected float widthMax = 50.0f;
    protected float heightMax  = 50.0f;
    protected float depthMax  = 50.0f;
    
    protected float maxValueOfAnyDimension = 30;
    
    public Representation(Ubik ubik, HomePieceOfFurniture defaultModel) {
        this.ubik = ubik;
        this.actualModel = defaultModel;
    }
        
    /**
     * Método para crear objetos 3d en el simulador.
     * Se añade a todos los escenarios (home) como invisibles para tenerlos disponibles rápidamente.
     * 
     * @param name Nombre del objeto 3d que representará a la entidad que se desee.
     * @param pathOfObject3d ruta relativa donde se encuentra el fichero .obj. La ruta es relativa a la localización de la clase baseClass. 
     * @param ubik 
     * @return
     */
    public static HomePieceOfFurniture createModel(String name, Class baseClass, String pathOfObject3d) throws IOException {
        HomePieceOfFurniture hpof = null;
        //ubik.getHomes().get(0).addPieceOfFurniture(hopf);
        
        Content modelContent;
        /* @TODO solucionar porque no se cargan bien los objetos de la forma que está comentada
         * if(pathOfObject3d.contains("://")) {
         * De esta forma da error!!
        	URL urlModel = new URL(pathOfObject3d);
        	modelContent = new ResourceURLContent(urlModel, false);
    	} else
        	*/modelContent = new ResourceURLContent(baseClass, pathOfObject3d, true);    
        final BranchGroup modelNode = ModelManager.getInstance().loadModel(modelContent);
        Vector3f modelSize = ModelManager.getInstance().getSize(modelNode);        
        CatalogPieceOfFurniture piece = new CatalogPieceOfFurniture(
				"", 		/* Metadata */
				"",             /* Rol */
				name, 		/* Name */
				null,	/* Icon */
				modelContent, 	/* Model */
				0, 		/* Amount */
				5.0f, 		/* Width */
				0.9f, 		/* Depth */
				7.8f, 		/* Height */
				160,		/* Elevation */
			        true, 		/* Movable */
			        null, 		/* Color */
			        new float [] [] { {1, 0, 0}, {0, 1, 0}, {0, 0, 1}}, /* ModelRotation */
			        false, 							/* isBackFaceShown */
			        (float) Math.PI / 8,			/* IconYaw */
			        true); 							/* isProportional*/
        
        hpof = new HomePieceOfFurniture(piece);
        hpof.setWidth(modelSize.x);
        hpof.setDepth(modelSize.z);
        hpof.setHeight(modelSize.y);
        return hpof;
    }

    public void setColor(Color color) {
    	if(actualModel != null)
    		actualModel.setColor(color.getRGB());
    }
    
	public float getWidthMax() {
		return widthMax;
	}

	public void setWidthMax(float widthMax) {
		this.widthMax = widthMax;
	}

	public float getHeightMax() {
		return heightMax;
	}

	public void setHeightMax(float heightMax) {
		this.heightMax = heightMax;
	}

	public float getDepthMax() {
		return depthMax;
	}

	public void setDepthMax(float depthMax) {
		this.depthMax = depthMax;
	}
	
	/**
	 * Escala el objeto 3d dejando la mayor dimensión al valor de value.
	 * @param value
	 */
	public void resizeToMaxDimension(float value) {
		if(actualModel.getDepth() > actualModel.getWidth() && actualModel.getDepth() > actualModel.getHeight()) {
			float factor = value / actualModel.getDepth();
			actualModel.setDepth(value);
			actualModel.setWidth(actualModel.getWidth()*factor);
			actualModel.setHeight(actualModel.getHeight()*factor);
		} else if(actualModel.getWidth() > actualModel.getDepth() && actualModel.getWidth() > actualModel.getHeight()) {
			float factor = value / actualModel.getWidth();
			actualModel.setDepth(actualModel.getDepth()*factor);
			actualModel.setWidth(value);
			actualModel.setHeight(actualModel.getHeight()*factor);
		}  else if(actualModel.getHeight() >= actualModel.getDepth() && actualModel.getHeight() >= actualModel.getDepth()) {
			float factor = value / actualModel.getHeight();
			actualModel.setDepth(actualModel.getDepth()*factor);
			actualModel.setWidth(actualModel.getWidth()*factor);
			actualModel.setHeight(value);
		}
	}
        
        /**
	 * Escala el objeto 3d dejando la mayor dimensión al valor de value.
         * @param actualModel 
	 * @param value
	 */
	public static void resizeToMaxDimension(HomePieceOfFurniture actualModel, float value) {
		if(actualModel.getDepth() > actualModel.getWidth() && actualModel.getDepth() > actualModel.getHeight()) {
			float factor = value / actualModel.getDepth();
			actualModel.setDepth(value);
			actualModel.setWidth(actualModel.getWidth()*factor);
			actualModel.setHeight(actualModel.getHeight()*factor);
		} else if(actualModel.getWidth() > actualModel.getDepth() && actualModel.getWidth() > actualModel.getHeight()) {
			float factor = value / actualModel.getWidth();
			actualModel.setDepth(actualModel.getDepth()*factor);
			actualModel.setWidth(value);
			actualModel.setHeight(actualModel.getHeight()*factor);
		}  else if(actualModel.getHeight() >= actualModel.getDepth() && actualModel.getHeight() >= actualModel.getDepth()) {
			float factor = value / actualModel.getHeight();
			actualModel.setDepth(actualModel.getDepth()*factor);
			actualModel.setWidth(actualModel.getWidth()*factor);
			actualModel.setHeight(value);
		}
	}
	
	
	/**
	 * Redimensiona el objeto (guardando escala)
	 * para que no supere ninguna dimensión máxima (depthMax, widthMax, heightMax).
	 */
	public void resizeUnderMaxDimension() {
		if(actualModel.getDepth() > depthMax) {
			float factor = depthMax / actualModel.getDepth();
			actualModel.setDepth(depthMax);
			actualModel.setWidth(actualModel.getWidth()*factor);
			actualModel.setHeight(actualModel.getHeight()*factor);
		}
		if(actualModel.getWidth() > widthMax) {
			float factor = widthMax / actualModel.getWidth();
			actualModel.setDepth(actualModel.getDepth()*factor);
			actualModel.setWidth(widthMax);
			actualModel.setHeight(actualModel.getHeight()*factor);
		}
		if(actualModel.getHeight() > heightMax) {
			float factor = heightMax / actualModel.getHeight();
			actualModel.setDepth(actualModel.getDepth()*factor);
			actualModel.setWidth(actualModel.getWidth()*factor);
			actualModel.setHeight(heightMax);
		}
	}

	public float getMaxValueOfAnyDimension() {
		return maxValueOfAnyDimension;
	}

	public void setMaxValueOfAnyDimension(float maxValueOfAnyDimension) {
		this.maxValueOfAnyDimension = maxValueOfAnyDimension;
	}
}
