package com.aura.model.elements;

import java.util.HashMap;

import com.aura.Brush;
import com.aura.input.KeyEvent;
import com.aura.input.MouseEvent;
import com.aura.input.MouseEvent.Mouse;
import com.aura.model.Bounds;
import com.aura.model.Element;
import com.aura.model.Entity;
import com.aura.model.Location.Hook;

public class Button extends Entity {

	private Panel background;
	private Text text;

	public Button(Bounds bounds, Object string) {
		super(bounds);
		bounds = Bounds.observe(this);
		text = new Text(bounds, string.toString());
		background = new Panel(bounds);
		setPolicy(Policy.PRIORITY_OVER_CHILDREN, true);
		attach(background, Hook.C);
		attach(text, Hook.C);
		text.lock();
		background.lock();
		styleChildren();
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
	public void mapChildren(HashMap<String, Element> children) {
		children.put("text", text);
		children.put("background", background);
	}

	public Text getText() {
		return text;
	}

	public Panel getBackground() {
		return background;
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
	protected Button recreate() {
		return new Button(bounds, text.getText());
	}

	@Override
	public void refresh() {
	}

	@Override
	public String style() {
		return style("button");
	}

	@Override
	public boolean mouse(MouseEvent event) {
		return true;
	}

	@Override
	public void key(KeyEvent event) {
	}

	@Override
	public void state(int state, boolean inState) {
		update();
	}

	@Override
	public int getCursor(MouseEvent mouseEvent) {
		return Mouse.HAND;
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
