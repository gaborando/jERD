package com.gaboragazzo.jerd.controllers.dialogs;

import com.gaboragazzo.jerd.model.er.Cardinality;

import javax.swing.*;
import java.awt.event.*;

public class EditAttributeDialog extends JDialog
{
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField nameField;
	private JComboBox typeBox;
	private JComboBox cardinalityBox;
	private boolean confirm = false;

	public EditAttributeDialog()
	{
		setLocationByPlatform(true);
		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

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

	@Override
	public void setVisible(boolean b)
	{
		if(b)
			confirm = false;
		super.setVisible(b);
	}

	private void onOK()
	{
		// add your code here
		this.confirm = true;
		dispose();
	}

	private void onCancel()
	{
		// add your code here if necessary
		dispose();
	}

	private void createUIComponents()
	{
		typeBox = new JComboBox(com.gaboragazzo.jerd.model.er.Type.values());
		typeBox.setSelectedItem(com.gaboragazzo.jerd.model.er.Type.STRING);
		cardinalityBox = new JComboBox();
		cardinalityBox.addItem(Cardinality.ONE_TO_ONE);
		cardinalityBox.addItem(Cardinality.ONE_TO_MANY);
		cardinalityBox.addItem(Cardinality.ZERO_TO_ONE);
		cardinalityBox.addItem(Cardinality.ZERO_TO_MANY);
		cardinalityBox.setSelectedItem(Cardinality.ONE_TO_ONE);
	}

	public boolean isConfirm()
	{
		return confirm;
	}

	public String getAttributeName(){
		return nameField.getText();
	}

	public com.gaboragazzo.jerd.model.er.Type getAttributeType(){
		return (com.gaboragazzo.jerd.model.er.Type) typeBox.getSelectedItem();
	}

	public Cardinality getAttributeCardinality(){
		return (Cardinality) cardinalityBox.getSelectedItem();
	}

	public void initializeData(String name, com.gaboragazzo.jerd.model.er.Type type, Cardinality cardinality)
	{
		nameField.setText(name);
		typeBox.setSelectedItem(type);
		cardinalityBox.setSelectedItem(cardinality);
	}
}
