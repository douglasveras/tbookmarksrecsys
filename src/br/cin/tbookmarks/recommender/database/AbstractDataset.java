package br.cin.tbookmarks.recommender.database;

import org.apache.mahout.cf.taste.model.DataModel;

public abstract class AbstractDataset {
	protected DataModel model;
	protected ItemDatasetInformation itemDatasetInformation;
	
	public DataModel getModel() {
		return model;
	}
	
	public ItemInformation getRecommendedItemInformationByID(long id) {
		for (ItemInformation item : getItemDatasetInformation().getItens()) {
			if (item.getId() == id) {
				return item;
			}
		}
		return null;
	}
	
	public ItemDatasetInformation getItemDatasetInformation() {
		return itemDatasetInformation;
	}
}
