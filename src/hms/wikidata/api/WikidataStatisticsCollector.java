package hms.wikidata.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import edu.stanford.nlp.ling.CoreLabel;
import hms.util.NLPUtil;

public class WikidataStatisticsCollector {

	private static final String PROPERTY_LIST_FILE = "C:/Devlopement/workspace_hanish/FactSpotter/input/Full_Prop_List.txt";
	
	/**
	 * Read property file and calculate of each property the number of classes for ARG1 and ARG2 and store the results in a file
	 * @param propertyListFile
	 * @param maxIntsances
	 * @param statisticsFile
	 */
	public static void getClassesPerPropertyArgument(String propertyListFile, int maxIntsances, String statisticsFile){
		
		List<String> propIDList = loadPropertiesFromFile(PROPERTY_LIST_FILE);
		
		try {
			PrintWriter out = new PrintWriter(new File(statisticsFile));
			
			
			for(String prop : propIDList){
				
				String label = WikidataAPI.getLabel(prop, WikidataLanguages.en);
				
				Collection<String> classesArg1 = WikidataAPI.getPropertyARG1Classes(prop, maxIntsances, WikidataLanguages.en);
				Collection<String> classesArg2 = WikidataAPI.getPropertyARG2Classes(prop, maxIntsances, WikidataLanguages.en);
				
				System.out.println(prop + "\t" + label + "\t" + classesArg1.size() + "\t" + classesArg2.size() + "\t" + classesArg1 + "\t" + classesArg2);
				
				out.println(prop + "\t" + label + "\t" + classesArg1.size() + "\t" + classesArg2.size() + "\t" + classesArg1 + "\t" + classesArg2);
				
				out.flush();
			}
			out.close();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * Load property ID list from a file
	 * @param file
	 * @return
	 */
	public static List<String> loadPropertiesFromFile(String file){
		List<String> propIdList = new ArrayList<>();
		
		FileInputStream fstream;
		try {
			
			fstream = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String strLine;

			// Read File Line By Line
			
			while ((strLine = br.readLine()) != null) {
				propIdList.add(strLine.split("\t")[0]);
			}
			br.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		return propIdList;
	}
	
	/**
	 * Get the statistics file of property and argument classes and extract the head words for the classes
	 * @param statisticsFile
	 */
	public static void getClassesPerPropertyArgumentWithHeadWords(String statisticsFile, String outputFile){
		FileInputStream fstream;
		try {
			PrintWriter out = new PrintWriter(new File(outputFile));

			fstream = new FileInputStream(statisticsFile);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String strLine;

			// Read File Line By Line
			
			while ((strLine = br.readLine()) != null) {
				
				
				String[] lineAsArr = strLine.split("\t");
				String propID = lineAsArr[0];
				String label = lineAsArr[1];
				
				System.out.println("Dealing with: " + label);
				
				Collection<String> arg1Classes = convertTextToCollection(lineAsArr[4]);
				Collection<String> arg2Classes = convertTextToCollection(lineAsArr[5]);
				Collection<String> arg1ClassesHeadwords = extractHeadWords(arg1Classes);
				Collection<String> arg2ClassesHeadwords = extractHeadWords(arg2Classes);
				
				out.println(propID + "\t" + label + "\t" + arg1ClassesHeadwords.size() + "\t" + arg2ClassesHeadwords.size() + "\t" + arg1ClassesHeadwords + "\t" + arg2ClassesHeadwords);
				
				System.out.print(propID + "\t" + label + "\t" + arg1ClassesHeadwords.size() + "\t" + arg2ClassesHeadwords.size() + "\t" + arg1ClassesHeadwords + "\t" + arg2ClassesHeadwords);
				
				out.flush();
				
			}
			br.close();
			out.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Extract headwords for phrases contained in a list
	 * @param phraseList
	 */
	private static Collection<String> extractHeadWords(Collection<String> phraseList){
		Collection<String> headwordSet = new HashSet<>();
		
		for(String phrase: phraseList){
			
			Collection<String> headwords = NLPUtil.identifyHeadWord(phrase);
			
			if(headwords.size() > 0){
				String firstHeadword = (String) headwords.toArray()[0];
				
				List<CoreLabel> lemma = NLPUtil.lemmatize(firstHeadword);
				CoreLabel firstLemma = lemma.get(0);
				if(firstLemma.tag().startsWith("N")){
					headwordSet.add(firstLemma.lemma());
					System.out.println(phrase + "\t" + firstLemma.lemma());
					System.out.println();

				}
				
				
			
			}
		}
		
		return headwordSet;
		
	}
	
	/**
	 * Take a list of phrases in text format and convert it into collection
	 * @param text
	 * @return
	 */
	private static Collection<String> convertTextToCollection(String text){
		Collection<String> result = new HashSet<>();
		
		String[] textArr = text.replace("[", "").replace("]", "").trim().split(",");
		
		for(String t : textArr){
			result.add(t.trim());
		}
		
		return result ;
	}

	public static void main(String[] args) {
//		getClassesPerPropertyArgument(PROPERTY_LIST_FILE, 2000, "output/stats/property_arg_classes_2000.txt");
		
		getClassesPerPropertyArgumentWithHeadWords("output/stats/property_arg_classes_2000.txt","output/stats/property_arg_classes_head_2000.txt");
		
//		String phrases = "[farmers' market, liberal arts colleges in the united states, historic district in the united states, dwelling, historic site, comune of italy]";
//		System.out.println(extractHeadWords(convertTextToCollection(phrases)));
	}
}
