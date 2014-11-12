package zbk.fun.crimson.entity;

import java.util.List;

import zbk.fun.crimson.enums.ExplosiveType;
import zbk.fun.crimson.utils.PoolManager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Explosive implements Poolable {

	public ExplosiveType type;
	public Vector2 position;
	public float time;
	public float animTime;
	public boolean proximityTriggered;
	
	public Sprite sprite;
	public Animation animation;
	public int width;
	public int height;
	
	public Explosive() {
		
	}
	
	public Explosive(ExplosiveType explosiveType, Vector2 position) {
		
		init(explosiveType, position);
	}
	
	public void init(ExplosiveType explosiveType, Vector2 position) {
		
		this.type = explosiveType;
		this.position = position;
		this.time = type.getTime();
		
		if (type.getTime() == -1)
			proximityTriggered = true;
		else {
			proximityTriggered = false;
			time = type.getTime();
		}
		TextureRegion[][] tmp = TextureRegion.split(new Texture(Gdx.files.internal("assets/" + type.name().toLowerCase() + ".png")), 40, 24);
		TextureRegion[] frames = new TextureRegion[2];
		frames[0] = tmp[0][0];
		frames[1] = tmp[0][1];
		animation = new Animation(0.5f, frames);
		sprite = new Sprite(frames[0]);
		sprite.setCenter(position.x, position.y);
		sprite.setOriginCenter();
	}
	
	public void update(List<Enemy> enemies) {
		
		boolean explode = false;
		time -= Gdx.graphics.getDeltaTime();
		animTime += Gdx.graphics.getDeltaTime();
		
		sprite.setRegion(animation.getKeyFrame(animTime, true));
		
		if (!proximityTriggered) {
			if (time <= 0) {
				explode = true;
			}
		} else {
			for (Enemy e : enemies) {
				if (e.position.dst(position) <= 20f) {
					explode = true;
					break;
				}
			}			
		}
		
		if (explode)
			explode(enemies);
	}
	
	private void explode(List<Enemy> enemies) {
		
		for (Enemy e : enemies) {
			if (e.position.dst(position) <= type.getRange()) {
				e.life -= type.getDamage();
			}
		}
		PoolManager.instance().getExplosivesToRemove().add(this);
	}
	
	public void render(SpriteBatch batch) {
		sprite.draw(batch);
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
}
