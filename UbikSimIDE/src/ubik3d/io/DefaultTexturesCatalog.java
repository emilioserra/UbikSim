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
package ubik3d.io;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import ubik3d.model.CatalogTexture;
import ubik3d.model.Content;
import ubik3d.model.IllegalHomonymException;
import ubik3d.model.TexturesCatalog;
import ubik3d.model.TexturesCategory;
import ubik3d.model.UserPreferences;
import ubik3d.tools.ResourceURLContent;
import ubik3d.tools.TemporaryURLContent;
import ubik3d.tools.URLContent;


/**
 * Textures default catalog read from localized resources.
 * @author Emmanuel Puybaret
 */
public class DefaultTexturesCatalog extends TexturesCatalog {
  /**
   * The keys of the properties values read in <code>.properties</code> files.
   */
  public enum PropertyKey {
    /**
     * The key for the ID of a texture (optional). 
     * Two textures read in a texture catalog can't have the same ID
     * and the second one will be ignored.   
     */
    ID("id"),
    /**
     * The key for the name of a texture (mandatory).
     */
    NAME("name"),
    /**
     * The key for the category's name of a texture (mandatory).
     * A new category with this name will be created if it doesn't exist.
     */
    CATEGORY("category"),
    /**
     * The key for the image file of a texture (mandatory). 
     * This image file can be either the path to an image relative to classpath
     * or an absolute URL. It should be encoded in application/x-www-form-urlencoded format 
     * if needed.
     */
    IMAGE("image"),
    /**
     * The key for the width in centimeters of a texture (mandatory).
     */
    WIDTH("width"),
    /**
     * The key for the height in centimeters of a texture (mandatory).
     */
    HEIGHT("height"),
    /**
     * The key for the creator of a texture (optional).
     * By default, creator is <code>null</code>.
     */
    CREATOR("creator");

    private String keyPrefix;

    private PropertyKey(String keyPrefix) {
      this.keyPrefix = keyPrefix;
    }
    
    /**
     * Returns the key for the piece property of the given index.
     */
    public String getKey(int textureIndex) {
      return keyPrefix + "#" + textureIndex;
    }
  }

  private static final String PLUGIN_TEXTURES_CATALOG_FAMILY = "PluginTexturesCatalog";

  private static final String ADDITIONAL_TEXTURES_CATALOG_FAMILY  = "AdditionalTexturesCatalog";

  private static final String HOMONYM_TEXTURE_FORMAT = "%s -%d-";

  /**
   * Creates a default textures catalog read from resources.
   */
  public DefaultTexturesCatalog() {
    this((File)null);
  }
  
  /**
   * Creates a default textures catalog read from resources and   
   * textures plugin folder if <code>texturesPluginFolder</code> isn't <code>null</code>.
   */
  public DefaultTexturesCatalog(File texturesPluginFolder) {
    this(null, texturesPluginFolder);
  }
  
  /**
   * Creates a default textures catalog read from resources and   
   * textures plugin folder if <code>texturesPluginFolder</code> isn't <code>null</code>.
   */
  public DefaultTexturesCatalog(final UserPreferences preferences, 
                                File texturesPluginFolder) {
    this(preferences, texturesPluginFolder == null ? null : new File [] {texturesPluginFolder});
  }
  
