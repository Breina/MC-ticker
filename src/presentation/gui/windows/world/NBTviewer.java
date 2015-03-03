package presentation.gui.windows.world;

import presentation.controllers.WorldController;
import presentation.gui.TreeUtil;
import utils.Tag;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;

public class NBTviewer extends WorldWindow {
	private static final long serialVersionUID = -6830958137411873462L;
	
	private DefaultMutableTreeNode top;
	private DefaultTreeModel model;
	private JTree tree;
	private JScrollPane scrollPane;
	
	public NBTviewer(JComponent parent, WorldController controller) {
		super(parent, controller, "NBTviewer", false);
		
		buildGUI();
	}
	
	public void buildGUI() {
		
		setSize(new Dimension(200, 200));
		setLocation(500, 500);
		
		top = new DefaultMutableTreeNode(controller.getWorldData().getName());
		
		tree = new JTree(top) {
			@Override
			public boolean getScrollableTracksViewportHeight() {
				return false;
			}

			@Override
			public boolean getScrollableTracksViewportWidth() {
				return false;
			}
		};
		model = (DefaultTreeModel) tree.getModel();
		
		tree.addTreeExpansionListener(new TreeExpansionHandler());

		scrollPane = new JScrollPane(tree);
		scrollPane.setBackground(Color.WHITE);

		add(scrollPane);
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
                parent.add(node);
				break;
		}
	}
	
	protected class TreeExpansionHandler implements TreeExpansionListener {

		@Override
		public void treeExpanded(TreeExpansionEvent event) {
			
			TreePath path = event.getPath();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
			
			if (node.getChildCount() == 1)				
				tree.expandPath(path);
		}

		@Override
		public void treeCollapsed(TreeExpansionEvent event) {
		}
		
	}
	
	public synchronized void updateNBTContents(Tag schematic) {

		Point prevScroll = scrollPane.getViewport().getViewPosition();

		String prevState = TreeUtil.getExpansionState(tree, 0);
		top.removeAllChildren();

		createNodes(top, schematic);

		model.reload(top);

		TreeUtil.restoreExpanstionState(tree, 0, prevState);

		SwingUtilities.invokeLater(new LaterUpdater(prevScroll));
	}

	/**
	 * Oh noes everything here is a hack just to make the scrollbar remember where it was
	 * after updating. Not pretty but it works 99% of the time.
	 * http://stackoverflow.com/questions/2039373/maintaing-jtextarea-scroll-position
 	 */
	class LaterUpdater implements Runnable {
		private Point o;

		public LaterUpdater(Point o) {
			this.o = o;
		}

		public void run() {
			NBTviewer.this.scrollPane.getViewport().setViewPosition(o);
		}
	}
}
