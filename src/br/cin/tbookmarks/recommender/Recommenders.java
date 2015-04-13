package br.cin.tbookmarks.recommender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.common.Weighting;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.CachingRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.ALSWRFactorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.similarity.CachingItemSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.CachingUserSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.SpearmanCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.math.function.LongIntProcedure;

import com.google.common.primitives.Longs;

import br.cin.tbookmarks.recommender.database.AbstractDataset;
import br.cin.tbookmarks.recommender.database.GroupLensDataset;
import br.cin.tbookmarks.recommender.database.contextual.AbstractContextualAttribute;
import br.cin.tbookmarks.recommender.database.contextual.ContextualCriteria;
import br.cin.tbookmarks.recommender.database.contextual.ContextualDataModel;
import br.cin.tbookmarks.recommender.database.contextual.ContextualFileAttributeSequence;
import br.cin.tbookmarks.recommender.database.contextual.ContextualFileDataModel;
import br.cin.tbookmarks.recommender.database.contextual.ContextualPreference;
import br.cin.tbookmarks.recommender.database.contextual.ContextualUserPreferenceArray;
import br.cin.tbookmarks.recommender.database.contextual.DayTypeContextualAttribute;
import br.cin.tbookmarks.recommender.database.contextual.PeriodOfDayContextualAttribute;
import br.cin.tbookmarks.recommender.database.item.ItemDomain;
import br.cin.tbookmarks.recommender.similarity.ItemCategoryItemSimilarity;
import br.cin.tbookmarks.recommender.similarity.ItemDomainRescorer;
import br.cin.tbookmarks.util.Functions;

public class Recommenders {
	
	private ArrayList<RecommenderBuilder> recommenderBuilders;
	private AbstractDataset dataset;
	private ContextualCriteria contextualCriteria;
	
	public Recommenders(AbstractDataset dataset, ContextualCriteria contextualAttributes) {
		recommenderBuilders = new ArrayList<RecommenderBuilder>();
		this.dataset = dataset;
		this.contextualCriteria = contextualAttributes;
		
		//recommenderBuilders.add(this.new RecommenderBuilderUserBasedNearestNeighbor());		
		recommenderBuilders.add(this.new PreFilteringContextualBuildRecommender(this.contextualCriteria, this.new RecommenderBuilderUserBasedNearestNeighbor()));
		
		//recommenderBuilders.add(this.new RecommenderBuilderUserBasedTreshold());
		recommenderBuilders.add(this.new PreFilteringContextualBuildRecommender(this.contextualCriteria, this.new RecommenderBuilderUserBasedTreshold()));
		
		//recommenderBuilders.add(this.new RecommenderBuilderItemBased());
		//recommenderBuilders.add(this.new PreFilteringContextualBuildRecommender(this.contextualCriteria, this.new RecommenderBuilderItemBased()));
		
		//recommenderBuilders.add(this.new RecommenderBuilderSVD());
		//recommenderBuilders.add(this.new PreFilteringContextualBuildRecommender(this.contextualCriteria, this.new RecommenderBuilderSVD()));
		
		//recommenderBuilders.add(this.new MyRecommenderBuilderContentGenreBased(this.dataset));
	}
	
	public ArrayList<RecommenderBuilder> getRecommenderBuilders() {
		return recommenderBuilders;
	}

	private UserSimilarity getUserSimilarity(DataModel dataModel) throws TasteException{
		//return new PearsonCorrelationSimilarity(dataModel,Weighting.WEIGHTED);
		//return new EuclideanDistanceSimilarity(dataModel);
		//SpearmanCorrelationSimilarity
		//TanimotoCoefficientSimilarity (boolean data)
		//return new CachingUserSimilarity(new LogLikelihoodSimilarity(dataModel), dataModel);
		return new EuclideanDistanceSimilarity(dataModel);

	}
	
