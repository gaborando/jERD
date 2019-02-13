package com.gaboragazzo.jerd.model.er;

import java.util.ArrayList;

public class ERDiagram
{
	private ArrayList<Entity> entities;
	private ArrayList<Relationship> relationships;
	private ArrayList<Generalization> generalizations;

	public ERDiagram(){
		entities = new ArrayList<>();
		relationships = new ArrayList<>();
		generalizations = new ArrayList<>();
	}

	public ArrayList<Entity> getEntities()
	{
		return entities;
	}

	public ArrayList<Relationship> getRelationships()
	{
		return relationships;
	}

	public void setRelationships(ArrayList<Relationship> relationships)
	{
		this.relationships = relationships;
	}

	public ArrayList<Generalization> getGeneralizations()
	{
		return generalizations;
	}

	public void setGeneralizations(ArrayList<Generalization> generalizations)
	{
		this.generalizations = generalizations;
	}
}
