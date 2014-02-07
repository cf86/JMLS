package com.cf.mls.extension;

import java.util.Locale;

public interface LanguageMenuExtension {

	/**
	 * this method will be called whenever the language is changed during the
	 * language menu
	 * 
	 * @param lang
	 *            the new language
	 */
	public void changeLanguage(Locale lang);
}
