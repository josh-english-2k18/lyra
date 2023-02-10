/*
 * RevbarWidget.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A widget portraying a rev-bar.
 *
 * Written by Josh English.
 */

// define package space

package spincycle;

// import external packages

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.Rectangle;

// import internal packages

import lyra.*;

// define class

public class RevbarWidget
{
	// define private class constants

	private static final String CLASS_NAME = RevbarWidget.class.getName();

	// define private class variables

	private boolean isVisible = false;
	private int type = 0;
	private int x = 0;
	private int y = 0;
	private int width = 0;
	private int height = 0;
	private int tickX = 0;
	private int tickY = 0;
	private int tickWidth = 0;
	private int tickHeight = 0;
	private Image barImage = null;
	private Image tickImage = null;
	private GameEngine gameEngine = null;

	// define public class variables

	public double aX = 0.0;
	public double aY = 0.0;
	public double bX = 0.0;
	public double bY = 0.0;
	public double cX = 0.0;
	public double cY = 0.0;
	public double dX = 0.0;
	public double dY = 0.0;

	// define private functions

	public void calculateLocation()
	{
		double theta = 0.0;

		aX = (double)x;
		aY = (double)y;

		bX = ((Math.cos(theta) * (double)width) + aX);
		bY = ((Math.sin(theta) * (double)width) + aY);

		cX = (bX - (Math.cos(theta - Math.toRadians(90)) * (double)height));
		cY = (bY - (Math.sin(theta - Math.toRadians(90)) * (double)height));

		dX = ((Math.cos(theta - Math.toRadians(180)) * (double)width) + cX);
		dY = ((Math.sin(theta - Math.toRadians(180)) * (double)width) + cY);
	}

	// define public functions

	public RevbarWidget(int x, int y, int width, int height,
			GameEngine gameEngine)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.gameEngine = gameEngine;

		isVisible = true;
		tickX = 0;
		tickY = 0;
		tickWidth = 0;
		tickHeight = 0;
		barImage = null;
		tickImage = null;
		calculateLocation();
	}

	public void setTick(int x, int y, int width, int height)
	{
		tickX = x;
		tickY = y;
		tickWidth = width;
		tickHeight = height;
	}

	public boolean isVisible()
	{
		return isVisible;
	}

	public void setVisibility(boolean mode)
	{
		isVisible = mode;
	}

	public int getX()
	{
		return this.x;
	}

	public int getY()
	{
		return this.y;
	}

	public int getWidth()
	{
		return this.width;
	}

	public int getHeight()
	{
		return this.height;
	}

	public void setTickX(int x)
	{
		this.tickX = x;
	}

	public int getTickX()
	{
		return this.tickX;
	}

	public void setTickY(int y)
	{
		this.tickY = y;
	}

	public int getTickY()
	{
		return this.tickY;
	}

	public Rectangle getBoundary()
	{
		Polygon brick = null;
		Rectangle result = null;

		brick = new Polygon();
		brick.addPoint((int)aX, (int)aY);
		brick.addPoint((int)bX, (int)bY);
		brick.addPoint((int)cX, (int)cY);
		brick.addPoint((int)dX, (int)dY);
		result = brick.getBounds();

		return result;
	}

	public void setBarImage(Image image)
	{
		this.barImage = image;
	}

	public void setTickImage(Image image)
	{
		this.tickImage = image;
	}

	public void render2D(Graphics2D render)
	{
		try {
			if(!isVisible) {
				return;
			}

			if(barImage != null) {
				render.drawImage(barImage, x, y, width, height, gameEngine);
			}
			else {
				render.setColor(Color.blue);
				render.fillRect(x, y, width, height);
			}
			if(tickImage != null) {
				render.drawImage(tickImage, tickX, tickY, tickWidth,
						tickHeight, gameEngine);
			}
			else {
				render.setColor(Color.red);
				render.fillRect(tickX, tickY, tickWidth, tickHeight);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

