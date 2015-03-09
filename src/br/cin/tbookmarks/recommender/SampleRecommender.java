package br.cin.tbookmarks.recommender;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

public class SampleRecommender {

	private DataModel model;

	public SampleRecommender() {
		try {
			model = new FileDataModel(new File(System.getProperty("user.dir")
					+ "\\resources\\dataset.csv"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void userBasedTreshold(double threshold) throws TasteException {

		UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
		UserNeighborhood neighborhood = new ThresholdUserNeighborhood(threshold,
				similarity, model);
		UserBasedRecommender recommender = new GenericUserBasedRecommender(
				model, neighborhood, similarity);

		List<RecommendedItem> recommendations = recommender.recommend(2, 3);
		for (RecommendedItem recommendation : recommendations) {
			System.out.println(recommendation);
		}

	}

	private void userBasedNearestNeighbor() throws TasteException {

		UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
		UserNeighborhood neighborhood = new NearestNUserNeighborhood(3,
				similarity, model);
		Recommender recommender = new GenericUserBasedRecommender(model,
				neighborhood, similarity);
		Recommender cachingRecommender = new CachingRecommender(recommender);

		List<RecommendedItem> recommendations = cachingRecommender.recommend(2,
				3);
		for (RecommendedItem recommendation : recommendations) {
			System.out.println(recommendation);
		}

	}
	
	private void itemBasedPearson() throws TasteException {

		ItemSimilarity similarity = new PearsonCorrelationSimilarity(model);
		Recommender recommender = new GenericItemBasedRecommender(model, similarity);
		Recommender cachingRecommender = new CachingRecommender(recommender);

		List<RecommendedItem> recommendations = cachingRecommender.recommend(2,
				3);
		for (RecommendedItem recommendation : recommendations) {
			System.out.println(recommendation);
		}

	}
	
	public static void main(String[] args) {
		SampleRecommender sr = new SampleRecommender();

		try {
			/*System.out.println("userBasedNearestNeighbor>>>>>>>>>>>>>>>\n");
			sr.userBasedNearestNeighbor();
			System.out.println("userBasedTreshold>>>>>>>>>>>>>>>\n");
			sr.userBasedTreshold(0.3);
			System.out.println("itemBasedPearson>>>>>>>>>>>>>>>\n");
			sr.itemBasedPearson();*/
			
			for(double t = 0.1;t<1.0;t=t+0.1){
				System.out.println("userBasedTreshold>>>>>>"+t+">>>>>>>>\n");
				sr.userBasedTreshold(t);
				System.out.println("\n");
			}
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
