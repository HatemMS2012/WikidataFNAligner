package hms.alignment.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import hms.embedding.EmbeddingUtil;
import hms.parser.AnnotatedWord;

public class PropertyContextCreatorEmbeddingMultiWord extends PropertyContextCreator {


	private String  wordEmbeddingSpace;
	private double threshold;

	
	public PropertyContextCreatorEmbeddingMultiWord(String propertyID) {
		super(propertyID);
		// TODO Auto-generated constructor stub
	}
	
	

	public PropertyContextCreatorEmbeddingMultiWord() {
		super();
		// TODO Auto-generated constructor stub
	}



	@Override
	public Collection<AnnotatedWord> createPropertyContext() {
		
		PropertyContextCreator defaultCreator = new PropertyContextCreatorMultiWord(this.propertyID);
		
		Collection<AnnotatedWord> context = defaultCreator.createPropertyContext();
		
		Collection<AnnotatedWord> finalContext = new ArrayList<>(context);
		
		EmbeddingUtil embUtil = new EmbeddingUtil(wordEmbeddingSpace);
		
		for(AnnotatedWord c : context){
			
			List<String> similarWords = embUtil.getEmbeddingSimilarWords(c.getWord(), threshold);
			for(String w : similarWords){
				Collection<AnnotatedWord> words = getContextWordSimple(w);
				finalContext.addAll(words);
//				finalContext.add(new AnnotatedWord(w,"n"));
			}
		}
		finalContext = removeDuplicatesFromContext(finalContext);
		
		return finalContext ;
	}

	public String getWordEmbeddingSpace() {
		return wordEmbeddingSpace;
	}

	public void setWordEmbeddingSpace(String wordEmbeddingSpace) {
		this.wordEmbeddingSpace = wordEmbeddingSpace;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
}
