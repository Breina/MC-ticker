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

/**
 * A panel that will draw the blocks of a world, used by Editor
 */
public class BlockPanel extends EditorSubComponent {

    private final TileController tileController;

    public BlockPanel(Editor editor) {
        super(editor);

        tileController = worldController.getMainController().getTileController();

        setOpaque(true);
    }

    /**
     * Paints all blocks
     */
    @Override
    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr);

        Graphics2D g = (Graphics2D) gr;
        g.setBackground(Color.GRAY);
        g.clearRect(0, 0, editorWidth * Editor.SIZE + 1, editorHeight * Editor.SIZE + 1);

        for (short y = 0; y < this.editorHeight; y++)
            for (short x = 0; x < this.editorWidth; x++) {
                Cord3S cords = getCord3D(x, y);

                BufferedImage image = getTile(cords.x, cords.y, cords.z);
                g.drawImage(image, x * Editor.SIZE + 1, y * Editor.SIZE + 1, null);
            }
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
        Block b = worldController.getWorldData().getBlock(x, y, z);

        if (b == null)
            return null;

        switch (b.getId()) {
            case Block.BLOCK_WIRE:
                tileImage = getRedstoneWire(b, x, y, z);
                return tileImage;

            case Block.BLOCK_CHEST:
                tileImage = getChest(b, x, y, z);
                return tileImage;
        }

        tileImage = tileController.getTile(b.getId(), b.getData(), orientation);

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
    private BufferedImage getRedstoneWire(Block b, short x, short y, short z) {
        byte powerLevel = b.getData();

        ViewData viewData = worldController.getWorldData();
        BufferedImage bi = null;
        boolean[] cons = new boolean[4];

        final short[] XCORDS = {x, (short) (x - 1), x, (short) (x + 1)};
        final short[] ZCORDS = {(short) (z - 1), z, (short) (z + 1), z};

        final boolean up = viewData.getBlock(x, (short) (y + 1), z).isTransparentBlock();

        Block testBlock;
        for (byte i = 0; i < 4; i++) {
            testBlock = viewData.getBlock(XCORDS[i], y, ZCORDS[i]);
            // anything on same level
            cons[i] = (testBlock.isConnectable(i % 2 == 0))
                    // wire 1 block lower
                    || (testBlock.isTransparentBlock() && viewData.getBlock(XCORDS[i], (short) (y - 1), ZCORDS[i]).getId() == Block.BLOCK_WIRE)
                    // wire 1 block higher
                    || (up && viewData.getBlock(XCORDS[i], (short) (y + 1), ZCORDS[i]).getId() == Block.BLOCK_WIRE);
        }

        bi = RedstoneWire.draw(powerLevel, this.orientation, cons);

        return bi;
    }

    // TODO
    private BufferedImage getChest(Block b, short x, short y, short z) {
        return null;
    }
}
