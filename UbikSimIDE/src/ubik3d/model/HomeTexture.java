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
package ubik3d.model;

import java.io.Serializable;

/**
 * An image used as texture on home 3D objects.
 * @author Emmanuel Puybaret
 */
public class HomeTexture implements TextureImage, Serializable {
  private static final long serialVersionUID = 1L;
  
  private final String name;
  private final Content image;
  private final float width;
  private final float height;
  
  /**
   * Creates a home texture from an existing one.
   * @param texture the texture from which data are copied
   */
  public HomeTexture(TextureImage texture) {
    this.name = texture.getName();
    this.image = texture.getImage();
    this.width = texture.getWidth();
    this.height = texture.getHeight();
  }
  
  /**
   * Returns the name of this texture.
   */
  public String getName() {
    return this.name;
  }
  
  /**
   * Returns the content of the image used for this texture. 
   */
  public Content getImage() {
    return this.image;
  }
  
  /**
   * Returns the width of the image in centimeters.
   */
  public float getWidth() {
    return this.width;
  }

  /**
   * Returns the height of the image in centimeters.
   */
  public float getHeight() {
    return this.height;
  }

  /**
   * Returns <code>true</code> if the object in parameter is equal to this texture.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    } else if (obj instanceof HomeTexture) {
      HomeTexture texture = (HomeTexture)obj;
      return texture.name.equals(this.name)
          && texture.image.equals(this.image)
          && texture.width == this.width
          && texture.height == this.height;
    } else {
      return false;
    }
  }
  
  /**
   * Returns a hash code for this texture.
   */
  @Override
  public int hashCode() {
    return this.name.hashCode()
        + this.image.hashCode()
        + Float.floatToIntBits(this.width)
        + Float.floatToIntBits(this.height);
  }
}
