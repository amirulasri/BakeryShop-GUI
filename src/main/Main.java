package main;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
	
	//SETTING
	static private double discount = 0.10;
	static String url = "jdbc:sqlite::resource:db/bakeryorderdata.db";
	private static Cashierframe cashier;
	
	static public double getdiscountvalue() {
		return discount;
	}
	
	static public Cashierframe getcashierframe() {
		return cashier;
	}
	
	public static Connection connect() {
        // SQLite connection string
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
	
	public static void createTable() {
        // SQL statement for creating a new table
		String turnofffk = "PRAGMA foreign_keys=off;";
		
		String createordertable = "CREATE TABLE IF NOT EXISTS orders (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	date text NOT NULL,\n"
                + "	time text NOT NULL,\n"
                + "	statuspaid text NOT NULL\n"
                + ");";
		
        String createcusttable = "CREATE TABLE IF NOT EXISTS customer (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	name text NOT NULL,\n"
                + "	phoneno text NOT NULL,\n"
                + "	address text NOT NULL,\n"
                + "	gender text NOT NULL,\n"
                + "	regularcustomer numeric NOT NULL,\n"
                + "	orderid integer NOT NULL,\n"
                + "	FOREIGN KEY (orderid)\n"
                + "	  REFERENCES orders (id) ON DELETE CASCADE\n"
                + ");";
        
        String createitemtable = "CREATE TABLE IF NOT EXISTS item (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	itemname text NOT NULL,\n"
                + "	itemnumber integer NOT NULL,\n"
                + "	quantity integer NOT NULL,\n"
                + "	totalitems numeric NOT NULL,\n"
                + "	orderid integer NOT NULL,\n"
                + "	FOREIGN KEY (orderid)\n"
                + "	  REFERENCES orders (id) ON DELETE CASCADE\n"
                + ");";
        
        String createpaymenttable = "CREATE TABLE IF NOT EXISTS payment (\n"
                + "	id integer PRIMARY KEY,\n"
                + "	paymenttype text NOT NULL,\n"
                + "	totalprice numeric NOT NULL,\n"
                + "	custpay numeric NOT NULL,\n"
                + "	orderid integer NOT NULL,\n"
                + "	FOREIGN KEY (orderid)\n"
                + "	  REFERENCES orders (id) ON DELETE CASCADE\n"
                + ");";
        
        String turnonfk = "PRAGMA foreign_keys=on;";
        
        try (Connection conn = connect();
                Statement statement = conn.createStatement()) {
            // create a new table
        	statement.execute(turnofffk);
        	statement.execute(createordertable);
        	statement.execute(createcusttable);
        	statement.execute(createitemtable);
        	statement.execute(createpaymenttable);
        	statement.execute(turnonfk);
        	conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

	public static void main(String[] args) {
		
		createTable();
		Welcomeframe welcomeframe;
		try {
			welcomeframe = new Welcomeframe();

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
				cashier = new Cashierframe();
				cashier.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}