
package hms.alignment.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import hms.parser.AnnotatedWord;
import hms.wikidata.api.WikidataAPI;

public class PropertyRawContextCreator extends PropertyContextCreator {

	public PropertyRawContextCreator(String propertyID, boolean lemmatize, boolean extendedContext) {
		super(propertyID, lemmatize, extendedContext);
	}

	public PropertyRawContextCreator() {
		super();
	}

	/**
	 * The property context consists of lemmatized version of its label and the
	 * set of aliases
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
			
			if(this.lemmatize){
				context.addAll(getLemmatizedFilteredWords(a));	
			}
			else{
				context.addAll(getFilteredWords(a));	
//				System.out.println();
			}
			
		}
		
		context = removeDuplicatesFromContext(context);

		return context;
		
	}

	public static void main(String[] args) {

		PropertyRawContextCreator c = new PropertyRawContextCreator("P22",true,false);
		System.out.println(c.createPropertyContext());
		PropertyRawContextCreator c2 = new PropertyRawContextCreator("P22",true,true);
		System.out.println(c2.createPropertyContext());
		
	}
}
