package zbk.fun.crimson.utils;

import box2dLight.RayHandler;

public class LightsManager {

	private static LightsManager instance;
	
	RayHandler rayHandler;
	
	pr
	
	private LightsManager() {
		
	}
	
	public static LightsManager instance() {
		
		if (instance == null) {
			instance = new LightsManager();
		}
		return instance;
	}
}
