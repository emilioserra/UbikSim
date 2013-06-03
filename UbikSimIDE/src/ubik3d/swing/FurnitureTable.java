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

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import ubik3d.model.CollectionEvent;
import ubik3d.model.CollectionListener;
import ubik3d.model.Content;
import ubik3d.model.Home;
import ubik3d.model.HomeFurnitureGroup;
import ubik3d.model.HomePieceOfFurniture;
import ubik3d.model.HomeTexture;
import ubik3d.model.Selectable;
import ubik3d.model.SelectionEvent;
import ubik3d.model.SelectionListener;
import ubik3d.model.UserPreferences;
import ubik3d.model.HomePieceOfFurniture.SortableProperty;
import ubik3d.tools.ResourceURLContent;
import ubik3d.viewcontroller.FurnitureController;
import ubik3d.viewcontroller.View;


/**
 * A table displaying home furniture.
 * @author Emmanuel Puybaret
 */
public class FurnitureTable extends JTable implements View, Printable {
  private ListSelectionListener  tableSelectionListener;
  private boolean selectionByUser;

  /**
   * Creates a table that displays furniture of <code>home</code>.
   * @param home        the home displayed by this view
   * @param preferences the preferences of the application
   */
  public FurnitureTable(Home home, UserPreferences preferences) {
    this(home, preferences, null);
  }

  /**
   * Creates a table controlled by <code>controller</code>
   * that displays furniture of <code>home</code>.
   */
  public FurnitureTable(Home home, UserPreferences preferences, 
                       FurnitureController controller) {
    setModel(new FurnitureTableModel(home));
    setColumnModel(new FurnitureTableColumnModel(home, preferences));
    updateTableColumnsWidth();
    updateTableSelectedFurniture(home.getSelectedItems());
    // Add listeners to model
    if (controller != null) {
      addSelectionListeners(home, controller);
      // Enable sort in table with click in header
      addTableHeaderListener(controller);
      addTableColumnModelListener(controller);
      addMouseListener(controller);
    }
    addHomeListener(home);
    addUserPreferencesListener(preferences);
  }
  
  /**
   * Adds selection listeners to this table.
   */
  private void addSelectionListeners(final Home home,
                                     final FurnitureController controller) {   
    final SelectionListener homeSelectionListener = new SelectionListener() {
        public void selectionChanged(SelectionEvent ev) {
          updateTableSelectedFurniture(home.getSelectedItems());        
        }
      };
    this.tableSelectionListener = new ListSelectionListener () {
        public void valueChanged(ListSelectionEvent ev) {
          selectionByUser = true;
          int [] selectedRows = getSelectedRows();
          // Build the list of selected furniture
          List<HomePieceOfFurniture> selectedFurniture =
              new ArrayList<HomePieceOfFurniture>(selectedRows.length);
          TableModel tableModel = getModel();
          for (int index : selectedRows) {
            // Add to selectedFurniture table model value that stores piece
            selectedFurniture.add((HomePieceOfFurniture)tableModel.getValueAt(index, 0));
          }
          // Set the new selection in home with controller
          controller.setSelectedFurniture(selectedFurniture);
          selectionByUser = false;
        }
      };
    getSelectionModel().addListSelectionListener(this.tableSelectionListener);
    home.addSelectionListener(homeSelectionListener);
  }

  /**
   * Updates selected furniture in table from <code>selectedItems</code>. 
   */
  private void updateTableSelectedFurniture(List<Selectable> selectedItems) {
    ListSelectionModel selectionModel = getSelectionModel();
    selectionModel.removeListSelectionListener(this.tableSelectionListener);

    FurnitureTableModel tableModel = (FurnitureTableModel)getModel();
    int minIndex = Integer.MAX_VALUE;
    int maxIndex = Integer.MIN_VALUE;
    int [] furnitureIndices = new int [selectedItems.size()];
    int selectedFurnitureCount = 0;
    for (Selectable item : selectedItems) {
      if (item instanceof HomePieceOfFurniture) {
        // Search index of piece in sorted table model
        int index = tableModel.getPieceOfFurnitureIndex((HomePieceOfFurniture)item);
        // If the piece was found (during the addition of a piece to home, the model may not be updated yet) 
        if (index != -1) {
          furnitureIndices [selectedFurnitureCount++] = index;
          minIndex = Math.min(minIndex, index);
          maxIndex = Math.max(maxIndex, index);
        }
      }
    }

    if (selectedFurnitureCount < furnitureIndices.length) {
      // Reduce furnitureIndices array size to selectedRowCount
      int [] tmp = new int [selectedFurnitureCount];
      System.arraycopy(furnitureIndices, 0, tmp, 0, selectedFurnitureCount);
      furnitureIndices = tmp;
    }
    Arrays.sort(furnitureIndices);
    
    if (getSelectedRowCount() != selectedFurnitureCount
        || !Arrays.equals(getSelectedRows(), furnitureIndices)) {
      // Update table selection if it differs from selected furniture 
      clearSelection();
      for (int min = 0; min < furnitureIndices.length; ) {
        // Search the interval of following indices
        int max = min;
        while (max + 1 < furnitureIndices.length
            && furnitureIndices [max] + 1 == furnitureIndices [max + 1]) {
          max++;
        }
        addRowSelectionInterval(furnitureIndices [min], furnitureIndices [max]);
        min = max + 1;
      }
    }

    if (!this.selectionByUser && minIndex != Integer.MIN_VALUE) {
      makeRowsVisible(minIndex, maxIndex);
    }
    getSelectionModel().addListSelectionListener(this.tableSelectionListener);
  }

