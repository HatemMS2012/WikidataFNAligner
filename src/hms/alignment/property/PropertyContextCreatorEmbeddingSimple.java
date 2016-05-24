package hms.alignment.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import hms.embedding.EmbeddingUtil;
import hms.embedding.WordEmbeddingSpace;
import hms.parser.AnnotatedWord;

public class PropertyContextCreatorEmbeddingSimple extends PropertyContextCreator {

	
	private String  wordEmbeddingSpace;
	private double threshold;
	
	public PropertyContextCreatorEmbeddingSimple(String propertyID,boolean lemmatize,boolean extendedContext) {
		super(propertyID,lemmatize, extendedContext);
		// TODO Auto-generated constructor stub
	}

	
	public PropertyContextCreatorEmbeddingSimple() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	/**
	 * Create an enriched context for the property. First obtain the default property context and then 
	 * identify for each word in the context a set of similar words based on some embedding space
	 */
	public Collection<AnnotatedWord> createPropertyContext() {
		
		PropertyContextCreator defaultCreator = new PropertyRawContextCreator(this.propertyID,this.lemmatize,this.extendedContext);
		
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
		PropertyContextCreatorEmbeddingSimple c = new PropertyContextCreatorEmbeddingSimple(propertyID2,true,false);
		c.setWordEmbeddingSpace(WordEmbeddingSpace.LEVY_DEP);
		c.setThreshold(0.7);
		System.out.println(c.createPropertyContext());
		
		
		PropertyContextCreatorEmbeddingSimple c2 = new PropertyContextCreatorEmbeddingSimple(propertyID2,true,true);
		c2.setWordEmbeddingSpace(WordEmbeddingSpace.LEVY_DEP);
		c2.setThreshold(0.7);
		System.out.println(c2.createPropertyContext());
	}
	

}
