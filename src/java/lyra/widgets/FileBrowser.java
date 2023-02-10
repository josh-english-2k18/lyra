/*
 * FileBrowser.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A GUI widget for browsing a file system
 *
 * Written by Josh English.
 */

// define package space

package lyra.widgets;

// import external packages

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.FilePermission;
import java.util.Arrays;
import java.util.Stack;
import java.util.List;
import java.util.Comparator;
import java.util.Collections;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.security.AccessController;
import javax.swing.JOptionPane;

// import internal packages

import lyra.GameEngine;

// define class

public class FileBrowser implements Widget
{
	// define private class constants

	private static final String CLASS_NAME = FileBrowser.class.getName();
	private static final int TOP_WIDGETS_HEIGHT = 25; // i.e. button,box, etc
	private static final int BUTTON_WIDTH = 75;
	private static final int BORDER_SPACING = 5;
	private static final int MIN_HEIGHT = TOP_WIDGETS_HEIGHT * 3;
	private static final int MIN_WIDTH = BUTTON_WIDTH * 5;

	// define public class constants

	public static final int STATE_NORMAL = 0;
	public static final int STATE_FOCUSED = 1;

	// define private inner classes

	private class FileComparator implements Comparator 
	{
		public int compare(Object o1, Object o2)
		{
			File f1 = (File)o1;
			File f2 = (File)o2;

			if(f1.isDirectory() && (f2.isDirectory())) {
				return f1.compareTo(f2);
			}
			else if(f1.isDirectory()) {
				return -1;
			}
			else if(f2.isDirectory()) {
				return 1;
			}
			else {
				return f1.compareTo(f2);
			}
		}
	}

	private class FileFilter implements FilenameFilter
	{
		public boolean accept(File dir, String name) {
			return (!name.startsWith("."));
		}
	};

	// define protected class variables

	protected boolean isVisible = false;
	protected boolean adequatePermissions = false;
	protected boolean hasSelectedFile = false;
	protected int state = 0;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected Tree fileTree = null;
	protected TextBox fileTextBox = null;
	protected Button upButton = null;
	protected Button actionButton = null;
	protected Button backButton = null;
	protected String name = null;
	protected String rootDirectory = null;
	protected String selectedFileName = null;
	protected File selectedFile = null;
	protected Stack history = null;
	protected FileComparator fileComparator = new FileComparator();
	protected FileFilter fileFilter = new FileFilter();
	protected GameEngine gameEngine = null;

	// define private class methods

	private void expandTree(TreeNode node, boolean expand)
	{
		if(!adequatePermissions) {
			return;
		}

		File dir = (File)node.getData();

		if(dir == null) {
			dir = new File(rootDirectory);
		}

		if(!(dir.isDirectory() && dir.canRead())) {
			return;
		}

		if(node.getNumberOfChildren() == 0) {
			List files = Arrays.asList(dir.listFiles(fileFilter));
			Collections.sort(files, fileComparator);

			for(int ii = 0; ii < files.size(); ii++) {
				File file = (File)files.get(ii);
				if(file.isDirectory()) {
					node.addChild(file.getName() + "/", file);
				}
				else {
					node.addChild(file.getName(), file);
				}
			}
		}

		if(expand) {
			for(int ii = 0; ii < node.getNumberOfChildren(); ii++) {
				expandTree(node.getChild(ii), false);
			}
		}
		fileTree.treeChanged();
	}

	private void fileTreeClicked()
	{
		TreeNode node = fileTree.getSelectedNode();
		if(node != null) {
			expandTree(node, true);
			selectedFile = (File)node.getData();
			selectedFileName = selectedFile.getAbsolutePath();
			fileTextBox.setContents(selectedFileName);
		}
	}

	private void upButtonClicked()
	{
		if(!adequatePermissions) {
			return;
		}

		File rootDirFile = new File(rootDirectory);
		String rootDirName = rootDirFile.getParent();
		if(rootDirName != null) {
			String oldRootDirName = this.rootDirectory;
			setRootDirectory(rootDirName);
			if(!oldRootDirName.equals(this.rootDirectory)) {
				history.push(oldRootDirName);
				if(backButton == null) {
					createBackButton();
				}
			}
		}
	}

	private void backButtonClicked()
	{
		if(!history.empty()) {
			setRootDirectory((String)history.pop());
		}

		if(history.empty()) {
			backButton = null;
			updateWidgets();
		}
	}

