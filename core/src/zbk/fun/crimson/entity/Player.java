package zbk.fun.crimson.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Player implements InputProcessor {

	private Sprite sprite;

	private int rows;
	private int cols;

	private int width;
	private int height;

	private Vector2 mouse;
	private Vector2 position;
	private Vector2 direction;
	private Vector2 origin;
	private Vector2 target;
	private float rotation;

	private TextureRegion[] frames;
	private Animation animation;

	private float time;
	private float animSpeed;
	private float walkSpeed;
	private float distance;
	
	private boolean moving;

	private BitmapFont font;

	public Player(int width, int height, int rows, int cols, Texture texture, float animSpeed) {

		this.width = width;
		this.height = height;
		this.rows = rows;
		this.cols = cols;
		this.animSpeed = animSpeed;
		this.walkSpeed = 1f;
		this.distance = 0f;

		this.frames = new TextureRegion[rows * cols];
		TextureRegion[][] tmp = TextureRegion.split(texture, width, height);

		for (int r=0; r<this.rows; r++)
			for (int c=0; c<this.cols; c++)
				this.frames[r * this.rows + c] = tmp[r][c];

		this.animation = new Animation(this.animSpeed, this.frames);
		this.position = new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		this.direction = this.position.cpy();
		this.mouse = new Vector2(0.0f, 0.0f);
		this.target = new Vector2(0.0f, 0.0f);
		this.origin = new Vector2(Gdx.graphics.getWidth() / 2 + (this.width / 2), Gdx.graphics.getHeight() / 2 + (this.height / 2));

		this.sprite = new Sprite(this.frames[0]);
		this.sprite.setPosition(this.position.x, this.position.y);
		this.sprite.setRotation(0.0f);
		this.sprite.setBounds(this.position.x, this.position.y, this.width, this.height);
		this.sprite.setCenter(this.origin.x, this.origin.y);
		this.sprite.setOriginCenter();

		this.font = new BitmapFont();
		font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		font.setScale(1.0f);
		
		moving = false;
	}

	public void update() {

		this.time += Gdx.graphics.getDeltaTime();
		if (moving)
			this.sprite.setRegion(this.animation.getKeyFrame(time, moving));
		else
			this.sprite.setRegion(this.frames[0]);
		
		if (distance > 1f) {
			position.x += walkSpeed * Math.cos(MathUtils.degreesToRadians * direction.angle());
			position.y += walkSpeed * Math.sin(MathUtils.degreesToRadians * direction.angle());
			this.sprite.setPosition(this.position.x, this.position.y);
			this.sprite.setOriginCenter();
			this.distance = (float) Math.sqrt(Math.pow(target.x - position.x+width/2, 2) + Math.pow(target.y - position.y+height/2, 2));
		} else {
			moving = false;
		}

	}

	public void render(SpriteBatch batch) {

		int lines = 1;
		font.draw(batch, "rotation: " + this.rotation, 10, 600 - lines * 15); 	lines++;
		font.draw(batch, "direction: " + this.direction, 10, 600 - lines * 15); lines++;
		font.draw(batch, "position: " + this.position, 10, 600 - lines * 15); 	lines++;
		font.draw(batch, "distance: " + this.distance, 10, 600 - lines * 15); 	lines++;
		sprite.draw(batch);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		
		this.mouse.x = screenX;
		this.mouse.y = Gdx.graphics.getHeight() - screenY;
		
		this.direction = mouse.sub(origin).nor().cpy();
		this.rotation = direction.angle();

		this.origin.x = this.position.x + (width / 2);
		this.origin.y = this.position.y + (height / 2);

		this.sprite.setRotation(rotation);
		
		this.target.x = screenX;
		this.target.y = screenY;
		this.distance = (float) Math.sqrt(Math.pow(target.x - position.x+width/2, 2) + Math.pow(target.y - position.y+height/2, 2));
		moving = true;
		
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
//		position.x += walkSpeed * Math.cos(direction.angle());
//		position.y += walkSpeed * Math.sin(direction.angle());
//		this.sprite.setPosition(this.position.x, this.position.y);
//		this.sprite.setOriginCenter();
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {

		this.mouse.x = screenX;
		this.mouse.y = Gdx.graphics.getHeight() - screenY;

//		this.direction = mouse.sub(origin).nor();
//		this.rotation = direction.angle();
//
//		this.origin.x = this.position.x + (width / 2);
//		this.origin.y = this.position.y + (height / 2);
//
//		this.sprite.setRotation(rotation);
		return false;
	}
	
	@Override
	public boolean keyDown(int keycode) { return false;	}

	@Override
	public boolean keyUp(int keycode) { return false; }

	@Override
	public boolean keyTyped(char character) { return false; }
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

	@Override
	public boolean scrolled(int amount) { return false; }
}
