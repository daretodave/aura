package com.aura.model.elements;

import com.aura.Brush;
import com.aura.input.KeyEvent;
import com.aura.input.MouseEvent;
import com.aura.model.Bounds;
import com.aura.model.Element;
import com.aura.model.Entity;

public class Panel extends Entity {

	private Brush background;

	public Panel(Bounds bounds) {
		super(bounds);
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
	public void render(float width, float height) {
		background.rect(width, height);
	}

	@Override
	public String style() {
		return style("panel");
	}

	@Override
	public void refresh() {
		background = brushForState();
	}

	@Override
	public boolean mouse(MouseEvent event) {
		return true;
	}

	@Override
	public void state(int state, boolean inState) {
		update();
		//System.out.println("Hey " + state + ", " + inState + ", " + State.FOCUSED);
	}

	@Override
	public int getCursor(MouseEvent mouseEvent) {
		return MouseEvent.Mouse.NORMAL;
	}

	@Override
	public void onChange(Brush brush, String style, Object before, Object after) {
	}

	@Override
	protected Panel recreate() {
		return new Panel(bounds);
	}

	@Override
	public void key(KeyEvent event) {
	}

	@Override
	public void onBoundChange() {
	}

	@Override
	public boolean draggedIn(Element what, float x, float y) {
		return false;
	}

	@Override
	public Object toClipboard() {
		return null;
	}

	@Override
	public void paste(Object object) {
	}

}
