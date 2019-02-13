package com.gaboragazzo.jerd.model.er;


import com.gaboragazzo.jerd.controllers.cell.RelationshipCell;

import java.io.Serializable;

public class RelBound extends Bound implements Serializable
{
	private Cardinality cardinality;
	private RelationshipCell.PORTS port;


	public RelBound(Entity entity, Cardinality cardinality, RelationshipCell.PORTS port)
	{
		super(entity);
		this.cardinality = cardinality;
		this.port = port;
	}



	public Cardinality getCardinality()
	{
		return cardinality;
	}

	public void setCardinality(Cardinality cardinality)
	{
		this.cardinality = cardinality;
	}

	public RelationshipCell.PORTS getPort()
	{
		return port;
	}



}
