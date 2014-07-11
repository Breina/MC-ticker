package logging;

import java.util.ArrayList;

import sim.constants.Constants;


public class Log {
	
	private static ArrayList<TimeString> messages = new ArrayList<TimeString>();
	
	private static ILogger output;
	
	private static long lastTime;
	private static boolean buffer = true;
	
	public static void i(String s) {	addMsg(s, TimeString.Types.info);		}
	public static void w(String s) {	addMsg(s, TimeString.Types.warning);	}
	public static void e(String s) {	addMsg(s, TimeString.Types.error);		}
	
	private static void addMsg(String s, TimeString.Types type) {
		
		long curTime = System.currentTimeMillis();		
		long interval = curTime - lastTime;
		
		TimeString ts = new TimeString(type, interval, s);
		
		if (output != null)
			output.print(ts.toString());
		
		if (buffer)
			messages.add(ts);
		
		if (!Constants.LOG_IGNORE_WARNINGS || !(type == TimeString.Types.warning))
			lastTime = curTime;
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

}
