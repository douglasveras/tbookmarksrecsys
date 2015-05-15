package br.cin.tbookmarks.recommender;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.Recommender;

import br.cin.tbookmarks.recommender.database.AbstractDataset;
import br.cin.tbookmarks.recommender.database.UserCategoriesPrefsInContexts;
import br.cin.tbookmarks.recommender.database.contextual.AbstractContextualAttribute;
import br.cin.tbookmarks.recommender.database.contextual.ContextualCriteria;
import br.cin.tbookmarks.recommender.database.contextual.ContextualPreference;
import br.cin.tbookmarks.recommender.database.contextual.ContextualUserPreferenceArray;
import br.cin.tbookmarks.recommender.database.item.ItemCategory;

public class PostFilteringStrategyRecommendation {
	public static enum PossibleFilteringStrategies implements AbstractPostFilteringStrategiesEnum{

		AT_LEAST_ONE_OCCURENCY, AT_LEAST_TWO_OCCURRENCIES, MOST_OCCURRED, AT_LEAST_MEDIA_OF_OCCURRENCIES;
		
	}
	
	public static enum PossibleAdjustingStrategies implements AbstractPostFilteringStrategiesEnum{

		NUMBER_OF_CATEGORIES, NUMBER_OF_OCCURENCIES;
		
	}
	
	private AbstractPostFilteringStrategiesEnum postFilteringStrategy;
	
	private boolean onlyWithGoodRatings;
	private float goodRatingMin;

	public boolean isOnlyWithGoodRatings() {
		return onlyWithGoodRatings;
	}
	
	public float getGoodRatingMin() {
		return goodRatingMin;
	}
	
	public void setGoodRatingMin(float goodRatingMin) {
		this.goodRatingMin = goodRatingMin;
	}
	
	public void setOnlyWithGoodRatings(boolean onlyWithGoodRatings) {
		this.onlyWithGoodRatings = onlyWithGoodRatings;
	}
	
	//private  HashMap<Long,HashMap<ContextualCriteria,HashMap<ItemCategory,Integer>>> userPrefs = UserCategoriesPrefsInContexts.getInstance().getUserPrefs();
	
	private UserCategoriesPrefsInContexts userCategoriesPrefsInContextsInstance = UserCategoriesPrefsInContexts.getInstance();
	
	public PostFilteringStrategyRecommendation(AbstractPostFilteringStrategiesEnum postFilteringStrategy, boolean onlyWithGoodRatings, float goodRatingMin) {
		this.postFilteringStrategy = postFilteringStrategy;
		this.onlyWithGoodRatings = onlyWithGoodRatings;
		this.goodRatingMin = goodRatingMin;
	}
	
	/*private String[] convertToStringList(long[] n){
		
		String[] converted = new String[n.length];
		
		for(int i=0; i<n.length; i++){
			converted[i] = String.valueOf(n[i]);
		}
		
		return converted;
	}*/
		
	public AbstractPostFilteringStrategiesEnum getPostFilteringStrategy() {
		return postFilteringStrategy;
	}
	
