package com.gaboragazzo.jerd.model.er;


import com.gaboragazzo.jerd.utils.Constants;
import com.mxgraph.model.mxGeometry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public abstract class Element implements Serializable, Positionable
{
	private Integer id;
	private String name;
	private ArrayList<Attribute> attributes = new ArrayList<>();
	private mxGeometry geometry;

	public Element(String name)
	{
		this.name = name;
		id = Constants.getNewId();
	}

	public boolean addAttribute(Attribute attribute){
		return attributes.add(attribute);
	}

	public boolean removeAttribute(Attribute attribute){
		return attributes.remove(attribute);
	}

	public List<Attribute> getAttributes(){
		return new ArrayList<Attribute>(attributes);
	}


	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	@Override
	public String toString()
	{
		return getName();
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
