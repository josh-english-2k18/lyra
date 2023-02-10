/*
 * GameAIOne.java
 *
 * Copyright (C) 2008 by Joshua S. English. This document is the intellectual
 * property of Joshua S. English. All rights reserved.
 *
 * The Galaxik game AI #1 definition.
 *
 * Written by Josh English.
 */

// define package space

package galaxik;

// import external packages

import java.util.Iterator;
import java.util.ArrayList;

// import interal packages

import galaxik.widgets.ScrollDisplay;

// define class

public class GameAIOne
{
	// define private class constants

	private static final String CLASS_NAME = GameAIOne.class.getName();

	private static final int DEFENSE_THRESHOLD = 20;
	private static final int OFFENSE_THRESHOLD = 30;

	// define private class variables

	private int id = 0;
	private Player player = null;
	private Game game = null;

	// define public class functions

	public GameAIOne(int id, Player player, Game game)
	{
		this.id = id;
		this.player = player;
		this.game = game;
	}

	public int getId()
	{
		return id;
	}

	public Player getPlayer()
	{
		return player;
	}

	public void execute(ScrollDisplay scrollDisplay)
	{
		int ii = 0;
		int credits = 0;
		int planetLength = 0;
		int planetRef[] = null;
		double tempDistance = 0.0;
		double distance = 0.0;

		ArrayList planets = null;
		Iterator iterator = null;
		Planet origin = null;
		Planet planet = null;
		String pick = null;
		Fleet fleet = null;
		Travel travel = null;

		try {
			if(!player.isAlive()) {
				return;
			}

			planets = game.getPlanets();

			// determine how many planets this AI controls

			iterator = planets.iterator();
			while(iterator.hasNext()) {
				planet = (Planet)iterator.next();
				if(planet == null) {
					break;
				}

				if(planet.getOwnerId() == id) {
					planetLength++;
				}
			}

			if(planetLength < 1) {
				return;
			}

			// perform default player planet-processing

			credits = (player.getCredits() / planetLength);

			iterator = planets.iterator();
			while(iterator.hasNext()) {
				planet = (Planet)iterator.next();
				if(planet == null) {
					break;
				}

				if(planet.getOwnerId() == id) {
					game.processPlayerPlanet(planet.getName(), true, credits);
				}
			}

			// compile a list of AI-owned planets

			planetRef = new int[planetLength];

			ii = 0;
			iterator = planets.iterator();
			while(iterator.hasNext()) {
				planet = (Planet)iterator.next();
				if(planet == null) {
					break;
				}

				if(planet.getOwnerId() == id) {
					planetRef[ii] = planet.getId();
					ii++;
				}
			}

			// iterator planets & make fleet movement decisions

			for(ii = 0; ii < planetLength; ii++) {
				origin = game.getPlanet(planetRef[ii]);

				if(origin.getFleet().getShipCount() <
						(DEFENSE_THRESHOLD + OFFENSE_THRESHOLD)) {
					continue;
				}

				pick = null;
				distance = 999999.99;

				iterator = planets.iterator();
				while(iterator.hasNext()) {
					planet = (Planet)iterator.next();
					if(planet == null) {
						break;
					}

					if(planet.getOwnerId() != id) {
						tempDistance = Game.calculateDistance(origin, planet);
						if(tempDistance < distance) {
							distance = tempDistance;
							pick = planet.getName();
						}
					}
				}

				if(pick == null) {
					continue;
				}

				planet = game.getPlanet(pick);


				fleet = origin.getFleet().divide(
						origin.getFleet().getShipCount() - DEFENSE_THRESHOLD);
				distance /= (fleet.getTechnology().getSpeed());
				if(distance < 1.0) {
					distance = 1.0;
				}
				travel = new Travel(id, origin.getId(), planet.getId(),
						(int)distance, fleet);
				game.addTravel(travel);

//System.out.println(player.getName() + " sent fleet " + fleet.getShipCount() + " from '" + origin.getName() + "' to '" + planet.getName() + "' with " + origin.getFleet().getShipCount() + " remaining");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

