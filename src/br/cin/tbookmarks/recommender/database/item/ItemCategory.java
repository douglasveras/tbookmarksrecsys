package br.cin.tbookmarks.recommender.database.item;

public enum ItemCategory {

	UNKNOWN(0), ACTION_ADVENTURE(1), INTERNATIONAL(2), ANIMATION(3), ANIME(4), BOXED_SETS(5),
	CLASSICS(6), COMEDY(7), DOCUMENTARY(8), DRAMA(9), EDUCATIONAL(10), HEALTH(11)
	, RELIGION(12), FANTASY(13), LGBT(14), HOLIDAY_SEASONAL(15), HORROR(16), ARTISTICAL(17), KIDS_FAMILY(18)
	, WAR(19), MUSICALS(20), MYSTERY(21), ROMANCE(22), SCI_FI(23), SPECIAL(24), SPORTS(25), WESTERNS(26),
	
	
	
	;

	private int code;
	//private ItemDomain usualDomain;

	private ItemCategory(int value) {
		this.code = value;
		/*if(value >=1 && value <= 26){
			this.usualDomain = ItemDomain.MOVIE;
		}*/
		
	}

	/*public int getCode() {
		return this.code;
	}*/
	
	/*public ItemDomain getUsualDomain() {
		return this.usualDomain;
	}
*/
	
