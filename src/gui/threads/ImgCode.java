package gui.threads;

import gui.objects.Orientation;

public class ImgCode {
	
	private Orientation side;
	private short layer;
	private String code;
	
	// for image series
	public ImgCode(Orientation side, short layer) {
		this.side = side;
		this.layer = layer;
	}
	
	// for gif's
	public ImgCode(Orientation side) {
		this.side = side;
	}

	public Orientation getSide() {
		return side;
	}
	
	public short getLayer() {
		return layer;
	}

	public void setSide(Orientation side) {
		this.side = side;
	}

	public String getCode() {
		return code;
	}
	
	public void setLayer(short layer) {
		this.layer = layer;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
