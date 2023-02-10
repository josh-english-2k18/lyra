/*
 * FontInfo.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A GUI widget component for obtain information about font metrics.
 *
 * Written by Josh English.
 */

// define package space

package lyra.widgets;

// import external packages

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Rectangle2D;

// import internal packages

import lyra.GameEngine;

// define class

public class FontInfo
{
	// define private class constants

	private static final String CLASS_NAME = FontInfo.class.getName();

	// define protected class variables

	protected double stringWidth = 0;
	protected double stringHeight = 0;
	protected String string = null;
	protected Font font = null;
	protected FontMetrics fontMetrics = null;
	protected GameEngine gameEngine = null;

	// define class private functions

	private void calculateFontCoords()
	{
		Rectangle2D rectangle = null;

		if(string == null) {
			string = new String("");
		}

		rectangle = fontMetrics.getStringBounds(string,
				gameEngine.getGraphics());
		stringWidth = rectangle.getWidth();
		stringHeight = rectangle.getHeight();
	}

	// define class public functions

	public FontInfo(GameEngine gameEngine, Font font, String string)
	{
		this.gameEngine = gameEngine;
		this.font = font;
		this.string = string;
		fontMetrics = gameEngine.getFontMetrics(font);
		calculateFontCoords();
	}

	public FontInfo(GameEngine gameEngine, String string)
	{
		this.gameEngine = gameEngine;
		this.font = gameEngine.getFont();
		this.string = string;
		fontMetrics = gameEngine.getFontMetrics(font);
		calculateFontCoords();
	}

	public FontInfo(GameEngine gameEngine)
	{
		this.gameEngine = gameEngine;
		this.font = gameEngine.getFont();
		this.string = null;
		fontMetrics = gameEngine.getFontMetrics(font);
	}

	public String getClassName() 
	{
		return CLASS_NAME;
	}

	public String getName()
	{
		String result = null;

		result = new String("{" + CLASS_NAME + "}[" + font.toString() + "](" +
				stringWidth + "x" + stringHeight + ")->'" + string + "'");

		return result;
	}

	public void setString(String value)
	{
		this.string = value;
		calculateFontCoords();
	}

	public int getWidth(String value)
	{
		this.string = value;
		calculateFontCoords();
		return (int)stringWidth;
	}

	public int getWidth()
	{
		return (int)stringWidth;
	}

	public double getPrecisionWidth()
	{
		return stringWidth;
	}

	public int getHeight()
	{
		return (int)stringHeight;
	}

	public double getPrecisionHeight()
	{
		return stringHeight;
	}

	public int getCharWidth(char value)
	{
		int result = 0;

		result = fontMetrics.charWidth(value);

		return result;
	}
}

