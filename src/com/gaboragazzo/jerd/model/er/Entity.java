package com.gaboragazzo.jerd.model.er;


import java.io.Serializable;
import java.util.ArrayList;

public class Entity extends Element implements Serializable
{


	private ArrayList<Generalization> generalizations;
	private ArrayList<Integer> primaryKey;


	public Entity()
	{
		super("Entity");
		generalizations = new ArrayList<>();
	}


	public Generalization addGeneralization()
	{
		Generalization generalization = new Generalization();
		generalizations.add(generalization);
		return generalization;
	}

	public ArrayList<Generalization> getGeneralizations()
	{
		return generalizations;
	}

	public void removeGeneralization(Generalization generalization)
	{
		generalizations.remove(generalization);

	}


	public void setPrimaryKey(ArrayList<Integer> pk)
	{
		primaryKey = pk;

	}

	public ArrayList<Integer> getPrimaryKey()
	{
		return primaryKey;
	}

}
