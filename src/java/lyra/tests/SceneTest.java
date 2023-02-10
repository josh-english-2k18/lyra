/*
 * SceneTest.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A simple extension of the GameEngine to test the Scene system.
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
import lyra.audio.AudioPlayer;

// define class

public class SceneTest extends GameEngine
{
	// define private class constants

	private static final String CLASS_NAME = SceneTest.class.getName();

	private static final int KEY_UP = 1;
	private static final int KEY_DOWN = 3;
	private static final int KEY_LEFT = 0;
	private static final int KEY_RIGHT = 2;

	// define private class variables

	boolean ballAnimationTrigger = false;
	int selection = 0;
	int defaultWidth = 0;
	int defaultHeight = 0;
	Sprite sprite = null;
	Sprite cannon = null;
	Sprite ball = null;
	SpritePhysics physics = null;
	SpritePhysics ballPhysics = null;
	Animation animation = null;
	Animation ballAnimation = null;
	AudioPlayer player = null;
	Scene scene = null;

	// graphics rendering functions

	public synchronized void pipeline()
	{
		if(!isGameLoaded) {
			return;
		}

		scene.render2D(render2D);
		cannon.render2D(render2D);
		animation.render2D(render2D);

		if(ball != null) {
			ball.render2D(render2D);
			ballAnimation.render2D(render2D);
		}

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
		drawString("Scene allocated memory: " +
				java.lang.Runtime.getRuntime().freeMemory(), 0, 76);
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

		if(keyboardEventType == KeyboardDriver.EVENT_KEY_PRESSED) {
			if(event.getKeyChar() == 't') {
				physics.setTurboMode(true);
			}
			else if(event.getKeyChar() == 'a') {
				direction = cannon.getDirection();
				direction -= 1;
				if(direction < 0) {
					direction = (Sprite.DIRECTIONS - 1);
				}
				cannon.changeDirection(direction, Sprite.COUNTER_CLOCKWISE,
						frameRate.get());
			}
			else if(event.getKeyChar() == 'd') {
				direction = cannon.getDirection();
				direction += 1;
				if(direction >= Sprite.DIRECTIONS) {
					direction = 0;
				}
				cannon.changeDirection(direction, Sprite.CLOCKWISE,
						frameRate.get());
			}
			else if(event.getKeyChar() == ' ') {
				ball = new Sprite("Ball Test",
						(sprite.getX() + (sprite.getWidth() / 2)),
						(sprite.getY() + (sprite.getHeight() / 2)),
						8, 8, this);
				ball.setDebugMode(true);
				ball.resetDirection(cannon.getDirection());
				ball.setImageTexture(ball.getDirection(),
						"assets/textures/test/ball.png");
				ball.setOffsets(sprite.getOffsetX(), sprite.getOffsetY());
				ballPhysics = new SpritePhysics(ball);
				ballPhysics.setLocation(ball.getX(), ball.getY());
				ballPhysics.setPhysics(physics.getCurrentThrust(),
						physics.getXSpeed(), physics.getYSpeed());
				ballPhysics.updatePhysicsOnCurrentThrust(32);
				ballPhysics.simulatePhysics(SpritePhysics.MODE_FORWARD, false,
						256);
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
		int x = 0;
		int y = 0;
		int width = 128;
		int height = 128;

		Config config = null;
		Texture texture = null;
		Tile tile = null;

		sprite = new Sprite("Sprite Test", 128, 128, 144, 144, this);
//		sprite.setDebugMode(true);

		sprite.setImageTexture(Sprite.DIRECTION_NORTH,
				"assets/textures/raw/sprites/sandrail_angle_01.png");
		sprite.setImageTexture(Sprite.DIRECTION_NORTH_NORTH_EAST,
				"assets/textures/raw/sprites/sandrail_angle_02.png");
		sprite.setImageTexture(Sprite.DIRECTION_NORTH_EAST,
				"assets/textures/raw/sprites/sandrail_angle_03.png");
		sprite.setImageTexture(Sprite.DIRECTION_NORTH_EAST_NORTH,
				"assets/textures/raw/sprites/sandrail_angle_04.png");
		sprite.setImageTexture(Sprite.DIRECTION_EAST,
				"assets/textures/raw/sprites/sandrail_angle_05.png");
		sprite.setImageTexture(Sprite.DIRECTION_SOUTH_EAST_SOUTH,
				"assets/textures/raw/sprites/sandrail_angle_06.png");
		sprite.setImageTexture(Sprite.DIRECTION_SOUTH_EAST,
				"assets/textures/raw/sprites/sandrail_angle_07.png");
		sprite.setImageTexture(Sprite.DIRECTION_SOUTH_SOUTH_EAST,
				"assets/textures/raw/sprites/sandrail_angle_08.png");
		sprite.setImageTexture(Sprite.DIRECTION_SOUTH,
				"assets/textures/raw/sprites/sandrail_angle_09.png");
		sprite.setImageTexture(Sprite.DIRECTION_SOUTH_SOUTH_WEST,
				"assets/textures/raw/sprites/sandrail_angle_10.png");
		sprite.setImageTexture(Sprite.DIRECTION_SOUTH_WEST,
				"assets/textures/raw/sprites/sandrail_angle_11.png");
		sprite.setImageTexture(Sprite.DIRECTION_SOUTH_WEST_SOUTH,
				"assets/textures/raw/sprites/sandrail_angle_12.png");
		sprite.setImageTexture(Sprite.DIRECTION_WEST,
				"assets/textures/raw/sprites/sandrail_angle_13.png");
		sprite.setImageTexture(Sprite.DIRECTION_NORTH_WEST_NORTH,
				"assets/textures/raw/sprites/sandrail_angle_14.png");
		sprite.setImageTexture(Sprite.DIRECTION_NORTH_WEST,
				"assets/textures/raw/sprites/sandrail_angle_15.png");
		sprite.setImageTexture(Sprite.DIRECTION_NORTH_NORTH_WEST,
				"assets/textures/raw/sprites/sandrail_angle_16.png");

		sprite.setRotateTimeSeconds(0.0625);
		sprite.setOutline(true);
		sprite.setSelectable(true);

		cannon = new Sprite("Cannon Test", 128, 128, 144, 144, this);
		cannon.setImageTexture(Sprite.DIRECTION_NORTH,
				"assets/textures/test/sprite_cannon_north.png");
		cannon.setImageTexture(Sprite.DIRECTION_NORTH_NORTH_EAST,
				"assets/textures/test/sprite_cannon_north_north_east.png");
		cannon.setImageTexture(Sprite.DIRECTION_NORTH_EAST,
				"assets/textures/test/sprite_cannon_north_east.png");
		cannon.setImageTexture(Sprite.DIRECTION_NORTH_EAST_NORTH,
				"assets/textures/test/sprite_cannon_north_east_north.png");
		cannon.setImageTexture(Sprite.DIRECTION_EAST,
				"assets/textures/test/sprite_cannon_east.png");
		cannon.setImageTexture(Sprite.DIRECTION_SOUTH_EAST_SOUTH,
				"assets/textures/test/sprite_cannon_south_east_south.png");
		cannon.setImageTexture(Sprite.DIRECTION_SOUTH_EAST,
				"assets/textures/test/sprite_cannon_south_east.png");
		cannon.setImageTexture(Sprite.DIRECTION_SOUTH_SOUTH_EAST,
				"assets/textures/test/sprite_cannon_south_south_east.png");
		cannon.setImageTexture(Sprite.DIRECTION_SOUTH,
				"assets/textures/test/sprite_cannon_south.png");
		cannon.setImageTexture(Sprite.DIRECTION_SOUTH_SOUTH_WEST,
				"assets/textures/test/sprite_cannon_south_south_west.png");
		cannon.setImageTexture(Sprite.DIRECTION_SOUTH_WEST,
				"assets/textures/test/sprite_cannon_south_west.png");
		cannon.setImageTexture(Sprite.DIRECTION_SOUTH_WEST_SOUTH,
				"assets/textures/test/sprite_cannon_south_west_south.png");
		cannon.setImageTexture(Sprite.DIRECTION_WEST,
				"assets/textures/test/sprite_cannon_west.png");
		cannon.setImageTexture(Sprite.DIRECTION_NORTH_WEST_NORTH,
				"assets/textures/test/sprite_cannon_north_west_north.png");
		cannon.setImageTexture(Sprite.DIRECTION_NORTH_WEST,
				"assets/textures/test/sprite_cannon_north_west.png");
		cannon.setImageTexture(Sprite.DIRECTION_NORTH_NORTH_WEST,
				"assets/textures/test/sprite_cannon_north_north_west.png");

		physics = new SpritePhysics(sprite);

		animation = new Animation("Animation Test", 128, 128, 128, 128, this);
		animation.setAnimationTime(100);

		animation.addImageTexture(
				"assets/animations/explosion01/explosion_01.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_02.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_03.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_04.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_05.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_06.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_07.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_08.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_09.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_10.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_11.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_12.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_13.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_14.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_15.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_16.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_17.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_18.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_19.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_20.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_21.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_22.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_23.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_24.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_25.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_26.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_27.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_28.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_29.png");
		animation.addImageTexture(
				"assets/animations/explosion01/explosion_30.png");

		ballAnimation = new Animation("Ball Animation", 128, 128, 32, 32, this);
		ballAnimation.setAnimationTime(100);

		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_01.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_02.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_03.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_04.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_05.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_06.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_07.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_08.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_09.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_10.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_11.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_12.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_13.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_14.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_15.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_16.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_17.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_18.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_19.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_20.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_21.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_22.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_23.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_24.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_25.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_26.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_27.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_28.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_29.png");
		ballAnimation.addImageTexture(
				"assets/animations/explosion01/explosion_30.png");

		config = (Config)assetCache.getAsset(AssetCache.TYPE_CONFIG,
				"assets/config/audio/effects/fireball01.mp3.config");
		player = new AudioPlayer(config, this);

		scene = new Scene("TestScene", 0, 0, windowWidth, windowHeight, this);
		scene.setDebugMode(true);
		scene.commitSprite(sprite,
				((windowWidth / 2) - (sprite.getWidth() / 2)),
				((windowHeight / 2) - (sprite.getHeight() / 2)));
		scene.commitSpritePhysics(sprite.getName(), physics);

		texture = (Texture)assetCache.getAsset(AssetCache.TYPE_TEXTURE,
				"assets/textures/test/tile_on.png");
		texture.buildMipMap(128, 128);
		texture = (Texture)assetCache.getAsset(AssetCache.TYPE_TEXTURE,
				"assets/textures/test/tile_slow.png");
		texture.buildMipMap(128, 128);
		texture = (Texture)assetCache.getAsset(AssetCache.TYPE_TEXTURE,
				"assets/textures/test/tile_off.png");
		texture.buildMipMap(128, 128);

		for(x = 0; x < 2048; x += width) {
			for(y = 0; y < 2048; y += height) {
				tile = new Tile("SceneTile(" + x + ", " + y + ")", x, y,
						width, height, this);
				tile.setFriction(0.80);
				tile.setCanCollide(false);
				tile.setImageTexture("assets/textures/test/tile_on.png");
				scene.commitTile(tile, x, y);
			}
		}

		for(x = (0 - width); x < (2048 + width); x += width) {
			for(y = (0 - height); y < (2048 + height); y += height) {
				tile = new Tile("SceneTile(" + x + ", " + y + ")", x, y,
						width, height, this);
				tile.setFriction(0.60);
				tile.setCanCollide(false);
				tile.setImageTexture("assets/textures/test/tile_slow.png");
				scene.commitTile(tile, x, y);
				if((x != (0 - width)) && (x != 2048)) {
					y += 2048;
				}
			}
		}

		for(x = (0 - (width * 2)); x < (2048 + (width * 2)); x += width) {
			for(y = (0 - (height * 2)); y < (2048 + (height * 2));
					y += height) {
				tile = new Tile("SceneTile(" + x + ", " + y + ")", x, y,
						width, height, this);
				tile.setFriction(0.40);
				tile.setCanCollide(true);
				tile.setImageTexture("assets/textures/test/tile_off.png");
				scene.commitTile(tile, x, y);
				if((x != (0 - (width * 2))) && (x != (2048 + width))) {
					y += (2048 + (height * 2));
				}
			}
		}

		isGameLoaded = true;
	}

	public synchronized void processGameplay()
	{
		int direction = 0;

		Sprite spriteCollision = null;

		if(!isGameLoaded) {
			return;
		}

		if(keyboard.isKeyDown(KEY_UP)) {
			physics.processPhysics(SpritePhysics.MODE_FORWARD);
			scene.getCamera().applyCameraDiff(sprite.getCameraDiffX(),
					sprite.getCameraDiffY());
		}
		if(keyboard.isKeyDown(KEY_DOWN)) {
			physics.processPhysics(SpritePhysics.MODE_REVERSE);
			scene.getCamera().applyCameraDiff(sprite.getCameraDiffX(),
					sprite.getCameraDiffY());
		}
		if(keyboard.isKeyDown(KEY_LEFT)) {
			if(physics.getPixelRate() > 0.1) {
				direction = sprite.getDirection();
				direction -= 1;
				if(direction < 0) {
					direction = (Sprite.DIRECTIONS - 1);
				}
				sprite.changeDirection(direction, Sprite.COUNTER_CLOCKWISE,
						frameRate.get());
			}
		}
		if(keyboard.isKeyDown(KEY_RIGHT)) {
			if(physics.getPixelRate() > 0.1) {
				direction = sprite.getDirection();
				direction += 1;
				if(direction >= Sprite.DIRECTIONS) {
					direction = 0;
				}
				sprite.changeDirection(direction, Sprite.CLOCKWISE,
						frameRate.get());
			}
		}

		scene.processGameplay(frameRate.get());

		spriteCollision = scene.processCollisions();
		if(spriteCollision != null) {
			animation.setLocation(
					(spriteCollision.getX() + spriteCollision.getOffsetX()),
					(spriteCollision.getY() + spriteCollision.getOffsetY()));
			animation.start(true);
			player.play();
		}

		cannon.processGameplay(frameRate.get());
		cannon.setLocation(sprite.getX(), sprite.getY());
		cannon.setOffsets(sprite.getOffsetX(), sprite.getOffsetY());

		animation.processGameplay(frameRate.get());
/*		animation.applyCamera(scene.getCamera().getCameraX(),
				scene.getCamera().getCameraY(),
				scene.getCamera().getWindowWidth(),
				scene.getCamera().getWindowHeight());*/

		if(ball != null) {
			ballPhysics.processGameplay();
			ball.setOffsets(sprite.getOffsetX(), sprite.getOffsetY());
			ball.applyCamera(scene.getCamera().getCameraX(),
					scene.getCamera().getCameraY(),
					scene.getCamera().getWindowWidth(),
					scene.getCamera().getWindowHeight());
			ball.processGameplay(frameRate.get());

			if((ballPhysics.getPixelRate() == 0.0) &&
					(!ballAnimationTrigger)) {
				ballAnimation.setLocation(
						(ball.getX() - (ballAnimation.getWidth() / 2)),
						(ball.getY() - (ballAnimation.getHeight() / 2)));
				ballAnimation.setOffsets(ball.getOffsetX(), ball.getOffsetY());
				ballAnimation.start(true);
				player.play();
				ballAnimationTrigger = true;
			}

			ballAnimation.processGameplay(frameRate.get());
			ballAnimation.applyCamera(scene.getCamera().getCameraX(),
					scene.getCamera().getCameraY(),
					scene.getCamera().getWindowWidth(),
					scene.getCamera().getWindowHeight());

			if((ballAnimationTrigger) && (!ballAnimation.isPlaying())) {
				ballAnimationTrigger = false;
				ball = null;
			}
		}
	}
}

