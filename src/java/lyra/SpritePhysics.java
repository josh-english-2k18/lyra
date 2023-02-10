/*
 * SpritePhysics.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A simple component for calculating the physics of 2D sprites.
 *
 * Written by Josh English.
 */

// define package space

package lyra;

// define class

public class SpritePhysics
{
	// define private class constants

	private static final String CLASS_NAME = SpritePhysics.class.getName();

	// define public class constants

	public static final boolean MODE_FORWARD = true;
	public static final boolean MODE_REVERSE = false;

	public static final double DEFAULT_THRUST = 0.105;
	public static final double DEFAULT_TURBO_THRUST = 0.105;

	public static final double DEFAULT_FRICTION = 0.65;

	public static final double DEFAULT_GRAVITY = 0.096;

	// define protected class variables

	protected boolean usingTurbo = false;
	protected boolean isSliding = false;
	protected boolean hasFriction = false;
	protected boolean hasGravity = false;
	protected boolean willBounce = false;
	protected int direction = 0;
	protected int gravityDirection = 0;
	protected long currentTimeNanos = 0;
	protected long elapsedTimeNanos = 0;
	protected double x = 0.0;
	protected double y = 0.0;
	protected double xSpeed = 0.0;
	protected double ySpeed = 0.0;
	protected double xBoost = 0.0;
	protected double yBoost = 0.0;
	protected double xSpeedCalc = 0.0;
	protected double ySpeedCalc = 0.0;
	protected double thrust = 0.0;
	protected double turboThrust = 0.0;
	protected double thrustCalc = 0.0;
	protected double currentThrust = 0.0;
	protected double gravity = 0.0;
	protected double friction = 0.0;
	protected double frictionModifier = 0.0;
	protected double lowestPixelRate = 0.0;
	protected double highestPixelRate = 0.0;
	protected double defaultRotateTimeSeconds = 0.0;
	protected Sprite sprite = null;

	// define class private functions

	private void calculateMovement(int localDirection, double localThrust)
	{
		double millisPerFrame = 0.0;

		millisPerFrame = ((double)elapsedTimeNanos / 1000000.0);

		xSpeedCalc = 0.0;
		ySpeedCalc = 0.0;

		switch(localDirection) {
			case Sprite.DIRECTION_NORTH:
				ySpeedCalc -= ((localThrust * 4.0) * millisPerFrame);
				break;
			case Sprite.DIRECTION_NORTH_NORTH_EAST:
				xSpeedCalc += (((localThrust * 2.0) * 0.894454) *
						millisPerFrame);
				ySpeedCalc -= (((localThrust * 4.0) * 0.894454) *
						millisPerFrame);
				break;
			case Sprite.DIRECTION_NORTH_EAST:
				xSpeedCalc += (((localThrust * 4.0) * 0.707106) *
						millisPerFrame);
				ySpeedCalc -= (((localThrust * 4.0) * 0.707106) *
						millisPerFrame);
				break;
			case Sprite.DIRECTION_NORTH_EAST_NORTH:
				xSpeedCalc += (((localThrust * 4.0) * 0.894454) *
						millisPerFrame);
				ySpeedCalc -= (((localThrust * 2.0) * 0.894454) *
						millisPerFrame);
				break;
			case Sprite.DIRECTION_EAST:
				xSpeedCalc += ((localThrust * 4.0) * millisPerFrame);
				break;
			case Sprite.DIRECTION_SOUTH_EAST_SOUTH:
				xSpeedCalc += (((localThrust * 4.0) * 0.894454) *
						millisPerFrame);
				ySpeedCalc += (((localThrust * 2.0) * 0.894454) *
						millisPerFrame);
				break;
			case Sprite.DIRECTION_SOUTH_EAST:
				xSpeedCalc += (((localThrust * 4.0) * 0.707106) *
						millisPerFrame);
				ySpeedCalc += (((localThrust * 4.0) * 0.707106) *
						millisPerFrame);
				break;
			case Sprite.DIRECTION_SOUTH_SOUTH_EAST:
				xSpeedCalc += (((localThrust * 2.0) * 0.894454) *
						millisPerFrame);
				ySpeedCalc += (((localThrust * 4.0) * 0.894454) *
						millisPerFrame);
				break;
			case Sprite.DIRECTION_SOUTH:
				ySpeedCalc += ((localThrust * 4.0) * millisPerFrame);
				break;
			case Sprite.DIRECTION_SOUTH_SOUTH_WEST:
				xSpeedCalc -= (((localThrust * 2.0) * 0.894454) *
						millisPerFrame);
				ySpeedCalc += (((localThrust * 4.0) * 0.894454) *
						millisPerFrame);
				break;
			case Sprite.DIRECTION_SOUTH_WEST:
				xSpeedCalc -= (((localThrust * 4.0) * 0.707106) *
						millisPerFrame);
				ySpeedCalc += (((localThrust * 4.0) * 0.707106) *
						millisPerFrame);
				break;
			case Sprite.DIRECTION_SOUTH_WEST_SOUTH:
				xSpeedCalc -= (((localThrust * 4.0) * 0.894454) *
						millisPerFrame);
				ySpeedCalc += (((localThrust * 2.0) * 0.894454) *
						millisPerFrame);
				break;
			case Sprite.DIRECTION_WEST:
				xSpeedCalc -= ((localThrust * 4.0) * millisPerFrame);
				break;
			case Sprite.DIRECTION_NORTH_WEST_NORTH:
				xSpeedCalc -= (((localThrust * 4.0) * 0.894454) *
						millisPerFrame);
				ySpeedCalc -= (((localThrust * 2.0) * 0.894454) *
						millisPerFrame);
				break;
			case Sprite.DIRECTION_NORTH_WEST:
				xSpeedCalc -= (((localThrust * 4.0) * 0.707106) *
						millisPerFrame);
				ySpeedCalc -= (((localThrust * 4.0) * 0.707106) *
						millisPerFrame);
				break;
			case Sprite.DIRECTION_NORTH_NORTH_WEST:
				xSpeedCalc -= (((localThrust * 2.0) * 0.894454) *
						millisPerFrame);
				ySpeedCalc -= (((localThrust * 4.0) * 0.894454) *
						millisPerFrame);
				break;
		}
	}

