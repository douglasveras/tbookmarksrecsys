package br.cin.tbookmarks.recommender;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;


public class EvaluateRecommender {
	private class MyRecommenderBuilder implements RecommenderBuilder{

		@Override
		public Recommender buildRecommender(DataModel dataModel)
				throws TasteException {
			UserSimilarity similarity = new PearsonCorrelationSimilarity(dataModel);
			UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, dataModel);
			return new GenericUserBasedRecommender(dataModel, neighborhood, similarity);
		}
		
	}
	public static void main(String[] args) {
		RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
		EvaluateRecommender er = new EvaluateRecommender();
		RecommenderBuilder builder = er.new MyRecommenderBuilder();
		DataModel model;
		try {
			model = new FileDataModel(new File(System.getProperty("user.dir")+"\\resources\\dataset.csv"));
			double result = evaluator.evaluate(builder, null, model, 0.9, 1.0);
			System.out.println(result);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
