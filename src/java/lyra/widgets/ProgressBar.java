/*
 * ProgressBar.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A GUI widget for handling progress bars.
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

public class ProgressBar implements Widget
{
	// define private class constants

	private static final String CLASS_NAME = ProgressBar.class.getName();
	private static final Color DEFAULT_COLOR = Color.black;

	// define public class constants

	public static final int STATE_INIT = 0;
	public static final int STATE_RUNNING = 1;
	public static final int STATE_COMPLETE = 2;

	// define protected class variables

	protected boolean isVisible = false;
	protected int state = 0;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected int ticks = 0;
	protected int totalTicks = 0;
	protected String name = null;
	protected Color color = null;
	protected Image fillImage = null;
	protected CorneredBox corneredBox = null;
	protected GameEngine gameEngine = null;
	protected FontInfo fontInfo = null;

	// define class private functions

	private void renderProgress(Graphics2D render, double percent)
	{
		int lx = 0;
		int ly = 0;
		int lwidth = 0;
		int lheight = 0;

		try {
			lx = (x + corneredBox.getXOffset());
			ly = (y + corneredBox.getYOffset());
			lwidth = ((int)((double)width * (percent / 100.0)) -
					corneredBox.getXOffset());
			lheight = (height -
					(corneredBox.getYOffset() * 2));

			render.drawImage(fillImage, lx, ly, lwidth, lheight,
					gameEngine);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	// define class public functions

	public ProgressBar(String name, int x, int y, int width, int height,
			GameEngine gameEngine)
	{
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.gameEngine = gameEngine;

		isVisible = true;
		state = STATE_INIT;
		ticks = 0;
		totalTicks = 0;
		color = DEFAULT_COLOR;
		fillImage = null;
		corneredBox = new CorneredBox(name + "CorneredBox", x, y, width,
				height, gameEngine);
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
		corneredBox.setLocation(x, y);
	}

	public void setVisibility(boolean mode)
	{
		isVisible = mode;
		corneredBox.setVisibility(mode);
	}

	public boolean isVisible()
	{
		return isVisible;
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

	public void setFillImage(String imageName)
	{
		try {
			fillImage = (Image)gameEngine.getAssetCache().getAsset(
					AssetCache.TYPE_IMAGE, imageName);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void setImage(int id, String imageName)
	{
		try {
			corneredBox.setImage(id, imageName);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void setTotalTicks(int amount)
	{
		totalTicks = amount;
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

	public void start()
	{
		ticks = 0;
		state = STATE_RUNNING;
	}

	public void start(int totalTicks)
	{
		ticks = 0;
		this.totalTicks = totalTicks;
		state = STATE_RUNNING;
	}

	public void increment()
	{
		ticks++;
	}

	public void increment(int ticks)
	{
		this.ticks += ticks;
	}

	public int getState()
	{
		return state;
	}

	public boolean isComplete()
	{
		if(state == STATE_COMPLETE) {
			return true;
		}
		return false;
	}

	public void render2D(Graphics2D render)
	{
		int fontX = 0;
		int fontY = 0;
		double percent = 0.0;

		String temp = null;
		String text = null;

		try {
			if(!isVisible) {
				return;
			}

			// setup render space

			render.setColor(Color.white);
			render.fillRect(x, y, width, height);

			// calculate completion percentage

			if(state == STATE_INIT) {
				percent = 0.0;
			}
			else if(state == STATE_RUNNING) {
				percent = (((double)ticks / (double)totalTicks) * 100.0);
				if(ticks >= totalTicks) {
					state = STATE_COMPLETE;
				}
			}
			else if(state == STATE_COMPLETE) {
				percent = 100.0;
			}

			// render image componenets

			renderProgress(render, percent);
			corneredBox.render2D(render);

			// build text string

			temp = new Double(percent).toString().trim();
			if(percent < 10.0) {
				text = new String(temp.substring(0, 3));
			}
			else {
				text = new String(temp.substring(0, 4));
			}
			if(((percent < 10.0) && (text.length() < 2)) ||
					((percent >= 10.0) && (text.length() < 3))) {
				text = text.concat(".");
			}
			while(((percent < 10.0) && (text.length() < 3)) ||
					((percent >= 10.0) && (text.length() < 4))) {
				text = text.concat("0");
			}
			text = text.concat("%");

			// render text

			fontInfo.setString(text);
			fontX = (x + ((width / 2) - (fontInfo.getWidth() / 2)));
			fontY = (y + (height / 2) + (fontInfo.getHeight() / 2));
			render.setColor(color);
			render.drawString(text, fontX, fontY);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

