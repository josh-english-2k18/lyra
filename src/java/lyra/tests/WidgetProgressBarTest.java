/*
 * WidgetProgressBarTest.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A simple extension of the GameEngine to test the GUI widget progress bar
 * system.
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
import lyra.widgets.Widget;
import lyra.widgets.CorneredBox;
import lyra.widgets.ProgressBar;
import lyra.util.AnimationLoader;

// define class

public class WidgetProgressBarTest extends GameEngine
{
	// define private class constants

	private static final String CLASS_NAME =
		WidgetProgressBarTest.class.getName();

	// define private class variables

	private ProgressBar widget = null;
	private AnimationLoader loader = null;
	private Animation animation = null;

	// graphics rendering functions

	public synchronized void pipeline()
	{
		if(!isGameLoaded) {
			return;
		}

		widget.render2D(render2D);
		loader.render2D(render2D);
		if(animation != null) {
			animation.render2D(render2D);
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
		if(animation != null) {
			drawString("Click mouse to start animation", 0, 60);
		}
		drawString("Loading Animation", 256, 240);
	}

	// event handling

	public synchronized void handleMouseEvents(int type, MouseEvent event)
	{
		if(!isGameLoaded) {
			return;
		}

		if(type == MouseDriver.EVENT_PRESSED) {
			if(mouse.getCurrentButton() == MouseEvent.BUTTON3) {
				widget.setLocation(mouseX, mouseY);
				widget.start();
			}
			else if(animation != null) {
				animation.stop();
				animation.setLocation(mouseX, mouseY);
				animation.start(true);			
			}
		}
	}

	public synchronized void handleKeyboardEvents(KeyEvent event)
	{
		if(!isGameLoaded) {
			return;
		}

		if(animation != null) {
			if(keyboardEventType == KeyboardDriver.EVENT_KEY_PRESSED) {
				if(event.getKeyChar() == 's') { // i.e. start
					animation.start(true);
				}
				else if(event.getKeyChar() == 'a') { // i.e. start looping
					animation.start(false);
				}
			}
			else if(keyboardEventType == KeyboardDriver.EVENT_KEY_RELEASED) {
				if(event.getKeyChar() == 'd') { // i.e. stop
						animation.stop();
				}
			}
		}
	}

	// game logic

	public synchronized void loadGame()
	{
		widget = new ProgressBar("ProgressBarWidgetTest", 128, 128, 256, 32,
				this);
		widget.setTotalTicks(500);
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

		loader = new AnimationLoader(
				"assets/config/animations/explosion01.config", 256, 256, 256,
				32, this);
		loader.start();

		isGameLoaded = true;
	}

	public synchronized void processGameplay()
	{
		if(!isGameLoaded) {
			return;
		}

		if(widget.getState() == ProgressBar.STATE_RUNNING) {
			widget.increment(1);
		}
		else {
			widget.start();
		}

		if(!loader.isComplete()) {
			loader.processLoad();
			if(loader.isComplete()) {
				loader.finalize();
				animation = (Animation)loader.getLoadedAsset();
			}
		}

		if(animation != null) {
			animation.processGameplay(frameRate.get());
		}
	}
}

