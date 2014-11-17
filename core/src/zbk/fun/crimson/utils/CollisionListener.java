package zbk.fun.crimson.utils;

import zbk.fun.crimson.entity.Enemy;
import zbk.fun.crimson.entity.Projectile;
import zbk.fun.crimson.enums.SurfacemarkType;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class CollisionListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		
		Projectile bullet = null;
		Enemy enemy = null;

		Object a = contact.getFixtureA().getBody().getUserData();
		Object b = contact.getFixtureB().getBody().getUserData();
		
		if (a instanceof Projectile && b instanceof Enemy) {
			bullet = (Projectile) a;
			enemy = (Enemy) b;
		} else if (b instanceof Projectile && a instanceof Enemy) {
			bullet = (Projectile) b;
			enemy = (Enemy) a;
		}
		
		if (enemy != null && bullet != null) {
			
			Vector2 enemyPos = new Vector2(WorldUtils.m2px(enemy.body.getPosition().x), WorldUtils.m2px(enemy.body.getPosition().y));
			
			enemy.life -= bullet.damage;
			bullet.active = false;
			EffectsManager.instance().getEffects().add(enemy.effect(bullet));
			MarksManager.instance().getMark().init(SurfacemarkType.BLOODMARK, enemyPos, MathUtils.random(360f));
			System.out.println("Enemy was hit by bullet");
		}
		
//		System.out.println(a.getClass().getSimpleName() + " collides with " + b.getClass().getSimpleName());
		
	}

	@Override
	public void endContact(Contact contact) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}



}
