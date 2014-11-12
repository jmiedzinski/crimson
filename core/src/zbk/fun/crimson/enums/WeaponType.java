package zbk.fun.crimson.enums;

import java.util.EnumSet;
import java.util.Set;

public enum WeaponType {

	PISTOL(10, 10f, 5f, 200f),
	MACHINE_GUN(25, 15f, 7f, 300f),
	SHOTGUN(15, 30f, 6f, 150f);
	
	private float damage;
	
	private int clipSize;
	
	private float speed;
	
	private float range;
	
	private static Set<WeaponType> map = null;
	
    static {
        map = EnumSet.allOf(WeaponType.class);
    }
    
    WeaponType(int clipSize, float damage, float speed, float range) {
    	this.clipSize = clipSize;
    	this.damage = damage;
    	this.speed = speed;
    	this.range = range;
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
