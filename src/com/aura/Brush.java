package com.aura;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.aura.model.Bounds;
import com.aura.model.State;
import com.aura.model.State.PointedValue;
import com.aura.model.Texture;

public abstract class Brush {

	@Override
	public String toString() {
		return "Brush [listeners=" + listeners + ", tx=" + tx + ", ty=" + ty
				+ ", style=" + style + ", gradient=" + gradient + ", texture="
				+ texture + ", fill=" + fill + ", other=" + other
				+ ", outline=" + outline + ", textStyle=" + textStyle
				+ ", font=" + font + ", textWrap=" + textWrap + ", textAlignH="
				+ textAlignH + ", textAlignV=" + textAlignV + ", stroke="
				+ stroke + ", textSize=" + textSize + ", radii="
				+ Arrays.toString(radii) + ", state=" + state + ", overlay="
				+ overlay + ", sopacity=" + sopacity + "]";
	}

	public enum Style {
		NONE, FILL, OUTLINE, FILL_AND_OUTLINE;
	}

	public enum Align {
		START, MIDDLE, END
	}

	public static final int TEXT_NORMAL     = 0;
	public static final int TEXT_BOLD       = 1;
	public static final int TEXT_ITALIC     = 2;
	public static final int TEXT_UNDERLINED = 4;


	public static class BrushGroup extends Brush {

		private Collection<Brush> pallet;

		public BrushGroup(Collection<Brush> pallet) {
			this.pallet = pallet;
		}

		public BrushGroup(HashMap<String, Brush> pallet) {
			this(pallet.values());
		}

		public Collection<Brush> getPallet() {
			return pallet;
		}

		@Override
		public Brush setStyle(Style style) {
			for(Brush brush : pallet) {
				brush.setStyle(style);
			}
			return this;
		}

		@Override
		public Brush setRadius(int index, float radius) {
			for(Brush brush : pallet) {
				brush.setRadius(index, radius);
			}
			return this;
		}

		@Override
		public Brush setRadius(float radius) {
			for(Brush brush : pallet) {
				brush.setRadius(radius);
			}
			return this;
		}

		@Override
		public Brush setGradient(Gradient gradient) {
			for(Brush brush : pallet) {
				brush.setGradient(gradient);
			}
			return this;
		}

		@Override
		public Brush setTexture(Texture texture) {
			for(Brush brush : pallet) {
				brush.setTexture(texture);
			}
			return this;
		}

		@Override
		public Brush setStroke(float stroke) {
			for(Brush brush : pallet) {
				brush.setStroke(stroke);
			}
			return this;
		}

		@Override
		public Brush setFillColor(int color) {
			for(Brush brush : pallet) {
				brush.setFillColor(color);
			}
			return this;
		}

		@Override
		public Brush setOutlineColor(int color) {
			for(Brush brush : pallet) {
				brush.setOutlineColor(color);
			}
			return this;
		}

		@Override
		public Brush setOtherColor(int color) {
			for(Brush brush : pallet) {
				brush.setOtherColor(color);
			}
			return this;
		}

		@Override
		protected void setTx(float tx) {
			for(Brush brush : pallet) {
				brush.setTx(tx);
			}
		}

		@Override
		protected void setTy(float ty) {
			for(Brush brush : pallet) {
				brush.setTx(ty);
			}
		}

		@Override
		public Brush setTextSize(float size) {
			for(Brush brush : pallet) {
				brush.setTextSize(size);
			}
			return this;
		}

		@Override
		public Brush setTextStyle(int style) {
			for(Brush brush : pallet) {
				brush.setTextStyle(style);
			}
			return this;
		}

		@Override
		public Brush setTextHorizontalAlign(Align align) {
			for(Brush brush : pallet) {
				brush.setTextHorizontalAlign(align);
			}
			return this;
		}

		@Override
		public Brush setTextVerticalAlign(Align align) {
			for(Brush brush : pallet) {
				brush.setTextVerticalAlign(align);
			}
			return this;
		}

		@Override
		public Brush setTextWordWrap(boolean wrapped) {
			for(Brush brush : pallet) {
				brush.setTextWordWrap(wrapped);
			}
			return this;
		}

