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

import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ubik3d.model.UserPreferences;


/**
 * An action which <code>actionPerformed</code> method 
 * will call a parametrizable method. 
 * @author Emmanuel Puybaret
 */
public class ControllerAction extends ResourceAction {
  private final Object    controller;
  private final Method    controllerMethod;
  private final Object [] parameters;

  /**
   * Creates a disabled action with properties retrieved from a resource bundle 
   * in which key starts with <code>actionPrefix</code>.
   * @param preferences   user preferences used to retrieve localized description of the action
   * @param resourceClass the class used as a context to retrieve localized properties of the action
   * @param actionPrefix prefix used in resource bundle to search action properties
   * @param controller   the controller on which the method will be called
   * @param method       the name of the controller method that will be invoked
   *          in {@link #actionPerformed(ActionEvent) actionPerfomed}
   * @param parameters list of parameters to be used with <code>method</code>
   * @throws NoSuchMethodException if <code>method</code> with a
   *           matching <code>parameters</code> list doesn't exist
   */
  public ControllerAction(UserPreferences preferences, 
                          Class<?> resourceClass, 
                          String actionPrefix, 
                          Object controller, 
                          String method, 
                          Object ... parameters) throws NoSuchMethodException {
    this(preferences, resourceClass, actionPrefix, false, controller, method, parameters);
  }

  /**
   * Creates an action with properties retrieved from a resource bundle 
   * in which key starts with <code>actionPrefix</code>.
   * @param preferences   user preferences used to retrieve localized description of the action
   * @param resourceClass the class used as a context to retrieve localized properties of the action
   * @param actionPrefix prefix used in resource bundle to search action properties
   * @param enabled <code>true</code> if the action should be enabled at creation.
   * @param controller   the controller on which the method will be called
   * @param method       the name of the controller method that will be invoked
   *          in {@link #actionPerformed(ActionEvent) actionPerfomed}
   * @param parameters list of parameters to be used with <code>method</code>
   * @throws NoSuchMethodException if <code>method</code> with a
   *           matching <code>parameters</code> list doesn't exist
   */
  public ControllerAction(UserPreferences preferences, 
                          Class<?> resourceClass, 
                          String actionPrefix,
                          boolean enabled,
                          Object controller, 
                          String method, 
                          Object ... parameters) throws NoSuchMethodException {
    super(preferences, resourceClass, actionPrefix, enabled);
    this.controller = controller;
    this.parameters = parameters;
    // Get parameters class
    Class<?> [] parametersClass = new Class [parameters.length];
    for(int i = 0; i < parameters.length; i++)
      parametersClass [i] = parameters [i].getClass();
    
    this.controllerMethod = controller.getClass().getMethod(method, parametersClass);
  }

  /**
   * Calls the method on controller given in constructor.
   */
  @Override
  public void actionPerformed(ActionEvent ev) {
    try {
      this.controllerMethod.invoke(controller, parameters);
    } catch (IllegalAccessException ex) {
      throw new RuntimeException (ex);
    } catch (InvocationTargetException ex) {
      throw new RuntimeException (ex);
    }
  }
}