	private void createBackButton()
	{
		backButton = new Button(name + "BackButton", 0, 0, 32, 32,
				gameEngine);
		backButton.setButtonText(Color.white, "BACK");
		backButton.setImage(Button.STATE_NORMAL,
				"assets/textures/gui/button01/normal.png");
		backButton.setImage(Button.STATE_FOCUSED,
				"assets/textures/gui/button01/focused.png");
		backButton.setImage(Button.STATE_HIGHLIGHTED,
				"assets/textures/gui/button01/highlighted.png");
		backButton.setImage(Button.STATE_DOWN,
				"assets/textures/gui/button01/down.png");
		updateWidgets();
	}

	private void actionButtonClicked()
	{
		//TODO: different behavior whether loading/saving
		// assuming saving for now
		verifySelectedFile();
	}

	private void verifySelectedFile()
	{
		// TODO: different behavior whether loading/saving
		// assuming saving for now
		if(selectedFile == null) {
			return;
		}
		if(!selectedFile.canWrite()) {
			selectedFile = null;
			selectedFileName = null;
			hasSelectedFile = false;
			// TODO: pop up dialog with error message; use swing's for now
			System.err.println("Lack write permissions");
			JOptionPane.showMessageDialog(null, "Lack write permissions");
		}
		else if(selectedFile.isDirectory()) {
			selectedFile = null;
			selectedFileName = null;
			hasSelectedFile = false;
			//TODO:  pop up dialog with error message; use swing's for now
			System.err.println("Can't save a directory");
			JOptionPane.showMessageDialog(null, "Can't save a directory");
		}
		else {
			hasSelectedFile = true;
		}
	}

	private void updateWidgets()
	{
		updateWidgetsSize(); // order is important
		updateWidgetsLocation();
	}

	private void updateWidgetsLocation()
	{
		this.fileTextBox.setLocation(
			x + BORDER_SPACING,
			y + BORDER_SPACING);

		this.actionButton.setLocation(
			fileTextBox.getX() + fileTextBox.getWidth() + BORDER_SPACING,
			y + BORDER_SPACING);

		this.upButton.setLocation(
			actionButton.getX() + actionButton.getWidth() + BORDER_SPACING,
			y + BORDER_SPACING);

		if(backButton != null) {
			this.backButton.setLocation(
				upButton.getX() + upButton.getWidth() + BORDER_SPACING,
				y + BORDER_SPACING);
		}

		this.fileTree.setLocation(
			x + BORDER_SPACING,
			y + TOP_WIDGETS_HEIGHT + (2 * BORDER_SPACING));
	}

	private void updateWidgetsSize()
	{
		if(backButton == null) {
			this.fileTextBox.resize(
				width - (4 * BORDER_SPACING) - (2 * BUTTON_WIDTH),
				TOP_WIDGETS_HEIGHT);
		}
		else {
			this.fileTextBox.resize(
				width - (5 * BORDER_SPACING) - (3 * BUTTON_WIDTH),
				TOP_WIDGETS_HEIGHT);

			this.backButton.resize(
				BUTTON_WIDTH,
				TOP_WIDGETS_HEIGHT);
		}
		
		this.actionButton.resize(
			BUTTON_WIDTH,
			TOP_WIDGETS_HEIGHT);

		this.upButton.resize(
			BUTTON_WIDTH,
			TOP_WIDGETS_HEIGHT);

		this.fileTree.resize(
			width - (2 * BORDER_SPACING),
			height - (3 * BORDER_SPACING) - TOP_WIDGETS_HEIGHT);
	}

	// define public class methods

