package com.aura;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import com.aura.cache.Asset;
import com.aura.input.Key;
import com.aura.input.KeyEvent;
import com.aura.input.MouseEvent;
import com.aura.input.MouseEvent.Mouse;
import com.aura.model.DraggingPolicy;
import com.aura.model.Element;
import com.aura.model.State;
import com.aura.model.Texture;

public abstract class AWindow extends Element implements Runnable {

	protected int clear;
	protected Context context;
	protected Brush brush;
	protected java.util.concurrent.CopyOnWriteArrayList<ElementCarrier> external;

	public static final String TITLE  	    = "title";
	public static final String WIDTH  	    = "width";
	public static final String HEIGHT 		= "height";
	public static final String CLEAR_COLOR  = "clear";
	public static final String DECOR 		= "decor";
	public static final String RESIZABLE 	= "resize";
	public static final String ICON    	    = "icon";
	public static final String BACKGROUND   = "bg";

	private Texture background;
	private long update;
	private int frames;
	private int fps;

	public abstract void resize(float width, float height);

	private Future<?> resizer;

	public void resize(float width, float height, float time) {
		resize(width, height, time, null);
	}

	public void resize(final float width, final float height, final float time, final Runnable onEnd) {
		if(AWindow.this.getWindowWidth() == width && AWindow.this.getWindowHeight() == height) {
			return;
		}
		if(time == 0F) {
			resize(width, height);
		} else {
			if(resizer != null && !resizer.isDone()) {
				resizer.cancel(true);
			}
			resizer = Aura.submit(new Runnable() {
				final long start = System.currentTimeMillis();
				final float swidth  = AWindow.this.getWindowWidth();
				final float sheight = AWindow.this.getWindowHeight();
				final float dwidth  = width -swidth;
				final float dheight = height-sheight;
				@Override
				public void run() {
					while(true) {
						long lapse   = System.currentTimeMillis() - start;
						if(lapse >= time) {
							resizer = null;
							resize(width, height);
							if(onEnd != null) {
								onEnd.run();
							}
							break;
						}
						float factor = lapse/time;
						try {
							resize(swidth + dwidth*factor, sheight + dheight*factor);
							Thread.sleep(10L);
						} catch(Exception e) {
							break;
						}
					}
				}
			});
		}
	}

	@Override
	protected AWindow recreate() {
		return Aura.build(context);
	}

	public static final class Decor {
		public static final int UNFRAMED = 0x0000;
		public static final int FRAMED   = 0x0001;
		public static final int STYLED   = 0x0002;
	}

	public static final class Type {
		public static final int MOVE    = 0x0000;
		public static final int PRESS   = 0x0001;
		public static final int RELEASE = 0x0002;
		public static final int DRAG    = 0x0003;
	}


	@Override
	public Object toClipboard() {
		return null;
	}

	@Override
	public void paste(Object object) {
	}

	public abstract void setLocation(float x, float y);

	@Override
	public void translate(float x, float y) {
		setLocation(getWindowX()+x, getWindowY()+y);
	}

	public static final class StatedElement extends ElementCarrier {

		@Override
		public Element getElement() {
			return element;
		}
		public void setElement(Element element) {
			this.element = element;
		}
		@Override
		public State getState() {
			return state;
		}
		public void setState(State state) {
			this.state = state;
		}
		public StatedElement(Element element, State state) {
			super();
			this.element = element;
			this.state = state;
			this.sx = element.getXOnScreen();
			this.sy = element.getYOnScreen();
		}
		@Override
		public boolean isDestroyed() {
			return destroyed;
		}
		public void setDestroyed(boolean destroyed) {
			this.destroyed = destroyed;
		}
		private Element element;
		private State state;
		private boolean destroyed;
		private float sx;
		private float sy;
		@Override
		public State getTrueState() {
			return element.getState();
		}
		@Override
		public boolean isHidden() {
			return false;
		}
		@Override
		public float getScreenX() {
			return state.getTx();
		}
		@Override
		public float getScreenY() {
			return state.getTy();
		}
		@Override
		public float getXOnScreen() {
			return sx;
		}
		@Override
		public float getYOnScreen() {
			return sy;
		}

	}

	public AWindow() {
		super();
		this.external = new java.util.concurrent.CopyOnWriteArrayList<ElementCarrier>();
	}

