package com.gaboragazzo.jerd.controllers.cell;

import com.gaboragazzo.jerd.model.er.Cardinality;
import com.gaboragazzo.jerd.model.er.RelBound;
import com.gaboragazzo.jerd.model.er.Relationship;
import com.gaboragazzo.jerd.model.er.Type;
import com.gaboragazzo.jerd.utils.LanguageUtil;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import com.gaboragazzo.jerd.controllers.dialogs.EditAttributeDialog;
import com.gaboragazzo.jerd.controllers.dialogs.HookEntityDialog;
import com.gaboragazzo.jerd.controllers.dialogs.SelectDialog;
import com.gaboragazzo.jerd.controllers.dialogs.SetNameDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.security.InvalidParameterException;
import java.util.*;

public class RelationshipCell extends mxCell implements ContextMenuOpenable, Keyable
{

    private HashMap<PORTS, mxCell> avabilePorts = new HashMap<>();
    private HashMap<PORTS, mxCell> usedPorts = new HashMap<>();
    private ArrayList<AttributeCell> attributeCells = new ArrayList<>();
    private ArrayList<BoundEdge> relationEdges = new ArrayList<>();

    @Override
    public mxCell getJoinEdge(EntityCell entityCell)
    {
        for(BoundEdge boundEdge: relationEdges){
            if(boundEdge.getTarget() == entityCell)
                return boundEdge;
        }
        return null;
    }


    public enum PORTS {
        WEST(0, "\uD83E\uDC44"),
        SOUTH (1, "\uD83E\uDC47"),
        NORTH (2, "\uD83E\uDC45"),
        EAST (3, "\uD83E\uDC46 ");

        private int index;
        private String arrow;
        PORTS(int i, String arrow) {
            index = i;
            this.arrow = arrow;
        }

        public int getIndex(){
            return index;
        }


        @Override
        public String toString()
        {
            return arrow;
        }
    }

    private static final String STYLE =
            "" +
                    "shape=rhombus;" +
                    "perimeter=rhombusPerimeter;" +
             //       "perimeter=ellipse;" +
                    "html=1;" +
                    "strokeColor=#000000;" +
                    "fillColor=#FFFFFF;" +
                    "whiteSpace=wrap;";

    private Relationship relationship;
    private transient mxGraph owner;

    private transient JPopupMenu contextMenu = new JPopupMenu();

    public RelationshipCell(mxGraph graph, int x, int y, int width, int height) {
        this.owner = graph;
        setGeometry(new mxGeometry(x,y,width,height));
        this.relationship = new Relationship();
        setStyle(STYLE);
        setVertex(true);
        setConnectable(false);
        graph.addCell(this);




        for (int i = 0; i < 4; i++) {
            mxGeometry geom = new mxGeometry(((i % 2) + (i > 1 ? 1 : 0)) / 2., ((i % 2) + (i > 1 ? 0 : 1)) / 2., 15,8);
            geom.setOffset(new mxPoint(-7.5,-4));
            geom.setRelative(true);
            String style = "editable=0;fillColor=none;strokeColor=none;resizable=0;";
            if(geom.getX() < 0.5)
                style += "labelPosition=left;";
            if(geom.getX() >= 0.5)
                style += "labelPosition=right;";
            if(geom.getY() <= 0.5)
                style += "verticalLabelPosition=top;";
            if(geom.getY() > 0.5)
                style += "verticalLabelPosition=bottom;";
            mxCell port = new mxCell(null, geom, style);
            port.setVertex(true);
            graph.addCell(port, this);
            port.setId("cardinality");
            avabilePorts.put(PORTS.values()[i], port);
        }
        initializeCm();
    }
    public RelationshipCell(mxGraph graph){
        this(graph, 20,20,80,34);
    }

    private void initializeCm()
    {
        JMenuItem setName = new JMenuItem(LanguageUtil.getResourceBundle().getString("set.name"));
        setName.addActionListener(this::setRelationshipNameAction);
        contextMenu.add(setName);

        JMenuItem addAttribute = new JMenuItem(LanguageUtil.getResourceBundle().getString("add.attribute"));
        addAttribute.addActionListener(this::setRelationshipAttributeAction);
        contextMenu.add(addAttribute);

		JMenuItem hookEntity = new JMenuItem(LanguageUtil.getResourceBundle().getString("hook.up.entity"));
		hookEntity.addActionListener(this::hookEntityAction);
		contextMenu.add(hookEntity);

        JMenuItem removeAssociation = new JMenuItem(LanguageUtil.getResourceBundle().getString("remove.association"));
        removeAssociation.addActionListener(this::removeAssociationAction);
        contextMenu.add(removeAssociation);

        JMenuItem remove = new JMenuItem(LanguageUtil.getResourceBundle().getString("remove"));
        remove.addActionListener(this::removeRelationshipAction);
        contextMenu.add(remove);

    }

    private void removeRelationshipAction(ActionEvent actionEvent)
    {
        removeRelationship();
    }

    public void removeRelationship()
    {
        for(BoundEdge cell: new ArrayList<>(relationEdges))
            removeAssociation(cell);
        for(AttributeCell attributeCell: attributeCells)
            removeAttribute(attributeCell);
        removeFromParent();
        owner.refresh();
    }

    private void removeAssociationAction(ActionEvent actionEvent)
    {
        ArrayList<EntityCell> cells = new ArrayList<>();
        for(mxCell cell: relationEdges)
            cells.add((EntityCell) cell.getTarget());
        SelectDialog selectDialog =
                new SelectDialog<>(
                        cells.toArray() ,
                        true);
        selectDialog.setTitle(LanguageUtil.getResourceBundle().getString("select.an.entity"));
        selectDialog.pack();
        selectDialog.setVisible(true);
        List<EntityCell> childs = selectDialog.getSelectedValueList();
        if(selectDialog.isConfirm())
            for(BoundEdge edge: ((ArrayList<BoundEdge>) relationEdges.clone()))
                if(childs.contains(edge.getTarget()))
                    removeAssociation(edge);
    }

