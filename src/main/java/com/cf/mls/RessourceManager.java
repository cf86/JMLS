package com.cf.mls;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RessourceManager {

	/**
	 * maps each known locale to its ressource
	 */
	private Map<Locale, Ressource> map;

	/**
	 * path to the language file without extension
	 */
	private String path;

	/**
	 * Constructor
	 * 
	 * @param path
	 *            path to ressource file without extension e.g.
	 *            path/to/file/file
	 */
	protected RessourceManager(String path) {
		this.map = new HashMap<Locale, Ressource>();
		this.path = path;
	}

	/**
	 * adds a new ressource. If the ressource is already added nothing happens
	 * 
	 * @param locale
	 *            new Ressource Locale
	 */
	public void addRessource(Locale locale) {

		if (this.map.containsKey(locale))
			return;

		Ressource res = new Ressource(path, locale);
		res.readRessource();

		this.map.put(locale, res);
	}

	/**
	 * gets the message for the given identifier in the given language
	 * 
	 * @param identifier
	 *            given identifier
	 * @param locale
	 *            given locale language
	 * 
	 * @return the message
	 */
	public String getMessage(String identifier, Locale locale) {
		if (this.map.get(locale) == null) {
			System.err.println("there is no ressource for for identifier " + identifier + " Locale " + locale.getLanguage() + "_" + locale.getCountry());
			return "message not found";
		}

		return this.map.get(locale).getMessage(identifier);
	}

	/**
	 * gets the element for the given identifier in the given language
	 * 
	 * @param identifier
	 *            given identifier
	 * @param locale
	 *            given locale language
	 * 
	 * @return the element
	 */
	public Element getElement(String identifier, Locale locale) {
		if (this.map.get(locale) == null) {
			System.err.println("there is no ressource for for identifier " + identifier + " Locale " + locale.getLanguage() + "_" + locale.getCountry());
			return null;
		}

		return this.map.get(locale).getElement(identifier);
	}
}