package br.cin.tbookmarks.recommender;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.common.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cin.tbookmarks.client.Result;
import br.cin.tbookmarks.recommender.algorithms.PostFilteringContextualBuildRecommender;
import br.cin.tbookmarks.recommender.algorithms.PreFilteringContextualBuildRecommender;
import br.cin.tbookmarks.recommender.algorithms.RecommenderBuilderItemBased;
import br.cin.tbookmarks.recommender.algorithms.RecommenderBuilderSVD;
import br.cin.tbookmarks.recommender.algorithms.RecommenderBuilderUserBasedNearestNeighbor;
import br.cin.tbookmarks.recommender.algorithms.RecommenderBuilderUserBasedTreshold;
import br.cin.tbookmarks.recommender.algorithms.Recommenders;
import br.cin.tbookmarks.recommender.database.AbstractDataset;
import br.cin.tbookmarks.recommender.database.AmazonCrossDataset;
import br.cin.tbookmarks.recommender.database.contextual.ContextualCriteria;
import br.cin.tbookmarks.recommender.database.contextual.ContextualDataModel;
import br.cin.tbookmarks.recommender.database.contextual.DayTypeContextualAttribute;
import br.cin.tbookmarks.recommender.database.contextual.PeriodOfDayContextualAttribute;
import br.cin.tbookmarks.recommender.database.item.ItemDomain;
import br.cin.tbookmarks.recommender.evaluation.AverageAbsoluteDifferenceRecommenderEvaluatorCrossDomain;
import br.cin.tbookmarks.recommender.evaluation.MAEAndRMSERecommenderEvaluatorCrossDomain;
import br.cin.tbookmarks.recommender.evaluation.RMSRecommenderEvaluatorCrossDomain;
import br.cin.tbookmarks.recommender.similarity.ItemDomainRescorer;
import br.cin.tbookmarks.util.Functions;


public class EvaluateRecommender {
	
	private static final Logger log = LoggerFactory.getLogger(EvaluateRecommender.class);
	
	private List<Result> results;
	
	private DataModel model;
	private AbstractDataset dataset;
	private ArrayList<RecommenderBuilder> recommenders;
	private ContextualCriteria contextualCriteriaEval;
	
	private double trainingPercentage = 0.80;
	private double datasetPercentage = 1.0;
	private int top_n = 2;
	private double relevantThresholdPrecisionRecall = GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD;
	private boolean enableFixedTestSeed = true;
	
	//private StringBuffer evaluationResults = new StringBuffer();
	
	public EvaluateRecommender(AbstractDataset dataset, ContextualCriteria contextualAttributes, ArrayList<RecommenderBuilder> recommenders) {
		this.dataset =dataset;
		model = this.dataset.getModel();
		//this.recommenders = new Recommenders(this.dataset,contextualAttributes);
		this.contextualCriteriaEval = contextualAttributes;
		this.recommenders = recommenders;
		this.results = new ArrayList<Result>();
	}
	
	private void showDataModelParameters(DataModel datamodel){
		log.info("Number of ratings: "+Functions.numOfRatings(datamodel));
		log.info("Number of items per domain: ");
		Functions.printNumOfItemsPerDomain(datamodel);
		Functions.getNumOfUsersAndOverlappedUsers(datamodel,this.dataset);
	}
	
	/*private void exportEvaluationToTXT(String fileName){

		File fileOutput = new File(System.getProperty("user.dir") + "\\resources\\results\\"+fileName);
		
		if (!fileOutput.exists()) {

			try {
								
				FileOutputStream streamOutput = new FileOutputStream(fileOutput);

				OutputStreamWriter streamWriter = new OutputStreamWriter(
						streamOutput);

				BufferedWriter bw = new BufferedWriter(streamWriter);
				
				bw.append(this.evaluationResults.toString());
				bw.close();
				log.info("File "+fileOutput.getName()+" exported!");

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	

	
	}*/
	
	public List<Result> getResults() {
		return results;
	}
	
