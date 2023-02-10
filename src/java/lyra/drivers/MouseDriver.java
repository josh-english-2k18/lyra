/*
 * MouseDriver.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A simple mouse driver to collect, queue, and deliver user input via the
 * mouse.
 *
 * Written by Josh English.
 */

// define package space

package lyra.drivers;

// import external packages

import java.util.LinkedList;
import java.applet.Applet;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

// define class

public class MouseDriver implements MouseListener, MouseMotionListener,
	   MouseWheelListener
{
	// define public class constants

	public static final int EVENT_PRESSED = 1;
	public static final int EVENT_RELEASED = 2;
	public static final int EVENT_ENTERED = 3;
	public static final int EVENT_EXITED = 4;
	public static final int EVENT_CLICKED = 5;
	public static final int EVENT_DRAGGED = 6;
	public static final int EVENT_MOVED = 7;
	public static final int EVENT_WHEEL_MOVED = 8;

	// define private class constants

	private static final String CLASS_NAME = MouseDriver.class.getName();

	// define private class variables

	private boolean hasEvent = false;
	private int type = 0;
	private int windowWidth = 0;
	private int windowHeight = 0;
	private int windowX = 0;
	private int windowY = 0;
	private int currentButton = 0;
	private MouseEvent event = null;
	private LinkedList eventQueue = null;

	// define private class objects

	private class MouseEventWrapper
	{
		private int type = 0;
		private MouseEvent event = null;

		private MouseEventWrapper(MouseEvent newEvent, int eventType)
		{
			this.event = newEvent;
			this.type = eventType;
		}
	}

	// define private class functions

	private void clear()
	{
		hasEvent = false;
		type = 0;
		event = null;
	}

	private boolean isValidEvent(MouseEvent event)
	{
		try {
			if(event == null) {
				return false;
			}
			if((event.getX() < windowX) || 
					(event.getX() >= (windowX + windowWidth))) {
				return false;
			}
			if((event.getY() < windowY) || 
					(event.getY() >= (windowY + windowHeight))) {
				return false;
			}
		}
		catch(Exception e) {
			System.err.println("mouse driver: " + e.getMessage());
			return false;
		}

		return true;
	}

	// define public class functions

	public MouseDriver(int windowWidth, int windowHeight)
	{
		this(0, 0, windowWidth, windowHeight);
	}

	public MouseDriver(int windowX, int windowY, int windowWidth,
			int windowHeight)
	{
		hasEvent = false;
		type = 0;
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
		this.windowX = windowX;
		this.windowY = windowY;
		event = null;
		eventQueue = new LinkedList();
	}

	public String getClassName()
	{
		return CLASS_NAME;
	}

	public int getCurrentButton()
	{
		return currentButton;
	}

	public void setWindowState(int windowX, int windowY, int windowWidth,
			int windowHeight)
	{
		this.windowX = windowX;
		this.windowY = windowY;
		this.windowWidth = windowWidth;
		this.windowHeight = windowHeight;
	}

	// implement MouseListener interface

	public synchronized void mouseClicked(MouseEvent event)
	{
		try {
			if(isValidEvent(event)) {
				hasEvent = true;
				type = EVENT_CLICKED;
				this.event = event;
				eventQueue.add(new MouseEventWrapper(event, type));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void mouseEntered(MouseEvent event)
	{
		try {
			if(isValidEvent(event)) {
				hasEvent = true;
				type = EVENT_ENTERED;
				this.event = event;
				eventQueue.add(new MouseEventWrapper(event, type));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void mouseExited(MouseEvent event)
	{
		try {
			if(isValidEvent(event)) {
				hasEvent = true;
				type = EVENT_EXITED;
				this.event = event;
				eventQueue.add(new MouseEventWrapper(event, type));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void mousePressed(MouseEvent event)
	{
		try {
			if(isValidEvent(event)) {
				hasEvent = true;
				type = EVENT_PRESSED;
				currentButton = event.getButton();
				this.event = event;
				eventQueue.add(new MouseEventWrapper(event, type));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void mouseReleased(MouseEvent event)
	{
		try {
			if(isValidEvent(event)) {
				hasEvent = true;
				type = EVENT_RELEASED;
				this.event = event;
				eventQueue.add(new MouseEventWrapper(event, type));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	// implement MouseMotionListener interface

	public synchronized void mouseDragged(MouseEvent event)
	{
		try {
			if(isValidEvent(event)) {
				hasEvent = true;
				type = EVENT_DRAGGED;
				this.event = event;
				eventQueue.add(new MouseEventWrapper(event, type));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void mouseMoved(MouseEvent event)
	{
		try {
			if(isValidEvent(event)) {
				hasEvent = true;
				type = EVENT_MOVED;
				this.event = event;
				eventQueue.add(new MouseEventWrapper(event, type));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void mouseWheelMoved(MouseWheelEvent event)
	{
		try {
			if(isValidEvent(event)) {
				hasEvent = true;
				type = EVENT_WHEEL_MOVED;
				this.event = event;
				eventQueue.add(new MouseEventWrapper(event, type));
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized boolean hasEvent()
	{
		if(this.eventQueue.isEmpty()) {
			this.clear();
		}
		return this.hasEvent;
	}

	public synchronized int getEventType()
	{
		return this.type;
	}

	public synchronized MouseEvent getMouseEvent()
	{
		MouseEventWrapper result = null;

		try {
			if(eventQueue.isEmpty()) {
				this.clear();
				return (MouseEvent)null;
			}

			result = (MouseEventWrapper)eventQueue.removeFirst();

			if(result == null) {
				this.clear();
				return (MouseEvent)null;
			}

			if(eventQueue.isEmpty()) {
				hasEvent = false;
			}
			type = result.type;
			event = result.event;
		}
		catch(Exception e) {
			this.clear();
			return (MouseEvent)null;
		}

		return result.event;
	}
}

