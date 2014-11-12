package zbk.fun.crimson.enums;

import java.util.EnumSet;
import java.util.Set;

public enum SurfacemarkType {

	BLOODMARK(7f),
	EXPLOSIONMARK(15f);
	
	private float ttl;
	
	private static Set<SurfacemarkType> map = null;
	
    static {
        map = EnumSet.allOf(SurfacemarkType.class);
    }
    
    SurfacemarkType(float ttl) {
    	this.ttl = ttl;
    }

	public float getTtl() {
		return ttl;
	}
    
}
