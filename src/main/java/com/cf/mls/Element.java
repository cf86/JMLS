package com.cf.mls;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Element {

	/**
	 * icon path
	 */
	private String icon;

	/**
	 * label text
	 */
	private String label;

	/**
	 * shortcut
	 */
	private Character shortcut;

	/**
	 * tooltip text
	 */
	private String toolTip;

	/**
	 * elements actioncommand
	 */
	private String actionCommand;

	/**
	 * constructor
	 */
	protected Element() {
		this.icon = null;
		this.label = null;
		this.shortcut = null;
		this.toolTip = null;
		this.actionCommand = null;
	}

	/**
	 * constructor
	 * 
	 * @param icon
	 *            icon path
	 * @param label
	 *            label text
	 * @param shortcut
	 *            shortcut
	 * @param toolTip
	 *            tooltip text
	 * @param actionCommand
	 *            actioncommand
	 */
	protected Element(String icon, String label, Character shortcut, String toolTip, String actionCommand) {
		this.icon = icon;
		this.label = label;
		this.shortcut = shortcut;
		this.toolTip = toolTip;
		this.actionCommand = actionCommand;
	}

	/**
	 * gets the icon path
	 * 
	 * @return the icon path
	 */
	public String getIcon() {
		return icon;
	}

	/**
	 * gets the label
	 * 
	 * @return the label text
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * gets the shortcut
	 * 
	 * @return the shortcut text
	 */
	public Character getShortcut() {
		return shortcut;
	}

	/**
	 * gets the tooltip text
	 * 
	 * @return the tooltip text
	 */
	public String getToolTip() {
		return toolTip;
	}

	/**
	 * gets the actioncommand
	 * 
	 * @return the actioncommand
	 */
	public String getActionCommand() {
		return this.actionCommand;
	}

	/**
	 * sets the icon path
	 * 
	 * @param icon
	 *            given path
	 */
	public void setIcon(String icon) {
		if (icon == null || icon.trim().length() < 1)
			this.icon = null;
		else
			this.icon = icon.trim();
	}

	/**
	 * sets a label where the text is given in a file
	 * 
	 * @param path
	 *            file path
	 * @param inJar
	 *            true if the file is in the jar, false if it is on the HDD
	 */
	public void setLabel(String path, boolean inJar) {
		try {
			if (inJar) {
				String label = readFileFromJar(path);
				setLabel(label);
			} else {
				String label = readFileFromHDD(path);
				setLabel(label);
			}
		} catch (IOException e) {
			System.err.println("Couldn't find label file: " + path);
			setLabel("Path not found");
		}
	}

	/**
	 * sets the given string as label
	 * 
	 * @param label
	 *            given label
	 */
	public void setLabel(String label) {
		if (label == null || label.trim().length() < 1)
			this.label = null;
		else
			this.label = label.trim();
	}

	/**
	 * sets the given shortcut as shortcut. Only the first letter will be taken
	 * as a shortcut
	 * 
	 * @param shortcut
	 *            given shortcut
	 */
	public void setShortcut(String shortcut) {
		if (shortcut == null || shortcut.trim().length() < 1)
			this.shortcut = null;
		else
			this.shortcut = shortcut.trim().charAt(0);
	}

	/**
	 * sets a toolTip where the text is given in a file
	 * 
	 * @param path
	 *            file path
	 * @param inJar
	 *            true if the file is in the jar, false if it is on the HDD
	 */
	public void setToolTip(String path, boolean inJar) {
		try {
			if (inJar) {
				String label = readFileFromJar(path);
				setToolTip(label);
			} else {
				String label = readFileFromHDD(path);
				setToolTip(label);
			}

		} catch (IOException e) {
			System.err.println("Couldn't find ToolTip file: " + path);
			setToolTip("Path not found");
		}
	}

	/**
	 * sets the given tooltip text.
	 * 
	 * @param toolTip
	 *            given tooltip text
	 */
	public void setToolTip(String toolTip) {
		if (toolTip == null || toolTip.trim().length() < 1)
			this.toolTip = null;
		else
			this.toolTip = toolTip.trim();
	}

	/**
	 * sets the actioncommand
	 * 
	 * @param cmd
	 *            given actioncommand
	 */
	public void setActionCommand(String cmd) {
		if (cmd == null || cmd.trim().length() < 1)
			this.actionCommand = null;
		else
			this.actionCommand = cmd.trim();
	}

	/**
	 * reads the given file from the HDD and returns its content as a string
	 * 
	 * @param path
	 *            the given path
	 * 
	 * @return the file content
	 * 
	 * @throws IOException
	 *             thrown if file couldn't be read or found.
	 */
	private String readFileFromHDD(String path) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File(path)));

		StringBuffer result = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null)
			result.append(line).append("\n");

		br.close();
		return result.toString().trim();
	}

	/**
	 * reads the given file from a jar file and returns its content as a string
	 * 
	 * @param path
	 *            the given path
	 * 
	 * @return the file content
	 * 
	 * @throws IOException
	 *             thrown if file couldn't be read or found.
	 */
	private String readFileFromJar(String path) throws IOException {
		InputStream stream;
		stream = MLS.class.getResourceAsStream("/" + path);
		// if this fails try again using relativ paths and ClassLoader
		if (stream == null) {
			stream = ClassLoader.getSystemResourceAsStream(path);
		}

		if (stream == null) {
			throw new IOException("Couldn't find: " + path);
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(stream));

		StringBuffer result = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null)
			result.append(line).append("\n");

		br.close();
		return result.toString().trim();
	}
}