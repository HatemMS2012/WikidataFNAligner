package hms.alignment.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import hms.embedding.EmbeddingUtil;
import hms.embedding.WordEmbeddingSpace;
import hms.parser.AnnotatedWord;

public class PropertyContextCreatorEmbeddingMultiWord extends PropertyContextCreator {


	private String  wordEmbeddingSpace;
	private double threshold;

	
	public PropertyContextCreatorEmbeddingMultiWord(String propertyID,boolean lemmatize,boolean extendedContext) {
		super(propertyID,lemmatize, extendedContext);
	}
	
	

	public PropertyContextCreatorEmbeddingMultiWord() {
		super();

	}



	@Override
	public Collection<AnnotatedWord> createPropertyContext() {
		
		PropertyContextCreator defaultCreator = new PropertyContextCreatorMultiWord(this.propertyID,this.lemmatize,this.extendedContext);
		
		Collection<AnnotatedWord> context = defaultCreator.createPropertyContext();
		
		Collection<AnnotatedWord> finalContext = new ArrayList<>(context);
		
		EmbeddingUtil embUtil = new EmbeddingUtil(wordEmbeddingSpace);
		
		for(AnnotatedWord c : context){
			
			List<String> similarWords = embUtil.getEmbeddingSimilarWords(c.getWord(), threshold);
			for(String w : similarWords){
				
				Collection<AnnotatedWord> words = null;
				if(this.lemmatize){
					
					words = getLemmatizedFilteredWords(w);

				}
				else{
					
					words = getFilteredWords(w);

				}
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


	public static void main(String[] args) {
		String propertyID2 = "P69";
		PropertyContextCreatorEmbeddingMultiWord c = new PropertyContextCreatorEmbeddingMultiWord(propertyID2,false,false);
		c.setWordEmbeddingSpace(WordEmbeddingSpace.LEVY_DEP);
		c.setThreshold(0.7);
		System.out.println(c.createPropertyContext());
		
		
		PropertyContextCreatorEmbeddingMultiWord c2 = new PropertyContextCreatorEmbeddingMultiWord(propertyID2,true,false);
		c2.setWordEmbeddingSpace(WordEmbeddingSpace.LEVY_DEP);
		c2.setThreshold(0.7);
		System.out.println(c2.createPropertyContext());
	}


}
