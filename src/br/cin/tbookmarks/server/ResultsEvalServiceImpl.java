package br.cin.tbookmarks.server;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cin.tbookmarks.client.Result;
import br.cin.tbookmarks.client.ResultsEvalService;
import br.cin.tbookmarks.recommender.EvaluateRecommender;
import br.cin.tbookmarks.recommender.database.AbstractDataset;
import br.cin.tbookmarks.recommender.database.AmazonCrossDataset;
import br.cin.tbookmarks.recommender.database.contextual.ContextualCriteria;
import br.cin.tbookmarks.recommender.database.contextual.DayTypeContextualAttribute;
import br.cin.tbookmarks.recommender.database.contextual.PeriodOfDayContextualAttribute;
import br.cin.tbookmarks.recommender.database.item.ItemDomain;
import br.cin.tbookmarks.recommender.evaluation.AverageAbsoluteDifferenceRecommenderEvaluatorCrossDomain;
import br.cin.tbookmarks.recommender.evaluation.RMSRecommenderEvaluatorCrossDomain;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class ResultsEvalServiceImpl extends RemoteServiceServlet implements
		ResultsEvalService{
	
	private static final Logger log = LoggerFactory.getLogger(ResultsEvalServiceImpl.class);
	
	/**
	 * 
	 */
	//private static final long serialVersionUID = 1385952445045847365L;

	@Override
	public List<Result> getResultsEval(String trial) throws Exception {
		return null;
		/*

		List<Result> resultsEval = new ArrayList<Result>();
		
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
			
			for(DayTypeContextualAttribute dayType : DayTypeContextualAttribute.values()){
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
								
					RecommenderEvaluator evaluator2 = new AverageAbsoluteDifferenceRecommenderEvaluatorCrossDomain(idrescorer,criteria);
					er2.evaluateRecommender(evaluator2,i);
					
					RecommenderEvaluator rmse2 = new RMSRecommenderEvaluatorCrossDomain(idrescorer,criteria);
					er2.evaluateRecommender(rmse2,i);
	
					for(Result r : er2.getResults()){
						r.setContext(dayType.name());
						r.setSourceDomain(sourceDomain.name());
						r.setTargetDomain(targetDomain.name());
						resultsEval.add(r);
						log.info(r.toString());
					}
					
					
					//er.exportEvaluationToTXT(dayType.name()+"exportedResults.txt");
				
			}
			
			for(PeriodOfDayContextualAttribute periodOfDay : PeriodOfDayContextualAttribute.values()){
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
			RecommenderIRStatsEvaluator precisionRecall = new GenericRecommenderIRStatsEvaluator();
			er.evaluateRecommenderIRStats(precisionRecall,idrescorer);
			
			log.warn("EVAL IS FINISHED!!");
			
			
			
		}catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		return resultsEval;
	
	*/}

}
