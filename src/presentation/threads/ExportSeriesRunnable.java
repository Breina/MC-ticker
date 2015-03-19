package presentation.threads;

import logging.Log;
import presentation.controllers.TimeController;
import presentation.controllers.WorldController;
import presentation.gui.editor.Editor;
import presentation.gui.windows.main.ExportWindow;
import presentation.main.GifSequenceWriter;
import presentation.objects.Orientation;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ExportSeriesRunnable implements Runnable {

    private final TimeController timeController;

    private final int min, max, gifDelay;
    private final boolean isSlices;
    private boolean isGif;
    private final Editor editor;
    private final ExportWindow exportWindow;

    private String basePath;

    public ExportSeriesRunnable(ExportWindow exportWindow, File outputFolder, WorldController worldController, Editor editor, Orientation orientation, boolean isSlices, int constant, int min, int max) {
        this(exportWindow, outputFolder, worldController, editor, orientation, isSlices, constant, min, max, 0);

        isGif = false;
        basePath += "_";
    }

    public ExportSeriesRunnable(ExportWindow exportWindow, File outputFolder, WorldController worldController, Editor editor, Orientation orientation, boolean isSlices, int constant, int min, int max, int gifDelay) {
        this.exportWindow = exportWindow;

        this.timeController = worldController.getTimeController();

        this.editor = editor;

        this.isSlices = isSlices;
        this.min = min;
        this.max = max;
        this.gifDelay = gifDelay;

        if (isSlices)
            timeController.gotoTickCount(constant);
        else
            editor.setLayerHeight((short) constant);

        isGif = true;
        basePath = outputFolder.getAbsolutePath() + File.separator + worldController.getWorldData().getName();

        switch (orientation) {
            case TOP:
                basePath += "_top";
                break;

            case FRONT:
                basePath += "_front";
                break;

            case RIGHT:
                basePath += "_right";
        }
    }

    @Override
    public void run() {

        try {
            // For GIF
            GifSequenceWriter writer = null;
            ImageOutputStream gifOutput = null;
            File gifFile = null;

            if (isGif) {
                gifFile = new File(basePath + ".gif");
                gifOutput = new FileImageOutputStream(gifFile);
                writer = new GifSequenceWriter(gifOutput, BufferedImage.TYPE_INT_RGB, gifDelay, true);
            }

            // Series
            int seriesIndex = 0;

            // Go!
            for (int index = min; index <= max; index++) {

                if (isSlices)
                    editor.setLayerHeight((short) index);
                else
                    timeController.gotoTickCount(index);

                BufferedImage buffer = editor.getImage();
//                Graphics2D g = (Graphics2D) buffer.getGraphics();
//                g.setColor(Color.BLACK);
//                g.drawString(String.valueOf(index), 0, 10);

                if (isGif)
                    writer.writeToSequence(buffer);
                else
                    ImageIO.write(buffer, "png", new File(basePath + seriesIndex++ + ".png"));
            }

            if (isGif) {
                writer.close();
                gifOutput.close();
            }

        } catch (IOException e) {
            Log.e("File error: " + e.getMessage());
            e.printStackTrace();
        }

        exportWindow.exportDone();
    }
}
