package br.cin.tbookmarks.recommender.database.contextual;

public enum DayTypeContextualAttribute implements AbstractContextualAttribute{

	WEEKDAY(0),WEEKEND(1);

	private int code;

	private DayTypeContextualAttribute(int value) {
		this.code = value;
	}

	public int getCode() {
		return this.code;
	}


}
