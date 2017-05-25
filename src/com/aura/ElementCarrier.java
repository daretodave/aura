package com.aura;

import com.aura.model.Element;
import com.aura.model.State;
import com.aura.util.SimpleMath;

public abstract class ElementCarrier {

	public abstract Element getElement();

	public abstract State getState();

	public abstract State getTrueState();

	public abstract boolean isDestroyed();

	public abstract float getScreenX();

	public abstract float getScreenY();

	public abstract float getXOnScreen();
	public abstract float getYOnScreen();

	public abstract boolean isHidden();

	protected boolean visible = true;

	public float distanceFromCenter(ElementCarrier carrier) {
		Element other = carrier.getElement();
		float cx = getElement().getXOnScreen()+getElement().width()/2;
		float cy = getElement().getYOnScreen()+getElement().height()/2;
		float cx2 = other.getXOnScreen()+other.width()/2;
		float cy2 = other.getYOnScreen()+other.height()/2;
		cx  += getScreenX();
		cy  += getScreenY();
		cx2 += carrier.getScreenX();
		cx2 += carrier.getScreenY();
		return SimpleMath.distance(cx, cy, cx2, cy2);
	}

	public float distanceFromCenter(float x, float y) {
		float cx = getElement().getXOnScreen()+getElement().width()/2;
		float cy = getElement().getYOnScreen()+getElement().height()/2;
		cx  += getScreenX();
		cy  += getScreenY();
		return SimpleMath.distance(cx, cy, x, y);
	}

}
