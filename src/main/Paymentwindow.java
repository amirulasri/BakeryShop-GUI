package main;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import java.awt.Color;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import javax.swing.ImageIcon;
import java.awt.Toolkit;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Paymentwindow extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5779497443670977835L;

	static DecimalFormat priceformatter = new DecimalFormat("#0.00");

	private JPanel contentPane;
	private JTextField custpayfield;

	/**
	 * Create the frame.
	 */
	public Paymentwindow(int orderid, double payment, boolean recoverystate) {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				String selectorbutton[] = {"Yes","No"};
		        int PromptResult = JOptionPane.showOptionDialog(null,"Cancel payment? You order data still keep until you close Order window. Make a payment by click Pay button on Order window","Exit Payment Window",JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE,null,selectorbutton,selectorbutton[1]);
		        if(PromptResult==JOptionPane.YES_OPTION)
		        {
		            dispose();
		            NewOrderwindow.setpaymentframenull();
		        }
			}
		});
		setIconImage(Toolkit.getDefaultToolkit().getImage(Paymentwindow.class.getResource("/main/logo/logo.png")));
		setTitle("Bakery Shop");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 844, 479);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(157, 129, 137));
		setContentPane(contentPane);
		setLocationRelativeTo(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(null);
		panel.setBackground(new Color(100, 30, 57));
		
		JLabel lblPayment = new JLabel("Payment for Order ID: " + orderid);
		lblPayment.setIcon(new ImageIcon(Paymentwindow.class.getResource("/main/logo/card-payment.png")));
		lblPayment.setForeground(Color.WHITE);
		lblPayment.setFont(new Font("Comic Sans MS", Font.PLAIN, 17));
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGap(0, 835, Short.MAX_VALUE)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(20)
					.addComponent(lblPayment)
					.addContainerGap(718, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGap(0, 68, Short.MAX_VALUE)
				.addGroup(gl_panel.createSequentialGroup()
					.addContainerGap(15, Short.MAX_VALUE)
					.addComponent(lblPayment)
					.addGap(21))
		);
		panel.setLayout(gl_panel);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBackground(new Color(250, 243, 221));
		
		JPanel panel_2 = new JPanel();
		panel_2.setBackground(new Color(216, 226, 220));
		
		JPanel panel_2_1 = new JPanel();
		panel_2_1.setBackground(new Color(216, 226, 220));
		
		JLabel lblNewLabel_1_1 = new JLabel("Enter Amount (RM)");
		lblNewLabel_1_1.setFont(new Font("SansSerif", Font.BOLD, 14));
		
		custpayfield = new JTextField();
		custpayfield.setFont(new Font("SansSerif", Font.PLAIN, 16));
		custpayfield.setColumns(10);
		GroupLayout gl_panel_2_1 = new GroupLayout(panel_2_1);
		gl_panel_2_1.setHorizontalGroup(
			gl_panel_2_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_2_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2_1.createParallelGroup(Alignment.LEADING)
						.addComponent(custpayfield, GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
						.addComponent(lblNewLabel_1_1, GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_2_1.setVerticalGroup(
			gl_panel_2_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel_1_1)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(custpayfield, GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)
					.addContainerGap())
		);
		panel_2_1.setLayout(gl_panel_2_1);
		
		
		JRadioButton creditcardtype = new JRadioButton("Credit Card");
		creditcardtype.setFont(new Font("SansSerif", Font.BOLD, 16));
		creditcardtype.setBackground(new Color(200, 213, 220));
		JRadioButton cashtype = new JRadioButton("Cash");
		cashtype.setFont(new Font("SansSerif", Font.BOLD, 16));
		cashtype.setBackground(new Color(200, 213, 220));
		JRadioButton debittype = new JRadioButton("Debit");
		debittype.setFont(new Font("SansSerif", Font.BOLD, 16));
		debittype.setBackground(new Color(200, 213, 220));
		
		cashtype.setActionCommand("Cash");
		debittype.setActionCommand("Debit");
		creditcardtype.setActionCommand("Credit Card");
		
		ButtonGroup paymenttypeselector = new ButtonGroup();
		paymenttypeselector.add(cashtype);
		paymenttypeselector.add(debittype);
		paymenttypeselector.add(creditcardtype);
		
		
		JButton btnNewButton = new JButton("Pay");
		btnNewButton.setIcon(new ImageIcon(Paymentwindow.class.getResource("/main/logo/payment-method.png")));
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				// PROCESS STATE
				boolean process = false;
				
				// ERROR STATE
				boolean custpayvalueerror = false;
				boolean paymenttypeerror = false;
				boolean insufficientbalance = false;
				
				double custpayvalue = 0;
				String paymenttype = null;
				
				//GET PAYMENT TYPE
				try {
					paymenttype = paymenttypeselector.getSelection().getActionCommand();
				}catch(Exception e1) {
					paymenttypeerror = true;
					System.out.println("No Value Selected: " + e1.getMessage());
				}
				
				//GET PAY VALUE
				try {
					custpayvalue = Double.parseDouble(custpayfield.getText());
					//CHECK IF PAY VALUE BELOW THAN PRICE
					if(custpayvalue < payment) {
						insufficientbalance = true;
					}
				}catch(Exception e1) {
					custpayvalueerror = true;
					System.out.println("INVALID PAY VALUE: " + e1.getMessage());
				}
				
				// ERROR MESSAGE
				if (custpayvalueerror || paymenttypeerror || insufficientbalance) {
					String error = "Payment unable to proceed:";
					if (custpayvalueerror) {
						error += "\nInvalid pay value";
					}
					if (paymenttypeerror) {
						error += "\nPayment type not selected";
					}
					if (insufficientbalance) {
						error += "\nInsufficient balance";
					}
					JOptionPane.showMessageDialog(null, error, "Error Payment. ID: " + orderid, JOptionPane.ERROR_MESSAGE);
				} else {
					process = true;
				}
				
				//PAYMENT PROCESS DATA
				if(process == true) {
					String insertnewitem = "INSERT INTO payment(paymenttype,totalprice,custpay,orderid) VALUES (?,?,?,?)";
					String updatepaidstatus = "UPDATE orders SET statuspaid = 'paid' WHERE id = '"+orderid+"'";
					try (Connection conn = Main.connect();
							PreparedStatement pstmt = conn.prepareStatement(insertnewitem);
							Statement stmt = conn.createStatement()) {
						pstmt.setString(1, paymenttype);
						pstmt.setDouble(2, payment);
						pstmt.setDouble(3, custpayvalue);
						pstmt.setInt(4, orderid);
						pstmt.executeUpdate();
						stmt.execute(updatepaidstatus);
						conn.close();
						
						Receiptwindow receiptframe = new Receiptwindow(orderid);
						receiptframe.setVisible(true);
						Cashierwindow.getorderframe().dispose();
						Cashierwindow.showdata();
						dispose();
			            NewOrderwindow.setpaymentframenull();
			            NewOrderwindow.listpricecust = 0;
						NewOrderwindow.finalprice = 0;
						NewOrderwindow.regularcustomer = false;
						NewOrderwindow.gender = "";
						Cashierwindow.showlastorder();
						
						if(recoverystate == true) {							
							Main.getcashierframe().setState(JFrame.NORMAL);
						}
						
						//DELETE RECOVERY FILE
						try {							
							File recoveryfile = new File("autorecover/autorecovery.txt");
							if(recoveryfile.exists()) {
								recoveryfile.delete();
							}
						}catch(Exception e1) {
							System.out.println("Error: " + e1.getMessage());
						}
					} catch (SQLException e1) {
						System.out.println("SQL ERROR PAYMENT: " + e1.getMessage());
					} catch (Exception e1) {
						System.out.println("ERROR OPENING RECEIPT: "+e1.getMessage());
					}	
				}
			}
		});
		btnNewButton.setBackground(new Color(204, 183, 174));
		btnNewButton.setFont(new Font("SansSerif", Font.BOLD, 16));
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addComponent(panel, GroupLayout.DEFAULT_SIZE, 832, Short.MAX_VALUE)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 268, Short.MAX_VALUE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_2, GroupLayout.DEFAULT_SIZE, 538, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel_2_1, GroupLayout.DEFAULT_SIZE, 812, Short.MAX_VALUE)
					.addContainerGap())
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap(628, Short.MAX_VALUE)
					.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 194, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 68, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE)
						.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 147, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_2_1, GroupLayout.PREFERRED_SIZE, 83, GroupLayout.PREFERRED_SIZE)
					.addGap(75)
					.addComponent(btnNewButton, GroupLayout.PREFERRED_SIZE, 44, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		JLabel lblNewLabel_1 = new JLabel("Total");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 14));
		
		JLabel pricedisplay = new JLabel("RM " + priceformatter.format(payment));
		pricedisplay.setFont(new Font("SansSerif", Font.BOLD, 18));
		GroupLayout gl_panel_2 = new GroupLayout(panel_2);
		gl_panel_2.setHorizontalGroup(
			gl_panel_2.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_2.createParallelGroup(Alignment.TRAILING)
						.addComponent(pricedisplay, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
						.addComponent(lblNewLabel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_2.setVerticalGroup(
			gl_panel_2.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_2.createSequentialGroup()
					.addGap(16)
					.addComponent(lblNewLabel_1)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(pricedisplay, GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
					.addContainerGap())
		);
		panel_2.setLayout(gl_panel_2);
		
		JLabel lblNewLabel = new JLabel("Choose Payment Type");
		lblNewLabel.setFont(new Font("SansSerif", Font.PLAIN, 17));
		
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING, false)
						.addComponent(lblNewLabel)
						.addComponent(debittype, GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
						.addComponent(cashtype, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(creditcardtype, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addContainerGap(84, Short.MAX_VALUE))
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addComponent(lblNewLabel)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(cashtype)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(debittype)
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(creditcardtype)
					.addContainerGap(13, Short.MAX_VALUE))
		);
		panel_1.setLayout(gl_panel_1);
		contentPane.setLayout(gl_contentPane);
	}
}
