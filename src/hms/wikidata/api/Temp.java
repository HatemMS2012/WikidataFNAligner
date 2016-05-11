package hms.wikidata.api;

import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import hms.util.NLPUtil;

public class Temp {

	
	public static void main(String[] args) {
		
		
		System.out.println(NLPUtil.identifyHeadWord("space mission"));
		
		List<CoreLabel> lemmas = NLPUtil.lemmatize("space mission");
		
		for(CoreLabel l : lemmas){
			
			System.out.println(l.lemma() + "\t" + l.tag());
		}
	}
}
