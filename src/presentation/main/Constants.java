package presentation.main;

import java.awt.*;

// This is a separate constants file than the sim.constants one, I originally thought
// to release the sim part as a library separately, but fuck that. I now regret that
// decision.
public class Constants {
	// RSFrame
	public final static String APPLICATIONTITLE = "MC ticker 0.2 (pre-alpha)";
	public final static String MOTD = "Thanks for trying out the sim! Lots of things are not done though.";

    public final static int MAX_FPS = 17; // ms (~60 fps)

	public final static String SCHEMATICSDIR = "schems";
	public final static String EXPORTDIR = "export";

	public final static String TILEMAPSFILE = "conf/tilemaps.xml";
	public final static String TILEIMAGES = "img/tiles/";
	public final static String IMAGEEXTENSION = ".png";
	public final static char GRAPHICSEPARATOR = '-';
	
	public final static String BLOCKSFILE = "conf/blocks.xml";
	
	public static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	public static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
	
	// Default colors
	public static final Color COLORBACKGROUND			= Color.WHITE;
	public static final Color COLORACTIVELAYER			= Color.BLUE;
	public static final Color COLORCURSOR               = Color.GREEN;
	public static final Color COLORENTITY				= Color.RED;
	public static final Color COLORENTITYVECTOR			= Color.ORANGE;
    public static final Color COLORSELECTIONBORDER      = Color.BLACK;
    public static final Color COLORSELECTIONINTERIOR    = new Color(0, 0, 0, 64);

    public static final float DEFAULTZOOM = 2f;

	public static final float ENTITYVELOCITYMULTIPLIER	= 10;
}
