/*
 * CorneredBox.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A GUI widget for rendering a box with corners.
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
import lyra.GameEngine;

// define class

public class CorneredBox
{
	// define private class constants

	private static final String CLASS_NAME = CorneredBox.class.getName();

	// define public class constants

	public static final int IMAGE_TOP = 0;
	public static final int IMAGE_BOTTOM = 1;
	public static final int IMAGE_LEFT = 2;
	public static final int IMAGE_RIGHT = 3;
	public static final int IMAGE_UPPER_LEFT = 4;
	public static final int IMAGE_UPPER_RIGHT = 5;
	public static final int IMAGE_LOWER_LEFT = 6;
	public static final int IMAGE_LOWER_RIGHT = 7;
	public static final int IMAGES = 8;

	// define protected class variables

	protected boolean isVisible = false;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected String name = null;
	protected Image images[] = null;
	protected GameEngine gameEngine = null;

	// define class private functions

	private void renderCorneredBox(Graphics2D render)
	{
		int lx = 0;
		int ly = 0;
		int lwidth = 0;
		int lheight = 0;

		try {
			lx = x;
			ly = y;
			lwidth = width;
			lheight = gameEngine.getImageHeight(images[IMAGE_TOP]);
			render.drawImage(images[IMAGE_TOP], lx, ly, lwidth, lheight,
					gameEngine);

			lheight = gameEngine.getImageHeight(images[IMAGE_BOTTOM]);
			ly = ((y + height) - lheight);
			render.drawImage(images[IMAGE_BOTTOM], lx, ly, lwidth, lheight,
					gameEngine);

			ly = y;
			lwidth = gameEngine.getImageWidth(images[IMAGE_LEFT]);
			lheight = height;
			render.drawImage(images[IMAGE_LEFT], lx, ly, lwidth, lheight,
					gameEngine);

			lwidth = gameEngine.getImageWidth(images[IMAGE_RIGHT]);
			lx = ((x + width) - lwidth);
			render.drawImage(images[IMAGE_RIGHT], lx, ly, lwidth, lheight,
					gameEngine);

			lx = x;
			ly = y;
			lwidth = gameEngine.getImageWidth(images[IMAGE_UPPER_LEFT]);
			lheight = gameEngine.getImageHeight(images[IMAGE_UPPER_LEFT]);
			render.drawImage(images[IMAGE_UPPER_LEFT], lx, ly, lwidth,
					lheight, gameEngine);

			lwidth = gameEngine.getImageWidth(images[IMAGE_UPPER_RIGHT]);
			lheight = gameEngine.getImageHeight(images[IMAGE_UPPER_RIGHT]);
			lx = ((x + width) - lwidth);
			render.drawImage(images[IMAGE_UPPER_RIGHT], lx, ly, lwidth,
					lheight, gameEngine);

			lx = x;
			lwidth = gameEngine.getImageWidth(images[IMAGE_LOWER_LEFT]);
			lheight = gameEngine.getImageHeight(images[IMAGE_LOWER_LEFT]);
			ly = ((y + height) - lheight);
			render.drawImage(images[IMAGE_LOWER_LEFT], lx, ly, lwidth,
					lheight, gameEngine);

			lwidth = gameEngine.getImageWidth(images[IMAGE_LOWER_RIGHT]);
			lheight = gameEngine.getImageHeight(images[IMAGE_LOWER_RIGHT]);
			lx = ((x + width) - lwidth);
			ly = ((y + height) - lheight);
			render.drawImage(images[IMAGE_LOWER_RIGHT], lx, ly, lwidth,
					lheight, gameEngine);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	// define class public functions

	public CorneredBox(String name, int x, int y, int width, int height,
			GameEngine gameEngine)
	{
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.gameEngine = gameEngine;

		isVisible = true;
		images = new Image[IMAGES];
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

	public void setVisibility(boolean mode)
	{
		isVisible = mode;
	}

	public boolean isVisible()
	{
		return isVisible;
	}

	public void setImage(int id, String imageName)
	{
		try {
			if((id < 0) || (id >= IMAGES)) {
				return;
			}
			images[id] = (Image)gameEngine.getAssetCache().getAsset(
					AssetCache.TYPE_IMAGE, imageName);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public int getXOffset()
	{
		int result = 0;

		try {
			result = gameEngine.getImageWidth(images[IMAGE_LEFT]);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public int getYOffset()
	{
		int result = 0;

		try {
			result = gameEngine.getImageHeight(images[IMAGE_TOP]);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public void render2D(Graphics2D render)
	{
		try {
			if(!isVisible) {
				return;
			}

			renderCorneredBox(render);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

