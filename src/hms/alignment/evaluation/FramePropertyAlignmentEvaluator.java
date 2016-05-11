package hms.alignment.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FramePropertyAlignmentEvaluator {

	private static final String GROUND_TRUTH_LOC = "test/input/test_cases_new_v5.csv";
	private static final String RESULT_DIR = "test/output/wd-fn/old/";
	private double scoreThreshold ;
	private String groundTruthFile = "test/input/test_cases_new_v5.csv";
	private String resultFile ;
	
	private Map<String, List<String>> resultMap ;
	private Map<String, List<String>> groundTruthMap ;
	
	
	private static final Map<String, Double> SIM_THRESHOLD_PER_METHOD ; 
	
	static {
		 SIM_THRESHOLD_PER_METHOD = new HashMap<String, Double>(); 
		 SIM_THRESHOLD_PER_METHOD.put("BL_1", 0.1);
		 SIM_THRESHOLD_PER_METHOD.put("BL_2", 3.0);
		 SIM_THRESHOLD_PER_METHOD.put("BL_3", 0.65);
		 SIM_THRESHOLD_PER_METHOD.put("BL_4", 0.1);
		 SIM_THRESHOLD_PER_METHOD.put("BL_5", 0.1);
		 SIM_THRESHOLD_PER_METHOD.put("METHOD_I", 0.7);
	}

	
//	public FramePropertyAlignmentEvaluator() {
//		super();
//	}
	
	public FramePropertyAlignmentEvaluator(double scoreThreshold) {
		this.scoreThreshold = scoreThreshold;
	}



	public FramePropertyAlignmentEvaluator(String groundTruthFile, String resultFile,double scoreThreshold) {
		this.groundTruthFile = groundTruthFile;
		this.resultFile = resultFile;
		this.scoreThreshold = scoreThreshold;
		try {
			initGroundTruthMap();
			initResultMap();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void initGroundTruthMap() throws IOException{
		
		groundTruthMap = new HashMap<String, List<String>>();
		
		FileInputStream fstream = new FileInputStream(groundTruthFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String strLine;
		int i = 0;
		while ((strLine = br.readLine()) != null)   {
			
			i++;
			//ignore the first line
			if(i==1)
				continue;
			
			String[] lineArr = strLine.split(",");
			
			String propertyLabel = lineArr[1];
		
			String frameLabel =  lineArr[3];

			if(groundTruthMap.get(propertyLabel)!=null){
				
				List<String> matchSet = groundTruthMap.get(propertyLabel);
				matchSet.add(frameLabel);
				groundTruthMap.put(propertyLabel, matchSet);
			}
			else{
				
				List<String> matchSet = new ArrayList<String>();
				matchSet.add(frameLabel);
				groundTruthMap.put(propertyLabel, matchSet);
			}
			
			
		}
		br.close();
	}
	
	public void initResultMap() throws IOException{

	
		resultMap = new HashMap<String, List<String>>();
		
		FileInputStream fstream = new FileInputStream(resultFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String strLine;
		int i = 0;
		
		int count = 0;
		while ((strLine = br.readLine()) != null)   {
			
			i++;
			//ignore the first line
			if(i==1)
				continue;
			

		
			String[] lineArr = strLine.split("\t");
			
			String propertyLabel = lineArr[1].replace(" ", "");
		
			String frameLabel =  lineArr[2];
			
			double score = Double.valueOf(lineArr[3]);

			if(score > scoreThreshold){
				if(resultMap.get(propertyLabel)!=null){
					
					List<String> matchSet = resultMap.get(propertyLabel);
					matchSet.add(matchSet.size(),frameLabel);
					resultMap.put(propertyLabel, matchSet);
				}
				else{
					
					List<String> matchSet = new ArrayList<String>();
					matchSet.add(0,frameLabel);
					resultMap.put(propertyLabel, matchSet);
				}
			}
			
		}
		br.close();
	}
	
	
	public EvaluationResult evaluate(String resEntry,int k){
		
		EvaluationResult evResutl = new EvaluationResult();
		//Get retrieved
		
		
		
		
		List<String> goldDocuments = groundTruthMap.get(resEntry);
		int countRelevantDocument = goldDocuments.size();
		
		int countRetrievedDocuments = 0;
		List<String> retrievedDocuments = resultMap.get(resEntry);
 		if(retrievedDocuments != null){
 			retrievedDocuments = getTopResult(resultMap.get(resEntry),countRelevantDocument);
			countRetrievedDocuments = retrievedDocuments.size() ;
		}
		
		//intersection
		int countIntersection = 0;
		if(retrievedDocuments!=null){

			goldDocuments.retainAll(retrievedDocuments);
			countIntersection = goldDocuments.size();	
		}
		
		double p = 0;
		if(countRetrievedDocuments > 0){
			p = (double)countIntersection /(countRetrievedDocuments);
		}
		
		double r = (double)countIntersection /(countRelevantDocument);
		
		
		double f1 = 0 ;
		if(r > 0 || p > 0){
			f1 = 2*((p*r)/(p+r));
		}
		
		
		
		evResutl.setPercision(p);
		evResutl.setRecall(r);
		evResutl.setfMeasure(f1);
		
//		System.out.println(resEntry + ": "+ p + "\t" + r + "\t" + f1);
		
		return evResutl;
	}
	
	
	private List<String> getTopResult(List<String> set, int size) {
		List<String> subList = new ArrayList<String>();
		if(set.size() > 0){
			for (int i = 0; i < Math.min(size, set.size()); i++) {
				subList.add(set.get(i));
			}
		}
		return subList;
	}


	public EvaluationResult evaluate(int k){
		
		EvaluationResult avgResult = new EvaluationResult();
		double p=0,r=0,f1 = 0;
		Set<String> resultElements = this.groundTruthMap.keySet();
		int total = resultElements.size();
		for(String resultElement : resultElements){
			
			EvaluationResult evalResult = evaluate(resultElement,k);
			p +=evalResult.getPercision();
			r += evalResult.getRecall();
			f1 += evalResult.getfMeasure();
			
		}
		
		avgResult.setPercision(p/total);
		avgResult.setRecall(r/total);
		avgResult.setfMeasure(f1/total);
		
		return avgResult;
		
	}
	
	
	public static void main(String[] args) {
		
		String[] files = new File(RESULT_DIR).list();
		
		for (int j = 0; j < files.length; j++) {
			
			if(files[j].endsWith(".txt")){

				double scoreThreshold = 0 ; //SIM_THRESHOLD_PER_METHOD.get(files[j].replace(".txt", ""));
//				System.out.println(files[j] + ":" + scoreThreshold);
				FramePropertyAlignmentEvaluator ev = new FramePropertyAlignmentEvaluator(GROUND_TRUTH_LOC,RESULT_DIR + files[j],scoreThreshold);

				EvaluationResult finalResult = ev.evaluate(1);
				
				System.out.println(files[j].replace(".txt", "") + "\t" + 1 + "\t" + finalResult.getPercision() + "\t" + finalResult.getRecall() + "\t" + finalResult.getfMeasure());
				
//				for (int i = 1; i < 2; i++) {
//					FramePropertyAlignmentEvaluator ev = new FramePropertyAlignmentEvaluator(GROUND_TRUTH_LOC,RESULT_DIR + files[j]);
//
//					EvaluationResult finalResult = ev.evaluate(i);
//					
//					System.out.println(files[j].replace(".txt", "") + "\t" + i + "\t" + finalResult.getPercision() + "\t" + finalResult.getRecall() + "\t" + finalResult.getfMeasure());
//
//				}
				System.out.println();
			}
			
		}
		

	}
}
