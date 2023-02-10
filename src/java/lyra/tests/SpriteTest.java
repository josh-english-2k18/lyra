/*
 * SpriteTest.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A simple extension of the GameEngine to test type Sprite system.
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

public class SpriteTest extends GameEngine
{
	// define private class constants

	private static final String CLASS_NAME = SpriteTest.class.getName();

	// define private class variables

	int selection = 0;
	int defaultWidth = 0;
	int defaultHeight = 0;
	Sprite sprite = null;

	// graphics rendering functions

	public synchronized void pipeline()
	{
		if(!isGameLoaded) {
			return;
		}

		sprite.render2D(render2D);

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
		int direction = 0;

		if(!isGameLoaded) {
			return;
		}

		if(keyboardEventType == KeyboardDriver.EVENT_KEY_PRESSED) {
			if(lastBinaryKeyPress == 37) { // left
				direction = sprite.getDirection();
				direction -= 1;
				if(direction < 0) {
					direction = (Sprite.DIRECTIONS - 1);
				}
				sprite.changeDirection(direction, Sprite.COUNTER_CLOCKWISE,
						frameRate.get());
			}
			else  if(lastBinaryKeyPress == 39) { // right
				direction = sprite.getDirection();
				direction += 1;
				if(direction >= Sprite.DIRECTIONS) {
					direction = 0;
				}
				sprite.changeDirection(direction, Sprite.CLOCKWISE,
						frameRate.get());
			}
		}
	}

	// game logic

	public synchronized void loadGame()
	{
		sprite = new Sprite("Sprite Test", 128, 128, 256, 256, this);
		sprite.setDebugMode(true);

		sprite.setImageTexture(Sprite.DIRECTION_NORTH,
				"assets/textures/test/sprite_north.png");
		sprite.setImageTexture(Sprite.DIRECTION_NORTH_NORTH_EAST,
				"assets/textures/test/sprite_north_north_east.png");
		sprite.setImageTexture(Sprite.DIRECTION_NORTH_EAST,
				"assets/textures/test/sprite_north_east.png");
		sprite.setImageTexture(Sprite.DIRECTION_NORTH_EAST_NORTH,
				"assets/textures/test/sprite_north_east_north.png");
		sprite.setImageTexture(Sprite.DIRECTION_EAST,
				"assets/textures/test/sprite_east.png");
		sprite.setImageTexture(Sprite.DIRECTION_SOUTH_EAST_SOUTH,
				"assets/textures/test/sprite_south_east_south.png");
		sprite.setImageTexture(Sprite.DIRECTION_SOUTH_EAST,
				"assets/textures/test/sprite_south_east.png");
		sprite.setImageTexture(Sprite.DIRECTION_SOUTH_SOUTH_EAST,
				"assets/textures/test/sprite_south_south_east.png");
		sprite.setImageTexture(Sprite.DIRECTION_SOUTH,
				"assets/textures/test/sprite_south.png");
		sprite.setImageTexture(Sprite.DIRECTION_SOUTH_SOUTH_WEST,
				"assets/textures/test/sprite_south_south_west.png");
		sprite.setImageTexture(Sprite.DIRECTION_SOUTH_WEST,
				"assets/textures/test/sprite_south_west.png");
		sprite.setImageTexture(Sprite.DIRECTION_SOUTH_WEST_SOUTH,
				"assets/textures/test/sprite_south_west_south.png");
		sprite.setImageTexture(Sprite.DIRECTION_WEST,
				"assets/textures/test/sprite_west.png");
		sprite.setImageTexture(Sprite.DIRECTION_NORTH_WEST_NORTH,
				"assets/textures/test/sprite_north_west_north.png");
		sprite.setImageTexture(Sprite.DIRECTION_NORTH_WEST,
				"assets/textures/test/sprite_north_west.png");
		sprite.setImageTexture(Sprite.DIRECTION_NORTH_NORTH_WEST,
				"assets/textures/test/sprite_north_north_west.png");

		isGameLoaded = true;
	}

	public synchronized void processGameplay()
	{
		if(!isGameLoaded) {
			return;
		}

		sprite.processGameplay(frameRate.get());
	}
}

