package hms.alignment.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import hms.alignment.frame.FrameContextCreator;
import hms.alignment.frame.FrameContextDefaultCreator;
import hms.alignment.similarity.EmbeddingSimilarityCalculator;
import hms.embedding.VectorCombinationMethod;
import hms.embedding.WordEmbeddingSpace;
import hms.parser.AnnotatedWord;
import hms.util.NLPUtil;
import hms.wikidata.api.WikidataAPI;

public class PropertyContextCreatorMultiWord extends PropertyContextCreator {

	public PropertyContextCreatorMultiWord(String propertyID) {
		super(propertyID);
		// TODO Auto-generated constructor stub
	}
	
	

	public PropertyContextCreatorMultiWord() {
		super();
		// TODO Auto-generated constructor stub
	}



	@Override
	/**
	 * Create property context. The context consists basically of property label and aliases which
	 * are cleaned according to POS tags. Additionally, multi-word label/aliases are identified and
	 * deal with as a single word
	 */
	public Collection<AnnotatedWord> createPropertyContext() {
	Collection<AnnotatedWord> context = new ArrayList<>();
		
		String label = WikidataAPI.getLabel(this.propertyID, this.defaultLanguage);
		Collection<AnnotatedWord> words = getWordsStatisfyingPatterns(label);
		context.addAll(words);
		
		Collection<String> aliases = WikidataAPI.getAliases(this.propertyID, defaultLanguage);

		for(String a : aliases){
			words = getWordsStatisfyingPatterns(a);
			context.addAll(words);
			
		}
		context = removeDuplicatesFromContext(context);

		return context;
	}

	
	/**
	 * Parse text and identify words with semantic relevance according to predefined set of patterns
	 * @param text
	 * @return
	 */
	private Collection<AnnotatedWord> getWordsStatisfyingPatterns(String text) {
		Collection<AnnotatedWord> finalWordList = new HashSet<>();
		
		List<CoreLabel> aliasLemmas = NLPUtil.lemmatize(text);
		String pattern = "" ;
		String[] words = new String[aliasLemmas.size()];
		int i = 0 ;
		for(CoreLabel l:aliasLemmas){
		
			String word = l.lemma();
			words[i] = word;
			i++;
			String tag = l.tag();
			if(tag.startsWith("NN")){
				tag = "n";
			}
			if(tag.startsWith("V")){
				tag = "v";
				
			}
			if(tag.startsWith("JJ")){
				tag = "a";
			}
			if(tag.startsWith("IN")){
				tag = "prop";
			}
			if(tag.startsWith("RB")){
				tag = "rb";
			}
			pattern += tag + "-";
		}

		if(pattern.equals("v-")){
			finalWordList.add(new AnnotatedWord(words[0],"v"));
		}
		if(pattern.equals("n-")){
			finalWordList.add(new AnnotatedWord(words[0],"n"));
		}
		if(pattern.equals("a-")){
			finalWordList.add(new AnnotatedWord(words[0],"a"));
		}
		
		if(pattern.equals("a-n-n-")){
			finalWordList.add(new AnnotatedWord(words[0],"a"));
			finalWordList.add(new AnnotatedWord(words[1],"n"));

			finalWordList.add(new AnnotatedWord(words[2],"n"));

		}
		if(pattern.equals("n-n-") || pattern.equals("n-n-prop-")){
			finalWordList.add(new AnnotatedWord(words[0],"n"));
			finalWordList.add(new AnnotatedWord(words[1],"n"));
			finalWordList.add(new AnnotatedWord(words[0] + "_" + words[1],"n"));
		}
		if(pattern.equals("v-prop-")){
			finalWordList.add(new AnnotatedWord(words[0],"v"));
		}
		if(pattern.equals("n-prop-")){
			finalWordList.add(new AnnotatedWord(words[0],"n"));
		}
		if(pattern.equals("n-v-")){
			finalWordList.add(new AnnotatedWord(words[0],"n"));
			finalWordList.add(new AnnotatedWord(words[1],"v"));
		}
		if(pattern.equals("a-n-") || pattern.equals("a-n-prop-")){
			finalWordList.add(new AnnotatedWord(words[0],"a"));
			finalWordList.add(new AnnotatedWord(words[1],"n"));
			
			finalWordList.add(new AnnotatedWord(words[0] + "_" + words[1],"n"));
		}
	
		if(pattern.equals("v-n-prop-")){
			finalWordList.add(new AnnotatedWord(words[0]+" "+words[1],"v"));
			
		}
		if(pattern.equals("rb-v-prop-")){
			finalWordList.add(new AnnotatedWord(words[1],"v"));
			
		}
		if(pattern.equals("n-prop-n-")){
			finalWordList.add(new AnnotatedWord(words[0],"n"));
			finalWordList.add(new AnnotatedWord(words[2],"n"));
			
		}
		if(pattern.equals("n-prop-a-n-")){
			finalWordList.add(new AnnotatedWord(words[0],"n"));
			finalWordList.add(new AnnotatedWord(words[3],"n"));
			
		}
		if(pattern.equals("rb-n-")){
			finalWordList.add(new AnnotatedWord(words[1],"n"));
			
		}
		if(pattern.equals("v-n-")){
			if(!words[0].equals("have") && !words[0].equals("be")){
				finalWordList.add(new AnnotatedWord(words[0],"v"));	
			}
			finalWordList.add(new AnnotatedWord(words[1],"n"));
			
		}
//		System.out.println(text +": " + pattern + "- " + finalWordList);
		return finalWordList;
	}
	
	public static void main(String[] args) {
		
		PropertyContextCreatorEmbeddingMultiWord c = new PropertyContextCreatorEmbeddingMultiWord("P1340");
		c.setWordEmbeddingSpace(WordEmbeddingSpace.LEVY_DEP);
		c.setThreshold(0.7);
		System.out.println(c.createPropertyContext());
		
		FrameContextCreator f = new FrameContextDefaultCreator("Body_decoration");
		System.out.println(f.createFrameContext());
		
		EmbeddingSimilarityCalculator simM = new EmbeddingSimilarityCalculator(WordEmbeddingSpace.GOOGLE_NEWS, VectorCombinationMethod.ADD);
		System.out.println(simM.calculateSimilarity(f.createFrameContext(), c.createPropertyContext()));
	}
}
