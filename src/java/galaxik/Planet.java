/*
 * Planet.java
 *
 * Copyright (C) 2008 by Joshua S. English. This document is the intellectual
 * property of Joshua S. English. All rights reserved.
 *
 * The Galaxik planet definition.
 *
 * Written by Josh English.
 */

// define package space

package galaxik;

// import external packages

import java.util.Iterator;

// define class

public class Planet
{
	// define private class constants

	private static final String CLASS_NAME = Planet.class.getName();

	private static final String PLANET_NAMES[] = {
			"Aakashshah",
			"Aavasaksa",
			"Aberdonia",
			"Bergengruen",
			"Bettarini",
			"Correggia",
			"Cupressus",
			"Dalgarno",
			"Degas",
			"Dimashchegolev",
			"Etheridgea",
			"Eurykleia",
			"Falcinelli",
			"Feiniqu",
			"Filipportera",
			"Galilea",
			"Gedania",
			"Hannadam",
			"Heimdal",
			"Ilmatar",
			"Jurijvega",
			"Kanetugu",
			"Langevin",
			"Mahakala",
			"Niinoama",
			"Okutama",
			"Patroclus",
			"Qinghai",
			"Reighard",
			"Sandvicensis",
			"Talthybius",
			"Univinnitsa",
			"Valpertile",
			"Werfel",
			"Xizang",
			"Yatsugatake",
			"Zaragoza",
	};

	private static final String PLANET_SUFFIXES[] = {
			"I",
			"II",
			"III",
			"IV",
			"V",
			"Prime",
			"Primus",
			"Secondus",
			"Alpha",
			"Beta",
			"Gamma",
			"Delta",
	};

	// define public class constants

	public static final int MIN_CREDITS_PRODUCTION = 8192;
	public static final int MAX_CREDITS_PRODUCTION = 131072;

	// define private class variables

	private boolean playerTurnOver = false;
	private boolean playerSelected = false;
	private boolean playerAutoBuild = false;
	private boolean playerAutoUpgrade = false;
	private int id = 0;
	private int ownerId = 0;
	private int x = 0;
	private int y = 0;
	private int shipProduction = 0;
	private int creditsProduction = 0;
	private int credits = 0;
	private String name = null;
	private String imageName = null;
	private Technology technology = null;
	private Fleet fleet = null;

	// define public class static functions

	public static String generatePlanetName()
	{
		String name = null;
		String suffix = null;
		String result = null;

		name = PLANET_NAMES[((int)(Math.random() * 10000.0) %
				PLANET_NAMES.length)];
		suffix = PLANET_SUFFIXES[((int)(Math.random() * 10000.0) %
				PLANET_SUFFIXES.length)];

		result = new String(name + " " + suffix);

		return result;
	}

	// define public class functions

	public Planet(int id, int ownerId, int x, int y, String imageName)
	{
		this.id = id;
		this.ownerId = ownerId;
		this.x = x;
		this.y = y;
		this.imageName = imageName;

		playerTurnOver = false;
		playerSelected = false;
		playerAutoBuild = false;
		playerAutoUpgrade = false;
		shipProduction = (((int)(Math.random() * 10000.0) %
					(Ship.MAX_PRODUCTION - Ship.MIN_PRODUCTION)) +
				Ship.MIN_PRODUCTION);
		creditsProduction = (((int)(Math.random() * 10000.0) %
					(MAX_CREDITS_PRODUCTION - MIN_CREDITS_PRODUCTION)) +
				MIN_CREDITS_PRODUCTION);
		credits = 0;
		name = null;
		technology = new Technology();
		fleet = new Fleet(ownerId);
	}

	public int getId()
	{
		return id;
	}

	public int getOwnerId()
	{
		return ownerId;
	}

	public void setOwnerId(int id)
	{
		ownerId = id;
		fleet.setOwnerId(id);
	}

	public String getImageName()
	{
		return imageName;
	}

