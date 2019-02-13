package com.gaboragazzo.jerd.utils;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;

public class Constants
{

	public static mxGraphComponent GRAPH_COMPONENT = null;
	private transient static int LAST_ID = 1;

	public static void initConstants()
	{
		mxConstants.ARROW_SPACING = 0;
		mxConstants.ARROW_WIDTH = 30;
		mxConstants.ARROW_SIZE = 10;
	}

	public static int getNewId(){
		return LAST_ID++;
	}
}