		@Override
		public Brush setFont(Object font) {
			for(Brush brush : pallet) {
				brush.setFont(font);
			}
			return this;
		}

		@Override
		public void addStyleChangeListener(ValueChangeListener listener) {
			for(Brush brush : pallet) {
				brush.addStyleChangeListener(listener);
			}
		}

		@Override
		public void removeStyleChangeListener(ValueChangeListener listener) {
			for(Brush brush : pallet) {
				brush.removeStyleChangeListener(listener);
			}
		}


		@Override
		public Brush setScaleX(float valuex) {
			for(Brush brush : pallet) {
				brush.setScaleX(valuex);
			}
			return this;
		}

		@Override
		public Brush setScaleY(float valuey) {
			for(Brush brush : pallet) {
				brush.setScaleY(valuey);
			}
			return this;
		}

		@Override
		public Brush setScaleLocation(float xratio, float yratio) {
			for(Brush brush : pallet) {
				brush.setScaleLocation(xratio, yratio);
			}
			return this;
		}

		@Override
		public Brush setRotationX(float valuex) {
			for(Brush brush : pallet) {
				brush.setRotationX(valuex);
			}
			return this;
		}

		@Override
		public Brush setRotationY(float valuey) {
			for(Brush brush : pallet) {
				brush.setRotationY(valuey);
			}
			return this;
		}

		@Override
		public Brush setRotationLocation(float xratio, float yratio) {
			for(Brush brush : pallet) {
				brush.setRotationLocation(xratio, yratio);
			}
			return this;
		}

		@Override
		public Object getDefaultFont() {
			return null;
		}

		@Override
		public void text(String text, float ox, float oy, float w, float h) {
			throw new RuntimeException("The manner in which this brush was selected can only be used for style purposes.");
		}

		@Override
		public TextComputation calculateTextBounds(String text, Bounds bounds) {
			throw new RuntimeException("The manner in which this brush was selected can only be used for style purposes.");
		}

		@Override
		public void build(Object o) {
			throw new RuntimeException("The manner in which this brush was selected can only be used for style purposes.");
		}

		@Override
		public void rect(float width, float height) {
			throw new RuntimeException("The manner in which this brush was selected can only be used for style purposes.");
		}

		@Override
		protected void _locate(float x, float y) {
			throw new RuntimeException("The manner in which this brush was selected can only be used for style purposes.");
		}

		@Override
		protected void _opacity(float opacity) {
			throw new RuntimeException("The manner in which this brush was selected can only be used for style purposes.");
		}

		@Override
		protected void _rotate(float x, float y) {
			throw new RuntimeException("The manner in which this brush was selected can only be used for style purposes.");
		}

		@Override
		protected void _scale(float x, float y) {
			throw new RuntimeException("The manner in which this brush was selected can only be used for style purposes.");
		}

		@Override
		public Object push() {
			throw new RuntimeException("The manner in which this brush was selected can only be used for style purposes.");
		}

		@Override
		public void pop(Object o) {
			throw new RuntimeException("The manner in which this brush was selected can only be used for style purposes.");
		}

		@Override
		public Brush recreate() {
			throw new RuntimeException("The manner in which this brush was selected can only be used for style purposes.");
		}

		@Override
		public void clip(float width, float height) {
			throw new RuntimeException("The manner in which this brush was selected can only be used for style purposes.");
		}

		@Override
		public boolean worthIt(float w, float h) {
			throw new RuntimeException("The manner in which this brush was selected can only be used for style purposes.");
		}

	}

	public static class TextSnippet {

		@Override
		public String toString() {
			return "TextSnippet [text=" + text + ", x=" + x + ", y=" + y
					+ ", w=" + w + ", h=" + h + "]";
		}
		public String getText() {
			return text;
		}
		public float getX() {
			return x;
		}
		public float getY() {
			return y;
		}
		public float getW() {
			return w;
		}
		public float getH() {
			return h;
		}

		public TextSnippet(String text, float x, float y, float w, float h) {
			this.text = text;
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}

		public TextSnippet(String text, float w, float h) {
			this.text = text;
			this.w = w;
			this.h = h;
		}

