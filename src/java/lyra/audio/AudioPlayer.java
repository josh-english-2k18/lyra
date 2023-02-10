/*
 * AudioPlayer.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A system for playing audio streams.
 *
 * Written by Josh English.
 */

// define package space

package lyra.audio;

// import external packages

import java.util.Date;
import java.io.ByteArrayInputStream;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.AudioInputStream;
import javax.swing.Timer;

// import internal packages

import lyra.GameEngine;
import lyra.util.Config;
import lyra.util.CachedInputStream;

// define class

public class AudioPlayer
{
	// define public class constants

	public static final int MODE_READY = 1;
	public static final int MODE_PLAYING = 2;
	public static final int MODE_PAUSED = 3;
	public static final int MODE_LOOP = 4;
	public static final int VOLUME_UP = 1;
	public static final int VOLUME_DOWN = 2;
	public static final int BALANCE_LEFT = 1;
	public static final int BALANCE_RIGHT = 2;

	// define private class constants

	private static final String CLASS_NAME = AudioPlayer.class.getName();
	private static final int DEFAULT_SAMPLE_THRESHOLD = 60;
	
	// define private class variables

	private int type = 0;
	private int mode = 0;
	private long playTimeMillis = 0;
	private long elapsedTimeMillis = 0;
	private long playStartTime = 0;
	private String assetName = null;
	private CachedInputStream inputStream = null;
	private DataLine line = null;
	private GameEngine gameEngine = null;

	// define class private functions

	private double longToDouble(long value)
	{
		Long convert = null;

		convert = new Long(value);
		return convert.doubleValue();
	}

	private long doubleToLong(double value)
	{
		Double convert = null;

		convert = new Double(value);
		return convert.longValue();
	}

	// define class public functions

	public AudioPlayer(Config config, GameEngine gameEngine)
	{
		long trackLengthSeconds = 0;

		AudioStream fileStream = null;

		try {
			trackLengthSeconds = config.getLong("global",
					"trackLengthSeconds", 0);
			playTimeMillis = (trackLengthSeconds * 1000);
			this.gameEngine = gameEngine;
			fileStream = new AudioStream(config, gameEngine);
			assetName = fileStream.getName();
			inputStream = fileStream.getCachedInputStream();
			line = fileStream.getSource();
			mode = MODE_READY;
			type = fileStream.getType();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public String getClassName()
	{
		return CLASS_NAME;
	}

	public String getName()
	{
		return assetName;
	}

	public boolean isStreaming()
	{
		if((inputStream != null) && (!inputStream.isEOF())) {
			return true;
		}
		return false;
	}

	public boolean isPlaying()
	{
		if((mode == MODE_PLAYING) || (mode == MODE_LOOP)) {
			return true;
		}
		return false;
	}

	public void setPlayTimeMillis(long millis)
	{
		playTimeMillis = millis;
	}

	public long getPlayTimeRemaining()
	{
		long current = 0;
		long delta = 0;
		long result = 0;

		if(mode != MODE_PLAYING) {
			return 0;
		}

		current = (new Date()).getTime();
		delta = doubleToLong(longToDouble(current) -
				longToDouble(playStartTime));
		if(delta < playTimeMillis) {
			result = (playTimeMillis - delta);
		}

		return result;
	}

	public float getControlMinimum(FloatControl.Type type)
	{
		float result = (float)0.0;

		FloatControl control = null;

		try {
			control = (FloatControl)line.getControl(type);
			result = control.getMinimum();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public float getControlMaximum(FloatControl.Type type)
	{
		float result = (float)0.0;

		FloatControl control = null;

		try {
			control = (FloatControl)line.getControl(type);
			result = control.getMaximum();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public float getControlPrecision(FloatControl.Type type)
	{
		float result = (float)0.0;

		FloatControl control = null;

		try {
			control = (FloatControl)line.getControl(type);
			result = control.getPrecision();
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public void modifyBalance(int direction)
	{
		float precision = (float)0.0;
		float current = (float)0.0;

		FloatControl balance = null;

		try {
			if((direction != BALANCE_LEFT) && (direction != BALANCE_RIGHT)) {
				return;
			}

			balance = (FloatControl)line.getControl(FloatControl.Type.BALANCE);
			precision = balance.getPrecision();
			current = balance.getValue();

			if(direction == BALANCE_RIGHT) {
				current += precision;
				if(current > balance.getMaximum()) {
					current = balance.getMaximum();
				}
			}
			else {
				current -= precision;
				if(current < balance.getMinimum()) {
					current = balance.getMinimum();
				}
			}

			balance.setValue(current);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void modifyVolume(int direction)
	{
		float precision = (float)0.0;
		float current = (float)0.0;

		FloatControl volume = null;

		try {
			if((direction != VOLUME_UP) && (direction != VOLUME_DOWN)) {
				return;
			}

			volume = (FloatControl)line.getControl(
					FloatControl.Type.MASTER_GAIN);
			precision = volume.getPrecision();
			current = volume.getValue();

			if(direction == VOLUME_UP) {
				current += precision;
				if(current > volume.getMaximum()) {
					current = volume.getMaximum();
				}
			}
			else {
				current -= precision;
				if(current < volume.getMinimum()) {
					current = volume.getMinimum();
				}
			}

			volume.setValue(current);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void play()
	{
		try {
			if(mode != MODE_READY) {
				return;
			}
			line.start();
			playStartTime = (new Date()).getTime();
			mode = MODE_PLAYING;

			ActionListener stopTask = new ActionListener() {
				public void actionPerformed(ActionEvent event)
				{
					Timer timer = null;

					timer = (Timer)event.getSource();
					timer.stop();

					line.stop();
					synchronized(this) {
						line.flush();
						inputStream.rewind();
					}

					mode = MODE_READY;
				}
			};
			new Timer((int)playTimeMillis, stopTask).start();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void pause()
	{
		try {
			if(mode != MODE_PLAYING) {
				return;
			}
			line.stop();
			elapsedTimeMillis = (playTimeMillis - getPlayTimeRemaining());
			mode = MODE_PAUSED;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void resume()
	{
		try {
			if(mode != MODE_PAUSED) {
				return;
			}
			line.start();
			playTimeMillis -= elapsedTimeMillis;
			playStartTime = (new Date()).getTime();
			mode = MODE_PLAYING;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void stop()
	{
		try {
			if(mode != MODE_LOOP) {
				return;
			}
			line.stop();
			synchronized(this) {
				line.flush();
				inputStream.rewind();
			}
			mode = MODE_READY;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void loop()
	{
		try {
			if(mode != MODE_READY) {
				return;
			}
			line.stop();
			line.start();
			mode = MODE_LOOP;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

