/*
 * Brick.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * The brick sprite.
 *
 * Written by Josh English.
 */

// define package space

package spincycle;

// import external packages

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;

// import internal packages

import lyra.*;

// define class

public class Brick extends Sprite
{
	// define private class constants

	private static final String CLASS_NAME = Brick.class.getName();

	private static final int ANIMATION_FALLING = 1;
	private static final int ANIMATION_FADING = 2;

	private static final int X = 0;
	private static final int Y = 1;

	private static final double ROTATE_ANGLE = 0.08;

	// define public class constants

	public static final boolean ROTATE_RIGHT = true;
	public static final boolean ROTATE_LEFT = false;

	public static final int TYPE_GREEN = 0;
	public static final int TYPE_BLUE = 1;
	public static final int TYPE_RED = 2;

	public static final int POINTS_GREEN = 1;
	public static final int POINTS_BLUE = 2;
	public static final int POINTS_RED = 5;

	public static final int HITS_GREEN = 1;
	public static final int HITS_BLUE = 1;
	public static final int HITS_RED = 1;

	public static final int BRICKS = 3;

	public static final int DEFAULT_WIDTH = 32;
	public static final int DEFAULT_HEIGHT = 16;

	// define private class variables

	private boolean isAlive = false;
	private boolean firstAnimationPass = false;
	private boolean isAnimating = false;
	private int type = 0;
	private int points = 0;
	private int hitsLeft = 0;
	private int displayWidth = 0;
	private int centerX = 0;
	private int centerY = 0;
	private int animationX = 0;
	private int animationY = 0;
	private int animationType = 0;
	private int animationTicks = 0;
	private double precisionX = 0.0;
	private double precisionY = 0.0;
	private double animationTheta = 0.0;

	// define public class variables

	public double aX = 0.0;
	public double aY = 0.0;
	public double bX = 0.0;
	public double bY = 0.0;
	public double cX = 0.0;
	public double cY = 0.0;
	public double dX = 0.0;
	public double dY = 0.0;

	// define private functions

	private void calculateCenter()
	{
		centerX = ((displayWidth / 2) +
				(gameEngine.getWindowWidth() - displayWidth));
		centerY = (displayWidth / 2);
	}

