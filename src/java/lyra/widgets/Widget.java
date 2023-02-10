/*
 * Widget.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A GUI public interface for defining widgets.
 *
 * Written by Josh English.
 */

// define package space

package lyra.widgets;

// import external packages

import java.awt.event.KeyEvent;
import java.awt.Graphics2D;

// define class

public interface Widget
{
	public String getClassName();

	public String getName();

	public int getX();

	public int getY();

	public int getWidth();

	public int getHeight();

	public void setLocation(int x, int y);

	public void setVisibility(boolean mode);

	public boolean isVisible();

	public boolean hasFocus(int mouseX, int mouseY);

	public void deFocus();

	public boolean isClicked(int mouseX, int mouseY);

	public void keyPressed(KeyEvent event);

	public void render2D(Graphics2D render);
}

