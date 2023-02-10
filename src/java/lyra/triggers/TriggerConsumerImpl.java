/*
 * TriggerConsumerImpl.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A basic implementation of the TriggerConsumer interface.
 *
 * Written by Josh English.
 */

// define package space

package lyra.triggers;

// define class

public class TriggerConsumerImpl implements TriggerConsumer
{
	// define private class constants

	private static final String CLASS_NAME =
		TriggerConsumerImpl.class.getName();

	// define protected class variables

	protected int consumeCount = 0;
	protected String name = null;

	// define public class functions

	public TriggerConsumerImpl(String name)
	{
		this.name = name;

		consumeCount = 0;
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
		if(event != null) {
			System.out.println("{" + CLASS_NAME + "}{" + name +
					"} consumed event #" + event.getId() + ", named '" +
					event.getName() + "'");
		}
		else {
			System.out.println("{" + CLASS_NAME + "}{" + name +
					"} unable to consume NULL event");
		}
	}
}

