package hms.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class GeneralUtil {

	public static List<String> loadPropertyIdsFromFile(String testCaseFile) throws IOException{
		
		List<String> propIdList  = new ArrayList<String>();
		
		FileInputStream fstream = new FileInputStream(testCaseFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String strLine;
		//Read File Line By Line
		
		int i = 0;
		while ((strLine = br.readLine()) != null)   {
			
			i++;
			//ignore the first line
			if(i==1)
				continue;
				 
			String[] lineArr = strLine.split(",");
			String propertyID = lineArr[0];
			propIdList.add(propertyID);
			
		}
		
		br.close();
		return propIdList;
	}
}
