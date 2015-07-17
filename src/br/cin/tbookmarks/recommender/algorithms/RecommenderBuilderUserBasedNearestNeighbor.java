package br.cin.tbookmarks.recommender.algorithms;

import java.lang.reflect.InvocationTargetException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class RecommenderBuilderUserBasedNearestNeighbor implements
		RecommenderBuilder {

	private int neiborSize/* = 475*/;
	private Class<? extends UserSimilarity> userSimilarity;
	
	public RecommenderBuilderUserBasedNearestNeighbor(int neiborSize, Class<? extends UserSimilarity> userSim) {
		this.neiborSize = neiborSize;
		this.userSimilarity = userSim;
	}
	
	@Override
	public Recommender buildRecommender(DataModel model)
			throws TasteException {
		UserSimilarity similarity = null;
		try {
			similarity = this.userSimilarity.getDeclaredConstructor(DataModel.class).newInstance(model);
			
			UserNeighborhood neighborhood = new NearestNUserNeighborhood(this.neiborSize,
					similarity, model);
			Recommender recommender = new GenericUserBasedRecommender(model,
					neighborhood, similarity);
			CachingRecommender cr = new CachingRecommender(recommender);
			return cr;
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return "NearestNeighbor_UserBased"+"(N="+neiborSize+")";
	}
}
