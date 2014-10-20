package presentation.blocks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import logging.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import presentation.objects.Orientation;

public class BlocksXML {
	
	private SAXParserFactory factory;
	private SAXParser saxParser;
	
	private DefaultHandler handler;
	
	private List<BlockSet> blockSets;
	
	public void prepareHandler() throws ParserConfigurationException, SAXException {
		
		handler = new DefaultHandler() {
			
			boolean isName = false;
			
			BlockSet blockSet = null;
			BlockCategory blockCaterogy = null;
			BlockLogic blockLogic = null;
			
			@Override
			public void startElement(String uri, String localName,String qName, 
	                Attributes attributes) throws SAXException {
				
					switch (qName) {
						case "blocks":
							
							String blocksName = null;
							int blocksAttLength = attributes.getLength();
							
							for (int i = 0; i < blocksAttLength; i++) {
								
								String type = attributes.getQName(i);
								String value = attributes.getValue(i);
								
								switch (type) {
									case "name":
										blocksName = value;
										break;
										
									default:
										Log.e("XML could not be parsed, unknown attribute in blocks: " + type);
										return;
								}
							}
							
							if (blocksName == null)
								blocksName = "Unnamed";
							
							blockSet = new BlockSet(blocksName);
							break;
							
						case "category":
							String categoryName = null;
							int categoryAttLength = attributes.getLength();
							
							for (int i = 0; i < categoryAttLength; i++) {
								
								String type = attributes.getQName(i);
								String value = attributes.getValue(i);
								
								switch (type) {
									case "name":
										categoryName = value;
										break;
										
									default:
										Log.e("XML could not be parsed, unknown attribute in category: " + type);
										return;
								}
							}
							
							if (categoryName == null)
								categoryName = "Unnamed";
							
							blockCaterogy = new BlockCategory(categoryName);
							break;
							
						case "block":
							byte id = 0;
							boolean hidden = false;
							int blockAttLength = attributes.getLength();
							
							for (int i = 0; i < blockAttLength; i++) {
								
								String type = attributes.getQName(i);
								String value = attributes.getValue(i);
								
								switch (type) {
									case "id":
										id = (byte) Short.parseShort(value); // because fuck negative id's
										break;
										
									case "hidden":
										hidden = Boolean.parseBoolean(value);
										break;
										
									default:
										Log.e("XML could not be parsed, unknown attribute in block: " + type);
										return;
								}
							}
							
							blockLogic = new BlockLogic(id, hidden);
							break;
							
						case "name":
							isName = true;
							break;
							
						case "icon":
							
							if (blockLogic == null) {
								Log.e("XML could not be parsed, adding icon outside of block.");
								return;
							}
							
							int iconAttLength = attributes.getLength();
							
							for (int i = 0; i < iconAttLength; i++) {
								
								String type = attributes.getQName(i);
								String value = attributes.getValue(i);
								
								switch (type) {
									
									case "data":
										blockLogic.setIconData((byte) Short.parseShort(value));
										break;
										
									case "orientation":
										switch (value) {
											case "front":
												blockLogic.setIconOrientation(Orientation.FRONT);
												break;
												
											case "right":
												blockLogic.setIconOrientation(Orientation.RIGHT);
												break;
												
											case "top":
												break;	// top is the default value for orientation
												
											default:
												Log.e("XML could not be parsed, unknown orientation: " + value);
												return;
										}
										break;
										
									default:
										Log.e("XML could not be parsed, unknown attribute in icon: " + type);
										return;
								}
							}
							break;
							
						case "rotation":
							
							if (blockLogic == null) {
								Log.e("XML could not be parsed, adding rotation outside of block.");
								return;
							}
							
							int rotationAttLength = attributes.getLength();
							
							for (int i = 0; i < rotationAttLength; i++) {
								
								String type = attributes.getQName(i);
								String value = attributes.getValue(i);
								
								switch (type) {
									
									case "mask":
										blockLogic.setRotationMask((byte) Short.parseShort(value));
										break;
										
									case "min":
										blockLogic.setRotationMin((byte) Short.parseShort(value));
										break;
										
									case "max":
										blockLogic.setRotationMax((byte) Short.parseShort(value));
										break;
										
									case "sides":
										blockLogic.setSides(value);
										break;
										
									default:
										Log.e("XML could not be parsed, unknown attribute in rotation: " + type);
										return;
								}
							}
							break;
							
//						case "click":
//							
//							if (blockLogic == null) {
//								Log.e("XML could not be parsed, adding click outside of block.");
//								return;
//							}
//							
//							int clickAttLength = attributes.getLength();
//							
//							for (int i = 0; i < clickAttLength; i++) {
//								
//								String type = attributes.getQName(i);
//								String value = attributes.getValue(i);
//								
//								switch (type) {
//									
//									case "mask":
//										blockLogic.setClickMask((byte) Short.parseShort(value));
//										break;
//									
//									case "min":
//										blockLogic.setClickMin((byte) Short.parseShort(value));
//										
//									case "max":
//										blockLogic.setClickMax((byte) Short.parseShort(value));
//										break;
//										
//									default:
//										Log.e("XML could not be parsed, unknown attribute in rotation: " + type);
//										return;
//								}
//							}
//							break;
						
						default:
							Log.e("XML could not be parsed, unknown element: " + qName);
							return;
					}
			}
			
			@Override
			public void endElement(String uri, String localName, String qName) throws SAXException {
				
				switch (qName) {
					
					case "blocks":
						
						if (blockSet == null) {
							Log.e("Could not parse XML, closed blocks before opening it.");
							break;
						}
						
						blockSets.add(blockSet);
						blockSet = null;
						break;
						
					case "category":
						
						if (blockCaterogy == null) {
							Log.e("Could not parse XML, closed category before opening it.");
							return;
						}
						
						if (blockSet == null) {
							Log.e("Could not parse XML, closed category outside of blocks.");
							return;
						}
						
						blockSet.addCategory(blockCaterogy);
						break;
						
					case "block":
						
						if (blockLogic == null) {
							Log.e("Could not parse XML, closed block before opening it.");
							return;
						}
							
						if (blockCaterogy == null) {
							Log.e("Could not parse XML, closed block outside of category.");
							return;
						}
						
						blockCaterogy.addBlock(blockLogic);
						break;
						
					case "name":
						isName = false;
						break;
						
					// TODO no bad closing tags are checked
				}	
			}
			
			@Override
			public void characters(char[] ch, int start, int length) throws SAXException {
				
				if (isName) {
					
					if (blockLogic == null) {
						Log.e("XML could not be parsed, adding name outside of block.");
						return;
					}
					
					String name = String.copyValueOf(ch, start, length);
					blockLogic.setName(name);
				}
			}
		};
	}
	
	public BlocksXML() throws ParserConfigurationException, SAXException {
		
		factory = SAXParserFactory.newInstance();
		saxParser = factory.newSAXParser();
		
		prepareHandler();
	}
	
	public List<BlockSet> parseTiles(File file) throws SAXException, IOException {
		
		blockSets = new ArrayList<>();
		
		saxParser.parse(file, handler);
		
		return blockSets;
	}
}