  /**
   * Creates a default textures catalog read from resources and   
   * textures plugin folders if <code>texturesPluginFolders</code> isn't <code>null</code>.
   */
  public DefaultTexturesCatalog(final UserPreferences preferences, 
                                File [] texturesPluginFolders) {
    Map<TexturesCategory, Map<CatalogTexture, Integer>> textureHomonymsCounter = 
        new HashMap<TexturesCategory, Map<CatalogTexture,Integer>>();
    List<String> identifiedTextures = new ArrayList<String>();
    
    // Try to load ubik3d.io.DefaultTexturesCatalog property file from classpath 
    final String defaultTexturesCatalogFamily = DefaultTexturesCatalog.class.getName();
    readTexturesCatalog(defaultTexturesCatalogFamily, 
        preferences, textureHomonymsCounter, identifiedTextures);

    // Try to load ubik3d.io.AdditionalTexturesCatalog property file from classpath 
    String classPackage = defaultTexturesCatalogFamily.substring(0, defaultTexturesCatalogFamily.lastIndexOf("."));
    readTexturesCatalog(classPackage + "." + ADDITIONAL_TEXTURES_CATALOG_FAMILY, 
        preferences, textureHomonymsCounter, identifiedTextures);

    if (texturesPluginFolders != null) {
      for (File texturesPluginFolder : texturesPluginFolders) {
        // Try to load sh3t files from textures plugin folder
        File [] pluginTexturesCatalogFiles = texturesPluginFolder.listFiles(new FileFilter () {
          public boolean accept(File pathname) {
            return pathname.isFile();
          }
        });
        
        if (pluginTexturesCatalogFiles != null) {
          // Treat textures catalog files in reverse order so file named with a date will be taken into account 
          // from most recent to least recent
          Arrays.sort(pluginTexturesCatalogFiles, Collections.reverseOrder());
          for (File pluginTexturesCatalogFile : pluginTexturesCatalogFiles) {
            // Try to load the properties file describing textures catalog from current file  
            readPluginTexturesCatalog(pluginTexturesCatalogFile, textureHomonymsCounter, identifiedTextures);
          }
        }
      }
    }
  }
  
  /**
   * Creates a default textures catalog read only from resources in the given URLs.
   */
  public DefaultTexturesCatalog(URL [] pluginTexturesCatalogUrls) {
    this(pluginTexturesCatalogUrls, null);
  }
  
  /**
   * Creates a default textures catalog read only from resources in the given URLs.
   * Texture image URLs will built from <code>texturesResourcesUrlBase</code> if it isn't <code>null</code>.
   */
  public DefaultTexturesCatalog(URL [] pluginTexturesCatalogUrls,
                                URL    texturesResourcesUrlBase) {
    Map<TexturesCategory, Map<CatalogTexture, Integer>> textureHomonymsCounter = 
        new HashMap<TexturesCategory, Map<CatalogTexture,Integer>>();
    List<String> identifiedTextures = new ArrayList<String>();

    for (URL pluginTexturesCatalogUrl : pluginTexturesCatalogUrls) {
      try {
        readTextures(ResourceBundle.getBundle(PLUGIN_TEXTURES_CATALOG_FAMILY, Locale.getDefault(),
                new URLClassLoader(new URL [] {pluginTexturesCatalogUrl})),
            pluginTexturesCatalogUrl, texturesResourcesUrlBase, textureHomonymsCounter, identifiedTextures);
      } catch (MissingResourceException ex) {
        // Ignore malformed textures catalog
      } catch (IllegalArgumentException ex) {
        // Ignore malformed textures catalog
      }
    }
  }

  private static final Map<File,URL> pluginTexturesCatalogUrlUpdates = new HashMap<File,URL>(); 
  
