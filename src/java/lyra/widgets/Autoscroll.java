/*
 * Autoscroll.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A GUI widget for handling auto-scrolling widgets.
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

public class Autoscroll implements Widget
{
	// define private class constants

	private static final String CLASS_NAME = Autoscroll.class.getName();

	// define public class constants

	public static final int MAX_ENTRIES = 128;

	public static final Color DEFAULT_TEXT_COLOR = Color.black;
	public static final Color DEFAULT_BACKGROUND_COLOR = Color.white;
	public static final Color DEFAULT_OUTLINE_COLOR = Color.black;

	// define protected class variables

	protected boolean isVisible = false;
	protected boolean isRunning = false;
	protected boolean hasOutline = false;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected int scrollTicks = 0;
	protected int scrollCount = 0;
	protected String name = null;
	protected Color backgroundColor = null;
	protected Color textColor = null;
	protected Color outlineColor = null;
	protected Image background = null;
	protected Entry entries[] = null;
	protected FontInfo fontInfo = null;
	protected GameEngine gameEngine = null;

	// define class private inner classes

	private class Entry
	{
		// define public class constants

		private static final int TYPE_TEXT = 1;
		private static final int TYPE_IMAGE = 2;
		private static final int TYPE_HYPERLINK = 3;

		// define private class variables

		private int type = 0;
		private int width = 0;
		private int height = 0;
		private Color color = null;
		private Object object = null;

		// define class public functions

		public Entry(int type, int width, int height, Object object)
		{
			this.type = type;
			this.width = width;
			this.height = height;
			this.object = object;
		}

		public int getType()
		{
			return type;
		}

		public int getWidth()
		{
			return width;
		}

		public int getHeight()
		{
			return height;
		}

		public Color getColor()
		{
			return color;
		}

		public void setColor(Color color)
		{
			this.color = color;
		}

		public Object getObject()
		{
			return object;
		}
	}

	// define class public functions

	public Autoscroll(String name, int x, int y, int width, int height,
			GameEngine gameEngine)
	{
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.gameEngine = gameEngine;

		isVisible = true;
		isRunning = true;
		hasOutline = false;
		scrollTicks = 0;
		scrollCount = 0;
		backgroundColor = DEFAULT_BACKGROUND_COLOR;
		textColor = DEFAULT_TEXT_COLOR;
		outlineColor = DEFAULT_OUTLINE_COLOR;
		background = null;
		entries = new Entry[MAX_ENTRIES];
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

	public void setVisibility(boolean mode)
	{
		isVisible = mode;
	}

	public boolean isVisible()
	{
		return isVisible;
	}

	public boolean isRunning()
	{
		return isRunning;
	}

	public void setBackgroundColor(Color color)
	{
		backgroundColor = color;
	}

	public void setBackground(Image image)
	{
		background = image;
	}

	public void setTextColor(Color color)
	{
		textColor = color;
	}

	public void setOutlineColor(Color color)
	{
		hasOutline = true;
		outlineColor = color;
	}

	public void setHasOutline(boolean mode)
	{
		hasOutline = mode;
	}

	public boolean hasOutline()
	{
		return hasOutline;
	}

	public void addText(Color color, String text)
	{
		Entry entry = null;

		if(scrollCount >= MAX_ENTRIES) {
			return;
		}

		fontInfo.setString(text);

		entry = new Entry(Entry.TYPE_TEXT, fontInfo.getWidth(),
				fontInfo.getHeight(), text);
		entry.setColor(color);

		entries[scrollCount] = entry;
		scrollCount++;
	}

	public void addText(String text)
	{
		Entry entry = null;

		if(scrollCount >= MAX_ENTRIES) {
			return;
		}

		fontInfo.setString(text);

		entry = new Entry(Entry.TYPE_TEXT, fontInfo.getWidth(),
				fontInfo.getHeight(), text);
		entry.setColor(textColor);

		entries[scrollCount] = entry;
		scrollCount++;
	}

	public void addImage(Image image, int width, int height)
	{
		Entry entry = null;

		if(scrollCount >= MAX_ENTRIES) {
			return;
		}

		entry = new Entry(Entry.TYPE_IMAGE, width, height, image);

		entries[scrollCount] = entry;
		scrollCount++;
	}

	public void addHyperlink(HyperLink link)
	{
		Entry entry = null;

		if(scrollCount >= MAX_ENTRIES) {
			return;
		}

		entry = new Entry(Entry.TYPE_HYPERLINK, link.getWidth(),
				link.getHeight(), link);

		entries[scrollCount] = entry;
		scrollCount++;
	}

	public boolean hasFocus(int mouseX, int mouseY)
	{
		boolean result = false;
		boolean hasFocus = false;
		int ii = 0;

		HyperLink link = null;

		if(!isVisible) {
			return false;
		}

		if((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
				(mouseY <= (y + height))) {
			result = true;
		}

		if(result) {
			for(ii = 0; ii < scrollCount; ii++) {
				if(entries[ii].getType() == Entry.TYPE_HYPERLINK) {
					link = (HyperLink)entries[ii].getObject();
					if(!hasFocus) {
						if(link.hasFocus(mouseX, mouseY)) {
							hasFocus = true;
						}
					}
					else {
						link.deFocus();
					}
				}
			}
		}

		return result;
	}

	public void deFocus()
	{
		int ii = 0;

		HyperLink link = null;

		for(ii = 0; ii < scrollCount; ii++) {
			if(entries[ii].getType() == Entry.TYPE_HYPERLINK) {
				link = (HyperLink)entries[ii].getObject();
				link.deFocus();
			}
		}
	}

	public boolean isClicked(int mouseX, int mouseY)
	{
		boolean result = false;
		int ii = 0;

		HyperLink link = null;

		if(!isVisible) {
			return false;
		}

		if((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
				(mouseY <= (y + height))) {
			result = true;
		}

		if(result) {
			for(ii = 0; ii < scrollCount; ii++) {
				if(entries[ii].getType() == Entry.TYPE_HYPERLINK) {
					link = (HyperLink)entries[ii].getObject();
					link.isClicked(mouseX, mouseY);
				}
			}
		}

		return result;
	}

	public void keyPressed(KeyEvent event)
	{
		// do nothing
	}

	public void reset()
	{
		isRunning = true;
		scrollTicks = 0;
	}

	public void process()
	{
		scrollTicks++;
	}

	public void render2D(Graphics2D render)
	{
		int ii = 0;
		int localX = 0;
		int localY = 0;
		int localTicks = 0;
		int localOffset = 0;
		int renderCount = 0;

		String text = null;
		Image image = null;
		HyperLink link = null;

		try {
			if(!isVisible) {
				return;
			}

			// render scroll area

			if(background == null) {
				render.setColor(backgroundColor);
				render.fillRect(x, y, width, height);
			}
			else {
				render.drawImage(background, x, y, width, height, gameEngine);
			}

			if(hasOutline) {
				render.setColor(outlineColor);
				render.drawRect(x, y, width, height);
			}

			if(!isRunning) {
				return;
			}

			// render scroll entries

			localX = x;
			localY = (y + 2);
			localOffset = (scrollTicks % height);
			for(ii = 0; ii < scrollCount; ii++) {
				if((localY + entries[ii].getHeight()) >= (y + height)) {
					break;
				}

				localTicks += (entries[ii].getHeight() + 4);
				if(localTicks < scrollTicks) {
					if(ii < (scrollCount - 1)) {
						if((localTicks + entries[(ii + 1)].getHeight() + 4) >=
								scrollTicks) {
							localOffset = (scrollTicks - localTicks);
						}
					}
					continue;
				}

				if((localY - localOffset) < (y + 2)) {
					localY += (entries[ii].getHeight() + 4);
					continue;
				}

				if(entries[ii].getType() == Entry.TYPE_TEXT) {
					text = (String)entries[ii].getObject();
					render.setColor(entries[ii].getColor());
					render.drawString(text,
							(localX +
							 ((width / 2) - entries[ii].getWidth() / 2)),
							((localY + entries[ii].getHeight()) -
							 localOffset));
				}
				else if(entries[ii].getType() == Entry.TYPE_IMAGE) {
					image = (Image)entries[ii].getObject();
					render.drawImage(image,
							(localX +
							 ((width / 2) - entries[ii].getWidth() / 2)),
							(localY - localOffset),
							entries[ii].getWidth(), entries[ii].getHeight(),
							gameEngine);
				}
				else if(entries[ii].getType() == Entry.TYPE_HYPERLINK) {
					link = (HyperLink)entries[ii].getObject();
					link.setLocation(
							(localX +
							 ((width / 2) - entries[ii].getWidth() / 2)),
							(localY - localOffset));
					link.render2D(render);
				}

				renderCount++;

				localY += (entries[ii].getHeight() + 4);
				if(localY >= (y + height)) {
					break;
				}
			}

			if(renderCount < 1) {
				isRunning = false;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

