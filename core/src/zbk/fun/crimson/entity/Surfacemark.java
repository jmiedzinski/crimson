package zbk.fun.crimson.entity;

import zbk.fun.crimson.enums.SurfacemarkType;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Surfacemark implements Poolable {

	public Vector2 position;
	public float rotation;
	public float time;
	public float ttl;
	
	public float alfa;
	
	public Texture texture;
	public Sprite sprite;
	
	public boolean active;
	
	public Surfacemark() {

	}
	
	public void init(SurfacemarkType type, Vector2 position, float rotation) {
		
		this.position = position;
		this.rotation = rotation;
		
		this.ttl = type.getTtl();
		this.time = ttl;
		this.texture = new Texture(Gdx.files.internal("assets/" + type.name().toLowerCase() + ".png"));
		this.alfa = 1.0f;
		this.active = true;
		this.sprite = new Sprite(texture);
		sprite.setCenter(position.x, position.y);
		sprite.setOriginCenter();
		sprite.setRotation(rotation);
	}
	
	public void update(float deltaTime) {
		
		time -= deltaTime;
		if (time > 0f) {
			alfa = (time * 1f) / ttl;
		} else {
			active = false;
		}
	}
	
	public void render(SpriteBatch batch, float deltaTime) {
		update(deltaTime);
		sprite.draw(batch, alfa);
	}

	@Override
	public void reset() {

		active = false;
		alfa = 1.0f;
		texture.dispose();
		
	}
}