	private double evaluateAndSetResultParameters(DataModel model, Result r, RecommenderEvaluator evaluator, RecommenderBuilder recommenderBuilder) throws TasteException{
		double result = evaluator.evaluate(recommenderBuilder, null, model,this.trainingPercentage, this.datasetPercentage);
		//showDataModelParameters(model);
		
		int numOfUsers = model.getNumUsers();
		//log.info("num users "+numOfUsers);
		r.setNumOfUsers(numOfUsers);
		
		int numOfItens = model.getNumItems();
		//log.info("num items "+numOfItens);
		r.setNumOfItens(numOfItens);
		
		int info[] = Functions.getNumOfUsersAndOverlappedUsers(model,this.dataset);
		r.setNumOfOverlappedUsers(info[1]);
		
		
		return result;
		//int numOfRatings = Functions.numOfRatings(model);
		//log.info("num ratings "+numOfRatings);
	}
	
	public void evaluateRecommender(RecommenderEvaluator evaluator, int trial) throws TasteException{
		
		//log.info("\n"+evaluator);
		//this.evaluationResults.append(evaluator+"\n");
		for (RecommenderBuilder recommenderBuilder : this.recommenders) {
			if(enableFixedTestSeed){
				RandomUtils.useTestSeed();
			}
			
			Result r = new Result();
			r.setTrial(trial);
			
			double result = -999;
			
			long timeMilis = System.currentTimeMillis();
			
			if(recommenderBuilder instanceof PreFilteringContextualBuildRecommender){
				DataModel contextualDM = ((PreFilteringContextualBuildRecommender) recommenderBuilder).preFilterDataModel(this.model,this.contextualCriteriaEval);
				
				result = evaluateAndSetResultParameters(contextualDM,r,evaluator,recommenderBuilder);
				
			}else if(recommenderBuilder instanceof PostFilteringContextualBuildRecommender){
				((PostFilteringContextualBuildRecommender) recommenderBuilder).setContextAndDataset(this.contextualCriteriaEval, this.dataset);
				result = evaluateAndSetResultParameters(this.model,r,evaluator,recommenderBuilder);
			}else{
				result = evaluateAndSetResultParameters(this.model,r,evaluator,recommenderBuilder);
				
			}
			//log.info(recommenderBuilder+": "+result);
			if(evaluator instanceof AverageAbsoluteDifferenceRecommenderEvaluatorCrossDomain){
				
				AverageAbsoluteDifferenceRecommenderEvaluatorCrossDomain eval = (AverageAbsoluteDifferenceRecommenderEvaluatorCrossDomain)evaluator;
				r.setMaeValue(result);
				r.setTotalOfTestRatings(eval.getTotalOfTestRatings());
				r.setTotalOfTrainingRatingsFromSource(eval.getTotalOfTrainingRatingsFromSource());
				r.setTotalOfTrainingRatingsFromTargetWithContext(eval.getTotalOfTrainingRatingsFromTargetWithContext());
				r.setTotalOfTrainingRatingsFromTargetWithoutContext(eval.getTotalOfTrainingRatingsFromTargetWithoutContext());
				
			}else if(evaluator instanceof RMSRecommenderEvaluatorCrossDomain){
				RMSRecommenderEvaluatorCrossDomain eval = (RMSRecommenderEvaluatorCrossDomain)evaluator;
				r.setRmseValue(result);
				r.setTotalOfTestRatings(eval.getTotalOfTestRatings());
				r.setTotalOfTrainingRatingsFromSource(eval.getTotalOfTrainingRatingsFromSource());
				r.setTotalOfTrainingRatingsFromTargetWithContext(eval.getTotalOfTrainingRatingsFromTargetWithContext());
				r.setTotalOfTrainingRatingsFromTargetWithoutContext(eval.getTotalOfTrainingRatingsFromTargetWithoutContext());
				
			}else if(evaluator instanceof MAEAndRMSERecommenderEvaluatorCrossDomain){
				MAEAndRMSERecommenderEvaluatorCrossDomain eval = (MAEAndRMSERecommenderEvaluatorCrossDomain)evaluator;
				r.setPredictionValues(eval.getValues());
				r.setMaeValue(eval.getMAEResult());
				r.setRmseValue(eval.getRMSEResult());
				r.setTotalOfTestRatings(eval.getTotalOfTestRatings());
				r.setTotalOfTrainingRatingsFromSource(eval.getTotalOfTrainingRatingsFromSource());
				r.setTotalOfTrainingRatingsFromTargetWithContext(eval.getTotalOfTrainingRatingsFromTargetWithContext());
				r.setTotalOfTrainingRatingsFromTargetWithoutContext(eval.getTotalOfTrainingRatingsFromTargetWithoutContext());
				
			} 
			
			r.setExecutionTime(System.currentTimeMillis()-timeMilis);
			r.setDate(new Date(new Long(timeMilis)*1000));
			
			r.setAlgorithmName(recommenderBuilder.toString());
			
			this.results.add(r);
			//this.evaluationResults.append(recommenderBuilder.getClass().getSimpleName().toString()+";"+result+"\n");
		}		
		//this.evaluationResults.append("\n");
	}
	
