package hms.alignment;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.saar.coli.salsa.reiter.framenet.Frame;
import de.saar.coli.salsa.reiter.framenet.FrameNet;
import hms.alignment.frame.FrameContextCreator;
import hms.alignment.frame.FrameContextDefaultCreator;
import hms.alignment.property.PropertyContextCreator;
import hms.alignment.property.PropertyContextCreatorEmbeddingSimple;
import hms.alignment.property.PropertyRawContextCreator;
import hms.alignment.similarity.ContextSimilarityCalculator;
import hms.alignment.similarity.EmbeddingSimilarityCalculator;
import hms.alignment.similarity.OverlapSimilarityCalculator;
import hms.embedding.VectorCombinationMethod;
import hms.embedding.WordEmbeddingSpace;
import hms.framenet.api.FrameNetDBConnector;
import hms.parser.AnnotatedWord;
import hms.util.GeneralUtil;
import hms.util.MapUtil;
import hms.wikidata.api.WikidataAPI;
import hms.wikidata.api.WikidataLanguages;

/**
 * This class provides methods for aligning Wikidata properties with FrameNet frames
 * @author Hatem Mousselly Sergieh
 *
 */
public class PropertyFrameMatcher {



	/**
	 * Calculate the similarity between the property and frame contexts
	 * @param fcc Frame context creator
	 * @param pcc Property context creator
	 * @param ccc Similarity measure
	 * @return
	 */
	public double calculateFramePropertySimilarity(FrameContextCreator fcc, PropertyContextCreator pcc, ContextSimilarityCalculator ccc){
		
		
		Collection<AnnotatedWord> frameContext = fcc.createFrameContext();
		Collection<AnnotatedWord> propertyContext = pcc.createPropertyContext();
		
		double sim = ccc.calculateSimilarity(frameContext, propertyContext);

		
		return sim;
	}
	
	
	/**
	 * Calculate the similarity between the property and frame contexts
	 * @param frameContext The frame context (collection of words)
	 * @param propertyContext The property context (collection of words)
	 * @param ccc The similarity measure
	 * @return
	 */
	public double calculateFramePropertySimilarity(Collection<AnnotatedWord> frameContext, Collection<AnnotatedWord> propertyContext,ContextSimilarityCalculator ccc){
		
		double sim = ccc.calculateSimilarity(frameContext, propertyContext);
		return sim;
	}
	
	
	/**
	 * Find a matching frames for a given property. The results is an ordered map of matching frames with the similarity scores.
	 * @param propertyID The ID of the property for which
	 * @param fcc Frame context creator
	 * @param pcc Property context creator
	 * @param ccc Similarity measure
	 * @return
	 */
	public Map<String, Double> getMatchingFrames(String propertyID, PropertyContextCreator pcc,FrameContextCreator fcc,ContextSimilarityCalculator ccc){
		
		
		Map<String, Double> matchingFrames = new HashMap<>();
		
		pcc.setPropertyID(propertyID);
		
		Collection<AnnotatedWord> propertyContext = pcc.createPropertyContext();
		
		System.out.println(propertyContext);
		FrameNet fn = FrameNetDBConnector.getFrameNetInstance();
		
		for(Frame f : fn.getFrames()){
			
			String frameID = f.getName();
			fcc.setFrameID(frameID);
			Collection<AnnotatedWord> frameContext = fcc.createFrameContext();
			
			double value = ccc.calculateSimilarity(frameContext, propertyContext);
			
			if(value > 0){
				
				matchingFrames.put(frameID, value);
			}
			
		}
		return MapUtil.sortMap(matchingFrames);
		
	}
	
	/**
	 * Find matching frames for a given property using the following settings:
	 * 1- Property consists of its label and aliases (default context)
	 * 2- Similarity measure is the lexical overlap
	 * @param propertyID
	 * @return
	 */
	public static Map<String, Double> baseline1(String propertyID){

		Map<String, Double> matchingFrames = new HashMap<>();

		ContextSimilarityCalculator overlapSimilarityMeasure = new OverlapSimilarityCalculator();
		
		//No lemmatization, no extension
		PropertyContextCreator propContextCreator = new PropertyRawContextCreator(propertyID,true,true);
		
		matchingFrames = finMatchingFrames(overlapSimilarityMeasure, propContextCreator);
		
		return matchingFrames;
	}

	
	/**
	 * Find matching frames for a list of properties according to baseline1
	 * @param propFile
	 * @param resultFile
	 * @param maxMatch
	 * @throws IOException
	 */
	public void baseline1(String propFile, String resultFile, int maxMatch) throws IOException{
	
		
		PrintWriter out = new PrintWriter(new File(resultFile));
		
		out.println("Property ID \t Property Label \t Matching Frame \t	 Score");
		
		List<String> propList = GeneralUtil.loadPropertyIdsFromFile(propFile);
		
		for(String property:propList){
			System.out.println("Dealing with property ... " + property + ":"+  WikidataAPI.getLabel(property, WikidataLanguages.en));
		
			
			
			Map<String, Double> matchingFrames =  baseline1(property);	
			
			matchingFrames = MapUtil.getTopN(matchingFrames,maxMatch);
			
			for(Entry<String, Double> match : matchingFrames.entrySet()) {
				out.println(property + "\t" + WikidataAPI.getLabel(property, WikidataLanguages.en) + "\t" + match.getKey() + "\t" + match.getValue());
				out.flush();
			}
		}
		out.close();

	}

