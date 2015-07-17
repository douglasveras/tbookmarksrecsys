package br.cin.tbookmarks.recommender.algorithms;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

public class RecommenderBuilderSVD implements RecommenderBuilder {
	
	private int numOfFeatures/* = 10*/;
	private double lambda/* = 0.05*/;
	private int numOfIterations/* = 10*/;
	
	
	
	public RecommenderBuilderSVD(int numOfFeatures, double lambda,
			int numOfIterations) {
		super();
		this.numOfFeatures = numOfFeatures;
		this.lambda = lambda;
		this.numOfIterations = numOfIterations;
	}

	@Override
	public Recommender buildRecommender(DataModel dataModel)
			throws TasteException {
		
		Recommender recommender = new SVDRecommender(dataModel, new ALSWRFactorizer(dataModel, numOfFeatures, lambda, numOfIterations));
		return new CachingRecommender(recommender);

	}
	
	@Override
	public String toString() {
		return "SVD"+"(Feat="+numOfFeatures+", lambda="+lambda+", iterat="+numOfIterations+")";
	}
}
