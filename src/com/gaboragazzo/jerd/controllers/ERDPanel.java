package com.gaboragazzo.jerd.controllers;

import com.gaboragazzo.jerd.controllers.cell.*;
import com.gaboragazzo.jerd.model.er.*;
import com.gaboragazzo.jerd.utils.LanguageUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.handler.mxRubberband;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.gaboragazzo.jerd.graph.ErdGraph;
import com.gaboragazzo.jerd.utils.Constants;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

public class ERDPanel {
    private JButton entityButton;
    private JButton relationshipButton;
    private JPanel mainPane;
    private ErdGraph graph;
    private mxGraphComponent graphComponent;
    private JButton newButton;
    private JButton openButton;
    private JButton saveButton;
    private JButton saveAsButton;
    private JLabel fileStats;
    private JLabel info;
    private JButton saveImage;
    private JComboBox sizeCombo;
    private JMenu menu;
    private final JFileChooser fc;

    private File currentFIle = null;
    private boolean modified = false;

    public ERDPanel() {
        entityButton.addActionListener(this::createEntity);
        relationshipButton.addActionListener(this::createRelationship);

        saveButton.addActionListener(e ->
                save(false)
        );

        saveAsButton.addActionListener(e -> save(true));

        newButton.addActionListener(e -> newDiagram());

        openButton.addActionListener(e -> openDiagram());


        Object parent = graph.getDefaultParent();

        fc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        FileNameExtensionFilter filter = new FileNameExtensionFilter(LanguageUtil.getResourceBundle().getString("jerd.descriptor"), LanguageUtil.getResourceBundle().getString("file.extention"));
        fc.setFileFilter(filter);


        //mainPane.add(graphComponent);
        saveImage.addActionListener(e -> saveAsImage());
    }