	public void setDraggable(boolean draggable) {
		setDraggingPolicy(draggable ? new DraggingPolicy() : null);
	}

	public void attachExternal(StatedElement element) {
		this.external.add(element);
	}

	public void setBackground(Texture image) {
		this.background = image;
	}

	public void setBackground(Asset asset) {
		setBackground(getImage(asset));
	}

	public void setBackground(String folder, String asset) {
		setBackground(getImage(folder, asset));
	}

	public void setBackground(String asset) {
		setBackground(getImage(asset));
	}

	public void setIcon(Asset asset) {
		setIcon(getImage(asset));
	}

	public void setIcon(String folder, String asset) {
		setIcon(getImage(folder, asset));
	}

	public void setIcon(String asset) {
		setIcon(getImage(asset));
	}

	public Texture getImage(String folder, String asset) {
		return getImage(Asset.derive(folder, asset));
	}

	public Texture getImage(String asset) {
		return getImage(Asset.derive(null, asset));
	}

	public Object getFont(String folder, String asset) {
		return getFont(Asset.derive(folder, asset));
	}

	public Object getFont(String asset) {
		return getFont(Asset.derive(null, asset));
	}

	@Override
	public void refresh() {
		brush = brush("window");
	}

	public abstract Texture getImage(Asset asset);

	public abstract Object getFont(Asset asset);

	public abstract void setIcon(Texture image);

	public abstract void  build(Context context);

	public abstract void  open();

	public abstract void  close();

	public abstract void  setPointer(int type);

	@Override
	public abstract float getWidthOnScreen();

	@Override
	public abstract float getHeightOnScreen();

	public void keyboard(int type, int button, boolean ctrl, boolean shift) {
		HashSet<Element> children = children();
		ArrayList<Element> focused  = new ArrayList<Element>();
		if(is(State.FOCUSED)) {
			focused.add(this);
		}
		for(Element element : children) {
			if(element.is(State.FOCUSED)) {
				focused.add(element);
			}
		}
		Key key = Key.KEYS.get(button);
		String text = null;
		if(key != null) {
			text = shift ? key.getShiftOutput() : key.getOutput();
		}
		KeyEvent event = new KeyEvent(this, type, key, text, ctrl, shift, focused);
		event.fire();
	}

	public void mouse(int type, int button, float x, float y, boolean ctrl, boolean shift) {
		ArrayList<Element> tree     = new ArrayList<Element>();
		ArrayList<Element> antitree = new ArrayList<Element>();
		mouse(x, y, tree, antitree, this, type);
		Collections.reverse(tree);
		MouseEvent event = new MouseEvent(this, type, button, x, y, ctrl, shift, tree);
		if(type == Type.MOVE) {
			int cursor = Mouse.NORMAL;
			for(Element element : antitree) {
				element.toState(State.HOVERED, false);
			}
			while(!event.isEmpty()) {
				int resolve = event.invoke();
				if(cursor == Mouse.NORMAL) {
					cursor = resolve;
				}
			}
			setPointer(cursor);
		} else {
			event.fire();
			if(type == Type.DRAG) {
				for(Element element : antitree) {
					if(element.getDraggingPolicy() != null) {
						element.getDraggingPolicy().drag(this, element, x, y);
					}
				}
				for(Element element : tree) {
					if(element.getDraggingPolicy() != null) {
						element.getDraggingPolicy().drag(this, element, x, y);
					}
				}
			}
			if(type == Type.PRESS) {
				ArrayList<Element> nofocus  = new ArrayList<Element>();
				Element focused = null;
				for(Element element : antitree) {
					nofocus.add(element);
				}
				for(Element element : tree) {
					if(focused == null && !element.isPolicy(Policy.CANT_GAIN_FOCUS)) {
						focused = element;
					} else {
						nofocus.add(element);
					}
				}
				if(focused != null)
					focused.toState(State.FOCUSED, true);
				for(Element element : nofocus) {
					if(!element.isPolicy(Policy.CANT_LOOSE_FOCUS) && !element.isPolicy(Policy.ALWAYS_FOCUSED)) {
						element.toState(State.FOCUSED, false);
					}
				}
			}
			if(type == Type.RELEASE) {
				for(Element element : antitree) {
					element.toState(State.PRESSED, false);
					if(element.getDraggingPolicy() != null) {
						element.getDraggingPolicy().onFinish(this, element, x, y);
					}
				}
				for(Element element : tree) {
					element.toState(State.PRESSED, false);
					if(element.getDraggingPolicy() != null) {
						element.getDraggingPolicy().onFinish(this, element, x, y);
					}
				}
			}

		}
	}

