/*
 * Sprite.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A simple component for managing and rendering 2D sprites.
 *
 * Written by Josh English.
 */

// define package space

package lyra;

// import external packages

import java.awt.Rectangle;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.MouseEvent;

// import internal packages

import lyra.interfaces.GraphicsInterface;
import lyra.drivers.MouseDriver;

// define class

public class Sprite implements GraphicsInterface
{
	// define public class constants

	public static final int DIRECTION_NORTH = 0;
	public static final int DIRECTION_NORTH_NORTH_EAST = 1;
	public static final int DIRECTION_NORTH_EAST = 2;
	public static final int DIRECTION_NORTH_EAST_NORTH = 3;
	public static final int DIRECTION_EAST = 4;
	public static final int DIRECTION_SOUTH_EAST_SOUTH = 5;
	public static final int DIRECTION_SOUTH_EAST = 6;
	public static final int DIRECTION_SOUTH_SOUTH_EAST = 7;
	public static final int DIRECTION_SOUTH = 8;
	public static final int DIRECTION_SOUTH_SOUTH_WEST = 9;
	public static final int DIRECTION_SOUTH_WEST = 10;
	public static final int DIRECTION_SOUTH_WEST_SOUTH = 11;
	public static final int DIRECTION_WEST = 12;
	public static final int DIRECTION_NORTH_WEST_NORTH = 13;
	public static final int DIRECTION_NORTH_WEST = 14;
	public static final int DIRECTION_NORTH_NORTH_WEST = 15;

	public static final int DIRECTIONS = 16;

	public static final String[] DIRECTION_NAMES = {
			"North",
			"North-North-East",
			"North-East",
			"North-East-North",
			"East",
			"South-East-South",
			"South-East",
			"South-South-East",
			"South",
			"South-South-West",
			"South-West",
			"South-West-South",
			"West",
			"North-West-North",
			"North-West",
			"North-North-West",
	};

	public static final double DEFAULT_OUTLINE_RATIO = 25.0;

	public static final int CLOCKWISE = 1;
	public static final int COUNTER_CLOCKWISE = 2;
	public static final double DEFAULT_ROTATE_TIME_SECONDS = 0.125;

	// define private class constants

	private static final String CLASS_NAME = Sprite.class.getName();
	private static final int DEFAULT_STARTING_DIRECTION = DIRECTION_EAST;

	// define protected class variables

	protected boolean debugMode = false;
	protected boolean isVisible = false;
	protected boolean isStatic = false;
	protected boolean isOutline = false;
	protected boolean isSelectable = false;
	protected boolean isSelected = false;
	protected boolean hasOutline = false;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected int outlineWidth = 0;
	protected int outlineHeight = 0;
	protected int lastX = 0;
	protected int lastY = 0;
	protected int offsetX = 0;
	protected int offsetY = 0;
	protected int direction = 0;
	protected int destDirection = 0;
	protected int nextDirection = 0;
	protected int clockDirection = 0;
	protected int rotationCount = 0;
	protected int rotationTotal = 0;
	protected int rotationMidPoint = 0;
	protected double currentRotation = 0.0;
	protected double rotateAmount = 0.0;
	protected double rotateTimeSeconds = 0.0;
	protected double outlineRatio = 0.0;
	protected String name = null;
	protected Color outlineColor = null;
	protected Image outlineImage = null;
	protected Image images[] = null;
	protected GameEngine gameEngine = null;

	// define class private functions

	private void calculateRotation(double frameRate)
	{
		double degreesPerMilli = 0.0;
		double millisPerFrame = 0.0;

		degreesPerMilli = (22.5 / (rotateTimeSeconds * 1000.0));
		millisPerFrame = (1000.0 / frameRate);
		rotateAmount = (degreesPerMilli * millisPerFrame);

		rotationCount = 0;
		rotationTotal = (int)(22.5 / rotateAmount);
		rotationMidPoint = (int)((22.5 / rotateAmount) / 2.0);

		if(rotationTotal < 3) {
			rotationTotal = 3;
			rotationMidPoint = 2;
			rotateAmount = (22.5 / 3.0);
		}
	}

