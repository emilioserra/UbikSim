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
import java.text.Collator;

/**
 * A catalog piece of furniture.
 * @author Emmanuel Puybaret
 */
public class CatalogPieceOfFurniture implements Comparable<CatalogPieceOfFurniture>, PieceOfFurniture {
  private static final long       serialVersionUID   = 1L;

  private static final float [][] INDENTITY_ROTATION = new float [] [] { {1, 0, 0}, {0, 1, 0}, {0, 0, 1}};

  private final String            metadata;
  private final String            rol;
  private final float             amount;
  private final String            id;
  private final String            name;
  private final String            description;
  private final Content           icon;
  private final Content           planIcon;
  private final Content           model;
  private final float             width;
  private final float             depth;
  private final float             height;
  private final boolean           proportional;
  private final float             elevation;
  private final boolean           movable;
  private final boolean           doorOrWindow;
  private final float [][]        modelRotation;
  private final String            creator;
  private final boolean           backFaceShown;
  private final Integer           color;
  private final float             iconYaw;
  private final boolean           modifiable;
  private final boolean           resizable;
  private final boolean           deformable;
  private final boolean           texturable;
  private final BigDecimal        price;
  private final BigDecimal        valueAddedTaxPercentage;

  private FurnitureCategory       category;

  private static final Collator   COMPARATOR         = Collator.getInstance();

  /**
   * Creates a catalog piece of furniture.
   * @param name the name of the new piece
   * @param icon content of the icon of the new piece
   * @param model content of the 3D model of the new piece
   * @param width the width in centimeters of the new piece
   * @param depth the depth in centimeters of the new piece
   * @param height the height in centimeters of the new piece
   * @param movable if <code>true</code>, the new piece is movable
   * @param doorOrWindow if <code>true</code>, the new piece is a door or a
   *          window
   * @deprecated As of version 1.7, use constructor without
   *             <code>doorOrWindow</code> parameter since a catalog door and
   *             window is supposed to be an instance of
   *             {@link CatalogDoorOrWindow}
   *             String id, String metadata, String rol, String name, String description,
                                  Content icon, Content planIcon, Content model, float amount, float width,
                                  float depth, float height, float elevation, boolean movable, boolean doorOrWindow,
                                  Integer color, float [][] modelRotation, String creator, boolean backFaceShown,
                                  boolean resizable, boolean deformable, boolean texturable, BigDecimal price,
                                  BigDecimal valueAddedTaxPercentage, float iconYaw, boolean proportional,
                                  boolean modifiable
   */
  @Deprecated
  public CatalogPieceOfFurniture(String metadata, String rol, String name, Content icon, Content model, float amount,
                                 float width, float depth, float height, float elevation,
                                 boolean movable, boolean doorOrWindow) {
    this(null, metadata, rol, name, null, icon, icon, model, amount, width, depth, height, elevation, movable, doorOrWindow, null,
        INDENTITY_ROTATION, "UMU", false, true, true, true, null, null, (float)Math.PI / 8, true, true);
  }

