package com.aura.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import com.aura.Aura;
import com.aura.Brush;
import com.aura.Brush.BrushGroup;
import com.aura.Context;
import com.aura.ElementCarrier;
import com.aura.cache.Cache;
import com.aura.input.DraggedInListener;
import com.aura.input.KeyEvent;
import com.aura.input.KeyListener;
import com.aura.input.MouseEvent;
import com.aura.input.MouseListener;
import com.aura.model.Location.Hook;

public abstract class Element extends ElementCarrier implements Brush.ValueChangeListener {

	private String id;

	private boolean destroyed;
	private int cursor;
	private DraggingPolicy draggingPolicy;
	private String override;
	private boolean hidden;
	private Set<Element> focusWhenFocused = new HashSet<Element>();

	protected State state;
	protected HashMap<String, Brush> pallet  = new HashMap<String, Brush>();
	protected HashMap<String, Brush> opallet = new HashMap<String, Brush>();
	protected boolean locked;
	protected Element parent;
	protected HashMap<String, Element> children = new HashMap<String, Element>();
	protected Set<String> tags = new TreeSet<String>();

	private boolean specialFocus;

	protected abstract Element recreate();

	public abstract Object toClipboard();

	public abstract void paste(Object object);

	public void tag(String tag) {
		tags.add(tag);
	}

	public void untag(String tag) {
		tags.remove(tag);
	}

	public boolean is(String tag) {
		return tags.contains(tag);
	}

	public EList getAllIntersections(final ElementCarrier examine, final float x, final float y) { //ranked by closest to center.
		ArrayList<Element> intersections = getIntersections(examine);
		for(ElementCarrier element : elements) {
			intersections.addAll(element.getElement().getAllIntersections(examine, x, y));
		}
		Collections.sort(intersections, new Comparator<ElementCarrier>() {
			@Override
			public int compare(ElementCarrier e, ElementCarrier e2) {
				return new Float(e.distanceFromCenter(x, y)).compareTo(new Float(e2.distanceFromCenter(x, y)));
			}
		});
		return new EList(intersections);
	}

	public ArrayList<Element> getIntersections(final ElementCarrier examine) {
		ArrayList<Element> intersections = new ArrayList<Element>();
		for(ElementCarrier carrier : elements) {
			Element element = carrier.getElement();
			if(!element.hidden && !element.locked && element.getElement() != examine.getElement() && element.intersects(examine)) {
				intersections.add(element);
			}
		}
		return intersections;
	}

	public abstract boolean draggedIn(Element what, float x, float y);

	@Override
	public String toString() {
		return id == null ? getClass().getSimpleName() : id;
	}

	public Group find(String key) {
		return new Group(collect(key));
	}

	public Group find(String... key) {
		HashSet<Element> elements = new HashSet<Element>();
		if(key.length == 0) {
			key = new String[] {""};
		}
		for(String tag : key) {
			elements.addAll(collect(tag));
		}
		return new Group(elements);
	}

	public boolean matches(String key) {
		if(key.startsWith("#")) {
			if(key.substring(1).equals(id)) {
				return true;
			}
		} else if(key.startsWith("$")) {
			if(is(key.substring(1))) {
				return true;
			}
		} else {
			key = key.toLowerCase();
			boolean extenstive = key.startsWith("~");
			if(extenstive) {
				key = key.substring(1);
			}
			if(key.endsWith("s")) {
				key = key.substring(0, key.length()-1);
			}
			if(!extenstive && locked) {
				return false;
			}
			String clazz = getClass().getSimpleName().toLowerCase();
			if(clazz.equalsIgnoreCase(key)) {
				return true;
			}
		}
		return false;
	}

	private HashSet<Element> collect(String key) {
		HashSet<Element> elements = new HashSet<Element>();
		if(key == null || key.isEmpty()) {
			return children();
		}
		if(key.equals("*")) {
			for(ElementCarrier element : this.elements) {
				elements.add(element.getElement());
			}
			return elements;
		}
		if(key.contains("|")) {
			String[] group = key.split("\\|");
			for(String tag : group) {
				elements.addAll(collect(tag));
			}
			return elements;
		}
		boolean lightSearch = key.endsWith("*");
		if(lightSearch) {
			key = key.substring(0, key.length()-1);
		}
		for(ElementCarrier element : this.elements) {
			Element resolve = element.getElement();
			if(resolve.matches(key)) {
				elements.add(resolve);
			}
			if(!lightSearch) {
				elements.addAll(resolve.collect(key));
			}
		}
		return elements;
	}

