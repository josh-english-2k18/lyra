/*
 * Galaxik.java
 *
 * Copyright (C) 2008 by Joshua S. English. This document is the intellectual
 * property of Joshua S. English. All rights reserved.
 *
 * A Lyra Game Engine based galaxy conquest game - Galaxik.
 *
 * Written by Josh English.
 */

// define package space

package galaxik;

// import external packages

import java.util.Iterator;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Font;

// import lyra game engine packages

import lyra.*;
import lyra.drivers.KeyboardDriver;
import lyra.drivers.MouseDriver;
import lyra.widgets.Autoscroll;
import lyra.widgets.Button;
import lyra.widgets.CheckBox;
import lyra.widgets.CorneredBox;
import lyra.widgets.DropdownBox;
import lyra.widgets.Equalizer;
import lyra.widgets.FontInfo;
import lyra.widgets.HyperLink;
import lyra.widgets.NumericBox;
import lyra.widgets.ProgressBar;
import lyra.widgets.TextBox;
import lyra.util.Config;
import lyra.audio.AudioPlayer;
import lyra.triggers.TriggerAction;
import lyra.triggers.TriggerActionImpl;
import lyra.triggers.TriggerSystem;

// import interal packages

import galaxik.widgets.ScrollDisplay;
import galaxik.triggers.BattleTriggerContext;
import galaxik.triggers.BattleTriggerEvent;
import galaxik.triggers.BattleTriggerConsumer;

// define class

public class Galaxik extends GameEngine
{
	// define private class constants

	private static final String CLASS_NAME = Galaxik.class.getName();

	private static final String HOSTNAME = "192.168.0.132";
	private static final int PORT = 8080;

	private static final boolean USE_BACKGROUND = false;
	private static final boolean USE_DIALOGUE_IMAGE = true;
	private static final boolean USE_HIGHLIGHT_IMAGE = false;

	private static final boolean WAIT_FOR_SOUND_COMPLETION = false;

	private static final int MAGNIFYING_GLASS_PIXELS = 80;

	private static final int PLANET_PIXEL_DIVISION = 20;

	private static final double MOUSE_DETECTION_THRESHOLD = 77.08333;

	private static final int DIALOGUE_INIT = 1;
	private static final int DIALOGUE_START = 2;
	private static final int DIALOGUE_PLAYING = 3;
	private static final int DIALOGUE_CAMERA = 4;
	private static final int DIALOGUE_TRIGGER = 5;
	private static final int DIALOGUE_OVER = 6;

	private static final int START_MODE_DIALOGUE = 1;
	private static final int START_MODE_DISPLAY_PROGRESS = 2;

	private static final int ZOOM_ARROW_UP = 0;
	private static final int ZOOM_ARROW_DOWN = 1;

	private static final int ZOOM_ARROWS = 2;

	private static final int SCROLL_ARROW_UP = 0;
	private static final int SCROLL_ARROW_DOWN = 1;

	private static final int SCROLL_ARROWS = 2;

	private static final int PLAYER_BUTTON_UPGRADE = 0;
	private static final int PLAYER_BUTTON_PROCESS = 1;
	private static final int PLAYER_BUTTON_LAUNCH = 2;

	private static final int PLAYER_BUTTONS = 3;

	// define private class variables

	private Image loadingImage = null;
	private Image logoImage = null;
	private Image gameEngineLogo = null;
	private GameLoader gameLoader = null;
	private Planet selectedPlanet = null;
	private Planet selectedPlanetAlternate = null;

	// define public class variables

	public boolean debugMode = false;
	public boolean internalGameLoaded = false;
	public boolean isTurnOver = false;
	public boolean isLaunchMode = false;
	public boolean selectShipsMode = false;
	public int dialogue = 0;
	public int startMode = 0;
	public int startTicks = 0;
	public int playerId = 0;
	public int sceneWidth = 0;
	public int sceneHeight = 0;
	public int turnCounter = 0;
	public int dCameraX = 0;
	public int dCameraY = 0;
	public int dCameraDestX = 0;
	public int dCameraDestY = 0;
	public int panCameraDiffX = 0;
	public int panCameraDiffY = 0;
	public int triggerAudio = 0;
	public String playerName = null;
	public String planetImages[] = null;
	public Image dialogueImage = null;
	public Image background = null;
	public Image foreground = null;
	public Image magnifyingGlass = null;
	public Image notAvailable = null;
	public Image playerIcon = null;
	public Image betaIcon = null;
	public Cursor snapshotCursor = null;
	public Cursor busyCursor = null;
	public Camera camera = null;
	public GameScene scene = null;
	public Game game = null;
	public Equalizer equalizer = null;
	public Button zoomArrows[] = null;
	public Button scrollArrows[] = null;
	public Button playerButtons[] = null;
	public NumericBox shipNumberBox = null;
	public AudioPlayer playDing = null;
	public AudioPlayer playBattle = null;
	public AudioPlayer playLostPlanet = null;
	public AudioPlayer playPlanetCaptured = null;
	public AudioPlayer playUpgrade = null;
	public ScrollDisplay scrollDisplay = null;
	public TextBox playerNameBox = null;
	public DropdownBox mapSizeBox = null;
	public DropdownBox aiNumberBox = null;
	public DropdownBox difficultyBox = null;
	public Button playButton = null;
	public HyperLink hyperlink = null;
	public Autoscroll credits = null;
	public FontInfo fontInfo = null;
	public CheckBox autoBuildBox = null;
	public CheckBox autoUpgradeBox = null;
	public ProgressBar progressBar = null;
	public BattleTriggerContext triggerContext = null;
	public TriggerSystem triggerSystem = null;

	// define private class objects

	private class GameLoader implements Runnable
	{
		private Thread thread = null;
		private Galaxik galaxik = null;

		public GameLoader(Galaxik galaxik)
		{
			this.galaxik = galaxik;
			thread = new Thread(this);
			thread.start();
		}

		public void run()
		{
			int ii = 0;
			int localX = 0;
			int localY = 0;
			double zoomLevel = 0.0;
			double localWidth = 0.0;
			double localHeight = 0.0;

			Texture texture = null;
			Player player = null;
			Config config = null;
			BattleTriggerConsumer consumer = null;
			TriggerActionImpl action = null;

			System.out.println("Game loading thread started.");

			// set defaults

			debugMode = false;
			internalGameLoaded = false;
			isTurnOver = false;
			isLaunchMode = false;
			selectShipsMode = false;
			dialogue = DIALOGUE_INIT;
			startMode = START_MODE_DIALOGUE;
			startTicks = 0;
			playerId = 0;
			sceneWidth = 0;
			sceneHeight = 0;
			turnCounter = 1;
			dCameraX = 0;
			dCameraY = 0;
			dCameraDestX = 0;
			dCameraDestY = 0;
			panCameraDiffX = 0;
			panCameraDiffY = 0;
			triggerAudio = 0;
			playerName = new String("Guest");
			planetImages = null;
			dialogueImage = null;
			background = null;
			foreground = null;
			magnifyingGlass = null;
			notAvailable = null;
			playerIcon = null;
			betaIcon = null;
			snapshotCursor = null;
			busyCursor = null;
			camera = null;
			scene = null;
			game = null;
			equalizer = null;
			zoomArrows = null;
			scrollArrows = null;
			playerButtons = null;
			shipNumberBox = null;
			playDing = null;
			playBattle = null;
			playLostPlanet = null;
			playPlanetCaptured = null;
			playUpgrade = null;
			scrollDisplay = null;
			playerNameBox = null;
			mapSizeBox = null;
			aiNumberBox = null;
			difficultyBox = null;
			playButton = null;
			hyperlink = null;
			credits = null;
			fontInfo = null;
			autoBuildBox = null;
			autoUpgradeBox = null;
			triggerContext = null;
			triggerSystem = null;

			// load custom mouse cursors

			cursor = galaxik.buildCursor(
					"assets/textures/galaxik/mouseCursor.png",
					"GalaxikCursor");
			if(cursor != null) {
				setCursor(cursor);
			}
			progressBar.increment(8);

			snapshotCursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
			progressBar.increment(14);
			busyCursor = new Cursor(Cursor.WAIT_CURSOR);
			progressBar.increment(14);

			// define font

			font = new Font("Arial", Font.PLAIN, 12);
			setFont(font);
			progressBar.increment(8);

			fontInfo = new FontInfo(galaxik);
			progressBar.increment(8);

			// define set of planet images

			planetImages = new String[12];
			planetImages[0] = new String(
					"assets/textures/galaxik/playerPlanet01.png");
			planetImages[1] = new String(
					"assets/textures/galaxik/planet01.png");
			planetImages[2] = new String(
					"assets/textures/galaxik/planet02.png");
			planetImages[3] = new String(
					"assets/textures/galaxik/planet03.png");
			planetImages[4] = new String(
					"assets/textures/galaxik/planet04.png");
			planetImages[5] = new String(
					"assets/textures/galaxik/planet05.png");
			planetImages[6] = new String(
					"assets/textures/galaxik/planet06.png");
			planetImages[7] = new String(
					"assets/textures/galaxik/planet07.png");
			planetImages[8] = new String(
					"assets/textures/galaxik/planet08.png");
			planetImages[9] = new String(
					"assets/textures/galaxik/planet09.png");
			planetImages[10] = new String(
					"assets/textures/galaxik/planet10.png");
			planetImages[11] = new String(
					"assets/textures/galaxik/planet11.png");
			progressBar.increment(8);

			// load the game logo

			logoImage = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
					"assets/textures/galaxik/logo.png");
			progressBar.increment(8);

			// load other images

