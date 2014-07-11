package gui.exceptions;

public class ImgurDeniedException extends Exception {

	public ImgurDeniedException(int code) {
		super("Upload failed: " + code);
	}
}
