package zbk.fun.crimson.entity;

import java.util.List;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy {

	public int id;

	public Vector2 position;
	public Vector2 direction;
	public Vector2 target;

	public float activationRadius;

	public float rotation;
	public float distance;
	public float walkSpeed;
	public float life;
	public float time;

	public float timeToChangeDir;

	public Sprite sprite;
	public TextureRegion[] frames;

	public int width;
	public int height;

	public Rectangle bbox;

	//	private PooledEffect effect;
	private ParticleEffectPool pool;

	public Enemy(ParticleEffectPool pool) {

		id = UUID.randomUUID().hashCode();

		width = 50;
		height = 50;

		this.position = new Vector2(MathUtils.random(1600), MathUtils.random(1600));
		int texID = MathUtils.random(1, 10);
		Texture tex = new Texture(Gdx.files.internal("assets/citizenzombie" + texID + ".png"));
		frames = new TextureRegion[1];
		TextureRegion[][] tmp = TextureRegion.split(tex, width, height);
		frames[0] = tmp[0][0];
		sprite = new Sprite(frames[0]);
		sprite.setCenter(position.x, position.y);
		sprite.setOriginCenter();
		sprite.setRotation(-90f);
		//		sprite.scale(-0.3f);
		timeToChangeDir = 0f;

		this.pool = pool;

		walkSpeed = 0.5f;
		life = 100f;
		activationRadius = MathUtils.random(150f, 300f);
		bbox = new Rectangle(position.x - width / 2, position.y - height / 2, width, height);
	}

	public void update(Player player, List<Enemy> enemies) {

		float walkFluct = walkSpeed;
		time += Gdx.graphics.getDeltaTime();

		if (position.dst(player.position) <= activationRadius) {
			this.direction = player.position.cpy().sub(position).nor();
			this.rotation = -90 + direction.angle();
		} else {

			timeToChangeDir -= Gdx.graphics.getDeltaTime();

			if (timeToChangeDir <= 0.0f) {
				timeToChangeDir = MathUtils.random(10f);
				target = new Vector2(MathUtils.random(1600), MathUtils.random(1600));

				this.direction = target.cpy().sub(position).nor();
				this.rotation = -90 + direction.angle();

				this.sprite.setRotation(rotation);

				this.distance = (float) Math.sqrt(Math.pow(target.x - position.x, 2) + Math.pow(target.y - position.y, 2));
				walkFluct = (float) (walkSpeed + (Math.sin(time) * 0.7f));

			}

			for (Enemy e : enemies) {
				if (!e.equals(this)) {
					if (e.bbox.overlaps(bbox)) {
						if (e.direction != null) {
							float angle = direction.angle(e.direction);
							direction.x = (float) Math.sin(angle);
							direction.y = (float) Math.cos(angle);
						}
					}
				}
			}
		}

		position.x += walkFluct * Math.cos(MathUtils.degreesToRadians * direction.angle());
		position.y += walkFluct * Math.sin(MathUtils.degreesToRadians * direction.angle());
		this.sprite.setCenter(this.position.x, this.position.y);
		this.sprite.setOriginCenter();
		bbox.set(position.x - width / 2, position.y - height / 2, width, height);
	}

	public void render(SpriteBatch batch) {

		sprite.draw(batch);
	}

	public void postRender(ShapeRenderer sr) {

		sr.setColor(Color.WHITE);
		sr.rect(bbox.x, bbox.y, bbox.width, bbox.height);
	}

	public PooledEffect effect(Projectile p) {

		PooledEffect effect = pool.obtain();
		effect.setPosition(position.x-width/2, position.y-height/2);
		for (int i = 0; i < effect.getEmitters().size; i++) {                          
			ScaledNumericValue val = effect.getEmitters().get(i).getAngle();           
			float h1 = p.rotation + 90f;                                            
			float h2 = p.rotation - 90f;                                            
			val.setHigh(h1, h2);                                           
			val.setLow(p.rotation);       
		}   
		return effect;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Enemy other = (Enemy) obj;
		if (id != other.id)
			return false;
		return true;
	}
}
