package com.cf.mls;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.cf.util.Regex;

public class Ressource {

	/**
	 * maps all identifiers to the component element
	 */
	private Map<String, Element> map;

	/**
	 * the path to the ressource
	 */
	private String path;

	/**
	 * used regex for config lines
	 */
	private static Regex regex = new Regex("(.*?)=(.*?)");;

	/**
	 * current used locale
	 */
	private Locale locale;

	/**
	 * constructor
	 * 
	 * @param path
	 *            path to the ressoure file without Locale e.g.
	 *            path/to/file/file
	 * @param locale
	 *            the given locale
	 */
	protected Ressource(String path, Locale locale) {
		this.map = new HashMap<String, Element>();
		this.path = path;
		this.locale = locale;
	}

	/**
	 * reads the ressource file for this Ressource
	 */
	public void readRessource() {
		InputStream stream;
		stream = MLS.class.getResourceAsStream("/" + path + "." + locale.getLanguage() + "_" + locale.getCountry());
		// if this fails try again using relativ paths and ClassLoader
		if (stream == null) {
			stream = ClassLoader.getSystemResourceAsStream(path + "." + locale.getLanguage() + "_" + locale.getCountry());
		}

		if (stream == null) {
			System.err.println("can't find ressource " + path + "." + locale.getLanguage() + "_" + locale.getCountry() + " (path must be in the form path/to/file/file)");
			return;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));

		String line;
		try {
			while ((line = br.readLine()) != null) {

				if (!regex.matches(line))
					continue;

				if (regex.getGroup(1).contains("."))
					setParameter(line);
				else
					setMessage(line);
			}
		} catch (IOException e) {
			System.err.println("Couldn't find ressource file: " + path + "." + locale.getLanguage() + "_" + locale.getCountry());
		}
	}

	/**
	 * sets the message for the given line
	 * 
	 * @param line
	 *            the given line in format identifier=value
	 */
	private void setMessage(String line) {
		String param = line.split("=", 2)[0].trim();
		String value = line.split("=", 2)[1].trim();
		Element ele = new Element();
		ele.setLabel(value);
		this.map.put(param, ele);
	}

	/**
	 * sets the Element for the given line
	 * 
	 * @param line
	 *            given line in format identifier.param=value
	 */
	private void setParameter(String line) {
		// icon, label, shortcut, toolTip
		String identifier = line.split("=", 2)[0].split("\\.", 2)[0].trim();
		String param = line.split("=", 2)[0].split("\\.", 2)[1].trim();
		// value
		String value = line.split("=", 2)[1].trim();

		if (this.map.containsKey(identifier)) {
			if (param.equalsIgnoreCase("icon"))
				this.map.get(identifier).setIcon(value);
			else if (param.equalsIgnoreCase("label"))
				this.map.get(identifier).setLabel(value);
			else if (param.equalsIgnoreCase("labelHDD"))
				this.map.get(identifier).setLabel(value, false);
			else if (param.equalsIgnoreCase("labelJar"))
				this.map.get(identifier).setLabel(value, true);
			else if (param.equalsIgnoreCase("shortcut"))
				this.map.get(identifier).setShortcut(value);
			else if (param.equalsIgnoreCase("tooltip"))
				this.map.get(identifier).setToolTip(value);
			else if (param.equalsIgnoreCase("actionCommand"))
				this.map.get(identifier).setActionCommand(value);
		} else {
			Element element = new Element();
			if (param.equalsIgnoreCase("icon"))
				element.setIcon(value);
			else if (param.equalsIgnoreCase("label"))
				element.setLabel(value);
			else if (param.equalsIgnoreCase("labelHDD"))
				element.setLabel(value, false);
			else if (param.equalsIgnoreCase("labelJar"))
				element.setLabel(value, true);
			else if (param.equalsIgnoreCase("shortcut"))
				element.setShortcut(value);
			else if (param.equalsIgnoreCase("toolTip"))
				element.setToolTip(value);
			else if (param.equalsIgnoreCase("actionCommand"))
				element.setActionCommand(value);

			this.map.put(identifier, element);
		}
	}

	/**
	 * sets the current locale
	 * 
	 * @param locale
	 *            new locale
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * gets the current locale
	 * 
	 * @return current locale
	 */
	public Locale getLocale() {
		return this.locale;
	}

	/**
	 * gets the path to the ressource
	 * 
	 * @return path to the ressource inside the jar file of the form
	 *         path/to/file
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * sets the path to the ressource, must be in the form path/to/file
	 * 
	 * @param path
	 *            given path
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * gets the element for this identifier
	 * 
	 * @param identifier
	 *            given identifier
	 * 
	 * @return the element for this identifier
	 */
	public Element getElement(String identifier) {
		if (this.map.get(identifier) == null)
			System.err.println("no element for identifier '" + identifier + "' available.");

		return this.map.get(identifier);
	}

	/**
	 * gets the message for this identifier
	 * 
	 * @param identifier
	 *            given identifier
	 * 
	 * @return the message for this identifier
	 */
	public String getMessage(String identifier) {
		if (this.map.get(identifier) == null) {
			System.err.println("no message for identifier '" + identifier + "' available.");
			return null;
		}

		return this.map.get(identifier).getLabel();
	}

	/**
	 * reads the given translator config path
	 * 
	 * @param path
	 *            path to the config file
	 * 
	 * @return a map mapping the locale string to the display name of the
	 *         language
	 */
	public static Map<String, Element> readMLSConfigFile(String path) {
		Map<String, Element> result = new HashMap<String, Element>();

		InputStream stream;
		stream = MLS.class.getResourceAsStream("/" + path);
		// if this fails try again using relativ paths and ClassLoader
		if (stream == null) {
			System.err.println("can't find ressource /" + path);
			stream = ClassLoader.getSystemResourceAsStream(path);
		}

		if (stream == null) {
			System.err.println("can't find ressource " + path + " (path must be in the form path/to/file/file)");
			return null;
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(stream));

		String line;
		try {
			while ((line = br.readLine()) != null) {
				if (line.length() < 1)
					continue;
				if (line.charAt(0) == '#')
					continue;
				if (!regex.matches(line))
					continue;

				String name = regex.getGroup(1);
				// check if it contains a dot
				if (name.contains(".")) {
					String identifier = line.split("=", 2)[0].split("\\.", 2)[0].trim();
					String param = line.split("=", 2)[0].split("\\.", 2)[1].trim();
					// value
					String value = line.split("=", 2)[1].trim();
					// check if it is an icon
					if (param.equalsIgnoreCase("icon")) {
						if (result.containsKey(identifier)) {
							result.get(identifier).setIcon(value);
						} else {
							Element element = new Element();
							element.setIcon(value);
							result.put(identifier, element);
						}
					}
				} else {
					Element element = new Element();
					element.setLabel(name);
					result.put(name, element);
				}
			}

			return result;
		} catch (IOException e) {
			System.err.println("can't find or read ressource " + path + " (path must be in the form path/to/file/file)");
		}

		return result;
	}
}