package br.cin.tbookmarks.recommender.algorithms;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.Recommender;

import br.cin.tbookmarks.recommender.PostFilteringContextualRecommender;
import br.cin.tbookmarks.recommender.PostFilteringStrategyRecommendation;
import br.cin.tbookmarks.recommender.database.AbstractDataset;
import br.cin.tbookmarks.recommender.database.contextual.ContextualCriteria;

public class PostFilteringContextualBuildRecommender implements
		RecommenderBuilder {

	private ContextualCriteria contextualAttributes;
	private RecommenderBuilder recommenderBuilder;
	private PostFilteringStrategyRecommendation postFilteringStrategyRecommendation;
	private AbstractDataset dataset;
	
	public PostFilteringContextualBuildRecommender(RecommenderBuilder recommenderBuilder, 
													PostFilteringStrategyRecommendation postFilteringStrategyRecommendation) {
		//this.contextualAttributes = contexutalAttributes;
		this.recommenderBuilder = recommenderBuilder;
		this.postFilteringStrategyRecommendation = postFilteringStrategyRecommendation;
		//this.dataset = dataset;
	}
	
	public void setContextAndDataset(ContextualCriteria contexutalAttributes, AbstractDataset dataset) {
		this.contextualAttributes = contexutalAttributes;
		this.dataset = dataset;

	}
			
	@Override
	public Recommender buildRecommender(DataModel model)
			throws TasteException {
		
		if(contextualAttributes == null || dataset == null){
			throw new TasteException("Context and/or dataset unset in PostF");
		}
		
		//if(model instanceof ContextualDataModel){
			return new PostFilteringContextualRecommender(this.recommenderBuilder.buildRecommender(model),contextualAttributes, this.postFilteringStrategyRecommendation,this.dataset);
		//}
		
		//return null;
		
	}
	
	@Override
	public String toString() {
		return "PostF"+"(CF-based="+recommenderBuilder+", strategy: "+postFilteringStrategyRecommendation.getPostFilteringStrategy()+"[onlyGoodRatings: "+postFilteringStrategyRecommendation.isOnlyWithGoodRatings()+", minimal: "+postFilteringStrategyRecommendation.getGoodRatingMin() +"])";
	}
	
}
