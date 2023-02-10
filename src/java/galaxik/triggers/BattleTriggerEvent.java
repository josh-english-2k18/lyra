/*
 * BattleTriggerEvent.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A Galaxik battle event representation.
 *
 * Written by Josh English.
 */

// define package space

package galaxik.triggers;

// import external packages

import java.util.Date;

// import lyra game engine packages

import lyra.triggers.TriggerEvent;

// import interal packages

import galaxik.*;

// define class

public class BattleTriggerEvent implements TriggerEvent
{
	// define private class constants

	private static final String CLASS_NAME = BattleTriggerEvent.class.getName();

	// define public class constants

	public static final int EVENT_ID = 1;
	public static final String EVENT_NAME = "BattleEvent";

	// define protected class variables

	protected int id = 0;
	protected long eventTime = 0;
	protected String name = null;
	protected Object context = null;

	// define public class functions

	public BattleTriggerEvent(Planet planet, Player playerWin,
			Player playerLost)
	{
		this.id = EVENT_ID;
		this.name = EVENT_NAME;

		eventTime = (new Date()).getTime();
		context = new BattleTriggerContext(planet, playerWin, playerLost);
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

