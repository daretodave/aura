package com.aura.cache;

public interface LoadProcessor {

	public void preLoad(int count);

	public void preLoadElement (String id, int index, int count);

	public void onLoadElement  (String id, int index, int count);

}
