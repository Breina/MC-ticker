package presentation.gui.editor;

import presentation.main.Constants;
import presentation.objects.Orientation;

import java.awt.*;

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

            g.drawLine(0, 0, 0, EditorPanel.SIZE);
            g.drawLine(0, 0, EditorPanel.SIZE * length, 0);
            g.drawLine(EditorPanel.SIZE * length, EditorPanel.SIZE, 0, EditorPanel.SIZE);

            for (int i = 1; i < length; i++)
                g.drawLine(EditorPanel.SIZE * i, EditorPanel.SIZE, EditorPanel.SIZE * i, 0);

        } else {

            g.drawLine(0, 0, EditorPanel.SIZE, 0);
            g.drawLine(0, 0, 0, EditorPanel.SIZE * length);
            g.drawLine(EditorPanel.SIZE, EditorPanel.SIZE * length, EditorPanel.SIZE, 0);

            for (int i = 1; i < length; i++)
                g.drawLine(EditorPanel.SIZE, EditorPanel.SIZE * i, 0, EditorPanel.SIZE * i);
        }
    }

    public void setLayerHeight(short layerHeight) {

        if (horizontal)
            setBounds(0, (int) (Editor.SIZE * layerHeight), (int) (width * Editor.SIZE), (int) ((Editor.SIZE + 1)));

        else
            setBounds((int) (Editor.SIZE * layerHeight), 0, (int) ((Editor.SIZE + 1)), (int) (height * Editor.SIZE));
    }
}
