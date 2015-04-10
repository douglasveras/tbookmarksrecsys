package br.cin.tbookmarks.recommender.database;

import java.io.File;
import java.io.IOException;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.model.DataModel;

import br.cin.tbookmarks.recommender.database.contextual.ContextualFileDataModel;
import br.cin.tbookmarks.recommender.database.item.ItemDatasetInformation;
import br.cin.tbookmarks.recommender.database.item.ItemInformation;

public abstract class AbstractDataset {
	protected DataModel model;
	protected ItemDatasetInformation itemDatasetInformation;
	protected String datasetURL;
	
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
	
	protected void initializeDataModel() throws IOException {
		model = new ContextualFileDataModel(new File(System.getProperty("user.dir")
				+ datasetURL));
	}
}
