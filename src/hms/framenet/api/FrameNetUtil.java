package hms.framenet.api;

import java.util.Collection;
import java.util.HashSet;

import de.saar.coli.salsa.reiter.framenet.Frame;
import de.saar.coli.salsa.reiter.framenet.FrameNet;
import de.saar.coli.salsa.reiter.framenet.LexicalUnit;

public class FrameNetUtil {

	
	public static Collection<String> getAllLus(){
		
		Collection<String> lus = new HashSet<>();
		
		FrameNet fn = FrameNetDBConnector.getFrameNetInstance();
		
		Collection<Frame> frames = fn.getFrames();
		
		for(Frame frame : frames){
			
			Collection<LexicalUnit> frameLUs = frame.getLexicalUnits();
			
			for(LexicalUnit lu : frameLUs){
				
				String luName = lu.getName();
				lu.getLexemeString();
				lus.add(lu.getLexemeString());
						
			}
		}
		return lus;
		
	}
	
	public static void main(String[] args) {
		Collection<String> lus = getAllLus();
		
		System.out.println(lus.size());
	}
}
