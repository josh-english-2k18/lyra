/*
 * Tile.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A component for managing and rendering 2D tiles.
 *
 * Written by Josh English.
 */

// define package space

package lyra;

// import external packages

import java.awt.Rectangle;
import java.awt.Point;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.Color;

// import internal packages

import lyra.interfaces.GraphicsInterface;

// define class

public class Tile implements GraphicsInterface
{
	// define private class constants

	private static final String CLASS_NAME = Tile.class.getName();

	// define private class variables

	protected boolean isVisible = false;
	protected boolean canCollide = false;
	protected boolean canIntersect = false;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected int offsetX = 0;
	protected int offsetY = 0;
	protected double friction = 0.0;
	protected String name = null;
	protected Image image = null;
	protected GameEngine gameEngine = null;

	// define class public functions

	public Tile(String name, int x, int y, int width, int height,
			GameEngine gameEngine)
	{
		isVisible = true;
		canCollide = false;
		canIntersect = true;
		offsetX = 0;
		offsetY = 0;
		friction = 0.0;
		image = null;

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

	public void setFriction(double amount)
	{
		friction = amount;
	}

	public double getFriction()
	{
		return friction;
	}

	public void setImageTexture(String imageName)
	{
		Texture texture = null;

		try {
			texture = (Texture)gameEngine.getAssetCache().getAsset(
					AssetCache.TYPE_TEXTURE, imageName);
			texture.buildMipMap(width, height);
			image = texture.getMipMap(width, height);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void setCanCollide(boolean mode)
	{
		canCollide = mode;
	}

	public boolean canCollide()
	{
		return canCollide;
	}

	public void setCanIntersect(boolean mode)
	{
		canIntersect = mode;
	}

	public boolean canIntersect()
	{
		return canIntersect;
	}

	public boolean detectCollision(Rectangle rectangle)
	{
		Rectangle location = null;

		if((!isVisible) || (!canCollide)) {
			return false;
		}

		location = this.getLocation();

		return location.intersects(rectangle);
	}

	public boolean detectIntersection(int x, int y)
	{
		Point point = null;
		Rectangle location = null;

		if(!isVisible) {
			return false;
		}

		point = new Point(x, y);
		location = this.getLocation();

		return location.contains(point);
	}

	public void render2D(Graphics2D render)
	{
		try {
			if(!isVisible) {
				return;
			}

			render.drawImage(image, (x + offsetX), (y + offsetY), width,
					height, gameEngine);

			render.setColor(Color.black);
			render.drawRect((x + offsetX), (y + offsetY), width, height);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

