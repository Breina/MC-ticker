package presentation.objects;

import java.util.Arrays;

public class Block {
	
	public static final byte BLOCK_AIR				= 0;
    public static final byte BLOCK_IRON			= 42;
	public static final byte BLOCK_CHEST			= 54;
	public static final byte BLOCK_WIRE				= 55;
	
	private static final byte DATA_NONE = 0;
	
	private byte id, data;

    public static byte getId(char data) {
        return (byte) ((data & 0xff0) >> 4);
    }

    public static byte getData(char data) {
        return (byte) (data & 0xf);
    }

    public static char getChar(byte id, byte data) {
        return (char) ((id & 0xff) << 4 | data & 0xf);
    }

    @Deprecated
	public Block(byte id) {
		this(id, DATA_NONE);
	}

    @Deprecated
	public Block(byte id, byte data) {
		this.id = id;
		this.data = data;
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

	public static boolean isTransparentBlock(byte id) {
		// Thank you so much java, I love you. Of course I wanted to double cast, of course I don't want to have unsigned bytes...
		return !Arrays.asList(1, 2, 3, 4, 5, 7, 14, 15, 16, 17, 19, 21, 22, 24, 35, 41, 42, 43, 45, 48, 49, 56, 57, 58, 60,
                61, 62, 73, 74, 80, 82, 84, 86, 87, 88, 91, 97, 98, 103, 110, 112, 121, (int) ((byte) 155), (int) ((byte) 159),
                (int) ((byte) 162), (int) ((byte) 170), (int) ((byte) 172), (int) ((byte) 173)).contains((int) id);
	}

	/**
	 * Checks if a wire should connect to a given blocka
	 * @param side Used for checking the side of a repeater
	 * @return
	 */
	public static boolean isConnectable(char block, boolean side) {

        byte id = Block.getId(block);

												// AARRRHGHGHH FUCKNG JAVA WTTFFF
		if (Arrays.asList(55, 69, 70, 72, 75, 76, 77, (int) ((byte) 143), (int) ((byte) 147), (int) ((byte) 148),
				(int) ((byte) 149), (int) ((byte) 152)).contains((int) id))
			return true;

        // Repeaters
		if (id == (byte) 93 || id == (byte) 94) {
			boolean orientation = (Block.getData(block) & 0b1) == (byte) 0; // This reduces it down to vertical or horizontal
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

	public short toShort() {

		short blockId = id;

		if (blockId < 0)
			blockId += 256;

		return (short) (blockId << 4 | data);
	}
}
