package zbk.fun.crimson.enums;

import java.util.EnumSet;
import java.util.Set;

public enum WeaponType {

	PISTOL(1, "assets/pistol.png", 10, 10f, 5f, 200f),
	MACHINE_GUN(2, "assets/machine_gun.png", 25, 15f, 7f, 300f),
	SHOTGUN(3, "assets/shotgun.png", 15, 30f, 6f, 150f);
	
	private int id;
	
	private String texture;
	
	private float damage;
	
	private int clipSize;
	
	private float speed;
	
	private float range;
	
	private static Set<WeaponType> map = null;
	
    static {
        map = EnumSet.allOf(WeaponType.class);
    }
    
    WeaponType(int id, String texture, int clipSize, float damage, float speed, float range) {
    	this.id = id;
    	this.texture = texture;
    	this.clipSize = clipSize;
    	this.damage = damage;
    	this.speed = speed;
    	this.range = range;
    }
    
    public int getId() {
		return id;
	}

	public String getTexture() {
		return texture;
	}

	public float getDamage() {
		return damage;
	}

	public int getClipSize() {
		return clipSize;
	}

	public float getSpeed() {
		return speed;
	}

	public float getRange() {
		return range;
	}
    
    
}
