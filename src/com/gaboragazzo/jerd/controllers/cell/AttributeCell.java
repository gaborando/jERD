package com.gaboragazzo.jerd.controllers.cell;

import com.gaboragazzo.jerd.model.er.Type;
import com.gaboragazzo.jerd.utils.LanguageUtil;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.view.mxGraph;
import com.sun.istack.internal.Nullable;
import com.gaboragazzo.jerd.controllers.dialogs.EditAttributeDialog;
import com.gaboragazzo.jerd.model.er.Attribute;
import com.gaboragazzo.jerd.model.er.Cardinality;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

public class AttributeCell extends mxCell implements ContextMenuOpenable, Keyable
{

    private Attribute attribute;
    private AttributeEdge attributeEdge;
    private mxCell cardinalityLabel;
    private static final String STYLE =
            "" +
                    "text;" +
                    "html=0;" +
                    "strokeColor=none;" +
                    "fillColor=none;" +
                    "align=center;" +
                    "verticalAlign=middle;" +
                    "whiteSpace=wrap;" +
                    "rounded=0;" +
                    "fontColor=#000000;" +
                    "resizable=1;" +
                    "startSize=0;" +
                    "spacing=0;" +
                    "autosize=0;";

    private transient mxGraph owner;

    //GUI FIELDS
    private transient JPopupMenu contextMenu = new JPopupMenu();


    public AttributeCell(mxGraph graph, mxCell source, String name, Type type, Cardinality cardinality)
    {
        this(graph, source, name, type, cardinality, source.getGeometry().getX(),source.getGeometry().getY() - 35,name.length()*9,15);

    }

    public AttributeCell(mxGraph graph, mxCell source, String name, Type type, Cardinality cardinality, double x, double y, double width, double height)
    {
        owner = graph;
        this.attribute = new Attribute(name, type, cardinality);
        setValue(attribute.getName());
        setStyle(STYLE);
        setVertex(true);
        setConnectable(false);
        graph.getModel().beginUpdate();
        graph.addCell(this);

        attributeEdge = new AttributeEdge(owner, source, this);
        setGeometry(new mxGeometry(x,y,width, height));

        setAttributeCardinality(attribute.getCardinality());
       // graph.updateCellSize(this);
        graph.getModel().endUpdate();
        initializeCm();
    }

	private void initializeCm()
    {
        JMenuItem setName = new JMenuItem(LanguageUtil.getResourceBundle().getString("edit"));
        setName.addActionListener(this::editAttributeAction);
        contextMenu.add(setName);

        JMenuItem remove = new JMenuItem(LanguageUtil.getResourceBundle().getString("remove"));
        remove.addActionListener(this::removeAttributeAction);
        contextMenu.add(remove);
    }

    private void removeAttributeAction(ActionEvent actionEvent)
    {
        removeAttribute();
    }

    public void removeAttribute()
    {
        if(attributeEdge.getSource() instanceof EntityCell)
            ((EntityCell)attributeEdge.getSource()).removeAttribute(this);

        if(attributeEdge.getSource() instanceof RelationshipCell)
            ((RelationshipCell)attributeEdge.getSource()).removeAttribute(this);

    }

    public void removeFromView(){
        owner.getModel().beginUpdate();
        removeFromParent();
        attributeEdge.removeFromParent();
        attributeEdge.removeFromTerminal(true);
        attributeEdge.removeFromTerminal(false);
        owner.getModel().endUpdate();
        owner.refresh();
    }

    private void editAttributeAction(ActionEvent actionEvent)
    {
        EditAttributeDialog editAttributeDialog = new EditAttributeDialog();
        editAttributeDialog.setTitle(LanguageUtil.getResourceBundle().getString("edit.attribute"));
        editAttributeDialog.pack();
        editAttributeDialog.initializeData(attribute.getName(), attribute.getType(), attribute.getCardinality());
        editAttributeDialog.setVisible(true);
        if(editAttributeDialog.isConfirm())
            editAttribute(editAttributeDialog.getAttributeName(), editAttributeDialog.getAttributeType(), editAttributeDialog.getAttributeCardinality());
    }

    public void editAttribute(String attributeName, Type attributeType, Cardinality attributeCardinality)
    {
        setAttributeName(attributeName);
        setAttributeType(attributeType);
        setAttributeCardinality(attributeCardinality);
    }

    public void setAttributeCardinality(Cardinality attributeCardinality)
    {
        owner.getModel().beginUpdate();
        if(cardinalityLabel!=null)
            cardinalityLabel.removeFromParent();
        cardinalityLabel = (mxCell) owner.insertVertex(
                attributeEdge,
                "cardinality",
                attributeCardinality!=Cardinality.ONE_TO_ONE?attributeCardinality:"",
                0.5,0,0,0,
                "fontSize=10;fontColor=#000000;fillColor=none;strokeColor=none;rounded=0;",
                true
        );
        attribute.setCardinality(attributeCardinality);
        owner.getModel().endUpdate();
        owner.refresh();
    }

    public void setAttributeType(Type attributeType)
    {
        attribute.setType(attributeType);
    }

    public void setAttributeName(String name)
    {
        attribute.setName(name);
        owner.getModel().setValue(this, attribute.getName());
        geometry.setWidth(name.length()*9);
        owner.getModel().setGeometry(this, getGeometry());

    }

    @Override
    public void setGeometry(mxGeometry geometry)
    {
        super.setGeometry(geometry);
        if(attribute!=null)
            attribute.setGeometry(geometry);

        if(attributeEdge!=null && attributeEdge.getSource() instanceof EntityCell)
        ((EntityCell) attributeEdge.getSource()).updatePkPosition(true);


    }



    @Override
    public void openMenu(MouseEvent e)
    {
        contextMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    public Attribute getAttribute()
    {
        return attribute;
    }

    public mxCell getKeyPort()
    {
        return attributeEdge.getKeyPort();
    }

    @Override
    public String toString()
    {
        return attribute.getName();
    }

    public void setAsPrimaryKey()
    {

        owner.getModel().setStyle(attributeEdge, "defaultEdge;endArrow=oval;html=1;strokeColor=#000000;strokeWidth=1;endFill=1;verticalLabelPosition=bottom;verticalAlign=top;");
    }

    public void revokePrimaryKey()
    {
        owner.getModel().setStyle(attributeEdge, "defaultEdge;endArrow=oval;html=1;strokeColor=#000000;strokeWidth=1;endFill=0;verticalLabelPosition=bottom;verticalAlign=top;");
    }

    public AttributeEdge getAttributeEdge()
    {
        return attributeEdge;
    }

    @Override
    public mxCell getJoinEdge(@Nullable EntityCell entityCell)
    {
        return getAttributeEdge();
    }
}
