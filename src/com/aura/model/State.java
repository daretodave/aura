package com.aura.model;


public class State {


	@Override
	public String toString() {
		return "State [tx=" + tx + ", ty=" + ty + ", opacity=" + opacity
				+ ", rotation=" + rotation + ", scale=" + scale + ", flags="
				+ flags + ", element=" + element + "]";
	}

	public void setScale(float scale) {
		setScaleX(scale);
		setScaleY(scale);
	}

	public void setScale(PointedValue value) {
		this.scale = value;
	}

	public void setScaleX(float scalex) {
		this.scale.setValueX(scalex);
	}

	public void setScaleY(float scaley) {
		this.scale.setValueY(scaley);
	}

	public float getScaleXRatio() {
		return this.scale.getX();
	}

	public float getScaleYRatio() {
		return this.scale.getY();
	}

	public float getRotationXRatio() {
		return this.rotation.getX();
	}

	public float getRotationYRatio() {
		return this.rotation.getY();
	}

	public float getScaleX() {
		return scale.getValueX();
	}

	public float getScaleY() {
		return scale.getValueY();
	}

	public void setScaleLocation(float xratio, float yratio) {
		scale.setX(xratio);
		scale.setY(yratio);
	}

	public void setRotationLocation(float xratio, float yratio) {
		rotation.setX(xratio);
		rotation.setY(yratio);
	}

	public float getTx() {
		return tx;
	}

	public void setTx(float tx) {
		this.tx = tx;
	}

	public float getTy() {
		return ty;
	}

	public void setTy(float ty) {
		this.ty = ty;
	}

	public float getRotationX() {
		return rotation.getValueX();
	}

	public float getRotationY() {
		return rotation.getValueY();
	}

	public void setRotationX(float rotation) {
		this.rotation.setValueX(rotation);
	}

	public void setRotationY(float rotation) {
		this.rotation.setValueY(rotation);
	}

	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public State copy(Element parent) {
		State state = new State(parent);

		state.tx = tx;
		state.ty = ty;

		state.opacity = opacity;

		state.rotation = rotation.copy();
		state.scale    = scale.copy();

		return state;
	}

	private float tx;
	private float ty;
	private float opacity;

	private PointedValue rotation;
	private PointedValue scale;

	private int flags;

	public static final int FOCUSED = 1;
	public static final int HOVERED = 2;
	public static final int PRESSED = 4;

	private Element element;

	public void toState(int state, boolean inState) {
		if(inState) {
			if ((flags & state) != state) {
				flags |= state;
				if(element != null)
					element.state(state, true);
			}
		} else {
			if ((flags & state) == state) {
				flags &= ~state;
				if(element != null)
					element.state(state, false);
			}
		}
	}

	public boolean is(int state) {
		return (flags & state) == state;
	}

	public State(Element element) {
		opacity  = 1L;
		scale    = new PointedValue(1F, 1F);
		rotation = new PointedValue(0F, 0F);
		this.element = element;
	}

	public State() {
		this(null);
	}


	public static class PointedValue {

		@Override
		public String toString() {
			return "PointedValue [valuex=" + valuex + ", valuey=" + valuey
					+ ", x=" + x + ", y=" + y + "]";
		}
		public float getValueX() {
			return valuex;
		}
		public PointedValue copy() {
			return new PointedValue(valuex, valuey, x, y);
		}
		public void setValue(float value) {
			valuex = value;
			valuey = value;
		}
		public void setValueX(float value) {
			this.valuex = value;
		}
		public float getValueY() {
			return valuey;
		}
		public void setValueY(float value) {
			this.valuey = value;
		}
		public float getX() {
			return x;
		}
		public void setX(float x) {
			this.x = x;
		}
		public float getY() {
			return y;
		}
		public void setY(float y) {
			this.y = y;
		}
		public PointedValue(float valueX, float valueY, float x, float y) {
			this.valuex = valueX;
			this.valuey = valueY;
			this.x = x;
			this.y = y;
		}
		public PointedValue(float valueX, float valueY) {
			this(valueX, valueY, .5F, .5F);
		}

		private float valuex;
		private float valuey;
		private float x;
		private float y;

	}

	public void reset() {
		opacity  = 1L;
		scale    = new PointedValue(1F, 1F);
		rotation = new PointedValue(0F, 0F);
	}



}
