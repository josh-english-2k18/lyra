/*
 * TextBox.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A GUI widget for handling text boxes.
 *
 * Written by Josh English.
 */

// define package space

package lyra.widgets;

// import external packages

import java.util.LinkedList;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.KeyEvent;

// import internal packages

import lyra.GameEngine;

// define class

public class TextBox implements Widget
{
	// define private class constants

	private static final String CLASS_NAME = TextBox.class.getName();
	private static final int DEFAULT_HISTORY_SIZE = 32;
	private static final int DEFAULT_MAX_CHARS = 64;
	private static final int BLINK_COUNT = 64;
	private static final String PASSWORD_MASK = "*";

	// define public class constants
	
	public static final int STATE_NORMAL = 0;
	public static final int STATE_FOCUSED = 1;

	// define protected class variables

	protected boolean isVisible = false;
	protected boolean wasClicked = false;
	protected boolean isMasked = false;
	protected boolean newOnCommit = true;
	protected int state = 0;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected int maxChars = 0;
	protected int fontX = 0;
	protected int fontY = 0;
	protected int historySize = 0;
	protected int historyIndex = 0;
	protected int bufferIndex = 0;
	protected int visualBeg = 0;
	protected int visualEnd = 0;
	protected int blink = 0;
	protected Color displayColor = null;
	protected Color backgroundColor = null;
	protected String name = null;
	protected StringBuffer submitted = null;
	protected LinkedList contents;
	protected FontInfo fontInfo = null;
	protected Cursor gameCursor = null;
	protected Cursor textCursor = null;
	protected CorneredBox corneredBox = null;
	protected GameEngine gameEngine = null;

	// define private class methods

	private void calculateFontCoords()
	{
		fontX = (x + 2);
		fontY = (y + (int)((double)height * 0.85));
	}

	private void commitBuffer()
	{
		submitted = (StringBuffer)contents.get(historyIndex);
		if(newOnCommit) {

			if(historyIndex != contents.size() - 1) {
				contents.set(
					contents.size() -1,
					contents.get(historyIndex));
			}

			contents.add(new StringBuffer(""));
			historyIndex = contents.size() - 1;
			bufferIndex = 0;
		}

		if(contents.size() > historySize) {
			contents.removeFirst(); 
			historyIndex = contents.size() - 1;
		}
	}

	private StringBuffer getContentsBuffer()
	{
		return (StringBuffer)contents.get(historyIndex);
	}

	private void calculateVisualIndices()
	{
		int stringWidth = 0;
		String buffer = null;

		buffer = getContents();

		if(isPasswordMasked()) {
			buffer = buffer.replaceAll(".", PASSWORD_MASK);
		}

		fontInfo.setString(buffer);
		stringWidth = fontInfo.getWidth();

		if(stringWidth < width) {
			visualBeg = 0;
			visualEnd = buffer.length();
			return;
		}

		visualEnd = bufferIndex;
		for(visualBeg = 0;
				fontInfo.getWidth(buffer.substring(visualBeg,
						visualEnd)) > width;
				visualBeg++) {
			// iterate
		}

		if(visualBeg != 0) {
			return;
		}

		for(visualEnd = bufferIndex;
				fontInfo.getWidth(buffer.substring(visualBeg,
						visualEnd)) < width;
				visualEnd++) {
			// iterate
		}

		visualEnd--;

		return;
	}

	// define public class methods

