/*
 * ScrollDisplay.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A GUI widget for handling game-syle scrolled-text display.
 *
 * Written by Josh English.
 */

// define package space

package galaxik.widgets;

// import external packages

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;

// import internal packages

import lyra.AssetCache;
import lyra.GameEngine;
import lyra.Texture;
import lyra.widgets.Widget;
import lyra.widgets.FontInfo;

// define class

public class ScrollDisplay implements Widget
{
	// define private class constants

	private static final String CLASS_NAME = ScrollDisplay.class.getName();

	private static final int BUFFER_LENGTH = 128;
	private static final int PIXEL_SPACING = 2;

	// define protected class variables

	protected boolean isVisible = false;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected int bufferRef = 0;
	protected Color color = null;
	protected Color backgroundColor = null;
	protected Color bufferColors[] = null;
	protected String name = null;
	protected String buffer[] = null;
	protected Image background = null;
	protected FontInfo fontInfo = null;
	protected GameEngine gameEngine = null;

	// define class public functions

	public ScrollDisplay(String name, int x, int y, int width, int height,
			GameEngine gameEngine)
	{
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.gameEngine = gameEngine;

		isVisible = true;
		bufferRef = 0;
		color = Color.white;
		backgroundColor = null;
		bufferColors = new Color[BUFFER_LENGTH];
		buffer = new String[BUFFER_LENGTH];
		background = null;
		fontInfo = new FontInfo(gameEngine);
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

	public void setColor(Color color)
	{
		this.color = color;
	}

	public boolean hasFocus(int mouseX, int mouseY)
	{
		return false;
	}

	public void deFocus()
	{
		// do nothing
	}

	public boolean isClicked(int mouseX, int mouseY)
	{
		return false;
	}

	public void keyPressed(KeyEvent event)
	{
		// do nothing
	}

	public void log(Color color, String entry)
	{
		boolean wasAdjusted = false;
		int ii = 0;
		int fontWidth = 0;

		fontInfo.setString(entry);
		fontWidth = fontInfo.getWidth();

		while(fontWidth > width) {
			wasAdjusted = true;
			entry = entry.substring(0, (entry.length() - 1));
			fontInfo.setString(entry);
			fontWidth = fontInfo.getWidth();
		}

		if(wasAdjusted) {
			entry = new String(entry.substring(0, (entry.length() - 4)) +
					" ...");
		}

		if(bufferRef >= buffer.length) {
			for(ii = 0; ii < (buffer.length - 1); ii++) {
				buffer[ii] = buffer[(ii + 1)];
				bufferColors[ii] = bufferColors[(ii + 1)];
			}
			bufferRef = (buffer.length - 1);
		}

		bufferColors[bufferRef] = color;
		buffer[bufferRef] = entry;
		bufferRef++;
	}

	public void log(String entry)
	{
		log(color, entry);
	}

	public void setBackgroundColor(Color color)
	{
		backgroundColor = color;
	}

	public void setBackgroundImage(String imageName)
	{
		Texture texture = null;

		try {
			texture = (Texture)gameEngine.getAssetCache().getAsset(
					AssetCache.TYPE_TEXTURE, imageName);
			texture.buildMipMap(width, height);
			background = texture.getMipMap(width, height);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void render2D(Graphics2D render)
	{
		int ii = 0;
		int fontX = 0;
		int fontY = 0;
		int fontHeight = 0;
		int renderCount = 0;

		try {
			if(!isVisible) {
				return;
			}

			if(bufferRef < 1) {
				return;
			}

			// render background

			if(background != null) {
				render.drawImage(background, x, y, width, height, gameEngine);
			}
			else if(backgroundColor != null) {
				render.setColor(backgroundColor);
				render.fillRect(x, y, width, height);
			}

			// render scrolled-text

			fontHeight = fontInfo.getHeight();

			renderCount = (height / (fontHeight + PIXEL_SPACING));

			fontX = x;
			fontY = (y + (renderCount * (fontHeight + PIXEL_SPACING)));

			for(ii = (bufferRef - 1);
					((ii >= (bufferRef - renderCount)) && (ii >= 0)); ii--) {
				render.setColor(bufferColors[ii]);
				render.drawString(buffer[ii], fontX, fontY);

				fontY -= (fontHeight + PIXEL_SPACING);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

