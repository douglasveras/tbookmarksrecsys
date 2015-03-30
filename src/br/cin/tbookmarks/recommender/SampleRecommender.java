package br.cin.tbookmarks.recommender;

import java.util.ArrayList;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;

import br.cin.tbookmarks.recommender.database.AbstractDataset;
import br.cin.tbookmarks.recommender.database.BookCrossingDataset;
import br.cin.tbookmarks.recommender.database.BooksTwitterDataset;
import br.cin.tbookmarks.recommender.database.EventsTwitterDataset;
import br.cin.tbookmarks.recommender.database.GroupLensDataset;
import br.cin.tbookmarks.recommender.database.ItemCategory;
import br.cin.tbookmarks.recommender.database.ItemDomain;
import br.cin.tbookmarks.recommender.database.MoviesCrossBooksDataset;
import br.cin.tbookmarks.recommender.database.MoviesCrossEventsDataset;
import br.cin.tbookmarks.recommender.similarity.ItemCategoryRescorer;
import br.cin.tbookmarks.recommender.similarity.ItemDomainRescorer;

public class SampleRecommender {

	private DataModel model;
	private List<RecommendedItem> recommendedItems;
	private AbstractDataset absDataset;
	private IDRescorer idrescorer;
	
	private Recommenders recommenders;
	private int maxOfRecommendedItems = 5;

	public SampleRecommender(AbstractDataset absDataset, IDRescorer idrescorer) {
		this.absDataset = absDataset;
		this.model = this.absDataset.getModel();
		this.recommendedItems = new ArrayList<RecommendedItem>();
		this.recommenders = new Recommenders(this.absDataset);

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
		
		AbstractDataset absDataset = MoviesCrossBooksDataset.getInstance();
		
		IDRescorer idrescorer = new ItemDomainRescorer(null,ItemDomain.MOVIE, absDataset);
		
		SampleRecommender sr = new SampleRecommender(absDataset, idrescorer);

		ArrayList<RecommenderBuilder> list = sr.recommenders
				.getRecommenderBuilders();

		try {
			int userId = 6041; //6041

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

}