	public static ItemCategory getCategoryEnum(String name) {
		
		try{
			return valueOf(name.toUpperCase());
		}catch(Exception e){
		
			if (name.toLowerCase().contains("action") || name.toLowerCase().contains("adventure")) {
				return ACTION_ADVENTURE;
			} else if (name.toLowerCase().contains("african") || name.toLowerCase().contains("hong kong")) {
				return INTERNATIONAL;
			} else if (name.equalsIgnoreCase("Anime & Manga")) {
				return ANIME;
			} else if (name.equalsIgnoreCase("Boxed Sets") || name.equalsIgnoreCase("TV Series")) {
				return BOXED_SETS;
			} else if (name.toLowerCase().equalsIgnoreCase("biograph")) {
				return DOCUMENTARY;
			} else if (name.equalsIgnoreCase("Business & Inveting")) {
				return EDUCATIONAL;
			}else if (name.equalsIgnoreCase("Classic Comedies")) {
				return COMEDY;
			} else if (name.equalsIgnoreCase("Exercise & Fitness")|| name.equalsIgnoreCase("Fitness")) {
				return HEALTH;
			} else if (name.equalsIgnoreCase("Faith & Spirituality")) {
				return RELIGION;
			} else if (name.equalsIgnoreCase("Foreign Language & International") || name.equalsIgnoreCase("British")  || name.equalsIgnoreCase("United Kingdom")) {
				return INTERNATIONAL;
			} else if (name.equalsIgnoreCase("Gay & Lesbian")) {
				return LGBT;
			} else if (name.equalsIgnoreCase("Holiday & Seasonal")) {
				return HOLIDAY_SEASONAL;
			} else if (name.equalsIgnoreCase("Indie & Art House") || name.equalsIgnoreCase("Art House & International")) {
				return ARTISTICAL;
			} else if (name.toLowerCase().contains("intructional")) {
				return EDUCATIONAL;
			} else if (name.toLowerCase().contains("family") || name.toLowerCase().contains("kids") || name.toLowerCase().contains("parenting") || name.toLowerCase().contains("child")) {
				return KIDS_FAMILY;
			} else if (name.toLowerCase().contains("romance")) {
				return ROMANCE;
			}else if (name.equalsIgnoreCase("Romantic Comedies")) {
				return COMEDY;
			}else if (name.equalsIgnoreCase("Military & War")) {
				return WAR;
			} else if (name.toLowerCase().contains("music")) {
				return MUSICALS;
			} else if (name.toLowerCase().contains("horror")) {
				return HORROR;
			} else if (name.toLowerCase().contains("mystery") || name.toLowerCase().contains("suspense") || name.toLowerCase().contains("thriller")) {
				return MYSTERY;
			} else if (name.equalsIgnoreCase("Science Fiction")) {
				return SCI_FI;
			} else if (name.equalsIgnoreCase("Special Interests")) {
				return SPECIAL;
			} else if (name.equalsIgnoreCase("Martial Arts") || name.equalsIgnoreCase("Wrestling") || name.equalsIgnoreCase("World Wrestling Entertainment (WWE)")) {
				return SPORTS;
			}
			//Books
			
			else if (name.equalsIgnoreCase("Arts & Photography")) {
				return ARTISTICAL;
			} else if (name.toLowerCase().contains("biograph") || name.equalsIgnoreCase("Nonfiction") || name.equalsIgnoreCase("Cult Movies")) {
				return DOCUMENTARY;
			} else if (name.equalsIgnoreCase("Business & Investing")) {
				return EDUCATIONAL;
			} else if (name.equalsIgnoreCase("Children's Books")) {
				return KIDS_FAMILY;
			}else if (name.equalsIgnoreCase("Christian Books & Bibles")) {
				return RELIGION;
			}else if (name.equalsIgnoreCase("Comics & Graphic Novels")) {
				return ANIME;
			} else if (name.equalsIgnoreCase("Cookbooks, Food & Wine") || name.equalsIgnoreCase("Cooking, Food & Wine")) {
				return EDUCATIONAL;
			} else if (name.equalsIgnoreCase("Crafts, Hobbies & Home")) {
				return DOCUMENTARY;
			}else if (name.equalsIgnoreCase("Computers & Technology") || name.equalsIgnoreCase("Computers & Internet")) {
				return EDUCATIONAL;
			}else if (name.equalsIgnoreCase("Education & Reference") || name.equalsIgnoreCase("Engineering")) {
				return EDUCATIONAL;
			}else if (name.equalsIgnoreCase("Gay & Lesbian")) {
				return LGBT;
			}else if (name.toLowerCase().contains("health") || name.toLowerCase().contains("fitness")) {
				return HEALTH;
			}else if (name.equalsIgnoreCase("History")) {
				return DOCUMENTARY;
			}else if (name.equalsIgnoreCase("Home & Garden")) {
				return EDUCATIONAL;
			}else if (name.equalsIgnoreCase("Humor & Entertainment")) {
				return COMEDY;
			}else if (name.equalsIgnoreCase("Law")) {
				return EDUCATIONAL;
			}else if (name.equalsIgnoreCase("Libros en Español")) {
				return INTERNATIONAL;
			}else if (name.equalsIgnoreCase("Literature & Fiction")) {
				return FANTASY;
			}else if (name.equalsIgnoreCase("Money & Markets")) {
				return EDUCATIONAL;
			}else if (name.equalsIgnoreCase("Medicine")) {
				return EDUCATIONAL;
			}else if (name.equalsIgnoreCase("Parenting & Relationships") || name.equalsIgnoreCase("Parenting & Families")) {
				return KIDS_FAMILY;
			}else if (name.equalsIgnoreCase("Outdoors & Nature") ) {
				return DOCUMENTARY;
			}else if (name.equalsIgnoreCase("Politics & Social Sciences")) {
				return EDUCATIONAL;
			}else if (name.equalsIgnoreCase("Professional & Technical")) {
				return EDUCATIONAL;
			}else if (name.equalsIgnoreCase("Religion & Spirituality")) {
				return RELIGION;
			}else if (name.equalsIgnoreCase("Reference")) {
				return EDUCATIONAL;
			}else if (name.equalsIgnoreCase("Science & Math") || name.equalsIgnoreCase("Science")) {
				return EDUCATIONAL;
			}else if (name.equalsIgnoreCase("Science Fiction & Fantasy")) {
				return SCI_FI;
			}else if (name.equalsIgnoreCase("Sports & Outdoors")) {
				return SPORTS;
			}else if (name.equalsIgnoreCase("Teens")) {
				return KIDS_FAMILY;
			}else if (name.equalsIgnoreCase("Travel")) {
				return INTERNATIONAL;
			}else{
				//System.out.println(name + " category unknown");
				return UNKNOWN;
			}
		}
	}
	
