package br.cin.tbookmarks.recommender;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.PatternSyntaxException;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.common.RandomUtils;

import br.cin.tbookmarks.recommender.Recommenders.PreFilteringContextualBuildRecommender;
import br.cin.tbookmarks.recommender.database.AbstractDataset;
import br.cin.tbookmarks.recommender.database.AmazonCrossDataset;
import br.cin.tbookmarks.recommender.database.BooksTwitterDataset;
import br.cin.tbookmarks.recommender.database.EventsTwitterDataset;
import br.cin.tbookmarks.recommender.database.GroupLensDataset;
import br.cin.tbookmarks.recommender.database.MoviesCrossBooksDataset;
import br.cin.tbookmarks.recommender.database.MoviesCrossEventsBooksDataset;
import br.cin.tbookmarks.recommender.database.MoviesCrossEventsDataset;
import br.cin.tbookmarks.recommender.database.contextual.ContextualCriteria;
import br.cin.tbookmarks.recommender.database.contextual.DayTypeContextualAttribute;
import br.cin.tbookmarks.recommender.database.contextual.PeriodOfDayContextualAttribute;
import br.cin.tbookmarks.recommender.database.item.ItemDomain;
import br.cin.tbookmarks.recommender.evaluation.AverageAbsoluteDifferenceRecommenderEvaluatorCrossDomain;
import br.cin.tbookmarks.recommender.evaluation.RMSRecommenderEvaluatorCrossDomain;
import br.cin.tbookmarks.recommender.similarity.ItemDomainRescorer;
import br.cin.tbookmarks.util.Functions;


public class EvaluateRecommender {
	
	
	private DataModel model;
	private AbstractDataset dataset;
	private Recommenders recommenders;
	
	private double trainingPercentage = 0.80;
	private double datasetPercentage = 1.0;
	private int top_n = 2;
	private double relevantThresholdPrecisionRecall = GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD;
	private boolean enableFixedTestSeed = true;
	
	private StringBuffer evaluationResults = new StringBuffer();
	
	public EvaluateRecommender(AbstractDataset dataset, ContextualCriteria contextualAttributes) {
		this.dataset =dataset;
		model = this.dataset.getModel();
		this.recommenders = new Recommenders(this.dataset,contextualAttributes);
	}
	
	private void showDataModelParameters(DataModel datamodel){
		System.out.println("Number of ratings: "+Functions.numOfRatings(datamodel));
		System.out.println("Number of items per domain: ");
		Functions.printNumOfItemsPerDomain(datamodel);
		Functions.printNumOfUsersAndOverlappedUsers(datamodel);
	}
	
