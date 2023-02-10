/*
 * TriggerEventImpl.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A basic implementation of the TriggerEvent interface.
 *
 * Written by Josh English.
 */

// define package space

package lyra.triggers;

// import external packages

import java.util.Date;

// define class

public class TriggerEventImpl implements TriggerEvent
{
	// define private class constants

	private static final String CLASS_NAME = TriggerEventImpl.class.getName();

	// define protected class variables

	protected int id = 0;
	protected long eventTime = 0;
	protected String name = null;
	protected Object context = null;

	// define public class functions

	public TriggerEventImpl(int id, String name)
	{
		this.id = id;
		this.name = name;

		eventTime = (new Date()).getTime();
		context = null;
	}

	public TriggerEventImpl(int id, String name, Object context)
	{
		this.id = id;
		this.name = name;
		this.context = context;

		eventTime = (new Date()).getTime();
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

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public long getWhen()
	{
		return eventTime;
	}

	public Object getContext()
	{
		return context;
	}

	public void setContext(Object context)
	{
		this.context = context;
	}
}