			if(USE_DIALOGUE_IMAGE) {
				dialogueImage = (Image)assetCache.getAsset(
						AssetCache.TYPE_IMAGE,
						"assets/textures/galaxik/dialogueImage.png");
				progressBar.increment(8);
				dialogueImage = (Image)assetCache.getAsset(
						AssetCache.TYPE_IMAGE,
						"assets/textures/galaxik/startImage.png");
				progressBar.increment(8);
			}
			if(USE_BACKGROUND) {
				background = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
						"assets/textures/galaxik/hyperspace.png");
				progressBar.increment(8);
			}
			foreground = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
					"assets/textures/galaxik/outline.png");
			progressBar.increment(8);
			magnifyingGlass = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
					"assets/textures/galaxik/magnifyingGlass.png");
			progressBar.increment(8);
			betaIcon = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
					"assets/textures/galaxik/beta.png");
			progressBar.increment(8);

			// load scene background

			texture = (Texture)assetCache.getAsset(AssetCache.TYPE_TEXTURE,
					"assets/textures/galaxik/starscape.png");
			progressBar.increment(8);

			// build sprite mipmaps

			texture = (Texture)assetCache.getAsset(AssetCache.TYPE_TEXTURE,
					"assets/textures/galaxik/playerIcon.png");
			progressBar.increment(8);
			texture.buildMipMap((windowWidth / PLANET_PIXEL_DIVISION),
					(windowWidth / PLANET_PIXEL_DIVISION));
			progressBar.increment(8);
			playerIcon = texture.getMipMap(
					(windowWidth / PLANET_PIXEL_DIVISION),
					(windowWidth / PLANET_PIXEL_DIVISION));
			for(ii = 0; ii < planetImages.length; ii++) {
				texture = (Texture)assetCache.getAsset(AssetCache.TYPE_TEXTURE,
						planetImages[ii]);
				progressBar.increment(2);
				texture.buildMipMap((windowWidth / PLANET_PIXEL_DIVISION),
						(windowWidth / PLANET_PIXEL_DIVISION));
				progressBar.increment(2);
			}

			zoomLevel = GameScene.ZOOM_MIN;
			while(zoomLevel <= GameScene.ZOOM_MAX) {
				localWidth = (GameScene.PLANET_PIXELS * (zoomLevel / 100.0));
				localHeight = (GameScene.PLANET_PIXELS * (zoomLevel / 100.0));
				for(ii = 0; ii < planetImages.length; ii++) {
					texture = (Texture)assetCache.getAsset(
							AssetCache.TYPE_TEXTURE, planetImages[ii]);
					progressBar.increment(2);
					texture.buildMipMap((int)localWidth, (int)localHeight);
					progressBar.increment(2);
				}
				if(USE_HIGHLIGHT_IMAGE) {
					texture = (Texture)assetCache.getAsset(
							AssetCache.TYPE_TEXTURE,
							"assets/textures/galaxik/planetHighlight.png");
					progressBar.increment(2);
					texture.buildMipMap((int)localWidth, (int)localHeight);
					progressBar.increment(2);
				}
				zoomLevel += GameScene.ZOOM_INCREMENT;
			}

			// setup play-dialogue GUI elements

			notAvailable = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
					"assets/textures/galaxik/notAvailable.png");
			progressBar.increment(8);

			equalizer = new Equalizer("TechnologyLevels",
					((windowWidth - 12) - 124),
					((windowHeight - (windowHeight / 4)) + 36),
					126, 76,
					galaxik);
			equalizer.setHighlightBarFocus(true);
			equalizer.setupEqualizer(Technology.LOWEST_LEVEL,
					Technology.LEVELS, 25, Technology.TECHNOLOGIES);
			progressBar.increment(8);

			zoomArrows = new Button[ZOOM_ARROWS];

			localX = ((windowWidth - MAGNIFYING_GLASS_PIXELS) - 8);
			localY = (MAGNIFYING_GLASS_PIXELS - 8);
			for(ii = 0; ii < ZOOM_ARROWS; ii++) {
				zoomArrows[ii] = new Button("ZoomArrow" + ii,
						localX, localY, 24, 24, galaxik);
				progressBar.increment(2);

				zoomArrows[ii].setBackgroundImage(
						"assets/textures/galaxik/gui/background/image01.png");
				progressBar.increment(2);

				if(ii == ZOOM_ARROW_UP) {
					zoomArrows[ii].setUncorneredImage(Button.STATE_NORMAL,
							"assets/textures/galaxik/gui/arrow/upNormal.png");
					progressBar.increment(2);
					zoomArrows[ii].setUncorneredImage(Button.STATE_FOCUSED,
							"assets/textures/galaxik/gui/arrow/upFocused.png");
					progressBar.increment(2);
					zoomArrows[ii].setUncorneredImage(Button.STATE_HIGHLIGHTED,
							"assets/textures/galaxik/gui/arrow/" +
							"upHighlighted.png");
					progressBar.increment(2);
					zoomArrows[ii].setUncorneredImage(Button.STATE_DOWN,
							"assets/textures/galaxik/gui/arrow/upDown.png");
					progressBar.increment(2);
				}
				else if(ii == ZOOM_ARROW_DOWN) {
					zoomArrows[ii].setUncorneredImage(Button.STATE_NORMAL,
							"assets/textures/galaxik/gui/arrow/downNormal.png");
					progressBar.increment(2);
					zoomArrows[ii].setUncorneredImage(Button.STATE_FOCUSED,
							"assets/textures/galaxik/gui/arrow/" +
							"downFocused.png");
					progressBar.increment(2);
					zoomArrows[ii].setUncorneredImage(Button.STATE_HIGHLIGHTED,
							"assets/textures/galaxik/gui/arrow/" +
							"downHighlighted.png");
					progressBar.increment(2);
					zoomArrows[ii].setUncorneredImage(Button.STATE_DOWN,
							"assets/textures/galaxik/gui/arrow/downDown.png");
					progressBar.increment(2);
				}

				localX += 24;
			}

			scrollArrows = new Button[SCROLL_ARROWS];

			localX = 16;
			localY = (8 + getImageHeight(logoImage));
			for(ii = 0; ii < SCROLL_ARROWS; ii++) {
				scrollArrows[ii] = new Button("ZoomArrow" + ii,
						localX, localY, 24, 24, galaxik);
				progressBar.increment(2);

				scrollArrows[ii].setBackgroundImage(
						"assets/textures/galaxik/gui/background/image01.png");
				progressBar.increment(2);

				if(ii == ZOOM_ARROW_UP) {
					scrollArrows[ii].setUncorneredImage(Button.STATE_NORMAL,
							"assets/textures/galaxik/gui/arrow/upNormal.png");
					progressBar.increment(2);
					scrollArrows[ii].setUncorneredImage(Button.STATE_FOCUSED,
							"assets/textures/galaxik/gui/arrow/upFocused.png");
					progressBar.increment(2);
					scrollArrows[ii].setUncorneredImage(
							Button.STATE_HIGHLIGHTED,
							"assets/textures/galaxik/gui/arrow/" +
							"upHighlighted.png");
					progressBar.increment(2);
					scrollArrows[ii].setUncorneredImage(Button.STATE_DOWN,
							"assets/textures/galaxik/gui/arrow/upDown.png");
					progressBar.increment(2);
					scrollArrows[ii].setVisibility(false);
				}
				else if(ii == ZOOM_ARROW_DOWN) {
					scrollArrows[ii].setUncorneredImage(Button.STATE_NORMAL,
							"assets/textures/galaxik/gui/arrow/downNormal.png");
					progressBar.increment(2);
					scrollArrows[ii].setUncorneredImage(Button.STATE_FOCUSED,
							"assets/textures/galaxik/gui/arrow/" +
							"downFocused.png");
					progressBar.increment(2);
					scrollArrows[ii].setUncorneredImage(
							Button.STATE_HIGHLIGHTED,
							"assets/textures/galaxik/gui/arrow/" +
							"downHighlighted.png");
					progressBar.increment(2);
					scrollArrows[ii].setUncorneredImage(Button.STATE_DOWN,
							"assets/textures/galaxik/gui/arrow/downDown.png");
					progressBar.increment(2);
				}
			}

			playerButtons = new Button[PLAYER_BUTTONS];

			localY = ((windowHeight - (windowHeight / 4)) + 24);
			for(ii = 0; ii < PLAYER_BUTTONS; ii++) {
				playerButtons[ii] = new Button("PlayerButton" + ii,
						((windowWidth / PLANET_PIXEL_DIVISION) + 256 +
						 (ii * 56) + (ii * 8)), localY,
						56, 56, galaxik);
				progressBar.increment(2);

				if(ii == PLAYER_BUTTON_UPGRADE) {
					playerButtons[ii].setImage(Button.STATE_NORMAL,
							"assets/textures/galaxik/gui/upgrade/normal01.png");
					progressBar.increment(2);
					playerButtons[ii].setImage(Button.STATE_FOCUSED,
							"assets/textures/galaxik/gui/upgrade/" +
							"focused01.png");
					progressBar.increment(2);
					playerButtons[ii].setImage(Button.STATE_HIGHLIGHTED,
							"assets/textures/galaxik/gui/upgrade/" +
							"highlighted01.png");
					progressBar.increment(2);
					playerButtons[ii].setImage(Button.STATE_DOWN,
							"assets/textures/galaxik/gui/upgrade/down01.png");
					progressBar.increment(2);
				}
				else if(ii == PLAYER_BUTTON_PROCESS) {
					playerButtons[ii].setImage(Button.STATE_NORMAL,
							"assets/textures/galaxik/gui/process/normal01.png");
					progressBar.increment(2);
					playerButtons[ii].setImage(Button.STATE_FOCUSED,
							"assets/textures/galaxik/gui/process/" +
							"focused01.png");
					progressBar.increment(2);
					playerButtons[ii].setImage(Button.STATE_HIGHLIGHTED,
							"assets/textures/galaxik/gui/process/" +
							"highlighted01.png");
					progressBar.increment(2);
					playerButtons[ii].setImage(Button.STATE_DOWN,
							"assets/textures/galaxik/gui/process/down01.png");
					progressBar.increment(2);
				}
				else if(ii == PLAYER_BUTTON_LAUNCH) {
					playerButtons[ii].setImage(Button.STATE_NORMAL,
							"assets/textures/galaxik/gui/launch/normal01.png");
					progressBar.increment(2);
					playerButtons[ii].setImage(Button.STATE_FOCUSED,
							"assets/textures/galaxik/gui/launch/focused01.png");
					progressBar.increment(2);
					playerButtons[ii].setImage(Button.STATE_HIGHLIGHTED,
							"assets/textures/galaxik/gui/launch/" +
							"highlighted01.png");
					progressBar.increment(2);
					playerButtons[ii].setImage(Button.STATE_DOWN,
							"assets/textures/galaxik/gui/launch/down01.png");
					progressBar.increment(2);
				}
			}

			localX = ((windowWidth / 2) - (160 / 2));
			localY = ((windowHeight / 2) - (56 / 2));
			shipNumberBox = new NumericBox("ShipNumberBoxWidget", localX,
					localY, 160, 56, galaxik);
			shipNumberBox.setLabel("Number of Ships");
			shipNumberBox.setTextColor(Color.white);
			shipNumberBox.setBackgroundColor(Color.darkGray);
			shipNumberBox.setOutlineColor(Color.gray);
			progressBar.increment(8);

			localX = ((windowWidth / PLANET_PIXEL_DIVISION) + 172);
			localY = ((windowHeight - (windowHeight / 4)) + 94);
			autoBuildBox = new CheckBox("AutoBuildBoxWidget",
					localX, localY, 16, 16, galaxik);
			progressBar.increment(8);
			autoBuildBox.setBoxImage("assets/textures/gui/checkbox01/box.png");
			progressBar.increment(2);
			autoBuildBox.setCheckImage(
					"assets/textures/gui/checkbox01/mark.png");
			progressBar.increment(2);

			localX = ((windowWidth / PLANET_PIXEL_DIVISION) + 300);
			localY = ((windowHeight - (windowHeight / 4)) + 94);
			autoUpgradeBox = new CheckBox("AutoUpgradeBoxWidget",
					localX, localY, 16, 16, galaxik);
			progressBar.increment(8);
			autoUpgradeBox.setBoxImage(
					"assets/textures/gui/checkbox01/box.png");
			progressBar.increment(2);
			autoUpgradeBox.setCheckImage(
					"assets/textures/gui/checkbox01/mark.png");
			progressBar.increment(2);

			// load audio

			config = (Config)assetCache.getAsset(AssetCache.TYPE_CONFIG,
					"assets/config/audio/effects/ding01.mp3.config");
			progressBar.increment(8);
			playDing = new AudioPlayer(config, galaxik);
			progressBar.increment(8);

			config = (Config)assetCache.getAsset(AssetCache.TYPE_CONFIG,
					"assets/config/audio/effects/battle01.mp3.config");
			progressBar.increment(8);
			playBattle = new AudioPlayer(config, galaxik);
			progressBar.increment(8);

			config = (Config)assetCache.getAsset(AssetCache.TYPE_CONFIG,
					"assets/config/audio/effects/lostPlanet01.mp3.config");
			progressBar.increment(8);
			playLostPlanet = new AudioPlayer(config, galaxik);
			progressBar.increment(8);

			config = (Config)assetCache.getAsset(AssetCache.TYPE_CONFIG,
					"assets/config/audio/effects/planetCaptured01.mp3.config");
			progressBar.increment(8);
			playPlanetCaptured = new AudioPlayer(config, galaxik);
			progressBar.increment(8);

			config = (Config)assetCache.getAsset(AssetCache.TYPE_CONFIG,
					"assets/config/audio/effects/upgrade01.mp3.config");
			progressBar.increment(8);
			playUpgrade = new AudioPlayer(config, galaxik);
			progressBar.increment(8);

			// load scroll display

			scrollDisplay = new ScrollDisplay("GalaxikGameScroll", 16,
					(8 + getImageHeight(logoImage) + 24), 384, 128, galaxik);
			scrollDisplay.setColor(Color.lightGray);
			scrollDisplay.setVisibility(false);
			progressBar.increment(8);
			scrollDisplay.setBackgroundImage(
					"assets/textures/galaxik/gui/background/image02.png");
			progressBar.increment(2);

			// init game-start widgets

			localX = ((windowWidth / 10) * 2);
			localY = ((windowHeight / 10) * 2);
			localWidth = (double)((windowWidth / 10) * 6);
			localHeight = (double)((windowHeight / 10) * 6);

			fontInfo.setString("A Test String");

			localY += 32;
			playerNameBox = new TextBox("PlayerNameWidget",
					(int)(localX + ((localWidth / 2) - (128 / 2))), localY,
					128, (fontInfo.getHeight() + 8), galaxik);
			playerNameBox.setContents(playerName);
			progressBar.increment(8);
			playerNameBox.setImage(CorneredBox.IMAGE_TOP,
					"assets/textures/gui/textbox01/top.png");
			progressBar.increment(2);
			playerNameBox.setImage(CorneredBox.IMAGE_BOTTOM,
					"assets/textures/gui/textbox01/bottom.png");
			progressBar.increment(2);
			playerNameBox.setImage(CorneredBox.IMAGE_LEFT,
					"assets/textures/gui/textbox01/left.png");
			progressBar.increment(2);
			playerNameBox.setImage(CorneredBox.IMAGE_RIGHT,
					"assets/textures/gui/textbox01/right.png");
			progressBar.increment(2);
			playerNameBox.setImage(CorneredBox.IMAGE_UPPER_LEFT,
					"assets/textures/gui/textbox01/upper_left.png");
			progressBar.increment(2);
			playerNameBox.setImage(CorneredBox.IMAGE_UPPER_RIGHT,
					"assets/textures/gui/textbox01/upper_right.png");
			progressBar.increment(2);
			playerNameBox.setImage(CorneredBox.IMAGE_LOWER_LEFT,
					"assets/textures/gui/textbox01/lower_left.png");
			progressBar.increment(2);
			playerNameBox.setImage(CorneredBox.IMAGE_LOWER_RIGHT,
					"assets/textures/gui/textbox01/lower_right.png");
			progressBar.increment(2);

			localY += (32 + (fontInfo.getHeight() + 8));
			mapSizeBox = new DropdownBox("MapSizeWidget",
					(int)(localX + ((localWidth / 2) - (128 / 2))), localY,
					128, (fontInfo.getHeight() + 4), galaxik);
			progressBar.increment(8);
			mapSizeBox.addEntry("Small");
			mapSizeBox.addEntry("Medium");
			mapSizeBox.addEntry("Large");
			mapSizeBox.addEntry("Huge");
			mapSizeBox.setSelection("Medium");
			mapSizeBox.getButton().setUncorneredImage(Button.STATE_NORMAL,
					"assets/textures/gui/dropdownButton01/normal.png");
			progressBar.increment(2);
			mapSizeBox.getButton().setUncorneredImage(Button.STATE_FOCUSED,
					"assets/textures/gui/dropdownButton01/focused.png");
			progressBar.increment(2);
			mapSizeBox.getButton().setUncorneredImage(Button.STATE_HIGHLIGHTED,
					"assets/textures/gui/dropdownButton01/highlighted.png");
			progressBar.increment(2);
			mapSizeBox.getButton().setUncorneredImage(Button.STATE_DOWN,
					"assets/textures/gui/dropdownButton01/down.png");
			progressBar.increment(2);

			localY += (32 + (fontInfo.getHeight() + 4));
			aiNumberBox = new DropdownBox("AiNumberWidget",
					(int)(localX + ((localWidth / 2) - (128 / 2))), localY,
					128, (fontInfo.getHeight() + 4), galaxik);
			progressBar.increment(8);
			aiNumberBox.addEntry("2");
			aiNumberBox.addEntry("3");
			aiNumberBox.addEntry("4");
			aiNumberBox.addEntry("5");
			aiNumberBox.addEntry("6");
			aiNumberBox.addEntry("7");
			aiNumberBox.addEntry("8");
			aiNumberBox.setSelection("3");
			aiNumberBox.getButton().setUncorneredImage(Button.STATE_NORMAL,
					"assets/textures/gui/dropdownButton01/normal.png");
			progressBar.increment(2);
			aiNumberBox.getButton().setUncorneredImage(Button.STATE_FOCUSED,
					"assets/textures/gui/dropdownButton01/focused.png");
			progressBar.increment(2);
			aiNumberBox.getButton().setUncorneredImage(Button.STATE_HIGHLIGHTED,
					"assets/textures/gui/dropdownButton01/highlighted.png");
			progressBar.increment(2);
			aiNumberBox.getButton().setUncorneredImage(Button.STATE_DOWN,
					"assets/textures/gui/dropdownButton01/down.png");
			progressBar.increment(2);

			localY += (32 + (fontInfo.getHeight() + 4));
			difficultyBox = new DropdownBox("DifficultyWidget",
					(int)(localX + ((localWidth / 2) - (128 / 2))), localY,
					128, (fontInfo.getHeight() + 4), galaxik);
			progressBar.increment(8);
			difficultyBox.addEntry("Easy");
			difficultyBox.addEntry("Normal");
			difficultyBox.addEntry("Hard");
			difficultyBox.addEntry("Insane");
			difficultyBox.setSelection("Normal");
			difficultyBox.getButton().setUncorneredImage(Button.STATE_NORMAL,
					"assets/textures/gui/dropdownButton01/normal.png");
			progressBar.increment(2);
			difficultyBox.getButton().setUncorneredImage(Button.STATE_FOCUSED,
					"assets/textures/gui/dropdownButton01/focused.png");
			progressBar.increment(2);
			difficultyBox.getButton().setUncorneredImage(
					Button.STATE_HIGHLIGHTED,
					"assets/textures/gui/dropdownButton01/highlighted.png");
			progressBar.increment(2);
			difficultyBox.getButton().setUncorneredImage(Button.STATE_DOWN,
					"assets/textures/gui/dropdownButton01/down.png");
			progressBar.increment(2);

			localY += (32 + (fontInfo.getHeight() + 4));
			playButton = new Button("PlayButtonWidget",
					(int)(localX + ((localWidth / 2) - (120 / 2))), localY,
					120, 32, galaxik);
			progressBar.increment(8);
			playButton.setUncorneredImage(Button.STATE_NORMAL,
					"assets/textures/galaxik/gui/play/normal.png");
			progressBar.increment(2);
			playButton.setUncorneredImage(Button.STATE_FOCUSED,
					"assets/textures/galaxik/gui/play/focused.png");
			progressBar.increment(2);
			playButton.setUncorneredImage(
					Button.STATE_HIGHLIGHTED,
					"assets/textures/galaxik/gui/play/highlighted.png");
			progressBar.increment(2);
			playButton.setUncorneredImage(Button.STATE_DOWN,
					"assets/textures/galaxik/gui/play/down.png");
			progressBar.increment(2);

			// init game-over widgets

			hyperlink = new HyperLink("CreditsLinkWidget", 0, 0, galaxik);
			hyperlink.setLink("Xaede Game Portal", "http://www.xaede.com");
			hyperlink.setAutoRedirect(true);

			localX = ((windowWidth / 10) * 2);
			localY = ((windowHeight / 10) * 2);
			localWidth = (double)((windowWidth / 10) * 6);
			localHeight = (double)((windowHeight / 10) * 6);

			credits = new Autoscroll("CreditsWidget", localX, localY,
					(int)localWidth, (int)localHeight, galaxik);
			credits.setTextColor(Color.white);
			progressBar.increment(8);
			texture = (Texture)assetCache.getAsset(AssetCache.TYPE_TEXTURE,
					"assets/textures/galaxik/gui/background/image02.png");
			progressBar.increment(2);
			texture.buildMipMap(80, 60);
			progressBar.increment(2);
			credits.setBackground(texture.getMipMap(80, 60));
			progressBar.increment(2);
			credits.addText("");
			credits.addText("");
			credits.addText("");
			credits.addText("");
			credits.addImage(logoImage, getImageWidth(logoImage),
					getImageHeight(logoImage));
			credits.addText("");
			credits.addText("");
			credits.addText("A Game of Galactic Conquest");
			credits.addText("");
			credits.addText("Copyright (C) 2008 by Joshua S. English.");
			credits.addText("All rights reserved.");
			credits.addText("");
			credits.addText("");
			credits.addText("- Credits -");
			credits.addText("");
			credits.addText(Color.lightGray, "Game Design/Programming");
			credits.addText("");
			credits.addText("Joshua S. English");
			credits.addText("");
			credits.addText("");
			credits.addText(Color.lightGray, "Sound Design");
			credits.addText("");
			credits.addText("Joshua S. English");
			credits.addText("");
			credits.addText("");
			credits.addText(Color.lightGray, "Artwork");
			credits.addText("");
			credits.addText("Joshua S. English");
			credits.addText("");
			credits.addText("");
			credits.addText(Color.lightGray, "- Special Thanks -");
			credits.addText("");
			credits.addText("Sponsoring Websites");
			credits.addText("");
			credits.addHyperlink(hyperlink);

			// battle event trigger

			consumer = new BattleTriggerConsumer(
					"GalaxikBattleTriggerConsumer");
			progressBar.increment(8);
			action = new TriggerActionImpl("GalaxikBattleTrigger");
			action.registerConsumer(BattleTriggerEvent.EVENT_NAME,
					consumer);
			progressBar.increment(8);

			// setup trigger system

			triggerSystem = new TriggerSystem("GalaxikGameTriggers");
			triggerSystem.registerAction(action);
			progressBar.increment(8);

			// set remaining internals

			internalGameLoaded = true;

			if(!USE_BACKGROUND) {
				setBackground(Color.black);
			}

			dialogue = DIALOGUE_START;

			System.out.println("Game loading thread shutdown.");
		}
	}

	// game functions

	private synchronized void initGame()
	{
		Player player = null;
		Texture texture = null;

		// init the game logic

		game = new Game(sceneWidth, sceneHeight, GameScene.PLANET_PIXELS,
				planetImages, this);

		scrollDisplay.log("Initialized game");
		scrollDisplay.log("Built " + game.getPlanets().size() + " planets");

		game.addPlayer(Player.HUMAN, playerName);
		player = game.getPlayer(playerName);
		playerId = player.getId();

		scrollDisplay.log("Created player #" + playerId + " named '" +
				playerName + "'");

		// begin constructing the scene

		scene = new GameScene("GalaxikScene", 0, 0, windowWidth, windowHeight,
				game, this);
		scene.setSpriteSize(GameScene.PLANET_PIXELS, GameScene.PLANET_PIXELS);

		// load scene background

		texture = (Texture)assetCache.getAsset(AssetCache.TYPE_TEXTURE,
				"assets/textures/galaxik/starscape.png");
		texture.buildMipMap(sceneWidth, sceneHeight);
		scene.commitBackground(texture.getMipMap(sceneWidth, sceneHeight),
				sceneWidth, sceneHeight);

		camera = scene.getCamera();
	}

	private synchronized void constructSprites()
	{
		Iterator iterator = null;
		Sprite sprite = null;
		Planet planet = null;
		Texture texture = null;

		// construct sprites

		iterator = game.getPlanets().iterator();
		while(iterator.hasNext()) {
			planet = (Planet)iterator.next();
			if(planet == null) {
				break;
			}

			sprite = new Sprite(planet.getName(), 0, 0,
					GameScene.PLANET_PIXELS, GameScene.PLANET_PIXELS,
					this);
			sprite.setDebugMode(false);
			sprite.setStatic(false);
			sprite.setOutline(true);
			sprite.setSelectable(true);
			sprite.setImageTexture(Sprite.DIRECTION_EAST,
					planet.getImageName());
			if(USE_HIGHLIGHT_IMAGE) {
				texture = (Texture)assetCache.getAsset(
						AssetCache.TYPE_TEXTURE,
						"assets/textures/galaxik/planetHighlight.png");
				sprite.setOutlineImage(texture.getMipMap(
							GameScene.PLANET_PIXELS,
							GameScene.PLANET_PIXELS));
			}
			scene.commitSprite(sprite,
					(planet.getX() * GameScene.PLANET_PIXELS),
					(planet.getY() * GameScene.PLANET_PIXELS));
		}

		scene.reset();

		scrollDisplay.log("Constructed sprites");
	}

	private synchronized void initAI(int number)
	{
		int ii = 0;

		for(ii = 0; ii < number; ii++) {
			game.addPlayer(Player.AI, null);

			scrollDisplay.log("Constructed AI #" + ii + " named '" +
					game.getPlayer(ii + 1).getName() + "'");
		}

		game.processPlanets();
	}

	private void launchFleet(Planet origin, Planet destination, int size,
			int distance)
	{
		Fleet fleet = null;
		Travel travel = null;

		try {
			fleet = origin.getFleet().divide(size);
			distance = (int)((double)distance / 
					fleet.getTechnology().getSpeed());
			if(distance < 1) {
				distance = 1;
			}
			travel = new Travel(playerId, origin.getId(), destination.getId(),
					distance, fleet);
			game.addTravel(travel);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	// graphics rendering functions

	public synchronized void pipeline()
	{
		int ii = 0;
		int localX = 0;
		int localY = 0;
		int localWidth = 0;
		int localHeight = 0;

		Image image = null;
		Texture texture = null;
		String text = null;
		Planet planet = null;
		Player player = null;
		Sprite sprite = null;

		if(!isGameLoaded) {
			return;
		}

		// render loading screen

		if(!internalGameLoaded) {
			drawImage(loadingImage, (windowWidth / 8), (windowHeight / 8),
					((windowWidth / 8) * 6), ((windowHeight / 8) * 6));
			drawImage(gameEngineLogo,
					((windowWidth / 2) - (32 / 2)), ((windowHeight / 8) * 5),
					32, 48);
			progressBar.render2D(render2D);
			return;
		}

		// render default background

		if(USE_BACKGROUND) {
			drawImage(background, 0, 0, windowWidth, windowHeight);
		}

		// check for non-playing diaglogues

		if(dialogue == DIALOGUE_START) {
			// render game-start dialogue

			texture = (Texture)assetCache.getAsset(AssetCache.TYPE_TEXTURE,
					"assets/textures/galaxik/starscape.png");
			drawImage(texture.getDefaultImage(), 0, 0, windowWidth,
					windowHeight);

			localX = ((windowWidth / 10) * 2);
			localY = ((windowHeight / 10) * 2);
			localWidth = ((windowWidth / 10) * 6);
			localHeight = ((windowHeight / 10) * 6);

			if(USE_DIALOGUE_IMAGE) {
				drawImage(dialogueImage, localX, localY, localWidth,
						localHeight);
			}
			else {
				setColor(Color.white);
				fillRect(localX, localY, localWidth, localHeight);
				setColor(Color.blue);
				drawRect(localX, localY, localWidth, localHeight);
			}

			drawImage(logoImage, 16, 16, getImageWidth(logoImage),
					getImageHeight(logoImage));

			if(startMode == START_MODE_DIALOGUE) {
				setColor(Color.black);

				fontInfo.setString("- Enter Player Name -");
				localY += (fontInfo.getHeight() + 16);
				drawString("- Enter Player Name -",
						((localX + (localWidth / 2)) -
						 (fontInfo.getWidth() / 2)), localY);

				fontInfo.setString("- Select Map Size -");
				localY += (32 + (fontInfo.getHeight() + 4));
				drawString("- Select Map Size -",
						((localX + (localWidth / 2)) -
						 (fontInfo.getWidth() / 2)), localY);

				fontInfo.setString("- Select Number of AI Players -");
				localY += (32 + (fontInfo.getHeight() + 4));
				drawString("- Select Number of AI Players -",
						((localX + (localWidth / 2)) -
						 (fontInfo.getWidth() / 2)), localY);

				fontInfo.setString("- Select Difficulty Level -");
				localY += (32 + (fontInfo.getHeight() + 4));
				drawString("- Select Difficulty Level -",
						((localX + (localWidth / 2)) -
						 (fontInfo.getWidth() / 2)), localY);

				playButton.render2D(render2D);
				difficultyBox.render2D(render2D);
				aiNumberBox.render2D(render2D);
				mapSizeBox.render2D(render2D);
				playerNameBox.render2D(render2D);
			}

			return;
		}
		else if(dialogue == DIALOGUE_OVER) {
			// render game-over dialogue

			texture = (Texture)assetCache.getAsset(AssetCache.TYPE_TEXTURE,
					"assets/textures/galaxik/starscape.png");
			drawImage(texture.getDefaultImage(), 0, 0, windowWidth,
					windowHeight);

			credits.render2D(render2D);

			return;
		}

		// render scene

		scene.render2D(render2D);

		// render foreground

		drawImage(foreground, 0, 0, windowWidth, windowHeight);
		drawImage(logoImage, 16, 16, getImageWidth(logoImage),
				getImageHeight(logoImage));
		setColor(Color.white);
		drawString("(" + (sceneWidth / GameScene.PLANET_PIXELS) + " x " +
				(sceneHeight / GameScene.PLANET_PIXELS) + ")", 114, 38);
		drawString("Turn #" + turnCounter, 114, 22);

		// render scroll display & scroll arrows

		scrollDisplay.render2D(render2D);

		text = null;
		for(ii = 0; ii < SCROLL_ARROWS; ii++) {
			scrollArrows[ii].render2D(render2D);
			if((ii == SCROLL_ARROW_UP) && (scrollArrows[ii].isFocused())) {
				text = "Hide Events";
			}
			else if((ii == SCROLL_ARROW_DOWN) &&
					(scrollArrows[ii].isFocused())) {
				text = "View Events";
			}
		}
		if(text != null) {
			setColor(Color.white);
			drawString(text, (24 + 20), (20 + getImageHeight(logoImage)));
		}

		// render selected planet

		planet = null;
		if(selectedPlanetAlternate != null) {
			planet = selectedPlanetAlternate;
		}
		else if(selectedPlanet != null) {
			planet = selectedPlanet;
		}

		if(planet != null) {
			// render planet details

			texture = (Texture)assetCache.getAsset(AssetCache.TYPE_TEXTURE,
					planet.getImageName());
			image = texture.getMipMap((windowWidth / PLANET_PIXEL_DIVISION),
					(windowWidth / PLANET_PIXEL_DIVISION));
			drawImage(image, 12, ((windowHeight - (windowHeight / 4)) + 20),
				(windowWidth / PLANET_PIXEL_DIVISION),
				(windowWidth / PLANET_PIXEL_DIVISION));

			setColor(Color.black);
			drawString("\"" + planet.getName() + "\"",
					((windowWidth / PLANET_PIXEL_DIVISION) + 16),
					((windowHeight - (windowHeight / 4)) + 32));
			if(planet.getOwnerId() == Game.NEUTRAL_PLANET_ID) {
				text = "Neutral";
			}
			else {
				player = game.getPlayer(planet.getOwnerId());
				text = player.getName();
			}

			drawString("Owner: " + text,
					((windowWidth / PLANET_PIXEL_DIVISION) + 16),
					((windowHeight - (windowHeight / 4)) + 46));

			if((planet.getOwnerId() == playerId) &&
					(dialogue != DIALOGUE_TRIGGER)) {
				drawString("Player Credits: " + player.getCredits(),
						((windowWidth / PLANET_PIXEL_DIVISION) + 16),
						((windowHeight - (windowHeight / 4)) + 62));
			}
			else {
				drawString("Credits: Unknown",
						((windowWidth / PLANET_PIXEL_DIVISION) + 16),
						((windowHeight - (windowHeight / 4)) + 62));
			}

			if(((planet.getOwnerId() == playerId) || (debugMode)) &&
					(dialogue != DIALOGUE_TRIGGER)) {
				drawString("Credit Production: " +
						planet.getCreditsProduction(),
						((windowWidth / PLANET_PIXEL_DIVISION) + 16),
						((windowHeight - (windowHeight / 4)) + 76));
			}
			else {
				drawString("Credit Production: Unknown",
						((windowWidth / PLANET_PIXEL_DIVISION) + 16),
						((windowHeight - (windowHeight / 4)) + 76));
			}

			if(((planet.getOwnerId() == playerId) || (debugMode)) &&
					(dialogue != DIALOGUE_TRIGGER)) {
				drawString("Ship Production: " +
						planet.getShipProduction(),
						((windowWidth / PLANET_PIXEL_DIVISION) + 16),
						((windowHeight - (windowHeight / 4)) + 92));
			}
			else {
				drawString("Ship Production: Unknown",
						((windowWidth / PLANET_PIXEL_DIVISION) + 16),
						((windowHeight - (windowHeight / 4)) + 92));
			}

			if(((planet.getOwnerId() == playerId) || (debugMode)) &&
					(dialogue != DIALOGUE_TRIGGER)) {
				drawString("Available ships: " +
						planet.getFleet().getShipCount(),
						((windowWidth / PLANET_PIXEL_DIVISION) + 16),
						((windowHeight - (windowHeight / 4)) + 108));
			}
			else {
				drawString("Available ships: Unknown",
						((windowWidth / PLANET_PIXEL_DIVISION) + 16),
						((windowHeight - (windowHeight / 4)) + 108));
			}

			drawString("Technology Levels",
					((windowWidth - 12) - 108),
					((windowHeight - (windowHeight / 4)) + 32));

			if(((planet.getOwnerId() == playerId) || (debugMode)) &&
					(dialogue != DIALOGUE_TRIGGER)) {
				for(ii = 0; ii < PLAYER_BUTTONS; ii++) {
					playerButtons[ii].render2D(render2D);
					if((playerButtons[ii].isFocused()) ||
							(equalizer.hasBarFocus())) {
						if(equalizer.hasBarFocus()) {
							switch(equalizer.getBarFocus()) {
								case 0:
									text = "Hull Technology: " +
										planet.getTechnology().getHull();
									break;
								case 1:
									text = "Frame Technology: " +
										planet.getTechnology().getFrame();
									break;
								case 2:
									text = "Armor Technology: " +
										planet.getTechnology().getArmor();
									break;
								case 3:
									text = "Weapon Technology: " +
										planet.getTechnology().getWeapon();
									break;
								case 4:
									text = "Engine Technology: " +
										planet.getTechnology().getEngine();
									break;
								case 5:
									text = "Shields Technology: " +
										planet.getTechnology().getShields();
									break;
							}
						}
						else if(ii == PLAYER_BUTTON_UPGRADE) {
							text = "Perfom technology upgrade";
						}
						else if(ii == PLAYER_BUTTON_PROCESS) {
							text = "Build ships";
						}
						else if(ii == PLAYER_BUTTON_LAUNCH) {
							text = "Create fleet";
						}
						drawString(text,
								((windowWidth / PLANET_PIXEL_DIVISION) + 256),
								((windowHeight - (windowHeight / 4)) + 92));
					}
				}

				autoBuildBox.render2D(render2D);
				drawString("Auto-build ships",
						((windowWidth / PLANET_PIXEL_DIVISION) + 172 + 20),
						((windowHeight - (windowHeight / 4)) + 108));

				autoUpgradeBox.render2D(render2D);
				drawString("Auto-upgrade technology",
						((windowWidth / PLANET_PIXEL_DIVISION) + 300 + 20),
						((windowHeight - (windowHeight / 4)) + 108));

				equalizer.setBarValue(0,
						planet.getTechnology().getHull());
				equalizer.setBarValue(1,
						planet.getTechnology().getFrame());
				equalizer.setBarValue(2,
						planet.getTechnology().getArmor());
				equalizer.setBarValue(3,
						planet.getTechnology().getWeapon());
				equalizer.setBarValue(4,
						planet.getTechnology().getEngine());
				equalizer.setBarValue(5,
						planet.getTechnology().getShields());
				equalizer.render2D(render2D);
			}
			else {
				drawImage(notAvailable, ((windowWidth - 12) - 124),
						((windowHeight - (windowHeight / 4)) + 36),
						126, 76);
			}

			setColor(Color.black);
			drawRect(((windowWidth - 12) - 124),
					((windowHeight - (windowHeight / 4)) + 18),
					126, 94);

			if(dialogue == DIALOGUE_TRIGGER) {
				setColor(Color.red);
				drawString("- Battle Results -",
						((windowWidth / PLANET_PIXEL_DIVISION) + 256),
						((windowHeight - (windowHeight / 4)) + 32));

				drawString("Winner: " +
						triggerContext.getPlayerWin().getName(),
						((windowWidth / PLANET_PIXEL_DIVISION) + 256),
						((windowHeight - (windowHeight / 4)) + 62));

				if(triggerContext.getPlayerLost() != null) {
					text = triggerContext.getPlayerLost().getName();
				}
				else {
					text = "Neutral Planet";
				}
				drawString("Loser: " + text,
						((windowWidth / PLANET_PIXEL_DIVISION) + 256),
						((windowHeight - (windowHeight / 4)) + 76));
			}
		}
		else {
			// render player details

			drawImage(playerIcon, 12,
					((windowHeight - (windowHeight / 4)) + 20),
					(windowWidth / PLANET_PIXEL_DIVISION),
					(windowWidth / PLANET_PIXEL_DIVISION));

			player = game.getPlayer(playerName);

			setColor(Color.black);

			drawString("Player \"" + player.getName() + "\"",
					((windowWidth / PLANET_PIXEL_DIVISION) + 16),
					((windowHeight - (windowHeight / 4)) + 32));

			drawString("Ships built: " + player.getShipsBuilt(),
					((windowWidth / PLANET_PIXEL_DIVISION) + 16),
					((windowHeight - (windowHeight / 4)) + 46));

			drawString("Ships destroyed: " + player.getShipsDestroyed(),
					((windowWidth / PLANET_PIXEL_DIVISION) + 16),
					((windowHeight - (windowHeight / 4)) + 62));

			drawString("Ships lost: " + player.getShipsLost(),
					((windowWidth / PLANET_PIXEL_DIVISION) + 16),
					((windowHeight - (windowHeight / 4)) + 76));

			drawString("Build efficiency: " +
					(int)(100.0 - (((double)player.getShipsLost() /
							   (double)player.getShipsBuilt()) * 100.0)) + "%",
					((windowWidth / PLANET_PIXEL_DIVISION) + 16),
					((windowHeight - (windowHeight / 4)) + 92));

			drawString("Battle efficiency: " +
					(int)(100.0 - (((double)player.getShipsLost() /
							   (double)player.getShipsDestroyed()) * 100.0)) +
					"%",
					((windowWidth / PLANET_PIXEL_DIVISION) + 16),
					((windowHeight - (windowHeight / 4)) + 108));

			drawString("Planets conquered: " + player.getPlanetsConquered(),
					((windowWidth / PLANET_PIXEL_DIVISION) + 256),
					((windowHeight - (windowHeight / 4)) + 32));

			drawString("Planets lost: " + player.getPlanetsLost(),
					((windowWidth / PLANET_PIXEL_DIVISION) + 256),
					((windowHeight - (windowHeight / 4)) + 46));

			drawString("Conquer efficiency: " +
					(int)(100.0 - (((double)player.getPlanetsLost() /
							   (double)player.getPlanetsConquered()) *
							  100.0)) + "%",
					((windowWidth / PLANET_PIXEL_DIVISION) + 256),
					((windowHeight - (windowHeight / 4)) + 62));

			drawString("Planet-to-ship ratio: " +
					(int)(100.0 - (((double)(player.getPlanetsConquered() -
										player.getPlanetsLost()) /
							   (double)(player.getShipsBuilt() -
								   player.getShipsLost())) * 100.0)) + "%",
					((windowWidth / PLANET_PIXEL_DIVISION) + 256),
					((windowHeight - (windowHeight / 4)) + 76));

			drawString("Credits: " + player.getCredits(),
					((windowWidth / PLANET_PIXEL_DIVISION) + 256),
					((windowHeight - (windowHeight / 4)) + 92));

			drawString("Credits produced: " + player.getCreditsProduced(),
					((windowWidth / PLANET_PIXEL_DIVISION) + 256),
					((windowHeight - (windowHeight / 4)) + 108));

			// draw the map overview

			localX = ((windowWidth - 12) - 156);
			localY = ((windowHeight - (windowHeight / 4)) + 18);
			localWidth = 158;
			localHeight = 94;

			image = scene.snapshot(sceneWidth, sceneHeight);
			drawImage(image, localX, localY, localWidth, localHeight);

			localX += (int)((double)camera.getCameraX() *
						((double)localWidth / (double)scene.getZoomWidth()));
			localWidth = (int)((double)localWidth *
						((double)windowWidth / (double)scene.getZoomWidth()));
			if(localX < ((windowWidth - 12) - 156)) {
				localWidth -= (((windowWidth - 12) - 156) - localX);
				localX = ((windowWidth - 12) - 156);
			}
			if((localX + localWidth) > (windowWidth - 12)) {
				localWidth = ((windowWidth - 12) - localX);
			}
			localY += (int)((double)camera.getCameraY() *
						((double)localHeight / (double)scene.getZoomHeight()));
			localHeight = (int)((double)localHeight *
						((double)windowHeight / (double)scene.getZoomHeight()));
			if(localY < ((windowHeight - (windowHeight / 4)) + 18)) {
				localHeight -= (((windowHeight - (windowHeight / 4)) + 18) -
						localY);
				localY = ((windowHeight - (windowHeight / 4)) + 18);
			}
			if((localY + localHeight) > (windowHeight - 8)) {
				localHeight = ((windowHeight - 8) - localY);
			}

			setColor(Color.blue);
			drawRect(localX, localY, localWidth, localHeight);

			localX = ((windowWidth - 12) - 156);
			localY = ((windowHeight - (windowHeight / 4)) + 18);
			localWidth = 158;
			localHeight = 94;
			setColor(Color.lightGray);
			drawRect(localX, localY, localWidth, localHeight);
		}

		// render magifying glass, zoom level & zoom buttons, & beta icon

		drawImage(magnifyingGlass,
				((windowWidth - MAGNIFYING_GLASS_PIXELS) - 8), 8,
				MAGNIFYING_GLASS_PIXELS, MAGNIFYING_GLASS_PIXELS);
		setColor(Color.black);
		drawString(scene.getZoom() + " %",
				(windowWidth - MAGNIFYING_GLASS_PIXELS), 46);

		text = null;
		for(ii = 0; ii < ZOOM_ARROWS; ii++) {
			zoomArrows[ii].render2D(render2D);
			if((ii == ZOOM_ARROW_UP) && (zoomArrows[ii].isFocused())) {
				text = "Zoom In";
			}
			else if((ii == ZOOM_ARROW_DOWN) && (zoomArrows[ii].isFocused())) {
				text = "Zoom Out";
			}
		}
		if(text != null) {
			setColor(Color.white);
			drawString(text, ((windowWidth - MAGNIFYING_GLASS_PIXELS) - 8),
					108);
		}

		drawImage(betaIcon,
				((windowWidth - MAGNIFYING_GLASS_PIXELS) - 78), 8,
				64, 26);

		// render turn over

		if(isTurnOver) {
			setColor(Color.white);
			fontInfo.setString("... Press (enter) to finish turn ...");
			drawString("... Press (enter) to finish turn ...",
					((windowWidth / 2) - (fontInfo.getWidth() / 2)),
					((windowHeight - (windowHeight / 4)) + 0));
		}

		// render trigger dialogue

		if(dialogue == DIALOGUE_TRIGGER) {
			if((WAIT_FOR_SOUND_COMPLETION) && (!playBattle.isPlaying())) {
				return;
			}
			setColor(Color.white);
			fontInfo.setString("... Press (enter) to continue ...");
			drawString("... Press (enter) to continue ...",
					((windowWidth / 2) - (fontInfo.getWidth() / 2)),
					((windowHeight - (windowHeight / 4)) + 0));
		}

		// launch fleet render

		if((isLaunchMode) && (selectedPlanet != null)) {
			if(!selectShipsMode) {
				sprite = scene.getSprite(selectedPlanet.getName());
				setColor(Color.red);
				drawLine(
						(sprite.getXWithOffset() +
						 (scene.getSpriteZoomWidth() / 2)),
						(sprite.getYWithOffset() +
						 (scene.getSpriteZoomHeight() / 2)),
						mouseX, mouseY);
			}
			else {
				shipNumberBox.render2D(render2D);
			}
		}

		// render debug information

		if(debugMode) {
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
			drawString("Camera{" + camera.getName() + "}@(" +
					camera.getCameraX() + ", " + camera.getCameraY() + ")",
					0, 60);
			drawString("Zoom: " + scene.getZoom() + " % (" +
					scene.getZoomWidth() + " x " + scene.getZoomHeight() + ")",
					0, 76);
		}
	}

	// event handling

	public synchronized void handleMouseEvents(int type, MouseEvent event)
	{
		boolean wasClicked = false;
		int ii = 0;
		int nn = 0;
		int threshold = 0;
		int snapshotX = 0;
		int snapshotY = 0;
		int snapshotWidth = 0;
		int snapshotHeight = 0;

		Player player = null;
		Iterator iterator = null;
		Sprite sprite = null;
		Sprite selectedSprite = null;
		Planet planet = null;

		if((!isGameLoaded) || (!internalGameLoaded)) {
			return;
		}

		if(dialogue == DIALOGUE_START) {
			if(startMode == START_MODE_DIALOGUE) {
				wasClicked = false;
				if(type == MouseDriver.EVENT_PRESSED) {
					if(playerNameBox.isClicked(mouseX, mouseY)) {
						mapSizeBox.deFocus();
						aiNumberBox.deFocus();
						difficultyBox.deFocus();
						playButton.deFocus();
					}
					else if(mapSizeBox.isClicked(mouseX, mouseY)) {
						playerNameBox.deFocus();
						aiNumberBox.deFocus();
						difficultyBox.deFocus();
						playButton.deFocus();
					}
					else if(aiNumberBox.isClicked(mouseX, mouseY)) {
						playerNameBox.deFocus();
						mapSizeBox.deFocus();
						difficultyBox.deFocus();
						playButton.deFocus();
					}
					else if(difficultyBox.isClicked(mouseX, mouseY)) {
						playerNameBox.deFocus();
						mapSizeBox.deFocus();
						aiNumberBox.deFocus();
						playButton.deFocus();
					}
					else if(playButton.isClicked(mouseX, mouseY)) {
						playerNameBox.deFocus();
						mapSizeBox.deFocus();
						aiNumberBox.deFocus();
						difficultyBox.deFocus();
						wasClicked = true;
					}
				}
				else {
					if(playerNameBox.hasFocus(mouseX, mouseY)) {
						mapSizeBox.deFocus();
						aiNumberBox.deFocus();
						difficultyBox.deFocus();
						playButton.deFocus();
					}
					else if(mapSizeBox.hasFocus(mouseX, mouseY)) {
						playerNameBox.deFocus();
						aiNumberBox.deFocus();
						difficultyBox.deFocus();
						playButton.deFocus();
					}
					else if(aiNumberBox.hasFocus(mouseX, mouseY)) {
						playerNameBox.deFocus();
						mapSizeBox.deFocus();
						difficultyBox.deFocus();
						playButton.deFocus();
					}
					else if(difficultyBox.hasFocus(mouseX, mouseY)) {
						playerNameBox.deFocus();
						mapSizeBox.deFocus();
						aiNumberBox.deFocus();
						playButton.deFocus();
					}
					else if(playButton.hasFocus(mouseX, mouseY)) {
						playerNameBox.deFocus();
						mapSizeBox.deFocus();
						aiNumberBox.deFocus();
						difficultyBox.deFocus();
					}
				}
				if(wasClicked) {

					dialogueImage = (Image)assetCache.getAsset(
							AssetCache.TYPE_IMAGE,
							"assets/textures/galaxik/dialogueImage.png");

					setCursor(busyCursor);

					startMode = START_MODE_DISPLAY_PROGRESS;
				}
			}
			else if(startMode == START_MODE_DISPLAY_PROGRESS) {
				// do nothing
			}

			return;
		}
		else if(dialogue == DIALOGUE_CAMERA) {
			return; // ignore mouse input
		}
		else if(dialogue == DIALOGUE_TRIGGER) {
			return; // ignore mouse input
		}
		else if(dialogue == DIALOGUE_OVER) {
			if(type == MouseDriver.EVENT_PRESSED) {
				credits.isClicked(mouseX, mouseY);
			}
			else {
				credits.hasFocus(mouseX, mouseY);
			}

			return;
		}

		// process scene-level events

		threshold = (int)((double)windowHeight *
				(MOUSE_DETECTION_THRESHOLD / 100.0));

		if(mouseY < threshold) {
			if(type == MouseDriver.EVENT_PRESSED) {
				for(ii = 0; ii < ZOOM_ARROWS; ii++) {
					if(zoomArrows[ii].isClicked(mouseX, mouseY)) {
						for(nn = 0; nn < ZOOM_ARROWS; nn++) {
							if(ii == nn) {
								continue;
							}
							zoomArrows[nn].setUnHighlighted();
						}
						if(ii == ZOOM_ARROW_UP) {
							scene.handleZoom(1, isLaunchMode, selectedPlanet);
						}
						else if(ii == ZOOM_ARROW_DOWN) {
							scene.handleZoom(-1, isLaunchMode, selectedPlanet);
						}
						return;
					}
				}
				for(ii = 0; ii < SCROLL_ARROWS; ii++) {
					if(scrollArrows[ii].isClicked(mouseX, mouseY)) {
						for(nn = 0; nn < SCROLL_ARROWS; nn++) {
							if(ii == nn) {
								continue;
							}
							scrollArrows[nn].setUnHighlighted();
						}
						if(ii == SCROLL_ARROW_UP) {
							scrollArrows[SCROLL_ARROW_UP].setVisibility(false);
							scrollArrows[SCROLL_ARROW_DOWN].setVisibility(true);
							scrollDisplay.setVisibility(false);
						}
						else if(ii == SCROLL_ARROW_DOWN) {
							scrollArrows[SCROLL_ARROW_UP].setVisibility(true);
							scrollArrows[SCROLL_ARROW_DOWN].setVisibility(
									false);
							scrollDisplay.setVisibility(true);
						}
						return;
					}
				}
				if(isLaunchMode) {
					if(!selectShipsMode) {
						try {
							selectedSprite = scene.getSprite(
									selectedPlanet.getName());
							iterator = scene.getSpriteSet().iterator();
							while(iterator.hasNext()) {
								sprite = (Sprite)iterator.next();
								if(sprite == null) {
									break;
								}

								if(!sprite.isVisible()) {
									continue;
								}

								if((sprite.hasOutline()) &&
										(sprite != selectedSprite)) {
									selectShipsMode = true;
									shipNumberBox.reset();
									shipNumberBox.setupNumbers(
											(selectedPlanet.getFleet().
											 getShipCount() / 2),
											0, 
											selectedPlanet.getFleet().
											getShipCount());
									break;
								}
							}
						}
						catch(Exception e) {
							e.printStackTrace();
						}
						if(!selectShipsMode) {
							scene.resetSelections();
							scene.resetOutlineColor(Color.blue);
							isLaunchMode = false;
							selectShipsMode = false;
							panCameraDiffX = 0;
							panCameraDiffY = 0;
						}
					}
					else {
						shipNumberBox.isClicked(mouseX, mouseY);
						if(shipNumberBox.isCancelled()) {
							scene.resetSelections();
							scene.resetOutlineColor(Color.blue);
							isLaunchMode = false;
							selectShipsMode = false;
							panCameraDiffX = 0;
							panCameraDiffY = 0;
						}
						else if(shipNumberBox.hasResult()) {
							try {
								selectedSprite = scene.getSprite(
										selectedPlanet.getName());
								iterator = scene.getSpriteSet().iterator();
								while(iterator.hasNext()) {
									sprite = (Sprite)iterator.next();
									if(sprite == null) {
										break;
									}

									if(!sprite.isVisible()) {
										continue;
									}

									if((sprite.hasOutline()) &&
											(sprite != selectedSprite)) {
										planet = game.getPlanet(
												sprite.getName());
										if(planet.getOwnerId() != playerId) {
											scrollDisplay.log(Color.red,
													"Attacking planet '" +
													sprite.getName() + "' " +
													"with " +
													shipNumberBox.getResult() +
													" ships");
										}
										else {
											scrollDisplay.log(Color.green,
													"Docking at planet '" +
													sprite.getName() + "' " +
													"with " +
													shipNumberBox.getResult() +
													" ships");
										}

										launchFleet(selectedPlanet, planet,
												shipNumberBox.getResult(),
												(int)Game.calculateDistance(
													selectedPlanet, planet));

										scene.resetSelections();
										scene.resetOutlineColor(Color.blue);
										isLaunchMode = false;
										selectShipsMode = false;
										panCameraDiffX = 0;
										panCameraDiffY = 0;
										break;
									}
								}
							}
							catch(Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
			else {
				for(ii = 0; ii < ZOOM_ARROWS; ii++) {
					if(zoomArrows[ii].hasFocus(mouseX, mouseY)) {
						for(nn = 0; nn < ZOOM_ARROWS; nn++) {
							if(ii == nn) {
								continue;
							}
							zoomArrows[nn].deFocus();
						}
						autoBuildBox.deFocus();
						autoUpgradeBox.deFocus();
						break;
					}
				}
				for(ii = 0; ii < SCROLL_ARROWS; ii++) {
					if(scrollArrows[ii].hasFocus(mouseX, mouseY)) {
						for(nn = 0; nn < SCROLL_ARROWS; nn++) {
							if(ii == nn) {
								continue;
							}
							scrollArrows[nn].deFocus();
						}
						autoBuildBox.deFocus();
						autoUpgradeBox.deFocus();
						break;
					}
				}
				if(isLaunchMode) {
					if(!selectShipsMode) {
						panCameraDiffX = 0;
						panCameraDiffY = 0;

						if((mouseX >= 0) && (mouseX <= (8 + 32))) {
							panCameraDiffX = -(windowWidth / 160);
						}
						else if((mouseX >= ((windowWidth - 8) - 32)) &&
								(mouseX <= windowWidth)) {
							panCameraDiffX = (windowWidth / 160);
						}
						if((mouseY >= 0) && (mouseY <= (8 + 32))) {
							panCameraDiffY = -(windowHeight / 120);
						}
						else if((mouseY >= ((threshold - 8) - 32)) &&
								(mouseY <= threshold)) {
							panCameraDiffY = (windowHeight / 120);
						}
					}
					else {
						shipNumberBox.hasFocus(mouseX, mouseY);
					}
				}
			}

			if(!selectShipsMode) {
				scene.processMouseEvent(type, mouseX, mouseY, event,
						isLaunchMode, selectedPlanet);
			}

			return;
		}

		// process the other events

		if(type != MouseDriver.EVENT_PRESSED) {
			for(ii = 0; ii < PLAYER_BUTTONS; ii++) {
				if(playerButtons[ii].hasFocus(mouseX, mouseY)) {
					for(nn = 0; nn < PLAYER_BUTTONS; nn++) {
						if(ii == nn) {
							continue;
						}
						playerButtons[nn].deFocus();
					}
					autoBuildBox.deFocus();
					autoUpgradeBox.deFocus();
					equalizer.deFocus();
					break;
				}
			}
			if(autoBuildBox.hasFocus(mouseX, mouseY)) {
				for(ii = 0; ii < PLAYER_BUTTONS; ii++) {
					playerButtons[nn].deFocus();
				}
				autoUpgradeBox.deFocus();
				equalizer.deFocus();
			}
			if(autoUpgradeBox.hasFocus(mouseX, mouseY)) {
				for(ii = 0; ii < PLAYER_BUTTONS; ii++) {
					playerButtons[nn].deFocus();
				}
				autoBuildBox.deFocus();
				equalizer.deFocus();
			}
			if(equalizer.hasFocus(mouseX, mouseY)) {
				for(ii = 0; ii < PLAYER_BUTTONS; ii++) {
					playerButtons[nn].deFocus();
				}
				autoBuildBox.deFocus();
				autoUpgradeBox.deFocus();
			}

			// detect & center the camera from a snapshot click

			snapshotX = ((windowWidth - 12) - 156);
			snapshotY = ((windowHeight - (windowHeight / 4)) + 18);
			snapshotWidth = 158;
			snapshotHeight = 94;

			if((selectedPlanet == null) &&
					(mouseX >= snapshotX) &&
					(mouseX <= (snapshotX + snapshotWidth)) &&
					(mouseY >= snapshotY) &&
					(mouseY <= (snapshotY + snapshotHeight))) {
				setCursor(snapshotCursor);
			}
			else {
				setCursor(cursor);
			}
		}
		else if(type == MouseDriver.EVENT_PRESSED) {
			if(isLaunchMode) {
				if(!selectShipsMode) {
					scene.resetOutlineColor(Color.blue);
					isLaunchMode = false;
					selectShipsMode = false;
					panCameraDiffX = 0;
					panCameraDiffY = 0;
				}
			}

			for(ii = 0; ii < PLAYER_BUTTONS; ii++) {
				if(playerButtons[ii].isClicked(mouseX, mouseY)) {
					for(nn = 0; nn < PLAYER_BUTTONS; nn++) {
						if(ii == nn) {
							continue;
						}
						playerButtons[nn].setUnHighlighted();
					}

					if(ii == PLAYER_BUTTON_UPGRADE) {
						player = game.getPlayer(playerName);
						game.upgradePlayerPlanet(selectedPlanet.getName(),
								player.getCredits());
						playUpgrade.play();
					}
					else if(ii == PLAYER_BUTTON_PROCESS) {
						if(!selectedPlanet.turnOver()) {
							player = game.getPlayer(playerName);
							game.processPlayerPlanet(selectedPlanet.getName(),
									false, player.getCredits());
						}
					}
					else if(ii == PLAYER_BUTTON_LAUNCH) {
						if((selectedPlanet != null) &&
								(selectedPlanet.getFleet().getShipCount() >
								 0)) {
							isLaunchMode = true;
							selectShipsMode = false;
						}
					}

					break;
				}
			}

			if((autoBuildBox.isClicked(mouseX, mouseY)) &&
					(selectedPlanet != null)) {
				player = game.getPlayer(playerName);
				if(selectedPlanet.getOwnerId() == player.getId()) {
					selectedPlanet.setAutoBuild(autoBuildBox.isChecked());
				}
			}
			if((autoUpgradeBox.isClicked(mouseX, mouseY)) &&
					(selectedPlanet != null)) {
				player = game.getPlayer(playerName);
				if(selectedPlanet.getOwnerId() == player.getId()) {
					selectedPlanet.setAutoUpgrade(autoUpgradeBox.isChecked());
				}
			}

			// detect & center the camera from a snapshot click

			snapshotX = ((windowWidth - 12) - 156);
			snapshotY = ((windowHeight - (windowHeight / 4)) + 18);
			snapshotWidth = 158;
			snapshotHeight = 94;

			if((selectedPlanet == null) &&
					(mouseX >= snapshotX) &&
					(mouseX <= (snapshotX + snapshotWidth)) &&
					(mouseY >= snapshotY) &&
					(mouseY <= (snapshotY + snapshotHeight))) {
				snapshotX = scene.zoomConvertCameraX(
						(int)(((double)(mouseX - snapshotX) /
								(double)snapshotWidth) * (double)windowWidth));
				snapshotY = scene.zoomConvertCameraY(
						(int)(((double)(mouseY - snapshotY) /
								(double)snapshotHeight) *
							(double)windowHeight));
				camera.setCameraCoords(
						(snapshotX - ((windowWidth / 2) -
									  scene.getSpriteZoomWidth())),
						(snapshotY - ((windowHeight / 2) -
									  scene.getSpriteZoomHeight())));
			}
		}
	}

	public synchronized void handleKeyboardEvents(KeyEvent event)
	{
		Player player = null;
		Sprite sprite = null;

		if((!isGameLoaded) || (!internalGameLoaded)) {
			return;
		}

		if(dialogue == DIALOGUE_START) {
			if(keyboardEventType == KeyboardDriver.EVENT_KEY_PRESSED) {
				playerNameBox.keyPressed(event);
			}
			return;
		}
		else if(dialogue == DIALOGUE_CAMERA) {
			return; // ignore keyboard input
		}
		else if(dialogue == DIALOGUE_TRIGGER) {
			if((keyboardEventType == KeyboardDriver.EVENT_KEY_RELEASED) &&
					(lastBinaryKeyPress == 10)) { // enter key
				if((WAIT_FOR_SOUND_COMPLETION) &&
						(!playBattle.isPlaying())) {
					return;
				}
				dialogue = DIALOGUE_PLAYING;
				triggerContext = null;
				selectedPlanet.beginPlayerTurn(); // allows auto-select
				sprite = scene.getSprite(selectedPlanet.getName());
				sprite.setHasOutline(false);
				isLaunchMode = false;
				selectShipsMode = false;
				panCameraDiffX = 0;
				panCameraDiffY = 0;
				selectedPlanet = null;
				selectedPlanetAlternate = null;
				scene.resetSelections();
				scene.resetOutlineColor(Color.blue);
				setCursor(cursor);
			}
			return;
		}
		else if(dialogue == DIALOGUE_OVER) {
			/*
			 * TODO: this
			 */
			return;
		}

		if(keyboardEventType == KeyboardDriver.EVENT_KEY_PRESSED) {
			/*
			 * TODO: this
			 */
		}
		else if(keyboardEventType == KeyboardDriver.EVENT_KEY_RELEASED) {
			if((!RELEASE_BUILD) && (event.getKeyChar() == 'D')) {
				if(debugMode) {
					debugMode = false;
				}
				else {
					debugMode = true;
				}
			}
			if((!RELEASE_BUILD) && (event.getKeyChar() == 'Q')) {
				credits.reset();
				dialogue = DIALOGUE_OVER;
			}
			if(lastBinaryKeyPress == 10) { // enter key
				if(isTurnOver) {
					if(selectedPlanet != null) {
						sprite = scene.getSprite(selectedPlanet.getName());
						sprite.setHasOutline(false);
					}
					game.processAIs(scrollDisplay);
					game.processTravel(scrollDisplay);
					game.updatePlayerState(scrollDisplay);

					// see if the game is over

					player = game.getPlayer(playerName);
					if((!player.isAlive()) || (game.allAisDead())) {
						credits.reset();
						dialogue = DIALOGUE_OVER;
					}

					game.processPlanets();
					turnCounter++;
					isTurnOver = false;
				}
			}
			else if(lastBinaryKeyPress == 27) { // escape key
				if(selectedPlanet != null) {
					sprite = scene.getSprite(selectedPlanet.getName());
					sprite.setHasOutline(false);
				}
				isLaunchMode = false;
				selectShipsMode = false;
				panCameraDiffX = 0;
				panCameraDiffY = 0;
				selectedPlanet = null;
				selectedPlanetAlternate = null;
				scene.resetOutlineColor(Color.blue);
			}
		}
	}

	// game logic

	public synchronized void loadGame()
	{
		int localWidth = 0;
		int localHeight = 0;

		Texture texture = null;

		dialogue = DIALOGUE_INIT;

		assetCache.loadHttpDriver(HOSTNAME, PORT);

		if(getLogo() == null) {
			reloadLogo();
		}

		texture = (Texture)assetCache.getAsset(AssetCache.TYPE_TEXTURE,
				"assets/textures/galaxik/loadingImage.png");
		localWidth = ((windowWidth / 8) * 6);
		localHeight = ((windowHeight / 8) * 6);
		texture.buildMipMap(localWidth, localHeight);
		loadingImage = texture.getMipMap(localWidth, localHeight);

		gameEngineLogo = (Image)assetCache.getAsset(AssetCache.TYPE_IMAGE,
				"assets/textures/lyra/logo32x48.png");

		progressBar = new ProgressBar("ProgressBarWidget",
				((windowWidth / 10) * 2), ((windowHeight / 8) * 6),
				((windowWidth / 10) * 6), 24,
				this);
		progressBar.setTotalTicks(1330);
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
		progressBar.start();

		gameLoader = new GameLoader(this);

		isGameLoaded = true;
	}

	public synchronized void processGameplay()
	{
		int counter = 0;
		int iterations = 0;

		Iterator iterator = null;
		Sprite sprite = null;
		Sprite selectedSprite = null;
		Planet planet = null;
		Player player = null;
		TriggerAction action = null;
		BattleTriggerConsumer consumer = null;

		if((!isGameLoaded) || (!internalGameLoaded)) {
			return;
		}

		if(dialogue == DIALOGUE_START) {
			if(startMode == START_MODE_DISPLAY_PROGRESS) {
				startTicks++;
				if(startTicks < 32) {
					return;
				}

				playerName = playerNameBox.getContents();
				if(mapSizeBox.getSelection().equals("Small")) {
					sceneWidth = 800;
					sceneHeight = 600;
				}
				else if(mapSizeBox.getSelection().equals("Medium")) {
					sceneWidth = 1024;
					sceneHeight = 768;
				}
				else if(mapSizeBox.getSelection().equals("Large")) {
					sceneWidth = 1536;
					sceneHeight = 1152;
				}
				else if(mapSizeBox.getSelection().equals("Huge")) {
					sceneWidth = 2048;
					sceneHeight = 1536;
				}

				initGame();

				constructSprites();

				if(difficultyBox.getSelection().equals("Easy")) {
					game.setDifficultyLevel(Game.DIFFICULTY_EASY);
				}
				else if(difficultyBox.getSelection().equals("Normal")) {
					game.setDifficultyLevel(Game.DIFFICULTY_NORMAL);
				}
				else if(difficultyBox.getSelection().equals("Hard")) {
					game.setDifficultyLevel(Game.DIFFICULTY_HARD);
				}
				else if(difficultyBox.getSelection().equals("Insane")) {
					game.setDifficultyLevel(Game.DIFFICULTY_INSANE);
				}

				if(aiNumberBox.getSelection().equals("2")) {
					initAI(2);
				}
				else if(aiNumberBox.getSelection().equals("3")) {
					initAI(3);
				}
				else if(aiNumberBox.getSelection().equals("4")) {
					initAI(4);
				}
				else if(aiNumberBox.getSelection().equals("5")) {
					initAI(5);
				}
				else if(aiNumberBox.getSelection().equals("6")) {
					initAI(6);
				}
				else if(aiNumberBox.getSelection().equals("7")) {
					initAI(7);
				}
				else if(aiNumberBox.getSelection().equals("8")) {
					initAI(8);
				}

				setCursor(cursor);

				dialogue = DIALOGUE_PLAYING;
				if(!USE_BACKGROUND) {
					setBackground(Color.black);
				}
			}
			return;
		}
		else if(dialogue == DIALOGUE_OVER) {
			if((getTicks() % 8) == 0) {
				credits.process();
			}
			if(!credits.isRunning()) {
				isTurnOver = false;
				isLaunchMode = false;
				selectShipsMode = false;
				startTicks = 0;
				playerId = 0;
				sceneWidth = 0;
				sceneHeight = 0;
				turnCounter = 1;
				dCameraX = 0;
				dCameraY = 0;
				dCameraDestX = 0;
				dCameraDestY = 0;
				panCameraDiffX = 0;
				panCameraDiffY = 0;
				dialogue = DIALOGUE_START;
				startMode = START_MODE_DIALOGUE;
				if(USE_DIALOGUE_IMAGE) {
					dialogueImage = (Image)assetCache.getAsset(
							AssetCache.TYPE_IMAGE,
							"assets/textures/galaxik/startImage.png");
				}
			}
			return;
		}
		else if((dialogue == DIALOGUE_CAMERA) ||
				(dialogue == DIALOGUE_TRIGGER)) {
			if(getCursor() != busyCursor) {
				setCursor(busyCursor);
			}

			if((dCameraX == dCameraDestX) && (dCameraY == dCameraDestY)) {
				if((selectedPlanet != null) &&
						(!selectedPlanet.wasSelected())) {
					scene.processGameplay(frameRate.get());
					sprite = scene.getSprite(selectedPlanet.getName());
					sprite.setIsSelected(true);
					selectedPlanet.setSelected();
					if(dialogue == DIALOGUE_CAMERA) {
						sprite.setOutlineColor(Color.blue);
						playDing.play();
					}
					else {
						sprite.setOutlineColor(Color.red);
						if(triggerAudio == 0) {
							playBattle.play();
						}
						else if(triggerAudio ==  1) {
							playLostPlanet.play();
						}
						else if(triggerAudio == 2) {
							playPlanetCaptured.play();
						}
					}
				}
				if(dialogue == DIALOGUE_CAMERA) {
					dialogue = DIALOGUE_PLAYING;
					setCursor(cursor);
				}
				return;
			}

			// adjust camera position

			counter = 0;
			iterations = (windowWidth / 160);
			if(dCameraX < dCameraDestX) {
				while((dCameraX < dCameraDestX) && (counter < iterations)) {
					dCameraX++;
					counter++;
				}
			}
			else if(dCameraX > dCameraDestX) {
				while((dCameraX > dCameraDestX) && (counter < iterations)) {
					dCameraX--;
					counter++;
				}
			}
			counter = 0;
			iterations = (windowHeight / 120);
			if(dCameraY < dCameraDestY) {
				while((dCameraY < dCameraDestY) && (counter < iterations)) {
					dCameraY++;
					counter++;
				}
			}
			else if(dCameraY > dCameraDestY) {
				while((dCameraY > dCameraDestY) && (counter < iterations)) {
					dCameraY--;
					counter++;
				}
			}

			camera.setCameraCoords(dCameraX, dCameraY);
			scene.processGameplay(frameRate.get());

			return;
		}

		scene.processGameplay(frameRate.get());

		if(scene.wasZoomed()) {
			if(selectedPlanet != null) {
				sprite = scene.getSprite(selectedPlanet.getName());
				sprite.setHasOutline(false);
			}
			isLaunchMode = false;
			selectShipsMode = false;
			panCameraDiffX = 0;
			panCameraDiffY = 0;
			selectedPlanet = null;
			selectedPlanetAlternate = null;
			scene.resetOutlineColor(Color.blue);
		}

		if((isLaunchMode) && (selectShipsMode)) {
			if((getTicks() % 8) == 0) {
				shipNumberBox.process();
			}
		}

		// sequence triggered events

		triggerSystem.processRecordedEvents();
		action = triggerSystem.getTriggeredAction();
		if(action != null) {
			consumer = (BattleTriggerConsumer)action.getTriggeredConsumer();
			triggerContext = consumer.pop();
			if(triggerContext != null) {
//				System.out.println("...obtained triggered event from " + consumer.getName() + ": " + triggerContext.getPlanet().getName() + ", winner: " + triggerContext.getPlayerWin().getName() + ", looser: " + triggerContext.getPlayerLost());

				if(triggerContext.getPlayerWin().getName().equals(playerName)) {
					triggerAudio = 2;
				}
				else if((triggerContext.getPlayerLost() != null) &&
						(triggerContext.getPlayerLost().getName().equals(
								playerName))) {
					triggerAudio = 1;
				}
				else {
					triggerAudio = 0;
				}

				// reset play properties

				if(selectedPlanet != null) {
					sprite = scene.getSprite(selectedPlanet.getName());
					sprite.setHasOutline(false);
				}
				isLaunchMode = false;
				selectShipsMode = false;
				panCameraDiffX = 0;
				panCameraDiffY = 0;
				selectedPlanet = null;
				selectedPlanetAlternate = null;
				scene.resetOutlineColor(Color.blue);

				// pan camera to triggered planet

				selectedPlanet = triggerContext.getPlanet();
				dCameraX = camera.getCameraX();
				dCameraY = camera.getCameraY();
				dCameraDestX =
					((selectedPlanet.getX() * scene.getSpriteZoomWidth()) -
					 ((windowWidth / 2) - scene.getSpriteZoomWidth()));
				dCameraDestY =
					((selectedPlanet.getY() * scene.getSpriteZoomHeight()) -
					 ((windowHeight / 2) - scene.getSpriteZoomHeight()));
				dialogue = DIALOGUE_TRIGGER;
				return;
			}
		}

		// determine selected planet alternate

		selectedPlanetAlternate = null;
		if((isLaunchMode) && (selectedPlanet != null)) {
			selectedSprite = scene.getSprite(selectedPlanet.getName());
			iterator = scene.getSpriteSet().iterator();
			while(iterator.hasNext()) {
				sprite = (Sprite)iterator.next();
				if(sprite == null) {
					break;
				}

				if(!sprite.isVisible()) {
					continue;
				}

				if((sprite.hasOutline()) && (sprite != selectedSprite)) {
					planet = game.getPlanet(sprite.getName());
					if(planet != null) {
						selectedPlanetAlternate = planet;
					}
					break;
				}
			}

			// perform camera panning

			if(selectedPlanetAlternate == null) {
				camera.setCameraCoords((camera.getCameraX() + panCameraDiffX),
						(camera.getCameraY() + panCameraDiffY));
			}

			return;
		}

		// determine selected planet

		selectedPlanet = null;
		iterator = scene.getSpriteSet().iterator();
		while(iterator.hasNext()) {
			sprite = (Sprite)iterator.next();
			if(sprite == null) {
				break;
			}

			if(!sprite.isVisible()) {
				continue;
			}

			if(sprite.hasOutline()) {
				planet = game.getPlanet(sprite.getName());
				if(planet != null) {
					selectedPlanet = planet;
				}
				break;
			}
		}

		// sequence player-owned planets

		if((selectedPlanet == null) && (!isTurnOver)) {
			player = game.getPlayer(playerName);
			iterator = scene.getSpriteSet().iterator();
			while(iterator.hasNext()) {
				sprite = (Sprite)iterator.next();
				if(sprite == null) {
					break;
				}

				planet = game.getPlanet(sprite.getName());

				if((planet.getOwnerId() == player.getId()) &&
						(!planet.turnOver())) {
					if(planet.autoUpgrade()) {
						game.upgradePlayerPlanet(planet.getName(),
								(player.getCredits() /
								 (player.getPlanetsConquered() -
								  player.getPlanetsLost())));
					}
					if(planet.autoBuild()) {
						planet.setSelected();
						game.processPlayerPlanet(planet.getName(), false,
								player.getCredits());
						continue;
					}
				}

				if((planet.getOwnerId() == player.getId()) &&
						(!planet.wasSelected()) && (!planet.turnOver())) {
					selectedPlanet = planet;
/*					camera.setCameraCoords(
							((planet.getX() * scene.getSpriteZoomWidth()) -
							 ((windowWidth / 2) - scene.getSpriteZoomWidth())),
							((planet.getY() * scene.getSpriteZoomHeight()) -
							 ((windowHeight / 2) -
							  scene.getSpriteZoomHeight())));*/
					dCameraX = camera.getCameraX();
					dCameraY = camera.getCameraY();
					dCameraDestX =
						((planet.getX() * scene.getSpriteZoomWidth()) -
						 ((windowWidth / 2) - scene.getSpriteZoomWidth()));
					dCameraDestY =
						((planet.getY() * scene.getSpriteZoomHeight()) -
						 ((windowHeight / 2) - scene.getSpriteZoomHeight()));
					dialogue = DIALOGUE_CAMERA;
					break;
				}
			}

			if(selectedPlanet == null) {
				isTurnOver = true;
			}
		}

		if(selectedPlanet != null) {
			autoBuildBox.setIsChecked(planet.autoBuild());
			autoUpgradeBox.setIsChecked(planet.autoUpgrade());
		}
	}

	public TriggerSystem getTriggerSystem()
	{
		return triggerSystem;
	}
}

