package presentation.gui.windows.world;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import presentation.controllers.WorldController;
import utils.Tag;

public class NBTviewer extends WorldWindow {
	private static final long serialVersionUID = -6830958137411873462L;
	
	private JTree tree;
	
	public NBTviewer(WorldController controller) {
		super(controller, "NBTviewer", false);
		
		buildGUI(controller);
	}
	
	public void buildGUI(WorldController controller) {
		
		setSize(new Dimension(200, 200));
		setLocation(500, 500);
		
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(controller.getWorldData().getName());
		
		createNodes(top);
		
		tree = new JTree(top);
		
		add(new JScrollPane(tree));
	}
	
	private void createNodes(DefaultMutableTreeNode top) {
	    DefaultMutableTreeNode category = null;
	    DefaultMutableTreeNode book = null;
	    
	    category = new DefaultMutableTreeNode("Books for Java Programmers");
	    top.add(category);
	    
	    //original Tutorial
	    book = new DefaultMutableTreeNode("The Java Tutorial: A Short Course on the Basics");
	    category.add(book);
	    
	    //Tutorial Continued
	    book = new DefaultMutableTreeNode("The Java Tutorial Continued: The Rest of the JDK");
	    category.add(book);
	    
	    //Swing Tutorial
	    book = new DefaultMutableTreeNode("The Swing Tutorial: A Guide to Constructing GUIs");
	    category.add(book);

	    //...add more books for programmers...

	    category = new DefaultMutableTreeNode("Books for Java Implementers");
	    top.add(category);

	    //VM
	    book = new DefaultMutableTreeNode("The Java Virtual Machine Specification");
	    category.add(book);

	    //Language Spec
	    book = new DefaultMutableTreeNode("The Java Language Specification");
	    category.add(book);
	}
	
	public void updateNBTContents(Tag schematic) {
		
//		for (schematic.)
	}
}
