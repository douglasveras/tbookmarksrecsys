package br.cin.tbookmarks.recommender.database.contextual;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ContextualCriteria {
	private DayTypeContextualAttribute dayTypeContextualAttribute;
	private PeriodOfDayContextualAttribute periodOfDayContextualAttribute;
	
	public ContextualCriteria() {
	}
	
	public ContextualCriteria(long contexts[]) {
		
		ContextualFileAttributeSequence instance = ContextualFileAttributeSequence.getInstance();		
		this.dayTypeContextualAttribute = DayTypeContextualAttribute.getInstanceByCode(contexts[instance.get(DayTypeContextualAttribute.class)]);
		this.periodOfDayContextualAttribute = PeriodOfDayContextualAttribute.getInstanceByCode(contexts[instance.get(PeriodOfDayContextualAttribute.class)]);
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
	
	private long getCodeByIndex(int i){
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
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ContextualCriteria){
			ContextualCriteria test = (ContextualCriteria) obj;
			if(test.getDayTypeContextualAttribute().equals(this.getDayTypeContextualAttribute())
					&& test.getPeriodOfDayContextualAttribute().equals(this.getPeriodOfDayContextualAttribute())){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		
		long dayType = -1;
		
		if(this.getDayTypeContextualAttribute() != null){
			dayType = this.getDayTypeContextualAttribute().getCode();
		}
		
		long periodOfDay = -1;
		
		if(this.getPeriodOfDayContextualAttribute() != null){
			periodOfDay = this.getPeriodOfDayContextualAttribute().getCode();
		}
		
		return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
	            // if deriving: appendSuper(super.hashCode()).
	            append(dayType).
	            append(periodOfDay).
	            toHashCode();
	}
	
	@Override
	public String toString() {
		
		String dayType = this.getDayTypeContextualAttribute() != null ? this.getDayTypeContextualAttribute().name() : "null";
		String periodOfDay = this.getPeriodOfDayContextualAttribute() != null ? this.getPeriodOfDayContextualAttribute().name() : "null";
		
		return "DAY:"+dayType+", PERIOD:"+periodOfDay;
	}
}
