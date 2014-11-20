package zbk.fun.crimson;

import java.util.ArrayList;

import zbk.fun.crimson.entity.Player;
import zbk.fun.crimson.enums.LightSourceType;
import zbk.fun.crimson.enums.WeaponType;
import zbk.fun.crimson.utils.CollisionListener;
import zbk.fun.crimson.utils.EffectsManager;
import zbk.fun.crimson.utils.GameObjectsManager;
import zbk.fun.crimson.utils.LightsManager;
import zbk.fun.crimson.utils.MarksManager;
import zbk.fun.crimson.utils.NPCManager;
import zbk.fun.crimson.utils.WorldUtils;
import box2dLight.ConeLight;
import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class CrimsonGame extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	BitmapFont font;
	Player player;
	OrthographicCamera camera;
	TiledMap tiledMap;
	TiledMapRenderer tiledMapRenderer;
	InputMultiplexer inputMultiplexer;

	int mapSize;
	int tileSize;
	Rectangle mapBounds;
	Rectangle camBounds;

	float camMaxLeft, camMaxRight, camMaxTop, camMaxBottom;
	Matrix4 normalProjection = new Matrix4();

	World world;
	Body groundBody;

	RayHandler rayHandler;
	ArrayList<Light> lights = new ArrayList<Light>();

	Box2DDebugRenderer debugRenderer;
	Matrix4 debugMatrix;

	boolean box2dDebug = false;
	boolean lightsEnabled = true;

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
		normalProjection.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		this.world = new World(new Vector2(0, 0), true);

		WorldUtils.createWorld(world, groundBody);
		world.setContactListener(new CollisionListener(player));
		GameObjectsManager.instance().setWorld(world);

		WorldUtils.createPlayerBody(world, player);
		NPCManager.instance().populateEnemies(world, player, 50);

		GameObjectsManager.instance().newWeapon(WeaponType.PISTOL);
		GameObjectsManager.instance().newWeapon(WeaponType.SHOTGUN);
		GameObjectsManager.instance().newWeapon(WeaponType.MACHINE_GUN);

		/** BOX2D LIGHT STUFF BEGIN */
		RayHandler.setGammaCorrection(true);
		RayHandler.useDiffuseLight(true);

		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0.5f, 0.5f, 0.5f, 0.5f);
		rayHandler.setBlurNum(3);
		rayHandler.setShadows(true);

		LightsManager.instance().setRayHandler(rayHandler);
		LightsManager.instance().newLight(LightSourceType.LIGHTSTICK, new Vector2(100f, 100f));

		//		PointLight light = new PointLight(rayHandler, 128, Color.WHITE, 300f, player.getPosition().x, player.getPosition().y);
		//		lights.add(light);
		ConeLight coneLight = new ConeLight(rayHandler, 128, Color.WHITE, 400f, player.getPosition().x, player.getPosition().y, player.getOrientation(), 30f);
		lights.add(coneLight);
		/** BOX2D LIGHT STUFF END */

		debugRenderer = new Box2DDebugRenderer();
		debugMatrix = new Matrix4(camera.combined);
		debugMatrix.scale(50f, 50f, 1f);

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
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		tiledMapRenderer.setView(camera);
		tiledMapRenderer.render();
		batch.begin();

		MarksManager.instance().renderMarks(batch, deltaTime);

		player.render(batch, deltaTime);

		GameObjectsManager.instance().renderWeapons(batch, deltaTime);

		NPCManager.instance().renderEnemies(batch, deltaTime);

		GameObjectsManager.instance().renderBullets(batch, deltaTime);

		GameObjectsManager.instance().renderExplosives(batch, deltaTime);

		EffectsManager.instance().renderEffects(batch, deltaTime);

		renderHUD(batch);
		batch.end();

		/** BOX2D LIGHT STUFF BEGIN */
		rayHandler.setCombinedMatrix(camera);
		lights.get(0).setPosition(WorldUtils.m2px(player.getPosition().x), WorldUtils.m2px(player.getPosition().y));
		lights.get(0).setDirection(player.getOrientation());
		rayHandler.updateAndRender();

		if (lightsEnabled) {
			LightsManager.instance().render(deltaTime);
		}

		/** BOX2D LIGHT STUFF END */

		moveCamera();

		batch.setProjectionMatrix(normalProjection);
		batch.begin();

		int lines = 0;
		font.draw(batch, "TRG: " + MathUtils.round(player.getTarget().x) + ":" + MathUtils.round(player.getTarget().y), 10, 600 - (lines*15)); 	lines++;
		font.draw(batch, "POS: " + WorldUtils.m2px(player.body.getPosition().x) + ":" + WorldUtils.m2px(player.body.getPosition().y), 10, 600 - (lines*15)); 	lines++;
		font.draw(batch, "CAM: " + MathUtils.round(camera.position.x) + ":" + MathUtils.round(camera.position.y), 10, 600 - (lines*15)); 	lines++;
		font.draw(batch, "ENM: " + NPCManager.instance().getEnemies().size, 10, 600 - (lines*15)); 	lines++;
		font.draw(batch, "PRJ: " + GameObjectsManager.instance().getBullets().size(), 10, 600 - (lines*15)); 	lines++;
		font.draw(batch, "BLM: " + MarksManager.instance().getMarks().size(), 10, 600 - (lines*15)); 	lines++;
		font.draw(batch, "LGT: " + lights.get(0).getPosition().toString(), 10, 600 - (lines*15)); 	lines++;
		font.draw(batch, "LIF: " + Float.toString(player.life), 10, 600 - (lines*15)); 	lines++;

		batch.end();

		if (box2dDebug) {
			debugMatrix.set(camera.combined);
			debugMatrix.scale(50f, 50f, 1f);
			debugRenderer.render(world, debugMatrix);
		}

	}

	public void renderHUD(SpriteBatch batch) {

		if (player.currentWeapon != null) {
			Vector3 screen = new Vector3(Gdx.graphics.getWidth() - player.currentWeapon.texture.getWidth() - 10, 10 + player.currentWeapon.texture.getHeight(), 0);
			camera.unproject(screen);
			player.currentWeapon.sprite.setPosition(screen.x, screen.y);
			player.currentWeapon.sprite.draw(batch);
			screen.set(Gdx.graphics.getWidth() - player.currentWeapon.texture.getWidth() - 20, 10 + player.currentWeapon.texture.getHeight(), 0);
			camera.unproject(screen);
			font.draw(batch, Integer.toString(player.currentWeapon.clip), screen.x, screen.y);
		}
	}

	@Override
	public void dispose() {
		batch.dispose();
		world.dispose();
		rayHandler.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		if (keycode == Keys.F1)
			box2dDebug = !box2dDebug;
		if (keycode == Keys.F2) {
			lightsEnabled = !lightsEnabled;
			rayHandler.setShadows(lightsEnabled);
		}
		

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


}
