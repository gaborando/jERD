package com.gaboragazzo.jerd.utils;

import java.util.ResourceBundle;

public class LanguageUtil
{
	private static ResourceBundle resourceBundle = ResourceBundle.getBundle("language/strings");

	public static ResourceBundle getResourceBundle()
	{
		return resourceBundle;
	}
}
