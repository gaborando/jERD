package com.gaboragazzo.jerd.controllers.dialogs;

import com.gaboragazzo.jerd.controllers.cell.RelationshipCell;
import com.gaboragazzo.jerd.utils.LanguageUtil;
import com.gaboragazzo.jerd.controllers.cell.EntityCell;
import com.gaboragazzo.jerd.model.er.Cardinality;

import javax.swing.*;
import java.awt.event.*;

public class HookEntityDialog extends JDialog
{
	private final Object[] openPorts;
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JComboBox entityList;
	private JComboBox cardinalityBox;
	private JComboBox portBox;
	private Object[] entityCells;
	private boolean confirmed = false;

	public HookEntityDialog(Object[] cells, Object[] openPorts)
	{
		this.openPorts = openPorts;
		this.entityCells = cells;
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);
		setTitle(LanguageUtil.getResourceBundle().getString("hook.up.entity"));

		buttonOK.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onOK();
			}
		});

		buttonCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onCancel();
			}
		});

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				onCancel();
			}
		});

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				onCancel();
			}
		}, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	private void onOK()
	{
		// add your code here
		confirmed = true;
		dispose();
	}

	private void onCancel()
	{
		// add your code here if necessary
		dispose();
	}

	public boolean isConfirmed(){
		return confirmed;
	}

	private void createUIComponents()
	{
		cardinalityBox = new JComboBox(Cardinality.values());
		entityList = new JComboBox(entityCells);
		portBox = new JComboBox(openPorts);
		setLocationByPlatform(true);

	}

	public Cardinality getCardinality(){
		return (Cardinality) cardinalityBox.getSelectedItem();
	}


	public RelationshipCell.PORTS getPort(){
		return (RelationshipCell.PORTS) portBox.getSelectedItem();
	}

	public EntityCell getEntity(){
		return (EntityCell) entityList.getSelectedItem();
	}
}
