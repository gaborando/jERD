package com.gaboragazzo.jerd.controllers.cell;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;

public class AttributeEdge extends mxCell
{
	private final mxCell keyPort;
	private final mxGraph owner;

	public AttributeEdge(mxGraph owner, mxCell source, AttributeCell attributeCell)
	{
		this.owner = owner;
		setSource(source);
		setTarget(attributeCell);
		setStyle("defaultEdge;endArrow=oval;html=1;strokeColor=#000000;strokeWidth=1;endFill=0;verticalLabelPosition=bottom;verticalAlign=top;selectable=0;");
		setEdge(true);
		mxGeometry geo = new mxGeometry();
		geo.setRelative(true);
		setGeometry(geo);
		getGeometry().setRelative(true);
		owner.addCell(this, owner.getDefaultParent());

		keyPort = (mxCell) owner.insertVertex(this, null, null, -1,-1,1,1,"strokeColor=none;fillColor=none", true);
		/*
		if(geometry!=null)
		{
			getGeometry().setPoints(geometry);
			owner.getModel().setGeometry(this, getGeometry());
		}
		*/
	}

	@Override
	public void setGeometry(mxGeometry geometry)
	{


		//System.out.println("halo");
		super.setGeometry(geometry);
	}


	public mxCell getKeyPort()
	{
		return keyPort;
	}


}
