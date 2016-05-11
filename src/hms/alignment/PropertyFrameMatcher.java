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
import hms.alignment.property.PropertyContextCreatorEmbeddingMultiWord;
import hms.alignment.property.PropertyContextCreatorEmbeddingSimple;
import hms.alignment.property.PropertyContextDefaultCreator;
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
		

		PropertyContextCreatorEmbeddingMultiWord propertyEmbeddingContextCalculator = new PropertyContextCreatorEmbeddingMultiWord();

		propertyEmbeddingContextCalculator.setPropertyID(propertyID);

		propertyEmbeddingContextCalculator.setWordEmbeddingSpace(wordEmbeddingSpaceForContextExpansion);
		propertyEmbeddingContextCalculator.setThreshold(expansionThreshold);
		Collection<AnnotatedWord> propertyContext = propertyEmbeddingContextCalculator.createPropertyContext();
		
		System.out.println("property context: " + propertyContext);
		

		FrameNet fn = FrameNetDBConnector.getFrameNetInstance();
		
		for(Frame f : fn.getFrames()){

			String frameID = f.getName();
			
			if(frameID.equals("Location_of_light")){
				System.out.println();
			}
			
			
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
		p.paperApproachForAllProps("groundtruth/Groud_Truth_Props.txt","results/FramePropertyAlignments/paper_method_"+embedThreshold+"_temp.txt",10,embedThreshold);
	}
	
	public static void main1(String[] args) {
		
		PropertyFrameMatcher p = new PropertyFrameMatcher();
		String prop = "P1038";
		String frameID = "Killing";
		FrameContextCreator defaultFrameContextCreator = new FrameContextDefaultCreator(frameID);
		PropertyContextCreator defaultPropertyContextCreator = new PropertyContextDefaultCreator(prop);
		
		System.out.println("Overlap Similarity");
		ContextSimilarityCalculator overlap = new OverlapSimilarityCalculator();
		double sim = p.calculateFramePropertySimilarity(defaultFrameContextCreator,defaultPropertyContextCreator,overlap);
		System.out.println(sim);
		System.out.println("............");
		
		System.out.println("..Embedding Similarity.");
		ContextSimilarityCalculator embedSim = new EmbeddingSimilarityCalculator(WordEmbeddingSpace.LEVY_DEP, VectorCombinationMethod.ADD);
		sim = p.calculateFramePropertySimilarity(defaultFrameContextCreator,defaultPropertyContextCreator,embedSim);
		System.out.println(sim);
		
		
		
		String property = "P1038";
		System.out.println("-.-----Default Property Context + Overlap------");
		System.out.println(p.getMatchingFrames(property,defaultPropertyContextCreator,defaultFrameContextCreator,overlap));
		System.out.println("-.------------");
		
		System.out.println("-.-----Default Property Context + Embedding------");
		System.out.println(p.getMatchingFrames(property,defaultPropertyContextCreator,defaultFrameContextCreator,embedSim));
		System.out.println("-.------------");
//		
		
//		
//		System.out.println("-.-----Multi-Word Property Context------");
//		System.out.println(p.getMatchingFrames(property, new PropertyContextCreatorMultiWord(),new FrameContextDefaultCreator()));
//		System.out.println("-.------------");
//
		System.out.println("-.-----Embeding Simple Property Context + overlap------");
		PropertyContextCreatorEmbeddingSimple propertyEmbeddingContextCalculator = new PropertyContextCreatorEmbeddingSimple();
		propertyEmbeddingContextCalculator.setWordEmbeddingSpace(WordEmbeddingSpace.LEVY_DEP);
		propertyEmbeddingContextCalculator.setThreshold(0.6);
		System.out.println(p.getMatchingFrames(property,propertyEmbeddingContextCalculator,defaultFrameContextCreator,overlap));
		System.out.println("-.------------");
		
		
		System.out.println("-.-----Embeding Simple Property Context + Embedding SImilarity------");
		System.out.println(p.getMatchingFrames(property,propertyEmbeddingContextCalculator,defaultFrameContextCreator,embedSim));
		System.out.println("-.------------");
		
		
//		System.out.println("-.-----Embeding Multi-Word Property Context------");
//		PropertyContextCreatorEmbeddingMultiWord pcc2 = new PropertyContextCreatorEmbeddingMultiWord();
//		pcc.setWordEmbeddingSpace(WordEmbeddingSpace.LEVY_DEP);
//		pcc.setThreshold(0.6);
//		System.out.println(p.getMatchingFrames(property,pcc2,new FrameContextDefaultCreator()));
//		System.out.println("-.------------");
//		
	}
	
}



