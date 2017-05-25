package com.aura.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import com.aura.Brush;
import com.aura.Brush.BrushGroup;
import com.aura.ElementCarrier;
import com.aura.input.KeyEvent;
import com.aura.input.KeyListener;
import com.aura.input.MouseEvent;
import com.aura.input.MouseListener;
import com.aura.model.Location.Hook;

public class Group extends Element implements Iterable<Element> {

	private HashSet<Element> elements;
	private Element selected;

	public Group() {
		this.elements = new HashSet<Element>();
		this.selected = this;
	}

	public Group(Collection<Element> elements) {
		this();
		this.elements.addAll(elements);
		if(!this.elements.isEmpty())
			this.selected = this.elements.iterator().next();
	}

	public Group(HashSet<Element> elements) {
		this.elements = elements;
		if(!elements.isEmpty())
			this.selected = elements.iterator().next();
	}

	@Override
	public String toString() {
		return elements.toString();
	}

	public boolean exists() {
		return !elements.isEmpty();
	}

	public static Group merge(Group alpha, Group beta) {
		HashSet<Element> elements = new HashSet<Element>();
		elements.addAll(alpha.elements);
		elements.addAll(beta.elements);
		return new Group(elements);
	}

	public Element select() {
		return selected;
	}

	@SuppressWarnings("unchecked")
	public <T> T select(Class<T> expected) {
		return (T) selected;
	}

	@Override
	public void onChange(Brush brush, String style, Object before, Object after) {
	}

	@Override
	protected Element recreate() {
		return selected.recreate();
	}

	@Override
	public void bound(Bounds bounds) {
		for(Element element : elements) {
			element.bound(bounds);
		}
	}

	@Override
	public void locate(Location location) {
		for(Element element : elements) {
			element.locate(location);
		}
	}

	@Override
	public float getXOnScreen() {
		return selected.getXOnScreen();
	}

	@Override
	public float getYOnScreen() {
		return selected.getYOnScreen();
	}

	@Override
	public void refresh() {
		for(Element element : elements) {
			element.refresh();
		}
	}

	@Override
	public void display() {
		throw new RuntimeException("This selector should not be displayed.");
	}

	@Override
	public float width() {
		return selected.width();
	}

	@Override
	public float height() {
		return selected.height();
	}

	@Override
	public float x() {
		return selected.x();
	}

	@Override
	public float y() {
		return selected.y();
	}

	@Override
	public String style() {
		return null;
	}

	@Override
	public boolean mouse(MouseEvent event) {
		return false;
	}

	@Override
	public void key(KeyEvent event) {
	}

	@Override
	public void state(int state, boolean inState) {
	}

	@Override
	public Bounds getBounds() {
		return null;
	}

	@Override
	public int getCursor(MouseEvent mouseEvent) {
		return 0;
	}

	@Override
	public void logic() {
	}

	@Override
	public Iterator<Element> iterator() {
		return elements.iterator();
	}

	@Override
	public void setMargin(int margin, float value) {
		for(Element element : elements) {
			element.setMargin(margin, value);
		}
	}

	@Override
	public void setMargin(float value) {
		for(Element element : elements) {
			element.setMargin(value);
		}
	}

	@Override
	public float margin(int margin) {
		return selected.margin(margin);
	}

	@Override
	public <T extends ElementCarrier> T copy(Class<T> expected) {
		throw new RuntimeException("This opperation is not supported by a selector.");
	}

	@Override
	public <T extends ElementCarrier> T copy(Class<T> expected,
			boolean listeners) {
		throw new RuntimeException("This opperation is not supported by a selector.");
	}

	@Override
	protected void styleChildren() {
		throw new RuntimeException("This opperation is not supported by a selector.");
	}

	@Override
	protected void mapChildren(HashMap<String, Element> children) {
		throw new RuntimeException("This opperation is not supported by a selector.");
	}

	@Override
	public <T extends ElementCarrier> ElementCarrier copy(Location location,
			Class<T> expected, boolean listeners) {
		throw new RuntimeException("This opperation is not supported by a selector.");
	}

	@Override
	public void copyStyleOf(Element other) {
		for(Element element : elements) {
			element.copyStyleOf(other);
		}
	}

	@Override
	public void setPolicy(int policy, boolean set) {
		for(Element element : elements) {
			element.setPolicy(policy, set);
		}
	}

	@Override
	public boolean isPolicy(int policy) {
		return selected.isPolicy(policy);
	}

	@Override
	public Element locate(Element other, Hook where) {
		for(Element element : elements) {
			element.locate(other, where);
		}
		return this;
	}

	@Override
	public Element locate(Hook on, Element other, Hook where) {
		for(Element element : elements) {
			element.locate(on, other, where);
		}
		return this;
	}

