/*
 * Game.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * The Spincycle game logic.
 *
 * Written by Josh English.
 */

// define package space

package spincycle;

// import external packages

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

// import internal packages

import lyra.*;
import lyra.drivers.KeyboardDriver;
import lyra.drivers.MouseDriver;

// define class

public class Game
{
	// define private class constants

	private static final String CLASS_NAME = Game.class.getName();

	private static final int DEFAULT_BRICKS = 128;

	private static final int BRICK_FUDGE_FACTOR = 100;

	// define private class variables

	private int displayWidth = 0;
	private int lastBrickType = 0;
	private int lastBrickTicks = 0;
	private int highestChain = 0;
	private int brickTicks[] = null;
	private Collision collision = null;
	private Player player = null;
	private Spincycle spincycle = null;

	// define private class functions

	private int calculateBrickXValue()
	{
		int result = 0;

		result = (int)(Math.random() * (displayWidth - BRICK_FUDGE_FACTOR));
		while((result < BRICK_FUDGE_FACTOR) ||
				(result > (displayWidth - BRICK_FUDGE_FACTOR))) {
			result = (int)(Math.random() * (displayWidth - BRICK_FUDGE_FACTOR));
		}

		result += (spincycle.getWindowWidth() - displayWidth);

		return result;
	}

	private int calculateBrickYValue()
	{
		int result = 0;

		result = (int)(Math.random() * (displayWidth - BRICK_FUDGE_FACTOR));
		while((result < BRICK_FUDGE_FACTOR) ||
				(result > (displayWidth - BRICK_FUDGE_FACTOR))) {
			result = (int)(Math.random() * (displayWidth - BRICK_FUDGE_FACTOR));
		}

		return result;
	}

	// define public static class functions

	public static double calculateDistance(Sprite alpha, Sprite beta)
	{
		double result = 0;

		result = Math.sqrt((((double)alpha.getX() - (double)beta.getX()) *
					((double)alpha.getX() - (double)beta.getX())) +
				(((double)alpha.getY() - (double)beta.getY()) *
				 ((double)alpha.getY() - (double)beta.getY())));

		return result;
	}

	// define public class functions

	public Game(int displayWidth, Player player, Spincycle spincycle)
	{
		this.displayWidth = displayWidth;
		this.player = player;
		this.spincycle = spincycle;

		lastBrickType = -1;
		lastBrickTicks = 0;
		highestChain = 0;
		brickTicks = new int[Brick.BRICKS];
		collision = new Collision();
	}

	public int getChainScore()
	{
		int score = 0;
		int result = 0;

		switch(lastBrickType) {
			case Brick.TYPE_GREEN:
				score = Brick.POINTS_GREEN;
				break;
			case Brick.TYPE_BLUE:
				score = Brick.POINTS_BLUE;
				break;
			case Brick.TYPE_RED:
				score = Brick.POINTS_RED;
				break;
		}

		result = (score * lastBrickTicks);

		return result;
	}

	public int getLastBrickType()
	{
		return lastBrickType;
	}

	public void setLastBrickType(int type)
	{
		lastBrickType = type;
	}

	public int getLastBrickTicks()
	{
		return lastBrickTicks;
	}

	public void incrementBrickTicks(int type)
	{
		switch(type) {
			case Brick.TYPE_GREEN:
				brickTicks[Brick.TYPE_GREEN] += 1;
				break;
			case Brick.TYPE_BLUE:
				brickTicks[Brick.TYPE_BLUE] += 1;
				break;
			case Brick.TYPE_RED:
				brickTicks[Brick.TYPE_RED] += 1;
				break;
		}

		if(type != lastBrickType) {
			if(lastBrickTicks > highestChain) {
				highestChain = lastBrickTicks;
			}
			lastBrickType = type;
			return;
		}

		lastBrickTicks++;
	}

	public int getBrickTicks(int type)
	{
		int result = 0;

		switch(type) {
			case Brick.TYPE_GREEN:
				result = brickTicks[Brick.TYPE_GREEN];
				break;
			case Brick.TYPE_BLUE:
				result = brickTicks[Brick.TYPE_BLUE];
				break;
			case Brick.TYPE_RED:
				result = brickTicks[Brick.TYPE_RED];
				break;
		}

		return result;
	}

