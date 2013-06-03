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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ubik3d.model.UserPreferences;
import ubik3d.tools.OperatingSystem;
import ubik3d.viewcontroller.DialogView;
import ubik3d.viewcontroller.Home3DAttributesController;
import ubik3d.viewcontroller.View;


/**
 * Home 3D attributes editing panel.
 * @author Emmanuel Puybaret
 */
public class Home3DAttributesPanel extends JPanel implements DialogView {
  private final Home3DAttributesController controller;
  private JLabel        observerFieldOfViewLabel;
  private JSpinner      observerFieldOfViewSpinner;
  private JLabel        observerHeightLabel;
  private JSpinner      observerHeightSpinner;
  private JRadioButton  groundColorRadioButton;
  private ColorButton   groundColorButton;
  private JRadioButton  groundTextureRadioButton;
  private JComponent    groundTextureComponent;
  private JRadioButton  skyColorRadioButton;
  private ColorButton   skyColorButton;
  private JRadioButton  skyTextureRadioButton;
  private JComponent    skyTextureComponent;
  private JLabel        brightnessLabel;
  private JSlider       brightnessSlider;
  private JLabel        wallsTransparencyLabel;
  private JSlider       wallsTransparencySlider;
  private String        dialogTitle;

  /**
   * Creates a panel that displays home 3D attributes data according to the units 
   * set in <code>preferences</code>.
   * @param preferences user preferences
   * @param controller the controller of this panel
   */
  public Home3DAttributesPanel(UserPreferences preferences,
                               Home3DAttributesController controller) {
    super(new GridBagLayout());
    this.controller = controller;
    createComponents(preferences, controller);
    setMnemonics(preferences);
    layoutComponents();
  }

