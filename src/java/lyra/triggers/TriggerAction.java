/*
 * TriggerAction.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A public interface for defining actions to be taken as a result of a trigger
 * event.
 *
 * Written by Josh English.
 */

// define package space

package lyra.triggers;

// define class

public interface TriggerAction
{
	public String getClassName();

	public String getName();

	public void setName(String name);

	public boolean isActive();

	public void setActive(boolean mode);

	public void toogleActive();

	public int getConsumerCount();

	public boolean hasConsumer(String consumerName);

	public boolean hasConsumer(TriggerConsumer consumer);

	public void registerConsumer(int eventId, TriggerConsumer consumer);

	public void registerConsumer(String eventName, TriggerConsumer consumer);

	public void execute(TriggerEvent event);

	public boolean isTriggered();

	public int getTriggerCount();

	public TriggerConsumer getTriggeredConsumer();
}