	public static final class Policy {
		public static final int CANT_GAIN_FOCUS  = 1;
		public static final int ALWAYS_FOCUSED   = 2;
		public static final int CANT_LOOSE_FOCUS = 4;
		public static final int PRIORITY_OVER_CHILDREN = 8;
	}

	private StateListener stateListener;
	private MouseListener mouseListener;
	private KeyListener keyListener;
	private DraggedInListener draggedInListener;
	private Brush brush;
	private Brush overlayBrush;
	private Brush overlay;
	private int policies;
	private boolean built;
	protected float[] margins;

	public static final String DEFAULT_STYLES = "_styles";

	public static final String STANDARD       = "standard";
	public static final String FOCUSED        = "focused";
	public static final String HOVER 		  = "hover";
	public static final String PRESSED		  = "pressed";

	public static final int MARGIN_LEFT   = 0;
	public static final int MARGIN_RIGHT  = 1;
	public static final int MARGIN_TOP    = 2;
	public static final int MARGIN_BOTTOM = 3;

	public static final String MARGIN_LEFT_KEY = "MARGIN_L";
	public static final String MARGIN_RIGHT_KEY = "MARGIN_R";
	public static final String MARGIN_TOP_KEY = "MARGIN_T";
	public static final String MARGIN_BOTTOM_KEY = "MARGIN_B";

	private final static String[] MARGIN_KEY_MAP = {
		MARGIN_LEFT_KEY, MARGIN_RIGHT_KEY, MARGIN_TOP_KEY, MARGIN_BOTTOM_KEY
	};

	public void setMargin(int margin, float value) {
		float prior = margins[margin];
		margins[margin] = value;
		onChange(null, MARGIN_KEY_MAP[margin], prior, value);
	}

	public void setMargin(float value) {
		setMargin(MARGIN_LEFT,   value);
		setMargin(MARGIN_RIGHT,  value);
		setMargin(MARGIN_TOP,    value);
		setMargin(MARGIN_BOTTOM, value);
	}

	public float margin(int margin) {
		return margins[margin];
	}

	@SuppressWarnings("unchecked")
	public <T extends ElementCarrier> T copy(Class<T> expected) {
		return (T) copy(null, expected, false);
	}

	@SuppressWarnings("unchecked")
	public <T extends ElementCarrier> T copy(Class<T> expected, boolean listeners) {
		return (T) copy(null, expected, listeners);
	}

	protected void styleChildren() {
		mapChildren(children);
		for(Entry<String, Element> e : children.entrySet()) {
			Element element = e.getValue();
			String resolve  = e.getKey();
			for(Entry<String, Brush> entry : pallet.entrySet()) {
				String key = entry.getKey();
				if(key.startsWith("*"+resolve)) {
					String brush = key.split("-")[1];
					if(brush.startsWith("~")) {
						brush = brush.substring(1);
						if(!element.opallet.containsKey(brush)) {
							entry.getValue().copyTo(element.addOverlayBrush(brush));
						} else {
							element.opallet.get(brush).sap(entry.getValue().getInitialized());
						}
					} else {
						if(!element.pallet.containsKey(brush)) {
							entry.getValue().copyTo(element.addBrush(brush));
						} else {
							element.pallet.get(brush).sap(entry.getValue().getInitialized());
						}
					}
				}
			}
			element.update();
		}
	}

	protected void mapChildren(HashMap<String, Element> children) {

	}

