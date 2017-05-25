package com.aura.input;

import java.util.ArrayList;

import com.aura.AWindow;
import com.aura.AWindow.Type;
import com.aura.model.Element;
import com.aura.model.State;

public class MouseEvent implements Event {

	public static final class Mouse {
		public static final int NORMAL     = 0x0;
		public static final int HAND       = 0x1;
		public static final int TYPE       = 0x2;
		public static final int CROSSHAIRS = 0x3;
	}

	public int getType() {
		return type;
	}
	public int getButton() {
		return button;
	}
	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}
	public boolean isCtrl() {
		return ctrl;
	}
	public boolean isShift() {
		return shift;
	}
	public boolean isEmpty() {
		return index == queue.size()-1;
	}

	public MouseEvent(AWindow window, int type, int button, float x, float y, boolean ctrl,
			boolean shift, ArrayList<Element> queue) {
		this.type = type;
		this.button = button;
		this.x = x;
		this.y = y;
		this.ctrl = ctrl;
		this.shift = shift;
		this.queue = queue;
		this.window = window;
		this.index = -1;
	}

	private AWindow window;
	private final int type;
	private final int button;
	private final float x;
	private final float y;
	private final boolean ctrl;
	private final boolean shift;
	private final ArrayList<Element> queue;
	private int index;

	@Override
	public int invoke() {
		if(isEmpty()) {
			throw new RuntimeException("A mouse event was fired with no elements left in the queue.");
		}
		index++;
		Element handle = queue.get(index);
		if(type == AWindow.Type.MOVE) {
			handle.toState(State.HOVERED, true);
			int cursor = handle.getCursor(this);
			if(handle.getCursor() != -1) {
				return handle.getCursor();
			}
			return cursor;
		}
		if(type == AWindow.Type.RELEASE && !handle.is(State.PRESSED)) {
			return UNHANDLED;
		}
		if(type == AWindow.Type.PRESS) {
			handle.toState(State.PRESSED, true);
			if(handle.getDraggingPolicy() != null) {
				handle.getDraggingPolicy().init(handle, x, y);
			}
		}
		if(handle.getMouseListener() != null) {
			handle.getMouseListener().onMouseEvent(handle, this);
		}
		boolean handled = handle.mouse(this);
		return handled ? HANDLED : UNHANDLED;
	}

	@Override
	public void fire() {
		while(!isEmpty()) {
			if(invoke() == HANDLED)
				break;
		}
	}

	@Override
	public boolean isRelease() {
		return type == Type.RELEASE;
	}

}
