package hms.alignment.frame;

import java.util.Collection;

import hms.parser.AnnotatedWord;

public abstract class FrameContextCreator {
	
	protected String frameID;
	
	public abstract Collection<AnnotatedWord> createFrameContext();

	public FrameContextCreator() {
	
	}
	public FrameContextCreator(String frameID) {
		super();
		this.frameID = frameID;
	}

	public String getFrameID() {
		return frameID;
	}

	public void setFrameID(String frameID) {
		this.frameID = frameID;
	}

	
	
}
