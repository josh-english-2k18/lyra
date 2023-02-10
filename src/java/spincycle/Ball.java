/*
 * Ball.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * The ball sprite.
 *
 * Written by Josh English.
 */

// define package space

package spincycle;

// import external packages

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

// import internal packages

import lyra.*;

// define class

public class Ball extends Sprite
{
	// define private class constants

	private static final String CLASS_NAME = Ball.class.getName();

	// define public class constants

	public static final int OUTLINE_TYPE_SOLID = 1;
	public static final int OUTLINE_TYPE_CORNERS = 2;
	public static final int OUTLINE_TYPE_NONE = 3;

	public static final int RATE_LIST_LENGTH = 60;

	public static final double MAX_MAGNITUDE = 9.0;

	public static final int DEFAULT_RADIUS = 7;

	public static final double DEFAULT_SPEED = 2.0;

	public static final double IDEAL_FRAMERATE = 60.0;

	// define private class variables

	private boolean isAtMaxSpeed = false;
	private int displayWidth = 0;
	private int radius = 0;
	private int edgeBounce = 0;
	private int reflectHits = 0;
	private int outlineType = 0;
	private int rateRef = 0;
	private int rateTicks = 0;
	private double precisionX = 0;
	private double precisionY = 0;
	private double xSpeed = 0;
	private double ySpeed = 0;
	private double fRatio = 0;
	private double aX = 0;
	private double aY = 0;
	private double bX = 0;
	private double bY = 0;
	private double frameRate = 0;
	private double maxMagnitude = 0;
	private double rateList[] = null;

	// define private functions

