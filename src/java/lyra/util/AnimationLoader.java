/*
 * AnimationLoader.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A simple component for loading lyra animations.
 *
 * Written by Josh English.
 */

// define package space

package lyra.util;

// import external packages

import java.awt.Graphics2D;

// import internal packages

import lyra.Animation;
import lyra.AssetCache;
import lyra.GameEngine;
import lyra.widgets.Widget;
import lyra.widgets.CorneredBox;
import lyra.widgets.ProgressBar;

// define class

public class AnimationLoader implements Loader
{
	// define private class constants

	private static final String CLASS_NAME = AnimationLoader.class.getName();

	// define private class variables

	protected boolean willAutoStop = false;
	protected boolean isComplete = false;
	protected int imageRef = 0;
	protected int imageTotal = 0;
	protected String name = null;
	protected GameEngine gameEngine = null;
	protected Config config = null;
	protected ProgressBar widget = null;
	protected Animation animation = null;

	// define class public functions

	public AnimationLoader(String name, int x, int y, int width, int height,
			GameEngine gameEngine)
	{
		this.name = name;
		this.gameEngine = gameEngine;

		willAutoStop = false;
		isComplete = false;
		imageRef = 0;
		imageTotal = 0;
		config = null;

		widget = new ProgressBar("{" + CLASS_NAME + "}" + name + "ProgressBar",
				x, y, width, height, gameEngine);
		widget.setFillImage("assets/textures/gui/progress01/progress.png");
		widget.setImage(CorneredBox.IMAGE_TOP,
				"assets/textures/gui/textbox01/top.png");
		widget.setImage(CorneredBox.IMAGE_BOTTOM,
				"assets/textures/gui/textbox01/bottom.png");
		widget.setImage(CorneredBox.IMAGE_LEFT,
				"assets/textures/gui/textbox01/left.png");
		widget.setImage(CorneredBox.IMAGE_RIGHT,
				"assets/textures/gui/textbox01/right.png");
		widget.setImage(CorneredBox.IMAGE_UPPER_LEFT,
				"assets/textures/gui/textbox01/upper_left.png");
		widget.setImage(CorneredBox.IMAGE_UPPER_RIGHT,
				"assets/textures/gui/textbox01/upper_right.png");
		widget.setImage(CorneredBox.IMAGE_LOWER_LEFT,
				"assets/textures/gui/textbox01/lower_left.png");
		widget.setImage(CorneredBox.IMAGE_LOWER_RIGHT,
				"assets/textures/gui/textbox01/lower_right.png");
	}

	public String getClassName()
	{
		return CLASS_NAME;
	}

	public String getName()
	{
		return name;
	}

	public boolean isComplete()
	{
		return isComplete;
	}

	public Object getLoadedAsset()
	{
		return animation;
	}

	public void start()
	{
		int x = 0;
		int y = 0;
		int width = 0;
		int height = 0;
		int animationMillis = 0;

		try {
			config = (Config)gameEngine.getAssetCache().getAsset(
					AssetCache.TYPE_CONFIG, name);
			if(config == null) {
				throw new Exception("{" + CLASS_NAME +
						"} unable to load config file '" + name + "'");
			}
			if(!config.getString("global", "type", "unknown").equals(
						"animation")) {
				throw new Exception("{" + CLASS_NAME + "} file '" + name +
						"' is not an animation");
			}

			willAutoStop = config.getBoolean("attributes", "willAutoStop",
					true);
			x = config.getInteger("attributes", "x", 0);
			y = config.getInteger("attributes", "y", 0);
			width = config.getInteger("attributes", "width", 0);
			height = config.getInteger("attributes", "height", 0);
			animationMillis = config.getInteger("attributes",
					"animationMillis", 0);

			imageTotal = config.getInteger("images", "imageCount", 0);
			widget.setTotalTicks(imageTotal);
			widget.start();
			isComplete = false;

			animation = new Animation(name, x, y, width, height, gameEngine);
			animation.setAnimationTime(animationMillis);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void processLoad()
	{
		String filename = null;
		StringBuffer property = null;
		Object asset = null;

		try {
			if(isComplete) {
				return;
			}

			property = new StringBuffer("" + imageRef);
			while(property.length() < 4) {
				property.insert(0, "0");
			}
			property.insert(0, "image");

			filename = config.getString("images", property.toString(),
					"unknown");
			asset = gameEngine.getAssetCache().getAsset(
					AssetCache.TYPE_RAW_IMAGE, filename);

			imageRef++;
			widget.increment(1);
			if(imageRef >= imageTotal) {
				isComplete = true;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void finalize()
	{
		int ii = 0;

		String filename = null;
		StringBuffer property = null;

		try {
			if(!isComplete) {
				return;
			}

			for(ii = 0; ii < imageTotal; ii++) {
				property = new StringBuffer("" + ii);
				while(property.length() < 4) {
					property.insert(0, "0");
				}
				property.insert(0, "image");
				filename = config.getString("images", property.toString(),
						"unknown");
				gameEngine.getAssetCache().finalizeRawAsset(
						AssetCache.TYPE_RAW_IMAGE, filename);
				animation.addImageTexture(filename);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void render2D(Graphics2D render)
	{
		widget.render2D(render);
	}
}

