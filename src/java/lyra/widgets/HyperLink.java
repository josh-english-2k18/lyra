/*
 * HyperLink.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A GUI widget for handling HTTP-syle hyper-links.
 *
 * Written by Josh English.
 */

// define package space

package lyra.widgets;

// import external packages

import java.net.URL;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.KeyEvent;

// import internal packages

import lyra.GameEngine;

// define class

public class HyperLink implements Widget
{
	// define private class constants

	private static final String CLASS_NAME = HyperLink.class.getName();

	// define public class constants

	public static final int STATE_NORMAL = 0;
	public static final int STATE_FOCUSED = 1;
	public static final int STATE_DOWN = 2;
	public static final int STATE_CLICKED = 3;
	public static final int STATE_DISABLED = 4;

	public static final int STATES = 5;

	// define protected class variables

	protected boolean isVisible = false;
	protected boolean autoRedirect = false;
	protected boolean wasClicked = false;
	protected int state = 0;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected Color linkColor = null;
	protected Color downColor = null;
	protected Color clickedColor = null;
	protected String name = null;
	protected String text = null;
	protected String url = null;
	protected Cursor gameCursor = null;
	protected Cursor linkCursor = null;
	protected FontInfo fontInfo = null;
	protected GameEngine gameEngine = null;

	// define class public functions

	public HyperLink(String name, int x, int y, GameEngine gameEngine)
	{
		this.name = name;
		this.x = x;
		this.y = y;
		this.gameEngine = gameEngine;

		isVisible = true;
		autoRedirect = false;
		wasClicked = false;
		state = STATE_NORMAL;
		linkColor = Color.blue;
		downColor = Color.orange;
		clickedColor = Color.magenta;
		text = null;
		url = null;
		gameCursor = gameEngine.getCursor();
		linkCursor = new Cursor(Cursor.HAND_CURSOR);
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

	public double getPrecisionWidth()
	{
		return fontInfo.getPrecisionWidth();
	}

	public int getHeight()
	{
		return height;
	}

	public double getPrecisionHeight()
	{
		return fontInfo.getPrecisionHeight();
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

	public boolean wasClicked()
	{
		return wasClicked;
	}

	public void setAutoRedirect(boolean value)
	{
		autoRedirect = value;
	}

	public void setLinkColors(Color linkColor, Color downColor,
			Color clickedColor)
	{
		this.linkColor = linkColor;
		this.downColor = downColor;
		this.clickedColor = clickedColor;
	}

	public void setLink(String text, String url)
	{
		this.text = text;
		this.url = url;

		fontInfo.setString(text);
		width = fontInfo.getWidth();
		height = fontInfo.getHeight();
	}

	public String getLinkText()
	{
		return text;
	}

	public String getLinkURL()
	{
		return url;
	}

	public void setEnabled()
	{
		if(wasClicked) {
			state = STATE_CLICKED;
		}
		else {
			state = STATE_NORMAL;
		}
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

		if(result) {
			gameEngine.setCursor(linkCursor);
			state = STATE_FOCUSED;
		}
		else {
			if((state == STATE_FOCUSED) || (state == STATE_DOWN)) {
				if(gameEngine.getCursor() != gameCursor) {
					gameEngine.setCursor(gameCursor);
				}
			}
			if(wasClicked) {
				state = STATE_CLICKED;
			}
			else {
				state = STATE_NORMAL;
			}
		}

		return result;
	}

	public void deFocus()
	{
		state = STATE_NORMAL;
		wasClicked = false;
	}

	public boolean isClicked(int mouseX, int mouseY)
	{
		boolean result = false;

		URL redirect = null;

		if(!isVisible) {
			return false;
		}

		if((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
				(mouseY <= (y + height))) {
			result = true;
		}

		if(result) {
			state = STATE_DOWN;
			wasClicked = true;
			state = STATE_CLICKED;
			if(autoRedirect) {
				try {
					redirect = new URL(url);
					gameEngine.getAppletContext().showDocument(redirect,
							"_blank");
				}
				catch(Exception e) {
					e.printStackTrace();
				}
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

			if((state == STATE_NORMAL) || (state == STATE_FOCUSED) ||
					(state == STATE_DISABLED)) {
				render.setColor(linkColor);
			}
			else if(state == STATE_DOWN) {
				render.setColor(downColor);
			}
			else {
				render.setColor(clickedColor);
			}

			fontInfo.setString(text);
			fontX = x;
			fontY = (int)((double)y + fontInfo.getPrecisionHeight());
			render.drawString(text, fontX, fontY);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

