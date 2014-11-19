package zbk.fun.crimson.entity;

import zbk.fun.crimson.utils.WorldUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Projectile implements Poolable {

	public Vector2 startPoint;
	public Vector2 position;
	public Vector2 direction;
	public Vector2 target;

	public float speed;
	public float damage;
	public float rotation;
	public float distance;
	public float time;
	public float maxDistance;

	public Sprite sprite;

	public boolean active;
	
	public Body body;
	
	public Projectile() {
		
	}

	public Projectile(Vector2 position, Vector2 target) {
		
		init(position, target);
	}
	
	public void init(Vector2 position, Vector2 target) {

		Texture t = new Texture(Gdx.files.internal("assets/bullet.png"));

		this.startPoint = position.cpy();
		this.position = position;
		this.target = target;
		this.direction = target.cpy().sub(position).nor();
		this.rotation = direction.angle();

		this.speed = 5f;
		this.damage = 25f;

		this.sprite = new Sprite(t);
		this.sprite.setCenter(position.x, position.y);
		this.sprite.setOriginCenter();
		this.sprite.setRotation(rotation);
		sprite.scale(-0.3f);
		maxDistance = 1000f;
		distance = 0f;

		this.active = true;
	}

	public void update(Array<Steerable<Vector2>> enemies, float deltaTime) {

		if (active) {
			time += deltaTime;

			position.x += speed * Math.cos(MathUtils.degreesToRadians * direction.angle());
			position.y += speed * Math.sin(MathUtils.degreesToRadians * direction.angle());
			this.sprite.setCenter(this.position.x, this.position.y);
			this.sprite.setOriginCenter();
			
			body.setTransform(WorldUtils.px2m((int) position.x), WorldUtils.px2m((int) position.y), direction.angle());
			
			this.distance = (float) Math.sqrt(Math.pow(position.x - startPoint.x, 2) + Math.pow(position.y - startPoint.y, 2));
			if (distance > maxDistance)
				active = false;
			
		}
	}

	public void render(SpriteBatch batch) {
		sprite.draw(batch);
	}
	
	@Override
	public void reset() {
		active = false;
		
	}
}
