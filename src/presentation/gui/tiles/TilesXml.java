package presentation.gui.tiles;

import logging.Log;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import presentation.gui.tiles.conditions.Condition;
import presentation.gui.tiles.conditions.ConditionData;
import presentation.gui.tiles.conditions.ConditionId;
import presentation.gui.tiles.conditions.ConditionOrientation;
import presentation.main.Constants;
import presentation.objects.Orientation;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class TilesXml {

    private XMLReader reader;

    private DefaultHandler handler;
	
	private List<TileSet> tileSets;
	
	void prepareHandler() {
		
		handler = new DefaultHandler() {
			
			boolean isName = false;
			
			final Stack<Condition> stacky = new Stack<>();
			TileSet tileSet = null;
			
			@Override
			public void startElement(String uri, String localName,String qName, 
	                Attributes attributes) throws SAXException {
				
//				System.out.println(qName);
				
				switch (qName) {
					case "tileset":
						String name = null;
						String extension = null;
						
						int length = attributes.getLength();
						
						for (int i = 0; i < length; i++) {
							
							String type = attributes.getQName(i);
							String value = attributes.getValue(i);
							
							switch (type) {
								case "name":
									name = value;
									break;
								
								case "extension":
									extension = value;
									break;
									
								case "xsi:noNamespaceSchemaLocation":
								case "xmlns:xsi":
									break;
									
								default:
									Log.e("XML could not be parsed, unknown attribute in tileset: " + type);
									return;
							}
						}
						
						if (name == null) {
							Log.e("XML could not be parsed, no 'name' attribute for tileset.");
							return;
						}
						
						if (extension == null) {
							Log.e("XML could not be parsed, no 'extension' attribute for tileset.");
							return;
						}
						
						tileSet = new TileSet(name, extension);		
						break;
						
					case "entry":
						if (!stacky.isEmpty()) {
							Log.e("XML could not be parsed, entry contained within another entry.");
							return;
						}				
						
						stacky.push(new Condition());
						break;
						
					case "condition":
						if (stacky.isEmpty()) {
							Log.e("XML could not be parsed, adding condition outside of entry.");
							return;
						}
						
						int attributeLength = attributes.getLength();
						Condition condition = null;
						
						for (int i = 0; i < attributeLength; i++) {
							
							String type = attributes.getQName(i);
							String value = attributes.getValue(i);
							
							switch (type) {
								
								case "id":
									
									short id = Short.parseShort(value);
									condition = new ConditionId((byte) id);
									
									tileSet.addEntry(id, stacky.firstElement());
									
									break;
									
								case "data":
									condition = new ConditionData((byte) Short.parseShort(value));
									break;
									
								case "mask":
									if (condition == null) {
										Log.e("XML could not be parsed, found mask before finding data in a condition.");
										return;
									}
									
									if (!(condition instanceof ConditionData)) {
										Log.e("XML could not be parsed, found mask where it does not belong.");
									}
								
									((ConditionData) condition).setMask((byte) Short.parseShort(value));
									break;
									
								default:
									parseConditionAttributes(condition, type, value);
							}	
						}
						
						stacky.peek().addCondition(condition);
						stacky.push(condition);
						break;
						
					case "top":
						Condition topCondition = new ConditionOrientation(Orientation.TOP);
						
						int topLength = attributes.getLength();
						
						for (int i = 0; i < topLength; i++) {
							
							parseConditionAttributes(topCondition, attributes.getQName(i), attributes.getValue(i));
						}
						
						stacky.peek().addCondition(topCondition);
						stacky.push(topCondition);
						break;
						
					case "front":
						Condition frontCondition = new ConditionOrientation(Orientation.FRONT);
						
						int frontLength = attributes.getLength();
						
						for (int i = 0; i < frontLength; i++)
							parseConditionAttributes(frontCondition, attributes.getQName(i), attributes.getValue(i));
						
						stacky.peek().addCondition(frontCondition);
						stacky.push(frontCondition);
						break;
						
					case "right":
						Condition rightCondition = new ConditionOrientation(Orientation.RIGHT);
						
						int rightLength = attributes.getLength();
						
						for (int i = 0; i < rightLength; i++)
							parseConditionAttributes(rightCondition, attributes.getQName(i), attributes.getValue(i));
						
						stacky.peek().addCondition(rightCondition);
						stacky.push(rightCondition);
						break;
						
					case "name":
						isName = true;
						break;
						
					default:
						Log.e("XML could not be parsed, unrecognized opening tag: " + qName);
				}					
			}
			
			private void parseConditionAttributes(Condition condition, String type, String value) {
				
				if (condition == null) {
					Log.e("XML could not be parsed, found " + type + " before the condition is declared.");
				}
				
				switch (type) {
					
					case "rotation":
						condition.setRotation(Integer.parseInt(value));
						break;
						
					case "mirror":
                        switch (value) {
                            case "horizontal":
                                condition.setMirror(Mirror.HORIZONTAL);
                                break;
                            case "vertical":
                                condition.setMirror(Mirror.VERTICAL);
                                break;
                            default:
                                Log.e("XML could not be parsed, unrecognized mirror type: " + value);
                                return;
                        }
				}
				
				
			}
			
			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				
				switch (qName) {
					case "tileset":
						
						tileSets.add(tileSet);
						tileSet = null;
						break;
						
					case "entry":
						
						if (stacky.size() != 1) {
							Log.e("XML could not be parsed, entry not closed properly.");
							return;
						}
						
//						Condition entry = 
						stacky.pop();

//						tileSet.put(entry);
						break;
						
					case "condition":
					case "top":
					case "front":
					case "right":
						
						if (stacky.isEmpty()) {
							Log.e("XML could not be parsed, condition not closed properly.");
							return;
						}
						
						stacky.pop();
						break;
						
					case "name":
						isName = false;
						break;
						
					default:
						Log.e("XML could not be parsed, unrecognized closing tag: " + qName);
				}
			}
			
			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				
				if (isName) {
					
					if (stacky.isEmpty()) {
						Log.e("XML could not be parsed, attempting to add name to nothing!");
						return;
					}
					
					String name = String.copyValueOf(ch, start, length);
					
//					System.out.println("name: " + name);
					
					stacky.peek().setName(name);
				}
			}
		};
	}
	
	public TilesXml() throws ParserConfigurationException, SAXException {

        SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(true);

        XmlErrorHandler errorHandler = new XmlErrorHandler();
		
		reader = factory.newSAXParser().getXMLReader();
		reader.setErrorHandler(errorHandler);
		
		try {
			reader.setProperty(Constants.JAXP_SCHEMA_LANGUAGE, Constants.W3C_XML_SCHEMA);
			
		} catch (SAXNotRecognizedException e) {
			Log.e("Sax parser does not support entered version: " + e.getMessage());
		}
		
		prepareHandler();
		
		reader.setContentHandler(handler);
	}
	
	public List<TileSet> parseTiles(File file) throws SAXException, IOException {
		
		tileSets = new ArrayList<>();
		
		reader.parse(convertToFileURL(file));
		
		return tileSets;
	}
	
	private static String convertToFileURL(File file) {
        String path = file.getAbsolutePath();
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }

        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return "file:" + path;
    }
}
