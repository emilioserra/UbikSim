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

import java.math.BigDecimal;

/**
 * A light of the catalog.
 * @author Emmanuel Puybaret
 * @since  1.7
 */
public class CatalogLight extends CatalogPieceOfFurniture implements Light {
  private static final long serialVersionUID = 1L;

  private final LightSource [] lightSources;

  /**
   * Creates an unmodifiable catalog light of the default catalog.
   * @param id    the id of the new light, or <code>null</code>
   * @param name  the name of the new light
   * @param description the description of the new light 
   * @param icon content of the icon of the new light
   * @param model content of the 3D model of the new light
   * @param width  the width in centimeters of the new light
   * @param depth  the depth in centimeters of the new light
   * @param height  the height in centimeters of the new light
   * @param elevation  the elevation in centimeters of the new light
   * @param movable if <code>true</code>, the new light is movable
   * @param lightSources the light sources of the new light
   * @param modelRotation the rotation 3 by 3 matrix applied to the light model
   * @param creator the creator of the model
   * @param resizable if <code>true</code>, the size of the new light may be edited
   * @param price the price of the new light, or <code>null</code> 
   * @param valueAddedTaxPercentage the Value Added Tax percentage applied to the 
   *             price of the new light or <code>null</code> 
   */
  public CatalogLight(String id, String metadata, String rol,  String name, String description, Content icon, Content model, float amount, 
                                 float width, float depth, float height, float elevation, boolean movable, 
                                 LightSource [] lightSources,
                                 float [][] modelRotation, String creator,
                                 boolean resizable, BigDecimal price, BigDecimal valueAddedTaxPercentage) {
    this(id, metadata, rol, name, description, icon, null, model, amount, width, depth, height, elevation, movable,   
        lightSources, modelRotation, creator, resizable, price, valueAddedTaxPercentage);
  }
         
  /**
   * Creates an unmodifiable catalog light of the default catalog.
   * @param id    the id of the new light, or <code>null</code>
   * @param name  the name of the new light
   * @param description the description of the new light 
   * @param icon content of the icon of the new light
   * @param planIcon content of the icon of the new piece displayed in plan
   * @param model content of the 3D model of the new light
   * @param width  the width in centimeters of the new light
   * @param depth  the depth in centimeters of the new light
   * @param height  the height in centimeters of the new light
   * @param elevation  the elevation in centimeters of the new light
   * @param movable if <code>true</code>, the new light is movable
   * @param lightSources the light sources of the new light
   * @param modelRotation the rotation 3 by 3 matrix applied to the light model
   * @param creator the creator of the model
   * @param resizable if <code>true</code>, the size of the new light may be edited
   * @param price the price of the new light, or <code>null</code> 
   * @param valueAddedTaxPercentage the Value Added Tax percentage applied to the 
   *             price of the new light or <code>null</code> 
   * @since 2.2            
   */
  public CatalogLight(String id, String metadata, String rol, String name, String description, 
                      Content icon, Content planIcon, Content model, float amount,
                      float width, float depth, float height, float elevation, boolean movable, 
                      LightSource [] lightSources,
                      float [][] modelRotation, String creator,
                      boolean resizable, BigDecimal price, BigDecimal valueAddedTaxPercentage) {
    this(id, metadata, rol, name, description, icon, planIcon, model, amount, width, depth, height, elevation, movable,   
        lightSources, modelRotation, creator, resizable, true, true, price, valueAddedTaxPercentage);
  }
         
  /**
   * Creates an unmodifiable catalog light of the default catalog.
   * @param id    the id of the new light, or <code>null</code>
   * @param name  the name of the new light
   * @param description the description of the new light 
   * @param icon content of the icon of the new light
   * @param planIcon content of the icon of the new piece displayed in plan
   * @param model content of the 3D model of the new light
   * @param width  the width in centimeters of the new light
   * @param depth  the depth in centimeters of the new light
   * @param height  the height in centimeters of the new light
   * @param elevation  the elevation in centimeters of the new light
   * @param movable if <code>true</code>, the new light is movable
   * @param lightSources the light sources of the new light
   * @param modelRotation the rotation 3 by 3 matrix applied to the light model
   * @param creator the creator of the model
   * @param resizable if <code>true</code>, the size of the new light may be edited
   * @param deformable if <code>true</code>, the width, depth and height of the new piece may 
   *            change independently from each other
   * @param texturable if <code>false</code> this piece should always keep the same color or texture.
   * @param price the price of the new light, or <code>null</code> 
   * @param valueAddedTaxPercentage the Value Added Tax percentage applied to the 
   *             price of the new light or <code>null</code> 
   * @since 3.0            
   */
  public CatalogLight(String id, String metadata, String rol, String name, String description, 
                      Content icon, Content planIcon, Content model, float amount,
                      float width, float depth, float height, float elevation, boolean movable, 
                      LightSource [] lightSources,
                      float [][] modelRotation, String creator,
                      boolean resizable, boolean deformable, boolean texturable,
                      BigDecimal price, BigDecimal valueAddedTaxPercentage) {
    super(id, metadata, rol, name, description, icon, planIcon, model, amount, width, depth, height, elevation, movable,   
        modelRotation, creator, resizable, deformable, texturable, price, valueAddedTaxPercentage);
    this.lightSources = lightSources;
  }
         
  /**
   * Returns the sources managed by this light. Each light source point
   * is a percentage of the width, the depth and the height of this light,
   * with the abscissa origin at the left side of the piece,
   * the ordinate origin at the front side of the piece
   * and the elevation origin at the bottom side of the piece.
   * @return a copy of light sources array.
   */
  public LightSource [] getLightSources() {
    if (this.lightSources.length == 0) {
      return this.lightSources;
    } else {
      return this.lightSources.clone();
    }
  }
}
