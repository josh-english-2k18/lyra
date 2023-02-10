/*
 * Game.java
 *
 * Copyright (C) 2008 by Joshua S. English. This document is the intellectual
 * property of Joshua S. English. All rights reserved.
 *
 * The Galaxik game logic.
 *
 * Written by Josh English.
 */

// define package space

package galaxik;

// import external packages

import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.Color;

// import lyra game engine packages

import lyra.*;
import lyra.triggers.TriggerSystem;

// import interal packages

import galaxik.widgets.ScrollDisplay;
import galaxik.triggers.BattleTriggerEvent;

// define class

public class Game
{
	// define private class constants

	private static final String CLASS_NAME = Game.class.getName();

	private static final String AI_NAMES[] = {
			"Ozymandius",
			"Holmes",
			"Mycroft",
			"Cerebrus"
	};

	private static final String AI_SUFFIXES[] = {
			"",
			"IX",
			"X",
			"IV",
			"Prime"
	};

	// define public class constants

	public static final int NEUTRAL_PLANET_ID = -1;

	public static final int DIFFICULTY_EASY = 0;
	public static final int DIFFICULTY_NORMAL = 1;
	public static final int DIFFICULTY_HARD = 2;
	public static final int DIFFICULTY_INSANE = 3;

	// define private class variables

	private int planetSizeX = 0;
	private int planetSizeY = 0;
	private int playerIdRef = 0;
	private int difficultyLevel = 0;
	private String planetImageNames[];
	private ArrayList travels = null;
	private ArrayList planets = null;
	private HashMap planetIdIndex = null;
	private HashMap planetNameIndex = null;
	private ArrayList players = null;
	private HashMap playerIdIndex = null;
	private HashMap playerNameIndex = null;
	private ArrayList ais = null;
	private HashMap aiIdIndex = null;
	private HashMap aiNameIndex = null;
	private Galaxik galaxik = null;

	// define public class functions

	public Game(int pixelWidth, int pixelHeight, int planetPixels,
			String planetImageNames[], Galaxik galaxik)
	{
		int x = 0;
		int y = 0;
		int ii = 0;
		int nn = 0;
		int pick = 0;
		int limit = 0;
		int planetCount = 0;
		int board[][] = null;

		String planetName = null;
		String imageName = null;
		Planet planet = null;

		// set defaults

		planetSizeX = 0;
		planetSizeY = 0;
		playerIdRef = 0;
		difficultyLevel = DIFFICULTY_NORMAL;
		this.planetImageNames = null;
		travels = null;
		planets = null;
		planetIdIndex = null;
		planetNameIndex = null;
		players = null;
		playerIdIndex = null;
		playerNameIndex = null;
		this.galaxik = galaxik;

		// setup travels

		travels = new ArrayList();

		// construct planets

		this.planetImageNames = planetImageNames;

		planetSizeX = (pixelWidth / planetPixels);
		planetSizeY = (pixelHeight / planetPixels);

		limit = (planetSizeX * planetSizeY);
		planetCount = (((int)(Math.random() * 10000.0) % limit) + 1);
		while((planetCount < (limit / 64)) ||
				(planetCount > (limit / 8))) {
			planetCount = (((int)(Math.random() * 10000.0) % limit) + 1);
		}

		board = new int[planetSizeX][planetSizeY];
		for(ii = 0; ii < planetSizeX; ii++) {
			for(nn = 0; nn < planetSizeY; nn++) {
				board[ii][nn] = 0;
			}
		}

		planets = new ArrayList();
		planetIdIndex = new HashMap();
		planetNameIndex = new HashMap();

		for(ii = 0; ii < planetCount; ii++) {
			x = ((int)(Math.random() * 10000.0) % planetSizeX);
			y = ((int)(Math.random() * 10000.0) % planetSizeY);
			while(board[x][y] == 1) {
				x = ((int)(Math.random() * 10000.0) % planetSizeX);
				y = ((int)(Math.random() * 10000.0) % planetSizeY);
			}
			board[x][y] = 1;
			pick = (((int)(Math.random() * 10000.0) %
						(planetImageNames.length - 1)) + 1);
			imageName = planetImageNames[pick];
			planet = new Planet(ii, NEUTRAL_PLANET_ID, x, y, imageName);
			planets.add(planet);

			planetName = Planet.generatePlanetName();
			while(planetNameIndex.get(planetName) != null) {
				planetName = Planet.generatePlanetName();
			}
			planet.setName(planetName);

			planetIdIndex.put(planet.getId(), planet);
			planetNameIndex.put(planet.getName(), planet);
		}

		// setup players

		players = new ArrayList();
		playerIdIndex = new HashMap();
		playerNameIndex = new HashMap();

		// setup AI's

		ais = new ArrayList();
		aiIdIndex = new HashMap();
		aiNameIndex = new HashMap();
	}

