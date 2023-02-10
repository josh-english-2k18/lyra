/*
 * Fleet.java
 *
 * Copyright (C) 2008 by Joshua S. English. This document is the intellectual
 * property of Joshua S. English. All rights reserved.
 *
 * The Galaxik fleet definition.
 *
 * Written by Josh English.
 */

// define package space

package galaxik;

// import external packages

import java.util.ArrayList;
import java.util.Iterator;

// define class

public class Fleet
{
	// define private class constants

	private static final String CLASS_NAME = Fleet.class.getName();

	// define public class constants

	public static final int MIN_PRODUCTION = 8;
	public static final int MAX_PRODUCTION = 16;
	public static final int MIN_HEALTH = 0;
	public static final int MAX_HEALTH = 128;

	// define private class variables

	private int ownerId = 0;
	private Technology technology = null;
	private ArrayList ships = null;

	// define public class functions

	public Fleet(int ownerId)
	{
		this.ownerId = ownerId;

		technology = new Technology();
		ships = new ArrayList();
	}

	public int getOwnerId()
	{
		return ownerId;
	}

	public void setOwnerId(int id)
	{
		ownerId = id;
	}

	public void addShip(Ship ship)
	{
		technology.upgrade(ship.getTechnology());
		ships.add(ship);
	}

	public void addShips(Technology technology, int number)
	{
		int ii = 0;

		Ship ship = null;

		for(ii = 0; ii < number; ii++) {
			ship = new Ship(technology);
			addShip(ship);
		}
	}

	public void removeShip(Ship ship)
	{
		ships.remove(ship);
	}

	public int getShipCount()
	{
		return ships.size();
	}

	public ArrayList getShips()
	{
		return ships;
	}

	public void reset()
	{
		technology.reset();
		ships.clear();
	}

	public Technology getTechnology()
	{
		return technology;
	}

	public Fleet divide(int number)
	{
		Iterator iterator = null;
		Ship ship = null;
		Fleet result = null;

		try {
			if((number < 0) || (number > ships.size())) {
				return null;
			}

			result = new Fleet(ownerId);

			iterator = ships.iterator();
			while(iterator.hasNext()) {
				ship = (Ship)iterator.next();
				if(ship == null) {
					break;
				}

				result.addShip(ship);
				if(result.getShipCount() >= number) {
					break;
				}
			}

			iterator = result.getShips().iterator();
			while(iterator.hasNext()) {
				ship = (Ship)iterator.next();
				if(ship == null) {
					break;
				}

				ships.remove(ship);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}

