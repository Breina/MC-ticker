package presentation.controllers;

import logging.Log;
import presentation.DesktopPane;
import presentation.RSFrame;
import presentation.StatusPanel;
import presentation.exceptions.SchematicException;
import presentation.gui.menu.FileMenu;
import presentation.gui.menu.WindowMenu;
import presentation.gui.windows.main.*;
import presentation.main.Cord3S;
import presentation.objects.Block;
import presentation.tools.Tool;
import sim.constants.Globals;
import sim.logic.SimWorld;
import sim.logic.Simulator;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MainController {
	
	private DesktopPane desktopPane;
	private WindowMenu windowMenu;
	private FileMenu fileMenu;
	private LogWindow logWindow;
	private BlockWindow blockWindow;
	private ToolWindow toolWindow;
	private RSFrame mainframe;
	private ExportWindow exportWindow;
	private StatusPanel statusPanel;
	private NewWorldWindow newWorldWindow;
	
	private TileController tileController;
	private BlockController blockController;
	private List<WorldController> worldControllers;
	
	private WorldController selectionController;
	private Cord3S selectionCord;
	
	private Tool tool;
	
	private Block block;
	
	private Simulator simulator;
	
	public MainController() {
		
		setLF();
		
		worldControllers = new ArrayList<WorldController>();
		
		tileController = new TileController(new File(presentation.main.Constants.TILEMAPSFILE));
		blockController = new BlockController(new File(presentation.main.Constants.BLOCKSFILE));

		// TODO The most emberassing part of the sim, fix this man :(
		desktopPane = new DesktopPane();
		statusPanel = new StatusPanel();
		fileMenu = new FileMenu(this);
		windowMenu = new WindowMenu(this);
		logWindow = new LogWindow(this);
		blockWindow = new BlockWindow(this);
		toolWindow = new ToolWindow(this);
		mainframe = new RSFrame(this);
		exportWindow = new ExportWindow(this);
		newWorldWindow = new NewWorldWindow(this);
		
		setupSim();
	}
	
	private void setLF() {
		try {
//			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
//		        if ("Nimbus".equals(info.getName())) {
//		            UIManager.setLookAndFeel(info.getClassName());
//		            return;
//		        }
//		    }
			
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			Log.e("ERROR: Could not set look and feel to system default, continuing in swing default.");
		}
	}
	
	/**
	 * Obtains the files.
	 */
	private void setupSim() {

		String mcpFolder = sim.constants.Constants.MCPCONFFOLDER;
		
		// Dumb windows attempt
		String minecraftFolder = Globals.getMinecraftFolder();
		
		try {
			simulator = new Simulator(mcpFolder, minecraftFolder);
			
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | InstantiationException
				| IllegalAccessException | NoSuchMethodException | IllegalArgumentException | InvocationTargetException
				| IOException e) {
			
			Log.e("Could not initlialize the Simulator" + SimController.analyseException(e));
		}
	}
	
	
	public void openNewWorldDialog() {

		newWorldWindow.setVisible(true);
	}
	
	public void openSchematic(File schematicFile) {
		
		try {
			
			SimWorld simWorld = simulator.createWorld();
			simWorld.createInstance();
			
			WorldController worldController = new WorldController(this, simWorld, schematicFile);
			worldControllers.add(worldController);
			
			onWorldAdded(worldController);
			
		} catch (SchematicException | IOException | NoSuchAlgorithmException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			Log.e("Failed to load schematic: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void createNewWorld(String name, short xSize, short ySize, short zSize, int worldTypeId, String worldType, String gameType, long seed, int worldProvider, boolean hardcoreEnabled, int difficulty) {

		try {
					Log.i("Creating new world     " + name);
					Log.i("Size (x, y, z)         (" + xSize + ", " + ySize + ", " + zSize + ")");

			switch (worldTypeId) {
				case 0:
				case 1:
				case 2:
				case 3:
				case 8:
					Log.i("WorldType              id=" + worldTypeId + ", name=" + worldType);
					break;

				default:
					Log.w("Custom WordType:       id=" + worldTypeId + ", name=" + worldType);
			}

					Log.i("GameType               " + gameType);
					Log.i("Seed                   " + seed);

			switch (worldProvider) {
				case -1:
					Log.i("WorldProvider          Hell");
					break;
				case 0:
					Log.i("WorldProvider          Surface");
					break;
				case 1:
					Log.i("WorldProvider          End");
					break;
				default:
					Log.i("Custom WorldProvider   " + worldProvider);
			}

					Log.i("Hardcore               " + (hardcoreEnabled ? "Enabled" : "Disabled"));

			switch (difficulty) {
				case 0:
					Log.i("Difficulty             Peaceful");
					break;
				case 1:
					Log.i("Difficulty             Easy");
					break;
				case 2:
					Log.i("Difficulty             Normal");
					break;
				case 3:
					Log.i("Difficulty             Hard");
					break;
				default:
					Log.e("Custom Difficulty      " + difficulty);
			}

			SimWorld simWorld = simulator.createWorld();
			simWorld.createInstance(worldTypeId, worldType, gameType, seed, worldProvider, hardcoreEnabled, difficulty, false, false);

			WorldController worldController = new WorldController(this, simWorld, name, xSize, ySize, zSize);
			worldControllers.add(worldController);

			onWorldAdded(worldController);

		} catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {

			Log.e("Failed to create world: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void onWorldAdded(WorldController worldController) {
		exportWindow.onWorldAdded(worldController);
	}
	
	public void onWorldRemoved(WorldController worldController) {
		exportWindow.onWorldRemoved(worldController);
	}
	
	public void onSelectionUpdated(WorldController source, Cord3S cord, boolean dragTools) {
		selectionController = source;
		selectionCord = cord;
		
		statusPanel.updateSelection(source, cord);

		if (dragTools)
			tool.onSelectionChanged();
	}
	
	public void saveAll() {

		for (WorldController worldController : worldControllers)
			worldController.save();
	}
	
	public void export() {
		exportWindow.setVisible(true);
	}
	
	public void exit() {
		
		System.exit(0);		
	}

	public DesktopPane getDesktop() {
		return desktopPane;
	}
	
	public FileMenu getFileMenu() {
		return fileMenu;
	}
	
	public WindowMenu getWindowMenu() {
		return windowMenu;
	}
	
	public BlockWindow getEditorWindow() {
		return blockWindow;
	}
	
	public RSFrame getRSframe() {
		return mainframe;
	}
	
	public StatusPanel getStatusPanel() {
		return statusPanel;
	}
	
	public List<WorldController> getWorldControllers() {
		return worldControllers;
	}
	
	public TileController getTileController() {
		return tileController;
	}
	
	public BlockController getBlockController() {
		return blockController;
	}
	
	public WorldController getSelectedWorld() {
		return selectionController;
	}
	
	public Cord3S getSelectedCord() {
		return selectionCord;
	}
	
	public void setTool(Tool tool) {
		this.tool = tool;
	}
	
	public Tool getTool() {
		return tool;
	}
	
	public void setBlock(Block block) {
		this.block = block;
	}
	
	public Block getBlock() {
		return block;
	}
}