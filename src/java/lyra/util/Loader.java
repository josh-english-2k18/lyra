/*
 * Loader.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A util public interface for defining asset loaders.
 *
 * Written by Josh English.
 */

// define package space

package lyra.util;

// import external packages

import java.awt.Graphics2D;

// define class

public interface Loader
{
	public String getClassName();

	public String getName();

	public boolean isComplete();

	public Object getLoadedAsset();

	public void start();

	public void processLoad();

	public void finalize();

	public void render2D(Graphics2D render);
}

