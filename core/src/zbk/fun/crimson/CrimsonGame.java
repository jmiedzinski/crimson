package zbk.fun.crimson;

import zbk.fun.crimson.entity.Player;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class CrimsonGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	Player player;
	OrthographicCamera camera;

	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		player = new Player(80, 87, 1, 6, new Texture(Gdx.files.internal("assets/player.png")), 0.125f);
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 600);
		Gdx.input.setInputProcessor(player);
	}

	@Override
	public void render () {
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		player.update();
		Gdx.gl.glClearColor(0, 0, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		player.render(batch);
		batch.end();
	}
	
	@Override
	public void dispose() {
		batch.dispose();
	}
}
