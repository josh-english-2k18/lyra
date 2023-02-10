/*
 * TriggerConsumer.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A public interface for defining a class to handle the processing of an event
 * from an action.
 *
 * Written by Josh English.
 */

// define package space

package lyra.triggers;

// define class

public interface TriggerConsumer
{
	public String getClassName();

	public String getName();

	public void setName(String name);

	public int getConsumations();

	public void consume(TriggerEvent event);
}

