package com.gaboragazzo.jerd.controllers.cell;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;
import com.gaboragazzo.jerd.model.er.Cardinality;

public class MultivalueAttributeCell extends AttributeCell
{
	public MultivalueAttributeCell(mxGraph graph, mxCell source, String name, Cardinality cardinality)
	{
		super(graph, source, name, null, cardinality);
	}

	public MultivalueAttributeCell(mxGraph graph, mxCell source, String name, Cardinality cardinality, double x, double y, double width, double height)
	{
		super(graph, source, name, null, cardinality, x, y, width, height);
	}
}
