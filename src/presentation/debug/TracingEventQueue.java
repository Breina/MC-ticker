package presentation.debug;

import java.awt.*;

/**
 * Code is courtesy of Kirill Grouchnikov
 * https://today.java.net/article/2007/08/28/debugging-swing
 * http://lct-eq2.googlecode.com/svn/trunk/LCT/src/com/lct/eq2/debug/
 */
public class TracingEventQueue extends EventQueue {

    private final TracingEventQueueThreadJMX tracingThread;

    public TracingEventQueue() {
        this.tracingThread = new TracingEventQueueThreadJMX(500);
        this.tracingThread.start();
    }

    @Override
    protected void dispatchEvent(AWTEvent event) {
        this.tracingThread.eventDispatched(event);
        super.dispatchEvent(event);
        this.tracingThread.eventProcessed(event);
    }
}