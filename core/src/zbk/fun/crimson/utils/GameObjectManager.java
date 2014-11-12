package zbk.fun.crimson.utils;

import java.util.ArrayList;
import java.util.List;

import zbk.fun.crimson.entity.Enemy;
import zbk.fun.crimson.entity.Explosive;
import zbk.fun.crimson.entity.Projectile;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class GameObjectManager {

	private static GameObjectManager instance;
	
	private Pool<Projectile> bulletPool;
	private List<Projectile> bullets;
	private List<Projectile> bulletsToRemove;
	
	private Pool<Explosive> explosivePool;
	private List<Explosive> explosives;
	private List<Explosive> explosivesToRemove;
	
	private GameObjectManager() {
		
		this.bulletPool = Pools.get(Projectile.class);
		this.bullets = new ArrayList<Projectile>();
		this.bulletsToRemove = new ArrayList<Projectile>();
		
		this.explosivePool = Pools.get(Explosive.class);
		this.explosives = new ArrayList<Explosive>();
		this.explosivesToRemove = new ArrayList<Explosive>();
	}
	
	public static GameObjectManager instance() {
		
		if (instance == null)
			instance = new GameObjectManager();
		return instance;
	}

	public Pool<Projectile> getBulletPool() {
		return bulletPool;
	}

	public List<Projectile> getBullets() {
		return bullets;
	}
	
	public List<Projectile> getBulletsToRemove() {
		return bulletsToRemove;
	}
	
	public void clearBullets() {
		for (Projectile p : bulletsToRemove)
			bulletPool.free(p);
		bullets.removeAll(bulletsToRemove);
		bulletsToRemove.clear();
	}

	public Pool<Explosive> getExplosivePool() {
		return explosivePool;
	}

	public List<Explosive> getExplosives() {
		return explosives;
	}

	public List<Explosive> getExplosivesToRemove() {
		return explosivesToRemove;
	}
	
	public void clearExplosives() {
		for (Explosive e : explosivesToRemove)
			explosivePool.free(e);
		explosives.removeAll(explosivesToRemove);
		explosivesToRemove.clear();
	}
	
	public Explosive getExplosive() {
		Explosive e = explosivePool.obtain();
		explosives.add(e);
		return e;
	}
	
	public void renderBullets(SpriteBatch batch, List<Enemy> enemies) {
		
		for (Projectile p : bullets) {
			if (p.active) {
				p.update(enemies);
				p.render(batch);
			} else {
				bulletsToRemove.add(p);
			}
		}
		clearBullets();
	}
	
	public void renderExplosives(SpriteBatch batch, List<Enemy> enemies) {
		
		for (Explosive e : explosives) {
			e.update(enemies);
			e.render(batch);
		}
		clearExplosives();
	}
	
}
