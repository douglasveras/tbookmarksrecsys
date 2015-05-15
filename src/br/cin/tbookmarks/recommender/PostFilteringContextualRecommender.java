package br.cin.tbookmarks.recommender;


import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import br.cin.tbookmarks.recommender.database.AbstractDataset;
import br.cin.tbookmarks.recommender.database.contextual.AbstractContextualAttribute;
import br.cin.tbookmarks.recommender.database.contextual.ContextualCriteria;
import br.cin.tbookmarks.recommender.database.item.ItemCategory;

import com.google.common.base.Preconditions;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.FullRunningAverage;
import org.apache.mahout.cf.taste.impl.common.RefreshHelper;
import org.apache.mahout.cf.taste.impl.common.RunningAverage;
import org.apache.mahout.cf.taste.impl.recommender.AbstractRecommender;
import org.apache.mahout.cf.taste.impl.recommender.EstimatedPreferenceCapper;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.PreferredItemsNeighborhoodCandidateItemsStrategy;
import org.apache.mahout.cf.taste.impl.recommender.TopItems;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.CandidateItemsStrategy;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.ItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.MostSimilarItemsCandidateItemsStrategy;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.Rescorer;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.common.LongPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PostFilteringContextualRecommender implements Recommender{
	
	private ContextualCriteria contextualAttributes;
	private Recommender delegated;
	private PostFilteringStrategyRecommendation postFilteringStrategy;
	private AbstractDataset dataset;
	
	public PostFilteringContextualRecommender(Recommender recommender,ContextualCriteria contextualAttributes, PostFilteringStrategyRecommendation postFilteringStrategy, AbstractDataset dataset) {
		this.delegated = recommender;
		this.contextualAttributes = contextualAttributes;
		this.postFilteringStrategy = postFilteringStrategy;
		this.dataset = dataset;
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
		this.delegated.refresh(alreadyRefreshed);
		
	}

	@Override
	public List<RecommendedItem> recommend(long userID, int howMany)
			throws TasteException {
		// TODO Auto-generated method stub
		return this.recommend(userID, howMany);
	}

	@Override
	public List<RecommendedItem> recommend(long userID, int howMany,
			IDRescorer rescorer) throws TasteException {
		// TODO Auto-generated method stub
		return this.recommend(userID, howMany,rescorer);
	}

	@Override
	public float estimatePreference(long userID, long itemID)
			throws TasteException {
		
		return postFilteringStrategy.filterOrAdjustPreference(userID,itemID,contextualAttributes,this.delegated,this.dataset);
		
	}

	@Override
	public void setPreference(long userID, long itemID, float value)
			throws TasteException {
		this.delegated.setPreference(userID, itemID, value);
		
	}

	@Override
	public void removePreference(long userID, long itemID)
			throws TasteException {
		this.removePreference(userID, itemID);
		
	}

	@Override
	public DataModel getDataModel() {
		return this.delegated.getDataModel();
	}

	
	
}
