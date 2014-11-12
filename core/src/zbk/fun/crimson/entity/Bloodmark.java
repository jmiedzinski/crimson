package zbk.fun.crimson.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Bloodmark {

	public Vector2 position;
	public float rotation;
	public float time;
	public float ttl;
	
	public float alfa;
	
	public Texture texture;
	public Sprite sprite;
	
	public boolean active;
	
	public Bloodmark(Vector2 position, float rotation) {
		this.position = position;
		this.rotation = rotation;
		
		this.ttl = MathUtils.random(5f, 10f);
		this.time = ttl;
		this.texture = new Texture(Gdx.files.internal("assets/bloodmark01.png"));
		this.alfa = 1.0f;
		this.active = true;
		this.sprite = new Sprite(texture);
		sprite.setCenter(position.x, position.y);
		sprite.setOriginCenter();
		sprite.setRotation(rotation);
	}
	
	public void update() {
		
		time -= Gdx.graphics.getDeltaTime();
		if (time > 0f) {
			alfa = (time * 1f) / ttl;
		} else {
			active = false;
		}
	}
	
	public void render(SpriteBatch batch) {
		update();
//		batch.draw(texture, position.x, position.y);
		sprite.draw(batch, alfa);
	}
}
