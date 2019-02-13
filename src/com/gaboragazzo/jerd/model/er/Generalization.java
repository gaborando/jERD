package com.gaboragazzo.jerd.model.er;


import com.mxgraph.model.mxGeometry;

import java.io.Serializable;
import java.util.ArrayList;


public class Generalization implements Serializable, Positionable
{

	private ArrayList<Bound> bounds = new ArrayList<>();
	private boolean isExclusive = true;
	private boolean isTotal = true;
	private mxGeometry geometry;

	public Generalization()
	{
	}

	public ArrayList<Bound> getBounds()
	{
		return new ArrayList<>(bounds);
	}

	public boolean isExclusive()
	{
		return isExclusive;
	}

	public void setExclusive(boolean exclusive)
	{
		isExclusive = exclusive;
	}

	public boolean isTotal()
	{
		return isTotal;
	}

	public void setTotal(boolean total)
	{
		isTotal = total;
	}

	public void setGeometry(mxGeometry geometry)
	{
		this.geometry = geometry;
	}

	public Bound addChild(Entity entity)
	{
		Bound bound = new Bound(entity);
		bounds.add(bound);
		return bound;
	}

	public void removeChild(Entity entity)
	{
		for(Bound bound: bounds)
			if(bound.getEntity().equals(entity))
			{
				bounds.remove(bound);
				return;
			}
	}

	public mxGeometry getGeometry()
	{
		return geometry;
	}

	public int[]  getChildrenIds()
	{
		return bounds.stream().mapToInt(x -> x.getEntityId()).toArray();
	}

	public boolean getBoundsContains(Entity entity)
	{
		for(Bound bound: bounds)
			if(bound.getEntity().equals(entity))
			{
				return true;
			}
			return false;
	}
}