    private void saveAsImage() {

        BufferedImage image = mxCellRenderer.createBufferedImage(graph, null, 1, Color.WHITE, true, null);
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        jfc.setDialogTitle(LanguageUtil.getResourceBundle().getString("choose.a.directory.to.save.your.file"));
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("PNG", "png");
        jfc.setFileFilter(filter);
        int returnValue = jfc.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            currentFIle = fc.getSelectedFile();
            if (!currentFIle.getName().endsWith(".png")) {
                currentFIle = new File(currentFIle.getParentFile(), currentFIle.getName() + ".png");
            }
            try {
                ImageIO.write(image, "PNG", currentFIle);
                JOptionPane.showMessageDialog(mainPane, LanguageUtil.getResourceBundle().getString("file.saved.successfully"), LanguageUtil.getResourceBundle().getString("info"), JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(mainPane, e, e.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openDiagram() {
        if (!modified || JOptionPane.showConfirmDialog(mainPane, LanguageUtil.getResourceBundle().getString("lose.changes")) == JOptionPane.YES_OPTION) {
            int returnVal = fc.showOpenDialog(mainPane.getParent());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File toLoad = fc.getSelectedFile();
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setPrettyPrinting();
                Gson gson = gsonBuilder.create();
                try {
                    FileReader fileReader = new FileReader(toLoad);
                    ExportFile exportFile = gson.fromJson(fileReader, ExportFile.class);
                    fileReader.close();
                    mxCell root = new mxCell();
                    root.insert(new mxCell());
                    graph.getModel().setRoot(root);
                    HashMap<Integer, EntityCell> entityMap = new HashMap<>();
                    HashMap<Integer, Keyable> keyableMap = new HashMap<>();
                    for (Entity entity : exportFile.entities.stream().filter(x -> x.getGeneralizations().isEmpty()).collect(Collectors.toList())) {
                        EntityCell cell = new EntityCell(graph);
                        cell.setEntityName(entity.getName());
                        cell.setGeometry(entity.getGeometry());
                        entityMap.put(entity.getId(), cell);

                        for (Attribute attribute : entity.getAttributes()) {
                            AttributeCell attributeCell = cell.addAttribute(attribute.getName(), attribute.getType(), attribute.getCardinality(), attribute.getGeometry());
                            keyableMap.put(attribute.getId(), attributeCell);
                        }
                    }

                    for (Entity entity : exportFile.entities.stream().filter(x -> !x.getGeneralizations().isEmpty()).collect(Collectors.toList())) {
                        EntityCell cell = new EntityCell(graph);
                        cell.setEntityName(entity.getName());
                        cell.setGeometry(entity.getGeometry());

                        for (Attribute attribute : entity.getAttributes()) {
                            AttributeCell attributeCell = cell.addAttribute(attribute.getName(), attribute.getType(), attribute.getCardinality(), attribute.getGeometry());
                            keyableMap.put(attribute.getId(), attributeCell);
                        }

                        for (Generalization generalization : entity.getGeneralizations()) {
                            GeneralizationCell generalizationCell = cell.addGeneralization(generalization.getGeometry());
                            generalizationCell.setExclusive(generalization.isExclusive());
                            generalizationCell.setTotal(generalization.isTotal());


                            for (Bound child : generalization.getBounds())
                                generalizationCell.addChild(entityMap.get(child.getEntityId()), child.getPoints());
                        }

                        entityMap.put(entity.getId(), cell);
                    }

                    for (Relationship relationship : exportFile.relationships) {
                        RelationshipCell relationshipCell = new RelationshipCell(graph);
                        relationshipCell.setRelationshipName(relationship.getName());
                        relationshipCell.setGeometry(relationship.getGeometry());

                        for (Attribute attribute : relationship.getAttributes()) {
                            relationshipCell.addAttribute(attribute.getName(), attribute.getType(), attribute.getCardinality(), attribute.getGeometry());
                        }

                        for (RelBound bound : relationship.getAssociations()) {
                            relationshipCell.addAssociation(bound.getPort(), bound.getCardinality(), entityMap.get(bound.getEntityId()), bound.getPoints());
                        }

                        keyableMap.put(relationship.getId(), relationshipCell);
                    }

                    for (Entity entity : exportFile.entities) {
                        if (entity.getPrimaryKey() == null)
                            continue;
                        ArrayList<Keyable> pk = new ArrayList<>();
                        for (Integer integer : entity.getPrimaryKey()) {
                            pk.add(keyableMap.get(integer));
                        }
                        entityMap.get(entity.getId()).setPrimaryKey(pk);
                    }


                    currentFIle = toLoad;
                    setModified(false);
                    graph.refresh();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(mainPane, e, e.getMessage(), JOptionPane.ERROR_MESSAGE);
                }

            }
        }
    }

    private void newDiagram() {
        if (!modified || JOptionPane.showConfirmDialog(mainPane, LanguageUtil.getResourceBundle().getString("lose.changes")) == JOptionPane.YES_OPTION) {

            // Check modified flag and display save dialog
            mxCell root = new mxCell();
            root.insert(new mxCell());
            graph.getModel().setRoot(root);


            currentFIle = null;
            setModified(false);
        }

    }

    private void save(boolean saveAs) {

        if (saveAs || currentFIle == null) {
            int returnVal = fc.showOpenDialog(mainPane.getParent());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                currentFIle = fc.getSelectedFile();
                if (!currentFIle.getName().endsWith(LanguageUtil.getResourceBundle().getString("file.extention"))) {
                    currentFIle = new File(currentFIle.getParentFile(), currentFIle.getName() + LanguageUtil.getResourceBundle().getString("file.extention"));
                    //file.createNewFile();
                }
            }
        }
        if (currentFIle != null) {
            ExportFile exportFile = new ExportFile();
            for (Object o : graph.getChildCells(graph.getDefaultParent(), true, false)) {
                if (o instanceof EntityCell) {
                    exportFile.entities.add(((EntityCell) o).getEntity());
                } else if (o instanceof RelationshipCell)
                    exportFile.relationships.add(((RelationshipCell) o).getRelationship());
            }
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setPrettyPrinting();
            Gson gson = gsonBuilder.create();
            try {
                if (!currentFIle.exists())
                    currentFIle.createNewFile();
                FileWriter fileWriter = new FileWriter(currentFIle);
                String json = gson.toJson(exportFile);
                fileWriter.write(json);
                fileWriter.close();
                setModified(false);
                if (saveAs)
                    JOptionPane.showMessageDialog(mainPane, LanguageUtil.getResourceBundle().getString("file.saved.successfully"), LanguageUtil.getResourceBundle().getString("info"), JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(mainPane, e, e.getMessage(), JOptionPane.ERROR_MESSAGE);
            }
        }

    }


    private void createRelationship(ActionEvent actionEvent) {
        new RelationshipCell(graph);
    }

    private void createEntity(ActionEvent actionEvent) {

        new EntityCell(graph);
    }


    private void createUIComponents() {
        graph = new ErdGraph();
        graphComponent = new mxGraphComponent(graph);
        Constants.GRAPH_COMPONENT = graphComponent;
        graphComponent.setBackground(Color.WHITE);
        graphComponent.setOpaque(false);
        graphComponent.setConnectable(false);
        graph.setEdgeLabelsMovable(false);
        graphComponent.setFoldingEnabled(false);
        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mxCell cell = (mxCell) graphComponent.getCellAt(e.getX(), e.getY());
                //System.out.println(cell);
                if (e.getButton() == MouseEvent.BUTTON3 && cell != null) {
                    if (cell instanceof ContextMenuOpenable)
                        ((ContextMenuOpenable) cell).openMenu(e);
                    else if (cell.isEdge() && cell.getSource().getParent() instanceof GeneralizationCell)
                        ((GeneralizationCell) cell.getSource().getParent()).openMenu(e);
                }
            }
        });
        graph.setAllowDanglingEdges(false);
        graph.setCellsDisconnectable(false);
        //graph.setCellsBendable(false);
        mxRubberband rubberband = new mxRubberband(graphComponent);
        menu = new JMenu();
        graph.getModel().addListener(mxEvent.CHANGE, (s, e) -> setModified(true));
        graph.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
        graph.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_STROKECOLOR, "#000000");
        graph.getStylesheet().getDefaultEdgeStyle().put(mxConstants.STYLE_ROUNDED, 1);
        graph.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_FONTCOLOR, "#000000");
        graph.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_FILLCOLOR, "#FFFFFF");
        graph.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_STROKECOLOR, "#000000");
        graph.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_FONTCOLOR, "#000000");

        sizeCombo = new JComboBox(new Object[]{"6pt", "8pt",
                "9pt", "10pt", "12pt", "14pt", "18pt", "24pt", "30pt", "36pt",
                "48pt", "60pt"});
        sizeCombo.setEditable(true);
        sizeCombo.setMinimumSize(new Dimension(65, 0));
        sizeCombo.setPreferredSize(new Dimension(65, 0));
        sizeCombo.setMaximumSize(new Dimension(65, 100));

        sizeCombo.addActionListener(new ActionListener() {
            /**
             *
             */
            public void actionPerformed(ActionEvent e) {
                graph.setCellStyles(mxConstants.STYLE_FONTSIZE, sizeCombo
                        .getSelectedItem().toString().replace("pt", ""));
            }
        });
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
        }
        Constants.initConstants();
        JFrame frame = new JFrame(LanguageUtil.getResourceBundle().getString("jerd.java.entity.relationship.diagram"));
        ERDPanel diagram = new ERDPanel();
        frame.setContentPane(diagram.mainPane);
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        frame.pack();
        frame.setSize(1280, 720);
        frame.setVisible(true);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if (!diagram.modified || JOptionPane.showConfirmDialog(diagram.mainPane, LanguageUtil.getResourceBundle().getString("lose.changes")) == JOptionPane.YES_OPTION) {

                    System.exit(0);
                }
            }
        });


    }

    public void setModified(boolean modified) {
        this.modified = modified;
        if (currentFIle != null) {
            fileStats.setText(currentFIle.getName() + " (" + (!modified ? LanguageUtil.getResourceBundle().getString("saved") : LanguageUtil.getResourceBundle().getString("not.saved")) + ")");
        } else fileStats.setText(LanguageUtil.getResourceBundle().getString("no.file.selected"));
    }
}
