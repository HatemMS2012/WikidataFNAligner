package hms.alignment.frame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.saar.coli.salsa.reiter.framenet.Frame;
import de.saar.coli.salsa.reiter.framenet.FrameNotFoundException;
import de.saar.coli.salsa.reiter.framenet.LexicalUnit;
import hms.framenet.api.FrameNetDBConnector;
import hms.parser.AnnotatedWord;
import hms.util.NLPUtil;

public class FrameContextDefaultCreator extends FrameContextCreator{

	
	public FrameContextDefaultCreator() {
		
	}
	public FrameContextDefaultCreator(String frameID) {
		super(frameID);
		// TODO Auto-generated constructor stub
	}

	
	
	@Override
	/**
	 * Create the default frame context which consists of its label and the lUs
	 */
	public Collection<AnnotatedWord> createFrameContext() {
		
		Collection<AnnotatedWord> frameContext = new ArrayList<>();
		
		try {
			Frame frame = FrameNetDBConnector.getFrameNetInstance().getFrame(this.frameID);
			String label = frame.getName().toLowerCase().replace("_", " ");
			Collection<LexicalUnit> lus = frame.getLexicalUnits();
			
			if(label!=null){
				
				List<AnnotatedWord> annoatedLabel = NLPUtil.tokenizeAndPosTag(label);
				for(AnnotatedWord aw :annoatedLabel ){
					if(!aw.getPos().equals("IN")){
						if(aw.getPos().startsWith("V")){
							aw.setPos("v");
						}
						if(aw.getPos().startsWith("N")){
							aw.setPos("n");
						}
						if(aw.getPos().startsWith("J")){
							aw.setPos("a");
						}
						if(aw.getPos().startsWith("IN")){
							aw.setPos("prop");
						}
						if(aw.getPos().startsWith("RB")){
							aw.setPos("rb");
						}
						
						frameContext.add(aw);
					}
				}
				
			}
			for(LexicalUnit lu : lus){
				AnnotatedWord aw = new AnnotatedWord(lu.getLexemeString(),lu.getPartOfSpeechAbbreviation());
				frameContext.add(aw);

			}
			
			
		} catch (FrameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return frameContext;
	}

}