	public FileBrowser(String name, int x, int y, int width, int height,
			GameEngine gameEngine) 
	{
		this.name = name;
		this.x = x;
		this.y = y;

		if((width < MIN_WIDTH) || (height < MIN_HEIGHT)) {
			System.out.println("Warning, FileBrowser widget minimums are (" +
					MIN_WIDTH + "x" + MIN_HEIGHT + ") vs (" + width + "x" +
					height + ")");
			this.width = MIN_WIDTH;
			this.height = MIN_HEIGHT;
		}
		else {
			this.width = width;
			this.height = height;
		}
		this.gameEngine = gameEngine;

		isVisible = true;
		this.fileTree = new Tree(name + "FileTree", 0, 0, 32, 32, gameEngine);
		this.fileTextBox = new TextBox(name + "TextBox", 0, 0, 32, 32,
				gameEngine);
		this.actionButton = new Button(name + "ActionButton", 0, 0, 32, 32,
				gameEngine);
		actionButton.setButtonText(Color.white, "SAVE");
		actionButton.setImage(Button.STATE_NORMAL,
				"assets/textures/gui/button01/normal.png");
		actionButton.setImage(Button.STATE_FOCUSED,
				"assets/textures/gui/button01/focused.png");
		actionButton.setImage(Button.STATE_HIGHLIGHTED,
				"assets/textures/gui/button01/highlighted.png");
		actionButton.setImage(Button.STATE_DOWN,
				"assets/textures/gui/button01/down.png");
		this.upButton = new Button(	name + "UpButton", 0, 0, 0, 0,
				gameEngine);
		upButton.setButtonText(Color.white, "UP");
		upButton.setImage(Button.STATE_NORMAL,
				"assets/textures/gui/button01/normal.png");
		upButton.setImage(Button.STATE_FOCUSED,
				"assets/textures/gui/button01/focused.png");
		upButton.setImage(Button.STATE_HIGHLIGHTED,
				"assets/textures/gui/button01/highlighted.png");
		upButton.setImage(Button.STATE_DOWN,
				"assets/textures/gui/button01/down.png");

		this.history = new Stack();

		updateWidgets();
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

	public void setVisibility(boolean mode)
	{
		isVisible = mode;
	}

	public boolean isVisible()
	{
		return isVisible;
	}

	public boolean hasSelectedFile()
	{
		return hasSelectedFile;
	}

	public File getSelectedFile()
	{
		hasSelectedFile = false;
		return selectedFile;
	}

	public String getRootDirectory()
	{
		return rootDirectory;
	}
	
	public void setRootDirectory(String dir)
	{
		try {
			AccessController.getContext().checkPermission(
				new FilePermission(dir, "read"));
		}
		catch (Exception e) {
			adequatePermissions = false;
			return;
		}
		adequatePermissions = true;

		File rootDirFile = new File(dir);
		if(rootDirFile.isDirectory() && rootDirFile.canRead()) {
			this.rootDirectory = dir;
			fileTree.getRootNode().deleteChildren();
			expandTree(fileTree.getRootNode(), true);
		}
	}

	public boolean hasFocus(int mouseX, int mouseY)
	{
		if(!isVisible) {
			return false;
		}

		if(!((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
				(mouseY <= (y + height)))) {
			return false;
		}

		if(backButton != null) {
			backButton.hasFocus(mouseX, mouseY);
		}
		
		upButton.hasFocus(mouseX, mouseY);
		actionButton.hasFocus(mouseX, mouseY);
		fileTree.hasFocus(mouseX, mouseY);
		fileTextBox.hasFocus(mouseX, mouseY);

		return true;
	}

	public void deFocus()
	{
		state = STATE_NORMAL;
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

		return fileTree.mouseWheelMoved(event);
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

		fileTextBox.isClicked(mouseX, mouseY);

		if(actionButton.isClicked(mouseX, mouseY)) {
			actionButtonClicked();
		}
		else if(backButton != null && backButton.isClicked(mouseX, mouseY)) {
			backButtonClicked();
		}
		else if(upButton.isClicked(mouseX, mouseY)) {
			upButtonClicked();
		}
		else if(fileTree.isClicked(mouseX, mouseY)) {
			fileTreeClicked();
		}

		return true;
	}

	public boolean mouseReleased(int mouseX, int mouseY) 
	{
		if(!isVisible) {
			return false;
		}

		if(!((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
				(mouseY <= (y + height)))) {
			return false;
		}

		fileTree.mouseReleased(mouseX, mouseY);

		return true;
	}

	public boolean mouseDragged(int mouseX, int mouseY)
	{
		if(!isVisible) {
			return false;
		}

		if(!((mouseX >= x) && (mouseX <= (x + width)) && (mouseY >= y) &&
						(mouseY <= (y + height)))) {
			return false;
		}

		fileTree.mouseDragged(mouseX, mouseY);

		return true;
	}

	public void keyPressed(KeyEvent event)
	{
		fileTextBox.keyPressed(event);
		if(fileTextBox.hasSubmitted()) {
			selectedFileName = fileTextBox.getSubmittedContents();
			selectedFile = new File(selectedFileName);
			verifySelectedFile();
		}
	}

	public void render2D(Graphics2D render)
	{
		try {
			if(!isVisible) {
				return;
			}

			fileTree.render2D(render);
			fileTextBox.render2D(render);
			upButton.render2D(render);
			actionButton.render2D(render);
			if(backButton != null) {
				backButton.render2D(render);
			}

			render.setColor(Color.green);
			render.drawRect(x, y, width, height);

			if(!adequatePermissions) {
				render.setColor(Color.red);
				render.drawString("Applet does not have adequate permissions",
						fileTree.getX() + 5, 
						fileTree.getY() + (fileTree.getHeight() / 2));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}







