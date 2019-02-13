package com.gaboragazzo.jerd.model.er;

import com.gaboragazzo.jerd.controllers.cell.RelationshipCell;

import java.util.ArrayList;


public class Relationship extends Element
{

	private ArrayList<RelBound> associations = new ArrayList<>();

	public Relationship(){
		super("");

	}

	public RelBound addAssociation(Entity entity, Cardinality cardinality, RelationshipCell.PORTS port){
		RelBound bound = new RelBound(entity, cardinality, port);
		associations.add(bound);
		return bound;
	}

	public boolean removeAssociation(Entity entity){
		for(RelBound bound: associations)
			if(bound.getEntity().equals(entity))
				return associations.remove(bound);
		return false;
	}

	public ArrayList<RelBound> getAssociations(){
		return (ArrayList<RelBound>) associations.clone();
	}
}
