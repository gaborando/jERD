package com.gaboragazzo.jerd.model.er;



import java.util.ArrayList;

public class MultivalueAttribute extends Attribute
{
	private ArrayList<Attribute> attributes = new ArrayList<>();

	public MultivalueAttribute(String name)
	{
		super(name, null,  null);
	}

	public ArrayList<Attribute> getAttributes()
	{
		return attributes;
	}
}
