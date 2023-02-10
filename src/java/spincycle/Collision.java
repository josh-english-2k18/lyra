/*
 * Collision.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * The Spincycle game collision system.
 *
 * Written by Josh English.
 */

// define package space

package spincycle;

// import external packages

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

// define class

public class Collision
{
	// define private class constants

	private static final String CLASS_NAME = Collision.class.getName();

	private static final int ASYMPTOTE_FACTOR = 3000;

	private static final double COLLISION_FUDGE_FACTOR = 6.0;

	// define private class variables

	private int myColor = 0;

	// define private class functions

	private boolean isBetweenFudge(double value, double valueA, double valueB,
			double fudge)
	{
		double low = 0.0;
		double high = 0.0;

		if(valueA > valueB) {
			low = valueB - fudge;
			high = valueA + fudge;
		}
		else if(valueA < valueB) {
			low = valueA - fudge;
			high = valueB + fudge;
		}
		else if(valueA == valueB) {
			if(Math.abs(value - valueA) < fudge) {
				return true;
			}
			return false;
		}

		if((low <= value) && (value <= high)) {
			return true;
		}

		return false;
	}

	// define public class functions

	public Collision()
	{
		myColor = 1;
	}

	public boolean isCollided(Ball ball, Brick brick)
	{
		Rectangle ballBoundary = null;
		Rectangle brickBoundary = null;

		ballBoundary = ball.getBoundary();
		brickBoundary = brick.getBoundary();

		if(ballBoundary.intersects(brickBoundary)) {
			return true;
		}
		return false;
	}

	public double computeCornerCollision(double xb, double yb, int ballRadius,
			double dx, double dy, double xp, double yp, int brick)
	{
		double distanceToCorner = 0.0;
		double distanceToCollision = 0.0;
		double progress = 0.0;
		double theta = 0.0;
		double usefulTheta = 0.0;
		double cornerVectorX = 0.0;
		double cornerVectorY = 0.0;
		double normalCornerVectorX = 0.0;
		double normalCornerVectorY = 0.0;
		double cornerVectorMagnitude = 0.0;
		double normalVelocityX = 0.0;
		double normalVelocityY = 0.0;
		double velocityMagnitude = 0.0;

		// law of sines + cosines

		velocityMagnitude = Math.sqrt((dy * dy) + (dx * dx));
		normalVelocityX = dx / velocityMagnitude;
		normalVelocityY = dy / velocityMagnitude;
		
		cornerVectorX = xp - xb;
		cornerVectorY = yp - yb;
		cornerVectorMagnitude = Math.sqrt((cornerVectorX * cornerVectorX) +
				(cornerVectorY * cornerVectorY));
		normalCornerVectorX = cornerVectorX / cornerVectorMagnitude;
		normalCornerVectorY = cornerVectorY / cornerVectorMagnitude;
		
		theta = Math.acos((normalCornerVectorX * normalVelocityX) +
				(normalCornerVectorY * normalVelocityY));
		distanceToCorner = Math.sqrt(((xp - xb) * (xp - xb)) + ((yp - yb) *
					(yp - yb)));
		usefulTheta = distanceToCorner * theta / ballRadius;

		distanceToCollision = Math.sqrt((ballRadius * ballRadius) +
				(distanceToCorner * distanceToCorner) - 
				(2 * distanceToCorner * ballRadius * Math.cos(usefulTheta)));
		
		if(distanceToCollision < velocityMagnitude) {
			return distanceToCollision;
		}

		return Double.MAX_VALUE;
	}

