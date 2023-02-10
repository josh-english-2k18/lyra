/*
 * MP3DataLine.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * An implementation of the DataLine system to play MP3s.
 *
 * Written by Josh English.
 */

// package declaration

package lyra.plugins;

// external libraries

import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.Control;
import javax.sound.sampled.SourceDataLine;

// internal libraries

import lyra.util.CachedInputStream;
import lyra.plugins.codecs.mp3.Decoder;
import lyra.plugins.codecs.mp3.Header;
import lyra.plugins.codecs.mp3.Bitstream;
import lyra.plugins.codecs.mp3.SampleBuffer;

// define class

public class MP3DataLine implements Clip
{
	// define private class constants

	private static final String CLASS_NAME = MP3DataLine.class.getName();

	private static final int MODE_INACTIVE = 0;
	private static final int MODE_STOPPED = 1;
	private static final int MODE_STARTED = 2;

	private static final int ENTIRE_TRACK = -1;

	// define private class variables

	private boolean isFinished = false;
	private boolean isActive = false;
	private boolean enableReplays = false;
	private boolean firstFramePlayed = false;
	private boolean cacheComplete = false;
	private int firstFrameSize = 0;
	private int mode = MODE_INACTIVE;
	private int loopTimes = 0;
	private int loopCount = 0;
	private long framesProcessed = 0;
	private long totalFrames = 0;
	private byte[] byteBuffer = null;
	private short[] firstFrameBuffer = null;
	private CachedInputStream inputStream = null;
	private SourceDataLine source = null;
	private AudioFormat audioFormat = null;
	private Bitstream mp3Bitstream = null;
	private Decoder mp3Decoder = null;
	private Thread runThread = null;
	private Header firstHeader = null;

	// constructor

	public MP3DataLine(InputStream stream, boolean enableReplays) 
		throws LineUnavailableException
	{
		byteBuffer = new byte[4096];

		if (enableReplays) {
			this.enableReplays = true;
			inputStream = new CachedInputStream(stream);
			mp3Bitstream = new Bitstream(inputStream);
		}
		else {
			this.enableReplays = false;
			mp3Bitstream = new Bitstream(stream);
		}

		mp3Decoder = new Decoder();

		createSource();
	}

	public MP3DataLine(InputStream stream) throws LineUnavailableException
	{
		this(stream, false);
	}

	// internal methods
	
	protected void createSource() throws LineUnavailableException
	{
		// In order to properly create the source the initial frame
		// of the mp3 must be read first due to a limitation in the
		// javalayer mp3 decoder hence the following:

		try {
			firstHeader = mp3Bitstream.readFrame();
			if (firstHeader == null) {
				throw new LineUnavailableException("Can not decode header" +
						" of first frame of MP3\n");
			}

			SampleBuffer output =
				(SampleBuffer) mp3Decoder.decodeFrame(firstHeader, 
						mp3Bitstream);
			firstFrameBuffer = output.getBuffer();
			firstFrameSize = output.getBufferLength();
		}
		catch (Exception e) {
			throw new LineUnavailableException(e.getMessage());
		}

		Line line = AudioSystem.getLine(getSourceLineInfo());
		if (line instanceof SourceDataLine) {
			source = (SourceDataLine)line;
		}
	}

