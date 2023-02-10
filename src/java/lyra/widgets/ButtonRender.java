/*
 * ButtonRender.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A GUI widget for handling the rendering needs of a button widget element.
 *
 * Written by Josh English.
 */

// define package space

package lyra.widgets;

// import external packages

import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.KeyEvent;

// import internal packages

import lyra.AssetCache;
import lyra.Texture;
import lyra.GameEngine;

// define class

public class ButtonRender
{
	// define private class constants

	private static final String CLASS_NAME = ButtonRender.class.getName();

	// define protected class variables

	protected boolean isVisible = false;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected String name = null;
	protected String imageName = null;
	protected Image image = null;
	protected GameEngine gameEngine = null;

	// define class public functions

	public ButtonRender(String name, int x, int y, int width, int height,
			GameEngine gameEngine)
	{
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.gameEngine = gameEngine;

		isVisible = true;
		imageName = null;
		image = null;
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
		setImage(imageName);
	}

	public void setVisibility(boolean mode)
	{
		isVisible = mode;
	}

	public boolean isVisible()
	{
		return isVisible;
	}

	public void setImage(String imageName)
	{
		int margin = 0;
		Texture texture = null;

		try {
			if(imageName == null) {
				return;
			}

			margin = (int)((((double)width + (double)height) / 2.0) / 5.3);
			texture = (Texture)gameEngine.getAssetCache().getAsset(
					AssetCache.TYPE_TEXTURE, imageName);
			texture.buildButtonMipMap(width, height, margin);
			image = texture.getMipMap(width, height);
			this.imageName = imageName;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void setUncorneredImage(String imageName)
	{
		Texture texture = null;

		try {
			texture = (Texture)gameEngine.getAssetCache().getAsset(
					AssetCache.TYPE_TEXTURE, imageName);
			texture.buildMipMap(width, height);
			image = texture.getMipMap(width, height);
			this.imageName = imageName;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void render2D(Graphics2D render)
	{
		try {
			if(!isVisible) {
				return;
			}

			if(image == null) {
				return;
			}

			render.drawImage(image, x, y, width, height, gameEngine);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

