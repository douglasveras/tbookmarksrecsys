package br.cin.tbookmarks.recommender.algorithms;

import java.lang.reflect.InvocationTargetException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class RecommenderBuilderItemBased implements RecommenderBuilder {

	private Class<? extends ItemSimilarity> itemSimilarity;
	
	public RecommenderBuilderItemBased(Class<? extends ItemSimilarity> itemSimilarity) {
		this.itemSimilarity = itemSimilarity;
	}
	
	@Override
	public Recommender buildRecommender(DataModel model)
			throws TasteException {
		ItemSimilarity similarity;
		try {
			similarity = this.itemSimilarity.getDeclaredConstructor(DataModel.class).newInstance(model);
			Recommender recommender = new GenericItemBasedRecommender(model, similarity);
			return new CachingRecommender(recommender);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "CF-ItemBased";
	}
	
}
