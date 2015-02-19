package presentation.gui.editor.layer;

import presentation.gui.editor.Editor;
import presentation.gui.editor.EditorSubComponent;
import presentation.main.Constants;
import presentation.objects.Orientation;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

public class LayerPanel extends EditorSubComponent {

    private final boolean horizontal;

    public LayerPanel(Editor editor, Editor layerEditor) {
        super(editor);

        Orientation layerOrientation = layerEditor.getOrientation();

        switch (orientation) {
            case TOP:
                switch (layerOrientation) {
                    case FRONT:
                        horizontal = true;
                        break;

                    case RIGHT:
                        horizontal = false;
                        break;

                    case UNDEFINED:
                    case TOP:
                    default:
                        throw new InternalError("Badly filtered layer addition");
                }
                break;

            case FRONT:
                switch (layerOrientation) {
                    case TOP:
                        horizontal = true;
                        break;

                    case RIGHT:
                        horizontal = false;
                        break;

                    case FRONT:
                    case UNDEFINED:
                    default:
                        throw new InternalError("Badly filtered layer addition");
                }
                break;

            case RIGHT:
                switch (layerOrientation) {
                    case TOP:
                        horizontal = true;
                        break;

                    case FRONT:
                        horizontal = false;
                        break;

                    case RIGHT:
                    case UNDEFINED:
                    default:
                        throw new InternalError("Badly filtered layer addition");
                }
                break;

            case UNDEFINED:
            default:
                throw new InternalError("Badly filtered layer addition");
        }
    }

    @Override
    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr);

        Graphics2D g = (Graphics2D) gr;

        int length = (horizontal ? width : height);

        g.setColor(Constants.COLORACTIVELAYER);

        if (horizontal) {

            g.draw(new Rectangle2D.Float(0.5f, 0.5f, Editor.SIZE * length, Editor.SIZE));

            for (int i = 1; i < length; i++)
                g.draw(new Line2D.Float(Editor.SIZE * i + 0.5f, Editor.SIZE, Editor.SIZE * i + 0.5f, 0.5f));

        } else {

            g.draw(new Rectangle2D.Float(0.5f, 0.5f, Editor.SIZE, Editor.SIZE * length));

            for (int i = 1; i < length; i++)
                g.draw(new Line2D.Float(Editor.SIZE, Editor.SIZE * i + 0.5f, 0.5f, Editor.SIZE * i + 0.5f));
        }
    }

    public void setLayerHeight(short layerHeight) {

        if (horizontal)
            setBounds(0, (int) (Editor.SIZE * layerHeight), (int) (width * Editor.SIZE), (int) ((Editor.SIZE + 1)));

        else
            setBounds((int) (Editor.SIZE * layerHeight), 0, (int) ((Editor.SIZE + 1)), (int) (height * Editor.SIZE));
    }
}