	public void setImageName(String imageName)
	{
		this.imageName = imageName;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getShipProduction()
	{
		return shipProduction;
	}

	public void setStandardShipProduction()
	{
		shipProduction = (int)((double)(Ship.MIN_PRODUCTION +
					Ship.MAX_PRODUCTION) / 2.0);
	}

	public int getCreditsProduction()
	{
		return creditsProduction;
	}

	public void setStandardCreditsProduction()
	{
		creditsProduction = (int)((double)(MIN_CREDITS_PRODUCTION +
					MAX_CREDITS_PRODUCTION) / 2.0);
	}

	public int getCredits()
	{
		return credits;
	}

	public void setCredits(int amount)
	{
		credits = amount;
	}

	public Technology getTechnology()
	{
		return technology;
	}

	public Fleet getFleet()
	{
		return fleet;
	}

	public void beginPlayerTurn()
	{
		playerTurnOver = false;
		playerSelected = false;
	}

	public void setSelected()
	{
		playerSelected = true;
	}

	public boolean wasSelected()
	{
		return playerSelected;
	}

	public boolean turnOver()
	{
		return playerTurnOver;
	}

	public boolean autoBuild()
	{
		return playerAutoBuild;
	}

	public void setAutoBuild(boolean mode)
	{
		playerAutoBuild = mode;
	}

	public boolean autoUpgrade()
	{
		return playerAutoUpgrade;
	}

	public void setAutoUpgrade(boolean mode)
	{
		playerAutoUpgrade = mode;
	}

	public void process()
	{
		int money = 0;
		int ships = 0;

		credits += creditsProduction;

		if(ownerId == Game.NEUTRAL_PLANET_ID) {
			money = technology.defaultUpgrade(credits);
			while(money != credits) {
				credits = money;
				money = technology.defaultUpgrade(credits);
			}
			ships = (shipProduction - fleet.getShipCount());
			fleet.addShips(technology, ships);
		}
	}

	public int upgradeTechnology(int playerCredits)
	{
		int money = 0;

		money = technology.defaultUpgrade(playerCredits);
		while(money < playerCredits) {
			playerCredits = money;
			money = technology.defaultUpgrade(playerCredits);
		}

		return playerCredits;
	}

	public int processForAiPlayerWithDifficulty(int difficultyLevel,
			boolean performUpgrade, int playerCredits)
	{
		int money = 0;
		int counter = 0;
		int localProduction = 0;

		if(performUpgrade) {
			if(difficultyLevel == Game.DIFFICULTY_HARD) {
				playerCredits *= 2;
			}
			else if(difficultyLevel == Game.DIFFICULTY_INSANE) {
				playerCredits *= 4;
			}
			money = technology.defaultUpgrade(playerCredits);
			counter++;
			while(money != playerCredits) {
				playerCredits = money;
				if((difficultyLevel == Game.DIFFICULTY_EASY) &&
						(counter > 0)) {
					break;
				}
				else if((difficultyLevel == Game.DIFFICULTY_NORMAL) &&
						(counter > 16)) {
					break;
				}
				else if((difficultyLevel == Game.DIFFICULTY_HARD) &&
						(counter > 32)) {
					break;
				}
				money = technology.defaultUpgrade(playerCredits);
				counter++;
			}
		}

		if(difficultyLevel == Game.DIFFICULTY_EASY) {
			localProduction = (int)((double)shipProduction * 0.5);
		}
		else if(difficultyLevel == Game.DIFFICULTY_NORMAL) {
			localProduction = (int)((double)shipProduction * 1.0);
		}
		else if(difficultyLevel == Game.DIFFICULTY_HARD) {
			localProduction = (int)((double)shipProduction * 1.2);
		}
		else if(difficultyLevel == Game.DIFFICULTY_INSANE) {
			localProduction = (int)((double)shipProduction * 1.4);
		}
		else {
			localProduction = shipProduction;
		}

		fleet.addShips(technology, localProduction);

		playerTurnOver = true;

		return playerCredits;
	}

	public int processForPlayer(boolean performUpgrade, int playerCredits)
	{
		int money = 0;

		if(performUpgrade) {
			money = technology.defaultUpgrade(playerCredits);
			while(money != playerCredits) {
				playerCredits = money;
				money = technology.defaultUpgrade(playerCredits);
			}
		}

		fleet.addShips(technology, shipProduction);

		playerTurnOver = true;

		return playerCredits;
	}

	public void dockFleet(Fleet dockFleet)
	{
		Iterator iterator = null;
		Ship ship = null;

		try {
			iterator = dockFleet.getShips().iterator();
			while(iterator.hasNext()) {
				ship = (Ship)iterator.next();
				if(ship == null) {
					break;
				}

				fleet.addShips(ship.getTechnology(), 1);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

