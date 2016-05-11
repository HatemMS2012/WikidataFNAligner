package hms.embedding;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.apache.lucene.index.CorruptIndexException;

import de.linguatools.disco.CorruptConfigFileException;
import de.linguatools.disco.DISCO;
import de.linguatools.disco.ReturnDataBN;
import de.linguatools.disco.WrongWordspaceTypeException;

public class EmbeddingUtil {

	//DISCO defaults
	private static DISCO disco ;

	
	public EmbeddingUtil(String wordEmbeddingSpace){
		try {
			
			disco = new DISCO(wordEmbeddingSpace.toString(), false);
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CorruptConfigFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Generate a list of words that are similar to an input word based on word embeddings
	 * @param word
	 * @param minScore
	 * @return
	 */
	public List<String> getEmbeddingSimilarWords(String word, double minScore){
		 
		List<String> similarWordsList = new ArrayList<String>();
		
		ReturnDataBN similarWords;
		try {
			similarWords = disco.similarWords(word);
			if(similarWords != null){
				for (int i = 0; i < similarWords.words.length; i++) {
					if(Double.valueOf(similarWords.values[i]) >=minScore){
										
						similarWordsList.add(i, similarWords.words[i]);
					}
	
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WrongWordspaceTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return similarWordsList;
		
		
	}
	
	
	public double[] combineEmbeddingVectors(List<String> words, VectorCombinationMethod method){
		if(words.size() == 0){
			return null;
		}
		
		if(words.size() == 1){
			
			return getWordVector(words.get(0));
			
		}
		
		//Initialize the final vector 
		double[] finalVector = null;
		int indexOfFirstNonNullVector = -1;
		
		for (int j =0; j < words.size(); j++) {
			
			double[] temp = getWordVector(words.get(j));
			
			if(temp !=null){
				indexOfFirstNonNullVector = j;
				finalVector =  temp;
				break;
			}
		}
		
		
//		System.out.println(Arrays.toString(finalVector));

		double countNonNullVectors = 0 ; //for the average approach
		
		if(method.equals(VectorCombinationMethod.AVG)){
			for(String w: words){
				
				if(getWordVector(w)!=null){
					
					countNonNullVectors ++;
				}
			}
		}
			
		for (int j = 0; j < words.size(); j++) {
			
			if(indexOfFirstNonNullVector == j){
				continue;
			}
			
			double[] wordVector = getWordVector(words.get(j));
			
//			System.out.println(Arrays.toString(wordVector));

			if(wordVector != null){
				if(method.equals(VectorCombinationMethod.ADD) || method.equals(VectorCombinationMethod.AVG)){
					for (int i = 0; i < wordVector.length; i++) {

						finalVector[i] += wordVector[i];	
					}
				}
				if(method.equals(VectorCombinationMethod.MINUS)){
					for (int i = 0; i < wordVector.length; i++) {

						finalVector[i] -= wordVector[i];	
					}
				}
				if(method.equals(VectorCombinationMethod.MULT)){
					for (int i = 0; i < wordVector.length; i++) {

						finalVector[i] *= wordVector[i];	
					}
				}
				
			}
		}
		
		if(method.equals(VectorCombinationMethod.AVG)){
			RealVector vec1 = new ArrayRealVector(finalVector);
			finalVector = vec1.mapDivide(countNonNullVectors).toArray();
		}
		return finalVector;
		
		
	}
	
	public double[] getWordVector(String word){
		
		try {
			
			Map<String, Float> wordVector = disco.getWordvector(word);
			if(wordVector != null){
				double[] wordVec = new double[wordVector.size()];
				
				for(Entry<String, Float> e:wordVector.entrySet()){
					wordVec[Integer.valueOf(e.getKey())-1] = e.getValue();
				}
				
				return wordVec;
			}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return null;
	}
	
	public static void main(String[] args) {
		EmbeddingUtil eU = new EmbeddingUtil(WordEmbeddingSpace.LEVY_DEP);
		System.out.println(eU.getEmbeddingSimilarWords("take", 0.6));
		
		
	}
}
