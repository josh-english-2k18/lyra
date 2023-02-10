/*
 * WidgetTest.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A simple extension of the GameEngine to test the GUI widget progress bar
 * system.
 *
 * Written by Josh English.
 */

// define package space

package lyra.tests;

// import external packages

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.Color;

// import internal packages

import lyra.*;
import lyra.drivers.KeyboardDriver;
import lyra.drivers.MouseDriver;
import lyra.widgets.Widget;
import lyra.widgets.CorneredBox;
import lyra.widgets.ProgressBar;
import lyra.widgets.ButtonRender;
import lyra.widgets.Button;
import lyra.widgets.TextBox;
import lyra.widgets.ScrollBar;
import lyra.widgets.FileBrowser;
import lyra.widgets.Equalizer;
import lyra.widgets.HyperLink;
import lyra.widgets.CheckBox;
import lyra.widgets.DropdownBox;
import lyra.widgets.Autoscroll;
import lyra.widgets.NumericBox;

// define class

public class WidgetTest extends GameEngine
{
	// define private class constants

	private static final String CLASS_NAME = WidgetTest.class.getName();

	// define private class variables

	private ProgressBar progressBar = null;
	private Button button = null;
	private TextBox textBox = null;
	private ScrollBar scrollBar = null;
	private FileBrowser fileBrowser = null;
	private Equalizer equalizer = null;
	private HyperLink hyperlink = null;
	private CheckBox checkBox = null;
	private DropdownBox dropdownBox = null;
	private Autoscroll autoscroll = null;
	private NumericBox numericBox = null;

	// graphics rendering functions

	public synchronized void pipeline()
	{
		if(!isGameLoaded) {
			return;
		}

		setColor(Color.white);
		drawString("Progress Bar Test", 2, 128);
		progressBar.render2D(render2D);

		setColor(Color.white);
		drawString("Button Test", 2, 210);
		button.render2D(render2D);

		setColor(Color.white);
		drawString("TextBox Test", 2, 292);
		textBox.render2D(render2D);

		setColor(Color.white);
		drawString("ScrollBar Test", 2, 374);
		scrollBar.render2D(render2D);

		setColor(Color.white);
		drawString("FileBrowser Test", 256, 456);
		fileBrowser.render2D(render2D);

		setColor(Color.white);
		drawString("Equalizer Test: " + equalizer.getBarFocus(), 382, 210);
		equalizer.render2D(render2D);

		setColor(Color.white);
		drawString("HyperLink Test", 640, 338);
		hyperlink.render2D(render2D);

		setColor(Color.white);
		drawString("CheckBox Test", 640, 32);
		checkBox.render2D(render2D);

		setColor(Color.white);
		drawString("DropdownBox Test", 640, 128);
		dropdownBox.render2D(render2D);

		setColor(Color.white);
		drawString("Autoscroll Test: " + autoscroll.isRunning(), 640, 510);
		autoscroll.render2D(render2D);

		setColor(Color.white);
		drawString("NumericBox Test: " + numericBox.getResult(), 256, 510);
		numericBox.render2D(render2D);

		setColor(Color.white);
		drawString("ENGINE render (" + windowWidth + " x " +
				windowHeight + "), framerate: " + frameRate.get(),
				0, 12);
		drawString("ENGINE mouse (" + mouseX + ", " + mouseY + 
				"): button: " + mouseButton + ", clicks: " +
				mouseClicks, 0, 28);
		drawString("ENGINE keyboard (" + lastBinaryKeyPress +
				"), type: " + keyboardEventType + ", '" +
				keyPressBuffer + "'", 0, 44);

//		Texture texture = (Texture)assetCache.getAsset(AssetCache.TYPE_TEXTURE,
//				"assets/textures/test/test01.png");
//		drawImage(texture.getMipMap(80, 60), 128, 128, 80, 60);
	}

	// event handling

