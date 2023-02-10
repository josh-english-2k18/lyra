/*
 * BattleTriggerConsumer.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * The Galaxik battle trigger consumer.
 *
 * Written by Josh English.
 */

// define package space

package galaxik.triggers;

// import external packages

import java.util.LinkedList;

// import lyra game engine packages

import lyra.triggers.TriggerEvent;
import lyra.triggers.TriggerConsumer;

// define class

public class BattleTriggerConsumer implements TriggerConsumer
{
	// define private class constants

	private static final String CLASS_NAME =
		BattleTriggerConsumer.class.getName();

	// define protected class variables

	protected int consumeCount = 0;
	protected String name = null;
	protected LinkedList eventQueue = null;

	// define public class functions

	public BattleTriggerConsumer(String name)
	{
		this.name = name;

		consumeCount = 0;
		eventQueue = new LinkedList();
	}

	public String getClassName()
	{
		return CLASS_NAME;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getConsumations()
	{
		return consumeCount;
	}

	public void consume(TriggerEvent event)
	{
		try {
			eventQueue.addLast(event);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public BattleTriggerContext pop()
	{
		BattleTriggerEvent event = null;
		BattleTriggerContext result = null;

		try {
			if(eventQueue.size() > 0) {
				event = (BattleTriggerEvent)eventQueue.pop();
				if(event != null) {
					result = (BattleTriggerContext)event.getContext();
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}

