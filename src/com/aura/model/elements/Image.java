package com.aura.model.elements;

import com.aura.Brush;
import com.aura.input.KeyEvent;
import com.aura.input.MouseEvent;
import com.aura.input.MouseEvent.Mouse;
import com.aura.model.Bounds;
import com.aura.model.Element;
import com.aura.model.Entity;
import com.aura.model.Texture;

public class Image extends Entity {

	private Brush brush;

	public Image(Bounds bounds, Texture texture) {
		super(bounds);
		setImage(texture);
	}

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
	public void render(float width, float height) {
		brush.rect(width, height);
	}

	@Override
	public void refresh() {
		brush = brushForState();
	}

	@Override
	public String style() {
		return style("image");
	}

	@Override
	public boolean mouse(MouseEvent event) {
		return true;
	}

	@Override
	public void state(int state, boolean inState) {
		update();
	}

	@Override
	public int getCursor(MouseEvent mouseEvent) {
		return Mouse.NORMAL;
	}

	public void setImage(Texture image, String...brushes) {
		brush(brushes).setTexture(image);
	}

	@Override
	protected Image recreate() {
		return new Image(bounds, null);
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
		return brush.getTexture();
	}

	@Override
	public void paste(Object object) {
	}

}
