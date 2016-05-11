package hms.alignment.evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MatchingEvaluator {

	
	
	public Map<String, Map<String, Integer>> loadGroundTruthFromFile(String groundTruthFile) throws IOException{
		
		Map<String, Map<String, Integer>> resultMap  = new HashMap<String, Map<String,Integer>>();
		
		
		String separtor = ",";
		
		FileInputStream fstream = new FileInputStream(groundTruthFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String strLine;
		
		int i = 0;
	
		while ((strLine = br.readLine()) != null)   {
			
			i++;
			//ignore the first line
			if(i==1)
				continue;
			
			//PropertyID	PropertyLabel	FrameLabel	Annotation
			
			String lineArr[] = strLine.split(separtor);
			String propertyId = lineArr[0];
			String propLable =  lineArr[1];
			String frameLabel = lineArr[2];
		
			int	annotation = Integer.valueOf(lineArr[3]);
			
			Map<String, Integer> groundTruthForOneProperty = resultMap.get(propertyId);
			
			if(groundTruthForOneProperty == null){
				groundTruthForOneProperty = new HashMap<String, Integer>();
				
			}
			
			groundTruthForOneProperty.put(propertyId+"-"+frameLabel, annotation);
			
			resultMap.put(propertyId, groundTruthForOneProperty);
			
		}
		br.close();
		return resultMap;
	}
	
	public Map<String, Map<String, Integer>> loadReultsFromFile(String resultFile, double scoreThreshold,int MAX_TRUE_MATCHINGS) throws IOException{
		
		Map<String, Map<String, Integer>> resultMap  = new HashMap<String, Map<String,Integer>>();
		
		
		String separtor = "\t";
			
		FileInputStream fstream = new FileInputStream(resultFile);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		String strLine;
		
		int i = 0;
	
		while ((strLine = br.readLine()) != null)   {
			
			i++;
			//ignore the first line
			if(i==1)
				continue;
			
			//PropertyID	PropertyLabel	FrameLabel	Annotation
			
			String lineArr[] = strLine.split(separtor);
			String propertyId = lineArr[0];
			String propLable =  lineArr[1];
			String frameLabel = lineArr[2];
		
			int annotation ;
		
	
			double score = Double.valueOf(lineArr[3]);

			if(score >= scoreThreshold){
					annotation = 1;
			}
			else{
					annotation = 0;
			}
			
			
			Map<String, Integer> resultsForOneProperty = resultMap.get(propertyId);
		
			if(resultsForOneProperty == null){
				resultsForOneProperty = new HashMap<String, Integer>();
				
			}
			if(resultsForOneProperty.size() <= MAX_TRUE_MATCHINGS){
				resultsForOneProperty.put(propertyId+"-"+frameLabel, annotation);	
			}
			else {
				resultsForOneProperty.put(propertyId+"-"+frameLabel, 0);
			}
		
			resultMap.put(propertyId, resultsForOneProperty);
			
		}
		br.close();
		return resultMap;
	}
	
	
	public EvaluationResult evaluatex(Map<String, Integer> result, Map<String, Integer> groundTruth){
		
		EvaluationResult evalResult = new EvaluationResult();
		if(result!=null){
			//Count true positives and false negatives
			int tp = 0;
			int fp = 0 ;
			for(Entry<String, Integer> e:result.entrySet()){
				
				if(e.getValue() == 1){
					
					Integer predictedClass = groundTruth.get(e.getKey());
					if(predictedClass != null && predictedClass == 1){
						
						tp +=1;
					}
					else{
						fp +=1;
					}
				}
				
			}
			
			//count false postives and true negatives
			int fn = 0;
			int tn = 0;
			
				for(Entry<String, Integer> e:result.entrySet()){
				
				if(e.getValue() == 0){
					
					Integer predictedClass = groundTruth.get(e.getKey());
					if(predictedClass != null && predictedClass == 0){
						
						tn +=1;
					}
					else if(predictedClass == null || predictedClass == 1){
						fn +=1;
					}
				}
				
			}
			
				
			double p = 0; 
			double r = 0 ;
			if(tp!=0){
				p = (double) tp/(tp + fp);
				r = (double) tp/(tp + fn);
			}
			
			
			
			
			
			double f1 = 0;
			if(p != 0 || r!= 0){
				f1 = 2*((p*r)/(p+r));
			}
			
			
			evalResult.setPercision(p);
			evalResult.setRecall(r);
			evalResult.setfMeasure(f1);
			
		}
		return evalResult;
		
	}
	
	public EvaluationResult evaluate(Map<String, Integer> result, Map<String, Integer> groundTruth){
		
		EvaluationResult evalResult = new EvaluationResult();
		if(result!=null){
			//Count true positives and false negatives
			int tp = 0;
			int fp = 0 ;
			for(Entry<String, Integer> e:groundTruth.entrySet()){
				
				if(e.getValue() == 1){
					
					Integer predictedClass = result.get(e.getKey());
					if(predictedClass != null && predictedClass == 1){
						
						tp +=1;
					}
					else{
						fp +=1;
					}
				}
				
			}
			
			//count false postives and true negatives
			int fn = 0;
			int tn = 0;
			
				for(Entry<String, Integer> e:groundTruth.entrySet()){
				
				if(e.getValue() == 0){
					
					Integer predictedClass = result.get(e.getKey());
					if(predictedClass == null || predictedClass == 0){
						
						tn +=1;
					}
					else if(predictedClass != null && predictedClass == 1){
						fn +=1;
					}
				}
				
			}
			
				
			double p = 0; 
			double r = 0 ;
			if(tp!=0){
				p = (double) tp/(tp + fp);
				r = (double) tp/(tp + fn);
			}
			
			
			
			
			
			double f1 = 0;
			if(p != 0 || r!= 0){
				f1 = 2*((p*r)/(p+r));
			}
			
			
			evalResult.setPercision(p);
			evalResult.setRecall(r);
			evalResult.setfMeasure(f1);
			
		}
		return evalResult;
		
	}
	
	
	public EvaluationResult evaluateTestCase(String gnFile, String resFile,double scoreThresold, int maxCorrectResults) throws IOException{
		
		Map<String, Map<String, Integer>> groundTruth = loadGroundTruthFromFile(gnFile);
		Map<String, Map<String, Integer>> results = loadReultsFromFile(resFile,scoreThresold,maxCorrectResults);

		double avgP = 0;
		double avgR = 0;
		double avgF1 = 0;
		int totalCases = groundTruth.keySet().size();
		
		for(String propId : groundTruth.keySet()){
			
			EvaluationResult evalRes = evaluate(results.get(propId),groundTruth.get(propId));
//			System.out.println(evalRes);
			avgP += evalRes.getPercision();
			avgR += evalRes.getRecall();
			avgF1 += evalRes.getfMeasure();
		}
		
		EvaluationResult finalEvalRes = new EvaluationResult(); 
		finalEvalRes.setPercision(avgP/totalCases);
		finalEvalRes.setRecall(avgR/totalCases);
		finalEvalRes.setfMeasure(2*finalEvalRes.getPercision()*finalEvalRes.getRecall() /(finalEvalRes.getPercision()+finalEvalRes.getRecall()));

		return finalEvalRes;
	}
	public static void main(String[] args) throws IOException {
		
//		test1();
		
		MatchingEvaluator m = new MatchingEvaluator();
		double scoreThresold = 0.4;
		int maxCorrectResults = 2;
		String groundTruthFile = "groundtruth/Ground_TRUTH_WITH_NEG_EXAMPLES_NO_DESC_TOW_RATERS_ONLY_AGREEMENT.csv";

		String resutlDir = "results/FramePropertyAlignments/";
		String[] resultFiles = new File(resutlDir).list();
		for(String f:resultFiles) {
			if(f.endsWith(".txt")){
				EvaluationResult avgEval2 = m.evaluateTestCase(groundTruthFile,resutlDir+f, scoreThresold,maxCorrectResults);
				System.out.println(f.replace(".txt", "") + "\t" + avgEval2);
			}
			
		}
//


	}
	
	
	public static void test1(){

		MatchingEvaluator m = new MatchingEvaluator();
	
		Map<String, Integer> result = new HashMap<String, Integer>();
		

		result.put("P101-Fields", 1);
		result.put("P101-Being_employed", 1);
		result.put("P101-Importance",1);
		result.put("P101-Education_teaching", 1);
		result.put("P101-Expertise", 0);
		
//		result.put("a-x", 1);
//		result.put("a-y", 0);
//		result.put("a-w", 1);
//		result.put("a-z", 0);
//		result.put("a-f", 1);
//		result.put("a-d", 0);
//		result.put("a-d1", 0);
//		result.put("a-d5", 1);
//		result.put("a-d2", 0);

		
		Map<String, Integer> gn = new HashMap<String, Integer>();
		

		gn.put("P101-Research", 1);
		gn.put("P101-Fields", 1);
		gn.put("P101-Institutions",1);
		gn.put("P101-Craft", 0);
		gn.put("P101-Studying", 0);
		gn.put("P101-Being_employed", 0);
		gn.put("P101-Importance", 0);
		gn.put("P101-Education_teaching", 0);
		
		
//		gn.put("a-x", 1);
//		gn.put("a-y", 1);
//		gn.put("a-w", 0);
//		gn.put("a-z", 0);

		
		
		
		EvaluationResult evalResult = m.evaluate(result, gn);
		System.out.println(evalResult);
		
		
//		EvaluationResult evalResult2 = m.evaluate2(result, gn);
//		System.out.println(evalResult2);
	}
}
