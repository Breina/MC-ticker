package presentation.gui.toolbar;

import java.awt.*;

/**
 * Modified FlowLayout by hardwired
 * http://www.java-forums.org/awt-swing/11618-how-control-layout-multiple-toolbars.html#post34289
 */
public class WrappingLayout implements LayoutManager {
    public static final int LEFT     = 0;
    private static final int CENTER   = 1;
    private static final int RIGHT    = 2;
    private static final int LEADING  = 3;
    private static final int TRAILING = 4;

    private int align;
    private int hgap;
    private int vgap;

    public WrappingLayout() {
        this(CENTER, 5, 5);
    }

    public WrappingLayout(int align) {
        this(align, 5, 5);
    }

    private WrappingLayout(int align, int hgap, int vgap) {
        this.hgap = hgap;
        this.vgap = vgap;
        setAlignment(align);
    }

    public int getAlignment() {
        return align;
    }

    void setAlignment(int align) {
        switch (align) {
            case LEADING:
                this.align = LEFT;
                break;
            case TRAILING:
                this.align = RIGHT;
                break;
            default:
                this.align = align;
                break;
        }
    }

    public int getHgap() {
        return hgap;
    }

    public void setHgap(int hgap) {
        this.hgap = hgap;
    }

    public int getVgap() {
        return vgap;
    }

    public void setVgap(int vgap) {
        this.vgap = vgap;
    }

    public Dimension preferredLayoutSize(Container parent) {
        synchronized(parent.getTreeLock()) {
            Dimension dim = new Dimension(0,0);
            int maxWidth = 0;
            int componentCount = parent.getComponentCount();

            for(int i = 0; i < componentCount; i++) {
                Component c = parent.getComponent(i);
                if(c.isVisible()) {
                    Dimension d = c.getPreferredSize();
                    if((dim.width + d.width + hgap) <= parent.getWidth()) {
                        dim.height = Math.max(dim.height, d.height);
                    } else {
                        dim.height += vgap + d.height;
                        dim.width = 0;
                    }
                    if(dim.width > 0) {
                        dim.width += hgap;
                    }
                    dim.width += d.width;
                    if(dim.width > maxWidth) {
                        maxWidth = dim.width;
                    }
                }
            }
            Insets insets = parent.getInsets();
            dim.width = Math.max(dim.width, maxWidth);
            dim.width += insets.left + insets.right + 2*hgap;
            dim.height += insets.top + insets.bottom + 2*vgap;
            return dim;
        }
    }

    public Dimension minimumLayoutSize(Container parent) {
        synchronized(parent.getTreeLock()) {
            Dimension dim = new Dimension(0,0);
            int componentCount = parent.getComponentCount();

            for(int i = 0; i < componentCount; i++) {
                Component c = parent.getComponent(i);
                if(c.isVisible()) {
                    Dimension d = c.getMinimumSize();
                    dim.height = Math.max(dim.height, d.height);
                    if(i > 0) {
                        dim.width += hgap;
                    }
                    dim.width += d.width;
                }
            }
            Insets insets = parent.getInsets();
            dim.width += insets.left + insets.right + 2*hgap;
            dim.height += insets.top + insets.bottom + 2*vgap;
            return dim;
        }
    }

    public void layoutContainer(Container parent) {
        synchronized(parent.getTreeLock()) {
            Insets insets = parent.getInsets();
            int maxWidth = parent.getWidth() -
                    (insets.left + insets.right + hgap*2);
            int componentCount = parent.getComponentCount();
            int x = 0, y = insets.top + vgap;
            int rowh = 0, start = 0;
            boolean ltr = parent.getComponentOrientation().isLeftToRight();

            for(int i = 0; i < componentCount; i++) {
                Component c = parent.getComponent(i);
                if(c.isVisible()) {
                    Dimension d = c.getPreferredSize();
                    c.setSize(d.width, d.height);
                    if((x == 0) || ((x + d.width) <= maxWidth)) {
                        if(x > 0) {
                            x += hgap;
                        }
                        x += d.width;
                        rowh = Math.max(rowh, d.height);
                    } else {
                        rowh = moveComponents(parent, insets.left + hgap, y,
                                maxWidth - x, rowh, start, i, ltr);
                        x = d.width;
                        y += vgap + rowh;
                        rowh = d.height;
                        start = i;
                    }
                }
            }
            moveComponents(parent, insets.left + hgap, y, maxWidth - x,
                    rowh, start, componentCount, ltr);
        }
    }

    private int moveComponents(Container parent, int x, int y, int width,
                               int height, int rowStart, int rowEnd,
                               boolean ltr) {
        switch(align) {
            case LEFT:
                x += ltr ? 0 : width;
                break;
            case CENTER:
                x += width/2;
                break;
            case RIGHT:
                x += ltr ? width : 0;
                break;
            case LEADING:
                break;
            case TRAILING:
                x += width;
                break;
        }
        for(int i = rowStart; i < rowEnd; i++) {
            Component c = parent.getComponent(i);
            if(c.isVisible()) {
                int cy;
                cy = y + (height - c.getHeight())/2;
                if(ltr) {
                    c.setLocation(x, cy);
                } else {
                    c.setLocation(parent.getWidth() - x - c.getWidth(), cy);
                }
                x += c.getWidth() + hgap;
            }
        }
        return height;
    }

    public void addLayoutComponent(String name, Component comp) { }
    public void removeLayoutComponent(Component comp) { }
}