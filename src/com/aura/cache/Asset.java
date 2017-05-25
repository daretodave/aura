package com.aura.cache;

public class Asset {

	public static Asset derive(String folder, String asset) {
		return new Asset(folder, asset);
	}
	public Asset(String folder, String asset) {
		this.folder = folder;
		this.asset = asset;
	}
	protected final String folder;
	protected final String asset;

}