	public <T extends ElementCarrier> ElementCarrier copy(Location location, Class<T> expected, boolean listeners) {
		ElementCarrier element = recreate();
		Element resolve = element.getElement();
		resolve.bound(getBounds().copy());
		resolve.copyStyleOf(this);
		resolve.copyState(this, listeners);
		HashMap<String, Element> children = new HashMap<String, Element>();
		resolve.mapChildren(children);
		if(!children.isEmpty()) {
			for(Entry<String, Element> e : children.entrySet()) {
				Element child = e.getValue();
				String  build = e.getKey();
				Element mchild = this.children.get(build);
				child.copyStyleOf(mchild);
				child.copyState(mchild, listeners);
				child.update();
			}
		}
		if(location != null)
			resolve.locate(location);
		resolve.update();
		return resolve;
	}

	private void copyState(Element other, boolean listeners) {
		state   = other.state.copy(this);
		cursor  = other.cursor;
		margins = Arrays.copyOf(other.margins, margins.length);
		if(listeners) {
			mouseListener = other.mouseListener;
			stateListener = other.stateListener;
			keyListener   = other.keyListener;
			draggingPolicy = other.draggingPolicy;
			draggedInListener = other.draggedInListener;
		}
	}

	public void copyStyleOf(Element other) {
		brush().removeStyleChangeListener(this);
		overlayBrush().removeStyleChangeListener(this);
		pallet.clear();
		opallet.clear();
		for(Entry<String, Brush> entry : other.pallet.entrySet()) {
			Brush brush = addBrush(entry.getKey());
			entry.getValue().copyTo(brush);
		}
		for(Entry<String, Brush> entry : other.opallet.entrySet()) {
			Brush brush = addOverlayBrush(entry.getKey());
			entry.getValue().copyTo(brush);
		}
	}

	public void setPolicy(int policy, boolean set) {
		if(set) {
			if ((policies & policy) != policy) {
				policies |= policy;
				switch(policy) {
				case Policy.ALWAYS_FOCUSED:
					toState(State.FOCUSED, true);
					break;
				}
			}
		} else {
			if ((policies & policy) == policy)
				policies &= ~policy;
		}
	}

	public final Value height = new Value() {
		@Override
		public float getValue() {
			return getHeightOnScreen();
		}
	};

	public final Value width = new Value() {
		@Override
		public float getValue() {
			return getWidthOnScreen();
		}
	};

	public boolean isPolicy(int policy) {
		return (policies & policy) == policy;
	}

	public Element locate(Element other, Hook where) {
		locate(new Location.Relative(this, other, where, where));
		return this;
	}
	public Element locate(Hook on, Element other, Hook where) {
		locate(new Location.Relative(this, other, on, where));
		return this;
	}
	public Element locate(float x, float y) {
		locate(new Location.Mold(this, x, y));
		return this;
	}

	public Element attach(Hook where, Element element, Hook on) {
		attach(element);
		element.locate(on, this, where);
		return this;
	}

	public Element attach(Element element, Hook hook) {
		attach(hook, element, hook);
		return element;
	}

	public void translate(float x, float y) {
		state.setTx(state.getTx() + x);
		state.setTy(state.getTy() + y);
	}

	public HashSet<Element> children() {
		HashSet<Element> children = new HashSet<Element>();
		addChildren(this, children);
		return children;
	}

	private static void addChildren(Element element, HashSet<Element> dest) {
		for(ElementCarrier child : element.elements) {
			dest.add(child.getElement());
			addChildren(child.getElement(), dest);
		}
	}

	public abstract void bound(Bounds bounds);
	public abstract void locate(Location location);

	public Element() {
		this.state = new State(this);
		this.build();
		this.brush = new BrushGroup(pallet);
		this.overlayBrush = new BrushGroup(opallet);
		this.margins = new float[4];
		this.update();
		this.setCursor(-1);
	}

	public void onAttachedToParent(Element parent) {
		this.parent = parent;
	}

	public void onDettachedFromParent(Element parent) {
		this.parent = null;
	}

	@Override
	public boolean isDestroyed() {
		return destroyed;
	}

	public void setDestroyed(boolean destroyed) {
		this.destroyed = destroyed;
	}

	protected java.util.concurrent.CopyOnWriteArrayList<ElementCarrier> elements = new java.util.concurrent.CopyOnWriteArrayList<ElementCarrier>();

