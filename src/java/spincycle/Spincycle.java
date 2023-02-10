/*
 * Spincycle.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A Lyra Game Engine based ball & brick game with mobile bricks - Spincycle.
 *
 * Written by Josh English.
 */

// define package space

package spincycle;

// import external packages

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.awt.Image;

// import internal packages

import lyra.*;
import lyra.drivers.KeyboardDriver;
import lyra.drivers.MouseDriver;
import lyra.widgets.Autoscroll;
import lyra.widgets.FontInfo;

// define class

public class Spincycle extends GameEngine
{
	// define private class constants

	private static final String CLASS_NAME = Spincycle.class.getName();

	// define public class constants

	public static final int STATE_INIT = 1;
	public static final int STATE_PLAYING = 2;
	public static final int STATE_PAUSED = 3;
	public static final int STATE_LEVEL = 4;
	public static final int STATE_OVER = 5;

	public static final int GAME_LEVELS = 5;

	// define public class variables

	public boolean debugMode = false;
	public boolean isInternalGameLoaded = false;
	public int gameState = 0;
	public int gameLevel = 0;
	public int displayWidth = 0;
	public long levelStartTime = 0;
	public long levelPauseTime = 0;
	public double currentBallSpeed = 0.0;
	public Image menu = null;
	public Image background = null;
	public Image outlineCorners = null;
	public Image outlineSolid = null;
	public Ball ball = null;
	public Brick bricks[] = null;
	public Player player = null;
	public Game game = null;
	public RevbarWidget revbar = null;
	public Autoscroll credits = null;
	public FontInfo fontInfo = null;

	// graphics rendering functions

	private void renderBackground()
	{
		int ii = 0;
		int localX = 0;
		int localY = 0;
		long diffTime = 0;

		String string = null;
		Image image = null;

		// render navbar

		render.drawImage(menu, 0, 0, (windowWidth - displayWidth),
				displayWidth, this);

		// render game title

		render.setColor(Color.red);
		fontInfo.setString("Spin Cycle");
		render.drawString("Spin Cycle",
				(((windowWidth - displayWidth) / 2) -
				 (fontInfo.getWidth() / 2)),
				(fontInfo.getHeight() + 8 + 2));

		// render player score

		string = (new Integer(player.getScore())).toString();
		fontInfo.setString(string);
		render.drawString(string,
				(((windowWidth - displayWidth) / 2) -
				 (fontInfo.getWidth() / 2)),
				(fontInfo.getHeight() + 8 + 32));

		// render elapsed time

		if(((gameState == STATE_PLAYING) || (gameState == STATE_PAUSED)) &&
				(player.isAlive())) {
			if(gameState == STATE_PAUSED) {
				diffTime = (System.currentTimeMillis() - levelPauseTime);
				levelPauseTime = System.currentTimeMillis();
				levelStartTime += diffTime;
			}
			string = new Long((System.currentTimeMillis() -
						levelStartTime) / 1000).toString();
			fontInfo.setString(string);
			render.drawString(string,
					(((windowWidth - displayWidth) / 2) -
					 (fontInfo.getWidth() / 2)),
					(fontInfo.getHeight() + 8 + 64));
		}

		// render remaining lives (balls)

		image = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
				"assets/textures/spincycle/ball.png");
		localX = 24; // TODO: auto-correct
		localY = 101; // TODO: auto-correct
		for(ii = 0; ii < player.getLives(); ii++) {
			render.drawImage(image, localX, localY,
					ball.getWidth(), ball.getHeight(), this);
			localX += (ball.getWidth() + 2);
		}

		// render ball-score bonus

