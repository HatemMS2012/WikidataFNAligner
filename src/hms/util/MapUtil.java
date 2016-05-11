package hms.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class MapUtil implements Comparator<String> {

	
	private static NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
	private static DecimalFormat df = (DecimalFormat)nf;
	static{
		df.setMaximumFractionDigits(3);
		Locale.setDefault(Locale.ENGLISH);

	}
	
	public double formatDouble(double x){
		
		return Double.valueOf(df.format(x));
	}
	
    Map<String, Double> base;
    public MapUtil(Map<String, Double> base) {
        this.base = base;
    }
 
    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
    
    
	public static TreeMap<String, Double> sortMap(Map<String, Double> candidateFrames) {
		MapUtil bvc = new MapUtil(candidateFrames);
		TreeMap<String, Double> sorted_map = new TreeMap<String,Double>(bvc);
		sorted_map.putAll(candidateFrames);
		return sorted_map;
	}
	
	public static Map<String, Double> getTopN(Map<String, Double> map, int max){
		
		//Given that the map is already sorted
		Map<String, Double> result = new HashMap<>();
		
		int i = 1 ;
		for(Entry<String, Double> e: map.entrySet()){
			
			if(i > max){
				break;
				
			}
			else{
				result.put(e.getKey(), Double.valueOf(df.format(e.getValue())));
				i ++;
			}
		}
		return sortMap(result);
		
		
	}
	
}
