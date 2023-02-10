/*
 * AudioStream.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A system for streaming audio files from a file or a socket.
 *
 * Written by Josh English.
 */

// define package space

package lyra.audio;

// import external packages

import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;

// import internal packages

import lyra.GameEngine;
import lyra.util.Config;
import lyra.util.CachedInputStream;
import lyra.plugins.MP3DataLine;

// define class

public class AudioStream
{
	// define private class constants

	private static final String CLASS_NAME = AudioStream.class.getName();
	private static final int PIPE_SIZE = 262144;
	private static final int BLOCK_SIZE = 32768;
	private static final int READ_THRESHOLD = 1024;
	private static final int READ_SIZE = 8192;//1024;

	// define public class constants

	public static final int WAV = 1;
	public static final int MP3 = 2;
	public static final int UNKNOWN = -1;

	// define private class variables

	private boolean isFinished = false;
	private int type = UNKNOWN;
	private String name = null;
	private AudioFormat format = null;
	private DataLine source = null;
	private CachedInputStream inputStream = null;
	private Config config = null;
	private GameEngine gameEngine = null;

	// define private class objects

	private class BufferedWriter implements Runnable
	{
		private BufferedInputStream external = null;
		private OutputStream output = null;

		public BufferedWriter(BufferedInputStream external, OutputStream output)
		{
			Thread thread = null;

			this.external = external;
			this.output = output;
			thread = new Thread(this);
			thread.start();
		}

		public void run()
		{
			int result = 0;
			byte[] buffer = null;

			buffer = new byte[BLOCK_SIZE];

			System.out.println("{" + CLASS_NAME +
					"} [BufferedWriter] running for '" + name + "'");

			try {
				while(!isFinished) {
					result = external.read(buffer);
					if(result < 0) {
						external.close();
						output.close();
						isFinished = true;
					}
					else if(result > 0) {
						output.write(buffer, 0, result);
					}
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}

			System.out.println("{" + CLASS_NAME +
					"} [BufferedWriter] shut down for '" + name + "'");
		}
	}

	private class Reader implements Runnable
	{
		private CachedInputStream input = null;
		private SourceDataLine source = null;

		public Reader(CachedInputStream input, SourceDataLine source)
		{
			Thread thread = null;
			this.input = input;
			this.source = source;
			thread = new Thread(this);
			thread.start();
		}

		public void run()
		{
			int result = 0;
			int length = 0;
			int target = 0;
			int lengthWritten = 0;
			int totalLength = 0;
			byte[] buffer = null;

			System.out.println("{" + CLASS_NAME + "} [AudioReader] running");

			try {
				buffer = new byte[READ_SIZE];

				while(true) {
					if(!source.isOpen()) {
						try {
							source.open(format);
						}
						catch(Exception e) {
							e.printStackTrace();
						}
					}

					if((isFinished) &&
							(input.getMarkTarget() >=
							 (input.getCacheSize() - 4))) {
						input.rewind();
					}

					if((source.available() >= READ_THRESHOLD) &&
							(lengthWritten > 0)) {
						input.rewindToTarget(target);
						lengthWritten = 0;
					}

					result = input.available();
					if(result == 0) {
						continue;
					}
					else if((result >= 4) && (source.available() >= 4)) {
						if(result > source.available()) {
							result = source.available();
						}
						while(((result % 4) != 0) && (result > 4)) {
							result--;
						}
						if(result < 4) {
							continue;
						}
					}
					else if((result % 4) != 0) {
						continue;
					}
					if(result >= buffer.length) {
						length = buffer.length;
					}
					else {
						length = result;
					}
					result = input.read(buffer, 0, length);
					if(result == -1) {
						break;
					}
					else if(result == 0) {
						continue;
					}

					if(source.available() >= result) {
						if(lengthWritten > 0) {
							input.rewindToTarget(target);
							lengthWritten = 0;
							result = input.read(buffer, 0, length);
							if(result == -1) {
								break;
							}
							else if(result == 0) {
								continue;
							}
						}
						while(((result % 4) != 0) && (result > 4)) {
							result--;
						}
						if(result < 4) {
							continue;
						}
						synchronized(this) {
							source.write(buffer, 0, result);
						}
					}
					else {
						if(lengthWritten == 0) {
							target = input.getMarkTarget();
						}
						lengthWritten += result;
					}

					totalLength = input.getCacheSize();
				}

				input.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}

			System.out.println("{" + CLASS_NAME + "} [AudioReader] shut down");
		}
	}

