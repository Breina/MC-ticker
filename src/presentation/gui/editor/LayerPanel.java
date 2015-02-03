//package presentation.gui.editor;
//
//import com.sun.org.apache.xalan.internal.xsltc.compiler.util.*;
//import presentation.objects.Orientation;
//
//import java.lang.InternalError;
//
//public class LayerPanel extends EditorSubComponent {
//
//    public LayerPanel(Editor editor, Editor layerEditor) {
//
//
//
//        switch (orientation) {
//            case UNDEFINED:
//                break;
//            case TOP:
//
//                switch (layerEditor.getOrientation()) {
//                    case FRONT:
//                        this(editor, true, width);
//                        break;
//                    
//                    case RIGHT:
//                        this(editor, false, height);
//                        break;
//
//                    case UNDEFINED:
//                    case TOP:
//                    default:
//                        throw new InternalError("Badly filtered layer addition");
//                }
//            case RIGHT:
//                break;
//            case FRONT:
//                break;
//        }
//    }
//
//    private LayerPanel(Editor editor, boolean horizontal, int length) {
//        super(editor);
//
//
//    }
//}