		localX = 100; // TODO: auto-correct
		localY = 95; // TODO: auto-correct
		switch(game.getLastBrickType()) {
			case Brick.TYPE_GREEN:
				image = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
						"assets/textures/spincycle/brickGreen.png");
				break;
			case Brick.TYPE_BLUE:
				image = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
						"assets/textures/spincycle/brickBlue.png");
				break;
			case Brick.TYPE_RED:
				image = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
						"assets/textures/spincycle/brickRed.png");
				break;
			default:
				image = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
						"assets/textures/spincycle/brickGreen.png");
		}
		render.drawImage(image, localX, localY,
				Brick.DEFAULT_WIDTH, Brick.DEFAULT_HEIGHT, this);
		string = "+ " + game.getChainScore();
		fontInfo.setString(string);
		render.drawString(string,
				(localX + Brick.DEFAULT_WIDTH + 2),
				(localY + fontInfo.getHeight()));

		// render level

		string = "Level " + (gameLevel + 1);
		fontInfo.setString(string);
		render.drawString(string,
				(((windowWidth - displayWidth) / 2) -
				 (fontInfo.getWidth() / 2)),
				(fontInfo.getHeight() + 8 + 164));

		// render ball speed

		string = "Ball Speed";
		fontInfo.setString(string);
		render.drawString(string,
				(((windowWidth - displayWidth) / 2) -
				 (fontInfo.getWidth() / 2)),
				(fontInfo.getHeight() + 8 + 196));
		revbar.render2D(render2D);

		// render game-area background

		render.drawImage(background, (windowWidth - displayWidth), 0,
					displayWidth, displayWidth, this);

		// render outline

		image = null;
		if(ball.getOutlineType() == Ball.OUTLINE_TYPE_SOLID) {
			image = outlineSolid;
		}
		else if(ball.getOutlineType() == Ball.OUTLINE_TYPE_CORNERS) {
			image = outlineCorners;
		}
		if(image != null) {
			render.drawImage(image, (windowWidth - displayWidth), 0,
					displayWidth, displayWidth, this);
		}
	}

	private void renderInit()
	{
		String string = null;

		render.setColor(Color.red);

		string = "( click mouse to begin )";
		fontInfo.setString(string);
		render.drawString(string,
				(((windowWidth - displayWidth) + (displayWidth / 2)) -
				 (fontInfo.getWidth() / 2)),
				((displayWidth / 2) + fontInfo.getHeight()));

		string = "Use left & right arrow keys to rotate bricks.";
		fontInfo.setString(string);
		render.drawString(string,
				(((windowWidth - displayWidth) + (displayWidth / 2)) -
				 (fontInfo.getWidth() / 2)),
				((displayWidth / 2) + (fontInfo.getHeight() * 2) + 4));

		string = "Press the space bar to pause & unpause the game.";
		fontInfo.setString(string);
		render.drawString(string,
				(((windowWidth - displayWidth) + (displayWidth / 2)) -
				 (fontInfo.getWidth() / 2)),
				((displayWidth / 2) + (fontInfo.getHeight() * 3) + 8));
	}

	private void renderPlaying()
	{
		int ii = 0;

		for(ii = 0; ii < bricks.length; ii++) {
			if((bricks[ii] == null) || (!bricks[ii].isAlive())) {
				continue;
			}

			bricks[ii].render2D(render2D);
			if(Game.calculateDistance(ball, bricks[ii]) < 64.0) { // TODO: auto-correct
				ball.renderDistanceAnimation(bricks[ii], render2D);
			}
		}

		for(ii = 0; ii < bricks.length; ii++) {
			if(bricks[ii] == null) {
				continue;
			}

			bricks[ii].renderAnimation(render2D);
		}

		ball.render2D(render2D);
	}

	private void renderPaused()
	{
		String string = null;

		render.setColor(Color.red);

		string = "!!! Paused !!!";
		fontInfo.setString(string);
		render.drawString(string,
				(((windowWidth - displayWidth) + (displayWidth / 2)) -
				 (fontInfo.getWidth() / 2)),
				((displayWidth / 2) + fontInfo.getHeight()));
	}

	public synchronized void pipeline()
	{
		if(!isGameLoaded) {
			return;
		}

		if(!isInternalGameLoaded) {
			setColor(Color.white);
			drawString("Loading...", 32, 32);
			return;
		}

		renderBackground();

		if(gameState == STATE_INIT) {
			renderInit();
		}
		else if(gameState == STATE_PLAYING) {
			renderPlaying();
		}
		else if(gameState == STATE_PAUSED) {
			renderPlaying();
			renderPaused();
		}
		else if(gameState == STATE_OVER) {
			credits.render2D(render2D);
		}

		if(debugMode) {
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
	}

	// event handling

	public synchronized void handleMouseEvents(int type, MouseEvent event)
	{
		if((!isGameLoaded) || (!isInternalGameLoaded)) {
			return;
		}

		if(gameState == STATE_INIT) {
			if(type == MouseDriver.EVENT_PRESSED) {
				gameState = STATE_PLAYING;
			}
		}
		else if(gameState == STATE_OVER) {
			if(!player.isAlive()) {
				if(type == MouseDriver.EVENT_PRESSED) {
					credits.isClicked(mouseX, mouseY);
				}
				else {
					credits.hasFocus(mouseX, mouseY);
				}
			}
		}
	}

	public synchronized void handleKeyboardEvents(KeyEvent event)
	{
		int ii = 0;

		if((!isGameLoaded) || (!isInternalGameLoaded)) {
			return;
		}

		// global events handling

		if(keyboardEventType == KeyboardDriver.EVENT_KEY_RELEASED) {
			if((!RELEASE_BUILD) && (event.getKeyChar() == 'D')) {
				if(debugMode) {
					debugMode = false;
				}
				else {
					debugMode = true;
				}
			}
			else if((!RELEASE_BUILD) && (lastBinaryKeyPress == 27)) { // escape
				player.kill();
				credits.reset();
				gameState = STATE_OVER;
			}
		}

		// game state-based handling

		if(gameState == STATE_PLAYING) {
			if(keyboardEventType == KeyboardDriver.EVENT_KEY_PRESSED) {
				if((lastBinaryKeyPress == 37) || (lastBinaryKeyPress == 39)) {
					game.calculateCollisions(ball, bricks, null);
					if(lastBinaryKeyPress == 37) { // left arrow
						for(ii = 0; ii < bricks.length; ii++) {
							if(bricks[ii].isAlive()) {
								bricks[ii].rotate(Brick.ROTATE_LEFT);
							}
						}
					}
					else if(lastBinaryKeyPress == 39) { // right arrow
						for(ii = 0; ii < bricks.length; ii++) {
							if(bricks[ii].isAlive()) {
								bricks[ii].rotate(Brick.ROTATE_RIGHT);
							}
						}
					}
				}
			}
			else if(keyboardEventType == KeyboardDriver.EVENT_KEY_RELEASED) {
				if(event.getKeyChar() == ' ') {
					gameState = STATE_PAUSED;
					levelPauseTime = System.currentTimeMillis();
				}
			}
		}
		else if(gameState == STATE_PAUSED) {
			if(keyboardEventType == KeyboardDriver.EVENT_KEY_RELEASED) {
				if(event.getKeyChar() == ' ') {
					gameState = STATE_PLAYING;
				}
			}
		}
	}

	// game logic

	public synchronized void loadGame()
	{
		GameLoader gameLoader = null;

		gameLoader = new GameLoader(this);

		isGameLoaded = true;
	}

	public synchronized void processGameplay()
	{
		boolean foundBrick = false;
		int ii = 0;
		int tempInt = 0;

		if((!isGameLoaded) || (!isInternalGameLoaded)) {
			return;
		}

		ball.setCurrentFramerate();

		if(gameState == STATE_PLAYING) {
			if(!player.isAlive()) {
				gameState = STATE_OVER;
			}

			game.calculateCollisions(ball, bricks, null);

			ball.animate();
			if((ball.getOutlineType() == Ball.OUTLINE_TYPE_SOLID) &&
					(ball.getReflectHits() > 3)) {
				ball.setOutlineType(Ball.OUTLINE_TYPE_CORNERS);
			}

			tempInt = ball.getEdgeBounce();
			if(tempInt > 0) {
				for(ii = 0; ii < tempInt; ii++) {
					player.lostLife();
				}
				if(!player.isAlive()) {
					gameState = STATE_OVER;
					return;
				}
				ball.reset();
				currentBallSpeed = ball.getMagnitude();
				revbar.setTickX(15);
				revbar.setTickY(195);
			}

			foundBrick = false;
			for(ii = 0; ii < bricks.length; ii++) {
				if(bricks[ii].isAlive()) {
					foundBrick = true;
					break;
				}
			}
			if(!foundBrick) {
				gameState = STATE_OVER;
			}

			if(ball.isAtMaxSpeed()) {
				currentBallSpeed = ball.getMagnitude();
			}

			if(ball.getMagnitude() != currentBallSpeed) {
				if((revbar.getTickX() < 175) &&
						(ball.getMagnitude() > currentBallSpeed)) {
					revbar.setTickX(revbar.getTickX() + 3);
				}
				else if((revbar.getTickX() > 15) &&
						(ball.getMagnitude() < currentBallSpeed)) {
					revbar.setTickX(revbar.getTickX() - 3);
				}
				currentBallSpeed = ball.getMagnitude();
			}
		}
		else if(gameState == STATE_LEVEL) {
			player.addLife();
			ball.setOutlineType(Ball.OUTLINE_TYPE_SOLID);
			ball.reset();
			bricks = game.loadLevel(gameLevel);
			if(bricks == null) {
				bricks = game.loadDefaultLevelOne();
			}
			levelStartTime = System.currentTimeMillis();
			gameState = STATE_PLAYING;
		}
		else if(gameState == STATE_OVER) {
			if(player.isAlive()) {
				gameLevel++;
				if(gameLevel >= GAME_LEVELS) {
					player.kill();
					credits.reset();
				}
				else {
					gameState = STATE_LEVEL;
				}
			}
			else {
				if((getTicks() % 8) == 0) {
					credits.process();
				}
				if(!credits.isRunning()) {
					debugMode = false;
					isInternalGameLoaded = true;
					gameLevel = 0;
					player = new Player();
					ball.reset();
					ball.setOutlineType(Ball.OUTLINE_TYPE_SOLID);
					bricks = game.loadLevel(gameLevel);
					if(bricks == null) {
						bricks = game.loadDefaultLevelOne();
					}
					levelStartTime = System.currentTimeMillis();
					gameState = STATE_PLAYING;
				}
			}
		}
	}
}

