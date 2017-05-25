package com.aura.cache.loaders;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.aura.cache.Loader;

public class ImageLoader implements Loader<BufferedImage> {
	@Override
	public BufferedImage load(InputStream input, String title) throws Exception {
		return ImageIO.read(input);
	}
}