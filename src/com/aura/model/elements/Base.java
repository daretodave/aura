package com.aura.model.elements;

import com.aura.Brush;
import com.aura.input.KeyEvent;
import com.aura.input.MouseEvent;
import com.aura.model.Bounds;
import com.aura.model.Element;
import com.aura.model.Entity;

public class Base extends Entity {

	public Base(Bounds bounds) {
		super(bounds);
	}

	@Override
	public void onChange(Brush brush, String style, Object before, Object after) {
	}

	@Override
	public void onBoundChange() {
	}

	@Override
	public void render(float width, float height) {
	}

	@Override
	protected Element recreate() {
		return new Base(bounds);
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
	public void refresh() {
	}

	@Override
	public String style() {
		return style("blank");
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
	public int getCursor(MouseEvent mouseEvent) {
		return 0;
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
