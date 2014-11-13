package zbk.fun.crimson;

import java.util.ArrayList;
import java.util.List;

import zbk.fun.crimson.ai.RadiusProximity;
import zbk.fun.crimson.entity.AIEnemy;
import zbk.fun.crimson.entity.Enemy;
import zbk.fun.crimson.entity.Player;
import zbk.fun.crimson.entity.Projectile;
import zbk.fun.crimson.entity.Weapon;
import zbk.fun.crimson.enums.WeaponType;
import zbk.fun.crimson.utils.EffectsManager;
import zbk.fun.crimson.utils.GameObjectsManager;
import zbk.fun.crimson.utils.MarksManager;
import zbk.fun.crimson.utils.NPCManager;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ai.steer.behaviors.CollisionAvoidance;
import com.badlogic.gdx.ai.steer.behaviors.PrioritySteering;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.ai.steer.limiters.LinearAccelerationLimiter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class CrimsonGame extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	BitmapFont font;
	Player player;
	OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	TiledMap tiledMap;
	TiledMapRenderer tiledMapRenderer;
	InputMultiplexer inputMultiplexer;

	int mapSize;
	int tileSize;
	Rectangle mapBounds;
	Rectangle camBounds;

	float camMaxLeft, camMaxRight, camMaxTop, camMaxBottom;

	List<Weapon> weapons;

	World world;

	Array<AIEnemy> characters;
	RadiusProximity char0Proximity;
	Array<RadiusProximity> proximities;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 600);

		player = new Player(camera, 80, 87, 1, 6, new Texture(Gdx.files.internal("assets/player.png")), 0.125f);
		player.setCamera(camera);
		camera.position.x = player.position.x + 5;
		camera.position.y = player.position.y + 5;
		camBounds = new Rectangle(camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2, camera.viewportWidth, camera.viewportHeight);
		shapeRenderer = new ShapeRenderer();

		tiledMap = new TmxMapLoader().load("assets/arena.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
		mapSize = Integer.parseInt(tiledMap.getProperties().get("map_size", String.class));
		tileSize = Integer.parseInt(tiledMap.getProperties().get("tile_size", String.class));
		mapBounds = new Rectangle(0f, 0f, mapSize*tileSize, mapSize*tileSize);

		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(player);
		inputMultiplexer.addProcessor(this);
		Gdx.input.setInputProcessor(inputMultiplexer);

		font = new BitmapFont();
		font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		font.setScale(1.0f);

		camMaxLeft = camera.viewportWidth / 2;
		camMaxRight = mapSize * tileSize - camera.viewportWidth / 2;
		camMaxTop = mapSize *tileSize - camera.viewportHeight / 2;
		camMaxBottom = camera.viewportHeight / 2;

		NPCManager.instance().populateEnemies(50);

		weapons = new ArrayList<Weapon>();
		Weapon pistol = new Weapon(WeaponType.PISTOL);
		weapons.add(pistol);
		Weapon shotgun = new Weapon(WeaponType.SHOTGUN);
		weapons.add(shotgun);
		Weapon machinegun = new Weapon(WeaponType.MACHINE_GUN);
		weapons.add(machinegun);

		this.world = new World(new Vector2(0, 0), true);
		characters = new Array<AIEnemy>();
		proximities = new Array<RadiusProximity>();

		Texture tex = new Texture(Gdx.files.internal("assets/citizenzombie1.png"));
		TextureRegion[] frames = new TextureRegion[1];
		TextureRegion[][] tmp = TextureRegion.split(tex, tex.getWidth(), tex.getHeight());
		frames[0] = tmp[0][0];

		for (int i = 0; i < 60; i++) {
			final AIEnemy character = createSteeringEntity(world, frames[0], false);
			character.setMaxLinearSpeed(1.5f);
			character.setMaxLinearAcceleration(40);

			RadiusProximity proximity = new RadiusProximity(character, world, character.getBoundingRadius() * 4);
			proximities.add(proximity);
			if (i == 0) char0Proximity = proximity;
			CollisionAvoidance<Vector2> collisionAvoidanceSB = new CollisionAvoidance<Vector2>(character, proximity);

			Wander<Vector2> wanderSB = new Wander<Vector2>(character) //
					// Don't use Face internally because independent facing is off
					.setFaceEnabled(false) //
					// We don't need a limiter supporting angular components because Face is not used
					// No need to call setAlignTolerance, setDecelerationRadius and setTimeToTarget for the same reason
					.setLimiter(new LinearAccelerationLimiter(30)) //
					.setWanderOffset(60) //
					.setWanderOrientation(10) //
					.setWanderRadius(40) //
					.setWanderRate(MathUtils.PI / 5);

			PrioritySteering<Vector2> prioritySteeringSB = new PrioritySteering<Vector2>(character, 0.0001f);
			prioritySteeringSB.add(collisionAvoidanceSB);
			prioritySteeringSB.add(wanderSB);

			character.setSteeringBehavior(prioritySteeringSB);

			setRandomNonOverlappingPosition(character, characters, AIEnemy.pixelsToMeters(5));

			characters.add(character);
		}

	}

	private void moveCamera() {

		Vector2 camNextPoint = new Vector2(camera.position.x + 1.5f * player.direction.x, camera.position.y + 1.5f * player.direction.y);
		Vector2 playerNextPoint = new Vector2(player.position.x + 1.5f * player.direction.x, player.position.y + 1.5f * player.direction.y);
		float playerCamDistanceNext = (float) Math.sqrt(Math.pow(playerNextPoint.x - camera.position.x, 2) + Math.pow(playerNextPoint.y - camera.position.y, 2));
		float playerCamDistance = (float) Math.sqrt(Math.pow(player.position.x - camera.position.x, 2) + Math.pow(player.position.y - camera.position.y, 2));


		if (player.isMoving() && playerCamDistanceNext > 200f && playerCamDistanceNext > playerCamDistance) {
			if (camNextPoint.x > camMaxLeft && camNextPoint.x < camMaxRight) {
				camera.translate(1.5f * player.direction.x, 0f);
			}
			if (camNextPoint.y > camMaxBottom && camNextPoint.y < camMaxTop) {
				camera.translate(0f, 1.5f * player.direction.y);
			}
		}
	}

	@Override
	public void render () {
		
		float deltaTime = Gdx.graphics.getDeltaTime();

		world.step(deltaTime, 8, 3);

		camera.update();
		camBounds.set(camera.position.x - camera.viewportWidth / 2, camera.position.y - camera.viewportHeight / 2, camera.viewportWidth, camera.viewportHeight);
		batch.setProjectionMatrix(camera.combined);
		player.update();
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();
		batch.begin();

		MarksManager.instance().renderMarks(batch);

		for (Weapon w : weapons) {
			w.update(player);
			w.render(batch);
		}

		player.render(batch);

		NPCManager.instance().renderEnemies(batch, player);

		GameObjectsManager.instance().renderBullets(batch);

		GameObjectsManager.instance().renderExplosives(batch);

		EffectsManager.instance().renderEffects(batch);
		
		for (int i = 0; i < characters.size; i++) {
			AIEnemy character = characters.get(i);
			character.update(deltaTime);
			character.draw(batch);
		}

		renderHUD(batch);
		batch.end();

		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.WHITE);

		for (Enemy e : NPCManager.instance().getEnemies()) {
			e.postRender(shapeRenderer);
		}

		for (Projectile p : GameObjectsManager.instance().getBullets()) {
			p.postRender(shapeRenderer);
		}

		shapeRenderer.rect(player.getBbox().x, player.getBbox().y, player.getBbox().width, player.getBbox().height);

		shapeRenderer.setColor(Color.RED);
		shapeRenderer.circle(player.getTarget().x, player.getTarget().y, 10);
		shapeRenderer.setColor(Color.YELLOW);
		shapeRenderer.line(player.getPosition().x, player.getPosition().y, player.getTarget().x, player.getTarget().y);
		shapeRenderer.setColor(Color.CYAN);
		shapeRenderer.line(player.getPosition().x, player.getPosition().y, camera.position.x, camera.position.y);
		shapeRenderer.end();

		if (!mapBounds.contains(player.getBbox())) {
			player.stop();
			player.stepBack();
		}

		moveCamera();

		batch.begin();
		Vector3 screen = new Vector3(10, 10, 0);
		camera.unproject(screen);

		int lines = 0;
		font.draw(batch, "TRG: " + MathUtils.round(player.getTarget().x) + ":" + MathUtils.round(player.getTarget().y), screen.x, screen.y - (lines*15)); 	lines++;
		font.draw(batch, "POS: " + MathUtils.round(player.getPosition().x) + ":" + MathUtils.round(player.getPosition().y), screen.x, screen.y - (lines*15)); 	lines++;
		font.draw(batch, "CAM: " + MathUtils.round(camera.position.x) + ":" + MathUtils.round(camera.position.y), screen.x, screen.y - (lines*15)); 	lines++;
		font.draw(batch, "ENM: " + NPCManager.instance().getEnemies().size(), screen.x, screen.y - (lines*15)); 	lines++;
		font.draw(batch, "PRJ: " + GameObjectsManager.instance().getBullets().size(), screen.x, screen.y - (lines*15)); 	lines++;
		font.draw(batch, "BLM: " + MarksManager.instance().getMarks().size(), screen.x, screen.y - (lines*15)); 	lines++;

		batch.end();

	}

	public void renderHUD(SpriteBatch batch) {

		if (player.currentWeapon != null) {
			Vector3 screen = new Vector3(Gdx.graphics.getWidth() - player.currentWeapon.width - 10, 10 + player.currentWeapon.height, 0);
			camera.unproject(screen);
			player.currentWeapon.sprite.setPosition(screen.x, screen.y);
			player.currentWeapon.sprite.draw(batch);
			screen.set(Gdx.graphics.getWidth() - player.currentWeapon.width - 20, 10 + player.currentWeapon.height, 0);
			camera.unproject(screen);
			font.draw(batch, Integer.toString(player.currentWeapon.clip), screen.x, screen.y);
		}
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.LEFT)
			camera.translate(-32,0);
		if(keycode == Input.Keys.RIGHT)
			camera.translate(32,0);
		if(keycode == Input.Keys.UP)
			camera.translate(0,-32);
		if(keycode == Input.Keys.DOWN)
			camera.translate(0,32);

		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		//		click = new Vector3(screenX, screenY, 0f);
		//		camera.unproject(click);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	public AIEnemy createSteeringEntity (World world, TextureRegion region) {
		return createSteeringEntity(world, region, false);
	}

	public AIEnemy createSteeringEntity (World world, TextureRegion region, boolean independentFacing) {
		return createSteeringEntity(world, region, independentFacing, 1600, 1600);
	}

	public AIEnemy createSteeringEntity (World world, TextureRegion region, int posX, int posY) {
		return createSteeringEntity(world, region, false, posX, posY);
	}

	public AIEnemy createSteeringEntity (World world, TextureRegion region, boolean independentFacing, int posX, int posY) {

		CircleShape circleChape = new CircleShape();
		circleChape.setPosition(new Vector2());
		int radiusInPixels = (int)((region.getRegionWidth() + region.getRegionHeight()) / 4f);
		circleChape.setRadius(AIEnemy.pixelsToMeters(radiusInPixels));

		BodyDef characterBodyDef = new BodyDef();
		characterBodyDef.position.set(AIEnemy.pixelsToMeters(posX), AIEnemy.pixelsToMeters(posY));
		characterBodyDef.type = BodyType.DynamicBody;
		Body characterBody = world.createBody(characterBodyDef);

		FixtureDef charFixtureDef = new FixtureDef();
		charFixtureDef.density = 1;
		charFixtureDef.shape = circleChape;
		charFixtureDef.filter.groupIndex = 0;
		characterBody.createFixture(charFixtureDef);

		circleChape.dispose();
		AIEnemy e = new AIEnemy();
		e.init(region, characterBody, independentFacing, AIEnemy.pixelsToMeters(radiusInPixels));
		return e;
	}

	protected void setRandomNonOverlappingPosition (AIEnemy character, Array<AIEnemy> others, float minDistanceFromBoundary) {
		int maxTries = Math.max(100, others.size * others.size); 
		SET_NEW_POS:
			while (--maxTries >= 0) {
				int x = MathUtils.random(1600);
				int y = MathUtils.random(1600);
				float angle = MathUtils.random(-MathUtils.PI, MathUtils.PI);
				character.body.setTransform(AIEnemy.pixelsToMeters(x), AIEnemy.pixelsToMeters(y), angle);
				for (int i = 0; i < others.size; i++) {
					AIEnemy other = (AIEnemy)others.get(i);
					if (character.getPosition().dst(other.getPosition()) <= character.getBoundingRadius() + other.getBoundingRadius()
							+ minDistanceFromBoundary) continue SET_NEW_POS;
				}
				return;
			}
		throw new GdxRuntimeException("Probable infinite loop detected");
	}
}
