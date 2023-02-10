/*
 * ScrollBar.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A GUI widget for handling scroll bars.
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
import java.awt.event.MouseWheelEvent;

// import internal packages

import lyra.AssetCache;
import lyra.Texture;
import lyra.GameEngine;

// define class

public class ScrollBar implements Widget
{
	// define private class constants

	private static final String CLASS_NAME = ScrollBar.class.getName();
	private static final int LARGE_STEP_MULT = 8;
	private static final int SMALL_STEP_MULT = 1;
	private static final int MINIMUM_BAR_SIZE = 5;

	// define public class constants

	public static final int STATE_NORMAL = 0;
	public static final int STATE_FOCUSED = 1;
	public static final int STATE_DRAGGING = 2;
	public static final int VERTICAL = 0;
	public static final int HORIZONTAL = 1;
	public static final int SCROLL_UP = 0;
	public static final int SCROLL_DOWN = 1;
	public static final int SCROLL_SLIDE = 2;
	public static final int SCROLL_BAR = 3;

	// define protected class variables
	
	protected boolean isVisible = false;
	protected boolean twoBarsActive = false;
	protected int state = 0;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected int currentOffset = 0;
	protected int possibleViews = 0;
	protected int dragDiff = 0;
	protected int direction = 0;
	protected int upX = 0;
	protected int upY = 0;
	protected int downX = 0;
	protected int downY = 0;
	protected int buttonWidth = 0;
	protected int buttonHeight = 0;
	protected int barX = 0;
	protected int barY = 0;
	protected int barWidth = 0;
	protected int barHeight = 0;
	protected int slideWidth = 0;
	protected int slideHeight = 0;
	protected double stepSize = 0.0;
	protected String name = null;
	protected String imageNames[] = null;
	protected Image images[] = null;
	protected Button upButton = null;
	protected Button downButton = null;
	protected GameEngine gameEngine = null;

	// define private class methods

	private void calculateCoords()
	{
		if(direction == VERTICAL) {
			calculateCoordsVertical();
		}
		else {
			calculateCoordsHorizontal();
		}
	}

	private void calculateCoordsVertical()
	{
		slideWidth = width;
		buttonWidth = slideWidth;
		buttonHeight = slideWidth; // not a typo 
		slideHeight = (height - buttonHeight - buttonHeight);

		if(twoBarsActive) {
			slideHeight -= buttonHeight;
		}

		upX = x;
		upY = y;

		downX = x;
		downY = y + buttonHeight + slideHeight;

		barWidth = slideWidth;

		stepSize = 1.0;
		barHeight = slideHeight - (int)(possibleViews / stepSize);
		if(barHeight < MINIMUM_BAR_SIZE) {
			barHeight = MINIMUM_BAR_SIZE;
		}
		
		barX = x;
		calculateBarLocation();
	}

	private void calculateCoordsHorizontal()
	{
		slideHeight = height;
		buttonHeight = slideHeight;
		buttonWidth = slideHeight; // not a typo
		slideWidth = (width - buttonWidth - buttonWidth);

		if(twoBarsActive) {
			slideWidth -= buttonWidth;
		}

		upX = x;
		upY = y;

		downX = x + buttonWidth + slideWidth;
		downY = y;

		barHeight = slideHeight;

		stepSize = 1.0;
		barWidth = slideWidth - (int)(possibleViews / stepSize);
		if(barWidth < MINIMUM_BAR_SIZE) {
			barWidth = MINIMUM_BAR_SIZE;
		}
		
		barY = y;
		calculateBarLocation();
	}

	private void calculateBarLocation()
	{
		int cutOff = 0;

		if(direction == VERTICAL) {
			barY = (y + buttonHeight + (int)((double)currentOffset / stepSize));
			cutOff = (y + height - buttonHeight - MINIMUM_BAR_SIZE
					- (twoBarsActive ? buttonHeight : 0));
			if(barY > cutOff) {
				barY = cutOff;
			}
		}
		else {
			barX = (x + buttonWidth + (int) ((double)currentOffset / stepSize));
			cutOff = (x + width - buttonWidth - MINIMUM_BAR_SIZE
					- (twoBarsActive ? buttonWidth : 0));
			if(barX > cutOff) {
				barX = cutOff;
			}
		}
	}

	private void updateButtons()
	{
//		upButton.resize(buttonWidth, buttonHeight);
//		downButton.resize(buttonWidth, buttonHeight);
		upButton.setLocation(upX, upY);
		downButton.setLocation(downX, downY);
	}

	// define public class methods

	public ScrollBar(String name, int x, int y, int width, int height, 
			int direction, GameEngine gameEngine)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.direction = direction;
		this.name = name;
		this.gameEngine = gameEngine;

		isVisible = true;
		twoBarsActive = false;
		state = STATE_NORMAL;
		currentOffset = 0;
		possibleViews = 0;
		dragDiff = 0;
		upX = 0;
		upY = 0;
		downX = 0;
		downY = 0;
		buttonWidth = 0;
		buttonHeight = 0;
		barX = 0;
		barY = 0;
		barWidth = 0;
		barHeight = 0;
		slideWidth = 0;
		slideHeight = 0;
		stepSize = 0.0;
		imageNames = null;
		images = null;
		upButton = null;
		downButton = null;

		calculateCoords();

		// when horizontal buttonUp refers to left, buttonDown to right
		
		upButton = new Button(name + "Up", upX, upY, buttonWidth, buttonHeight,
				gameEngine);
		downButton = new Button(name + "Down", downX, downY, buttonWidth,
				buttonHeight, gameEngine);

		imageNames = new String[4];
		images = new Image[4];
		images[SCROLL_UP] = null;
		images[SCROLL_BAR] = null;
		images[SCROLL_DOWN] = null;
		images[SCROLL_SLIDE] = null;
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

	public void setVisibility(boolean mode)
	{
		isVisible = mode;
	}

	public boolean isVisible()
	{
		return isVisible;
	}

	public void resize(int width, int height)
	{
		int ii = 0;
		Texture texture = null;

		this.width = width;
		this.height = height;

		for(ii = 0; ii < SCROLL_BAR; ii++) {
			try {
				texture = (Texture)gameEngine.getAssetCache().getAsset(
						AssetCache.TYPE_TEXTURE, imageNames[ii]);
				texture.buildMipMap(width, height);
				images[ii] = texture.getMipMap(width, height);

				if(ii == SCROLL_DOWN) {
					downButton.setImage(Button.STATE_NORMAL, imageNames[ii]);
					downButton.setImage(Button.STATE_FOCUSED, imageNames[ii]);
					downButton.setImage(Button.STATE_HIGHLIGHTED,
							imageNames[ii]);
					downButton.setImage(Button.STATE_DOWN, imageNames[ii]);
				}
				else if(ii == SCROLL_UP) {
					upButton.setImage(Button.STATE_NORMAL, imageNames[ii]);
					upButton.setImage(Button.STATE_FOCUSED, imageNames[ii]);
					upButton.setImage(Button.STATE_HIGHLIGHTED, imageNames[ii]);
					upButton.setImage(Button.STATE_DOWN, imageNames[ii]);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void setPossibleViews(int possibleViews)
	{
		this.possibleViews = possibleViews;
		calculateCoords();
	}

	public void setCurrentOffset(int currentOffset)
	{
		this.currentOffset = currentOffset;
		calculateCoords();
	}

	public void setTwoBarsState(boolean mode)
	{
		twoBarsActive = mode;
		calculateCoords();
		updateButtons();
	}

	public void setImage(int id, String imageName)
	{
		Texture texture = null;

		if((id < 0) || (id > SCROLL_BAR)) {
			return;
		}

		try {
			texture = (Texture)gameEngine.getAssetCache().getAsset(
					AssetCache.TYPE_TEXTURE, imageName);
			texture.buildMipMap(width, height);
			images[id] = texture.getMipMap(width, height);

			if(id == SCROLL_DOWN) {
				downButton.setImage(Button.STATE_NORMAL, imageName);
				downButton.setImage(Button.STATE_FOCUSED, imageName);
				downButton.setImage(Button.STATE_HIGHLIGHTED, imageName);
				downButton.setImage(Button.STATE_DOWN, imageName);
			}
			else if(id == SCROLL_UP) {
				upButton.setImage(Button.STATE_NORMAL, imageName);
				upButton.setImage(Button.STATE_FOCUSED, imageName);
				upButton.setImage(Button.STATE_HIGHLIGHTED, imageName);
				upButton.setImage(Button.STATE_DOWN, imageName);
			}

			imageNames[id] = imageName;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public Image getImage(int id)
	{
		if((id >= 0) || (id <= SCROLL_BAR)) {
			return images[id];
		}
		return null;
	}

	public int getCurrentOffset()
	{
		return currentOffset;
	}

	public boolean hasFocus(int mouseX, int mouseY)
	{
		if(!isVisible) {
			state = STATE_NORMAL;
			return false;
		}

		if(!((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
					(mouseY <= (y + height)))) {
			return false;
		}

		upButton.hasFocus(mouseX, mouseY);
		downButton.hasFocus(mouseX, mouseY);

		return true;
	}

	public void deFocus()
	{
		state = STATE_NORMAL;
	}

	public boolean mouseDragged(int mouseX, int mouseY)
	{
		if(!isVisible) {
			return false;
		}

		if(state == STATE_DRAGGING) {
			if(direction == VERTICAL) {
				currentOffset = (int)(stepSize *
						(double)(mouseY - dragDiff - y - buttonHeight));
			}
			else {
				currentOffset = (int)(stepSize *
						(double)(mouseX - dragDiff - x - buttonWidth));
			}

			if(currentOffset < 0) {
				currentOffset = 0;
			}

			if(currentOffset > possibleViews) {
				currentOffset = possibleViews;
			}

			calculateCoords();
		}

		if(!((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
					(mouseY <= (y + height)))) {
			return false;
		}

		if((mouseX >= barX) && (mouseX <= (barX + barWidth)) &&
				(mouseY >= barY) && (mouseY <= (barY + barHeight))) {
			if(state != STATE_DRAGGING) {
				if(direction == HORIZONTAL) {
					dragDiff = mouseX - barX;
				} 
				else {
					dragDiff = mouseY - barY;
				}
			}
			state = STATE_DRAGGING;
		}

		return true;
	}

	public boolean mouseReleased(int mouseX, int mouseY)
	{
		if(!isVisible) {
			return false;
		}

		//drag regardless of mouse location

		if(state == STATE_DRAGGING) {
			state = STATE_NORMAL;
		}

		// if released in boundaries, return true

		if(!((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
					(mouseY <= (y + height)))) {
			return false;
		}

		return true;
	}
	
	public boolean mouseWheelMoved(MouseWheelEvent event)
	{
		if(!isVisible) {
			return false;
		}

		// assume this is triggered only by widgets that it's attached
		// to, when appropriate-

		int rotations = event.getWheelRotation() * -1;

		currentOffset = (int) ((double)currentOffset - 
				(SMALL_STEP_MULT * stepSize * rotations));

		if(currentOffset < 0)
			currentOffset = 0;

		if(currentOffset > possibleViews)
			currentOffset = possibleViews;

		calculateCoords();
		return true;
	}

	public boolean isClicked(int mouseX, int mouseY)
	{
		if(!isVisible) {
			return false;
		}

		if(!((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
						(mouseY <= (y + height)))) {
			return false;
		}

		// bar clicked

		if((mouseX >= barX) && (mouseX <= (barX + barWidth)) &&
				(mouseY >= barY) && (mouseY <= (barY + barHeight))) {
			// do nothing
		}
		else if(upButton.isClicked(mouseX, mouseY)) {
			currentOffset = (int) ((double)currentOffset - 
					(SMALL_STEP_MULT * stepSize));
			if(currentOffset < 0) {
				currentOffset = 0;
			}
		}
		else if(downButton.isClicked(mouseX, mouseY)) {
			currentOffset = (int) ((double)currentOffset + 
					(SMALL_STEP_MULT * stepSize));
			if(currentOffset > possibleViews) {
				currentOffset = possibleViews;
			}
		}
		else if((mouseY > (y + buttonHeight + slideHeight)) ||
				(mouseX > (x + buttonWidth + slideWidth))) {
			// clicked in area below bar when 2active bars
		}
		else {
			// slide clicked
			if(((direction == VERTICAL) && (mouseY > barY)) ||
					(direction == HORIZONTAL) && (mouseX > barX)) {
				// below bar
				currentOffset = (int) ((double)currentOffset + 
						(LARGE_STEP_MULT * stepSize));
				if(currentOffset > possibleViews) {
					currentOffset = possibleViews;
				}
			}
			else {
				// above bar
				currentOffset = (int) ((double)currentOffset - 
						(LARGE_STEP_MULT * stepSize));
				if(currentOffset < 0) {
					currentOffset = 0;
				}
			}
		}

		calculateCoords();

		return true;
	}

	public void keyPressed(KeyEvent event)
	{
		// do nothing
	}
	
	public void render2D(Graphics2D render) 
	{
		try {
			if(!isVisible) {
				return;
			}

			// the slide

			if(images[SCROLL_SLIDE] == null) {
//				render.setColor(Color.lightGray);
				render.setColor(Color.white);
				render.fillRect(x, y, width, height);
			}
			else {
				render.drawImage(images[SCROLL_SLIDE], x, y, width, height,
						gameEngine);
			}

			// the bar

			if(images[SCROLL_BAR] == null) {
				render.setColor(Color.darkGray);
				render.fillRect(barX, barY, barWidth, barHeight);
			}
			else {
				render.drawImage(images[SCROLL_BAR], barX, barY, barWidth,
						barHeight, gameEngine);
			}

			// the buttons

			upButton.render2D(render);
			downButton.render2D(render);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

