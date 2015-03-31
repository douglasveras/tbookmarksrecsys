package br.cin.tbookmarks.recommender.similarity;

import java.util.ArrayList;

import org.apache.mahout.cf.taste.recommender.IDRescorer;

import br.cin.tbookmarks.recommender.database.AbstractDataset;
import br.cin.tbookmarks.recommender.database.ItemDomain;
import br.cin.tbookmarks.recommender.database.ItemInformation;

public class ItemDomainRescorer implements IDRescorer {

	private final ArrayList<ItemDomain> itemDomainCriteria;
	private final ArrayList<ItemDomain> itemDomainCriteriaExclusion;
	private final AbstractDataset dataset;

	public ItemDomainRescorer(ArrayList<ItemDomain> itemDomainCriteria, ArrayList<ItemDomain> itemDomainCriteriaExclusion, AbstractDataset dataset) {
		this.itemDomainCriteria = itemDomainCriteria;
		this.dataset = dataset;
		this.itemDomainCriteriaExclusion = itemDomainCriteriaExclusion;
	}

	@Override
	public double rescore(long id, double originalScore) {
		ItemInformation itemInfo = this.dataset.getRecommendedItemInformationByID(id);
		if(this.itemDomainCriteria != null){
			for (ItemDomain itemDomain : this.itemDomainCriteria) {
				if (itemInfo.getItemDomain().equals(itemDomain)) {
					return originalScore * 2;
				}
	
			}
		}
		return originalScore;
	}

	@Override
	public boolean isFiltered(long id) {
		ItemInformation itemInfo = this.dataset.getRecommendedItemInformationByID(id);
		if(this.itemDomainCriteriaExclusion != null){
			for (ItemDomain itemDomain : this.itemDomainCriteriaExclusion) {
				if (itemInfo.getItemDomain().equals(itemDomain)) {
					return true;
				}
	
			}
		}
		return false;
	}

}
