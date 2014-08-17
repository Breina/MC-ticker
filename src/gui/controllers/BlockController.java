package gui.controllers;

import gui.bettergui.blocks.BlockCategory;
import gui.bettergui.blocks.BlockLogic;
import gui.bettergui.blocks.BlockSet;
import gui.bettergui.blocks.BlocksXML;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import logging.Log;

import org.xml.sax.SAXException;

public class BlockController {

	private List<BlockSet> blockSets;
	
	public BlockController(File xmlPath) {
		
		try {
			BlocksXML parser = new BlocksXML();
			blockSets = parser.parseTiles(xmlPath);
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			
			Log.e("Could not parse XML: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public List<BlockCategory> getAllCategories() {
		
		List<BlockCategory> cats = new ArrayList<>();
		
		for (BlockSet blockSet : blockSets) {
			
			cats.addAll(blockSet.getCategories());
		}
		
		return cats;
	}
	
	public BlockLogic getBlock(byte id) {
		
		for (BlockSet blockSet : blockSets)
			if (blockSet.containsBlock(id))
				return blockSet.getBlock(id);
		
		return null;
	}
}
