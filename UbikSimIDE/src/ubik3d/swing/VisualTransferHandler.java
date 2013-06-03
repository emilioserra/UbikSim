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

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * Transfer handler with visual representation on systems that support it.
 * @author Emmanuel Puybaret
 */
public class VisualTransferHandler extends TransferHandler {
  private final DragGestureRecognizerWithVisualRepresentation dragGestureRecognizerWithVisualRepresentation = 
        new DragGestureRecognizerWithVisualRepresentation();

  /**
   * Causes the Swing drag support to be initiated with a drag icon if necessary. 
   * This method implements the expected behavior of <code>exportAsDrag</code> 
   * if <code>getVisualRepresentation</code> was used in <code>TransferHandler</code> implementation.  
   * As only Mac OS X supports drag image, the drag icon will actually appear only on this system.
   */
  @Override
  public void exportAsDrag(JComponent source, InputEvent ev, int action) {
    int sourceActions = getSourceActions(source);
    int dragAction = sourceActions & action;
    if (DragSource.isDragImageSupported() 
        && dragAction != NONE 
        && (ev instanceof MouseEvent)) {
      this.dragGestureRecognizerWithVisualRepresentation.gestured(source, (MouseEvent)ev, sourceActions, dragAction);
    } else {
      super.exportAsDrag(source, ev, action);
    }
  }

  /**
   * A drag gesture recognizer that uses transfer handler visual representation.
   * See <code>TransferHandler$SwingDragGestureRecognizer</code> for original code. 
   */
  private class DragGestureRecognizerWithVisualRepresentation extends DragGestureRecognizer {
    public DragGestureRecognizerWithVisualRepresentation() {
      super(DragSource.getDefaultDragSource(), null, NONE, new DragListenerWithVisualRepresentation());
    }

    void gestured(JComponent component, MouseEvent ev, int sourceActions, int action) {
      setComponent(component);
      setSourceActions(sourceActions);
      appendEvent(ev);
      fireDragGestureRecognized(action, ev.getPoint());
    }

    @Override
    protected void registerListeners() {
    }

    @Override
    protected void unregisterListeners() {
    }
  };

  /**
   * A drag listener that uses transfer handler visual representation for drag image.
   * See <code>TransferHandler$DragHandler</code> for base code. 
   */
  private class DragListenerWithVisualRepresentation implements DragGestureListener, DragSourceListener {
    private boolean autoscrolls;
    private final Image EMPTY_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

    public void dragGestureRecognized(DragGestureEvent ev) {
      JComponent component = (JComponent)ev.getComponent();
      Transferable transferable = createTransferable(component);
      if (transferable != null) {
        this.autoscrolls = component.getAutoscrolls();
        component.setAutoscrolls(false);
        try {
          Icon icon = getVisualRepresentation(transferable);
          if (icon != null) {
            // Create a half transparent drag image from icon
            BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2D = (Graphics2D)image.getGraphics();
            g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.66f));
            icon.paintIcon(component, g2D, 0, 0);
            g2D.dispose();
            
            ev.startDrag(null, image, new Point(12, 24),  transferable, this);
          } else {
            // Force the use of an empty image otherwise Mac OS X uses a grey rectangle
            ev.startDrag(null, EMPTY_IMAGE, new Point(48, 48), transferable, this);
          }          
        } catch (InvalidDnDOperationException re) {
          component.setAutoscrolls(this.autoscrolls);
        }
      }

      exportDone(component, transferable, NONE);
    }

    public void dragEnter(DragSourceDragEvent ev) {
    }

    public void dragOver(DragSourceDragEvent ev) {
    }

    public void dragExit(DragSourceEvent ev) {
    }

    public void dragDropEnd(DragSourceDropEvent ev) {
      DragSourceContext dragSourceContext = ev.getDragSourceContext();
      JComponent component = (JComponent)dragSourceContext.getComponent();
      if (ev.getDropSuccess()) {
        exportDone(component, dragSourceContext.getTransferable(), ev.getDropAction());
      } else {
        exportDone(component, dragSourceContext.getTransferable(), NONE);
      }
      component.setAutoscrolls(this.autoscrolls);
    }

    public void dropActionChanged(DragSourceDragEvent ev) {
    }
  }
}
