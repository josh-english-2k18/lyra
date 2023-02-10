/*
 * KeyboardDriver.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A simple keyboard driver to collect, queue, and delivery user input via the
 * keyboard.
 *
 * Written by Josh English.
 */

// define package space

package lyra.drivers;

// import external packages

import java.lang.StringBuffer;
import java.util.LinkedList;
import java.util.Date;
import java.applet.Applet;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

// define class

public class KeyboardDriver implements KeyListener
{
	// define public class constants

	public static final int EVENT_KEY_PRESSED = 1;
	public static final int EVENT_KEY_RELEASED = 2;
	public static final int EVENT_KEY_TYPED = 3;

	// define private class constants

	private static final String CLASS_NAME = KeyboardDriver.class.getName();
	private static final int ARRAY_SIZE = 256;
	private static final int AUTOGEN_PER_SECOND = 20;

	// define private class variables

	private boolean shiftKeyOn = false;
	private boolean keyState[] = null;
	private int type = 0;
	private double threshold = 0.0;
	private long keyTimes[] = null;
	private StringBuffer buffer = null;
	private LinkedList eventQueue = null;
	private Applet applet = null;

	// define private class objects

	private class KeyboardEventWrapper
	{
		private int type;
		private KeyEvent event;

		KeyboardEventWrapper(KeyEvent e, int type)
		{
			this.event = e;
			this.type = type;
		}
	}		

	// define private class functions

	private int charToInt(char value)
	{
		int result = 0;

		result = (int)value;
		if(result < 0) {
			result += ARRAY_SIZE;
		}
		result = (result % ARRAY_SIZE);

		return result;
	}

	private double getTimeDeltaMillis(long timeStamp)
	{
		double delta = 0.0;

		Long current = null;
		Long previous = null;

		previous = new Long(timeStamp);
		current = new Long((new Date()).getTime());

		delta = (current.doubleValue() - previous.doubleValue());

		return delta;
	}

	// define static public class functions

	public static int charToInteger(char value)
	{
		int result = 0;

		result = (int)value;
		if(result < 0) {
			result += ARRAY_SIZE;
		}
		result = (result % ARRAY_SIZE);

		return result;
	}

	// define public class functions

	public KeyboardDriver(Applet applet)
	{
		int ii = 0;

		type = 0;
		threshold = ((1.0 / new Double(AUTOGEN_PER_SECOND).doubleValue()) *
				1000.0);
		keyState = new boolean[ARRAY_SIZE];
		for(ii = 0; ii < ARRAY_SIZE; ii++) {
			keyState[ii] = false;
		}
		keyTimes = new long[ARRAY_SIZE];
		buffer = new StringBuffer();
		eventQueue = new LinkedList();
		this.applet = applet;
	}

	public String getClassName()
	{
		return CLASS_NAME;
	}

	public synchronized void keyPressed(KeyEvent event)
	{
		int asciiValue = 0;

		asciiValue = charToInt(event.getKeyChar());
		if((asciiValue > 31) && (asciiValue < 127)) {
			buffer.append(event.getKeyChar());
			keyState[asciiValue] = true;
			keyTimes[asciiValue] = ((new Date()).getTime() + (long)threshold);
		}
		else if((event.getKeyCode() >= 37) && (event.getKeyCode() <= 40)) {
			asciiValue = (event.getKeyCode() - 37);
			keyState[asciiValue] = true;
			keyTimes[asciiValue] = ((new Date()).getTime() + (long)threshold);
		}
		if(asciiValue == 16) {
			shiftKeyOn = true;
		}

		eventQueue.add(new KeyboardEventWrapper(event, EVENT_KEY_PRESSED));
	}

	public synchronized void keyReleased(KeyEvent event)
	{
		int asciiValue = 0;

		asciiValue = charToInt(event.getKeyChar());
		if((asciiValue > 31) && (asciiValue < 127)) {
			keyState[asciiValue] = false;
		}
		else if((event.getKeyCode() >= 37) && (event.getKeyCode() <= 40)) {
			asciiValue = (event.getKeyCode() - 37);
			keyState[asciiValue] = false;
		}
		if(asciiValue == 16) {
			shiftKeyOn = false;
		}

		eventQueue.add(new KeyboardEventWrapper(event, EVENT_KEY_RELEASED));
	}

	public synchronized void keyTyped(KeyEvent event)
	{
		type = EVENT_KEY_TYPED;
	}

	public synchronized int getKeyType()
	{
		return type;
	}

	public synchronized String getKeyBuffer()
	{
		String result = null;

		result = buffer.toString();
		buffer = new StringBuffer();

		return result;
	}

	public synchronized void processKeyPressEvents()
	{
		int ii = 0;
		int value = 0;

		KeyEvent event = null;

		for(ii = 0; ii < ARRAY_SIZE; ii++) {
			if((keyState[ii]) &&
					(getTimeDeltaMillis(keyTimes[ii]) >= threshold)) {
				keyTimes[ii] = (new Date()).getTime();
				if(ii > 3) {
					value = ii;
				}
				else {
					value = (ii + 37);
				}
				event = new KeyEvent(applet, KeyEvent.KEY_PRESSED,
						(new Date()).getTime(), 0, value, (char)value);
				eventQueue.add(new KeyboardEventWrapper(event,
							EVENT_KEY_PRESSED));
			}
		}
	}

	public synchronized boolean hasEvent()
	{
		return (!this.eventQueue.isEmpty());
	}

	public synchronized boolean isShiftKeyOn()
	{
		return shiftKeyOn;
	}

	public synchronized boolean isKeyUp(int keyRef)
	{
		if((keyRef < 0) || (keyRef >= ARRAY_SIZE)) {
			return false;
		}
		if(keyState[keyRef]) {
			return false;
		}
		return true;
	}

	public synchronized boolean isKeyDown(int keyRef)
	{
		if((keyRef < 0) || (keyRef >= ARRAY_SIZE)) {
			return false;
		}
		return keyState[keyRef];
	}

	public synchronized KeyEvent getKeyEvent()
	{
		KeyboardEventWrapper result = null;

		try {
			result = (KeyboardEventWrapper)this.eventQueue.removeFirst();
		}
		catch(Exception e) {
			result = null;
			this.type = 0;
			return (KeyEvent)null;
		}

		if(result == null) {
			return null;
		}
		this.type = result.type;

		return result.event;
	}
}

