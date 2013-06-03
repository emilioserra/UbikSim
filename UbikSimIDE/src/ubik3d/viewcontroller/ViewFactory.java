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
package ubik3d.viewcontroller;

import ubik3d.model.BackgroundImage;
import ubik3d.model.CatalogPieceOfFurniture;
import ubik3d.model.CatalogTexture;
import ubik3d.model.FurnitureCatalog;
import ubik3d.model.Home;
import ubik3d.model.UserPreferences;

/**
 * A factory that specifies how to create the views displayed in Sweet Home 3D. 
 * @author Emmanuel Puybaret
 */
public interface ViewFactory {
  /**
   * Returns a new view that displays furniture <code>catalog</code>.
   */
  public abstract View createFurnitureCatalogView(FurnitureCatalog catalog,
                                           UserPreferences preferences,
                                           FurnitureCatalogController furnitureCatalogController);
  
  /**
   * Returns a new view that displays <code>home</code> furniture list.
   */
  public abstract View createFurnitureView(Home home, UserPreferences preferences,
                                           FurnitureController furnitureController);

  /**
   * Returns a new view that displays <code>home</code> on a plan.
   */
  public abstract PlanView createPlanView(Home home, UserPreferences preferences,
                                          PlanController planController);

  /**
   * Returns a new view that displays <code>home</code> in 3D.
   */
  public abstract View createView3D(Home home, UserPreferences preferences,
                                    HomeController3D homeController3D);

  /**
   * Returns a new view that displays <code>home</code> and its sub views.
   */
  public abstract HomeView createHomeView(Home home, UserPreferences preferences,
                                          HomeController homeController);

  /**
   * Returns a new view that displays a wizard. 
   */
  public abstract DialogView createWizardView(UserPreferences preferences,
                                              WizardController wizardController);

  /**
   * Returns a new view that displays the different steps that helps the user to choose a background image. 
   */
  public abstract View createBackgroundImageWizardStepsView(
                                 BackgroundImage backgroundImage,
                                 UserPreferences preferences, 
                                 BackgroundImageWizardController backgroundImageWizardController);

  /**
   * Returns a new view that displays the different steps that helps the user to import furniture. 
   */
  public abstract ImportedFurnitureWizardStepsView createImportedFurnitureWizardStepsView(
                                 CatalogPieceOfFurniture piece,
                                 String modelName, boolean importHomePiece,
                                 UserPreferences preferences, 
                                 ImportedFurnitureWizardController importedFurnitureWizardController);

  /**
   * Returns a new view that displays the different steps that helps the user to import a texture. 
   */
  public abstract View createImportedTextureWizardStepsView(
                                 CatalogTexture texture, String textureName,
                                 UserPreferences preferences,
                                 ImportedTextureWizardController importedTextureWizardController);

  /**
   * Returns a new view that displays message for a threaded task.
   */
  public abstract ThreadedTaskView createThreadedTaskView(String taskMessage,
                                                          UserPreferences userPreferences, 
                                                          ThreadedTaskController threadedTaskController);

  /**
   * Returns a new view that edits user preferences.
   */
  public abstract DialogView createUserPreferencesView(
                                          UserPreferences preferences,
                                          UserPreferencesController userPreferencesController);
  /**
   * Returns a new view that edits furniture values.
   */
  public abstract DialogView createHomeFurnitureView(UserPreferences preferences,
                                         HomeFurnitureController homeFurnitureController);

  /**
   * Returns a new view that edits wall values.
   */
  public abstract DialogView createWallView(UserPreferences preferences,
                                          WallController wallController);

  /**
   * Returns a new view that edits room values.
   */
  public abstract DialogView createRoomView(UserPreferences preferences,
                                            RoomController roomController);
  
  /**
   * Returns a new view that edits label values.
   */
  public abstract DialogView createLabelView(boolean modification,
                                             UserPreferences preferences,
                                             LabelController labelController);

  /**
   * Returns a new view that edits compass values.
   */
  public abstract DialogView createCompassView(UserPreferences preferences, 
                                               CompassController compassController);
  
  /**
   * Returns a new view that edits 3D attributes.
   */
  public abstract DialogView createHome3DAttributesView(UserPreferences preferences,
                                           Home3DAttributesController home3DAttributesController);

  /**
   * Returns a new view that edits the texture of its controller.  
   */
  public abstract TextureChoiceView createTextureChoiceView(UserPreferences preferences,
                                                     TextureChoiceController textureChoiceController);

  /**
   * Creates a new view that edits page setup.
   */
  public abstract DialogView createPageSetupView(UserPreferences preferences,
                                                    PageSetupController pageSetupController);

  /**
   * Returns a new view that displays home print preview. 
   */
  public abstract DialogView createPrintPreviewView(Home home,
                                                    UserPreferences preferences,
                                                    HomeController homeController,
                                                    PrintPreviewController printPreviewController);

  /**
   * Returns a new view able to compute a photo realistic image of a home. 
   */
  public abstract DialogView createPhotoView(Home home, UserPreferences preferences, 
                                             PhotoController photoController);

  /**
   * Returns a new view able to compute a 3D video of a home. 
   */
  public abstract DialogView createVideoView(Home home, UserPreferences preferences, 
                                             VideoController videoController);

  /**
   * Returns a new view that displays Sweet Home 3D help.
   */
  public abstract HelpView createHelpView(UserPreferences preferences,
                                          HelpController helpController);
}
