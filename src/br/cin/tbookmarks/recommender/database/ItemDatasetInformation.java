package br.cin.tbookmarks.recommender.database;

import java.util.ArrayList;
import java.util.List;

public class ItemDatasetInformation {
	List<ItemInformation> itens;
	
	public ItemDatasetInformation() {
		itens = new ArrayList<ItemInformation>();
	}

	public List<ItemInformation> getItens() {
		return itens;
	}

	public void setItens(List<ItemInformation> itens) {
		this.itens = itens;
	}
	
	
}