	public void mouse(float x, float y, ArrayList<Element> tree, ArrayList<Element> antitree, Element element, int type) {
		boolean contains = element.contains(x, y) || (type == Type.DRAG && element.is(State.PRESSED));
		if(!element.visible) {
			return;
		}
		if(!element.isPolicy(Policy.PRIORITY_OVER_CHILDREN)) {
			if(contains) {
				tree.add(element);
			} else {
				antitree.add(element);
			}
		}
		if(element.hasChildren()) {
			float cx = element.adhereX(x, y);
			float cy = element.adhereY(x, y);
			for(ElementCarrier child : element.getElements()) {
				mouse(cx, cy, tree, antitree, child.getElement(), type);
			}
		}
		if(element.isPolicy(Policy.PRIORITY_OVER_CHILDREN)) {
			if(contains) {
				tree.add(element);
			} else {
				antitree.add(element);
			}
		}
	}

	public void render(Object o) {
		long time = System.currentTimeMillis();
		if(time - update > 1000L) {
			fps = frames;
			frames = 0;
			update = time;
			onFPSUpdate(fps);
		}
		brush.setFillColor(clear);
		brush.rect(getWidthOnScreen(), getHeightOnScreen());
		if(background != null) {
			brush.setTexture(background);
			brush.rect(getWidthOnScreen(), getHeightOnScreen());
			brush.setTexture(null);
		}
		brush.adhere(this);
		display();
		render(o, elements);
		brush.resetStack();
		render(o, external);
		brush.resetStack();
		frames++;
	}

	public abstract float getWindowX();
	public abstract float getWindowY();
	public abstract float getWindowWidth();
	public abstract float getWindowHeight();

	public abstract void onFPSUpdate(int fps);

	public void render(Object handshake, CopyOnWriteArrayList<ElementCarrier> elements) {
		Iterator<ElementCarrier> itr = elements.iterator();
		while (itr.hasNext()) {
			ElementCarrier resolve = itr.next();
			if(resolve.isHidden()) {
				continue;
			}
			Element element = resolve.getElement();
			element.logic();
			if(element.getXOnScreen() > width() || element.getXOnScreen()+element.getWidthOnScreen() < 0 || element.getYOnScreen() > height() || element.getYOnScreen()+element.getHeightOnScreen() < 0) {
				continue;
			}
			if(element.getState().getOpacity() <= 0) {
				continue;
			}
			if(resolve.isDestroyed()) {
				elements.remove(resolve);
			} else {
				Object alternate = brush.push();
				brush.build(alternate);
				element.build(alternate);
				brush.translate(resolve.getScreenX(), resolve.getScreenY());
				brush.adhere(resolve);
				if(brush.worthIt(element.getWidthOnScreen(), element.getHeightOnScreen())) {
					element.visible = true;
					element.display();
					brush.clip(element.getWidthOnScreen(), element.getHeightOnScreen());
					brush.untranslate(resolve);
					if(element.hasChildren()) {
						render(alternate, element.getElements());
					}
					brush.pop(alternate);
					brush.build(handshake);
					alternate = brush.push();
					brush.build(alternate);
					element.build(alternate);
					brush.translate(resolve.getScreenX(), resolve.getScreenY());
					brush.adhere(resolve);
				} else {
					element.visible = false;
				}
				element.overlay();
				brush.untranslate(resolve);
				brush.pop(alternate);
				brush.build(handshake);
			}
		}
	}

	public int getFps() {
		return fps;
	}

	private boolean exitsOnEscape;

	public abstract void setClearColor(int color);

	public void setExitOnEscape(boolean exits) {
		exitsOnEscape = exits;
	}

	@Override
	public void key(com.aura.input.KeyEvent event) {
		if(event.getKey() == Key.ESCAPE && exitsOnEscape) {
			System.exit(-1);
		}
	}

	@Override
	public float getContentWidth() {
		return -1;
	}

	@Override
	public float getContentHeight() {
		return -1;
	}

}