	private void exportEvaluationToTXT(String fileName){

		File fileOutput = new File(System.getProperty("user.dir") + "\\resources\\results\\"+fileName);
		
		if (!fileOutput.exists()) {

			try {
								
				FileOutputStream streamOutput = new FileOutputStream(fileOutput);

				OutputStreamWriter streamWriter = new OutputStreamWriter(
						streamOutput);

				BufferedWriter bw = new BufferedWriter(streamWriter);
				
				bw.append(this.evaluationResults.toString());
				bw.close();
				System.out.println("File "+fileOutput.getName()+" exported!");

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	

	
	}
	
	private void evaluateRecommender(RecommenderEvaluator evaluator) throws TasteException{
		System.out.println("\n"+evaluator);
		this.evaluationResults.append(evaluator+"\n");
		for (RecommenderBuilder recommenderBuilder : this.recommenders.getRecommenderBuilders()) {
			if(enableFixedTestSeed){
				RandomUtils.useTestSeed();
			}
			
			double result = -999;
			
			if(recommenderBuilder instanceof PreFilteringContextualBuildRecommender){
				DataModel contextualDM = ((PreFilteringContextualBuildRecommender) recommenderBuilder).preFilterDataModel(model);
				
				result = evaluator.evaluate(recommenderBuilder, null, contextualDM,this.trainingPercentage, this.datasetPercentage);
				//showDataModelParameters(contextualDM);
			}else{			
				result = evaluator.evaluate(recommenderBuilder, null, model,this.trainingPercentage, this.datasetPercentage);
				//showDataModelParameters(model);
				
			}
			System.out.println(recommenderBuilder+": "+result);
			this.evaluationResults.append(recommenderBuilder.getClass().getSimpleName().toString()+";"+result+"\n");
		}		
		this.evaluationResults.append("\n");
	}
	
	public void evaluateRecommenderIRStats(RecommenderIRStatsEvaluator evaluator, IDRescorer idrescorer) throws TasteException{
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
					
			HashSet<ItemDomain> domainsDataset = new HashSet<ItemDomain>();
			domainsDataset.add(ItemDomain.BOOK);
			domainsDataset.add(ItemDomain.MOVIE);
			
			
			//AbstractDataset dataset = AmazonCrossDataset.getInstance(domainsDataset,20,true);
			AbstractDataset dataset = AmazonCrossDataset.getInstance();
			
			HashSet<ItemDomain> domainsFilter = new HashSet<ItemDomain>();
			//domainsFilter.add(ItemDomain.BOOK);
			domainsFilter.add(ItemDomain.MOVIE);
		
			IDRescorer idrescorer = new ItemDomainRescorer(null,domainsFilter, dataset);
			//IDRescorer idrescorer = null;
			
			/*EvaluateRecommender er = new EvaluateRecommender(dataset,null);
			
			RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluatorCrossDomain(idrescorer,null);
			er.evaluateRecommender(evaluator);
			
			RecommenderEvaluator rmse = new RMSRecommenderEvaluatorCrossDomain(idrescorer,null);
			er.evaluateRecommender(rmse);*/

			//er.exportEvaluationToTXT(dayType.name()+periodOfDay.name()+"exportedResults.txt");
			
			/*for(DayTypeContextualAttribute dayType : DayTypeContextualAttribute.values()){
				for(PeriodOfDayContextualAttribute periodOfDay : PeriodOfDayContextualAttribute.values()){
					ContextualCriteria criteria = new ContextualCriteria(dayType,periodOfDay);
					//ContextualCriteria criteria = new ContextualCriteria(DayTypeContextualAttribute.WEEKDAY,PeriodOfDayContextualAttribute.DAWN);
					
					System.out.println("Contexutal criteria: "+dayType.name()+periodOfDay.name());
					
					EvaluateRecommender er = new EvaluateRecommender(dataset,criteria);
								
					RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluatorCrossDomain(idrescorer,criteria);
					er.evaluateRecommender(evaluator);
					
					RecommenderEvaluator rmse = new RMSRecommenderEvaluatorCrossDomain(idrescorer,criteria);
					er.evaluateRecommender(rmse);
	
					//er.exportEvaluationToTXT(dayType.name()+periodOfDay.name()+"exportedResults.txt");
				}
			}*/
			
			for(DayTypeContextualAttribute dayType : DayTypeContextualAttribute.values()){
					ContextualCriteria criteria = new ContextualCriteria(dayType,PeriodOfDayContextualAttribute.DAWN);
					//ContextualCriteria criteria = new ContextualCriteria(DayTypeContextualAttribute.WEEKDAY,PeriodOfDayContextualAttribute.DAWN);
					
					System.out.println("Contexutal criteria: "+dayType.name());
					
					EvaluateRecommender er = new EvaluateRecommender(dataset,criteria);
								
					RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluatorCrossDomain(idrescorer,criteria);
					er.evaluateRecommender(evaluator);
					
					RecommenderEvaluator rmse = new RMSRecommenderEvaluatorCrossDomain(idrescorer,criteria);
					er.evaluateRecommender(rmse);
	
					//er.exportEvaluationToTXT(dayType.name()+"exportedResults.txt");
				
			}
			
			/*for(PeriodOfDayContextualAttribute periodOfDay : PeriodOfDayContextualAttribute.values()){
				ContextualCriteria criteria = new ContextualCriteria(null,periodOfDay);
				//ContextualCriteria criteria = new ContextualCriteria(DayTypeContextualAttribute.WEEKDAY,PeriodOfDayContextualAttribute.DAWN);
				
				System.out.println("Contexutal criteria: "+periodOfDay.name());
				
				EvaluateRecommender er = new EvaluateRecommender(dataset,criteria);
							
				RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluatorCrossDomain(idrescorer,criteria);
				er.evaluateRecommender(evaluator);
				
				RecommenderEvaluator rmse = new RMSRecommenderEvaluatorCrossDomain(idrescorer,criteria);
				er.evaluateRecommender(rmse);

				//er.exportEvaluationToTXT(periodOfDay.name()+"exportedResults.txt");
			
			}*/
			
			
			/*RecommenderIRStatsEvaluator precisionRecall = new GenericRecommenderIRStatsEvaluator();
			er.evaluateRecommenderIRStats(precisionRecall,idrescorer);*/
			
			System.out.println("FINISHED!!");
			
		}catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
}