	protected DataLine.Info getSourceLineInfo()
	{
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, 
				getFormat());
		return info;
	}

	protected boolean decodeFrame()
	{
		return decodeFrame(false);
	}

	protected boolean decodeFrame(boolean silently)
	{
		try {
			if (!firstFramePlayed) {
				synchronized(this) {
					write(firstFrameBuffer, 0, firstFrameSize);
				}
				mp3Bitstream.closeFrame();
				framesProcessed++;
				firstFramePlayed = true;
			}

			Header header = mp3Bitstream.readFrame();

			if (header == null) {
				return false;
			}

			SampleBuffer output = 
				(SampleBuffer) mp3Decoder.decodeFrame(header, mp3Bitstream);

			if (!silently) {
				synchronized(this) {
					write(output.getBuffer(), 0, output.getBufferLength());
				}
			}
			mp3Bitstream.closeFrame();
			framesProcessed++;
		}
		catch (Exception e) {
			System.err.println("MP3DataLine Exception in decodeFrame(): " + 
					e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}

	protected void write(short[] samples, int offsets, int length)
	{
		if (source == null) {
			return;
		}

		byte[] bytes = toByteArray(samples, offsets, length);
		source.write(bytes, 0, (length * 2));
	}

	protected byte[] getByteArray(int length)
	{
		if (byteBuffer.length < length) {
			byteBuffer = new byte[length + 1024];
		}
		return byteBuffer;
	}

	protected byte[] toByteArray(short[] samples, int offsets, int length)
	{
		byte[] bytes = null;
		int index = 0;
		short sample = 0;

		bytes = getByteArray(length * 2);

		while (length-- > 0) {
			sample = samples[offsets];
			offsets++;
			bytes[index] = (byte)sample;
			index++;
			bytes[index] = (byte)(sample>>>8);
			index++;
		}
		return bytes;
	}

	protected void start(int frames, boolean silently)
	{
		if (isActive()) {
			return;
		}

		if (isFinished()) {
			if (enableReplays) {
				refresh();
			}
			else {
				return;
			}
		}

		setMode(MODE_STARTED);
		setIsActive(true);

		source.start();

		try {
			runThread = new MP3Thread(frames, silently);
			runThread.start();
		}
		catch (Exception e) {
			System.err.println("Couldn't start mp3 thread: " + e.getMessage());
		}
	}

	protected void loopCheck()
	{
		if (mode != MODE_STARTED) {
			return;
		}

		setLoopCount(getLoopCount() + 1);
		if ((getLoopCount() < getLoopTimes()) || 
				(getLoopTimes() == Clip.LOOP_CONTINUOUSLY)) {
			start();
		}
	}

	protected synchronized void refresh()
	{
		inputStream.rewind();
		setIsFinished(false);
		framesProcessed = 0;

		mp3Decoder = new Decoder();
		mp3Bitstream = new Bitstream(inputStream);
	}

	protected synchronized void setMode(int mode)
	{
		this.mode = mode;
	}

	protected synchronized void setIsActive(boolean bool)
	{
		this.isActive = bool;
	}

	protected synchronized void setIsFinished(boolean bool)
	{
		this.isFinished = bool;
	}

	protected synchronized void setCacheComplete(boolean bool)
	{
		this.cacheComplete = bool;
	}

	protected synchronized void setTotalFrames(long totalFrames)
	{
		this.totalFrames = totalFrames;
	}

	protected synchronized void setLoopCount(int loopCount)
	{
		this.loopCount = loopCount;
	}

	protected synchronized void setLoopTimes(int loopTimes)
	{
		this.loopTimes = loopTimes;
	}

	protected boolean isFinished()
	{
		return isFinished;
	}

	protected boolean isCacheComplete()
	{
		return cacheComplete;
	}

	protected long getTotalFrames()
	{
		return totalFrames;
	}

	protected int getLoopTimes()
	{
		return loopTimes;
	}

	protected int getLoopCount()
	{
		return loopCount;
	}

	protected void start(int frames)
	{
		start(frames, false);
	}

	protected void start (boolean silently)
	{
		start(ENTIRE_TRACK, silently);
	}

	// public methods

	public CachedInputStream getCachedInputStream()
	{
		return inputStream;
	}

	// Line Interface

	public void addLineListener(LineListener listener)
	{
		if (source != null) {
			source.addLineListener(listener);
		}
	}

	public synchronized void close()
	{
		if (isActive()) {
			stop();
		}

		try {
			if (mp3Bitstream != null) {
				mp3Bitstream.close();
			}
		}
		catch (Exception e) {
			System.err.println("MP3DataLine: could not close bitstream: " + 
					e.getMessage());
		}

		if (source != null) {
			source.close();
		}
	}

	public Control getControl(Control.Type control)
	{
		if (source != null) {
			return source.getControl(control);
		}
		return null;
	}

	public Control[] getControls()
	{
		if (source != null) {
			return source.getControls();
		}
		return null;
	}

	public Line.Info getLineInfo()
	{
		if (source != null) {
			return source.getLineInfo();
		}
		return null;
	}

	public boolean isControlSupported(Control.Type control)
	{
		if (source != null) {
			return source.isControlSupported(control);
		}
		return false;
	}

	public boolean isOpen()
	{
		if (source != null) {
			return source.isOpen();
		}
		return false;
	}

	public void open()
	{
		if ((source != null) && (!source.isOpen())) {
			try {
				source.open(getFormat());
			}
			catch (Exception e) {
				System.err.println("MP3DataLine: open(): " + 
						e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public void removeLineListener(LineListener listener)
	{
		if (source != null) {
			source.removeLineListener(listener);
		}
	}

	// DataLine Interface

	public void drain()
	{
		if (source != null) {
			source.drain();
		}
	}

	public void flush()
	{
		if (source != null) {
			source.flush();
		}
	}

	public void start()
	{
		start(ENTIRE_TRACK, false);
	}

	public void stop()
	{
		setLoopTimes(0);
		setLoopCount(0);
		setMode(MODE_STOPPED);
		setIsActive(false);
		while (runThread.isAlive()) {
			;
		}
		source.stop();
	}

	public boolean isRunning()
	{
		if (source != null) {
			return source.isRunning();
		}
		return false;
	}

	public boolean isActive()
	{
		return isActive;
	}

	public AudioFormat getFormat()
	{
		if (audioFormat == null) {
			audioFormat = new AudioFormat(
				mp3Decoder.getOutputFrequency(),
				16,
				mp3Decoder.getOutputChannels(),
				true,
				false);
		}
		return audioFormat;
	}

	public int getBufferSize()
	{
		if (source != null) {
			return source.getBufferSize();
		}
		return 0;
	}

	public int available()
	{
		if (source != null) {
			return source.available();
		}
		return 0;
	}

	public int getFramePosition()
	{
		return (int)framesProcessed;
	}

	public long getLongFramePosition()
	{
		return framesProcessed;
	}

	public long getMicrosecondPosition()
	{
		if (source != null) {
			return source.getMicrosecondPosition();
		}
		return 0;
	}

	public float getLevel()
	{
		if (source != null) {
			return source.getLevel();
		}
		return 0;
	}

	// clip interface

	public int getFrameLength()
	{
		if (!isCacheComplete()) {
			return -1;
		}
		return (int)getTotalFrames();
	}

	public long getMicrosecondLength()
	{
		// NOTE: for VBR mp3s the bitrate returned is an average
		// bitrate per frame so time may be slightly off in these
		// cases.

		if (!isCacheComplete()) {
			return -1;
		}

		long result = 0;
		if ((firstHeader != null)  && (inputStream != null)) {
			 long bytesPerSecond = firstHeader.bitrate() / 8;
			 if ((bytesPerSecond != 0)) {
				 long seconds = 
					 (long)inputStream.getCacheSize() / bytesPerSecond;
				 result = seconds * 1000000;
			 }
		}
		return result;
	}

	public void loop (int count)
	{
		stop();
		setLoopCount(0);
		setLoopTimes(count);
		start();
	}

	public void open (AudioFormat format, byte[] data, int offset, 
			int bufferSize)
	{
		System.err.println("MP3DataLine: open() not supported\n");
	}

	public void open(AudioInputStream stream)
	{
		System.err.println("MP3DataLine: open() not supported\n");
	}

	public void setFramePosition(int frame)
	{
		stop();
		if (!isCacheComplete()) {
			start(true);
			while (isActive()) {
				;
			}
		}

		refresh();
		start(frame, true);

		while (isActive()) {
			;
		}
	}
	
	public void setLoopPoints(int start, int end) 
	{
		System.err.println("MP3DataLine: setLoopPoints() not supported\n");
	}

	public void setMicrosecondPosition(long microseconds)
	{
		System.err.println("MP3DataLine: setMicroSecondPosition() not " +
				"supported\n");
	}
	
	public class MP3Thread extends Thread
	{
		private int frames = 0;
		private boolean silently = false;

		public MP3Thread(int frames, boolean silently)
		{
			this.frames = frames;
			this.silently = silently;
		}

		public void run() {
			boolean result = true;

			while ((result) && (mode == MODE_STARTED) && 
					((frames == ENTIRE_TRACK) || (framesProcessed < frames))) {
				result = decodeFrame(silently);
			}

			if (result == false) {
				setIsFinished(true);
				setTotalFrames(framesProcessed);
				setCacheComplete(true);
			}

			setIsActive(false);
			loopCheck();
			return;
		}
	}
}

