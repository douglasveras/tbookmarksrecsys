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
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.common.RandomUtils;

import br.cin.tbookmarks.recommender.database.AbstractDataset;
import br.cin.tbookmarks.recommender.database.BooksTwitterDataset;
import br.cin.tbookmarks.recommender.database.EventsTwitterDataset;
import br.cin.tbookmarks.recommender.database.GroupLensDataset;
import br.cin.tbookmarks.recommender.database.ItemDomain;
import br.cin.tbookmarks.recommender.database.MoviesCrossBooksDataset;
import br.cin.tbookmarks.recommender.database.MoviesCrossEventsBooksDataset;
import br.cin.tbookmarks.recommender.database.MoviesCrossEventsDataset;
import br.cin.tbookmarks.recommender.evaluation.AverageAbsoluteDifferenceRecommenderEvaluatorCrossDomain;
import br.cin.tbookmarks.recommender.evaluation.RMSRecommenderEvaluatorCrossDomain;
import br.cin.tbookmarks.recommender.similarity.ItemDomainRescorer;


public class EvaluateRecommender {
	
	
	private DataModel model;
	private AbstractDataset dataset;
	private Recommenders recommenders;
	
	private double trainingPercentage = 0.90;
	private double datasetPercentage = 1.0;
	private int top_n = 2;
	private double relevantThresholdPrecisionRecall = GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD;
	private boolean enableFixedTestSeed = true;
	
	public EvaluateRecommender(AbstractDataset dataset) {
		this.dataset =dataset;
		model = this.dataset.getModel();
		this.recommenders = new Recommenders(this.dataset);
	}
	
	private void evaluateRecommender(RecommenderEvaluator evaluator) throws TasteException{
		System.out.println("\n"+evaluator);
		for (RecommenderBuilder recommenderBuilder : this.recommenders.getRecommenderBuilders()) {
			if(enableFixedTestSeed){
				RandomUtils.useTestSeed();
			}
			double result = evaluator.evaluate(recommenderBuilder, null, model,this.trainingPercentage, this.datasetPercentage);
			System.out.println(recommenderBuilder.getClass().getSimpleName().toString()+": "+result);
		}		
	}
	
	private void evaluateRecommenderIRStats(RecommenderIRStatsEvaluator evaluator, IDRescorer idrescorer) throws TasteException{
		for (RecommenderBuilder recommenderBuilder : this.recommenders.getRecommenderBuilders()) {
			if(enableFixedTestSeed){
				RandomUtils.useTestSeed();
			}	
			IRStatistics result = evaluator.evaluate(
					recommenderBuilder, null, model, idrescorer, this.top_n,
					this.relevantThresholdPrecisionRecall,
					this.datasetPercentage);
			System.out.println(recommenderBuilder.getClass().getSimpleName().toString()+" Precision: "+result.getPrecision());
			System.out.println(recommenderBuilder.getClass().getSimpleName().toString()+" Recall: "+result.getRecall());
		}		
	}
	
	public static void main(String[] args) {
		
		try {
					
			AbstractDataset dataset = MoviesCrossEventsBooksDataset.getInstance();
			
			ArrayList<ItemDomain> domainsFilter = new ArrayList<ItemDomain>();
			domainsFilter.add(ItemDomain.MOVIE);
			domainsFilter.add(ItemDomain.BOOK);
			
			IDRescorer idrescorer = new ItemDomainRescorer(null,domainsFilter, dataset);
			//IDRescorer idrescorer = null;
			
			
			EvaluateRecommender er = new EvaluateRecommender(dataset);
			
			RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluatorCrossDomain(idrescorer);
			er.evaluateRecommender(evaluator);
			
			RecommenderEvaluator rmse = new RMSRecommenderEvaluatorCrossDomain(idrescorer);
			er.evaluateRecommender(rmse);

			RecommenderIRStatsEvaluator precisionRecall = new GenericRecommenderIRStatsEvaluator();
			er.evaluateRecommenderIRStats(precisionRecall,idrescorer);
			
			System.out.println("FINISHED!!");
			
		}catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
}
