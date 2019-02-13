package com.gaboragazzo.jerd.model.er;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ExportFile
{
	public ArrayList<Entity> entities = new ArrayList<>();
	public ArrayList<Relationship> relationships = new ArrayList<>();
	public String timestamp = LocalDateTime.now().toString();
}