	public void evaluateRecommenderIRStats(RecommenderIRStatsEvaluator evaluator, IDRescorer idrescorer) throws TasteException{
		for (RecommenderBuilder recommenderBuilder : this.recommenders) {
			if(enableFixedTestSeed){
				RandomUtils.useTestSeed();
			}	
			IRStatistics result = evaluator.evaluate(
					recommenderBuilder, null, model, idrescorer, this.top_n,
					this.relevantThresholdPrecisionRecall,
					this.datasetPercentage);
			log.info(recommenderBuilder.getClass().getSimpleName().toString()+" Precision: "+result.getPrecision());
			log.info(recommenderBuilder.getClass().getSimpleName().toString()+" Recall: "+result.getRecall());
		}		
	}
	
	private static void evaluateSingleDomain(ArrayList<RecommenderBuilder> recommenders, int trial, ItemDomain sourceDomain, ItemDomain targetDomain, IDRescorer idrescorer) {

		List<Result> resultsEval = new ArrayList<Result>();
		
		try {
						
			AbstractDataset dataset = AmazonCrossDataset.getInstance(true,targetDomain); //single domain
			
			//IDRescorer idrescorer = null; //single-domain

			
			
			for(DayTypeContextualAttribute dayType : DayTypeContextualAttribute.values()){
					ContextualCriteria criteria = new ContextualCriteria(dayType,PeriodOfDayContextualAttribute.DAWN);
					
					EvaluateRecommender er2 = new EvaluateRecommender(dataset,criteria,recommenders);
							
					RecommenderEvaluator evaluator2 = new MAEAndRMSERecommenderEvaluatorCrossDomain(idrescorer,criteria);
					er2.evaluateRecommender(evaluator2,trial);
	
					for(Result r : er2.getResults()){
						r.setContext(dayType.name());
						r.setSourceDomain(sourceDomain.name());
						r.setTargetDomain(targetDomain.name());
						resultsEval.add(r);
						log.warn(r.toString());
						exportPredicitions(r);
					}
				
			}

			
			
			
			for(Result r : resultsEval){
				log.info(r.toString());
				//exportPredicitions(r);
			}
			log.warn("SINGLE DOMAIN TESTS ARE FINISHED!!");
			
			
			
		}catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	
	
		
	}

