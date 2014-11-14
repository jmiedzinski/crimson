package zbk.fun.crimson.entity;

import java.util.LinkedList;
import java.util.List;

import zbk.fun.crimson.enums.ExplosiveType;
import zbk.fun.crimson.utils.GameObjectsManager;
import zbk.fun.crimson.utils.WorldUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;

public class Player implements InputProcessor, Steerable<Vector2> {

	public static final int weaponSlots = 3;

	OrthographicCamera camera;

	private Sprite sprite;

	private int rows;
	private int cols;

	private int width;
	private int height;

	private Vector2 mouse;
	public Vector2 position;
	public Vector2 direction;
	public Vector2 target;
	public float rotation;

	private TextureRegion[] frames;
	private Animation animation;

	private float time;
	private float animSpeed;
	private float walkSpeed;
	private float distance;

	private boolean moving;

	private BitmapFont font;

	private Rectangle bbox;

	public List<Weapon> weapons;

	public Weapon currentWeapon;
	public int currWeaponIndex = -1;
	
	public Body body;

	private float maxLinerSpeed;

	private float maxLinearAcceleration;

	private float maxAngularSpeed;

	private float maxAngularAcceleration;

	private float boundingRadius;
	
	private boolean tagged;

	public Player(OrthographicCamera camera, int width, int height, int rows, int cols, Texture texture, float animSpeed) {

		this.camera = camera;

		this.width = width;
		this.height = height;
		this.rows = rows;
		this.cols = cols;
		this.animSpeed = animSpeed;
		this.walkSpeed = 1.5f;
		this.distance = 0f;

		this.frames = new TextureRegion[rows * cols];
		TextureRegion[][] tmp = TextureRegion.split(texture, width, height);

		for (int r=0; r<this.rows; r++)
			for (int c=0; c<this.cols; c++)
				this.frames[r * this.rows + c] = tmp[r][c];

		this.animation = new Animation(this.animSpeed, this.frames);
		this.position = new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		this.direction = new Vector2(1, 0);
		this.mouse = new Vector2(0.0f, 0.0f);
		this.target = new Vector2(0.0f, 0.0f);

		this.sprite = new Sprite(this.frames[0]);
		this.sprite.setRotation(0.0f);
		this.sprite.setBounds(this.position.x, this.position.y, this.width, this.height);
		this.sprite.setCenter(position.x, position.y);
		this.sprite.setOriginCenter();

		this.font = new BitmapFont();
		font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
		font.setScale(1.0f);

		moving = false;
		bbox = new Rectangle(position.x-25, position.y-25, 50, 50);
		weapons = new LinkedList<Weapon>();
		
		this.boundingRadius = WorldUtils.px2m((width + height) / 2);
		this.tagged = false;
	}

	public void update() {

		this.time += Gdx.graphics.getDeltaTime();
		if (moving)
			this.sprite.setRegion(this.animation.getKeyFrame(time, moving));
		else
			this.sprite.setRegion(this.frames[0]);

		if (distance > 20f) {
			position.x += walkSpeed * Math.cos(MathUtils.degreesToRadians * direction.angle());
			position.y += walkSpeed * Math.sin(MathUtils.degreesToRadians * direction.angle());
			this.sprite.setCenter(this.position.x, this.position.y);
			this.sprite.setOriginCenter();
			this.distance = (float) Math.sqrt(Math.pow(target.x - position.x, 2) + Math.pow(target.y - position.y, 2));
		} else {
			moving = false;
		}
		bbox.set(position.x-25, position.y-25, 50, 50);
		body.setTransform(WorldUtils.px2m((int) position.x), WorldUtils.px2m((int) position.y), rotation);

	}