	private double calcDistanceFormula(double x1, double y1, double x2,
			double y2)
	{
		double result = 0;

		result = Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));

		return result;
	}

	private boolean isBetween(double value, double valueA, double valueB)
	{
		double low = 0.0;
		double high = 0.0;

		if(valueA > valueB) {
			low = valueB;
			high = valueA;
		}
		else if(valueA < valueB) {
			low = valueA;
			high = valueB;
		}
		else if(valueA == valueB) {
			if(value == valueA) {
				return true;
			}
			return false;
		}

		if((low <= value) && (value <= high)) {
			return true;
		}

		return false;
	}

	private double getDistance(Brick brick, Graphics2D render)
	{
		double baseSide = 0.0;
		double sP = 0.0; // semi perimiter
		double theta = 0.0;
		double xLocation = 0.0;
		double yLocation = 0.0;
		double[] distance = new double[4];
		double[] height = new double[4];

		theta = brick.getRotationTheta();

		// find the distance to each corner of the brick

		distance[0] = calcDistanceFormula(precisionX, precisionY,
				brick.aX, brick.aY);
		distance[1] = calcDistanceFormula(precisionX, precisionY,
				brick.bX, brick.bY);
		distance[2] = calcDistanceFormula(precisionX, precisionY,
				brick.cX, brick.cY);
		distance[3] = calcDistanceFormula(precisionX, precisionY,
				brick.dX, brick.dY);

		// find the lowest height in order to determine the closest brick line

		for(int ii = 0; ii < 4; ii++) {
			if(ii == 0) {
				baseSide = calcDistanceFormula(brick.aX, brick.aY, brick.bX,
						brick.bY);
			}
			else if(ii == 1) {
				baseSide = calcDistanceFormula(brick.bX, brick.bY, brick.cX,
						brick.cY);
			}
			else if(ii == 2) {
				baseSide = calcDistanceFormula(brick.cX, brick.cY, brick.dX,
						brick.dY);
			}
			else if(ii == 3) {
				baseSide = calcDistanceFormula(brick.dX, brick.dY, brick.aX,
						brick.aY);
			}
			if(ii < 3) {
				sP = ((distance[ii] + distance[(ii + 1)] + baseSide) / 2.0);
				height[ii] = (2.0 * Math.sqrt((sP * (sP - distance[ii]) *
						(sP - baseSide) * (sP - distance[(ii + 1)]))) /
						baseSide);
			}
			else {
				sP = ((distance[ii] + distance[0] + baseSide) / 2.0);
				height[ii] = (2.0 * Math.sqrt((sP * (sP - distance[ii]) *
						(sP - baseSide) * (sP - distance[0]))) /
						baseSide);
			}

			xLocation = ((height[ii] * Math.cos(theta +
							(ii * Math.toRadians(90.0)))) + precisionX);
			yLocation = ((height[ii] * Math.sin(theta +
							(ii * Math.toRadians(90.0)))) + precisionY);

			if(ii == 0) {
				if(!isBetween(xLocation, brick.aX, brick.bX)) {
					height[ii] = Double.MAX_VALUE;
					if(render != null) {
						render.setColor(Color.blue);
						render.drawLine((int)precisionX, (int)precisionY,
								(int)xLocation, (int)yLocation);
					}
				}
				else {
					if(render != null) {
						render.setColor(Color.cyan);
						render.drawLine((int)precisionX, (int)precisionY,
								(int)xLocation, (int)yLocation);
					}
				}
			}
			else if(ii == 1) {
				if(!isBetween(xLocation, brick.bX, brick.cX)) {
					height[ii] = Double.MAX_VALUE;
					if(render != null) {
						render.setColor(Color.green);
						render.drawLine((int)precisionX, (int)precisionY,
								(int)xLocation, (int)yLocation);
					}
				}
				else {
					if(render != null) {
						render.setColor(Color.magenta);
						render.drawLine((int)precisionX, (int)precisionY,
								(int)xLocation, (int)yLocation);
					}
				}
			}
			else if(ii == 2) {
				if(!isBetween(xLocation, brick.cX, brick.dX)) {
					height[ii] = Double.MAX_VALUE;
					if(render != null) {
						render.setColor(Color.orange);
						render.drawLine((int)precisionX, (int)precisionY,
								(int)xLocation, (int)yLocation);
					}
				}
				else {
					if(render != null) {
						render.setColor(Color.pink);
						render.drawLine((int)precisionX, (int)precisionY,
								(int)xLocation, (int)yLocation);
					}
				}
			}
			else if(ii == 3) {
				if(!isBetween(xLocation, brick.dX, brick.aX)) {
					height[ii] = Double.MAX_VALUE;
					if(render != null) {
						render.setColor(Color.red);
						render.drawLine((int)precisionX, (int)precisionY,
								(int)xLocation, (int)yLocation);
					}
				}
				else {
					if(render != null) {
						render.setColor(Color.yellow);
						render.drawLine((int)precisionX, (int)precisionY,
								(int)xLocation, (int)yLocation);
					}
				}
			}
		}

		// assign the height

		int ref = 0;
		for(int ii = 1; ii < 4; ii++) {
			if(height[ii] < height[ref]) {
				ref = ii;
			}
		}

		// assign the coordinates

		if(ref == 0) {
			aX = brick.aX;
			aY = brick.aY;
			bX = brick.bX;
			bY = brick.bY;
		}
		else if(ref == 1) {
			aX = brick.bX;
			aY = brick.bY;
			bX = brick.cX;
			bY = brick.cY;
		}
		else if(ref == 2) {
			aX = brick.cX;
			aY = brick.cY;
			bX = brick.dX;
			bY = brick.dY;
		}
		else if(ref == 3) {
			aX = brick.dX;
			aY = brick.dY;
			bX = brick.aX;
			bY = brick.aY;
		}

		return height[ref];
	}

	// define public class functions

	public Ball(String name, int x, int y, int width, int height,
			int displayWidth, GameEngine gameEngine)
	{
		super(name, x, y, width, height, gameEngine);

		this.displayWidth = displayWidth;

		isAtMaxSpeed = false;
		radius = DEFAULT_RADIUS;
		edgeBounce = 0;
		reflectHits = 0;
		outlineType = OUTLINE_TYPE_NONE;
		rateRef = 0;
		rateTicks = 0;
		precisionX = (double)(x + radius);
		precisionY = (double)(y + radius);

		if(((int)(Math.random() * 1000000.0) % 2) == 0) {
			xSpeed = DEFAULT_SPEED;
		}
		else {
			xSpeed = -DEFAULT_SPEED;
		}
		if(((int)(Math.random() * 1000000.0) % 2) == 0) {
			ySpeed = DEFAULT_SPEED;
		}
		else {
			ySpeed = -DEFAULT_SPEED;
		}

		fRatio = 1.0;

		aX = 0.0;
		aY = 0.0;
		bX = 0.0;
		bY = 0.0;
		frameRate = 0.0;
		maxMagnitude = MAX_MAGNITUDE;
		rateList = new double[RATE_LIST_LENGTH];
	}

	public int getReflectHits()
	{
		return reflectHits;
	}

	public double getBallX()
	{
		return precisionX;
	}

	public double getBallY()
	{
		return precisionY;
	}

	public void setLocation(int x, int y)
	{
		precisionX = (double)x;
		precisionY = (double)y;
		lastX = this.x;
		lastY = this.y;
		this.x = ((int)precisionX - radius);
		this.y = ((int)precisionY - radius);
	}

	public Rectangle getBoundary()
	{
		int fudgeFactor = 0;
		double magnitude = 0.0;

		Rectangle result = null;

		magnitude = Math.sqrt((xSpeed * xSpeed) + (ySpeed * ySpeed));
		fudgeFactor = (((radius + (int)(magnitude)) * 2) * 2) * 2;

		result = new Rectangle((int)(precisionX - (fudgeFactor / 2)),
				(int)(precisionY - (fudgeFactor / 2)), 
				fudgeFactor, fudgeFactor);

		return result;
	}

	public int getRadius()
	{
		return radius;
	}

	public void setRadius(int radius)
	{
		this.radius = radius;
	}

	public double getXSpeed()
	{
		return xSpeed;
	}

	public double getYSpeed()
	{
		return ySpeed;
	}

	public void setBallSpeed(double xSpeed, double ySpeed)
	{
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
	}

	public double getMagnitude()
	{
		double result = 0.0;

		result = Math.sqrt((xSpeed * xSpeed) + (ySpeed * ySpeed));

		return result;
	}

	public boolean isAtMaxSpeed()
	{
		return isAtMaxSpeed;
	}

	public int getOutlineType()
	{
		return outlineType;
	}

	public void setOutlineType(int type)
	{
		if((type != OUTLINE_TYPE_SOLID) && (type != OUTLINE_TYPE_CORNERS) &&
				(type != OUTLINE_TYPE_NONE)) {
			return;
		}

		outlineType = type;
	}

	public int getEdgeBounce()
	{
		int result = this.edgeBounce;
		this.edgeBounce = 0;
		return result;
	}

	public double getFramerate()
	{
		return frameRate;
	}

	public void setFramerateOverride(double rate)
	{
		frameRate = rate;
	}

	public void setCurrentFramerate()
	{
		int ii = 0;
		double rate = 0.0;
		double delta = 0.0;
		double average = 0.0;

		// skip the first few frames to ignore garbage

		rateTicks++;
		if(rateTicks < (RATE_LIST_LENGTH * 2)) {
			return;
		}

		rate = gameEngine.getFramerate();

		// create the framerate list for averaging

		if(rateRef < RATE_LIST_LENGTH) {
			rateList[rateRef] = rate;
			rateRef++;
			return;
		}

		// update the framerate list

		for(ii = 0; ii < (RATE_LIST_LENGTH - 1); ii++) {
			rateList[ii] = rateList[(ii + 1)];
		}
		rateList[(RATE_LIST_LENGTH - 1)] = rate;

		// calculate the current average framerate

		average = 0.0;
		for(ii = 0; ii < RATE_LIST_LENGTH; ii++) {
			average += rateList[ii];
		}
		average /= (double)RATE_LIST_LENGTH;

		// modifiy the ball speed accordingly

		frameRate = average;

		fRatio = (IDEAL_FRAMERATE / frameRate);
	}

	public void bounce()
	{
		if(((int)(Math.random() * 1000000.0) % 2) == 0) {
			xSpeed *= -1.0;
		}
		else {
			ySpeed *= -1.0;
		}
	}

	public void cornerBounce(double x1, double y1, Brick brick)
	{
		xSpeed *= -1;
		ySpeed *= -1;

		if((!isAtMaxSpeed) && (getMagnitude() < maxMagnitude)) {
			if(brick.getType() == Brick.TYPE_BLUE) {
				xSpeed *= 1.02;
				ySpeed *= 1.02;
			}
			else if(brick.getType() == Brick.TYPE_RED) {
				xSpeed *= 1.03;
					ySpeed *= 1.03;
			}
			else {
				xSpeed *= 1.01;
				ySpeed *= 1.01;
			}
		}
		else {
			isAtMaxSpeed = true;
		}
	}

	public void sideBounce(double x1, double y1, double x2, double y2,
			Brick brick)
	{
		int brickSide = 0;
		double brickTheta = 0.0;
		double tempX = 0.0;
		double tempY = 0.0;
		double brickX = 0.0;

		brickTheta = brick.getRotationTheta();
		brickTheta = Math.PI * 3/2 - brickTheta;

		// rotate to origin case

		tempX = xSpeed * Math.cos(brickTheta) - ySpeed * Math.sin(brickTheta);
		tempY = xSpeed * Math.sin(brickTheta) + ySpeed * Math.cos(brickTheta);
		brickX = (x2 - x1) * Math.cos(brickTheta) - (y2 - y1) *
			Math.sin(brickTheta);

		if(Math.abs(brickX) < 0.5) {
			brickSide = 1;
		}

		// bounce

		if(brickSide == 1) {
			tempX *= -1.0;
		}
		else {	
			tempY *= -1.0;
		}

		// rotate back

		xSpeed = tempX * Math.cos(-1 * brickTheta) - tempY *
			Math.sin(-1.0 * brickTheta);
		ySpeed = tempX * Math.sin(-1 * brickTheta) + tempY *
			Math.cos(-1.0 * brickTheta);

		// increase difficulty

		if((!isAtMaxSpeed) && (this.getMagnitude() < maxMagnitude)) {
			if(brick.getType() == Brick.TYPE_BLUE) {
				xSpeed *= 1.02;
				ySpeed *= 1.02;
			}
			else if(brick.getType() == Brick.TYPE_RED) {
				xSpeed *= 1.03;
					ySpeed *= 1.03;
			}
			else {
				xSpeed *= 1.01;
				ySpeed *= 1.01;
			}
		}
		else {
			isAtMaxSpeed = true;
		}
	}

	public void brickBounce(Brick brick)
	{
		double tempX = 0.0;
		double tempY = 0.0;
		double vectorX = 0.0;
		double vectorY = 0.0;
		double height = 0.0;
		double dotProduct = 0.0;
		double incidence = 0.0;
		double magnitude = 0.0;

		magnitude = Math.sqrt((xSpeed * xSpeed) + (ySpeed * ySpeed));

		height = getDistance(brick, null);
		if((height - magnitude) > (double)radius) {
			return;
		}

		// find the base vector

		vectorX = (aX - bX);
		vectorY = (aY - bY);
		tempX = vectorX;
		tempY = vectorY;
		tempX = (tempX / (Math.sqrt((vectorX * vectorX) +
						(vectorY * vectorY))));
		tempY = (tempY / (Math.sqrt((vectorX * vectorX) +
						(vectorY * vectorY))));
		vectorX = tempX;
		vectorY = tempY;
		tempX = xSpeed;
		tempY = ySpeed;
		tempX = (tempX / (Math.sqrt((xSpeed * xSpeed) +
						(ySpeed * ySpeed))));
		tempY = (tempY / (Math.sqrt((xSpeed * xSpeed) +
						(ySpeed * ySpeed))));

		// find the dot product of the ball to the brick line

		dotProduct = Math.acos((vectorX * tempX) + (vectorY * tempY));

		// find the amount of ball vector rotation

		incidence = Math.toRadians(90.0 + Math.toDegrees(dotProduct));

		// perform matrix rotation

		tempX = ((Math.cos(incidence) * xSpeed) -
				(Math.sin(incidence) * ySpeed));
		tempY = ((Math.sin(incidence) * xSpeed) +
				(Math.cos(incidence) * ySpeed));

		// assign result

		xSpeed = tempX;
		ySpeed = tempY;

		// increase difficulty

		if((!isAtMaxSpeed) && (this.getMagnitude() < maxMagnitude)) {
			if(brick.getType() == Brick.TYPE_BLUE) {
				xSpeed *= 1.02;
				ySpeed *= 1.02;
			}
			else if(brick.getType() == Brick.TYPE_RED) {
				xSpeed *= 1.03;
				ySpeed *= 1.03;
			}
			else {
				xSpeed *= 1.01;
				ySpeed *= 1.01;
			}
		}
		else {
			isAtMaxSpeed = true;
		}
	}

	public void reset()
	{
		precisionX = (double)((gameEngine.getWindowWidth() -
					(displayWidth / 2)) - radius);
		precisionY = (double)((displayWidth / 2) - radius);
		x = ((int)precisionX - radius);
		y = ((int)precisionY - radius);
		if(((int)(Math.random() * 1000000.0) % 2) == 0) {
			xSpeed = DEFAULT_SPEED;
		}
		else {
			xSpeed = -DEFAULT_SPEED;
		}
		if(((int)(Math.random() * 1000000.0) % 2) == 0) {
			ySpeed = DEFAULT_SPEED;
		}
		else {
			ySpeed = -DEFAULT_SPEED;
		}
		edgeBounce = 0;
		reflectHits = 0;
	}

	public void animate()
	{
		boolean flag = false;
		int localWidth = 0;
		int localWidthLow = 0;
		int localWidthHigh = 0;

		localWidthLow = (gameEngine.getWindowWidth() - displayWidth);
		localWidthHigh = gameEngine.getWindowWidth();
		localWidth = (localWidthHigh - localWidthLow);

		precisionX += (xSpeed * fRatio);
		if(outlineType == OUTLINE_TYPE_SOLID) {
			if((precisionX + radius) >= localWidthHigh) {
				precisionX = (localWidthHigh - radius);
				xSpeed *= -1.0;
				reflectHits++;
			}
			if((precisionX - radius) < localWidthLow) {
				precisionX = (localWidthLow + radius);
				xSpeed *= -1.0;
				reflectHits++;
			}
		}
		else if(outlineType == OUTLINE_TYPE_CORNERS) {
			if((precisionX + radius) >= localWidthHigh) {
				if(((precisionY + radius) >
							(gameEngine.getWindowHeight() * 0.166)) &&
						((precisionY - radius) <
						 (gameEngine.getWindowHeight() -
						  (gameEngine.getWindowHeight() * 0.166)))) {
					if(!flag) {
						edgeBounce++;
						flag = true;
					}
				}
				precisionX = (localWidthHigh - radius);
				xSpeed *= -1.0;
			}
			if((precisionX - radius) < localWidthLow) {
				if(((precisionY + radius) >
							(gameEngine.getWindowHeight() * 0.166)) &&
						((precisionY - radius) <
						 (gameEngine.getWindowHeight() -
						  (gameEngine.getWindowHeight() * 0.166)))) {
					if(!flag) {
						edgeBounce++;
						flag = true;
					}
				}
				precisionX = (localWidthLow + radius);
				xSpeed *= -1.0;
			}
		}
		else if(outlineType == OUTLINE_TYPE_NONE) {
			if((precisionX + radius) >= localWidthHigh) {
				precisionX = (localWidthHigh - radius);
				xSpeed *= -1.0;
				if(!flag) {
					edgeBounce++;
					flag = true;
				}
			}
			if((precisionX - radius) < localWidthLow) {
				precisionX = (localWidthLow + radius);
				xSpeed *= -1.0;
				if(!flag) {
					edgeBounce++;
					flag = true;
				}
			}
		}
		precisionY += (ySpeed * fRatio);
		if(outlineType == OUTLINE_TYPE_SOLID) {
			if((precisionY + radius) >= gameEngine.getWindowHeight()) {
				precisionY = (gameEngine.getWindowHeight() - radius);
				ySpeed *= -1.0;
				reflectHits++;
			}
			if((precisionY - radius) < 0) {
				precisionY = radius;
				ySpeed *= -1.0;
				reflectHits++;
			}
		}
		else if(outlineType == OUTLINE_TYPE_CORNERS) {
			if((precisionY + radius) >= gameEngine.getWindowHeight()) {
				if(((precisionX - radius) >
							(localWidthLow + (localWidth * 0.166))) &&
						((precisionX + radius) <
						 (localWidthHigh - (localWidth * 0.166)))) {
					if(!flag) {
						edgeBounce++;
						flag = true;
					}
				}
				precisionY = (gameEngine.getWindowHeight() - radius);
				ySpeed *= -1.0;
			}
			if((precisionY - radius) < 0) {
				if(((precisionX - radius) >
							(localWidthLow + (localWidth * 0.166))) &&
						((precisionX + radius) <
						 (localWidthHigh - (localWidth * 0.166)))) {
					if(!flag) {
						edgeBounce++;
						flag = true;
					}
				}
				precisionY = radius;
				ySpeed *= -1.0;
			}
		}
		else if(outlineType == OUTLINE_TYPE_NONE) {
			if((precisionY + radius) >= gameEngine.getWindowHeight()) {
				precisionY = (gameEngine.getWindowHeight() - radius);
				ySpeed *= -1.0;
				if(!flag) {
					edgeBounce++;
					flag = true;
				}
			}
			if((precisionY - radius) < 0) {
				precisionY = radius;
				ySpeed *= -1.0;
				if(!flag) {
					edgeBounce++;
					flag = true;
				}
			}
		}

		x = ((int)precisionX - radius);
		y = ((int)precisionY - radius);
	}

	public void renderDistanceAnimation(Brick brick, Graphics2D render)
	{
		try {
			getDistance(brick, render);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

