package com.gaboragazzo.jerd.controllers.cell;

import com.gaboragazzo.jerd.model.er.Generalization;
import com.gaboragazzo.jerd.utils.LanguageUtil;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.util.mxPoint;
import com.mxgraph.view.mxGraph;
import com.gaboragazzo.jerd.controllers.dialogs.SelectDialog;
import com.gaboragazzo.jerd.model.er.Bound;
import com.gaboragazzo.jerd.model.er.Entity;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

public class GeneralizationCell extends mxCell implements ContextMenuOpenable
{

    private mxCell parentEdge;
    private mxCell parentPort;
    private ArrayList<mxCell> childEdges = new ArrayList<>();
    private Generalization generalization;
    private static final String STYLE =
            "" +
                    "shape=ellipse;" +
                    "whiteSpace=wrap;" +
                    "html=1;" +
                    "strokeWidth=2;" +
                    "fillColor=none;strokeColor=none;resizable=0;";

    private transient JPopupMenu contextMenu = new JPopupMenu();
    private mxGraph owner;
    private EntityCell parentEntity;

    public GeneralizationCell(mxGraph graph, EntityCell parent, Entity entity, mxGeometry geometry){
        this.parentEntity = parent;
        this.owner = graph;
        this.generalization = entity.addGeneralization();
        setGeometry(geometry);

        setStyle(STYLE);
        setVertex(true);
        setConnectable(false);
        graph.addCell(this);


        mxGeometry geom = new mxGeometry(0.5,0.5, 0,0);
        geom.setRelative(true);
        parentPort = new mxCell(null, geom, "editable=0;fillColor=none");
        parentPort.setVertex(true);
        graph.addCell(parentPort, this);




        parentEdge = (mxCell) graph.insertEdge(
                graph.getDefaultParent(),
                null,
                null,
                parentPort,
                parent,
                "shape=arrow;html=1;strokeWidth=1;strokeColor=#000000;fillColor=#000000");
        initializeCm();
    }

    public GeneralizationCell(mxGraph graph, EntityCell parent, Entity entity){

    this(graph, parent, entity, new mxGeometry(parent.getGeometry().getX() + parent.getGeometry().getWidth()/5,parent.getGeometry().getY()+parent.getGeometry().getHeight()+30,50,50));

    }

    private void initializeCm()
    {
        JCheckBoxMenuItem isTotal = new JCheckBoxMenuItem(LanguageUtil.getResourceBundle().getString("total"));
        isTotal.setSelected(generalization.isTotal());
        isTotal.addActionListener(this::setTotalAction);
        contextMenu.add(isTotal);

        JCheckBoxMenuItem isExclusive = new JCheckBoxMenuItem(LanguageUtil.getResourceBundle().getString("exclusive"));
        isExclusive.setSelected(generalization.isExclusive());
        isExclusive.addActionListener(this::setExclusiveAction);
        contextMenu.add(isExclusive);

        JMenuItem addChild = new JMenuItem(LanguageUtil.getResourceBundle().getString("add.child"));
        addChild.addActionListener(this::addChildAction);
        contextMenu.add(addChild);

        JMenuItem removeChild = new JMenuItem(LanguageUtil.getResourceBundle().getString("remove.child"));
        removeChild.addActionListener(this::removeChildAction);
        contextMenu.add(removeChild);

        JMenuItem remove = new JMenuItem(LanguageUtil.getResourceBundle().getString("remove"));
        remove.addActionListener(this::removeGeneralizationAction);
        contextMenu.add(remove);
    }

    private void removeGeneralizationAction(ActionEvent actionEvent)
    {
        removeGeneralization();
    }

    public void removeGeneralization()
    {
        ArrayList<mxCell> oldEdges = new ArrayList<>(childEdges);
        for(mxCell edge: oldEdges)
            removeChild(edge);
        parentPort.removeFromParent();
        parentEdge.removeFromParent();
        removeFromParent();
        owner.refresh();
        parentEntity.getEntity().removeGeneralization(this.generalization);
    }

    private void removeChildAction(ActionEvent actionEvent)
    {

        ArrayList<EntityCell> cells = new ArrayList<>();
        for(mxCell cell: new ArrayList<>(childEdges))
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
            for(mxCell edge: (ArrayList<mxCell>)childEdges.clone())
                if(childs.contains(edge.getTarget()))
                    removeChild(edge);
    }

    public void removeChild(mxCell edge)
    {
        edge.removeFromParent();
        owner.refresh();
        generalization.removeChild(((EntityCell) edge.getTarget()).getEntity());
        childEdges.remove(edge);
    }

    private void addChildAction(ActionEvent actionEvent)
    {
        Object allCells[] = owner.getChildVertices(owner.getDefaultParent());
        Object cells[] =  Arrays.stream(allCells)
                .filter(o -> o instanceof EntityCell && !o.equals(parentEdge.getTarget()) && !generalization.getBoundsContains(((EntityCell) o).getEntity()))
                .toArray();
        SelectDialog selectDialog =
                new SelectDialog<>(
                              cells ,
                        true);
        selectDialog.setTitle(LanguageUtil.getResourceBundle().getString("select.an.entity"));
        selectDialog.pack();
        selectDialog.setVisible(true);
        List<EntityCell> childs = selectDialog.getSelectedValueList();
        if(selectDialog.isConfirm())
            for(EntityCell child: childs)
                addChild(child, null);
    }

    private void setExclusiveAction(ActionEvent actionEvent)
    {
        setExclusive(((JCheckBoxMenuItem) actionEvent.getSource()).isSelected());
        contextMenu.setVisible(false);
    }

    public void setExclusive(boolean isExclusive)
    {
        generalization.setExclusive(isExclusive);
    }

    private void setTotalAction(ActionEvent actionEvent)
    {
        setTotal(((JCheckBoxMenuItem) actionEvent.getSource()).isSelected());
        contextMenu.setVisible(false);
    }

    public void setTotal(boolean isTotal)
    {
        generalization.setTotal(isTotal);
        owner.getModel().setStyle(parentEdge,"shape=arrow;html=1;strokeWidth=1;strokeColor=#000000;fillColor=#"+(isTotal?"000000":"ffffff") );
    }


    public void addChild(EntityCell child, List<mxPoint> points) {
        Bound bound = generalization.addChild(child.getEntity());
        BoundEdge boundEdge = new BoundEdge(owner, bound, parentPort, child, points);
        childEdges.add(boundEdge);
        owner.refresh();
        /*
        childEdges.add((mxCell) owner.insertEdge(
                owner.getDefaultParent(),
                "generalization_child_edge",
                null,
                parentPort,
                child,
                "endArrow=none;html=1;edgeStyle=elbowEdgeStyle;strokeColor=#000000;"));
                */
    }

    @Override
    public void openMenu(MouseEvent e)
    {
        contextMenu.show(e.getComponent(), e.getX(), e.getY());
    }

	@Override
	public void setGeometry(mxGeometry geometry)
	{
		if(generalization != null)
		generalization.setGeometry(geometry);
		super.setGeometry(geometry);
	}


}
