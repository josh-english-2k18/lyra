/*
 * GameEngine.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * An extension to the Java Applet foundation to define a game rendering
 * pipeline.
 *
 * Written by Josh English.
 */

// define package space

package lyra;

// import external packages

import java.applet.Applet;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Panel;
import java.awt.Point;

// import internal packages

import lyra.drivers.KeyboardDriver;
import lyra.drivers.MouseDriver;

// define class

public class GameEngine extends Applet implements Runnable
{
	// define public class constants

	public static final boolean RELEASE_BUILD = false;

	public static final int VERSION_MAJOR = 0;
	public static final int VERSION_MINOR = 1;
	public static final int VERSION_REVISION = 14;

	public static final int STATE_UNINIT = 0;
	public static final int STATE_INIT = 1;
	public static final int STATE_RUNNING = 2;
	public static final int STATE_PAUSED = 3;
	public static final int STATE_STOPPED = 4;
	public static final int STATE_SHUTDOWN = 5;
	public static final int STATE_ERROR = 6;

	// define protected class constants

	protected static final long serialVersionUID = 1L;

	// define private class constants

	private static final String CLASS_NAME = GameEngine.class.getName();

	// define private class variables

	private int renderState = STATE_UNINIT;
	private long ticks = 0;
	private Image lyraLogo = null;
	private String path = null;
	private Thread thread = null;

	// define protected class variables
	
	// game render variables

	protected boolean isGameLoaded = false;
	protected boolean isHighPerformanceGraphics = false;
	protected int windowWidth = 0;
	protected int windowHeight = 0;

	// game mouse variables

	protected int mouseX = 0;
	protected int mouseY = 0;
	protected int mouseButton = 0;
	protected int mouseClicks = 0;
	protected MouseDriver mouse = null;

	// game keyboard variables

	protected boolean shiftKey = false;
	protected int keyboardEventType = 0;
	protected int lastBinaryKeyPress = 0;
	protected String keyPressBuffer = null;
	protected KeyboardDriver keyboard = null;

	// game engine variables

	protected Cursor cursor = null;
	protected Font font = null;
	protected BufferedImage renderBuffer = null;
	protected BufferedImage offscreenRenderBuffer = null;
	protected Panel internalObserver = null;
	protected Graphics render = null;
	protected Graphics2D render2D = null;
	protected AssetCache assetCache = null;
	protected FrameRate frameRate = null;

	// define game engine private functions

	private void displayRuntimeProperties()
	{
		try {
			System.out.println("Lyra Game Engine Ver " + VERSION_MAJOR + "." +
					VERSION_MINOR + "." + VERSION_REVISION);
			System.out.println("Copyright (C) 2007 - 2008 by Joshua S. " +
					"English.");
			System.out.println("");
			System.out.println("ENGINE(java version)    : " +
					System.getProperty("java.version"));
			System.out.println("ENGINE(java vendor)     : " +
					System.getProperty("java.vendor"));
			System.out.println("ENGINE(os name)         : " +
					System.getProperty("os.name"));
			System.out.println("ENGINE(os architecture) : " +
					System.getProperty("os.arch"));
			System.out.println("ENGINE(os version)      : " +
					System.getProperty("os.version"));
			System.out.println("ENGINE(code base)       : " +
					this.getCodeBase().toString());
			System.out.println("ENGINE(path)            : " +
					this.getCodeBase().getPath().toString());
			System.out.println("");
		}
		catch(Exception e) {
			if(!RELEASE_BUILD) {
				e.printStackTrace();
			}
		}
	}

