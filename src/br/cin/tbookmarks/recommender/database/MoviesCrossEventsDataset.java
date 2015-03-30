package br.cin.tbookmarks.recommender.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;

import com.google.gwt.dev.util.collect.HashSet;

public final class MoviesCrossEventsDataset extends AbstractDataset {

	private static final MoviesCrossEventsDataset INSTANCE = new MoviesCrossEventsDataset();

	/*
	 * public static String datasetURL =
	 * "\\resources\\datasets\\groupLens\\100K\\ua.base"; public static String
	 * datasetInformationURL = "\\resources\\datasets\\groupLens\\100K\\u.item";
	 * public static String datasetInformationDelimiter = "\\|";
	 */

	private String datasetURL = "\\resources\\datasets\\cross-domain\\movies_cross_events_test_user_without_events_rating.dat";

	private MoviesCrossEventsDataset() {
		try {
			initializeDataModel();
			initializeDBInfo();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void initializeDBInfo() {
		this.itemDatasetInformation = new ItemDatasetInformation();
		this.itemDatasetInformation.getItens().addAll(GroupLensDataset.getInstance().getItemDatasetInformation().getItens());
		this.itemDatasetInformation.getItens().addAll(EventsTwitterDataset.getInstance().getItemDatasetInformation().getItens());
	}

	public static MoviesCrossEventsDataset getInstance() {
		return INSTANCE;
	}


	private void initializeDataModel() throws IOException {
		model = new FileDataModel(new File(System.getProperty("user.dir")
				+ datasetURL));
	}

	

}
