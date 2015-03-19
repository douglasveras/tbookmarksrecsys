package br.cin.tbookmarks.recommender.database;

import java.util.Set;


public class ItemInformation {
	private long id;
	private String name;
	private String yearReleased;
	private String link;
	private Set<ItemCategory> categories;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getYearReleased() {
		return yearReleased;
	}
	public void setYearReleased(String yearReleased) {
		this.yearReleased = yearReleased;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	
	public void setCategories(Set<ItemCategory> categories) {
		this.categories = categories;
	}
	
	public Set<ItemCategory> getCategories() {
		return categories;
	}
	

}
