package br.cin.tbookmarks.util;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;

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
}
