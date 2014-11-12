package zbk.fun.crimson;

import java.util.ArrayList;
import java.util.List;

import zbk.fun.crimson.entity.Enemy;
import zbk.fun.crimson.entity.Player;
import zbk.fun.crimson.entity.Projectile;
import zbk.fun.crimson.entity.Weapon;
import zbk.fun.crimson.enums.WeaponType;
import zbk.fun.crimson.utils.EffectsManager;
import zbk.fun.crimson.utils.GameObjectManager;
import zbk.fun.crimson.utils.MarksManager;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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

	List<Enemy> enemies;
	List<Enemy> deadEnemies;

	List<Weapon> weapons;

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



		enemies = new ArrayList<Enemy>();
		for (int i=0; i<50; i++) {
			Enemy e = new Enemy();
			enemies.add(e);
		}
		
		deadEnemies = new ArrayList<Enemy>();

		weapons = new ArrayList<Weapon>();
		Weapon pistol = new Weapon(WeaponType.PISTOL);
		weapons.add(pistol);
		Weapon shotgun = new Weapon(WeaponType.SHOTGUN);
		weapons.add(shotgun);
		Weapon machinegun = new Weapon(WeaponType.MACHINE_GUN);
		weapons.add(machinegun);

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
		for (Enemy e : enemies) {
			if (e.life > 0f) {
				e.update(player, enemies);
				e.render(batch);
			} else {
				deadEnemies.add(e);
			}
		}
		enemies.removeAll(deadEnemies);
		deadEnemies.clear();

		GameObjectManager.instance().renderBullets(batch, enemies);
		
		GameObjectManager.instance().renderExplosives(batch, enemies);
		
		// Update and draw effects:
		EffectsManager.instance().renderEffects(batch);
		
		renderHUD(batch);
		batch.end();

		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.WHITE);

		for (Enemy e : enemies) {
			e.postRender(shapeRenderer);
		}

		for (Projectile p : GameObjectManager.instance().getBullets()) {
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
		font.draw(batch, "ENM: " + enemies.size(), screen.x, screen.y - (lines*15)); 	lines++;
		font.draw(batch, "PRJ: " + GameObjectManager.instance().getBullets().size(), screen.x, screen.y - (lines*15)); 	lines++;
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
}