	public Element attach(ElementCarrier element) {
		if(element == null)
			throw new RuntimeException("Element trying to attach to:"+this+" was resolved to null.");
		if(element == this)
			throw new RuntimeException("An element can not be added to itself.");
		if(element.getElement().parent != null) {
			element.getElement().parent.dettach(element);
		}
		element.getElement().onAttachedToParent(this);
		elements.add(element);
		return element.getElement();
	}

	public Element dettach(ElementCarrier element) {
		if(element == null)
			throw new RuntimeException("Element trying to dettach from:"+this+" was resolved to null.");
		if(element == this)
			throw new RuntimeException("An element can not be added to itself.");
		if(elements.remove(element)) {
			element.getElement().onDettachedFromParent(this);
			elements.remove(element);
			return element.getElement();
		}
		return null;
	}

	public static String style(String element) {
		return DEFAULT_STYLES+"."+element;
	}

	@Override
	public Element getElement() {
		return this;
	}

	public final void build() {
		String style = style();
		if(style == null) {
			built = true;
			return;
		}
		setStyle(style);
		built = true;
	}

	public Element setStyle(String style) {
		Context context = Cache.asset(style, Context.class);
		if(context == null)
			throw new RuntimeException("No style " + style + " was found.");
		Set<Entry<Object, Object>> pallet = context.entrySet();
		for(Entry<Object, Object> entry : pallet) {
			String key  = entry.getKey().toString();
			Brush brush;
			if(key.charAt(0) == '~') {
				brush = addOverlayBrush(key.substring(1));
			} else {
				brush = addBrush(key);
			}
			Context resolve = (Context) entry.getValue();
			brush.sap(resolve);
		}
		return this;
	}

	public void update() {
		refresh();
		overlay = overlayForState();
	}

	public float getWidthOnScreen() {
		float value =  width();
		value -= margin(Entity.MARGIN_LEFT);
		value -= margin(Entity.MARGIN_RIGHT);
		return Math.abs(value);
	}

	public float getHeightOnScreen() {
		float value = height();
		value -= margin(Entity.MARGIN_TOP);
		value -= margin(Entity.MARGIN_BOTTOM);
		return Math.abs(value);
	}

	@Override
	public abstract float getXOnScreen();
	@Override
	public abstract float getYOnScreen();

	public abstract float getContentWidth();
	public abstract float getContentHeight();

	public abstract void  refresh();
	public abstract void  display();
	public abstract float width();
	public abstract float height();
	public abstract float x();
	public abstract float y();
	public abstract String  style();
	public abstract boolean mouse(MouseEvent event);
	public abstract void    key  (KeyEvent   event);
	public abstract void    state(int state, boolean inState);

	@Override
	public float getScreenX() {
		return 0F;
	}

	@Override
	public float getScreenY() {
		return 0F;
	}

	public float getOuterX() {
		return getXOnScreen()+getWidthOnScreen();
	}

	public float getOuterY() {
		return getYOnScreen()+getHeightOnScreen();
	}

	public void toState(int stated, boolean inState) {
		if(stated == State.FOCUSED) {
			if((inState && !is(State.FOCUSED)) || (!inState && is(State.FOCUSED))) { //focus on
				for(Element element : focusWhenFocused) {
					element.toState(stated, inState);
				}
			}
		}
		state.toState(stated, inState);
		if(stateListener != null) {
			stateListener.onStateChanged(stated, inState);
		}
	}

	public boolean is(int stated) {
		return state.is(stated);
	}

	@Override
	public State getState() {
		return state;
	}

	public void reset() {
		state.reset();
	}

	public Brush brushForState() {
		if(override != null)
			return brush(override);
		return is(State.PRESSED) ? brush(PRESSED) : (is(State.HOVERED) ? brush(HOVER) : (is(State.FOCUSED) ? brush(FOCUSED) : brush(STANDARD)));
	}

	public Brush overlayForState() {
		return is(State.PRESSED) ? overlayBrush(PRESSED) : (is(State.HOVERED) ? overlayBrush(HOVER) : (is(State.FOCUSED) ? overlayBrush(FOCUSED) : overlayBrush(STANDARD)));
	}

	public void scale(float scale) {
		setScaleX(scale + state.getScaleX());
		setScaleY(scale + state.getScaleY());
	}

