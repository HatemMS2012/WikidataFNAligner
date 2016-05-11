package hms.alignment.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Evaluator {

	
	private static String TEST_INPUT_TEST_CASES_NEW_CSV = "test/input/test_cases_new_v2.csv";
	
	
	

	public static String getTEST_INPUT_TEST_CASES_NEW_CSV() {
		return TEST_INPUT_TEST_CASES_NEW_CSV;
	}

	public static void setGroundTruthFile(
			String tEST_INPUT_TEST_CASES_NEW_CSV) {
		TEST_INPUT_TEST_CASES_NEW_CSV = tEST_INPUT_TEST_CASES_NEW_CSV;
	}

	public static void evaluateProperty(String resultFile, int topN) throws IOException{
		
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
		DecimalFormat df = (DecimalFormat)nf;
		df.applyPattern("###.##");
		

		
		
		Map<String, Map<String, String>> groundTruthMap = loadGroundTruth(TEST_INPUT_TEST_CASES_NEW_CSV);
		

		
		FileInputStream fstream = new FileInputStream(resultFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;
		int totalCases = 0;
		int totalCorrectSubjects = 0;
		int totalCorrectObjects = 0;
		//Read File Line By Line
		int count = 0;
		String alpha = "" ;
		while ((strLine = br.readLine()) != null)   {
			
			count++ ;
			if(count <= 1){
			
				alpha = strLine.substring(strLine.lastIndexOf(": ")+2,strLine.length());
				
				continue;
			}
			
			int correctSubject = 0 ;
			int correctObject = 0;
			totalCases ++;
			
			String[] lineArr = strLine.split("\t");
			
			String frameId = lineArr[0];
			String propId = lineArr[1];
			String subject = lineArr[2];
			String object = lineArr[3];
			String subjectRole ="[";
			String objectRole = "[";
			
			
			String[] subjectRoleMatchings = null ;
			if(subject.contains("=")){
//				subjectRole = subject.substring(subject.indexOf("{")+1, subject.indexOf("="));
				//get first n matching
				subjectRoleMatchings = subject.substring(subject.indexOf("Sub:{")+"Sub:{".length(), subject.lastIndexOf("}")).split(",");
				
			}
			String[] objectRoleMatchings = null ;

			if(object.contains("=")){
//				objectRole = object.substring(object.indexOf("{")+1, object.indexOf("="));
				objectRoleMatchings = object.substring(object.indexOf("Obj:{")+"Sub:{".length(), object.lastIndexOf("}")).split(",");

			}
			
			
			String groundSubject= groundTruthMap.get(propId+"_"+frameId).get("subject");
			String groundObject= groundTruthMap.get(propId+"_"+frameId).get("object");
			String groundCase =  groundTruthMap.get(propId+"_"+frameId).get("case");
			
			if(subjectRoleMatchings!=null){
				for (int i = 0; i < Math.min(subjectRoleMatchings.length,topN); i++) {
					
					String match = subjectRoleMatchings[i].split("=")[0].trim();
					String score = df.format(Float.valueOf(subjectRoleMatchings[i].split("=")[1].trim()));
					subjectRole +=match+":"+score +",";
					
					if(groundSubject.contains(match)){
						correctSubject = 1;
						break;
					}
				}
				subjectRole = subjectRole.substring(0, subjectRole.length()-1) + "]";
			}
			
			if(objectRoleMatchings !=null){

				for (int i = 0; i <Math.min(objectRoleMatchings.length,topN); i++) {
					String match = objectRoleMatchings[i].split("=")[0].trim();
					String score = df.format(Float.valueOf(objectRoleMatchings[i].split("=")[1].trim()));
					objectRole +=match+":"+score +",";
	
					if(groundObject.contains(match)){
						correctObject = 1 ;
						break;
					}
				}
				objectRole = objectRole.substring(0, objectRole.length()-1) + "]";
			}
			
			totalCorrectSubjects +=correctSubject;
			totalCorrectObjects += correctObject;
			
//			System.out.println(groundCase + "\t" + subjectRole+"/["+groundSubject + "]\t" + correctSubject  + "\t" + objectRole+"/["+groundObject + "]\t" + correctObject);
		}
		
//		System.out.println( "Total \t" + totalCases + "\t" + totalCases);
//		System.out.println( "AVG \t" + (totalCorrectSubjects/(double)totalCases) + "\t" + (totalCorrectObjects/(double)totalCases));
		System.out.println("ARG1 \t" +alpha +"\t"+ (totalCorrectSubjects/(double)totalCases));
		System.out.println("ARG2 \t" +alpha +"\t"+ (totalCorrectObjects/(double)totalCases) );
		br.close();
	}
	
	public static void evaluate(String resultFile, String subjectRole, String objectRole) throws IOException{
	

		//Obj: Stanford University	{Qualification=
		
		int truePositivesSubject = 0;
		int falsePositivesSubject = 0;
		
		int truePositivesObject = 0;
		int falsePositivesObject = 0;
		
		int totalSubject=0;
		int totalObject=0;
		// Open the file
		FileInputStream fstream = new FileInputStream(resultFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;

		//Read File Line By Line
		while ((strLine = br.readLine()) != null)   {
		 
			if(strLine.equals("..................."))
				continue;
			
			String[] lineArr = strLine.split("\t");
			String arg = lineArr[0];
			if(arg.contains("null"))
				continue;
			if(!strLine.contains("="))
				continue;
			String argLabel =  lineArr[1].substring(1, lineArr[1].indexOf("="));
			
			
			
			if(arg.contains("Sub:")){
				totalSubject ++;
				if(argLabel.equalsIgnoreCase(subjectRole)){
					
					truePositivesSubject++;
				}
				else{
					falsePositivesSubject ++ ;
				}
			}
			if(arg.contains("Obj:")){
				totalObject ++ ;
				if(argLabel.equalsIgnoreCase(objectRole)){
					
					truePositivesObject++;
				}
				else{
					falsePositivesObject ++ ;
				}
			}
			
		}

		//Close the input stream
		br.close();
		System.out.println(truePositivesSubject + "\t" + falsePositivesSubject + "\t" + totalSubject + "\t" + ((double)truePositivesSubject/(double)totalSubject)
							+ "\t" + truePositivesObject + "\t" + falsePositivesObject + "\t" + totalObject + "\t" + ((double)truePositivesObject/(double)totalObject));
		
	}
	
	public static void main(String[] args) throws IOException {

		String inputDir = "test/output/cosLucene/new/";
		
		getResults( inputDir,1);

	}

	public static void getResults(String inputDir,int k) throws IOException {
	
		String[]  soFileNames = new File(inputDir).list();
//		System.out.println("Case \t subject(pred/GT) \t correct_subject \t object(pred/GT) \t correct_object");

		for(String soFile : soFileNames){
//			System.out.println(soFile);
			
			evaluateProperty(inputDir+soFile,k);
//			System.out.println("......................");
		}
	}
	
	public static Map<String, Map<String, String>> loadGroundTruth(String testCaseFile ) throws IOException{
		
		Map<String, Map<String, String>> groundTruthMap  = new HashMap<String, Map<String,String>>();
		
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
			String frameID =  lineArr[2];
			String propLabel =  lineArr[1];
			String frameLabel =  lineArr[3];
			String subject = lineArr[4].trim();
			String object =  lineArr[5].trim();
			
			String key = propertyID+"_"+frameID ;
			Map<String,String> groundLabels = new HashMap<String, String>();
			groundLabels.put("subject", subject);
			groundLabels.put("object", object);
			groundLabels.put("case", propLabel+"<->" + frameLabel);
			groundTruthMap.put(key, groundLabels);
			
		}
		
		br.close();
		return groundTruthMap;
	}
}