	public Brick[] loadDefaultLevelOne()
	{
		int ii = 0;

		Brick bricks[] = null;

		try {
			bricks = new Brick[DEFAULT_BRICKS];
			for(ii = 0; ii < DEFAULT_BRICKS; ii++) {
				bricks[ii] = new Brick("Brick" + ii,
						calculateBrickXValue(), calculateBrickYValue(),
						Brick.DEFAULT_WIDTH, Brick.DEFAULT_HEIGHT,
						displayWidth, spincycle);
				bricks[ii].setDebugMode(false);
				bricks[ii].setStatic(false);
				bricks[ii].setOutline(false);
				bricks[ii].setSelectable(false);
				if((ii % 3) == 1) {
					bricks[ii].setType(Brick.TYPE_BLUE);
					bricks[ii].setImageTexture(Sprite.DIRECTION_EAST,
							"assets/textures/spincycle/brickBlue.png");
				}
				else if((ii % 3) == 2) {
					bricks[ii].setType(Brick.TYPE_RED);
					bricks[ii].setImageTexture(Sprite.DIRECTION_EAST,
							"assets/textures/spincycle/brickRed.png");
				}
				else {
					bricks[ii].setType(Brick.TYPE_GREEN);
					bricks[ii].setImageTexture(Sprite.DIRECTION_EAST,
							"assets/textures/spincycle/brickGreen.png");
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return bricks;
	}

	public Brick[] loadLevel(int level)
	{
		int ii = 0;
		int x = 0;
		int y = 0;
		int type = 0;
		int brickRef = 0;
		int brickLength = 0;

		String line = null;
		String assetName = null;
		ByteArrayInputStream byteArrayInputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		Brick bricks[] = null;

		try {
			assetName = new String("assets/config/spincycle/levels/game" +
					level + ".level");

			byteArrayInputStream = spincycle.getAssetCache().
				obtainAssetStream(assetName);
			inputStreamReader = new InputStreamReader(byteArrayInputStream);
			bufferedReader = new BufferedReader(inputStreamReader);

			/*
			 * TODO: detect & parse HTTP header
			 */

			line = bufferedReader.readLine();
			brickLength = (new Integer(line)).intValue();
			bricks = new Brick[brickLength];

			while((line = bufferedReader.readLine()) != null) {
				x = -100;
				y = -100;
				type = 0;

				line.trim();
				String[] values = line.split(",");

				for(ii = 0; ii < values.length; ii++) {
					if(ii == 0) {
						type = (new Integer(values[ii])).intValue();
					}
					else if(ii == 1) {
						x = (new Integer(values[ii])).intValue();
					}
					else if(ii == 2) {
						y = (new Integer(values[ii])).intValue();
					}
				}

				bricks[brickRef] = new Brick("Brick" + brickRef, x, y,
						Brick.DEFAULT_WIDTH, Brick.DEFAULT_HEIGHT,
						displayWidth, spincycle);
				bricks[brickRef].setDebugMode(false);
				bricks[brickRef].setStatic(false);
				bricks[brickRef].setOutline(false);
				bricks[brickRef].setSelectable(false);
				if(type == Brick.TYPE_BLUE) {
					bricks[brickRef].setType(Brick.TYPE_BLUE);
					bricks[brickRef].setImageTexture(Sprite.DIRECTION_EAST,
							"assets/textures/spincycle/brickBlue.png");
				}
				else if(type == Brick.TYPE_RED) {
					bricks[brickRef].setType(Brick.TYPE_RED);
					bricks[brickRef].setImageTexture(Sprite.DIRECTION_EAST,
							"assets/textures/spincycle/brickRed.png");
				}
				else {
					bricks[brickRef].setType(Brick.TYPE_GREEN);
					bricks[brickRef].setImageTexture(Sprite.DIRECTION_EAST,
							"assets/textures/spincycle/brickGreen.png");
				}

				brickRef++;
				if(brickRef >= brickLength) {
					break;
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return bricks;
	}

	public void calculateCollisions(Ball ball, Brick[] bricks,
			Graphics2D render)
	{
		int ii = 0;
		int brickRef = 0;
		int brickSide = 0;
		double brickDistance = 0.0;
		double distance = 0.0;
		double ballXSpeed = 0.0;
		double ballYSpeed = 0.0;

		brickRef = -1;
		brickSide = -1;
		brickDistance = Double.MAX_VALUE;
		distance = Double.MAX_VALUE;
		for(ii = 0; ii < bricks.length; ii++) {
			if((bricks[ii] == null) || (!bricks[ii].isAlive())) {
				continue;
			}

			if(collision.isCollided(ball, bricks[ii])) {
				distance = collision.computeCollision(
						ball.getBallX(), ball.getBallY(),
						ball.getRadius(), ball.getXSpeed(),
						ball.getYSpeed(), bricks[ii].aX,
						bricks[ii].aY, bricks[ii].bX,
						bricks[ii].bY, ii, render);
				if(distance < brickDistance) {
					brickDistance = distance;
					brickRef = ii;
					brickSide = 1;
				}
				distance = collision.computeCollision(
						ball.getBallX(), ball.getBallY(),
						ball.getRadius(), ball.getXSpeed(),
						ball.getYSpeed(), bricks[ii].bX,
						bricks[ii].bY, bricks[ii].cX,
						bricks[ii].cY, ii, render);
				if(distance < brickDistance) {
					brickDistance = distance;
					brickRef = ii;
					brickSide = 2;
				}
				distance = collision.computeCollision(
						ball.getBallX(), ball.getBallY(),
						ball.getRadius(), ball.getXSpeed(),
						ball.getYSpeed(), bricks[ii].cX,
						bricks[ii].cY, bricks[ii].dX,
						bricks[ii].dY, ii, render);
				if(distance < brickDistance) {
					brickDistance = distance;
					brickRef = ii;
					brickSide = 3;
				}
				distance = collision.computeCollision(
						ball.getBallX(), ball.getBallY(),
						ball.getRadius(), ball.getXSpeed(),
						ball.getYSpeed(), bricks[ii].dX,
						bricks[ii].dY, bricks[ii].aX,
						bricks[ii].aY, ii, render);
				if(distance < brickDistance) {
					brickDistance = distance;
					brickRef = ii;
					brickSide = 4;
				}
				distance = collision.computeCornerCollision(
						ball.getBallX(), ball.getBallY(),
						ball.getRadius(), ball.getXSpeed(),
						ball.getYSpeed(), bricks[ii].aX,
						bricks[ii].aY, ii);
				if(distance < brickDistance) {
					brickDistance = distance;
					brickRef = ii;
					brickSide = 5;
				}
				distance = collision.computeCornerCollision(
						ball.getBallX(), ball.getBallY(),
						ball.getRadius(), ball.getXSpeed(),
						ball.getYSpeed(), bricks[ii].bX,
						bricks[ii].bY, ii);
				if(distance < brickDistance) {
					brickDistance = distance;
					brickRef = ii;
					brickSide = 6;
				}
				distance = collision.computeCornerCollision(
						ball.getBallX(), ball.getBallY(),
						ball.getRadius(), ball.getXSpeed(),
						ball.getYSpeed(), bricks[ii].cX,
						bricks[ii].cY, ii);
				if(distance < brickDistance) {
					brickDistance = distance;
					brickRef = ii;
					brickSide = 7;
				}
				distance = collision.computeCornerCollision(
						ball.getBallX(), ball.getBallY(),
						ball.getRadius(), ball.getXSpeed(),
						ball.getYSpeed(), bricks[ii].dX,
						bricks[ii].dY, ii);
				if(distance < brickDistance) {
					brickDistance = distance;
					brickRef = ii;
					brickSide = 8;
				}
			}
		}

		if(brickRef != -1) {
			ballXSpeed = ball.getXSpeed();
			ballYSpeed = ball.getYSpeed();

			if(brickSide == 1) {
				ball.sideBounce(bricks[brickRef].aX,
						bricks[brickRef].aY, bricks[brickRef].bX,
						bricks[brickRef].bY, bricks[brickRef]);
			}
			else if(brickSide == 2) {
				ball.sideBounce(bricks[brickRef].bX,
						bricks[brickRef].bY, bricks[brickRef].cX,
						bricks[brickRef].cY, bricks[brickRef]);
			}
			else if(brickSide == 3) {
				ball.sideBounce(bricks[brickRef].cX,
						bricks[brickRef].cY, bricks[brickRef].dX,
						bricks[brickRef].dY, bricks[brickRef]);
			}
			else if(brickSide == 4) {
				ball.sideBounce(bricks[brickRef].dX,
						bricks[brickRef].dY, bricks[brickRef].aX,
						bricks[brickRef].aY, bricks[brickRef]);
			}
			else if(brickSide == 5) {
				ball.cornerBounce(bricks[brickRef].aX,
						bricks[brickRef].aY, bricks[brickRef]);	
			}
			else if(brickSide == 6) {
				ball.cornerBounce(bricks[brickRef].bX,
						bricks[brickRef].bY, bricks[brickRef]);	
			}
			else if(brickSide == 7) {
				ball.cornerBounce(bricks[brickRef].cX,
						bricks[brickRef].cY, bricks[brickRef]);	
			}
			else if(brickSide == 8) {
				ball.cornerBounce(bricks[brickRef].dX,
						bricks[brickRef].dY, bricks[brickRef]);	
			}

			incrementBrickTicks(bricks[brickRef].getType());

			player.updateScore(getChainScore());
			player.updateScore(bricks[brickRef].getPoints());

			bricks[brickRef].kill(ballXSpeed, ballYSpeed);
		}

		// TODO: do this somewhere else: brickNoise.play();
	}
}