	public void rotatex(float rotatex) {
		setRotationX(rotatex + state.getRotationX());
	}

	public void rotatey(float rotatey) {
		setRotationY(rotatey + state.getRotationY());
	}

	public void setScale(float scale) {
		state.setScale(scale);
	}

	public void setScaleX(float scalex) {
		state.setScaleX(scalex);
	}

	public void setScaleX(float scale, float sx, float sy) {
		state.setScaleX(scale);
		state.setScaleLocation(sx, sy);
	}

	public void setScaleY(float scaley) {
		state.setScaleY(scaley);
	}

	public void setScaleY(float scale, float sx, float sy) {
		state.setScaleY(scale);
		state.setScaleLocation(sx, sy);
	}

	public void setScale(float scale, float sx, float sy) {
		state.setScale(scale);
		state.setScaleLocation(sx, sy);
	}

	public void setScale(float scalex, float scaley) {
		state.setScaleX(scalex);
		state.setScaleY(scaley);
	}

	public void setScale(float scalex, float scaley, float sx, float sy) {
		state.setScaleX(scalex);
		state.setScaleY(scaley);
		state.setScaleLocation(sx, sy);
	}

	public void setRotationX(float rotation) {
		state.setRotationX(rotation);
	}

	public void setRotationX(float rotation, float rx, float ry) {
		state.setRotationX(rotation);
		state.setRotationLocation(rx, ry);
	}

	public void setRotationY(float rotation) {
		state.setRotationX(rotation);
	}

	public void setRotationY(float rotation, float rx, float ry) {
		state.setRotationX(rotation);
		state.setRotationLocation(rx, ry);
	}

	public float adhereX(float x, float y) {
		x -= getXOnScreen();
		y -= getYOnScreen();
		float sx = (getWidthOnScreen()  * state.getScaleXRatio());
		float sy = (getHeightOnScreen() * state.getScaleYRatio());
		x = ((1F/state.getScaleX()) * (x - sx)) + sx;
		y = ((1F/state.getScaleY()) * (y - sy)) + sy;
		float rx = (width()  * state.getRotationXRatio());
		float ry = (height() * state.getRotationYRatio());
		float dg = (float) ((state.getRotationX()/-180F)*Math.PI);
		float c = (float) Math.cos(dg);
		float s = (float) Math.sin(dg);
		float dx = (x-rx);
		float dy = (y-ry);
		x = rx + (int) (dx*c-dy*s);
	    y = ry + (int) (dx*s+dy*c);
	    x += getXOnScreen();
		y += getYOnScreen();
	    return x;
	}

	public float adhereY(float x, float y) {
		x -= getXOnScreen();
		y -= getYOnScreen();
		float sx = (getWidthOnScreen()  * state.getScaleXRatio());
		float sy = (getHeightOnScreen() * state.getScaleYRatio());
		x = ((1F/state.getScaleX()) * (x - sx)) + sx;
		y = ((1F/state.getScaleY()) * (y - sy)) + sy;
		float rx = (width()  * state.getRotationXRatio());
		float ry = (height() * state.getRotationYRatio());
		float dg = (float) ((state.getRotationX()/-180F)*Math.PI);
		float c = (float) Math.cos(dg);
		float s = (float) Math.sin(dg);
		float dx = (x-rx);
		float dy = (y-ry);
		x = rx + (int) (dx*c-dy*s);
	    y = ry + (int) (dx*s+dy*c);
	    x += getXOnScreen();
		y += getYOnScreen();
	    return y;
	}

	public boolean contains(float ox, float oy) {
		float x = adhereX(ox, oy) - getXOnScreen();
		float y = adhereY(ox, oy) - getYOnScreen();
		return x <= getWidthOnScreen() && x >= 0 && y <= getHeightOnScreen() && y >= 0;
	}

	public boolean intersects(ElementCarrier carrier) {
		Element element = carrier.getElement();
		float tx = carrier.getScreenX();
		float ty = carrier.getScreenY();

		float left  = getXOnScreen();
		float right = getOuterX();
		float top  =   getYOnScreen();
		float bottom = getOuterY();

		float bleft  = element.getXOnScreen();
		float bright = element.getOuterX();
		float btop  = element.getYOnScreen();
		float bbottom = element.getOuterY();
		bleft += tx;
		bright += tx;
		btop += ty;
		bbottom += ty;

		return bleft < right && left < bright
	               && btop < bottom && top < bbottom;
		//return x1 <= getWidthOnScreen() && x1 >= 0 && y1 <= getHeightOnScreen() && y1 >= 0;
	}


