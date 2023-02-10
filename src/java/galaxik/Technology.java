/*
 * Technology.java
 *
 * Copyright (C) 2008 by Joshua S. English. This document is the intellectual
 * property of Joshua S. English. All rights reserved.
 *
 * The Galaxik technology definition.
 *
 * Written by Josh English.
 */

// define package space

package galaxik;

// define class

public class Technology
{
	// define private class constants

	private static final String CLASS_NAME = Technology.class.getName();

	private static final int HULL = 0;
	private static final int FRAME = 1;
	private static final int ARMOR = 2;
	private static final int WEAPON = 3;
	private static final int ENGINE = 4;
	private static final int SHIELDS = 5;

	// define public class constants

	public static final int TECHNOLOGIES = 6;
	public static final int LEVELS = 96;
	public static final int LOWEST_LEVEL = 8;
	public static final int LOWEST_COST = 1024;
	public static final double MAX_SPEED = 8.0;
	public static final double COST_FACTOR = 1.6;
	public static final double ATTENUATION = 65.0;

	// define private class variables

	private int technology[] = null;

	// define public class static functions

	public static Technology copy(Technology technology)
	{
		Technology result = null;

		result = new Technology();
		result.setHull(technology.getHull());
		result.setFrame(technology.getFrame());
		result.setArmor(technology.getArmor());
		result.setWeapon(technology.getWeapon());
		result.setEngine(technology.getEngine());
		result.setShields(technology.getShields());

		return result;
	}

	// define public class functions

	public Technology()
	{
		int ii = 0;

		technology = new int[TECHNOLOGIES];
		for(ii = 0; ii < TECHNOLOGIES; ii++) {
			technology[ii] = LOWEST_LEVEL;
		}
	}

	public int getHull()
	{
		return technology[HULL];
	}

	public void setHull(int amount)
	{
		technology[HULL] = amount;
	}

	public int getFrame()
	{
		return technology[FRAME];
	}

	public void setFrame(int amount)
	{
		technology[FRAME] = amount;
	}

	public int getArmor()
	{
		return technology[ARMOR];
	}

	public void setArmor(int amount)
	{
		technology[ARMOR] = amount;
	}

	public int getWeapon()
	{
		return technology[WEAPON];
	}

	public void setWeapon(int amount)
	{
		technology[WEAPON] = amount;
	}

	public int getEngine()
	{
		return technology[ENGINE];
	}

	public void setEngine(int amount)
	{
		technology[ENGINE] = amount;
	}

	public int getShields()
	{
		return technology[SHIELDS];
	}

	public void setShields(int amount)
	{
		technology[SHIELDS] = amount;
	}

	public void reset()
	{
		int ii = 0;

		for(ii = 0; ii < TECHNOLOGIES; ii++) {
			technology[ii] = LOWEST_LEVEL;
		}
	}

	public int getWeaponDamage()
	{
		int frame = 0;
		int result = 0;

		frame = (int)((double)technology[FRAME] * (ATTENUATION / 100.0));
		if(frame < 1) {
			frame = 1;
		}
		result = (technology[WEAPON] + frame);
		if(result < 1) {
			result = 1;
		}

		return result;
	}

	public int getDefenseDamage()
	{
		int shields = 0;
		int armor = 0;
		int result = 0;

		shields = (int)((double)technology[SHIELDS] * (ATTENUATION / 100.0));
		if(shields < 1) {
			shields = 1;
		}

		armor = (int)((double)technology[ARMOR] * (ATTENUATION / 100.0));
		if(armor < 1) {
			armor = 1;
		}

		result = (shields + armor);
		if(result < 1) {
			result = 1;
		}

		return result;
	}

	public double getSpeed()
	{
		double hull = 0.0;
		double level = 0.0;
		double increment = 0.0;
		double result = 0.0;

		hull = ((double)technology[HULL] * (ATTENUATION / 100.0));
		if(hull < (double)LOWEST_LEVEL) {
			hull = (double)LOWEST_LEVEL;
		}
		level = ((hull + (double)technology[ENGINE]) / 2.0);

		increment = ((double)LEVELS / MAX_SPEED);
		result = (level / increment);
		if(result < 1.0) {
			result = 1.0;
		}
		else if(result > MAX_SPEED) {
			result = MAX_SPEED;
		}

		return result;
	}

	public void upgrade(Technology technology)
	{
		if(technology.getHull() > this.getHull()) {
			this.setHull(technology.getHull());
		}
		if(technology.getFrame() > this.getFrame()) {
			this.setFrame(technology.getFrame());
		}
		if(technology.getArmor() > this.getArmor()) {
			this.setArmor(technology.getArmor());
		}
		if(technology.getWeapon() > this.getWeapon()) {
			this.setWeapon(technology.getWeapon());
		}
		if(technology.getEngine() > this.getEngine()) {
			this.setEngine(technology.getEngine());
		}
		if(technology.getShields() > this.getShields()) {
			this.setShields(technology.getShields());
		}
	}

	public int defaultUpgrade(int credits)
	{
		int ii = 0;
		int ref = 0;
		int levels = 0;
		int cost = 0;

		for(ii = 0; ii < TECHNOLOGIES; ii++) {
			if(ii < (TECHNOLOGIES - 1)) {
				if(technology[ii] < technology[(ii + 1)]) {
					ref = ii;
					break;
				}
			}
			else {
				if(technology[ii] < technology[0]) {
					ref = ii;
					break;
				}
			}
		}

		if(technology[ref] >= LEVELS) {
			return credits;
		}

		levels = (int)(technology[ref] - LOWEST_LEVEL);
		cost = ((int)((double)(LOWEST_COST * levels) * COST_FACTOR) +
				LOWEST_COST);

		if(cost > credits) {
			return credits;
		}

		technology[ref] += 1;
		if(technology[ref] > LEVELS) {
			technology[ref] = LEVELS;
		}
		credits -= cost;

		return credits;
	}
}