	private void updateSpriteSpeed(boolean directionMode)
	{
		if(usingTurbo) {
			thrustCalc = (thrust + turboThrust);
		}
		else {
			thrustCalc = thrust;
		}
		if(directionMode == MODE_REVERSE) {
			thrustCalc /= 8.0;
		}
		currentThrust += thrustCalc;
		calculateMovement(direction, thrustCalc);
		if(directionMode == MODE_REVERSE) {
			xSpeedCalc *= -1.0;
			ySpeedCalc *= -1.0;
		}
		xSpeed += xSpeedCalc;
		ySpeed += ySpeedCalc;
	}

	private int getRoundedDouble(double value)
	{
		if((value - (double)(int)value) >= 0.5) {
			value += 1.0;
		}
		return (int)value;
	}

	private void determineAlignedWithAngularVelocity()
	{
		int localX = 0;
		int localY = 0;
		int localDestX = 0;
		int localDestY = 0;
		int spriteX = 0;
		int spriteY = 0;
		int spriteDestX = 0;
		int spriteDestY = 0;
		double localMagnitudeA = 0.0;
		double localMagnitudeB = 0.0;
		double localAngle = 0.0;
		double spriteMagnitudeA = 0.0;
		double spriteMagnitudeB = 0.0;
		double spriteAngle = 0.0;

		// calculate sprite movement with no angular velocity

		direction = sprite.getDirection();
		thrustCalc = currentThrust;
		calculateMovement(direction, thrustCalc);

		// determine coordinates

		localX = this.getX();
		localY = this.getY();
		localDestX = getRoundedDouble(x + xSpeed);
		localDestY = getRoundedDouble(y + ySpeed);
		spriteX = sprite.getX();
		spriteY = sprite.getY();
		spriteDestX = getRoundedDouble((double)spriteX + xSpeedCalc);
		spriteDestY = getRoundedDouble((double)spriteY + ySpeedCalc);

		// calcluate the local angle from the magnitude of the local vectors

		localMagnitudeA = Math.sqrt((double)(localX * localX) +
				(double)(localY * localY));
		localMagnitudeB = Math.sqrt((double)(localDestX * localDestX) +
				(double)(localDestY * localDestY));
		localAngle = Math.acos(((double)(localX * localDestX) +
				(double)(localY * localDestY)) /
				(localMagnitudeA * localMagnitudeB));

		// calcluate the sprite angle from the magnitude of the sprite vectors

		spriteMagnitudeA = Math.sqrt((double)(spriteX * spriteX) +
				(double)(spriteY * spriteY));
		spriteMagnitudeB = Math.sqrt((double)(spriteDestX * spriteDestX) +
				(double)(spriteDestY * spriteDestY));

		spriteAngle = Math.acos(((double)(spriteX * spriteDestX) +
				(double)(spriteY * spriteDestY)) /
				(spriteMagnitudeA * spriteMagnitudeB));

		// determine if has angular velocity

		if(Math.abs(localAngle - spriteAngle) > 0.001) {
			isSliding = true;
		}
		else {
			isSliding = false;
		}
	}

