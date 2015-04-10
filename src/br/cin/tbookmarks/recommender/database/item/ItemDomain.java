package br.cin.tbookmarks.recommender.database.item;

public enum ItemDomain {

	MOVIE(0),EVENT(1), BOOK(2);

	private int code;

	private ItemDomain(int value) {
		this.code = value;
	}

	public int getCode() {
		return this.code;
	}


}
