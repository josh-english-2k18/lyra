/*
 * SideScrollerTest.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A simple extension of the GameEngine to test the Scene system performing
 * side-scrolling gameplay.
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
import lyra.util.Config;

// define class

public class SideScrollerTest extends GameEngine
{
	// define private class constants

	private static final String CLASS_NAME = SideScrollerTest.class.getName();

	private static final int TILE_WIDTH = 128;
	private static final int TILE_HEIGHT = 128;

	private static final int KEY_LEFT = 0;
	private static final int KEY_RIGHT = 2;
	private static final int KEY_JUMP = KeyboardDriver.charToInteger(' ');
	private static final int KEY_FIRE = 3;

	// define private class variables

	private int selection = 0;
	private int defaultWidth = 0;
	private int defaultHeight = 0;
	private int ballCount = 0;
	private Sprite sprite = null;
	private Sprite ball = null;
	private SpritePhysics physics = null;
	private SpritePhysics ballPhysics = null;
	private Scene scene = null;

	// graphics rendering functions

	public synchronized void pipeline()
	{
		if(!isGameLoaded) {
			setColor(Color.white);
			drawString("Loading...", 0, 12);
			return;
		}

		scene.render2D(render2D);

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
		drawString("Scene(" + scene.getCamera().getCameraX() + ", " +
				scene.getCamera().getCameraY() + ") friction: " +
				physics.getFriction() + ", sliding: " + physics.isSliding(),
				0, 60);
	}

	// event handling

	public synchronized void handleMouseEvents(int type, MouseEvent event)
	{
		if(!isGameLoaded) {
			return;
		}

		scene.processMouseEvent(type, mouseX, mouseY, event);
	}

	public synchronized void handleKeyboardEvents(KeyEvent event)
	{
		int ii = 0;
		int direction = 0;

		if(!isGameLoaded) {
			return;
		}

		if(keyboardEventType == KeyboardDriver.EVENT_KEY_RELEASED) {
			if(event.getKeyChar() == ' ') { // jump (space bar)
				direction = sprite.getDirection();
				sprite.resetDirection(Sprite.DIRECTION_NORTH);
				physics.simulatePhysics(SpritePhysics.MODE_FORWARD, false, 320);
				scene.getCamera().applyCameraDiff(sprite.getCameraDiffX(),
						sprite.getCameraDiffY());
				sprite.resetDirection(direction);
			}
			else if(lastBinaryKeyPress == 40) { // fire (down arrow)
				ball = new Sprite("FireBall#" + ballCount,
						((sprite.getX() + (sprite.getWidth() / 2)) - 6),
						((sprite.getY() + (sprite.getHeight() / 2)) - 6),
						12, 12, this);
				ball.resetDirection(sprite.getDirection());
				ball.setImageTexture(ball.getDirection(),
						"assets/textures/test/ball.png");
				ball.setOffsets(sprite.getOffsetX(), sprite.getOffsetY());
				ballPhysics = new SpritePhysics(ball);
				ballPhysics.setFrictionMode(false);
				ballPhysics.setFrictionModifier(0.064);
				ballPhysics.setGravityMode(true);
				ballPhysics.setGravityDirection(Sprite.DIRECTION_SOUTH);
				ballPhysics.setWillBounce(true);
				ballPhysics.setLocation(ball.getX(), ball.getY());
				ballPhysics.setPhysics(physics.getCurrentThrust(),
						physics.getXSpeed(), physics.getYSpeed());
				ballPhysics.simulatePhysics(SpritePhysics.MODE_FORWARD, false,
						256);
				scene.commitSprite(ball, ball.getX(), ball.getY());
				scene.commitSpritePhysics(ball.getName(), ballPhysics);
				ballCount++;
			}
		}
	}

	// game logic

	public synchronized void loadGame()
	{
		int x = 0;
		int y = 0;

		Texture texture = null;
		Tile tile = null;

		sprite = new Sprite("Sprite Test", 0, 0, 96, 96, this);

		sprite.setImageTexture(Sprite.DIRECTION_EAST,
				"assets/textures/test/dragonRight.png");
		sprite.setImageTexture(Sprite.DIRECTION_WEST,
				"assets/textures/test/dragonLeft.png");

		sprite.setRotateTimeSeconds(0.0625);
		sprite.setOutline(true);
		sprite.setSelectable(true);

		physics = new SpritePhysics(sprite);
		physics.setGravityMode(true);
		physics.setGravityDirection(Sprite.DIRECTION_SOUTH);

		scene = new Scene("TestScene", 0, 0, windowWidth, windowHeight, this);
		scene.commitSprite(sprite,
				((windowWidth / 2) - (sprite.getWidth() / 2)),
				((windowHeight / 2) - (sprite.getHeight() / 2)));
		scene.commitSpritePhysics(sprite.getName(), physics);

		texture = (Texture)assetCache.getAsset(AssetCache.TYPE_TEXTURE,
				"assets/textures/test/tile_on.png");
		texture.buildMipMap(TILE_WIDTH, TILE_HEIGHT);
		texture = (Texture)assetCache.getAsset(AssetCache.TYPE_TEXTURE,
				"assets/textures/test/tile_slow.png");
		texture.buildMipMap(TILE_WIDTH, TILE_HEIGHT);
		texture = (Texture)assetCache.getAsset(AssetCache.TYPE_TEXTURE,
				"assets/textures/test/tile_off.png");
		texture.buildMipMap(TILE_WIDTH, TILE_HEIGHT);

		for(x = -2048; x < 4096; x += TILE_WIDTH) {
			for(y = 0; y < 1024; y += TILE_HEIGHT) {
				tile = new Tile("SceneTile(" + x + ", " + y + ")", x, y,
						TILE_WIDTH, TILE_HEIGHT, this);
				if(y < 768) {
					tile.setFriction(0.65);
					tile.setCanCollide(false);
					tile.setImageTexture("assets/textures/test/tile_on.png");
				}
				else {
					tile.setFriction(0.65);
					tile.setCanCollide(true);
					tile.setCanIntersect(false);
					tile.setImageTexture("assets/textures/test/tile_off.png");
				}
				scene.commitTile(tile, x, y);
			}
		}

		isGameLoaded = true;
	}

	public synchronized void processGameplay()
	{
		int ref = 0;
		int direction = 0;

		Tile tile = null;

		if(!isGameLoaded) {
			return;
		}

		if(keyboard.isKeyDown(KEY_LEFT)) {
			if(sprite.getDirection() != Sprite.DIRECTION_WEST) {
				sprite.resetDirection(Sprite.DIRECTION_WEST);
			}
			else {
				physics.processPhysics(SpritePhysics.MODE_FORWARD);
				scene.getCamera().applyCameraDiff(sprite.getCameraDiffX(),
						sprite.getCameraDiffY());
			}
		}
		if(keyboard.isKeyDown(KEY_RIGHT)) {
			if(sprite.getDirection() != Sprite.DIRECTION_EAST) {
				sprite.resetDirection(Sprite.DIRECTION_EAST);
			}
			else {
				physics.processPhysics(SpritePhysics.MODE_FORWARD);
				scene.getCamera().applyCameraDiff(sprite.getCameraDiffX(),
						sprite.getCameraDiffY());
			}
		}

		scene.processGameplay(frameRate.get());
		scene.processSpriteCollisions(false, 0, -1, 512);
	}
}

