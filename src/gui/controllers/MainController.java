package gui.controllers;

import gui.bettergui.BlockWindow;
import gui.bettergui.DesktopPane;
import gui.bettergui.ExportWindow;
import gui.bettergui.LogWindow;
import gui.bettergui.RSFrame;
import gui.bettergui.StatusPanel;
import gui.bettergui.choosers.MCPFolderChooser;
import gui.bettergui.choosers.MinecraftFolderChooser;
import gui.bettergui.menu.FileMenu;
import gui.bettergui.menu.WindowMenu;
import gui.exceptions.SchematicException;

import gui.main.Cord3S;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import logging.Log;
import sim.controller.Response;
import sim.controller.Sim;

public class MainController {
	
	private DesktopPane desktopPane;
	private WindowMenu windowMenu;
	private FileMenu fileMenu;
	private LogWindow logWindow;
	private BlockWindow blockWindow;
	private RSFrame mainframe;
	private ExportWindow exportWindow;
	private StatusPanel statusPanel;
	
	private TileController tileController;
	private BlockController blockController;
	private List<WorldController> worldControllers;
	
	private WorldController selectionController;
	private Cord3S selectionCord;
	
	public MainController() {
		
		setLF();
		
		worldControllers = new ArrayList<WorldController>();
		
		tileController = new TileController(new File(gui.main.Constants.TILEMAPSFILE));
		blockController = new BlockController(new File(gui.main.Constants.BLOCKSFILE));
		
		desktopPane = new DesktopPane();
		statusPanel = new StatusPanel();
		fileMenu = new FileMenu(this);
		windowMenu = new WindowMenu(this);
		logWindow = new LogWindow(this);
		blockWindow = new BlockWindow(this);
		mainframe = new RSFrame(this);
		exportWindow = new ExportWindow(this);
		
		setupSim();
	}
	
	private void setLF() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			Log.e("ERROR: Could not set look and feel to system default, continuing in swing default.");
		}
	}
	
	/**
	 * Obtains the files.
	 */
	private boolean setupSim() {

		String mcpFolder = sim.constants.Constants.MCPCONFFOLDER;
//		if (!(new File(mcpFolder).exists())) {
//			
//			MCPFolderChooser mcpDialog = new MCPFolderChooser(new File(Constants.MCPCONFFOLDER));
//			int result = mcpDialog.showDialog(mainframe, "Select folder");
//			
//			if (result != MCPFolderChooser.APPROVE_OPTION)  {
//				if (result == MCPFolderChooser.ERROR_OPTION)
//					Log.e("Something went wrong when finding MCP's folder.");
//				
//				return false;
//			}
//			
//			mcpFolder = mcpDialog.getSelectedFile().getPath();
//		}
		
		// Dumb windows attempt
		String minecraftFolder = System.getenv("APPDATA") + sim.constants.Constants.MINECRAFTFOLDER;
		
		if (!new File(minecraftFolder).exists())
			minecraftFolder = "minecraft";

		if (!new File(minecraftFolder).exists()) {
			
			MinecraftFolderChooser minecraftDialog = new MinecraftFolderChooser();
			int result = minecraftDialog.showDialog(mainframe, "Select");
			
			if (result != MinecraftFolderChooser.APPROVE_OPTION) {
				if (result == MinecraftFolderChooser.ERROR_OPTION)
					Log.e("Something went wrong when finding .minecraft folder.");
				
				return false;
			}
			
			minecraftFolder = minecraftDialog.getSelectedFile().getAbsolutePath();
		}
		
		Response response = Sim.getController().initialize(mcpFolder, minecraftFolder);

		if (response.getStatus() != Response.Type.SUCCESS) {
			
			Log.e(response.getMessage());
			return false;
		}
		
		return true;
	}
	
	
	public void newWorld() {
		// TODO unimplemented
	}
	
	public void openSchematic(File schematicFile) {
		
		try {
			
			WorldController worldController = new WorldController(this, schematicFile);
			worldControllers.add(worldController);
			
			onWorldAdded(worldController);
			
		} catch (SchematicException | IOException | NoSuchAlgorithmException e) {
			Log.e("Failed to load schematic: " + e.getMessage());
		}
	}
	
	public void onWorldAdded(WorldController worldController) {
		exportWindow.onWorldAdded(worldController);
	}
	
	public void onWorldRemoved(WorldController worldController) {
		exportWindow.onWorldRemoved(worldController);
	}
	
	public void onSelectionUpdated(WorldController source, Cord3S cord) {
		selectionController = source;
		selectionCord = cord;
		
		statusPanel.updateSelection(source.getWorldData().getName(), cord);
	}
	
	public void saveAll() {
		
		for (WorldController worldController : worldControllers)
			worldController.getWorldData().save();
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
}