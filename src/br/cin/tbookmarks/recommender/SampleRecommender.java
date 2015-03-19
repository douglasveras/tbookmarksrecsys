package br.cin.tbookmarks.recommender;

import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import br.cin.tbookmarks.recommender.database.AbstractDataset;
import br.cin.tbookmarks.recommender.database.GroupLensDataset;
import br.cin.tbookmarks.recommender.database.ItemCategory;
import br.cin.tbookmarks.recommender.similarity.ItemCategoryRescorer;

public class SampleRecommender {

	private DataModel model;
	private List<RecommendedItem> recommendedItems;
	private AbstractDataset absDataset = GroupLensDataset.getInstance();

	private Recommenders recommenders;
	private int maxOfRecommendedItems = 3;

	public SampleRecommender() {
		this.model = absDataset.getModel();
		this.recommendedItems = new ArrayList<RecommendedItem>();
		this.recommenders = new Recommenders();

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
		
		/*ItemCategoryRescorer icr = new ItemCategoryRescorer(ItemCategory.MUSICAL,null, absDataset);
		
		this.recommendedItems = rb.buildRecommender(this.model).recommend(
				userId, numberOfRecommendations,icr);*/
		this.recommendedItems = rb.buildRecommender(this.model).recommend(
				userId, numberOfRecommendations);
	}

	private void printInfoRecommendations() {
		int position = 1;
		for (RecommendedItem recommendation : this.recommendedItems) {
			System.out.println(position + ": " + recommendation.getItemID()
					+ " - "
					+ absDataset.getRecommendedItemInformationByID(recommendation.getItemID()).getName()
					+ " - " + recommendation.getValue()
					+ " - " + absDataset.getRecommendedItemInformationByID(recommendation.getItemID()).getCategories());
			position++;
		}
	}

	public static void main(String[] args) {
		SampleRecommender sr = new SampleRecommender();

		ArrayList<RecommenderBuilder> list = sr.recommenders
				.getRecommenderBuilders();

		try {
			int userId = 2;

			for (RecommenderBuilder recommenderBuilder : list) {
				System.out.println("\n"+recommenderBuilder.getClass()
						.getSimpleName() + ">>>>>>>>>>>>>>>");
				sr.recommendByAlgorithm(userId, sr.maxOfRecommendedItems,
						recommenderBuilder);
				sr.printInfoRecommendations();
			}

		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
