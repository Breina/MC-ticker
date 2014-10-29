package presentation.gui.windows.world;

import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import presentation.controllers.WorldController;
import utils.Tag;
import utils.Tag.Type;

public class NBTviewer extends WorldWindow {
	private static final long serialVersionUID = -6830958137411873462L;
	
	private DefaultMutableTreeNode top;
	private DefaultTreeModel model;
	private JTree tree;
	
	public NBTviewer(WorldController controller) {
		super(controller, "NBTviewer", false);
		
		buildGUI(controller);
	}
	
	public void buildGUI(WorldController controller) {
		
		setSize(new Dimension(200, 200));
		setLocation(500, 500);
		
		top = new DefaultMutableTreeNode(controller.getWorldData().getName());
		
		tree = new JTree(top);
		model = (DefaultTreeModel) tree.getModel();
		
		add(new JScrollPane(tree));
	}
	
	private void createNodes(DefaultMutableTreeNode parent, Tag tag) {
		
		DefaultMutableTreeNode node;
		
		switch (tag.getType()) {
		
			case TAG_End:
		        	break;
			
			case TAG_Byte:
		        	node = new DefaultMutableTreeNode("byte " + tag.getName() + ": " + (byte) tag.getValue());
					node.setAllowsChildren(false);
					parent.add(node);
					break;
					
			case TAG_Short:
		        	node = new DefaultMutableTreeNode("short " + tag.getName() + ": " + (short) tag.getValue());
					node.setAllowsChildren(false);
					parent.add(node);
					break;
		        	
			case TAG_Int:
		        	node = new DefaultMutableTreeNode("int " + tag.getName() + ": " + (int) tag.getValue());
					node.setAllowsChildren(false);
					parent.add(node);
					break;
		        	
			case TAG_Long:
		        	node = new DefaultMutableTreeNode("long " + tag.getName() + ": " + (long) tag.getValue());
					node.setAllowsChildren(false);
					parent.add(node);
					break;
		        	
			case TAG_Float:
		        	node = new DefaultMutableTreeNode("float " + tag.getName() + ": " + (float) tag.getValue());
					node.setAllowsChildren(false);
					parent.add(node);
					break;
		        	
			case TAG_Double:
		        	node = new DefaultMutableTreeNode("double " + tag.getName() + ": " + (double) tag.getValue());
					node.setAllowsChildren(false);
					parent.add(node);
					break;
		        	
			case TAG_Byte_Array:
					byte[] byteArray = (byte[]) tag.getValue();
					
					StringBuilder sbByte = new StringBuilder();
					sbByte.append("byte array ");
					sbByte.append(tag.getName());
					sbByte.append('[');
					sbByte.append(byteArray.length);
					sbByte.append("]: {");
					
					if (byteArray.length > 0) {
						if (byteArray.length > 100) {
							
							sbByte.append('…');
							
						} else {
						
							sbByte.append(byteArray[0]);
							
							for (int i = 1; i < byteArray.length; i++) {
								sbByte.append(", ");
								sbByte.append(byteArray[i]);
							}
						}
					}
					
					sbByte.append('}');
					
		        	node = new DefaultMutableTreeNode(sbByte.toString());
					node.setAllowsChildren(false);
					parent.add(node);
					break;
		        	
			case TAG_String:
		        	node = new DefaultMutableTreeNode("string " + tag.getName() + ": " + (String) tag.getValue());
					node.setAllowsChildren(false);
					parent.add(node);
					break;
		        	
			case TAG_List:
					Tag[] listValues = (Tag[]) tag.getValue();
					
					node = new DefaultMutableTreeNode("list " + tag.getName() + "[" + listValues.length + "]");
					parent.add(node);
					
					for (Tag subTag : listValues)
						createNodes(node, subTag);
					
					break;
				
			case TAG_Compound:
					Tag[] compoundValues = (Tag[]) tag.getValue();
				
		        	node = new DefaultMutableTreeNode("compound[" + compoundValues.length + "]");
					parent.add(node);
					
					for (Tag subTag : compoundValues)
						createNodes(node, subTag);
					
					break;
		        	
			case TAG_Int_Array:
					byte[] intArray = (byte[]) tag.getValue();
					
					StringBuilder sbInt = new StringBuilder();
					sbInt.append("int array ");
					sbInt.append(tag.getName());
					sbInt.append('[');
					sbInt.append(intArray.length);
					sbInt.append("]: {");
					
					if (intArray.length > 0) {
						if (intArray.length > 100) {
							
							sbInt.append('…');
							
						} else {
							sbInt.append(intArray[0]);
							
							for (int i = 1; i < intArray.length; i++) {
								sbInt.append(", ");
								sbInt.append(intArray[i]);
							}
						}
					}
					
					sbInt.append('}');
					
		        	node = new DefaultMutableTreeNode(sbInt.toString());
					node.setAllowsChildren(false);
					parent.add(node);
					break;
				
			default:
				node = new DefaultMutableTreeNode("unknown " + tag.getName());
				break;
		}
	}
	
	public void updateNBTContents(Tag schematic) {
		
		top.removeAllChildren();
		
		createNodes(top, schematic);
		
		model.reload(top);
	}
}
