package main;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import be.quodlibet.boxable.BaseTable;
import be.quodlibet.boxable.Cell;
import be.quodlibet.boxable.HorizontalAlignment;
import be.quodlibet.boxable.Row;
import be.quodlibet.boxable.VerticalAlignment;

public class ReceiptPDF {
	public ReceiptPDF(int orderid) throws IOException {
		DecimalFormat priceformatter = new DecimalFormat("#0.00");
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
		PDImageXObject logoimage = PDImageXObject.createFromFile("src/main/logo/logo.png", document);
		contentStream.drawImage(logoimage, 25, 730, 45, 45);

		// ADD TITLE STORE
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA, 30);
		contentStream.newLineAtOffset(74, 738);
		contentStream.showText("Bakery Shop");
		contentStream.endText();

		// ADD CUSTOMER NAME TITLE
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA, 11);
		contentStream.newLineAtOffset(35, 700);
		contentStream.showText("Name: ");
		contentStream.endText();

		// ADD CUSTOMER NAME GET FROM SQL
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA, 11);
		contentStream.newLineAtOffset(73, 700);
		contentStream.showText(name);
		contentStream.endText();

		// ADD PHONE NO TITLE
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA, 11);
		contentStream.newLineAtOffset(35, 685);
		contentStream.showText("Phone number: ");
		contentStream.endText();

		// ADD PHONE NO GET FROM SQL
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA, 11);
		contentStream.newLineAtOffset(115, 685);
		contentStream.showText(phoneno);
		contentStream.endText();

		// ADD GENDER TITLE
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA, 11);
		contentStream.newLineAtOffset(35, 670);
		contentStream.showText("Gender: ");
		contentStream.endText();

		// ADD GENDER GET FROM SQL
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA, 11);
		contentStream.newLineAtOffset(80, 670);
		contentStream.showText(gender);
		contentStream.endText();

		// ADD ADDRESS TITLE
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA, 11);
		contentStream.newLineAtOffset(35, 655);
		contentStream.showText("Address: ");
		contentStream.endText();

		// SEPARATE LINE \n
		String addresslines[] = address.split("\\r?\\n");
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA, 11);
		contentStream.newLineAtOffset(82, 655);
		for (int i = 0; i < addresslines.length; i++) {
			// ADD ADDRESS GET FROM SQL
			contentStream.showText(addresslines[i]);
			contentStream.newLine();
		}
		contentStream.endText();

		// ADD ORDER ID TITLE
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA, 11);
		contentStream.newLineAtOffset(400, 700);
		contentStream.showText("Order ID: ");
		contentStream.endText();

		// ADD ORDER ID
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA, 11);
		contentStream.newLineAtOffset(450, 700);
		contentStream.showText(String.valueOf(orderid));
		contentStream.endText();

		// ADD ORDER ID TITLE
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA, 11);
		contentStream.newLineAtOffset(400, 685);
		contentStream.showText("Order date: ");
		contentStream.endText();

		// ADD ORDER ID
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA, 11);
		contentStream.newLineAtOffset(460, 685);
		contentStream.showText(date + " " + time);
		contentStream.endText();

		// ADD REGULAR CUSTOMER TITLE
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA, 11);
		contentStream.newLineAtOffset(400, 670);
		contentStream.showText("Regular Customer: ");
		contentStream.endText();

		// ADD REGULAR CUSTOMER GET FROM SQL
		contentStream.beginText();
		contentStream.setFont(PDType1Font.HELVETICA, 11);
		contentStream.newLineAtOffset(495, 670);
		contentStream.showText(regularcustomer);
		contentStream.endText();

		// Closing the content stream
		contentStream.close();

		// Initialize table
		float margin = 40;
		float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
		float yStartNewPage = page.getMediaBox().getHeight() - (2 * margin);
		float bottomMargin = 0;

		BaseTable table = new BaseTable(600, yStartNewPage, bottomMargin, tableWidth, margin, document, page, true,
				true);

		// Create Header row
		Row<PDPage> headerRow = table.createRow(15f);
		Cell<PDPage> cell = headerRow.createCell(100, "Items Ordered");
		cell.setFontSize(12);
		cell.setFont(PDType1Font.HELVETICA_BOLD);
		cell.setFillColor(new Color(254, 232, 170));
		table.addHeaderRow(headerRow);
		Row<PDPage> row = table.createRow(10f);
		cell = row.createCell(7, "No.");
		cell.setFont(PDType1Font.HELVETICA_BOLD);
		cell.setFillColor(new Color(255, 102, 186));
		cell = row.createCell(58, "Item name");
		cell.setFont(PDType1Font.HELVETICA_BOLD);
		cell.setFillColor(new Color(255, 102, 186));
		cell = row.createCell(10, "Quantity");
		cell.setFont(PDType1Font.HELVETICA_BOLD);
		cell.setFillColor(new Color(255, 102, 186));
		cell = row.createCell(25, "Price");
		cell.setFont(PDType1Font.HELVETICA_BOLD);
		cell.setFillColor(new Color(255, 102, 186));
		String querygetlistitem = "SELECT itemnumber,itemname,quantity,totalitems FROM item WHERE orderid = ?";
		Color itemcolor = new Color(171, 255, 255);
		double totalitemprice = 0;
		try (Connection conn = Main.connect(); PreparedStatement pstmt = conn.prepareStatement(querygetlistitem)) {

			pstmt.setInt(1, orderid);
			ResultSet result = pstmt.executeQuery();

			// loop through the result set
			while (result.next()) {
				totalitemprice = totalitemprice + result.getDouble("totalitems");

				Row<PDPage> row1 = table.createRow(10f);
				cell = row1.createCell(7, result.getString("itemnumber"));
				cell.setFillColor(itemcolor);
				cell = row1.createCell(58, result.getString("itemname"));
				cell.setFillColor(itemcolor);
				cell = row1.createCell(10, String.valueOf(result.getInt("quantity")));
				cell.setFillColor(itemcolor);
				cell = row1.createCell(25, "RM " + priceformatter.format(result.getDouble("totalitems")));
				cell.setFillColor(itemcolor);
			}
			conn.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		// ROW FOR TOTAL PRICE
		Row<PDPage> row2 = table.createRow(17f);
		cell = row2.createCell(75, "Total");
		cell.setFontSize(10);
		cell.setValign(VerticalAlignment.get("center"));
		cell.setAlign(HorizontalAlignment.get("right"));
		cell.setFillColor(new Color(122, 255, 217));
		cell = row2.createCell(25, "RM " + priceformatter.format(totalitemprice));
		cell.setFontSize(10);
		cell.setValign(VerticalAlignment.get("center"));
		cell.setFillColor(new Color(122, 255, 217));

		// SPACING NULL ROW
		Row<PDPage> rownull = table.createRow(13f);
		cell = rownull.createCell(100, "");
		cell.setBorderStyle(null);

		// ROW FOR OTHER PAYMENT DETAIL
		if (regularcuststate == true) {
			Row<PDPage> row3 = table.createRow(17f);
			cell = row3.createCell(75, "Total after discount:");
			cell.setFont(PDType1Font.HELVETICA);
			cell.setFontSize(11);
			cell.setValign(VerticalAlignment.get("bottom"));
			cell.setAlign(HorizontalAlignment.get("right"));
			cell.setBorderStyle(null);
			cell = row3.createCell(25, "RM " + priceformatter.format(totalprice));
			cell.setValign(VerticalAlignment.get("bottom"));
			cell.setFont(PDType1Font.HELVETICA);
			cell.setFontSize(11);
			cell.setBorderStyle(null);
		}

		// CUSTOMER PAY ROW
		Row<PDPage> row4 = table.createRow(17f);
		cell = row4.createCell(75, "Customer pay:");
		cell.setFont(PDType1Font.HELVETICA);
		cell.setFontSize(11);
		cell.setValign(VerticalAlignment.get("bottom"));
		cell.setAlign(HorizontalAlignment.get("right"));
		cell.setBorderStyle(null);
		cell = row4.createCell(25, "RM " + priceformatter.format(customerpay));
		cell.setValign(VerticalAlignment.get("bottom"));
		cell.setFont(PDType1Font.HELVETICA);
		cell.setFontSize(11);
		cell.setBorderStyle(null);

		// CUSTOMER PAY ROW
		Row<PDPage> row5 = table.createRow(17f);
		cell = row5.createCell(75, "Balance:");
		cell.setFont(PDType1Font.HELVETICA);
		cell.setFontSize(11);
		cell.setValign(VerticalAlignment.get("bottom"));
		cell.setAlign(HorizontalAlignment.get("right"));
		cell.setBorderStyle(null);
		cell = row5.createCell(25, "RM " + priceformatter.format(customerpay - totalprice));
		cell.setValign(VerticalAlignment.get("bottom"));
		cell.setFont(PDType1Font.HELVETICA);
		cell.setFontSize(11);
		cell.setBorderStyle(null);

		// PAYMENT TYPE ROW
		Row<PDPage> row6 = table.createRow(17f);
		cell = row6.createCell(75, "Payment type:");
		cell.setFont(PDType1Font.HELVETICA);
		cell.setFontSize(11);
		cell.setValign(VerticalAlignment.get("bottom"));
		cell.setAlign(HorizontalAlignment.get("right"));
		cell.setBorderStyle(null);
		cell = row6.createCell(25, paymenttype);
		cell.setValign(VerticalAlignment.get("bottom"));
		cell.setFont(PDType1Font.HELVETICA);
		cell.setFontSize(11);
		cell.setBorderStyle(null);

		table.draw();

		JFrame saveframe = new JFrame();
		saveframe.setIconImage(new ImageIcon(Cashierframe.class.getResource("/main/logo/logo.png")).getImage());
		JFileChooser fileChooser = new JFileChooser() {
			/**
			 * 
			 */
			private static final long serialVersionUID = -894233845899825024L;

			public void approveSelection() {

				File file = getSelectedFile();
				String filestring = file.toString();

				String[] left_side_of_dot = filestring.split("\\.");

				file = new File(left_side_of_dot[0] + ".pdf");

				if (file.exists() && getDialogType() == SAVE_DIALOG) {
					int result = JOptionPane.showConfirmDialog(saveframe, "The file exists, overwrite?",
							"Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
					switch (result) {
					case JOptionPane.YES_OPTION:
						super.approveSelection();
						return;
					case JOptionPane.NO_OPTION:
						return;
					case JOptionPane.CLOSED_OPTION:
						return;
					case JOptionPane.CANCEL_OPTION:
						cancelSelection();
						return;
					}
				}
				super.approveSelection();
			}
		};
		fileChooser.setDialogTitle("Save Receipt PDF file");
		fileChooser.setSelectedFile(new File("Receipt ID " + orderid));
		fileChooser.setFileFilter(new FileNameExtensionFilter("pdf file", "pdf"));
		int userSelection = fileChooser.showSaveDialog(saveframe);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			// CHECK IF EXISTS

			String filename = fileChooser.getSelectedFile().toString();
			if (!filename.endsWith(".pdf")) {
				filename += ".pdf";
				fileToSave = new File(filename);
				document.save(new File(fileToSave.getAbsolutePath()));
			}

		}

		// Closing the document
		document.close();
	}
}