	@Override
	public Element locate(float x, float y) {
		for(Element element : elements) {
			element.locate(x, y);
		}
		return this;
	}

	@Override
	public Element attach(Hook where, Element element, Hook on) {
		throw new RuntimeException("This opperation is not supported by a selector.");
	}

	@Override
	public Element attach(Element element, Hook hook) {
		throw new RuntimeException("This opperation is not supported by a selector.");
	}

	@Override
	public void translate(float x, float y) {
		for(Element element : elements) {
			element.translate(x, y);
		}
	}

	@Override
	public HashSet<Element> children() {
		throw new RuntimeException("Iterate through the selector rather then call it's children. ");
	}

	@Override
	public void onAttachedToParent(Element parent) {
		throw new RuntimeException("This opperation is not supported by a selector.");
	}

	@Override
	public void onDettachedFromParent(Element parent) {
		throw new RuntimeException("This opperation is not supported by a selector.");
	}

	@Override
	public boolean isDestroyed() {
		return selected.isDestroyed();
	}

	@Override
	public void setDestroyed(boolean destroyed) {
		throw new RuntimeException("This opperation is not supported by a selector.");
	}

	@Override
	public Element attach(ElementCarrier element) {
		throw new RuntimeException("This opperation is not supported by a selector.");
	}

	@Override
	public Element dettach(ElementCarrier element) {
		throw new RuntimeException("This opperation is not supported by a selector.");
	}

	@Override
	public Element getElement() {
		return selected;
	}

	@Override
	public Element setStyle(String style) {
		for(Element element : elements) {
			element.setStyle(style);
		}
		return this;
	}

	@Override
	public void update() {
		if(elements != null)
			for(Element element : elements) {
				element.update();
			}
	}

	@Override
	public float getWidthOnScreen() {
		return selected.getWidthOnScreen();
	}

	@Override
	public float getHeightOnScreen() {
		return selected.getHeightOnScreen();
	}

	@Override
	public void toState(int stated, boolean inState) {
		for(Element element : elements) {
			element.toState(stated, inState);
		}
	}

	@Override
	public boolean is(int stated) {
		return selected.is(stated);
	}

	@Override
	public State getState() {
		return selected.getState();
	}

	@Override
	public void reset() {
		for(Element element : elements) {
			element.reset();
		}
	}

	@Override
	public Brush brushForState() {
		return selected.brushForState();
	}

	@Override
	public Brush overlayForState() {
		return selected.overlayForState();
	}

	@Override
	public void scale(float scale) {
		for(Element element : elements) {
			element.scale(scale);
		}
	}

	@Override
	public void rotatex(float rotatex) {
		for(Element element : elements) {
			element.rotatex(rotatex);
		}
	}

	@Override
	public void rotatey(float rotatey) {
		for(Element element : elements) {
			element.rotatey(rotatey);
		}
	}

	@Override
	public void setScale(float scale) {
		for(Element element : elements) {
			element.setScale(scale);
		}
	}

	@Override
	public void setScaleX(float scalex) {
		for(Element element : elements) {
			element.setScaleX(scalex);
		}
	}

	@Override
	public void setScaleX(float scale, float sx, float sy) {
		for(Element element : elements) {
			element.setScaleX(scale, sx, sy);
		}
	}

	@Override
	public void setScaleY(float scaley) {
		for(Element element : elements) {
			element.setScaleY(scaley);
		}
	}

	@Override
	public void setScaleY(float scale, float sx, float sy) {
		for(Element element : elements) {
			element.setScaleY(scale, sx, sy);
		}
	}

	@Override
	public void setScale(float scale, float sx, float sy) {
		for(Element element : elements) {
			element.setScale(scale, sx, sy);
		}
	}

	@Override
	public void setScale(float scalex, float scaley) {
		for(Element element : elements) {
			element.setScale(scalex, scaley);
		}
	}

	@Override
	public void setScale(float scalex, float scaley, float sx, float sy) {
		for(Element element : elements) {
			element.setScale(scalex, scaley, sx, sy);
		}
	}

	@Override
	public void setRotationX(float rotation) {
		for(Element element : elements) {
			element.setRotationX(rotation);
		}
	}

	@Override
	public void setRotationX(float rotation, float rx, float ry) {
		for(Element element : elements) {
			element.setRotationX(rotation, rx, ry);
		}
	}

	@Override
	public void setRotationY(float rotation) {
		for(Element element : elements) {
			element.setRotationY(rotation);
		}
	}

	@Override
	public void setRotationY(float rotation, float rx, float ry) {
		for(Element element : elements) {
			element.setRotationY(rotation, rx, ry);
		}
	}

	@Override
	public float adhereX(float x, float y) {
		throw new RuntimeException("This opperation is not supported by a selector.");
	}

	@Override
	public float adhereY(float x, float y) {
		throw new RuntimeException("This opperation is not supported by a selector.");
	}

