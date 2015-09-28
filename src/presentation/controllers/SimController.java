package presentation.controllers;

import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import com.google.common.util.concurrent.UncheckedTimeoutException;
import logging.Log;
import presentation.objects.Entity;
import sim.logic.SimWorld;
import utils.Tag;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Largely responsible for catching errors and giving meaningful messages back
 * to the GUI. Fuck I hate this class.
 */
public class SimController {

    private static final int SUF = 5; // Seconds Until Failure

    private final SimWorld simWorld;

    private final TimeLimiter limiter;

    public SimController(SimWorld simWorld) {
        this.simWorld = simWorld;

        limiter = new SimpleTimeLimiter();
    }

    public void setSchematic(Tag schematic) {

        try {

            limiter.callWithTimeout(() -> {
                simWorld.setSchematic(schematic);
                return null;
            }, SUF, TimeUnit.SECONDS, false);

        } catch (Exception e) {

            Log.e("Failed to send schematic to the simulator " + analyseException(e));
        }
    }

    public Tag getSchematic() {

        try {

            return limiter.callWithTimeout(() -> simWorld.getSchematic(), SUF, TimeUnit.SECONDS, false);

        } catch (Exception e) {

            Log.e("Could not get schematic from the simulator" + analyseException(e));
            return null;
        }
    }

    public void createNewWorld(int xSize, int ySize, int zSize) {

        try {

            limiter.callWithTimeout(() -> {
                simWorld.createEmptyWorld(xSize, ySize, zSize);
                return null;
            }, SUF, TimeUnit.SECONDS, false);

        } catch (Exception e) {

            Log.e("Could not create new world" + analyseException(e));
        }

    }

    public void saveWorld(OutputStream output) {

        try {

            limiter.callWithTimeout(() -> {
                simWorld.getSchematic(output);
                return null;
            }, SUF, TimeUnit.SECONDS, false);

        } catch (Exception e) {

            Log.e("Could not save world" + analyseException(e));
        }
    }

    public boolean tick() {

        try {

            return limiter.callWithTimeout(() -> simWorld.tickWorld(), SUF, TimeUnit.SECONDS, false);

        } catch (Exception e) {

            Log.e("Could not tick world" + analyseException(e));
            return false;
        }
    }

    public void setBlock(int x, int y, int z, byte blockId, byte blockData, boolean update) {

        try {

            limiter.callWithTimeout(() -> {
                simWorld.setBlock(x, y, z, blockId, blockData, update);
                return null;
            }, SUF, TimeUnit.SECONDS, false);

        } catch (Exception e) {

            Log.e("Could not set block" + analyseException(e));
        }
    }

    public void activateBlock(int x, int y, int z) {

        try {
            limiter.callWithTimeout(() -> {
                simWorld.onBlockActivated(x, y, z);
                return null;
            }, SUF, TimeUnit.SECONDS, false);

        } catch (Exception e) {

            Log.e("Could not activate block (" + x + ", " + y + ", " + z + "): " + analyseException(e));
        }
    }

    public void updateBlock(int x, int y, int z, boolean randomUpdate) {

        try {
            limiter.callWithTimeout(() -> {
                simWorld.updateBlock(x, y, z, randomUpdate);
                return null;
            }, SUF, TimeUnit.SECONDS, false);

        } catch (Exception e) {

            Log.e("Could not update block (" + x + ", " + y + ", " + z + "): " + analyseException(e));
        }
    }

    public void debug(int x, int y, int z) {

        try {
            limiter.callWithTimeout(() -> {
                simWorld.debug(x, y, z);
                return null;
            }, SUF, TimeUnit.SECONDS, false);

        } catch (Exception e) {

            Log.e("Could not debug block (" + x + ", " + y + ", " + z + "): " + analyseException(e));
        }
    }

    public char[][][] getBlocks() {

        try {
            return limiter.callWithTimeout(() -> simWorld.getBlocks(), SUF, TimeUnit.SECONDS, false);

        } catch (Exception e) {

            Log.e("Could not get blocks: " + analyseException(e));
            return null;
        }
    }

    public Object getBlockState(int x, int y, int z) {

        try {
            return limiter.callWithTimeout(() -> simWorld.getBlockState(x, y, z), SUF, TimeUnit.SECONDS, false);
        } catch (Exception e) {

            Log.e("Could not get block state" + analyseException(e));
            return null;
        }
    }

    public Object getBlockFromState(Object blockState) {

        try {
            return limiter.callWithTimeout(() -> simWorld.getBlockFromState(blockState), SUF, TimeUnit.SECONDS, false);
        } catch (Exception e) {

            Log.e("Could not get block from state" + analyseException(e));
            return null;
        }
    }

    public Entity[] getEntityObjects() {

        try {
            return limiter.callWithTimeout(() -> simWorld.getEntityObjects(), SUF, TimeUnit.SECONDS, false);

        } catch (Exception e) {

            Log.e("Could not create view data for entities" + analyseException(e));
            return null;
        }
    }

    public long getWorldTime() {

        try {
            return limiter.callWithTimeout(() -> simWorld.getWorldTime(), SUF, TimeUnit.SECONDS, false);
        } catch (Exception e) {

            Log.e("Could not get the world time: " + analyseException(e));
            return -1l;
        }
    }

    public boolean isFullCube(Object block) {

        try {
            return limiter.callWithTimeout(() -> simWorld.isFullCube(block), SUF, TimeUnit.SECONDS, false);

        } catch (Exception e) {

            Log.e("Could determine if block was a full cube" + analyseException(e));
            return false;
        }
    }

    public boolean isOpaque(Object block) {

        try {
            return limiter.callWithTimeout(() -> simWorld.isOpaque(block), SUF, TimeUnit.SECONDS, false);

        } catch (Exception e) {

            Log.e("Could determine if block was opaque" + analyseException(e));
            return false;
        }
    }


    public static String analyseException(Exception e) {

        String msg;

        if (e instanceof IllegalAccessException
                || e instanceof SecurityException
                || e instanceof InstantiationException
                || e instanceof ArrayIndexOutOfBoundsException
                || e instanceof NoSuchFieldException
                || e instanceof IllegalArgumentException
                || e instanceof NoSuchMethodException
                || e instanceof ClassNotFoundException)

            msg = "Internal reflection error, are we using the correct version? " + e.getCause();

        else if (e instanceof IOException)

            msg = "File exception: " + e.getMessage();

        else if (e instanceof InvocationTargetException) {

            Throwable cause = e.getCause();

            if (cause == null)
                throw new IllegalStateException("Got InvocationTargetException, but the cause is null.", e);

            else if (cause instanceof RuntimeException)
                throw (RuntimeException) cause;

            else if (cause instanceof Exception)
                msg = "Invocation failed with cause: " + cause;

            else
                msg = "Invocation failed with error: " + cause;

        } else if (e instanceof UncheckedTimeoutException || e instanceof TimeoutException) {

            msg = "A call took too long (> " + SUF + " sec): " + e.getStackTrace()[1].getMethodName();

        } else
            msg = "Error: " + e.getClass();

        e.printStackTrace();

        return ":\n" + msg;
    }
}
