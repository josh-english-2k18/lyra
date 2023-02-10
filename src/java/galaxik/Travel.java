/*
 * Travel.java
 *
 * Copyright (C) 2008 by Joshua S. English. This document is the intellectual
 * property of Joshua S. English. All rights reserved.
 *
 * The Galaxik travel definition.
 *
 * Written by Josh English.
 */

// define package space

package galaxik;

// define class

public class Travel
{
	// define private class constants

	private static final String CLASS_NAME = Travel.class.getName();

	// define private class variables

	private int ownerId = 0;
	private int originId = 0;
	private int destinationId = 0;
	private int days = 0;
	private Fleet fleet = null;

	// define public class functions

	public Travel(int ownerId, int originId, int destinationId, int days,
			Fleet fleet)
	{
		this.ownerId = ownerId;
		this.originId = originId;
		this.destinationId = destinationId;
		this.days = days;
		this.fleet = fleet;
	}

	public int getOwnerId()
	{
		return ownerId;
	}

	public int getOriginId()
	{
		return originId;
	}

	public int getDestinationId()
	{
		return destinationId;
	}

	public int getDays()
	{
		return days;
	}

	public Fleet getFleet()
	{
		return fleet;
	}

	public void process()
	{
		days--;
		if(days < 0) {
			days = 0;
		}
	}

	public boolean hasArrived()
	{
		if(days == 0) {
			return true;
		}
		return false;
	}
}