	public synchronized void handleMouseEvents(int type, MouseEvent event)
	{
		if(!isGameLoaded) {
			return;
		}

		if(type == MouseDriver.EVENT_PRESSED) {
			progressBar.isClicked(mouseX, mouseY);
			if(button.isClicked(mouseX, mouseY)) {
				autoscroll.reset();
				numericBox.reset();
			}
			textBox.isClicked(mouseX, mouseY);
			scrollBar.isClicked(mouseX, mouseY);
			fileBrowser.isClicked(mouseX, mouseY);
			equalizer.isClicked(mouseX, mouseY);
			hyperlink.isClicked(mouseX, mouseY);
			checkBox.isClicked(mouseX, mouseY);
			dropdownBox.isClicked(mouseX, mouseY);
			numericBox.isClicked(mouseX, mouseY);
		}
		else if(type == MouseDriver.EVENT_RELEASED) {
			hyperlink.isClicked(mouseX, mouseY);
		}
		else {
			if(progressBar.hasFocus(mouseX, mouseY)) {
				button.deFocus();
				textBox.deFocus();
				scrollBar.deFocus();
				fileBrowser.deFocus();
				equalizer.deFocus();
				hyperlink.deFocus();
				checkBox.deFocus();
				dropdownBox.deFocus();
				numericBox.deFocus();
			}
			else if(button.hasFocus(mouseX, mouseY)) {
				progressBar.deFocus();
				textBox.deFocus();
				scrollBar.deFocus();
				fileBrowser.deFocus();
				equalizer.deFocus();
				hyperlink.deFocus();
				checkBox.deFocus();
				dropdownBox.deFocus();
				numericBox.deFocus();
			}
			else if(textBox.hasFocus(mouseX, mouseY)) {
				progressBar.deFocus();
				button.deFocus();
				scrollBar.deFocus();
				fileBrowser.deFocus();
				equalizer.deFocus();
				hyperlink.deFocus();
				checkBox.deFocus();
				dropdownBox.deFocus();
				numericBox.deFocus();
			}
			else if(scrollBar.hasFocus(mouseX, mouseY)) {
				progressBar.deFocus();
				button.deFocus();
				textBox.deFocus();
				fileBrowser.deFocus();
				equalizer.deFocus();
				hyperlink.deFocus();
				checkBox.deFocus();
				dropdownBox.deFocus();
				numericBox.deFocus();
			}
			else if(fileBrowser.hasFocus(mouseX, mouseY)) {
				progressBar.deFocus();
				button.deFocus();
				textBox.deFocus();
				scrollBar.deFocus();
				equalizer.deFocus();
				hyperlink.deFocus();
				checkBox.deFocus();
				dropdownBox.deFocus();
				numericBox.deFocus();
			}
			else if(equalizer.hasFocus(mouseX, mouseY)) {
				progressBar.deFocus();
				button.deFocus();
				textBox.deFocus();
				scrollBar.deFocus();
				fileBrowser.deFocus();
				hyperlink.deFocus();
				checkBox.deFocus();
				dropdownBox.deFocus();
				numericBox.deFocus();
			}
			else if(hyperlink.hasFocus(mouseX, mouseY)) {
				progressBar.deFocus();
				button.deFocus();
				textBox.deFocus();
				scrollBar.deFocus();
				fileBrowser.deFocus();
				equalizer.deFocus();
				checkBox.deFocus();
				dropdownBox.deFocus();
				numericBox.deFocus();
			}
			else if(checkBox.hasFocus(mouseX, mouseY)) {
				progressBar.deFocus();
				button.deFocus();
				textBox.deFocus();
				scrollBar.deFocus();
				fileBrowser.deFocus();
				equalizer.deFocus();
				hyperlink.deFocus();
				dropdownBox.deFocus();
				numericBox.deFocus();
			}
			else if(dropdownBox.hasFocus(mouseX, mouseY)) {
				progressBar.deFocus();
				button.deFocus();
				textBox.deFocus();
				scrollBar.deFocus();
				fileBrowser.deFocus();
				equalizer.deFocus();
				hyperlink.deFocus();
				checkBox.deFocus();
				numericBox.deFocus();
			}
			else if(numericBox.hasFocus(mouseX, mouseY)) {
				progressBar.deFocus();
				button.deFocus();
				textBox.deFocus();
				scrollBar.deFocus();
				fileBrowser.deFocus();
				equalizer.deFocus();
				hyperlink.deFocus();
				checkBox.deFocus();
				dropdownBox.deFocus();
			}
			if(type == MouseDriver.EVENT_DRAGGED) {
				scrollBar.mouseDragged(mouseX, mouseY);
				fileBrowser.mouseDragged(mouseX, mouseY);
			}
			else if(type == MouseDriver.EVENT_RELEASED) {
				scrollBar.mouseReleased(mouseX, mouseY);
				fileBrowser.mouseReleased(mouseX, mouseY);
			}
			else if(type == MouseDriver.EVENT_WHEEL_MOVED) {
				scrollBar.mouseWheelMoved((MouseWheelEvent)event);
				fileBrowser.mouseWheelMoved((MouseWheelEvent)event);
			}
		}
	}

