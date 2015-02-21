package logging;

import sim.constants.Constants;

import java.util.ArrayList;


public class Log {
	
	private static ArrayList<TimeString> messages = new ArrayList<TimeString>();
	
	private static ILogger output;
	
	private static long lastTime;
	private static boolean buffer = true;
	
	private static boolean isTest = false;
	
	public static void i(String s) {	addMsg(s, TimeString.Types.info);		}
	public static void w(String s) {	addMsg(s, TimeString.Types.warning);	}
	public static void e(String s) {	addMsg(s, TimeString.Types.error);	    }
    public static void d(String s) {    addMsg(s, TimeString.Types.debug);      }
	
	private static void addMsg(Object msg, TimeString.Types type) {

        String s;

        if (msg instanceof String)
            s = (String) msg;
        else
            s = String.valueOf(msg);

        if (type == TimeString.Types.debug)
            s = getCallerName() + s;
		
		long curTime = System.currentTimeMillis();		
		long interval = curTime - lastTime;
		
		TimeString ts = new TimeString(type, interval, s);
		
		if (output != null)
			output.print(ts.toString());
		
		if (buffer)
			messages.add(ts);
		
		if (!Constants.LOG_IGNORE_WARNINGS || !(type == TimeString.Types.warning))
			lastTime = curTime;
		
		if (!isTest)
			System.out.print(ts);
	}
	
	public static String getMessages() {
		
		StringBuilder sb = new StringBuilder();
		
		for (TimeString ts : messages)
			sb.append(ts);
		
		return sb.toString();		
	}
	
	public static void setBufferring(boolean _buffer) {
		
		if (!_buffer)
			messages.clear();
		
		buffer = _buffer;
		
	}
	
	public static void setLogger(ILogger _output) {
		output = _output;
	}

	public static void setTest(boolean isTest) {
		Log.isTest = isTest;
	}

    private static String getCallerName() {
        StackTraceElement stackTraceElement = Thread.currentThread().getStackTrace()[4];
        return stackTraceElement.getClassName() + '.' + stackTraceElement.getMethodName() + ": ";
    }
}
