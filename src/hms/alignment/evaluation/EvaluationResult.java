package hms.alignment.evaluation;

public class EvaluationResult {

	
	private double percision;
	private double recall;
	private double fMeasure;
	public double getPercision() {
		return percision;
	}
	public void setPercision(double percision) {
		this.percision = percision;
	}
	public double getRecall() {
		return recall;
	}
	public void setRecall(double recall) {
		this.recall = recall;
	}
	public double getfMeasure() {
		return fMeasure;
	}
	public void setfMeasure(double fMeasure) {
		this.fMeasure = fMeasure;
	}
	
	@Override
	public String toString() {
	
		return "p: "+percision + "\t r: "+recall + "\t f1: "+ fMeasure; 
				
	}
	
}
