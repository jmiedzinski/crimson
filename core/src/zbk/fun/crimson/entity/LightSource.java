package zbk.fun.crimson.entity;

import zbk.fun.crimson.enums.LightSourceType;
import box2dLight.ConeLight;
import box2dLight.DirectionalLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Pool.Poolable;

public class LightSource implements Poolable {
	
	private RayHandler handler;
	
	private LightSourceType type;
	
	private Light light;
	
	public float ttl;
	
	public LightSource() {
		// TODO Auto-generated constructor stub
	}
	
	public void init(RayHandler handler, LightSourceType lightSourceType) {
		this.handler = handler;
		this.type = lightSourceType;
		this.ttl = lightSourceType.getTtl();
		
		switch (lightSourceType.getType()) {
		case POINT:
			this.light = new PointLight(handler, lightSourceType.getRays(), lightSourceType.getColor(), lightSourceType.getRadius(), 0f, 0f);
		case CONE:
			this.light = new ConeLight(handler, lightSourceType.getRays(), lightSourceType.getColor(), lightSourceType.getRadius(), 0f, 0f, 0f, 45f);
			break;
		case DIRECTIONAL:
			this.light = new DirectionalLight(handler, lightSourceType.getRays(), lightSourceType.getColor(), 0f);
			break;
		default:
			break;
		}
	}
	
	public void setPosition(float x, float y) {
		this.light.setPosition(x, y);
	}
	
	public Vector2 getPosition() {
		return this.light.getPosition();
	}
	
	public void setConeAngle(float angle) {
		
		((ConeLight)light).setConeDegree(angle);
	}
	
	public void setDirectionAngle(float angle) {
		
		light.setDirection(angle);
	}

	public LightSourceType getType() {
		return type;
	}

	public Light getLight() {
		return light;
	}
	
	public void attachToBody(Body body) {
		light.attachToBody(body);
	}

	@Override
	public void reset() {
		
		this.light.remove();
		this.light.dispose();

	}

}
