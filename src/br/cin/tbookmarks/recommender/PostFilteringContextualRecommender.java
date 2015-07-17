package br.cin.tbookmarks.recommender;


import java.util.Collection;
import java.util.List;
import br.cin.tbookmarks.recommender.database.AbstractDataset;
import br.cin.tbookmarks.recommender.database.contextual.ContextualCriteria;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;


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
