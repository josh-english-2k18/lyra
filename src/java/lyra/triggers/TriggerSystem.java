/*
 * TriggerSystem.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A basic implementation of the Trigger interface.
 *
 * Written by Josh English.
 */

// define package space

package lyra.triggers;

// import external packages

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.HashMap;

// define class

public class TriggerSystem implements Trigger
{
	// define private class constants

	private static final String CLASS_NAME = TriggerSystem.class.getName();

	// define protected class variables

	protected boolean isTriggered = false;
	protected int triggerCount = 0;
	protected int triggerActionRef = 0;
	protected int triggerActionCount = 0;
	protected String name = null;
	protected ArrayList actions = null;
	protected LinkedList eventQueue = null;
	protected HashMap triggeredActions = null;

	// define private class functions

	private String buildNewKey()
	{
		Integer integer = null;
		String result = null;

		integer = new Integer(triggerActionCount);
		result = new String(integer.toString());
		triggerActionCount++;

		return result;
	}

	private String buildRefKey()
	{
		Integer integer = null;
		String result = null;

		if(triggerActionRef >= triggerActionCount) {
			return null;
		}

		integer = new Integer(triggerActionRef);
		result = new String(integer.toString());
		triggerActionRef++;

		return result;
	}

	// define public class functions

	public TriggerSystem(String name)
	{
		this.name = name;

		isTriggered = false;
		triggerCount = 0;
		triggerActionRef = 0;
		triggerActionCount = 0;
		actions = new ArrayList();
		eventQueue = new LinkedList();
		triggeredActions = new HashMap();
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

	public int getTriggerCount()
	{
		return triggerCount;
	}

	public boolean isTriggered()
	{
		return isTriggered;
	}

	public int getTriggeredActionCount()
	{
		return triggeredActions.size();
	}

	public int getActionCount()
	{
		return actions.size();
	}

	public void registerAction(TriggerAction action)
	{
		try {
			if(!actions.contains(action)) {
				actions.add(action);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void processEvent(TriggerEvent event)
	{
		Iterator iterator = null;
		TriggerAction action = null;
		String key = null;

		try {
			iterator = actions.iterator();
			while(iterator.hasNext()) {
				action = (TriggerAction)iterator.next();
				if(action == null) {
					break;
				}

				action.execute(event);
				if(action.isTriggered()) {
					key = buildNewKey();
					triggeredActions.put(key, action);
					isTriggered = true;
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void recordEvent(TriggerEvent event)
	{
		try {
			eventQueue.addLast(event);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void processRecordedEvents()
	{
		TriggerEvent event = null;

		try {
			isTriggered = false;
			while(eventQueue.size() > 0) {
				event = (TriggerEvent)eventQueue.pop();
				if(event != null) {
					processEvent(event);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public TriggerAction getTriggeredAction()
	{
		String key = null;
		TriggerAction result = null;

		try {
			key = buildRefKey();
			if(key == null) {
				return null;
			}

			result = (TriggerAction)triggeredActions.remove(key);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}

