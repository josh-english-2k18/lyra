/*
 * Tree.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A GUI widget for handling tree displays.
 *
 * Written by Josh English.
 */

// define package space

package lyra.widgets;

// import external packages

import java.util.Iterator;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;

// import internal packages

import lyra.GameEngine;

// define class

public class Tree implements Widget
{
	// define public class constants

	public static final int STATE_NORMAL = 0;
	public static final int STATE_FOCUSED = 1;

	// define private class constants

	private static final String CLASS_NAME = Tree.class.getName();
	private static final Color DEFAULT_FONT_COLOR = Color.black;
	private static final Color DEFAULT_SELECTED_COLOR = Color.blue;
	private static final Color DEFAULT_BACKGROUND_COLOR = Color.white;
	private static final Color DEFAULT_BACKGROUND_COLOR2 = 
			new Color(235, 235, 185);
	private static final int SCROLLBAR_SIZE = 16;
	private static final int ROW_SPACING = 4;
	private static final String COLUMN_OFFSET = "   ";

	// define protected class variables

	protected boolean isVisible = false;
	protected int state = 0;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected int rowHeight = 0;
	protected int viewableWidth = 0;
	protected int viewableHeight = 0;
	protected int fontHeight = 0;
	protected int currentElement = 0;
	protected int currentStringIndex = 0;
	protected FontMetrics fontMetrics = null;
	protected Color backgroundColor = null;
	protected Color backgroundColor2 = null;
	protected Color selectedColor = null;
	protected Color fontColor = null;
	protected Font font = null;
	protected String name = null;
	protected TreeNode selectedNode = null;
	protected TreeNode rootNode = null;
	protected ScrollBar vScroll = null;
	protected ScrollBar hScroll = null;
	protected GameEngine gameEngine = null;

	// define private class methods

	private void updateWidgetsLocation()
	{
		if(vScroll != null) {
			vScroll.setLocation(x + width - SCROLLBAR_SIZE, y);
		}
		if(hScroll != null) {
			vScroll.setLocation(x, y + height - SCROLLBAR_SIZE);
		}
	}

	private void updateWidgetsSize()
	{
		if(vScroll != null) {
			vScroll.resize(SCROLLBAR_SIZE, height);
		}
		if(hScroll != null) {
			hScroll.resize(width, SCROLLBAR_SIZE);
		}
	}

	private void setupVScrollBar()
	{
		if(vScroll == null) {
			vScroll = new ScrollBar(name + "sv", x + width - SCROLLBAR_SIZE, y,
					SCROLLBAR_SIZE, height, ScrollBar.VERTICAL, gameEngine);
			vScroll.setImage(ScrollBar.SCROLL_UP,
					"assets/textures/gui/scrollbar01/up.png");
			vScroll.setImage(ScrollBar.SCROLL_DOWN,
					"assets/textures/gui/scrollbar01/down.png");
			vScroll.setImage(ScrollBar.SCROLL_BAR,
					"assets/textures/gui/scrollbar01/bar.png");
		}
	}

	private void setupHScrollBar()
	{
		if(hScroll == null) {
			hScroll = new ScrollBar(name + "sh", x, 
					y + height - SCROLLBAR_SIZE,
					width, SCROLLBAR_SIZE, ScrollBar.HORIZONTAL, gameEngine);
			hScroll.setImage(ScrollBar.SCROLL_UP,
					"assets/textures/gui/scrollbar01/up.png");
			hScroll.setImage(ScrollBar.SCROLL_DOWN,
					"assets/textures/gui/scrollbar01/down.png");
			hScroll.setImage(ScrollBar.SCROLL_BAR,
					"assets/textures/gui/scrollbar01/bar.png");
		}
	}

	private String generateName(TreeNode node)
	{
		int ii = 0;
		StringBuffer name = null;

		name = new StringBuffer("");
		for(ii = 0; ii < node.getDepth(); ii++) {
			name.append(COLUMN_OFFSET);
		}
		name.append(node.getName());

		return name.toString();
	}

