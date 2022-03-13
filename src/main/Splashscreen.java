package main;

import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.JProgressBar;

public class Splashscreen extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create the application.
	 */
	JProgressBar progressBar = new JProgressBar();
	JLabel lblNewLabel_3 = new JLabel("Welcome!");
	public Splashscreen() {
		setUndecorated(true);
		setResizable(false);
		setTitle("Bakery Shop");
		setBounds(100, 100, 765, 387);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setIconImage(new ImageIcon(this.getClass().getResource("/main/logo/logo.png")).getImage());
		getContentPane().setLayout(null);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(0, 0, 766, 387);
		panel_1.setBackground(new Color(33, 33, 33));
		getContentPane().add(panel_1);

		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setBounds(194, 143, 85, 80);
		lblNewLabel_1.setIcon(new ImageIcon(this.getClass().getResource("/main/logo/logo.png")));

		JLabel lblNewLabel_2 = new JLabel("Bakery Shop");
		lblNewLabel_2.setBounds(289, 143, 329, 89);
		lblNewLabel_2.setForeground(Color.WHITE);
		lblNewLabel_2.setFont(new Font("SansSerif", Font.BOLD, 54));

		JLabel lblNewLabel = new JLabel("By: Amirul Asri, Harris Irfan, Sholihin Ilias, Aliff Redzuan, Mifzal Dini");
		lblNewLabel.setBounds(88, 360, 509, 27);
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setFont(new Font("Calibri Light", Font.PLAIN, 17));
		
				JPanel panel = new JPanel();
				panel.setBounds(0, 0, 78, 387);
				panel.setBackground(new Color(255, 204, 102));
				
						
						GroupLayout gl_panel = new GroupLayout(panel);
						gl_panel.setHorizontalGroup(
							gl_panel.createParallelGroup(Alignment.LEADING)
								.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 766, Short.MAX_VALUE)
						);
						gl_panel.setVerticalGroup(
							gl_panel.createParallelGroup(Alignment.TRAILING)
								.addGroup(gl_panel.createSequentialGroup()
									.addContainerGap(373, Short.MAX_VALUE)
									.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						);
						progressBar.setBackground(new Color(255, 204, 0));
						progressBar.setVisible(false);
						progressBar.setForeground(new Color(204, 0, 0));
						panel.setLayout(gl_panel);
		lblNewLabel_3.setBounds(88, 340, 347, 17);
		
		lblNewLabel_3.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNewLabel_3.setForeground(new Color(255, 255, 255));
		panel_1.setLayout(null);
		panel_1.add(panel);
		panel_1.add(lblNewLabel_1);
		panel_1.add(lblNewLabel);
		panel_1.add(lblNewLabel_3);
		panel_1.add(lblNewLabel_2);
	}
}
