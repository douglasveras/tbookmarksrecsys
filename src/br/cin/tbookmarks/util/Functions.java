package br.cin.tbookmarks.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;

import br.cin.tbookmarks.recommender.database.AbstractDataset;
import br.cin.tbookmarks.recommender.database.BooksTwitterDataset;
import br.cin.tbookmarks.recommender.database.EventsTwitterDataset;
import br.cin.tbookmarks.recommender.database.GroupLensDataset;
import br.cin.tbookmarks.recommender.database.item.ItemDomain;

public final class Functions {
		
	public static final int numOfRatings(DataModel datamodel){
		int counter = 0;
		try {
			LongPrimitiveIterator iterator = datamodel.getUserIDs();
			
			while(iterator.hasNext()){
				long userId = iterator.nextLong();
				counter = counter + datamodel.getPreferencesFromUser(userId).getIDs().length;
			}
			
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return counter;
	}
	
	public static final int[] getNumOfUsersAndOverlappedUsers(DataModel datamodel, AbstractDataset dataset){

		int counterOverlapped = 0,counterUsers = 0;
				
		try {
			LongPrimitiveIterator iterator = datamodel.getUserIDs();
			
			while(iterator.hasNext()){
				counterUsers++;
				long userId = iterator.nextLong();
				long itemsFromUser[] = datamodel.getPreferencesFromUser(userId).getIDs(); 
				
				boolean hasMovie = false;
				//boolean hasEvent = false;
				boolean hasBook = false;
				
				for(int i=0; i < itemsFromUser.length;i++){
					if(!hasMovie && dataset.getRecommendedItemInformationByID(itemsFromUser[i]).getItemDomain().equals(ItemDomain.MOVIE)){
						hasMovie = true;
					}else if(!hasBook && dataset.getRecommendedItemInformationByID(itemsFromUser[i]).getItemDomain().equals(ItemDomain.BOOK)){
						hasBook = true;
					}
					
					if(hasMovie && hasBook){
						counterOverlapped++;
						break;
					}
				}
			}
			
			System.out.println("Number of users:"+counterUsers+", overlapped: "+counterOverlapped);
			
			
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int info[] = {counterUsers,counterOverlapped};
		return info;
	
	}
	
	public static final void printNumOfItemsPerDomain(DataModel datamodel){
		HashMap<AbstractDataset,HashSet<Long>> numOfItemsPerDomain = new HashMap<AbstractDataset, HashSet<Long>>();
		numOfItemsPerDomain.put(GroupLensDataset.getInstance(), new HashSet<Long>());
		numOfItemsPerDomain.put(BooksTwitterDataset.getInstance(), new HashSet<Long>());
		numOfItemsPerDomain.put(EventsTwitterDataset.getInstance(), new HashSet<Long>());
		try {
			LongPrimitiveIterator iterator = datamodel.getUserIDs();
			
			while(iterator.hasNext()){
				long userId = iterator.nextLong();
				long itemsFromUser[] = datamodel.getPreferencesFromUser(userId).getIDs(); 
				for(int i=0; i < itemsFromUser.length;i++){
					if(GroupLensDataset.getInstance().getRecommendedItemInformationByID(itemsFromUser[i]) != null){
						Long itemID= new Long(itemsFromUser[i]);
						if(!numOfItemsPerDomain.get(GroupLensDataset.getInstance()).contains(itemID)){
							numOfItemsPerDomain.get(GroupLensDataset.getInstance()).add(itemID);
						}
					}else if(BooksTwitterDataset.getInstance().getRecommendedItemInformationByID(itemsFromUser[i]) != null){
						Long itemID= new Long(itemsFromUser[i]);
						if(!numOfItemsPerDomain.get(BooksTwitterDataset.getInstance()).contains(itemID)){
							numOfItemsPerDomain.get(BooksTwitterDataset.getInstance()).add(itemID);
						}
					}else if(EventsTwitterDataset.getInstance().getRecommendedItemInformationByID(itemsFromUser[i]) != null){
						Long itemID= new Long(itemsFromUser[i]);
						if(!numOfItemsPerDomain.get(EventsTwitterDataset.getInstance()).contains(itemID)){
							numOfItemsPerDomain.get(EventsTwitterDataset.getInstance()).add(itemID);
						}
					}
				}
			}
			
			for(AbstractDataset abs:  numOfItemsPerDomain.keySet()){
				System.out.println("Number of items in "+abs.getClass().getSimpleName()+": "+numOfItemsPerDomain.get(abs).size());
			}
			
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		 String text    =
                 "Genres[404274]|Kids & Family[132]|Mopa|"
               ;

		 String patternString = "Genres\\[.*\\]\\|(.*?)\\|";

       Pattern pattern = Pattern.compile(patternString);
       Matcher matcher = pattern.matcher(text);

       while(matcher.find()) {
           System.out.println("found: " + matcher.group().split("\\|")[1].trim());
       }
	}
}
