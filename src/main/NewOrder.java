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
import java.awt.Toolkit;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

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

public class NewOrder extends JFrame {

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
	static Payment paymentframe = null;
	
	static boolean regularcustomer = false;

	/**
	 * Create the frame.
	 * 
	 * @throws IOException
	 */
	
	//SAVE FOR AUTORECOVERY IF POWER LOST ETC
	private void saveautorecovery() {
		try {
			FileWriter recoveryfile = new FileWriter("autorecover/autorecovery.txt");
			PrintWriter recoverywriter = new PrintWriter(recoveryfile);
			recoverywriter.print("ok");
			recoverywriter.close();
		}catch(Exception e) {
			
		}
	}
	
	static public void calctotalprice(double totalprice) {
		listpricecust = totalprice;
		if(regularcustomer == true) {
			totalprice = totalprice - (totalprice*Main.getdiscountvalue());
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

	public NewOrder(int orderid) throws IOException {
		ItemSelector itemselector = new ItemSelector(orderid);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				itemselector.dispose();
				Cashierframe.getbuttoncreate().setEnabled(true);
			}

			@Override
			public void windowClosing(WindowEvent e) {
				String selectorbutton[] = { "Yes", "No" };
				int PromptResult = JOptionPane.showOptionDialog(null,
						"Are you sure you want to exit?. This order will be discarded.", "Exit Order Window",
						JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, selectorbutton,
						selectorbutton[1]);
				if (PromptResult == JOptionPane.YES_OPTION) {
					//SQL FOR DELETING ORDER
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
			        Cashierframe.showdata();
			        if(paymentframe != null) {						
						paymentframe.dispose();
						paymentframe = null;
					}
					dispose();
					listpricecust = 0;
					finalprice = 0;
					regularcustomer = false;
					Cashierframe.setorderframenull();
					System.out.println("SQL orders DELETED");
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
		setTitle(Main.getappname());
		setIconImage(Toolkit.getDefaultToolkit().getImage(NewOrder.class.getResource("/main/logo/logo.png")));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 1023, 587);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(250, 243, 221));
		contentPane.setBorder(null);
		setContentPane(contentPane);
		setLocationRelativeTo(null);

		JPanel panel = new JPanel();
		panel.setBackground(new Color(74, 124, 89));

		JPanel panel_1 = new JPanel();
		panel_1.setBackground(new Color(104, 176, 171));

		JPanel panel_2 = new JPanel();
		panel_2.setBackground(new Color(200, 213, 185));

		JLabel lblNewLabel_1 = new JLabel("Customer Name");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));