	public TextBox(String name, int x, int y, int width, int height,
			GameEngine gameEngine)
	{
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.gameEngine = gameEngine;

		isVisible = true;
		wasClicked = false;
		isMasked = false;
		newOnCommit = true;
		state = STATE_NORMAL;
		maxChars = DEFAULT_MAX_CHARS;
		fontX = 0;
		fontY = 0;
		historySize = DEFAULT_HISTORY_SIZE;
		historyIndex = 0;
		bufferIndex = 0;
		visualBeg = 0;
		visualEnd = 0;
		blink = 0;
		displayColor = Color.black;
		backgroundColor = Color.white;
		submitted = null;
		contents = new LinkedList();
		contents.add(new StringBuffer(""));
		fontInfo = new FontInfo(gameEngine);
		gameCursor = gameEngine.getCursor();
		textCursor = new Cursor(Cursor.TEXT_CURSOR);
		corneredBox = new CorneredBox(name + "CorneredBox", x, y, width,
				height, gameEngine);
		calculateFontCoords();
	}

	public String getClassName()
	{
		return CLASS_NAME;
	}

	public String getName()
	{
		return name;
	}

	public int getX()
	{
		return x;
	}

	public int getY()
	{
		return y;
	}

	public int getWidth()
	{
		return width;
	}

	public int getHeight()
	{
		return height;
	}

	public void setLocation(int x, int y)
	{
		this.x = x;
		this.y = y;
		corneredBox.setLocation(x, y);
	}

	public void resize(int width, int height)
	{
		this.width = width;
		this.height = height;
		corneredBox.resize(width, height);
	}

	public void setVisibility(boolean mode)
	{
		isVisible = mode;
	}

	public boolean isVisible()
	{
		return isVisible;
	}

	public boolean hasFocus(int mouseX, int mouseY)
	{
		boolean result = false;

		if(!isVisible) {
			state = STATE_NORMAL;
			return false;
		}

		if((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y)
				&& (mouseY <= (y + height))) {
			result = true;
		}

		if(result) {
			gameEngine.setCursor(textCursor);
			state = STATE_FOCUSED;
		}
		else {
			if(state == STATE_FOCUSED) {
				if(gameEngine.getCursor() != gameCursor) {
					gameEngine.setCursor(gameCursor);
				}
			}
			state = STATE_NORMAL;
		}

		return result;
	}

	public void deFocus()
	{
		state = STATE_NORMAL;
	}

	public boolean isClicked(int mouseX, int mouseY)
	{
		int ii = 0;
		int cursorX = 0;
		StringBuffer buffer = null;

		if(!isVisible) {
			wasClicked = false;
			return false;
		}

		if(!((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y)	&&
						(mouseY <= (y + height)))) {
			wasClicked = false;
			return false;
		}

		wasClicked = true;

		cursorX = x;
		buffer = getContentsBuffer();
		for(ii = 0; ii < buffer.length(); ii++) {
			cursorX += fontInfo.getCharWidth(buffer.charAt(ii));
			if(cursorX > mouseX) {
				bufferIndex = (ii + 1);
				return true;
			}
		}
		bufferIndex = buffer.length();

		return true;
	}

