/*
 * CachedInputStream.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A utility for extending the FilterInputStream to include an in-memory
 * cache of the input stream.
 *
 * Written by Josh English.
 */

// define package space

package lyra.util;

// external libraries

import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.IOException;

// internal libraries

public class CachedInputStream extends FilterInputStream
{
	// define private class constants

	private static final String CLASS_NAME = CachedInputStream.class.getName();
	private static final int DEFAULT_INITIAL_CACHE_SIZE = 4096;

	// define private class variables

	private boolean isEOF = false;
	private int markPosition = 0;
	private int nextRead = 0;
	private int cacheSize = 0;
	private byte[] cache = null;

	// define class protected functions

	protected void growCache()
	{
		byte[] newCache = null;

		newCache = new byte[(cache.length * 2)];
		System.arraycopy(cache, 0, newCache, 0, cache.length);
		cache = newCache;
	}

	// define class public functions

	public CachedInputStream(InputStream in, int cacheStartSize)
	{
		super(in);
		cache = new byte[cacheStartSize];
	}

	public CachedInputStream(InputStream in)
	{
		this(in, DEFAULT_INITIAL_CACHE_SIZE);
	}

	// overidden FilterInputStream functions 

	public int available() throws IOException
	{
		return ((cacheSize - nextRead) + super.available());
	}

	public void mark(int readlimit)
	{
		markPosition = nextRead;
	}

	public boolean markSupported()
	{
		return true;
	}

	public void reset()
	{
		nextRead = markPosition;
	}

	public int read() throws IOException
	{
		int cachePos = 0;
		int cacheResult = -1;
		int result = -1;

		if(nextRead < cacheSize) {
			if(super.available() > 0) {
				cacheResult = super.read();
				if(cacheResult != -1) {
					cachePos = cacheSize;
					if(cachePos >= cache.length) {
						growCache();
					}
					cache[cachePos] = (byte)cacheResult;
					cacheSize++;
				}
			}
			result = (int) (cache[nextRead] & 0xff);
			nextRead++;
		}
		else if((nextRead == cacheSize) && (isEOF)) {
			result = -1;
		}
		else {
			result = super.read();
			if(result != -1) {
				if(nextRead >= cache.length) {
					growCache();
				}
				cache[nextRead] = (byte)result;
				cacheSize++;
				nextRead++;
			}
			else {
				isEOF = true;
			}
		}

		return result;
	}

	public int read(byte[] b) throws IOException
	{
		return read(b, 0, b.length);
	}

	public int read(byte[] b, int off, int len) throws IOException
	{
		int result = 0;
		int bytesRead = 0;

		if((b == null) || (off < 0) || (len < 0) || ((off + len) > b.length)) {
			throw new IOException("argument(s) missing or invalid, b is " + b +
					", off is " + off + ", length is " + len + ".");
		}

		for(bytesRead = 0; bytesRead < len; bytesRead++) {
			result = read();

			if(result == -1) {
				break;
			}

			b[(off + bytesRead)] = (byte)result;
		}

		return bytesRead;
	}

	public long skip(long n) throws IOException
	{
		int result = 0;
		long bytesRead = 0;

		for(bytesRead = 0; bytesRead < n; bytesRead++) {
			result = read();
			if(result == -1) {
				break;
			}
		}

		return bytesRead;
	}

	public void close() throws IOException
	{
		super.close();
		isEOF = true;
	}

	// non-overridden functions

	public String getClassName()
	{
		return CLASS_NAME;
	}

	public void rewind()
	{
		nextRead = 0;
	}

	public int getMarkTarget()
	{
		return nextRead;
	}

	public void rewindToTarget(int target)
	{
		nextRead = target;
	}

	public int getCacheSize()
	{
		return cacheSize;
	}

	public boolean isEOF()
	{
		return this.isEOF;
	}

	public byte[] getCache()
	{
		return this.cache;
	}
}

