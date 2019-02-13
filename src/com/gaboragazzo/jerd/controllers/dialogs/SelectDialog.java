package com.gaboragazzo.jerd.controllers.dialogs;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

public class SelectDialog<T> extends JDialog
{
	private final boolean multipleChoice;
	private final T[] list;
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JList<T> objectList;
	private boolean confirm = false;

	public SelectDialog(T[] list, boolean multipleChoice)
	{

		this.list = list;
		this.multipleChoice = multipleChoice;

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

	private void onOK()
	{
		// add your code here
		confirm = true;
		dispose();
	}

	private void onCancel()
	{
		// add your code here if necessary

		dispose();
	}



	private void createUIComponents()
	{
		objectList = new JList<T>(list);
		if(!multipleChoice)
			objectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setLocationByPlatform(true);
	}

	public boolean isConfirm()
	{
		return confirm;
	}


	public T getSelectedValue()
	{
		return (T) objectList.getSelectedValue();
	}

	public List<T> getSelectedValueList()
	{
		return  objectList.getSelectedValuesList();
	}
}
