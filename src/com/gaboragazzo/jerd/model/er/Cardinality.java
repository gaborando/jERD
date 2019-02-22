package com.gaboragazzo.jerd.model.er;

public enum Cardinality
{
	ZERO_TO_ONE("(0,1)"),
	ZERO_TO_MANY("(0,N)"),
	ONE_TO_ONE("(1,1)"),
	ONE_TO_MANY("(1,N)");

	private String literal;

	Cardinality(String n)
	{
		literal = n;
	}

	public String getLiteral()
	{
		return literal;
	}


	@Override
	public String toString()
	{
		return literal;
	}
}
