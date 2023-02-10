/*
 * RenderableInterface.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * An interface to define if any given entity can render within the game engine.
 *
 * Written by Josh English.
 */

// define package space

package lyra.interfaces;

// import external packages

import java.awt.Graphics2D;

// define class

public interface RenderableInterface
{
	public void render2D(Graphics2D render);
}

