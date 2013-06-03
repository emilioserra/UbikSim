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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A catalog of texture images used as patterns to fill plan areas.
 * @author Emmanuel Puybaret
 * @since  2.0
 */
public class PatternsCatalog {
  private List<TextureImage> patterns;
  
  /**
   * Creates a patterns catalog.
   */
  public PatternsCatalog(List<TextureImage> patterns) {
    this.patterns = new ArrayList<TextureImage>(patterns);
  }

  /**
   * Returns the patterns list.
   * @return an unmodifiable list of furniture.
   */
  public List<TextureImage> getPatterns() {
    return Collections.unmodifiableList(this.patterns);
  }

  /**
   * Returns the count of patterns in this category.
   */
  public int getPatternsCount() {
    return this.patterns.size();
  }

  /**
   * Returns the pattern at a given <code>index</code>.
   */
  public TextureImage getPattern(int index) {
    return this.patterns.get(index);
  }

  /**
   * Returns the pattern with a given <code>name</code>.
   * @throws IllegalArgumentException if no pattern with the given <code>name</code> exists
   */
  public TextureImage getPattern(String name) {
    for (TextureImage pattern : patterns) {
      if (name.equals(pattern.getName())) {
        return pattern;
      }
    }
    throw new IllegalArgumentException("No pattern with name " + name);
  }
}
