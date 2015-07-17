package br.cin.tbookmarks.recommender.database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.ArrayUtils;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;

import com.google.common.primitives.Longs;

import br.cin.tbookmarks.recommender.database.contextual.ContextualDataModel;
import br.cin.tbookmarks.recommender.database.contextual.ContextualFileDataModel;
import br.cin.tbookmarks.recommender.database.contextual.ContextualUserPreferenceArray;
import br.cin.tbookmarks.recommender.database.item.ItemCategory;
import br.cin.tbookmarks.recommender.database.item.ItemDatasetInformation;
import br.cin.tbookmarks.recommender.database.item.ItemDomain;
import br.cin.tbookmarks.recommender.database.item.ItemInformation;
import br.cin.tbookmarks.recommender.database.item.ItemInformationComparatorID;

public final class AmazonCrossDataset extends AbstractDataset {

	private static AmazonCrossDataset INSTANCE;
	private static boolean generateNewFiles;
	private static boolean isSingleDomain;
	private static ItemDomain singleDomain;

	private String datasetURLOriginal = "C:\\Users\\Douglas\\Desktop\\Cross_Domain_Tools\\datasets\\amazon\\amazon-meta.txt\\amazon-meta.txt.001";
	private String datasetInformationURL = "C:\\Users\\Douglas\\Desktop\\Cross_Domain_Tools\\datasets\\amazon\\amazon-meta.txt\\full-ratings-information";
	//private String datasetInformationURL = "WEB-INF/resources/datasets/full-ratings-information"; GAE
	private String datasetUserIDsMap = "C:\\Users\\Douglas\\Desktop\\Cross_Domain_Tools\\datasets\\amazon\\amazon-meta.txt\\userIDsMap.dat";
	private String datasetInformationDelimiter = ";";
	private String timestampFormat = "yyyy-MM-dd";

	{
		datasetURL = "C:\\Users\\Douglas\\Desktop\\Cross_Domain_Tools\\datasets\\amazon\\amazon-meta.txt\\contextual-ratings.dat";
	//	datasetURL = "WEB-INF/resources/datasets/contextual-ratings.dat"; GAE
	}

	@Override
	protected void initializeDataModel() throws IOException {
		model = new ContextualFileDataModel(new File(datasetURL));
	}

	/*
	 * private AmazonCrossDataset() { try {
	 * convertDatasetFileToDefaultPattern(null); initializeDataModel();
	 * initializeDBInfo(); } catch (IOException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } catch (NumberFormatException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); }
	 * 
	 * }
	 */