	// define class public functions

	public Sprite(String name, int x, int y, int width, int height,
			GameEngine gameEngine)
	{
		debugMode = false;
		isVisible = true;
		isStatic = false;
		isOutline = false;
		isSelectable = false;
		isSelected = false;
		hasOutline = false;
		offsetX = 0;
		offsetY = 0;
		lastX = 0;
		lastY = 0;
		direction = DEFAULT_STARTING_DIRECTION;
		destDirection = DEFAULT_STARTING_DIRECTION;
		nextDirection = DEFAULT_STARTING_DIRECTION;
		clockDirection = CLOCKWISE;
		rotationCount = 0;
		rotationTotal = 0;
		rotationMidPoint = 0;
		currentRotation = 0.0;
		rotateAmount = 0.0;
		outlineRatio = DEFAULT_OUTLINE_RATIO;
		rotateTimeSeconds = DEFAULT_ROTATE_TIME_SECONDS;
		outlineColor = Color.blue;
		outlineImage = null;
		images = new Image[DIRECTIONS];

		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.outlineWidth = width;
		this.outlineHeight = height;
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
		lastX = this.x;
		lastY = this.y;
		this.x = x;
		this.y = y;
	}

	public void setSizes(int width, int height)
	{
		if(this.width == outlineWidth) {
			outlineWidth = width;
		}
		this.width = width;
		if(this.height == outlineHeight) {
			outlineHeight = height;
		}
		this.height = height;
	}