  /**
   * Reads plug-in textures catalog from the <code>pluginTexturesCatalogFile</code> file. 
   */
  private void readPluginTexturesCatalog(File pluginTexturesCatalogFile,
                                         Map<TexturesCategory, Map<CatalogTexture, Integer>> textureHomonymsCounter, 
                                         List<String> identifiedTextures) {
    try {
      URL pluginTexturesCatalogUrl = pluginTexturesCatalogFile.toURI().toURL();
      long urlModificationDate = pluginTexturesCatalogFile.lastModified();
      URL urlUpdate = pluginTexturesCatalogUrlUpdates.get(pluginTexturesCatalogFile);
      boolean modifiableUrl = pluginTexturesCatalogFile.canWrite();
      if (modifiableUrl
          && (urlUpdate == null 
              || urlUpdate.openConnection().getLastModified() < urlModificationDate)) {
        // Copy updated resource URL content to a temporary file to ensure textures used in home can safely 
        // reference any file of the catalog file even if its content is changed afterwards
        TemporaryURLContent contentCopy = TemporaryURLContent.copyToTemporaryURLContent(new URLContent(pluginTexturesCatalogUrl));
        URL temporaryTexturesCatalogUrl = contentCopy.getURL();
        pluginTexturesCatalogUrlUpdates.put(pluginTexturesCatalogFile, temporaryTexturesCatalogUrl);
        pluginTexturesCatalogUrl = temporaryTexturesCatalogUrl;
      } else if (urlUpdate != null) {
        pluginTexturesCatalogUrl = urlUpdate;
      }
      
      ResourceBundle resourceBundle = ResourceBundle.getBundle(PLUGIN_TEXTURES_CATALOG_FAMILY, Locale.getDefault(),
          new URLClassLoader(new URL [] {pluginTexturesCatalogUrl}));      
      readTextures(resourceBundle, pluginTexturesCatalogUrl, null, textureHomonymsCounter, identifiedTextures);
    } catch (MissingResourceException ex) {
      // Ignore malformed textures catalog
    } catch (IllegalArgumentException ex) {
      // Ignore malformed textures catalog
    } catch (IOException ex) {
      // Ignore unaccessible catalog
    }
  }
  
  /**
   * Reads textures of a given catalog family from resources.
   */
  private void readTexturesCatalog(final String texturesCatalogFamily,
                                   final UserPreferences preferences,
                                   Map<TexturesCategory, Map<CatalogTexture, Integer>> textureHomonymsCounter,
                                   List<String> identifiedTextures) {
    ResourceBundle resource;
    if (preferences != null) {
      // Adapt getLocalizedString to ResourceBundle
      resource = new ResourceBundle() {
          @Override
          protected Object handleGetObject(String key) {
            try {
              return preferences.getLocalizedString(texturesCatalogFamily, key);
            } catch (IllegalArgumentException ex) {
              throw new MissingResourceException("Unknown key " + key, 
                  texturesCatalogFamily + "_" + Locale.getDefault(), key);
            }
          }
          
          @Override
          public Enumeration<String> getKeys() {
            // Not needed
            throw new UnsupportedOperationException();
          }
        };
    } else {
      try {
        resource = ResourceBundle.getBundle(texturesCatalogFamily);
      } catch (MissingResourceException ex) {
        return;
      }
    }
    readTextures(resource, null, null, textureHomonymsCounter, identifiedTextures);
  }
  
  /**
   * Reads each texture described in <code>resource</code> bundle.
   * Resources described in texture properties will be loaded from <code>texturesUrl</code> 
   * if it isn't <code>null</code>. 
   */
  private void readTextures(ResourceBundle resource, 
                            URL texturesUrl,
                            URL texturesResourcesUrlBase,
                            Map<TexturesCategory, Map<CatalogTexture, Integer>> textureHomonymsCounter,
                            List<String> identifiedTextures) {
    for (int index = 1;; index++) {
      String name = null;
      try {
        name = resource.getString(PropertyKey.NAME.getKey(index));
      } catch (MissingResourceException ex) {
        // Stop the loop when a key name# doesn't exist
        break;
      }
      String category = resource.getString(PropertyKey.CATEGORY.getKey(index));
      Content image  = getContent(resource, PropertyKey.IMAGE.getKey(index), 
          texturesUrl, texturesResourcesUrlBase);
      float width = Float.parseFloat(resource.getString(PropertyKey.WIDTH.getKey(index)));
      float height = Float.parseFloat(resource.getString(PropertyKey.HEIGHT.getKey(index)));
      String creator = getOptionalString(resource, PropertyKey.CREATOR.getKey(index));
      String id = getOptionalString(resource, PropertyKey.ID.getKey(index));

      CatalogTexture texture = new CatalogTexture(id, name, image, width, height, creator);
      if (texture.getId() != null) {
        // Take into account only texture that have an ID
        if (identifiedTextures.contains(texture.getId())) {
          continue;
        } else {
          // Add id to identifiedTextures to be sure that two textures with a same ID
          // won't be added twice to texture catalog (in case they are cited twice
          // in different texture properties files)
          identifiedTextures.add(texture.getId());
        }
      }

      add(new TexturesCategory(category), texture, textureHomonymsCounter);
    }
  }
  
