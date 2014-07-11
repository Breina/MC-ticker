package sim.controller;

public class Response {

	public enum Type {

		SUCCESS,		// payload will be	null
		ERROR,			// payload will be	Exception or null
	}

	private Type type;
	private String message;
	private Object payload;
	
	public Response(Type type) {
		this(type, null, null);
	}
	
	public Response(Type type, String message) {
		this(type, message, null);
	}
	
	public Response(Type type, String message, Object payload) {
		this.type = type;
		this.message = message;
		this.payload = payload;
	}

	public Type getStatus() {
		return type;
	}

	public void setStatus(Type type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}
}
