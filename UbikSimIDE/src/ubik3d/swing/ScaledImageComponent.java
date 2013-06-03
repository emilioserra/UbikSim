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

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.EtchedBorder;

/**
 * Component displaying an image scaled to fit within its bound.
 * @author Emmanuel Puybaret 
 */
public class ScaledImageComponent extends JComponent {
  private BufferedImage image;
  private boolean       imageEnlargementEnabled;
  
  /**
   * Creates a component that will display no image.
   */
  public ScaledImageComponent() {
    this(null);
  }

  /**
   * Creates a component that will display the given <code>image</code> 
   * at a maximum scale equal to 1.
   */
  public ScaledImageComponent(BufferedImage image) {
    this(image, false);
  }

  /**
   * Creates a component that will display the given <code>image</code> 
   * with no maximum scale if <code>imageEnlargementEnabled</code> is <code>true</code>.
   */
  public ScaledImageComponent(BufferedImage image, 
                              boolean imageEnlargementEnabled) {
    this.image = image;
    this.imageEnlargementEnabled = imageEnlargementEnabled;
    setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
  }

  /**
   * Returns the preferred size of this component.
   */
  @Override
  public Dimension getPreferredSize() {
    if (isPreferredSizeSet()) {
      return super.getPreferredSize();
    } else {
      Insets insets = getInsets();
      final int defaultPreferredWidth = 300; 
      final int defaultPreferredHeight = 300; 
      if (this.image == null) {
        return new Dimension(defaultPreferredWidth + insets.left + insets.right, 
            defaultPreferredHeight + insets.top + insets.bottom);
      } else {
        // Compute the component preferred size in such a way 
        // its bigger dimension (width or height) is 300 pixels
        int maxImagePreferredWith   = defaultPreferredWidth - insets.left - insets.right;
        int maxImagePreferredHeight = defaultPreferredHeight - insets.top - insets.bottom;
        float widthScale = (float)this.image.getWidth() / maxImagePreferredWith;
        float heightScale = (float)this.image.getHeight() / maxImagePreferredHeight;
        if (widthScale > heightScale) {
          return new Dimension(defaultPreferredWidth, (int)(image.getHeight() / widthScale) + insets.top + insets.bottom);
        } else {
          return new Dimension((int)(this.image.getWidth() / heightScale) + insets.left + insets.right, defaultPreferredHeight);
        }
      }
    }
  }
  
  @Override
  protected void paintComponent(Graphics g) {
    if (isOpaque()) {
      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());
    }
    paintImage(g, null);
  }

  /**
   * Paints the image with a given <code>composite</code>. 
   * Image is scaled to fill width of the component. 
   */
  protected void paintImage(Graphics g, AlphaComposite composite) {
    if (image != null) {
      Graphics2D g2D = (Graphics2D)g;
      g2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
      AffineTransform oldTransform = g2D.getTransform();
      Composite oldComposite = g2D.getComposite();
      Point translation = getImageTranslation();      
      g2D.translate(translation.x, translation.y);
      float scale = getImageScale();
      g2D.scale(scale, scale);    
      
      if (composite != null) {
        g2D.setComposite(composite);
      }
      // Draw image with composite
      g2D.drawImage(this.image, 0, 0, this);
      g2D.setComposite(oldComposite);
      g2D.setTransform(oldTransform);
    }
  }
  
  /**
   * Sets the image drawn by this component.
   */
  public void setImage(BufferedImage image) {
    this.image = image;
    revalidate();
    repaint();
  }

  /**
   * Returns the image drawn by this component.
   */
  public BufferedImage getImage() {
    return this.image;
  }
  
  /**
   * Returns the scale used to draw the image of this component.
   */
  protected float getImageScale() {
    if (this.image != null) {
      Insets insets = getInsets();
      float imageScale = Math.min((float)(getWidth() - insets.left - insets.right) / image.getWidth(), 
          (float)(getHeight() - insets.top - insets.bottom) / image.getHeight());
      if (this.imageEnlargementEnabled) {
        return imageScale;
      } else {
        return Math.min(1, imageScale);
      }
    } else {
      return 1;
    }
  }
  
  /**
   * Returns the origin point where the image of this component is drawn.
   */
  protected Point getImageTranslation() {
    float scale = getImageScale();
    Insets insets = getInsets();
    return new Point(insets.left + (getWidth() - insets.left - insets.right - Math.round(image.getWidth() * scale)) / 2,
        insets.top + (getHeight() - insets.top - insets.bottom - Math.round(image.getHeight() * scale)) / 2);
  }

  /**
   * Returns <code>true</code> if point at (<code>x</code>, <code>y</code>)
   * is in the image displayed by this component.
   */
  protected boolean isPointInImage(int x, int y) {
    Point translation = getImageTranslation();
    float scale = getImageScale();
    return x > translation.x && x < translation.x + Math.round(getImage().getWidth() * scale)
        && y > translation.y && y < translation.y + Math.round(getImage().getHeight() * scale);
  }
}