  /**
   * Creates an unmodifiable catalog piece of furniture of the default catalog.
   * @param id the id of the new piece or <code>null</code>
   * @param name the name of the new piece
   * @param description the description of the new piece
   * @param icon content of the icon of the new piece
   * @param model content of the 3D model of the new piece
   * @param width the width in centimeters of the new piece
   * @param depth the depth in centimeters of the new piece
   * @param height the height in centimeters of the new piece
   * @param elevation the elevation in centimeters of the new piece
   * @param movable if <code>true</code>, the new piece is movable
   * @param doorOrWindow if <code>true</code>, the new piece is a door or a
   *          window
   * @param modelRotation the rotation 3 by 3 matrix applied to the piece model
   * @param creator the creator of the model
   * @param resizable if <code>true</code>, the size of the new piece may be
   *          edited
   * @param price the price of the new piece or <code>null</code>
   * @param valueAddedTaxPercentage the Value Added Tax percentage applied to
   *          the price of the new piece or <code>null</code>
   * @deprecated As of version 1.7, use constructor without
   *             <code>doorOrWindow</code> parameter since a catalog door and
   *             window is supposed to be an instance of
   *             {@link CatalogDoorOrWindow}
   */
  @Deprecated
  public CatalogPieceOfFurniture(String id, String metadata, String rol, String name, String description, Content icon,
                                 Content model, float amount, float width, float depth, float height, float elevation,
                                 boolean movable, boolean doorOrWindow, float [][] modelRotation, String creator,
                                 boolean resizable, BigDecimal price, BigDecimal valueAddedTaxPercentage) {
    this(id, metadata, rol, name, description, icon, null, model, amount, width, depth, height, elevation, movable,
        doorOrWindow, null, modelRotation, creator, false, resizable, true, true, price, valueAddedTaxPercentage,
        (float) Math.PI / 8, true, false);
  }

  /**
   * Creates an unmodifiable catalog piece of furniture of the default catalog.
   * @param id the id of the new piece or <code>null</code>
   * @param name the name of the new piece
   * @param description the description of the new piece
   * @param icon content of the icon of the new piece
   * @param model content of the 3D model of the new piece
   * @param width the width in centimeters of the new piece
   * @param depth the depth in centimeters of the new piece
   * @param height the height in centimeters of the new piece
   * @param elevation the elevation in centimeters of the new piece
   * @param movable if <code>true</code>, the new piece is movable
   * @param modelRotation the rotation 3 by 3 matrix applied to the piece model
   * @param creator the creator of the model
   * @param resizable if <code>true</code>, the size of the new piece may be
   *          edited
   * @param price the price of the new piece or <code>null</code>
   * @param valueAddedTaxPercentage the Value Added Tax percentage applied to
   *          the price of the new piece or <code>null</code>
   * @since 1.7
   */
  public CatalogPieceOfFurniture(String id, String metadata, String rol, String name, String description, Content icon,
                                 Content model, float amount, float width, float depth, float height, float elevation,
                                 boolean movable, float [][] modelRotation, String creator, boolean resizable,
                                 BigDecimal price, BigDecimal valueAddedTaxPercentage) {
    this(id, metadata, rol, name, description, icon, null, model, amount, width, depth, height, elevation, movable,
        modelRotation, creator, resizable, price, valueAddedTaxPercentage);
  }

  /**
   * Creates an unmodifiable catalog piece of furniture of the default catalog.
   * @param id the id of the new piece or <code>null</code>
   * @param name the name of the new piece
   * @param description the description of the new piece
   * @param icon content of the icon of the new piece
   * @param planIcon content of the icon of the new piece displayed in plan
   * @param model content of the 3D model of the new piece
   * @param width the width in centimeters of the new piece
   * @param depth the depth in centimeters of the new piece
   * @param height the height in centimeters of the new piece
   * @param elevation the elevation in centimeters of the new piece
   * @param movable if <code>true</code>, the new piece is movable
   * @param modelRotation the rotation 3 by 3 matrix applied to the piece model
   * @param creator the creator of the model
   * @param resizable if <code>true</code>, the size of the new piece may be
   *          edited
   * @param price the price of the new piece or <code>null</code>
   * @param valueAddedTaxPercentage the Value Added Tax percentage applied to
   *          the price of the new piece or <code>null</code>
   * @since 2.2
   */
  public CatalogPieceOfFurniture(String id, String metadata, String rol, String name, String description, Content icon,
                                 Content planIcon, Content model, float amount, float width, float depth, float height,
                                 float elevation, boolean movable, float [][] modelRotation, String creator,
                                 boolean resizable, BigDecimal price, BigDecimal valueAddedTaxPercentage) {
    this(id, metadata, rol, name, description, icon, planIcon, model, amount, width, depth, height, elevation, movable,
        modelRotation, creator, resizable, true, true, price, valueAddedTaxPercentage);
  }

