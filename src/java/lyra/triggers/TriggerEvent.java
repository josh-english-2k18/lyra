/*
 * TriggerEvent.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A public interface for defining events to trigger an action.
 *
 * Written by Josh English.
 */

// define package space

package lyra.triggers;

// define class

public interface TriggerEvent
{
	public String getClassName();

	public String getName();

	public void setName(String name);

	public int getId();

	public void setId(int id);

	public long getWhen();

	public Object getContext();

	public void setContext(Object context);
}

