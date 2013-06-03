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
package ubik3d.swing;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import ubik3d.model.Content;
import ubik3d.tools.ResourceURLContent;


/**
 * Singleton managing icons cache.
 * @author Emmanuel Puybaret
 */
public class IconManager {
  private static IconManager                     instance;
  // Icon used if an image content couldn't be loaded
  private final Content                          errorIconContent;
  // Icon used while an image content is loaded
  private final Content                          waitIconContent;
  // Map storing loaded icons
  private final Map<Content, Map<Integer, Icon>> icons;
  // Executor used by IconProxy to load images
  private ExecutorService                        iconsLoader;

  private IconManager() {
    this.errorIconContent = new ResourceURLContent(IconManager.class, "resources/icons/tango/image-missing.png");
    this.waitIconContent = new ResourceURLContent(IconManager.class, "resources/icons/tango/image-loading.png");
    this.icons = Collections.synchronizedMap(new WeakHashMap<Content, Map<Integer, Icon>>());
  }
  
  /**
   * Returns an instance of this singleton. 
   */
  public static IconManager getInstance() {
    if (instance == null) {
      instance = new IconManager();
    }
    return instance;
  }

  /**
   * Clears the loaded resources cache and shutdowns the multithreaded service 
   * that loads icons. 
   */
  public void clear() {
    if (this.iconsLoader != null) {
      this.iconsLoader.shutdownNow();
      this.iconsLoader = null;
    }
    this.icons.clear();
  }
  
  /**
   * Returns the icon displayed for wrong content resized at a given height.
   */
  public Icon getErrorIcon(int height) {
    return getIcon(this.errorIconContent, height, null);
  }
  
  /**
   * Returns the icon displayed for wrong content.
   */
  public Icon getErrorIcon() {
    return getIcon(this.errorIconContent, -1, null);
  }
  
  /**
   * Returns <code>true</code> if the given <code>icon</code> is the error icon
   * used by this manager to indicate it couldn't load an icon.
   */
  public boolean isErrorIcon(Icon icon) {
    Map<Integer, Icon> errorIcons = this.icons.get(this.errorIconContent);
    return errorIcons != null
        && (errorIcons.containsValue(icon)
            || icon instanceof IconProxy
                && errorIcons.containsValue(((IconProxy)icon).getIcon()));
  }

  /**
   * Returns the icon displayed while a content is loaded resized at a given height.
   */
  public Icon getWaitIcon(int height) {
    return getIcon(this.waitIconContent, height, null);
  }
  
  /**
   * Returns the icon displayed while a content is loaded.
   */
  public Icon getWaitIcon() {
    return getIcon(this.waitIconContent, -1, null);
  }
  
  /**
   * Returns <code>true</code> if the given <code>icon</code> is the wait icon
   * used by this manager to indicate it's currently loading an icon.
   */
  public boolean isWaitIcon(Icon icon) {
    Map<Integer, Icon> waitIcons = this.icons.get(this.waitIconContent);
    return waitIcons != null
        && (waitIcons.containsValue(icon)
            || icon instanceof IconProxy
                && waitIcons.containsValue(((IconProxy)icon).getIcon()));
  }

  /**
   * Returns an icon read from <code>content</code>.
   * @param content an object containing an image
   * @param waitingComponent a waiting component. If <code>null</code>, the returned icon will
   *            be read immediately in the current thread.
   */
  public Icon getIcon(Content content, Component waitingComponent) {
    return getIcon(content, -1, waitingComponent);
  }
  
  /**
   * Returns an icon read from <code>content</code> and rescaled at a given <code>height</code>.
   * @param content an object containing an image
   * @param height  the desired height of the returned icon
   * @param waitingComponent a waiting component. If <code>null</code>, the returned icon will
   *            be read immediately in the current thread.
   */
  public Icon getIcon(Content content, final int height, Component waitingComponent) {
    Map<Integer, Icon> contentIcons = this.icons.get(content);
    if (contentIcons == null) {
      contentIcons = Collections.synchronizedMap(new HashMap<Integer, Icon>());
      this.icons.put(content, contentIcons);
    }
    Icon icon = contentIcons.get(height);
    if (icon == null) {
      // Tolerate null content
      if (content == null) {
        icon = new Icon() {
          public void paintIcon(Component c, Graphics g, int x, int y) {
          }
          
          public int getIconWidth() {
            return Math.max(0, height);
          }
          
          public int getIconHeight() {
            return Math.max(0, height);
          }
        };
      } else if (content == this.errorIconContent ||
                 content == this.waitIconContent) {
        // Load error and wait icons immediately in this thread 
        icon = createIcon(content, height, null); 
      } else if (waitingComponent == null) {
        // Load icon immediately in this thread 
        icon = createIcon(content, height, 
            getIcon(this.errorIconContent, height, null)); 
      } else {
        // For content different from error icon and wait icon, 
        // load it in a different thread with a virtual proxy 
        icon = new IconProxy(content, height, waitingComponent,
                 getIcon(this.errorIconContent, height, null),
                 getIcon(this.waitIconContent, height, null));
      }
      // Store the icon in icons map
      contentIcons.put(height, icon);
    }
    return icon;    
  }
  
  /**
   * Returns an icon created and scaled from its content.
   * @param content the content from which the icon image is read
   * @param height  the desired height of the returned icon
   * @param errorIcon the returned icon in case of error
   */
  private Icon createIcon(Content content, int height, Icon errorIcon) {
    try {
      // Read the icon of the piece 
      InputStream contentStream = content.openStream();
      BufferedImage image = ImageIO.read(contentStream);
      contentStream.close();
      if (image != null) {
        if (height != -1 && height != image.getHeight()) {
          int width = image.getWidth() * height / image.getHeight();
          // Create a scaled image not bound to original image to let the original image being garbage collected 
          BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
          Graphics g = scaledImage.getGraphics();
          g.drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
          g.dispose();
          return new ImageIcon(scaledImage);
        } else {
          return new ImageIcon(image);
        }
      }
    } catch (IOException ex) {
      // Too bad, we'll use errorIcon
    }
    return errorIcon;
  }
  
  /**
   * Proxy icon that displays a temporary icon while waiting 
   * image loading completion. 
   */
  private class IconProxy implements Icon {
    private Icon icon;
    
    public IconProxy(final Content content, final int height,
                     final Component waitingComponent,
                     final Icon errorIcon, Icon waitIcon) {
      this.icon = waitIcon;
      if (iconsLoader == null) {
        iconsLoader = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
      }
      // Load the icon in a different thread
      iconsLoader.execute(new Runnable () {
          public void run() {
            icon = createIcon(content, height, errorIcon);
            waitingComponent.repaint();
          }
        });
    }

    public int getIconWidth() {
      return this.icon.getIconWidth();
    }

    public int getIconHeight() {
      return this.icon.getIconHeight();
    }
    
    public void paintIcon(Component c, Graphics g, int x, int y) {
      this.icon.paintIcon(c, g, x, y);
    }
    
    public Icon getIcon() {
      return this.icon;
    }
  }
}
