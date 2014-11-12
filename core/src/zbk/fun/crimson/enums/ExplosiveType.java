package zbk.fun.crimson.enums;

import java.util.EnumSet;
import java.util.Set;

public enum ExplosiveType {

	LANDMINE(100f, 50f, -1f),
	C4(300f, 100f, 5f);
	
	private float damage;
	
	private float range;
	
	private float time;
	
	private static Set<ExplosiveType> map = null;
	
    static {
        map = EnumSet.allOf(ExplosiveType.class);
    }
    
    ExplosiveType(float damage, float range, float time) {
    	this.damage = damage;
    	this.range = range;
    	this.time = time;
    }

	public float getDamage() {
		return damage;
	}

	public float getRange() {
		return range;
	}

	public float getTime() {
		return time;
	}
    
}