	public void build(Object o) {
		for(Brush brush : pallet.values()) {
			brush.build(o);
		}
		for(Brush brush : opallet.values()) {
			brush.build(o);
		}
	}

	public Brush addBrush(String tag) {
		Brush brush = Aura.getBrush();
		brush.addStyleChangeListener(this);
		pallet.put(tag, brush);
		return brush;
	}

	public Brush addOverlayBrush(String tag) {
		Brush brush = Aura.getBrush();
		brush.addStyleChangeListener(this);
		brush.setOverlay(true);
		opallet.put(tag, brush);
		return brush;
	}

	public Brush brush() {
		return brush;
	}

	public Brush brush(String... tag) {
		if(tag.length == 0) {
			return brush;
		}
		if(tag.length == 1) {
			return pallet.get(tag[0]);
		}
		HashMap<String, Brush> subpallet = new HashMap<String, Brush>();
		for(String s : tag) {
			if(pallet.containsKey(s)) {
				subpallet.put(s, pallet.get(s));
			}
		}
		return new BrushGroup(subpallet);
	}

	public Brush overlayBrush(String... tag) {
		if(tag.length == 0) {
			return brush;
		}
		if(tag.length == 1) {
			return opallet.get(tag[0]);
		}
		HashMap<String, Brush> subpallet = new HashMap<String, Brush>();
		for(String s : tag) {
			if(opallet.containsKey(s)) {
				subpallet.put(s, opallet.get(s));
			}
		}
		return new BrushGroup(subpallet);
	}

	public abstract Bounds getBounds();

	public boolean hasChildren() {
		return !elements.isEmpty();
	}

	public java.util.concurrent.CopyOnWriteArrayList<ElementCarrier> getElements() {
		return elements;
	}

	public abstract int getCursor(MouseEvent mouseEvent);

	protected Collection<Brush> brushes() {
		return pallet.values();
	}

	protected Collection<Brush> overlayBrushes() {
		return opallet.values();
	}

	public int getCursor() {
		return cursor;
	}

	public void setCursor(int cursor) {
		this.cursor = cursor;
	}

	public StateListener getStateListener() {
		return stateListener;
	}

	public void setStateListener(StateListener stateListener) {
		this.stateListener = stateListener;
	}

	public MouseListener getMouseListener() {
		return mouseListener;
	}

	public void setMouseListener(MouseListener mouseListener) {
		this.mouseListener = mouseListener;
	}

	public KeyListener getKeyListener() {
		return keyListener;
	}

	public void setKeyListener(KeyListener keyListener) {
		this.keyListener = keyListener;
	}

	@Override
	public boolean isBuilt() {
		return built;
	}

	public void lock() {
		this.locked = true;
	}

	public Brush getOverlayBrush() {
		return overlayBrush;
	}

	public void overlay() {
		float w = getWidthOnScreen();
		float h = getHeightOnScreen();
		if(overlay != null)
			overlay.rect(w, h);
	}

	public DraggingPolicy getDraggingPolicy() {
		return draggingPolicy;
	}

	public void setDraggingPolicy(DraggingPolicy draggingPolicy) {
		this.draggingPolicy = draggingPolicy;
	}

	public abstract void logic();

	public void setOverrideBrush(String override) {
		this.override = override;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public State getTrueState() {
		return null;
	}

	@Override
	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

	public DraggedInListener getDraggedInListener() {
		return draggedInListener;
	}

	public void setDraggedInListener(DraggedInListener draggedInListener) {
		this.draggedInListener = draggedInListener;
	}

	public void focusOnWhenFocused(Element element) {
		focusWhenFocused.add(element);
	}

	public boolean isSpecialFocus() {
		return specialFocus;
	}

	public void setSpecialFocus(boolean specialFocus) {
		this.specialFocus = specialFocus;
	}

}