	private static void evaluateCrossDomain(ArrayList<RecommenderBuilder> recommenders, int trial, ItemDomain sourceDomain, ItemDomain targetDomain) {

		List<Result> resultsEval = new ArrayList<Result>();
		
		try {
						
			AbstractDataset dataset = AmazonCrossDataset.getInstance(); //cross domain
			
			HashSet<ItemDomain> domainsFilter = new HashSet<ItemDomain>();
			domainsFilter.add(sourceDomain);
			//domainsFilter.add(ItemDomain.MOVIE);
		
			IDRescorer idrescorer = new ItemDomainRescorer(null,domainsFilter, dataset); // cross-domain

			
			//DayTypeContextualAttribute dayType = DayTypeContextualAttribute.WEEKEND;
			for(DayTypeContextualAttribute dayType : DayTypeContextualAttribute.values()){
					ContextualCriteria criteria = new ContextualCriteria(dayType,PeriodOfDayContextualAttribute.DAWN);
					
					EvaluateRecommender er2 = new EvaluateRecommender(dataset,criteria,recommenders);
							
					RecommenderEvaluator evaluator2 = new MAEAndRMSERecommenderEvaluatorCrossDomain(idrescorer,criteria);
					er2.evaluateRecommender(evaluator2,trial);
	
					for(Result r : er2.getResults()){
						r.setContext(dayType.name());
						r.setSourceDomain(sourceDomain.name());
						r.setTargetDomain(targetDomain.name());
						resultsEval.add(r);
						log.warn(r.toString());
						exportPredicitions(r);
					}
				
			}

			
			
			
			for(Result r : resultsEval){
				log.info(r.toString());
				//exportPredicitions(r);
			}
			log.warn("CROSS DOMAIN TESTS ARE FINISHED!!");
			
			
			
		}catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		
	
	
		
	}
	
