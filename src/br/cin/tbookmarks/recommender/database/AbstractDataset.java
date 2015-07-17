package br.cin.tbookmarks.recommender.database;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import org.apache.mahout.cf.taste.model.DataModel;

import br.cin.tbookmarks.recommender.database.contextual.ContextualFileDataModel;
import br.cin.tbookmarks.recommender.database.item.ItemDatasetInformation;
import br.cin.tbookmarks.recommender.database.item.ItemInformation;
import br.cin.tbookmarks.recommender.database.item.ItemInformationComparatorID;

public abstract class AbstractDataset {
	protected DataModel model;
	protected ItemDatasetInformation itemDatasetInformation;
	protected String datasetURL;

	public DataModel getModel() {
		return model;
	}

	public ItemInformation getRecommendedItemInformationByID(long id) {
		/*
		 * for (ItemInformation item : getItemDatasetInformation().getItens()) {
		 * if (item.getId() == id) { return item; } } return null;
		 */
		
		ItemInformation p = new ItemInformation();
		p.setId(id); // Essa pessoa será usada como critério de comparação para
						// a busca binária
		int ResultIndex = Collections.binarySearch(getItemDatasetInformation()
				.getItens(), p, new ItemInformationComparatorID()); // Busca
																	// Binária
																	// com o
																	// objeto
																	// comparador
		if (ResultIndex > -1) {
			return getItemDatasetInformation().getItens().get(ResultIndex);
		} else {
			return null;
		}
	}

	public ItemDatasetInformation getItemDatasetInformation() {
		return itemDatasetInformation;
	}

	protected void initializeDataModel() throws IOException {
		model = new ContextualFileDataModel(new File(
				System.getProperty("user.dir") + datasetURL));
	}
}