	// define private class functions

	private AudioFormat determineFormat()
	{
		boolean isSigned = false;
		boolean isBigEndian = false;
		int channels = 0;
		int sampleBitSize = 0;
		float sampleRate = (float)0.0;

		AudioFormat result = null;

		try {
			isSigned = config.getBoolean(
					"global", "isSigned", true).booleanValue();
			isBigEndian = config.getBoolean(
					"global", "isBigEndian", false).booleanValue();
			channels = config.getLong(
					"global", "channels", 2).intValue();
			sampleBitSize = config.getInteger(
					"global", "sampleBitSize", 16).intValue();
			sampleRate = config.getDouble(
					"global", "sampleRate", 44100).floatValue();
			result = new AudioFormat(sampleRate, sampleBitSize, channels,
					isSigned, isBigEndian);
		}
		catch(Exception e) {
			e.printStackTrace();
			result = null;
		}

		return result;
	}

	private BufferedInputStream obtainInputStream()
	{
		ByteArrayInputStream input = null;
		BufferedInputStream result = null;

		try {
			input = gameEngine.getAssetCache().obtainAssetStream(name);
			result = new BufferedInputStream(input);
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private DataLine getWavDataLine()
	{
		Reader reader = null;
		BufferedWriter writer = null;
		BufferedInputStream bufferedInput = null;
		PipedInputStream pipedInputStream = null;
		PipedOutputStream outputStream = null;
		SourceDataLine sourceDataLine = null;

		try {
			name = config.getString("global", "url");

			if(name == null) {
				return null;
			}

			format = determineFormat();
			bufferedInput = obtainInputStream();
			sourceDataLine = (SourceDataLine)AudioSystem.getLine(
				new DataLine.Info(SourceDataLine.class, format));
			sourceDataLine.open(format);
			outputStream = new PipedOutputStream();
			pipedInputStream = new PipedInputStream(PIPE_SIZE);
			pipedInputStream.connect(outputStream);
			inputStream = new CachedInputStream(pipedInputStream);
			writer = new BufferedWriter(bufferedInput, outputStream);
			reader = new Reader(inputStream, sourceDataLine);
			type = WAV;
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return sourceDataLine;
	}

	private DataLine getMp3DataLine()
	{
		BufferedWriter writer = null;
		MP3DataLine mp3Line = null;
		BufferedInputStream bufferedInput = null;
		PipedOutputStream pipedOutputStream = null;
		PipedInputStream pipedInputStream = null;

		try {
			name = config.getString("global", "url");

			if(name == null) {
				return null;
			}

			bufferedInput = obtainInputStream();
			pipedOutputStream = new PipedOutputStream();
			pipedInputStream = new PipedInputStream(PIPE_SIZE);
			pipedInputStream.connect(pipedOutputStream);
			writer = new BufferedWriter(bufferedInput, pipedOutputStream);
			mp3Line = new MP3DataLine(pipedInputStream, true);
			mp3Line.open();
			inputStream = mp3Line.getCachedInputStream();
			type = MP3;
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return mp3Line;
	}

	// define public class functions

	public AudioStream(Config config, GameEngine gameEngine)
	{
		String type = null;

		try {
			this.config = config;
			this.gameEngine = gameEngine;

			isFinished = false;
			this.type = UNKNOWN;
			name = null;
			format = null;
			source = null;
			inputStream = null;

			type = config.getString("global", "type");
			if ((type == null) || (type.equals("wav"))) {
				source = getWavDataLine();
			}
			else if (type.equals("mp3")) {
				source = getMp3DataLine();
			}
			else {
				throw new Exception("{" + CLASS_NAME +
						"} unknown audio type for '" +
						config.getString("global", "url", "unknown"));
			}
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
		return name;
	}

	public boolean isFinished()
	{
		return isFinished;
	}

	public CachedInputStream getCachedInputStream()
	{
		return inputStream;
	}

	public DataLine getSource()
	{
		return source;
	}

	public int getType()
	{
		return type;
	}
}

