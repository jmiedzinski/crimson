package zbk.fun.crimson.utils;

public class LightsManager {

	private static LightsManager instance;
	
	private LightsManager() {
		
	}
	
	public static LightsManager instance() {
		
		if (instance == null) {
			instance = new LightsManager();
		}
		return instance;
	}
}