	public static double calculateDistance(Planet alpha, Planet beta)
	{
		double result = 0.0;

		result = Math.sqrt(
				(((double)beta.getX() - (double)alpha.getX()) *
				 ((double)beta.getX() - (double)alpha.getX())) +
				(((double)beta.getY() - (double)alpha.getY()) *
				 ((double)beta.getY() - (double)alpha.getY())));

		return result;
	}

	public void setDifficultyLevel(int level)
	{
		if((level < 0) || (level > DIFFICULTY_INSANE)) {
			return;
		}
		difficultyLevel = level;
	}

	public int getPlanetCount()
	{
		return planets.size();
	}

	public ArrayList getPlanets()
	{
		return planets;
	}

	public Planet getPlanet(int id)
	{
		return (Planet)planetIdIndex.get(id);
	}

	public Planet getPlanet(String name)
	{
		return (Planet)planetNameIndex.get(name);
	}

	public void processPlanets()
	{
		Iterator iterator = null;
		Planet planet = null;
		Player player = null;

		iterator = planets.iterator();
		while(iterator.hasNext()) {
			planet = (Planet)iterator.next();
			if(planet == null) {
				break;
			}

			planet.beginPlayerTurn();
			planet.process();

			if(planet.getOwnerId() != NEUTRAL_PLANET_ID) {
				player = (Player)playerIdIndex.get(planet.getOwnerId());
				player.setCredits(player.getCredits() + planet.getCredits());
				planet.setCredits(0);
			}
		}
	}

	public void addPlayer(int type, String name)
	{
		boolean isTaken = false;
		int pick = 0;
		int counter = 0;

		String suffix = null;
		Player player = null;
		Player otherPlayer = null;
		Planet planet = null;
		GameAIOne ai = null;
		Iterator iterator = null;

		if((type != Player.HUMAN) && (type != Player.AI)) {
			return;
		}

		if(type == Player.AI) {
			name = AI_NAMES[((int)(Math.random() * 10000.0) %
					AI_NAMES.length)];
			suffix = AI_SUFFIXES[((int)(Math.random() * 10000.0) %
					AI_SUFFIXES.length)];
			name = new String(name + " " + suffix);
			while(aiNameIndex.get(name) != null) {
				name = AI_NAMES[((int)(Math.random() * 10000.0) %
						AI_NAMES.length)];
				suffix = AI_SUFFIXES[((int)(Math.random() * 10000.0) %
						AI_SUFFIXES.length)];
				name = new String(name + " " + suffix);
			}
		}

		player = new Player(playerIdRef, type, name);
		playerIdIndex.put(player.getId(), player);
		playerNameIndex.put(name, player);
		playerIdRef++;

		pick = ((int)(Math.random() * 10000.0) % planets.size());
		planet = (Planet)planets.get(pick);
		while(planet.getOwnerId() != NEUTRAL_PLANET_ID) {
			pick = ((int)(Math.random() * 10000.0) % planets.size());
			planet = (Planet)planets.get(pick);
		}

		planet.setOwnerId(player.getId());
		planet.setStandardShipProduction();
		planet.setStandardCreditsProduction();

		if(type == Player.HUMAN) {
			planet.setImageName(planetImageNames[0]);
			player.setPlanetImageName(planetImageNames[0]);
		}
		else {
			counter = 0;
			do {
				pick = (((int)(Math.random() * 10000.0) %
							(planetImageNames.length - 1)) + 1);

				isTaken = false;
				iterator = players.iterator();
				while(iterator.hasNext()) {
					otherPlayer = (Player)iterator.next();
					if(player == null) {
						break;
					}

					if(otherPlayer.getPlanetImageName().equals(
								planetImageNames[pick])) {
						isTaken = true;
						break;
					}
				}

				counter++;
			} while((isTaken) && (counter < 8));

			planet.setImageName(planetImageNames[pick]);
			player.setPlanetImageName(planetImageNames[pick]);
		}

		player.addPlanetsConquered(1);

		if(type == Player.AI) {
			ai = new GameAIOne(player.getId(), player, this);
			ais.add(ai);
			aiIdIndex.put(ai.getId(), ai);
			aiNameIndex.put(ai.getPlayer().getName(), ai);
		}

		players.add(player);
	}

