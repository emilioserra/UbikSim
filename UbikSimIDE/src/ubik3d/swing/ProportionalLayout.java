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
package ubik3d.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;

/**
 * A layout manager that displays two components at the top of each other. 
 * The component at top is sized at container width and at its preferred height.
 * The component at bottom is centered in the rest of the space and sized proportionally 
 * to its preferred size.
 * @author Emmanuel Puybaret
 */
public class ProportionalLayout implements LayoutManager2 {
  /**
   * The two locations where components managed by a {@link ProportionalLayout} instance can be placed.
   */
  public enum Constraints {TOP, BOTTOM}

  private Component topComponent;
  private Component bottomComponent;
  private int       gap;

  /**
   * Creates a layout manager which layouts its component 
   * with a default gap of 5 pixels between them.
   */
  public ProportionalLayout() {
    this(5);
  }

  /**
   * Creates a layout manager which layouts its component 
   * with a given <code>gap</code> between them.
   */
  public ProportionalLayout(int gap) {
    this.gap = gap;
  }
  
  /**
   * Records a given <code>component</code> in this layout manager as the component at 
   * <code>Constraints.TOP</code> or at <code>Constraints.BOTTOM</code> of its container.
   */
  public void addLayoutComponent(Component component, Object constraints) {
    if (constraints == Constraints.TOP) {
      this.topComponent = component; 
    } else if (constraints == Constraints.BOTTOM) {
      this.bottomComponent = component;
    }
  }

  /**
   * Do not use.
   */
  public void addLayoutComponent(String name, Component comp) {
    throw new IllegalArgumentException("Use addLayoutComponent with a Constraints object");
  }

  /**
   * Removes the given <code>component</code> from the ones managed by this layout manager.
   */
  public void removeLayoutComponent(Component component) {
  }
  
  /**
   * Returns 0.5.
   */
  public float getLayoutAlignmentX(Container target) {
    return 0.5f;
  }

  /**
   * Return 0.
   */
  public float getLayoutAlignmentY(Container target) {
    return 0f;
  }

  /**
   * Invalidates layout.
   */
  public void invalidateLayout(Container target) {
    // Sizes are computed on the fly each time
  }

  /**
   * Layouts the container.
   */
  public void layoutContainer(Container parent) {
    Insets parentInsets = parent.getInsets();
    int parentAvailableWidth = parent.getWidth() - parentInsets.left - parentInsets.right;
    int parentAvailableHeight = parent.getHeight() - parentInsets.top - parentInsets.bottom;
    
    // Component at top is sized at container width and at its preferred height
    boolean topComponentUsed = this.topComponent != null && this.topComponent.getParent() != null;
    if (topComponentUsed) {
      this.topComponent.setBounds(parentInsets.left, parentInsets.top, 
          parentAvailableWidth, 
          Math.min(this.topComponent.getPreferredSize().height, parentAvailableHeight));
    }
    // Component is centered in the rest of the space and sized proportionally to its preferred size
    if (this.bottomComponent != null && this.bottomComponent.getParent() != null) {
      Dimension bottomComponentPreferredSize = this.bottomComponent.getPreferredSize();
      int bottomComponentHeight = parentAvailableHeight;
      int bottomComponentY = parentInsets.top;
      if (topComponentUsed) {
        int occupiedHeight = this.topComponent.getHeight() + this.gap;
        bottomComponentHeight -= occupiedHeight;
        bottomComponentY += occupiedHeight;
      }
      int bottomComponentWidth = bottomComponentHeight * bottomComponentPreferredSize.width 
                                 / bottomComponentPreferredSize.height;
      int bottomComponentX = parentInsets.left;
      // Adjust component width and height if it's larger than parent
      if (bottomComponentWidth > parentAvailableWidth) {
        bottomComponentWidth = parentAvailableWidth;
        int previousHeight = bottomComponentHeight;
        bottomComponentHeight = bottomComponentWidth * bottomComponentPreferredSize.height 
                                / bottomComponentPreferredSize.width;
        bottomComponentY += (previousHeight - bottomComponentHeight)  / 2;
      } else {
        // Center component in width
        bottomComponentX += (parentAvailableWidth - bottomComponentWidth)  / 2; 
      }
        
      this.bottomComponent.setBounds(bottomComponentX, bottomComponentY, 
          bottomComponentWidth, bottomComponentHeight);
    }
  }

