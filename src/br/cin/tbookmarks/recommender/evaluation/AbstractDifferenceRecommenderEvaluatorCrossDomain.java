package br.cin.tbookmarks.recommender.evaluation;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mahout.cf.taste.common.NoSuchItemException;
import org.apache.mahout.cf.taste.common.NoSuchUserException;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FullRunningAverageAndStdDev;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.common.RunningAverageAndStdDev;
import org.apache.mahout.cf.taste.impl.eval.AbstractDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericPreference;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.Preference;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.recommender.IDRescorer;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.common.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.cin.tbookmarks.recommender.database.contextual.ContextualCriteria;
import br.cin.tbookmarks.recommender.database.contextual.ContextualDataModel;
import br.cin.tbookmarks.recommender.database.contextual.ContextualPreference;
import br.cin.tbookmarks.recommender.database.contextual.ContextualUserPreferenceArray;

import com.google.appengine.api.ThreadManager;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * Abstract superclass of a couple implementations, providing shared
 * functionality.
 */
public abstract class AbstractDifferenceRecommenderEvaluatorCrossDomain
		implements RecommenderEvaluator {

	private static final Logger log = LoggerFactory
			.getLogger(AbstractDifferenceRecommenderEvaluatorCrossDomain.class);

	protected int totalOfTrainingRatingsFromSource;
	protected int totalOfTrainingRatingsFromTargetWithoutContext;
	protected int totalOfTrainingRatingsFromTargetWithContext;
	protected int totalOfTestRatings;

	private final Random random;
	private float maxPreference;
	private float minPreference;
	protected IDRescorer idrescorer;
	protected ContextualCriteria contextualCriteria;

	protected AbstractDifferenceRecommenderEvaluatorCrossDomain() {
		random = RandomUtils.getRandom();
		maxPreference = Float.NaN;
		minPreference = Float.NaN;
	}

	@Override
	public final float getMaxPreference() {
		return maxPreference;
	}

	@Override
	public final void setMaxPreference(float maxPreference) {
		this.maxPreference = maxPreference;
	}

	@Override
	public final float getMinPreference() {
		return minPreference;
	}

	@Override
	public final void setMinPreference(float minPreference) {
		this.minPreference = minPreference;
	}

	@Override
	public double evaluate(RecommenderBuilder recommenderBuilder,
			DataModelBuilder dataModelBuilder, DataModel dataModel,
			double trainingPercentage, double evaluationPercentage)
			throws TasteException {
		Preconditions.checkNotNull(recommenderBuilder);
		Preconditions.checkNotNull(dataModel);
		Preconditions.checkArgument(trainingPercentage >= 0.0
				&& trainingPercentage <= 1.0, "Invalid trainingPercentage: "
				+ trainingPercentage
				+ ". Must be: 0.0 <= trainingPercentage <= 1.0");
		Preconditions.checkArgument(evaluationPercentage >= 0.0
				&& evaluationPercentage <= 1.0,
				"Invalid evaluationPercentage: " + evaluationPercentage
						+ ". Must be: 0.0 <= evaluationPercentage <= 1.0");

		log.info("Beginning evaluation using {} of {}", trainingPercentage,
				dataModel);

		int numUsers = dataModel.getNumUsers();
		FastByIDMap<PreferenceArray> trainingPrefs = new FastByIDMap<PreferenceArray>(
				1 + (int) (evaluationPercentage * numUsers));
		FastByIDMap<PreferenceArray> testPrefs = new FastByIDMap<PreferenceArray>(
				1 + (int) (evaluationPercentage * numUsers));

		totalOfTrainingRatingsFromSource = 0;
		totalOfTrainingRatingsFromTargetWithContext = 0;
		totalOfTrainingRatingsFromTargetWithoutContext = 0;
		totalOfTestRatings = 0;

		LongPrimitiveIterator it = dataModel.getUserIDs();
		while (it.hasNext()) {
			long userID = it.nextLong();
			if (random.nextDouble() < evaluationPercentage) {
				splitOneUsersPrefs(trainingPercentage, trainingPrefs,
						testPrefs, userID, dataModel);
			}
		}

		// System.out.println("Training (Source, TargetWithoutContext, TargetWithContext): "+totalOfTrainingRatingsFromSource+"/"+totalOfTrainingRatingsFromTargetWithoutContext+"/"+totalOfTrainingRatingsFromTargetWithContext);
		// int totalTraining =
		// (totalOfTrainingRatingsFromSource+totalOfTrainingRatingsFromTargetWithContext+totalOfTrainingRatingsFromTargetWithoutContext);
		// System.out.println("Training/Test: "+totalTraining+"/"+totalOfTestRatings);

		DataModel newDataModel = dataModel instanceof ContextualDataModel ? new ContextualDataModel(
				trainingPrefs) : new GenericDataModel(trainingPrefs);

		DataModel trainingModel = dataModelBuilder == null ? newDataModel
				: dataModelBuilder.buildDataModel(trainingPrefs);

		Recommender recommender = recommenderBuilder
				.buildRecommender(trainingModel);

		double result = getEvaluation(testPrefs, recommender);
		log.info("Evaluation result: {}", result);
		return result;
	}

	/*
	 * private void removeTestsFromRescorer(FastByIDMap<PreferenceArray>
	 * testPrefs) { for (Map.Entry<Long,PreferenceArray> entry :
	 * testPrefs.entrySet()) { long ids[] = entry.getValue().getIDs();
	 * 
	 * HashMap<Long,Float> itemValue = new HashMap<Long, Float>();
	 * 
	 * for(int i = 0; i < ids.length; i++){
	 * if(!this.idrescorer.isFiltered(ids[i])){ itemValue.put(ids[i],
	 * entry.getValue().getValue(i)); } } PreferenceArray value = new
	 * GenericUserPreferenceArray(itemValue.keySet().size()); value.setUserID(0,
	 * entry.getValue().getUserID(0));
	 * 
	 * int index = 0;
	 * 
	 * for (Long itemId : itemValue.keySet()) { value.setItemID(index, itemId);
	 * value.setValue(index, itemValue.get(itemId));
	 * 
	 * index++; } entry.setValue(value);
	 * 
	 * }
	 * 
	 * 
	 * }
	 */

	private void splitOneUsersPrefs(double trainingPercentage,
			FastByIDMap<PreferenceArray> trainingPrefs,
			FastByIDMap<PreferenceArray> testPrefs, long userID,
			DataModel dataModel) throws TasteException {
		List<Preference> oneUserTrainingPrefs = null;
		List<Preference> oneUserTestPrefs = null;
		PreferenceArray prefs = dataModel.getPreferencesFromUser(userID);
		int size = prefs.length();
		boolean isInstanceOfContextualUserPreferenceArray = prefs instanceof ContextualUserPreferenceArray;

		for (int i = 0; i < size; i++) {
			Preference newPref = isInstanceOfContextualUserPreferenceArray ? new ContextualPreference(
					userID, prefs.getItemID(i), prefs.getValue(i),
					((ContextualUserPreferenceArray) prefs)
							.getContextualPreferences(i))
					: new GenericPreference(userID, prefs.getItemID(i),
							prefs.getValue(i));

			if (this.idrescorer != null
					&& this.idrescorer.isFiltered(newPref.getItemID())) { // adiciona
																			// ratings
																			// de
																			// source
																			// domain
																			// sempre
																			// em
																			// training
																			// set
				if (oneUserTrainingPrefs == null) {
					oneUserTrainingPrefs = Lists.newArrayListWithCapacity(3);
				}
				oneUserTrainingPrefs.add(newPref);
				totalOfTrainingRatingsFromSource++;
				continue;
			}

			if (this.contextualCriteria != null
					&& isInstanceOfContextualUserPreferenceArray) { // adiciona
																	// ratings
																	// de outro
																	// contexto
																	// sempre em
																	// training
																	// set
				ContextualPreference contextualPref = (ContextualPreference) newPref;
				if (!this.contextualCriteria
						.containsAllContextualAttributes(contextualPref
								.getContextualPreferences())) {
					if (oneUserTrainingPrefs == null) {
						oneUserTrainingPrefs = Lists
								.newArrayListWithCapacity(3);
					}
					oneUserTrainingPrefs.add(newPref);
					totalOfTrainingRatingsFromTargetWithoutContext++;
					continue;
				}
			}

			// para ratings do target e do contexto, fazer proporcao definida
			if (random.nextDouble() < trainingPercentage) {
				if (oneUserTrainingPrefs == null) {
					oneUserTrainingPrefs = Lists.newArrayListWithCapacity(3);
				}
				oneUserTrainingPrefs.add(newPref);
				totalOfTrainingRatingsFromTargetWithContext++;
			} else {
				if (oneUserTestPrefs == null) {
					oneUserTestPrefs = Lists.newArrayListWithCapacity(3);
				}
				oneUserTestPrefs.add(newPref);
				totalOfTestRatings++;
			}

			// OLD training/test set
			/*
			 * if (random.nextDouble() < trainingPercentage) { if
			 * (oneUserTrainingPrefs == null) { oneUserTrainingPrefs =
			 * Lists.newArrayListWithCapacity(3); }
			 * oneUserTrainingPrefs.add(newPref); totalOfTrainingRatings++; }
			 * else { if (oneUserTestPrefs == null) { oneUserTestPrefs =
			 * Lists.newArrayListWithCapacity(3); } //testa somente com um tipo
			 * de rating (rescorer e/ou context) if(this.idrescorer == null &&
			 * this.contextualCriteria == null){ oneUserTestPrefs.add(newPref);
			 * totalOfTestRatings++; }else{ if(this.idrescorer != null &&
			 * !this.idrescorer.isFiltered(newPref.getItemID())){
			 * if(this.contextualCriteria != null &&
			 * isInstanceOfContextualUserPreferenceArray){ ContextualPreference
			 * contextualPref = (ContextualPreference) newPref;
			 * if(this.contextualCriteria
			 * .containsAllContextualAttributes(contextualPref
			 * .getContextualPreferences())){ oneUserTestPrefs.add(newPref);
			 * totalOfTestRatings++; } }else{ oneUserTestPrefs.add(newPref);
			 * totalOfTestRatings++; } }else if(this.idrescorer == null &&
			 * this.contextualCriteria != null &&
			 * isInstanceOfContextualUserPreferenceArray){ ContextualPreference
			 * contextualPref = (ContextualPreference) newPref;
			 * if(this.contextualCriteria
			 * .containsAllContextualAttributes(contextualPref
			 * .getContextualPreferences())){ oneUserTestPrefs.add(newPref);
			 * totalOfTestRatings++; } } } }
			 */
		}
		if (oneUserTrainingPrefs != null) {
			trainingPrefs
					.put(userID,
							isInstanceOfContextualUserPreferenceArray ? new ContextualUserPreferenceArray(
									oneUserTrainingPrefs)
									: new GenericUserPreferenceArray(
											oneUserTrainingPrefs));
			if (oneUserTestPrefs != null) {
				testPrefs
						.put(userID,
								isInstanceOfContextualUserPreferenceArray ? new ContextualUserPreferenceArray(
										oneUserTestPrefs)
										: new GenericUserPreferenceArray(
												oneUserTestPrefs));
			}
		}
	}

	private float capEstimatedPreference(float estimate) {
		if (estimate > maxPreference) {
			return maxPreference;
		}
		if (estimate < minPreference) {
			return minPreference;
		}
		return estimate;
	}

	private double getEvaluation(FastByIDMap<PreferenceArray> testPrefs,
			Recommender recommender) throws TasteException {
		reset();
		Collection<Callable<Void>> estimateCallables = Lists.newArrayList();
		AtomicInteger noEstimateCounter = new AtomicInteger();
		for (Map.Entry<Long, PreferenceArray> entry : testPrefs.entrySet()) {
			estimateCallables.add(new PreferenceEstimateCallable(recommender,
					entry.getKey(), entry.getValue(), noEstimateCounter));
		}
		log.info("Beginning evaluation of {} users", estimateCallables.size());
		RunningAverageAndStdDev timing = new FullRunningAverageAndStdDev();
		execute(estimateCallables, noEstimateCounter, timing);
		return computeFinalEvaluation();
	}

	/*
	 * protected static void execute(Collection<Callable<Void>> callables,
	 * AtomicInteger noEstimateCounter, RunningAverageAndStdDev timing) throws
	 * TasteException {
	 * 
	 * Collection<Callable<Void>> wrappedCallables =
	 * wrapWithStatsCallables(callables, noEstimateCounter, timing); int
	 * numProcessors = Runtime.getRuntime().availableProcessors();
	 * ExecutorService executor = Executors.newFixedThreadPool(numProcessors);
	 * log.info("Starting timing of {} tasks in {} threads",
	 * wrappedCallables.size(), numProcessors); try { List<Future<Void>> futures
	 * = executor.invokeAll(wrappedCallables); // Go look for exceptions here,
	 * really for (Future<Void> future : futures) { future.get(); }
	 * 
	 * } catch (InterruptedException ie) { throw new TasteException(ie); } catch
	 * (ExecutionException ee) { throw new TasteException(ee.getCause()); }
	 * 
	 * executor.shutdown(); try { executor.awaitTermination(10,
	 * TimeUnit.SECONDS); } catch (InterruptedException e) { throw new
	 * TasteException(e.getCause()); } }
	 */

	protected static void execute(Collection<Callable<Void>> callables,
			AtomicInteger noEstimateCounter, RunningAverageAndStdDev timing)
			throws TasteException {

		Collection<Callable<Void>> wrappedCallables = wrapWithStatsCallables(
				callables, noEstimateCounter, timing);
		int numProcessors = Runtime.getRuntime().availableProcessors();
	    ExecutorService executor = Executors.newFixedThreadPool(numProcessors);
		/*int numProcessors = 50; GAE
		ThreadFactory tf = ThreadManager.currentRequestThreadFactory();
		ExecutorService executor = Executors.newCachedThreadPool(tf);*/
		log.info("Starting timing of {} tasks in {} threads",
				wrappedCallables.size(), numProcessors);
		try {
			List<Future<Void>> futures = executor.invokeAll(wrappedCallables);
			// Go look for exceptions here, really
			for (Future<Void> future : futures) {
				future.get();
			}

		} catch (InterruptedException ie) {
			throw new TasteException(ie);
		} catch (ExecutionException ee) {
			throw new TasteException(ee.getCause());
		}

		executor.shutdown();
		try {
			executor.awaitTermination(10, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			throw new TasteException(e.getCause());
		}
	}

	private static Collection<Callable<Void>> wrapWithStatsCallables(
			Iterable<Callable<Void>> callables,
			AtomicInteger noEstimateCounter, RunningAverageAndStdDev timing) {
		Collection<Callable<Void>> wrapped = Lists.newArrayList();
		int count = 0;
		for (Callable<Void> callable : callables) {
			boolean logStats = count++ % 1000 == 0; // log every 1000 or so
													// iterations
			wrapped.add(new StatsCallable(callable, logStats, timing,
					noEstimateCounter));
		}
		return wrapped;
	}

	protected abstract void reset();

	protected abstract void processOneEstimate(float estimatedPreference,
			Preference realPref);

	protected abstract double computeFinalEvaluation();

	public final class PreferenceEstimateCallable implements Callable<Void> {

		private final Recommender recommender;
		private final long testUserID;
		private final PreferenceArray prefs;
		private final AtomicInteger noEstimateCounter;

		public PreferenceEstimateCallable(Recommender recommender,
				long testUserID, PreferenceArray prefs,
				AtomicInteger noEstimateCounter) {
			this.recommender = recommender;
			this.testUserID = testUserID;
			this.prefs = prefs;
			this.noEstimateCounter = noEstimateCounter;
		}

		@Override
		public Void call() throws TasteException {
			for (Preference realPref : prefs) {
				float estimatedPreference = Float.NaN;
				try {
					estimatedPreference = recommender.estimatePreference(
							testUserID, realPref.getItemID());
				} catch (NoSuchUserException nsue) {
					// It's possible that an item exists in the test data but
					// not training data in which case
					// NSEE will be thrown. Just ignore it and move on.
					log.info(
							"User exists in test data but not training data: {}",
							testUserID);
				} catch (NoSuchItemException nsie) {
					log.info(
							"Item exists in test data but not training data: {}",
							realPref.getItemID());
				}
				if (Float.isNaN(estimatedPreference)) {
					noEstimateCounter.incrementAndGet();
				} else {
					estimatedPreference = capEstimatedPreference(estimatedPreference);
					processOneEstimate(estimatedPreference, realPref);
				}
			}
			return null;
		}

	}

	public int getTotalOfTrainingRatingsFromSource() {
		return totalOfTrainingRatingsFromSource;
	}

	public int getTotalOfTrainingRatingsFromTargetWithoutContext() {
		return totalOfTrainingRatingsFromTargetWithoutContext;
	}

	public int getTotalOfTrainingRatingsFromTargetWithContext() {
		return totalOfTrainingRatingsFromTargetWithContext;
	}

	public int getTotalOfTestRatings() {
		return totalOfTestRatings;
	}

	public ContextualCriteria getContextualCriteria() {
		return contextualCriteria;
	}

}
