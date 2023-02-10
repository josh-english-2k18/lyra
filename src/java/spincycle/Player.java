/*
 * Player.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * The Spincycle player.
 *
 * Written by Josh English.
 */

// define package space

package spincycle;

// define class

public class Player
{
	// define private class constants

	private static final String CLASS_NAME = Player.class.getName();
	private static final int DEFAULT_LIVES = 3;
	
	// define private class variables

	private boolean isAlive;
	private int score;
	private int lives;
	private String name;

	// define public class functions

	public Player()
	{
		isAlive = true;
		score = 0;
		lives = DEFAULT_LIVES;
		name = new String("unknown");
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public boolean isAlive()
	{
		return isAlive;
	}

	public int getScore()
	{
		return score;
	}

	public int getLives()
	{
		return lives;
	}

	public void updateScore(int modifier)
	{
		score += modifier;
	}

	public void addLife()
	{
		lives += 1;
	}

	public void lostLife()
	{
		lives -= 1;
		if(lives <= 0) {
			isAlive = false;
		}
	}

	public void kill()
	{
		lives = 0;
		isAlive = false;
	}
}

