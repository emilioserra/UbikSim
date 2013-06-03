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

import java.io.Serializable;

/**
 * The print attributes for a home.
 * @author Emmanuel Puybaret
 */
public class HomePrint implements Serializable {
  /**
   * Paper orientation. 
   */
  public enum PaperOrientation {PORTRAIT, LANDSCAPE, REVERSE_LANDSCAPE};
    
  private static final long serialVersionUID = -2868070768300325498L;
  
  private final PaperOrientation paperOrientation;
  private final float            paperWidth;
  private final float            paperHeight;
  private final float            paperTopMargin;
  private final float            paperLeftMargin;
  private final float            paperBottomMargin;
  private final float            paperRightMargin;
  private final boolean          furniturePrinted;
  private final boolean          planPrinted;
  private final boolean          view3DPrinted;
  private final Float            planScale;
  private final String           headerFormat;
  private final String           footerFormat;

  
  /**
   * Create a print attributes for home from the given parameters.
   */
  public HomePrint(PaperOrientation paperOrientation,
                   float paperWidth,
                   float paperHeight,
                   float paperTopMargin,
                   float paperLeftMargin,
                   float paperBottomMargin,
                   float paperRightMargin,
                   boolean furniturePrinted,
                   boolean planPrinted,
                   boolean view3DPrinted,
                   Float planScale,
                   String headerFormat,
                   String footerFormat) {
    this.paperOrientation = paperOrientation;
    this.paperWidth = paperWidth;
    this.paperHeight = paperHeight;
    this.paperTopMargin = paperTopMargin;
    this.paperLeftMargin = paperLeftMargin;
    this.paperBottomMargin = paperBottomMargin;
    this.paperRightMargin = paperRightMargin;
    this.furniturePrinted = furniturePrinted;
    this.planPrinted = planPrinted;
    this.view3DPrinted = view3DPrinted;
    this.planScale = planScale;
    this.headerFormat = headerFormat;
    this.footerFormat = footerFormat;
  }

  /**
   * Returns the paper orientation.
   */
  public PaperOrientation getPaperOrientation() {
    return this.paperOrientation;
  }
  
  /**
   * Returns the margin at paper bottom in 1/72nds of an inch.
   */
  public float getPaperBottomMargin() {
    return this.paperBottomMargin;
  }

  /**
   * Returns the paper height in 1/72nds of an inch.
   */
  public float getPaperHeight() {
    return this.paperHeight;
  }

  /**
   * Returns the margin at paper left in 1/72nds of an inch.
   */
  public float getPaperLeftMargin() {
    return this.paperLeftMargin;
  }

  /**
   * Returns the margin at paper right in 1/72nds of an inch.
   */
  public float getPaperRightMargin() {
    return this.paperRightMargin;
  }

  /**
   * Returns the margin at paper top in 1/72nds of an inch.
   */
  public float getPaperTopMargin() {
    return this.paperTopMargin;
  }

  /**
   * Returns the paper width in 1/72nds of an inch.
   */
  public float getPaperWidth() {
    return this.paperWidth;
  }

  /**
   * Returns whether home furniture should be printed or not.
   */
  public boolean isFurniturePrinted() {
    return this.furniturePrinted;
  }

  /**
   * Returns whether home plan should be printed or not.
   */
  public boolean isPlanPrinted() {
    return this.planPrinted;
  }

  /**
   * Returns whether home 3D view should be printed or not.
   */
  public boolean isView3DPrinted() {
    return this.view3DPrinted;
  } 
  
  /**
   * Returns the scale used to print home plan or 
   * <code>null</code> if no special scale is desired. 
   */
  public Float getPlanScale() {
    return this.planScale;
  }
  
  /**
   * Returns the string format used to print page headers. 
   */
  public String getHeaderFormat() {
    return this.headerFormat;
  }

  /**
   * Returns the string format used to print page footers. 
   */
  public String getFooterFormat() {
    return this.footerFormat;
  }
}