	/*public static ItemCategory getBookCategoryEnum(String name) {
		
		try{
			return valueOf(name.toUpperCase());
		}catch(Exception e){
			if (name.equalsIgnoreCase("Arts & Photography")) {
				return ARTISTICAL;
			} else if (name.toLowerCase().contains("biograph") || name.equalsIgnoreCase("Nonfiction")) {
				return DOCUMENTARY;
			} else if (name.equalsIgnoreCase("Business & Investing")) {
				return EDUCATIONAL;
			} else if (name.equalsIgnoreCase("Children's Books")) {
				return KIDS_FAMILY;
			}else if (name.equalsIgnoreCase("Christian Books & Bibles")) {
				return RELIGION;
			}else if (name.equalsIgnoreCase("Comics & Graphic Novels")) {
				return ANIME;
			} else if (name.equalsIgnoreCase("Cookbooks, Food & Wine") || name.equalsIgnoreCase("Cooking, Food & Wine")) {
				return EDUCATIONAL;
			} else if (name.equalsIgnoreCase("Crafts, Hobbies & Home")) {
				return DOCUMENTARY;
			}else if (name.equalsIgnoreCase("Computers & Technology") || name.equalsIgnoreCase("Computers & Internet")) {
				return EDUCATIONAL;
			}else if (name.equalsIgnoreCase("Education & Reference") || name.equalsIgnoreCase("Engineering")) {
				return EDUCATIONAL;
			}else if (name.equalsIgnoreCase("Gay & Lesbian")) {
				return LGBT;
			}else if (name.equalsIgnoreCase("Health, Fitness & Dieting") || name.equalsIgnoreCase("Health, Mind & Body")) {
				return HEALTH;
			}else if (name.equalsIgnoreCase("History")) {
				return DOCUMENTARY;
			}else if (name.equalsIgnoreCase("Home & Garden")) {
				return EDUCATIONAL;
			}else if (name.equalsIgnoreCase("Humor & Entertainment")) {
				return COMEDY;
			}else if (name.equalsIgnoreCase("Law")) {
				return EDUCATIONAL;
			}else if (name.equalsIgnoreCase("Libros en Español")) {
				return INTERNATIONAL;
			}else if (name.equalsIgnoreCase("Literature & Fiction")) {
				return FANTASY;
			}else if (name.equalsIgnoreCase("Money & Markets")) {
				return EDUCATIONAL;
			}else if (name.equalsIgnoreCase("Medicine")) {
				return EDUCATIONAL;
			}else if (name.equalsIgnoreCase("Mystery, Thriller & Suspense") || name.equalsIgnoreCase("Mystery & Thrillers")) {
				return MYSTERY;
			}else if (name.equalsIgnoreCase("Parenting & Relationships") || name.equalsIgnoreCase("Parenting & Families")) {
				return KIDS_FAMILY;
			}else if (name.equalsIgnoreCase("Outdoors & Nature") ) {
				return DOCUMENTARY;
			}else if (name.equalsIgnoreCase("Politics & Social Sciences")) {
				return EDUCATIONAL;
			}else if (name.equalsIgnoreCase("Professional & Technical")) {
				return EDUCATIONAL;
			}else if (name.equalsIgnoreCase("Religion & Spirituality")) {
				return RELIGION;
			}else if (name.equalsIgnoreCase("Reference")) {
				return EDUCATIONAL;
			}else if (name.equalsIgnoreCase("Science & Math") || name.equalsIgnoreCase("Science")) {
				return EDUCATIONAL;
			}else if (name.equalsIgnoreCase("Science Fiction & Fantasy")) {
				return SCI_FI;
			}else if (name.equalsIgnoreCase("Sports & Outdoors")) {
				return SPORTS;
			}else if (name.equalsIgnoreCase("Teens")) {
				return KIDS_FAMILY;
			}else if (name.equalsIgnoreCase("Travel")) {
				return INTERNATIONAL;
			}else{
				System.out.println(name + " Book category unknown");
				return UNKNOWN;
			}
		}
		
		
	}*/
}
