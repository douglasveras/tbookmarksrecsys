package br.cin.tbookmarks.recommender.database.contextual;

public class ContextualCriteria {
	private DayTypeContextualAttribute dayTypeContextualAttribute;
	private PeriodOfDayContextualAttribute periodOfDayContextualAttribute;
	
	public ContextualCriteria() {
	}
	
	public ContextualCriteria(
			DayTypeContextualAttribute dayTypeContextualAttribute,
			PeriodOfDayContextualAttribute periodOfDayContextualAttribute) {
		this.dayTypeContextualAttribute = dayTypeContextualAttribute;
		this.periodOfDayContextualAttribute = periodOfDayContextualAttribute;
	}

	private DayTypeContextualAttribute getDayTypeContextualAttribute() {
		return dayTypeContextualAttribute;
	}

	public void setDayTypeContextualAttribute(
			DayTypeContextualAttribute dayTypeContextualAttribute) {
		this.dayTypeContextualAttribute = dayTypeContextualAttribute;
	}

	private PeriodOfDayContextualAttribute getPeriodOfDayContextualAttribute() {
		return periodOfDayContextualAttribute;
	}

	public void setPeriodOfDayContextualAttribute(
			PeriodOfDayContextualAttribute periodOfDayContextualAttribute) {
		this.periodOfDayContextualAttribute = periodOfDayContextualAttribute;
	}
	
	private int getCodeByIndex(int i){
		Class<? extends AbstractContextualAttribute> contextualAttributeClass = ContextualFileAttributeSequence.getInstance().get(i);
		if(this.getDayTypeContextualAttribute() != null && contextualAttributeClass.equals(DayTypeContextualAttribute.class)){
			return this.getDayTypeContextualAttribute().getCode();
		}else if(this.getPeriodOfDayContextualAttribute() != null && contextualAttributeClass.equals(PeriodOfDayContextualAttribute.class)){
			return this.getPeriodOfDayContextualAttribute().getCode();
		}
		return -1;
	}
	
	public boolean containsAllContextualAttributes(long[] contextualPreferences){
		if(contextualPreferences.length > 0){
			for(int i = 0; i < contextualPreferences.length; i++){
				
				if(this.getCodeByIndex(i) != -1 && contextualPreferences[i] != this.getCodeByIndex(i)){
					return false;
				}
				
			}
			return true;
		}else{
			return false;
		}
	}
	
}