		public void setLocation(float x, float y) {
			this.x = x;
			this.y = y;
		}

		private final String text;
		private float x;
		private float y;
		private float w;
		private float h;



	}

	public interface ValueChangeListener {
		public void onChange(Brush brush, String style, Object before, Object after);

		public boolean isBuilt();
	}

	public static class TextComputation {

		@Override
		public String toString() {
			return "TextComputation [snippets=" + snippets + ", bounds="
					+ bounds + "]";
		}
		public List<TextSnippet> getSnippets() {
			return snippets;
		}
		public Bounds getBounds() {
			return bounds;
		}
		public TextComputation(List<TextSnippet> snippets, Bounds bounds) {
			this.snippets = snippets;
			this.bounds = bounds;
		}
		private final List<TextSnippet> snippets;
		private final Bounds bounds;

	}

	public enum Gradient {

		VERTICAL(0F, 0F, 1F, 0F), HORTIZONTAL(0F, 0F, 0F, 1F), DIAGONOL(0F, 0F,
				1F, 1F);

		private Gradient(float xratioa, float yratioa, float xratiob,
				float yratiob) {
			this.xratioa = xratioa;
			this.yratioa = yratioa;
			this.xratiob = xratiob;
			this.yratiob = yratiob;
		}

		private final float xratioa;
		private final float yratioa;
		private final float xratiob;
		private final float yratiob;

		public float xa(float x) {
			return xratioa * x;
		}

		public float ya(float y) {
			return yratioa * y;
		}

		public float xb(float x) {
			return xratiob * x;
		}

		public float yb(float y) {
			return yratiob * y;
		}

	}

	//private ArrayDeque<Context> stack = new ArrayDeque<Context>();
	private ArrayList<ValueChangeListener> listeners = new ArrayList<ValueChangeListener>();

	private float tx;
	private float ty;
	protected Style style;
	protected Gradient gradient;
	protected Texture texture;
	protected int fill;
	protected int other;
	protected int outline;
	protected int textStyle;
	protected Object font;
	protected boolean textWrap;
	protected Align textAlignH;
	protected Align textAlignV;
	protected float stroke;
	protected float textSize;
	protected float[] radii;
	protected State state;
	private boolean overlay;
	private float sopacity;

	public void copyTo(Brush brush) {
		brush.tx = tx;
		brush.ty = ty;
		brush.style = style;
		brush.gradient = gradient;
		brush.texture = texture;
		brush.fill = fill;
		brush.other = other;
		brush.outline = outline;
		brush.textStyle = textStyle;
		brush.font = font;
		brush.textWrap = textWrap;
		brush.textAlignV = textAlignV;
		brush.textAlignH = textAlignH;
		brush.stroke = stroke;
		brush.textSize = textSize;
		brush.radii = Arrays.copyOf(radii, radii.length);
		brush.state = state.copy(null);
		brush.overlay = overlay;
	}

	public static final int TOP_LEFT = 0;
	public static final int TOP_RIGHT = 1;
	public static final int BOTTOM_LEFT = 2;
	public static final int BOTTOM_RIGHT = 3;

	public static final String STYLE = "style";
	public static final String GRADIENT = "gradient";
	public static final String TEXTURE = "texture";
	public static final String FILL = "fill";
	public static final String OTHER = "other";
	public static final String OUTLINE = "outline";
	public static final String TX = "tx";
	public static final String TY = "ty";
	public static final String TL = "tl_radius";
	public static final String TR = "tr_radius";
	public static final String BL = "bl_radius";
	public static final String BR = "br_radius";
	public static final String STROKE = "stroke";
	public static final String TEXT_STYLE = "text_style";
	public static final String TEXT_SIZE  = "text_size";
	public static final String H_ALIGN  = "h_align";
	public static final String V_ALIGN  = "v_align";
	public static final String TEXT_WRAP  = "wrapped";
	public static final String FONT = "font";
	public static final String OPACITY   = "opacity";
	public static final String SCALE    = "scale";
	public static final String SCALEX    = "scalex";
	public static final String SCALEY    = "scaley";
	public static final String ROTATION  = "rotation";
	public static final String ROTATIONX  = "rotationx";
	public static final String ROTATIONY  = "rotationy";
	public static final String ROTATION_LOC = "rotation_loc";
	public static final String SCALE_LOC = "scale_loc";