	private void updateParameters()
	{
		int totalExposedElements = 0;
		int largestWidth = 0;
		int stringWidth = 0;
		String name = null;
		String largestName = null;
		TreeNode node = null;
		Iterator treeIterator = null;

		totalExposedElements = rootNode.getTotalExposedElements();
		if((totalExposedElements * rowHeight) > viewableHeight) {
			setupVScrollBar();
			viewableWidth = width - SCROLLBAR_SIZE;
		}
		else {
			vScroll = null;
			viewableWidth = width;
			currentElement = 0;
		}


		largestWidth = 0;
		largestName = "";
		treeIterator = rootNode.getOnlyExposedIterator();

		while(treeIterator.hasNext()) {
			node = (TreeNode) treeIterator.next();
			name = generateName(node);
			stringWidth = fontMetrics.stringWidth(name);
			if(stringWidth > largestWidth) {
				largestWidth = stringWidth;
				largestName = name;
			}
		}
		
		if(largestWidth > viewableWidth) {
			setupHScrollBar();
			viewableHeight = height - SCROLLBAR_SIZE;
		}
		else {
			hScroll = null;
			viewableHeight = height;
			currentStringIndex = 0;
		}

		if((hScroll != null) && (vScroll != null)) {
			hScroll.setTwoBarsState(true);
			vScroll.setTwoBarsState(true);
			
			vScroll.setPossibleViews(totalExposedElements - 
					determineViewableRows());
			hScroll.setPossibleViews(determineMaxStringIndex(largestName));


		}
		else if(vScroll != null) {
			vScroll.setTwoBarsState(false);
			vScroll.setPossibleViews(totalExposedElements - 
					determineViewableRows());
			
		}
		else if(hScroll != null) {
			hScroll.setTwoBarsState(false);
			hScroll.setPossibleViews(determineMaxStringIndex(largestName));
		}
	}

	private void renderTree(Graphics2D render)
	{
		int ii = 0;
		int fontY = 0;
		int fontX = 0;
		int counter = 0;
		int maxY = 0;
		int endIndex = 0;
		String name = null;
		TreeNode nextNode = null;
		Iterator treeIterator = null;

		fontY = (y + rowHeight);
		fontX = x;
		treeIterator = rootNode.getOnlyExposedIterator();

		for(ii = 0; (ii < currentElement) && treeIterator.hasNext(); ii++) {
			treeIterator.next();
		}

		counter = 0;
		maxY = (y + viewableHeight);
		while(treeIterator.hasNext() && (fontY <= maxY)) {
			if((counter & 1) == 1) {
				render.setColor(backgroundColor);
			}
			else {
				render.setColor(backgroundColor2);
			}
			render.fillRect(x, fontY - rowHeight, viewableWidth, rowHeight);
			counter++;

			nextNode = (TreeNode) treeIterator.next();
			if(nextNode == selectedNode) {
				render.setColor(selectedColor);
			}
			else {
				render.setColor(fontColor);
			}

			name = generateName(nextNode);
			for(endIndex = name.length();
					((currentStringIndex <= endIndex) &&
					 (fontMetrics.stringWidth(
						name.substring(currentStringIndex, endIndex)) >
					  viewableWidth)); endIndex--) {
				// do nothing
			}
			
			if(currentStringIndex <= endIndex) {
				render.drawString(name.substring(currentStringIndex, endIndex), 
						fontX, fontY - ROW_SPACING);
			}

			fontY += rowHeight;
		}
	}

	private TreeNode getClickedNode(int mouseY)
	{
		int ii = 0;
		int maxY = 0;
		int yCounter = 0;
		Iterator treeIterator = null;

		maxY = (y + viewableHeight);

		if((mouseY < y) || (mouseY > maxY)) {
			return null;
		}

		yCounter = (y + rowHeight);
		treeIterator = rootNode.getOnlyExposedIterator();
		for(ii = 0; (ii < currentElement) && treeIterator.hasNext(); ii++) {
			treeIterator.next();
		}

		while(treeIterator.hasNext() && (yCounter <= maxY)) {
			TreeNode nextNode = (TreeNode) treeIterator.next();
			if(mouseY < yCounter) {
				return nextNode;
			}
			yCounter += rowHeight;
		}

		return null;
	}

	private int determineViewableRows()
	{
		int maxY = 0;
		int rows = 0;
		int yCounter = 0;
		Iterator treeIterator = null;

		maxY = (y + viewableHeight);
		yCounter = (y + rowHeight);
		treeIterator = rootNode.getOnlyExposedIterator();

		if(hScroll != null) {
			maxY -= SCROLLBAR_SIZE;
		}

		while(treeIterator.hasNext()  && (yCounter <= maxY)) {
			treeIterator.next();
			yCounter += rowHeight;
			rows++;
		}

		return rows;
	}

	private int determineMaxStringIndex(String largestString)
	{
		int begIndex = 0;
		int endIndex = 0;

		endIndex = largestString.length();

		for(begIndex = 0;
				fontMetrics.stringWidth(largestString.substring(begIndex,
						endIndex)) > viewableWidth; begIndex++) {
			// do nothing
		}

		return begIndex;
	}

	// define public class methods

