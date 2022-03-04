package main;

import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
	static ArrayList<Customerclass> customers = new ArrayList<Customerclass>();
	static ArrayList<Itemsclass> listitems = new ArrayList<Itemsclass>();
	static ArrayList<Ordersclass> listorders = new ArrayList<Ordersclass>();
	static ArrayList<Paymentclass> listpayment = new ArrayList<Paymentclass>();
	
	//CONFIGURATION
	static private double discount = 0.10;
	static private String appname = "Bakery Shop";
	static private String contributor = "Amirul Asri, Harris Irfan, Sholihin Ilias, Aliff Redzuan, Mifzal Dini";
	static String url = "jdbc:sqlite:db/bakeryorderdata.db";
	
	static public String getappname() {
		return appname;
	}
	
	static public String getcontributor() {
		return contributor;
	}
	
	static public double getdiscountvalue() {
		return discount;
	}

	public static ArrayList<Customerclass> getcustomer() {
		return customers;
	}

	public static ArrayList<Itemsclass> getitems() {
		return listitems;
	}
	
	public static ArrayList<Ordersclass> getorders() {
		return listorders;
	}
	
	public static ArrayList<Paymentclass> getpayment(){
		return listpayment;
	}
	
	public static void createNewTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS customer (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name text NOT NULL,\n"
                + "	phoneno text NOT NULL,\n"
                + "	address text NOT NULL,\n"
                + "	gender text NOT NULL,\n"
                + "	regularcustomer numeric NOT NULL\n"
                + ");";
        
        try (Connection conn = DriverManager.getConnection(url);
                Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
	
	public static void connect() {
        Connection conn = null;
        try {
            // db parameters
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
            System.out.println("Connection to SQLite has been established.");
            
            
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

	public static void main(String[] args) {
		createNewTable();
		/*
		Welcomeframe welcomeframe;
		Cashierframe cashier;
		try {
			welcomeframe = new Welcomeframe();
			cashier = new Cashierframe();

			welcomeframe.setVisible(true);
			Thread.sleep(2000);
			welcomeframe.progressBar.setVisible(true);
			try {
				for (int i = 0; i <= 100; i += 4) {
					Thread.sleep(20);
					welcomeframe.progressBar.setValue(i);
					welcomeframe.lblNewLabel_3.setText("Welcome! Starting up " + i + "%");
				}
				Thread.sleep(1000);
				welcomeframe.setVisible(false);
				cashier.setVisible(true);
			} catch (Exception e) {
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		*/
	}
}