	/**
	 * Find matching frames for a property given a context creation method and a similarity measure
	 * @param overlapSimilarityMeasure
	 * @param propContextCreator
	 * @return
	 */
	private static Map<String, Double> finMatchingFrames(ContextSimilarityCalculator overlapSimilarityMeasure,
			PropertyContextCreator propContextCreator) {

		Map<String, Double> matchingFrames = new HashMap<>();

		Collection<AnnotatedWord> propertyContext = propContextCreator.createPropertyContext();

		System.out.println("property context: " + propertyContext);

		FrameNet fn = FrameNetDBConnector.getFrameNetInstance();

		for (Frame f : fn.getFrames()) {

			String frameID = f.getName();

			FrameContextCreator defaultFrameContextCreator = new FrameContextDefaultCreator(frameID);

			Collection<AnnotatedWord> frameContext = defaultFrameContextCreator.createFrameContext();

			double value = overlapSimilarityMeasure.calculateSimilarity(frameContext, propertyContext);

			if (value > 0) {
				matchingFrames.put(frameID, value);
			}
		}

		matchingFrames = MapUtil.sortMap(matchingFrames);
		return matchingFrames;
	}
	/**
	 * This approach was applied in the paper to align Wikidata properties with FrameNet frames
	 * @param propertyID
	 * @param wordEmbeddingSpaceForContextExpansion
	 * @param vectorComMethod
	 * @param expansionThreshold
	 * @return
	 */
	public  Map<String, Double> paperApproach(String propertyID, String wordEmbeddingSpaceForContextExpansion, String wordEmbeddingSpaceForSimilarity, VectorCombinationMethod vectorComMethod, double expansionThreshold){
		
		ContextSimilarityCalculator embedSim = new EmbeddingSimilarityCalculator(wordEmbeddingSpaceForSimilarity,vectorComMethod);

		Map<String, Double> matchingFrames = new HashMap<>();
		

		PropertyContextCreatorEmbeddingSimple propertyEmbeddingContextCalculator = new PropertyContextCreatorEmbeddingSimple(propertyID,true,true);

		propertyEmbeddingContextCalculator.setWordEmbeddingSpace(wordEmbeddingSpaceForContextExpansion);
		propertyEmbeddingContextCalculator.setThreshold(expansionThreshold);
	
		
		Collection<AnnotatedWord> propertyContext = propertyEmbeddingContextCalculator.createPropertyContext();
		
		System.out.println("property context: " + propertyContext);
		

		FrameNet fn = FrameNetDBConnector.getFrameNetInstance();
		
		for(Frame f : fn.getFrames()){

			String frameID = f.getName();
			
			
			FrameContextCreator defaultFrameContextCreator = new FrameContextDefaultCreator(frameID);

			Collection<AnnotatedWord> frameContext = defaultFrameContextCreator.createFrameContext();
			
			double value = embedSim.calculateSimilarity(frameContext, propertyContext);
			
			if(value > 0){
				matchingFrames.put(frameID, value);
			}
			
		}
		return MapUtil.sortMap(matchingFrames);
		
	}
	
	/**
	 * Apply the paper approach on a collection of properties and write the results to a file
	 * @param propFile
	 * @param resultFile
	 * @param maxMatch
	 * @param embedThreshold
	 * @throws IOException
	 */
	public void paperApproachForAllProps(String propFile, String resultFile, int maxMatch,double embedThreshold) throws IOException{
		//Property ID 	 Property Label 	 Matching Frame 	 Score
		
		PrintWriter out = new PrintWriter(new File(resultFile));
		
		out.println("Property ID \t Property Label \t Matching Frame \t	 Score");
		
		
		List<String> propList = GeneralUtil.loadPropertyIdsFromFile(propFile);
		
		for(String property:propList){
			System.out.println("Dealing with property ... " + property + ":"+  WikidataAPI.getLabel(property, WikidataLanguages.en));
			Map<String, Double> matchingFrames = paperApproach(property, WordEmbeddingSpace.LEVY_DEP, WordEmbeddingSpace.GOOGLE_NEWS, VectorCombinationMethod.ADD, embedThreshold);
			
			matchingFrames = MapUtil.getTopN(matchingFrames,maxMatch);
			
			for(Entry<String, Double> match : matchingFrames.entrySet()) {
				out.println(property + "\t" + WikidataAPI.getLabel(property, WikidataLanguages.en) + "\t" + match.getKey() + "\t" + match.getValue());
				out.flush();
			}
		}
		out.close();

	}
	
	public static void main(String[] args) throws IOException {
		PropertyFrameMatcher p = new PropertyFrameMatcher();
//		String property = "P1038";
//		Map<String, Double> matchingFrames = p.paperApproach(property, WordEmbeddingSpace.LEVY_DEP, WordEmbeddingSpace.GOOGLE_NEWS,VectorCombinationMethod.ADD, 0.7);
////		
//		System.out.println(MapUtil.getTopN(matchingFrames,5));
		double embedThreshold = 0.7;
		p.paperApproachForAllProps("groundtruth/Groud_Truth_Props.txt","results/FramePropertyAlignments/paper_method_"+embedThreshold+"_l_e.txt",10,embedThreshold);
		
		
//		p.baseline1("groundtruth/Groud_Truth_Props.txt", "results/FramePropertyAlignments/baseline_1_lemmatize_extend.txt", 10);
	}
	

	
}



