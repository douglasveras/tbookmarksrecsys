package br.cin.tbookmarks.recommender;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import br.cin.tbookmarks.recommender.algorithms.Recommenders;
import br.cin.tbookmarks.recommender.database.AbstractDataset;
import br.cin.tbookmarks.recommender.database.AmazonCrossDataset;
import br.cin.tbookmarks.recommender.database.contextual.ContextualCriteria;
import br.cin.tbookmarks.recommender.database.contextual.DayTypeContextualAttribute;
import br.cin.tbookmarks.recommender.database.item.ItemDomain;
import br.cin.tbookmarks.recommender.similarity.ItemDomainRescorer;

public class SampleRecommender {/*

	private DataModel model;
	private List<RecommendedItem> recommendedItems;
	private AbstractDataset absDataset;
	private IDRescorer idrescorer;
	
	private Recommenders recommenders;
	private int maxOfRecommendedItems = 5;

	public SampleRecommender(AbstractDataset absDataset, IDRescorer idrescorer, ContextualCriteria contextualAttributes) {
		this.absDataset = absDataset;
		this.model = this.absDataset.getModel();
		this.recommendedItems = new ArrayList<RecommendedItem>();
		this.recommenders = new Recommenders(this.absDataset,contextualAttributes);

		this.idrescorer = idrescorer;

	}

	public DataModel getModel() {
		return model;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}

	public List<RecommendedItem> getRecommendedItems() {
		return recommendedItems;
	}

	public void setRecommendedItems(List<RecommendedItem> recommendedItems) {
		this.recommendedItems = recommendedItems;
	}

	public void recommendByAlgorithm(long userId, int numberOfRecommendations,
			RecommenderBuilder rb) throws TasteException {
		
		this.recommendedItems = rb.buildRecommender(this.model).recommend(
				userId, numberOfRecommendations,this.idrescorer);

	}

	private void printInfoRecommendations() {
		int position = 1;
		for (RecommendedItem recommendation : this.recommendedItems) {
			System.out.println(position + ": " + recommendation.getItemID()
					+ " - "
					+ absDataset.getRecommendedItemInformationByID(recommendation.getItemID()).getName()
					+ " - " + recommendation.getValue()
					+ " - " + absDataset.getRecommendedItemInformationByID(recommendation.getItemID()).getCategories()
					+ " - " + absDataset.getRecommendedItemInformationByID(recommendation.getItemID()).getItemDomain());
			position++;
		}
	}

	public static void main(String[] args) {
		
		HashSet<ItemDomain> domainsDataset = new HashSet<ItemDomain>();
		domainsDataset.add(ItemDomain.BOOK);
		domainsDataset.add(ItemDomain.MOVIE);
		//domainsFilter.add(ItemDomain.BOOK);
		
		AbstractDataset absDataset = AmazonCrossDataset.getInstance();	
		
		HashSet<ItemDomain> domainsFilter = new HashSet<ItemDomain>();
		domainsFilter.add(ItemDomain.BOOK);
		
		IDRescorer idrescorer = new ItemDomainRescorer(null,domainsFilter, absDataset);
		
		//ContextualCriteria criteria = new ContextualCriteria(DayTypeContextualAttribute.WEEKEND,PeriodOfDayContextualAttribute.DAWN);
		ContextualCriteria criteria = new ContextualCriteria(DayTypeContextualAttribute.WEEKEND,null);
		
		SampleRecommender sr = new SampleRecommender(absDataset, idrescorer,criteria);
		

		ArrayList<RecommenderBuilder> list = sr.recommenders
				.getRecommenderBuilders();

		try {
			int userId =41; //6041

			for (RecommenderBuilder recommenderBuilder : list) {
				System.out.println("\n"+recommenderBuilder.getClass()
						.getSimpleName() + ">>>>>>>>>>>>>>>");
				sr.recommendByAlgorithm(userId, sr.maxOfRecommendedItems,
						recommenderBuilder);
				sr.printInfoRecommendations();
			}
			System.out.println("FINISHED!!");

		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

*/}
