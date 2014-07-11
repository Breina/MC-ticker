package gui.bettergui.blocks;

import gui.objects.Orientation;

public class BlockLogic {
	
	private byte id;
	private String name;
	
	private byte iconData;
	private Orientation iconOrientation;
	
	private byte rotationMask, rotationMin, rotationMax, rotationIncrease;
	
	private byte clickMask, clickMin, clickMax, clickIncrease;

	public BlockLogic(byte id) {
		this.id = id;
		
		iconOrientation = Orientation.TOP;
	}

	public void setId(byte id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setIconData(byte iconData) {
		this.iconData = iconData;
	}

	public void setIconOrientation(Orientation iconOrientation) {
		this.iconOrientation = iconOrientation;
	}

	public void setRotationMask(byte rotationMask) {
		this.rotationMask = rotationMask;
		this.rotationMax = rotationMask;
		this.rotationIncrease = getIncreaseFromMask(rotationMask);
	}
	
	public void setRotationMin(byte rotationMin) {
		this.rotationMin = rotationMin;
	}

	public void setRotationMax(byte rotationMax) {
		this.rotationMax = rotationMax;
	}

	public void setClickMask(byte clickMask) {
		this.clickMask = clickMask;
		this.clickMax = clickMask;
		this.clickIncrease = getIncreaseFromMask(clickMask);
	}
	
	public void setClickMin(byte clickMin) {
		this.clickMin = clickMin;
	}

	public void setClickMax(byte clickMax) {
		this.clickMax = clickMax;
	}
	
	public byte getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public byte getIconData() {
		return iconData;
	}
	
	public Orientation getIconOrientation() {
		return iconOrientation;
	}
	
	public byte getIncreaseFromMask(byte mask) {
		
		for (byte b = 1; b <= 8; b *= 2) {
			
			if ((mask & b) != 0)
				return b;
		}
		
		return 0;
	}
	
	private byte increaseData(byte data, byte mask, byte increase, byte min, byte max) {
		
		byte increasedData = (byte) ((data + increase) & mask);
		
		if (increasedData > max)
			increasedData = min;
		
		return (byte) ((data & ~mask) | increasedData);
	}
	
	public byte rotate(byte data) {
		
		return increaseData(data, rotationMask, rotationIncrease, rotationMin, rotationMax);
	}
	
	public byte click(byte data) {
		
		return increaseData(data, clickMask, clickIncrease, clickMin, clickMax);
	}
}