		JLabel lblNewLabel_3 = new JLabel("Phone Number");
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 14));

		JLabel lblNewLabel_4 = new JLabel("Items");
		lblNewLabel_4.setFont(new Font("Tahoma", Font.PLAIN, 14));

		JLabel lblNewLabel = new JLabel("Address");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));

		JLabel lblNewLabel_5 = new JLabel("Regular Customer");
		lblNewLabel_5.setFont(new Font("Tahoma", Font.PLAIN, 14));

		JLabel lblNewLabel_6 = new JLabel("Gender");
		lblNewLabel_6.setFont(new Font("Tahoma", Font.PLAIN, 14));
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup().addContainerGap()
						.addGroup(gl_panel_2.createParallelGroup(Alignment.LEADING).addComponent(lblNewLabel_1)
								.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING, false)
										.addComponent(lblNewLabel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
												GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(lblNewLabel_3, Alignment.LEADING, GroupLayout.DEFAULT_SIZE,
												GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addComponent(lblNewLabel_5).addComponent(lblNewLabel_4).addComponent(lblNewLabel_6))
						.addContainerGap(64, Short.MAX_VALUE)));
		gl_panel_2.setVerticalGroup(gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup().addGap(45).addComponent(lblNewLabel_1).addGap(18)
						.addComponent(lblNewLabel_3).addGap(18).addComponent(lblNewLabel).addGap(62)
						.addComponent(lblNewLabel_6).addPreferredGap(ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
						.addComponent(lblNewLabel_4).addGap(37).addComponent(lblNewLabel_5).addGap(57)));
		panel_2.setLayout(gl_panel_2);

		JTextField custnamefield = new JTextField();
		custnamefield.setColumns(10);
		custnamefield.getDocument().addDocumentListener((DocumentListener) new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				saveautorecovery();
			}
			@Override
			public void removeUpdate(DocumentEvent e) {
				saveautorecovery();
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
				saveautorecovery();
			}
			});

		JTextField phonenofield = new JTextField();
		phonenofield.setColumns(10);

		JTextArea addressfield = new JTextArea();
		addressfield.setFont(new Font("Tahoma", Font.PLAIN, 11));

		//CHECK IF CUSTOMER ID REGULAR
		JCheckBox regularcustomercheck = new JCheckBox("Yes");
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
			}
		});

		JScrollPane scrollPane = new JScrollPane();

		JRadioButton malevalueradio = new JRadioButton("Male");
		JRadioButton femalevalueradio = new JRadioButton("Female");
		malevalueradio.setActionCommand("Male");
		femalevalueradio.setActionCommand("Female");

		ButtonGroup genderselector = new ButtonGroup();
		genderselector.add(malevalueradio);
		genderselector.add(femalevalueradio);

		JButton btnNewButton = new JButton("Pay");
		btnNewButton.setFont(new Font("SansSerif", Font.PLAIN, 17));
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String customername = custnamefield.getText();
				String phoneno = phonenofield.getText();
				String address = addressfield.getText();
				String gender = "";
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
						if(paymentframe == null) {						
							paymentframe = new Payment(orderid, finalprice);
							paymentframe.setVisible(true);
						}else {
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
						if(paymentframe == null) {						
							paymentframe = new Payment(orderid, finalprice);
							paymentframe.setVisible(true);
						}else {
							paymentframe.setVisible(true);
						}
					}
				}
			}
		});

		JButton btnNewButton_1 = new JButton("Open Order Item Manager");
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				itemselector.setVisible(true);
			}
		});

		titletotalprice = new JLabel("Total Price:");
		titletotalprice.setFont(new Font("SansSerif", Font.BOLD, 16));
		titletotalprice.setForeground(Color.BLACK);

		totalpricedisplay = new JLabel("RM 0.00");
		totalpricedisplay.setFont(new Font("SansSerif", Font.BOLD, 19));

		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(gl_panel_1.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_1
				.createSequentialGroup()
				.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGroup(gl_panel_1.createParallelGroup(Alignment.TRAILING).addGroup(gl_panel_1.createSequentialGroup()
						.addGap(18)
						.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 687, Short.MAX_VALUE)
								.addComponent(custnamefield, GroupLayout.DEFAULT_SIZE, 687, Short.MAX_VALUE)
								.addComponent(phonenofield, GroupLayout.DEFAULT_SIZE, 687, Short.MAX_VALUE)
								.addGroup(gl_panel_1.createSequentialGroup().addComponent(btnNewButton_1).addGap(131)
										.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
												.addComponent(totalpricedisplay).addComponent(titletotalprice)))
								.addGroup(gl_panel_1.createSequentialGroup().addComponent(malevalueradio)
										.addPreferredGap(ComponentPlacement.UNRELATED).addComponent(femalevalueradio))
								.addComponent(regularcustomercheck)))
						.addGroup(gl_panel_1.createSequentialGroup().addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 169,
										GroupLayout.PREFERRED_SIZE)))
				.addContainerGap()));
		gl_panel_1
				.setVerticalGroup(
						gl_panel_1
								.createParallelGroup(
										Alignment.LEADING)
								.addGroup(gl_panel_1.createSequentialGroup().addGap(42)
										.addComponent(custnamefield, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addGap(18)
										.addComponent(phonenofield, GroupLayout.PREFERRED_SIZE,
												GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
										.addGap(18)
										.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 58,
												GroupLayout.PREFERRED_SIZE)
										.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING).addGroup(gl_panel_1
												.createSequentialGroup().addGap(18)
												.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
														.addComponent(malevalueradio).addComponent(femalevalueradio))
												.addGap(29).addComponent(btnNewButton_1).addGap(31)
												.addComponent(regularcustomercheck))
												.addGroup(gl_panel_1.createSequentialGroup().addGap(52)
														.addComponent(titletotalprice)
														.addPreferredGap(ComponentPlacement.RELATED)
														.addComponent(totalpricedisplay).addGap(50)
														.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 35,
																GroupLayout.PREFERRED_SIZE)))
										.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE));

		scrollPane.setViewportView(addressfield);
		panel_1.setLayout(gl_panel_1);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 899, Short.MAX_VALUE).addGap(108)));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 72, GroupLayout.PREFERRED_SIZE).addGap(39)
						.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 376, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(61, Short.MAX_VALUE)));

		lblNewLabel_2 = new JLabel("New order for ID: " + orderid);
		lblNewLabel_2.setIcon(new ImageIcon(NewOrder.class.getResource("/main/logo/contract.png")));
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
	}
}
