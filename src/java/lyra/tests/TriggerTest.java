/*
 * TriggerTest.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A simple extension of the GameEngine to test the Trigger system.
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
import lyra.triggers.*;

// define class

public class TriggerTest extends GameEngine
{
	// define private class constants

	private static final String CLASS_NAME = TriggerTest.class.getName();
	private static final int MIP_MAPS = 7;

	// define private class variables

	int triggerX = 0;
	int triggerY = 0;
	int triggerWidth = 0;
	int triggerHeight = 0;
	TriggerSystem triggerSystem = null;

	// graphics rendering functions

	public synchronized void pipeline()
	{
		if(!isGameLoaded) {
			return;
		}

		setColor(Color.blue);
		drawRect(triggerX, triggerY, triggerWidth, triggerHeight);

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
		int ii = 0;
		int length = 0;

		TriggerEventImpl triggerEvent = null;

		if(!isGameLoaded) {
			return;
		}

		if(type == MouseDriver.EVENT_PRESSED) {
			if((mouseX >= triggerX) &&
					(mouseX <= (triggerX + triggerWidth)) &&
					(mouseY >= triggerY) &&
					(mouseY <= (triggerY + triggerHeight))) {
				length = (((int)(Math.random() * 10000.0) % 3) + 1);
				for(ii = 0; ii < length; ii++) {
					triggerEvent = new TriggerEventImpl(ii, "TestEvent");
					triggerSystem.recordEvent(triggerEvent);
				}
			}
		}
	}

	public synchronized void handleKeyboardEvents(KeyEvent event)
	{
		if(!isGameLoaded) {
			return;
		}
	}

	// game logic

	public synchronized void loadGame()
	{
		TriggerConsumerImpl consumer = null;
		TriggerActionImpl action = null;

		triggerX = 128;
		triggerY = 128;
		triggerWidth = 256;
		triggerHeight = 256;

		triggerSystem = new TriggerSystem("TestTriggerSystem");

		consumer = new TriggerConsumerImpl("TestTriggerConsumer");
		action = new TriggerActionImpl("TestTriggerAction");

		action.registerConsumer("TestEvent", consumer);

		triggerSystem.registerAction(action);

		isGameLoaded = true;
	}

	public synchronized void processGameplay()
	{
		TriggerAction action = null;

		if(!isGameLoaded) {
			return;
		}

		triggerSystem.processRecordedEvents();
		if(triggerSystem.isTriggered()) {
			action = triggerSystem.getTriggeredAction();
			while(action != null) {
				System.out.println("...obtained triggered action: " +
						action.getName());
				action = triggerSystem.getTriggeredAction();
			}
		}
	}
}