	private void applyFriction()
	{
		xSpeed *= (friction * frictionModifier);
		ySpeed *= (friction * frictionModifier);
		currentThrust *= (friction * frictionModifier);
	}

	private void applyBoostFriction()
	{
		xBoost *= (friction * frictionModifier);
		yBoost *= (friction * frictionModifier);
	}

	private void snapshot()
	{
		long timeNanos = 0;

		timeNanos = System.nanoTime();
		elapsedTimeNanos = (timeNanos - currentTimeNanos);
		currentTimeNanos = timeNanos;
	}

	private int invertDirection(int localDirection)
	{
		int result = 0;

		switch(localDirection) {
			case Sprite.DIRECTION_NORTH:
				result = Sprite.DIRECTION_SOUTH;
				break;
			case Sprite.DIRECTION_NORTH_NORTH_EAST:
				result = Sprite.DIRECTION_SOUTH_SOUTH_WEST;
				break;
			case Sprite.DIRECTION_NORTH_EAST:
				result = Sprite.DIRECTION_SOUTH_WEST;
				break;
			case Sprite.DIRECTION_NORTH_EAST_NORTH:
				result = Sprite.DIRECTION_SOUTH_WEST_SOUTH;
				break;
			case Sprite.DIRECTION_EAST:
				result = Sprite.DIRECTION_WEST;
				break;
			case Sprite.DIRECTION_SOUTH_EAST_SOUTH:
				result = Sprite.DIRECTION_NORTH_WEST_NORTH;
				break;
			case Sprite.DIRECTION_SOUTH_EAST:
				result = Sprite.DIRECTION_NORTH_WEST;
				break;
			case Sprite.DIRECTION_SOUTH_SOUTH_EAST:
				result = Sprite.DIRECTION_NORTH_NORTH_WEST;
				break;
			case Sprite.DIRECTION_SOUTH:
				result = Sprite.DIRECTION_NORTH;
				break;
			case Sprite.DIRECTION_SOUTH_SOUTH_WEST:
				result = Sprite.DIRECTION_NORTH_NORTH_EAST;
				break;
			case Sprite.DIRECTION_SOUTH_WEST:
				result = Sprite.DIRECTION_NORTH_EAST;
				break;
			case Sprite.DIRECTION_SOUTH_WEST_SOUTH:
				result = Sprite.DIRECTION_NORTH_EAST_NORTH;
				break;
			case Sprite.DIRECTION_WEST:
				result = Sprite.DIRECTION_EAST;
				break;
			case Sprite.DIRECTION_NORTH_WEST_NORTH:
				result = Sprite.DIRECTION_SOUTH_EAST_SOUTH;
				break;
			case Sprite.DIRECTION_NORTH_WEST:
				result = Sprite.DIRECTION_SOUTH_EAST;
				break;
			case Sprite.DIRECTION_NORTH_NORTH_WEST:
				result = Sprite.DIRECTION_SOUTH_SOUTH_EAST;
				break;
		}

		return result;
	}

	// define class public functions

