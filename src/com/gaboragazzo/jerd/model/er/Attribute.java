package com.gaboragazzo.jerd.model.er;


import com.gaboragazzo.jerd.utils.Constants;
import com.mxgraph.model.mxGeometry;

import java.io.Serializable;

public class Attribute implements Serializable, Positionable
{
	private String name;
	private Cardinality cardinality = Cardinality.ONE_TO_ONE;
	private Type type;
	private mxGeometry geometry;
	private final Integer id;


	public Attribute(String name, Type type, Cardinality cardinality)
	{
		this.id = Constants.getNewId();
		this.name = name;
		this.type = type;
		this.cardinality = cardinality;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}



	public Type getType()
	{
		return type;
	}

	public void setType(Type type)
	{
		this.type = type;
	}

	@Override
	public String toString()
	{
		return getName();
	}

	public Cardinality getCardinality()
	{
		return cardinality;
	}

	public void setCardinality(Cardinality cardinality)
	{
		this.cardinality = cardinality;
	}


	public void setGeometry(mxGeometry geometry)
	{
		this.geometry = geometry;
	}

	public mxGeometry getGeometry()
	{
		return geometry;
	}

	public Integer getId()
	{
		return id;
	}
}
