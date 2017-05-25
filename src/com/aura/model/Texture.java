package com.aura.model;

public interface Texture {

	public float width();

	public float height();

	public void render(Object renderer, float x, float y, float w, float h);

}
