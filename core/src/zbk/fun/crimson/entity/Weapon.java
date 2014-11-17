package zbk.fun.crimson.entity;

import zbk.fun.crimson.enums.WeaponType;
import zbk.fun.crimson.utils.GameObjectsManager;
import zbk.fun.crimson.utils.WorldUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Weapon implements Pickable {
	
	boolean attached;

	WeaponType type;

	Texture texture;
	public Sprite sprite;

	Vector2 position;

	public int width;
	public int height;

	public int clip;

	Rectangle bbox;

	public Weapon(WeaponType weaponType) {
		type = weaponType;
		attached = false;
		texture = new Texture(Gdx.files.internal("assets/" + weaponType.name().toLowerCase() + ".png"));
		width = texture.getWidth();
		height = texture.getHeight();
		sprite = new Sprite(texture);
		position = new Vector2(MathUtils.random(1600f), MathUtils.random(1600f));
		sprite.setPosition(position.x, position.y);
		sprite.setOriginCenter();
		bbox = new Rectangle(position.x-width/2, position.y-height/2, width, height);
		clip = type.getClipSize();
	}

	public void update(Player player) {
		if (!attached && player.getBbox().overlaps(bbox)) {
			attached = true;
			player.pickup(this);
		}
	}

	public void render(SpriteBatch batch) {
		if (!attached)
			sprite.draw(batch);
	}

	public void fire(Vector2 position, Vector2 target) {

		if (clip > 0) {
			Projectile p = GameObjectsManager.instance().newBullet(position, target);
			p.speed = type.getSpeed();
			p.damage= type.getDamage();
			p.maxDistance = type.getRange();
			clip--;
		} 
	}
	
	public void reload() {
		clip = type.getClipSize();
	}
}
