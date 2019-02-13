package com.gaboragazzo.jerd.controllers.dialogs;

import com.gaboragazzo.jerd.utils.LanguageUtil;

import javax.swing.*;
import java.awt.event.*;

public class SetNameDialog extends JDialog
{
	private JPanel contentPane;
	private JButton buttonOK;
	private JButton buttonCancel;
	private JTextField newName;
	private boolean confirm = false;

	public SetNameDialog()
	{
		setLocationByPlatform(true);
		setTitle(LanguageUtil.getResourceBundle().getString("set.name"));
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

	public String getNewName()
	{
		return newName.getText();
	}

	public void setNewName(String name)
	{
		newName.setText(name);
	}

	public boolean isConfirm()
	{
		return confirm;
	}
}
