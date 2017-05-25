package com.aura.model.elements;

import java.util.HashMap;

import com.aura.Brush;
import com.aura.Brush.TextComputation;
import com.aura.Brush.TextSnippet;
import com.aura.input.KeyEvent;
import com.aura.input.MouseEvent;
import com.aura.input.MouseEvent.Mouse;
import com.aura.model.Bounds;
import com.aura.model.DraggingPolicy;
import com.aura.model.Element;
import com.aura.model.Entity;

public class Text extends Entity {

	protected boolean bounded;
	private HashMap<Brush, TextComputation> computed;
	private TextComputation rendered;
	private Brush tbrush;
	private String text;
	private Bounds.Mold bounds;
	private boolean hiddenTextMode;

	private String hint;

	public static final String HINT = "hint";

	private static final String HIDDEN_TEXT = "\u2022";

	public Text(Bounds bounds, String text, String hint) {
		super(bounds);
		this.bounded = bounds == null;
		this.text = text;
		this.hint = hint;
		this.bounds = new Bounds.Mold(0F, 0F);
		if (bounded) {
			super.bounds = this.bounds;
		}
		this.setDraggingPolicy(new DraggingPolicy());
		computed = new HashMap<Brush, TextComputation>();
		compute(null);
		update();
	}

	@Override
	public float getContentWidth() {
		return -1;
	}

	@Override
	public float getContentHeight() {
		return -1;
	}

	public Text() {
		this("");
	}

	public Text(Bounds bounds, String text) {
		this(bounds, text, "");
	}

	public Text(Bounds bounds) {
		this(bounds, "");
	}


	public Text(String text) {
		this(null, text);
	}

	private void compute(Brush brush) {
		if (brush == null) {
			for (Brush resolve : brushes()) {
				compute(resolve);
			}
			return;
		}
		computed.put(brush, brush.calculateTextBounds(text.isEmpty() ? hint : (hiddenTextMode ? password(text) : text), bounded ? null : getBounds()));

	}

	private String password(String text) {
		String resolve = "";
		for(int i = 0; i < text.length(); i++) {
			resolve += HIDDEN_TEXT + "";
		}
		return resolve;
	}

	@Override
	public void render(float width, float height) {
		for (TextSnippet snippet : rendered.getSnippets()) {
			tbrush.text(snippet.getText(), snippet.getX(), snippet.getY(),
					width, height);
		}
	}

	@Override
	public void refresh() {
		if (computed != null) {
			tbrush = text.isEmpty() ?  brush(HINT) : brushForState();
			if(!computed.containsKey(tbrush)) {
				compute(tbrush);
			}
			rendered = computed.get(tbrush);
			bounds.setWidth(rendered.getBounds().getWidth());
			bounds.setHeight(rendered.getBounds().getHeight());
		}
	}

	@Override
	public String style() {
		return "text";
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

	@Override
	public void onChange(Brush brush, String style, Object before, Object after) {
		if (style == Brush.FILL || style == Brush.OUTLINE
				|| style == Brush.OTHER) {
			return;
		}
		if (brush == null) {
			switch (style) {
			case Element.MARGIN_LEFT_KEY:
			case Element.MARGIN_RIGHT_KEY:
			case Element.MARGIN_BOTTOM_KEY:
			case Element.MARGIN_TOP_KEY:
				compute(null);
				update();
				break;
			case Entity.BOUNDS_KEY:
				if (before == after) {
					return;
				}
				if (bounded && after != null) {
					bounded = false;
				} else if (!bounded && after == null) {
					bounded = true;
					bound(bounds);
				}
				compute(null);
				update();
				break;
			}
			return;
		}
		if (brush.isOverlay()) {
			return;
		}
		compute(brush);
		update();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		text = text == null ? "" : text;
		if (this.text == text) {
			return;
		}
		this.text = text;
		compute(null);
		update();
	}

	@Override
	protected Text recreate() {
		Text text = new Text(null, this.text, hint);
		return text;
	}

	@Override
	public void key(KeyEvent event) {
	}

	@Override
	public void onBoundChange() {
		if (!bounded) {
			compute(null);
			update();
		}
	}


	public void remove(int amount) {
		if (text.isEmpty()) {
			return;
		}
		amount = Math.min(amount, text.length());
		setText(text.substring(0, text.length() - amount));
	}

	public void append(String text) {
		setText(this.text + text);
	}

	public boolean isEmpty() {
		return text.isEmpty();
	}

	public int size() {
		return text.length();
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
		compute(brush(HINT));
		update();
	}

	@Override
	public boolean draggedIn(Element what, float x, float y) {
		return false;
	}

	@Override
	public Object toClipboard() {
		return hiddenTextMode ? password(text) : text;
	}

	@Override
	public void paste(Object object) {
	}

	public boolean isHiddenTextMode() {
		return hiddenTextMode;
	}

	public void setHiddenTextMode(boolean hiddenTextMode) {
		this.hiddenTextMode = hiddenTextMode;
	}

}
