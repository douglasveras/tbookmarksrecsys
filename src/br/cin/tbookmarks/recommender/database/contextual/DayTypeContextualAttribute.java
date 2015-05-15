package br.cin.tbookmarks.recommender.database.contextual;

public enum DayTypeContextualAttribute implements AbstractContextualAttribute{

	WEEKDAY(0),WEEKEND(1);

	private long code;

	private DayTypeContextualAttribute(long value) {
		this.code = value;
	}

	public long getCode() {
		return this.code;
	}
	
	public static DayTypeContextualAttribute getInstanceByCode(long code){
		
		for(DayTypeContextualAttribute d : DayTypeContextualAttribute.values()){
			if(d.getCode() == code){
				return d;
			}
		}
		
		return null;
	}
}