	public synchronized void handleKeyboardEvents(KeyEvent event)
	{
		if(!isGameLoaded) {
			return;
		}

		if(keyboardEventType == KeyboardDriver.EVENT_KEY_PRESSED) {
			progressBar.keyPressed(event);
			button.keyPressed(event);
			textBox.keyPressed(event);
			scrollBar.keyPressed(event);
			fileBrowser.keyPressed(event);
			numericBox.keyPressed(event);
		}
	}

	// game logic

	public synchronized void loadGame()
	{
		Texture texture = null;

		progressBar = new ProgressBar("ProgressBarWidgetTest", 2, 142, 256,
				32, this);
		progressBar.setTotalTicks(500);
		progressBar.setFillImage("assets/textures/gui/progress01/progress.png");
		progressBar.setImage(CorneredBox.IMAGE_TOP,
				"assets/textures/gui/textbox01/top.png");
		progressBar.setImage(CorneredBox.IMAGE_BOTTOM,
				"assets/textures/gui/textbox01/bottom.png");
		progressBar.setImage(CorneredBox.IMAGE_LEFT,
				"assets/textures/gui/textbox01/left.png");
		progressBar.setImage(CorneredBox.IMAGE_RIGHT,
				"assets/textures/gui/textbox01/right.png");
		progressBar.setImage(CorneredBox.IMAGE_UPPER_LEFT,
				"assets/textures/gui/textbox01/upper_left.png");
		progressBar.setImage(CorneredBox.IMAGE_UPPER_RIGHT,
				"assets/textures/gui/textbox01/upper_right.png");
		progressBar.setImage(CorneredBox.IMAGE_LOWER_LEFT,
				"assets/textures/gui/textbox01/lower_left.png");
		progressBar.setImage(CorneredBox.IMAGE_LOWER_RIGHT,
				"assets/textures/gui/textbox01/lower_right.png");

		button = new Button("ButtonWidgetTest", 2, 224, 128, 32, this);
		button.setText("select");
		button.setImage(Button.STATE_NORMAL,
				"assets/textures/gui/button01/normal.png");
		button.setImage(Button.STATE_FOCUSED,
				"assets/textures/gui/button01/focused.png");
		button.setImage(Button.STATE_HIGHLIGHTED,
				"assets/textures/gui/button01/highlighted.png");
		button.setImage(Button.STATE_DOWN,
				"assets/textures/gui/button01/down.png");

		textBox = new TextBox("TextBoxWidgetTest", 2, 306, 256, 32, this);
		textBox.setImage(CorneredBox.IMAGE_TOP,
				"assets/textures/gui/textbox01/top.png");
		textBox.setImage(CorneredBox.IMAGE_BOTTOM,
				"assets/textures/gui/textbox01/bottom.png");
		textBox.setImage(CorneredBox.IMAGE_LEFT,
				"assets/textures/gui/textbox01/left.png");
		textBox.setImage(CorneredBox.IMAGE_RIGHT,
				"assets/textures/gui/textbox01/right.png");
		textBox.setImage(CorneredBox.IMAGE_UPPER_LEFT,
				"assets/textures/gui/textbox01/upper_left.png");
		textBox.setImage(CorneredBox.IMAGE_UPPER_RIGHT,
				"assets/textures/gui/textbox01/upper_right.png");
		textBox.setImage(CorneredBox.IMAGE_LOWER_LEFT,
				"assets/textures/gui/textbox01/lower_left.png");
		textBox.setImage(CorneredBox.IMAGE_LOWER_RIGHT,
				"assets/textures/gui/textbox01/lower_right.png");

		scrollBar = new ScrollBar("ScrollBarWidgetTest", 128, 388, 16, 128,
				ScrollBar.VERTICAL, this);
		scrollBar.setPossibleViews(32);
		scrollBar.setImage(ScrollBar.SCROLL_UP,
				"assets/textures/gui/scrollbar01/up.png");
		scrollBar.setImage(ScrollBar.SCROLL_DOWN,
				"assets/textures/gui/scrollbar01/down.png");
		scrollBar.setImage(ScrollBar.SCROLL_BAR,
				"assets/textures/gui/scrollbar01/bar.png");

		fileBrowser = new FileBrowser("FileBrowserWidgetTest", 256, 470, 512,
				256, this);
		fileBrowser.setRootDirectory(getCodeBase().getPath());
		fileBrowser.setVisibility(false);

		equalizer = new Equalizer("EqualizerTest", 384, 224, 128, 128, this);
		equalizer.setHighlightBarFocus(true);
		equalizer.setBackgroundColor(Color.white);
		equalizer.setupEqualizer(0, 100, 20, 4);
		equalizer.setBarValue(0, 32);
		equalizer.setBarValue(1, 64);
		equalizer.setBarValue(2, 12);
		equalizer.setBarValue(3, 100);

		hyperlink = new HyperLink("HyperLinkTest", 640, 370, this);
		hyperlink.setLink("A Test Link (google)", "http://www.google.com");
		hyperlink.setAutoRedirect(true);

		checkBox = new CheckBox("CheckBoxTest", 640, 32, 32, 32, this);
		checkBox.setBoxImage("assets/textures/gui/checkbox01/box.png");
		checkBox.setCheckImage("assets/textures/gui/checkbox01/mark.png");

		dropdownBox = new DropdownBox("DropdownBoxTest", 640, 128, 128, 32,
				this);
		dropdownBox.addEntry("Test 01");
		dropdownBox.addEntry("Test 02");
		dropdownBox.addEntry("Test 03");
		dropdownBox.addEntry("Test 04");
		dropdownBox.getButton().setImage(Button.STATE_NORMAL,
				"assets/textures/gui/button01/normal.png");
		dropdownBox.getButton().setImage(Button.STATE_FOCUSED,
				"assets/textures/gui/button01/focused.png");
		dropdownBox.getButton().setImage(Button.STATE_HIGHLIGHTED,
				"assets/textures/gui/button01/highlighted.png");
		dropdownBox.getButton().setImage(Button.STATE_DOWN,
				"assets/textures/gui/button01/down.png");

		texture = (Texture)assetCache.getAsset(AssetCache.TYPE_TEXTURE,
				"assets/textures/test/test01.png");
		texture.buildMipMap(80, 60);

		autoscroll = new Autoscroll("AutoscrollTest", 640, 512, 256, 256,
				this);
		autoscroll.setBackgroundColor(Color.black);
		autoscroll.setOutlineColor(Color.white);
		autoscroll.addText(Color.white, "");
		autoscroll.addText(Color.white, "");
		autoscroll.addText(Color.white, "");
		autoscroll.addText(Color.white, "");
		autoscroll.addText(Color.white, "Test Line #01");
		autoscroll.addText(Color.white, "");
		autoscroll.addText(Color.blue, "Test Line #02");
		autoscroll.addText(Color.green, "Test Line #03");
		autoscroll.addText(Color.white, "");
		autoscroll.addHyperlink(hyperlink);
		autoscroll.addText(Color.white, "");
		autoscroll.addText(Color.red, "Test Line #04");
		autoscroll.addText(Color.white, "");
		autoscroll.addImage(texture.getMipMap(80, 60), 80, 60);
		autoscroll.addText(Color.white, "");
		autoscroll.addText(Color.yellow, "Test Line #05");
		autoscroll.addText(Color.white, "");
		autoscroll.addText(Color.white, "Final Test Line");

		numericBox = new NumericBox("NumericBoxTest", 256, 512, 168, 72,
				this);
		numericBox.setLabel("Select a Number");
		numericBox.setIncrements(2);
		numericBox.setupNumbers(32, 0, 64);

		isGameLoaded = true;
	}

	public synchronized void processGameplay()
	{
		if(!isGameLoaded) {
			return;
		}

		if(progressBar.getState() == ProgressBar.STATE_RUNNING) {
			progressBar.increment(1);
		}
		else {
			progressBar.start();
		}

		if((getTicks() % 8) == 0) {
			autoscroll.process();
			numericBox.process();
		}
	}
}

