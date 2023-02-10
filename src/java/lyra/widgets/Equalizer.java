/*
 * Equalizer.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A GUI widget for handling equalizers.
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

public class Equalizer implements Widget
{
	// define private class constants

	private static final String CLASS_NAME = Equalizer.class.getName();

	// define protected class variables

	protected boolean isVisible = false;
	protected boolean highlightBarFocus = false;
	protected boolean barFocus[] = null;
	protected boolean barClick[] = null;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected int minValue = 0;
	protected int maxValue = 0;
	protected int barWidth = 0;
	protected int barHeight = 0;
	protected int barIncrements = 0;
	protected int bars[] = null;
	protected String name = null;
	protected Color backgroundColor = null;
	protected GameEngine gameEngine = null;

	// define class private functions

	private void calculateBarSize()
	{
		barWidth = ((width / bars.length) - 4);
		barHeight = ((height / barIncrements) - 2);
	}

	// define class public functions

	public Equalizer(String name, int x, int y, int width, int height,
			GameEngine gameEngine)
	{
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.gameEngine = gameEngine;

		isVisible = true;
		highlightBarFocus = false;
		barFocus = null;
		barClick = null;
		minValue = 0;
		maxValue = 0;
		barIncrements = 0;
		bars = null;
		backgroundColor = Color.black;
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

	public void setHighlightBarFocus(boolean mode)
	{
		highlightBarFocus = mode;
	}

	public boolean getHighlightBarFocus()
	{
		return highlightBarFocus;
	}

	public void setBackgroundColor(Color color)
	{
		backgroundColor = color;
	}

	public void setupEqualizer(int minValue, int maxValue, int increments,
			int bars)
	{
		if((minValue >= maxValue) || (bars < 1)) {
			return;
		}

		this.minValue = minValue;
		this.maxValue = maxValue;
		this.barIncrements = increments;
		this.barFocus = new boolean[bars];
		this.barClick = new boolean[bars];
		this.bars = new int[bars];

		calculateBarSize();
	}

	public void setBarValue(int bar, int value)
	{
		if((bar < 0) || (bar >= bars.length) || (value < minValue) ||
				(value > maxValue)) {
			return;
		}

		bars[bar] = value;
	}

	public boolean hasFocus(int mouseX, int mouseY)
	{
		boolean result = false;
		int ii = 0;
		int localX = 0;
		int localY = 0;
		int localHeight = 0;

		if(!isVisible) {
			return false;
		}

		for(ii = 0; ii < bars.length; ii++) {
			barFocus[ii] = false;
		}

		if((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
				(mouseY <= (y + height))) {
			result = true;
		}

		if(result) {
			localHeight = (height - 4);
			for(ii = 0; ii < bars.length; ii++) {
				localX = (x + (ii * (barWidth)) + (ii * 4) + 2);
				localY = (y + 2);
				if((mouseX >= localX) && (mouseX <= (localX + barWidth)) &&
						(mouseY >= localY) &&
						(mouseY <= (localY + localHeight))) {
					barFocus[ii] = true;
				}
			}
		}

		return result;
	}

	public boolean hasBarFocus()
	{
		boolean result = false;
		int ii = 0;

		for(ii = 0; ii < bars.length; ii++) {
			if(barFocus[ii]) {
				result = true;
				break;
			}
		}

		return result;
	}

	public int getBarFocus()
	{
		int result = -1;
		int ii = 0;

		for(ii = 0; ii < bars.length; ii++) {
			if(barFocus[ii]) {
				result = ii;
				break;
			}
		}

		return result;
	}

	public void deFocus()
	{
		int ii = 0;

		for(ii = 0; ii < bars.length; ii++) {
			barFocus[ii] = false;
			barClick[ii] = false;
		}
	}

	public boolean isClicked(int mouseX, int mouseY)
	{
		boolean result = false;
		int ii = 0;
		int localX = 0;
		int localY = 0;
		int localHeight = 0;

		if(!isVisible) {
			return false;
		}

		for(ii = 0; ii < bars.length; ii++) {
			barClick[ii] = false;
		}

		if((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
				(mouseY <= (y + height))) {
			result = true;
		}

		if(result) {
			localHeight = (height - 4);
			for(ii = 0; ii < bars.length; ii++) {
				localX = (x + (ii * (barWidth)) + (ii * 4) + 2);
				localY = (y + 2);
				if((mouseX >= localX) && (mouseX <= (localX + barWidth)) &&
						(mouseY >= localY) &&
						(mouseY <= (localY + localHeight))) {
					barClick[ii] = true;
				}
			}
		}

		return result;
	}

	public boolean hasBarClick()
	{
		boolean result = false;
		int ii = 0;

		for(ii = 0; ii < bars.length; ii++) {
			if(barClick[ii]) {
				result = true;
				break;
			}
		}

		return result;
	}

	public int getBarClick()
	{
		int result = -1;
		int ii = 0;

		for(ii = 0; ii < bars.length; ii++) {
			if(barClick[ii]) {
				result = ii;
				break;
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
		int ii = 0;
		int nn = 0;
		int ref = 0;
		int localX = 0;
		int localY = 0;
		int localHeight = 0;
		int increments = 0;
		double percent = 0.0;

		try {
			if(!isVisible) {
				return;
			}

			// check sizes

			if(barWidth < 1) {
				return;
			}
			if(barHeight < 1) {
				return;
			}

			// setup render space

			render.setColor(backgroundColor);
			render.fillRect(x, y, width, height);

			// render equalizer bars

			for(ii = 0; ii < bars.length; ii++) {
				percent = (((double)(bars[ii] - minValue) /
							(double)(maxValue - minValue)) * 100.0);
				increments = ((int)((double)barIncrements * (percent / 100.0)) +
						1);
				localX = (x + (ii * (barWidth)) + (ii * 4) + 2);
				localY = ((y + height) - 2);
				for(nn = 0; nn < increments; nn++) {
					if(nn < (int)((double)barIncrements * 0.25)) {
						render.setColor(Color.blue);
					}
					else if(nn < (int)((double)barIncrements * 0.50)) {
						render.setColor(Color.green);
					}
					else if(nn < (int)((double)barIncrements * 0.75)) {
						render.setColor(Color.yellow);
					}
					else {
						render.setColor(Color.red);
					}

					render.fillRect(localX, (localY - barHeight), barWidth,
							barHeight);

					localY -= (barHeight + 2);
				}
			}

			// hight focused bar

			if((highlightBarFocus) && (hasBarFocus())) {
				ref = getBarFocus();
				localX = (x + (ref * (barWidth)) + (ref * 4) + 2);
				localY = (y + 2);
				localHeight = (height - 4);

				render.setColor(Color.blue);
				render.drawRect(localX, localY, barWidth, localHeight);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

