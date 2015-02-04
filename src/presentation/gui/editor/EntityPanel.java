package presentation.gui.editor;

import presentation.main.Constants;
import presentation.objects.Entity;
import presentation.objects.Orientation;

import java.awt.*;

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
    }

    /**
     * Calculates the bounds where the entity should be
     * @return A rectangle containing the said bounds
     */
    @Override
    public Rectangle getBounds() {
        switch (orientation) {
            case TOP:
                return new Rectangle((int) (entity.getX() - entity.getWidth() / 2),
                        (int) (entity.getZ() - entity.getWidth() / 2),
                        (int) (entity.getWidth() + 1), (int) (entity.getWidth() + 1));

            case FRONT:
                return new Rectangle((int) (entity.getX() - entity.getWidth() / 2),
                        (int) (height * Editor.SIZE - entity.getY() - entity.getHeight()),
                        (int) (entity.getWidth() + 1), (int) (entity.getHeight() + 1));

            case RIGHT:
                return new Rectangle((int) (width * Editor.SIZE - entity.getZ() - entity.getWidth() / 2),
                        (int) (height * Editor.SIZE - entity.getY() - entity.getHeight()),
                        (int) (entity.getWidth() + 1), (int) (entity.getHeight()));

            case UNDEFINED:
            default:
                return null;
        }
    }

//    private void position() {
//
//        switch (orientation) {
//            case TOP:
//                setBounds((int) (entity.getX() - entity.getWidth() / 2),
//                        (int) (entity.getZ() - entity.getWidth() / 2),
//                        (int) (entity.getWidth() + 1), (int) (entity.getWidth() + 1));
//                break;
//
//            case FRONT:
//                setBounds((int) (entity.getX() - entity.getWidth() / 2),
//                        (int) (height * Editor.SIZE - entity.getY() - entity.getHeight()),
//                        (int) (entity.getWidth() + 1), (int) (entity.getHeight() + 1));
//                break;
//
//            case RIGHT:
//                setBounds((int) (width * Editor.SIZE - entity.getZ() - entity.getWidth() / 2),
//                        (int) (height * Editor.SIZE - entity.getY() - entity.getHeight()),
//                        (int) (entity.getWidth() + 1), (int) (entity.getHeight()));
//        }
//    }

    /**
     * Checks whether the entity is within the visible layer
     * @return True if it is visible
     */
    @Override
    public boolean isVisible() {

        short layer = editor.getLayerHeight();

        switch (orientation) {
            case TOP:
                return entity.getY() + entity.getHeight() >= layer &&
                        entity.getY() <= layer + 1;

            case FRONT:
                return entity.getZ() + entity.getWidth() / 2 >= layer &&
                        entity.getZ() - entity.getWidth() / 2 <= layer + 1;

            case RIGHT:
                return entity.getX() + entity.getWidth() / 2 >= layer &&
                        entity.getX() + entity.getWidth() / 2 <= layer + 1;

            case UNDEFINED:
            default:
                return false;
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
        g.drawRect(0, 0, (int) width, (int) height);

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
