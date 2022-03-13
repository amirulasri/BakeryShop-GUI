package main;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JCheckBox;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JRadioButton;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class NewOrderwindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -331137392899726080L;
	static DecimalFormat priceformatter = new DecimalFormat("#0.00");
	DecimalFormat discountnumber = new DecimalFormat("#0");

	private JPanel contentPane;
	public JLabel lblNewLabel_2;

	static double listpricecust = 0;
	static double finalprice = 0;
	static private JLabel titletotalprice;
	static private JLabel totalpricedisplay;
	static Paymentwindow paymentframe = null;

	static String gender = "";
	static boolean regularcustomer = false;

	/**
	 * Create the frame.
	 * 
	 * @throws IOException
	 */

	// SAVE FOR AUTORECOVERY IF POWER LOST ETC
	private void saveautorecovery(String customername, String phoneno, String address, String gender,
			boolean regularcustomer) {
		if(customername.isEmpty()) {
			customername = "nodata";
		}
		if(phoneno.isEmpty()) {
			phoneno = "nodata";
		}
		if(address.isEmpty()) {
			address = "nodata";
		}
		if(gender.isEmpty()) {
			gender = "nodata";
		}
		try {
			FileWriter recoveryfile = new FileWriter("autorecover/autorecovery.txt");
			PrintWriter recoverywriter = new PrintWriter(recoveryfile);
			recoverywriter.print(customername + ";" + phoneno + ";" + address.replace("\n", "\\n") + ";" + gender + ";"
					+ regularcustomer);
			recoverywriter.close();
		} catch (Exception e) {
			System.out.println("ERROR SAVE RECOVERY: " + e.getMessage());
		}
	}

	static public void calctotalprice(double totalprice) {
		listpricecust = totalprice;
		if (regularcustomer == true) {
			totalprice = totalprice - (totalprice * Main.getdiscountvalue());
		}
		totalpricedisplay.setText("RM " + priceformatter.format(totalprice));
		finalprice = totalprice;
	}

	static public void setpaymentframenull() {
		paymentframe = null;
	}

	private boolean containsOrderId(final int orderid) {
		boolean duplicatestate = false;
		String checkid = "SELECT orderid FROM customer WHERE orderid='" + orderid + "'";
		try (Connection conn = Main.connect();
				Statement stmt = conn.createStatement();
				ResultSet result = stmt.executeQuery(checkid)) {

			// loop through the result set
			while (result.next()) {
				duplicatestate = true;
			}
			conn.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return duplicatestate;
	}

	public NewOrderwindow(int orderid, boolean recoverystate) throws IOException {
		BakerySelector itemselector;
		if(recoverystate == true) {
			itemselector = new BakerySelector(orderid, true);
		}else {
			itemselector = new BakerySelector(orderid, false);
		}
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				itemselector.dispose();
				Cashierwindow.getbuttoncreate().setEnabled(true);
			}

			@Override
			public void windowClosing(WindowEvent e) {
				String selectorbutton[] = { "Yes", "No" };
				int PromptResult = JOptionPane.showOptionDialog(null,
						"Are you sure you want to exit?. This order will be discarded.", "Exit Order Window",
						JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, selectorbutton,
						selectorbutton[1]);
				if (PromptResult == JOptionPane.YES_OPTION) {
					// SQL FOR DELETING ORDER
					String sqlonfk = "PRAGMA foreign_keys=on;";
					String sqldeleteorder = "DELETE FROM orders WHERE id = ?";

					try (Connection conn = Main.connect();
							Statement stmt = conn.createStatement();
							PreparedStatement pstmt = conn.prepareStatement(sqldeleteorder)) {

						stmt.execute(sqlonfk);
						// set the corresponding parameter
						pstmt.setInt(1, orderid);
						// execute the statement
						pstmt.executeUpdate();
						conn.close();

					} catch (SQLException e1) {
						System.out.println(e1.getMessage());
					}
					Cashierwindow.showdata();
					if (paymentframe != null) {
						paymentframe.dispose();
						paymentframe = null;
					}
					dispose();
					listpricecust = 0;
					finalprice = 0;
					regularcustomer = false;
					gender = "";
					Cashierwindow.setorderframenull();
					System.out.println("SQL orders DELETED");

					// DELETE RECOVERY FILE
					try {
						File recoveryfile = new File("autorecover/autorecovery.txt");
						if (recoveryfile.exists()) {
							recoveryfile.delete();
						}
					} catch (Exception e1) {
						System.out.println("Error: " + e1.getMessage());
					}
				}
			}
		});

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
		setTitle("Bakery Shop");
		setIconImage(Toolkit.getDefaultToolkit().getImage(NewOrderwindow.class.getResource("/main/logo/logo.png")));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 1019, 549);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(250, 243, 221));
		contentPane.setBorder(null);
		setContentPane(contentPane);
		setLocationRelativeTo(null);
		setPreferredSize(new Dimension(1000, 550));
		pack();

		JPanel panel = new JPanel();
		panel.setBackground(new Color(59, 80, 107));

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(new Color(153, 204, 204));

		JPanel panel_2 = new JPanel();
		panel_2.setBackground(new Color(140, 47, 57));
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGap(0, 155, Short.MAX_VALUE)
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGap(0, 513, Short.MAX_VALUE)
		);
		panel_2.setLayout(gl_panel_2);

		JTextField custnamefield = new JTextField();
		custnamefield.setColumns(10);

		JTextField phonenofield = new JTextField();
		phonenofield.setColumns(10);

		JTextArea addressfield = new JTextArea();
		addressfield.setFont(new Font("Tahoma", Font.PLAIN, 11));

		// CHECK IF CUSTOMER ID REGULAR
		JCheckBox regularcustomercheck = new JCheckBox("Yes");
		regularcustomercheck.setBackground(new Color(204, 255, 255));
		regularcustomercheck.setFont(new Font("SansSerif", Font.PLAIN, 15));
		regularcustomercheck.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (regularcustomercheck.isSelected()) {
					regularcustomer = true;
				} else {
					regularcustomer = false;
				}

				if (regularcustomer == true) {
					titletotalprice.setText("Total Price with discount "
							+ discountnumber.format((Main.getdiscountvalue() * 100)) + "%");
					double totalwithdiscount = listpricecust - (listpricecust * Main.getdiscountvalue());
					totalpricedisplay.setText("RM " + priceformatter.format(totalwithdiscount));
					finalprice = totalwithdiscount;
				} else {
					titletotalprice.setText("Total Price");
					totalpricedisplay.setText("RM " + priceformatter.format(listpricecust));
					finalprice = listpricecust;
				}

				saveautorecovery(custnamefield.getText(), phonenofield.getText(), addressfield.getText(), gender,
						regularcustomer);
			}
		});

		JScrollPane scrollPane = new JScrollPane();

		JRadioButton malevalueradio = new JRadioButton("Male");
		malevalueradio.setBackground(new Color(204, 255, 255));
		malevalueradio.setFont(new Font("Arial", Font.PLAIN, 15));
		JRadioButton femalevalueradio = new JRadioButton("Female");
		femalevalueradio.setBackground(new Color(204, 255, 255));
		femalevalueradio.setFont(new Font("Arial", Font.PLAIN, 15));

		malevalueradio.setActionCommand("Male");
		femalevalueradio.setActionCommand("Female");

		ButtonGroup genderselector = new ButtonGroup();
		genderselector.add(malevalueradio);
		genderselector.add(femalevalueradio);

		malevalueradio.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				// ERROR HANDLING FOR RADIO GET ACTION COMMAND
				try {
					gender = genderselector.getSelection().getActionCommand();
				} catch (Exception e1) {
					System.out.println(e1.getMessage());
				}
				saveautorecovery(custnamefield.getText(), phonenofield.getText(), addressfield.getText(), gender,
						regularcustomer);
			}
		});

		femalevalueradio.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				// ERROR HANDLING FOR RADIO GET ACTION COMMAND
				try {
					gender = genderselector.getSelection().getActionCommand();
				} catch (Exception e1) {
					System.out.println(e1.getMessage());
				}
				saveautorecovery(custnamefield.getText(), phonenofield.getText(), addressfield.getText(), gender,
						regularcustomer);
			}
		});

		JButton btnNewButton_1 = new JButton("Add Items");
		btnNewButton_1.setBackground(new Color(51, 204, 255));
		btnNewButton_1.setFont(new Font("Arial", Font.PLAIN, 14));
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				itemselector.setVisible(true);
			}
		});

		titletotalprice = new JLabel("Total Price:");
		titletotalprice.setFont(new Font("Arial", Font.PLAIN, 16));
		titletotalprice.setForeground(Color.BLACK);

		totalpricedisplay = new JLabel("RM 0.00");
		totalpricedisplay.setFont(new Font("Arial Black", Font.PLAIN, 19));
		
				JLabel lblNewLabel_1 = new JLabel("Customer Name");
				lblNewLabel_1.setFont(new Font("Arial", Font.PLAIN, 14));
		
				JLabel lblNewLabel_3 = new JLabel("Phone Number");
				lblNewLabel_3.setFont(new Font("Arial", Font.PLAIN, 14));
		
				JLabel lblNewLabel = new JLabel("Address");
				lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		
				JLabel lblNewLabel_6 = new JLabel("Gender");
				lblNewLabel_6.setFont(new Font("Arial", Font.PLAIN, 14));
		
				JLabel lblNewLabel_4 = new JLabel("Items");
				lblNewLabel_4.setFont(new Font("Arial", Font.PLAIN, 14));
		
				JLabel lblNewLabel_5 = new JLabel("Regular Customer");
				lblNewLabel_5.setFont(new Font("Arial", Font.PLAIN, 14));
		
		JPanel panel_3 = new JPanel();
		panel_3.setOpaque(false);

		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 18, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(custnamefield, GroupLayout.DEFAULT_SIZE, 969, Short.MAX_VALUE)
						.addComponent(lblNewLabel_1)
						.addComponent(lblNewLabel_3)
						.addComponent(phonenofield, GroupLayout.DEFAULT_SIZE, 969, Short.MAX_VALUE)
						.addComponent(lblNewLabel)
						.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 969, Short.MAX_VALUE)
						.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_panel_1.createSequentialGroup()
								.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
									.addGroup(gl_panel_1.createSequentialGroup()
										.addComponent(malevalueradio)
										.addPreferredGap(ComponentPlacement.UNRELATED)
										.addComponent(femalevalueradio))
									.addComponent(btnNewButton_1, GroupLayout.PREFERRED_SIZE, 188, GroupLayout.PREFERRED_SIZE))
								.addGap(143)
								.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
									.addComponent(titletotalprice)
									.addComponent(totalpricedisplay)
									.addComponent(regularcustomercheck, GroupLayout.PREFERRED_SIZE, 117, GroupLayout.PREFERRED_SIZE)
									.addComponent(lblNewLabel_5, GroupLayout.PREFERRED_SIZE, 153, GroupLayout.PREFERRED_SIZE)))
							.addComponent(lblNewLabel_6)
							.addComponent(lblNewLabel_4)))
					.addContainerGap())
				.addGroup(Alignment.TRAILING, gl_panel_1.createSequentialGroup()
					.addContainerGap(687, Short.MAX_VALUE)
					.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 320, GroupLayout.PREFERRED_SIZE))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(custnamefield, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblNewLabel_3)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(phonenofield, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 58, GroupLayout.PREFERRED_SIZE)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGap(18)
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_1.createSequentialGroup()
									.addComponent(lblNewLabel_6)
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
										.addComponent(malevalueradio)
										.addComponent(femalevalueradio)))
								.addGroup(gl_panel_1.createSequentialGroup()
									.addGap(5)
									.addComponent(lblNewLabel_5)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(regularcustomercheck)))
							.addGap(18)
							.addComponent(lblNewLabel_4)
							.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_1.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(btnNewButton_1, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_panel_1.createSequentialGroup()
									.addGap(10)
									.addComponent(titletotalprice)
									.addPreferredGap(ComponentPlacement.RELATED)
									.addComponent(totalpricedisplay))))
						.addGroup(gl_panel_1.createSequentialGroup()
							.addGap(36)
							.addComponent(panel_3, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)))
					.addGap(130))
				.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 544, Short.MAX_VALUE)
		);
		panel_3.setLayout(null);
		
				JButton btnNewButton = new JButton("Pay");
				btnNewButton.setBounds(142, 102, 168, 45);
				panel_3.add(btnNewButton);
				btnNewButton.setIcon(new ImageIcon(NewOrderwindow.class.getResource("/main/logo/payment-method.png")));
				btnNewButton.setBackground(new Color(102, 102, 255));
				btnNewButton.setFont(new Font("Arial", Font.PLAIN, 17));
				btnNewButton.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e) {
						String customername = custnamefield.getText();
						String phoneno = phonenofield.getText();
						String address = addressfield.getText();
						boolean regularcustomer = false;
						if (regularcustomercheck.isSelected()) {
							regularcustomer = true;
						}

						// ERROR HANDLING FOR RADIO GET ACTION COMMAND
						try {
							gender = genderselector.getSelection().getActionCommand();
						} catch (Exception e1) {
							System.out.println(e1.getMessage());
						}

						// PROCESS STATE
						boolean process = false;

						// ERROR STATE
						boolean customernameerror = false;
						boolean phonenoerror = false;
						boolean addresserror = false;
						boolean gendererror = false;
						boolean quantityerror = false;

						// CHECK NAME
						if (customername.isEmpty()) {
							customernameerror = true;
						}

						// CHECK PHONE NO
						if (phoneno.isEmpty()) {
							phonenoerror = true;
						}

						// CHECK ADDRESS
						if (address.isEmpty()) {
							addresserror = true;
						}

						// CHECK GENDER
						if (gender.isEmpty()) {
							gendererror = true;
						}

						// CHECK IF ITEM ADDED
						int quantitycount = 0;
						String checkid = "SELECT orderid,itemnumber,quantity FROM item WHERE orderid='" + orderid + "'";
						try (Connection conn = Main.connect();
								Statement stmt = conn.createStatement();
								ResultSet result = stmt.executeQuery(checkid)) {

							// loop through the result set
							while (result.next()) {
								quantitycount = quantitycount + result.getInt("quantity");
							}
							conn.close();
						} catch (SQLException e1) {
							System.out.println(e1.getMessage());
						}

						// CHECK QUANTITY ITEMS ADDED
						if (quantitycount == 0) {
							quantityerror = true;
						}

						// ERROR MESSAGE
						if (customernameerror || phonenoerror || addresserror || gendererror || quantityerror) {
							String error = "Check your required field:";
							if (customernameerror) {
								error += "\nName is empty";
							}
							if (phonenoerror) {
								error += "\nPhone number is empty";
							}
							if (addresserror) {
								error += "\nAddress is empty";
							}
							if (gendererror) {
								error += "\nGender is empty";
							}
							if (quantityerror) {
								error += "\nItems is empty";
							}
							JOptionPane.showMessageDialog(null, error, "Error. ID: " + orderid, JOptionPane.ERROR_MESSAGE);
						} else {
							process = true;
						}

						// IF TRUE, SAVE THE RECORD
						if (process == true) {
							boolean duplicateorderid = containsOrderId(orderid);
							String insertnewcust = "INSERT INTO customer(name,phoneno,address,gender,regularcustomer,orderid) VALUES (?,?,?,?,?,?)";
							if (duplicateorderid) {
								if (paymentframe == null) {
									if(recoverystate == true) {										
										paymentframe = new Paymentwindow(orderid, finalprice, true);
									}else {
										paymentframe = new Paymentwindow(orderid, finalprice, false);
									}
									paymentframe.setVisible(true);
								} else {
									paymentframe.setVisible(true);
								}
							} else {
								try (Connection conn = Main.connect();
										PreparedStatement pstmt = conn.prepareStatement(insertnewcust)) {
									pstmt.setString(1, customername);
									pstmt.setString(2, phoneno);
									pstmt.setString(3, address);
									pstmt.setString(4, gender);
									pstmt.setBoolean(5, regularcustomer);
									pstmt.setInt(6, orderid);
									pstmt.executeUpdate();

								} catch (SQLException e1) {
									System.out.println(e1.getMessage());
								} catch (Exception e1) {
									System.out.println(e1.getMessage());
								}
								if (paymentframe == null) {
									if(recoverystate == true) {										
										paymentframe = new Paymentwindow(orderid, finalprice, true);
									}else {
										paymentframe = new Paymentwindow(orderid, finalprice, false);
									}
									paymentframe.setVisible(true);
								} else {
									paymentframe.setVisible(true);
								}
							}
						}
					}
				});

		scrollPane.setViewportView(addressfield);
		panel_1.setLayout(gl_panel_1);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 1007, Short.MAX_VALUE)
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 416, Short.MAX_VALUE)
					.addContainerGap())
		);

		lblNewLabel_2 = new JLabel("New order for ID: " + orderid);
		lblNewLabel_2.setIcon(new ImageIcon(NewOrderwindow.class.getResource("/main/logo/notepad.png")));
		lblNewLabel_2.setForeground(Color.WHITE);
		lblNewLabel_2.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup().addGap(20)
						.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 808, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(179, Short.MAX_VALUE)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(gl_panel
				.createSequentialGroup().addGap(21).addComponent(lblNewLabel_2).addContainerGap(19, Short.MAX_VALUE)));
		panel.setLayout(gl_panel);
		contentPane.setLayout(gl_contentPane);

		// AUTO SAVE RECOVERY
		custnamefield.getDocument().addDocumentListener((DocumentListener) new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				saveautorecovery(custnamefield.getText(), phonenofield.getText(), addressfield.getText(), gender,
						regularcustomer);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				saveautorecovery(custnamefield.getText(), phonenofield.getText(), addressfield.getText(), gender,
						regularcustomer);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				saveautorecovery(custnamefield.getText(), phonenofield.getText(), addressfield.getText(), gender,
						regularcustomer);
			}
		});

		phonenofield.getDocument().addDocumentListener((DocumentListener) new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				saveautorecovery(custnamefield.getText(), phonenofield.getText(), addressfield.getText(), gender,
						regularcustomer);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				saveautorecovery(custnamefield.getText(), phonenofield.getText(), addressfield.getText(), gender,
						regularcustomer);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				saveautorecovery(custnamefield.getText(), phonenofield.getText(), addressfield.getText(), gender,
						regularcustomer);
			}
		});

		addressfield.getDocument().addDocumentListener((DocumentListener) new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				saveautorecovery(custnamefield.getText(), phonenofield.getText(), addressfield.getText(), gender,
						regularcustomer);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				saveautorecovery(custnamefield.getText(), phonenofield.getText(), addressfield.getText(), gender,
						regularcustomer);
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				saveautorecovery(custnamefield.getText(), phonenofield.getText(), addressfield.getText(), gender,
						regularcustomer);
			}
		});

		//AUTO RECOVER BACK
		if (recoverystate == true) {
			try {							
				FileReader fr = new FileReader("autorecover/autorecovery.txt");
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
				if(line != null) {
					StringTokenizer st = new StringTokenizer(line, ";");	
					String customername = st.nextToken();
					String phoneno = st.nextToken();
					String address = st.nextToken();
					String gender = st.nextToken();
					
					if(!customername.equalsIgnoreCase("nodata")) {						
						custnamefield.setText(customername);
					}
					if(!phoneno.equalsIgnoreCase("nodata")) {						
						phonenofield.setText("nodata");
					}
					if(!address.equalsIgnoreCase("nodata")) {						
						addressfield.setText(address.replace("\\n", "\n"));
					}
					if(!gender.equalsIgnoreCase("nodata")) {						
						if(gender.equalsIgnoreCase("Male")) {
							malevalueradio.setSelected(true);
						} else if(gender.equalsIgnoreCase("Female")) {
							femalevalueradio.setSelected(true);
						}
					}
					String regularcuststatecheck = st.nextToken();
					if(regularcuststatecheck.equalsIgnoreCase("true")) {
						regularcustomercheck.setSelected(true);
					}
				}
				br.close();
				
				//GET TOTALPRICE
				double listpricecust2 = 0;
				String querygetpriceitem = "SELECT totalitems FROM item WHERE orderid='"+orderid+"'";
				try (Connection conn = Main.connect();
						Statement stmt = conn.createStatement();
						ResultSet result = stmt.executeQuery(querygetpriceitem)) {

					// loop through the result set
					while (result.next()) {
						listpricecust2 = listpricecust2 + result.getDouble("totalitems");
					}
					conn.close();
					
					listpricecust = listpricecust2;
					if (regularcustomer == true) {
						listpricecust2 = listpricecust2 - (listpricecust2 * Main.getdiscountvalue());
					}
					totalpricedisplay.setText("RM " + priceformatter.format(listpricecust2));
					finalprice = listpricecust2;
					
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}
				
			}catch(Exception e1) {
				System.out.println("Error recover: " + e1.getMessage());
			}
		}
	}
}
