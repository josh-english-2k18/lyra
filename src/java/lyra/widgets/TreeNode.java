/*
 * TreeNode.java
 *
 * Copyright (C) 2007 - 2008 by Joshua S. English. This document is the
 * intellectual property of Joshua S. English. All rights reserved.
 *
 * A widget data support structure for the Tree widget.
 *
 * Written by Josh English.
 */

// define package space

package lyra.widgets;

// import external packages

import java.util.Stack;
import java.util.Vector;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Collections;
import java.lang.UnsupportedOperationException;

// define class

public class TreeNode
{
    // define private class constants

	private static final String CLASS_NAME = TreeNode.class.getName();

	// define private classes

	private class DefaultTreeNodeComparator implements Comparator
	{
		public int compare(Object o1, Object o2)
		{
			return String.CASE_INSENSITIVE_ORDER.compare(((TreeNode)o1).name,
					((TreeNode)o2).name);
		}
	}

	private class TreeNodeIterator implements Iterator
	{
		boolean onlyExposedNodes = false;
		private Stack nodeStack = null;

		TreeNodeIterator(TreeNode source, boolean onlyExposedNodes)
		{
			this.onlyExposedNodes = onlyExposedNodes;
			nodeStack = new Stack();

			if(source.getNumberOfChildren() > 0) {
				Object iteratorInfo[] = new Object[2];
				iteratorInfo[0] = source;
				iteratorInfo[1] = new Integer(0);
				nodeStack.push(iteratorInfo);
			}
		}

		public boolean hasNext()
		{
			return (!nodeStack.empty());
		}

		public Object next()
		{
			int childIndex = 0;
			TreeNode parentNode = null;
			TreeNode childNode = null;
			Object iteratorInfo[] = null;

			iteratorInfo = (Object[])nodeStack.peek();
			parentNode = (TreeNode)iteratorInfo[0];
			childIndex = ((Integer)iteratorInfo[1]).intValue();

			if(childIndex < parentNode.getNumberOfChildren()) {
				childNode = parentNode.getChild(childIndex);
				if((childIndex + 1) < parentNode.getNumberOfChildren()) {
					iteratorInfo[1] = new Integer(childIndex + 1);
				}
				else {
					nodeStack.pop();
				}
				
				if((childNode.getNumberOfChildren() > 0) &&
						((onlyExposedNodes == false) || (childNode.isOpen()))) {
					Object childInfo[] = new Object[2];
					childInfo[0] = childNode;
					childInfo[1] = new Integer(0);
					nodeStack.push(childInfo);
				}
			}

			return childNode;
		}

		public void remove() throws UnsupportedOperationException
		{
			throw new UnsupportedOperationException();
		}
	}

	// define private class variables

	private boolean open = false;;
	private int depth = 0;
	private String name = null;
	private Object data = null;
	private TreeNode parent = null;
	private Vector children = null;

	// define public class methods

	public TreeNode(String name, Object data)
	{
		this.name = name;
		this.data = data;

		open = false;
		depth = 0;
		parent = null;
		children = null;
	}

	public String getClassName()
	{
		return CLASS_NAME;
	}

	public boolean isOpen()
	{
		return open;
	}

	public void setOpen(boolean open)
	{
		this.open = open;
	}

	public void toggleOpen()
	{
		if(open) {
			open = false;
		}
		else {
			open = true;
		}
	}

	public String getName()
	{
		return name;
	}
	
	public int getDepth()
	{
		return depth;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Object getData()
	{
		return data;
	}

	public void setData(Object data)
	{
		this.data = data;
	}

	public TreeNode getParent()
	{
		return parent;
	}

	public TreeNode addChild(String name, Object data)
	{
		TreeNode newNode = null;

		newNode = new TreeNode(name, data);
		newNode.parent = this;
		newNode.depth = this.depth + 1;

		if(this.children == null) {
			this.children = new Vector();
		}
		this.children.add(newNode);

		return newNode;
	}

	public void deleteChild(int index)
	{
		if((children != null) && (index < children.size())){
			children.remove(index);
		}
	}

	public void deleteChildren()
	{
		children = null;
	}

	public TreeNode getChild(int index)
	{
		if((children != null) && (index < children.size())){
			return (TreeNode)children.get(index);
		}
		return null;
	}

	public int getNumberOfChildren()
	{
		if(children == null) {
			return 0;
		}
		return children.size();
	}

	public TreeNode[] getChildren()
	{
		return (TreeNode[])children.toArray(new TreeNode[children.size()]);
	}

	public int getTotalExposedElements()
	{
		int ii = 0;
		Iterator iterator = null;

		iterator = this.getOnlyExposedIterator();	
		for(ii = 0; iterator.hasNext(); ii++) {
			iterator.next();
		}

		return ii;
	}

	public int getTotalElements()
	{
		int ii = 0;
		Iterator iterator = null;

		iterator = this.getIterator();
		for(ii = 0; iterator.hasNext(); ii++) {
			iterator.next();
		}

		return ii;
	}

	public Iterator getOnlyExposedIterator()
	{
		return new TreeNodeIterator(this, true);
	}

	public Iterator getIterator()
	{
		return new TreeNodeIterator(this, false);
	}

	public void sort()
	{
		sort(new DefaultTreeNodeComparator());
	}

	public void sort(Comparator comparator)
	{
		int ii = 0;
		TreeNode node = null;

		Collections.sort(children, comparator);
		for(ii = 0; ii < children.size(); ii++) {
			node = (TreeNode)children.get(ii);
			if(node.children != null) {
				node.sort(comparator);
			}
		}
	}
}

