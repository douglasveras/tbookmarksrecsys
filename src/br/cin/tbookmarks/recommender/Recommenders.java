package br.cin.tbookmarks.recommender;

import java.util.ArrayList;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.common.Weighting;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CachingItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.CachingUserSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.SpearmanCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import br.cin.tbookmarks.recommender.database.GroupLensDataset;
import br.cin.tbookmarks.recommender.similarity.ItemCategoryItemSimilarity;

public class Recommenders {
	
	private ArrayList<RecommenderBuilder> recommenderBuilders;
	
	public Recommenders() {
		recommenderBuilders = new ArrayList<RecommenderBuilder>();
		recommenderBuilders.add(this.new RecommenderBuilderUserBasedNearestNeighbor());
		recommenderBuilders.add(this.new RecommenderBuilderUserBasedTreshold());
		recommenderBuilders.add(this.new RecommenderBuilderItemBased());
		recommenderBuilders.add(this.new RecommenderBuilderSVD());
		recommenderBuilders.add(this.new MyRecommenderBuilderContentGenreBased());
	}
	
	public ArrayList<RecommenderBuilder> getRecommenderBuilders() {
		return recommenderBuilders;
	}

	private UserSimilarity getUserSimilarity(DataModel dataModel) throws TasteException{
		//return new PearsonCorrelationSimilarity(dataModel,Weighting.WEIGHTED);
		//return new EuclideanDistanceSimilarity(dataModel);
		//SpearmanCorrelationSimilarity
		//TanimotoCoefficientSimilarity (boolean data)
		//return new CachingUserSimilarity(new LogLikelihoodSimilarity(dataModel), dataModel);
		return new EuclideanDistanceSimilarity(dataModel);

	}
	
	private ItemSimilarity getItemSimilarity(DataModel dataModel) throws TasteException{
		//return new PearsonCorrelationSimilarity(dataModel,Weighting.WEIGHTED);
		//return new EuclideanDistanceSimilarity(dataModel);
		//return new CachingItemSimilarity(new LogLikelihoodSimilarity(dataModel), dataModel);
		//TanimotoCoefficientSimilarity (boolean data)
		return new EuclideanDistanceSimilarity(dataModel);

	}
	
	public class RecommenderBuilderUserBasedNearestNeighbor implements RecommenderBuilder{

		private int neiborSize = 500;
		
		@Override
		public Recommender buildRecommender(DataModel model)
				throws TasteException {
			UserSimilarity similarity = (UserSimilarity) getUserSimilarity(model);
			UserNeighborhood neighborhood = new NearestNUserNeighborhood(this.neiborSize,
					similarity, model);
			Recommender recommender = new GenericUserBasedRecommender(model,
					neighborhood, similarity);
			return new CachingRecommender(recommender);
		}
		
	}
	
	public class RecommenderBuilderUserBasedTreshold implements RecommenderBuilder{

		private double threshold = 0.5;
		
		@Override
		public Recommender buildRecommender(DataModel dataModel)
				throws TasteException {
			
			UserSimilarity similarity = (UserSimilarity) getUserSimilarity(dataModel);
			
			UserNeighborhood neighborhood = new ThresholdUserNeighborhood(this.threshold,
					similarity, dataModel);
			Recommender recommender =  new GenericUserBasedRecommender(
					dataModel, neighborhood, similarity);
			return new CachingRecommender(recommender); 

		}
	}
	
	public class RecommenderBuilderItemBased implements RecommenderBuilder{

		@Override
		public Recommender buildRecommender(DataModel model)
				throws TasteException {
			ItemSimilarity similarity = (ItemSimilarity) getItemSimilarity(model);
			Recommender recommender = new GenericItemBasedRecommender(model, similarity);
			return new CachingRecommender(recommender);
		}
		
	}
	
	public class RecommenderBuilderSVD implements RecommenderBuilder{
		
		@Override
		public Recommender buildRecommender(DataModel dataModel)
				throws TasteException {
			
			Recommender recommender = new SVDRecommender(dataModel, new ALSWRFactorizer(dataModel, 10, 0.05, 10));
			return new CachingRecommender(recommender);

		}
	}
	
	public class MyRecommenderBuilderContentGenreBased implements RecommenderBuilder{

		@Override
		public Recommender buildRecommender(DataModel model)
				throws TasteException {
			ItemSimilarity similarity = (ItemSimilarity) new ItemCategoryItemSimilarity(model,GroupLensDataset.getInstance());
			Recommender recommender = new GenericContentRecommender(model, similarity,GroupLensDataset.getInstance());
			return new CachingRecommender(recommender);
		}
		
	}
	
	
}