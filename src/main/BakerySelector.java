package main;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.SpinnerNumberModel;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;

public class BakerySelector extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1088155671171769293L;

	DecimalFormat priceformatter = new DecimalFormat("#0.00");
	private JPanel contentPane;
	private JTable table;
	DefaultTableModel listitemmodel;
	private int orderid;
	JLabel totalpricedisplay;
	

	/**
	 * Create the frame.
	 */

	@SuppressWarnings("unchecked")
	public BakerySelector(final int orderid, boolean recoverystate) throws IOException {
		this.orderid = orderid;
		
		setIconImage(Toolkit.getDefaultToolkit().getImage(BakerySelector.class.getResource("/main/logo/logo.png")));
		setTitle("Bakery Shop");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 927, 587);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("Item");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Delete item");
		mntmNewMenuItem.setIcon(new ImageIcon(BakerySelector.class.getResource("/main/logo/multiply.png")));
		mntmNewMenuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				//DELETE ITEMS HERE
				try {	        
					String snumberitem = JOptionPane.showInputDialog(null, "Enter existence Item Number", "Delete Item Number",
							JOptionPane.INFORMATION_MESSAGE);
					int deletenumber = 0;
					boolean processstate = false;
					
					if (!(snumberitem == null)) {
						if (!snumberitem.isEmpty()) {
							try {
								deletenumber = Integer.parseInt(snumberitem);
								processstate = true;
							}catch(Exception e1) {
								System.out.println("Error CONVERT TO INT: " + e1.getMessage());
								JOptionPane.showMessageDialog(null, "Item number can only be entered in numbers", "Invalid Item Number", JOptionPane.ERROR_MESSAGE);
								processstate = false;
							}
							if(processstate == true) {
								String sqldeleteitem = "DELETE FROM item WHERE itemnumber = ? AND orderid = ?";

						        try (Connection conn = Main.connect();
						                PreparedStatement pstmt = conn.prepareStatement(sqldeleteitem)) {
						            // set the corresponding parameter
						            pstmt.setInt(1, deletenumber);
						            pstmt.setInt(2, orderid);
						            // execute the statement
						            pstmt.executeUpdate();
						            conn.close();

						        } catch (SQLException e1) {
						            System.out.println(e1.getMessage());
						        }
							}
						} else {
							JOptionPane.showMessageDialog(null, "Please enter Item Number", "Empty Item Number field",
									JOptionPane.ERROR_MESSAGE);
						}
					}
			        
					calctotalprice();
					showdata();
				}catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "Enter a valid item number", "Invalid Item Number", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		mnNewMenu.add(mntmNewMenuItem);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(239, 233, 174));
		contentPane.setBorder(null);
		setContentPane(contentPane);
		setLocationRelativeTo(null);
		setPreferredSize(new Dimension(930, 550));
		pack();

		JPanel panel = new JPanel();
		panel.setBackground(new Color(153, 51, 51));

		JScrollPane scrollPane = new JScrollPane();

		// READ ITEM FROM FILE
		BufferedReader itemlistinput = null;
		List<String> itemlist = new ArrayList<String>();
		List<String> itemlistname = new ArrayList<String>();
		List<Double> priceperitem = new ArrayList<Double>();
		
		try {
			itemlistinput = new BufferedReader(new FileReader("bakerylist.txt"));
			String bakeryitemline = null;
			itemlist.add("Choose Bakery Item");
			while ((bakeryitemline = itemlistinput.readLine()) != null) {
				String[] listitemcomma = bakeryitemline.split(",");
				itemlist.add(listitemcomma[0] + " RM"+ listitemcomma[1]);
				itemlistname.add(listitemcomma[0]);
				priceperitem.add(Double.parseDouble(listitemcomma[1]));
			}
		}
		catch (FileNotFoundException e) {
			System.err.println("Error, file didn't exist.");
		} finally {
			itemlistinput.close();
		}

		String[] itemlistArray = itemlist.toArray(new String[] {});
		String[] itemlistnameArray = itemlistname.toArray(new String[] {});
		Double[] priceperitemArray = priceperitem.toArray(new Double[] {});
		
		@SuppressWarnings("rawtypes")
		JComboBox itemcombobox = new JComboBox(itemlistArray);
		itemcombobox.setFont(new Font("Arial", Font.PLAIN, 17));

		JSpinner quantity = new JSpinner();
		quantity.setFont(new Font("Arial", Font.BOLD, 14));
		quantity.setModel(new SpinnerNumberModel(1, 1, null, 1));
		

		JButton btnNewButton = new JButton("Add");
		btnNewButton.setFont(new Font("Arial", Font.PLAIN, 13));
		btnNewButton.setBackground(new Color(255, 153, 51));
		btnNewButton.addMouseListener(new MouseAdapter() {
			int lastitemnumber = 1;
			@Override
			public void mouseClicked(MouseEvent e) {
				//ADD ITEMS TO LIST ORDERS FOR CUSTOMER
				int selecteditem = 0;
				int quantityno;
				double totalitemsprice = 0;
				
				selecteditem = itemcombobox.getSelectedIndex();
				
				try {
					if(selecteditem != 0) {
						if(table.getRowCount() > 0) {
							lastitemnumber = (int) table.getModel().getValueAt(table.getRowCount() - 1, 0) + 1;
						}else {
							lastitemnumber = 1;
						}
						
						//CALCULATE PRICE FOR SELECTED ITEM AND QUANTITY
						quantityno = (int) quantity.getValue();
						totalitemsprice = priceperitemArray[selecteditem - 1] * quantityno;
						
						String insertnewitem = "INSERT INTO item(itemname,itemnumber,quantity,totalitems,orderid) VALUES (?,?,?,?,?)";
						try (Connection conn = Main.connect();
								PreparedStatement pstmt = conn.prepareStatement(insertnewitem)) {
							pstmt.setString(1, String.valueOf(itemlistnameArray[selecteditem - 1]));
							pstmt.setInt(2, lastitemnumber);
							pstmt.setInt(3, quantityno);
							pstmt.setDouble(4, totalitemsprice);
							pstmt.setInt(5, orderid);
							pstmt.executeUpdate();
							conn.close();
							
						} catch (SQLException e1) {
							System.out.println("SQL ERROR: " + e1.getMessage());
						} catch (Exception e1) {
							System.out.println(e1.getMessage());
						}						
						quantity.setValue(1); // RESET FIELD TO 1 quantity
						showdata(); // UPDATE TABLE
					}else {
						JOptionPane.showMessageDialog(null, "Please select item", "No item selected", JOptionPane.ERROR_MESSAGE);
					}
				}catch(Exception error) {
					System.out.println("Error: " + error);
				}
				calctotalprice();
			}
		});
		
		JLabel lblNewLabel = new JLabel("Items");
		lblNewLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		
		JLabel lblNewLabel_1 = new JLabel("Quantity");
		lblNewLabel_1.setFont(new Font("Arial", Font.PLAIN, 14));
		
		JButton btnNewButton_1 = new JButton("Save");
		btnNewButton_1.setIcon(new ImageIcon(BakerySelector.class.getResource("/main/logo/save.png")));
		btnNewButton_1.setFont(new Font("Arial", Font.PLAIN, 15));
		btnNewButton_1.setBackground(new Color(239, 149, 157));
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dispose();
			}
		});
		
		totalpricedisplay = new JLabel("Total Price: RM 0.00");
		totalpricedisplay.setHorizontalAlignment(SwingConstants.RIGHT);
		totalpricedisplay.setFont(new Font("Arial", Font.BOLD, 17));
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addComponent(panel, GroupLayout.DEFAULT_SIZE, 911, Short.MAX_VALUE)
				.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
					.addGap(8)
					.addComponent(lblNewLabel, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(itemcombobox, GroupLayout.PREFERRED_SIZE, 505, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(lblNewLabel_1)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(quantity, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(215, Short.MAX_VALUE)
					.addComponent(totalpricedisplay, GroupLayout.PREFERRED_SIZE, 488, GroupLayout.PREFERRED_SIZE)
					.addGap(18)
					.addComponent(btnNewButton_1, GroupLayout.PREFERRED_SIZE, 180, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
				.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 911, Short.MAX_VALUE)
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 73, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
							.addComponent(quantity, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblNewLabel_1)
						.addComponent(itemcombobox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(scrollPane, GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnNewButton_1, GroupLayout.PREFERRED_SIZE, 40, GroupLayout.PREFERRED_SIZE)
						.addComponent(totalpricedisplay, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
					.addGap(6))
		);

		table = new JTable();
		listitemmodel = new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"Item Number", "Item Name", "Quantity", "Price"
				}
			){
			/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int rowIndex, int columnIndex) {
			    return false;
			}
			};
		
		table.setModel(listitemmodel);
		
		table.getColumnModel().getColumn(0).setPreferredWidth(20);
		table.getColumnModel().getColumn(1).setPreferredWidth(150);
		table.getColumnModel().getColumn(2).setPreferredWidth(62);
		scrollPane.setViewportView(table);

		JLabel lblNewLabel_2 = new JLabel("Items for order ID: " + orderid);

		lblNewLabel_2.setIcon(new ImageIcon(BakerySelector.class.getResource("/main/logo/card-payment.png")));
		lblNewLabel_2.setForeground(Color.WHITE);
		lblNewLabel_2.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup().addGap(18)
						.addComponent(lblNewLabel_2, GroupLayout.PREFERRED_SIZE, 358, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(33, Short.MAX_VALUE)));
		gl_panel.setVerticalGroup(gl_panel.createParallelGroup(Alignment.LEADING).addGroup(Alignment.TRAILING, gl_panel
				.createSequentialGroup().addContainerGap(21, Short.MAX_VALUE).addComponent(lblNewLabel_2).addGap(20)));
		panel.setLayout(gl_panel);
		contentPane.setLayout(gl_contentPane);
		
		showdata();
		
		if(recoverystate == true) {
			double listpricecust = 0;
			String querygetpriceitem = "SELECT totalitems FROM item WHERE orderid='"+orderid+"'";
			try (Connection conn = Main.connect();
					Statement stmt = conn.createStatement();
					ResultSet result = stmt.executeQuery(querygetpriceitem)) {

				// loop through the result set
				while (result.next()) {
					listpricecust = listpricecust + result.getDouble("totalitems");
				}
				conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
			totalpricedisplay.setText("Total Price: RM " + priceformatter.format(listpricecust));
		}
	}
	
	private void calctotalprice() {
		double listpricecust = 0;
		String querygetpriceitem = "SELECT totalitems FROM item WHERE orderid='"+orderid+"'";
		try (Connection conn = Main.connect();
				Statement stmt = conn.createStatement();
				ResultSet result = stmt.executeQuery(querygetpriceitem)) {

			// loop through the result set
			while (result.next()) {
				listpricecust = listpricecust + result.getDouble("totalitems");
			}
			conn.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		totalpricedisplay.setText("Total Price: RM " + priceformatter.format(listpricecust));
		NewOrderwindow.calctotalprice(listpricecust);
	}
	
	private void showdata() {
		//ADD DATA HERE
		listitemmodel.setRowCount(0);
		String querygetlistitem = "SELECT itemnumber, itemname, quantity, totalitems FROM item WHERE orderid = '"+orderid+"'";
		try (Connection conn = Main.connect();
				Statement stmt = conn.createStatement();
				ResultSet result = stmt.executeQuery(querygetlistitem)) {

			// loop through the result set
			while (result.next()) {
				listitemmodel.addRow(new Object[]{result.getInt("itemnumber"), result.getString("itemname"), result.getInt("quantity"), "RM " + priceformatter.format(result.getDouble("totalitems"))});
			}
			conn.close();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}
