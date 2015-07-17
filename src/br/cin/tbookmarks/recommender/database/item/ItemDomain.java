package br.cin.tbookmarks.recommender.database.item;

public enum ItemDomain {

	MOVIE(0),EVENT(1), BOOK(2), MUSIC(3),TOY(4), VIDEO_GAME(5), SOFTWARE(6), BABY_PRODUCT(7), CE(8), SPORTS(9);

	private int code;

	private ItemDomain(int value) {
		this.code = value;
	}

	public int getCode() {
		return this.code;
	}


}