	private static void exportPredicitions(Result r) {


		String algorithmName[] = r.getAlgorithmName().split(":");
		
		
		
		File fileOutput = new File("C:\\Users\\Douglas\\Google Drive\\Cross-Domain\\results\\"+algorithmName[0]+r.getContext()+r.getTrial()+".txt");
		
		if (!fileOutput.exists()) {

			try {
								
				FileOutputStream streamOutput = new FileOutputStream(fileOutput);

				OutputStreamWriter streamWriter = new OutputStreamWriter(
						streamOutput);

				BufferedWriter bw = new BufferedWriter(streamWriter);
				
				bw.append(r.toString()+"\n");
				for(int i = 0; i < r.getPredictionValues().size(); i++){
					if(r.getPredictionValues() != null && r.getPredictionValues().get(i) != null){
						bw.append(r.getPredictionValues().get(i).getRealPref()+"\t"+r.getPredictionValues().get(i).getEstimatedPref()+"\n");
					}
				}
				bw.close();
				log.info("File "+fileOutput.getName()+" exported!");

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	

		
	}


	
	public static void main(String[] args) {
		
		ArrayList<RecommenderBuilder> recommenders = new ArrayList<RecommenderBuilder>();
		recommenders.add(new RecommenderBuilderUserBasedNearestNeighbor(475, EuclideanDistanceSimilarity.class));
		//recommenders.add(new RecommenderBuilderUserBasedTreshold(0.5, EuclideanDistanceSimilarity.class));
		//recommenders.add(new RecommenderBuilderItemBased(EuclideanDistanceSimilarity.class));
		//recommenders.add(new RecommenderBuilderSVD(10,0.05,10));
		//recommenders.add(new PreFilteringContextualBuildRecommender(new RecommenderBuilderUserBasedNearestNeighbor(475, EuclideanDistanceSimilarity.class)));
		
//		PostFilteringStrategyRecommendation pfStrategy2 = new PostFilteringStrategyRecommendation(PostFilteringStrategyRecommendation.PossibleFilteringStrategies.MOST_OCCURRED,false,0.0f);
//		recommenders.add(new PostFilteringContextualBuildRecommender(new RecommenderBuilderUserBasedNearestNeighbor(475, EuclideanDistanceSimilarity.class),pfStrategy2));
		
		//PostFilteringStrategyRecommendation pfStrategy3 = new PostFilteringStrategyRecommendation(PostFilteringStrategyRecommendation.PossibleFilteringStrategies.MOST_OCCURRED,true,4.0f);
		//recommenders.add(new PostFilteringContextualBuildRecommender(new RecommenderBuilderUserBasedNearestNeighbor(475, EuclideanDistanceSimilarity.class),pfStrategy3));
		
		//PostFilteringStrategyRecommendation pfStrategy4 = new PostFilteringStrategyRecommendation(PostFilteringStrategyRecommendation.PossibleFilteringStrategies.AT_LEAST_MEDIA_OF_OCCURRENCIES,true,4.0f);
		//recommenders.add(new PostFilteringContextualBuildRecommender(new RecommenderBuilderUserBasedNearestNeighbor(475, EuclideanDistanceSimilarity.class),pfStrategy4));
		
		//evaluateSingleDomain(recommenders,1,ItemDomain.MOVIE,ItemDomain.MOVIE,null);
		evaluateCrossDomain(recommenders,2,ItemDomain.BOOK,ItemDomain.MOVIE);

		/*List<Result> resultsEval = new ArrayList<Result>();
		
		int trial = 1;
		
		try {
					
			HashSet<ItemDomain> domainsDataset = new HashSet<ItemDomain>();
			domainsDataset.add(ItemDomain.BOOK);
			domainsDataset.add(ItemDomain.MOVIE);
			
			ItemDomain sourceDomain = ItemDomain.MOVIE;
			ItemDomain targetDomain = ItemDomain.BOOK;
			
			//AbstractDataset dataset = AmazonCrossDataset.getInstance(domainsDataset,20,true); //generate new dataset
			AbstractDataset dataset = AmazonCrossDataset.getInstance(true,targetDomain); //single domain
			//AbstractDataset dataset = AmazonCrossDataset.getInstance(); //use actual dataset
			
			
			
			//System.out.println((System.currentTimeMillis()-timeMilis)/60);
			
			HashSet<ItemDomain> domainsFilter = new HashSet<ItemDomain>();
			domainsFilter.add(ItemDomain.BOOK);
			//domainsFilter.add(ItemDomain.MOVIE);
		
			//IDRescorer idrescorer = new ItemDomainRescorer(null,domainsFilter, dataset); // cross-domain
			IDRescorer idrescorer = null; //single-domain
			
			//NO CONTEXT
//			EvaluateRecommender er = new EvaluateRecommender(dataset,null);
//			
//			RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluatorCrossDomain(idrescorer,null);
//			er.evaluateRecommender(evaluator);
//			
//			RecommenderEvaluator rmse = new RMSRecommenderEvaluatorCrossDomain(idrescorer,null);
//			er.evaluateRecommender(rmse);

			//er.exportEvaluationToTXT(dayType.name()+periodOfDay.name()+"exportedResults.txt");

			for(int i=1; i <= Integer.valueOf(trial); i++ ){
			
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
			}
			
			for(DayTypeContextualAttribute dayType : DayTypeContextualAttribute.values()){
					ContextualCriteria criteria = new ContextualCriteria(dayType,PeriodOfDayContextualAttribute.DAWN);
					//ContextualCriteria criteria = new ContextualCriteria(DayTypeContextualAttribute.WEEKDAY,PeriodOfDayContextualAttribute.DAWN);
					
					//log.info("Contexutal criteria: "+dayType.name());
					
					EvaluateRecommender er2 = new EvaluateRecommender(dataset,criteria);
							
					RecommenderEvaluator evaluator2 = new MAEAndRMSERecommenderEvaluatorCrossDomain(idrescorer,criteria);
					er2.evaluateRecommender(evaluator2,i);
					
					/*RecommenderEvaluator evaluator2 = new AverageAbsoluteDifferenceRecommenderEvaluatorCrossDomain(idrescorer,criteria);
					er2.evaluateRecommender(evaluator2,i);
					
					RecommenderEvaluator rmse2 = new RMSRecommenderEvaluatorCrossDomain(idrescorer,criteria);
					er2.evaluateRecommender(rmse2,i);
	
					for(Result r : er2.getResults()){
						r.setContext(dayType.name());
						r.setSourceDomain(sourceDomain.name());
						r.setTargetDomain(targetDomain.name());
						resultsEval.add(r);
						//log.info(r.toString());
					}
					
					
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
			
			}
			
			}
			/*RecommenderIRStatsEvaluator precisionRecall = new GenericRecommenderIRStatsEvaluator();
			er.evaluateRecommenderIRStats(precisionRecall,idrescorer);
			for(Result r : resultsEval){
				log.info(r.toString());
				exportPredicitions(r);
			}
			log.warn("EVAL IS FINISHED!!");
			
			
			
		}catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		*/
	
	}
}
	