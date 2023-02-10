/*
 * DropdownBox.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A GUI widget for handling dropdown boxes.
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

public class DropdownBox implements Widget
{
	// define private class constants

	private static final String CLASS_NAME = DropdownBox.class.getName();

	// define public class constants

	public static final int MAX_ENTRIES = 128;

	public static final Color DEFAULT_BOX_COLOR = Color.gray;
	public static final Color DEFAULT_TEXT_COLOR = Color.black;
	public static final Color DEFAULT_FOREGROUND_COLOR = Color.white;
	public static final Color DEFAULT_BACKGROUND_COLOR = Color.blue;
	public static final Color DEFAULT_OUTLINE_COLOR = Color.black;

	// define protected class variables

	protected boolean isVisible = false;
	protected boolean isInDropdown = false;
	protected boolean isSelected[] = null;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected int downWidth = 0;
	protected int downHeight = 0;
	protected int entryCount = 0;
	protected String name = null;
	protected String selection = null;
	protected String entries[] = null;
	protected Color boxColor = null;
	protected Color textColor = null;
	protected Color foregroundColor = null;
	protected Color backgroundColor = null;
	protected Color outlineColor = null;
	protected Button button = null;
	protected FontInfo fontInfo = null;
	protected GameEngine gameEngine = null;

	// define class public functions

	public DropdownBox(String name, int x, int y, int width, int height,
			GameEngine gameEngine)
	{
		int buttonSize = 0;

		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.gameEngine = gameEngine;

		isVisible = true;
		isInDropdown = false;
		isSelected = new boolean[MAX_ENTRIES];
		downWidth = 0;
		downHeight = 0;
		entryCount = 0;
		entries = new String[MAX_ENTRIES];
		boxColor = DEFAULT_BOX_COLOR;
		textColor = DEFAULT_TEXT_COLOR;
		foregroundColor = DEFAULT_FOREGROUND_COLOR;
		backgroundColor = DEFAULT_BACKGROUND_COLOR;
		outlineColor = DEFAULT_OUTLINE_COLOR;

		buttonSize = (height - 4);
		button = new Button(name + "Button",
				(((x + width) - buttonSize) - 2), (y + 2),
				buttonSize, buttonSize, gameEngine);

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
		int buttonSize = 0;

		this.x = x;
		this.y = y;

		buttonSize = (height - 4);
		button.setLocation((((x + width) - buttonSize) - 2), (y + 2));
	}

	public void setVisibility(boolean mode)
	{
		isVisible = mode;
		button.setVisibility(mode);
	}

	public boolean isVisible()
	{
		return isVisible;
	}

	public void setBoxColor(Color color)
	{
		boxColor = color;
	}

	public void setTextColor(Color color)
	{
		textColor = color;
	}

	public void setForegroundColor(Color color)
	{
		foregroundColor = color;
	}

	public void setBackgroundColor(Color color)
	{
		backgroundColor = color;
	}

	public void setOutlineColor(Color color)
	{
		outlineColor = color;
	}

	public Button getButton()
	{
		return button;
	}

	public void addEntry(String entry)
	{
		int ii = 0;

		if(entryCount >= MAX_ENTRIES) {
			return;
		}

		if(selection == null) {
			isSelected[entryCount] = true;
			selection = entry;
		}
		else {
			isSelected[entryCount] = false;
		}

		entries[entryCount] = entry;
		entryCount++;

		downWidth = 0;
		for(ii = 0; ii < entryCount; ii++) {
			fontInfo.setString(entries[ii]);
			if(fontInfo.getWidth() > downWidth) {
				downWidth = fontInfo.getWidth();
			}
		}
		if(downWidth < width) {
			downWidth = width;
		}
		downWidth += 4;

		downHeight = ((fontInfo.getHeight() + 4) * entryCount);
	}

	public void setSelection(String entry)
	{
		int ii = 0;

		for(ii = 0; ii < entryCount; ii++) {
			if(entries[ii].equals(entry)) {
				selection = entry;
				break;
			}
		}
	}

	public String getSelection()
	{
		return selection;
	}

	public boolean hasFocus(int mouseX, int mouseY)
	{
		boolean result = false;
		int ii = 0;
		int ref = 0;
		int localX = 0;
		int localY = 0;

		if(!isVisible) {
			return false;
		}

		for(ii = 0; ii < entryCount; ii++) {
			isSelected[ii] = false;
		}

		if((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
				(mouseY <= (y + height))) {
			result = true;
		}
		else if(isInDropdown) {
			localX = x;
			localY = (y + height);
			if((mouseX >= localX) && (mouseX <= (localX + downWidth)) &&
					(mouseY >= localY) && (mouseY <= (localY + downHeight))) {
				result = true;
			}
			if(result) {
				ref = ((downHeight - (mouseY - localY)) /
						(fontInfo.getHeight() + 4));
				if((ref >= 0) && (ref < entryCount)) {
					ref = ((entryCount - 1) - ref);
					isSelected[ref] = true;
				}
			}
		}

		button.hasFocus(mouseX, mouseY);

		return result;
	}

	public void deFocus()
	{
		button.deFocus();
	}

	public boolean isClicked(int mouseX, int mouseY)
	{
		boolean inWindow = false;
		boolean result = false;
		int ref = 0;
		int localX = 0;
		int localY = 0;

		if(!isVisible) {
			return false;
		}

		if((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
				(mouseY <= (y + height))) {
			inWindow = true;
			result = true;
		}
		else if(isInDropdown) {
			localX = x;
			localY = (y + height);
			if((mouseX >= localX) && (mouseX <= (localX + downWidth)) &&
					(mouseY >= localY) && (mouseY <= (localY + downHeight))) {
				result = true;
			}
			if(result) {
				ref = ((downHeight - (mouseY - localY)) /
						(fontInfo.getHeight() + 4));
				if((ref >= 0) && (ref < entryCount)) {
					ref = ((entryCount - 1) - ref);
					selection = entries[ref];
				}
			}
		}

		if(isInDropdown) {
			isInDropdown = false;
		}

		if((inWindow) || (button.isClicked(mouseX, mouseY))) {
			if(isInDropdown) {
				isInDropdown = false;
			}
			else {
				isInDropdown = true;
			}
		}

		return result;
	}

	public void keyPressed(KeyEvent event)
	{
		button.keyPressed(event);
	}

	public void render2D(Graphics2D render)
	{
		int ii = 0;
		int localX = 0;
		int localY = 0;

		try {
			if(!isVisible) {
				return;
			}

			// render box

			render.setColor(Color.white);
			render.fillRect(x, y, width, height);
			render.setColor(boxColor);
			render.drawRect(x, y, width, height);

			if(selection != null) {
				fontInfo.setString(selection);
				render.setColor(Color.black);
				render.drawString(selection, (x + 1),
						(y + fontInfo.getHeight() + 2));
			}

			button.render2D(render);

			// render dropdown

			if(isInDropdown) {
				localX = x;
				localY = (y + height);
				render.setColor(Color.white);
				render.fillRect(localX, localY, downWidth, downHeight);
				render.setColor(outlineColor);
				render.drawRect(localX, localY, downWidth, downHeight);

				localX += 1;
				localY += (fontInfo.getHeight() + 2);
				for(ii = 0; ii < entryCount; ii++) {
					if(!isSelected[ii]) {
						render.setColor(textColor);
					}
					else {
						render.setColor(backgroundColor);
						render.fillRect(localX,
								(localY - fontInfo.getHeight()),
								(downWidth - 2), fontInfo.getHeight());
						render.setColor(foregroundColor);
					}

					render.drawString(entries[ii], localX, localY);

					localY += (fontInfo.getHeight() + 4);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

