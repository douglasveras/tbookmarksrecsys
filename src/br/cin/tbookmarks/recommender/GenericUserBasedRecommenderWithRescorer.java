package br.cin.tbookmarks.recommender;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.RefreshHelper;
import org.apache.mahout.cf.taste.impl.recommender.AbstractRecommender;
import org.apache.mahout.cf.taste.impl.recommender.EstimatedPreferenceCapper;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.TopItems;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.recommender.Rescorer;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.LongPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

/**
 * <p>
 * A simple {@link org.apache.mahout.cf.taste.recommender.Recommender} which
 * uses a given {@link DataModel} and {@link UserNeighborhood} to produce
 * recommendations.
 * </p>
 */
public class GenericUserBasedRecommenderWithRescorer implements Recommender {

	private final Recommender delegate;
	private final DataModel model;
	private final IDRescorer idrescorer;


	public GenericUserBasedRecommenderWithRescorer(DataModel dataModel,
			UserNeighborhood neighborhood, UserSimilarity similarity,
			IDRescorer idrescorer) {
		this.model = dataModel;
		this.idrescorer = idrescorer;
		this.delegate = new GenericUserBasedRecommender(this.model, neighborhood, similarity);
	}

	@Override
	public void refresh(Collection<Refreshable> alreadyRefreshed) {
		delegate.refresh(alreadyRefreshed);
		
	}

	@Override
	public List<RecommendedItem> recommend(long userID, int howMany)
			throws TasteException {
		if(this.idrescorer == null){
			return this.delegate.recommend(userID, howMany);
		}else{
			return this.delegate.recommend(userID, howMany,this.idrescorer);
		}
		
	}

	@Override
	public List<RecommendedItem> recommend(long userID, int howMany,
			IDRescorer rescorer) throws TasteException {
		return this.delegate.recommend(userID, howMany, rescorer);
	}

	@Override
	public float estimatePreference(long userID, long itemID)
			throws TasteException {
		
		if(this.idrescorer == null){
			return (float) delegate.estimatePreference(userID, itemID);
		}else{
			return (float) this.idrescorer.rescore(itemID, delegate.estimatePreference(userID, itemID));
		}
		
	}

	@Override
	public void setPreference(long userID, long itemID, float value)
			throws TasteException {
		delegate.setPreference(userID, itemID, value);
		
	}

	@Override
	public void removePreference(long userID, long itemID)
			throws TasteException {
		delegate.removePreference(userID, itemID);
		
	}

	@Override
	public DataModel getDataModel() {
		return delegate.getDataModel();
	}

}
