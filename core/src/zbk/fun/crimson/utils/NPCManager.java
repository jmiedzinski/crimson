package zbk.fun.crimson.utils;

import java.util.ArrayList;
import java.util.List;

import zbk.fun.crimson.entity.Enemy;
import zbk.fun.crimson.entity.Player;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class NPCManager {

	private static NPCManager instance;
	
	private Pool<Enemy> enemyPool;
	private List<Enemy> enemies;
	private List<Enemy> enemiesToDelete;
	
	private NPCManager() {
		
		enemyPool = Pools.get(Enemy.class);
		enemies = new ArrayList<Enemy>();
		enemiesToDelete = new ArrayList<Enemy>();
	}
	
	public static NPCManager instance() {
		
		if (instance == null)
			instance = new NPCManager();
		return instance;
	}

	public Pool<Enemy> getEnemyPool() {
		return enemyPool;
	}

	public List<Enemy> getEnemies() {
		return enemies;
	}

	public List<Enemy> getEnemiesToDelete() {
		return enemiesToDelete;
	}
	
	public void clearEnemies() {
		for (Enemy e : enemiesToDelete)
			enemyPool.free(e);
		enemies.removeAll(enemiesToDelete);
		enemiesToDelete.clear();
	}
	
	public Enemy newEnemy() {
		Enemy e = enemyPool.obtain();
		enemies.add(e);
		return e;
	}
	
	public void renderEnemies(SpriteBatch batch, Player player) {
		
		for (Enemy e : enemies) {
			if (e.life > 0f) {
				e.update(player, enemies);
				e.render(batch);
			} else {
				enemiesToDelete.add(e);
			}
		}
		clearEnemies();
	}
	
	public void populateEnemies(int count) {
		
		for (int i=0; i<count; i++)
			newEnemy();
	}
	
}
