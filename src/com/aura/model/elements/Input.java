package com.aura.model.elements;

import java.util.HashMap;

import com.aura.Brush;
import com.aura.Brush.Align;
import com.aura.input.Key;
import com.aura.input.KeyEvent;
import com.aura.input.MouseEvent;
import com.aura.model.Bounds;
import com.aura.model.Element;
import com.aura.model.Entity;
import com.aura.model.Location.Hook;
import com.aura.model.Value;

public class Input extends Entity implements Value {

	private Text text;
	private Panel background;
	private float padding;
	private OnTextChangeListener onTextChangeListener;

	public interface OnTextChangeListener {
		public void onChange(String current, String before, boolean copiedIn);
	}

	@Override
	public float getContentWidth() {
		return -1;
	}

	@Override
	public float getContentHeight() {
		return -1;
	}

	public Input(Bounds bounds) {
		super(bounds);
		bounds = Bounds.observe(this);
		padding = 20F;
		setPolicy(Policy.PRIORITY_OVER_CHILDREN, true);
		text = new Text(Bounds.implode(bounds, this), "");
		background = new Panel(bounds);
		attach(background, Hook.C);
		attach(text, Hook.C);
		text.brush().setTextVerticalAlign(Align.MIDDLE).setTextHorizontalAlign(Align.START);
		background.lock();
		text.lock();
		styleChildren();
	}

	public void setPadding(float padding) {
		this.padding = padding;
	}

	@Override
	public void mapChildren(HashMap<String, Element> children) {
		children.put("input", text);
		children.put("background", background);
	}

	public Text getText() {
		return text;
	}

	public Panel getBackground() {
		return background;
	}

	public String getInput() {
		return text.getText();
	}

	@Override
	public void onChange(Brush brush, String style, Object before, Object after) {
	}

	@Override
	public void render(float width, float height) {
	}

	@Override
	protected Element recreate() {
		return new Input(bounds);
	}

	@Override
	public void refresh() {
	}

	@Override
	public String style() {
		return style("input");
	}

	@Override
	public boolean mouse(MouseEvent event) {
		return false;
	}

	@Override
	public void key(KeyEvent event) {
		if(!event.isRelease()) {
			String prior = text.getText();
			Key key = event.getKey();
			if(key == null)
				return;
			switch(key) {
			case BACKSPACE:
				text.remove(1);
				onTextChangeListener.onChange(text.getText(), prior, false);
				break;
			default:
				if(event.getText() != null) {
					text.append(event.getText());
					onTextChangeListener.onChange(text.getText(), prior, false);
				}
				break;
			}
		}
	}

	public void setHiddenMode(boolean hidden) {
		text.setHiddenTextMode(hidden);
	}

	@Override
	public void state(int state, boolean inState) {
		update();
	}

	@Override
	public int getCursor(MouseEvent mouseEvent) {
		return MouseEvent.Mouse.TYPE;
	}

	@Override
	public float getValue() {
		return padding;
	}

	@Override
	public void onBoundChange() {
	}

	public void setHint(String hint) {
		text.setHint(hint);
	}

	@Override
	public boolean draggedIn(Element what, float x, float y) {
		if(what instanceof Text) {
			String prior = text.getText();
			text.append(((Text) what).getText());
			onTextChangeListener.onChange(text.getText(), prior, true);
			return true;
		}
		return false;
	}

	@Override
	public Object toClipboard() {
		return text.toClipboard();
	}

	@Override
	public void paste(Object object) {
		if(object instanceof String) {
			String prior = text.getText();
			text.append(object.toString());
			onTextChangeListener.onChange(text.getText(), prior, true);
		}
	}

	public OnTextChangeListener getOnTextChangeListener() {
		return onTextChangeListener;
	}

	public void setOnTextChangeListener(OnTextChangeListener onTextChangeListener) {
		this.onTextChangeListener = onTextChangeListener;
	}

}
