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

	
	
	public PropertyContextCreatorMultiWord(String propertyID, boolean lemmatize,boolean extendedContext) {
		super(propertyID,lemmatize, extendedContext);
		// TODO Auto-generated constructor stub
	}

	public PropertyContextCreatorMultiWord() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	/**
	 * Create property context. The context consists basically of property label
	 * and aliases which are cleaned according to POS tags. Additionally,
	 * multi-word label/aliases are identified and deal with as a single word
	 */
	public Collection<AnnotatedWord> createPropertyContext() {
		
		Collection<String> allMetaData = new HashSet<>();
		
		String label = WikidataAPI.getLabel(this.propertyID, this.defaultLanguage);
		allMetaData.add(label);
		Collection<String> aliases = WikidataAPI.getAliases(this.propertyID, defaultLanguage);
		allMetaData.addAll(aliases);
		
		if(this.extendedContext){
			//Add subject of the property and its aliases to the context
			String subID = WikidataAPI.getSubjectOfProperty(this.propertyID);
			if(subID != null){
			
				String subLabel = WikidataAPI.getLabel(subID, defaultLanguage);
				allMetaData.add(subLabel);
				Collection<String> subAliases = WikidataAPI.getAliases(subID, defaultLanguage);
				allMetaData.addAll(subAliases);
			}
						
			//Add further information from the talk page
			Collection<String> talkPageMetaDataList = new HashSet<>();
			talkPageMetaDataList.addAll(WikidataAPI.getPropertyAllowedValuesItems(this.propertyID));
			talkPageMetaDataList.addAll(WikidataAPI.getPropertyDomainItems(this.propertyID));
			talkPageMetaDataList.addAll(WikidataAPI.getPropertyRepresentingItems(this.propertyID));
			
			for(String talPageMetaDataID : talkPageMetaDataList){
				if(!talPageMetaDataID.equals("Q5") && !talPageMetaDataID.equals("Q16334295") && !talPageMetaDataID.equals("Q215627")) { //excludes human, person, ...
					String itemLabel = WikidataAPI.getLabel(talPageMetaDataID, defaultLanguage);
					if(itemLabel!=null){
						allMetaData.add(itemLabel);
					}
					Collection<String> itemAliases = WikidataAPI.getAliases(talPageMetaDataID, defaultLanguage);
					allMetaData.addAll(itemAliases);
				}
			}
		}
		
		
		Collection<AnnotatedWord> context = new ArrayList<>(); //Final context

		for(String a : allMetaData){
		
			context.addAll(getWordsStatisfyingPatterns(a,this.lemmatize));	
					
		}
		
		context = removeDuplicatesFromContext(context);

		return context;
		
	}

	/**
	 * Parse text and identify words with semantic relevance according to
	 * predefined set of patterns
	 * 
	 * @param text
	 * @return
	 */
	private Collection<AnnotatedWord> getWordsStatisfyingPatterns(String text, boolean lemmatize) {
		Collection<AnnotatedWord> finalWordList = new HashSet<>();

		List<CoreLabel> aliasLemmas = NLPUtil.lemmatize(text);
		String pattern = "";
		String[] words = new String[aliasLemmas.size()];
		int i = 0;
		for (CoreLabel l : aliasLemmas) {
			
			String word = null;
			if(lemmatize){
				word = l.lemma();
			}
			else{
				word = l.originalText();
			}
			words[i] = word;
			i++;
			String tag = l.tag();
			if (tag.startsWith("NN")) {
				tag = "n";
			}
			if (tag.startsWith("V")) {
				tag = "v";

			}
			if (tag.startsWith("JJ")) {
				tag = "a";
			}
			if (tag.startsWith("IN")) {
				tag = "prop";
			}
			if (tag.startsWith("RB")) {
				tag = "rb";
			}
			pattern += tag + "-";
		}

		if (pattern.equals("v-")) {
			finalWordList.add(new AnnotatedWord(words[0], "v"));
		}
		if (pattern.equals("n-")) {
			finalWordList.add(new AnnotatedWord(words[0], "n"));
		}
		if (pattern.equals("a-")) {
			finalWordList.add(new AnnotatedWord(words[0], "a"));
		}

		if (pattern.equals("a-n-n-")) {
			finalWordList.add(new AnnotatedWord(words[0], "a"));
			finalWordList.add(new AnnotatedWord(words[1], "n"));

			finalWordList.add(new AnnotatedWord(words[2], "n"));

		}
		if (pattern.equals("n-n-") || pattern.equals("n-n-prop-")) {
			finalWordList.add(new AnnotatedWord(words[0], "n"));
			finalWordList.add(new AnnotatedWord(words[1], "n"));
			finalWordList.add(new AnnotatedWord(words[0] + "_" + words[1], "n"));
		}
		if (pattern.equals("v-prop-")) {
			finalWordList.add(new AnnotatedWord(words[0], "v"));
		}
		if (pattern.equals("n-prop-")) {
			finalWordList.add(new AnnotatedWord(words[0], "n"));
		}
		if (pattern.equals("n-v-")) {
			finalWordList.add(new AnnotatedWord(words[0], "n"));
			finalWordList.add(new AnnotatedWord(words[1], "v"));
		}
		if (pattern.equals("a-n-") || pattern.equals("a-n-prop-")) {
			finalWordList.add(new AnnotatedWord(words[0], "a"));
			finalWordList.add(new AnnotatedWord(words[1], "n"));

			finalWordList.add(new AnnotatedWord(words[0] + "_" + words[1], "n"));
		}

		if (pattern.equals("v-n-prop-")) {
			finalWordList.add(new AnnotatedWord(words[0] + " " + words[1], "v"));

		}
		if (pattern.equals("rb-v-prop-")) {
			finalWordList.add(new AnnotatedWord(words[1], "v"));

		}
		if (pattern.equals("n-prop-n-")) {
			finalWordList.add(new AnnotatedWord(words[0], "n"));
			finalWordList.add(new AnnotatedWord(words[2], "n"));

		}
		if (pattern.equals("n-prop-a-n-")) {
			finalWordList.add(new AnnotatedWord(words[0], "n"));
			finalWordList.add(new AnnotatedWord(words[3], "n"));

		}
		if (pattern.equals("rb-n-")) {
			finalWordList.add(new AnnotatedWord(words[1], "n"));

		}
		if (pattern.equals("v-n-")) {
			if (!words[0].equals("have") && !words[0].equals("be")) {
				finalWordList.add(new AnnotatedWord(words[0], "v"));
			}
			finalWordList.add(new AnnotatedWord(words[1], "n"));

		}
		// System.out.println(text +": " + pattern + "- " + finalWordList);
		return finalWordList;
	}

	public static void main(String[] args) {

		PropertyContextCreatorMultiWord c = new PropertyContextCreatorMultiWord("P22",true,false);
		System.out.println(c.createPropertyContext());
		
		PropertyContextCreatorMultiWord c2 = new PropertyContextCreatorMultiWord("P22",true,true);
		System.out.println(c2.createPropertyContext());

	}


}
