package br.cin.tbookmarks.recommender.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.Recommender;

import br.cin.tbookmarks.recommender.database.contextual.ContextualCriteria;
import br.cin.tbookmarks.recommender.database.contextual.ContextualDataModel;
import br.cin.tbookmarks.recommender.database.contextual.ContextualUserPreferenceArray;

import com.google.common.primitives.Longs;

public class PreFilteringContextualBuildRecommender implements
		RecommenderBuilder {

	//private ContextualCriteria contextualAttributes;
	private RecommenderBuilder recommenderBuilder;
	
	/*public PreFilteringContextualBuildRecommender(ContextualCriteria contexutalAttributes, RecommenderBuilder recommenderBuilder) {
		this.contextualAttributes = contexutalAttributes;
		this.recommenderBuilder = recommenderBuilder;
	}*/
	public PreFilteringContextualBuildRecommender(RecommenderBuilder recommenderBuilder) {
		this.recommenderBuilder = recommenderBuilder;
	}
	
	@Override
	public String toString() {
		return "PreF"+"(CF-based="+recommenderBuilder+")";
	}
	
	public DataModel preFilterDataModel(DataModel model, ContextualCriteria contextualAttributes) throws TasteException{
		
		//criar um novo datamodel verificando cada preferencia e adicionando no novo datamodel caso case com o contexto
		
		FastByIDMap<PreferenceArray> preferences = new FastByIDMap<PreferenceArray>();
		LongPrimitiveIterator userIdsIterator = model.getUserIDs();
		
		
		while(userIdsIterator.hasNext()){
			
			Long userId = userIdsIterator.next();
			PreferenceArray prefsForUser = model.getPreferencesFromUser(userId);
			if(contextualAttributes != null && prefsForUser instanceof ContextualUserPreferenceArray){
				ContextualUserPreferenceArray contextualPrefsForUser = (ContextualUserPreferenceArray) prefsForUser;
				ArrayList<Long> newItemIds = new ArrayList<Long>();
				ArrayList<Float> newPrefValues = new ArrayList<Float>();
				ArrayList<List<Long>> newContextualPrefs = new ArrayList<List<Long>>();
				
				for(int i = 0; i < contextualPrefsForUser.getIDs().length; i++){
					
					if(contextualAttributes.containsAllContextualAttributes(contextualPrefsForUser.get(i).getContextualPreferences())){
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
		
		if(model instanceof ContextualDataModel == false){
			throw new TasteException("Model is not ContextualDataModel in PreF");
		}
		
		return this.recommenderBuilder.buildRecommender(model);
		
		/*if(model instanceof ContextualDataModel){
			//System.out.println("Number of ratings: "+Functions.numOfRatings(model));
			return this.recommenderBuilder.buildRecommender(model);
		}else{
			DataModel contextualmodel = this.preFilterDataModel(model);
			//System.out.println("Number of ratings: "+Functions.numOfRatings(contextualmodel));
			return this.recommenderBuilder.buildRecommender(contextualmodel);
		}*/
	}
	
}
