package zbk.fun.crimson.enums;

import com.badlogic.gdx.graphics.Color;

public enum LightSourceType {

	LIGHTSTICK(60f, 300f, 128, Color.GREEN, LightType.POINT),
	CAMPFIRE(120f, 400f, 128, Color.ORANGE, LightType.POINT),
	LANTERN(300f, 300f, 128, Color.WHITE, LightType.POINT);
	
	private float ttl;
	
	private float radius;
	
	private int rays;
	
	private Color color;
	
	private LightType type;
	
	LightSourceType(float ttl, float radius, int rays, Color color, LightType type) {
		this.ttl = ttl;
		this.radius = radius;
		this.rays = rays;
		this.color = color;
		this.type = type;
	}

	public float getTtl() {
		return ttl;
	}

	public float getRadius() {
		return radius;
	}

	public LightType getType() {
		return type;
	}

	public int getRays() {
		return rays;
	}

	public Color getColor() {
		return color;
	}
	
}