	public SpritePhysics(Sprite sprite)
	{
		this.sprite = sprite;

		usingTurbo = false;
		isSliding = false;
		hasFriction = true;
		hasGravity = false;
		willBounce = false;
		direction = sprite.getDirection();
		gravityDirection = Sprite.DIRECTION_SOUTH;
		currentTimeNanos = System.nanoTime();
		elapsedTimeNanos = 0;
		x = (double)sprite.getX();
		y = (double)sprite.getY();
		xSpeed = 0.0;
		ySpeed = 0.0;
		xBoost = 0.0;
		yBoost = 0.0;
		xSpeedCalc = 0.0;
		ySpeedCalc = 0.0;
		thrust = DEFAULT_THRUST;
		turboThrust = DEFAULT_TURBO_THRUST;
		thrustCalc = 0.0;
		currentThrust = thrust;
		gravity = DEFAULT_GRAVITY;
		friction = DEFAULT_FRICTION;
		frictionModifier = 1.0;
		lowestPixelRate = 100000.0;
		highestPixelRate = 0.0;
		defaultRotateTimeSeconds = sprite.getRotateTimeSeconds();
	}

	public String getClassName()
	{
		return CLASS_NAME;
	}

	public String getName()
	{
		return sprite.getName();
	}

	public int getX()
	{
		return getRoundedDouble(x);
	}

	public int getY()
	{
		return getRoundedDouble(y);
	}

	public void setLocation(int x, int y)
	{
		this.x = (double)x;
		this.y = (double)y;
		sprite.setLocation(x, y);
	}

	public double getCurrentThrust()
	{
		return currentThrust;
	}

	public double getXSpeed()
	{
		return xSpeed;
	}

	public double getYSpeed()
	{
		return ySpeed;
	}

	public void setPhysics(double currentThrust, double xSpeed, double ySpeed)
	{
		this.currentThrust = currentThrust;
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
	}

	public void updatePhysicsOnCurrentThrust(long simulatedMillis)
	{
		double millisPerFrame = 0.0;
		double update = 0.0;

		if(simulatedMillis < 1) {
			return;
		}

		elapsedTimeNanos = (simulatedMillis * 1000000);
		millisPerFrame = ((double)elapsedTimeNanos / 1000000.0);

		update = (currentThrust * millisPerFrame);

		currentThrust *= update;
		xSpeed *= update;
		ySpeed *= update;
	}

	public boolean hasFriction()
	{
		return hasFriction;
	}

	public void setFrictionMode(boolean mode)
	{
		hasFriction = mode;
	}

	public double getFriction()
	{
		return friction;
	}

	public void setFriction(double amount)
	{
		friction = amount;
	}

	public double getFrictionModifier()
	{
		return frictionModifier;
	}

	public void setFrictionModifier(double amount)
	{
		frictionModifier = amount;
	}

	public boolean hasGravity()
	{
		return hasGravity;
	}

	public void setGravityMode(boolean mode)
	{
		hasGravity = mode;
	}

	public boolean willBounce()
	{
		return willBounce;
	}

	public void setWillBounce(boolean mode)
	{
		willBounce = mode;
	}

	public double getGravity()
	{
		return gravity;
	}

	public void setGravity(double amount)
	{
		gravity = amount;
	}

	public int getGravityDirection()
	{
		return gravityDirection;
	}

	public void setGravityDirection(int direction)
	{
		gravityDirection = direction;
	}

	public boolean isUsingTurbo()
	{
		return usingTurbo;
	}

	public void setTurboMode(boolean mode)
	{
		usingTurbo = mode;
	}

	public double getPixelRate()
	{
		double result = 0.0;

		if(xSpeed == 0.0) {
			result = Math.abs(ySpeed);
		}
		else if(ySpeed == 0.0) {
			result = Math.abs(xSpeed);
		}
		else {
			result = (Math.abs(xSpeed) * Math.abs(ySpeed));
		}

		return result;
	}

	public double getRate()
	{
		double result = 0.0;

		result = Math.sqrt((double)(xSpeed * xSpeed) +
				(double)(ySpeed * ySpeed));

		return result;
	}

	public boolean isSliding()
	{
		return isSliding;
	}

	public void simpleBounce(boolean xAxis, boolean yAxis)
	{
		if(xAxis) {
			xSpeed *= -1.0;
		}
		if(yAxis) {
			ySpeed *= -1.0;
		}
		direction = invertDirection(sprite.getDirection());
		sprite.resetDirection(direction);
	}

