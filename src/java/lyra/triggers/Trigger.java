/*
 * Trigger.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A public interface for managin triggers.
 *
 * Written by Josh English.
 */

// define package space

package lyra.triggers;

// define class

public interface Trigger
{
	public String getClassName();

	public String getName();

	public void setName(String name);

	public int getTriggerCount();

	public boolean isTriggered();

	public int getTriggeredActionCount();

	public int getActionCount();

	public void registerAction(TriggerAction action);

	public void processEvent(TriggerEvent event);

	public void recordEvent(TriggerEvent event);

	public void processRecordedEvents();

	public TriggerAction getTriggeredAction();
}