  /**
   * Creates an unmodifiable catalog piece of furniture of the default catalog.
   * @param id the id of the new piece or <code>null</code>
   * @param name the name of the new piece
   * @param description the description of the new piece
   * @param icon content of the icon of the new piece
   * @param planIcon content of the icon of the new piece displayed in plan
   * @param model content of the 3D model of the new piece
   * @param width the width in centimeters of the new piece
   * @param depth the depth in centimeters of the new piece
   * @param height the height in centimeters of the new piece
   * @param elevation the elevation in centimeters of the new piece
   * @param movable if <code>true</code>, the new piece is movable
   * @param modelRotation the rotation 3 by 3 matrix applied to the piece model
   * @param creator the creator of the model
   * @param resizable if <code>true</code>, the size of the new piece may be
   *          edited
   * @param deformable if <code>true</code>, the width, depth and height of the
   *          new piece may change independently from each other
   * @param texturable if <code>false</code> this piece should always keep the
   *          same color or texture.
   * @param price the price of the new piece or <code>null</code>
   * @param valueAddedTaxPercentage the Value Added Tax percentage applied to
   *          the price of the new piece or <code>null</code>
   * @since 3.0
   */
  public CatalogPieceOfFurniture(String id, String metadata, String rol, String name, String description, Content icon,
                                 Content planIcon, Content model, float amount, float width, float depth, float height,
                                 float elevation, boolean movable, float [][] modelRotation, String creator,
                                 boolean resizable, boolean deformable, boolean texturable, BigDecimal price,
                                 BigDecimal valueAddedTaxPercentage) {
    this(id, metadata, rol, name, description, icon, planIcon, model, amount, width, depth, height, elevation, movable,
        false, null, modelRotation, creator, false, resizable, deformable, texturable, price, valueAddedTaxPercentage,
        (float) Math.PI / 8, true, false);
  }

  /**
   * Creates a modifiable catalog piece of furniture with all its values.
   * @param name the name of the new piece
   * @param icon content of the icon of the new piece
   * @param model content of the 3D model of the new piece
   * @param width the width in centimeters of the new piece
   * @param depth the depth in centimeters of the new piece
   * @param height the height in centimeters of the new piece
   * @param elevation the elevation in centimeters of the new piece
   * @param movable if <code>true</code>, the new piece is movable
   * @param doorOrWindow if <code>true</code>, the new piece is a door or a
   *          window
   * @param color the color of the piece as RGB code or <code>null</code> if
   *          piece color is unchanged
   * @param modelRotation the rotation 3 by 3 matrix applied to the piece model
   * @param backFaceShown <code>true</code> if back face should be shown
   * @param iconYaw the yaw angle used to create the piece icon
   * @param proportional if <code>true</code>, size proportions will be kept
   * @deprecated As of version 1.7, use constructor without
   *             <code>doorOrWindow</code> parameter since a catalog door and
   *             window is supposed to be an instance of
   *             {@link CatalogDoorOrWindow}
   */
  @Deprecated
  public CatalogPieceOfFurniture(String metadata, String rol, String name, Content icon, Content model, float amount,
                                 float width, float depth, float height, float elevation, boolean movable,
                                 boolean doorOrWindow, Integer color, float [][] modelRotation, boolean backFaceShown,
                                 float iconYaw, boolean proportional) {
    this(null, metadata, rol, name, null, icon, null, model, amount, width, depth, height, elevation, movable, doorOrWindow, color,
        modelRotation, null, backFaceShown, true, true, true, null, null, iconYaw, proportional, true);
  }

