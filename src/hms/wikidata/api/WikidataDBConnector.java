package hms.wikidata.api;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class WikidataDBConnector {

	private static Connection conn = null;

	
	/**
	 * Establish an SQL connection to Wikidata database
	 */
	private static void initDBConnection() {

		Properties prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream("config.properties");
			// STEP1: load a properties file
			prop.load(input);

			String dbDriver = prop.getProperty("DB_DRIVER");
			String dbURL = prop.getProperty("DB_URL");
			String dbUSERNAME = prop.getProperty("DB_USERNAME");
			String dbPW = prop.getProperty("DB_PASSWORD");

			// STEP 2: Register JDBC driver
			Class.forName(dbDriver);
			conn = DriverManager.getConnection(dbURL, dbUSERNAME, dbPW);

		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get a DB connection instance
	 * @return
	 */
	public static Connection getDBConnectin(){
		
		if(conn == null){
			initDBConnection();
		}
		return conn;
	}


}
