/*
 * BattleTriggerContext.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A Galaxik battle event context.
 *
 * Written by Josh English.
 */

// define package space

package galaxik.triggers;

// import external packages

import java.util.Date;

// import interal packages

import galaxik.*;

// define class

public class BattleTriggerContext
{
	// define private class constants

	private static final String CLASS_NAME =
		BattleTriggerContext.class.getName();

	// define private class variables

	private Planet planet = null;
	private Player playerWin = null;
	private Player playerLost = null;

	// define public class functions

	public BattleTriggerContext(Planet planet, Player playerWin,
			Player playerLost)
	{
		this.planet = planet;
		this.playerWin = playerWin;
		this.playerLost = playerLost;
	}

	public String getClassName()
	{
		return CLASS_NAME;
	}

	public Planet getPlanet()
	{
		return planet;
	}

	public Player getPlayerWin()
	{
		return playerWin;
	}

	public Player getPlayerLost()
	{
		return playerLost;
	}
}

