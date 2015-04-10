package br.cin.tbookmarks.recommender.database.item;

public enum ItemCategory {

	UNKNOWN(0), ACTION(1), ADVENTURE(2), ANIMATION(3), CHILDRENS(4), COMEDY(5), CRIME(
			6), DOCUMENTARY(7), DRAMA(8), FANTASY(9), FILMNOIR(10), HORROR(11), MUSICAL(
			12), MYSTERY(13), ROMANCE(14), SCIFI(15), THRILLER(16), WAR(17), WESTERN(
			18);

	private int code;

	private ItemCategory(int value) {
		this.code = value;
	}

	public int getCode() {
		return this.code;
	}

	public static ItemCategory convertToItem(String name) {
		if (name.equalsIgnoreCase("CHILDREN'S")) {
			return CHILDRENS;
		} else if (name.equalsIgnoreCase("FILM-NOIR")) {
			return FILMNOIR;
		} else if (name.equalsIgnoreCase("SCI-FI")) {
			return SCIFI;
		} else {
			return valueOf(name);
		}
	}
}
