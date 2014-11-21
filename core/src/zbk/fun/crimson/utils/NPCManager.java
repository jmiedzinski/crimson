package zbk.fun.crimson.utils;

import java.util.Iterator;

import zbk.fun.crimson.ai.RadiusProximity;
import zbk.fun.crimson.entity.Enemy;
import zbk.fun.crimson.entity.Player;
import zbk.fun.crimson.enums.NPCBehavior;
import zbk.fun.crimson.enums.NPCType;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.behaviors.CollisionAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.Flee;
import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.ai.steer.behaviors.Seek;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.ai.steer.limiters.LinearAccelerationLimiter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class NPCManager {

	private static NPCManager instance;

	private World world;
	private Pool<Enemy> enemyPool;
	private Array<Steerable<Vector2>> enemies;
	private Array<Steerable<Vector2>> enemiesToDelete;
	private Array<RadiusProximity> proximities;

	private NPCManager() {

		enemyPool = Pools.get(Enemy.class);
		enemies = new Array<Steerable<Vector2>>();
		enemiesToDelete = new Array<Steerable<Vector2>>();
		proximities = new Array<RadiusProximity>();
	}

	public static NPCManager instance() {

		if (instance == null)
			instance = new NPCManager();
		return instance;
	}

	public Pool<Enemy> getEnemyPool() {
		return enemyPool;
	}

	public Array<Steerable<Vector2>> getEnemies() {
		return enemies;
	}

	public Array<Steerable<Vector2>> getEnemiesToDelete() {
		return enemiesToDelete;
	}

	public void clearEnemies() {
		Iterator i = enemiesToDelete.iterator();
		while (i.hasNext()) {
			Enemy e = (Enemy)i.next();
			world.destroyBody(e.body);
			enemyPool.free(e);

		}
		enemies.removeAll(enemiesToDelete, false);
		enemiesToDelete.clear();
	}

	public Enemy newEnemy(World world, Player player) {

		NPCType type = NPCType.getById(MathUtils.random(1, 10));
		Texture t = new Texture(Gdx.files.internal(type.getTexture()));
		
		Enemy enemy = enemyPool.obtain();
		enemy.init(type, TextureRegion.split(t, t.getWidth(), t.getHeight())[0][0], false);
		WorldUtils.createNPCBody(world, enemy);

//		final Enemy enemy = WorldUtils.createNPC(world, TextureRegion.split(t, t.getWidth(), t.getHeight())[0][0], false, type);
		enemy.setMaxLinearSpeed(MathUtils.random(0.5f, 1.5f));
		enemy.setMaxLinearAcceleration(40);

		RadiusProximity proximity = new RadiusProximity(enemy, world, enemy.getBoundingRadius() * 4);
		proximities.add(proximity);

		Wander<Vector2> wanderSB = new Wander<Vector2>(enemy) //
				.setFaceEnabled(false) //
				.setLimiter(new LinearAccelerationLimiter(30)) //
				.setWanderOffset(60) //
				.setWanderOrientation(10) //
				.setWanderRadius(40) //
				.setWanderRate(MathUtils.PI / 5);
		
		enemy.behaviors.put(NPCBehavior.WANDER, wanderSB);

		Seek<Vector2> seekSB = new Seek<Vector2>(enemy, player);
		seekSB.setLimiter(new LinearAccelerationLimiter(30));
		
		enemy.behaviors.put(NPCBehavior.SEEK, seekSB);
		
		Flee<Vector2> fleeSB = new Flee<Vector2>(enemy, player);
		fleeSB.setLimiter(new LinearAccelerationLimiter(30f));
		
		enemy.behaviors.put(NPCBehavior.FLEE, fleeSB);

		enemy.changeBehavior(NPCBehavior.WANDER);

		WorldUtils.setRandomNonOverlappingPosition(enemy, enemies, WorldUtils.pixelsToMeters(5));

		enemies.add(enemy);
		return enemy;
	}

	public void renderEnemies(SpriteBatch batch, float deltaTime) {

		for (int i = 0; i < enemies.size; i++) {
			Enemy character = (Enemy)enemies.get(i);
			if (character.life <= 0f && character.readyToClean)
				enemiesToDelete.add(character);
			else {
				character.update(deltaTime);
				character.draw(batch);
			}
		}
		clearEnemies();
	}

	public void populateEnemies(World world, Player player, int count) {

		for (int i=0; i<count; i++)
			newEnemy(world, player);
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

}