	@Override
	public boolean contains(float ox, float oy) {
		throw new RuntimeException("This opperation is not supported by a selector.");
	}

	@Override
	public void build(Object o) {
		throw new RuntimeException("This opperation is not supported by a selector.");
	}

	@Override
	public Brush addBrush(String tag) {
		ArrayList<Brush> brushes = new ArrayList<Brush>();
		for(Element element : elements) {
			brushes.add(element.addBrush(tag));
		}
		return new Brush.BrushGroup(brushes);
	}

	@Override
	public Brush addOverlayBrush(String tag) {
		ArrayList<Brush> brushes = new ArrayList<Brush>();
		for(Element element : elements) {
			brushes.add(element.addOverlayBrush(tag));
		}
		return new Brush.BrushGroup(brushes);
	}

	@Override
	public Brush brush() {
		ArrayList<Brush> brushes = new ArrayList<Brush>();
		for(Element element : elements) {
			brushes.addAll(element.brushes());
		}
		return new Brush.BrushGroup(brushes);
	}

	@Override
	public Brush brush(String... tag) {
		ArrayList<Brush> brushes = new ArrayList<Brush>();
		for(Element element : elements) {
			brushes.addAll(((BrushGroup)element.brush(tag)).getPallet());
		}
		return new Brush.BrushGroup(brushes);
	}

	@Override
	public Brush overlayBrush(String... tag) {
		ArrayList<Brush> brushes = new ArrayList<Brush>();
		for(Element element : elements) {
			brushes.addAll(((BrushGroup)element.overlayBrush(tag)).getPallet());
		}
		return new Brush.BrushGroup(brushes);
	}

	@Override
	public boolean hasChildren() {
		return selected.hasChildren();
	}

	@Override
	public CopyOnWriteArrayList<ElementCarrier> getElements() {
		throw new RuntimeException("Iterate through this selector rather than call it's elements.");
	}

	@Override
	protected Collection<Brush> brushes() {
		return ((BrushGroup)brush()).getPallet();
	}

	@Override
	protected Collection<Brush> overlayBrushes() {
		return ((BrushGroup)overlayBrush()).getPallet();
	}

	@Override
	public int getCursor() {
		return selected.getCursor();
	}

	@Override
	public void setCursor(int cursor) {
		if(elements != null)
			for(Element element : elements) {
				element.setCursor(cursor);
			}
	}

	@Override
	public StateListener getStateListener() {
		return selected.getStateListener();
	}

	@Override
	public void setStateListener(StateListener stateListener) {
		for(Element element : elements) {
			element.setStateListener(stateListener);
		}
	}

	@Override
	public MouseListener getMouseListener() {
		return selected.getMouseListener();
	}

	@Override
	public void setMouseListener(MouseListener mouseListener) {
		for(Element element : elements) {
			element.setMouseListener(mouseListener);
		}
	}

	@Override
	public KeyListener getKeyListener() {
		return selected.getKeyListener();
	}

	@Override
	public void setKeyListener(KeyListener keyListener) {
		for(Element element : elements) {
			element.setKeyListener(keyListener);
		}
	}

	@Override
	public boolean isBuilt() {
		return selected.isBuilt();
	}

	@Override
	public void lock() {
		for(Element element : elements) {
			element.lock();
		}
	}

	@Override
	public Brush getOverlayBrush() {
		return selected.getOverlayBrush();
	}

	@Override
	public void overlay() {
		throw new RuntimeException("This opperation is not supported by a selector.");
	}

	@Override
	public DraggingPolicy getDraggingPolicy() {
		return selected.getDraggingPolicy();
	}

	@Override
	public void setDraggingPolicy(DraggingPolicy draggingPolicy) {
		for(Element element : elements) {
			element.setDraggingPolicy(draggingPolicy);
		}
	}

	@Override
	public void setOverrideBrush(String override) {
		for(Element element : elements) {
			element.setOverrideBrush(override);
		}
	}

	@Override
	public String getId() {
		return selected.getId();
	}

	@Override
	public void setId(String id) {
		for(Element element : elements) {
			element.setId(id);
		}
	}

	@Override
	public boolean draggedIn(Element what, float x, float y) {
		return false;
	}

	@Override
	public Object toClipboard() {
		throw new RuntimeException("This opperation is not supported by a selector.");
	}

	@Override
	public void paste(Object object) {
		for(Element element : elements) {
			element.paste(object);
		}
	}

	@Override
	public float getContentWidth() {
		float width = 0F;
		for(Element element : elements) {
			width += element.getWidthOnScreen();
		}
		return width;
	}

	@Override
	public float getContentHeight() {
		float height = 0F;
		for(Element element : elements) {
			height += element.getHeightOnScreen();
		}
		return height;
	}

}