	private double calculateRotationTheta()
	{
		double compX = 0.0;
		double tempX = 0.0;
		double tempY = 0.0;
		double centerTheta = 0.0;
		double[] vectorA = null;
		double[] vectorB = null;

		try {
			vectorA = new double[2];
			vectorB = new double[2];

			// find center of brick

			compX = (double)this.x;
			tempX = (double)this.x;
			tempY = (double)this.y;

			// initialize vectors

			vectorA[X] = (double)(centerX - centerX);
			vectorA[Y] = (double)(centerY - 0.0);
			vectorB[X] = (tempX - (double)centerX);
			vectorB[Y] = (tempY - (double)centerY);

			// normalize vectors

			tempX = vectorA[X];
			tempY = vectorA[Y];
			vectorA[X] = (vectorA[X] / (Math.sqrt((tempX * tempX) +
							(tempY * tempY))));
			vectorA[Y] = (vectorA[Y] / (Math.sqrt((tempX * tempX) +
							(tempY * tempY))));
			tempX = vectorB[X];
			tempY = vectorB[Y];
			vectorB[X] = (vectorB[X] / (Math.sqrt((tempX * tempX) +
							(tempY * tempY))));
			vectorB[Y] = (vectorB[Y] / (Math.sqrt((tempX * tempX) +
							(tempY * tempY))));

			// dot product

			centerTheta = Math.acos((vectorA[X] * vectorB[X]) +
					(vectorA[Y] * vectorB[Y]));

			if(compX > centerX) {
				centerTheta = ((Math.PI * 2.0) - centerTheta);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return centerTheta;
	}

	private void calculateLocation()
	{
		int iTempX = 0;
		int iTempY = 0;
		double moveX = 0.0;
		double moveY = 0.0;
		double theta = 0.0;
		double dTempX = 0;
		double dTempY = 0;
		double dDiffX = 0;
		double dDiffY = 0;

		iTempX = x;
		iTempY = y;
		dTempX = precisionX;
		dTempY = precisionY;

		theta = calculateRotationTheta();

		moveX = (precisionX * -1.0);
		moveY = (precisionY * -1.0);

		precisionX += moveX;
		precisionY += moveY;
		x += (int)moveX;
		y += (int)moveY;

		aX = (((precisionX - width / 2) * Math.cos(theta)) -
				((precisionY - height / 2) * Math.sin(theta)));
		aY = (((precisionX - width / 2) * Math.sin(theta)) +
				((precisionY - height / 2) * Math.cos(theta)));
		aX -= moveX;
		aY -= moveY;

		bX = (((precisionX + width / 2) * Math.cos(theta)) -
				( (precisionY - height / 2) * Math.sin(theta)));
		bY = (((precisionX + width / 2) * Math.sin(theta)) +
				( (precisionY - height / 2) * Math.cos(theta)));
		bX -= moveX;
		bY -= moveY;

		cX = (((precisionX + width / 2) * Math.cos(theta)) -
				((precisionY + height / 2) * Math.sin(theta)));
		cY = (((precisionX + width / 2) * Math.sin(theta)) +
				((precisionY + height / 2) * Math.cos(theta)));
		cX -= moveX;
		cY -= moveY;

		dX = (((precisionX - width / 2) * Math.cos(theta)) -
				((precisionY + height / 2) * Math.sin(theta)));
		dY = (((precisionX - width / 2) * Math.sin(theta)) +
				((precisionY + height / 2) * Math.cos(theta)));
		dX -= moveX;
		dY -= moveY;

		precisionX -= moveX;
		precisionY -= moveY;
		x -= (int)moveX;
		y -= (int)moveY;
	}

	private void renderFadingAnimation(Graphics2D render)
	{
		int ii = 0;
		int nn = 0;
		int alphaMod = 0;
		int sourceRed = 0;
		int sourceGreen = 0;
		int sourceBlue = 0;
		int alpha = 0;
		int value = 0;
		int originalPixelArray[] = null;

		PixelGrabber grabber = null;
		MemoryImageSource mis = null;

		try {
			alphaMod = 16;

			grabber = new PixelGrabber(images[DIRECTION_EAST], 0, 0, -1, -1,
					true);
			if(grabber.grabPixels()) {
				originalPixelArray = (int[])grabber.getPixels();
				mis = new MemoryImageSource(width, height, originalPixelArray,
						0, width);
				mis.setAnimated(true);
				images[DIRECTION_EAST] = gameEngine.createImage(mis);
			} 
			else {
				throw new Exception("failed to grab pixels");
			}

			for(ii = 0; ii < width; ii++) {
				for(nn = 0; nn < height; nn++) {
					// find the color components

					value = originalPixelArray[ii * height + nn];
					alpha = ((value >> 24) & 0x000000ff);
					alpha -= alphaMod;
					sourceRed = ((value >> 16) & 0x000000ff);
					sourceGreen = ((value >> 8) & 0x000000ff);
					sourceBlue = (value & 0x000000ff);
					value = (alpha << 24);
					value += (sourceRed << 16);
					value += (sourceGreen << 8);
					value += sourceBlue;

					// fill pixel array

					originalPixelArray[ii * height + nn] = value;
				}

				// send pixels to ImageConsumer

				mis.newPixels();
			}

			animationTheta = calculateRotationTheta();
			render.rotate(animationTheta, x, y);
			if(images[DIRECTION_EAST] != null) {
				render.drawImage(images[DIRECTION_EAST], (x - width / 2),
						(y - height / 2), width, height, gameEngine);
			}
			else {
				render.fillRect((x - width / 2), (y - height / 2), width,
						height);
			}
			render.rotate(-animationTheta, x, y);
	
			animationTicks++;
			if(animationTicks >= ((256 / alphaMod) - 1)) {
				isAnimating = false;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void renderFallingAnimation(Graphics2D render)
	{
		try {
			if(firstAnimationPass) {
				animationTheta = calculateRotationTheta();
				animationX = (((int)(Math.random() * 1000000.0) % 6) + 2);
				if(((int)(Math.random() * 1000000.0) % 2) == 0) {
					animationX *= -1;
				}
				animationY = (((int)(Math.random() * 1000000.0) % 6) + 2);
				if(((int)(Math.random() * 1000000.0) % 2) == 0) {
					animationY *= -1;
				}
			}

			// perform animation

			y += animationY;
			if((y < ((height + width) / 2)) &&
					(y >= (gameEngine.getWindowHeight() -
						   ((height + width) / 2)))) {
				isAnimating = false;
			}
			x += animationX;
			if((x < (gameEngine.getWindowWidth() - displayWidth)) ||
					(x > gameEngine.getWindowWidth())) {
				isAnimating = false;
			}

			animationTheta += Math.toRadians(
					Math.abs(Math.random()) * 10.0);

			// perform rotation

			render.rotate(animationTheta, x, y);

			// perform rendering

			if(images[DIRECTION_EAST] != null) {
				render.drawImage(images[DIRECTION_EAST], x, y, width, height,
						gameEngine);
			}
			else {
				render.setColor(Color.red);
				render.fillRect(x, y, width, height);
			}

			// rotate back to original position

			render.rotate(-animationTheta, x, y);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	// define public class functions

	public Brick(String name, int x, int y, int width, int height,
			int displayWidth, GameEngine gameEngine)
	{
		super(name, x, y, width, height, gameEngine);

		this.displayWidth = displayWidth;
		this.precisionX = (double)x;
		this.precisionY = (double)y;

		isAlive = true;
		firstAnimationPass = true;
		isAnimating = false;
		type = TYPE_GREEN;
		points = POINTS_GREEN;
		hitsLeft = HITS_GREEN;
		centerX = 0;
		centerY = 0;
		animationX = 0;
		animationY = 0;
		animationType = 0;
		animationTicks = 0;
		animationTheta = 0.0;

		aX = 0.0;
		aY = 0.0;
		bX = 0.0;
		bY = 0.0;
		cX = 0.0;
		cY = 0.0;
		dX = 0.0;
		dY = 0.0;

		calculateCenter();
		calculateLocation();
	}

	public boolean isAlive()
	{
		return isAlive;
	}

	public void setAlive(boolean isAlive)
	{
		this.isAlive = isAlive;
	}

	public boolean isAnimating()
	{
		return isAnimating;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		switch(type) {
			case TYPE_GREEN:
				this.type = TYPE_GREEN;
				points = POINTS_GREEN;
				hitsLeft = HITS_GREEN;
				break;
			case TYPE_BLUE:
				this.type = TYPE_BLUE;
				points = POINTS_BLUE;
				hitsLeft = HITS_BLUE;
				break;
			case TYPE_RED:
				this.type = TYPE_RED;
				points = POINTS_RED;
				hitsLeft = HITS_RED;
				break;
		}
	}

	public int getHitsLeft()
	{
		return hitsLeft;
	}

	public void setHitsLeft(int hits)
	{
		hitsLeft = hits;
	}

	public int getPoints()
	{
		return points;
	}

	public Rectangle getBoundary()
	{
		Polygon brick = null;
		Rectangle result = null;

		brick = new Polygon();
		brick.addPoint((int)aX, (int)aY);
		brick.addPoint((int)bX, (int)bY);
		brick.addPoint((int)cX, (int)cY);
		brick.addPoint((int)dX, (int)dY);
		result = brick.getBounds();

		return result;
	}

	public void kill()
	{
		isAlive = false;
		isAnimating = true;
	}

	public void kill(double ballXSpeed, double ballYSpeed)
	{
		isAlive = false;
		isAnimating = true;

		animationTheta = calculateRotationTheta();
		animationX = (int)ballXSpeed;
		if(animationX < 0) {
			animationX--;
		}
		else {
			animationX++;
		}
		animationY = (int)ballYSpeed;
		if(animationY < 0) {
			animationY--;
		}
		else {
			animationY++;
		}
		if(((int)(Math.random() * 1000000.0) % 2) == 0) {
			animationType = ANIMATION_FALLING;
		}
		else {
			animationType = ANIMATION_FADING;
		}
		firstAnimationPass = false;
	}

	public void rotate(boolean direction)
	{
		double radius = 0.0;
		double theta = 0.0;

		radius = Math.sqrt(((precisionX - (double)centerX) *
					(precisionX - (double)centerX)) +
				((precisionY - (double)centerY) *
				 (precisionY - (double)centerY)));
		theta = Math.acos((precisionX - (double)centerX) / radius);
		if(precisionY > (double)centerY) {
			theta *= -1;
		}

		if(direction == ROTATE_RIGHT) {
			theta -= ROTATE_ANGLE;
		}
		else {
			theta += ROTATE_ANGLE;
		}

		precisionX = (Math.cos(theta) * radius) + (double)centerX;
		precisionY = -(Math.sin(theta) * radius) + (double)centerY;

		this.x = (int)precisionX;
		this.y = (int)precisionY;

		calculateLocation();
	}

	public double getRotationTheta()
	{
		return calculateRotationTheta();
	}

	public void renderAnimation(Graphics2D render)
	{
		try {
			if((!isVisible) || (!isAnimating)) {
				return;
			}

			if(firstAnimationPass) {
				if(((int)(Math.random() * 1000000.0) % 2) == 0) {
					animationType = ANIMATION_FALLING;
				}
				else {
					animationType = ANIMATION_FADING;
				}
			}

			if(animationType == ANIMATION_FADING) {
				renderFadingAnimation(render);
			}
			else if(animationType == ANIMATION_FALLING) {
				renderFallingAnimation(render);
			}

			if(firstAnimationPass) {
				firstAnimationPass = false;
			}

			animationTicks++;
			if(animationTicks > 100) {
				isAnimating = false;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void render2DCalcTest(Graphics2D render)
	{
		Polygon brickPolygon = null;

		try {
			if((!isVisible) || (!isAlive)) {
				return;
			}

			brickPolygon = new Polygon();
			brickPolygon.addPoint((int)aX, (int)aY);
			brickPolygon.addPoint((int)bX, (int)bY);
			brickPolygon.addPoint((int)cX, (int)cY);
			brickPolygon.addPoint((int)dX, (int)dY);

			// perform rendering

			render.setColor(Color.green);
			render.fillPolygon(brickPolygon);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void render2D(Graphics2D render)
	{
		int localX = 0;
		int localY = 0;
		double rotation = 0.0;

		try {
			if((!isVisible) || (!isAlive)) {
				return;
			}

			localX = x;
			localY = y;

			rotation = calculateRotationTheta();

			render.rotate(rotation, x, y);

			render.drawImage(images[direction], (x - (width / 2)),
					(y - (height / 2)), width, height, gameEngine);

			render.rotate(-rotation, x, y);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