	private synchronized void lyraInit()
	{
		String version = null;
		Double versionDouble = null;

		// set state

		renderState = STATE_INIT;

		// code to correct tab key usage

		version = System.getProperty("java.version");
		this.displayRuntimeProperties();
		versionDouble = new Double(version.substring(0, 3));
		if(versionDouble.doubleValue() >= 1.4) {
			try {
				this.setFocusTraversalKeysEnabled(false);
			} 
			catch(Exception e) {
				if(!RELEASE_BUILD) {
					e.printStackTrace();
				}
			}
		} 
		else {
            System.out.println(System.getProperty("java.version"));
       	}

		// initialize render variables

		windowWidth = this.getSize().width;
		windowHeight = this.getSize().height;
		ticks = 0;

		// initialize mouse variables

		mouseX = 0;
		mouseY = 0;
		mouseButton = 0;
		mouseClicks = 0;
		mouse = new MouseDriver(windowWidth, windowHeight);
		this.addMouseListener(mouse);
		this.addMouseMotionListener(mouse);
		this.addMouseWheelListener(mouse);

		// initialize keyboard variables

		keyboardEventType = 0;
		lastBinaryKeyPress = 0;
		keyPressBuffer = null;
		keyboard = new KeyboardDriver(this);
		this.addKeyListener(keyboard);

		// initialize game objects

		path = this.getCodeBase().getPath();
		cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
		this.setCursor(cursor);
		font = new Font("Lucida Console", Font.BOLD, 16);
		renderBuffer = null;
		render = this.getGraphics();
		assetCache = new AssetCache(this);
		frameRate = new FrameRate();

		// setup environment

		setBackground(Color.black);
		setForeground(Color.white);
		setFont(font);

		// setup internal observer

		internalObserver = new Panel();

		// setup rendering buffers

		try {
			manageRenderBuffer();
		}
		catch(Exception e) {
			if(!RELEASE_BUILD) {
				e.printStackTrace();
			}
		}

		// load the lyra logo

		lyraLogo = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
				"assets/textures/lyra/logo.png");
	}

	private synchronized void manageRenderBuffer() throws Exception
	{
		int counter = 0;

		// setup rendering buffer

		if((renderBuffer == null) || (renderBuffer.getGraphics() == null)) {
			renderBuffer = (BufferedImage)createImage(windowWidth,
					windowHeight);
			counter = 0;
			while((counter < 8) &&
					((renderBuffer == null) ||
					 (renderBuffer.getGraphics() == null))) {
				counter++;
				renderBuffer = (BufferedImage)createImage(windowWidth,
						windowHeight);
			}
			if((renderBuffer == null) ||
					(renderBuffer.getGraphics() == null)) {
				throw new Exception("failed to build rendering buffer");
			}
		}

		// test rendering buffer

		if((renderBuffer == null) || (renderBuffer.getGraphics() == null)) {
			throw new Exception("unable to perform render at " + ticks);
		}

		// setup offscreen rendering buffer

		if((offscreenRenderBuffer == null) ||
				(offscreenRenderBuffer.getGraphics() == null)) {
			offscreenRenderBuffer = (BufferedImage)createImage(windowWidth,
					windowHeight);
			counter = 0;
			while((counter < 8) &&
					((offscreenRenderBuffer == null) ||
					 (offscreenRenderBuffer.getGraphics() == null))) {
				counter++;
				offscreenRenderBuffer = (BufferedImage)createImage(windowWidth,
						windowHeight);
			}
			if((offscreenRenderBuffer == null) ||
					(offscreenRenderBuffer.getGraphics() == null)) {
				throw new Exception(
						"failed to build offscreen rendering buffer");
			}
		}
		else {
			// setup default rendering poperties

			offscreenRenderBuffer.getGraphics().setColor(getBackground());
			offscreenRenderBuffer.getGraphics().fillRect(0, 0, windowWidth,
					windowHeight);
			offscreenRenderBuffer.getGraphics().setColor(getForeground());
		}

		// test offscreen rendering buffer

		if((offscreenRenderBuffer == null) ||
				(offscreenRenderBuffer.getGraphics() == null)) {
			throw new Exception("unable to perform render at " + ticks);
		}

		// setup default rendering poperties

		render = renderBuffer.getGraphics();
		render.setColor(getBackground());
		render.fillRect(0, 0, windowWidth, windowHeight);
		render.setColor(getForeground());
		render2D = (Graphics2D)render;
	}

	private synchronized void iPaint(Graphics render)
	{
		try {
			if((renderState != STATE_RUNNING) || (this.render == null) ||
					(this.render2D == null)) {
				return;
			}

			if(isGameLoaded) {
				try {
					pipeline();
				}
				catch(Exception e) {
					if(!RELEASE_BUILD) {
						e.printStackTrace();
					}
				}
			}
			else {
				render.setColor(Color.black);
				render.fillRect(0, 0, windowWidth, windowHeight);
				if(lyraLogo != null) {
					render.drawImage(lyraLogo,
							((windowWidth / 2) -
							 (getImageWidth(lyraLogo) / 2)),
							((windowHeight / 2) -
							 (getImageHeight(lyraLogo) / 2)),
							getImageWidth(lyraLogo), getImageHeight(lyraLogo),
							this);
				}
				else {
					render.setColor(Color.white);
					render.fillOval(2, 2, 4, 4);
					render.fillOval(8, 2, 4, 4);
					render.fillOval(14, 2, 4, 4);
				}
			}
		}
		catch(Exception e) {
			if(!RELEASE_BUILD) {
				e.printStackTrace();
			}
		}
	}

	private synchronized void iUpdate(Graphics graphics)
	{
		try {
			if(renderState != STATE_RUNNING) {
				return;
			}

			// setup rendering double-buffer

			manageRenderBuffer();

			// perform render path

			iPaint(render);

			// paint rendering result

			graphics.drawImage(renderBuffer, 0, 0, this);
		}
		catch(Exception e) {
			if(!RELEASE_BUILD) {
				e.printStackTrace();
			}
		}
	}

	// define game engine public functions

	public synchronized String getClassName()
	{
		return CLASS_NAME;
	}

	public synchronized void init()
	{
		// init private class variables

		renderState = STATE_UNINIT;
		ticks = 0;
		lyraLogo = null;
		path = null;
		thread = null;

		// init protected class variables
	
		// game render variables

		isGameLoaded = false;
		isHighPerformanceGraphics = true;
		windowWidth = 0;
		windowHeight = 0;

		// game mouse variables

		mouseX = 0;
		mouseY = 0;
		mouseButton = 0;
		mouseClicks = 0;
		mouse = null;

		// game keyboard variables

		shiftKey = false;
		keyboardEventType = 0;
		lastBinaryKeyPress = 0;
		keyPressBuffer = null;
		keyboard = null;

		// game engine variables

		cursor = null;
		font = null;
		renderBuffer = null;
		offscreenRenderBuffer = null;
		internalObserver = null;
		render = null;
		render2D = null;
		assetCache = null;
		frameRate = null;
	}

	public synchronized void start()
	{
		thread = new Thread(this);
		thread.start();
	}

	public synchronized void stop()
	{
		this.shutdown();
	}

	public synchronized void shutdown()
	{
		renderState = STATE_STOPPED;
	}

	public synchronized AssetCache getAssetCache()
	{
		return assetCache;
	}

	public synchronized int getWindowWidth()
	{
		return windowWidth;
	}

	public synchronized int getWindowHeight()
	{
		return windowHeight;
	}

	public synchronized int getRenderState()
	{
		return renderState;
	}

	public synchronized long getTicks()
	{
		return ticks;
	}

	public synchronized String getAssetMode()
	{
		String codeBase = null;
		String result = null;

		try {
			codeBase = this.getCodeBase().toString();
			if(codeBase.startsWith("http://")) {
				result = new String("HTTP");
			}
			else if(codeBase.startsWith("file:/")) {
				result = new String("FILE");
			}
			else {
				result = new String("UNKNOWN");
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			if(result == null) {
				result = new String("UNKNOWN");
			}
		}

		return result;
	}

	public synchronized String getPath()
	{
		return path;
	}

	public synchronized Image getLogo()
	{
		return lyraLogo;
	}

	public synchronized void reloadLogo()
	{
		lyraLogo = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
				"assets/textures/lyra/logo.png");
	}

	public synchronized boolean isHighPerformanceMode()
	{
		return isHighPerformanceGraphics;
	}

	public synchronized void toggleHighPerformanceMode()
	{
		if(isHighPerformanceGraphics) {
			isHighPerformanceGraphics = false;
		}
		else {
			isHighPerformanceGraphics = true;
		}
	}

	public synchronized double getFramerate()
	{
		return frameRate.get();
	}

	public synchronized void paint(Graphics graphics)
	{
		if(isHighPerformanceGraphics) {
			iPaint(graphics);
		}
		super.paint(graphics);
	}

	public synchronized void update(Graphics graphics)
	{
		if(isHighPerformanceGraphics) {
			iUpdate(graphics);
		}
		super.update(graphics);
	}

	public void run()
	{
		int counter = 0;
		int mouseEventType = 0;
		int sleepMillis = 0;
		double delta = 0.0;

		MouseEvent mouseEvent = null;
		KeyEvent keyEvent = null;

		try {
			if(renderState == STATE_UNINIT) {
				this.lyraInit();
			}
			renderState = STATE_RUNNING;

			System.out.println("Lyra Game Engine Running");

			// render the first frame

			iUpdate(this.getGraphics());
			try {
				Thread.sleep(1024); // millis
			}
			catch(Exception e) {
				// do noting
			}

			// main game engine loop

			while(renderState == STATE_RUNNING) {
				ticks++;

				// handle mouse events

				counter = 0;
				while((mouse.hasEvent()) && (counter < 8)) {
					mouseEventType = mouse.getEventType();
					mouseEvent = mouse.getMouseEvent();

					if((mouseEvent.getX() < 0) ||
							(mouseEvent.getX() > windowWidth) ||
							(mouseEvent.getY() < 0) ||
							(mouseEvent.getY() > windowHeight)) {
						mouseEvent = mouse.getMouseEvent();
						continue;
					}

					mouseX = mouseEvent.getX();
					mouseY = mouseEvent.getY();
					mouseButton = mouseEvent.getButton();
					mouseClicks = mouseEvent.getClickCount();

					try {
						handleMouseEvents(mouseEventType, mouseEvent);
					}
					catch(Exception e) {
						if(!RELEASE_BUILD) {
							e.printStackTrace();
						}
					}

					counter++;
				}

				// handle keyboard events

				counter = 0;
				keyboard.processKeyPressEvents();
				while((keyboard.hasEvent()) && (counter < 8)) {
					keyEvent = keyboard.getKeyEvent();
					if(keyEvent != null) {
						keyboardEventType = keyboard.getKeyType();
						shiftKey = keyboard.isShiftKeyOn();
						lastBinaryKeyPress = keyEvent.getKeyCode();
						keyPressBuffer = keyboard.getKeyBuffer();

						try {
							handleKeyboardEvents(keyEvent);
						}
						catch(Exception e) {
							if(!RELEASE_BUILD) {
								e.printStackTrace();
							}
						}
					}
					counter++;
				}

				// process the gameplay

				if(!isGameLoaded) {
					try {
						loadGame();
					}
					catch(Exception e) {
						if(!RELEASE_BUILD) {
							e.printStackTrace();
						}
					}
				}
				else {
					try {
						processGameplay();
					}
					catch(Exception e) {
						if(!RELEASE_BUILD) {
							e.printStackTrace();
						}
					}
				}

				// render the frame

				iUpdate(this.getGraphics());

				if(renderState == STATE_STOPPED) {
					break;
				}

				// calculate the frame rate

				frameRate.calculate();

				// take a nap

				if(((frameRate.getFrameCounter() % 32) == 0) &&
						(frameRate.get() > 120.0)) {
					delta = ((1.0 / (frameRate.get() - 80.0)) / 2.0);
					sleepMillis = (int)(delta * 1000.0);
					try {
						Thread.sleep(sleepMillis);
					}
					catch(Exception e) {
						// do nothing
					}
				}
			}

			renderState = STATE_SHUTDOWN;

			System.out.println("Lyra Game Engine Shutdown");
		}
		catch(Exception e) {
			if(!RELEASE_BUILD) {
				e.printStackTrace();
			}
		}
	}

	// graphics rendering functions

	public synchronized void buildRenderBuffers()
	{
		try {
			manageRenderBuffer();
		}
		catch(Exception e) {
			if(!RELEASE_BUILD) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void setColor(Color color)
	{
		try {
			if(render2D == null) {
				manageRenderBuffer();
			}
			render2D.setColor(color);
		}
		catch(Exception e) {
			if(!RELEASE_BUILD) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void drawRect(int x, int y, int width, int height)
	{
		try {
			if(render2D == null) {
				manageRenderBuffer();
			}
			render2D.drawRect(x, y, width, height);
		}
		catch(Exception e) {
			if(!RELEASE_BUILD) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void fillRect(int x, int y, int width, int height)
	{
		try {
			if(render2D == null) {
				manageRenderBuffer();
			}
			render2D.fillRect(x, y, width, height);
		}
		catch(Exception e) {
			if(!RELEASE_BUILD) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void drawLine(int x1, int y1, int x2, int y2)
	{
		try {
			if(render2D == null) {
				manageRenderBuffer();
			}
			render2D.drawLine(x1, y1, x2, y2);
		}
		catch(Exception e) {
			if(!RELEASE_BUILD) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void drawString(String string, int x, int y)
	{
		try {
			if(render2D == null) {
				manageRenderBuffer();
			}
			render2D.drawString(string, x, y);
		}
		catch(Exception e) {
			if(!RELEASE_BUILD) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void drawImage(Image image, int x, int y, int width,
			int height)
	{
		try {
			if(render2D == null) {
				manageRenderBuffer();
			}
			render2D.drawImage(image, x, y, width, height, this);
		}
		catch(Exception e) {
			if(!RELEASE_BUILD) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void drawOffscreenImage(Image image, int x, int y,
			int width, int height)
	{
		try {
			if(render2D == null) {
				manageRenderBuffer();
			}
			offscreenRenderBuffer.getGraphics().drawImage(image, x, y, width,
					height, internalObserver);
		}
		catch(Exception e) {
			if(!RELEASE_BUILD) {
				e.printStackTrace();
			}
		}
	}

	public synchronized Image buildInternalImage(String imageName)
	{
		Image result = null;

		try {
			result = this.getImage(this.getDocumentBase(), imageName);
		}
		catch(Exception e) {
			if(!RELEASE_BUILD) {
				e.printStackTrace();
			}
			result = null;
		}

		return result;
	}

	public synchronized Cursor buildCursor(String imageName, String cursorName)
	{
		Image image = null;
		Cursor result = null;

		try {
			image = (Image)assetCache.getAsset(AssetCache.TYPE_INTERNAL_IMAGE,
					imageName);
			if(image != null) {
				result = this.getToolkit().createCustomCursor(image,
						(new Point(0, 0)), cursorName);
			}
		}
		catch(Exception e) {
			if(!RELEASE_BUILD) {
				e.printStackTrace();
			}
			result = null;
		}

		return result;
	}

	public synchronized int getImageWidth(Image image)
	{
		int result = 0;

		try {
			if(render2D == null) {
				manageRenderBuffer();
			}
			result = image.getWidth(internalObserver);
		}
		catch(Exception e) {
			if(!RELEASE_BUILD) {
				e.printStackTrace();
			}
			result = 0;
		}

		return result;
	}

	public synchronized int getImageHeight(Image image)
	{
		int result = 0;

		try {
			if(render2D == null) {
				manageRenderBuffer();
			}
			result = image.getHeight(internalObserver);
		}
		catch(Exception e) {
			if(!RELEASE_BUILD) {
				e.printStackTrace();
			}
			result = 0;
		}

		return result;
	}

	// functions designed to be overidden (for game implementation)

	public synchronized void loadGame()
	{
		// note: override me
	}

	public synchronized void pipeline()
	{
		// note: override me
	}

	public synchronized void handleMouseEvents(int type, MouseEvent event)
	{
		// note: override me
	}

	public synchronized void handleKeyboardEvents(KeyEvent event)
	{
		// note: override me
	}

	public synchronized void processGameplay()
	{
		// note: override me
	}
}

