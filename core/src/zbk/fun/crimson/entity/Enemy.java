package zbk.fun.crimson.entity;

import zbk.fun.crimson.enums.NPCType;
import zbk.fun.crimson.utils.EffectsManager;
import zbk.fun.crimson.utils.WorldUtils;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.ScaledNumericValue;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class Enemy implements Steerable<Vector2> {
	
	NPCType type;
	
	TextureRegion region;
	
	public float life;
	
	public Body body;

	float boundingRadius;
	boolean tagged;

	float maxLinearSpeed;
	float maxLinearAcceleration;
	float maxAngularSpeed;
	float maxAngularAcceleration;

	boolean independentFacing;

	protected SteeringBehavior<Vector2> steeringBehavior;
	
	private static final SteeringAcceleration<Vector2> steeringOutput = new SteeringAcceleration<Vector2>(new Vector2());
	
	public Enemy() {
		
	}
	
	public void init(NPCType type, TextureRegion region, boolean independentFacing) {
		
		this.type = type;
		this.life = type.getLife();
		
		this.region = region;
		this.independentFacing = independentFacing;
		this.tagged = false;
	}
	
	public TextureRegion getRegion () {
		return region;
	}

	public void setRegion (TextureRegion region) {
		this.region = region;
	}

	public Body getBody () {
		return body;
	}

	public void setBody (Body body) {
		this.body = body;
		this.body.setUserData(this);
		this.boundingRadius = body.getFixtureList().get(0).getShape().getRadius();
	}

	public boolean isIndependentFacing () {
		return independentFacing;
	}

	public void setIndependentFacing (boolean independentFacing) {
		this.independentFacing = independentFacing;
	}

	@Override
	public Vector2 getPosition () {
        return body.getPosition();
	}

	@Override
	public float getOrientation () {
		return body.getAngle();
	}

	@Override
	public Vector2 getLinearVelocity () {
		return body.getLinearVelocity();
	}

	@Override
	public float getAngularVelocity () {
		return body.getAngularVelocity();
	}

	@Override
	public float getBoundingRadius () {
		return boundingRadius;
	}

	@Override
	public boolean isTagged () {
		return tagged;
	}

	@Override
	public void setTagged (boolean tagged) {
		this.tagged = tagged;
	}
	
	public float getDamage() {
		return type.getDamage();
	}

	@Override
	public Vector2 newVector () {
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

	public SteeringBehavior<Vector2> getSteeringBehavior () {
		return steeringBehavior;
	}

	public void setSteeringBehavior (SteeringBehavior<Vector2> steeringBehavior) {
		this.steeringBehavior = steeringBehavior;
	}

	public void update (float deltaTime) {
		if (steeringBehavior != null) {
			// Calculate steering acceleration
			steeringBehavior.calculateSteering(steeringOutput);

			/*
			 * Here you might want to add a motor control layer filtering steering accelerations.
			 * 
			 * For instance, a car in a driving game has physical constraints on its movement: it cannot turn while stationary; the
			 * faster it moves, the slower it can turn (without going into a skid); it can brake much more quickly than it can
			 * accelerate; and it only moves in the direction it is facing (ignoring power slides).
			 */
			
			// Apply steering acceleration
			applySteering(steeringOutput, deltaTime);
		}

		wrapAround(WorldUtils.pixelsToMeters(1600), WorldUtils.pixelsToMeters(1600));
	}

	protected void applySteering (SteeringAcceleration<Vector2> steering, float deltaTime) {
		boolean anyAccelerations = false;

		// Update position and linear velocity.
		if (!steeringOutput.linear.isZero()) {
			Vector2 force = steeringOutput.linear.scl(deltaTime);
			body.applyForceToCenter(force, true);
			anyAccelerations = true;
		}

		// Update orientation and angular velocity
		if (isIndependentFacing()) {
			if (steeringOutput.angular != 0) {
				body.applyTorque(steeringOutput.angular * deltaTime, true);
				anyAccelerations = true;
			}
		}
		else {
			// If we haven't got any velocity, then we can do nothing.
			Vector2 linVel = getLinearVelocity();
			if (!linVel.isZero(MathUtils.FLOAT_ROUNDING_ERROR)) {
				float newOrientation = vectorToAngle(linVel);
				body.setAngularVelocity((newOrientation - getAngularVelocity()) * deltaTime); // this is superfluous if independentFacing is always true
				body.setTransform(body.getPosition(), newOrientation);
			}
		}

		if (anyAccelerations) {
			// body.activate();

			// TODO:
			// Looks like truncating speeds here after applying forces doesn't work as expected.
			// We should likely cap speeds form inside an InternalTickCallback, see
			// http://www.bulletphysics.org/mediawiki-1.5.8/index.php/Simulation_Tick_Callbacks

			// Cap the linear speed
			Vector2 velocity = body.getLinearVelocity();
			float currentSpeedSquare = velocity.len2();
			float maxLinearSpeed = getMaxLinearSpeed();
			if (currentSpeedSquare > maxLinearSpeed * maxLinearSpeed) {
				body.setLinearVelocity(velocity.scl(maxLinearSpeed / (float)Math.sqrt(currentSpeedSquare)));
			}

			// Cap the angular speed
			float maxAngVelocity = getMaxAngularSpeed();
			if (body.getAngularVelocity() > maxAngVelocity) {
				body.setAngularVelocity(maxAngVelocity);
			}
		}
	}
	
	public PooledEffect effect(Projectile p) {

		PooledEffect effect = EffectsManager.instance().newBloodEffect();
		effect.setPosition(WorldUtils.m2px(body.getPosition().x)-region.getRegionWidth()/2, WorldUtils.m2px(body.getPosition().y)-region.getRegionHeight()/2);
//		Add a comment to this line
		for (int i = 0; i < effect.getEmitters().size; i++) {                          
			ScaledNumericValue val = effect.getEmitters().get(i).getAngle();           
			float h1 = p.rotation + 90f;                                            
			float h2 = p.rotation - 90f;                                            
			val.setHigh(h1, h2);                                           
			val.setLow(p.rotation);       
		}   
		return effect;

	}

	// the display area is considered to wrap around from top to bottom
	// and from left to right
	protected void wrapAround (float maxX, float maxY) {
		float k = Float.POSITIVE_INFINITY;
		Vector2 pos = body.getPosition();
		
		if (pos.x > maxX) k = pos.x = 0.0f;

		if (pos.x < 0) k = pos.x = maxX;

		if (pos.y < 0) k = pos.y = maxY;

		if (pos.y > maxY) k = pos.y = 0.0f;
		
		if (k != Float.POSITIVE_INFINITY)
			body.setTransform(pos, body.getAngle());
	}

	public void draw (Batch batch) {
		Vector2 pos = body.getPosition();
		float w = region.getRegionWidth();
		float h = region.getRegionHeight();
		float ox = w / 2f;
		float oy = h / 2f;

		batch.draw(region, //
			WorldUtils.metersToPixels(pos.x) - ox, WorldUtils.metersToPixels(pos.y) - oy, //
			ox, oy, //
			w, h, //
			1, 1, //
			body.getAngle() * MathUtils.radiansToDegrees); //
		
	}

	//
	// Limiter implementation
	//

	@Override
	public float getMaxLinearSpeed () {
		return maxLinearSpeed;
	}

	@Override
	public void setMaxLinearSpeed (float maxLinearSpeed) {
		this.maxLinearSpeed = maxLinearSpeed;
	}

	@Override
	public float getMaxLinearAcceleration () {
		return maxLinearAcceleration;
	}

	@Override
	public void setMaxLinearAcceleration (float maxLinearAcceleration) {
		this.maxLinearAcceleration = maxLinearAcceleration;
	}

	@Override
	public float getMaxAngularSpeed () {
		return maxAngularSpeed;
	}

	@Override
	public void setMaxAngularSpeed (float maxAngularSpeed) {
		this.maxAngularSpeed = maxAngularSpeed;
	}

	@Override
	public float getMaxAngularAcceleration () {
		return maxAngularAcceleration;
	}

	@Override
	public void setMaxAngularAcceleration (float maxAngularAcceleration) {
		this.maxAngularAcceleration = maxAngularAcceleration;
	}
	
}