	private final static String[] RADII = {
		TL, TR, BL, BR
	};

	public Brush() {
		this.style = Style.NONE;
		this.textAlignH = Align.START;
		this.textAlignV = Align.START;
		this.font = getDefaultFont();
		this.radii = new float[4];
		this.stroke   = 1F;
		this.sopacity = 1F;
		this.state = new State();
	}

	/* Add state loading if reopened
	 * private void declare(Context context) {
		context.set(STYLE, style);
		context.set(GRADIENT, gradient);
		context.set(TEXTURE, texture);
		context.set(FILL, fill);
		context.set(OTHER, other);
		context.set(OUTLINE, outline);
		context.set(TX, getTx());
		context.set(TY, getTy());
		context.set(STROKE, stroke);
		context.set(TL, radii[TOP_LEFT]);
		context.set(TR, radii[TOP_RIGHT]);
		context.set(BL, radii[BOTTOM_LEFT]);
		context.set(BR, radii[BOTTOM_RIGHT]);
		context.set(BR, radii[BOTTOM_RIGHT]);
		context.set(TEXT_STYLE, textStyle);
		context.set(TEXT_SIZE, textSize);
		context.set(H_ALIGN, textAlignH);
		context.set(V_ALIGN, textAlignV);
		context.set(TEXT_WRAP, textWrap);
		context.set(FONT, font);
	}*/

	private Context initialized;

	public void sap(Context context) {
		this.initialized = context;
		if (context.exist(STYLE))
			setStyle(context.get(STYLE, Style.class));
		if (context.exist(GRADIENT))
			setGradient(context.get(GRADIENT, Gradient.class));
		if (context.exist(TEXTURE))
			setTexture(context.get(TEXTURE, Texture.class));
		if (context.exist(FILL))
			setFillColor(context.get(FILL, Integer.class));
		if (context.exist(OTHER))
			setOtherColor(context.get(OTHER, Integer.class));
		if (context.exist(OUTLINE))
			setOutlineColor(context.get(OUTLINE, Integer.class));
		if (context.exist(TX))
			setTx(context.get(TX, Float.class));
		if (context.exist(TY))
			setTy(context.get(TY, Float.class));
		if (context.exist(TL))
			setRadius(TOP_LEFT, context.get(TL, Float.class));
		if (context.exist(TR))
			setRadius(TOP_RIGHT, context.get(TR, Float.class));
		if (context.exist(BL))
			setRadius(BOTTOM_LEFT, context.get(BL, Float.class));
		if (context.exist(BR))
			setRadius(BOTTOM_RIGHT, context.get(BR, Float.class));
		if (context.exist(STROKE))
			setStroke(context.get(STROKE, Float.class));
		if (context.exist(TEXT_SIZE))
			setTextSize(context.get(TEXT_SIZE, Float.class));
		if (context.exist(TEXT_STYLE))
			setTextStyle(context.get(TEXT_STYLE, Integer.class));
		if (context.exist(H_ALIGN))
			setTextHorizontalAlign(context.get(H_ALIGN, Align.class));
		if (context.exist(V_ALIGN))
			setTextVerticalAlign(context.get(V_ALIGN, Align.class));
		if (context.exist(TEXT_WRAP))
			setTextWordWrap(context.get(TEXT_WRAP, Boolean.class));
		if (context.exist(FONT))
			setFont(context.raw(FONT));
		if (context.exist(SCALE)) {
			PointedValue value = context.get(SCALE, PointedValue.class);
			setScale(value.getValueX(), value.getValueY(), value.getX(), value.getY());
		}
		if (context.exist(ROTATION)) {
			PointedValue value = context.get(ROTATION, PointedValue.class);
			setRotation(value.getValueX(), value.getValueY(), value.getX(), value.getY());
		}
	}

	/*public int save() {
		Context context = new Context();
		declare(context);
		stack.add(context);
		return stack.size();
	}

	public void restore() {
		if (stack.isEmpty()) {
			throw new IllegalArgumentException(
					"The brush could not restore from an empty stack.");
		}
		Context context = stack.poll();
		sap(context);
		sopacity = 1F;
	}*/

