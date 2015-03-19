package br.cin.tbookmarks.recommender;

import java.util.ArrayList;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.common.RandomUtils;

import br.cin.tbookmarks.recommender.database.GroupLensDataset;


public class EvaluateRecommender {
	
	
	private DataModel model;
	private double trainingPercentage = 0.99;
	private double datasetPercentage = 0.2;
	private int top_n = 10;
	private boolean enableFixedTestSeed = true;
	
	public EvaluateRecommender() {
		model = GroupLensDataset.getInstance().getModel();
	}
	
	private void evaluateRecommender(ArrayList<RecommenderBuilder> recommenders, RecommenderEvaluator evaluator) throws TasteException{
		for (RecommenderBuilder recommenderBuilder : recommenders) {
			if(enableFixedTestSeed){
				RandomUtils.useTestSeed();
			}
			double result = evaluator.evaluate(recommenderBuilder, null, model, this.trainingPercentage, this.datasetPercentage);
			System.out.println(recommenderBuilder.getClass().getSimpleName().toString()+": "+result);
		}		
	}
	
	private void evaluateRecommenderIRStats(ArrayList<RecommenderBuilder> recommenders, RecommenderIRStatsEvaluator evaluator) throws TasteException{
		for (RecommenderBuilder recommenderBuilder : recommenders) {
			if(enableFixedTestSeed){
				RandomUtils.useTestSeed();
			}	
			IRStatistics result = evaluator.evaluate(
					recommenderBuilder, null, model, null, this.top_n,
					GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD,
					this.datasetPercentage);
			System.out.println(recommenderBuilder.getClass().getSimpleName().toString()+" Precision: "+result.getPrecision());
			System.out.println(recommenderBuilder.getClass().getSimpleName().toString()+" Recall: "+result.getRecall());
		}		
	}
	
	public static void main(String[] args) {
		
		try {
					
			EvaluateRecommender er = new EvaluateRecommender();
						
			Recommenders recommenders = new Recommenders();
			
			System.out.println("\nAverageAbsoluteDifferenceRecommenderEvaluator>>>>>>>>>>>>>>>");
			RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
			er.evaluateRecommender(recommenders.getRecommenderBuilders(), evaluator);
			
			System.out.println("\nRMSRecommenderEvaluator>>>>>>>>>>>>>>>");
			RecommenderEvaluator rmse = new RMSRecommenderEvaluator();
			er.evaluateRecommender(recommenders.getRecommenderBuilders(), rmse);
			
			System.out.println("\nGenericRecommenderIRStatsEvaluator>>>>>>>>>>>>>>>");
			RecommenderIRStatsEvaluator precisionRecall = new GenericRecommenderIRStatsEvaluator();
			er.evaluateRecommenderIRStats(recommenders.getRecommenderBuilders(), precisionRecall);
			
		}catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
}
