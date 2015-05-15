package br.cin.tbookmarks.recommender.database.contextual;

public enum PeriodOfDayContextualAttribute implements AbstractContextualAttribute{

	DAWN(0),MORNING(1),AFTERNOON(2),NIGHT(3);

	private long code;

	private PeriodOfDayContextualAttribute(long value) {
		this.code = value;
	}

	public long getCode() {
		return this.code;
	}

	public static PeriodOfDayContextualAttribute getInstanceByCode(long code){
		
		for(PeriodOfDayContextualAttribute d : PeriodOfDayContextualAttribute.values()){
			if(d.getCode() == code){
				return d;
			}
		}
		
		return null;
	}

}