  /**
   * Returns the largest minimum width of the components managed by this layout manager,
   * and the sum of their minimum heights.
   */
  public Dimension minimumLayoutSize(Container parent) {
    Insets parentInsets = parent.getInsets();
    int minWidth = 0;
    int minHeight = 0;
    boolean topComponentUsed = this.topComponent != null && this.topComponent.getParent() != null;
    if (topComponentUsed) {
      Dimension topComponentMinSize = this.topComponent.getMinimumSize();
      minWidth = Math.max(minWidth, topComponentMinSize.width);
      minHeight = topComponentMinSize.height;
    }
    if (this.bottomComponent != null && this.bottomComponent.getParent() != null) {
      Dimension bottomComponentMinSize = this.bottomComponent.getMinimumSize();
      minWidth = Math.max(minWidth, bottomComponentMinSize.width);
      minHeight += bottomComponentMinSize.height;
      if (topComponentUsed) {
        minHeight += this.gap;
      }
    }
    
    return new Dimension(minWidth + parentInsets.left + parentInsets.right, 
        minHeight + parentInsets.top + parentInsets.bottom);
  }

  /**
   * Returns the largest maximum width of the components managed by this layout manager,
   * and the sum of their maximum heights.
   */
  public Dimension maximumLayoutSize(Container parent) {
    Insets parentInsets = parent.getInsets();
    int maxWidth = 0;
    int maxHeight = 0;
    boolean topComponentUsed = this.topComponent != null && this.topComponent.getParent() != null;
    if (topComponentUsed) {
      Dimension topComponentMaxSize = this.topComponent.getMaximumSize();
      maxWidth = Math.max(maxWidth, topComponentMaxSize.width);
      maxHeight = topComponentMaxSize.height;
    }
    if (this.bottomComponent != null && this.bottomComponent.getParent() != null) {
      Dimension bottomComponentMaxSize = this.bottomComponent.getMaximumSize();
      maxWidth = Math.max(maxWidth, bottomComponentMaxSize.width);
      maxHeight += bottomComponentMaxSize.height;
      if (topComponentUsed) {
        maxHeight += this.gap;
      }
    }
    
    return new Dimension(maxWidth + parentInsets.left + parentInsets.right, 
        maxHeight + parentInsets.top + parentInsets.bottom);
  }

  /**
   * Returns the largest preferred width of the components managed by this layout manager,
   * and the sum of their preferred heights.
   */
  public Dimension preferredLayoutSize(Container parent) {
    Insets parentInsets = parent.getInsets();
    int preferredWidth = 0;
    int preferredHeight = 0;
    boolean topComponentUsed = this.topComponent != null && this.topComponent.getParent() != null;
    if (topComponentUsed) {
      Dimension topComponentPreferredSize = this.topComponent.getPreferredSize();
      preferredWidth = Math.max(preferredWidth, topComponentPreferredSize.width);
      preferredHeight = topComponentPreferredSize.height;
    }
    if (this.bottomComponent != null && this.bottomComponent.getParent() != null) {
      Dimension bottomComponentPreferredSize = this.bottomComponent.getPreferredSize();
      preferredWidth = Math.max(preferredWidth, bottomComponentPreferredSize.width);
      preferredHeight += bottomComponentPreferredSize.height;
      if (topComponentUsed) {
        preferredHeight += this.gap;
      }
    }
    
    return new Dimension(preferredWidth + parentInsets.left + parentInsets.right, 
        preferredHeight + parentInsets.top + parentInsets.bottom);
  }
}