	private HashMap<ContextualCriteria, HashMap<ItemCategory, Integer>> getContextualCategoryPreferencesOnlyWithGoodRatings(
			long userId, DataModel model, AbstractDataset dataset) throws TasteException {
		
		Long userKey = new Long(userId);
		
		HashMap<ContextualCriteria,HashMap<ItemCategory,Integer>> contexttualCategoryPrefs = userCategoriesPrefsInContextsInstance.getUserPrefsWithGoodRatingsOnly().get(userKey);
		
		if(contexttualCategoryPrefs == null){
			
			contexttualCategoryPrefs = new HashMap<ContextualCriteria, HashMap<ItemCategory,Integer>>();
			
			PreferenceArray prefs = model.getPreferencesFromUser(userId);
			
			int size = prefs.length();
		    boolean isInstanceOfContextualUserPreferenceArray = prefs instanceof ContextualUserPreferenceArray;
		    
		    if(!isInstanceOfContextualUserPreferenceArray){
		    	throw new TasteException("Prefs are not ContextualUserPreferenceArray for Post-Filtering approach");
		    }
			    
			for (int i = 0; i < size; i++) {
				
				if(prefs.getValue(i) < this.getGoodRatingMin()){
					continue;
				}
				
				long[] contexts = ((ContextualUserPreferenceArray)prefs).getContextualPreferences(i);
				
				ContextualCriteria cc = new ContextualCriteria(contexts);
				
				Set<ItemCategory> categories = dataset.getRecommendedItemInformationByID(prefs.getItemID(i)).getCategories();
				
				HashMap<ItemCategory,Integer> categoryMap = contexttualCategoryPrefs.get(cc);
				
				if(categoryMap == null){
					HashMap<ItemCategory,Integer> newCategoryMap = new HashMap<ItemCategory, Integer>();
					
					for(ItemCategory category : categories){
						Integer numberOfOccurrences = newCategoryMap.get(category);
						if(numberOfOccurrences == null){
							newCategoryMap.put(category, 1);
						}else{
							newCategoryMap.put(category, ++numberOfOccurrences);
						}
						
					}
					contexttualCategoryPrefs.put(cc, newCategoryMap);
				}else{
					for(ItemCategory category : categories){
						Integer numberOfOccurrences = categoryMap.get(category);
						if(numberOfOccurrences == null){
							categoryMap.put(category, 1);
						}else{
							categoryMap.put(category, ++numberOfOccurrences);
						}
						
					}
				}
				
			}
		
			userCategoriesPrefsInContextsInstance.setUserPrefsWithGoodRatingsOnlyToUser(userKey, contexttualCategoryPrefs);
		}
		
		return contexttualCategoryPrefs;
	
	}
	
	private HashMap<ContextualCriteria,HashMap<ItemCategory,Integer>> getContextualCategoryPreferences(long userId, DataModel model,AbstractDataset dataset) throws TasteException{
		
		Long userKey = new Long(userId);
		
		HashMap<ContextualCriteria,HashMap<ItemCategory,Integer>> contexttualCategoryPrefs = userCategoriesPrefsInContextsInstance.getUserPrefs().get(userKey);
		
		if(contexttualCategoryPrefs == null){
			
			contexttualCategoryPrefs = new HashMap<ContextualCriteria, HashMap<ItemCategory,Integer>>();
			
			PreferenceArray prefs = model.getPreferencesFromUser(userId);
			
			int size = prefs.length();
		    boolean isInstanceOfContextualUserPreferenceArray = prefs instanceof ContextualUserPreferenceArray;
		    
		    if(!isInstanceOfContextualUserPreferenceArray){
		    	throw new TasteException("Prefs are not ContextualUserPreferenceArray for Post-Filtering approach");
		    }
			    
			for (int i = 0; i < size; i++) {
				
				long[] contexts = ((ContextualUserPreferenceArray)prefs).getContextualPreferences(i);
				
				ContextualCriteria cc = new ContextualCriteria(contexts);
				
				Set<ItemCategory> categories = dataset.getRecommendedItemInformationByID(prefs.getItemID(i)).getCategories();
				
				HashMap<ItemCategory,Integer> categoryMap = contexttualCategoryPrefs.get(cc);
				
				if(categoryMap == null){
					HashMap<ItemCategory,Integer> newCategoryMap = new HashMap<ItemCategory, Integer>();
					
					for(ItemCategory category : categories){
						Integer numberOfOccurrences = newCategoryMap.get(category);
						if(numberOfOccurrences == null){
							newCategoryMap.put(category, 1);
						}else{
							newCategoryMap.put(category, ++numberOfOccurrences);
						}
						
					}
					contexttualCategoryPrefs.put(cc, newCategoryMap);
				}else{
					for(ItemCategory category : categories){
						Integer numberOfOccurrences = categoryMap.get(category);
						if(numberOfOccurrences == null){
							categoryMap.put(category, 1);
						}else{
							categoryMap.put(category, ++numberOfOccurrences);
						}
						
					}
				}
				
			}
		
			userCategoriesPrefsInContextsInstance.setUserPrefsToUser(userKey, contexttualCategoryPrefs);
		}
		
		return contexttualCategoryPrefs;
	}
	
	private boolean containsAtLeastOneCategory(ItemCategory categoryItem, Set<ItemCategory> categories){
		for(ItemCategory category : categories){
			if(categoryItem.equals(category)){
				return true;
			}
		}
		
		return false;
	}
	
