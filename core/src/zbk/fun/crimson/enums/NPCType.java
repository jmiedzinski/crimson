package zbk.fun.crimson.enums;

import java.util.EnumSet;
import java.util.Set;

public enum NPCType {

	ZOMBIE1(1, "assets/zombie1.png", 100f, 50f),
	ZOMBIE2(2, "assets/zombie2.png", 100f, 50f),
	ZOMBIE3(3, "assets/zombie3.png", 100f, 50f),
	ZOMBIE4(4, "assets/zombie4.png", 100f, 50f),
	ZOMBIE5(5, "assets/zombie5.png", 100f, 50f),
	ZOMBIE6(6, "assets/zombie6.png", 100f, 50f),
	ZOMBIE7(7, "assets/zombie7.png", 100f, 50f),
	ZOMBIE8(8, "assets/zombie8.png", 100f, 50f),
	ZOMBIE9(9, "assets/zombie9.png", 100f, 50f),
	ZOMBIE10(10, "assets/zombie10.png", 100f, 50f);
	
	private int id;
	
	private String texture;
	
	private float life;
	
	private float damage;
	
	private static Set<NPCType> map = null;
	
    static {
        map = EnumSet.allOf(NPCType.class);
    }
    
    private NPCType(int id, String texture, float life, float damage) {

    	this.id = id;
    	this.texture = texture;
    	this.life = life;
    	this.damage = damage;
	}
    
    public static NPCType getById(int id) {
    	
    	for (NPCType type : map) {
    		if (type.id == id)
    			return type;
    	}
    	return null;
    }

	public int getId() {
		return id;
	}

	public String getTexture() {
		return texture;
	}

	public float getLife() {
		return life;
	}

	public float getDamage() {
		return damage;
	}
    
}
