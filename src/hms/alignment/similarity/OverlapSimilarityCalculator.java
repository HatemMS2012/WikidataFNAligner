package hms.alignment.similarity;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import hms.parser.AnnotatedWord;

public class OverlapSimilarityCalculator extends ContextSimilarityCalculator{

	@Override
	public double calculateSimilarity(Collection<AnnotatedWord> frameContext, Collection<AnnotatedWord> propertyContext) {
		
		
		Set<String> frameContextSet = new HashSet<>();
		
		for(AnnotatedWord faw : frameContext){
			frameContextSet.add(faw.getWord());
			
		}
		
		Set<String> propertyContextSet = new HashSet<>();
		for(AnnotatedWord paw : propertyContext){
			propertyContextSet.add(paw.getWord());
			
		}
		
		frameContextSet.retainAll(propertyContextSet);
		

		
		return frameContextSet.size();
		
		
	}

	
	
}
