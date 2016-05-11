
package hms.alignment.property;

import java.util.ArrayList;
import java.util.Collection;

import hms.parser.AnnotatedWord;
import hms.wikidata.api.WikidataAPI;

public class PropertyContextDefaultCreator  extends PropertyContextCreator{

	public PropertyContextDefaultCreator(String propertyID) {
		super(propertyID);
	}
	
	

	public PropertyContextDefaultCreator() {
		super();
		// TODO Auto-generated constructor stub
	}



	@Override
	/**
	 * The default property context consists of lemmatized version of its label and the set of aliases
	 */
	public Collection<AnnotatedWord> createPropertyContext() {

		String label = WikidataAPI.getLabel(this.propertyID, this.defaultLanguage);
		
		Collection<AnnotatedWord> context = new ArrayList<>();

		context.addAll(getContextWordSimple(label));
		
		Collection<String> aliases = WikidataAPI.getAliases(this.propertyID, defaultLanguage);

		for(String a : aliases){
			
			context.addAll(getContextWordSimple(a));
		}
		context = removeDuplicatesFromContext(context);
		
		return context;
	}

	
	public static void main(String[] args) {
		
		PropertyContextDefaultCreator c = new PropertyContextDefaultCreator("P69");
		System.out.println(c.createPropertyContext());
		
	}
}