    private void hookEntityAction(ActionEvent actionEvent)
	{
		hookEntity();
	}

	private void hookEntity()
	{
		Object allCells[] = owner.getChildVertices(owner.getDefaultParent());
		Object cells[] =  Arrays.stream(allCells)
				.filter(o -> o instanceof EntityCell)
				.toArray();
		HookEntityDialog hookEntityDialog = new HookEntityDialog(cells, avabilePorts.keySet().toArray());
		hookEntityDialog.pack();
		hookEntityDialog.setVisible(visible);
		if(hookEntityDialog.isConfirmed()){
		    addAssociation(hookEntityDialog.getPort(), hookEntityDialog.getCardinality(), hookEntityDialog.getEntity(), null);
        }
	}

	private void setRelationshipNameAction(ActionEvent actionEvent)
    {
        SetNameDialog setNameDialog = new SetNameDialog();
        setNameDialog.setNewName(relationship.getName());
        setNameDialog.pack();
        setNameDialog.setVisible(true);
        if(setNameDialog.isConfirm())
            setRelationshipName(setNameDialog.getNewName());
    }

    public void setRelationshipName(String name){
        relationship.setName(name);
        owner.getModel().setValue(this, relationship.getName());
    }


    private void setRelationshipAttributeAction(ActionEvent actionEvent)
    {
        EditAttributeDialog editAttributeDialog = new EditAttributeDialog();
        editAttributeDialog.setTitle(LanguageUtil.getResourceBundle().getString("new.attribute"));
        editAttributeDialog.pack();
        editAttributeDialog.setVisible(visible);

        if(editAttributeDialog.isConfirm())
            addAttribute(editAttributeDialog.getAttributeName(),
                    editAttributeDialog.getAttributeType(),
                    editAttributeDialog.getAttributeCardinality());
    }

    public void addAttribute(String name, Type type, Cardinality cardinality){
        AttributeCell a = new AttributeCell(owner, this, name, type, cardinality);
        attributeCells.add(a);
        relationship.addAttribute(a.getAttribute());
    }

    public void addAttribute(String name, Type type, Cardinality cardinality, mxGeometry geometry){
        addAttribute(name, type, cardinality, geometry.getX(), geometry.getY(), geometry.getWidth(), geometry.getHeight());

    }

    public void addAttribute(String name, Type type, Cardinality cardinality, double x, double y, double width, double height)
    {
        AttributeCell a = new AttributeCell(owner, this, name, type, cardinality, x, y, width, height);
        attributeCells.add(a);
        relationship.addAttribute(a.getAttribute());
    }

    public void removeAttribute(AttributeCell attributeCell)
    {
        attributeCells.remove(attributeCell);
        relationship.removeAttribute(attributeCell.getAttribute());
    }

    /**
     *
     * @param port 0
     * @param cardinality
     * @param entityCell
     * @param geometry
     * @return
     */
    public void addAssociation(PORTS port, Cardinality cardinality, EntityCell entityCell, List<mxPoint> geometry){
        mxCell p = avabilePorts.remove(port);
        if(owner.getEdges(p).length > 0)
            throw new InvalidParameterException();
        usedPorts.put(port, p);
        owner.getModel().setValue(p, cardinality);
        RelBound bound = relationship.addAssociation(entityCell.getEntity(), cardinality, port);
        BoundEdge boundEdge = new BoundEdge(owner, bound, p, entityCell, geometry);

        relationEdges.add(boundEdge);
        owner.refresh();
        /*
        relationEdges.add ((mxCell) owner.insertEdge(owner.getDefaultParent(), "bound", bound, p, entityCell,
                "endArrow=none;html=1;edgeStyle=elbowEdgeStyle;sourcePerimeterSpacing=-20"
        ));
        */

    }

    public void removeAssociation(BoundEdge cell)
    {

        owner.getModel().setValue(cell.getSource(), null);
        relationship.removeAssociation(((EntityCell) cell.getTarget()).getEntity());
        ArrayList<PORTS> ports =new ArrayList<>(usedPorts.keySet());
        for(PORTS p: ports)
            if(usedPorts.get(p).equals(cell.getSource()))
            {
                avabilePorts.put(p, usedPorts.remove(p));
                avabilePorts.get(p).removeEdge(cell,true );
            }
        cell.removeFromParent();
        relationEdges.remove(cell);


        if(cell.getTarget() instanceof EntityCell)
            ((EntityCell) cell.getTarget()).removeFromPk(this);

        cell.removeFromTerminal(false);
        cell.removeFromTerminal(true);
        owner.refresh();


    }

    @Override
    public void setGeometry(mxGeometry geometry)
    {
        super.setGeometry(geometry);
        if(relationship!=null)
        relationship.setGeometry(geometry);
        if(relationEdges!=null)
        for(BoundEdge relBound: relationEdges){
            if(relBound.getTarget() instanceof EntityCell)
                ((EntityCell) relBound.getTarget()).updatePkPosition(true);
        }
    }

    @Override
    public void openMenu(MouseEvent e)
    {
        contextMenu.show(e.getComponent(), e.getX(), e.getY());
    }

    public Relationship getRelationship()
    {
        return relationship;
    }

    @Override
    public String toString()
    {
        return relationship.getName();
    }
}
