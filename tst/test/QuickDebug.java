package test;

import logging.Log;
import org.junit.Before;
import org.junit.Test;
import presentation.controllers.SimController;
import sim.constants.Constants;
import sim.constants.Globals;
import sim.logic.SimWorld;
import sim.logic.Simulator;
import utils.Tag;

import java.io.File;
import java.io.FileInputStream;

import static org.junit.Assert.fail;

public class QuickDebug {

    private static final int TICKS = 10;
    private Simulator simulator;

    @Before
    public void setUp() throws Exception {

        Log.setTest(true);
        simulator = new Simulator(Constants.MCPCONFFOLDER, Globals.getMinecraftFolder());
    }

    @Test
    public void testLoadNTick() {

        StringBuilder sb = new StringBuilder("DEBUG SHIT RESULTS:");
        boolean hasFailed = false;

        for (File file : new File("schems/").listFiles()) {

            if (file.isDirectory())
                continue;

            System.out.println(file.getName());

            String result = openFileAndRunNTicks(file, TICKS);
            sb.append(file.getName()).append('\t');
            if (result == null)
                sb.append("SUCCESS");
            else {
                hasFailed = true;
                sb.append(result);
            }
            sb.append('\n');
        }

        if (hasFailed)
            fail("One or more schematics failed to load 'n tick:\n" + sb);
        else
            System.out.println(sb);
    }

    private String openFileAndRunNTicks(File schematic, int n) {

        try {
            SimWorld world = simulator.createWorld();
            world.createInstance();
            SimController controller = new SimController(world);

            Tag schematicTag = Tag.readFrom(new FileInputStream(schematic));
            controller.setSchematic(schematicTag);

            if (schematic.getName().contains("mort"))
                System.out.println("YO");

            for (; n >= 0; n--)
                controller.tick();

            return null;

        } catch (Exception e) {

            return "FAILED after " + (TICKS - n - 1) + " ticks: " + e.getMessage();
        }
    }
}
