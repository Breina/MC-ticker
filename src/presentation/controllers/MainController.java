package presentation.controllers;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import presentation.DesktopPane;
import presentation.RSFrame;
import presentation.StatusPanel;
import presentation.exceptions.SchematicException;
import presentation.gui.choosers.MinecraftFolderChooser;
import presentation.gui.menu.FileMenu;
import presentation.gui.menu.WindowMenu;
import presentation.gui.windows.main.BlockWindow;
import presentation.gui.windows.main.ExportWindow;
import presentation.gui.windows.main.LogWindow;
import presentation.gui.windows.main.ToolWindow;
import presentation.main.Cord3S;
import presentation.objects.Block;
import presentation.tools.Tool;
import logging.Log;
import sim.logic.Simulator;
import sim.logic.SimWorld;

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
		
		desktopPane = new DesktopPane();
		statusPanel = new StatusPanel();
		fileMenu = new FileMenu(this);
		windowMenu = new WindowMenu(this);
		logWindow = new LogWindow(this);
		blockWindow = new BlockWindow(this);
		toolWindow = new ToolWindow(this);
		mainframe = new RSFrame(this);
		exportWindow = new ExportWindow(this);
		
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
			
			if (result != MinecraftFolderChooser.APPROVE_OPTION)
				if (result == MinecraftFolderChooser.ERROR_OPTION)
					Log.e("Something went wrong when finding .minecraft folder.");
			
			minecraftFolder = minecraftDialog.getSelectedFile().getAbsolutePath();
		}
		
		try {
			simulator = new Simulator(mcpFolder, minecraftFolder);
			
		} catch (ClassNotFoundException | NoSuchFieldException | SecurityException | InstantiationException
				| IllegalAccessException | NoSuchMethodException | IllegalArgumentException | InvocationTargetException
				| IOException e) {
			
			Log.e("Could not initlialize the Simulator" + SimController.analyseException(e));
		}
	}
	
	
	public void newWorld() {
		// TODO unimplemented
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
		tool.onSelectionChanged();
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