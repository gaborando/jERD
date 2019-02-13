package com.gaboragazzo.jerd.controllers.cell;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import com.gaboragazzo.jerd.model.er.Bound;

import java.util.List;

public class BoundEdge extends mxCell
{
	private Bound bound;

	public BoundEdge(mxGraph owner, Bound bound, mxCell p, EntityCell entityCell, List<mxPoint> points)
	{
		this.bound = bound;
		setSource(p);
		setTarget(entityCell);
		setStyle("endArrow=none;html=1;edgeStyle=elbowEdgeStyle;sourcePerimeterSpacing=-20");
		setEdge(true);
		mxGeometry geo = new mxGeometry();
		geo.setRelative(true);
		geo.setPoints(points);
		setGeometry(geo);
		getGeometry().setRelative(true);
		owner.addCell(this, owner.getDefaultParent());
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
		if(geometry!=null)
			bound.setPoints(geometry.getPoints());
		super.setGeometry(geometry);
		if(getTarget() instanceof EntityCell)
			((EntityCell) getTarget()).updatePkPosition(true);
	}


	public Bound getBound()
	{
		return bound;
	}
}
