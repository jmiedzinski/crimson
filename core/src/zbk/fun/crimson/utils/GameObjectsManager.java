package zbk.fun.crimson.utils;

import java.util.ArrayList;
import java.util.List;

import zbk.fun.crimson.entity.Explosive;
import zbk.fun.crimson.entity.Projectile;
import zbk.fun.crimson.entity.Weapon;
import zbk.fun.crimson.enums.WeaponType;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class GameObjectsManager {

	private static GameObjectsManager instance;
	
	private World world;
	
	private Pool<Projectile> bulletPool;
	private List<Projectile> bullets;
	private List<Projectile> bulletsToRemove;
	
	private Pool<Explosive> explosivePool;
	private List<Explosive> explosives;
	private List<Explosive> explosivesToRemove;
	
	private Pool<Weapon> weaponPool;
	private List<Weapon> weapons;
	private List<Weapon> weaponsToRemove;
	
	private GameObjectsManager() {
		
		this.bulletPool = Pools.get(Projectile.class);
		this.bullets = new ArrayList<Projectile>();
		this.bulletsToRemove = new ArrayList<Projectile>();
		
		this.explosivePool = Pools.get(Explosive.class);
		this.explosives = new ArrayList<Explosive>();
		this.explosivesToRemove = new ArrayList<Explosive>();
		
		this.weaponPool = Pools.get(Weapon.class);
		this.weapons = new ArrayList<Weapon>();
		this.weaponsToRemove = new ArrayList<Weapon>();
	}
	
	public static GameObjectsManager instance() {
		
		if (instance == null)
			instance = new GameObjectsManager();
		return instance;
	}

	public Pool<Projectile> getBulletPool() {
		return bulletPool;
	}

	public List<Projectile> getBullets() {
		return bullets;
	}
	
	public Projectile newBullet(Vector2 position, Vector2 target) {
		
		Projectile p = bulletPool.obtain();
		p.init(position, target);
		WorldUtils.createBulletBody(world, position, p);
		bullets.add(p);
		
		return p;
	}
	
	public List<Projectile> getBulletsToRemove() {
		return bulletsToRemove;
	}
	
	public void clearBullets() {
		for (Projectile p : bulletsToRemove) {
			world.destroyBody(p.body);
			bulletPool.free(p);
		}
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
	
	public void renderBullets(SpriteBatch batch, float deltaTime) {
		
		for (Projectile p : bullets) {
			if (p.active) {
				p.update(NPCManager.instance().getEnemies(), deltaTime);
				p.render(batch);
			} else {
				bulletsToRemove.add(p);
			}
		}
		clearBullets();
	}
	
	public void renderExplosives(SpriteBatch batch, float deltaTime) {
		
		for (Explosive e : explosives) {
			e.update(NPCManager.instance().getEnemies(), deltaTime);
			e.render(batch);
		}
		clearExplosives();
	}
	
	public void renderWeapons(SpriteBatch batch, float deltaTime) {
		
		for (Weapon w : weapons) {
			w.render(batch, deltaTime);
		}
	}
	
	public Weapon newWeapon(WeaponType weaponType) {
		
		Weapon w = weaponPool.obtain();
		w.init(world, weaponType);
		weapons.add(w);
		
		return w;
	}
	
	public Pool<Weapon> getWeaponPool() {
		return weaponPool;
	}

	public List<Weapon> getWeapons() {
		return weapons;
	}

	public List<Weapon> getWeaponsToRemove() {
		return weaponsToRemove;
	}

	public void setWorld(World world) {
		this.world = world;
	}
	
}
