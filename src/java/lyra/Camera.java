/*
 * Camera.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A 2D camera system with a movement-managment API.
 *
 * Written by Josh English.
 */

// define package space

package lyra;

// import external packages

import java.awt.event.MouseEvent;

// import internal packages

import lyra.drivers.MouseDriver;

// define class

public class Camera
{
	// define private class constants

	private static final String CLASS_NAME = Camera.class.getName();

	// define public class constants

	public static final int NORTH = 0;
	public static final int NORTH_EAST = 1;
	public static final int EAST = 2;
	public static final int SOUTH_EAST = 3;
	public static final int SOUTH = 4;
	public static final int SOUTH_WEST = 5;
	public static final int WEST = 6;
	public static final int NORTH_WEST = 7;

	public static final int MODE_NORMAL = 1;
	public static final int MODE_INVERTED = 2;

	// define private class variables

	protected boolean isDragging = false;
	protected boolean hasMoved = false;
	protected int windowX = 0;
	protected int windowY = 0;
	protected int windowWidth = 0;
	protected int windowHeight = 0;
	protected int cameraX = 0;
	protected int cameraY = 0;
	protected int cameraWidth = 0;
	protected int cameraHeight = 0;
	protected int mouseX = 0;
	protected int mouseY = 0;
	protected String name = null;

	// define class public functions

	public Camera(String name, int windowX, int windowY, int windowWidth,
			int windowHeight)
	{
		this.name = name;
		this.windowX = windowX;
		this.windowY = windowY;
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;

		isDragging = false;
		hasMoved = false;
		cameraX = windowX;
		cameraY = windowY;
		cameraWidth = windowWidth;
		cameraHeight = windowHeight;
		mouseX = 0;
		mouseY = 0;
	}

	public String getClassName()
	{
		return CLASS_NAME;
	}

	public String getName()
	{
		return name;
	}

	public int getWindowX()
	{
		return windowX;
	}

	public int getWindowY()
	{
		return windowY;
	}

	public int getWindowWidth()
	{
		return windowWidth;
	}

	public int getWindowHeight()
	{
		return windowHeight;
	}

	public void setWindowLocation(int windowX, int windowY)
	{
		this.windowX = windowX;
		this.windowY = windowY;
	}

	public void resizeWindow(int width, int height)
	{
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		cameraWidth = windowWidth;
		cameraHeight = windowHeight;
	}

	public int getCameraX()
	{
		return cameraX;
	}

	public int getCameraY()
	{
		return cameraY;
	}

	public int getCameraOffsetX()
	{
		return (0 - cameraX);
	}

	public int getCameraOffsetY()
	{
		return (0 - cameraY);
	}

	public void setCameraCoords(int x, int y)
	{
		this.cameraX = x;
		this.cameraY = y;
		hasMoved = true;
	}

	public void applyCameraDiff(int diffX, int diffY)
	{
		cameraX += diffX;
		cameraY += diffY;
		hasMoved = true;
	}

	public boolean hasMoved()
	{
		boolean result = false;

		result = hasMoved;
		hasMoved = false;

		return result;
	}

	public void moveCamera(int direction, int distance)
	{
		if((direction < 0) || (direction > NORTH_WEST) || (distance < 0)) {
			return;
		}

		switch(direction) {
			case NORTH:
				cameraY -= distance;
				break;
			case NORTH_EAST:
				cameraX += (int)((double)distance * 0.707106);
				cameraY -= (int)((double)distance * 0.707106);
				break;
			case EAST:
				cameraX += distance;
				break;
			case SOUTH_EAST:
				cameraX += (int)((double)distance * 0.707106);
				cameraY += (int)((double)distance * 0.707106);
				break;
			case SOUTH:
				cameraY += distance;
				break;
			case SOUTH_WEST:
				cameraX -= (int)((double)distance * 0.707106);
				cameraY += (int)((double)distance * 0.707106);
				break;
			case WEST:
				cameraX -= distance;
				break;
			case NORTH_WEST:
				cameraX -= (int)((double)distance * 0.707106);
				cameraY -= (int)((double)distance * 0.707106);
				break;
		}

		hasMoved = true;
	}

	public void processMouseEvent(int mode, int type, int mouseX, int mouseY,
			MouseEvent event)
	{
		if((mode != MODE_NORMAL) && (mode != MODE_INVERTED)) {
			return;
		}

		if((mouseX < 0) || (mouseX > cameraWidth) || (mouseY < 0) ||
				(mouseY > cameraHeight)) {
			return;
		}

		if(type == MouseDriver.EVENT_DRAGGED) {
			if(isDragging) {
				if(mouseX < this.mouseX) {
					if(mode == MODE_NORMAL) {
						cameraX += (this.mouseX - mouseX);
					}
					else {
						cameraX -= (this.mouseX - mouseX);
					}
					hasMoved = true;
				}
				else if(mouseX > this.mouseX) {
					if(mode == MODE_NORMAL) {
						cameraX -= (mouseX - this.mouseX);
					}
					else {
						cameraX += (mouseX - this.mouseX);
					}
					hasMoved = true;
				}
				if(mouseY < this.mouseY) {
					if(mode == MODE_NORMAL) {
						cameraY += (this.mouseY - mouseY);
					}
					else {
						cameraY -= (this.mouseY - mouseY);
					}
					hasMoved = true;
				}
				else if(mouseY > this.mouseY) {
					if(mode == MODE_NORMAL) {
						cameraY -= (mouseY - this.mouseY);
					}
					else {
						cameraY += (mouseY - this.mouseY);
					}
					hasMoved = true;
				}
			}
			else {
				isDragging = true;
			}
		}
		else {
			isDragging = false;
		}

		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}
}

