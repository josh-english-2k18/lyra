/*
 * Texture.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A component for automating the managment of 2D images, with mipmaps and
 * hardware acceleration.
 *
 * Written by Josh English.
 */

// define package space

package lyra;

// import external packages

import java.util.HashMap;
import java.awt.Rectangle;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;

// import internal packages

import lyra.interfaces.GraphicsInterface;

// define class

public class Texture implements GraphicsInterface
{
	// define private class constants

	private static final String CLASS_NAME = Texture.class.getName();

	// define protected class variables

	protected boolean isVisible = false;
	protected boolean debugMode = false;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected int offsetX = 0;
	protected int offsetY = 0;
	protected String name = null;
	protected HashMap index = null;
	protected GameEngine gameEngine = null;

	// define class private functions

	private String buildKey(int width, int height)
	{
		String result = null;

		result = new String("/" + width + "x" + height);

		return result;
	}

	// define class public functions

	public Texture(String name, GameEngine gameEngine)
	{
		Image image = null;
		String key = null;

		try {
			isVisible = true;
			x = 0;
			y = 0;
			width = 0;
			height = 0;
			offsetX = 0;
			offsetY = 0;
			index = new HashMap();

			this.name = name;
			this.gameEngine = gameEngine;

			image = (Image)gameEngine.getAssetCache().getAsset(
					AssetCache.TYPE_IMAGE, name);
			image.setAccelerationPriority((float)1.0);
			width = gameEngine.getImageWidth(image);
			height = gameEngine.getImageHeight(image);
			key = buildKey(width, height);
			index.put(key, image);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public Texture(String name, Image image, GameEngine gameEngine)
	{
		String key = null;

		isVisible = true;
		x = 0;
		y = 0;
		width = 0;
		height = 0;
		offsetX = 0;
		offsetY = 0;
		index = new HashMap();

		this.name = name;
		this.gameEngine = gameEngine;

		image.setAccelerationPriority((float)1.0);
		width = gameEngine.getImageWidth(image);
		height = gameEngine.getImageHeight(image);
		key = buildKey(width, height);
		index.put(key, image);
	}

	public String getClassName()
	{
		return CLASS_NAME;
	}

	public String getName()
	{
		return name;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	public void resize(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	public Rectangle getLocation()
	{
		return new Rectangle((x + offsetX), (y + offsetY), width, height);
	}

	public boolean isVisible()
	{
		return isVisible;
	}

	public boolean applyCamera(int cameraX, int cameraY, int cameraWidth,
			int cameraHeight)
	{
		if(((x >= cameraX) || ((x + width) >= cameraX)) &&
				(x <= (cameraX + cameraWidth)) &&
				((y >= cameraY) || ((y + height) >= cameraY)) &&
				(y <= (cameraY + cameraHeight))) {
			isVisible = true;
			offsetX = (0 - cameraX);
			offsetY = (0 - cameraY);
		}
		else {
			isVisible = false;
		}

		return isVisible;
	}

	public void setDebugMode(boolean mode)
	{
		debugMode = mode;
	}

	public Image getDefaultImage()
	{
		String key = null;
		Image result = null;

		try {
			key = buildKey(width, height);
			result = (Image)index.get(key);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public void buildMipMap(int width, int height)
	{
		String key = null;
		Image image = null;
		Image mipMap = null;

		try {
			key = buildKey(width, height);
			image = (Image)index.get(key);
			if(image != null) {
				return;
			}
			key = buildKey(this.width, this.height);
			image = (Image)index.get(key);
			if(image == null) {
				return;
			}
			mipMap = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			if(mipMap == null) {
				throw new Exception("{" + CLASS_NAME +
						"} failed to build scaled instance of " + image +
						" @ " + width + "x" + height);
			}
			mipMap.setAccelerationPriority((float)1.0);
			gameEngine.drawOffscreenImage(mipMap, 0, 0, width, height);
			key = buildKey(width, height);
			index.put(key, mipMap);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void buildButtonMipMap(int width, int height, int margin)
	{
		int ii = 0;
		int currentWidth = 0;
		int currentHeight = 0;
		int inputWidth = 0;
		int inputHeight = 0;
		int localMargin = 0;
		double widthRatio = 0.0;
		double heightRatio = 0.0;

		String key = null;
		Image image = null;
		Image mipMap = null;
		BufferedImage imageInput = null;
		BufferedImage temp = null;
		BufferedImage center = null;
		BufferedImage[] corners = null;
		BufferedImage[] tempMargins = null;
		BufferedImage[] margins = null;
		BufferedImage result = null;
		Graphics2D render = null;

		try {
			key = buildKey(width, height);
			image = (Image)index.get(key);
			if(image != null) {
				return;
			}
			key = buildKey(this.width, this.height);
			image = (Image)index.get(key);
			if(image == null) {
				return;
			}

			currentWidth = this.width;
			currentHeight = this.height;
			localMargin = margin;

			// construct a sized representation of the image input

			widthRatio = ((double)width / (double)currentWidth);
			heightRatio = ((double)height / (double)currentHeight);

			if(width < currentWidth) {
				if(height < currentHeight) {
					if(widthRatio == heightRatio) {
						imageInput = new BufferedImage(currentWidth,
								currentHeight, BufferedImage.TYPE_INT_ARGB);
						localMargin = (int)((double)margin * widthRatio);
					}
					else if(widthRatio < heightRatio) {
						imageInput = new BufferedImage(
								(int)((double)currentWidth * widthRatio),
								(int)((double)currentHeight * widthRatio),
								BufferedImage.TYPE_INT_ARGB);
						localMargin = (int)((double)margin * widthRatio);
					}
					else {
						imageInput = new BufferedImage(
								(int)((double)currentWidth * heightRatio),
								(int)((double)currentHeight * heightRatio),
								BufferedImage.TYPE_INT_ARGB);
						localMargin = (int)((double)margin * heightRatio);
					}
				}
				else {
					imageInput = new BufferedImage(
							(int)((double)currentWidth * widthRatio),
							(int)((double)currentHeight * widthRatio),
							BufferedImage.TYPE_INT_ARGB);
					localMargin = (int)((double)margin * widthRatio);
				}
			}
			else if(height < currentHeight) {
				imageInput = new BufferedImage(
						(int)((double)currentWidth * heightRatio),
						(int)((double)currentHeight * heightRatio),
						BufferedImage.TYPE_INT_ARGB);
				localMargin = (int)((double)margin * heightRatio);
			}
			else {
				imageInput = new BufferedImage(currentWidth, currentHeight,
						BufferedImage.TYPE_INT_ARGB);
			}

			inputWidth = gameEngine.getImageWidth(imageInput);
			inputHeight = gameEngine.getImageHeight(imageInput);
			render = imageInput.createGraphics();
			render.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			render.drawImage(image, 0, 0, inputWidth, inputHeight, gameEngine);

			// construct the image corners

			corners = new BufferedImage[4];

			for(ii = 0; ii < 4; ii++) {
				corners[ii] = new BufferedImage(localMargin, localMargin,
						BufferedImage.TYPE_INT_ARGB);

				render = corners[ii].createGraphics();
				render.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);

				switch(ii) {
					case 0:
						render.drawImage(imageInput, 0, 0, gameEngine);
						break;
					case 1:
						render.drawImage(imageInput,
								((inputWidth * -1) + localMargin),
								0, gameEngine);
						break;
					case 2:
						render.drawImage(imageInput,
								((inputWidth * -1) + localMargin),
								((inputHeight * -1) + localMargin), gameEngine);
						break;
					case 3:
						render.drawImage(imageInput, 0,
								((inputHeight * -1) + localMargin), gameEngine);
						break;
				}
			}

			// construct center

			temp = new BufferedImage((inputWidth - (2 * localMargin)),
					(inputHeight - (2 * localMargin)),
					BufferedImage.TYPE_INT_ARGB);
			render = temp.createGraphics();
			render.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			render.drawImage(imageInput, (localMargin * -1), (localMargin * -1),
					gameEngine);

			center = new BufferedImage((width - (2 * localMargin)),
					(height - (2 * localMargin)), BufferedImage.TYPE_INT_ARGB);
			render = center.createGraphics();
			render.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			render.drawImage(temp, 0, 0, (width - (2 * localMargin)),
					(height - (2 * localMargin)), gameEngine);

			// construct the margins

			tempMargins = new BufferedImage[4];
			tempMargins[0] = new BufferedImage(localMargin,
					(inputHeight - (2 * localMargin)),
					BufferedImage.TYPE_INT_ARGB);
			tempMargins[1] = new BufferedImage(
					(inputWidth - (2 * localMargin)), localMargin,
					BufferedImage.TYPE_INT_ARGB);
			tempMargins[2] = new BufferedImage(localMargin,
					(inputHeight - (2 * localMargin)),
					BufferedImage.TYPE_INT_ARGB);
			tempMargins[3] = new BufferedImage(
					(inputWidth - (2 * localMargin)), localMargin,
					BufferedImage.TYPE_INT_ARGB);

			for(ii = 0; ii < 4; ii++) {
				render = tempMargins[ii].createGraphics();
				render.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);

				switch(ii) {
					case 0:
						render.drawImage(imageInput, 0, (localMargin * -1),
								gameEngine);
						break;
					case 1:
						render.drawImage(imageInput, (localMargin * -1), 0,
								gameEngine);
						break;
					case 2:
						render.drawImage(imageInput,
								(localMargin - inputWidth), (localMargin * -1),
								gameEngine);
						break;
					case 3:
						render.drawImage(imageInput,
								(localMargin * -1), (localMargin - inputHeight),
								gameEngine);
						break;
				}
			}

			margins = new BufferedImage[4];
			margins[0] = new BufferedImage(localMargin,
					(height - (2 * localMargin)), BufferedImage.TYPE_INT_ARGB);
			margins[1] = new BufferedImage((width - (2 * localMargin)),
					localMargin, BufferedImage.TYPE_INT_ARGB);
			margins[2] = new BufferedImage(localMargin,
					(height - (2 * localMargin)), BufferedImage.TYPE_INT_ARGB);
			margins[3] = new BufferedImage((width - (2 * localMargin)),
					localMargin, BufferedImage.TYPE_INT_ARGB);

			for(ii = 0; ii < 4; ii++) {
				render = margins[ii].createGraphics();
				render.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				render.drawImage(tempMargins[ii], 0, 0,
						gameEngine.getImageWidth(margins[ii]),
						gameEngine.getImageHeight(margins[ii]), gameEngine);
			}

			// composite the new image

			result = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_ARGB);
			render = result.createGraphics();
			render.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			render.drawImage(center, localMargin, localMargin, gameEngine);
			render.drawImage(corners[3], 0, (height - localMargin), gameEngine);
			render.drawImage(margins[0], 0, localMargin, gameEngine);
			render.drawImage(margins[1], localMargin, 0, gameEngine);
			render.drawImage(margins[2], (width - localMargin), localMargin,
					gameEngine);
			render.drawImage(margins[3], localMargin, (height - localMargin),
					gameEngine);
			render.drawImage(corners[0], 0, 0, gameEngine);
			render.drawImage(corners[1], (width - localMargin), 0, gameEngine);
			render.drawImage(corners[2], (width - localMargin),
					(height - localMargin), gameEngine);

			// add new image to index

			mipMap = (Image)result;
			mipMap.setAccelerationPriority((float)1.0);
			gameEngine.drawOffscreenImage(mipMap, 0, 0, width, height);
			key = buildKey(width, height);
			index.put(key, mipMap);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public Image getMipMap(int width, int height)
	{
		String key = null;
		Image result = null;

		try {
			key = buildKey(width, height);
			result = (Image)index.get(key);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public void setMipMapAsDefault(int width, int height)
	{
		String key = null;
		Image image = null;

		try {
			key = buildKey(width, height);
			image = (Image)index.get(key);
			if(image != null) {
				this.width = width;
				this.height = height;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void render2D(Graphics2D render)
	{
		String key = null;
		Image image = null;

		try {
			if(!isVisible) {
				return;
			}

			key = buildKey(width, height);
			image = (Image)index.get(key);
			if(image != null) {
				render.drawImage(image, x, y, width, height, gameEngine);
			}

			if(debugMode) {
				render.setColor(Color.blue);
				render.drawRect(x, y, width, height);
				render.setColor(Color.white);
				render.drawString("Image[" + name + "]@" + width + "x" + height,
						(x + 2), (y + 12));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

