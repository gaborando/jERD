package com.gaboragazzo.jerd.controllers.cell;

import com.gaboragazzo.jerd.model.er.Cardinality;
import com.gaboragazzo.jerd.model.er.Entity;
import com.gaboragazzo.jerd.model.er.Type;
import com.gaboragazzo.jerd.utils.LanguageUtil;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxICell;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.gaboragazzo.jerd.controllers.dialogs.EditAttributeDialog;
import com.gaboragazzo.jerd.controllers.dialogs.SelectDialog;
import com.gaboragazzo.jerd.controllers.dialogs.SetNameDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EntityCell extends mxCell implements ContextMenuOpenable
{


	private Entity entity;
	private ArrayList<AttributeCell> attributeCells = new ArrayList<>();
	private static final String STYLE =
			"" +
					"rounded=0;" +
					"whiteSpace=wrap;" +
					"html=1;" +
					"fillColor=#FFFFFF;" +
					"strokeColor=#000000;";

	//GUI FIELDS
	private transient JPopupMenu contextMenu = new JPopupMenu();

	private transient mxGraph owner;
	private ArrayList<GeneralizationCell> generalizationCells = new ArrayList<>();
	private List<Keyable> pkList;
	private mxCell pkEdge;


	public EntityCell(mxGraph graph, double x, double y, double width, double height)
	{
		super();
		owner = graph;
		entity = new Entity();
		setValue(entity.getName());
		setId("entity");
		setStyle(STYLE);
		setVertex(true);
		setConnectable(false);
		graph.addCell(this);
		graph.getModel().setGeometry(this, new mxGeometry(x, y, width, height));
		initializeCm();
		pkEdge = new mxCell();
		pkEdge.setEdge(true);
		pkEdge.setStyle("endArrow=oval;html=1;strokeColor=#000000;strokeWidth=1;endFill=1;verticalLabelPosition=bottom;verticalAlign=top;edgeStyle=orthogonalEdgeStyle;selectable=0;jumpStyle=arc;rounded=1");
		owner.addCell(pkEdge, owner.getDefaultParent());
	}

	public EntityCell(mxGraph graph)
	{
		this(graph, 20, 20, 80, 34);
	}

	private void initializeCm()
	{
		JMenuItem setName = new JMenuItem(LanguageUtil.getResourceBundle().getString("set.name"));
		setName.addActionListener(this::setEntityNameAction);
		contextMenu.add(setName);

		JMenuItem addAttribute = new JMenuItem(LanguageUtil.getResourceBundle().getString("add.attribute"));
		addAttribute.addActionListener(this::addAttributeAction);
		contextMenu.add(addAttribute);

		JMenuItem addGeneralization = new JMenuItem(LanguageUtil.getResourceBundle().getString("add.generalization"));
		addGeneralization.addActionListener(this::addGeneralizationAction);
		contextMenu.add(addGeneralization);

		JMenuItem setPrimaryKey = new JMenuItem(LanguageUtil.getResourceBundle().getString("set.primary.key"));
		setPrimaryKey.addActionListener(this::setPrimaryKeyAction);
		contextMenu.add(setPrimaryKey);

		JMenuItem remove = new JMenuItem(LanguageUtil.getResourceBundle().getString("remove"));
		remove.addActionListener(this::removeEntityAction);
		contextMenu.add(remove);


	}

	private void setPrimaryKeyAction(ActionEvent actionEvent)
	{
		ArrayList<Keyable> avabileKeys = new ArrayList<>(attributeCells);
		for(int i = 0; i<getEdgeCount(); i++){
			mxICell edge = getEdgeAt(i);
			if(edge instanceof BoundEdge)
			{
				/*
				Bound bound = ((BoundEdge) edge).getBound();
				if(bound instanceof RelBound)
					if(((RelBound) bound).getCardinality().equals(Cardinality.ONE_TO_ONE))
						avabileKeys.add((RelationshipCell) ((BoundEdge) edge).getSource());

						*/
				avabileKeys.add((RelationshipCell) ((BoundEdge) edge).getSource().getParent());
			}

		}
		SelectDialog<Keyable> selectDialog = new SelectDialog<>(avabileKeys.toArray(new Keyable[]{}), true);
		selectDialog.setTitle(LanguageUtil.getResourceBundle().getString("select.attributes.or.associations"));
		selectDialog.pack();
		selectDialog.setVisible(true);
		if (selectDialog.isConfirm())
		{
			setPrimaryKey(selectDialog.getSelectedValueList());
		}

	}

	public void setPrimaryKey(List<Keyable> selectedValueList)
	{
		pkList = selectedValueList;
		entity.setPrimaryKey(new ArrayList<>());
		for (Keyable k : selectedValueList)
		{
			if (k instanceof AttributeCell)
				entity.getPrimaryKey().add(((AttributeCell) k).getAttribute().getId());
			else if (k instanceof RelationshipCell)
				entity.getPrimaryKey().add(((RelationshipCell) k).getRelationship().getId());
		}
		updatePkPosition(true);
	}

	public synchronized void updatePkPosition(boolean redo)
	{

		if (pkEdge == null || geometry == null || attributeCells == null)
			return;
		for (AttributeCell attributeCell : attributeCells)
		{
			attributeCell.revokePrimaryKey();
			owner.getModel().setGeometry(pkEdge, new mxGeometry());
		}
		if (pkList == null || pkList.isEmpty())
			return;
		if (pkList.size() == 1 && pkList.get(0) instanceof AttributeCell)
		{
				((AttributeCell) pkList.get(0)).setAsPrimaryKey();
		} else
		{

			mxGeometry border = new mxGeometry(geometry.getX() - 10, geometry.getY() - 10, geometry.getWidth() + 20, geometry.getHeight() + 20);
			List<mxPoint> points = new ArrayList<>();
			for (int i = 0; i < pkList.size(); i++)
			{
				mxCellState state = null;
				try
				{
					state = owner.getView().getState(pkList.get(i).getJoinEdge(this));
				} catch (Exception ignored)
				{
				}
				if (state == null || state.getAbsolutePointCount() < 2)
					return;
				mxPoint sourcePoint = state.getAbsolutePoint(state.getAbsolutePointCount()-2);
				mxPoint targetPoint = state.getAbsolutePoint(state.getAbsolutePointCount()-1);
				mxPoint intersection = border.intersectLine(sourcePoint.getX(), sourcePoint.getY(), targetPoint.getX(), targetPoint.getY());
				if (intersection != null)
				{
					points.add(intersection);

				}
			}

			if(points.isEmpty())
				return;


			Comparator<mxPoint> comparator = new Comparator<mxPoint>()
			{
				@Override
				public int compare(mxPoint a, mxPoint b)
				{


					if (isless(a, b)) return -1;
					else return 1;
				}

				public boolean isless(mxPoint a, mxPoint b)
				{

					//return (int) -((a.getX() - border.getCenterX()) * (b.getY() - border.getCenterY()) - (b.getX() - border.getCenterX()) * (a.getY() - border.getCenterY())*100);
					if (a.getX() - border.getCenterX() >= 0 && b.getX() - border.getCenterX() < 0)
						return true;
					if (a.getX() - border.getCenterX() < 0 && b.getX() - border.getCenterX() >= 0)
						return false;
					if (a.getX() - border.getCenterX() == 0 && b.getX() - border.getCenterX() == 0)
					{
						if (a.getY() - border.getCenterY() >= 0 || b.getY() - border.getCenterY() >= 0)
							return a.getY() > b.getY();
						return b.getY() > a.getY();
					}

					// compute the cross product of vectors (center -> a) x (center -> b)
					double det = (a.getX() - border.getCenterX()) * (b.getY() - border.getCenterY()) - (b.getX() - border.getCenterX()) * (a.getY() - border.getCenterY());
					if (det < 0)
						return true;
					if (det > 0)
						return false;

					// points a and b are on the same line from the center
					// check which point is closer to the center
					double d1 = (a.getX() - border.getCenterX()) * (a.getX() - border.getCenterX()) + (a.getY() - border.getCenterY()) * (a.getY() - border.getCenterY());
					double d2 = (b.getX() - border.getCenterX()) * (b.getX() - border.getCenterX()) + (b.getY() - border.getCenterY() * (b.getY() - border.getCenterY()));
					return d1 > d2;
				}
			};

			if(points.size()>1)
			{
				List<mxPoint> rectCoords = new ArrayList<>();
				rectCoords.add(new mxPoint(border.getX(), border.getY()));
				rectCoords.add(new mxPoint(border.getX(), border.getY() + border.getHeight()));
				rectCoords.add(new mxPoint(border.getX() + border.getWidth(), border.getY() + border.getHeight()));
				rectCoords.add(new mxPoint(border.getX() + border.getWidth(), border.getY()));
				List<mxPoint> toAdd = new ArrayList<>();
				for (int i = 1; i < points.size(); i++)
				{
					for (mxPoint point : rectCoords)
					{
						if (comparator.compare(points.get(i - 1), point) < 0 && comparator.compare(point, points.get(i)) < 0)
							toAdd.add(point);

					}
				}
				for (mxPoint point : rectCoords)
				{
					if (comparator.compare(points.get(points.size() - 1), point) < 0 && comparator.compare(point, points.get(0)) < 0)
						toAdd.add(point);

				}
				points.addAll(toAdd);
			}

			points.sort(comparator);
			mxPoint sPoint = points.get(0);
			//mxRectangle rectangle = new mxRectangle(sPoint.getX()-5,sPoint.getY()-5,sPoint.getX()+5, sPoint.getY()+5);

			/*
			if(border.contains(sPoint.getX(), sPoint.getY()-5))
				points.add(0,new mxPoint(sPoint.getX(), sPoint.getY()-5));
			if(border.contains(sPoint.getX(), sPoint.getY()+5))
				points.add(0,new mxPoint(sPoint.getX(), sPoint.getY()+5));
			if(border.contains(sPoint.getX()-5, sPoint.getY()))
				points.add(0,new mxPoint(sPoint.getX()-5, sPoint.getY()));
			if(border.contains(sPoint.getX()+5, sPoint.getY()))
				points.add(0,new mxPoint(sPoint.getX()+5, sPoint.getY()));
				*/
			ArrayList<mxPoint> temp = new ArrayList<mxPoint>(){
				@Override
				public boolean add(mxPoint o)
				{
					if(o == null)
						return false;
					return super.add(o);
				}
			};
			temp.add(border.intersectLine(sPoint.getX() - 10, sPoint.getY() - 10, sPoint.getX() - 10, sPoint.getY() + 10));
			temp.add(border.intersectLine(sPoint.getX() + 10, sPoint.getY() + 10, sPoint.getX() + 10, sPoint.getY() - 10));
			temp.add(border.intersectLine(sPoint.getX() - 10, sPoint.getY() - 10, sPoint.getX() + 10, sPoint.getY() - 10));
			temp.add(border.intersectLine(sPoint.getX() + 10, sPoint.getY() + 10, sPoint.getX() - 10, sPoint.getY() + 10));
			temp.sort(comparator);
			points.add(0,temp.remove(0));
			points.add(0,temp.remove(0));

			sPoint = points.get(points.size()-1);
			temp.clear();
			temp.add(border.intersectLine(sPoint.getX() - 10, sPoint.getY() - 10, sPoint.getX() - 10, sPoint.getY() + 10));
			temp.add(border.intersectLine(sPoint.getX() + 10, sPoint.getY() + 10, sPoint.getX() + 10, sPoint.getY() - 10));
			temp.add(border.intersectLine(sPoint.getX() - 10, sPoint.getY() - 10, sPoint.getX() + 10, sPoint.getY() - 10));
			temp.add(border.intersectLine(sPoint.getX() + 10, sPoint.getY() + 10, sPoint.getX() - 10, sPoint.getY() + 10));
			temp.sort(comparator);
			points.add(temp.remove(0));
			points.add(temp.remove(0));




			owner.getModel().beginUpdate();
			mxGeometry geometry = new mxGeometry();
			geometry.setSourcePoint(points.remove(0));
			geometry.setTargetPoint(points.remove(points.size() - 1));
			geometry.setPoints(points);
			owner.getModel().setGeometry(pkEdge, geometry);
			owner.getModel().endUpdate();
			owner.refresh();

		}

		if (redo)
			updatePkPosition(false);


	}

	@Override
	public mxGeometry getGeometry()
	{
		//updatePkPosition(false);
		return super.getGeometry();
	}

	private void removeEntityAction(ActionEvent actionEvent)
	{
		removeEntity();
	}

	private void removeEntity()
	{
		for(GeneralizationCell generalizationCell: generalizationCells)
			generalizationCell.removeGeneralization();
		for (AttributeCell attributeCell : (ArrayList<AttributeCell>) attributeCells.clone())
			removeAttribute(attributeCell);

		ArrayList<mxCell> edges = new ArrayList<>();
		for (int i = 0; i < getEdgeCount(); i++)
			edges.add((mxCell) getEdgeAt(i));
		for (mxCell edge : edges)
		{
			if (edge.getSource().getParent() instanceof GeneralizationCell)
				((GeneralizationCell) edge.getSource().getParent()).removeChild(edge);

		}
		removeFromParent();

		owner.refresh();
	}

	private void addGeneralizationAction(ActionEvent actionEvent)
	{
		addGeneralization();
	}

	public GeneralizationCell addGeneralization()
	{
		GeneralizationCell generalizationCell = new GeneralizationCell(owner, this, entity);
		generalizationCells.add(generalizationCell);
		return  generalizationCell;
	}

	public GeneralizationCell addGeneralization(mxGeometry geometry)
	{
		GeneralizationCell generalizationCell = new GeneralizationCell(owner, this, entity, geometry);
		generalizationCells.add(generalizationCell);
		return  generalizationCell;
	}

	private void addAttributeAction(ActionEvent actionEvent)
	{
		EditAttributeDialog editAttributeDialog = new EditAttributeDialog();
		editAttributeDialog.setTitle(LanguageUtil.getResourceBundle().getString("new.attribute"));
		editAttributeDialog.pack();
		editAttributeDialog.setVisible(visible);

		if (editAttributeDialog.isConfirm())
			addAttribute(editAttributeDialog.getAttributeName(),
					editAttributeDialog.getAttributeType(),
					editAttributeDialog.getAttributeCardinality());
	}

	private void setEntityNameAction(ActionEvent actionEvent)
	{

		SetNameDialog setNameDialog = new SetNameDialog();
		setNameDialog.setNewName(entity.getName());
		setNameDialog.pack();
		setNameDialog.setVisible(true);
		if (setNameDialog.isConfirm())
			setEntityName(setNameDialog.getNewName());

	}

	public AttributeCell addAttribute(String name, Type type, Cardinality cardinality)
	{
		AttributeCell a = new AttributeCell(owner, this, name, type, cardinality);
		attributeCells.add(a);
		entity.addAttribute(a.getAttribute());
		owner.refresh();
		return a;
	}

	public AttributeCell addAttribute(String name, Type type, Cardinality cardinality, mxGeometry geometry)
	{
		return addAttribute(name, type, cardinality, geometry.getX(), geometry.getY(), geometry.getWidth(), geometry.getHeight());

	}

	public AttributeCell addAttribute(String name, Type type, Cardinality cardinality, double x, double y, double width, double height)
	{
		AttributeCell a = new AttributeCell(owner, this, name, type, cardinality, x, y, width, height);
		attributeCells.add(a);
		entity.addAttribute(a.getAttribute());
		return a;
	}

	public void removeAttribute(AttributeCell attributeCell)
	{
		attributeCells.remove(attributeCell);
		entity.removeAttribute(attributeCell.getAttribute());
		removeFromPk(attributeCell);
	}

	public void setEntityName(String name)
	{
		entity.setName(name);
		owner.getModel().setValue(this, entity.getName());
	}

	@Override
	public void setGeometry(mxGeometry geometry)
	{
		if (entity != null)
			entity.setGeometry(geometry);
		super.setGeometry(geometry);
		updatePkPosition(true);
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		EntityCell entityCell = (EntityCell) super.clone();
		entityCell.updatePkPosition(true);
		return entityCell;
	}

	public mxCell getCell()
	{
		return this;
	}

	@Override
	public void openMenu(MouseEvent e)
	{
		contextMenu.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public String toString()
	{
		return getValue().toString();
	}

	public Entity getEntity()
	{
		return entity;
	}


	public ArrayList<GeneralizationCell> getGeneralizations()
	{
		return generalizationCells;
	}

	public void removeFromPk(Keyable keyable){
		pkList.remove(keyable);
		setPrimaryKey(pkList);
	}
}
