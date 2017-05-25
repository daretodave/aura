package com.aura.model;


public abstract class Entity extends Element {

	public static final String BOUNDS_KEY = "BOUNDS";

	public abstract void onBoundChange();

	protected float lrw = -1; //last recorded width
	protected float lrh = -1;

	protected Location location;
	protected Bounds bounds;
	private OnBoundsChangeListener onBoundsChangeListener;

	public Entity(Bounds bounds) {
		super();
		this.bounds = bounds;
		if(this.bounds != null) {
			lrw = getWidthOnScreen();
			lrh = getHeightOnScreen();
		}
		locate(0, 0);
	}

	public abstract void render(float width, float height);

	@Override
	public void display() {
		float w = getWidthOnScreen();
		float h = getHeightOnScreen();
		render(w, h);
	}

	@Override
	public float width() {
		return bounds == null ? 0 : bounds.width();
	}

	@Override
	public float height() {
		return bounds == null ? 0 : bounds.height();
	}

	@Override
	public float x() {
		return location.x() + state.getTx();
	}

	@Override
	public float y() {
		return location.y() + state.getTy();
	}

	@Override
	public void locate(Location location) {
		if(locked) {
			throw new RuntimeException("This element is locked to another element and can not be moved.");
		}
		this.location = location;
	}

	@Override
	public void logic() {
		float w = getWidthOnScreen();
		float h = getHeightOnScreen();
		if(lrh != h || lrw != w) {
			lrh = h;
			lrw = w;
			onBoundChange();
			if(onBoundsChangeListener != null) {
				onBoundsChangeListener.onBoundsChange();
			}
		}
	}

	public interface OnBoundsChangeListener {
		public void onBoundsChange();
	}

	@Override
	public void bound(Bounds bound) {
		if(bound == null) {
			return;
		}
		Bounds prior = this.bounds;
		this.bounds = bound;
		onChange(null, BOUNDS_KEY, prior, bounds);
		refresh();
	}
	@Override
	public Bounds getBounds() {
		return bounds;
	}

	@Override
	public float getXOnScreen() {
		float value = location.getXOnScreen() + state.getTx();
		value += margin(Entity.MARGIN_LEFT);
		return value;
	}

	@Override
	public float getYOnScreen() {
		float value = location.getYOnScreen() + state.getTy();
		value += margin(Entity.MARGIN_TOP);
		return value;
	}

	public OnBoundsChangeListener getOnBoundsChangeListener() {
		return onBoundsChangeListener;
	}

	public void setOnBoundsChangeListener(OnBoundsChangeListener onBoundsChangeListener) {
		this.onBoundsChangeListener = onBoundsChangeListener;
	}


}
