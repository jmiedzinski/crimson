package zbk.fun.crimson.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

public class EffectsManager {

	private static EffectsManager instance;
	
	ParticleEffectPool bloodEffectPool;
	Array<PooledEffect> effects = new Array<PooledEffect>();
	
	ParticleEffectPool explosionEffectPool;
	Array<PooledEffect> explosionEffects = new Array<PooledEffect>();
	
	private EffectsManager() {
		
		ParticleEffect bloodEffect = new ParticleEffect();
		bloodEffect.load(Gdx.files.internal("assets/blood.p"), Gdx.files.internal("assets"));
		bloodEffectPool = new ParticleEffectPool(bloodEffect, 10, 50);
		
		ParticleEffect explosionEffect = new ParticleEffect();
		explosionEffect.load(Gdx.files.internal("assets/explosion.p"), Gdx.files.internal("assets"));
		explosionEffectPool = new ParticleEffectPool(explosionEffect, 10, 50);
	}
	
	public static EffectsManager instance() {
		
		if (instance == null)
			instance = new EffectsManager();
		return instance;
	}
	
	public void renderEffects(SpriteBatch batch) {
		
		for (int i = effects.size - 1; i >= 0; i--) {
			PooledEffect effect = effects.get(i);
			effect.draw(batch, Gdx.graphics.getDeltaTime());
			if (effect.isComplete()) {
				effect.free();
				effects.removeIndex(i);
			}
		}
		
		// Update and draw effects:
		for (int i = explosionEffects.size - 1; i >= 0; i--) {
			PooledEffect effect = explosionEffects.get(i);
			effect.draw(batch, Gdx.graphics.getDeltaTime());
			if (effect.isComplete()) {
				effect.free();
				explosionEffects.removeIndex(i);
			}
		}	
	}
	
	public PooledEffect newExplosionEffect() {
		PooledEffect effect = explosionEffectPool.obtain();
		explosionEffects.add(effect);
		return effect;
	}
	
	public PooledEffect newBloodEffect() {
		PooledEffect effect = bloodEffectPool.obtain();
		effects.add(effect);
		return effect;
	}

	public ParticleEffectPool getBloodEffectPool() {
		return bloodEffectPool;
	}

	public Array<PooledEffect> getEffects() {
		return effects;
	}

	public ParticleEffectPool getExplosionEffectPool() {
		return explosionEffectPool;
	}

	public Array<PooledEffect> getExplosionEffects() {
		return explosionEffects;
	}
	
}
