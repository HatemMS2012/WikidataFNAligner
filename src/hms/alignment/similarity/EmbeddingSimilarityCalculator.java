package hms.alignment.similarity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import hms.embedding.EmbeddingUtil;
import hms.embedding.VectorCombinationMethod;
import hms.embedding.WordEmbeddingSpace;
import hms.parser.AnnotatedWord;

public class EmbeddingSimilarityCalculator extends ContextSimilarityCalculator {

	private VectorCombinationMethod vectorCombinationMethod;
	
	private EmbeddingUtil util ;
	
	public EmbeddingSimilarityCalculator(String wordEmbeddingSpace,VectorCombinationMethod vectorCombinationMethod) {
	
		this.vectorCombinationMethod = vectorCombinationMethod;
		this.util = new EmbeddingUtil(wordEmbeddingSpace);
		
	}
	@Override
	public double calculateSimilarity(Collection<AnnotatedWord> frameContext, Collection<AnnotatedWord> propertyContext) {
		
		//Combine the contexts
		//apply cosine similarity
		
		Set<String> frameContextSet = new HashSet<>();
		
		for(AnnotatedWord faw : frameContext){
			frameContextSet.add(faw.getWord());
			
		}
		
		Set<String> propertyContextSet = new HashSet<>();
		for(AnnotatedWord paw : propertyContext){
			propertyContextSet.add(paw.getWord());
			
		}

		return calculateSimilarity(frameContextSet, propertyContextSet);
	}

	
	private double calculateSimilarity(Set<String> text1, Set<String>  text2) {
		
		double similarity = 0;

		if(text1!=null && text2!=null){
	
			try {
				Collection<String> textList1 = getWordsWithDiscoEntriesAsList(text1);
				Collection<String> textList2 = getWordsWithDiscoEntriesAsList(text2);

				if(textList1.size() > 0 && textList2.size() > 0) {
					
					double[] v1 = util.combineEmbeddingVectors(new ArrayList<>(textList1), vectorCombinationMethod);
					double[] v2  = util.combineEmbeddingVectors(new ArrayList<>(textList2), vectorCombinationMethod);
					RealVector vec1 = new ArrayRealVector(v1);
					RealVector vec2 = new ArrayRealVector(v2);
					
					similarity = vec1.cosine(vec2);
					
				}
				
			} catch (IOException e) {

				e.printStackTrace();
			}
	
		}
		return similarity;
		
		
	}
	
	private Collection<String> getWordsWithDiscoEntriesAsList(Set<String>  text1) throws IOException{
	
		Collection<String> result = new HashSet<>();
		
		for(String w : text1){
			w  = w.trim();
			if(!w.contains(" ") && util.getWordVector(w)!=null){
				result.add(w.trim());
			}
			
		}
		return result;
	}
	
	
	public static void main(String[] args) {
		EmbeddingSimilarityCalculator s = new EmbeddingSimilarityCalculator(WordEmbeddingSpace.LEVY_DEP, VectorCombinationMethod.AVG);
		
		Set<String> s1 = new HashSet<>();
		s1.add("war");
		s1.add("love");
		
		Set<String> s2 = new HashSet<>();
		s2.add("weapon");
		s2.add("soldier");
		s2.add("woman");
		
		double sim = s.calculateSimilarity(s1, s2);
		System.out.println(sim);
	}
	
}
