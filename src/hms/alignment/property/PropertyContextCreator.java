package hms.alignment.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.stanford.nlp.ling.CoreLabel;
import hms.parser.AnnotatedWord;
import hms.util.NLPUtil;
import hms.wikidata.api.WikidataLanguages;

public abstract class PropertyContextCreator {

	protected boolean lemmatize ;
	protected boolean extendedContext ; //Extend context with Subject Items and Talk pages
	protected String propertyID; 
	protected WikidataLanguages defaultLanguage = WikidataLanguages.en;

	public abstract Collection<AnnotatedWord> createPropertyContext();

	public PropertyContextCreator() {
	
	}
	
	public PropertyContextCreator(String propertyID,boolean lemmatize, boolean extendedContext) {
		
		this.propertyID = propertyID;
		this.lemmatize = lemmatize;
		this.extendedContext = extendedContext;
	}

	/**
	 * Helper method to lemmatize an input text
	 * @param context
	 * @param text
	 */
	protected Collection<AnnotatedWord> getLemmatizedFilteredWords(String text) {
		Collection<AnnotatedWord> context = new ArrayList<>();
		
		if(text!=null){
			List<CoreLabel> aliasLemmas = NLPUtil.lemmatize(text);
			
			for(CoreLabel l:aliasLemmas){
			
				String word = l.lemma();
				
				String tag = l.tag();
				
				if(!tag.equals("IN") && !tag.equals("CC")){ //remove propositions and coordinators
					if(tag.startsWith("V")){
						tag = "v";
					}
					if(tag.startsWith("N")){
						tag = "n";
					}
					if(tag.startsWith("J")){
						tag = "a";
					}
					if(tag.startsWith("IN")){
						tag = "prop";
					}
					if(tag.startsWith("RB")){
						tag = "rb";
					}
					context.add(new AnnotatedWord(word,tag));
				}
			}
		}
		return context;
	}
	
	protected Collection<AnnotatedWord> getFilteredWords(String text) {
		Collection<AnnotatedWord> context = new ArrayList<>();
		
		if(text!=null){
			List<CoreLabel> aliasLemmas = NLPUtil.lemmatize(text);
			
			for(CoreLabel l:aliasLemmas){
			
				String word = l.originalText();
				
				String tag = l.tag();
				
				if(!tag.equals("IN") && !tag.equals("CC")){ //remove propositions and coordinators
					if(tag.startsWith("V")){
						tag = "v";
					}
					if(tag.startsWith("N")){
						tag = "n";
					}
					if(tag.startsWith("J")){
						tag = "a";
					}
					if(tag.startsWith("IN")){
						tag = "prop";
					}
					if(tag.startsWith("RB")){
						tag = "rb";
					}
					context.add(new AnnotatedWord(word,tag));
				}
			}
		}
		return context;
	}
	
	/**
	 * Remove duplicate entries from the context
	 * @param context
	 * @return
	 */
	public static Collection<AnnotatedWord> removeDuplicatesFromContext(Collection<AnnotatedWord> context){
		Collection<AnnotatedWord> cleanedContext = new ArrayList<>();
		
		Set<String> words = new HashSet<>();
		
		for(AnnotatedWord aw : context){
			if(!aw.getWord().equals("#")){
				words.add(aw.getWord()+"#"+aw.getPos());
			}
		}
		
		for(String w : words){
			String wText = w.split("#")[0];
			String tag = w.split("#")[1];
			cleanedContext.add(new AnnotatedWord(wText,tag));
		}
		return cleanedContext;
		
	}

	public String getPropertyID() {
		return propertyID;
	}

	public void setPropertyID(String propertyID) {
		this.propertyID = propertyID;
	}
	
	
}
