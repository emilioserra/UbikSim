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

import java.awt.Font;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStream;

import ubik3d.model.Home;
import ubik3d.model.UserPreferences;
import ubik3d.viewcontroller.ContentManager;
import ubik3d.viewcontroller.HomeController;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Home PDF printer. PDF creation is implemented with iText. 
 * @author Emmanuel Puybaret
 */
public class HomePDFPrinter {
  private final Home            home;
  private final UserPreferences preferences;
  private final HomeController  controller;
  private final Font            defaultFont;

  /**
   * Creates a PDF printer able to write to an output stream. 
   */
  public HomePDFPrinter(Home home, 
                        UserPreferences preferences, 
                        HomeController controller, 
                        Font defaultFont) {
    this.home = home;
    this.preferences = preferences;
    this.controller = controller;
    this.defaultFont = defaultFont;
  }

  /**
   * Writes to <code>outputStream</code> the print of a home in PDF format.
   */
  public void write(OutputStream outputStream) throws IOException {
    PageFormat pageFormat = HomePrintableComponent.getPageFormat(this.home.getPrint());
    Document pdfDocument = new Document(new Rectangle((float)pageFormat.getWidth(), (float)pageFormat.getHeight()));
    try {
      // Get a PDF writer that will write to the given PDF output stream
      PdfWriter pdfWriter = PdfWriter.getInstance(pdfDocument, outputStream);
      pdfDocument.open();
      
      // Set PDF document description
      pdfDocument.addAuthor(System.getProperty("user.name", ""));
      String pdfDocumentCreator = this.preferences.getLocalizedString(
          HomePDFPrinter.class, "pdfDocument.creator");    
      pdfDocument.addCreator(pdfDocumentCreator);
      pdfDocument.addCreationDate();
      String homeName = this.home.getName();
      if (homeName != null) {
        pdfDocument.addTitle(this.controller.getContentManager().getPresentationName(
            homeName, ContentManager.ContentType.PDF));
      }
      
      PdfContentByte pdfContent = pdfWriter.getDirectContent();
      HomePrintableComponent printableComponent = 
          new HomePrintableComponent(this.home, this.controller, this.defaultFont);
      // Print each page
      for (int page = 0, pageCount = printableComponent.getPageCount(); page < pageCount; page++) {
        // Check current thread isn't interrupted
        if (Thread.interrupted()) {
          throw new InterruptedIOException();
        }
        PdfTemplate pdfTemplate = pdfContent.createTemplate((float)pageFormat.getWidth(), 
            (float)pageFormat.getHeight());
        Graphics g = pdfTemplate.createGraphicsShapes((float)pageFormat.getWidth(), 
            (float)pageFormat.getHeight());        
        
        printableComponent.print(g, pageFormat, page);
        
        pdfContent.addTemplate(pdfTemplate, 0, 0);
        g.dispose();
        
        if (page != pageCount - 1) {
          pdfDocument.newPage();
        }
      }
      pdfDocument.close();
    } catch (DocumentException ex) {
      IOException exception = new IOException("Couldn't print to PDF");
      exception.initCause(ex);
      throw exception;
    } catch (InterruptedPrinterException ex) {
      throw new InterruptedIOException("Print to PDF interrupted");
    } catch (PrinterException ex) {
      IOException exception = new IOException("Couldn't print to PDF");
      exception.initCause(ex);
      throw exception;
    }
  }
}
