package logging;

import sim.constants.Constants;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TimeString {
	
	public long millis;
	public String msg;
	public Types type;
	
	public enum Types {
		info,
		warning,
		error,
        debug
	}

    public TimeString(Types type, long millis, String msg) {
		this.type = type;
		this.millis = millis;
		this.msg = msg;
	}
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(timeStamp());
		sb.append('\t');
		
		switch (type) {
			
			case info:
				sb.append('-');
				break;
				
			case warning:
				sb.append('!');
				break;
				
			case error:
				sb.append('E');
				break;

            case debug:
                sb.append('D');
                break;
			
			default:
				sb.append('?');
				break;
		}
		
		sb.append(' ');
		
		int pos = 0;
		
		if (msg.length() >= Constants.OUTPUT_INDENT) {
			
			String[] words = msg.split(" ");
			
			
			for (int i = 0; i < words.length; i++) {
				
				String word = words[i];
				
				if (((pos + word.length() + 1)) >= Constants.OUTPUT_INDENT) {
					
					sb.append("\n   ");
					pos = 1;
					
				} else
					if (pos != 0)
						sb.append(' ');
				
				sb.append(word);				
				pos += word.length() + 1;				
			}
			
			pos--;
			
		} else {
			
			sb.append(msg);
			pos = msg.length();
		}
		
//		if (Constants.LOG_IGNORE_WARNINGS && type == Types.warning)
//			return sb.toString();
//		
//		for (; pos < Constants.OUTPUT_INDENT; pos++)
//			sb.append(' ');
//		
//		sb.append(millis);
		sb.append('\n');
		
		return sb.toString();
	}
	
	private String timeStamp() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");
		Date date = new Date(System.currentTimeMillis());
		return dateFormat.format(date) + " ";
	}

}