	private ItemSimilarity getItemSimilarity(DataModel dataModel) throws TasteException{
		//return new PearsonCorrelationSimilarity(dataModel,Weighting.WEIGHTED);
		//return new EuclideanDistanceSimilarity(dataModel);
		//return new CachingItemSimilarity(new LogLikelihoodSimilarity(dataModel), dataModel);
		//TanimotoCoefficientSimilarity (boolean data)
		return new EuclideanDistanceSimilarity(dataModel);

	}
	
	public class RecommenderBuilderUserBasedNearestNeighbor implements RecommenderBuilder{

		private int neiborSize = 500;
		
		@Override
		public Recommender buildRecommender(DataModel model)
				throws TasteException {
			UserSimilarity similarity = (UserSimilarity) getUserSimilarity(model);
			UserNeighborhood neighborhood = new NearestNUserNeighborhood(this.neiborSize,
					similarity, model);
			Recommender recommender = new GenericUserBasedRecommender(model,
					neighborhood, similarity);
			CachingRecommender cr = new CachingRecommender(recommender);
			return cr;
		}
		
	}
	
	public class RecommenderBuilderUserBasedTreshold implements RecommenderBuilder{

		private double threshold = 0.5;
		
		@Override
		public Recommender buildRecommender(DataModel dataModel)
				throws TasteException {
			
			UserSimilarity similarity = (UserSimilarity) getUserSimilarity(dataModel);
			
			UserNeighborhood neighborhood = new ThresholdUserNeighborhood(this.threshold,
					similarity, dataModel);
			Recommender recommender =  new GenericUserBasedRecommender(
					dataModel, neighborhood, similarity);
			return new CachingRecommender(recommender); 

		}
	}
	
	/*public class RecommenderBuilderUserBasedTresholdWithRescorer implements RecommenderBuilder{

		private double threshold = 0.5;
		private IDRescorer idrescorer;
		
		public RecommenderBuilderUserBasedTresholdWithRescorer(IDRescorer idrescorer) {
			this.idrescorer = idrescorer;
		}
		
		@Override
		public Recommender buildRecommender(DataModel dataModel)
				throws TasteException {
			
			UserSimilarity similarity = (UserSimilarity) getUserSimilarity(dataModel);
			
			UserNeighborhood neighborhood = new ThresholdUserNeighborhood(this.threshold,
					similarity, dataModel);
			Recommender recommender =  new GenericUserBasedRecommenderWithRescorer(
					dataModel, neighborhood, similarity, this.idrescorer);
			return new CachingRecommender(recommender); 

		}
	}*/
	
	public class RecommenderBuilderItemBased implements RecommenderBuilder{

		@Override
		public Recommender buildRecommender(DataModel model)
				throws TasteException {
			ItemSimilarity similarity = (ItemSimilarity) getItemSimilarity(model);
			Recommender recommender = new GenericItemBasedRecommender(model, similarity);
			return new CachingRecommender(recommender);
		}
		
	}
	
	public class RecommenderBuilderSVD implements RecommenderBuilder{
		
		@Override
		public Recommender buildRecommender(DataModel dataModel)
				throws TasteException {
			
			Recommender recommender = new SVDRecommender(dataModel, new ALSWRFactorizer(dataModel, 10, 0.05, 10));
			return new CachingRecommender(recommender);

		}
	}
	
	public class MyRecommenderBuilderContentGenreBased implements RecommenderBuilder{

		private AbstractDataset dataset;
		
		public MyRecommenderBuilderContentGenreBased(AbstractDataset dataset) {
			this.dataset = dataset;
		}
		
		@Override
		public Recommender buildRecommender(DataModel model)
				throws TasteException {
			ItemSimilarity similarity = (ItemSimilarity) new ItemCategoryItemSimilarity(model,dataset);
			Recommender recommender = new GenericContentRecommender(model, similarity,dataset);
			return new CachingRecommender(recommender);
		}
		
	}
	
	public class PreFilteringContextualBuildRecommender implements RecommenderBuilder{

		private ContextualCriteria contextualAttributes;
		private RecommenderBuilder recommenderBuilder;
		
