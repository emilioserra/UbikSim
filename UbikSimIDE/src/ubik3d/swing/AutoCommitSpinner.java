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

import java.text.DecimalFormat;
import java.text.Format;

import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

/**
 * A spinner which commits its value during edition and selects 
 * the value displayed in its editor when it gains focus.
 * @author Emmanuel Puybaret
 */
public class AutoCommitSpinner extends JSpinner {
  /**
   * Creates a spinner with a given <code>model</code>.
   */
  public AutoCommitSpinner(SpinnerModel model) {
    super(model);    
    JComponent editor = getEditor();
    if (editor instanceof JSpinner.DefaultEditor) {
      final JFormattedTextField textField = ((JSpinner.DefaultEditor)editor).getTextField();      
      SwingTools.addAutoSelectionOnFocusGain(textField);
      
      // Commit text during edition
      if (textField.getFormatterFactory() instanceof DefaultFormatterFactory) {
        DefaultFormatterFactory formatterFactory = (DefaultFormatterFactory)textField.getFormatterFactory();
        JFormattedTextField.AbstractFormatter defaultFormatter = formatterFactory.getDefaultFormatter();
        if (defaultFormatter instanceof DefaultFormatter) {
          ((DefaultFormatter)defaultFormatter).setCommitsOnValidEdit(true);
        }
        if (defaultFormatter instanceof NumberFormatter) {
          final NumberFormatter numberFormatter = (NumberFormatter)defaultFormatter;
          final DecimalFormat defaultFormat = (DecimalFormat)numberFormatter.getFormat();
          final DecimalFormat noGroupingFormat = (DecimalFormat)defaultFormat.clone();
          noGroupingFormat.setGroupingUsed(false);
          // Create a delegate of default formatter to change value returned by getFormat
          NumberFormatter editFormatter = new NumberFormatter() {
              @Override
              public Format getFormat() {
                // Use a different format depending on whether the text field has focus or not
                if (textField.hasFocus()) {
                  // No grouping when text field has focus 
                  return noGroupingFormat;
                } else {
                  return defaultFormat;
                }
              }
            
              @Override
              public boolean getCommitsOnValidEdit() {
                return true;
              }
              
              @SuppressWarnings("unchecked")
              @Override
              public Comparable getMaximum() {
                return numberFormatter.getMaximum();
              }
              
              @SuppressWarnings("unchecked")
              @Override
              public Comparable getMinimum() {
                return numberFormatter.getMinimum();
              }
              
              @SuppressWarnings("unchecked")
              @Override
              public void setMaximum(Comparable maximum) {
                numberFormatter.setMaximum(maximum);
              }
              
              @SuppressWarnings("unchecked")
              @Override
              public void setMinimum(Comparable minimum) {
                numberFormatter.setMinimum(minimum);
              }
              
              @Override
              public Class<?> getValueClass() {
                return numberFormatter.getValueClass();
              }
            };
          textField.setFormatterFactory(new DefaultFormatterFactory(editFormatter));
        }
      }
    }
  }
}
