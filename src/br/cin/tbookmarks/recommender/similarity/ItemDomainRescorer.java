package br.cin.tbookmarks.recommender.similarity;

import org.apache.mahout.cf.taste.recommender.IDRescorer;

import br.cin.tbookmarks.recommender.database.AbstractDataset;
import br.cin.tbookmarks.recommender.database.ItemDomain;
import br.cin.tbookmarks.recommender.database.ItemInformation;

public class ItemDomainRescorer implements IDRescorer {

	private final ItemDomain itemDomainCriteria;
	private final ItemDomain itemDomainCriteriaExclusion;
	private final AbstractDataset dataset;

	public ItemDomainRescorer(ItemDomain itemDomainCriteria, ItemDomain itemDomainCriteriaExclusion, AbstractDataset dataset) {
		this.itemDomainCriteria = itemDomainCriteria;
		this.dataset = dataset;
		this.itemDomainCriteriaExclusion = itemDomainCriteriaExclusion;
	}

	@Override
	public double rescore(long id, double originalScore) {
		ItemInformation itemInfo = this.dataset.getRecommendedItemInformationByID(id);
		if(itemInfo.getItemDomain().equals(this.itemDomainCriteria)) {
			return originalScore * 2;
	}
		return originalScore;
	}

	@Override
	public boolean isFiltered(long id) {
		ItemInformation itemInfo = this.dataset.getRecommendedItemInformationByID(id);
		if(itemInfo.getItemDomain().equals(this.itemDomainCriteriaExclusion)) {
				return true;
		}
		return false;
	}

}
