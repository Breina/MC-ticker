package presentation.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import logging.Log;

import org.xml.sax.SAXException;

import presentation.blocks.BlockCategory;
import presentation.blocks.BlockLogic;
import presentation.blocks.BlockSet;
import presentation.blocks.BlocksXML;

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
