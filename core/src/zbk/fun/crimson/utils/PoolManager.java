package zbk.fun.crimson.utils;

import java.util.ArrayList;
import java.util.List;

import zbk.fun.crimson.entity.Projectile;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class PoolManager {

	private static PoolManager instance;
	
	private Pool<Projectile> bulletPool;
	private List<Projectile> bullets;
	private List<Projectile> bulletsToRemove;
	
	private PoolManager() {
		
		this.bulletPool = Pools.get(Projectile.class);
		this.bullets = new ArrayList<Projectile>();
		this.bulletsToRemove = new ArrayList<Projectile>();
	}
	
	public static PoolManager instance() {
		
		if (instance == null)
			instance = new PoolManager();
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
	
}
