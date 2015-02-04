package presentation.gui.editor;

import logging.Log;
import presentation.main.Constants;
import presentation.objects.Entity;
import presentation.objects.Orientation;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * The entity panel used by Editor
 */
public class EntityPanel extends EditorSubComponent {

    /**
     * The entity object that is drawn
     */
    private Entity entity;

    public EntityPanel(Editor editor, Entity entity) {
        super(editor);

        setEntity(entity);
    }

    /**
     * Sets the entity to be drawn
     * @param entity
     */
    public void setEntity(Entity entity) {

        this.entity = entity;

        Log.i("Setting entity");

        position();
        checkVisibility();
    }

    /**
     * Positions the entity to the right bounds according to iets properties
     */
    private void position() {

        int scaledX = (int) (entity.getX() * Editor.SIZE);
        int scaledY = (int) (entity.getY() * Editor.SIZE);
        int scaledZ = (int) (entity.getZ() * Editor.SIZE);
        float scaledWidth = entity.getWidth() * Editor.SIZE;
        int scaledHeight = (int) (entity.getHeight() * Editor.SIZE);

        switch (orientation) {
            case TOP:
                setBounds((int) (scaledX - scaledWidth / 2),
                        (int) (scaledZ - scaledWidth / 2),
                        (int) (scaledWidth + 1), (int) (scaledWidth + 1));
                break;

            case FRONT:
                setBounds((int) (scaledX - scaledWidth / 2),
                        height - scaledY - scaledHeight,
                        (int) (scaledWidth + 1), (int) (scaledHeight + 1));
                break;

            case RIGHT:
                setBounds((int) (width - scaledZ - scaledWidth / 2),
                        height - scaledY - scaledHeight,
                        (int) (scaledWidth + 1), (int) (scaledHeight));
        }
    }

    /**
     * Checks whether the entity is within the visible layer
     * @return True if it is visible
     */
    public void checkVisibility() {

        short layer = editor.getLayerHeight();

        switch (orientation) {
            case TOP:
                setVisible(entity.getY() + entity.getHeight() >= layer &&
                        entity.getY() <= layer + 1);
                break;

            case FRONT:
                setVisible(entity.getZ() + entity.getWidth() / 2 >= layer &&
                        entity.getZ() - entity.getWidth() / 2 <= layer + 1);
                break;

            case RIGHT:
                setVisible(entity.getX() + entity.getWidth() / 2 >= layer &&
                        entity.getX() + entity.getWidth() / 2 <= layer + 1);
        }
    }

    @Override
    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr);

        Graphics2D g = (Graphics2D) gr;

        // Box
        float width = entity.getWidth() * EditorPanel.SIZE;
        float height;

        if (orientation == Orientation.TOP)
            height = width;
        else
            height = entity.getHeight() * EditorPanel.SIZE;

        g.setColor(Constants.COLORENTITY);
        g.draw(new Rectangle2D.Float(0.5f, 0.5f, width, height));

        if (entity.isDead()) {
            g.setColor(Color.BLACK);
            g.drawLine(0, 0, (int) width, (int) height);
            g.drawLine(0, (int) height, (int) width, 0);
        }

        // Vector
        g.setColor(Constants.COLORENTITYVECTOR);

        float x = 0.5f;
        float y = 0.5f;

        switch (orientation) {
            case TOP:
                if (entity.getvX() != 0)
                    x += entity.getvX() * Constants.ENTITYVELOCITYMULTIPLIER;

                if (entity.getvZ() != 0)
                    y += entity.getvZ() * Constants.ENTITYVELOCITYMULTIPLIER;

                break;

            case FRONT:
                if (entity.getvX() != 0)
                    x += entity.getvX() * Constants.ENTITYVELOCITYMULTIPLIER;

                if (entity.getvY() != 0)
                    y += entity.getvY() * Constants.ENTITYVELOCITYMULTIPLIER;

                break;

            case RIGHT:
                if (entity.getvZ() != 0)
                    x -= entity.getvZ() * Constants.ENTITYVELOCITYMULTIPLIER;

                if (entity.getvY() != 0)
                    y += entity.getvY() * Constants.ENTITYVELOCITYMULTIPLIER;
        }

        if (x != 0.5f | y != 0.5f)
            g.drawLine(	(int) (width / 2), (int) (height / 2),
                    (int) (x * width), (int) (y * height));
    }
}
