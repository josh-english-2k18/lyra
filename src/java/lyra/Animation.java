/*
 * Animation.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A simple component for managing and rendering 2D frame-based animation
 * sequences.
 *
 * Written by Josh English.
 */

// define package space

package lyra;

// import external packages

import java.util.Date;
import java.util.Vector;
import java.awt.Rectangle;
import java.awt.Image;
import java.awt.Graphics2D;

// import internal packages

import lyra.interfaces.GraphicsInterface;

// define class

public class Animation implements GraphicsInterface
{
	// define private class constants

	private static final String CLASS_NAME = Animation.class.getName();

	// define private class variables

	protected boolean isVisible = false;
	protected boolean isPlaying = false;
	protected boolean willAutoStop = false;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected int offsetX = 0;
	protected int offsetY = 0;
	protected int animationRef = 0;
	protected long animationTime = 0;
	protected long animationThresholdMillis = 0;
	protected String name = null;
	protected Vector frames = null;
	protected GameEngine gameEngine = null;

	// define class public functions

	public Animation(String name, int x, int y, int width, int height,
			GameEngine gameEngine)
	{
		isVisible = true;
		isPlaying = false;
		willAutoStop = false;
		offsetX = 0;
		offsetY = 0;
		animationRef = 0;
		animationTime = (long)0;
		animationThresholdMillis = (long)0;
		frames = new Vector(8, 8);

		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.gameEngine = gameEngine;
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

	public int getOffsetX()
	{
		return offsetX;
	}

	public int getOffsetY()
	{
		return offsetY;
	}

	public void setOffsets(int x, int y)
	{
		offsetX = x;
		offsetY = y;
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

	public void addImageTexture(String imageName)
	{
		Texture texture = null;

		try {
			texture = (Texture)gameEngine.getAssetCache().getAsset(
					AssetCache.TYPE_TEXTURE, imageName);
			texture.buildMipMap(width, height);
			frames.add(texture.getMipMap(width, height));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void setAnimationTime(long millis)
	{
		this.animationThresholdMillis = millis;
	}

	public long getAnimationTime()
	{
		return this.animationThresholdMillis;
	}

	public Image getImage(int id)
	{
		if((frames == null) || (id < 0) || (id >= frames.size())) {
			return null;
		}
		return (Image)frames.get(id);
	}

	public boolean isPlaying()
	{
		return isPlaying;
	}

	public void start()
	{
		if(!isVisible) {
			isPlaying = false;
			return;
		}
		isPlaying = true;
		willAutoStop = true;
		animationRef = 0;
		animationTime = (new Date()).getTime();
	}

	public void start(boolean willAutoStop)
	{
		if(!isVisible) {
			isPlaying = false;
			return;
		}
		if(isPlaying) {
			return;
		}
		isPlaying = true;
		this.willAutoStop = willAutoStop;
		animationRef = 0;
		animationTime = (new Date()).getTime();
	}

	public void stop()
	{
		isPlaying = false;
	}

	public void processGameplay(double frameRate)
	{
		long currentTime = (long)0;

		if(!isPlaying) {
			return;
		}

		currentTime = (new Date()).getTime();
		if(currentTime >= (animationTime + animationThresholdMillis)) {
			animationTime = currentTime;
			animationRef++;
			if(animationRef >= frames.size()) {
				animationRef = 0;
				if(willAutoStop) {
					isPlaying = false;
				}
			}
		}
	}

	public void render2D(Graphics2D render)
	{
		try {
			if((!isVisible) || (!isPlaying)) {
				return;
			}

			render.drawImage((Image)frames.get(animationRef), (x + offsetX),
					(y + offsetY), width, height, gameEngine);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

