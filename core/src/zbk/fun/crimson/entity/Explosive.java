package zbk.fun.crimson.entity;

import java.util.List;

import zbk.fun.crimson.enums.ExplosiveType;
import zbk.fun.crimson.enums.SurfacemarkType;
import zbk.fun.crimson.utils.EffectsManager;
import zbk.fun.crimson.utils.GameObjectsManager;
import zbk.fun.crimson.utils.MarksManager;
import zbk.fun.crimson.utils.WorldUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
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
	
	public void update(Array<Steerable<Vector2>> enemies) {
		
		boolean explode = false;
		time -= Gdx.graphics.getDeltaTime();
		animTime += Gdx.graphics.getDeltaTime();
		
		sprite.setRegion(animation.getKeyFrame(animTime, true));
		
		if (!proximityTriggered) {
			if (time <= 0) {
				explode = true;
			}
		} else {
			for (int i = 0; i < enemies.size; i++) {
				Enemy e = (Enemy) enemies.get(i);
				if (e.getPosition().dst(position) <= 20f) {
					explode = true;
					break;
				}
			}			
		}
		
		if (explode)
			explode(enemies);
	}
	
	private void explode(Array<Steerable<Vector2>> enemies) {
		
		for (int i = 0; i < enemies.size; i++) {
			Enemy e = (Enemy) enemies.get(i);
			Vector2 enemyPos = new Vector2(WorldUtils.m2px(e.getPosition().x), WorldUtils.m2px(e.getPosition().y));
			if (enemyPos.dst(position) <= type.getRange()) {
				e.life -= type.getDamage();
				e.body.applyForceToCenter(enemyPos.sub(position).nor().scl(100f), true);
			}
		}
		effect();
		GameObjectsManager.instance().getExplosivesToRemove().add(this);
		mark();
	}
	
	private void effect() {
		PooledEffect effect = EffectsManager.instance().newExplosionEffect();
		effect.setPosition(position.x-width/2, position.y-height/2);
	}
	
	private void mark() {
		Surfacemark mark = MarksManager.instance().getMark();
		mark.init(SurfacemarkType.EXPLOSIONMARK, position, 90f);
	}
	
	public void render(SpriteBatch batch) {
		sprite.draw(batch);
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
}
