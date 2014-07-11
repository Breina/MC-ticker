package gui.objects;

import java.util.Arrays;

public class Block {
	
	public static final byte BLOCK_AIR				= 0;
	public static final byte BLOCK_WATER_FLOWING	= 8;
	public static final byte BLOCK_WATER_SOURCE		= 9;
	public static final byte BLOCK_GLASS			= 20;
	public static final byte BLOCK_SLAB				= 44;
	public static final byte BLOCK_CHEST			= 54;
	public static final byte BLOCK_WIRE				= 55;
	public static final byte BLOCK_WOOD_DOOR		= 64;
	public static final byte BLOCK_LEVER			= 69;
	public static final byte BLOCK_STONE_PLATE		= 70;
	public static final byte BLOCK_IRON_DOOR		= 71;
	public static final byte BLOCK_WOOD_PLATE		= 72;
	public static final byte BLOCK_TORCH_OFF		= 75; 
	public static final byte BLOCK_TORCH_ON			= 76;
	public static final byte BLOCK_STONE_BUTTON		= 77;
	public static final byte BLOCK_ICE				= 79;
	public static final byte BLOCK_REPEATER_OFF		= 93;
	public static final byte BLOCK_REPEATER_ON		= 94;
	public static final byte BLOCK_STAINED_GLASS	= 95;
	public static final byte BLOCK_WOOD_BUTTON		= (byte) 143; // yeah good job java...
	public static final byte BLOCK_GOLD_PLATE		= (byte) 147;
	public static final byte BLOCK_IRON_PLATE		= (byte) 148;
	public static final byte BLOCK_COMPARATOR		= (byte) 149;
//	public static final byte BLOCK_COMPARATOR_ON	= (byte) 150; depricated since MC 1.6
	public static final byte BLOCK_REDSTONE			= (byte) 152;
	public static final byte BLOCK_HOPPER			= (byte) 154;
	public static final byte BLOCK_PACKED_ICE		= (byte) 174;
	
	public static final byte DATA_NONE = 0;
	
	public static final Block B_AIR = new Block(BLOCK_AIR);
	
	private byte id, data;
	
	public Block(byte id) {
		this(id, DATA_NONE);
	}
	
	public Block(byte id, byte data) {
		this.id = id;
		this.data = data;
	}
	
	public void setId(byte id) {
		this.id = id;
	}
	
	public void setData(byte data) {
		this.data = data;
	}
	
	public byte getId() {
		return this.id;
	}
	
	public byte getData() {
		return this.data;
	}
	
	public boolean isSolidBlock() {
		// Thank you so much java, I love you. Of course I wanted to double cast, of course I don't want to have unsigned bytes...
		return Arrays.asList(1, 2, 3, 4, 5, 7, 14, 15, 16, 17, 19, 21, 22, 24, 35, 41, 42, 43, 45, 48, 49, 56, 57, 58, 60,
				61, 62, 73, 74, 80, 82, 84, 86, 87, 88, 91, 97, 98, 103, 110, 112, 121, (int) ((byte) 155), (int) ((byte) 159),
				(int) ((byte) 162), (int) ((byte) 170), (int) ((byte) 172), (int) ((byte) 173)).contains((int) id);
	}
	
	/**
	 * Checks if a wire should connect to a given block
	 * @param b The block in question
	 * @param side Used for checking the side of a repeater
	 * @return
	 */
	public boolean isConnectable(boolean side) {
												// AARRRHGHGHH FUCKNG JAVA WTTFFF
		if (Arrays.asList(55, 69, 70, 72, 75, 76, 77, (int) ((byte) 143), (int) ((byte) 147), (int) ((byte) 148),
				(int) ((byte) 149), (int) ((byte) 152)).contains((int) this.id))
			return true;

		if (this.id == (byte) 93 || id == (byte) 94) {
			boolean orientation = (this.data & 0b1) != (byte) 0 ? false : true; // This reduces it down to vertical or horizontal
			return (orientation == side);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "id=" + ((id < 0) ? id + 256 : id) + ", data=" + data; 
	}
	
	@Override
	public boolean equals(Object obj) {
		
		Block b = (Block) obj;
		
		return (id == b.getId() && data == b.getData());
	}
}
