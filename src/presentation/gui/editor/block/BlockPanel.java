package presentation.gui.editor.block;

import presentation.controllers.TileController;
import presentation.exceptions.UnhandledBlockDataException;
import presentation.exceptions.UnhandledBlockIdException;
import presentation.gui.editor.Editor;
import presentation.gui.editor.EditorSubComponent;
import presentation.main.Cord3S;
import presentation.objects.Block;
import presentation.objects.ViewData;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

/**
 * A panel that will draw the blocks of a world, used by Editor
 */
public class BlockPanel extends EditorSubComponent {

    private BufferedImage[] layerBuffer;
    private final TileController tileController;

    public BlockPanel(Editor editor) {
        super(editor);

        tileController = worldController.getMainController().getTileController();

        int maxLayer = 0;
        ViewData worldData = editor.getWorldController().getWorldData();
        switch (orientation) {
            case TOP:
                maxLayer = worldData.getYSize();
                break;

            case FRONT:
                maxLayer = worldData.getZSize();
                break;

            case RIGHT:
                maxLayer = worldData.getXSize();
        }

        layerBuffer = new BufferedImage[maxLayer];
        setOpaque(true);
    }

    private BufferedImage paintLayer(int layerHeight) {
        BufferedImage bi = new BufferedImage(pixelWidth, pixelHeight, BufferedImage.TYPE_3BYTE_BGR);

        Graphics2D g = bi.createGraphics();
        g.setBackground(Color.GRAY);

        for (short y = 0; y < this.editorHeight; y++)
            for (short x = 0; x < this.editorWidth; x++) {
                Cord3S cords = getCord3D(x, y);

                BufferedImage image = getTile(cords.x, cords.y, cords.z);
                g.drawImage(image, x * Editor.SIZE + 1, y * Editor.SIZE + 1, null);
            }

        layerBuffer[layerHeight] = bi;

        return bi;
    }

    /**
     * Paints all blocks
     */
    @Override
    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr);

        int layerHeight = editor.getLayerHeight();

        BufferedImage bi = layerBuffer[layerHeight];

        if (bi == null)
            bi = paintLayer(layerHeight);

        Graphics2D g = (Graphics2D) gr;

        g.drawImage(bi, 0, 0, null);
    }

    public void clearBuffer() {
        Arrays.fill(layerBuffer, null);
    }

    /**
     * Returns the tile image for the given coordinates
     * @param x
     * @param y
     * @param z
     * @return The BufferedImage of size Editor.SIZE x Editor.SIZE
     * @throws UnhandledBlockDataException
     * @throws UnhandledBlockIdException
     */
    private BufferedImage getTile(short x, short y, short z) {

        BufferedImage tileImage;
        ViewData worldData = worldController.getWorldData();
        char b = worldData.getBlock(x, y, z);
        char behindBlock = Block.BLOCK_AIR;

        switch (orientation) {
            case TOP:
                behindBlock = worldData.getBlock(x, y - 1, z);
                break;

            case FRONT:
                behindBlock = worldData.getBlock(x, y, z + 1);
                break;

            case RIGHT:
                behindBlock = worldData.getBlock(x + 1, y, z);
        }

        byte blockId = Block.getId(b);

        if (blockId == 0 && !Block.isTransparentBlock(Block.getId(behindBlock)))
            return tileController.getTile("solid-below");

        switch (blockId) {
            case Block.BLOCK_WIRE:
                tileImage = getRedstoneWire(b, x, y, z);
                return tileImage;

            case Block.BLOCK_CHEST:
                tileImage = getChest(b, x, y, z);
                return tileImage;
        }

        tileImage = tileController.getTile(blockId, Block.getData(b), orientation);

        return tileImage;
    }

    /**
     * A customized drawing method for redstone wire, otherwise it'd take up a ton of images
     * @param b The block object
     * @param x
     * @param y
     * @param z
     * @return The BufferedImage of size Editor.SIZE x Editor.SIZE
     */
    private BufferedImage getRedstoneWire(char b, short x, short y, short z) {
        byte powerLevel = Block.getData(b);

        ViewData viewData = worldController.getWorldData();
        BufferedImage bi = null;
        boolean[] cons = new boolean[4];

        final short[] XCORDS = {x, (short) (x - 1), x, (short) (x + 1)};
        final short[] ZCORDS = {(short) (z - 1), z, (short) (z + 1), z};

        final boolean up = Block.isTransparentBlock(Block.getId(viewData.getBlock(x, (short) (y + 1), z)));

        char testBlock;
        for (byte i = 0; i < 4; i++) {
            testBlock = viewData.getBlock(XCORDS[i], y, ZCORDS[i]);
            // anything on same level
            cons[i] = (Block.isConnectable(testBlock, i % 2 == 0))
                    // wire 1 block lower
                    || (Block.isTransparentBlock(Block.getId(testBlock)) && Block.getId(viewData.getBlock(XCORDS[i], (short) (y - 1), ZCORDS[i])) == Block.BLOCK_WIRE)
                    // wire 1 block higher
                    || (up && Block.getId(viewData.getBlock(XCORDS[i], (short) (y + 1), ZCORDS[i])) == Block.BLOCK_WIRE);
        }

        bi = RedstoneWire.draw(powerLevel, this.orientation, cons);

        return bi;
    }

    // TODO
    private BufferedImage getChest(char b, short x, short y, short z) {
        return null;
    }
}
