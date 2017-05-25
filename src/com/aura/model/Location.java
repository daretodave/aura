package com.aura.model;


public abstract class Location {

	private Value offsetx = new Value.StaticValue(0F);
	private Value offsety = new Value.StaticValue(0F);

	private float alpha;
	private float beta;

	public void translate(float x, float y) {
		this.alpha += x;
		this.beta  += y;
	}

	public float getAlpha() {
		return alpha;
	}

	public float getBeta() {
		return beta;
	}

	public enum Hook {

		NW(0F, 0F),
		CW (0F, 0.5F),
		SW(0F, 1.0F),

		NC(.5F, 0F),
		 C(.5F, .5F),
		SC(.5F, 1.0F),

		NE(1.0F, 0F),
		CE (1.0F, 0.5F),
		SE(1.0F, 1.0F);

		private float xRatio;
		private float yRatio;

		Hook(float xRatio, float yRatio) {
			this.xRatio = xRatio;
			this.yRatio = yRatio;
		}

		public float x(float width) {
			return xRatio * width;
		}

		public float y(float height) {
			return yRatio * height;
		}

	}

	public static class Mold extends Location {

		private float alpha;
		private float beta;
		private Element element;

		public Mold(Element element, float alpha, float beta) {
			this.element = element;
			this.alpha = alpha;
			this.beta = beta;
		}

		@Override
		public float getX() {
			return alpha;
		}

		@Override
		public float getY() {
			return beta;
		}

		public void setX(float x) {
			alpha = x;
		}

		public void setY(float y) {
			beta = y;
		}

		@Override
		public void translate(float x, float y) {
			this.alpha += x;
			this.beta += y;
		}

		@Override
		public float xOnScreen() {
			return element.parent.getXOnScreen() + getX();
		}

		@Override
		public float yOnScreen() {
			return element.parent.getYOnScreen() + getY();
		}
	}

	public static class Relative extends Location {

		private Element[] structures;
		private Hook[] hooks;

		public Relative(Element a, Element b, Hook aHook, Hook bHook) {
			structures = new Element[2];
			hooks      = new Hook[2];
			if(a == b)
				throw new RuntimeException("Elements can not be relative to themselves.");
			structures[0] = a;
			structures[1] = b;
			hooks[0]      = aHook;
			hooks[1]	  = bHook;
		}

		@Override
		public float xOnScreen() {
			return structures[1].getXOnScreen()
					+ hooks[1].x((structures[1].getWidthOnScreen()/**structures[1].getScaleX()*/))
					- hooks[0].x((structures[0].getWidthOnScreen()/**structures[0].getScaleX()*/));
		}

		@Override
		public float yOnScreen() {
			return structures[1].getYOnScreen()
					+ hooks[1].y((structures[1].getHeightOnScreen()/**structures[1].getScaleY()*/))
					- hooks[0].y((structures[0].getHeightOnScreen()/**structures[1].getScaleY()*/));
		}

		@Override
		public float getX() {
			return xOnScreen();
		}

		@Override
		public float getY() {
			return yOnScreen();
		}

	}

	public float x() {
		return getX() + alpha + offsetx.getValue();
	}

	public float y() {
		return getY() + beta + offsety.getValue();
	}

	public abstract float xOnScreen();

	public abstract float yOnScreen();

	public abstract float getX();

	public abstract float getY();

	public float getXOnScreen() {
		return xOnScreen() + alpha + offsetx.getValue();
	}

	public float getYOnScreen() {
		return yOnScreen() + beta + offsety.getValue();
	}

	public void setTranslate(float sx, float sy) {
		alpha = sx;
		beta  = sy;
	}

	public Value getOffsetx() {
		return offsetx;
	}

	public void setOffsetx(Value offsetx) {
		this.offsetx = offsetx;
	}

	public Value getOffsety() {
		return offsety;
	}

	public void setOffsety(Value offsety) {
		this.offsety = offsety;
	}

}
