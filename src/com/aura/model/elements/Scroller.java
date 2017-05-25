package com.aura.model.elements;

import com.aura.Brush;
import com.aura.input.KeyEvent;
import com.aura.input.MouseEvent;
import com.aura.model.Bounds;
import com.aura.model.Element;
import com.aura.model.Location;

public class Scroller extends Element {

	@Override
	public void onChange(Brush brush, String style, Object before, Object after) {
	}

	@Override
	public float getContentWidth() {
		return -1;
	}

	@Override
	public float getContentHeight() {
		return -1;
	}

	@Override
	protected Element recreate() {
		return null;
	}

	@Override
	public Object toClipboard() {
		return null;
	}

	@Override
	public void paste(Object object) {
	}

	@Override
	public boolean draggedIn(Element what, float x, float y) {
		return false;
	}

	@Override
	public void bound(Bounds bounds) {
	}

	@Override
	public void locate(Location location) {
	}

	@Override
	public float getXOnScreen() {
		return 0;
	}

	@Override
	public float getYOnScreen() {
		return 0;
	}

	@Override
	public void refresh() {
	}

	@Override
	public void display() {
	}

	@Override
	public float width() {
		return 0;
	}

	@Override
	public float height() {
		return 0;
	}

	@Override
	public float x() {
		return 0;
	}

	@Override
	public float y() {
		return 0;
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

}
