package com.gaboragazzo.jerd.model.er;

import com.mxgraph.util.mxPoint;

import java.io.Serializable;
import java.util.List;

public class Bound implements Serializable
{
	private transient Entity entity;
	private Integer entityId;
	private List<mxPoint> points;

	public Bound(Entity entity)
	{
		setEntity(entity);
	}

	public Entity getEntity()
	{
		return entity;
	}

	public void setEntity(Entity entity)
	{
		this.entity = entity;
		this.entityId = entity.getId();
	}

	public Integer getEntityId()
	{
		return entityId;
	}

	public void setEntityId(Integer entityId)
	{
		this.entityId = entityId;
	}

	public List<mxPoint> getPoints()
	{
		return points;
	}

	public void setPoints(List<mxPoint> points)
	{
		this.points = points;
	}

	@Override
	public String toString()
	{
		return "";
	}
}
