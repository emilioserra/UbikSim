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
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import ubik3d.io.RolsLoader;

import ubik3d.model.UserPreferences;
import ubik3d.tools.OperatingSystem;
import ubik3d.viewcontroller.*;

/**
 * Home furniture editing panel.
 *
 * @author Emmanuel Puybaret
 */
public class HomeFurniturePanel extends JPanel implements DialogView {

    private final HomeFurnitureController controller;
    private JLabel metadataLabel;
    private JTextArea metadataTextArea;
    private JLabel rolCategoryLabel;
    private JComboBox rolCategoryComboBox;
    private JLabel rolLabel;
    private JComboBox rolComboBox;
    private JLabel amountLabel;
    private JSpinner amountSpinner;
    private JLabel nameLabel;
    private JTextField nameTextField;
    private NullableCheckBox nameVisibleCheckBox;
    private JLabel xLabel;
    private JSpinner xSpinner;
    private JLabel yLabel;
    private JSpinner ySpinner;
    private JLabel elevationLabel;
    private JSpinner elevationSpinner;
    private JLabel angleLabel;
    private JSpinner angleSpinner;
    private NullableCheckBox basePlanItemCheckBox;
    private JLabel widthLabel;
    private JSpinner widthSpinner;
    private JLabel depthLabel;
    private JSpinner depthSpinner;
    private JLabel heightLabel;
    private JSpinner heightSpinner;
    private JCheckBox keepProportionsCheckBox;
    private NullableCheckBox mirroredModelCheckBox;
    private JRadioButton defaultColorAndTextureRadioButton;
    private JRadioButton colorRadioButton;
    private ColorButton colorButton;
    private JRadioButton textureRadioButton;
    private JComponent textureComponent;
    private JRadioButton defaultShininessRadioButton;
    private JRadioButton mattRadioButton;
    private JRadioButton shinyRadioButton;
    private NullableCheckBox visibleCheckBox;
    private JLabel lightPowerLabel;
    private JSpinner lightPowerSpinner;
    private String dialogTitle;

    /**
     * Creates a panel that displays home furniture data according to the units
     * set in
     * <code>preferences</code>.
     *
     * @param preferences user preferences
     * @param controller the controller of this panel
     */
    public HomeFurniturePanel(UserPreferences preferences, HomeFurnitureController controller) {
        super(new GridBagLayout());
        this.controller = controller;
        createComponents(preferences, controller);
        setMnemonics(preferences);
        layoutComponents(preferences, controller);
    }

