/*
 * Ship.java
 *
 * Copyright (C) 2008 by Joshua S. English. This document is the intellectual
 * property of Joshua S. English. All rights reserved.
 *
 * The Galaxik ship definition.
 *
 * Written by Josh English.
 */

// define package space

package galaxik;

// define class

public class Ship
{
	// define private class constants

	private static final String CLASS_NAME = Ship.class.getName();

	// define public class constants

	public static final int MIN_PRODUCTION = 8;
	public static final int MAX_PRODUCTION = 16;
	public static final int MIN_HEALTH = 0;
	public static final int MAX_HEALTH = Technology.LEVELS;

	// define private class variables

	private int health = 0;
	private Technology technology = null;

	// define public class functions

	public Ship()
	{
		health = MAX_HEALTH;
		technology = new Technology();
	}

	public Ship(Technology technology)
	{
		health = MAX_HEALTH;
		this.technology = Technology.copy(technology);
	}

	public int getHealth()
	{
		return health;
	}

	public void setHealth(int amount)
	{
		health = amount;
		if(health < MIN_HEALTH) {
			health = MIN_HEALTH;
		}
	}

	public void heal()
	{
		health = MAX_HEALTH;
	}

	public Technology getTechnology()
	{
		return technology;
	}

	public void setTechnology(Technology technology)
	{
		this.technology = Technology.copy(technology);
	}
}

