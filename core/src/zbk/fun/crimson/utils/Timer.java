package zbk.fun.crimson.utils;

public class Timer {

	private float timeToLive;
	
	private float currentTime;
	
	private TimerAware owner;
	
	private boolean active;
	
	public Timer(float ttl, TimerAware owner) {
		this.timeToLive = ttl;
		this.currentTime = ttl;
		this.owner = owner;
		this.active = false;
	}
	
	public void update(float deltaTime) {

		if (active) {
			if (currentTime <= 0f) {
				owner.timeExceeded(this);
				active = false;
			} else {
				currentTime =- deltaTime;
			}
		}	
	}
	
	public void start() {
		active = true;
	}
	
	public void stop() {
		active = false;
	}
	
	public void reset() {
		currentTime = timeToLive;
	}
	
	public float currentTime() {
		return currentTime;
	}
	
	public boolean isActive() {
		return active;
	}
}