	public double computeCollision(double xb, double yb, int ballRadius,
			double dx, double dy, double x1, double y1, double x2, double y2,
			int brick, Graphics2D render)
	{
		double ballLineSlope = 0.0;
		double brickLineSlope = 0.0;
		double perpLineSlope = 0.0;
		double intersectionX = 0.0;
		double intersectionY = 0.0;
		double heightX = 0.0;
		double heightY = 0.0;
		double touchpointX = 0.0;
		double touchpointY = 0.0;
		double distanceToCollision = 0.0;
		double distanceToIntersection = 0.0;
		double heightLength = 0.0;
		double normalizedBallVectorX = 0.0;
		double normalizedBallVectorY = 0.0;
	 	double perpVectorX = 0.0;
		double perpVectorY = 0.0;
		double normalizedPerpVectorX = 0.0;
		double normalizedPerpVectorY = 0.0;
		double velocityMagnitude = 0.0;

		if(render != null) {
			render.setColor(Color.white);
			render.drawLine((int)x1, (int)y1, (int)x2, (int)y2);
		}

		if(dx != 0.0) {
			ballLineSlope = (dy / dx);
		}
		else {
			ballLineSlope = (dy * ASYMPTOTE_FACTOR);
		}

		if((x2 - x1) == 0.0) {
			brickLineSlope = (Math.abs((y2 - y1)) * ASYMPTOTE_FACTOR);
		}
		else {
			brickLineSlope = ((y2 - y1) / (x2 - x1));
		}

		if((y2 - y1) == 0.0) {
			perpLineSlope = (Math.abs((x2 - x1)) * ASYMPTOTE_FACTOR);
			perpVectorX = Math.abs((x2 - x1));
			perpVectorY = 0.0;
		}
		else {
			perpLineSlope = (-1.0 / brickLineSlope);
			perpVectorX = 1.0;
			perpVectorY = (-1.0 / brickLineSlope);
		}

		if(dx * perpVectorX + dy * perpVectorY < 0) {
			perpVectorX *= -1.0;
			perpVectorY *= -1.0;
		}	

		if(ballLineSlope == brickLineSlope) {
			return Double.MAX_VALUE;
		}

		// find the height

		heightX = ((-1.0 * perpLineSlope * xb) + yb - y1 +
				(brickLineSlope * x1)) / (brickLineSlope - perpLineSlope);
		heightY = y1 + brickLineSlope * (heightX - x1);

		if(render != null) {
			if(myColor == 1) {
				render.setColor(Color.black);
				myColor = 2;
			}
			else if(myColor == 2) {
				render.setColor(Color.blue);
				myColor = 3;
			}
			else if(myColor == 3) {
				render.setColor(Color.yellow);
				myColor = 4;
			}
			else if(myColor == 4) {
				render.setColor(Color.green);
				myColor = 1;
			}
			render.drawLine((int)heightX, (int)heightY, (int)xb, (int)yb);
		}

		// find the intersection

		intersectionX = ((ballLineSlope * xb) - (brickLineSlope * x1) + y1 - 
				yb) / (ballLineSlope - brickLineSlope);
		intersectionY = yb + ballLineSlope * (intersectionX - xb);

		if(render != null) {
			render.setColor(Color.green);
			render.fillOval((int)intersectionX, (int)intersectionY, 2, 2);
		}

		// find the distance

		distanceToIntersection = Math.sqrt((((intersectionX - xb) *
						(intersectionX - xb)) + ((intersectionY - yb) *
												 (intersectionY - yb))));
		heightLength = Math.sqrt((((heightX - xb) * (heightX - xb)) +
					((heightY - yb) * (heightY - yb))));

	
		// find the collision distance

		distanceToCollision = (distanceToIntersection -
				(distanceToIntersection * (ballRadius / heightLength)));

		// normalize the vectors

		normalizedBallVectorX = dx / Math.sqrt( (dx * dx) + (dy * dy) );
		normalizedBallVectorY = dy / Math.sqrt( (dx * dx) + (dy * dy) );

		normalizedPerpVectorX = perpVectorX /
			(Math.sqrt((perpVectorX * perpVectorX) +
					   (perpVectorY * perpVectorY)));
		normalizedPerpVectorY = perpVectorY /
			(Math.sqrt((perpVectorX * perpVectorX) +
					   (perpVectorY * perpVectorY)));

		// apply magnitude of ball movement
	
		velocityMagnitude = Math.sqrt((dy * dy) + (dx * dx));
		if(Math.abs(distanceToCollision) > velocityMagnitude) {
			return Double.MAX_VALUE;
		}

		touchpointX = xb + (normalizedBallVectorX * distanceToCollision) +
			(normalizedPerpVectorX * ballRadius);
		touchpointY = yb + (normalizedBallVectorY * distanceToCollision) +
			(normalizedPerpVectorY * ballRadius);

		if((heightLength < (double)ballRadius) &&
				(((!isBetweenFudge(touchpointX, x1, x2,
								  COLLISION_FUDGE_FACTOR)) ||
				  (!isBetweenFudge(touchpointY, y1, y2,
								  COLLISION_FUDGE_FACTOR))))) {
			return Double.MAX_VALUE;
		}

		if(render != null) {
			render.setColor(Color.yellow);
			render.fillOval((int)touchpointX, (int)touchpointY, 3, 3);
		}

		if((isBetweenFudge(touchpointX, x1, x2, COLLISION_FUDGE_FACTOR)) &&
				(isBetweenFudge(touchpointY, y1, y2, COLLISION_FUDGE_FACTOR))) {
/*
			System.out.println("ballRadius: " + ballRadius);
			System.out.println("heightLength: " + heightLength);
			System.out.println("touchpointX: " + touchpointX);
			System.out.println("touchpointY: " + touchpointY);
			System.out.println("distanceToIntersection: " +
					distanceToIntersection);
			System.out.println("distanceToCollision: " + distanceToCollision);
*/
			return distanceToCollision;
		}
		else {
			return Double.MAX_VALUE;
		}
	}
}