	public void keyPressed(KeyEvent event) 
	{
		char value = (char)0;
		StringBuffer buffer = null;

		if(!wasClicked) {
			return;
		}

		buffer = getContentsBuffer();

		if(event.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			if((bufferIndex > 0) && (buffer.length() >= bufferIndex)) {
				buffer.deleteCharAt(bufferIndex-1);
				bufferIndex--;
			}
			return;
		}

		if(event.getKeyCode() == KeyEvent.VK_DELETE) {
			if((bufferIndex) < buffer.length()) {
				buffer.deleteCharAt(bufferIndex);
			}
			return;
		}

		if(event.getKeyCode() == KeyEvent.VK_ENTER) {
			if(buffer.length() > 0) {
				commitBuffer();
			}
			return;
		}

		if(event.getKeyCode() == KeyEvent.VK_ESCAPE) {
			wasClicked = false;
			return;
		}

		if((event.getKeyCode() == KeyEvent.VK_LEFT) ||
				(event.getKeyCode() == KeyEvent.VK_KP_LEFT)) {
			if(bufferIndex == 0) {
				return;
			}
			bufferIndex--;
			return;
		}

		if((event.getKeyCode() == KeyEvent.VK_RIGHT) ||
				(event.getKeyCode() == KeyEvent.VK_KP_RIGHT)) {
			if(bufferIndex == buffer.length()) {
				return;
			}
			bufferIndex++;
			return;
		}

		if((event.getKeyCode() == KeyEvent.VK_UP) ||
				(event.getKeyCode() == KeyEvent.VK_KP_UP)) {
			if(historyIndex == 0) {
				return;
			} 
			historyIndex--;
			return;
		}

		if((event.getKeyCode() == KeyEvent.VK_DOWN) ||
				(event.getKeyCode() == KeyEvent.VK_KP_DOWN)) {
			if(historyIndex == (contents.size() - 1)) {
				return;
			} 
			historyIndex++;
			return;
		}

		if(event.getKeyCode() == KeyEvent.VK_HOME) {
			bufferIndex = 0;
			return;
		}

		if(event.getKeyCode() == KeyEvent.VK_END) {
			bufferIndex = buffer.length();
			return;
		}

		if(buffer.length() < maxChars) {
			value = event.getKeyChar();
			if(((int)value > 31) && ((int)value < 127)) {
				buffer.insert(bufferIndex, value);
				bufferIndex++;
			}
			return;
		}
	}

	public void setDisplayColor(Color displayColor)
	{
		this.displayColor = displayColor;
	}

	public void setBackgroundColor(Color backgroundColor)
	{
		this.backgroundColor = backgroundColor;
	}

	public void setColorScheme(Color displayColor, Color backgroundColor)
	{
		this.displayColor = displayColor;
		this.backgroundColor = backgroundColor;
	}

	public void setMaxChars(int maxChars)
	{
		this.maxChars = maxChars;
	}

	public void setPasswordMasking(boolean mode)
	{
		isMasked = mode;
	}

	public boolean isPasswordMasked()
	{
		return isMasked;
	}

	public boolean hasSubmitted()
	{
		return (submitted != null);
	}

	public void setContents(String text)
	{
		contents.add(new StringBuffer(text));
		historyIndex = contents.size() - 1;
		bufferIndex = 0;
	}

	public String getContents()
	{
		return ((StringBuffer)contents.get(historyIndex)).toString();
	}

	public String getSubmittedContents()
	{
		String result = null;

		if(submitted == null) {
			return null;
		}

		result = new String(submitted);
		submitted = null;

		return result;
	}

	public void setImage(int id, String imageName)
	{
		try {
			corneredBox.setImage(id, imageName);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void render2D(Graphics2D render)
	{
		int stringWidth = 0;
		String password = null;

		try {
			if(!isVisible) {
				return;
			}

			// setup render space

			render.setColor(Color.white);
			render.fillRect(x, y, width, height);

			// render image componenets

			corneredBox.render2D(render);

			// render text

			calculateVisualIndices();
			render.setColor(displayColor);
			if(isPasswordMasked()) {
				password = getContents().substring(visualBeg,
						visualEnd).replaceAll(".", PASSWORD_MASK);
				render.drawString(password, fontX, fontY);
			}
			else {
				render.drawString(
					getContents().substring(visualBeg, visualEnd),
					fontX, fontY);
			}

			if(wasClicked) {
				if(isPasswordMasked()) {
					password = getContents().substring(visualBeg,
							bufferIndex).replaceAll(".", PASSWORD_MASK);
					fontInfo.setString(password);
				} 
				else {
					fontInfo.setString(getContents().substring(visualBeg,
								bufferIndex));
				}
				stringWidth = fontInfo.getWidth();
				
				blink++;
				if(blink < (BLINK_COUNT >> 1)) {
					render.fill3DRect((fontX + stringWidth),
							(y + corneredBox.getYOffset() + 2), 2,
							((height - (corneredBox.getYOffset() * 2)) - 4),
							true);
				}
				if(blink == BLINK_COUNT) {
					blink = 0;
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