    /**
     * Creates and initializes components and spinners model.
     */
    private void createComponents(UserPreferences preferences, final HomeFurnitureController controller) {
        // Get unit name matching current unit
        String unitName = preferences.getLengthUnit().getName();

        this.metadataLabel = new JLabel(SwingTools.getLocalizedLabelText(preferences, HomeFurniturePanel.class,
                "metadataLabel.text"));
        this.metadataTextArea = new JTextArea(controller.getMetadata(), 3, 20);
        this.metadataTextArea.setAutoscrolls(true);
        if (!OperatingSystem.isMacOSXLeopardOrSuperior()) {
            SwingTools.addAutoSelectionOnFocusGain(this.metadataTextArea);
        }
        final PropertyChangeListener metadataChangeListener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent ev) {
                metadataTextArea.setText(controller.getMetadata());
            }
        };
        controller.addPropertyChangeListener(HomeFurnitureController.Property.METADATA, metadataChangeListener);
        this.metadataTextArea.getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(DocumentEvent ev) {
                controller.removePropertyChangeListener(HomeFurnitureController.Property.METADATA, metadataChangeListener);
                String metadata = metadataTextArea.getText();
                if (metadata == null || metadata.trim().length() == 0) {
                    controller.setMetadata("");
                } else {
                    controller.setMetadata(metadata);
                }
                controller.addPropertyChangeListener(HomeFurnitureController.Property.METADATA, metadataChangeListener);
            }

            public void insertUpdate(DocumentEvent ev) {
                changedUpdate(ev);
            }

            public void removeUpdate(DocumentEvent ev) {
                changedUpdate(ev);
            }
        });

        this.amountLabel = new JLabel(SwingTools.getLocalizedLabelText(preferences, HomeFurniturePanel.class,
                "amountLabel.text", unitName));
        final NullableSpinner.NullableSpinnerLengthModel amountSpinnerModel = new NullableSpinner.NullableSpinnerLengthModel(
                preferences, -100000f, 100000f);
        this.amountSpinner = new NullableSpinner(amountSpinnerModel);
        amountSpinnerModel.setNullable(controller.getAmount() == null);
        amountSpinnerModel.setLength(controller.getAmount());
        final PropertyChangeListener amountChangeListener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent ev) {
                amountSpinnerModel.setNullable(ev.getNewValue() == null);
                amountSpinnerModel.setLength((Float) ev.getNewValue());
            }
        };
        controller.addPropertyChangeListener(HomeFurnitureController.Property.AMOUNT, amountChangeListener);
        amountSpinnerModel.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ev) {
                controller.removePropertyChangeListener(HomeFurnitureController.Property.AMOUNT, amountChangeListener);
                controller.setAmount(amountSpinnerModel.getLength());
                controller.addPropertyChangeListener(HomeFurnitureController.Property.AMOUNT, amountChangeListener);
            }
        });

        this.rolCategoryLabel = new JLabel(SwingTools.getLocalizedLabelText(preferences, HomeFurniturePanel.class, "rolCategoryLabel.text"));
        this.rolCategoryComboBox = new JComboBox(RolsLoader.getInstance().getCagegories().toArray());
        this.rolCategoryComboBox.setEditable(false);

        String categoryName = RolsLoader.getInstance().getCagegoryOf(controller.getRol());
        if(categoryName == null) {
          categoryName = (String) JOptionPane.showInputDialog(this, 
              "Please, specify a category for the rol \""+controller.getRol()+"\"", "Rol not recognised!",
              JOptionPane.ERROR_MESSAGE, null, RolsLoader.getInstance().getCagegories().toArray(), null);
          RolsLoader.getInstance().setActual(categoryName, controller.getRol());
        }
          
        this.rolCategoryComboBox.setSelectedItem(categoryName);

        // Create rol label and combobox
        this.rolLabel = new JLabel(SwingTools.getLocalizedLabelText(preferences, HomeFurniturePanel.class, "rolLabel.text"));

        this.rolComboBox = new JComboBox();
        this.rolComboBox.setModel(new DefaultComboBoxModel() {
            private String selected;
            
            @Override
            public void setSelectedItem(Object anItem) {
                selected = (String)anItem;
            }

            @Override
            public Object getSelectedItem() {
                return selected;
            }

            @Override
            public int getSize() {
                String categoryName = (String) rolCategoryComboBox.getSelectedItem();
                return RolsLoader.getInstance().getRols(categoryName).size();
            }

            @Override
            public Object getElementAt(int index) {
                String categoryName = (String) rolCategoryComboBox.getSelectedItem();
                return RolsLoader.getInstance().getRols(categoryName).get(index);
            }
        });
        this.rolComboBox.setSelectedItem(controller.getRol());
        this.rolComboBox.setEditable(true);
        final PropertyChangeListener rolChangeListener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent ev) {
                String categoryName = (String) rolCategoryComboBox.getSelectedItem();                
                rolComboBox.setSelectedItem(controller.getRol());
                final JTextComponent jtc = (JTextComponent) rolComboBox.getEditor().getEditorComponent();
                jtc.setText(controller.getRol());
                RolsLoader.getInstance().setActual(categoryName, controller.getRol());
            }
        };
        controller.addPropertyChangeListener(HomeFurnitureController.Property.ROL, rolChangeListener);
        final JTextComponent jtc = (JTextComponent) this.rolComboBox.getEditor().getEditorComponent();
        jtc.setText(controller.getRol());
        jtc.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }
            public void changedUpdate(DocumentEvent e) {
                controller.removePropertyChangeListener(HomeFurnitureController.Property.ROL, rolChangeListener);
                String rol = null;
                try {
                    rol = e.getDocument().getText(0, e.getDocument().getLength());
                } catch (BadLocationException ex) {
                    Logger.getLogger(HomeFurniturePanel.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (rol == null || rol.trim().length() == 0) {
                    controller.setRol(null);
                } else {
                    controller.setRol(rol);
                }
                String categoryName = (String) rolCategoryComboBox.getSelectedItem();
                RolsLoader.getInstance().setActual(categoryName, controller.getRol());
                controller.addPropertyChangeListener(HomeFurnitureController.Property.ROL, rolChangeListener);
            }
        });

        this.rolCategoryComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    String rolCategory = (String) e.getItem();
                    controller.setRol(RolsLoader.getInstance().getRols(rolCategory).get(0));
                }
            }
        });

        // Create name label and its text field bound to NAME controller property
        this.nameLabel = new JLabel(SwingTools.getLocalizedLabelText(preferences, HomeFurniturePanel.class,
                "nameLabel.text"));
        this.nameTextField = new JTextField(controller.getName(), 15);
        if (!OperatingSystem.isMacOSXLeopardOrSuperior()) {
            SwingTools.addAutoSelectionOnFocusGain(this.nameTextField);
        }
        final PropertyChangeListener nameChangeListener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent ev) {
                nameTextField.setText(controller.getName());
            }
        };
        controller.addPropertyChangeListener(HomeFurnitureController.Property.NAME, nameChangeListener);
        this.nameTextField.getDocument().addDocumentListener(new DocumentListener() {

            public void changedUpdate(DocumentEvent ev) {
                controller.removePropertyChangeListener(HomeFurnitureController.Property.NAME, nameChangeListener);
                String name = nameTextField.getText();
                if (name == null || name.trim().length() == 0) {
                    controller.setName(null);
                } else {
                    controller.setName(name);
                }
                controller.addPropertyChangeListener(HomeFurnitureController.Property.NAME, nameChangeListener);
            }

            public void insertUpdate(DocumentEvent ev) {
                changedUpdate(ev);
            }

            public void removeUpdate(DocumentEvent ev) {
                changedUpdate(ev);
            }
        });

        // Create name visible check box bound to NAME_VISIBLE controller property
        this.nameVisibleCheckBox = new NullableCheckBox(SwingTools.getLocalizedLabelText(preferences,
                HomeFurniturePanel.class, "nameVisibleCheckBox.text"));
        this.nameVisibleCheckBox.setNullable(controller.getNameVisible() == null);
        this.nameVisibleCheckBox.setValue(controller.getNameVisible());
        final PropertyChangeListener nameVisibleChangeListener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent ev) {
                nameVisibleCheckBox.setNullable(ev.getNewValue() == null);
                nameVisibleCheckBox.setValue((Boolean) ev.getNewValue());
            }
        };
        controller.addPropertyChangeListener(HomeFurnitureController.Property.NAME_VISIBLE, nameVisibleChangeListener);
        this.nameVisibleCheckBox.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ev) {
                controller.removePropertyChangeListener(HomeFurnitureController.Property.NAME_VISIBLE,
                        nameVisibleChangeListener);
                controller.setNameVisible(nameVisibleCheckBox.getValue());
                controller.addPropertyChangeListener(HomeFurnitureController.Property.NAME_VISIBLE, nameVisibleChangeListener);
            }
        });

        // Create X label and its spinner bound to X controller property
        this.xLabel = new JLabel(SwingTools.getLocalizedLabelText(preferences, HomeFurniturePanel.class, "xLabel.text",
                unitName));
        final NullableSpinner.NullableSpinnerLengthModel xSpinnerModel = new NullableSpinner.NullableSpinnerLengthModel(
                preferences, -100000f, 100000f);
        this.xSpinner = new NullableSpinner(xSpinnerModel);
        xSpinnerModel.setNullable(controller.getX() == null);
        xSpinnerModel.setLength(controller.getX());
        final PropertyChangeListener xChangeListener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent ev) {
                xSpinnerModel.setNullable(ev.getNewValue() == null);
                xSpinnerModel.setLength((Float) ev.getNewValue());
            }
        };
        controller.addPropertyChangeListener(HomeFurnitureController.Property.X, xChangeListener);
        xSpinnerModel.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ev) {
                controller.removePropertyChangeListener(HomeFurnitureController.Property.X, xChangeListener);
                controller.setX(xSpinnerModel.getLength());
                controller.addPropertyChangeListener(HomeFurnitureController.Property.X, xChangeListener);
            }
        });

        // Create Y label and its spinner bound to Y controller property
        this.yLabel = new JLabel(SwingTools.getLocalizedLabelText(preferences, HomeFurniturePanel.class, "yLabel.text",
                unitName));
        final NullableSpinner.NullableSpinnerLengthModel ySpinnerModel = new NullableSpinner.NullableSpinnerLengthModel(
                preferences, -100000f, 100000f);
        this.ySpinner = new NullableSpinner(ySpinnerModel);
        ySpinnerModel.setNullable(controller.getY() == null);
        ySpinnerModel.setLength(controller.getY());
        final PropertyChangeListener yChangeListener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent ev) {
                ySpinnerModel.setNullable(ev.getNewValue() == null);
                ySpinnerModel.setLength((Float) ev.getNewValue());
            }
        };
        controller.addPropertyChangeListener(HomeFurnitureController.Property.Y, yChangeListener);
        ySpinnerModel.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ev) {
                controller.removePropertyChangeListener(HomeFurnitureController.Property.Y, yChangeListener);
                controller.setY(ySpinnerModel.getLength());
                controller.addPropertyChangeListener(HomeFurnitureController.Property.Y, yChangeListener);
            }
        });

        // Create elevation label and its spinner bound to ELEVATION controller
        // property
        this.elevationLabel = new JLabel(SwingTools.getLocalizedLabelText(preferences, HomeFurniturePanel.class,
                "elevationLabel.text", unitName));
        final NullableSpinner.NullableSpinnerLengthModel elevationSpinnerModel = new NullableSpinner.NullableSpinnerLengthModel(
                preferences, 0f, 2500f);
        this.elevationSpinner = new NullableSpinner(elevationSpinnerModel);
        elevationSpinnerModel.setNullable(controller.getElevation() == null);
        elevationSpinnerModel.setLength(controller.getElevation());
        final PropertyChangeListener elevationChangeListener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent ev) {
                elevationSpinnerModel.setNullable(ev.getNewValue() == null);
                elevationSpinnerModel.setLength((Float) ev.getNewValue());
            }
        };
        controller.addPropertyChangeListener(HomeFurnitureController.Property.ELEVATION, elevationChangeListener);
        elevationSpinnerModel.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ev) {
                controller.removePropertyChangeListener(HomeFurnitureController.Property.ELEVATION, elevationChangeListener);
                controller.setElevation(elevationSpinnerModel.getLength());
                controller.addPropertyChangeListener(HomeFurnitureController.Property.ELEVATION, elevationChangeListener);
            }
        });

        // Create angle label and its spinner bound to ANGLE_IN_DEGREES controller
        // property
        this.angleLabel = new JLabel(SwingTools.getLocalizedLabelText(preferences, HomeFurniturePanel.class,
                "angleLabel.text"));
        final NullableSpinner.NullableSpinnerNumberModel angleSpinnerModel = new NullableSpinner.NullableSpinnerNumberModel(
                0, 0, 360, 1);
        this.angleSpinner = new NullableSpinner(angleSpinnerModel);
        Integer angle = controller.getAngleInDegrees();
        angleSpinnerModel.setNullable(angle == null);
        angleSpinnerModel.setValue(angle);
        final PropertyChangeListener angleChangeListener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent ev) {
                Float newAngle = (Float) ev.getNewValue();
                angleSpinnerModel.setNullable(newAngle == null);
                angleSpinnerModel.setValue(newAngle);
            }
        };
        controller.addPropertyChangeListener(HomeFurnitureController.Property.ANGLE_IN_DEGREES, angleChangeListener);
        angleSpinnerModel.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ev) {
                controller.removePropertyChangeListener(HomeFurnitureController.Property.ANGLE_IN_DEGREES, angleChangeListener);
                Number value = (Number) angleSpinnerModel.getValue();
                if (value == null) {
                    controller.setAngleInDegrees(null);
                } else {
                    controller.setAngleInDegrees(value.intValue());
                }
                controller.addPropertyChangeListener(HomeFurnitureController.Property.ANGLE_IN_DEGREES, angleChangeListener);
            }
        });

        // Create base plan item check box bound to BASE_PLAN_ITEM controller
        // property
        this.basePlanItemCheckBox = new NullableCheckBox(SwingTools.getLocalizedLabelText(preferences,
                HomeFurniturePanel.class, "basePlanItemCheckBox.text"));
        String basePlanItemToolTip = preferences.getLocalizedString(HomeFurniturePanel.class,
                "basePlanItemCheckBox.tooltip");
        if (basePlanItemToolTip.length() > 0) {
            this.basePlanItemCheckBox.setToolTipText(basePlanItemToolTip);
        }
        this.basePlanItemCheckBox.setNullable(controller.getBasePlanItem() == null);
        this.basePlanItemCheckBox.setValue(controller.getBasePlanItem());
        final PropertyChangeListener basePlanItemModelChangeListener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent ev) {
                basePlanItemCheckBox.setNullable(ev.getNewValue() == null);
                basePlanItemCheckBox.setValue((Boolean) ev.getNewValue());
            }
        };
        controller.addPropertyChangeListener(HomeFurnitureController.Property.BASE_PLAN_ITEM,
                basePlanItemModelChangeListener);
        this.basePlanItemCheckBox.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ev) {
                controller.removePropertyChangeListener(HomeFurnitureController.Property.BASE_PLAN_ITEM,
                        basePlanItemModelChangeListener);
                controller.setBasePlanItem(basePlanItemCheckBox.getValue());
                controller.addPropertyChangeListener(HomeFurnitureController.Property.BASE_PLAN_ITEM,
                        basePlanItemModelChangeListener);
            }
        });
        this.basePlanItemCheckBox.setEnabled(controller.isBasePlanItemEditable());

        // Create width label and its spinner bound to WIDTH controller property
        this.widthLabel = new JLabel(SwingTools.getLocalizedLabelText(preferences, HomeFurniturePanel.class,
                "widthLabel.text", unitName));
        final float minimumLength = preferences.getLengthUnit().getMinimumLength();
        final NullableSpinner.NullableSpinnerLengthModel widthSpinnerModel = new NullableSpinner.NullableSpinnerLengthModel(
                preferences, minimumLength, 100000f);
        this.widthSpinner = new NullableSpinner(widthSpinnerModel);
        final PropertyChangeListener widthChangeListener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent ev) {
                Float width = controller.getWidth();
                widthSpinnerModel.setNullable(width == null);
                widthSpinnerModel.setLength(width);
                if (width != null) {
                    widthSpinnerModel.setMinimum(Math.min(width, minimumLength));
                }
            }
        };
        widthChangeListener.propertyChange(null);
        controller.addPropertyChangeListener(HomeFurnitureController.Property.WIDTH, widthChangeListener);
        widthSpinnerModel.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ev) {
                controller.removePropertyChangeListener(HomeFurnitureController.Property.WIDTH, widthChangeListener);
                controller.setWidth(widthSpinnerModel.getLength());
                controller.addPropertyChangeListener(HomeFurnitureController.Property.WIDTH, widthChangeListener);
            }
        });

        // Create depth label and its spinner bound to DEPTH controller property
        this.depthLabel = new JLabel(SwingTools.getLocalizedLabelText(preferences, HomeFurniturePanel.class,
                "depthLabel.text", unitName));
        final NullableSpinner.NullableSpinnerLengthModel depthSpinnerModel = new NullableSpinner.NullableSpinnerLengthModel(
                preferences, minimumLength, 100000f);
        this.depthSpinner = new NullableSpinner(depthSpinnerModel);
        final PropertyChangeListener depthChangeListener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent ev) {
                Float depth = controller.getDepth();
                depthSpinnerModel.setNullable(depth == null);
                depthSpinnerModel.setLength(depth);
                if (depth != null) {
                    depthSpinnerModel.setMinimum(Math.min(depth, minimumLength));
                }
            }
        };
        depthChangeListener.propertyChange(null);
        controller.addPropertyChangeListener(HomeFurnitureController.Property.DEPTH, depthChangeListener);
        depthSpinnerModel.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ev) {
                controller.removePropertyChangeListener(HomeFurnitureController.Property.DEPTH, depthChangeListener);
                controller.setDepth(depthSpinnerModel.getLength());
                controller.addPropertyChangeListener(HomeFurnitureController.Property.DEPTH, depthChangeListener);
            }
        });

        // Create height label and its spinner bound to HEIGHT controller property
        this.heightLabel = new JLabel(SwingTools.getLocalizedLabelText(preferences, HomeFurniturePanel.class,
                "heightLabel.text", unitName));
        final NullableSpinner.NullableSpinnerLengthModel heightSpinnerModel = new NullableSpinner.NullableSpinnerLengthModel(
                preferences, minimumLength, 100000f);
        this.heightSpinner = new NullableSpinner(heightSpinnerModel);
        final PropertyChangeListener heightChangeListener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent ev) {
                Float height = controller.getHeight();
                heightSpinnerModel.setNullable(height == null);
                heightSpinnerModel.setLength(height);
                if (height != null) {
                    heightSpinnerModel.setMinimum(Math.min(height, minimumLength));
                }
            }
        };
        heightChangeListener.propertyChange(null);
        controller.addPropertyChangeListener(HomeFurnitureController.Property.HEIGHT, heightChangeListener);
        heightSpinnerModel.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ev) {
                controller.removePropertyChangeListener(HomeFurnitureController.Property.HEIGHT, heightChangeListener);
                controller.setHeight(heightSpinnerModel.getLength());
                controller.addPropertyChangeListener(HomeFurnitureController.Property.HEIGHT, heightChangeListener);
            }
        });

        // Create keep proportions check box bound to PROPORTIONAL controller
        // property
        this.keepProportionsCheckBox = new JCheckBox(SwingTools.getLocalizedLabelText(preferences,
                ImportedFurnitureWizardStepsPanel.class, "keepProportionsCheckBox.text"));
        this.keepProportionsCheckBox.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent ev) {
                controller.setProportional(keepProportionsCheckBox.isSelected());
            }
        });
        this.keepProportionsCheckBox.setSelected(controller.isProportional());
        controller.addPropertyChangeListener(HomeFurnitureController.Property.PROPORTIONAL, new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent ev) {
                // If proportional property changes update keep proportions check box
                keepProportionsCheckBox.setSelected(controller.isProportional());
            }
        });

        // Create mirror check box bound to MODEL_MIRRORED controller property
        this.mirroredModelCheckBox = new NullableCheckBox(SwingTools.getLocalizedLabelText(preferences,
                HomeFurniturePanel.class, "mirroredModelCheckBox.text"));
        String mirroredModelToolTip = preferences.getLocalizedString(HomeFurniturePanel.class,
                "mirroredModelCheckBox.tooltip");
        if (mirroredModelToolTip.length() > 0) {
            this.mirroredModelCheckBox.setToolTipText(mirroredModelToolTip);
        }
        this.mirroredModelCheckBox.setNullable(controller.getModelMirrored() == null);
        this.mirroredModelCheckBox.setValue(controller.getModelMirrored());
        final PropertyChangeListener mirroredModelChangeListener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent ev) {
                mirroredModelCheckBox.setNullable(ev.getNewValue() == null);
                mirroredModelCheckBox.setValue((Boolean) ev.getNewValue());
            }
        };
        controller.addPropertyChangeListener(HomeFurnitureController.Property.MODEL_MIRRORED, mirroredModelChangeListener);
        this.mirroredModelCheckBox.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ev) {
                controller.removePropertyChangeListener(HomeFurnitureController.Property.MODEL_MIRRORED,
                        mirroredModelChangeListener);
                controller.setModelMirrored(mirroredModelCheckBox.getValue());
                controller.addPropertyChangeListener(HomeFurnitureController.Property.MODEL_MIRRORED,
                        mirroredModelChangeListener);
            }
        });

        // Create radio buttons bound to COLOR and TEXTURE controller properties
        this.defaultColorAndTextureRadioButton = new JRadioButton(SwingTools.getLocalizedLabelText(preferences,
                HomeFurniturePanel.class, "defaultColorAndTextureRadioButton.text"));
        this.defaultColorAndTextureRadioButton.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ev) {
                if (defaultColorAndTextureRadioButton.isSelected()) {
                    controller.setPaint(HomeFurnitureController.FurniturePaint.DEFAULT);
                }
            }
        });
        controller.addPropertyChangeListener(HomeFurnitureController.Property.PAINT, new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent ev) {
                updateColorAndTextureRadioButtons(controller);
            }
        });
        this.colorRadioButton = new JRadioButton(SwingTools.getLocalizedLabelText(preferences, HomeFurniturePanel.class,
                "colorRadioButton.text"));
        this.colorRadioButton.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ev) {
                if (colorRadioButton.isSelected()) {
                    controller.setPaint(HomeFurnitureController.FurniturePaint.COLORED);
                }
            }
        });

        this.colorButton = new ColorButton();
        this.colorButton.setColorDialogTitle(preferences.getLocalizedString(HomeFurniturePanel.class, "colorDialog.title"));
        this.colorButton.setColor(controller.getColor());
        this.colorButton.addPropertyChangeListener(ColorButton.COLOR_PROPERTY, new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent ev) {
                controller.setColor(colorButton.getColor());
                controller.setPaint(HomeFurnitureController.FurniturePaint.COLORED);
            }
        });
        controller.addPropertyChangeListener(HomeFurnitureController.Property.COLOR, new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent ev) {
                colorButton.setColor(controller.getColor());
            }
        });

        this.textureRadioButton = new JRadioButton(SwingTools.getLocalizedLabelText(preferences, HomeFurniturePanel.class,
                "textureRadioButton.text"));
        this.textureRadioButton.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ev) {
                if (textureRadioButton.isSelected()) {
                    controller.setPaint(HomeFurnitureController.FurniturePaint.TEXTURED);
                }
            }
        });

        TextureChoiceController textureController = controller.getTextureController();
        if (textureController != null) {
            this.textureComponent = (JComponent) textureController.getView();
        }

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(this.defaultColorAndTextureRadioButton);
        buttonGroup.add(this.colorRadioButton);
        buttonGroup.add(this.textureRadioButton);
        updateColorAndTextureRadioButtons(controller);

        // Create radio buttons bound to SHININESS controller properties
        this.defaultShininessRadioButton = new JRadioButton(SwingTools.getLocalizedLabelText(preferences,
                HomeFurniturePanel.class, "defaultShininessRadioButton.text"));
        this.defaultShininessRadioButton.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ev) {
                if (defaultShininessRadioButton.isSelected()) {
                    controller.setShininess(HomeFurnitureController.FurnitureShininess.DEFAULT);
                }
            }
        });
        controller.addPropertyChangeListener(HomeFurnitureController.Property.SHININESS, new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent ev) {
                updateShininessRadioButtons(controller);
            }
        });
        this.mattRadioButton = new JRadioButton(SwingTools.getLocalizedLabelText(preferences, HomeFurniturePanel.class,
                "mattRadioButton.text"));
        this.mattRadioButton.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ev) {
                if (mattRadioButton.isSelected()) {
                    controller.setShininess(HomeFurnitureController.FurnitureShininess.MATT);
                }
            }
        });
        this.shinyRadioButton = new JRadioButton(SwingTools.getLocalizedLabelText(preferences, HomeFurniturePanel.class,
                "shinyRadioButton.text"));
        this.shinyRadioButton.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ev) {
                if (shinyRadioButton.isSelected()) {
                    controller.setShininess(HomeFurnitureController.FurnitureShininess.SHINY);
                }
            }
        });

        buttonGroup = new ButtonGroup();
        buttonGroup.add(this.defaultShininessRadioButton);
        buttonGroup.add(this.mattRadioButton);
        buttonGroup.add(this.shinyRadioButton);
        updateShininessRadioButtons(controller);

        // Create visible check box bound to VISIBLE controller property
        this.visibleCheckBox = new NullableCheckBox(SwingTools.getLocalizedLabelText(preferences, HomeFurniturePanel.class,
                "visibleCheckBox.text"));
        this.visibleCheckBox.setNullable(controller.getVisible() == null);
        this.visibleCheckBox.setValue(controller.getVisible());
        final PropertyChangeListener visibleChangeListener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent ev) {
                visibleCheckBox.setNullable(ev.getNewValue() == null);
                visibleCheckBox.setValue((Boolean) ev.getNewValue());
            }
        };
        controller.addPropertyChangeListener(HomeFurnitureController.Property.VISIBLE, visibleChangeListener);
        this.visibleCheckBox.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent ev) {
                controller.removePropertyChangeListener(HomeFurnitureController.Property.VISIBLE, visibleChangeListener);
                controller.setVisible(visibleCheckBox.getValue());
                controller.addPropertyChangeListener(HomeFurnitureController.Property.VISIBLE, visibleChangeListener);
            }
        });

        // Create power label and its spinner bound to POWER controller property
        if (controller.isLightPowerEditable()) {
            this.lightPowerLabel = new JLabel(SwingTools.getLocalizedLabelText(preferences, HomeFurniturePanel.class,
                    "lightPowerLabel.text", unitName));
            final NullableSpinner.NullableSpinnerNumberModel lightPowerSpinnerModel = new NullableSpinner.NullableSpinnerNumberModel(
                    0, 0, 100, 5);
            this.lightPowerSpinner = new NullableSpinner(lightPowerSpinnerModel);
            lightPowerSpinnerModel.setNullable(controller.getLightPower() == null);
            lightPowerSpinnerModel.setValue(controller.getLightPower() != null
                    ? Math.round(controller.getLightPower() * 100)
                    : null);
            final PropertyChangeListener lightPowerChangeListener = new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent ev) {
                    Float lightPower = (Float) ev.getNewValue();
                    lightPowerSpinnerModel.setNullable(lightPower == null);
                    lightPowerSpinnerModel.setValue(lightPower != null
                            ? Math.round((Float) ev.getNewValue() * 100)
                            : null);
                }
            };
            controller.addPropertyChangeListener(HomeFurnitureController.Property.LIGHT_POWER, lightPowerChangeListener);
            lightPowerSpinnerModel.addChangeListener(new ChangeListener() {

                public void stateChanged(ChangeEvent ev) {
                    controller.removePropertyChangeListener(HomeFurnitureController.Property.LIGHT_POWER,
                            lightPowerChangeListener);
                    controller.setLightPower(((Number) lightPowerSpinnerModel.getValue()).floatValue() / 100f);
                    controller.addPropertyChangeListener(HomeFurnitureController.Property.LIGHT_POWER, lightPowerChangeListener);
                }
            });
        }

        updateSizeComponents(controller);
        // Add a listener that enables / disables size fields depending on furniture
        // resizable and deformable
        PropertyChangeListener sizeListener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent ev) {
                updateSizeComponents(controller);
            }
        };
        controller.addPropertyChangeListener(HomeFurnitureController.Property.RESIZABLE, sizeListener);
        controller.addPropertyChangeListener(HomeFurnitureController.Property.DEFORMABLE, sizeListener);

        this.dialogTitle = preferences.getLocalizedString(HomeFurniturePanel.class, "homeFurniture.title");
    }

    /**
     * Updates color and texture radio buttons.
     */
    private void updateColorAndTextureRadioButtons(HomeFurnitureController controller) {
        if (controller.getPaint() == HomeFurnitureController.FurniturePaint.DEFAULT) {
            this.defaultColorAndTextureRadioButton.setSelected(true);
        } else if (controller.getPaint() == HomeFurnitureController.FurniturePaint.COLORED) {
            this.colorRadioButton.setSelected(true);
        } else if (controller.getPaint() == HomeFurnitureController.FurniturePaint.TEXTURED) {
            this.textureRadioButton.setSelected(true);
        } else { // null
            SwingTools.deselectAllRadioButtons(this.defaultColorAndTextureRadioButton, this.colorRadioButton,
                    this.textureRadioButton);
        }
    }

    /**
     * Updates shininess radio buttons.
     */
    private void updateShininessRadioButtons(HomeFurnitureController controller) {
        if (controller.getShininess() == HomeFurnitureController.FurnitureShininess.DEFAULT) {
            this.defaultShininessRadioButton.setSelected(true);
        } else if (controller.getShininess() == HomeFurnitureController.FurnitureShininess.MATT) {
            this.mattRadioButton.setSelected(true);
        } else if (controller.getShininess() == HomeFurnitureController.FurnitureShininess.SHINY) {
            this.shinyRadioButton.setSelected(true);
        } else { // null
            SwingTools.deselectAllRadioButtons(this.defaultShininessRadioButton, this.mattRadioButton, this.shinyRadioButton);
        }
    }

    /**
     * Updates size components depending on the fact that furniture is resizable
     * or not.
     */
    private void updateSizeComponents(final HomeFurnitureController controller) {
        boolean editableSize = controller.isResizable();
        this.widthLabel.setEnabled(editableSize);
        this.widthSpinner.setEnabled(editableSize);
        this.depthLabel.setEnabled(editableSize);
        this.depthSpinner.setEnabled(editableSize);
        this.heightLabel.setEnabled(editableSize);
        this.heightSpinner.setEnabled(editableSize);
        this.keepProportionsCheckBox.setEnabled(editableSize && controller.isDeformable());
        this.mirroredModelCheckBox.setEnabled(editableSize);
    }

    /**
     * Sets components mnemonics and label / component associations.
     */
    private void setMnemonics(UserPreferences preferences) {
        if (!OperatingSystem.isMacOSX()) {
            /*
             * this.metadataLabel.setLabelFor(this.metadataTextArea);
             * this.metadataLabel.setDisplayedMnemonic(KeyStroke.getKeyStroke(preferences.getLocalizedString(
             * HomeFurniturePanel.class,
             * "metadataLabel.mnemonic")).getKeyCode());
             * this.rolLabel.setDisplayedMnemonic(KeyStroke.getKeyStroke(preferences.getLocalizedString(
             * HomeFurniturePanel.class, "rolLabel.mnemonic")).getKeyCode());
             * this.rolLabel.setLabelFor(this.rolTextField);
             * this.amountLabel.setDisplayedMnemonic(KeyStroke.getKeyStroke(preferences.getLocalizedString(
             * HomeFurniturePanel.class, "amountLabel.mnemonic")).getKeyCode());
             * this.amountLabel.setLabelFor(this.amountSpinner);
             */
            this.nameLabel.setDisplayedMnemonic(KeyStroke.getKeyStroke(
                    preferences.getLocalizedString(HomeFurniturePanel.class, "nameLabel.mnemonic")).getKeyCode());
            this.nameLabel.setLabelFor(this.nameTextField);
            this.nameVisibleCheckBox.setMnemonic(KeyStroke.getKeyStroke(
                    preferences.getLocalizedString(HomeFurniturePanel.class, "nameVisibleCheckBox.mnemonic")).getKeyCode());
            this.xLabel.setDisplayedMnemonic(KeyStroke.getKeyStroke(
                    preferences.getLocalizedString(HomeFurniturePanel.class, "xLabel.mnemonic")).getKeyCode());
            this.xLabel.setLabelFor(this.xSpinner);
            this.yLabel.setDisplayedMnemonic(KeyStroke.getKeyStroke(
                    preferences.getLocalizedString(HomeFurniturePanel.class, "yLabel.mnemonic")).getKeyCode());
            this.yLabel.setLabelFor(this.ySpinner);
            this.elevationLabel.setDisplayedMnemonic(KeyStroke.getKeyStroke(
                    preferences.getLocalizedString(HomeFurniturePanel.class, "elevationLabel.mnemonic")).getKeyCode());
            this.elevationLabel.setLabelFor(this.elevationSpinner);
            this.angleLabel.setDisplayedMnemonic(KeyStroke.getKeyStroke(
                    preferences.getLocalizedString(HomeFurniturePanel.class, "angleLabel.mnemonic")).getKeyCode());
            this.angleLabel.setLabelFor(this.angleSpinner);
            this.keepProportionsCheckBox.setMnemonic(KeyStroke.getKeyStroke(
                    preferences.getLocalizedString(HomeFurniturePanel.class, "keepProportionsCheckBox.mnemonic")).getKeyCode());
            this.widthLabel.setDisplayedMnemonic(KeyStroke.getKeyStroke(
                    preferences.getLocalizedString(HomeFurniturePanel.class, "widthLabel.mnemonic")).getKeyCode());
            this.widthLabel.setLabelFor(this.widthSpinner);
            this.depthLabel.setDisplayedMnemonic(KeyStroke.getKeyStroke(
                    preferences.getLocalizedString(HomeFurniturePanel.class, "depthLabel.mnemonic")).getKeyCode());
            this.depthLabel.setLabelFor(this.depthSpinner);
            this.heightLabel.setDisplayedMnemonic(KeyStroke.getKeyStroke(
                    preferences.getLocalizedString(HomeFurniturePanel.class, "heightLabel.mnemonic")).getKeyCode());
            this.heightLabel.setLabelFor(this.heightSpinner);
            this.basePlanItemCheckBox.setMnemonic(KeyStroke.getKeyStroke(
                    preferences.getLocalizedString(HomeFurniturePanel.class, "basePlanItemCheckBox.mnemonic")).getKeyCode());
            ;
            this.mirroredModelCheckBox.setMnemonic(KeyStroke.getKeyStroke(
                    preferences.getLocalizedString(HomeFurniturePanel.class, "mirroredModelCheckBox.mnemonic")).getKeyCode());
            this.defaultColorAndTextureRadioButton.setMnemonic(KeyStroke.getKeyStroke(
                    preferences.getLocalizedString(HomeFurniturePanel.class, "defaultColorAndTextureRadioButton.mnemonic")).getKeyCode());
            this.colorRadioButton.setMnemonic(KeyStroke.getKeyStroke(
                    preferences.getLocalizedString(HomeFurniturePanel.class, "colorRadioButton.mnemonic")).getKeyCode());
            this.textureRadioButton.setMnemonic(KeyStroke.getKeyStroke(
                    preferences.getLocalizedString(HomeFurniturePanel.class, "textureRadioButton.mnemonic")).getKeyCode());
            this.defaultShininessRadioButton.setMnemonic(KeyStroke.getKeyStroke(
                    preferences.getLocalizedString(HomeFurniturePanel.class, "defaultShininessRadioButton.mnemonic")).getKeyCode());
            this.mattRadioButton.setMnemonic(KeyStroke.getKeyStroke(
                    preferences.getLocalizedString(HomeFurniturePanel.class, "mattRadioButton.mnemonic")).getKeyCode());
            this.shinyRadioButton.setMnemonic(KeyStroke.getKeyStroke(
                    preferences.getLocalizedString(HomeFurniturePanel.class, "shinyRadioButton.mnemonic")).getKeyCode());
            this.visibleCheckBox.setMnemonic(KeyStroke.getKeyStroke(
                    preferences.getLocalizedString(HomeFurniturePanel.class, "visibleCheckBox.mnemonic")).getKeyCode());
            if (this.lightPowerLabel != null) {
                this.lightPowerLabel.setDisplayedMnemonic(KeyStroke.getKeyStroke(
                        preferences.getLocalizedString(HomeFurniturePanel.class, "lightPowerLabel.mnemonic")).getKeyCode());
                this.lightPowerLabel.setLabelFor(this.lightPowerSpinner);
            }
        }
    }

    /**
     * Layouts panel components in panel with their labels.
     */
    private void layoutComponents(UserPreferences preferences, final HomeFurnitureController controller) {
        int labelAlignment = OperatingSystem.isMacOSX()
                ? GridBagConstraints.LINE_END
                : GridBagConstraints.LINE_START;
        int rowGap = OperatingSystem.isMacOSXLeopardOrSuperior()
                ? 0
                : 5;
        // Zero row    
        Insets labelInsets = new Insets(0, 0, 10, 5);
        Insets componentInsets = new Insets(0, 0, 10, 15);
        JPanel extraPanel = new JPanel();
        extraPanel.add(this.metadataLabel, new GridBagConstraints(
                0, 0, 1, 1, 0, 0, labelAlignment,
                GridBagConstraints.NONE, labelInsets, 0, 0));
        extraPanel.add(this.metadataTextArea, new GridBagConstraints(
                1, 0, 1, 1, 0, 0, GridBagConstraints.NONE,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

        extraPanel.add(this.amountLabel, new GridBagConstraints(
                0, 1, 1, 1, 0, 0, labelAlignment,
                GridBagConstraints.NONE, labelInsets, 0, 0));
        extraPanel.add(this.amountSpinner, new GridBagConstraints(
                1, 1, 1, 1, 0, 0, GridBagConstraints.NONE,
                GridBagConstraints.HORIZONTAL, componentInsets, -15, 0));
        extraPanel.add(this.rolCategoryLabel, new GridBagConstraints(
                2, 0, 1, 1, 0, 0, labelAlignment,
                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
        extraPanel.add(this.rolCategoryComboBox, new GridBagConstraints(
                2, 1, 1, 1, 0, 0, GridBagConstraints.NONE,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));
        extraPanel.add(this.rolLabel, new GridBagConstraints(
                3, 0, 1, 1, 0, 0, labelAlignment,
                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));
        extraPanel.add(this.rolComboBox, new GridBagConstraints(
                3, 1, 1, 1, 0, 0, GridBagConstraints.NONE,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));
        add(extraPanel, new GridBagConstraints(0, 0, 3, 1, 0, 0, labelAlignment, GridBagConstraints.HORIZONTAL, new Insets(
                0, 0, rowGap, 0), 0, 0));

        // First row
        JPanel namePanel = SwingTools.createTitledPanel(preferences.getLocalizedString(HomeFurniturePanel.class,
                "namePanel.title"));
        namePanel.add(this.nameLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, labelAlignment, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 5), 0, 0));
        namePanel.add(this.nameTextField, new GridBagConstraints(1, 0, 2, 1, 0, 0, GridBagConstraints.LINE_START,
                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 10), 0, 0));
        namePanel.add(this.nameVisibleCheckBox, new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START,
                GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(namePanel, new GridBagConstraints(0, 1, 3, 1, 0, 0, labelAlignment, GridBagConstraints.HORIZONTAL, new Insets(
                0, 0, rowGap, 0), 0, 0));
        // Location panel
        JPanel locationPanel = SwingTools.createTitledPanel(preferences.getLocalizedString(HomeFurniturePanel.class,
                "locationPanel.title"));
        labelInsets = new Insets(0, 0, 5, 5);
        Insets rightComponentInsets = new Insets(0, 0, 5, 0);
        locationPanel.add(this.xLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, labelAlignment, GridBagConstraints.NONE,
                labelInsets, 0, 0));
        locationPanel.add(this.xSpinner, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START,
                GridBagConstraints.HORIZONTAL, rightComponentInsets, -15, 0));
        locationPanel.add(this.yLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, labelAlignment, GridBagConstraints.NONE,
                labelInsets, 0, 0));
        locationPanel.add(this.ySpinner, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START,
                GridBagConstraints.HORIZONTAL, rightComponentInsets, -15, 0));
        locationPanel.add(this.elevationLabel, new GridBagConstraints(0, 2, 1, 1, 0, 0, labelAlignment,
                GridBagConstraints.NONE, labelInsets, 0, 0));
        locationPanel.add(this.elevationSpinner, new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.LINE_START,
                GridBagConstraints.HORIZONTAL, rightComponentInsets, -15, 0));
        locationPanel.add(this.angleLabel, new GridBagConstraints(0, 3, 1, 1, 0, 0, labelAlignment,
                GridBagConstraints.NONE, labelInsets, 0, 0));
        locationPanel.add(this.angleSpinner, new GridBagConstraints(1, 3, 1, 1, 0, 1, GridBagConstraints.LINE_START,
                GridBagConstraints.HORIZONTAL, rightComponentInsets, -15, 0));
        locationPanel.add(this.basePlanItemCheckBox, new GridBagConstraints(0, 4, 2, 1, 0, 0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(locationPanel, new GridBagConstraints(0, 2, 1, 1, 1, 0, labelAlignment, GridBagConstraints.BOTH, new Insets(0,
                0, rowGap, 0), 0, 0));
        // Size panel
        JPanel sizePanel = SwingTools.createTitledPanel(preferences.getLocalizedString(HomeFurniturePanel.class,
                "sizePanel.title"));
        sizePanel.add(this.widthLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, labelAlignment, GridBagConstraints.NONE,
                labelInsets, 0, 0));
        sizePanel.add(this.widthSpinner, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.LINE_START,
                GridBagConstraints.NONE, rightComponentInsets, -10, 0));
        sizePanel.add(this.depthLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, labelAlignment, GridBagConstraints.NONE,
                labelInsets, 0, 0));
        sizePanel.add(this.depthSpinner, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START,
                GridBagConstraints.NONE, rightComponentInsets, -10, 0));
        sizePanel.add(this.heightLabel, new GridBagConstraints(0, 2, 1, 1, 0, 0, labelAlignment, GridBagConstraints.NONE,
                labelInsets, 0, 0));
        sizePanel.add(this.heightSpinner, new GridBagConstraints(1, 2, 1, 1, 0, 1, GridBagConstraints.LINE_START,
                GridBagConstraints.NONE, rightComponentInsets, -10, 0));
        sizePanel.add(this.keepProportionsCheckBox, new GridBagConstraints(0, 3, 2, 1, 0, 0, GridBagConstraints.LINE_START,
                GridBagConstraints.NONE, rightComponentInsets, 0, 0));
        sizePanel.add(this.mirroredModelCheckBox, new GridBagConstraints(0, 4, 2, 1, 0, 0, GridBagConstraints.LINE_START,
                GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        add(sizePanel, new GridBagConstraints(1, 2, 2, 1, 1, 0, labelAlignment, GridBagConstraints.BOTH, new Insets(0, 0,
                rowGap, 0), 0, 0));
        // Color and Texture panel
        final JPanel colorAndTexturePanel = SwingTools.createTitledPanel(preferences.getLocalizedString(
                HomeFurniturePanel.class, "colorAndTexturePanel.title"));
        colorAndTexturePanel.add(this.defaultColorAndTextureRadioButton, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE, labelInsets, 0, 0));
        colorAndTexturePanel.add(this.colorRadioButton, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
        colorAndTexturePanel.add(this.colorButton, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.LINE_START,
                GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        if (this.textureComponent != null) {
            colorAndTexturePanel.add(this.textureRadioButton, new GridBagConstraints(0, 2, 1, 1, 0, 0,
                    GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5, 0, 0, 5), 0, 0));
            colorAndTexturePanel.add(this.textureComponent, new GridBagConstraints(1, 2, 1, 1, 0, 0,
                    GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
            this.textureComponent.setPreferredSize(this.colorButton.getPreferredSize());
        }
        add(colorAndTexturePanel, new GridBagConstraints(0, 3, 1, 1, 0, 0, labelAlignment, GridBagConstraints.BOTH,
                new Insets(0, 0, rowGap, 0), 0, 0));
        // Shininess panel
        final JPanel shininessPanel = SwingTools.createTitledPanel(preferences.getLocalizedString(HomeFurniturePanel.class,
                "shininessPanel.title"));
        shininessPanel.add(this.defaultShininessRadioButton, new GridBagConstraints(0, 0, 1, 1, 0, 1,
                GridBagConstraints.LINE_START, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
        shininessPanel.add(this.mattRadioButton, new GridBagConstraints(0, 1, 1, 1, 0, 1, GridBagConstraints.LINE_START,
                GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        shininessPanel.add(this.shinyRadioButton, new GridBagConstraints(0, 2, 1, 1, 0, 1, GridBagConstraints.LINE_START,
                GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
        add(shininessPanel, new GridBagConstraints(1, 3, 2, 1, 0, 0, labelAlignment, GridBagConstraints.BOTH, new Insets(0,
                0, rowGap, 0), 0, 0));
        // Last row
        add(this.visibleCheckBox, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.LINE_START,
                GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
        if (this.lightPowerLabel != null) {
            add(this.lightPowerLabel, new GridBagConstraints(1, 4, 1, 1, 0, 0, labelAlignment, GridBagConstraints.NONE,
                    new Insets(0, 10, 0, 5), 0, 0));
            add(this.lightPowerSpinner, new GridBagConstraints(2, 4, 1, 1, 0, 0, GridBagConstraints.LINE_START,
                    GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
        }

        controller.addPropertyChangeListener(HomeFurnitureController.Property.TEXTURABLE, new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent ev) {
                colorAndTexturePanel.setVisible(controller.isTexturable());
                shininessPanel.setVisible(controller.isTexturable());
            }
        });
        colorAndTexturePanel.setVisible(controller.isTexturable());
        shininessPanel.setVisible(controller.isTexturable());
    }

    /**
     * Displays this panel in a modal dialog box.
     */
    public void displayView(View parentView) {
        if (SwingTools.showConfirmDialog((JComponent) parentView, this, this.dialogTitle, this.nameTextField) == JOptionPane.OK_OPTION) {
            this.controller.modifyFurniture();
        }
    }
}
