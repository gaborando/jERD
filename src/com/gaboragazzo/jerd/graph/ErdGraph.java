package com.gaboragazzo.jerd.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.gaboragazzo.jerd.controllers.cell.AttributeEdge;

import java.io.Serializable;
import java.util.Map;

public class ErdGraph extends mxGraph implements Serializable
{
	// Overrides method to disallow edge label editing
	public boolean isCellEditable(Object cell)
	{
		return false;
	}

	@Override
	public boolean isCellSelectable(Object cell) {
		if(cell instanceof mxCell){
			//if (((mxCell) cell).isEdge())
			//	return false;
			if(((mxCell) cell).getId().equals("cardinality"))
				return false;
			if(((mxCell) cell).getStyle().contains("selectable=0"))
				return false;
			if(cell instanceof AttributeEdge)
				return false;
		}
		return super.isCellSelectable(cell);
	}

	@Override
	public boolean isAutoSizeCell(Object cell) {

		mxCellState state = this.getView().getState(cell);
		Map<String, Object> style = state != null ? state.getStyle() : this.getCellStyle(cell);
		return super.isAutoSizeCell(cell) || ((int) style.getOrDefault("autosize", 0)) == 1;
	}

	/*
	@Override
	public String convertValueToString(Object cell)
	{
		if(cell instanceof mxCell){
			Object value = ((mxCell) cell).getValue();
			if(value instanceof Element){
				return ((Element) value).getName();
			}
			if(value instanceof Generalization)
				return "";
		}
		return super.convertValueToString(cell);
	}

	@Override
	public void cellLabelChanged(Object cell, Object newValue,
								 boolean autoSize)
	{
		if (cell instanceof mxCell && newValue != null)
		{
			Object value = ((mxCell) cell).getValue();

			if (value instanceof Element)
			{
				String name = newValue.toString();
				Element elt = (Element) value;
				elt.setName(name);
				newValue = elt;
			}
		}

		super.cellLabelChanged(cell, newValue, autoSize);
	}
	*/



}