  /**
   * Adds a <code>piece</code> to its category in catalog. If <code>piece</code> has an homonym
   * in its category its name will be suffixed indicating its sequence.
   */
  private void add(TexturesCategory textureCategory,
                   CatalogTexture texture,
                   Map<TexturesCategory, Map<CatalogTexture, Integer>> textureHomonymsCounter) {
    try {        
      add(textureCategory, texture);
    } catch (IllegalHomonymException ex) {
      // Search the counter of piece name
      Map<CatalogTexture, Integer> categoryTextureHomonymsCounter = 
        textureHomonymsCounter.get(textureCategory);
      if (categoryTextureHomonymsCounter == null) {
        categoryTextureHomonymsCounter = new HashMap<CatalogTexture, Integer>();
        textureHomonymsCounter.put(textureCategory, categoryTextureHomonymsCounter);
      }
      Integer textureHomonymCounter = categoryTextureHomonymsCounter.get(texture);
      if (textureHomonymCounter == null) {
        textureHomonymCounter = 1;
      }
      categoryTextureHomonymsCounter.put(texture, ++textureHomonymCounter);
      // Try to add texture again to catalog with a suffix indicating its sequence
      texture = new CatalogTexture(String.format(HOMONYM_TEXTURE_FORMAT, texture.getName(), textureHomonymCounter), 
          texture.getImage(), texture.getWidth(), texture.getHeight());
      add(textureCategory, texture, textureHomonymsCounter);
    }
  }

  /**
   * Returns a valid content instance from the resource file or URL value of key.
   * @param resource a resource bundle
   * @param contentKey the key of a resource file
   * @param texturesUrl the URL of the file containing the target resource if it's not <code>null</code> 
   * @param resourceUrlBase the URL used as a base to build the URL to content file  
   *            or <code>null</code> if it's read from current classpath or <code>texturesUrl</code>.
   * @throws IllegalArgumentException if the file value doesn't match a valid resource or URL.
   */
  private Content getContent(ResourceBundle resource, 
                             String         contentKey,
                             URL            texturesUrl,
                             URL            resourceUrlBase) {
    String contentFile = resource.getString(contentKey);
    try {
      // Try first to interpret contentFile as an absolute URL 
      // or an URL relative to resourceUrlBase if it's not null
      URL url;
      if (resourceUrlBase != null) {
        url = new URL(resourceUrlBase, contentFile);
      } else {
        url = new URL(contentFile);
      }
      return new URLContent(url);
    } catch (MalformedURLException ex) {
      if (texturesUrl == null) {
        // Otherwise find if it's a resource
        return new ResourceURLContent(DefaultTexturesCatalog.class, contentFile);
      } else {
        try {
          return new URLContent(new URL("jar:" + texturesUrl + "!" + contentFile));
        } catch (MalformedURLException ex2) {
          throw new IllegalArgumentException("Invalid URL", ex2);
        }
      }
    }
  }

  /**
   * Returns the value of <code>propertyKey</code> in <code>resource</code>, 
   * or <code>null</code> if the property doesn't exist.
   */
  private String getOptionalString(ResourceBundle resource, 
                                   String propertyKey) {
    try {
      return resource.getString(propertyKey);
    } catch (MissingResourceException ex) {
      return null;
    }
  }
}
