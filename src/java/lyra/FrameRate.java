/*
 * FrameRate.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A simple framerate calculator.
 *
 * Written by Josh English.
 */

// define package space

package lyra;

// import external packages

import java.util.Date;

// define class

public class FrameRate
{
	// define private class constants

	private static final String CLASS_NAME = FrameRate.class.getName();
	private static final int DEFAULT_SAMPLE_THRESHOLD = 60;
	
	// define private class variables

	private int sampleThreshold = 0;
	private int frameCounter = 0;
	private double frameRate = 0.0;
	private long prevTime = 0;

	// define class public functions

	public FrameRate()
	{
		sampleThreshold = DEFAULT_SAMPLE_THRESHOLD;
		frameCounter = 0;
		frameRate = 60.0;
		prevTime = (new Date()).getTime();
	}

	public String getClassName()
	{
		return CLASS_NAME;
	}

	public void setSampleThreshold(int threshold)
	{
		sampleThreshold = threshold;
	}

	public void calculate()
	{
		long current = 0;
		double delta = 0.0;

		Long currentConv = null;
		Long prevConv = null;
		Integer sampleConv = null;

		frameCounter++;
		if(frameCounter >= sampleThreshold) {
			current = (new Date()).getTime();
			currentConv = new Long(current);
			prevConv = new Long(prevTime);
			delta = ((currentConv.doubleValue() - prevConv.doubleValue()) /
					1000.0);
			prevTime = current;
			sampleConv = new Integer(sampleThreshold);
			frameRate = (sampleConv.doubleValue() / delta);
			frameCounter = 0;
		}
	}

	public synchronized int getFrameCounter()
	{
		return frameCounter;
	}

	public synchronized double get()
	{
		return frameRate;
	}
}

