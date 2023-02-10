/*
 * AnimationTest.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A simple extension of the GameEngine to test the Animation system.
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

public class AnimationTest extends GameEngine
{
	// define private class constants

	private static final String CLASS_NAME = AnimationTest.class.getName();

	// define private class variables

	int selection = 0;
	int defaultWidth = 0;
	int defaultHeight = 0;
	Animation animation = null;

	// graphics rendering functions

	public synchronized void pipeline()
	{
		if(!isGameLoaded) {
			return;
		}

		animation.render2D(render2D);

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
		drawString("Press 'a' to loop, 's' to start, and 'd' to stop", 0, 60);
		drawString("Click on-screen to start the animation at a new location",
				0, 76);
	}

	// event handling

	public synchronized void handleMouseEvents(int type, MouseEvent event)
	{
		if(!isGameLoaded) {
			return;
		}

		if(type == MouseDriver.EVENT_PRESSED) {
			animation.stop();
			animation.setLocation(mouseX, mouseY);
			animation.start(true);
		}
	}

	public synchronized void handleKeyboardEvents(KeyEvent event)
	{
		if(!isGameLoaded) {
			return;
		}

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

	// game logic

	public synchronized void loadGame()
	{
		animation = new Animation("Animation Test", 128, 128, 256, 256, this);
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

		isGameLoaded = true;
	}

	public synchronized void processGameplay()
	{
		if(!isGameLoaded) {
			return;
		}

		animation.processGameplay(frameRate.get());
	}
}