		public PreFilteringContextualBuildRecommender(ContextualCriteria contexutalAttributes, RecommenderBuilder recommenderBuilder) {
			this.contextualAttributes = contexutalAttributes;
			this.recommenderBuilder = recommenderBuilder;
		}
		
		public DataModel preFilterDataModel(DataModel model) throws TasteException{
			
			//criar um novo datamodel verificando cada preferencia e adicionando no novo datamodel caso case com o contexto
			
			FastByIDMap<PreferenceArray> preferences = new FastByIDMap<PreferenceArray>();
			LongPrimitiveIterator userIdsIterator = model.getUserIDs();
			
			
			while(userIdsIterator.hasNext()){
				
				Long userId = userIdsIterator.next();
				PreferenceArray prefsForUser = model.getPreferencesFromUser(userId);
				if(this.contextualAttributes != null && prefsForUser instanceof ContextualUserPreferenceArray){
					ContextualUserPreferenceArray contextualPrefsForUser = (ContextualUserPreferenceArray) prefsForUser;
					ArrayList<Long> newItemIds = new ArrayList<Long>();
					ArrayList<Float> newPrefValues = new ArrayList<Float>();
					ArrayList<List<Long>> newContextualPrefs = new ArrayList<List<Long>>();
					
					for(int i = 0; i < contextualPrefsForUser.getIDs().length; i++){
						
						if( this.contextualAttributes.containsAllContextualAttributes(contextualPrefsForUser.get(i).getContextualPreferences())){
							newItemIds.add(contextualPrefsForUser.get(i).getItemID());
							newPrefValues.add(contextualPrefsForUser.get(i).getValue());
							Long[] longObjects = ArrayUtils.toObject(contextualPrefsForUser.get(i).getContextualPreferences());
							newContextualPrefs.add(Arrays.asList(longObjects));
						}
						
						
					}
					
					if(newItemIds.size() > 0 && newContextualPrefs.size() > 0){
						ContextualUserPreferenceArray newPrefsForUser = new ContextualUserPreferenceArray(newItemIds.size());
						newPrefsForUser.setUserID(0, userId);
						
						for(int n=0; n < newItemIds.size();n++){
							newPrefsForUser.setItemID(n, newItemIds.get(n));
							newPrefsForUser.setValue(n, newPrefValues.get(n));
							newPrefsForUser.setContextualPreferences(n, Longs.toArray(newContextualPrefs.get(n)));
							
						}
						
						preferences.put(userId, newPrefsForUser);
					}
				}else{
					preferences.put(userId, prefsForUser);
				}
				
			}
			//System.out.println(counter);
			DataModel filteredDataModel = new ContextualDataModel(preferences);
			
			return filteredDataModel;
		}
		
		@Override
		public Recommender buildRecommender(DataModel model)
				throws TasteException {
			
			if(model instanceof ContextualDataModel){
				System.out.println("Number of ratings: "+Functions.numOfRatings(model));
				return this.recommenderBuilder.buildRecommender(model);
			}else{
				DataModel contextualmodel = this.preFilterDataModel(model);
				System.out.println("Number of ratings: "+Functions.numOfRatings(contextualmodel));
				return this.recommenderBuilder.buildRecommender(contextualmodel);
			}
		}
		
	}
	
	/*public class PostFilteringContextualBuildRecommender implements RecommenderBuilder{

		private ContextualCriteria contextualAttributes;
		private RecommenderBuilder recommenderBuilder;
		
		public PostFilteringContextualBuildRecommender(ContextualCriteria contexutalAttributes, RecommenderBuilder recommenderBuilder) {
			this.contextualAttributes = contexutalAttributes;
			this.recommenderBuilder = recommenderBuilder;
		}
		
		@Override
		public Recommender buildRecommender(DataModel model)
				throws TasteException {
			
			return this.recommenderBuilder.buildRecommender(model);
		}
		
	}*/
	
	
}
