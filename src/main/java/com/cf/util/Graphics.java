package com.cf.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.cf.mls.MLS;

public class Graphics {


	/**
	 * gets an image out of a Jar File
	 * 
	 * @param path
	 *            the path in the jar file
	 * @param clazz
	 *            a class which is in the same package as the image
	 * 
	 * @return the BufferedImage found at the given path. If no image could be
	 *         found null is returned.
	 */
	public static BufferedImage getImageFromJar(String path, Class<?> clazz) {
		InputStream stream = MLS.class.getResourceAsStream("/" + path);
		// if this fails try again using relativ paths and ClassLoader
		if (stream == null) {
//			System.err.println("can't find ressource /" + path);
			stream = ClassLoader.getSystemResourceAsStream(path);
		}

		if (stream == null)
			return null;
		try {
			return ImageIO.read(stream);
		} catch (IOException e) {
			return null;
		}
	}
}