	public ArrayList getPlayers()
	{
		return players;
	}

	public Player getPlayer(int id)
	{
		return (Player)playerIdIndex.get(id);
	}

	public Player getPlayer(String name)
	{
		return (Player)playerNameIndex.get(name);
	}

	public void upgradePlayerPlanet(String name, int credits)
	{
		int remainder = 0;

		Planet planet = null;
		Player player = null;

		planet = (Planet)planetNameIndex.get(name);
		player = (Player)playerIdIndex.get(planet.getOwnerId());

		if(credits > player.getCredits()) {
			credits = player.getCredits();
		}

		remainder = planet.upgradeTechnology(credits);

		player.setCredits(player.getCredits() - (credits - remainder));
	}

	public void processPlayerPlanet(String name, boolean performUpgrade,
			int credits)
	{
		int remainder = 0;

		Planet planet = null;
		Player player = null;

		planet = (Planet)planetNameIndex.get(name);
		player = (Player)playerIdIndex.get(planet.getOwnerId());

		if(credits > player.getCredits()) {
			credits = player.getCredits();
		}

		if(player.getType() == Player.HUMAN) {
			remainder = planet.processForPlayer(performUpgrade, credits);
		}
		else if(player.getType() == Player.AI) {
			remainder = planet.processForAiPlayerWithDifficulty(
					difficultyLevel, performUpgrade, credits);
		}

		player.setCredits(player.getCredits() - (credits - remainder));
		player.addShipsBuilt(planet.getShipProduction());
	}

