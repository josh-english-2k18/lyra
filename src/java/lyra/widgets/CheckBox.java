/*
 * CheckBox.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A GUI widget for handling check boxes.
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

public class CheckBox implements Widget
{
	// define private class constants

	private static final String CLASS_NAME = CheckBox.class.getName();

	// define protected class variables

	protected boolean isVisible = false;
	protected boolean isChecked = false;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected Image boxImage = null;
	protected Image checkImage = null;
	protected String name = null;
	protected GameEngine gameEngine = null;

	// define class public functions

	public CheckBox(String name, int x, int y, int width, int height,
			GameEngine gameEngine)
	{
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.gameEngine = gameEngine;

		isVisible = true;
		isChecked = false;
		boxImage = null;
		checkImage = null;
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

	public void setIsChecked(boolean mode)
	{
		isChecked = mode;
	}

	public boolean isChecked()
	{
		return isChecked;
	}

	public void setBoxImage(String name)
	{
		try {
			boxImage = (Image)gameEngine.getAssetCache().getAsset(
					AssetCache.TYPE_IMAGE, name);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void setCheckImage(String name)
	{
		try {
			checkImage = (Image)gameEngine.getAssetCache().getAsset(
					AssetCache.TYPE_IMAGE, name);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void setImages(String boxImageName, String checkImageName)
	{
		setBoxImage(boxImageName);
		setCheckImage(checkImageName);
	}

	public boolean hasFocus(int mouseX, int mouseY)
	{
		if(!isVisible) {
			return false;
		}

		if((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
				(mouseY <= (y + height))) {
			return true;
		}

		return false;
	}

	public void deFocus()
	{
		// do nothing
	}

	public boolean isClicked(int mouseX, int mouseY)
	{
		boolean result = false;

		if(!isVisible) {
			return false;
		}

		if((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
				(mouseY <= (y + height))) {
			result = true;
		}

		if(result) {
			if(isChecked) {
				isChecked = false;
			}
			else {
				isChecked = true;
			}
		}

		return result;
	}

	public void keyPressed(KeyEvent event)
	{
		// do nothing
	}

	public void render2D(Graphics2D render)
	{
		int fontX = 0;
		int fontY = 0;

		try {
			if(!isVisible) {
				return;
			}

			render.drawImage(boxImage, x, y, width, height, gameEngine);
			if(isChecked) {
				render.drawImage(checkImage, x, y, width, height, gameEngine);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

