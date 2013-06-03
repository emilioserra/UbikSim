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

/**
 * The aspect ratio of pictures.
 * @since 2.0
 */
public enum AspectRatio {
  FREE_RATIO(null), 
  VIEW_3D_RATIO(null), 
  RATIO_4_3(4f / 3), 
  RATIO_3_2(1.5f), 
  RATIO_16_9(16f / 9), 
  RATIO_2_1(2f / 1f), 
  SQUARE_RATIO(1f);
  
  private final Float value;
  
  private AspectRatio(Float value) {
    this.value = value;
  }    
  
  /**
   * Returns the value of this aspect ratio (width / height) or <code>null</code> if it's not known.
   */
  public Float getValue() {
    return value;
  }
}