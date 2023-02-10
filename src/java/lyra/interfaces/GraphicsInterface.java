/*
 * GraphicsInterface.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * An interface to define a rendering-pipeline graphical component.
 *
 * Written by Josh English.
 */

// define package space

package lyra.interfaces;

// import external packages

import java.awt.Rectangle;

// define class

public interface GraphicsInterface extends RenderableInterface
{
	public String getClassName();

	public String getName();

	public int getX();

	public int getY();

	public int getWidth();

	public int getHeight();

	public void setLocation(int x, int y);

	public void resize(int width, int height);

	public Rectangle getLocation();

	public boolean isVisible();

	public boolean applyCamera(int cameraX, int cameraY, int cameraWidth,
			int cameraHeight);
}