	public Tree(String name, int x, int y, int width, int height,
			GameEngine gameEngine)
	{
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.gameEngine = gameEngine;

		isVisible = true;
		state = STATE_NORMAL;
		rowHeight = 0;
		viewableWidth = 0;
		viewableHeight = 0;
		fontHeight = 0;
		currentElement = 0;
		currentStringIndex = 0;
		backgroundColor = DEFAULT_BACKGROUND_COLOR;
		backgroundColor2 = DEFAULT_BACKGROUND_COLOR2;
		selectedColor = DEFAULT_SELECTED_COLOR;
		fontColor = DEFAULT_FONT_COLOR;
		font = gameEngine.getFont();
		fontMetrics = gameEngine.getFontMetrics(font);
		selectedNode = null;
		rootNode = new TreeNode("Root", null);
		vScroll = null;
		hScroll = null;

		fontHeight = fontMetrics.getAscent();
		rowHeight = fontHeight + ROW_SPACING;
		viewableWidth = width;
		viewableHeight = height;
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
	}

	public void resize(int width, int height)
	{
		this.width = width;
		this.height = height;
	}

	public void setVisibility(boolean mode)
	{
		isVisible = mode;
	}

	public boolean isVisible()
	{
		return isVisible;
	}

	public void setColors(Color backgroundColor, Color backgroundColor2, 
			Color fontColor, Color selectedColor)
	{
		this.fontColor = fontColor;
		this.selectedColor = selectedColor;
		this.backgroundColor = backgroundColor;
		this.backgroundColor2 = backgroundColor2;
	}

	public TreeNode getRootNode()
	{
		return rootNode;
	}

	public TreeNode getSelectedNode()
	{
		return selectedNode;
	}

	public void treeChanged()
	{
		updateParameters();
	}

	public boolean hasFocus(int mouseX, int mouseY)
	{
		if(!isVisible) {
			return false;
		}

		if(vScroll != null) {
			vScroll.hasFocus(mouseX, mouseY);
		}

		if(hScroll != null) {
			hScroll.hasFocus(mouseX, mouseY);
		}
		
		if(!((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
						(mouseY <= (y + height)))) {
			return false;
		}
		return true;
	}

	public void deFocus()
	{
		state = STATE_NORMAL;
	}

	public boolean isClicked(int mouseX, int mouseY)
	{
		if(!isVisible) {
			return false;
		}

		if(!((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
						(mouseY <= (y + height)))) {
			return false;
		}

		if((vScroll != null) && vScroll.isClicked(mouseX, mouseY)) {
			currentElement = (vScroll.getCurrentOffset());
			return true;
		}

		if((hScroll != null) && hScroll.isClicked(mouseX, mouseY)) {
			currentStringIndex = (int)(hScroll.getCurrentOffset());
			return true;
		}

	    TreeNode clickedNode = getClickedNode(mouseY);
		if(clickedNode != null) {
			selectedNode = clickedNode;			
			if(selectedNode.getNumberOfChildren() != 0) {
				clickedNode.toggleOpen();
				updateParameters();
			}
 		}
		return true;
	}

	public boolean mouseWheelMoved(MouseWheelEvent event)
	{
		int mouseX = 0;
		int mouseY = 0;

		if(!isVisible) {
			return false;
		}

		mouseX = event.getX();
		mouseY = event.getY();

		if(!((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
						(mouseY <= (y + height)))) {
			return false;
		}

		if((vScroll != null) && vScroll.mouseWheelMoved(event)) {
			currentElement = (vScroll.getCurrentOffset());
		}
		return true;
	}

	public boolean mouseReleased(int mouseX, int mouseY) 
	{
		if(!isVisible) {
			return false;
		}

		if(vScroll != null) {
			vScroll.mouseReleased(mouseX, mouseY);
		}

		if(hScroll != null) {
			hScroll.mouseReleased(mouseX, mouseY);
		}

		if(!((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
						(mouseY <= (y + height)))) {
			return false;
		}

		return true;
	}

	public boolean mouseDragged(int mouseX, int mouseY)
	{
		if(!isVisible) {
			return false;
		}

		if(vScroll != null) {
			vScroll.mouseDragged(mouseX, mouseY);
			currentElement = (vScroll.getCurrentOffset());
		}

		if(hScroll != null) {
			hScroll.mouseDragged(mouseX, mouseY);
			currentStringIndex = (int)(hScroll.getCurrentOffset());
		}

		if(!((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
						(mouseY <= (y + height)))) {
			return false;
		}

		return true;
	}

	public void keyPressed(KeyEvent event)
	{
		// do nothing
	}

	public void render2D(Graphics2D render)
	{
		try {
			if(!isVisible) {
				return;
			}

			render.setColor(backgroundColor);
			render.fillRect(x, y, width, height);
			renderTree(render);

			if(vScroll != null) {
				vScroll.render2D(render);
			}

			if(hScroll != null) {
				hScroll.render2D(render);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

