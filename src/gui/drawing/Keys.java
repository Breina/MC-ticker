package gui.drawing;

/**
 * These constants are used as part of generating hashcodes for the BufferedImage HashMap
 */
public class Keys {
	
	// IMPORTANT: ONLY USE 6 BITS FOR THESE (graphical errors would otherwise arise)
	public static final byte AIR				= 0b00_0000;
	public static final byte SOLID				= 0b00_0001;
	public static final byte REDSTONE_WIRE		= 0b00_0010;
	public static final byte REDSTONE_TORCH		= 0b00_0011;
	public static final byte REDSTONE_REPEATER	= 0b00_0100;
	public static final byte BUTTON				= 0b00_0101;
	public static final byte REDSTONE_BLOCK		= 0b00_0110;
	public static final byte PRESSURE_PLATE		= 0b00_0111;
	public static final byte GLASS				= 0b00_1000;
	public static final byte ICE				= 0b00_1001;
	public static final byte COMPARATOR			= 0b00_1010;
	public static final byte PACKED_ICE			= 0b00_1011;
	public static final byte HOPPER				= 0b00_1100;
	public static final byte WATER				= 0b00_1101;
	public static final byte SLAB				= 0b00_1110;
	
	public static final byte LAYER				= 0b10_0000;
}
