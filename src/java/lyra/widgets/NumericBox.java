/*
 * NumericBox.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A GUI widget for handling number-range selection boxes.
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

public class NumericBox implements Widget
{
	// define private class constants

	private static final String CLASS_NAME = NumericBox.class.getName();

	private static final int UP_ARROW = 0;
	private static final int DOWN_ARROW = 1;
	private static final int OK = 2;
	private static final int CANCEL = 3;

	private static final int BUTTONS = 4;

	// define public class constants

	public static final Color DEFAULT_TEXT_COLOR = Color.black;
	public static final Color DEFAULT_BACKGROUND_COLOR = Color.white;
	public static final Color DEFAULT_OUTLINE_COLOR = Color.black;

	// define protected class variables

	protected boolean isVisible = false;
	protected boolean hasResult = false;
	protected boolean isCancelled = false;
	protected boolean skipProcess = false;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected int textX = 0;
	protected int textY = 0;
	protected int textWidth = 0;
	protected int textHeight = 0;
	protected int labelX = 0;
	protected int labelY = 0;
	protected int labelWidth = 0;
	protected int labelHeight = 0;
	protected int fontHeight = 0;
	protected int minNumeric = 0;
	protected int maxNumeric = 0;
	protected int currentNumeric = 0;
	protected int numericIncrement = 0;
	protected String name = null;
	protected String label = null;
	protected Color textColor = null;
	protected Color backgroundColor = null;
	protected Color outlineColor = null;
	protected Button buttons[] = null;
	protected FontInfo fontInfo = null;
	protected GameEngine gameEngine = null;

	// define class private functions

	private void loadDefaultButtonImages()
	{
		buttons[UP_ARROW].setUncorneredImage(Button.STATE_NORMAL,
				"assets/textures/gui/upArrowButton01/normal.png");
		buttons[UP_ARROW].setUncorneredImage(Button.STATE_FOCUSED,
				"assets/textures/gui/upArrowButton01/focused.png");
		buttons[UP_ARROW].setUncorneredImage(Button.STATE_HIGHLIGHTED,
				"assets/textures/gui/upArrowButton01/highlighted.png");
		buttons[UP_ARROW].setUncorneredImage(Button.STATE_DOWN,
				"assets/textures/gui/upArrowButton01/down.png");

		buttons[DOWN_ARROW].setUncorneredImage(Button.STATE_NORMAL,
				"assets/textures/gui/downArrowButton01/normal.png");
		buttons[DOWN_ARROW].setUncorneredImage(Button.STATE_FOCUSED,
				"assets/textures/gui/downArrowButton01/focused.png");
		buttons[DOWN_ARROW].setUncorneredImage(Button.STATE_HIGHLIGHTED,
				"assets/textures/gui/downArrowButton01/highlighted.png");
		buttons[DOWN_ARROW].setUncorneredImage(Button.STATE_DOWN,
				"assets/textures/gui/downArrowButton01/down.png");

		buttons[OK].setText("ok");
		buttons[OK].setImage(Button.STATE_NORMAL,
				"assets/textures/gui/button01/normal.png");
		buttons[OK].setImage(Button.STATE_FOCUSED,
				"assets/textures/gui/button01/focused.png");
		buttons[OK].setImage(Button.STATE_HIGHLIGHTED,
				"assets/textures/gui/button01/highlighted.png");
		buttons[OK].setImage(Button.STATE_DOWN,
				"assets/textures/gui/button01/down.png");

		buttons[CANCEL].setText("cancel");
		buttons[CANCEL].setImage(Button.STATE_NORMAL,
				"assets/textures/gui/button01/normal.png");
		buttons[CANCEL].setImage(Button.STATE_FOCUSED,
				"assets/textures/gui/button01/focused.png");
		buttons[CANCEL].setImage(Button.STATE_HIGHLIGHTED,
				"assets/textures/gui/button01/highlighted.png");
		buttons[CANCEL].setImage(Button.STATE_DOWN,
				"assets/textures/gui/button01/down.png");
	}

	private void setupVisibleArea()
	{
		int ii = 0;
		int localX = 0;
		int localY = 0;
		int localWidth = 0;
		int localHeight = 0;

		// determine label configuration

		if(label == null) {
			labelX = x;
			labelY = (y - 2);
			labelWidth = 0;
			labelHeight = 0;
		}
		else {
			fontInfo.setString(label);
			labelWidth = fontInfo.getWidth();
			labelHeight = fontInfo.getHeight();
			labelX = (x + ((width / 2) - (labelWidth / 2)));
			labelY = (y + 2);
		}

		// determine text-box configuration

		fontInfo.setString("The Quick Brown Fox Jumped Over The Lazy Dogs.");

		fontHeight = fontInfo.getHeight();

		textX = (x + 2);
		textY = (labelY + labelHeight + 4);
		textHeight = (fontHeight + 4);
		if(textHeight > (height - 6)) {
			textHeight = (int)((double)(height - 6) / 2.0);
		}
		textWidth = (int)(((double)(width - 6) / 4.0) * 3.0);

		// determine up-arrow button location & dimensions

		localX = (textX + textWidth + 2);
		localY = (labelY + labelHeight + 4);
		localWidth = ((width - 6) - textWidth);
		localHeight = (int)((double)(textHeight - 2) / 2.0);

		buttons[UP_ARROW].setLocation(localX, localY);
		buttons[UP_ARROW].resize(localWidth, localHeight);

		// determine down-arrow button location & dimensions

		localY += (localHeight + 2);

		buttons[DOWN_ARROW].setLocation(localX, localY);
		buttons[DOWN_ARROW].resize(localWidth, localHeight);

		// determine ok button location & dimensions

		localX = textX;
		localY = (textY + textHeight + 2);
		localWidth = (int)((double)(width - 6) / 2.0);
		localHeight = textHeight;
		if((localY + localHeight) >= (y + height)) {
			localHeight = ((height - 6) - textHeight);
			if(label != null) {
				localHeight = ((localHeight - 2) - labelHeight);
			}
		}

		buttons[OK].setLocation(localX, localY);
		buttons[OK].resize(localWidth, localHeight);

		// determine cancel button location & dimensions

		localX += (localWidth + 2);

		buttons[CANCEL].setLocation(localX, localY);
		buttons[CANCEL].resize(localWidth, localHeight);
	}

	// define class public functions

	public NumericBox(String name, int x, int y, int width, int height,
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
		hasResult = false;
		isCancelled = false;
		skipProcess = false;
		textX = 0;
		textY = 0;
		textWidth = 0;
		textHeight = 0;
		labelX = 0;
		labelY = 0;
		labelWidth = 0;
		labelHeight = 0;
		fontHeight = 0;
		minNumeric = 0;
		maxNumeric = 0;
		currentNumeric = 0;
		numericIncrement = 1;
		label = null;
		textColor = DEFAULT_TEXT_COLOR;
		backgroundColor = DEFAULT_BACKGROUND_COLOR;
		outlineColor = DEFAULT_OUTLINE_COLOR;

		buttons = new Button[BUTTONS];
		for(ii = 0; ii < BUTTONS; ii++) {
			buttons[ii] = new Button(name + "Button" + (ii + 1), x, y, width,
					height, gameEngine);
		}

		fontInfo = new FontInfo(gameEngine);

		setupVisibleArea();
		loadDefaultButtonImages();
	}

	public String getClassName()
	{
		return CLASS_NAME;
	}

	public String getName()
	{
		return name;
	}

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
		setupVisibleArea();
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

		setupVisibleArea();
	}

	public void setVisibility(boolean mode)
	{
		int ii = 0;

		isVisible = mode;

		for(ii = 0; ii < BUTTONS; ii++) {
			buttons[ii].setVisibility(mode);
		}
	}

	public boolean isVisible()
	{
		return isVisible;
	}

	public void setTextColor(Color color)
	{
		textColor = color;
	}

	public void setBackgroundColor(Color color)
	{
		backgroundColor = color;
	}

	public void setOutlineColor(Color color)
	{
		outlineColor = color;
	}

	public Button getButton(int id)
	{
		if((id < 0) || (id >= BUTTONS)) {
			return null;
		}

		return buttons[id];
	}

	public void setIncrements(int increment)
	{
		numericIncrement = increment;
	}

	public void setupNumbers(int selection, int minimum, int maximum)
	{
		currentNumeric = selection;
		minNumeric = minimum;
		maxNumeric = maximum;

		reset();
	}

	public boolean hasResult()
	{
		return hasResult;
	}

	public int getResult()
	{
		return currentNumeric;
	}

	public boolean isCancelled()
	{
		return isCancelled;
	}

	public void reset()
	{
		hasResult = false;
		isCancelled = false;
	}

	public void process()
	{
		if(skipProcess) {
			skipProcess = false;
			return;
		}

		if(buttons[UP_ARROW].isDown()) {
			currentNumeric += numericIncrement;
			if(currentNumeric > maxNumeric) {
				currentNumeric = maxNumeric;
			}
		}
		else if(buttons[DOWN_ARROW].isDown()) {
			currentNumeric -= numericIncrement;
			if(currentNumeric < minNumeric) {
				currentNumeric = minNumeric;
			}
		}
	}

	public boolean hasFocus(int mouseX, int mouseY)
	{
		boolean result = false;
		int ii = 0;
		int nn = 0;

		if(!isVisible) {
			return false;
		}

		if((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
				(mouseY <= (y + height))) {
			result = true;
		}

		if(result) {
			for(ii = 0; ii < BUTTONS; ii++) {
				if(buttons[ii].hasFocus(mouseX, mouseY)) {
					for(nn = 0; nn < BUTTONS; nn++) {
						if(ii != nn) {
							buttons[nn].deFocus();
						}
					}
					break;
				}
			}
		}

		return result;
	}

	public void deFocus()
	{
		int ii = 0;

		for(ii = 0; ii < BUTTONS; ii++) {
			buttons[ii].deFocus();
		}
	}

	public boolean isClicked(int mouseX, int mouseY)
	{
		boolean result = false;
		int ii = 0;

		if(!isVisible) {
			return false;
		}

		if((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
				(mouseY <= (y + height))) {
			result = true;
		}

		if(result) {
			for(ii = 0; ii < BUTTONS; ii++) {
				if(buttons[ii].isClicked(mouseX, mouseY)) {
					switch(ii) {
						case UP_ARROW:
							currentNumeric += numericIncrement;
							if(currentNumeric > maxNumeric) {
								currentNumeric = maxNumeric;
							}
							skipProcess = true;
							break;
						case DOWN_ARROW:
							currentNumeric -= numericIncrement;
							if(currentNumeric < minNumeric) {
								currentNumeric = minNumeric;
							}
							skipProcess = true;
							break;
						case OK:
							hasResult = true;
							break;
						case CANCEL:
							isCancelled = true;
							break;
					}
				}
			}
		}

		return result;
	}

	public void keyPressed(KeyEvent event)
	{
		int ii = 0;

		for(ii = 0; ii < BUTTONS; ii++) {
			buttons[ii].keyPressed(event);
		}
	}

	public void render2D(Graphics2D render)
	{
		int ii = 0;

		try {
			if(!isVisible) {
				return;
			}

			// render box

			render.setColor(backgroundColor);
			render.fillRect(x, y, width, height);
			render.setColor(outlineColor);
			render.drawRect(x, y, width, height);

			// render label

			if(label != null) {
				render.setColor(textColor);
				render.drawString(label, labelX, (labelY + fontHeight));
			}

			// render numeric box & contents

			render.setColor(outlineColor);
			render.drawRect(textX, textY, textWidth, textHeight);
			render.setColor(textColor);
			fontInfo.setString("" + currentNumeric);
			render.drawString("" + currentNumeric,
					(((textX + textWidth) - 2) - fontInfo.getWidth()),
					(textY + fontHeight + 2));

			// render buttons

			for(ii = 0; ii < BUTTONS; ii++) {
				buttons[ii].render2D(render);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

