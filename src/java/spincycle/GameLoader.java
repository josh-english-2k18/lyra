/*
 * GameLoader.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * The Spincycle game loading system.
 *
 * Written by Josh English.
 */

// define package space

package spincycle;

// import external packages

import java.awt.Color;
import java.awt.Image;

// import internal packages

import lyra.*;
import lyra.widgets.Autoscroll;
import lyra.widgets.FontInfo;
import lyra.widgets.HyperLink;

// define class

public class GameLoader implements Runnable
{
	// define private class constants

	private static final String CLASS_NAME = GameLoader.class.getName();

	// define private class variables

	private Thread thread = null;
	private Spincycle spincycle = null;

	// define public class functions

	public GameLoader(Spincycle spincycle)
	{
		this.spincycle = spincycle;

		thread = new Thread(this);
		thread.start();
	}

	public void run()
	{
		Image image = null;
		AssetCache assetCache = null;
		HyperLink hyperlink = null;

		System.out.println("Game loading thread started.");

		spincycle.debugMode = false;
		spincycle.isInternalGameLoaded = false;
		spincycle.gameState = Spincycle.STATE_INIT;
		spincycle.gameLevel = 0;
		spincycle.displayWidth = 0;
		spincycle.levelStartTime = 0;
		spincycle.levelPauseTime = 0;
		spincycle.currentBallSpeed = 0.0;
		spincycle.menu = null;
		spincycle.background = null;
		spincycle.outlineCorners = null;
		spincycle.outlineSolid = null;
		spincycle.ball = null;
		spincycle.bricks = null;
		spincycle.player = null;
		spincycle.game = null;
		spincycle.revbar = null;
		spincycle.credits = null;
		spincycle.fontInfo = null;

		// set asset cache

		assetCache = spincycle.getAssetCache();

		// preload images

		image = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
				"assets/textures/spincycle/ball.png");

		image = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
				"assets/textures/spincycle/brickGreen.png");

		image = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
				"assets/textures/spincycle/brickBlue.png");

		image = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
				"assets/textures/spincycle/brickRed.png");

		// calculate display width

		spincycle.displayWidth = spincycle.getWindowWidth();
		if(spincycle.displayWidth > spincycle.getWindowHeight()) {
			spincycle.displayWidth = spincycle.getWindowHeight();
		}

		// init the game logic

		spincycle.player = new Player();
		spincycle.game = new Game(spincycle.displayWidth, spincycle.player,
				spincycle);

		// load bricks

		spincycle.bricks = spincycle.game.loadLevel(spincycle.gameLevel);
		if(spincycle.bricks == null) {
			spincycle.bricks = spincycle.game.loadDefaultLevelOne();
		}

		// load ball

		spincycle.ball = new Ball("Ball",
				((spincycle.getWindowWidth() - (spincycle.displayWidth / 2)) -
				 Ball.DEFAULT_RADIUS),
				((spincycle.getWindowHeight() / 2) - Ball.DEFAULT_RADIUS),
				(Ball.DEFAULT_RADIUS * 2), (Ball.DEFAULT_RADIUS * 2),
				spincycle.displayWidth, spincycle);
		spincycle.ball.setDebugMode(false);
		spincycle.ball.setStatic(false);
		spincycle.ball.setOutline(false);
		spincycle.ball.setSelectable(false);
		spincycle.ball.setOutlineType(Ball.OUTLINE_TYPE_SOLID);
		spincycle.ball.setImageTexture(Sprite.DIRECTION_EAST,
				"assets/textures/spincycle/ball.png");

		// load revbar

		spincycle.revbar = new RevbarWidget(10, 200, 180, 30, spincycle);// TODO: auto-correct
		spincycle.revbar.setTick(15, 195, 10, 40);

		image = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
				"assets/textures/spincycle/revbar.png");
		spincycle.revbar.setBarImage(image);

		image = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
				"assets/textures/spincycle/revbarTick.png");
		spincycle.revbar.setTickImage(image);

		// load images

		spincycle.menu = (Image)assetCache.getAsset(
				AssetCache.TYPE_IMAGE,
				"assets/textures/spincycle/menu.png");

		spincycle.background = (Image)assetCache.getAsset(
				AssetCache.TYPE_IMAGE,
				"assets/textures/spincycle/background.png");

		spincycle.outlineCorners = (Image)assetCache.getAsset(
				AssetCache.TYPE_IMAGE,
				"assets/textures/spincycle/outlineCorners.png");

		spincycle.outlineSolid = (Image)assetCache.getAsset(
				AssetCache.TYPE_IMAGE,
				"assets/textures/spincycle/outlineSolid.png");

		// build the font info

		spincycle.fontInfo = new FontInfo(spincycle);
		spincycle.fontInfo.setString(
				"The Quick Brown Fox Jumped Over The Lazy Dogs.");

		// setup the credits

		hyperlink = new HyperLink("CreditsLinkWidget", 0, 0, spincycle);
		hyperlink.setLink("Xaede Game Portal", "http://www.xaede.com");
		hyperlink.setAutoRedirect(true);

		spincycle.credits = new Autoscroll("CreditsWidget",
				(spincycle.getWindowWidth() - spincycle.displayWidth), 0,
				spincycle.displayWidth, spincycle.displayWidth,
				spincycle);
		spincycle.credits.setTextColor(Color.white);
		image = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
				"assets/textures/galaxik/gui/background/image02.png");
		spincycle.credits.setBackground(image);
		spincycle.credits.addText("");
		spincycle.credits.addText("");
		spincycle.credits.addText("");
		spincycle.credits.addText(Color.red, "Spincycle");
		spincycle.credits.addText("");
		spincycle.credits.addText("");
		spincycle.credits.addText("A ball & brick game spun out of control!");
		spincycle.credits.addText("");
		spincycle.credits.addText("Copyright (C) 2008 by Joshua S. English.");
		spincycle.credits.addText("All rights reserved.");
		spincycle.credits.addText("");
		spincycle.credits.addText("");
		spincycle.credits.addText("- Credits -");
		spincycle.credits.addText("");
		spincycle.credits.addText(Color.lightGray, "Game Design/Programming");
		spincycle.credits.addText("");
		spincycle.credits.addText("Joshua S. English");
		spincycle.credits.addText("");
		spincycle.credits.addText("");
		spincycle.credits.addText(Color.lightGray, "Sound Design");
		spincycle.credits.addText("");
		spincycle.credits.addText("Joshua S. English");
		spincycle.credits.addText("");
		spincycle.credits.addText("");
		spincycle.credits.addText(Color.lightGray, "Artwork");
		spincycle.credits.addText("");
		spincycle.credits.addText("Joshua S. English");
		spincycle.credits.addText("");
		spincycle.credits.addText("");
		spincycle.credits.addText(Color.lightGray, "- Special Thanks -");
		spincycle.credits.addText("");
		spincycle.credits.addText("Sponsoring Websites");
		spincycle.credits.addText("");
		spincycle.credits.addHyperlink(hyperlink);

		// set the start time

		spincycle.levelStartTime = System.currentTimeMillis();

		// end of game load

		System.out.println("Game loading thread shutdown.");

		spincycle.isInternalGameLoaded = true;
	}
}

