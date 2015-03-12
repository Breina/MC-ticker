package presentation.debug;

import javax.swing.*;

class TracingRepaintManager extends RepaintManager {
    @Override
    public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
        try {
            throw new Exception();
        } catch (Exception exc) {
            StringBuilder sb = new StringBuilder();
            StackTraceElement[] stack = exc.getStackTrace();
            int count = 0;
            for (StackTraceElement stackEntry : stack) {
                if (count++ > 8)
                break;
                sb.append('\t');
                sb.append(stackEntry.toString());
                sb.append('\n');
            }
            System.out.println("**** Repaint stack ****");
            System.out.println(sb.toString());
        }

        super.addDirtyRegion(c, x, y, w, h);
    }
}