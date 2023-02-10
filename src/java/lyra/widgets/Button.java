/*
 * Button.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A GUI widget for handling buttons.
 *
 * Written by Josh English.
 */

// define package space

package lyra.widgets;

// import external packages

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;

// import internal packages

import lyra.AssetCache;
import lyra.GameEngine;
import lyra.Texture;

// define class

public class Button implements Widget
{
	// define private class constants

	private static final String CLASS_NAME = Button.class.getName();
	private static final Color DEFAULT_COLOR = Color.black;

	// define public class constants

	public static final int STATE_NORMAL = 0;
	public static final int STATE_FOCUSED = 1;
	public static final int STATE_HIGHLIGHTED = 2;
	public static final int STATE_DOWN = 3;
	public static final int STATES = 4;

	// define protected class variables

	protected boolean isVisible = false;
	protected boolean isFixedState = false;
	protected boolean wasHighlighted = false;
	protected int state = 0;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected String name = null;
	protected Color color = null;
	protected Color backgroundColor = null;
	protected String text = null;
	protected Image background = null;
	protected ButtonRender buttonRender[] = null;
	protected FontInfo fontInfo = null;
	protected GameEngine gameEngine = null;

	// define class public functions

	public Button(String name, int x, int y, int width, int height,
			GameEngine gameEngine)
	{
		int ii = 0;

		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.gameEngine = gameEngine;

		isVisible = true;
		isFixedState = false;
		wasHighlighted = false;
		state = STATE_NORMAL;
		color = DEFAULT_COLOR;
		backgroundColor = null;
		text = new String("");
		background = null;
		buttonRender = new ButtonRender[STATES];
		for(ii = 0; ii < STATES; ii++) {
			buttonRender[ii] = new ButtonRender(name + "ButtonRender" + ii,
					x, y, width, height, gameEngine);
		}
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
		int ii = 0;

		this.x = x;
		this.y = y;

		for(ii = 0; ii < STATES; ii++) {
			buttonRender[ii].setLocation(x, y);
		}
	}

	public void resize(int width, int height)
	{
		int ii = 0;

		this.width = width;
		this.height = height;

		for(ii = 0; ii < STATES; ii++) {
			buttonRender[ii].resize(width, height);
		}
	}

	public void setVisibility(boolean mode)
	{
		isVisible = mode;
	}

	public boolean isVisible()
	{
		return isVisible;
	}

	public boolean hasFocus(int mouseX, int mouseY)
	{
		boolean result = false;

		if(!isVisible) {
			state = STATE_NORMAL;
			return false;
		}

		if((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
				(mouseY <= (y + height))) {
			result = true;
		}

		if(state == STATE_DOWN) {
			if(result) {
				state = STATE_HIGHLIGHTED;
				wasHighlighted = true;
			}
			else {
				state = STATE_NORMAL;
			}
		}
		else if(state == STATE_HIGHLIGHTED) {
			if(result) {
				state = STATE_FOCUSED;
			}
			else {
				if(wasHighlighted) {
					state = STATE_HIGHLIGHTED;
				}
				else {
					state = STATE_NORMAL;
				}
			}
		}
		else {
			if(result) {
				state = STATE_FOCUSED;
			}
			else {
				if(wasHighlighted) {
					state = STATE_HIGHLIGHTED;
				}
				else {
					state = STATE_NORMAL;
				}
			}
		}

		return result;
	}

	public void deFocus()
	{
		if(state != STATE_HIGHLIGHTED) {
			state = STATE_NORMAL;
		}
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
			if(isFixedState) {
				if(state == STATE_DOWN) {
					state = STATE_FOCUSED;
				}
				else {
					state = STATE_DOWN;
				}
			}
			else {
				state = STATE_DOWN;
			}
		}

		return result;
	}

	public void keyPressed(KeyEvent event)
	{
		// do nothing
	}

	public void setFixedState(boolean state)
	{
		this.isFixedState = state;
	}

	public boolean getFixedState()
	{
		return isFixedState;
	}

	public void setButtonText(Color color, String text)
	{
		this.color = color;
		this.text = text;
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public boolean isHighlighted()
	{
		if(state == STATE_HIGHLIGHTED) {
			return true;
		}
		return false;
	}

	public void setUnHighlighted()
	{
		state = STATE_NORMAL;
		wasHighlighted = false;
	}

	public boolean isFocused()
	{
		if(state == STATE_FOCUSED) {
			return true;
		}
		return false;
	}

	public boolean isDown()
	{
		if(state == STATE_DOWN) {
			return true;
		}
		return false;
	}

	public void setImage(int id, String imageName)
	{
		try {
			if((id < 0) || (id > STATES)) {
				return;
			}

			buttonRender[id].setImage(imageName);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void setUncorneredImage(int id, String imageName)
	{
		try {
			if((id < 0) || (id > STATES)) {
				return;
			}

			buttonRender[id].setUncorneredImage(imageName);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
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
		int fontX = 0;
		int fontY = 0;

		try {
			if(!isVisible) {
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

			// render image componenets

			buttonRender[state].render2D(render);

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

