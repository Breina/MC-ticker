package gui.bettergui.tiles;

import gui.bettergui.tiles.conditions.Condition;
import gui.bettergui.tiles.conditions.ConditionData;
import gui.bettergui.tiles.conditions.ConditionId;
import gui.bettergui.tiles.conditions.ConditionOrientation;
import gui.objects.Orientation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import logging.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class TilesXml {
	
	private SAXParserFactory factory;
	private SAXParser saxParser;
	
	private DefaultHandler handler;
	
	private List<TileSet> tileSets;
	
	public void prepareHandler() throws ParserConfigurationException, SAXException {
		
		handler = new DefaultHandler() {
			
			boolean isName = false;
			
			Stack<Condition> stacky = new Stack<>();
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
						if (value.equals("horizontal"))
							condition.setMirror(Mirror.HORIZONTAL);
						else if (value.equals("vertical"))
							condition.setMirror(Mirror.VERTICAL);
						else {
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
		
		factory = SAXParserFactory.newInstance();
		saxParser = factory.newSAXParser();
		
		prepareHandler();
	}
	
	public List<TileSet> parseTiles(File file) throws SAXException, IOException {
		
		tileSets = new ArrayList<>();
		
		saxParser.parse(file, handler);
		
		return tileSets;
	}
}
