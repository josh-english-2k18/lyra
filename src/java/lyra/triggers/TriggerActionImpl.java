/*
 * TriggerActionImpl.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A basic implementation of the TriggerAction interface.
 *
 * Written by Josh English.
 */

// define package space

package lyra.triggers;

// import external packages

import java.util.HashMap;
import java.util.LinkedList;

// define class

public class TriggerActionImpl implements TriggerAction
{
	// define private class constants

	private static final String CLASS_NAME = TriggerActionImpl.class.getName();

	// define protected class variables

	protected boolean isActive = false;
	protected boolean isTriggered = false;
	protected int triggerCount = 0;
	protected String name = null;
	protected HashMap eventIndex = null;
	protected HashMap consumers = null;
	protected LinkedList triggeredConsumers = null;

	// define public class functions

	public TriggerActionImpl(String name)
	{
		this.name = name;

		isActive = true;
		isTriggered = false;
		triggerCount = 0;
		eventIndex = new HashMap();
		consumers = new HashMap();
		triggeredConsumers = new LinkedList();
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

	public boolean isActive()
	{
		return isActive;
	}

	public void setActive(boolean mode)
	{
		isActive = mode;
	}

	public void toogleActive()
	{
		if(isActive) {
			isActive = false;
		}
		else {
			isActive = true;
		}
	}

	public int getConsumerCount()
	{
		return consumers.size();
	}

	public boolean hasConsumer(String consumerName)
	{
		boolean result = false;

		try {
			if(consumers.get(consumerName) != null) {
				result = true;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public boolean hasConsumer(TriggerConsumer consumer)
	{
		boolean result = false;

		if(consumers.get(consumer.getName()) != null) {
			result = true;
		}

		return result;
	}

	public void registerConsumer(int eventId, TriggerConsumer consumer)
	{
		try {
			eventIndex.put(eventId, consumer.getName());
			if(!hasConsumer(consumer)) {
				consumers.put(consumer.getName(), consumer);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void registerConsumer(String eventName, TriggerConsumer consumer)
	{
		try {
			eventIndex.put(eventName, consumer.getName());
			if(!hasConsumer(consumer)) {
				consumers.put(consumer.getName(), consumer);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void execute(TriggerEvent event)
	{
		String name = null;
		TriggerConsumer consumer = null;

		try {
			name = (String)eventIndex.get(event.getId());
			if(name == null) {
				name = (String)eventIndex.get(event.getName());
				if(name == null) {
					return;
				}
			}
			consumer = (TriggerConsumer)consumers.get(name);
			if(consumer == null) {
				return;
			}
			consumer.consume(event);
			triggeredConsumers.addLast(consumer);
			isTriggered = true;
			triggerCount++;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isTriggered()
	{
		boolean result = false;

		if(isTriggered) {
			result = true;
			isTriggered = false;
		}

		return result;
	}

	public int getTriggerCount()
	{
		return triggerCount;
	}

	public TriggerConsumer getTriggeredConsumer()
	{
		TriggerConsumer result = null;

		try {
			result = (TriggerConsumer)triggeredConsumers.pop();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}

