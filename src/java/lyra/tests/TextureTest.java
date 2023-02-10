/*
 * TextureTest.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A simple extension of the GameEngine to test the Texture system.
 *
 * Written by Josh English.
 */

// define package space

package lyra.tests;

// import external packages

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.Color;

// import internal packages

import lyra.*;
import lyra.drivers.KeyboardDriver;
import lyra.drivers.MouseDriver;

// define class

public class TextureTest extends GameEngine
{
	// define private class constants

	private static final String CLASS_NAME = TextureTest.class.getName();
	private static final int MIP_MAPS = 7;

	// define private class variables

	int selection = 0;
	int defaultWidth = 0;
	int defaultHeight = 0;
	Texture texture = null;

	// graphics rendering functions

	public synchronized void pipeline()
	{
		if(!isGameLoaded) {
			return;
		}

		texture.render2D(render2D);

		setColor(Color.white);
		drawString("ENGINE render (" + windowWidth + " x " +
				windowHeight + "), framerate: " + frameRate.get(),
				0, 12);
		drawString("ENGINE mouse (" + mouseX + ", " + mouseY + 
				"): button: " + mouseButton + ", clicks: " +
				mouseClicks, 0, 28);
		drawString("ENGINE keyboard (" + lastBinaryKeyPress +
				"), type: " + keyboardEventType + ", '" +
				keyPressBuffer + "'", 0, 44);
	}

	// event handling

	public synchronized void handleMouseEvents(int type, MouseEvent event)
	{
		// note: does nothing
	}

	public synchronized void handleKeyboardEvents(KeyEvent event)
	{
		if(!isGameLoaded) {
			return;
		}

		if(keyboardEventType == KeyboardDriver.EVENT_KEY_PRESSED) {
			if(lastBinaryKeyPress == 38) { // up
				selection++;
				if(selection > (MIP_MAPS - 1)) {
					selection = 0;
				}
			}
			else  if(lastBinaryKeyPress == 40) { // down
				selection--;
				if(selection < 0) {
					selection = (MIP_MAPS - 1);
				}
			}
		}
	}

	// game logic

	public synchronized void loadGame()
	{
		int ii = 0;
		int width = 0;
		int height = 0;

		texture = new Texture("assets/textures/test/milla_jovovich01.png",
				this);
		texture.setLocation(64, 64);
		texture.setDebugMode(true);

		defaultWidth = texture.getWidth();
		defaultHeight = texture.getHeight();

		width = ((defaultWidth * 2) * 2);
		height = ((defaultHeight * 2) * 2);
		for(ii = 0; ii < MIP_MAPS; ii++) {
			if((width == texture.getWidth()) &&
					(height == texture.getHeight())) {
				selection = ii;
			}
			texture.buildMipMap(width, height);
			width /= 2;
			height /= 2;
		}

		isGameLoaded = true;
	}

	public synchronized void processGameplay()
	{
		int ii = 0;
		int width = 0;
		int height = 0;

		if(!isGameLoaded) {
			return;
		}

		width = ((defaultWidth * 2) * 2);
		height = ((defaultHeight * 2) * 2);
		for(ii = 0; ii < MIP_MAPS; ii++) {
			if(ii == selection) {
				texture.setMipMapAsDefault(width, height);
				break;
			}
			width /= 2;
			height /= 2;
		}
	}
}

