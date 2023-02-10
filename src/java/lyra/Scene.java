/*
 * Scene.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A simple component for managing and rendering scenes.
 *
 * Written by Josh English.
 */

// define package space

package lyra;

// import external packages

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.awt.Rectangle;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.MouseEvent;

// import internal packages

import lyra.interfaces.GraphicsInterface;

// define class

public class Scene implements GraphicsInterface
{
	// define private class constants

	private static final String CLASS_NAME = Scene.class.getName();

	// define private class variables

	protected boolean debugMode = false;
	protected int x = 0;
	protected int y = 0;
	protected int width = 0;
	protected int height = 0;
	protected int offsetX = 0;
	protected int offsetY = 0;
	protected int spriteCenterX = 0;
	protected int spriteCenterY = 0;
	protected String name = null;
	protected String cameraSpriteName = null;
	protected ArrayList tileSet = null;
	protected ArrayList spriteSet = null;
	protected HashMap spritePhysics = null;
	protected Camera camera = null;
	protected BufferedImage renderBuffer = null;
	protected GameEngine gameEngine = null;

	// define class public functions

	public Scene(String name, int x, int y, int width, int height,
			GameEngine gameEngine)
	{
		this.name = name;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.gameEngine = gameEngine;

		debugMode = false;
		offsetX = 0;
		offsetY = 0;
		spriteCenterX = 0;
		spriteCenterY = 0;
		cameraSpriteName = null;
		tileSet = new ArrayList();
		spriteSet = new ArrayList();
		spritePhysics = new HashMap();
		camera = new Camera(name + "Camera", x, y, width, height);
		renderBuffer = null;
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

	public Rectangle getLocation()
	{
		return new Rectangle((x + offsetX), (y + offsetY), width, height);
	}

	public boolean isVisible()
	{
		return true;
	}

	public boolean applyCamera(int cameraX, int cameraY, int cameraWidth,
			int cameraHeight)
	{
		return true;
	}

	public Camera getCamera()
	{
		return camera;
	}

	public boolean getDebugMode()
	{
		return debugMode;
	}

	public void setDebugMode(boolean mode)
	{
		debugMode = mode;
	}

	public String getCameraSpriteName()
	{
		return cameraSpriteName;
	}

	public void setCameraSpritename(String name)
	{
		cameraSpriteName = name;
	}

	public void commitTile(Tile tile, int x, int y)
	{
		tile.setLocation(x, y);
		tileSet.add(tile);
	}

	public void commitSprite(Sprite sprite, int x, int y)
	{
		if(cameraSpriteName == null) {
			cameraSpriteName = sprite.getName();
		}

		sprite.setLocation(x, y);
		spriteSet.add(sprite);
	}

	public ArrayList getSpriteSet()
	{
		return spriteSet;
	}

	public void commitSpritePhysics(String spriteName, SpritePhysics physics)
	{
		spritePhysics.put(spriteName, physics);
	}

	public void updateScene(boolean mode, double frameRate)
	{
		Iterator iterator = null;
		Tile tile = null;
		Sprite sprite = null;

		try {
			// apply camera to tile-set (check the tile-set for visiblity)

			iterator = tileSet.iterator();
			while(iterator.hasNext()) {
				tile = (Tile)iterator.next();
				if(tile == null) {
					break;
				}

				tile.applyCamera(camera.getCameraX(), camera.getCameraY(),
						camera.getWindowWidth(), camera.getWindowHeight());
			}

			// process the sprite-set gameplay and check them for visibility

			iterator = spriteSet.iterator();
			while(iterator.hasNext()) {
				sprite = (Sprite)iterator.next();
				if(sprite == null) {
					break;
				}

				if(sprite.isStatic()) {
					continue;
				}

				if(mode) {
					sprite.processGameplay(frameRate);
				}

				sprite.applyCamera(camera.getCameraX(), camera.getCameraY(),
						camera.getWindowWidth(), camera.getWindowHeight());
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void processGameplay(double frameRate)
	{
		boolean cameraMoved = false;
		boolean hasCenterpoint = false;
		double friction = 0.0;

		Iterator iterator = null;
		Tile tile = null;
		Sprite sprite = null;
		SpritePhysics physics = null;

		try {
			// determine current friction status

			offsetX = (0 - camera.getCameraX());
			offsetY = (0 - camera.getCameraY());

			friction = SpritePhysics.DEFAULT_FRICTION;

			iterator = tileSet.iterator();
			while(iterator.hasNext()) {
				tile = (Tile)iterator.next();
				if(tile == null) {
					break;
				}

				if((tile.isVisible()) &&
						(tile.detectIntersection((spriteCenterX + offsetX),
												 (spriteCenterY + offsetY)))) {
					friction = tile.getFriction();
					break;
				}
			}

			// process the sprite physics

			iterator = spriteSet.iterator();
			while(iterator.hasNext()) {
				sprite = (Sprite)iterator.next();
				if(sprite == null) {
					break;
				}

				if(!hasCenterpoint) {
					spriteCenterX = (sprite.getX() + (sprite.getWidth() / 2));
					spriteCenterY = (sprite.getY() + (sprite.getHeight() / 2));
					hasCenterpoint = true;
				}

				physics = (SpritePhysics)spritePhysics.get(sprite.getName());
				if(physics != null) {
					physics.setFriction(friction);
					physics.processGameplay();
					if(sprite.getName().equals(cameraSpriteName)) {
						camera.applyCameraDiff(sprite.getCameraDiffX(),
								sprite.getCameraDiffY());
					}
				}
			}

			cameraMoved = camera.hasMoved();

			// check the tile-set for visiblity

			if(cameraMoved) {
				updateScene(true, frameRate);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public Sprite processCollisions()
	{
		Iterator spriteIterator = null;
		Iterator tileIterator = null;
		Tile tile = null;
		Sprite sprite = null;

		try {
			spriteIterator = spriteSet.iterator();
			while(spriteIterator.hasNext()) {
				sprite = (Sprite)spriteIterator.next();
				if(sprite == null) {
					break;
				}

				tileIterator = tileSet.iterator();
				while(tileIterator.hasNext()) {
					tile = (Tile)tileIterator.next();
					if(tile == null) {
						break;
					}
					if(tile.detectCollision(sprite.getLocation())) {
						return sprite;
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void processSpriteCollisions(boolean dynamicMode, int xDiff,
			int yDiff, long simulateMillis)
	{
		boolean cameraChanged = false;
		int ii = 0;

		Iterator spriteIterator = null;
		Iterator tileIterator = null;
		Tile tile = null;
		Sprite sprite = null;
		SpritePhysics physics = null;

		try {
			spriteIterator = spriteSet.iterator();
			while(spriteIterator.hasNext()) {
				sprite = (Sprite)spriteIterator.next();
				if(sprite == null) {
					break;
				}

				if(sprite.isStatic()) {
					continue;
				}

				tileIterator = tileSet.iterator();
				while(tileIterator.hasNext()) {
					tile = (Tile)tileIterator.next();
					if(tile == null) {
						break;
					}

					if((tile.detectCollision(sprite.getLocation())) &&
							(!tile.canIntersect())) {
						if(dynamicMode) {
							/*
							 * TODO: this
							 */
						}
						else {
							physics = (SpritePhysics)spritePhysics.get(
									sprite.getName());
							while(tile.detectCollision(sprite.getLocation())) {
								if(physics != null) {
									physics.setLocation(
											(sprite.getX() + xDiff),
											(sprite.getY() + yDiff));
								}
								else {
									sprite.setLocation(
											(sprite.getX() + xDiff),
											(sprite.getY() + yDiff));
								}
								if(sprite.getName().equals(cameraSpriteName)) {
									camera.applyCameraDiff(
											sprite.getCameraDiffX(),
											sprite.getCameraDiffY());
									cameraChanged = true;
								}
							}
							if((physics != null) && (physics.willBounce())) {
								if((xDiff != 0) && (yDiff != 0)) {
									physics.antiGravityBounce(true, true,
											simulateMillis);
								}
								else if(xDiff != 0) {
									physics.antiGravityBounce(true, false,
											simulateMillis);
								}
								else if(yDiff != 0) {
									physics.antiGravityBounce(false, true,
											simulateMillis);
								}
							}
						}
						break;
					}
				}
			}

			if(cameraChanged) {
				updateScene(false, 0.0);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void processMouseEvent(int type, int mouseX, int mouseY,
			MouseEvent event)
	{
		boolean hasSelected = false;

		Iterator spriteIterator = null;
		Sprite sprite = null;

		try {
			spriteIterator = spriteSet.iterator();
			while(spriteIterator.hasNext()) {
				sprite = (Sprite)spriteIterator.next();
				if(sprite == null) {
					break;
				}

				if(!sprite.isVisible()) {
					continue;
				}

				sprite.processMouseEvent(type, mouseX, mouseY, event);
				if(sprite.isSelected()) {
					hasSelected = true;
				}
			}

			if(hasSelected) {
				spriteIterator = spriteSet.iterator();
				while(spriteIterator.hasNext()) {
					sprite = (Sprite)spriteIterator.next();
					if(sprite == null) {
						break;
					}

					if(!sprite.isSelected()) {
						sprite.setHasOutline(false);
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		camera.processMouseEvent(camera.MODE_INVERTED, type, mouseX, mouseY,
				event);
	}

	public Image snapshot()
	{
		int counter = 0;

		Image result = null;
		Graphics2D render2D = null;

		try {
			// setup rendering buffer

			if((renderBuffer == null) || (renderBuffer.getGraphics() == null)) {
				renderBuffer = (BufferedImage)gameEngine.createImage(width,
						height);
				counter = 0;
				while((counter < 8) &&
						((renderBuffer == null) ||
						 (renderBuffer.getGraphics() == null))) {
					counter++;
					renderBuffer = (BufferedImage)gameEngine.createImage(width,
							height);
				}
				if((renderBuffer == null) ||
						(renderBuffer.getGraphics() == null)) {
					throw new Exception("failed to build rendering buffer");
				}
			}

			// test rendering buffer

			if((renderBuffer == null) || (renderBuffer.getGraphics() == null)) {
				throw new Exception("unable to perform snapshot render");
			}

			// perform scene render to buffer

			render2D = (Graphics2D)renderBuffer.getGraphics();
			render2D.setColor(gameEngine.getBackground());
			render2D.fillRect(0, 0, width, height);
			render2D.setColor(gameEngine.getForeground());
			this.render2D(render2D);

			result = (Image)renderBuffer;
		}
		catch(Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public void render2D(Graphics2D render)
	{
		Iterator iterator = null;
		Tile tile = null;
		Sprite sprite = null;

		try {
			offsetX = (0 - camera.getCameraX());
			offsetY = (0 - camera.getCameraY());

			// render background set

			iterator = tileSet.iterator();
			while(iterator.hasNext()) {
				tile = (Tile)iterator.next();
				if(tile == null) {
					break;
				}
				if(tile.isVisible()) {
					tile.render2D(render);
				}
			}

			// render sprite set

			iterator = spriteSet.iterator();
			while(iterator.hasNext()) {
				sprite = (Sprite)iterator.next();
				if(sprite == null) {
					break;
				}
				if(sprite.isVisible()) {
					sprite.render2D(render);
				}
			}

			if(debugMode) {
				render.setColor(Color.blue);
				render.fillOval(((spriteCenterX - 4) + offsetX),
						((spriteCenterY - 4) + offsetY), 8, 8);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}