  /**
   * Creates and initializes components and spinners model.
   */
  private void createComponents(UserPreferences preferences,
                                final Home3DAttributesController controller) {
    // Get unit name matching current unit 
    String unitName = preferences.getLengthUnit().getName();
    
    // Create observer field of view label and spinner bound to OBSERVER_FIELD_OF_VIEW_IN_DEGREES controller property
    this.observerFieldOfViewLabel = new JLabel(SwingTools.getLocalizedLabelText(preferences, 
        Home3DAttributesPanel.class, "observerFieldOfViewLabel.text"));
    final SpinnerNumberModel observerFieldOfViewSpinnerModel = new SpinnerNumberModel(10, 10, 120, 1);
    this.observerFieldOfViewSpinner = new AutoCommitSpinner(observerFieldOfViewSpinnerModel);
    observerFieldOfViewSpinnerModel.setValue(controller.getObserverFieldOfViewInDegrees());
    observerFieldOfViewSpinnerModel.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent ev) {
          controller.setObserverFieldOfViewInDegrees(
              ((Number)observerFieldOfViewSpinnerModel.getValue()).intValue());
        }
      });
    controller.addPropertyChangeListener(Home3DAttributesController.Property.OBSERVER_FIELD_OF_VIEW_IN_DEGREES, 
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent ev) {
            observerFieldOfViewSpinnerModel.setValue(controller.getObserverFieldOfViewInDegrees());
          }
        });
    
    // Create observer height label and spinner bound to OBSERVER_HEIGHT controller property
    this.observerHeightLabel = new JLabel(String.format(SwingTools.getLocalizedLabelText(preferences, 
        Home3DAttributesPanel.class, "observerHeightLabel.text"), unitName));
    final NullableSpinner.NullableSpinnerLengthModel observerHeightSpinnerModel = 
        new NullableSpinner.NullableSpinnerLengthModel(preferences, 10f, 2500f * 15 / 14);
    this.observerHeightSpinner = new AutoCommitSpinner(observerHeightSpinnerModel);
    observerHeightSpinnerModel.setLength((float)Math.round(controller.getObserverHeight() * 100) / 100);
    observerHeightSpinnerModel.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent ev) {
          controller.setObserverHeight(observerHeightSpinnerModel.getLength());
        }
      });
    controller.addPropertyChangeListener(Home3DAttributesController.Property.OBSERVER_HEIGHT, 
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent ev) {
            observerHeightSpinnerModel.setLength((float)Math.round(controller.getObserverHeight() * 100) / 100);
          }
        });
    
    // Ground color and texture buttons bound to ground controller properties
    this.groundColorRadioButton = new JRadioButton(SwingTools.getLocalizedLabelText(preferences, 
        Home3DAttributesPanel.class, "groundColorRadioButton.text"));
    this.groundColorRadioButton.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent ev) {
          if (groundColorRadioButton.isSelected()) {
            controller.setGroundPaint(Home3DAttributesController.EnvironmentPaint.COLORED);
          }
        }
      });
    controller.addPropertyChangeListener(Home3DAttributesController.Property.GROUND_PAINT, 
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent ev) {
            updateGroundRadioButtons(controller);
          }
        });
  
    this.groundColorButton = new ColorButton();
    this.groundColorButton.setColorDialogTitle(preferences.getLocalizedString(
        Home3DAttributesPanel.class, "groundColorDialog.title"));
    this.groundColorButton.setColor(controller.getGroundColor());
    this.groundColorButton.addPropertyChangeListener(ColorButton.COLOR_PROPERTY, 
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent ev) {
            controller.setGroundColor(groundColorButton.getColor());
          }
        });
    controller.addPropertyChangeListener(Home3DAttributesController.Property.GROUND_COLOR, 
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent ev) {
            groundColorButton.setColor(controller.getGroundColor());
          }
        });
    
    this.groundTextureRadioButton = new JRadioButton(SwingTools.getLocalizedLabelText(preferences, 
        Home3DAttributesPanel.class, "groundTextureRadioButton.text"));
    this.groundTextureRadioButton.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent ev) {
        if (groundTextureRadioButton.isSelected()) {
          controller.setGroundPaint(Home3DAttributesController.EnvironmentPaint.TEXTURED);
        }
      }
    });
    
    this.groundTextureComponent = (JComponent)controller.getGroundTextureController().getView();

    ButtonGroup groundGroup = new ButtonGroup();
    groundGroup.add(this.groundColorRadioButton);
    groundGroup.add(this.groundTextureRadioButton);
    updateGroundRadioButtons(controller);
    
    // Sky color and texture buttons bound to sky controller properties
    this.skyColorRadioButton = new JRadioButton(SwingTools.getLocalizedLabelText(preferences, 
        Home3DAttributesPanel.class, "skyColorRadioButton.text"));
    this.skyColorRadioButton.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent ev) {
          if (skyColorRadioButton.isSelected()) {
            controller.setSkyPaint(Home3DAttributesController.EnvironmentPaint.COLORED);
          }
        }
      });
    controller.addPropertyChangeListener(Home3DAttributesController.Property.SKY_PAINT, 
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent ev) {
            updateSkyRadioButtons(controller);
          }
        });
  
    this.skyColorButton = new ColorButton();
    this.skyColorButton.setColorDialogTitle(preferences.getLocalizedString(
        Home3DAttributesPanel.class, "skyColorDialog.title"));
    this.skyColorButton.setColor(controller.getSkyColor());
    this.skyColorButton.addPropertyChangeListener(ColorButton.COLOR_PROPERTY, 
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent ev) {
            controller.setSkyColor(skyColorButton.getColor());
          }
        });
    controller.addPropertyChangeListener(Home3DAttributesController.Property.SKY_COLOR, 
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent ev) {
            skyColorButton.setColor(controller.getSkyColor());
          }
        });
    
    this.skyTextureRadioButton = new JRadioButton(SwingTools.getLocalizedLabelText(preferences, 
        Home3DAttributesPanel.class, "skyTextureRadioButton.text"));
    this.skyTextureRadioButton.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent ev) {
        if (skyTextureRadioButton.isSelected()) {
          controller.setSkyPaint(Home3DAttributesController.EnvironmentPaint.TEXTURED);
        }
      }
    });
    
    this.skyTextureComponent = (JComponent)controller.getSkyTextureController().getView();

    ButtonGroup skyGroup = new ButtonGroup();
    skyGroup.add(this.skyColorRadioButton);
    skyGroup.add(this.skyTextureRadioButton);
    updateSkyRadioButtons(controller);
    
    // Brightness label and slider bound to LIGHT_COLOR controller property
    this.brightnessLabel = new JLabel(SwingTools.getLocalizedLabelText(preferences, 
        Home3DAttributesPanel.class, "brightnessLabel.text"));
    this.brightnessSlider = new JSlider(0, 255);
    JLabel darkLabel = new JLabel(preferences.getLocalizedString(
        Home3DAttributesPanel.class, "darkLabel.text"));
    JLabel brightLabel = new JLabel(preferences.getLocalizedString(
        Home3DAttributesPanel.class, "brightLabel.text"));
    Dictionary<Integer,JComponent> brightnessSliderLabelTable = new Hashtable<Integer,JComponent>();
    brightnessSliderLabelTable.put(0, darkLabel);
    brightnessSliderLabelTable.put(255, brightLabel);
    this.brightnessSlider.setLabelTable(brightnessSliderLabelTable);
    this.brightnessSlider.setPaintLabels(true);
    this.brightnessSlider.setPaintTicks(true);
    this.brightnessSlider.setMajorTickSpacing(16);
    this.brightnessSlider.setValue(controller.getLightColor() & 0xFF);
    this.brightnessSlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent ev) {
          int brightness = brightnessSlider.getValue();
          controller.setLightColor((brightness << 16) + (brightness << 8) + brightness);
        }
      });
    controller.addPropertyChangeListener(Home3DAttributesController.Property.LIGHT_COLOR, 
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent ev) {
            brightnessSlider.setValue(controller.getLightColor() & 0xFF);
          }
        });
    
    // Walls transparency label and slider bound to WALLS_ALPHA controller property
    this.wallsTransparencyLabel = new JLabel(SwingTools.getLocalizedLabelText(preferences, 
        Home3DAttributesPanel.class, "wallsTransparencyLabel.text"));
    this.wallsTransparencySlider = new JSlider(0, 255);
    JLabel opaqueLabel = new JLabel(preferences.getLocalizedString(
        Home3DAttributesPanel.class, "opaqueLabel.text"));
    JLabel invisibleLabel = new JLabel(preferences.getLocalizedString(
        Home3DAttributesPanel.class, "invisibleLabel.text"));
    Dictionary<Integer,JComponent> wallsTransparencySliderLabelTable = new Hashtable<Integer,JComponent>();
    wallsTransparencySliderLabelTable.put(0, opaqueLabel);
    wallsTransparencySliderLabelTable.put(255, invisibleLabel);
    this.wallsTransparencySlider.setLabelTable(wallsTransparencySliderLabelTable);
    this.wallsTransparencySlider.setPaintLabels(true);
    this.wallsTransparencySlider.setPaintTicks(true);
    this.wallsTransparencySlider.setMajorTickSpacing(16);
    this.wallsTransparencySlider.setValue((int)(controller.getWallsAlpha() * 255));
    this.wallsTransparencySlider.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent ev) {
          controller.setWallsAlpha(wallsTransparencySlider.getValue() / 255f);
        }
      });
    controller.addPropertyChangeListener(Home3DAttributesController.Property.WALLS_ALPHA, 
        new PropertyChangeListener() {
          public void propertyChange(PropertyChangeEvent ev) {
            wallsTransparencySlider.setValue((int)(controller.getWallsAlpha() * 255));
          }
        });

    this.dialogTitle = preferences.getLocalizedString(
        Home3DAttributesPanel.class, "home3DAttributes.title");
  }

  /**
   * Updates ground radio buttons. 
   */
  private void updateGroundRadioButtons(Home3DAttributesController controller) {
    if (controller.getGroundPaint() == Home3DAttributesController.EnvironmentPaint.COLORED) {
      this.groundColorRadioButton.setSelected(true);
    } else {
      this.groundTextureRadioButton.setSelected(true);
    } 
  }

  /**
   * Updates sky radio buttons. 
   */
  private void updateSkyRadioButtons(Home3DAttributesController controller) {
    if (controller.getSkyPaint() == Home3DAttributesController.EnvironmentPaint.COLORED) {
      this.skyColorRadioButton.setSelected(true);
    } else {
      this.skyTextureRadioButton.setSelected(true);
    } 
  }

  /**
   * Sets components mnemonics and label / component associations.
   */
  private void setMnemonics(UserPreferences preferences) {
    if (!OperatingSystem.isMacOSX()) {
      this.observerFieldOfViewLabel.setDisplayedMnemonic(
          KeyStroke.getKeyStroke(preferences.getLocalizedString(
              Home3DAttributesPanel.class, "observerFieldOfViewLabel.mnemonic")).getKeyCode());
      this.observerFieldOfViewLabel.setLabelFor(this.observerFieldOfViewLabel);
      this.observerHeightLabel.setDisplayedMnemonic(
          KeyStroke.getKeyStroke(preferences.getLocalizedString(
              Home3DAttributesPanel.class, "observerHeightLabel.mnemonic")).getKeyCode());
      this.observerHeightLabel.setLabelFor(this.observerHeightSpinner);
      this.groundColorRadioButton.setMnemonic(
          KeyStroke.getKeyStroke(preferences.getLocalizedString(
              Home3DAttributesPanel.class,"groundColorRadioButton.mnemonic")).getKeyCode());
      this.groundTextureRadioButton.setMnemonic(
          KeyStroke.getKeyStroke(preferences.getLocalizedString(
              Home3DAttributesPanel.class,"groundTextureRadioButton.mnemonic")).getKeyCode());
      this.skyColorRadioButton.setMnemonic(
          KeyStroke.getKeyStroke(preferences.getLocalizedString(
              Home3DAttributesPanel.class,"skyColorRadioButton.mnemonic")).getKeyCode());
      this.skyTextureRadioButton.setMnemonic(
          KeyStroke.getKeyStroke(preferences.getLocalizedString(
              Home3DAttributesPanel.class,"skyTextureRadioButton.mnemonic")).getKeyCode());
      this.brightnessLabel.setDisplayedMnemonic(
          KeyStroke.getKeyStroke(preferences.getLocalizedString(
              Home3DAttributesPanel.class,"brightnessLabel.mnemonic")).getKeyCode());
      this.brightnessLabel.setLabelFor(this.brightnessSlider);
      this.wallsTransparencyLabel.setDisplayedMnemonic(
          KeyStroke.getKeyStroke(preferences.getLocalizedString(
              Home3DAttributesPanel.class,"wallsTransparencyLabel.mnemonic")).getKeyCode());
      this.wallsTransparencyLabel.setLabelFor(this.wallsTransparencySlider);
    }
  }
  
  /**
   * Layouts panel components in panel with their labels. 
   */
  private void layoutComponents() {
    int labelAlignment = OperatingSystem.isMacOSX() 
        ? GridBagConstraints.LINE_END
        : GridBagConstraints.LINE_START;
    // First row
    Insets labelInsets = new Insets(0, 0, 10, 5);
    add(this.observerFieldOfViewLabel, new GridBagConstraints(
        0, 0, 1, 1, 0, 0, labelAlignment, 
        GridBagConstraints.NONE, labelInsets, 0, 0));
    Insets componentInsets = new Insets(0, 0, 10, 15);
    add(this.observerFieldOfViewSpinner, new GridBagConstraints(
        1, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, 
        GridBagConstraints.HORIZONTAL, componentInsets, 20, 0));
    add(this.observerHeightLabel, new GridBagConstraints(
        2, 0, 1, 1, 0, 0, labelAlignment, 
        GridBagConstraints.NONE, labelInsets, 0, 0));
    Insets rightComponentInsets = new Insets(0, 0, 10, 0);
    add(this.observerHeightSpinner, new GridBagConstraints(
        3, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START, 
        GridBagConstraints.HORIZONTAL, rightComponentInsets, -25, 0));
    // Second row
    Insets closeLabelInsets = new Insets(0, 0, 2, 5);
    add(this.groundColorRadioButton, new GridBagConstraints(
        0, 1, 1, 1, 0, 0, labelAlignment, 
        GridBagConstraints.NONE, closeLabelInsets, 0, 0));
    add(this.groundColorButton, new GridBagConstraints(
        1, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, 
        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 2, 15), 0, 0));
    add(this.skyColorRadioButton, new GridBagConstraints(
        2, 1, 1, 1, 0, 0, labelAlignment, 
        GridBagConstraints.NONE, closeLabelInsets, 0, 0));
    add(this.skyColorButton, new GridBagConstraints(
        3, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START, 
        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 2, 0), 0, 0));
    // Third row
    add(this.groundTextureRadioButton, new GridBagConstraints(
        0, 2, 1, 1, 0, 0, labelAlignment, 
        GridBagConstraints.NONE, labelInsets, 0, 0));
    add(this.groundTextureComponent, new GridBagConstraints(
        1, 2, 1, 1, 0, 0, GridBagConstraints.LINE_START, 
        GridBagConstraints.HORIZONTAL, componentInsets, 0, 0));
    add(this.skyTextureRadioButton, new GridBagConstraints(
        2, 2, 1, 1, 0, 0, labelAlignment, 
        GridBagConstraints.NONE, labelInsets, 0, 0));
    add(this.skyTextureComponent, new GridBagConstraints(
        3, 2, 1, 1, 0, 0, GridBagConstraints.LINE_START, 
        GridBagConstraints.HORIZONTAL, rightComponentInsets, 0, 0));
    // Fourth row
    add(this.brightnessLabel, new GridBagConstraints(
        0, 3, 1, 1, 0, 0, labelAlignment, 
        GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
    add(this.brightnessSlider, new GridBagConstraints(
        1, 3, 3, 1, 0, 0, GridBagConstraints.LINE_START, 
        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    // Last row
    add(this.wallsTransparencyLabel, new GridBagConstraints(
        0, 4, 1, 1, 0, 0, labelAlignment, 
        GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
    add(this.wallsTransparencySlider, new GridBagConstraints(
        1, 4, 3, 1, 0, 0, GridBagConstraints.LINE_START, 
        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
  }

  /**
   * Displays this panel in a modal dialog box. 
   */
  public void displayView(View parentView) {
    JFormattedTextField observerFieldOfViewSpinnerTextField = 
        ((JSpinner.DefaultEditor)this.observerFieldOfViewSpinner.getEditor()).getTextField();
    if (SwingTools.showConfirmDialog((JComponent)parentView, 
            this, this.dialogTitle, observerFieldOfViewSpinnerTextField) == JOptionPane.OK_OPTION
        && this.controller != null) {
      this.controller.modify3DAttributes();
    }
  }
}