  /**
   * Creates a modifiable catalog piece of furniture with all its values.
   * @param name the name of the new piece
   * @param icon content of the icon of the new piece
   * @param model content of the 3D model of the new piece
   * @param width the width in centimeters of the new piece
   * @param depth the depth in centimeters of the new piece
   * @param height the height in centimeters of the new piece
   * @param elevation the elevation in centimeters of the new piece
   * @param movable if <code>true</code>, the new piece is movable
   * @param color the color of the piece as RGB code or <code>null</code> if
   *          piece color is unchanged
   * @param modelRotation the rotation 3 by 3 matrix applied to the piece model
   * @param backFaceShown <code>true</code> if back face should be shown
   * @param iconYaw the yaw angle used to create the piece icon
   * @param proportional if <code>true</code>, size proportions will be kept
   * @since 1.7
   */
  public CatalogPieceOfFurniture(String metadata, String rol, String name, Content icon, Content model, float amount,
                                 float width, float depth, float height, float elevation, boolean movable,
                                 Integer color, float [][] modelRotation, boolean backFaceShown, float iconYaw,
                                 boolean proportional) {
    this(null, metadata, rol, name, null, icon, null, model, amount, width, depth, height, elevation, movable, false,
        color, modelRotation, null, backFaceShown, true, true, true, null, null, iconYaw, proportional, true);
  }

  private CatalogPieceOfFurniture(String id, String metadata, String rol, String name, String description,
                                  Content icon, Content planIcon, Content model, float amount, float width,
                                  float depth, float height, float elevation, boolean movable, boolean doorOrWindow,
                                  Integer color, float [][] modelRotation, String creator, boolean backFaceShown,
                                  boolean resizable, boolean deformable, boolean texturable, BigDecimal price,
                                  BigDecimal valueAddedTaxPercentage, float iconYaw, boolean proportional,
                                  boolean modifiable) {
    this.metadata = metadata;
    this.rol = rol;
    this.amount = amount;
    this.id = id;
    this.name = name;
    this.description = description;
    this.icon = icon;
    this.planIcon = planIcon;
    this.model = model;
    this.width = width;
    this.depth = depth;
    this.height = height;
    this.elevation = elevation;
    this.movable = movable;
    this.doorOrWindow = doorOrWindow;
    this.color = color;
    this.creator = creator;
    this.price = price;
    this.valueAddedTaxPercentage = valueAddedTaxPercentage;
    if (modelRotation == null) {
      this.modelRotation = INDENTITY_ROTATION;
    } else {
      this.modelRotation = deepCopy(modelRotation);
    }
    this.backFaceShown = backFaceShown;
    this.resizable = resizable;
    this.deformable = deformable;
    this.texturable = texturable;
    this.iconYaw = iconYaw;
    this.proportional = proportional;
    this.modifiable = modifiable;
  }

  /**
   * Returns the ID of this piece of furniture or <code>null</code>.
   */
  public String getId() {
    return this.id;
  }

  /**
   * Returns the metadata of this piece of furniture.
   */
  public String getMetadata() {
    return this.metadata;
  }

  /**
   * Returns the rol of this piece of furniture.
   */

  public float getAmount() {
    return this.amount;
  }

  public String getRol() {
    return this.rol;
  }

  /**
   * Returns the name of this piece of furniture.
   */
  public String getName() {
    return this.name;
  }

  /**
   * Returns the description of this piece of furniture. The returned value may
   * be <code>null</code>.
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Returns the depth of this piece of furniture.
   */
  public float getDepth() {
    return this.depth;
  }

  /**
   * Returns the height of this piece of furniture.
   */
  public float getHeight() {
    return this.height;
  }

  /**
   * Returns the width of this piece of furniture.
   */
  public float getWidth() {
    return this.width;
  }

  /**
   * Returns the elevation of this piece of furniture.
   */
  public float getElevation() {
    return this.elevation;
  }

  /**
   * Returns <code>true</code> if this piece of furniture is movable.
   */
  public boolean isMovable() {
    return this.movable;
  }