	public Brush setScale(float valueX, float valueY, float x, float y) {
		setScaleX(x);
		setScaleY(y);
		setScaleLocation(x, y);
		return this;
	}

	public Brush setScale(float value) {
		setScaleX(value);
		setScaleY(value);
		return this;
	}

	public Brush setScaleX(float value) {
		float prior = state.getScaleX();
		state.setScaleX(value);
		onChange(SCALEX, prior, value);
		return this;
	}

	public Brush setScaleY(float value) {
		float prior = state.getScaleY();
		state.setScaleY(value);
		onChange(SCALEY, prior, value);
		return this;
	}

	public Brush setScaleLocation(float xratio, float yratio) {
		state.setScaleLocation(xratio, yratio);
		onChange(SCALE_LOC, -1, -1);
		return this;
	}

	public Brush setRotation(float valueX, float valueY, float x, float y) {
		setRotationX(x);
		setRotationY(y);
		setRotationLocation(x, y);
		return this;
	}

	public Brush setRotation(float value) {
		setRotationX(value);
		setRotationY(value);
		return this;
	}

	public Brush setRotationX(float value) {
		float prior = state.getRotationX();
		state.setRotationX(value);
		onChange(ROTATIONX, prior, value);
		return this;
	}

	public Brush setRotationY(float value) {
		float prior = state.getRotationY();
		state.setRotationY(value);
		onChange(ROTATIONY, prior, value);
		return this;
	}

	public Brush setRotationLocation(float xratio, float yratio) {
		state.setRotationLocation(xratio, yratio);
		onChange(ROTATION_LOC, -1, -1);
		return this;
	}

	public Brush setFont(Object font) {
		if (font == null)
			throw new IllegalArgumentException(
					"The font for a brush can not be null!");
		Object prior = this.font;
		this.font = font;
		onChange(FONT, prior, font);
		return this;
	}

	public Brush setTextHorizontalAlign(Align align) {
		if (align == null)
			throw new IllegalArgumentException(
					"The alignment for a brush can not be null!");
		Align prior = this.textAlignH;
		this.textAlignH = align;
		onChange(H_ALIGN, prior, align);
		return this;
	}

	public Brush setTextVerticalAlign(Align align) {
		if (align == null)
			throw new IllegalArgumentException(
					"The alignment for a brush can not be null!");
		Align prior = this.textAlignV;
		this.textAlignV = align;
		onChange(V_ALIGN, prior, align);
		return this;
	}

	public Brush setTextWordWrap(boolean wrapped) {
		boolean prior = this.textWrap;
		this.textWrap = wrapped;
		onChange(TEXT_WRAP, prior, wrapped);
		return this;
	}

	public Brush setTextStyle(int style) {
		int prior = this.textStyle;
		this.textStyle = style;
		onChange(TEXT_STYLE, prior, style);
		return this;
	}

	public Brush setTextSize(float size) {
		float prior = this.textSize;
		this.textSize = size;
		onChange(TEXT_SIZE, prior, size);
		return this;
	}

	public Brush setStyle(Style style) {
		if (style == null)
			throw new IllegalArgumentException(
					"The style for a brush can not be null!");
		Style prior = this.style;
		this.style = style;
		onChange(STYLE,prior, style);
		return this;
	}

	public Brush setRadius(int index, float radius) {
		float prior = radii[index];
		radii[index] = radius;
		onChange(RADII[index], prior, radius);
		return this;
	}

	public Brush setRadius(float radius) {
		setRadius(TOP_LEFT, radius);
		setRadius(TOP_RIGHT, radius);
		setRadius(BOTTOM_LEFT, radius);
		setRadius(BOTTOM_RIGHT, radius);
		return this;
	}

	public Brush setGradient(Gradient gradient) {
		Gradient prior = this.gradient;
		this.gradient = gradient;
		onChange(GRADIENT, prior, gradient);
		return this;
	}

	public Brush setTexture(Texture texture) {
		Texture prior = this.texture;
		this.texture = texture;
		onChange(TEXTURE, prior, texture);
		return this;
	}