	public void antiGravityBounce(boolean xAxis, boolean yAxis,
			long simulatedMillis)
	{
		int localDirection = 0;

		elapsedTimeNanos = (simulatedMillis * 1000000);
		localDirection = invertDirection(gravityDirection);

		calculateMovement(localDirection, gravity);
		if(hasFriction) {
			xSpeed += xSpeedCalc;
			ySpeed += ySpeedCalc;
		}
		else {
			xBoost += xSpeedCalc;
			yBoost += ySpeedCalc;
		}
		sprite.setLocation(this.getX(), this.getY());

		direction = invertDirection(sprite.getDirection());
		sprite.resetDirection(direction);
	}

	public void processPhysics(boolean directionMode)
	{
		snapshot();

		direction = sprite.getDirection();
		updateSpriteSpeed(directionMode);
		determineAlignedWithAngularVelocity();
		if(!hasFriction) {
			applyFriction(); // normalize the speed
		}

		// apply gravity

		if(hasGravity) {
			calculateMovement(gravityDirection, gravity);
			x += xSpeedCalc;
			y += ySpeedCalc;
		}

		sprite.setLocation(this.getX(), this.getY());
	}

	public void simulatePhysics(boolean directionMode, boolean useGravity,
			long simulatedMillis)
	{
		if(simulatedMillis < 1) {
			return;
		}

		elapsedTimeNanos = (simulatedMillis * 1000000);

		direction = sprite.getDirection();
		updateSpriteSpeed(directionMode);
		determineAlignedWithAngularVelocity();
		if(!hasFriction) {
			applyFriction(); // normalize the speed
		}

		// apply gravity

		if((useGravity) && (hasGravity)) {
			calculateMovement(gravityDirection, gravity);
			x += xSpeedCalc;
			y += ySpeedCalc;
		}

		sprite.setLocation(this.getX(), this.getY());
	}

	public void processGameplay()
	{
		double pixelRate = 0.0;
		double ratio = 0.0;
		double average = 0.0;
		double rotateTimeSeconds = 0.0;

		// apply friction to speed & update location

		snapshot();

		if(hasFriction) {
			applyFriction();
		}

		applyBoostFriction();

		if(Math.abs(xSpeed) < 0.01) {
			xSpeed = 0.0;
		}
		if(Math.abs(ySpeed) < 0.01) {
			ySpeed = 0.0;
		}
		if(Math.abs(xBoost) < 0.01) {
			xBoost = 0.0;
		}
		if(Math.abs(yBoost) < 0.01) {
			yBoost = 0.0;
		}
		if(Math.abs(currentThrust) < 0.01) {
			currentThrust = 0.0;
		}
		if((xSpeed == 0.0) && (ySpeed == 0.0)) {
			isSliding = false;
		}

		x += xSpeed;
		y += ySpeed;
		x += xBoost;
		y += yBoost;

		// apply gravity

		if(hasGravity) {
			calculateMovement(gravityDirection, gravity);
			x += xSpeedCalc;
			y += ySpeedCalc;
		}

		// update sprite location

		sprite.setLocation(this.getX(), this.getY());

		// normalize sprite rotation

		pixelRate = getPixelRate();
		if(pixelRate > highestPixelRate) {
			highestPixelRate = pixelRate;
		}
		if(pixelRate < lowestPixelRate) {
			lowestPixelRate = pixelRate;
		}

		if((highestPixelRate - lowestPixelRate) > 200.0) {
			ratio = (pixelRate / (highestPixelRate + lowestPixelRate));
			if(((ratio * 100.0) < 0.1) || ((ratio * 100.0) > 99.9)) {
				sprite.setRotateTimeSeconds(defaultRotateTimeSeconds);
				return;
			}
			average = ((highestPixelRate + lowestPixelRate) / 2.0);
			ratio = (Math.abs(pixelRate - average) / average);
			rotateTimeSeconds = (defaultRotateTimeSeconds +
					(defaultRotateTimeSeconds * ratio));
			sprite.setRotateTimeSeconds(rotateTimeSeconds);
		}
	}
}

