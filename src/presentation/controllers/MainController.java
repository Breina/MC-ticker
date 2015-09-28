package presentation.controllers;

import logging.Log;
import presentation.debug.TracingEventQueue;
import presentation.gui.RSFrame;
import presentation.gui.WorldListener;
import presentation.gui.windows.main.ExportWindow;
import presentation.gui.windows.main.NewWorldWindow;
import presentation.gui.windows.main.options.OptionsController;
import presentation.gui.windows.main.options.OptionsWindow;
import presentation.main.Constants;
import presentation.main.Cord2S;
import presentation.main.Cord3S;
import presentation.objects.ViewData;
import presentation.tools.Tool;
import sim.constants.Globals;
import sim.logic.SimWorld;
import sim.logic.Simulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MainController {

    private final RSFrame mainframe;

	private final TileController tileController;
	private final BlockController blockController;
    private final OptionsController optionsController;
    private final List<WorldController> worldControllers;

    private WorldController activeWorldController;
    private Cord2S selectionCord2D;
	private Cord3S selectionCord3D;

	private final List<WorldListener> worldListeners;
	
	private Tool tool;
	
	private char block;
	
	private Simulator simulator;
	
	public MainController() {

		Log.i(Constants.MOTD);

        // This will debug actions that hog Swing's thread (EDT)
        if (sim.constants.Constants.DEBUG_SWING)
            Toolkit.getDefaultToolkit().getSystemEventQueue().push(new TracingEventQueue());

		setLF();
		
		worldControllers = new ArrayList<>();
		worldListeners = new ArrayList<>();
		
		tileController = new TileController(new File(presentation.main.Constants.TILEMAPSFILE));
		blockController = new BlockController(new File(presentation.main.Constants.BLOCKSFILE));
        optionsController = new OptionsController();

        mainframe = new RSFrame(this);

        setKeyboardShortcuts();

        if (!sim.constants.Constants.DEBUG_SKIP_LOADING)
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

    private void setKeyboardShortcuts() {
        //Hijack the keyboard manager
        KeyboardFocusManager manager =
                KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher( new KeyDispatcher() );
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
        new NewWorldWindow(mainframe, this);
	}
	
	public void openSchematic(File schematicFile) {
		
		try {
			
			SimWorld simWorld = simulator.createWorld();
			simWorld.createInstance();
			
			WorldController worldController = new WorldController(this, simWorld, schematicFile);
			worldControllers.add(worldController);
			
			onWorldAdded(worldController);
			
		} catch (IOException | NoSuchAlgorithmException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
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
			simWorld.createInstance(worldTypeId, worldType, gameType, seed, worldProvider, hardcoreEnabled);

			WorldController worldController = new WorldController(this, simWorld, name, xSize, ySize, zSize);
			worldControllers.add(worldController);

			onWorldAdded(worldController);

		} catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {

			Log.e("Failed to create world: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	void onWorldAdded(WorldController worldController) {

		for (WorldListener listener : worldListeners)
			listener.onWorldAdded(worldController);
	}
	
	public void onWorldRemoved(WorldController worldController) {

		for (WorldListener listener : worldListeners)
			listener.onWorldRemoved(worldController);

        worldControllers.remove(worldController);
	}

	public void addWorldListener(WorldListener worldListener) {
		worldListeners.add(worldListener);
	}

    public void removeWorldListener(WorldListener worldListener) {
        worldListeners.remove(worldListener);
    }
	
	public void onSelectionUpdated(WorldController source, Cord2S cord2D, Cord3S cord3D, boolean dragTools) {
		activeWorldController = source;

        selectionCord2D = cord2D;
		selectionCord3D = cord3D;
		
		mainframe.getStatusPanel().updateSelection(source, cord3D);

		if (dragTools)
			tool.onSelectionChanged();
	}
	
	public void saveAll() {

		for (WorldController worldController : worldControllers)
			worldController.save();
	}

    public void showOptions() {
        new OptionsWindow(this);
    }
	
	public void export() {

        if (worldControllers.size() == 0)
            return;

        addWorldListener(new ExportWindow(mainframe.getDesktop(), this));
	}
	
	public void exit() {
		
		System.exit(0);		
	}

	public RSFrame getFrame() {
		return mainframe;
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

    public OptionsController getOptionsController() {
        return optionsController;
    }

    public WorldController getSelectedWorld() {
		return activeWorldController;
	}

    public Cord2S getSelectedCord2D() {
        return selectionCord2D;
    }
	
	public Cord3S getSelectedCord3D() {
		return selectionCord3D;
	}

    /**
     * Sets the currently active tool
     * @param tool The tool to become active
     */
	public void setTool(Tool tool) {
        this.tool = tool;
	}

    /**
     * Gets the currently active tool
     * @return The selected tool
     */
    public Tool getTool() {
        return tool;
    }

    /**
     * Sets the currently selected blocks
     * @param block The selected block
     */
	public void setBlock(char block) {
		this.block = block;
	}
	
	public char getBlock() {
		return block;
	}

    //Custom dispatcher
    class KeyDispatcher implements KeyEventDispatcher {
        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {

            if (e.getID() == KeyEvent.KEY_PRESSED) {

                int keyCode = e.getKeyCode();

                if (keyCode >= KeyEvent.VK_F1 && keyCode <= KeyEvent.VK_F5)
                    getFrame().getToolbar().selectButton(keyCode - KeyEvent.VK_F1);
            }

            //Allow the event to be redispatched
            return false;
        }
    }

    public void debug() {
        for (WorldController wc : worldControllers) {
            ViewData vd = wc.getWorldData();
            Log.d("WC: " + vd.getName() + ", " + vd.getBlock(0, 0, 0));
        }
    }
}