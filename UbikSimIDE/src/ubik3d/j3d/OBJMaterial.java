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
package ubik3d.j3d;

import javax.media.j3d.Material;
import javax.media.j3d.NodeComponent;

/**
 * A material with additional parameters useful for raytracing rendering. 
 * @author Emmanuel Puybaret
 */
public class OBJMaterial extends Material {
  private Float   opticalDensity;
  private Integer illuminationModel;
  private Float   sharpness;

  /**
   * Sets the optical density of this material.
   */
  public void setOpticalDensity(float opticalDensity) {
    this.opticalDensity = opticalDensity;
  }
  
  /**
   * Returns the optical density of this material.
   * @throws IllegalStateException if optical density wasn't set.
   */
  public float getOpticalDensity() {
    if (this.opticalDensity != null) {
      return this.opticalDensity;
    } else {
      throw new IllegalStateException("Optical density not set");
    }
  }
  
  /**
   * Returns <code>true</code> if optical density was set on this material.
   */
  public boolean isOpticalDensitySet() {
    return this.opticalDensity != null;
  }

  /**
   * Sets the illumination model of this material.
   */
  public void setIlluminationModel(int illuminationModel) {
    this.illuminationModel = illuminationModel;
  }
  
  /**
   * Returns the illumination model of this material as defined in MTL format.
   * @throws IllegalStateException if illumination model wasn't set.
   */
  public int getIlluminationModel() {
    if (this.illuminationModel != null) {
      return this.illuminationModel;
    } else {
      throw new IllegalStateException("Optical density not set");
    }
  }
  
  /**
   * Returns <code>true</code> if illumination model was set on this material.
   */
  public boolean isIlluminationModelSet() {
    return this.illuminationModel != null;
  }
  
  /**
   * Sets the sharpness of this material.
   */
  public void setSharpness(float sharpness) {
    this.sharpness = sharpness;
  }
  
  /**
   * Returns the sharpness of this material.
   * @throws IllegalStateException if sharpness wasn't set.
   */
  public float getSharpness() {
    if (this.sharpness != null) {
      return this.sharpness;
    } else {
      throw new IllegalStateException("Sharpness not set");
    }
  }
  
  /**
   * Returns <code>true</code> if sharpness was set on this material.
   */
  public boolean isSharpnessSet() {
    return this.sharpness != null;
  }

  /**
   * Returns a clone of this material.
   */
  @Override
  public NodeComponent cloneNodeComponent(boolean forceDuplicate) {
    OBJMaterial material = new OBJMaterial();
    material.duplicateNodeComponent(this, forceDuplicate);
    material.opticalDensity = this.opticalDensity;
    material.illuminationModel = this.illuminationModel;
    material.sharpness = this.sharpness;
    return material;
  }
}
