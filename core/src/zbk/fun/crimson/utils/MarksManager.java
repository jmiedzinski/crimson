package zbk.fun.crimson.utils;

import java.util.ArrayList;
import java.util.List;

import zbk.fun.crimson.entity.Surfacemark;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;

public class MarksManager {

	private static MarksManager instance;
	
	private Pool<Surfacemark> markPool;
	private List<Surfacemark> marks;
	private List<Surfacemark> marksToRemove;
	
	private MarksManager() {
		
		this.markPool = Pools.get(Surfacemark.class);
		this.marks = new ArrayList<Surfacemark>();
		this.marksToRemove = new ArrayList<Surfacemark>();
	}
	
	public static MarksManager instance() {
		
		if (instance == null)
			instance = new MarksManager();
		return instance;
	}

	public Pool<Surfacemark> getMarkPool() {
		return markPool;
	}

	public List<Surfacemark> getMarks() {
		return marks;
	}

	public List<Surfacemark> getMarksToRemove() {
		return marksToRemove;
	}
	
	public void clearMarks() {
		
		for (Surfacemark m : marksToRemove)
			markPool.free(m);
		marks.removeAll(marksToRemove);
		marksToRemove.clear();
	}
	
	public Surfacemark getMark() {
		Surfacemark mark = markPool.obtain();
		marks.add(mark);
		return mark;
	}
	
	public void renderMarks(SpriteBatch batch) {
		
		for (Surfacemark mark : marks) {
			if (mark.active)
				mark.render(batch);
			else
				marksToRemove.add(mark);
		}
		clearMarks();
	}
}
