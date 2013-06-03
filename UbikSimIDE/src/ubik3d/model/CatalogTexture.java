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

import java.text.Collator;

/**
 * A texture in textures catalog.
 * @author Emmanuel Puybaret
 */
public class CatalogTexture implements TextureImage, Comparable<CatalogTexture> {
  private static final long serialVersionUID = 1L;
  
  private final String          id;
  private final String          name;
  private final Content         image;
  private final float           width;
  private final float           height;
  private final String          creator;
  private final boolean         modifiable;
  
  private TexturesCategory      category;
  
  private static final Collator COMPARATOR = Collator.getInstance();

  
  /**
   * Creates an unmodifiable catalog texture.
   * @param name the name of this texture 
   * @param image the content of the image used for this texture
   * @param width the width of the texture in centimeters
   * @param height the height of the texture in centimeters
   */
  public CatalogTexture(String name, Content image, float width, float height) {
    this(null, name, image, width, height, null);
  }

  /**
   * Creates a catalog texture.
   * @param id   the id of the texture
   * @param name the name of this texture 
   * @param image the content of the image used for this texture
   * @param width the width of the texture in centimeters
   * @param height the height of the texture in centimeters
   * @param creator the creator of this texture
   */
  public CatalogTexture(String id, 
                        String name, Content image, 
                        float width, float height,
                        String creator) {
    this(id, name, image, width, height, creator, false);
  }
  
  /**
   * Creates a catalog texture.
   * @param name the name of this texture 
   * @param image the content of the image used for this texture
   * @param width the width of the texture in centimeters
   * @param height the height of the texture in centimeters
   * @param modifiable <code>true</code> if this texture can be modified
   */
  public CatalogTexture(String name, Content image, 
                        float width, float height,
                        boolean modifiable) {
    this(null, name, image, width, height, null, modifiable);
  }
  
  public CatalogTexture(String id, 
                        String name, Content image, 
                        float width, float height,
                        String creator,
                        boolean modifiable) {
    this.id = id;
    this.name = name;
    this.image = image;
    this.width = width;
    this.height = height;
    this.creator = creator;
    this.modifiable = modifiable;
  }
  
  /**
   * Returns the ID of this texture or <code>null</code>.
   * @since 2.3
   */
  public String getId() {
    return this.id;
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
   * Returns the creator of this texture or <code>null</code>.
   * @since 2.3
   */
  public String getCreator() {
    return this.creator;
  }

  /**
   * Returns <code>true</code> if this texture is modifiable (not read from resources).
   */
  public boolean isModifiable() {
    return this.modifiable;
  }
  
  /**
   * Returns the category of this texture.
   */
  public TexturesCategory getCategory() {
    return this.category;
  }
  
  /**
   * Sets the category of this texture.
   */
  void setCategory(TexturesCategory category) {
    this.category = category;
  }
  
  /** 
   * Returns true if this texture and the one in parameter have the same name.
   */
  @Override
  public boolean equals(Object obj) {
    return obj instanceof CatalogTexture
           && COMPARATOR.equals(this.name, ((CatalogTexture)obj).name);
  }

  /** 
   * Returns a hash code computed from the name of this texture.
   */
   @Override
  public int hashCode() {
    return this.name.hashCode();
  }

  /** 
   * Compares the names of this texture and the one in parameter.
   */
  public int compareTo(CatalogTexture texture) {
    return COMPARATOR.compare(this.name, texture.name);
  }
}
