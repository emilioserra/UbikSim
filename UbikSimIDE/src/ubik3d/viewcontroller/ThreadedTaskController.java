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
package ubik3d.viewcontroller;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import ubik3d.model.UserPreferences;


/**
 * A MVC controller used to execute a particular task in a separated thread.
 * @author Emmanuel Puybaret
 */
public class ThreadedTaskController implements Controller {
  private final UserPreferences  preferences;
  private final ViewFactory      viewFactory;
  private final Callable<Void>   threadedTask;
  private final String           taskMessage;
  private final ExceptionHandler exceptionHandler;
  private final ExecutorService  threadExecutor;
  private ThreadedTaskView       view;
  private Future<?>              task;

  /**
   * Creates a controller that will execute in a separated thread the given task. 
   * This task shouldn't modify any model objects and should be able to handle
   * interruptions with <code>Thread</code> methods that the user may provoke 
   * when he wants to cancel a threaded task. 
   */
  public ThreadedTaskController(Callable<Void> threadedTask,
                                String taskMessage,
                                ExceptionHandler exceptionHandler,
                                UserPreferences preferences, 
                                ViewFactory viewFactory) {
    this.preferences = preferences;
    this.viewFactory = viewFactory;
    this.threadedTask = threadedTask;
    this.taskMessage = taskMessage;
    this.exceptionHandler = exceptionHandler;
    this.threadExecutor = Executors.newSingleThreadExecutor();
  }
  
  /**
   * Returns the view controlled by this controller.
   */
  public ThreadedTaskView getView() {
    // Create view lazily only once it's needed
    if (this.view == null) {
      this.view = this.viewFactory.createThreadedTaskView(this.taskMessage, this.preferences, this);
    }
    return this.view;
  }

  /**
   * Executes in a separated thread the task given in constructor. This task shouldn't
   * modify any model objects. 
   */
  public void executeTask(final View executingView) {
    this.task = this.threadExecutor.submit(new FutureTask<Void>(threadedTask) {
        @Override
        public void run() {
          // Update running status in view
          getView().invokeLater(new Runnable() {
              public void run() {
                getView().setTaskRunning(true, executingView);
              }
            });
          super.run();
        }
      
        @Override
        protected void done() {
          // Update running status in view
          getView().invokeLater(new Runnable() {
              public void run() {
                getView().setTaskRunning(false, executingView);
                task = null;
              }
            });
          
          try {
            get();
          } catch (ExecutionException ex) {
            // Handle exceptions with handler            
            final Throwable throwable = ex.getCause();
            if (throwable instanceof Exception) {
              getView().invokeLater(new Runnable() {
                  public void run() {
                    exceptionHandler.handleException((Exception)throwable);
                  }
                });
            } else {
              throwable.printStackTrace();
            }
          } catch (final InterruptedException ex) {
            // Handle exception with handler            
            getView().invokeLater(new Runnable() {
                public void run() {
                  exceptionHandler.handleException(ex);
                }
              });
          }
        }
      });
  }
  
  /**
   * Cancels the threaded task if it's running.
   */
  public void cancelTask() {
    if (this.task != null) {
      this.task.cancel(true);
    }
  }
  
  /**
   * Returns <code>true</code> if the threaded task is running.
   */
  public boolean isTaskRunning() {
    return this.task != null && !this.task.isDone();
  }

  /**
   * Handles exception that may happen during the execution of a threaded task.
   */
  public static interface ExceptionHandler {
    public void handleException(Exception ex);
  }
}
