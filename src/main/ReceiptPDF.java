package main;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.Row;

public class ReceiptPDF {
	public ReceiptPDF(int orderid) throws IOException {
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Windows".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}

		// GET ALL DATA FROM DATABASE
		// DECLARE ALL DATA FOR RECEIPT
		String name = null;
		String phoneno = null;
		String date = null;
		String time = null;
		String paymenttype = null;
		double totalprice = 0;
		double customerpay = 0;
		String address = null;
		String gender = null;
		boolean regularcuststate = false;

		// GET ORDER DATA FROM DATABASE
		String querygetdataorders = "SELECT * FROM orders WHERE id = ?";
		try (Connection conn = Main.connect(); PreparedStatement pstmt = conn.prepareStatement(querygetdataorders)) {

			pstmt.setInt(1, orderid);
			ResultSet result = pstmt.executeQuery();

			// loop through the result set
			while (result.next()) {
				date = result.getString("date");
				time = result.getString("time");
			}
			conn.close();
		} catch (SQLException e) {
			System.out.println("Receipt SQL ERROR: " + e.getMessage());
		}

		// GET CUSTOMER DATA FROM DATABASE
		String querygetdatacustomer = "SELECT * FROM customer WHERE orderid = ?";
		try (Connection conn = Main.connect(); PreparedStatement pstmt = conn.prepareStatement(querygetdatacustomer)) {

			pstmt.setInt(1, orderid);
			ResultSet result = pstmt.executeQuery();

			// loop through the result set
			while (result.next()) {
				name = result.getString("name");
				phoneno = result.getString("phoneno");
				address = result.getString("address");
				gender = result.getString("gender");
				regularcuststate = result.getBoolean("regularcustomer");
			}
			conn.close();
		} catch (SQLException e) {
			System.out.println("Receipt SQL ERROR: " + e.getMessage());
		}

		// GET PAYMENT DATA FROM DATABASE
		String querygetdatapayment = "SELECT * FROM payment WHERE orderid = ?";
		try (Connection conn = Main.connect(); PreparedStatement pstmt = conn.prepareStatement(querygetdatapayment)) {

			pstmt.setInt(1, orderid);
			ResultSet result = pstmt.executeQuery();

			// loop through the result set
			while (result.next()) {
				paymenttype = result.getString("paymenttype");
				totalprice = result.getDouble("totalprice");
				customerpay = result.getDouble("custpay");
			}
			conn.close();
		} catch (SQLException e) {
			System.out.println("Receipt SQL ERROR: " + e.getMessage());
		}

		String regularcustomer = "No";
		if (regularcuststate == true) {
			regularcustomer = "Yes";
		}

		// CREATE NEW PDF DOCUMENT
		PDDocument document = new PDDocument();

		// Retrieving the pages of the document
		PDPage page = new PDPage();
		document.addPage(page);

		page = document.getPage(0);
		PDPageContentStream contentStream = new PDPageContentStream(document, page);

		// Begin the Content stream
		contentStream.beginText();

		// Setting the font to the Content stream
		contentStream.setFont(PDType1Font.TIMES_ROMAN, 12);

		// Setting the position for the line
		contentStream.newLineAtOffset(90, 740);

		String text = "HARRIS";

		// Adding text in the form of string
		contentStream.showText(text);

		// Ending the content stream
		contentStream.endText();

		// Closing the content stream
		contentStream.close();
		
		//Initialize table
        float margin = 10;
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
        float yStart = yStartNewPage;
        float bottomMargin = 0;

		BaseTable table = new BaseTable(yStart, yStartNewPage, bottomMargin, tableWidth, margin, document, page, true,
				true);
		
		//Create Header row
		Row<PDPage> headerRow = table.createRow(15f);
		Cell<PDPage> cell = headerRow.createCell(100, "Awesome Facts About Belgium");
		cell.setFont(PDType1Font.HELVETICA_BOLD);
		cell.setFillColor(Color.BLACK);
		table.addHeaderRow(headerRow);
		table.draw();

		JFrame saveframe = new JFrame();
		saveframe.setIconImage(new ImageIcon(Cashierframe.class.getResource("/main/logo/logo.png")).getImage());
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Save Receipt PDF file");
		fileChooser.setSelectedFile(new File("Receipt ID " + orderid));
		fileChooser.setFileFilter(new FileNameExtensionFilter("pdf file", "pdf"));
		int userSelection = fileChooser.showSaveDialog(saveframe);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			String filename = fileChooser.getSelectedFile().toString();
			if (!filename.endsWith(".pdf")) {
				filename += ".pdf";
				fileToSave = new File(filename);
			}
			document.save(new File(fileToSave.getAbsolutePath()));
		}

		// Closing the document
		document.close();
	}
}
