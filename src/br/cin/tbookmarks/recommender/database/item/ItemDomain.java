package br.cin.tbookmarks.recommender.database.item;

public enum ItemDomain {

	MOVIE(0),EVENT(1), BOOK(2), MUSIC(3),TOY(4), VIDEO_GAME(5);

	private int code;

	private ItemDomain(int value) {
		this.code = value;
	}

	public int getCode() {
		return this.code;
	}


}