  /**
   * Updates table columns width from the content of its cells.
   */
  private void updateTableColumnsWidth() {
    int intercellWidth = getIntercellSpacing().width;
    TableColumnModel columnModel = getColumnModel();
    TableModel tableModel = getModel();
    for (int columnIndex = 0, n = columnModel.getColumnCount(); columnIndex < n; columnIndex++) {
      TableColumn column = columnModel.getColumn(columnIndex);
      int modelColumnIndex = convertColumnIndexToModel(columnIndex);
      int preferredWidth = column.getHeaderRenderer().getTableCellRendererComponent(
          this, column.getHeaderValue(), false, false, -1, columnIndex).getPreferredSize().width;
      int rowCount = tableModel.getRowCount();
      if (rowCount > 0) {
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
          preferredWidth = Math.max(preferredWidth, 
              column.getCellRenderer().getTableCellRendererComponent(
                  this, tableModel.getValueAt(rowIndex, modelColumnIndex), false, false, -1, columnIndex).
                      getPreferredSize().width);
        }
      } else {
        preferredWidth = Math.max(preferredWidth, column.getPreferredWidth());
      }
      column.setPreferredWidth(preferredWidth + intercellWidth);
      column.setWidth(preferredWidth + intercellWidth);
    }
  }
  
  /**
   * Adds a double click mouse listener to modify selected furniture.
   */
  private void addMouseListener(final FurnitureController controller) {
    addMouseListener(new MouseAdapter () {
        @Override
        public void mouseClicked(MouseEvent ev) {
          if (ev.getClickCount() == 2) {
            controller.modifySelectedFurniture();
          }
        }
      });
  }

  /**
   * Adds a listener to <code>preferences</code> to repaint this table
   * and its header when unit or language changes.  
   */
  private void addUserPreferencesListener(UserPreferences preferences) {
    preferences.addPropertyChangeListener(
        UserPreferences.Property.UNIT, new UserPreferencesChangeListener(this));
    preferences.addPropertyChangeListener(
        UserPreferences.Property.LANGUAGE, new UserPreferencesChangeListener(this));
  }

  /**
   * Preferences property listener bound to this table with a weak reference to avoid
   * strong link between user preferences and this table.  
   */
  private static class UserPreferencesChangeListener implements PropertyChangeListener {
    private WeakReference<FurnitureTable>  furnitureTable;

    public UserPreferencesChangeListener(FurnitureTable furnitureTable) {
      this.furnitureTable = new WeakReference<FurnitureTable>(furnitureTable);
    }
    
    public void propertyChange(PropertyChangeEvent ev) {
      // If furniture table was garbage collected, remove this listener from preferences
      FurnitureTable furnitureTable = this.furnitureTable.get();
      if (furnitureTable == null) {
        ((UserPreferences)ev.getSource()).removePropertyChangeListener(
            UserPreferences.Property.valueOf(ev.getPropertyName()), this);
      } else {
        furnitureTable.repaint();
        furnitureTable.getTableHeader().repaint();
      }
    }
  }

  /**
   * Adds <code>PropertyChange</code> and {@link FurnitureListener FurnitureListener} listeners 
   * to home to update furniture sort in table when <code>furnitureSortedProperty</code>, 
   * <code>furnitureAscendingSorted</code> or furniture in <code>home</code> changes.
   */
  private void addHomeListener(final Home home) {
    PropertyChangeListener sortListener = 
      new PropertyChangeListener () {
        public void propertyChange(PropertyChangeEvent ev) {
          ((FurnitureTableModel)getModel()).filterAndSortFurniture();
          // Update selected rows
          updateTableSelectedFurniture(home.getSelectedItems());
          getTableHeader().repaint();
        }
      };
    home.addPropertyChangeListener(Home.Property.FURNITURE_SORTED_PROPERTY, sortListener);
    home.addPropertyChangeListener(Home.Property.FURNITURE_DESCENDING_SORTED, sortListener);
    
    final PropertyChangeListener furnitureChangeListener = 
      new PropertyChangeListener () {
        public void propertyChange(PropertyChangeEvent ev) {
          // As furniture properties values change may alter sort order, update sort and whole table
          ((FurnitureTableModel)getModel()).filterAndSortFurniture();
          // Update selected rows
          updateTableSelectedFurniture(home.getSelectedItems());
        }
      };
    for (HomePieceOfFurniture piece : home.getFurniture()) {
      piece.addPropertyChangeListener(furnitureChangeListener);
    }
    home.addFurnitureListener(new CollectionListener<HomePieceOfFurniture>() {
        public void collectionChanged(CollectionEvent<HomePieceOfFurniture> ev) {
          if (ev.getType() == CollectionEvent.Type.ADD) {
            ev.getItem().addPropertyChangeListener(furnitureChangeListener);
          } else {
            ev.getItem().removePropertyChangeListener(furnitureChangeListener);
          }
        }
      });
  }

  /**
   * Ensures the rectangle which displays rows from <code>minIndex</code> to <code>maxIndex</code> is visible.
   */
  private void makeRowsVisible(int minRow, int maxRow) {    
    // Compute the rectangle that includes a row 
    Rectangle includingRectangle = getCellRect(minRow, 0, true);
    if (minRow != maxRow) {
      includingRectangle = includingRectangle.
          union(getCellRect(maxRow, 0, true));      
    }
    if (getAutoResizeMode() == AUTO_RESIZE_OFF) {
      int lastColumn = getColumnCount() - 1;
      includingRectangle = includingRectangle.
          union(getCellRect(minRow, lastColumn, true));
      if (minRow != maxRow) {
        includingRectangle = includingRectangle.
            union(getCellRect(maxRow, lastColumn, true));      
      }
    }
    scrollRectToVisible(includingRectangle);
  }

  /**
   * Adds a mouse listener on table header that will call <code>controller</code> sort method.
   */
  private void addTableHeaderListener(final FurnitureController controller) {
    // Sort on click in column header 
    getTableHeader().addMouseListener(new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent ev) {
          int columnIndex = getTableHeader().columnAtPoint(ev.getPoint());
          Object columnIdentifier = getColumnModel().getColumn(columnIndex).getIdentifier();
          if (columnIdentifier instanceof HomePieceOfFurniture.SortableProperty) {
            controller.sortFurniture((HomePieceOfFurniture.SortableProperty)columnIdentifier);
          }
        }
      });
  }

  /**
   * Adds a listener on table column model that will call <code>controller</code> 
   * <code>setFurnitureVisibleProperties</code> method.
   */
  private void addTableColumnModelListener(final FurnitureController controller) {
    // Update furniture visible properties when users move table columns 
    getColumnModel().addColumnModelListener(new TableColumnModelListener() {
        public void columnAdded(TableColumnModelEvent ev) {
        }
  
        public void columnMarginChanged(ChangeEvent ev) {
        }
  
        public void columnMoved(TableColumnModelEvent ev) {
          getColumnModel().removeColumnModelListener(this);
          // Build the list of furniture visible properties
          List<HomePieceOfFurniture.SortableProperty> furnitureVisibleProperties = 
              new ArrayList<HomePieceOfFurniture.SortableProperty>();
          for (Enumeration<TableColumn> it = getColumnModel().getColumns(); it.hasMoreElements(); ) {
            Object columnIdentifier = it.nextElement().getIdentifier();
            if (columnIdentifier instanceof HomePieceOfFurniture.SortableProperty) {
              furnitureVisibleProperties.add((HomePieceOfFurniture.SortableProperty)columnIdentifier);
            }
          }
          controller.setFurnitureVisibleProperties(furnitureVisibleProperties);
          getColumnModel().addColumnModelListener(this);
        }
  
        public void columnRemoved(TableColumnModelEvent ev) {
        }
  
        public void columnSelectionChanged(ListSelectionEvent ev) {
        }
      });
  }

  /**
   * Prints this component to make it fill <code>pageFormat</code> imageable size.
   */
  public int print(Graphics g, PageFormat pageFormat, int pageIndex) throws PrinterException {
    // Create a printable column model from the column model of this table 
    // with printable renderers for each column
    DefaultTableColumnModel printableColumnModel = new DefaultTableColumnModel();
    TableColumnModel columnModel = getColumnModel();
    final DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer();
    defaultRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    TableCellRenderer printableHeaderRenderer = new TableCellRenderer() {
        public Component getTableCellRendererComponent(JTable table, Object value, 
                                   boolean isSelected, boolean hasFocus, int row, int column) {
          // Delegate rendering to default cell renderer
          JLabel headerRendererLabel = (JLabel)defaultRenderer.getTableCellRendererComponent(table, value, 
              isSelected, hasFocus, row, column);
          // Don't display sort icon
          headerRendererLabel.setIcon(null);
          // Change header background and foreground
          headerRendererLabel.setBackground(Color.LIGHT_GRAY);
          headerRendererLabel.setForeground(Color.BLACK);
          headerRendererLabel.setBorder(BorderFactory.createCompoundBorder(
              BorderFactory.createLineBorder(Color.BLACK),
              headerRendererLabel.getBorder()));
          return headerRendererLabel;
        }
      };
    for (int columnIndex = 0, n = columnModel.getColumnCount(); columnIndex < n; columnIndex++) {
      final TableColumn tableColumn = columnModel.getColumn(columnIndex);
      // Create a printable column from existing table column
      TableColumn printableColumn = new TableColumn();
      printableColumn.setIdentifier(tableColumn.getIdentifier());
      printableColumn.setHeaderValue(tableColumn.getHeaderValue());
      TableCellRenderer printableCellRenderer = new TableCellRenderer() {
          public Component getTableCellRendererComponent(JTable table, Object value, 
                                 boolean isSelected, boolean hasFocus, int row, int column) {
            // Delegate rendering to existing cell renderer 
            TableCellRenderer cellRenderer = tableColumn.getCellRenderer();
            Component rendererComponent = cellRenderer.getTableCellRendererComponent(table, value, 
                isSelected, hasFocus, row, column);
            if (rendererComponent instanceof JCheckBox) {
              // Prefer a x sign for boolean values instead of check boxes
              rendererComponent = defaultRenderer.getTableCellRendererComponent(table, 
                  ((JCheckBox)rendererComponent).isSelected() ? "x" : "", false, false, row, column);
            }
            rendererComponent.setBackground(Color.WHITE);
            rendererComponent.setForeground(Color.BLACK);
            return rendererComponent;
          }
        };
      // Change printable column cell renderer 
      printableColumn.setCellRenderer(printableCellRenderer);
      // Change printable column header renderer
      printableColumn.setHeaderRenderer(printableHeaderRenderer);
      printableColumnModel.addColumn(printableColumn);
    }    
    return print(g, pageFormat, pageIndex, printableColumnModel, Color.BLACK);
  }

  /**
   * Prints this table in Event Dispatch Thread.
  */
  private int print(final Graphics g, 
                    final PageFormat pageFormat, 
                    final int pageIndex, 
                    final TableColumnModel printableColumnModel,
                    final Color gridColor) throws PrinterException {
    if (EventQueue.isDispatchThread()) {
      TableColumnModel oldColumnModel = getColumnModel();
      Color oldGridColor = getGridColor();
      setColumnModel(printableColumnModel);   
      updateTableColumnsWidth();
      setGridColor(gridColor);
      Printable printable = getPrintable(PrintMode.FIT_WIDTH, null, null);
      int pageExists = printable.print(g, pageFormat, pageIndex);
      // Restore column model and grid color to their previous values
      setColumnModel(oldColumnModel);
      setGridColor(oldGridColor);
      return pageExists;
    } else {
      // Print synchronously table in Event Dispatch Thread
      // The best solution should be to be able to print out of Event Dispatch Thread
      // a new instance of JTable customized with printableColumnModel and gridColor,
      // but Swing refuses to print a JTable that isn't attached to a visible frame
      class RunnableContext {
        int pageExists;
        PrinterException exception;
      }
      
      final RunnableContext context = new RunnableContext();
      try {
        EventQueue.invokeAndWait(new Runnable() {
            public void run() {
              try {
                 context.pageExists = print(g, pageFormat, pageIndex, printableColumnModel, gridColor);
              } catch (PrinterException ex) {
                context.exception = ex;
              }
            }
          });
        if (context.exception != null) {
          throw context.exception;
        }
        return context.pageExists;
      } catch (InterruptedException ex) {
        throw new InterruptedPrinterException("Print interrupted");
      } catch (InvocationTargetException ex) {
        if (ex.getCause() instanceof RuntimeException) {
          throw (RuntimeException)ex.getCause();
        } else {
          throw (Error)ex.getCause();
        }
      }
    }
  }

  /**
   * Returns a CSV formatted text describing the selected pieces of <code>furniture</code>.  
   */
  public String getClipboardCSV() {
    StringBuilder csv = new StringBuilder(); 
    String lineSeparator = System.getProperty("line.separator");
    // Header
    for (int columnIndex = 0, n = this.columnModel.getColumnCount(); columnIndex < n; columnIndex++) {
      if (columnIndex > 0) {
        csv.append("\t");
      }
      csv.append(this.columnModel.getColumn(columnIndex).getHeaderValue());
    }
    csv.append(lineSeparator);
    
    // Selected values 
    for (int rowIndex : getSelectedRows()) {
      TableModel model = getModel();
      HomePieceOfFurniture copiedPiece = (HomePieceOfFurniture)model.getValueAt(rowIndex, 0);
      for (int columnIndex = 0, n = this.columnModel.getColumnCount(); columnIndex < n; columnIndex++) {
        if (columnIndex > 0) {
          csv.append("\t");
        }
        TableColumn column = this.columnModel.getColumn(columnIndex);
        Object columnIdentifier = column.getIdentifier();
        if (columnIdentifier instanceof HomePieceOfFurniture.SortableProperty) {
          switch ((HomePieceOfFurniture.SortableProperty)columnIdentifier) {
            case CATALOG_ID :
              // Copy piece catalog id
              String catalogId = copiedPiece.getCatalogId();
              csv.append(catalogId != null ? catalogId : "");
              break;
            case NAME :
              // Copy piece name
              csv.append(copiedPiece.getName());
              break;
            case COLOR :
              if (copiedPiece.getColor() != null) {
                // Copy piece color at #xxxxxx format              
                csv.append("#" + Integer.toHexString(copiedPiece.getColor()).substring(2));
              }
              break;
            case TEXTURE :
              if (copiedPiece.getTexture() != null) {
                csv.append(copiedPiece.getTexture().getName());
              }
            case WIDTH :
            case DEPTH :
            case HEIGHT : 
            case X : 
            case Y :
            case ELEVATION : 
            case ANGLE :
            case PRICE : 
            case VALUE_ADDED_TAX_PERCENTAGE : 
            case VALUE_ADDED_TAX :
            case PRICE_VALUE_ADDED_TAX_INCLUDED : 
              // Copy numbers as they are displayed by their renderer
              csv.append(((JLabel)column.getCellRenderer().getTableCellRendererComponent(
                  this, copiedPiece, false, false, rowIndex, columnIndex)).getText());
              break;
            case MOVABLE :
              // Copy boolean as true or false
              csv.append(copiedPiece.isMovable());
              break;
            case DOOR_OR_WINDOW : 
              csv.append(copiedPiece.isDoorOrWindow());
              break;
            case VISIBLE :
              csv.append(copiedPiece.isVisible());
              break;
          }
        } else {
          Component rendererComponent = column.getCellRenderer().getTableCellRendererComponent(
              this, copiedPiece, false, false, rowIndex, columnIndex);
          if (rendererComponent instanceof JLabel) {
            csv.append(((JLabel)rendererComponent).getText());              
          } else {
            csv.append(model.getValueAt(rowIndex, columnIndex));
          }
        }  
      }
      csv.append(lineSeparator);
    }
    return csv.toString();
  }
  
  /**
   * Sets the filter applied to the furniture displayed in this table.
   */
  public void setFurnitureFilter(FurnitureTable.FurnitureFilter filter) {
    FurnitureTableModel tableModel = (FurnitureTableModel)getModel();
    tableModel.setFurnitureFilter(filter);
  }
  
  /**
   * Column table model used by this table.
   */
  private static class FurnitureTableColumnModel extends DefaultTableColumnModel {
    private static final ResourceURLContent GROUP_ICON_CONTENT = 
        new ResourceURLContent(FurnitureTable.class, "resources/groupIcon.png");
    
    private Map<HomePieceOfFurniture.SortableProperty, TableColumn> availableColumns;

    public FurnitureTableColumnModel(Home home, UserPreferences preferences) {
      createAvailableColumns(home, preferences);
      addHomeListener(home);
      addLanguageListener(preferences);
      updateModelColumns(home.getFurnitureVisibleProperties());
    }

    /**
     * Creates the list of available columns from furniture sortable properties.
     */
    private void createAvailableColumns(Home home, UserPreferences preferences) {
      this.availableColumns = new HashMap<HomePieceOfFurniture.SortableProperty, TableColumn>();
      TableCellRenderer headerRenderer = getHeaderRenderer(home);
      // Create the list of custom columns
      for (HomePieceOfFurniture.SortableProperty columnProperty : HomePieceOfFurniture.SortableProperty.values()) {
        TableColumn tableColumn = new TableColumn();
        tableColumn.setIdentifier(columnProperty);
        tableColumn.setHeaderValue(getColumnName(columnProperty, preferences));
        tableColumn.setCellRenderer(getColumnRenderer(columnProperty, preferences));
        tableColumn.setPreferredWidth(getColumnPreferredWidth(columnProperty));
        tableColumn.setHeaderRenderer(headerRenderer);
        this.availableColumns.put(columnProperty, tableColumn);
      }
    }

    /**
     * Adds a property change listener to home to update displayed columns list 
     * from furniture visible properties. 
     */
    private void addHomeListener(final Home home) {
      home.addPropertyChangeListener(Home.Property.FURNITURE_VISIBLE_PROPERTIES, 
          new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent ev) {
              updateModelColumns(home.getFurnitureVisibleProperties());
            }
          });
    }

    /**
     * Adds a property change listener to <code>preferences</code> to update
     * column names when preferred language changes.
     */
    private void addLanguageListener(UserPreferences preferences) {
      preferences.addPropertyChangeListener(UserPreferences.Property.LANGUAGE, 
          new LanguageChangeListener(this));
    }

    /**
     * Preferences property listener bound to this component with a weak reference to avoid
     * strong link between preferences and this component.  
     */
    private static class LanguageChangeListener implements PropertyChangeListener {
      private WeakReference<FurnitureTableColumnModel> furnitureTableColumnModel;

      public LanguageChangeListener(FurnitureTableColumnModel furnitureTable) {
        this.furnitureTableColumnModel = new WeakReference<FurnitureTableColumnModel>(furnitureTable);
      }
      
      public void propertyChange(PropertyChangeEvent ev) {
        // If furniture table column model was garbage collected, remove this listener from preferences
        FurnitureTableColumnModel furnitureTableColumnModel = this.furnitureTableColumnModel.get();
        UserPreferences preferences = (UserPreferences)ev.getSource();
        if (furnitureTableColumnModel == null) {
          preferences.removePropertyChangeListener(
              UserPreferences.Property.LANGUAGE, this);
        } else {          
          // Change column name and renderer from current locale
          for (TableColumn tableColumn : furnitureTableColumnModel.availableColumns.values()) {
            HomePieceOfFurniture.SortableProperty columnIdentifier = 
                (HomePieceOfFurniture.SortableProperty)tableColumn.getIdentifier();
            tableColumn.setHeaderValue(furnitureTableColumnModel.getColumnName(columnIdentifier, preferences));
            tableColumn.setCellRenderer(furnitureTableColumnModel.getColumnRenderer(columnIdentifier, preferences));
          }
        }
      }
    }
    
    /**
     * Updates displayed columns list from furniture visible properties. 
     */
    private void updateModelColumns(List<HomePieceOfFurniture.SortableProperty> furnitureVisibleProperties) {
      // Remove columns not in furnitureVisibleProperties
      for (int i = this.tableColumns.size() - 1; i >= 0; i--) {
        TableColumn tableColumn = this.tableColumns.get(i);
        Object columnIdentifier = tableColumn.getIdentifier();
        if ((columnIdentifier instanceof HomePieceOfFurniture.SortableProperty)
            && !furnitureVisibleProperties.contains(columnIdentifier)) {
          removeColumn(tableColumn);
        } 
      }
      // Add columns not currently displayed
      for (HomePieceOfFurniture.SortableProperty visibleProperty : furnitureVisibleProperties) {
        TableColumn tableColumn = this.availableColumns.get(visibleProperty);
        if (!this.tableColumns.contains(tableColumn)) {
          addColumn(tableColumn);
        }
      }
      // Reorder columns 
      for (int i = 0, n = furnitureVisibleProperties.size(); i < n; i++) {
        TableColumn tableColumn = this.availableColumns.get(furnitureVisibleProperties.get(i));
        int tableColumnIndex = this.tableColumns.indexOf(tableColumn);
        if (tableColumnIndex != i) {
          moveColumn(tableColumnIndex, i);
        }
      }
    }

    /**
     * Returns localized column names.
     */
    private String getColumnName(HomePieceOfFurniture.SortableProperty property, 
                                 UserPreferences preferences) {
      switch (property) {
        case CATALOG_ID :
          return preferences.getLocalizedString(FurnitureTable.class, "catalogIdColumn");
        case NAME :
          return preferences.getLocalizedString(FurnitureTable.class, "nameColumn");
        case WIDTH :
          return preferences.getLocalizedString(FurnitureTable.class, "widthColumn");
        case DEPTH :
          return preferences.getLocalizedString(FurnitureTable.class, "depthColumn");
        case HEIGHT : 
          return preferences.getLocalizedString(FurnitureTable.class, "heightColumn");
        case X : 
          return preferences.getLocalizedString(FurnitureTable.class, "xColumn");
        case Y :
          return preferences.getLocalizedString(FurnitureTable.class, "yColumn");
        case ELEVATION : 
          return preferences.getLocalizedString(FurnitureTable.class, "elevationColumn");
        case ANGLE :
          return preferences.getLocalizedString(FurnitureTable.class, "angleColumn");
        case COLOR :
          return preferences.getLocalizedString(FurnitureTable.class, "colorColumn");
        case TEXTURE :
          return preferences.getLocalizedString(FurnitureTable.class, "textureColumn");
        case MOVABLE :
          return preferences.getLocalizedString(FurnitureTable.class, "movableColumn");
        case DOOR_OR_WINDOW : 
          return preferences.getLocalizedString(FurnitureTable.class, "doorOrWindowColumn");
        case VISIBLE :
          return preferences.getLocalizedString(FurnitureTable.class, "visibleColumn");
        case PRICE :
          return preferences.getLocalizedString(FurnitureTable.class, "priceColumn");          
        case VALUE_ADDED_TAX_PERCENTAGE :
          return preferences.getLocalizedString(FurnitureTable.class, "valueAddedTaxPercentageColumn");          
        case VALUE_ADDED_TAX :
          return preferences.getLocalizedString(FurnitureTable.class, "valueAddedTaxColumn");          
        case PRICE_VALUE_ADDED_TAX_INCLUDED :
          return preferences.getLocalizedString(FurnitureTable.class, "priceValueAddedTaxIncludedColumn");          
        default :
          throw new IllegalArgumentException("Unknown column name " + property);
      }
    }

    /**
     * Returns the default preferred width of a column.
     */
    private int getColumnPreferredWidth(SortableProperty property) {
      switch (property) {
        case CATALOG_ID :
        case NAME :
          return 120; 
        case WIDTH :
        case DEPTH :
        case HEIGHT : 
        case X : 
        case Y :
        case ELEVATION : 
          return 50;
        case ANGLE :
          return 35;        
        case COLOR :
        case TEXTURE :
          return 30;        
        case MOVABLE :
        case DOOR_OR_WINDOW : 
        case VISIBLE :
          return 20;
        case PRICE :
        case VALUE_ADDED_TAX_PERCENTAGE :
        case VALUE_ADDED_TAX :
        case PRICE_VALUE_ADDED_TAX_INCLUDED :
          return 70;          
        default :
          throw new IllegalArgumentException("Unknown column name " + property);
      }
    }

    /**
     * Returns column renderers.
     */
    private TableCellRenderer getColumnRenderer(HomePieceOfFurniture.SortableProperty property, 
                                                UserPreferences preferences) {
      switch (property) {
        case CATALOG_ID :
          return getCatalogIdRenderer(); 
        case NAME :
          return getNameWithIconRenderer(); 
        case WIDTH :
          return getSizeRenderer(HomePieceOfFurniture.SortableProperty.WIDTH, preferences);
        case DEPTH :
          return getSizeRenderer(HomePieceOfFurniture.SortableProperty.DEPTH, preferences);
        case HEIGHT : 
          return getSizeRenderer(HomePieceOfFurniture.SortableProperty.HEIGHT, preferences);
        case X : 
          return getSizeRenderer(HomePieceOfFurniture.SortableProperty.X, preferences);
        case Y :
          return getSizeRenderer(HomePieceOfFurniture.SortableProperty.Y, preferences);
        case ELEVATION : 
          return getSizeRenderer(HomePieceOfFurniture.SortableProperty.ELEVATION, preferences);
        case ANGLE :
          return getAngleRenderer();        
        case COLOR :
          return getColorRenderer();        
        case TEXTURE :
          return getTextureRenderer();        
        case MOVABLE :
          return getBooleanRenderer(HomePieceOfFurniture.SortableProperty.MOVABLE);
        case DOOR_OR_WINDOW : 
          return getBooleanRenderer(HomePieceOfFurniture.SortableProperty.DOOR_OR_WINDOW);
        case VISIBLE :
          return getBooleanRenderer(HomePieceOfFurniture.SortableProperty.VISIBLE);
        case PRICE :
          return getPriceRenderer(HomePieceOfFurniture.SortableProperty.PRICE, preferences);          
        case VALUE_ADDED_TAX_PERCENTAGE :
          return getValueAddedTaxPercentageRenderer();          
        case VALUE_ADDED_TAX :
          return getPriceRenderer(HomePieceOfFurniture.SortableProperty.VALUE_ADDED_TAX, preferences);          
        case PRICE_VALUE_ADDED_TAX_INCLUDED :
          return getPriceRenderer(HomePieceOfFurniture.SortableProperty.PRICE_VALUE_ADDED_TAX_INCLUDED, preferences);          
        default :
          throw new IllegalArgumentException("Unknown column name " + property);
      }
    }

    /**
     * Returns a renderer that displays the catalog id of a piece of furniture. 
     */
    private TableCellRenderer getCatalogIdRenderer() {
      return new DefaultTableCellRenderer() { 
        @Override
        public Component getTableCellRendererComponent(JTable table, 
             Object value, boolean isSelected, boolean hasFocus, 
             int row, int column) {
          HomePieceOfFurniture piece = (HomePieceOfFurniture)value; 
          return super.getTableCellRendererComponent(
              table, piece.getCatalogId(), isSelected, hasFocus, row, column); 
        }
      };
    }

    /**
     * Returns a renderer that displays the name of a piece of furniture with its icon ahead. 
     */
    private TableCellRenderer getNameWithIconRenderer() {
      return new DefaultTableCellRenderer() { 

        @Override
        public Component getTableCellRendererComponent(JTable table, 
             Object value, boolean isSelected, boolean hasFocus, 
             int row, int column) {
          HomePieceOfFurniture piece = (HomePieceOfFurniture)value; 
          JLabel label = (JLabel)super.getTableCellRendererComponent(
            table, piece.getName(), isSelected, hasFocus, row, column); 
          Content iconContent;
          if (piece instanceof HomeFurnitureGroup) {
            iconContent = GROUP_ICON_CONTENT;
          } else {
            iconContent = piece.getIcon();
          }
          label.setIcon(IconManager.getInstance().getIcon(
              iconContent, table.getRowHeight(), table)); 
          return label;
        }
      };
    }

    /**
     * Returns a renderer that converts the displayed <code>property</code> of a piece of furniture 
     * to inch in case preferences unit us equal to INCH. 
     */
    private TableCellRenderer getSizeRenderer(HomePieceOfFurniture.SortableProperty property,
                                              final UserPreferences preferences) {
      // Renderer super class used to display sizes
      class SizeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, 
             Object value, boolean isSelected, boolean hasFocus, 
             int row, int column) {
          value = preferences.getLengthUnit().getFormat().format(value);
          setHorizontalAlignment(SwingConstants.RIGHT);
          return super.getTableCellRendererComponent(
              table, value, isSelected, hasFocus, row, column);
        }
      };
      
      switch (property) {
        case WIDTH :
          return new SizeRenderer() {
              @Override
              public Component getTableCellRendererComponent(JTable table, 
                  Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, 
                    ((HomePieceOfFurniture)value).getWidth(), isSelected, hasFocus, row, column);
              }
            };
        case DEPTH :
          return new SizeRenderer() {
              @Override
              public Component getTableCellRendererComponent(JTable table, 
                  Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, 
                    ((HomePieceOfFurniture)value).getDepth(), isSelected, hasFocus, row, column);
              }
            };
        case HEIGHT :
          return new SizeRenderer() {
              @Override
              public Component getTableCellRendererComponent(JTable table, 
                  Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, 
                    ((HomePieceOfFurniture)value).getHeight(), isSelected, hasFocus, row, column);
              }
            };
        case X :
          return new SizeRenderer() {
              @Override
              public Component getTableCellRendererComponent(JTable table, 
                  Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, 
                    ((HomePieceOfFurniture)value).getX(), isSelected, hasFocus, row, column);
              }
            };
        case Y :
          return new SizeRenderer() {
              @Override
              public Component getTableCellRendererComponent(JTable table, 
                  Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, 
                    ((HomePieceOfFurniture)value).getY(), isSelected, hasFocus, row, column);
              }
            };
        case ELEVATION :
          return new SizeRenderer() {
              @Override
              public Component getTableCellRendererComponent(JTable table, 
                  Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, 
                    ((HomePieceOfFurniture)value).getElevation(), isSelected, hasFocus, row, column);
              }
            };
        default :
          throw new IllegalArgumentException(property + " column not a size column");
      }
    }

    /**
     * Returns a renderer that converts the displayed <code>property</code> of a piece of furniture 
     * as a price. 
     */
    private TableCellRenderer getPriceRenderer(HomePieceOfFurniture.SortableProperty property,
                                               final UserPreferences preferences) {
      // Renderer super class used to display sizes
      class PriceRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, 
             Object value, boolean isSelected, boolean hasFocus, 
             int row, int column) {
          String currency = preferences.getCurrency();
          if (value != null && currency != null) {
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
            currencyFormat.setCurrency(Currency.getInstance(currency));
            value = currencyFormat.format(value);
          } else {
            value = "";
          }
          setHorizontalAlignment(SwingConstants.RIGHT);
          return super.getTableCellRendererComponent(
              table, value, isSelected, hasFocus, row, column);
        }
      };
      
      switch (property) {
        case PRICE :
          return new PriceRenderer() {
              @Override
              public Component getTableCellRendererComponent(JTable table, 
                  Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, 
                    ((HomePieceOfFurniture)value).getPrice(), isSelected, hasFocus, row, column);
              }
            };
        case VALUE_ADDED_TAX :
          return new PriceRenderer() {
              @Override
              public Component getTableCellRendererComponent(JTable table, 
                  Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, 
                    ((HomePieceOfFurniture)value).getValueAddedTax(), isSelected, hasFocus, row, column);
              }
            };
        case PRICE_VALUE_ADDED_TAX_INCLUDED :
          return new PriceRenderer() {
              @Override
              public Component getTableCellRendererComponent(JTable table, 
                  Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, 
                    ((HomePieceOfFurniture)value).getPriceValueAddedTaxIncluded(), isSelected, hasFocus, row, column);
              }
            };
        default :
          throw new IllegalArgumentException(property + " column not a price column");
      }
    }

    /**
     * Returns a renderer that displays the angle property of a piece of furniture. 
     */
    private TableCellRenderer getAngleRenderer() {
      return new DefaultTableCellRenderer() { 
        private TableCellRenderer integerRenderer;
        
        @Override
        public Component getTableCellRendererComponent(JTable table, 
             Object value, boolean isSelected, boolean hasFocus, 
             int row, int column) {
          if (this.integerRenderer == null) {
            this.integerRenderer = table.getDefaultRenderer(Integer.class);
          }
          int angle = (int)(Math.round(Math.toDegrees(((HomePieceOfFurniture)value).getAngle()) + 360) % 360);
          return integerRenderer.getTableCellRendererComponent(
              table, angle, isSelected, hasFocus, row, column); 
        }
      };
    }

    /**
     * Returns a renderer that displays the value added tax percentage property of a piece of furniture. 
     */
    private TableCellRenderer getValueAddedTaxPercentageRenderer() {
      return new DefaultTableCellRenderer() { 
        @Override
        public Component getTableCellRendererComponent(JTable table, 
             Object value, boolean isSelected, boolean hasFocus, 
             int row, int column) {
          BigDecimal valueAddedTaxPercentage = ((HomePieceOfFurniture)value).getValueAddedTaxPercentage();
          if (valueAddedTaxPercentage != null) {
            NumberFormat percentInstance = NumberFormat.getPercentInstance();
            percentInstance.setMinimumFractionDigits(valueAddedTaxPercentage.scale() - 2);
            value = percentInstance.format(valueAddedTaxPercentage);
          } else {
            value = "";
          }
          setHorizontalAlignment(SwingConstants.RIGHT);
          return super.getTableCellRendererComponent(
              table, value, isSelected, hasFocus, row, column);
        }
      };
    }

    /**
     * Returns a renderer that displays the RGB value of the color property 
     * of a piece of furniture in a bordered label.
     */
    private TableCellRenderer getColorRenderer() {
      return new DefaultTableCellRenderer() {
        // A square icon filled with the foreground color of its component 
        // and surrounded by table foreground color 
        private Icon squareIcon = new Icon () {
          public int getIconHeight() {
            return getFont().getSize();
          }

          public int getIconWidth() {
            return getIconHeight();
          }

          public void paintIcon(Component c, Graphics g, int x, int y) {
            int squareSize = getIconHeight();
            g.setColor(c.getForeground());          
            g.fillRect(x + 2, y + 2, squareSize - 3, squareSize - 3);
            g.setColor(c.getParent().getParent().getForeground());
            g.drawRect(x + 1, y + 1, squareSize - 2, squareSize - 2);
          }
        };
          
        @Override
        public Component getTableCellRendererComponent(JTable table, 
             Object value, boolean isSelected, boolean hasFocus, 
             int row, int column) {
          Integer color = ((HomePieceOfFurniture)value).getColor();
          JLabel label = (JLabel)super.getTableCellRendererComponent(
              table, color, isSelected, hasFocus, row, column);
          if (color != null) {
            label.setText(null);
            label.setIcon(squareIcon);
            label.setForeground(new Color(color));
          } else {
            label.setText("-");
            label.setIcon(null);
            label.setForeground(table.getForeground());
          }
          label.setHorizontalAlignment(SwingConstants.CENTER);
          return label;
        } 
      };
    }
   
    /**
     * Returns a renderer that displays the texture of a piece as an icon. 
     */
    private TableCellRenderer getTextureRenderer() {
      return new DefaultTableCellRenderer() { 
        {
          setHorizontalAlignment(CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, 
             Object value, boolean isSelected, boolean hasFocus, 
             int row, int column) {
          HomePieceOfFurniture piece = (HomePieceOfFurniture)value; 
          JLabel label = (JLabel)super.getTableCellRendererComponent(
            table, null, isSelected, hasFocus, row, column); 
          HomeTexture texture = piece.getTexture();
          if (texture != null) {
            Content textureContent = texture.getImage();
            label.setIcon(IconManager.getInstance().getIcon(
                textureContent, table.getRowHeight() - 2, table));
          } else {
            label.setIcon(null);
          }
          return label;
        }
      };
    }

    /**
     * Returns a renderer that displays <code>property</code> of a piece of furniture 
     * with <code>JTable</code> default boolean renderer. 
     */
    private TableCellRenderer getBooleanRenderer(HomePieceOfFurniture.SortableProperty property) {
      // Renderer super class used to display booleans
      class BooleanRenderer implements TableCellRenderer {
        private TableCellRenderer booleanRenderer;

        public Component getTableCellRendererComponent(JTable table, 
             Object value, boolean isSelected, boolean hasFocus, int row, int column) {
          if (this.booleanRenderer == null) {
            this.booleanRenderer = table.getDefaultRenderer(Boolean.class);
          }
          return this.booleanRenderer.getTableCellRendererComponent(
              table, value, isSelected, hasFocus, row, column);
        }
      };
      
      switch (property) {
        case MOVABLE :
          return new BooleanRenderer() {
              @Override
              public Component getTableCellRendererComponent(JTable table, 
                  Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, 
                    ((HomePieceOfFurniture)value).isMovable(), isSelected, hasFocus, row, column);
              }
            };
        case DOOR_OR_WINDOW :
          return new BooleanRenderer() {
              @Override
              public Component getTableCellRendererComponent(JTable table, 
                  Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, 
                    ((HomePieceOfFurniture)value).isDoorOrWindow(), isSelected, hasFocus, row, column);
              }
            };
        case VISIBLE :
          return new BooleanRenderer() {
              @Override
              public Component getTableCellRendererComponent(JTable table, 
                  Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, 
                    ((HomePieceOfFurniture)value).isVisible(), isSelected, hasFocus, row, column);
              }
            };
        default :
          throw new IllegalArgumentException(property + " column not a boolean column");
      }
    }
    
    /**
     * Returns column header renderer that displays an ascending or a descending icon 
     * when column is sorted, beside column name.
     */
    private TableCellRenderer getHeaderRenderer(final Home home) {
      // Return a table renderer that displays the icon matching current sort
      return new TableCellRenderer() {
          private TableCellRenderer headerRenderer;        
          private ImageIcon ascendingSortIcon = new ImageIcon(getClass().getResource("resources/ascending.png"));
          private ImageIcon descendingSortIcon = new ImageIcon(getClass().getResource("resources/descending.png"));
          
          public Component getTableCellRendererComponent(JTable table, 
               Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (this.headerRenderer == null) {
              this.headerRenderer = table.getTableHeader().getDefaultRenderer();
            }
            // Get default label
            JLabel label = (JLabel)this.headerRenderer.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);
            // Add to column an icon matching sort
            if (getColumn(column).getIdentifier().equals(home.getFurnitureSortedProperty())) {
              label.setHorizontalTextPosition(SwingConstants.LEADING);
              if (home.isFurnitureDescendingSorted()) {
                label.setIcon(descendingSortIcon);
              } else {
                label.setIcon(ascendingSortIcon);
              }
            } else {
              label.setIcon(null);
            }
            return label;
          }
        };
    }
  }

  
  /**
   * Model used by this table.
   */
  private static class FurnitureTableModel extends AbstractTableModel {
    private Home                       home;
    private List<HomePieceOfFurniture> filteredAndSortedFurniture;
    private FurnitureFilter            furnitureFilter;
    
    public FurnitureTableModel(Home home) {
      this.home = home;
      addHomeListener(home);
      filterAndSortFurniture();
    }

    private void addHomeListener(final Home home) {
      home.addFurnitureListener(new CollectionListener<HomePieceOfFurniture>() {
        public void collectionChanged(CollectionEvent<HomePieceOfFurniture> ev) {
          HomePieceOfFurniture piece = ev.getItem();
          int pieceIndex = ev.getIndex();
          switch (ev.getType()) {
            case ADD :
              int insertionIndex = getPieceOfFurnitureInsertionIndex(piece, home, pieceIndex);
              if (insertionIndex != -1) {
                filteredAndSortedFurniture.add(insertionIndex, piece);
                fireTableRowsInserted(insertionIndex, insertionIndex);
              }
              break;
            case DELETE :
              int deletionIndex = getPieceOfFurnitureDeletionIndex(piece, home, pieceIndex);
              if (deletionIndex != -1) {
                filteredAndSortedFurniture.remove(deletionIndex);
                fireTableRowsDeleted(deletionIndex, deletionIndex);
              }
              break;
          }
        }

        /**
         * Returns the index of an added <code>piece</code> in furniture table, with a default index
         * of <code>homePieceIndex</code> if <code>home</code> furniture isn't sorted.
         * If <code>piece</code> isn't added to furniture table, the returned value is
         * equals to the insertion index where piece should be added.
         */
        private int getPieceOfFurnitureInsertionIndex(HomePieceOfFurniture piece, Home home, int homePieceIndex) {
          if (furnitureFilter == null) {
            if (home.getFurnitureSortedProperty() == null) {
              return homePieceIndex;
            } 
          } else if (!furnitureFilter.include(home, piece)) {
            return -1;
          } else if (home.getFurnitureSortedProperty() == null) {
            // Find the index of the previous piece included in filteredAndSortedFurniture
            List<HomePieceOfFurniture> homeFurniture = home.getFurniture();
            int previousIncludedPieceIndex = homePieceIndex - 1;
            while (previousIncludedPieceIndex > 0 
                  && !furnitureFilter.include(home, homeFurniture.get(previousIncludedPieceIndex))) {
              previousIncludedPieceIndex--;
            }
            if (filteredAndSortedFurniture.size() == 0) {
              return 0;
            } else {
              return getPieceOfFurnitureIndex(homeFurniture.get(previousIncludedPieceIndex)) + 1;
            }
          }
          
          // Default case when piece is included and furniture is  sorted 
          int sortedIndex = Collections.binarySearch(filteredAndSortedFurniture, piece, getFurnitureComparator(home));
          if (sortedIndex >= 0) {
            return sortedIndex;
          } else {
            return -(sortedIndex + 1);
          }              
        }

        /**
         * Returns the index of an existing <code>piece</code> in furniture table, with a default index
         * of <code>homePieceIndex</code> if <code>home</code> furniture isn't sorted.
         */
        private int getPieceOfFurnitureDeletionIndex(HomePieceOfFurniture piece, Home home, int homePieceIndex) {
          if (furnitureFilter == null) {
            if (home.getFurnitureSortedProperty() == null) {
              return homePieceIndex;
            } 
          } 
          return getPieceOfFurnitureIndex(piece);              
        }
      });
    }

    @Override
    public String getColumnName(int columnIndex) {
      // Column name is set by TableColumn instances themselves 
      return null;
    }

    public int getColumnCount() {
      // Column count is set by TableColumnModel itself 
      return 0;
    }

    public int getRowCount() {
      return this.filteredAndSortedFurniture.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
      // Always return piece itself, the real property displayed at screen is chosen by renderer
      return this.filteredAndSortedFurniture.get(rowIndex);
    }

    /**
     * Returns the index of <code>piece</code> in furniture table, or -1 if it is included by filter. 
     */
    public int getPieceOfFurnitureIndex(HomePieceOfFurniture piece) {
      return this.filteredAndSortedFurniture.indexOf(piece);
    }

    /**
     * Sorts <code>home</code> furniture.
     */
    public void filterAndSortFurniture() {
      int previousRowCount = this.filteredAndSortedFurniture != null 
          ? this.filteredAndSortedFurniture.size()
          : 0;
      List<HomePieceOfFurniture> homeFurniture = this.home.getFurniture();
      if (this.furnitureFilter == null) {
        this.filteredAndSortedFurniture = new ArrayList<HomePieceOfFurniture>(homeFurniture);
      } else {
        // Create the filtered list of home furniture
        this.filteredAndSortedFurniture = new ArrayList<HomePieceOfFurniture>(homeFurniture.size());
        for (HomePieceOfFurniture homePiece : homeFurniture) {
          if (this.furnitureFilter.include(this.home, homePiece)) {
            this.filteredAndSortedFurniture.add(homePiece);
          }
        }
      }
      // Sort it if necessary
      if (this.home.getFurnitureSortedProperty() != null) {
        Comparator<HomePieceOfFurniture> furnitureComparator = getFurnitureComparator(this.home);
        Collections.sort(this.filteredAndSortedFurniture, furnitureComparator);         
      }
      
      if (previousRowCount != this.filteredAndSortedFurniture.size()) {
        fireTableDataChanged();
      } else {
        fireTableRowsUpdated(0, getRowCount() - 1);
      }
    }

    private Comparator<HomePieceOfFurniture> getFurnitureComparator(Home home) {
      Comparator<HomePieceOfFurniture> furnitureComparator = 
        HomePieceOfFurniture.getFurnitureComparator(home.getFurnitureSortedProperty());
      if (home.isFurnitureDescendingSorted()) {
        furnitureComparator = Collections.reverseOrder(furnitureComparator);
      }
      return furnitureComparator;
    }

    /**
     * Sets the filter applied to the furniture listed in this model.
     */
    public void setFurnitureFilter(FurnitureFilter furnitureFilter) {
      this.furnitureFilter = furnitureFilter;      
      filterAndSortFurniture();
    }
  }
  
  /**
   * The super type used to specify how furniture should be filtered in furniture table.
   */
  public static interface FurnitureFilter {
    /**
     * Returns <code>true</code> if the given <code>piece</code> should be shown, 
     * otherwise returns <code>false</code> if the <code>piece</code> should be hidden.
     */
    public abstract boolean include(Home home, HomePieceOfFurniture piece);
  }
}
