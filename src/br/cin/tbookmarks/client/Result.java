package br.cin.tbookmarks.client;

import java.util.ArrayList;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import br.cin.tbookmarks.recommender.evaluation.PredictionValues;

@PersistenceCapable
public class Result {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long resultID;

	@Persistent
	private int trial;

	@Persistent
	private String algorithmName;

	@Persistent
	private double maeValue = -1;

	@Persistent
	private double rmseValue = -1;

	@Persistent
	private long executionTime;

	@Persistent
	private Date date;

	@Persistent
	private String context;

	@Persistent
	private String sourceDomain;

	@Persistent
	private String targetDomain;

	@Persistent
	private int totalOfTrainingRatingsFromSource;

	@Persistent
	private int totalOfTrainingRatingsFromTargetWithoutContext;

	@Persistent
	private int totalOfTrainingRatingsFromTargetWithContext;

	@Persistent
	private int totalOfTestRatings;

	@Persistent
	private int numOfUsers;

	@Persistent
	private int numOfOverlappedUsers;

	@Persistent
	private int numOfItens;

	@Persistent
	private ArrayList<PredictionValues> predictionValues;

	public Result() {
	}

	public Long getResultID() {
		return resultID;
	}

	public void setResultID(Long resultID) {
		this.resultID = resultID;
	}

	public String getAlgorithmName() {
		return algorithmName;
	}

	public void setAlgorithmName(String algorithmName) {
		this.algorithmName = algorithmName;
	}

	public double getMaeValue() {
		return maeValue;
	}

	public void setMaeValue(double maeValue) {
		this.maeValue = maeValue;
	}

	public double getRmseValue() {
		return rmseValue;
	}

	public void setRmseValue(double rmseValue) {
		this.rmseValue = rmseValue;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public int getTotalOfTrainingRatingsFromSource() {
		return totalOfTrainingRatingsFromSource;
	}

	public void setTotalOfTrainingRatingsFromSource(
			int totalOfTrainingRatingsFromSource) {
		this.totalOfTrainingRatingsFromSource = totalOfTrainingRatingsFromSource;
	}

	public int getTotalOfTrainingRatingsFromTargetWithoutContext() {
		return totalOfTrainingRatingsFromTargetWithoutContext;
	}

	public void setTotalOfTrainingRatingsFromTargetWithoutContext(
			int totalOfTrainingRatingsFromTargetWithoutContext) {
		this.totalOfTrainingRatingsFromTargetWithoutContext = totalOfTrainingRatingsFromTargetWithoutContext;
	}

	public int getTotalOfTrainingRatingsFromTargetWithContext() {
		return totalOfTrainingRatingsFromTargetWithContext;
	}

	public void setTotalOfTrainingRatingsFromTargetWithContext(
			int totalOfTrainingRatingsFromTargetWithContext) {
		this.totalOfTrainingRatingsFromTargetWithContext = totalOfTrainingRatingsFromTargetWithContext;
	}

	public int getTotalOfTestRatings() {
		return totalOfTestRatings;
	}

	public void setTotalOfTestRatings(int totalOfTestRatings) {
		this.totalOfTestRatings = totalOfTestRatings;
	}

	public int getNumOfUsers() {
		return numOfUsers;
	}

	public void setNumOfUsers(int numOfUsers) {
		this.numOfUsers = numOfUsers;
	}

	public int getNumOfOverlappedUsers() {
		return numOfOverlappedUsers;
	}

	public void setNumOfOverlappedUsers(int numOfOverlappedUsers) {
		this.numOfOverlappedUsers = numOfOverlappedUsers;
	}

	public int getNumOfItens() {
		return numOfItens;
	}

	public void setNumOfItens(int numOfItens) {
		this.numOfItens = numOfItens;
	}

	public int getTrial() {
		return trial;
	}

	public void setTrial(int trial) {
		this.trial = trial;
	}

	public String getSourceDomain() {
		return sourceDomain;
	}

	public void setSourceDomain(String sourceDomain) {
		this.sourceDomain = sourceDomain;
	}

	public String getTargetDomain() {
		return targetDomain;
	}

	public void setTargetDomain(String targetDomain) {
		this.targetDomain = targetDomain;
	}

	public ArrayList<PredictionValues> getPredictionValues() {
		return predictionValues;
	}
	
	public void setPredictionValues(ArrayList<PredictionValues> predictionValues) {
		this.predictionValues = predictionValues;
	}

	@Override
	public String toString() {
		return resultID + "\t" + date + "\t" + trial + "\t" + context + "\t"
				+ algorithmName + "\t" + maeValue + "\t" + rmseValue + "\t"
				+ sourceDomain + "\t" + targetDomain + "\t" + executionTime
				+ "\t" + totalOfTrainingRatingsFromSource + "\t"
				+ totalOfTrainingRatingsFromTargetWithoutContext + "\t"
				+ totalOfTrainingRatingsFromTargetWithContext + "\t"
				+ totalOfTestRatings + "\t" + numOfUsers + "\t"
				+ numOfOverlappedUsers + "\t" + numOfItens;
	}
}
