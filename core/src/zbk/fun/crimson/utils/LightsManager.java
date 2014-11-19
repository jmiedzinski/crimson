package zbk.fun.crimson.utils;

import zbk.fun.crimson.entity.LightSource;
import zbk.fun.crimson.enums.LightSourceType;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

import box2dLight.RayHandler;

public class LightsManager {

	private static LightsManager instance;
	
	RayHandler rayHandler;
	
	private Pool<LightSource> lightsPool;
	private Array<LightSource> lights;
	private Array<LightSource> lightsToDelete;
	
	private LightsManager() {
		
		this.lightsPool = Pools.get(LightSource.class);
		this.lights = new Array<LightSource>();
		this.lightsToDelete = new Array<LightSource>();
	}
	
	public static LightsManager instance() {
		
		if (instance == null) {
			instance = new LightsManager();
		}
		return instance;
	}
	
	public LightSource newLight(LightSourceType type, Vector2 position) {
		
		LightSource l = lightsPool.obtain();
		l.init(rayHandler, type);
		l.setPosition(position.x, position.y);
		lights.add(l);
		
		return l;
	}
	
	public void clearLights() {
		
		for (LightSource light : lights) {
			if (light.ttl <= 0f) {
				lightsPool.free(light);
				lightsToDelete.add(light);
			}
		}
		lights.removeAll(lightsToDelete, true);
		lightsToDelete.clear();
	}
	
	public void render(float deltaTime) {
		
		for (LightSource light : lights) {
			light.ttl -= deltaTime;
		}
		clearLights();
		rayHandler.updateAndRender();
	}

	public Array<LightSource> getLights() {
		return lights;
	}

	public Array<LightSource> getLightsToDelete() {
		return lightsToDelete;
	}

	public RayHandler getRayHandler() {
		return rayHandler;
	}

	public void setRayHandler(RayHandler rayHandler) {
		this.rayHandler = rayHandler;
	}
	
	
}
