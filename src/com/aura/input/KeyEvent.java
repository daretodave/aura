package com.aura.input;

import java.util.ArrayList;

import com.aura.AWindow;
import com.aura.AWindow.Type;
import com.aura.Aura;
import com.aura.model.Element;

public class KeyEvent implements Event {

	public AWindow getWindow() {
		return window;
	}
	public void setWindow(AWindow window) {
		this.window = window;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isControl() {
		return control;
	}
	public void setControl(boolean control) {
		this.control = control;
	}
	public boolean isShift() {
		return shift;
	}
	public void setShift(boolean shift) {
		this.shift = shift;
	}
	public ArrayList<Element> getQueue() {
		return queue;
	}
	public void setQueue(ArrayList<Element> queue) {
		this.queue = queue;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public KeyEvent(AWindow window, int type, Key key, String text,
			boolean control, boolean shift, ArrayList<Element> queue) {
		this.window = window;
		this.type = type;
		this.setKey(key);
		this.text = text;
		this.control = control;
		this.shift = shift;
		this.queue = queue;
		this.index = -1;
	}

	private AWindow window;
	private int type;
	private Key key;
	private String text;
	private boolean control;
	private boolean shift;
	private ArrayList<Element> queue;
	private int index;

	@Override
	public int invoke() {
		if(isEmpty()) {
			throw new RuntimeException("A mouse event was fired with no elements left in the queue.");
		}
		index++;
		Element handle = queue.get(index);
		if(handle.getKeyListener() != null) {
			handle.getKeyListener().onKeyEvent(handle, this);
		}
		if(type == Type.RELEASE && control && key == Key.C) {
				Object clip = handle.toClipboard();
				if(clip != null) {
					Aura.setClipboardObject(clip);
				}
		} else if(type == Type.PRESS && control && key == Key.V) {
				Object resolve = Aura.getClipboardContents();
				if(resolve != null) {
					handle.paste(resolve);
				}
		} else {
			handle.key(this);
		}
		return Event.UNHANDLED;
	}
	public boolean isEmpty() {
		return index == queue.size()-1;
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
	public Key getKey() {
		return key;
	}
	public void setKey(Key key) {
		this.key = key;
	}



}
