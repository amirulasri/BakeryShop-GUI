package main;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;
import java.awt.Color;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingConstants;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class Receipt extends JFrame {

	DecimalFormat priceformatter = new DecimalFormat("#0.00");

	private JPanel contentPane;
	private JTable table;

	/**
	 * Create the frame.
	 */
	public Receipt(int orderid) {
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

		DefaultTableModel listitemmodel = new DefaultTableModel(new Object[][] {},
				new String[] { "Item Number", "Item Name", "Quantity", "Price" }) {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return false;
			}
		};

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
				System.out.println(regularcuststate);
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

		String regularcustomer = "";
		if (regularcuststate == true) {
			regularcustomer = "Yes";
		}

		setIconImage(Toolkit.getDefaultToolkit().getImage(Receipt.class.getResource("/main/logo/logo.png")));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 936, 727);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(230, 184, 156));
		setContentPane(contentPane);
		setTitle(Main.getappname());
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		panel.setBackground(new Color(140, 47, 57));

		JLabel lblNewLabel_4 = new JLabel("Customer Details");
		lblNewLabel_4.setBackground(Color.WHITE);
		lblNewLabel_4.setFont(new Font("SansSerif", Font.PLAIN, 18));
		lblNewLabel_4.setForeground(Color.BLACK);

		JLabel lblNewLabel_1 = new JLabel("Name:");
		lblNewLabel_1.setForeground(Color.BLACK);
		lblNewLabel_1.setFont(new Font("SansSerif", Font.PLAIN, 16));

		JLabel namedisplay = new JLabel(name);
		namedisplay.setForeground(Color.BLACK);
		namedisplay.setFont(new Font("SansSerif", Font.PLAIN, 14));

		JLabel lblNewLabel_2 = new JLabel("Phone Number:");
		lblNewLabel_2.setForeground(Color.BLACK);
		lblNewLabel_2.setFont(new Font("SansSerif", Font.PLAIN, 16));

		JLabel phonenodisplay = new JLabel(phoneno);
		phonenodisplay.setForeground(Color.BLACK);
		phonenodisplay.setFont(new Font("SansSerif", Font.PLAIN, 14));

		JLabel lblNewLabel_4_2 = new JLabel("Order Details");
		lblNewLabel_4_2.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_4_2.setForeground(Color.BLACK);
		lblNewLabel_4_2.setFont(new Font("SansSerif", Font.PLAIN, 18));
		lblNewLabel_4_2.setBackground(Color.WHITE);

		JLabel lblNewLabel_1_2 = new JLabel("Order Date:");
		lblNewLabel_1_2.setForeground(Color.BLACK);
		lblNewLabel_1_2.setFont(new Font("SansSerif", Font.PLAIN, 16));

		JLabel orderdatedisplay = new JLabel(date + " " + time);
		orderdatedisplay.setHorizontalAlignment(SwingConstants.RIGHT);
		orderdatedisplay.setForeground(Color.BLACK);
		orderdatedisplay.setFont(new Font("SansSerif", Font.PLAIN, 14));

		JLabel lblNewLabel_1_2_1 = new JLabel("Order ID:");
		lblNewLabel_1_2_1.setForeground(Color.BLACK);
		lblNewLabel_1_2_1.setFont(new Font("SansSerif", Font.PLAIN, 16));

		JLabel orderiddisplay = new JLabel(String.valueOf(orderid));
		orderiddisplay.setHorizontalAlignment(SwingConstants.RIGHT);
		orderiddisplay.setForeground(Color.BLACK);
		orderiddisplay.setFont(new Font("SansSerif", Font.PLAIN, 14));

		JLabel lblNewLabel_1_2_2 = new JLabel("Payment Type:");
		lblNewLabel_1_2_2.setForeground(Color.BLACK);
		lblNewLabel_1_2_2.setFont(new Font("SansSerif", Font.PLAIN, 16));

		JLabel paymenttypedisplay = new JLabel(paymenttype);
		paymenttypedisplay.setHorizontalAlignment(SwingConstants.RIGHT);
		paymenttypedisplay.setForeground(Color.BLACK);
		paymenttypedisplay.setFont(new Font("SansSerif", Font.PLAIN, 14));

		JScrollPane scrollPane = new JScrollPane();

		JLabel lblNewLabel_4_1 = new JLabel("Total: RM" + priceformatter.format(totalprice));
		lblNewLabel_4_1.setForeground(Color.BLACK);
		lblNewLabel_4_1.setFont(new Font("SansSerif", Font.BOLD, 18));
		lblNewLabel_4_1.setBackground(Color.WHITE);

		JLabel custpaiddisplay = new JLabel("Customer paid: RM" + priceformatter.format(customerpay));
		custpaiddisplay.setFont(new Font("Tahoma", Font.PLAIN, 14));

		JLabel balancedisp = new JLabel("Balance: RM" + priceformatter.format(customerpay - totalprice));
		balancedisp.setFont(new Font("Tahoma", Font.PLAIN, 14));

		JLabel lblNewLabel_2_1 = new JLabel("Address:");
		lblNewLabel_2_1.setForeground(Color.BLACK);
		lblNewLabel_2_1.setFont(new Font("SansSerif", Font.PLAIN, 16));

		String addressline = "<html>"
				+ address.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\n", "<br/>") + "</html>";
		JLabel addressdisplay = new JLabel(addressline);
		addressdisplay.setVerticalAlignment(SwingConstants.TOP);
		addressdisplay.setForeground(Color.BLACK);
		addressdisplay.setFont(new Font("SansSerif", Font.PLAIN, 14));

		JLabel lblNewLabel_2_2 = new JLabel("Gender:");
		lblNewLabel_2_2.setForeground(Color.BLACK);
		lblNewLabel_2_2.setFont(new Font("SansSerif", Font.PLAIN, 16));

		JLabel lblGendeDisp = new JLabel(gender);
		lblGendeDisp.setForeground(Color.BLACK);
		lblGendeDisp.setFont(new Font("SansSerif", Font.PLAIN, 14));

		JLabel lblNewLabel_2_1_1 = new JLabel("Regular Customer:");
		lblNewLabel_2_1_1.setForeground(Color.BLACK);
		lblNewLabel_2_1_1.setFont(new Font("SansSerif", Font.PLAIN, 16));

		JLabel regularcustdisp = new JLabel(regularcustomer);
		regularcustdisp.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane
				.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(panel, GroupLayout.DEFAULT_SIZE, 920, Short.MAX_VALUE)
						.addGroup(gl_contentPane
								.createSequentialGroup().addContainerGap().addComponent(lblNewLabel_4).addGap(626)
								.addComponent(lblNewLabel_4_2, GroupLayout.PREFERRED_SIZE, 137,
										GroupLayout.PREFERRED_SIZE)
								.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addGroup(gl_contentPane
								.createSequentialGroup().addContainerGap()
								.addComponent(
										lblNewLabel_4_1, GroupLayout.PREFERRED_SIZE, 289, GroupLayout.PREFERRED_SIZE)
								.addContainerGap(621, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup().addContainerGap()
								.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING, false)
										.addComponent(custpaiddisplay, Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
												GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(balancedisp, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 598,
												Short.MAX_VALUE))
								.addContainerGap(312, Short.MAX_VALUE))
						.addGroup(gl_contentPane.createSequentialGroup().addContainerGap().addGroup(gl_contentPane
								.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_contentPane.createSequentialGroup()
										.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
												.addComponent(lblNewLabel_2_2, GroupLayout.PREFERRED_SIZE, 108,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(lblNewLabel_2_1, GroupLayout.PREFERRED_SIZE, 73,
														GroupLayout.PREFERRED_SIZE))
										.addPreferredGap(ComponentPlacement.UNRELATED)
										.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
												.addComponent(addressdisplay, GroupLayout.PREFERRED_SIZE, 465,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(lblGendeDisp, GroupLayout.PREFERRED_SIZE, 207,
														GroupLayout.PREFERRED_SIZE)))
								.addGroup(gl_contentPane.createSequentialGroup()
										.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
												.addComponent(lblNewLabel_2).addComponent(lblNewLabel_1))
										.addPreferredGap(ComponentPlacement.UNRELATED)
										.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
												.addComponent(phonenodisplay, GroupLayout.PREFERRED_SIZE, 207,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(namedisplay, GroupLayout.PREFERRED_SIZE, 333,
														GroupLayout.PREFERRED_SIZE))))
								.addGap(16)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
										.addGroup(gl_contentPane.createSequentialGroup().addComponent(lblNewLabel_1_2)
												.addPreferredGap(ComponentPlacement.UNRELATED)
												.addComponent(orderdatedisplay, GroupLayout.PREFERRED_SIZE, 209,
														GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_contentPane.createSequentialGroup()
												.addComponent(lblNewLabel_1_2_1, GroupLayout.PREFERRED_SIZE, 82,
														GroupLayout.PREFERRED_SIZE)
												.addPreferredGap(ComponentPlacement.RELATED)
												.addComponent(orderiddisplay, GroupLayout.PREFERRED_SIZE, 211,
														GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_contentPane.createSequentialGroup()
												.addComponent(lblNewLabel_1_2_2, GroupLayout.PREFERRED_SIZE, 113,
														GroupLayout.PREFERRED_SIZE)
												.addGap(90).addComponent(paymenttypedisplay, GroupLayout.PREFERRED_SIZE,
														96, GroupLayout.PREFERRED_SIZE)))
								.addContainerGap())
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 920, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup().addContainerGap()
								.addComponent(lblNewLabel_2_1_1, GroupLayout.PREFERRED_SIZE, 138,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.RELATED).addComponent(regularcustdisp,
										GroupLayout.PREFERRED_SIZE, 107, GroupLayout.PREFERRED_SIZE)
								.addContainerGap(659, Short.MAX_VALUE)));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING).addGroup(gl_contentPane
				.createSequentialGroup().addComponent(panel, GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(lblNewLabel_4)
						.addComponent(lblNewLabel_4_2, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(ComponentPlacement.RELATED)
				.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE).addComponent(lblNewLabel_1)
								.addComponent(namedisplay))
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(orderdatedisplay, GroupLayout.PREFERRED_SIZE, 21,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(lblNewLabel_1_2, GroupLayout.PREFERRED_SIZE, 21,
										GroupLayout.PREFERRED_SIZE)))
				.addGap(6)
				.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_contentPane.createSequentialGroup()
								.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
										.addComponent(lblNewLabel_1_2_1, GroupLayout.PREFERRED_SIZE, 21,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(orderiddisplay, GroupLayout.PREFERRED_SIZE, 21,
												GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addComponent(lblNewLabel_1_2_2, GroupLayout.PREFERRED_SIZE, 21,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(
												paymenttypedisplay, GroupLayout.PREFERRED_SIZE, 21,
												GroupLayout.PREFERRED_SIZE)))
						.addGroup(gl_contentPane.createSequentialGroup()
								.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblNewLabel_2).addComponent(phonenodisplay,
												GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
										.addComponent(lblNewLabel_2_2, GroupLayout.PREFERRED_SIZE, 21,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(lblGendeDisp, GroupLayout.PREFERRED_SIZE, 21,
												GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
										.addComponent(lblNewLabel_2_1, GroupLayout.PREFERRED_SIZE, 21,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(addressdisplay, GroupLayout.PREFERRED_SIZE, 65,
												GroupLayout.PREFERRED_SIZE))))
				.addPreferredGap(ComponentPlacement.UNRELATED)
				.addGroup(
						gl_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(lblNewLabel_2_1_1, GroupLayout.PREFERRED_SIZE, 21,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(regularcustdisp))
				.addGap(20).addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(lblNewLabel_4_1, GroupLayout.PREFERRED_SIZE, 26, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(ComponentPlacement.RELATED).addComponent(custpaiddisplay)
				.addPreferredGap(ComponentPlacement.RELATED)
				.addComponent(balancedisp, GroupLayout.PREFERRED_SIZE, 15, GroupLayout.PREFERRED_SIZE)
				.addContainerGap()));

		table = new JTable();
		table.setModel(listitemmodel);
		table.getColumnModel().getColumn(0).setPreferredWidth(20);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);
		table.getColumnModel().getColumn(2).setPreferredWidth(62);
		scrollPane.setViewportView(table);

		JLabel lblNewLabel = new JLabel("Receipt for Order ID " + orderid);
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setIcon(new ImageIcon(Receipt.class.getResource("/main/logo/receiptframe.png")));
		lblNewLabel.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup().addGap(20)
						.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 475, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(403, Short.MAX_VALUE)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.TRAILING).addGroup(Alignment.LEADING, gl_panel
				.createSequentialGroup().addGap(15).addComponent(lblNewLabel).addContainerGap(21, Short.MAX_VALUE)));
		panel.setLayout(gl_panel);
		contentPane.setLayout(gl_contentPane);

		listitemmodel.setRowCount(0);

		String querygetlistitem = "SELECT itemnumber,itemname,quantity,totalitems FROM item WHERE orderid = ?";
		try (Connection conn = Main.connect(); PreparedStatement pstmt = conn.prepareStatement(querygetlistitem)) {

			pstmt.setInt(1, orderid);
			ResultSet result = pstmt.executeQuery();

			// loop through the result set
			while (result.next()) {
				listitemmodel.addRow(new Object[] { result.getInt("itemnumber"), result.getString("itemname"),
						result.getInt("quantity"), "RM " + priceformatter.format(result.getDouble("totalitems")) });
			}
			conn.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
