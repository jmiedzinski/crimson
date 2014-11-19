package zbk.fun.crimson.utils;

import zbk.fun.crimson.entity.Enemy;
import zbk.fun.crimson.entity.Player;
import zbk.fun.crimson.entity.Projectile;
import zbk.fun.crimson.entity.Weapon;
import zbk.fun.crimson.enums.NPCType;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class WorldUtils {
	
	// 1 pixel = 0,02 meters (2 cm)
	public static float pixelsToMeters(int pixels) {
		return (float)pixels * 0.02f;
	}
	
	public static float px2m(int pixels) {
		return pixelsToMeters(pixels);
	}
	
	// 1 meter = 50 pixels
	public static int metersToPixels(float meters) {
		return (int)(meters * 50.0f);
	}
	
	public static int m2px(float meters) {
		return metersToPixels(meters);
	}
	
	public static void createWorld(World world, Body groundBody) {
		
		world = new World(new Vector2(0, 0), true);
		float worldWidth = 1600f;
		float worldHeight = 1600f;
		
		float halfWidth = worldWidth / 2f;
		ChainShape chainShape = new ChainShape();
		chainShape.createLoop(new Vector2[] {
				new Vector2(-halfWidth, 0f),
				new Vector2(halfWidth, 0f),
				new Vector2(halfWidth, worldHeight),
				new Vector2(-halfWidth, worldHeight) });
		BodyDef chainBodyDef = new BodyDef();
		chainBodyDef.type = BodyType.StaticBody;
		groundBody = world.createBody(chainBodyDef);
		groundBody.createFixture(chainShape, 0);
		chainShape.dispose();
	}
	
	public static void createPlayerBody(World world, Player player) {
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(px2m(Gdx.graphics.getWidth() / 2), px2m(Gdx.graphics.getHeight() / 2));

		player.body = world.createBody(bodyDef);
		float radiusInMeters = px2m((int) ((player.getWidth() + player.getHeight()) / 3f));

		// Create a circle shape and set its radius to 6
		CircleShape circle = new CircleShape();
		circle.setRadius(radiusInMeters);

		// Create a fixture definition to apply our shape to
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 1f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0f;

		// Create our fixture and attach it to the body
		Fixture fixture = player.body.createFixture(fixtureDef);
		fixture.setSensor(true);
		player.body.setUserData(player);
		player.body.setTransform(px2m((int) player.position.x), px2m((int) player.position.y), player.rotation);

		circle.dispose();
		
		circle = new CircleShape();
		circle.setRadius(8f);
		
		fixtureDef = new FixtureDef();
		fixtureDef.isSensor = true;
		fixtureDef.shape = circle;
		fixtureDef.density = 1f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0f;
		
		fixture = player.body.createFixture(fixtureDef);
		
		circle.dispose();
		
	}
	
	public static void createBulletBody(World world, Vector2 position, Projectile bullet) {
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(px2m((int) position.x), px2m((int) position.y));
		
		bullet.body = world.createBody(bodyDef);
		
		CircleShape circle = new CircleShape();
		circle.setRadius(0.05f);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 1f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0f;
		
		bullet.body.createFixture(fixtureDef);
		bullet.body.setUserData(bullet);
		
		circle.dispose();
	}
	
	public static void createWeaponBody(World world, Weapon weapon) {
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(px2m((int) weapon.position.x), px2m((int) weapon.position.y));
		
		weapon.body = world.createBody(bodyDef);
		
		CircleShape circle = new CircleShape();
		circle.setRadius(2f);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = circle;
		fixtureDef.density = 1f; 
		fixtureDef.friction = 0.4f;
		fixtureDef.restitution = 0f;
		
		weapon.body.createFixture(fixtureDef);
		weapon.body.setUserData(weapon);
		weapon.body.setTransform(px2m((int) weapon.position.x), px2m((int) weapon.position.y), 0f);
		
		circle.dispose();
	}
	
	public static void createNPCBody(World world, Enemy enemy) {
		
		CircleShape circleChape = new CircleShape();
		circleChape.setPosition(new Vector2());
		int radiusInPixels = (int)((enemy.getRegion().getRegionWidth() + enemy.getRegion().getRegionHeight()) / 4f);
		circleChape.setRadius(pixelsToMeters(radiusInPixels));

		BodyDef characterBodyDef = new BodyDef();
		characterBodyDef.position.set(px2m(MathUtils.random(1600)), px2m(MathUtils.random(1600)));
		characterBodyDef.type = BodyType.DynamicBody;
		Body characterBody = world.createBody(characterBodyDef);

		FixtureDef charFixtureDef = new FixtureDef();
		charFixtureDef.density = 1;
		charFixtureDef.shape = circleChape;
		charFixtureDef.filter.groupIndex = 0;
		characterBody.createFixture(charFixtureDef);

		circleChape.dispose();
		enemy.setBody(characterBody);
	}
	
	public static Enemy createNPC (World world, TextureRegion region, NPCType type) {
		return createNPC(world, region, false, type);
	}

	public static Enemy createNPC (World world, TextureRegion region, boolean independentFacing, NPCType type) {

		Enemy e = new Enemy();
		e.init(type, region, independentFacing);
		WorldUtils.createNPCBody(world, e);
		return e;
	}

	public static void setRandomNonOverlappingPosition (Enemy character, Array<Steerable<Vector2>> others, float minDistanceFromBoundary) {
		int maxTries = Math.max(100, others.size * others.size); 
		SET_NEW_POS:
			while (--maxTries >= 0) {
				int x = (int) MathUtils.random(1600);
				int y = (int) MathUtils.random(1600);
				float angle = MathUtils.random(-MathUtils.PI, MathUtils.PI);
				character.body.setTransform(pixelsToMeters(x), pixelsToMeters(y), angle);
				for (int i = 0; i < others.size; i++) {
					Enemy other = (Enemy)others.get(i);
					if (character.getPosition().dst(other.getPosition()) <= character.getBoundingRadius() + other.getBoundingRadius()
							+ minDistanceFromBoundary) continue SET_NEW_POS;
				}
				return;
			}
		throw new GdxRuntimeException("Probable infinite loop detected");
	}
	
}
