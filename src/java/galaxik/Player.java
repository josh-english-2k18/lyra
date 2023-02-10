/*
 * Player.java
 *
 * Copyright (C) 2008 by Joshua S. English. This document is the intellectual
 * property of Joshua S. English. All rights reserved.
 *
 * The Galaxik player definition.
 *
 * Written by Josh English.
 */

// define package space

package galaxik;

// define class

public class Player
{
	// define private class constants

	private static final String CLASS_NAME = Player.class.getName();

	// define public class constants

	public static final int HUMAN = 1;
	public static final int AI = 2;

	// define private class variables

	private boolean isAlive = false;
	private int id = 0;
	private int type = 0;
	private int shipsBuilt = 0;
	private int shipsDestroyed = 0;
	private int shipsLost = 0;
	private int planetsConquered = 0;
	private int planetsLost = 0;
	private int credits = 0;
	private int totalCredits = 0;
	private String name = null;
	private String planetImageName = null;

	// define public class functions

	public Player(int id, int type, String name)
	{
		this.id = id;
		this.type = type;
		this.name = name;

		isAlive = true;
		shipsBuilt = 0;
		shipsDestroyed = 0;
		shipsLost = 0;
		planetsConquered = 0;
		planetsLost = 0;
		credits = 0;
		totalCredits = 0;
		planetImageName = null;
	}

	public int getId()
	{
		return id;
	}

	public int getType()
	{
		return type;
	}

	public String getName()
	{
		return name;
	}

	public boolean isAlive()
	{
		return isAlive;
	}

	public void kill()
	{
		isAlive = false;
	}

	public String getPlanetImageName()
	{
		return planetImageName;
	}

	public void setPlanetImageName(String name)
	{
		planetImageName = name;
	}

	public int getShipsBuilt()
	{
		return shipsBuilt;
	}

	public void addShipsBuilt(int amount)
	{
		shipsBuilt += amount;
	}

	public int getShipsDestroyed()
	{
		return shipsDestroyed;
	}

	public void addShipsDestroyed(int amount)
	{
		shipsDestroyed += amount;
	}

	public int getShipsLost()
	{
		return shipsLost;
	}

	public void addShipsLost(int amount)
	{
		shipsLost += amount;
	}

	public int getPlanetsConquered()
	{
		return planetsConquered;
	}

	public void addPlanetsConquered(int amount)
	{
		planetsConquered += amount;
	}

	public int getPlanetsLost()
	{
		return planetsLost;
	}

	public void addPlanetsLost(int amount)
	{
		planetsLost += amount;
	}

	public int getCredits()
	{
		return credits;
	}

	public void setCredits(int amount)
	{
		if(amount > credits) {
			totalCredits += (amount - credits);
		}
		credits = amount;
	}

	public int getCreditsProduced()
	{
		return totalCredits;
	}
}