  /**
   * Returns <code>true</code> if this piece of furniture is a door or a window.
   * As this method existed before {@linkplain CatalogDoorOrWindow
   * CatalogDoorOrWindow} class, you shouldn't rely on the value returned by
   * this method to guess if a piece is an instance of <code>DoorOrWindow</code>
   * class.
   */
  public boolean isDoorOrWindow() {
    return this.doorOrWindow;
  }

  /**
   * Returns the icon of this piece of furniture.
   */
  public Content getIcon() {
    return this.icon;
  }

  /**
   * Returns the icon of this piece of furniture displayed in plan or
   * <code>null</code>.
   * @since 2.2
   */
  public Content getPlanIcon() {
    return this.planIcon;
  }

  /**
   * Returns the 3D model of this piece of furniture.
   */
  public Content getModel() {
    return this.model;
  }

  /**
   * Returns the rotation 3 by 3 matrix of this piece of furniture that ensures
   * its model is correctly oriented.
   */
  public float [][] getModelRotation() {
    // Return a deep copy to avoid any misuse of piece data
    return deepCopy(this.modelRotation);
  }

  private float [][] deepCopy(float [][] modelRotation) {
    return new float [] [] { {modelRotation [0] [0], modelRotation [0] [1], modelRotation [0] [2]},
        {modelRotation [1] [0], modelRotation [1] [1], modelRotation [1] [2]},
        {modelRotation [2] [0], modelRotation [2] [1], modelRotation [2] [2]}};
  }

  /**
   * Returns the creator of this piece.
   */
  public String getCreator() {
    return this.creator;
  }

  /**
   * Returns <code>true</code> if the back face of the piece of furniture model
   * should be displayed.
   */
  public boolean isBackFaceShown() {
    return this.backFaceShown;
  }

  /**
   * Returns the color of this piece of furniture.
   */
  public Integer getColor() {
    return this.color;
  }

  /**
   * Returns the yaw angle used to create the piece icon.
   */
  public float getIconYaw() {
    return this.iconYaw;
  }

  /**
   * Returns <code>true</code> if size proportions should be kept.
   */
  public boolean isProportional() {
    return this.proportional;
  }

  /**
   * Returns <code>true</code> if this piece is modifiable (not read from
   * resources).
   */
  public boolean isModifiable() {
    return this.modifiable;
  }

  /**
   * Returns <code>true</code> if this piece is resizable.
   */
  public boolean isResizable() {
    return this.resizable;
  }

  /**
   * Returns <code>true</code> if this piece is deformable.
   * @since 3.0
   */
  public boolean isDeformable() {
    return this.deformable;
  }

  /**
   * Returns <code>false</code> if this piece should always keep the same color
   * or texture.
   * @since 3.0
   */
  public boolean isTexturable() {
    return this.texturable;
  }

  /**
   * Returns the price of this piece of furniture or <code>null</code>.
   */
  public BigDecimal getPrice() {
    return this.price;
  }

  /**
   * Returns the Value Added Tax percentage applied to the price of this piece
   * of furniture.
   */
  public BigDecimal getValueAddedTaxPercentage() {
    return this.valueAddedTaxPercentage;
  }

  /**
   * Returns the category of this piece of furniture.
   */
  public FurnitureCategory getCategory() {
    return this.category;
  }

  /**
   * Sets the category of this piece of furniture.
   */
  void setCategory(FurnitureCategory category) {
    this.category = category;
  }

  /**
   * Returns true if this piece and the one in parameter have the same name.
   */
  @Override
  public boolean equals(Object obj) {
    return obj instanceof CatalogPieceOfFurniture && COMPARATOR.equals(this.name, ((CatalogPieceOfFurniture) obj).name);
  }

  /**
   * Returns a hash code computed from the name of this piece.
   */
  @Override
  public int hashCode() {
    return this.name.hashCode();
  }

  /**
   * Compares the names of this piece and the one in parameter.
   */
  public int compareTo(CatalogPieceOfFurniture piece) {
    return COMPARATOR.compare(this.name, piece.name);
  }
}
