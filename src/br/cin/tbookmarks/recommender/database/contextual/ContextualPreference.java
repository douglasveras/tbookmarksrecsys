package br.cin.tbookmarks.recommender.database.contextual;

import java.io.Serializable;

import org.apache.mahout.cf.taste.model.Preference;

import com.google.common.base.Preconditions;

/**
 * <p>
 * A simple {@link Preference} encapsulating an item and preference value.
 * </p>
 */
public class ContextualPreference implements Preference, Serializable {
  
  private final long userID;
  private final long itemID;
  private float value;
  private long contextualPreferences[];
  
  public ContextualPreference(long userID, long itemID, float value, long contextualPreferences[]) {
    Preconditions.checkArgument(!Float.isNaN(value), "NaN value");
    this.userID = userID;
    this.itemID = itemID;
    this.value = value;
    this.contextualPreferences = contextualPreferences;
  }
  
  @Override
  public long getUserID() {
    return userID;
  }
  
  @Override
  public long getItemID() {
    return itemID;
  }
  
  @Override
  public float getValue() {
    return value;
  }
  
  @Override
  public void setValue(float value) {
    Preconditions.checkArgument(!Float.isNaN(value), "NaN value");
    this.value = value;
  }
  
  @Override
  public String toString() {
    return "ContextualPreference[userID: " + userID + ", itemID:" + itemID + ", value:" + value + ", contextualPreferences:" + contextualPreferences +']';
  }
  
  public long[] getContextualPreferences() {
	return contextualPreferences;
  }
  
  public void setContextualPreferences(long[] contextualPreferences) {
	this.contextualPreferences = contextualPreferences;
  }
 
}
