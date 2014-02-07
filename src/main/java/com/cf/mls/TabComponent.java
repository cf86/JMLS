package com.cf.mls;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;

import com.cf.structures.DataDouble;

public class TabComponent extends Component {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the tabbed pane
	 */
	private JTabbedPane pane;

	/**
	 * maps the tab index to its identifier
	 */
	private List<DataDouble<Integer, String>> tabs;

	/**
	 * constructor
	 * 
	 * @param pane
	 *            the tabbed pane
	 */
	protected TabComponent(JTabbedPane pane) {
		this.tabs = new ArrayList<DataDouble<Integer, String>>();
		this.pane = pane;
	}

	/**
	 * adds a new tab to the tab component
	 * 
	 * @param identifier
	 *            the identifier for this tab
	 */
	public void addTab(String identifier) {
		int index = this.tabs.size();
		this.tabs.add(new DataDouble<Integer, String>(index, identifier));
	}

	/**
	 * gets all tabs and their indicies
	 * 
	 * @return all tabs
	 */
	public List<DataDouble<Integer, String>> getTabs() {
		return tabs;
	}

	/**
	 * gets the tabbed pane
	 * 
	 * @return the tabbed pane
	 */
	public JTabbedPane getPane() {
		return pane;
	}
}