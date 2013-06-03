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
package ubik3d.io;

import java.util.ArrayList;
import java.util.List;

import ubik3d.model.Content;
import ubik3d.model.LengthUnit;
import ubik3d.model.PatternsCatalog;
import ubik3d.model.RecorderException;
import ubik3d.model.TextureImage;
import ubik3d.model.UserPreferences;
import ubik3d.tools.ResourceURLContent;


/**
 * Default user preferences.
 * @author Emmanuel Puybaret
 */
public class DefaultUserPreferences extends UserPreferences {
  /**
   * Creates default user preferences read from resource files.
   */
  public DefaultUserPreferences() {
    // Read default furniture catalog
    setFurnitureCatalog(new DefaultFurnitureCatalog());
    // Read default textures catalog
    setTexturesCatalog(new DefaultTexturesCatalog());
    // Build default patterns catalog
    List<TextureImage> patterns = new ArrayList<TextureImage>();
    patterns.add(new PatternTexture("foreground"));
    patterns.add(new PatternTexture("hatchUp"));
    patterns.add(new PatternTexture("hatchDown"));
    patterns.add(new PatternTexture("crossHatch"));
    patterns.add(new PatternTexture("background"));
    PatternsCatalog patternsCatalog = new PatternsCatalog(patterns);
    setPatternsCatalog(patternsCatalog);
    // Read other preferences from resource bundle
    setFurnitureCatalogViewedInTree(Boolean.parseBoolean(
        getLocalizedString(DefaultUserPreferences.class, "furnitureCatalogViewedInTree")));
    setNavigationPanelVisible(Boolean.parseBoolean(getLocalizedString(DefaultUserPreferences.class, "navigationPanelVisible")));    
    setUnit(LengthUnit.valueOf(getLocalizedString(DefaultUserPreferences.class, "unit").toUpperCase()));
    setRulersVisible(Boolean.parseBoolean(getLocalizedString(DefaultUserPreferences.class, "rulersVisible")));
    setGridVisible(Boolean.parseBoolean(getLocalizedString(DefaultUserPreferences.class, "gridVisible")));
    setFurnitureViewedFromTop(Boolean.parseBoolean(getLocalizedString(DefaultUserPreferences.class, "furnitureViewedFromTop")));
    setFloorColoredOrTextured(Boolean.parseBoolean(getLocalizedString(DefaultUserPreferences.class, "roomFloorColoredOrTextured")));
    setWallPattern(patternsCatalog.getPattern(getLocalizedString(DefaultUserPreferences.class, "wallPattern")));
    setNewWallThickness(Float.parseFloat(getLocalizedString(DefaultUserPreferences.class, "newWallThickness")));
    setNewWallHeight(Float.parseFloat(getLocalizedString(DefaultUserPreferences.class, "newHomeWallHeight")));
    try {
      setAutoSaveDelayForRecovery(Integer.parseInt(getLocalizedString(DefaultUserPreferences.class, "autoSaveDelayForRecovery")));
    } catch (IllegalArgumentException ex) {
      // Disable auto save
      setAutoSaveDelayForRecovery(0);
    }
    setRecentHomes(new ArrayList<String>());
    try {
      setCurrency(getLocalizedString(DefaultUserPreferences.class, "currency"));
    } catch (IllegalArgumentException ex) {
      // Don't use currency and prices in program
    }
  }

  /**
   * Throws an exception because default user preferences can't be written 
   * with this class.
   */
  @Override
  public void write() throws RecorderException {
    throw new RecorderException("Default user preferences can't be written");
  }

  /**
   * Throws an exception because default user preferences can't manage language libraries.
   */
  @Override
  public boolean languageLibraryExists(String name) throws RecorderException {
    throw new RecorderException("Default user preferences can't manage language libraries");
  }

  /**
   * Throws an exception because default user preferences can't manage additional language libraries.
   */
  @Override
  public void addLanguageLibrary(String name) throws RecorderException {
    throw new RecorderException("Default user preferences can't manage language libraries");
  }
  
  /**
   * Throws an exception because default user preferences can't manage furniture libraries.
   */
  @Override
  public boolean furnitureLibraryExists(String name) throws RecorderException {
    throw new RecorderException("Default user preferences can't manage furniture libraries");
  }

  /**
   * Throws an exception because default user preferences can't manage additional furniture libraries.
   */
  @Override
  public void addFurnitureLibrary(String name) throws RecorderException {
    throw new RecorderException("Default user preferences can't manage furniture libraries");
  }
  
  /**
   * Throws an exception because default user preferences can't manage textures libraries.
   */
  @Override
  public boolean texturesLibraryExists(String name) throws RecorderException {
    throw new RecorderException("Default user preferences can't manage textures libraries");
  }

  /**
   * Throws an exception because default user preferences can't manage additional textures libraries.
   */
  @Override
  public void addTexturesLibrary(String name) throws RecorderException {
    throw new RecorderException("Default user preferences can't manage textures libraries");
  }
  
  /**
   * A fixed sized pattern.
   */
  private static class PatternTexture implements TextureImage {
    private final String name;
    private final Content image;

    public PatternTexture(String name) {
      this.name = name;
      this.image = new ResourceURLContent(PatternTexture.class, "resources/patterns/" + name + ".png");
    }

    public String getName() {
      return this.name;
    }

    public Content getImage() {
      return this.image;
    }
    
    public float getWidth() {
      return 10;
    }
    
    public float getHeight() {
      return 10;
    }
  }
}