	public Brush setStroke(float stroke) {
		float prior = this.stroke;
		this.stroke = stroke;
		onChange(STROKE, prior, stroke);
		return this;
	}

	public Brush setFillColor(int color) {
		int prior = this.fill;
		this.fill = color;
		onChange(FILL, prior, color);
		return this;
	}

	public Brush setOutlineColor(int color) {
		int prior = this.outline;
		this.outline = color;
		onChange(OUTLINE, prior, color);
		return this;
	}

	public Brush setOtherColor(int color) {
		int prior = this.other;
		this.other = color;
		onChange(OTHER, prior, color);
		return this;
	}

	protected void setTx(float tx) {
		this.tx = tx;
	}

	protected void setTy(float ty) {
		this.ty = ty;
	}

	public Brush translate(float x, float y) {
		this.setTx(x);
		this.setTy(y);
		_locate(getTx(), getTy());
		return this;
	}

	public void text(String text, float w, float h) {
		text(text, 0, 0, w, h);
	}

	public void addStyleChangeListener(ValueChangeListener listener) {
		listeners.add(listener);
	}

	public void removeStyleChangeListener(ValueChangeListener listener) {
		listeners.remove(listener);
	}

	private void onChange(String value, Object before, Object after) {
		for(ValueChangeListener listener : listeners) {
			if(!listener.isBuilt())
				continue;
			listener.onChange(this, value, before, after);
		}
	}

	public abstract Object getDefaultFont();

	public abstract Brush recreate();

	public abstract Object push();

	public abstract void pop(Object o);

	public abstract void build(Object o);

	public abstract void rect(float width, float height);

	public abstract void text(String text, float ox, float oy, float w, float h);

	public abstract boolean worthIt(float w, float h);

	public abstract TextComputation calculateTextBounds(String text, Bounds bounds);

	protected abstract void _locate(float x, float y);

	protected abstract void _opacity(float opacity);

	protected abstract void _rotate(float x, float y);

	protected abstract void _scale(float x, float y);

	public Brush untranslate() {
		_locate(-getTx(), -getTy());
		this.setTx(0);
		this.setTy(0);
		return this;
	}

	public float getTx() {
		return tx;
	}

	public float getTy() {
		return ty;
	}

	public boolean isOverlay() {
		return overlay;
	}

	public void setOverlay(boolean overlay) {
		this.overlay = overlay;
	}

	protected void resetStack() {
		sopacity = 1F;
	}

	public void adhere(State state, float w, float h) {
		if(state.getOpacity() < sopacity) {
			sopacity -= (1F - state.getOpacity());
			sopacity = Math.max(sopacity, 0F);
			sopacity = Math.min(sopacity, 1F);
			_opacity(sopacity);
		}
		float sx = w * state.getScaleXRatio();
		float sy = h * state.getScaleYRatio();
		translate(sx, sy);
		_scale(state.getScaleX(), state.getScaleY());
		translate(-sx, -sy);
		float rx = w * state.getRotationXRatio();
		float ry = h * state.getRotationYRatio();
		translate(rx, ry);
		_rotate((float) Math.toRadians(state.getRotationX()),(float) Math.toRadians(state.getRotationY()));
		translate(-rx, -ry);
	}


	public void adhere(ElementCarrier carrier) {
		translate(carrier);
		if(carrier.getTrueState() != null) {
			adhere(carrier.getTrueState(), carrier.getElement().getWidthOnScreen(),  carrier.getElement().getHeightOnScreen());
		}
		adhere(carrier.getState(), carrier.getElement().getWidthOnScreen(),  carrier.getElement().getHeightOnScreen());
	}

	public void untranslate(ElementCarrier element) {
		translate(-element.getElement().getXOnScreen(), -element.getElement().getYOnScreen());
	}

	public void translate(ElementCarrier carrier) {
		translate(carrier.getElement().getXOnScreen(), carrier.getElement().getYOnScreen());
	}

	public Context getInitialized() {
		return initialized;
	}

	public abstract void clip(float width, float height);

	public Texture getTexture() {
		return texture;
	}

}
