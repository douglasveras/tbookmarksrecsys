package br.cin.tbookmarks.recommender.database.contextual;

public enum PeriodOfDayContextualAttribute implements AbstractContextualAttribute{

	MORNING(1),AFTERNOON(2),NIGHT(3), DAWN(0);

	private int code;

	private PeriodOfDayContextualAttribute(int value) {
		this.code = value;
	}

	public int getCode() {
		return this.code;
	}


}