	private AmazonCrossDataset(HashSet<ItemDomain> domains,
			int minRatingsPerUser, boolean onlyOverlap) {
		try {
			convertDatasetFileToDefaultPattern(domains, minRatingsPerUser,
					onlyOverlap);
			
			initializeDataModel();
			initializeDBInfo();
			if(isSingleDomain && singleDomain != null){
				this.model = filterDataModelSingleDomain(this.model);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TasteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static AmazonCrossDataset getInstance() {
		if (INSTANCE == null) {
			generateNewFiles = false;
			return new AmazonCrossDataset(null, 0, false);
		}
		return INSTANCE;
	}
	
	public static AmazonCrossDataset getInstance(boolean getSingleDomain, ItemDomain domain) {
		if (INSTANCE == null) {
			generateNewFiles = false;
			isSingleDomain = getSingleDomain;
			singleDomain = domain;
			return new AmazonCrossDataset(null, 0, false);
		}
		return INSTANCE;
	}

	public static AmazonCrossDataset getInstance(HashSet<ItemDomain> domains,
			int minRatingsPerUser, boolean onlyOverlap) {
		if (INSTANCE == null) {
			generateNewFiles = true;
			return new AmazonCrossDataset(domains, minRatingsPerUser,
					onlyOverlap);
		}
		return INSTANCE;
	}

	private long getDifference(String text) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(this.timestampFormat);
		Date d = sdf.parse(text);

		return d.getTime();
	}

	private class EntryDBItemInformation {
		private String itemId;
		private String title;
		private String categories;
		private String domain;

		public EntryDBItemInformation(String itemId, String title,
				String categories, String domain) {

			this.itemId = itemId;
			this.title = title;
			this.categories = categories;
			this.domain = domain;
		}

		public String getItemId() {
			return itemId;
		}

		public String getTitle() {
			return title;
		}

		public String getCategories() {
			return categories;
		}

		public String getDomain() {
			return domain;
		}

		@Override
		public boolean equals(Object o) {
			EntryDBItemInformation entry;
			if (o instanceof EntryDBItemInformation) {
				entry = (EntryDBItemInformation) o;
				return this.itemId.equals(entry.itemId);
			}
			return false;
		}

	}

	private class EntryRatingContextualFile {

		private long userText;
		private String itemId;
		private String rating;
		private long dayType;
		private long periodOfDay;
		private String domain;

		public EntryRatingContextualFile(long userText, String itemId,
				String rating, long dayType, long periodOfDay, String domain) {

			this.userText = userText;
			this.itemId = itemId;
			this.rating = rating;
			this.dayType = dayType;
			this.periodOfDay = periodOfDay;
			this.domain = domain;
		}

		public long getUserText() {
			return userText;
		}

		public String getItemId() {
			return itemId;
		}

		public String getRating() {
			return rating;
		}

		public long getDayType() {
			return dayType;
		}

		public long getPeriodOfDay() {
			return periodOfDay;
		}

		public String getDomain() {
			return domain;
		}

	}

	private class EntryRatingContextualFileComparator implements
			Comparator<EntryRatingContextualFile> {
		@Override
		public int compare(EntryRatingContextualFile p1,
				EntryRatingContextualFile p2) {
			if (p1.getUserText() < p2.getUserText()) {
				return -1;
			} else if (p1.getUserText() > p2.getUserText()) {
				return 1;
			} else {
				return 0;
			}
		}
	}
	
	private DataModel filterDataModelSingleDomain(DataModel model) throws TasteException{
		
		//criar um novo datamodel verificando cada preferencia e adicionando no novo datamodel caso case com o domain
		
		FastByIDMap<PreferenceArray> preferences = new FastByIDMap<PreferenceArray>();
		LongPrimitiveIterator userIdsIterator = model.getUserIDs();
		
		
		while(userIdsIterator.hasNext()){
			
			Long userId = userIdsIterator.next();
			PreferenceArray prefsForUser = model.getPreferencesFromUser(userId);
			if(prefsForUser instanceof ContextualUserPreferenceArray){
				ContextualUserPreferenceArray contextualPrefsForUser = (ContextualUserPreferenceArray) prefsForUser;
				ArrayList<Long> newItemIds = new ArrayList<Long>();
				ArrayList<Float> newPrefValues = new ArrayList<Float>();
				ArrayList<List<Long>> newContextualPrefs = new ArrayList<List<Long>>();
				
				for(int i = 0; i < contextualPrefsForUser.getIDs().length; i++){
					
					if(singleDomain.equals(this.getRecommendedItemInformationByID(contextualPrefsForUser.get(i).getItemID()).getItemDomain())){
						newItemIds.add(contextualPrefsForUser.get(i).getItemID());
						newPrefValues.add(contextualPrefsForUser.get(i).getValue());
						Long[] longObjects = ArrayUtils.toObject(contextualPrefsForUser.get(i).getContextualPreferences());
						newContextualPrefs.add(Arrays.asList(longObjects));
					}
					
					
				}
				
				if(newItemIds.size() > 0 && newContextualPrefs.size() > 0){
					ContextualUserPreferenceArray newPrefsForUser = new ContextualUserPreferenceArray(newItemIds.size());
					newPrefsForUser.setUserID(0, userId);
					
					for(int n=0; n < newItemIds.size();n++){
						newPrefsForUser.setItemID(n, newItemIds.get(n));
						newPrefsForUser.setValue(n, newPrefValues.get(n));
						newPrefsForUser.setContextualPreferences(n, Longs.toArray(newContextualPrefs.get(n)));
						
					}
					
					preferences.put(userId, newPrefsForUser);
				}
			}else{
				throw new TasteException("prefs not ContextualUserPreferenceArray in AmazonCrossDataset for SingleDomain");
			}
			
		}
		//System.out.println(counter);
		DataModel filteredDataModel = new ContextualDataModel(preferences);
		
		return filteredDataModel;
	}

	private void convertDatasetFileToDefaultPattern(
			HashSet<ItemDomain> domains, int minRatingsPerUser,
			boolean onlyOverlap) {

		if (generateNewFiles) {/*
			
			File fileEN = new File(datasetURLOriginal);
			File fileOutput = new File(datasetURL);
			File fileOutputInfo = new File(datasetInformationURL);

			FileInputStream stream;
			
			Integer counter = 1;

			String line = "";
			try {
				stream = new FileInputStream(fileEN);

				InputStreamReader streamReader = new InputStreamReader(stream);
				BufferedReader reader = new BufferedReader(streamReader);

				FileOutputStream streamOutput = new FileOutputStream(fileOutput);

				OutputStreamWriter streamWriter = new OutputStreamWriter(
						streamOutput);

				BufferedWriter bw = new BufferedWriter(streamWriter);

				FileOutputStream streamOutputItemInfo = new FileOutputStream(
						fileOutputInfo);

				OutputStreamWriter streamWriterItemInfo = new OutputStreamWriter(
						streamOutputItemInfo);

				BufferedWriter bwInfo = new BufferedWriter(streamWriterItemInfo);

				HashMap<String, Integer> idMaps = new HashMap<String, Integer>();

				Integer userId = 1;

				HashSet<String> unknownCategories = new HashSet<String>();

				ArrayList<EntryRatingContextualFile> entriesRatingContextualFile = new ArrayList<AmazonCrossDataset.EntryRatingContextualFile>();
				ArrayList<EntryDBItemInformation> entriesDBItemInformation = new ArrayList<AmazonCrossDataset.EntryDBItemInformation>();

				line = reader.readLine();

				boolean hasNextFile = true;
				do{
					
					while (line != null) {
	
						line = line.replaceAll(";", " ");
							
						if (line.contains("Id:")) {
							String itemId = line.split(":")[1].trim();
	
							String title = "";
							String domain = "";
							String categories = "";
							String rating = "";
							String userText = "";
							String timestamp = "";
	
							HashSet<String> usersRatingThisItem = new HashSet<String>();
	
							while ((line = reader.readLine()) != null
									&& !line.contains("Id:")) {
	
								line = line.replaceAll(";", " ");
								if (line.contains("title:")) {
									String patternString = "title:(.*)";
	
									Pattern pattern = Pattern
											.compile(patternString);
	
									Matcher matcher = pattern.matcher(line);
									if (matcher.find()) {
										title = matcher.group(1).trim();
									}
								} else if (line.contains("group:")) {
									domain = line.split(":")[1].trim();
	
									if (domain.equalsIgnoreCase("DVD")
											|| domain.equalsIgnoreCase("Video")) {
										domain = ItemDomain.MOVIE.name();
									} else if (domain.equalsIgnoreCase("Musicals")) {
										domain = ItemDomain.MUSIC.name();
									} else if (domain
											.equalsIgnoreCase("Video Games")) {
										domain = ItemDomain.VIDEO_GAME.name();
									}else if (domain
											.equalsIgnoreCase("Baby Product")) {
										domain = ItemDomain.BABY_PRODUCT.name();
									}
	
									if (!domains.contains(ItemDomain.valueOf(domain
											.toUpperCase()))) {
										break;
									}
	
								} else if (line.contains("categories:")) {
									HashSet<ItemCategory> itemCategories = new HashSet<ItemCategory>();
									while (!(line = reader.readLine())
											.contains("reviews:")) {
										line = line.replaceAll(";", " ");
	
										String patternString = "Subjects\\[1000\\]\\|(.*?)\\|";
	
										Pattern pattern = Pattern
												.compile(patternString);
	
										Matcher matcher = pattern.matcher(line);
										if (matcher.find()) {
											String categoryText = matcher.group()
													.split("\\|")[1].trim().split(
													"\\[")[0];
											ItemCategory category = ItemCategory
													.getCategoryEnum(categoryText);
											if (!itemCategories.contains(category)) {
												itemCategories.add(category);
												if (!category
														.equals(ItemCategory.UNKNOWN)
														|| categories.equals("")) {
													categories = categories
															+ category + "|";
												}
											}
	
											if (category
													.equals(ItemCategory.UNKNOWN)
													&& !unknownCategories
															.contains(categoryText)) {
												unknownCategories.add(categoryText);
												System.out.println(categoryText
														+ " category unknown");
											}
										} else {
											patternString = "Styles\\[301668\\]\\|(.*?)\\|";
	
											pattern = Pattern
													.compile(patternString);
	
											matcher = pattern.matcher(line);
	
											if (matcher.find()) {
												// String categoryText =
												// matcher.group().split("\\|")[1].trim().split("\\[")[0];
												// ItemCategory category =
												// ItemCategory.getCategoryEnum(categoryText);
												// if(!category.equals(ItemCategory.UNKNOWN)
												// || categories.equals("")){
												categories = categories
														+ "Musicals" + "|";
												// }
												
												 * if(!unknownCategories.contains(
												 * categoryText)){
												 * unknownCategories.
												 * add(categoryText);
												 * System.out.println(categoryText +
												 * " category unknown"); }
												 
											} else {
												patternString = "Genres\\[.*\\]\\|(.*?)\\|";
	
												pattern = Pattern
														.compile(patternString);
	
												matcher = pattern.matcher(line);
	
												if (matcher.find()) {
													String categoryText = matcher
															.group().split("\\|")[1]
															.trim().split("\\[")[0];
													ItemCategory category = ItemCategory
															.getCategoryEnum(categoryText);
													if (!itemCategories
															.contains(category)) {
														itemCategories
																.add(category);
														if (!category
																.equals(ItemCategory.UNKNOWN)
																|| categories
																		.equals("")) {
															categories = categories
																	+ category
																	+ "|";
														}
													}
													if (category
															.equals(ItemCategory.UNKNOWN)
															&& !unknownCategories
																	.contains(categoryText)) {
														unknownCategories
																.add(categoryText);
														System.out
																.println(categoryText
																		+ " category unknown");
													}
												}
											}
	
										}
									}
									if (categories.length() == 0) {
										categories = "Unknown";
										// continue;
									} else {
										categories = categories.substring(0,
												categories.length() - 1);
									}
									while ((line = reader.readLine()) != null
											&& line.contains("rating:")) {
	
										line = line.replaceAll(";", " ");
	
										String patternString = "\\d\\d\\d\\d-\\d+-\\d+";
	
										Pattern pattern = Pattern
												.compile(patternString);
	
										Matcher matcher = pattern.matcher(line);
	
										if (matcher.find()) {
											timestamp = String
													.valueOf(this
															.getDifference(matcher
																	.group()));
										}
	
										long dayType = ContextualFileGenerator
												.getDayType(timestamp, 1);
										long periodOfDay = ContextualFileGenerator
												.getPeriodOfDay(timestamp, 1);
	
										patternString = "cutomer:(.*)rating";
	
										pattern = Pattern.compile(patternString);
	
										matcher = pattern.matcher(line);
	
										if (matcher.find()) {
											userText = matcher.group(1).trim();
										}
	
										if (!usersRatingThisItem.contains(userText)) {
											usersRatingThisItem.add(userText);
										} else {
											continue; // do not insert duplicated
														// rating
										}
	
										if (!idMaps.keySet().contains(userText)) {
											idMaps.put(userText, userId);
											userText = String.valueOf(userId);
											userId++;
										} else {
											userText = String.valueOf(idMaps
													.get(userText));
										}
	
										patternString = "rating:(.*)votes";
	
										pattern = Pattern.compile(patternString);
	
										matcher = pattern.matcher(line);
	
										if (matcher.find()) {
											rating = matcher.group(1).trim();
										}
	
										entriesRatingContextualFile
												.add(this.new EntryRatingContextualFile(
														new Long(userText), itemId,
														rating, dayType,
														periodOfDay, domain));
	
										
										 * bw.append(userText + "\t" + itemId + "\t"
										 * + rating + "\t" +
										 * dayType+"|"+periodOfDay); bw.newLine();
										 
	
									}
	
									entriesDBItemInformation
											.add(this.new EntryDBItemInformation(
													itemId, title, categories,
													domain));
	
									
									 * bwInfo.append(itemId +
									 * datasetInformationDelimiter + title +
									 * datasetInformationDelimiter + categories +
									 * datasetInformationDelimiter + domain);
									 * bwInfo.newLine();
									 
								}
							}
						} else {
							line = reader.readLine();
						}
	
					}
					
					
					counter++;
					fileEN = new File(datasetURLOriginal.substring(0,datasetURLOriginal.length()-1).concat(counter.toString()));
					
					if(fileEN.exists()){
						stream = new FileInputStream(fileEN);

						streamReader = new InputStreamReader(stream);
						reader = new BufferedReader(streamReader);
						
						line = reader.readLine();
					}else{
						hasNextFile = false;
					}
					
				}while(hasNextFile );
				
				Collections.sort(entriesRatingContextualFile,
						new EntryRatingContextualFileComparator());

				StringBuffer entriesRatingAux = new StringBuffer();
				long currentUserID = -1;
				int numUsersCounter = 0;

				if (entriesRatingContextualFile.size() > 0) {
					System.out.println("entries file "
							+ entriesRatingContextualFile.size());
					currentUserID = entriesRatingContextualFile.get(0)
							.getUserText();
					numUsersCounter = 1;
				}

				int ratingsCounter = 0;

				HashSet<ItemDomain> itemDomainAux = (HashSet<ItemDomain>) domains
						.clone();

				for (EntryRatingContextualFile entry : entriesRatingContextualFile) {

					if (entry.getUserText() == currentUserID) {
						entriesRatingAux.append(entry.getUserText() + "\t"
								+ entry.getItemId() + "\t" + entry.getRating()
								+ "\t" + entry.getDayType() + "|"
								+ entry.getPeriodOfDay() + "\n");
						ratingsCounter++;

						if (onlyOverlap && !itemDomainAux.isEmpty()) {
							itemDomainAux.remove(ItemDomain.valueOf(entry
									.getDomain().toUpperCase()));
						}

					} else {
						if (ratingsCounter >= minRatingsPerUser) {
							if (onlyOverlap) {
								if (itemDomainAux.isEmpty()) {
									bw.append(entriesRatingAux);
									numUsersCounter++;
								}
							} else {
								bw.append(entriesRatingAux);
								numUsersCounter++;
							}

						} else {
							// System.out.println(currentUserID);
						}
						entriesRatingAux = new StringBuffer();
						entriesRatingAux.append(entry.getUserText() + "\t"
								+ entry.getItemId() + "\t" + entry.getRating()
								+ "\t" + entry.getDayType() + "|"
								+ entry.getPeriodOfDay() + "\n");
						ratingsCounter = 1;
						itemDomainAux = (HashSet<ItemDomain>) domains.clone();

						if (onlyOverlap && !itemDomainAux.isEmpty()) {
							itemDomainAux.remove(ItemDomain.valueOf(entry
									.getDomain().toUpperCase()));
						}

						currentUserID = entry.getUserText();

					}

					// bw.newLine();
				}

				System.out.println("Number of users: " + numUsersCounter);

				if (ratingsCounter >= minRatingsPerUser) {// adiciona ultimo
															// rating (caso min
															// = 0 ou 1)
					if (onlyOverlap) {
						if (itemDomainAux.isEmpty()) {
							bw.append(entriesRatingAux);
							numUsersCounter++;
						}
					} else {
						bw.append(entriesRatingAux);
						numUsersCounter++;
					}
				} else {
					// System.out.println(currentUserID);
				}

				for (EntryDBItemInformation entry : entriesDBItemInformation) {
					bwInfo.append(entry.getItemId()
							+ datasetInformationDelimiter + entry.getTitle()
							+ datasetInformationDelimiter
							+ entry.getCategories()
							+ datasetInformationDelimiter + entry.getDomain());
					bwInfo.newLine();
				}

				generateUserIDMap(idMaps);

				reader.close();
				streamReader.close();
				bw.close();
				bwInfo.close();

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ArrayIndexOutOfBoundsException e) {
				System.err.println(line);
				e.printStackTrace();
			} catch (PatternSyntaxException e) {
				System.err.println(line);
				e.printStackTrace();
			} catch (ParseException e) {
				System.err.println(line);
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println(line);
				e.printStackTrace();
			}
		*/}

	}

	private void generateUserIDMap(HashMap<String, Integer> idMaps)
			throws FileNotFoundException, IOException {/*
		File fileOutputUserIDsMap = new File(datasetUserIDsMap);

		FileOutputStream streamOutputUserIDsMap = new FileOutputStream(
				fileOutputUserIDsMap);

		OutputStreamWriter streamWriterUserIDsMap = new OutputStreamWriter(
				streamOutputUserIDsMap);

		BufferedWriter bwUserIDMap = new BufferedWriter(streamWriterUserIDsMap);

		for (String key : idMaps.keySet()) {
			bwUserIDMap.append(idMaps.get(key) + ";" + key);
			bwUserIDMap.newLine();
		}

		bwUserIDMap.close();
	*/}

	private Set<ItemCategory> getCategoriesFromDB(String row) {
		HashSet<ItemCategory> returnedItemCateogories = new HashSet<ItemCategory>();
		String extractedItemCateogories[] = row.split("\\|");
		for (String itemCategoryText : extractedItemCateogories) {
			returnedItemCateogories.add(ItemCategory.valueOf(itemCategoryText
					.toUpperCase()));
		}
		return returnedItemCateogories;
	}

	protected void initializeDBInfo() throws NumberFormatException, IOException {

		itemDatasetInformation = new ItemDatasetInformation();

		int countItemInformation = 0;
		
		for(int splitedFilesNumber = 1; splitedFilesNumber <= 2; splitedFilesNumber++){
			File fileEN = new File(datasetInformationURL+splitedFilesNumber+".dat");

			FileInputStream stream;

			stream = new FileInputStream(fileEN);

			InputStreamReader streamReader = new InputStreamReader(stream);
			BufferedReader reader = new BufferedReader(streamReader);

			String line;


			ItemInformation itemInfo;

			line = reader.readLine();
			
			while (line != null) {
				itemInfo = new ItemInformation();

				String row[] = line.split(datasetInformationDelimiter);
				if (row.length != 4) {
					System.out.println("Line " + line
							+ " have more or less than four terms");
				}
				String itemId = row[0];

				itemInfo.setId(Long.parseLong(itemId));

				itemInfo.setName(row[1]);
				// String categories[] = row[index].split("\\|");

				itemInfo.setCategories(getCategoriesFromDB(row[2]));
				itemInfo.setItemDomain(ItemDomain.valueOf(row[3].toUpperCase()));

				// itemInfo.setYearReleased(row[index++]);
				// itemInfo.setLink(row[index++]);

				itemDatasetInformation.getItens().add(itemInfo);
				countItemInformation++;
				line = reader.readLine();
			}
			reader.close();
			streamReader.close();
		}
		
		
		if(countItemInformation > 0){
			Collections.sort(itemDatasetInformation.getItens(),
				new ItemInformationComparatorID());
									
		}

		System.out.println(countItemInformation);

		

	}

	/*
	 * private String removeSemiColonInField(String line) {
	 * 
	 * String regex = "\".*?\"";
	 * 
	 * Pattern pattern = Pattern.compile(regex);
	 * 
	 * Matcher matcher = pattern.matcher(line);
	 * 
	 * String replaced = line;
	 * 
	 * while (matcher.find()) { String auxReplace =
	 * matcher.group().replaceAll(",", ""); replaced =
	 * replaced.replace(matcher.group(), auxReplace); } return replaced; }
	 */

}
