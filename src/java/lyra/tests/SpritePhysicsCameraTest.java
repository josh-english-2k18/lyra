/*
 * SpritePhysicsCameraTest.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A simple extension of the GameEngine to test Sprite Physics with a Camera.
 *
 * Written by Josh English.
 */

// define package space

package lyra.tests;

// import external packages

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Image;

// import internal packages

import lyra.*;
import lyra.drivers.KeyboardDriver;
import lyra.drivers.MouseDriver;

// define class

public class SpritePhysicsCameraTest extends GameEngine
{
	// define private class constants

	private static final String CLASS_NAME =
			SpritePhysicsCameraTest.class.getName();

	private static final int KEY_UP = 1;
	private static final int KEY_DOWN = 3;
	private static final int KEY_LEFT = 0;
	private static final int KEY_RIGHT = 2;

	// define private class variables

	int selection = 0;
	int defaultWidth = 0;
	int defaultHeight = 0;
	Sprite sprite = null;
	SpritePhysics physics = null;
	Camera camera = null;
	Image background = null;

	// graphics rendering functions

	public synchronized void pipeline()
	{
		if(!isGameLoaded) {
			return;
		}

		drawImage(background, (0 - camera.getCameraX()),
				(0 - camera.getCameraY()), 1024, 1024);

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
		drawString("Camera{" + camera.getName() + "}@(" + camera.getCameraX() +
				", " + camera.getCameraY() + ")", 0, 60);
	}

	// event handling

	public synchronized void handleMouseEvents(int type, MouseEvent event)
	{
		if(!isGameLoaded) {
			return;
		}

		camera.processMouseEvent(camera.MODE_INVERTED, type, mouseX, mouseY,
				event);
	}

	public synchronized void handleKeyboardEvents(KeyEvent event)
	{
		if(!isGameLoaded) {
			return;
		}

		if(keyboardEventType == KeyboardDriver.EVENT_KEY_PRESSED) {
			if(event.getKeyChar() == 't') {
				physics.setTurboMode(true);
			}
		}
		else if(keyboardEventType == KeyboardDriver.EVENT_KEY_RELEASED) {
			if(event.getKeyChar() == 't') {
				physics.setTurboMode(false);
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

		sprite.setRotateTimeSeconds(0.0625);
		physics = new SpritePhysics(sprite);

		camera = new Camera("SpritePhysicsCamera", 0, 0, windowWidth,
				windowHeight);

		background = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
				"assets/textures/test/background.png");

		isGameLoaded = true;
	}

	public synchronized void processGameplay()
	{
		int direction = 0;

		if(!isGameLoaded) {
			return;
		}

		if(keyboard.isKeyDown(KEY_UP)) {
			physics.processPhysics(SpritePhysics.MODE_FORWARD);
		}
		if(keyboard.isKeyDown(KEY_DOWN)) {
			physics.processPhysics(SpritePhysics.MODE_REVERSE);
		}
		if(keyboard.isKeyDown(KEY_LEFT)) {
			direction = sprite.getDirection();
			direction -= 1;
			if(direction < 0) {
				direction = (Sprite.DIRECTIONS - 1);
			}
			sprite.changeDirection(direction, Sprite.COUNTER_CLOCKWISE,
					frameRate.get());
		}
		if(keyboard.isKeyDown(KEY_RIGHT)) {
			direction = sprite.getDirection();
			direction += 1;
			if(direction >= Sprite.DIRECTIONS) {
				direction = 0;
			}
			sprite.changeDirection(direction, Sprite.CLOCKWISE,
					frameRate.get());
		}

		physics.processGameplay();
		camera.applyCameraDiff(sprite.getCameraDiffX(),
				sprite.getCameraDiffY());
		sprite.applyCamera(camera.getCameraX(), camera.getCameraY(),
				camera.getWindowWidth(), camera.getWindowHeight());
		sprite.processGameplay(frameRate.get());
	}
}

