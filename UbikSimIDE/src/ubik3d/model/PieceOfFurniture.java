/*
 * Contact, UbikSimIDE has been developed by:
 * 
 * Juan A. Bot�a , juanbot@um.es
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
 * A piece of furniture.
 * @author Emmanuel Puybaret
 */
public interface PieceOfFurniture {
  /**
  * Returns the rol of this piece of furniture.
  */
 public abstract String getMetadata();
   /**
  * Returns the rol of this piece of furniture.
  */
 public abstract String getRol();
 
 public abstract float getAmount();
  /**
   * Returns the name of this piece of furniture.
   */
  public abstract String getName();

  /**
   * Returns the description of this piece of furniture.
   */
  public abstract String getDescription();
  
  /**
   * Returns the depth of this piece of furniture.
   */
  public abstract float getDepth();

  /**
   * Returns the height of this piece of furniture.
   */
  public abstract float getHeight();

  /**
   * Returns the width of this piece of furniture.
   */
  public abstract float getWidth();

  /**
   * Returns the elevation of this piece of furniture.
   */
  public abstract float getElevation();

  /**
   * Returns <code>true</code> if this piece of furniture is movable.
   */
  public abstract boolean isMovable();

  /**
   * Returns <code>true</code> if this piece of furniture is a door or a window.
   * As this method existed before {@linkplain DoorOrWindow DoorOrWindow} interface,
   * you shouldn't rely on the value returned by this method to guess if a piece
   * is an instance of <code>DoorOrWindow</code> class.
   */
  public abstract boolean isDoorOrWindow();

  /**
   * Returns the icon of this piece of furniture.
   */
  public abstract Content getIcon();

  /**
   * Returns the icon of this piece of furniture displayed in plan or <code>null</code>.
   * @since 2.2
   */
  public abstract Content getPlanIcon();

    /**
   * Returns the 3D model of this piece of furniture.
   */
  public abstract Content getModel();
  
  /**
   * Returns the rotation 3 by 3 matrix of this piece of furniture that ensures 
   * its model is correctly oriented.
   */
  public float [][] getModelRotation();
  
  /**
   * Returns <code>true</code> if the back face of the piece of furniture
   * model should be displayed.
   */
  public abstract boolean isBackFaceShown();
  
  /**
   * Returns the color of this piece of furniture.
   */
  public abstract Integer getColor();
  
  /**
   * Returns <code>true</code> if this piece is resizable.
   */
  public abstract boolean isResizable();
  
  /**
   * Returns <code>true</code> if this piece is deformable. The width, depth and height
   * of a deformable piece may change independently from each other.
   * @since 3.0
   */
  public abstract boolean isDeformable();

  /**
   * Returns <code>false</code> if this piece should always keep the same color or texture.
   * @since 3.0
   */
  public abstract boolean isTexturable();
  
  /**
   * Returns the price of this piece of furniture or <code>null</code>. 
   */
  public abstract BigDecimal getPrice();
  
  /**
   * Returns the Value Added Tax percentage applied to the price of this piece of furniture. 
   */
  public abstract BigDecimal getValueAddedTaxPercentage();
}