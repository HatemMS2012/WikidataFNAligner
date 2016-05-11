package hms.alignment.similarity;

import java.util.Collection;

import hms.parser.AnnotatedWord;

public abstract class ContextSimilarityCalculator {
	
	
	public abstract double calculateSimilarity(Collection<AnnotatedWord> frameContext, Collection<AnnotatedWord> propertyContext);
	
	
}