	public float filterOrAdjustPreference(long userID, long itemID,
			ContextualCriteria contextualAttributes, Recommender delegated, AbstractDataset dataset) throws TasteException {

		HashMap<ContextualCriteria,HashMap<ItemCategory,Integer>> contexttualCategoryPrefs;
		if(this.isOnlyWithGoodRatings()){
			contexttualCategoryPrefs = getContextualCategoryPreferencesOnlyWithGoodRatings(userID,delegated.getDataModel(),dataset);
		}else{
			contexttualCategoryPrefs =  getContextualCategoryPreferences(userID,delegated.getDataModel(),dataset);
		}
		
		if(this.postFilteringStrategy.equals(PossibleFilteringStrategies.AT_LEAST_ONE_OCCURENCY)){
			if(contexttualCategoryPrefs.get(contextualAttributes) !=null){
				for(ItemCategory categoryPreferred : contexttualCategoryPrefs.get(contextualAttributes).keySet()){
					if(containsAtLeastOneCategory(categoryPreferred,dataset.getRecommendedItemInformationByID(itemID).getCategories())){
						return delegated.estimatePreference(userID, itemID);
					}
				}
			}
		}else if(this.postFilteringStrategy.equals(PossibleFilteringStrategies.AT_LEAST_TWO_OCCURRENCIES)){
			if(contexttualCategoryPrefs.get(contextualAttributes) !=null){
				for(ItemCategory categoryPreferred : contexttualCategoryPrefs.get(contextualAttributes).keySet()){
					Integer numberOfOccurrences = contexttualCategoryPrefs.get(contextualAttributes).get(categoryPreferred);
					if(numberOfOccurrences >= 2 && containsAtLeastOneCategory(categoryPreferred,dataset.getRecommendedItemInformationByID(itemID).getCategories())){
						return delegated.estimatePreference(userID, itemID);
					}
				}
			}
		}else if(this.postFilteringStrategy.equals(PossibleFilteringStrategies.MOST_OCCURRED)){
			if(contexttualCategoryPrefs.get(contextualAttributes) !=null){
				HashSet<ItemCategory> mostOccurred = new HashSet<ItemCategory>();
				Integer occurrencies = 0;
				for(ItemCategory categoryPreferred : contexttualCategoryPrefs.get(contextualAttributes).keySet()){
					if(mostOccurred.size() == 0){
						mostOccurred.add(categoryPreferred);
						occurrencies = contexttualCategoryPrefs.get(contextualAttributes).get(categoryPreferred);
					}else{
						if(contexttualCategoryPrefs.get(contextualAttributes).get(categoryPreferred) > occurrencies){
							mostOccurred = new HashSet<ItemCategory>();
							mostOccurred.add(categoryPreferred);
							occurrencies = contexttualCategoryPrefs.get(contextualAttributes).get(categoryPreferred);
						}else if(contexttualCategoryPrefs.get(contextualAttributes).get(categoryPreferred) == occurrencies){
							mostOccurred.add(categoryPreferred);
						}
					}
				}
				for(ItemCategory categoryMost : mostOccurred){
					if(containsAtLeastOneCategory(categoryMost,dataset.getRecommendedItemInformationByID(itemID).getCategories())){
						return delegated.estimatePreference(userID, itemID);
					}
				}
			}
		}else if(this.postFilteringStrategy.equals(PossibleFilteringStrategies.AT_LEAST_MEDIA_OF_OCCURRENCIES)){
			
			if(contexttualCategoryPrefs.get(contextualAttributes) !=null){

				int max = 0;
				
				Integer occurrencies = 0;
				for(ItemCategory categoryPreferred : contexttualCategoryPrefs.get(contextualAttributes).keySet()){
					occurrencies = contexttualCategoryPrefs.get(contextualAttributes).get(categoryPreferred);
					
					if(occurrencies > max){
						max = occurrencies;
					}
					
				}
				int media = (max*2)/3;
				
				for(ItemCategory categoryPreferred : contexttualCategoryPrefs.get(contextualAttributes).keySet()){
					Integer numberOfOccurrences = contexttualCategoryPrefs.get(contextualAttributes).get(categoryPreferred);
					if(numberOfOccurrences >= media && containsAtLeastOneCategory(categoryPreferred,dataset.getRecommendedItemInformationByID(itemID).getCategories())){
						return delegated.estimatePreference(userID, itemID);
					}
				}
			}
		}
		
		//System.out.println("Not estimated because post-filtering: "+userID+" "+itemID+" "+delegated.estimatePreference(userID, itemID));
		//System.out.println(contexttualCategoryPrefs);
		
		return Float.NaN;
	}

	


	
}
