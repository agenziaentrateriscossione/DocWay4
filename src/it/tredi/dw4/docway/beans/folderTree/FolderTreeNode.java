package it.tredi.dw4.docway.beans.folderTree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

public class FolderTreeNode {
	private String code;
	private String counter;
	private String title;
	
	private List<FolderTreeNode> children;
	
	private int checked; // 0- no; 1 - partially; 2 -full
	
	public FolderTreeNode(Element node) {
		
		code = node.attributeValue("code");
		counter = node.attributeValue("counter");
		title = node.attributeValue("title");
		
		children = new ArrayList<>();
		
		for ( Iterator<Element> i = node.elementIterator("folder"); i.hasNext(); ) {
	        children.add(new FolderTreeNode(i.next()));
	    }
		
	}
	
	// add all checked children and theirs lists in ret
	public void getSelectedFolders(List<String> ret) {
		if(getChecked()){
			ret.add(this.getCode());
			
			for(FolderTreeNode el: children){
				el.getSelectedFolders(ret);
			}
		}
	}
	
	
	// h:selectBooleanCheckbox need boolean
	public boolean getChecked() {
		return this.getCheckedExt() > 0;
	}
	
	// here we handle real state
	public int getCheckedExt() {
		// read children and only iof all are checked we have 2 else 1 or 0
		if(children.size() > 0){
			int ret  = 0;
			for(FolderTreeNode el: children){
				ret += el.getCheckedExt();
			}
			
			return ret == children.size() * 2 ? 
							2 :  // sum of all children checked is 2 * size => all children are checked
							ret > 0 ? 1 : // sum is more then 0 => some child is checked 
							0;  // no child is checked
			
		} else {
			return checked;
		}		
	}

	// just go down the tree and select
	public void setChecked(boolean checked) {
		this.checked = checked ? 2 : 0;
		
		for(FolderTreeNode el: children){
			el.setChecked(checked);
		}
	}



	public boolean hasChildren() {
		return children.size() > 0;
	}
	
	public List<FolderTreeNode> getChildren() {
		return children;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getCounter() {
		return counter;
	}
	
	public String getTitle() {
		return title;
	}
}
