package zbk.fun.crimson.entity;

import java.util.List;

import zbk.fun.crimson.enums.SurfacemarkType;
import zbk.fun.crimson.utils.EffectsManager;
import zbk.fun.crimson.utils.MarksManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Projectile implements Poolable{

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

	public Rectangle bbox;

	private int width;
	private int height;

	public boolean active;
	
	public Projectile() {
		
	}

	public Projectile(Vector2 position, Vector2 target) {
		
		init(position, target);
	}
	
	public void init(Vector2 position, Vector2 target) {

		Texture t = new Texture(Gdx.files.internal("assets/bullet.png"));
		this.width = t.getWidth();
		this.height = t.getHeight();

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

		this.bbox = new Rectangle(position.x - t.getWidth() / 2, position.y - t.getHeight() / 2, t.getWidth(), t.getHeight());
		this.active = true;
	}

	public void update(List<Enemy> enemies) {

		if (active) {
			time += Gdx.graphics.getDeltaTime();

			position.x += speed * Math.cos(MathUtils.degreesToRadians * direction.angle());
			position.y += speed * Math.sin(MathUtils.degreesToRadians * direction.angle());
			this.sprite.setCenter(this.position.x, this.position.y);
			this.sprite.setOriginCenter();
			bbox.set(position.x - width / 2, position.y - height / 2, width, height);
			
			this.distance = (float) Math.sqrt(Math.pow(position.x - startPoint.x, 2) + Math.pow(position.y - startPoint.y, 2));
			if (distance > maxDistance)
				active = false;
			
			for (Enemy e : enemies) {
				
				if (bbox.overlaps(e.bbox)) {
					e.life -= damage;
					e.effect(this);
					Surfacemark mark = MarksManager.instance().getMark();
					mark.init(SurfacemarkType.BLOODMARK, position.cpy(), rotation);
					active = false;
				}
			}
		}
	}

	public void render(SpriteBatch batch) {
		sprite.draw(batch);
	}
	
	public void postRender(ShapeRenderer sr) {
		sr.setColor(Color.WHITE);
		sr.rect(bbox.x,  bbox.y,  bbox.width,  bbox.height);
	}

	@Override
	public void reset() {
		active = false;
		
	}
}
