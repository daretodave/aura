package com.aura.model;

import com.aura.model.Value.StaticValue;

public abstract class Bounds {

	public enum Scalar {
		WIDTH,
		HEIGHT;
	}

	public static class Bounded extends Bounds {

		private Value mdelta;
		private Value delta;
		private Scalar maintain;
		private Element element;

		public Bounded(Element element, Scalar maintain, Value mdelta, Value delta) {
			this.element  = element;
			this.maintain = maintain;
			this.mdelta   = mdelta;
			this.delta    = delta;
		}

		public Bounded(Element element, Scalar maintain, float mdelta, float delta) {
			this(element, maintain, new Value.StaticValue(mdelta), new Value.StaticValue(delta));
		}


		@Override
		public float getWidth() {
			switch(maintain) {
			case HEIGHT:
				return delta.getValue();
			case WIDTH:
				return element.getWidthOnScreen()-mdelta.getValue()*2F;
			default:
				throw new RuntimeException();
			}
		}

		@Override
		public float getHeight() {
			switch(maintain) {
			case HEIGHT:
				return element.getHeightOnScreen()-mdelta.getValue()*2F;
			case WIDTH:
				return delta.getValue();
			default:
				throw new RuntimeException();
			}
		}

		@Override
		public Bounds copy() {
			return new Bounds.Bounded(element, maintain, mdelta, delta);
		}

	}

	private float alpha;
	private float beta;
	private Extreme widthExtreme;
	private Extreme heightExtreme;

	public void translate(float w, float h) {
		this.alpha += w;
		this.beta += h;
	}

	public static class Mold extends Bounds {

		private float alpha;
		private float beta;

		public Mold(float alpha, float beta) {
			this.alpha = alpha;
			this.beta = beta;
		}

		@Override
		public float getWidth() {
			return alpha;
		}

		@Override
		public float getHeight() {
			return beta;
		}

		public void setWidth(float width) {
			alpha = width;
		}

		public void setHeight(float height) {
			beta = height;
		}

		public static Bounds.Mold box(float wh) {
			return rect(wh, wh);
		}

		public static Bounds.Mold rect(float w, float h) {
			return new Bounds.Mold(w, h);
		}

		@Override
		public Bounds copy() {
			return new Bounds.Mold(alpha, beta);
		}

	}

	public static Bounds.Mold box(float wh) {
		return rect(wh, wh);
	}

	public static Bounds.Mold rect(float w, float h) {
		return Bounds.Mold.rect(w, h);
	}

	public static class Respective extends Bounds {

		private Value alpha;
		private Value beta;

		public Respective(float alpha, float beta) {
			this(new StaticValue(alpha), new StaticValue(beta));
		}

		public Respective(Value alpha, Value beta) {
			this.alpha = alpha;
			this.beta  = beta;
		}

		public Respective(float alpha, Value beta) {
			this(new StaticValue(alpha), beta);
		}

		public Respective(Value alpha, float beta) {
			this(alpha, new StaticValue(beta));
		}

		@Override
		public float getWidth() {
			return alpha.getValue();
		}

		@Override
		public float getHeight() {
			return beta.getValue();
		}

		public Value getWidthValue() {
			return alpha;
		}

		public Value getHeightValue() {
			return beta;
		}

		@Override
		public Bounds copy() {
			return new Respective(alpha, beta);
		}

	}

	public float width() {
		float width =  getWidth() + alpha;
		return widthExtreme != null ? widthExtreme.normalize(width) : width;
	}

	public float height() {
		float height =  getHeight() + beta;
		return heightExtreme != null ? heightExtreme.normalize(height) : height;
	}

	public abstract float getWidth();

	public abstract float getHeight();

	public Extreme getWidthExtreme() {
		return widthExtreme;
	}

	public void setWidthExtreme(Extreme widthExtreme) {
		this.widthExtreme = widthExtreme;
	}

	public Extreme getHeightExtreme() {
		return heightExtreme;
	}

	public void setHeightExtreme(Extreme heightExtreme) {
		this.heightExtreme = heightExtreme;
	}

	public static Bounds implode(final Bounds bounds, final float f) {
		return implode(bounds, new StaticValue(f));
	}

	public static Bounds implode(final Bounds bounds, final Value padding) {
		return new Bounds() {

			@Override
			public float getWidth() {
				return bounds.width()-padding.getValue();
			}

			@Override
			public float getHeight() {
				return bounds.height()-padding.getValue();
			}

		};
	}

	public static Bounds observe(final Element element) {
		return Bounds.observe(element, 0, 0);
	}

	public static Bounds observe(final Element element, final float ox, final float oy) {
		return new Bounds() {
			@Override
			public float getWidth() {
				return element.width() + ox;
			}

			@Override
			public float getHeight() {
				return element.height() + oy;
			}
		};
	}

	public Bounds copy() {
		return null;
	}

}