	public void render(SpriteBatch batch) {

		font.draw(batch, Float.toString(Math.round(this.distance)), target.x, target.y);
		sprite.draw(batch);

	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		if (button == Input.Buttons.RIGHT) {

			Vector3 click = new Vector3(screenX, screenY, 0f);
			camera.unproject(click);

			this.target.x = click.x;
			this.target.y = click.y;

			this.direction = target.cpy().sub(position).nor();
			this.rotation = direction.angle();

			this.sprite.setRotation(rotation);

			this.distance = (float) Math.sqrt(Math.pow(target.x - position.x, 2) + Math.pow(target.y - position.y, 2));
			if (distance > 20f)
				moving = true;

		} else if (button == Input.Buttons.LEFT) {

			Vector3 projTarget = new Vector3(screenX, screenY, 0f);
			camera.unproject(projTarget);

			if (currentWeapon != null) {
				currentWeapon.fire(position.cpy(), new Vector2(projTarget.x, projTarget.y));
			}
		}
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {

		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {

		if (!moving) {
			Vector3 click = new Vector3(screenX, screenY, 0f);
			camera.unproject(click);

			this.target.x = click.x;
			this.target.y = click.y;

			this.direction = target.cpy().sub(position).nor();
			this.rotation = direction.angle();

			this.sprite.setRotation(rotation);
		}

		return false;
	}

	@Override
	public boolean keyDown(int keycode) {

		if (keycode == Keys.Q)
			cycleWeapon();
		if (keycode == Keys.R) {
			if (currentWeapon != null)
				currentWeapon.reload();
		}
		if (keycode == Keys.E) {
			Explosive e = GameObjectsManager.instance().getExplosive();
			e.init(ExplosiveType.C4, position.cpy());
		}

		return false;
	}

	@Override
	public boolean keyUp(int keycode) { return false; }

	@Override
	public boolean keyTyped(char character) { return false; }

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

	@Override
	public boolean scrolled(int amount) { return false; }

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Vector2 getMouse() {
		return mouse;
	}

	public Vector2 getPosition() {
		return body.getPosition();
	}

	public Vector2 getDirection() {
		return direction;
	}

	public Vector2 getTarget() {
		return target;
	}

	public Sprite getSprite() {
		return sprite;
	}

	public boolean isMoving() {
		return moving;
	}

	public void setCamera(OrthographicCamera camera) {
		this.camera = camera;
	}

	public void stop() {
		this.distance = 0f;
	}

	public void stepBack() {
		position.x -= walkSpeed * Math.cos(MathUtils.degreesToRadians * direction.angle());
		position.y -= walkSpeed * Math.sin(MathUtils.degreesToRadians * direction.angle());
		this.sprite.setCenter(this.position.x, this.position.y);
		this.sprite.setOriginCenter();
	}

	public Rectangle getBbox() {
		return bbox;
	}

	public void pickup(Pickable pickable) {

		if (pickable instanceof Weapon) {
			Weapon weapon = (Weapon) pickable;
			weapons.add(weapon);
			if (currWeaponIndex == -1) {
				currWeaponIndex++;
				currentWeapon = weapons.get(currWeaponIndex);
			}
		}
	}

	public void cycleWeapon() {

		if (!weapons.isEmpty()) {
			if (currWeaponIndex < weapons.size() - 1)
				currWeaponIndex++;
			else {
				currWeaponIndex = 0;
			}
			currentWeapon = weapons.get(currWeaponIndex);
		}

	}

	@Override
	public float getMaxLinearSpeed() {
		return maxLinerSpeed;
	}

	@Override
	public void setMaxLinearSpeed(float maxLinearSpeed) {
		this.maxLinerSpeed = maxLinearSpeed;
	}

	@Override
	public float getMaxLinearAcceleration() {
		return maxLinearAcceleration;
	}

	@Override
	public void setMaxLinearAcceleration(float maxLinearAcceleration) {
		this.maxLinearAcceleration = maxLinearAcceleration;
	}

	@Override
	public float getMaxAngularSpeed() {
		return maxAngularSpeed;
	}

	@Override
	public void setMaxAngularSpeed(float maxAngularSpeed) {
		this.maxAngularSpeed = maxAngularSpeed;
	}

	@Override
	public float getMaxAngularAcceleration() {
		return maxAngularAcceleration;
	}

	@Override
	public void setMaxAngularAcceleration(float maxAngularAcceleration) {
		this.maxAngularAcceleration = maxAngularAcceleration;
	}

	@Override
	public Vector2 getLinearVelocity() {
		return body.getLinearVelocity();
	}

	@Override
	public float getAngularVelocity() {
		return body.getAngularVelocity();
	}

	@Override
	public float getBoundingRadius() {
		return boundingRadius;
	}

	@Override
	public boolean isTagged() {
		return tagged;
	}

	@Override
	public void setTagged(boolean tagged) {
		this.tagged = tagged;
	}

	@Override
	public Vector2 newVector() {
		return new Vector2();
	}

	@Override
	public float vectorToAngle (Vector2 vector) {
		return (float)Math.atan2(-vector.x, vector.y);
	}

	@Override
	public Vector2 angleToVector (Vector2 outVector, float angle) {
		outVector.x = -(float)Math.sin(angle);
		outVector.y = (float)Math.cos(angle);
		return outVector;
	}

	@Override
	public float getOrientation() {
		return body.getAngle();
	}


}