	public void resize(int width, int height)
	{
		boolean exists = false;
		int ii = 0;

		Texture texture = null;

		this.width = width;
		this.height = height;

		try {
			for(ii = 0; ii < DIRECTIONS; ii++) {
				if(images[ii] == null) {
					continue;
				}

				texture = (Texture)gameEngine.getAssetCache().getAsset(
						AssetCache.TYPE_TEXTURE, name + "Resize" + ii);
				if(texture == null) {
					texture = new Texture(name + "Resize" + ii, images[ii],
							gameEngine);
				}
				else {
					exists = true;
				}

				texture.buildMipMap(width, height);
				images[ii] = texture.getMipMap(width, height);

				if(!exists) {
					if(gameEngine.getAssetCache().addAsset(
							AssetCache.TYPE_TEXTURE, name + "Resize" + ii,
							texture)) {
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public Rectangle getLocation()
	{
		return new Rectangle((x + offsetX), (y + offsetY), width, height);
	}

	public boolean isVisible()
	{
		return isVisible;
	}

	public boolean isStatic()
	{
		return isStatic;
	}

	public void setStatic(boolean mode)
	{
		isStatic = mode;
	}

	public boolean isOutline()
	{
		return isOutline;
	}

	public boolean hasOutline()
	{
		return hasOutline;
	}

	public void setOutline(boolean mode)
	{
		isOutline = mode;
	}

	public void setHasOutline(boolean mode)
	{
		hasOutline = mode;
	}

	public void setOutlineRatio(double ratio)
	{
		isOutline = true;
		outlineRatio = ratio;
	}

	public void setOutlineColor(Color color)
	{
		isOutline = true;
		outlineColor = color;
	}

	public void setOutlineImage(Image image)
	{
		outlineImage = image;
	}

	public void setOutlineSize(int width, int height)
	{
		outlineWidth = width;
		outlineHeight = height;
	}

	public boolean isSelectable()
	{
		return isSelectable;
	}

	public boolean isSelected()
	{
		return isSelected;
	}

	public void setSelectable(boolean mode)
	{
		isSelectable = mode;
		if(isSelectable) {
			isOutline = true;
		}
	}

	public void setIsSelected(boolean mode)
	{
		isSelected = mode;
		if(isOutline) {
			hasOutline = mode;
		}
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

	public int getXWithOffset()
	{
		return (offsetX + x);
	}

	public int getYWithOffset()
	{
		return (offsetY + y);
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

	public int getCameraDiffX()
	{
		int result = 0;

		result = (x - lastX);
		lastX = x;

		return result;
	}

	public int getCameraDiffY()
	{
		int result = 0;

		result = (y - lastY);
		lastY = y;

		return result;
	}

	public void setDebugMode(boolean mode)
	{
		debugMode = mode;
	}

	public void setImageTexture(int direction, String imageName)
	{
		Texture texture = null;

		try {
			if((direction < 0) || (direction >= DIRECTIONS)) {
				return;
			}
			texture = (Texture)gameEngine.getAssetCache().getAsset(
					AssetCache.TYPE_TEXTURE, imageName);
			texture.buildMipMap(width, height);
			images[direction] = texture.getMipMap(width, height);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void setImage(int direction, Image image)
	{
		try {
			if((direction < 0) || (direction >= DIRECTIONS)) {
				return;
			}
			images[direction] = image;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public Image getImage(int direction)
	{
		if((direction < 0) || (direction >= DIRECTIONS)) {
			return null;
		}
		return images[direction];
	}

	public void setRotateTimeSeconds(double seconds)
	{
		rotateTimeSeconds = seconds;
	}

	public double getRotateTimeSeconds()
	{
		return rotateTimeSeconds;
	}

	public void resetDirection(int direction)
	{
		this.direction = direction;
		this.destDirection = direction;
		this.nextDirection = direction;
	}

	public int getDirection()
	{
		return direction;
	}

	public void changeDirection(int direction, int clockDirection,
			double frameRate)
	{
		if((direction < 0) || (direction >= DIRECTIONS) ||
				(direction == this.direction) ||
				((clockDirection != CLOCKWISE) &&
				 (clockDirection != COUNTER_CLOCKWISE))) {
			return;
		}

		if((destDirection != this.direction) || (currentRotation != 0.0)) {
			return;
		}

		destDirection = direction;
		if(clockDirection == CLOCKWISE) {
			nextDirection = ((this.direction + 1) % DIRECTIONS);
			calculateRotation(frameRate);
		}
		else {
			nextDirection = (this.direction - 1);
			if(nextDirection < 0) {
				nextDirection = (DIRECTIONS - 1);
			}
			calculateRotation(frameRate);
		}
		this.clockDirection = clockDirection;
	}

	public void processMouseEvent(int type, int mouseX, int mouseY,
			MouseEvent event)
	{
		if((!isOutline) && (!isSelectable)) {
			return;
		}

		if((isSelectable) && (type == MouseDriver.EVENT_PRESSED)) {
			if((mouseX >= (x + offsetX)) &&
					(mouseX <= ((x + offsetX) + width)) &&
					(mouseY >= (y + offsetY)) &&
					(mouseY <= ((y + offsetY) + height))) {
				isSelected = true;
				if(isOutline) {
					hasOutline = true;
				}
			}
			else {
				isSelected = false;
				if(isOutline) {
					hasOutline = false;
				}
			}
		}

		if(isSelected) {
			return;
		}

		if((mouseX >= (x + offsetX)) && (mouseX <= ((x + offsetX) + width)) &&
				(mouseY >= (y + offsetY)) &&
				(mouseY <= ((y + offsetY) + height))) {
			hasOutline = true;
		}
		else {
			hasOutline = false;
		}
	}

	public void processGameplay(double frameRate)
	{
		if((destDirection == direction) && (currentRotation == 0.0)) {
			return;
		}

		if((nextDirection == direction) && (direction != destDirection)) {
			if(clockDirection == CLOCKWISE) {
				nextDirection = ((direction + 1) % DIRECTIONS);
				calculateRotation(frameRate);
				currentRotation = rotateAmount;
			}
			else {
				nextDirection = (direction - 1);
				if(nextDirection < 0) {
					nextDirection = (DIRECTIONS - 1);
				}
				calculateRotation(frameRate);
				currentRotation = -rotateAmount;
			}
		}
		else {
			if(clockDirection == CLOCKWISE) {
				currentRotation += rotateAmount;
			}
			else {
				currentRotation -= rotateAmount;
			}
			rotationCount++;
			if(rotationCount == rotationMidPoint) {
				currentRotation *= -1.0;
				direction = nextDirection;
			}
			else if(rotationCount >= rotationTotal) {
				currentRotation = 0.0;
			}
		}
	}

	public void render2D(Graphics2D render)
	{
		try {
			if(!isVisible) {
				return;
			}

			// perform sprite rotation

			if(currentRotation != 0.0) {
				render.rotate(Math.toRadians(currentRotation),
						((x + offsetX) + (width / 2)),
						((y + offsetY) + (height / 2)));
			}

			// render sprite

			render.drawImage(images[direction], (x + offsetX), (y + offsetY),
					width, height, gameEngine);

			// perform counter-rotation (reset rendering to normal rotation)

			if(currentRotation != 0.0) {
				render.rotate(Math.toRadians(-currentRotation),
						((x + offsetX) + (width / 2)),
						((y + offsetY) + (height / 2)));
			}

			// render outline (if set & mouse hover-over)

			if((isOutline) && (hasOutline)) {
				if(outlineImage == null) {
					render.setColor(outlineColor);

					// draw outline top

					render.drawLine((x + offsetX), (y + offsetY),
							((x + offsetX) + (int)((double)outlineWidth *
								(outlineRatio / 100.0))), (y + offsetY));
					render.drawLine(
							(((x + offsetX) + outlineWidth) -
							 (int)((double)outlineWidth *
								 (outlineRatio / 100.0))),
							(y + offsetY), ((x + offsetX) + outlineWidth),
							(y + offsetY));

					// draw outline right

					render.drawLine(((x + offsetX) + outlineWidth),
							(y + offsetY), ((x + offsetX) + outlineWidth),
							((y + offsetY) + (int)((double)outlineHeight *
								(outlineRatio / 100.00))));
					render.drawLine(((x + offsetX) + outlineWidth),
							((y + offsetY) + (outlineHeight -
								(int)((double)outlineHeight *
									(outlineRatio / 100.00)))),
							((x + offsetX) + outlineWidth),
							((y + offsetY) + outlineHeight));

					// draw outline bottom

					render.drawLine(((x + offsetX) + outlineWidth),
							((y + offsetY) + outlineHeight),
							(((x + offsetX) + outlineWidth) -
							 (int)((double)outlineWidth *
								 (outlineRatio / 100.0))),
							((y + offsetY) + outlineHeight));
					render.drawLine(((x + offsetX) +
								(int)((double)outlineWidth *
									(outlineRatio / 100.0))),
							((y + offsetY) + outlineHeight), (x + offsetX),
							((y + offsetY) + outlineHeight));

					// draw outline left

					render.drawLine((x + offsetX),
							((y + offsetY) + outlineHeight), (x + offsetX),
							((y + offsetY) + (outlineHeight -
								(int)((double)outlineHeight *
									(outlineRatio / 100.00)))));
					render.drawLine((x + offsetX),
							((y + offsetY) + (int)((double)outlineHeight *
								(outlineRatio / 100.00))),
							(x + offsetX), (y + offsetY));
				}
				else {
					render.drawImage(outlineImage, (x + offsetX),
							(y + offsetY), outlineWidth, outlineHeight,
							gameEngine);
				}
			}

			// render debug mode

			if(debugMode) {
				render.setColor(Color.white);
				render.drawString("Sprite{" + name + "}[" +
						DIRECTION_NAMES[direction] + "]@(" + (x + offsetX) +
						", " + (y + offsetY) + "):" + width + "x" + height,
						((x + offsetX) - 4), ((y + offsetY) - 4));
				render.setColor(Color.red);
				render.drawRect((x + offsetX), (y + offsetY), width, height);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