	public void processAIs(ScrollDisplay scrollDisplay)
	{
		Iterator iterator = null;
		GameAIOne ai = null;

		try {
			iterator = ais.iterator();
			while(iterator.hasNext()) {
				ai = (GameAIOne)iterator.next();
				if(ai == null) {
					break;
				}

				ai.execute(scrollDisplay);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public boolean allAisDead()
	{
		boolean result = true;

		Iterator iterator = null;
		GameAIOne ai = null;

		try {
			iterator = ais.iterator();
			while(iterator.hasNext()) {
				ai = (GameAIOne)iterator.next();
				if(ai == null) {
					break;
				}

				if(ai.getPlayer().isAlive()) {
					result = false;
					break;
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public void updatePlayerState(ScrollDisplay scrollDisplay)
	{
		boolean isFound = false;

		Iterator playerIterator = null;
		Iterator planetIterator = null;
		Iterator travelIterator = null;
		Player player = null;
		Planet planet = null;
		Travel travel = null;

		try {
			playerIterator = players.iterator();
			while(playerIterator.hasNext()) {
				player = (Player)playerIterator.next();
				if(player == null) {
					break;
				}

				if(!player.isAlive()) {
					continue;
				}

				isFound = false;
				planetIterator = planets.iterator();
				while(planetIterator.hasNext()) {
					planet = (Planet)planetIterator.next();
					if(planet == null) {
						break;
					}

					if(planet.getOwnerId() == player.getId()) {
						isFound = true;
						break;
					}
				}

				if(isFound) {
					continue;
				}

				isFound = false;
				travelIterator = travels.iterator();
				while(travelIterator.hasNext()) {
					travel = (Travel)travelIterator.next();
					if(travel == null) {
						break;
					}

					if(travel.getOwnerId() == player.getId()) {
						isFound = true;
						break;
					}
				}

				if(!isFound) {
					player.kill();
					scrollDisplay.log(Color.blue,
							"Player '" + player.getName() +
							"' has been destroyed");
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void addTravel(Travel travel)
	{
		travels.add(travel);
	}

	public static Fleet fightFleets(Fleet alpha, Fleet beta)
	{
		int offense = 0;
		int defense = 0;
		int damage = 0;

		Iterator iterator = null;
		Iterator alphaIterator = null;
		Iterator betaIterator = null;
		Ship ship = null;
		Ship alphaShip = null;
		Ship betaShip = null;
		Fleet winner = null;

		try {
			while((alpha.getShipCount() > 0) && (beta.getShipCount() > 0)) {
				alphaIterator = alpha.getShips().iterator();
				while(alphaIterator.hasNext()) {
					alphaShip = (Ship)alphaIterator.next();
					break;
				}

				betaIterator = beta.getShips().iterator();
				while(betaIterator.hasNext()) {
					betaShip = (Ship)betaIterator.next();
					break;
				}
//System.out.println("    ...a " + alphaShip.getTechnology().getWeaponDamage() + ", " + alphaShip.getTechnology().getDefenseDamage() + " at " + alphaShip.getHealth() + " vs b " + betaShip.getTechnology().getWeaponDamage() + ", " + betaShip.getTechnology().getDefenseDamage() + " at " + betaShip.getHealth());

				while((alphaShip.getHealth() > Ship.MIN_HEALTH) &&
						(betaShip.getHealth() > Ship.MIN_HEALTH)) {
					// ship alpha attacks ship beta

					offense = alphaShip.getTechnology().getWeaponDamage();
					defense = betaShip.getTechnology().getDefenseDamage();

					damage = (offense - defense);
					if(damage < 1) {
						damage = 1;
					}
					else if(damage > 8) {
						damage = 8;
					}

					if((alphaShip.getTechnology().getHull() >
								betaShip.getTechnology().getHull()) &&
							(alphaShip.getTechnology().getFrame() >
							 betaShip.getTechnology().getFrame()) &&
							(alphaShip.getTechnology().getEngine() >
							 betaShip.getTechnology().getEngine())) {
						damage *= 2;
					}

					betaShip.setHealth(betaShip.getHealth() - damage);

					// ship beta attacks ship alpha

					offense = betaShip.getTechnology().getWeaponDamage();
					defense = alphaShip.getTechnology().getDefenseDamage();

					damage = (offense - defense);
					if(damage < 1) {
						damage = 1;
					}
					else if(damage > 8) {
						damage = 8;
					}

					if((betaShip.getTechnology().getHull() >
								alphaShip.getTechnology().getHull()) &&
							(betaShip.getTechnology().getFrame() >
							 alphaShip.getTechnology().getFrame()) &&
							(betaShip.getTechnology().getEngine() >
							 alphaShip.getTechnology().getEngine())) {
						damage *= 2;
					}

					alphaShip.setHealth(alphaShip.getHealth() - damage);
				}

				if(alphaShip.getHealth() <= Ship.MIN_HEALTH) {
//System.out.println("  ...alpha lost");
					alpha.removeShip(alphaShip);
				}
				if(betaShip.getHealth() <= Ship.MIN_HEALTH) {
//System.out.println("  ...beta lost");
					beta.removeShip(betaShip);
				}
			}

			if(alpha.getShipCount() > beta.getShipCount()) {
//System.out.println("...offense wins, with " + alpha.getShipCount() + " remaining");
				winner = alpha;
			}
			else {
//System.out.println("...defense wins, with " + beta.getShipCount() + " remaining");
				winner = beta;
			}

			// heal the ships in the winning fleet

			iterator = winner.getShips().iterator();
			while(iterator.hasNext()) {
				ship = (Ship)iterator.next();
				if(ship == null) {
					break;
				}

				ship.heal();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return winner;
	}

	public void processTravel(ScrollDisplay scrollDisplay)
	{
		boolean foundArrival = false;
		int pick = 0;
		int shipCount = 0;
		int planetShipCount = 0;

		Iterator iterator = null;
		Travel travel = null;
		Player playerOne = null;
		Player playerTwo = null;
		Planet planet = null;
		Fleet fleet = null;
		Sprite sprite = null;
		BattleTriggerEvent event = null;

		try {
			iterator = travels.iterator();
			while(iterator.hasNext()) {
				travel = (Travel)iterator.next();
				if(travel == null) {
					break;
				}

				travel.process();
			}

			foundArrival = false;
			iterator = travels.iterator();
			while(iterator.hasNext()) {
				travel = (Travel)iterator.next();
				if(travel == null) {
					break;
				}

				if(travel.hasArrived()) {
					foundArrival = true;
					break;
				}
			}

			while(foundArrival) {
				travels.remove(travel);

				playerOne = null;
				planet = null;
				playerTwo = null;

				playerOne = getPlayer(travel.getOwnerId());
				planet = getPlanet(travel.getDestinationId());
				if(planet.getOwnerId() != NEUTRAL_PLANET_ID) {
					playerTwo = getPlayer(planet.getOwnerId());
				}

				if(playerOne.getId() == planet.getOwnerId()) {
					scrollDisplay.log(Color.green, "Fleet arrived at '" +
							planet.getName() + "'");
					planet.dockFleet(travel.getFleet());
				}
				else {
					shipCount = travel.getFleet().getShipCount();
					planetShipCount = planet.getFleet().getShipCount();
					fleet = Game.fightFleets(travel.getFleet(),
							planet.getFleet());
					if(fleet.getOwnerId() == playerOne.getId()) {
						event = new BattleTriggerEvent(planet, playerOne,
								playerTwo);
						galaxik.getTriggerSystem().recordEvent(event);
						scrollDisplay.log(Color.red,
								"Battle at '" + planet.getName() +
								"', " + playerOne.getName() +
								" was victorious");

						// update planet & sprite images

						planet.setImageName(playerOne.getPlanetImageName());
						sprite = galaxik.scene.getSprite(planet.getName());
						sprite.setImageTexture(Sprite.DIRECTION_EAST,
								planet.getImageName());
						galaxik.scene.triggerNewSnapshot();

						// update planet state

						planet.setOwnerId(playerOne.getId());
						planet.getFleet().reset();
						planet.dockFleet(fleet);
						planet.setAutoBuild(false);

						// update statistics

						if(playerTwo != null) {
							playerTwo.addPlanetsLost(1);
							playerTwo.addShipsLost(planetShipCount -
									planet.getFleet().getShipCount());
							playerTwo.addShipsDestroyed(shipCount -
									fleet.getShipCount());
						}
						playerOne.addShipsLost(shipCount -
								fleet.getShipCount());
						playerOne.addShipsDestroyed(planetShipCount -
								planet.getFleet().getShipCount());
						playerOne.addPlanetsConquered(1);
					}
					else {
						event = new BattleTriggerEvent(planet, playerTwo,
								playerOne);
						galaxik.getTriggerSystem().recordEvent(event);
						scrollDisplay.log(Color.red,
								"Battle at '" + planet.getName() +
								"', " + playerOne.getName() +
								" was defeated");

						// update statistics

						if(playerTwo != null) {
							playerTwo.addShipsLost(planetShipCount -
									planet.getFleet().getShipCount());
							playerTwo.addShipsDestroyed(shipCount);
						}
						playerOne.addShipsLost(shipCount);
						playerOne.addShipsDestroyed(planetShipCount -
								planet.getFleet().getShipCount());
					}
				}

				foundArrival = false;
				iterator = travels.iterator();
				while(iterator.hasNext()) {
					travel = (Travel)iterator.next();
					if(travel == null) {
						break;
					}

					if(travel.hasArrived()) {
						foundArrival = true;
						break;
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

