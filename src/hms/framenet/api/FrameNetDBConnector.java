package hms.framenet.api;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.saar.coli.salsa.reiter.framenet.DatabaseReader;
import de.saar.coli.salsa.reiter.framenet.FNDatabaseReader;
import de.saar.coli.salsa.reiter.framenet.FrameNet;

public class FrameNetDBConnector {

	
	private static FrameNet fn = null ;
	
	/**
	 * Establish a connection to FrameNet database
	 */
	private static void initDBConnection() {

		Properties prop = new Properties();
		InputStream input = null;
		DatabaseReader reader;
		try {

			input = new FileInputStream("config.properties");
			// STEP1: load a properties file
			prop.load(input);

			String FN_DB_LOCATION = prop.getProperty("FN_DB_LOCATION");
			fn = new FrameNet();
			reader = new FNDatabaseReader(new File(FN_DB_LOCATION), false);
			fn.readData(reader);
		} catch (IOException ex) {
			ex.printStackTrace();
		} 
	}
	

	/**
	 * Get FrameNet instance
	 * @return
	 */
	public static FrameNet getFrameNetInstance(){
		if(fn==null){
			initDBConnection();
		}
		return fn;
	}
	

}
