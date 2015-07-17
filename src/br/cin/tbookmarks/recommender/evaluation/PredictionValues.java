package br.cin.tbookmarks.recommender.evaluation;

public class PredictionValues {
	  float realPref;
	  float estimatedPref;
	  
	  public PredictionValues(float rp,float ep) {
		this.realPref = rp;
		this.estimatedPref = ep;
	  }
	  
	  public float getEstimatedPref() {
		return estimatedPref;
	  }
	  
	  public float getRealPref() {
		return realPref;
	}
}
