package com.aura.model;

public class Extreme {
	
	public float getMin() {
		return min;
	}
	public void setMin(float min) {
		this.min = min;
	}
	public float getMax() {
		return max;
	}
	public void setMax(float max) {
		this.max = max;
	}
	public Extreme(float min, float max) {
		this.min = min;
		this.max = max;
	}
	
	private float min;
	private float max;
	
	public float normalize(float value) {
		value = Math.max(min, value);
		value = Math.min(max, value);
		return value;
	}

}
