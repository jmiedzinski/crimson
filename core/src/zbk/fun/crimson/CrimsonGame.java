package zbk.fun.crimson;

import zbk.fun.crimson.entity.Player;

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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class CrimsonGame extends ApplicationAdapter implements InputProcessor {
	SpriteBatch batch;
	Texture img;
	BitmapFont font;
	Player player;
	OrthographicCamera camera;
	private ShapeRenderer shapeRenderer;
	TiledMap tiledMap;
	TiledMapRenderer tiledMapRenderer;
	InputMultiplexer inputMultiplexer;

	@Override
	public void create () {
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 600);
		player = new Player(camera, 80, 87, 1, 6, new Texture(Gdx.files.internal("assets/player.png")), 0.125f);
		shapeRenderer = new ShapeRenderer();
		
		tiledMap = new TmxMapLoader().load("assets/arena.tmx");
		tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
		
		inputMultiplexer = new InputMultiplexer();
		inputMultiplexer.addProcessor(player);
		inputMultiplexer.addProcessor(this);
		Gdx.input.setInputProcessor(inputMultiplexer);
		
		font = new BitmapFont();
		font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		font.setScale(1.0f);
	}

	@Override
	public void render () {
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		player.update();
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        tiledMapRenderer.setView(camera);
        tiledMapRenderer.render();
		batch.begin();
		player.render(batch);
		
		batch.end();
		
		shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
		shapeRenderer.begin(ShapeType.Line);
		shapeRenderer.setColor(Color.WHITE);
		shapeRenderer.rect(player.getSprite().getX(), player.getSprite().getY(), player.getWidth(), player.getHeight());
		shapeRenderer.setColor(Color.RED);
		shapeRenderer.circle(player.getTarget().x, player.getTarget().y, 10);
		shapeRenderer.setColor(Color.YELLOW);
		shapeRenderer.line(player.getPosition().x, player.getPosition().y, player.getTarget().x, player.getTarget().y);
		shapeRenderer.end();
		
		if (player.isMoving())
			camera.translate(1.5f * player.getDirection().x, 1.5f * player.getDirection().y);

		batch.begin();
		Vector3 screen = new Vector3(10, 10, 0);
		camera.unproject(screen);
		
		int lines = 0;
		font.draw(batch, "TRG: " + MathUtils.round(player.getTarget().x) + ":" + MathUtils.round(player.getTarget().y), screen.x, screen.y - (lines*15)); 	lines++;
		font.draw(batch, "POS: " + MathUtils.round(player.getPosition().x) + ":" + MathUtils.round(player.getPosition().y), screen.x, screen.y - (lines*15)); 	lines++;
		font.draw(batch, "CAM: " + MathUtils.round(camera.position.x) + ":" + MathUtils.round(camera.position.y), screen.x, screen.y - (lines*15)); 	lines++;
		batch.end();
		
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
		// TODO Auto-generated method stub
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
