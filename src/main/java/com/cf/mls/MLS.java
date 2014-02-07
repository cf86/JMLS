package com.cf.mls;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.border.TitledBorder;

import com.cf.mls.extension.LanguageMenuExtension;
import com.cf.structures.DataDouble;
import com.cf.util.Graphics;

public class MLS {

	// @formatter:off
	/*
	 * possible Identifiers:
	 * 
	 * [identifier].label 
	 * [identifier].labelHHD 
	 * [identifier].labelJar
	 * [identifier].icon 
	 * [identifier].shortcut 
	 * [identifier].toolTip
	 * [identifier].toolTipJar 
	 * [identifier].toolTipHDD
	 * [identifier].actionCommand
	 */
	// @formatter:on

	/**
	 * maps the identifier of a component to its component
	 */
	private Map<String, Component> components;

	/**
	 * current used locale
	 */
	private Locale locale;

	/**
	 * the ressource manager
	 */
	private RessourceManager ressources;

	/**
	 * current config path, default is mls.conf
	 */
	private String configPath = "mls.conf";

	/**
	 * sets the config path. Config file needs to be inside the jar
	 * 
	 * @param path
	 *            path in the format path/to/file/file inside the jar file
	 */
	public void setConfigPath(String path) {
		configPath = path;
	}

	/**
	 * gets the config path
	 * 
	 * @return the path
	 */
	public String getConfigPath() {
		return configPath;
	}

	/**
	 * Constructor
	 * 
	 * @param path
	 *            path to ressource file without extension e.g.
	 *            path/to/file/file
	 * @param locale
	 *            the default locale
	 */
	public MLS(String path, Locale locale) {
		this.components = new HashMap<String, Component>();
		this.locale = locale;
		this.ressources = new RessourceManager(path);
		this.ressources.addRessource(locale);
	}

	/**
	 * sets the tooltip duration
	 * 
	 * @param duration
	 *            duration in ms, if -1 is used it will stay until mouse is
	 *            moved
	 */
	public void setToolTipDuration(int duration) {
		if (duration == -1)
			ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);
		else
			ToolTipManager.sharedInstance().setDismissDelay(duration);
	}

	/**
	 * creates a Language Menu with RadioButtons on the given menu and auto
	 * selects the given current selected language.
	 * 
	 * @param menu
	 *            given menu
	 * @param currentSelected
	 *            given current Locale
	 */
	public void addRadioButtonLanguageMenuItem(final JComponent menu, final Locale currentSelected) {
		addRadioButtonLanguageMenuItem(menu, currentSelected, null);
	}

	/**
	 * creates a Language Menu with RadioButtons on the given menu and auto
	 * selects the given current selected language. At each click the given
	 * extension will be called
	 * 
	 * @param menu
	 *            given menu
	 * @param currentSelected
	 *            given current Locale
	 * @param ext
	 *            given extension
	 */
	public void addRadioButtonLanguageMenuItem(final JComponent menu, final Locale currentSelected, final LanguageMenuExtension ext) {
		Map<String, Element> conf = Ressource.readMLSConfigFile(configPath);

		if (conf == null)
			return;

		List<JRadioButtonMenuItem> btns = new ArrayList<JRadioButtonMenuItem>();
		for (final String k : conf.keySet()) {

			JRadioButtonMenuItem btn = new JRadioButtonMenuItem(conf.get(k).getLabel());

			if (conf.get(k).getIcon() != null) {
				ImageIcon img = getImageIcon(conf.get(k).getIcon());
				if (img == null)
					System.err.println("couldnt find image " + conf.get(k).getIcon());
				else
					btn.setIcon(img);
			}

			btn.setActionCommand(k);
			btn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					String[] tmp = k.split("_");
					setLocale(new Locale(tmp[0], tmp[1]));
					translate();
					if (ext != null)
						ext.changeLanguage(getLocale());
				}
			});
			btns.add(btn);
			if (k.equals(currentSelected.getLanguage() + "_" + currentSelected.getCountry()))
				btn.setSelected(true);
		}

		Collections.sort(btns, new Comparator<JRadioButtonMenuItem>() {

			@Override
			public int compare(JRadioButtonMenuItem arg0, JRadioButtonMenuItem arg1) {
				return arg0.getText().compareToIgnoreCase(arg1.getText());
			}
		});

		ButtonGroup bg = new ButtonGroup();
		for (JRadioButtonMenuItem btn : btns) {
			bg.add(btn);
			menu.add(btn);
		}
	}

	/**
	 * creates a Language Menu with JCheckboxses on the given menu and auto
	 * selects the given current selected language.
	 * 
	 * @param menu
	 *            given menu
	 * @param currentSelected
	 *            given current Locale
	 */
	public void addCheckBoxLanguageMenuItem(final JComponent menu, final Locale currentSelected) {
		addCheckBoxLanguageMenuItem(menu, currentSelected, null);
	}

	/**
	 * creates a Language Menu with JCheckboxses on the given menu and auto
	 * selects the given current selected language. At each click the given
	 * extension will be called
	 * 
	 * @param menu
	 *            given menu
	 * @param currentSelected
	 *            given current Locale
	 * @param ext
	 *            given extension
	 */
	public void addCheckBoxLanguageMenuItem(final JComponent menu, final Locale currentSelected, final LanguageMenuExtension ext) {

		Map<String, Element> conf = Ressource.readMLSConfigFile(configPath);

		if (conf == null)
			return;

		List<JCheckBoxMenuItem> btns = new ArrayList<JCheckBoxMenuItem>();
		for (final String k : conf.keySet()) {

			JCheckBoxMenuItem btn = new JCheckBoxMenuItem(conf.get(k).getLabel());
			btn.setActionCommand(k);

			if (conf.get(k).getIcon() != null) {
				ImageIcon img = getImageIcon(conf.get(k).getIcon());
				if (img == null)
					System.err.println("couldnt find image " + conf.get(k).getIcon());
				else
					btn.setIcon(img);
			}

			btn.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					String[] tmp = k.split("_");
					setLocale(new Locale(tmp[0], tmp[1]));
					translate();
					if (ext != null)
						ext.changeLanguage(getLocale());
				}
			});
			btns.add(btn);
			if (k.equals(currentSelected.getLanguage() + "_" + currentSelected.getCountry()))
				btn.setSelected(true);
		}

		Collections.sort(btns, new Comparator<JCheckBoxMenuItem>() {

			@Override
			public int compare(JCheckBoxMenuItem arg0, JCheckBoxMenuItem arg1) {
				return arg0.getText().compareToIgnoreCase(arg1.getText());
			}
		});

		ButtonGroup bg = new ButtonGroup();
		for (JCheckBoxMenuItem btn : btns) {
			bg.add(btn);
			menu.add(btn);
		}
	}

	/**
	 * generates a JButton
	 * 
	 * @param identifier
	 *            the identifier
	 * 
	 * @return the JButton
	 */
	public JButton generateJButton(String identifier) {
		return generateJButton(identifier, true, null);
	}

	/**
	 * generates a JButton
	 * 
	 * @param identifier
	 *            the identifier
	 * @param actionListener
	 *            the action listener
	 * 
	 * @return the JButton
	 */
	public JButton generateJButton(String identifier, ActionListener actionListener) {
		return generateJButton(identifier, true, actionListener);
	}

	/**
	 * generates a JButton
	 * 
	 * @param identifier
	 *            the identifier
	 * @param enabled
	 *            button is enabled
	 * @param actionListener
	 *            an action listener
	 * 
	 * @return the JButton
	 */
	public JButton generateJButton(String identifier, boolean enabled, ActionListener actionListener) {
		return generateJButton(identifier, enabled, actionListener, null);
	}

	/**
	 * generates a JButton
	 * 
	 * @param identifier
	 *            the identifier
	 * @param enabled
	 *            button is enabled
	 * @param actionListener
	 *            an action listener
	 * @param actionCommand
	 *            an action command string
	 * 
	 * @return the JButton
	 */
	public JButton generateJButton(String identifier, boolean enabled, ActionListener actionListener, String actionCommand) {
		JButton result = new JButton();
		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			result.setText("id is missing.");
			return result;
		}

		if (element.getLabel() != null)
			result.setText(element.getLabel());

		if (element.getIcon() != null) {
			ImageIcon img = getImageIcon(element.getIcon());
			if (img == null)
				System.err.println("couldnt find image " + element.getIcon());
			else
				result.setIcon(img);
		}

		if (actionListener != null)
			result.addActionListener(actionListener);

		setToolTip(element.getToolTip(), result);
		setShortcut(element.getShortcut(), result);

		// if actioncommand is given take this, else take the one from the file
		if (actionCommand != null)
			setActionCommand(actionCommand, result);
		else
			setActionCommand(element.getActionCommand(), result);

		result.setEnabled(enabled);

		register(identifier, result);

		return result;
	}

	/**
	 * generates a JToggleButton
	 * 
	 * @param identifier
	 *            the identifier
	 * 
	 * @return the JButton
	 */
	public JToggleButton generateJToggleButton(String identifier) {
		return generateJToggleButton(identifier, true, null);
	}

	/**
	 * generates a JToggleButton
	 * 
	 * @param identifier
	 *            the identifier
	 * @param actionListener
	 *            the action listener
	 * 
	 * @return the JToggleButton
	 */
	public JToggleButton generateJToggleButton(String identifier, ActionListener actionListener) {
		return generateJToggleButton(identifier, true, actionListener);
	}

	/**
	 * generates a JToggleButton
	 * 
	 * @param identifier
	 *            the identifier
	 * @param enabled
	 *            button is enabled
	 * @param actionListener
	 *            an action listener
	 * 
	 * @return the JToggleButton
	 */
	public JToggleButton generateJToggleButton(String identifier, boolean enabled, ActionListener actionListener) {
		return generateJToggleButton(identifier, enabled, actionListener, null);
	}

	/**
	 * generates a JToggleButton
	 * 
	 * @param identifier
	 *            the identifier
	 * @param enabled
	 *            button is enabled
	 * @param actionListener
	 *            an action listener
	 * @param actionCommand
	 *            an action command string
	 * 
	 * @return the JToggleButton
	 */
	public JToggleButton generateJToggleButton(String identifier, boolean enabled, ActionListener actionListener, String actionCommand) {
		JToggleButton result = new JToggleButton();
		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			result.setText("id is missing.");
			return result;
		}

		if (element.getLabel() != null)
			result.setText(element.getLabel());

		if (element.getIcon() != null) {
			ImageIcon img = getImageIcon(element.getIcon());
			if (img == null)
				System.err.println("couldnt find image " + element.getIcon());
			else
				result.setIcon(img);
		}
		if (actionListener != null)
			result.addActionListener(actionListener);

		setToolTip(element.getToolTip(), result);
		setShortcut(element.getShortcut(), result);

		// if actioncommand is given take this, else take the one from the file
		if (actionCommand != null)
			setActionCommand(actionCommand, result);
		else
			setActionCommand(element.getActionCommand(), result);

		result.setEnabled(enabled);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JTextField
	 * 
	 * @param identifier
	 *            given identifier
	 * 
	 * @return the JTextField
	 */
	public JTextField generateJTextField(String identifier) {
		return generateJTextField(identifier, true, true, 10, "");
	}

	/**
	 * generates a JTextField
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            TextField enabled
	 * @param editable
	 *            TextField editable
	 * @param columns
	 *            default TextField width
	 * @param defaultText
	 *            default text
	 * 
	 * @return the JTextField
	 */
	public JTextField generateJTextField(String identifier, boolean enabled, boolean editable, int columns, String defaultText) {
		// < -1 => 10, else width
		JTextField result = new JTextField(columns < 0 ? 10 : columns);
		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			return result;
		}

		result.setText(defaultText);

		setToolTip(element.getToolTip(), result);
		result.setEnabled(enabled);
		result.setEditable(editable);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JFormattedTextField
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            component enabled
	 * @param editable
	 *            component editable
	 * @param defaultText
	 *            default text
	 * @param format
	 *            the AbstractFormatter
	 * 
	 * @return the JFormattedTextField
	 */
	public JFormattedTextField generateJFormattedTextField(String identifier, boolean enabled, boolean editable, String defaultText, AbstractFormatter format) {
		JFormattedTextField result;
		if (format != null)
			result = new JFormattedTextField(format);
		else
			result = new JFormattedTextField();

		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			return result;
		}

		result.setText(defaultText);

		setToolTip(element.getToolTip(), result);
		result.setEnabled(enabled);
		result.setEditable(editable);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JFormattedTextField
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            component enabled
	 * @param editable
	 *            component editable
	 * @param defaultText
	 *            default text
	 * @param format
	 *            the format String
	 * 
	 * @return the JFormattedTextField
	 */
	public JFormattedTextField generateJFormattedTextField(String identifier, boolean enabled, boolean editable, String defaultText, String format) {
		JFormattedTextField result = new JFormattedTextField(format);
		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			return result;
		}

		result.setText(defaultText);

		setToolTip(element.getToolTip(), result);
		result.setEnabled(enabled);
		result.setEditable(editable);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a Bordered JPanel
	 * 
	 * @param identifier
	 *            given identifier
	 * 
	 * @return the bordered Panel
	 */
	public JPanel generateTitledPanel(String identifier) {
		return generateTitledPanel(identifier, true);
	}

	/**
	 * generates a Bordered JPanel
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            sets the JPanel enabled
	 * 
	 * @return the bordered Panel
	 */
	public JPanel generateTitledPanel(String identifier, boolean enabled) {
		JPanel result = new JPanel();
		result.setBorder(BorderFactory.createTitledBorder("id is missing"));
		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			return result;
		}

		((TitledBorder) result.getBorder()).setTitle(element.getLabel());
		setToolTip(element.getToolTip(), result);

		result.setEnabled(enabled);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JPanel
	 * 
	 * @param identifier
	 *            given identifier
	 * 
	 * @return the JPanel
	 */
	public JPanel generateJPanel(String identifier) {
		return generateJPanel(identifier, true);
	}

	/**
	 * generates a JPanel
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            sets the JPanel enabled
	 * 
	 * @return the JPanel
	 */
	public JPanel generateJPanel(String identifier, boolean enabled) {
		JPanel result = new JPanel();
		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			return result;
		}

		setToolTip(element.getToolTip(), result);

		result.setEnabled(enabled);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JLabel
	 * 
	 * @param identifier
	 *            given identifier
	 * 
	 * @return the JLabel
	 */
	public JLabel generateJLabel(String identifier) {
		return generateJLabel(identifier, true);
	}

	/**
	 * generates a JLabel
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            JLabel enabled
	 * 
	 * @return the JLabel
	 */
	public JLabel generateJLabel(String identifier, boolean enabled) {
		JLabel result = new JLabel();
		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			result.setText("id is missing.");
			return result;
		}

		if (element.getLabel() != null)
			result.setText(element.getLabel());

		if (element.getIcon() != null) {
			ImageIcon img = getImageIcon(element.getIcon());
			if (img == null)
				System.err.println("couldnt find image " + element.getIcon());
			else
				result.setIcon(img);
		}

		setToolTip(element.getToolTip(), result);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JComboBox
	 * 
	 * @param identifier
	 *            given identifier
	 * 
	 * @return the JComboBox
	 */
	public <T> JComboBox<T> generateJComboBox(String identifier, List<T> elements) {
		return generateJComboBox(identifier, elements, -1, true, false, null);
	}

	/**
	 * generates a JComboBox
	 * 
	 * @param identifier
	 *            given identifier
	 * 
	 * @return the JComboBox
	 */
	public <T> JComboBox<T> generateJComboBox(String identifier, T[] elements) {
		return generateJComboBox(identifier, elements, -1, true, false);
	}

	/**
	 * generates a JComboBox
	 * 
	 * @param identifier
	 *            given identifier
	 * @param elements
	 *            given elements
	 * @param selectedIndex
	 *            the selected index
	 * @param enabled
	 *            component enabled
	 * @param editable
	 *            component editable
	 * 
	 * @return the JComboBox
	 */
	public <T> JComboBox<T> generateJComboBox(String identifier, T[] elements, int selectedIndex, boolean enabled, boolean editable) {
		if (elements == null)
			return generateJComboBox(identifier, null, selectedIndex, enabled, editable, null);
		return generateJComboBox(identifier, Arrays.asList(elements), selectedIndex, enabled, editable, null);
	}

	/**
	 * generates a JComboBox
	 * 
	 * @param identifier
	 *            given identifier
	 * @param elements
	 *            given elements
	 * @param selectedIndex
	 *            the selected index
	 * @param enabled
	 *            component enabled
	 * @param editable
	 *            component editable
	 * @param actionListener
	 *            the Action Listener
	 * 
	 * @return the JComboBox
	 */
	public <T> JComboBox<T> generateJComboBox(String identifier, List<T> elements, int selectedIndex, boolean enabled, boolean editable, ActionListener actionListener) {
		return generateJComboBox(identifier, elements, selectedIndex, enabled, editable, actionListener, null);
	}

	/**
	 * generates a JComboBox
	 * 
	 * @param identifier
	 *            given identifier
	 * @param elements
	 *            given elements
	 * @param selectedIndex
	 *            the selected index
	 * @param enabled
	 *            component enabled
	 * @param editable
	 *            component editable
	 * @param actionListener
	 *            the Action Listener
	 * @param actionCommand
	 *            an action command string
	 * 
	 * @return the JComboBox
	 */
	public <T> JComboBox<T> generateJComboBox(String identifier, List<T> elements, int selectedIndex, boolean enabled, boolean editable, ActionListener actionListener,
			String actionCommand) {
		JComboBox<T> result = new JComboBox<>();

		if (elements != null && elements.size() > 0) {
			DefaultComboBoxModel<T> model = new DefaultComboBoxModel<T>();
			for (T e : elements)
				model.addElement(e);

			result.setModel(model);

			if (selectedIndex < elements.size() && selectedIndex >= 0)
				result.setSelectedIndex(selectedIndex);
		}

		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			return result;
		}

		setToolTip(element.getToolTip(), result);

		// if actioncommand is given take this, else take the one from the file
		if (actionCommand != null)
			setActionCommand(actionCommand, result);
		else
			setActionCommand(element.getActionCommand(), result);

		result.setEnabled(enabled);
		result.setEditable(editable);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JCheckBox
	 * 
	 * @param identifier
	 *            given identifier
	 * @param isChecked
	 *            is checkbox selected
	 * 
	 * @return the JCheckBox
	 */
	public JCheckBox generateJCheckBox(String identifier, boolean isChecked) {
		return generateJCheckBox(identifier, true, isChecked, null);
	}

	/**
	 * generates a JCheckBox
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            component enabled
	 * @param isChecked
	 *            is checkbox selected
	 * @param actionListener
	 *            the actionListener
	 * 
	 * @return the JCheckBox
	 */
	public JCheckBox generateJCheckBox(String identifier, boolean enabled, boolean isChecked, ActionListener actionListener) {
		return generateJCheckBox(identifier, enabled, isChecked, actionListener, null);
	}

	/**
	 * generates a JCheckBox
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            component enabled
	 * @param isChecked
	 *            is checkbox selected
	 * @param actionListener
	 *            the actionListener
	 * @param actionCommand
	 *            an action command string
	 * 
	 * @return the JCheckBox
	 */
	public JCheckBox generateJCheckBox(String identifier, boolean enabled, boolean isChecked, ActionListener actionListener, String actionCommand) {
		JCheckBox result = new JCheckBox();
		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			result.setText("id is missing.");
			return result;
		}

		if (element.getLabel() != null)
			result.setText(element.getLabel());

		if (element.getIcon() != null) {
			ImageIcon img = getImageIcon(element.getIcon());
			if (img == null)
				System.err.println("couldnt find image " + element.getIcon());
			else
				result.setIcon(img);
		}

		if (actionListener != null)
			result.addActionListener(actionListener);

		setToolTip(element.getToolTip(), result);
		setShortcut(element.getShortcut(), result);

		// if actioncommand is given take this, else take the one from the file
		if (actionCommand != null)
			setActionCommand(actionCommand, result);
		else
			setActionCommand(element.getActionCommand(), result);

		result.setEnabled(enabled);
		result.setSelected(isChecked);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JRadioButton
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            component enabled
	 * @param actionListener
	 *            the actionListener
	 * 
	 * @return the JRadioButton
	 */
	public JRadioButton generateJRadioButton(String identifier, boolean enabled, ActionListener actionListener) {
		return generateJRadioButton(identifier, enabled, actionListener, null);
	}

	/**
	 * generates a JRadioButton
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            component enabled
	 * @param actionListener
	 *            the actionListener
	 * @param actionCommand
	 *            an action command string
	 * 
	 * @return the JRadioButton
	 */
	public JRadioButton generateJRadioButton(String identifier, boolean enabled, ActionListener actionListener, String actionCommand) {
		JRadioButton result = new JRadioButton();
		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			result.setText("id is missing.");
			return result;
		}

		if (element.getLabel() != null)
			result.setText(element.getLabel());

		if (element.getIcon() != null) {
			ImageIcon img = getImageIcon(element.getIcon());
			if (img == null)
				System.err.println("couldnt find image " + element.getIcon());
			else
				result.setIcon(img);
		}

		if (actionListener != null)
			result.addActionListener(actionListener);

		setToolTip(element.getToolTip(), result);
		setShortcut(element.getShortcut(), result);

		// if actioncommand is given take this, else take the one from the file
		if (actionCommand != null)
			setActionCommand(actionCommand, result);
		else
			setActionCommand(element.getActionCommand(), result);

		result.setEnabled(enabled);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JTextArea
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            TextField enabled
	 * @param editable
	 *            TextField editable
	 * @param columns
	 *            default TextArea width
	 * @param rows
	 *            default TextArea rows
	 * @param defaultText
	 *            default text
	 * 
	 * @return the JTextField
	 */
	public JTextArea generateJTextArea(String identifier, boolean enabled, boolean editable, int columns, int rows, String defaultText) {
		JTextArea result = new JTextArea(rows < 0 ? 10 : rows, columns < 0 ? 10 : columns);
		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			return result;
		}

		result.setText(defaultText);

		setToolTip(element.getToolTip(), result);
		result.setEnabled(enabled);
		result.setEditable(editable);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JPasswordField
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            JPasswordField enabled
	 * @param editable
	 *            JPasswordField editable
	 * @param columns
	 *            default JPasswordField width
	 * @param defaultText
	 *            default text
	 * 
	 * @return the JPasswordField
	 */
	public JPasswordField generateJPasswordField(String identifier, boolean enabled, boolean editable, int columns, String defaultText) {
		JPasswordField result = new JPasswordField(columns < 0 ? 10 : columns);
		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			return result;
		}

		result.setText(defaultText);

		setToolTip(element.getToolTip(), result);
		result.setEnabled(enabled);
		result.setEditable(editable);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JTextPane
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            component enabled
	 * @param editable
	 *            component editable
	 * @param defaultText
	 *            default text
	 * @param contentType
	 *            the contenttype "text/plain", "text/html", "text/rtf", can be
	 *            found in ContentTypes interface
	 * 
	 * @return the JTextPane
	 */
	public JTextPane generateJTextPane(String identifier, boolean enabled, boolean editable, String defaultText, String contentType) {
		JTextPane result = new JTextPane();
		if (defaultText != null) {
			result.setText(defaultText);
		}

		if (contentType != null)
			result.setContentType(contentType);

		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			return result;
		}

		setToolTip(element.getToolTip(), result);
		result.setEnabled(enabled);
		result.setEditable(editable);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JEditorPane
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            component enabled
	 * @param editable
	 *            component editable
	 * @param defaultText
	 *            default text
	 * @param contentType
	 *            the contenttype "text/plain", "text/html", "text/rtf", can be
	 *            found in ContentTypes interface
	 * 
	 * @return the JEditorPane
	 */
	public JEditorPane generateJEditorPane(String identifier, boolean enabled, boolean editable, String defaultText, String contentType) {
		JEditorPane result = new JEditorPane();
		if (defaultText != null) {
			result.setText(defaultText);
		}

		if (contentType != null)
			result.setContentType(contentType);

		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			return result;
		}

		setToolTip(element.getToolTip(), result);
		result.setEnabled(enabled);
		result.setEditable(editable);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JList
	 * 
	 * @param identifier
	 *            given identifier
	 * @param elements
	 *            default elements
	 * @param selectedIndex
	 *            selected index
	 * @param enabled
	 *            component enabled
	 * @param selectionMode
	 *            the selection mode, found in ListSelectionModel
	 * 
	 * @return the JList
	 */
	public <T> JList<T> generateJList(String identifier, T[] elements, int selectedIndex, boolean enabled, int selectionMode) {
		return generateJList(identifier, Arrays.asList(elements), selectedIndex, enabled, selectionMode);
	}

	/**
	 * generates a JList
	 * 
	 * @param identifier
	 *            given identifier
	 * @param elements
	 *            default elements
	 * @param selectedIndex
	 *            selected index
	 * @param enabled
	 *            component enabled
	 * @param selectionMode
	 *            the selection mode, found in ListSelectionModel
	 * 
	 * @return the JList
	 */
	public <T> JList<T> generateJList(String identifier, List<T> elements, int selectedIndex, boolean enabled, int selectionMode) {
		JList<T> result = new JList<T>();

		if (elements != null && elements.size() > 0) {
			DefaultListModel<T> model = new DefaultListModel<T>();
			for (T e : elements)
				model.addElement(e);

			result.setModel(model);

			if (selectedIndex < elements.size() && selectedIndex >= 0)
				result.setSelectedIndex(selectedIndex);
		}

		if (!(selectionMode < 0 || selectionMode > 2))
			result.setSelectionMode(selectionMode);

		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			return result;
		}

		setToolTip(element.getToolTip(), result);
		result.setEnabled(enabled);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JMenuBar
	 * 
	 * @param identifier
	 *            given identifier
	 * 
	 * @return the JMenuBar
	 */
	public JMenuBar generateJMenuBar(String identifier) {
		return generateJMenuBar(identifier, true);
	}

	/**
	 * generates a JMenuBar
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            component enabled
	 * 
	 * @return the JMenuBar
	 */
	public JMenuBar generateJMenuBar(String identifier, boolean enabled) {
		JMenuBar result = new JMenuBar();
		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			return result;
		}

		setToolTip(element.getToolTip(), result);
		result.setEnabled(enabled);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JMenu
	 * 
	 * @param identifier
	 *            given identifier
	 * 
	 * @return the JMenu
	 */
	public JMenu generateJMenu(String identifier) {
		return generateJMenu(identifier, true, null);
	}

	/**
	 * generates a JMenu
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            component enabled
	 * @param actionListener
	 *            the Action Listener
	 * 
	 * @return the JMenu
	 */
	public JMenu generateJMenu(String identifier, boolean enabled, ActionListener actionListener) {
		return generateJMenu(identifier, enabled, actionListener, null);
	}

	/**
	 * generates a JMenu
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            component enabled
	 * @param actionListener
	 *            the Action Listener
	 * @param actionCommand
	 *            an action command string
	 * 
	 * @return the JMenu
	 */
	public JMenu generateJMenu(String identifier, boolean enabled, ActionListener actionListener, String actionCommand) {
		JMenu result = new JMenu();
		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			result.setText("id is missing.");
			return result;
		}

		if (actionListener != null)
			result.addActionListener(actionListener);

		if (element.getIcon() != null)
			result.setIcon(getImageIcon(element.getIcon()));

		if (element.getLabel() != null)
			result.setText(element.getLabel());

		setToolTip(element.getToolTip(), result);
		setShortcut(element.getShortcut(), result);

		// if actioncommand is given take this, else take the one from the file
		if (actionCommand != null)
			setActionCommand(actionCommand, result);
		else
			setActionCommand(element.getActionCommand(), result);

		result.setEnabled(enabled);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JPopupMenu
	 * 
	 * @param identifier
	 *            given identifier
	 * 
	 * @return the JPopupMenu
	 */
	public JPopupMenu generateJPopupMenu(String identifier) {
		return generateJPopupMenu(identifier, true);
	}

	/**
	 * generates a JPopupMenu
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            component enabled
	 * 
	 * @return the JPopupMenu
	 */
	public JPopupMenu generateJPopupMenu(String identifier, boolean enabled) {
		JPopupMenu result = new JPopupMenu();

		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			return result;
		}

		if (element.getLabel() != null)
			result.setLabel(element.getLabel());

		setToolTip(element.getToolTip(), result);

		result.setEnabled(enabled);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JMenuItem
	 * 
	 * @param identifier
	 *            given identifier
	 * 
	 * @return the JMenuItem
	 */
	public JMenuItem generateJMenuItem(String identifier) {
		return generateJMenuItem(identifier, true, null);
	}

	/**
	 * generates a JMenuItem
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            component enabled
	 * @param actionListener
	 *            the Action Listener
	 * 
	 * @return the JMenuItem
	 */
	public JMenuItem generateJMenuItem(String identifier, boolean enabled, ActionListener actionListener) {
		return generateJMenuItem(identifier, enabled, actionListener, null);
	}

	/**
	 * generates a JMenuItem
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            component enabled
	 * @param actionListener
	 *            the Action Listener
	 * @param actionCommand
	 *            an action command string
	 * 
	 * @return the JMenuItem
	 */
	public JMenuItem generateJMenuItem(String identifier, boolean enabled, ActionListener actionListener, String actionCommand) {
		JMenuItem result = new JMenuItem();

		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			result.setText("id is missing.");
			return result;
		}

		if (actionListener != null)
			result.addActionListener(actionListener);

		if (element.getIcon() != null)
			result.setIcon(getImageIcon(element.getIcon()));

		if (element.getLabel() != null)
			result.setText(element.getLabel());

		setToolTip(element.getToolTip(), result);
		setShortcut(element.getShortcut(), result);

		// if actioncommand is given take this, else take the one from the file
		if (actionCommand != null)
			setActionCommand(actionCommand, result);
		else
			setActionCommand(element.getActionCommand(), result);

		result.setEnabled(enabled);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JCheckBoxMenuItem
	 * 
	 * @param identifier
	 *            given identifier
	 * @param isChecked
	 *            checkbox is checked
	 * 
	 * @return the JCheckBoxMenuItem
	 */
	public JCheckBoxMenuItem generateJCheckBoxMenuItem(String identifier, boolean isChecked) {
		return generateJCheckBoxMenuItem(identifier, true, isChecked, null);
	}

	/**
	 * generates a JCheckBoxMenuItem
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            component enabled
	 * @param isChecked
	 *            checkbox is checked
	 * @param actionListener
	 *            the Action Listener
	 * 
	 * @return the JCheckBoxMenuItem
	 */
	public JCheckBoxMenuItem generateJCheckBoxMenuItem(String identifier, boolean enabled, boolean isChecked, ActionListener actionListener) {
		return generateJCheckBoxMenuItem(identifier, enabled, isChecked, actionListener, null);
	}

	/**
	 * generates a JCheckBoxMenuItem
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            component enabled
	 * @param isChecked
	 *            checkbox is checked
	 * @param actionListener
	 *            the Action Listener
	 * @param actionCommand
	 *            an action command string
	 * 
	 * @return the JCheckBoxMenuItem
	 */
	public JCheckBoxMenuItem generateJCheckBoxMenuItem(String identifier, boolean enabled, boolean isChecked, ActionListener actionListener, String actionCommand) {
		JCheckBoxMenuItem result = new JCheckBoxMenuItem();

		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			result.setText("id is missing.");
			return result;
		}

		if (actionListener != null)
			result.addActionListener(actionListener);

		if (element.getIcon() != null)
			result.setIcon(getImageIcon(element.getIcon()));

		if (element.getLabel() != null)
			result.setText(element.getLabel());

		setToolTip(element.getToolTip(), result);
		setShortcut(element.getShortcut(), result);

		// if actioncommand is given take this, else take the one from the file
		if (actionCommand != null)
			setActionCommand(actionCommand, result);
		else
			setActionCommand(element.getActionCommand(), result);

		result.setEnabled(enabled);
		result.setSelected(isChecked);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JRadioButtonMenuItem
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            component enabled
	 * @param isChecked
	 *            checkbox is checked
	 * @param actionListener
	 *            the Action Listener
	 * 
	 * @return the JRadioButtonMenuItem
	 */
	public JRadioButtonMenuItem generateJRadioButtonMenuItem(String identifier, boolean enabled, boolean isChecked, ActionListener actionListener) {
		return generateJRadioButtonMenuItem(identifier, enabled, isChecked, actionListener, null);
	}

	/**
	 * generates a JRadioButtonMenuItem
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            component enabled
	 * @param isChecked
	 *            checkbox is checked
	 * @param actionListener
	 *            the Action Listener
	 * @param actionCommand
	 *            an action command string
	 * 
	 * @return the JRadioButtonMenuItem
	 */
	public JRadioButtonMenuItem generateJRadioButtonMenuItem(String identifier, boolean enabled, boolean isChecked, ActionListener actionListener, String actionCommand) {
		JRadioButtonMenuItem result = new JRadioButtonMenuItem();

		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			result.setText("id is missing.");
			return result;
		}

		if (actionListener != null)
			result.addActionListener(actionListener);

		if (element.getIcon() != null)
			result.setIcon(getImageIcon(element.getIcon()));

		if (element.getLabel() != null)
			result.setText(element.getLabel());

		setToolTip(element.getToolTip(), result);
		setShortcut(element.getShortcut(), result);

		// if actioncommand is given take this, else take the one from the file
		if (actionCommand != null)
			setActionCommand(actionCommand, result);
		else
			setActionCommand(element.getActionCommand(), result);

		result.setEnabled(enabled);
		result.setSelected(isChecked);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JTable
	 * 
	 * @param identifier
	 *            given identifier
	 * 
	 * @return the JTable
	 */
	public JTable generateJTable(String identifier) {
		return generateJTable(identifier, true);
	}

	/**
	 * generates a JTable
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            component enabled
	 * 
	 * @return the JTable
	 */
	public JTable generateJTable(String identifier, boolean enabled) {
		JTable result = new JTable();
		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			return result;
		}

		setToolTip(element.getToolTip(), result);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JTree
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            component enabled
	 * 
	 * @return the JTree
	 */
	public JTree generateJTree(String identifier, boolean enabled) {
		JTree result = new JTree();
		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			return result;
		}

		setToolTip(element.getToolTip(), result);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JTabbedPane
	 * 
	 * @param identifier
	 *            given identifier
	 * @param enabled
	 *            component enabled
	 * 
	 * @return the JTabbedPane
	 */
	public JTabbedPane generateJTabbedPane(String identifier, boolean enabled) {
		JTabbedPane result = new JTabbedPane();
		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			return result;
		}

		setToolTip(element.getToolTip(), result);

		register(identifier, result);
		return result;
	}

	/**
	 * generates a JSlider
	 * 
	 * @param identifier
	 *            given identifier
	 * 
	 * @return the JSlider
	 */
	public JSlider generateJSlider(String identifier) {
		JSlider result = new JSlider();
		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			return result;
		}

		setToolTip(element.getToolTip(), result);

		register(identifier, result);

		return result;
	}

	/**
	 * adds a JFrame to the translator
	 * 
	 * @param identifier
	 *            given identifier
	 * @param jFrame
	 *            given JFrame
	 */
	public void addJFrame(String identifier, JFrame jFrame) {
		Element element = ressources.getElement(identifier, this.locale);
		if (element == null)
			jFrame.setTitle("id is missing.");
		else
			jFrame.setTitle(element.getLabel());

		register(identifier, jFrame);
	}

	/**
	 * adds a Custom Panel to the Translator
	 * 
	 * @param panel
	 *            the custom Panel
	 * @param identifier
	 *            panels identifier
	 */
	public void addCustomJPanel(JPanel panel, String identifier) {
		Element element = ressources.getElement(identifier, this.locale);
		if (element == null) {
			return;
		}

		setToolTip(element.getToolTip(), panel);

		register(identifier, panel);
	}

	/**
	 * adds a new Tab to the given Pane with the given TabIdentifier
	 * 
	 * @param pane
	 *            given TabbedPane
	 * @param tab
	 *            given tab
	 * @param TabIdentifier
	 *            given Tab identifier
	 */
	public void addTab(JTabbedPane pane, Component tab, String TabIdentifier) {
		Element element = ressources.getElement(TabIdentifier, this.locale);
		if (element == null) {
			pane.addTab("id is missing", tab);
			return;
		}

		if (element.getIcon() != null) {
			ImageIcon img = getImageIcon(element.getIcon());
			if (img == null) {
				System.err.println("couldnt find image " + element.getIcon());
				pane.addTab(element.getLabel(), tab);
			} else
				pane.addTab(element.getLabel(), img, tab);
		} else
			pane.addTab(element.getLabel(), tab);

		if (!this.components.containsKey(Integer.toString(pane.hashCode()))) {
			TabComponent c = new TabComponent(pane);
			c.addTab(TabIdentifier);

			if (element.getShortcut() != null)
				pane.setMnemonicAt(0, element.getShortcut());

			pane.setToolTipTextAt(0, element.getToolTip());

			register(Integer.toString(pane.hashCode()), c);
		} else {
			if (element.getShortcut() != null)
				pane.setMnemonicAt(((TabComponent) this.components.get(Integer.toString(pane.hashCode()))).getTabs().size(), element.getShortcut());

			pane.setToolTipTextAt(((TabComponent) this.components.get(Integer.toString(pane.hashCode()))).getTabs().size(), element.getToolTip());

			((TabComponent) this.components.get(Integer.toString(pane.hashCode()))).addTab(TabIdentifier);
		}
	}

	/**
	 * gets the image icon at the given path
	 * 
	 * @param path
	 *            given path
	 * 
	 * @return the image icon
	 */
	private ImageIcon getImageIcon(String path) {
		BufferedImage img = Graphics.getImageFromJar(path, this.getClass());

		if (img == null)
			return null;

		return new ImageIcon(img);
	}

	/**
	 * sets the given shortcut to the given component
	 * 
	 * @param key
	 *            given shortcut
	 * @param component
	 *            given component
	 */
	private void setShortcut(Character key, AbstractButton component) {
		if (key == null)
			return;

		component.setMnemonic(key);
	}

	/**
	 * sets the given toolTip to the given component
	 * 
	 * @param toolTip
	 *            given toolTip
	 * @param component
	 *            given component
	 */
	private void setToolTip(String toolTip, JComponent component) {
		if (toolTip == null || toolTip.trim().length() < 1) {
			component.setToolTipText(null);
			return;
		}

		component.setToolTipText(toolTip);
	}

	/**
	 * sets the given ActionCommand to the given component
	 * 
	 * @param cmd
	 *            given command
	 * @param component
	 *            given component
	 */
	private void setActionCommand(String cmd, AbstractButton component) {
		if (cmd == null || cmd.trim().length() < 1) {
			component.setActionCommand(null);
			return;
		}

		component.setActionCommand(cmd);
	}

	/**
	 * sets the given ActionCommand to the given component
	 * 
	 * @param cmd
	 *            given command
	 * @param component
	 *            given component
	 */
	private void setActionCommand(String cmd, JComboBox<?> component) {
		if (cmd == null || cmd.trim().length() < 1) {
			component.setActionCommand(null);
			return;
		}

		component.setActionCommand(cmd);
	}

	/**
	 * registers the given component under the given identifier
	 * 
	 * @param identifier
	 *            given identifier
	 * @param component
	 *            given component to register
	 */
	private void register(String identifier, Component component) {
		if (this.components.containsKey(identifier)) {
			System.err.println("The identifier '" + identifier + "' is already used. The second one will be ignored.");
			return;
		}

		this.components.put(identifier, component);
	}

	/**
	 * gets the message for the given identifier in the given language
	 * 
	 * @param identifier
	 *            given identifier
	 * 
	 * @return the message in the current Locale
	 */
	public String getMessage(String identifier) {
		return this.ressources.getMessage(identifier, locale);
	}

	/**
	 * sets a new Locale
	 * 
	 * @param locale
	 *            new Locale
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * gets the current set locale
	 * 
	 * @return the current locale
	 */
	public Locale getLocale() {
		return this.locale;
	}

	/**
	 * translates all components to the set locale
	 */
	public void translate() {
		ressources.addRessource(this.locale);
		for (String id : this.components.keySet())
			updateComponent(id, this.components.get(id));
	}

	/**
	 * updates the given component to the set locale
	 * 
	 * @param component
	 *            given component
	 */
	private void updateComponent(String identifier, Component component) {
		if (component instanceof JButton) {
			JButton result = (JButton) component;
			Element element = ressources.getElement(identifier, locale);
			if (element == null) {
				result.setText("id is missing.");
				result.setIcon(null);
				return;
			}
			if (element.getLabel() != null)
				result.setText(element.getLabel());
			else
				result.setText("");

			if (element.getIcon() != null) {
				ImageIcon img = getImageIcon(element.getIcon());
				if (img == null)
					System.err.println("couldnt find image " + element.getIcon());

				result.setIcon(img);
			} else
				result.setIcon(null);

			setToolTip(element.getToolTip(), result);
			setShortcut(element.getShortcut(), result);
			setActionCommand(element.getActionCommand(), result);
		} else if (component instanceof JToggleButton) {
			JToggleButton result = (JToggleButton) component;
			Element element = ressources.getElement(identifier, locale);
			if (element == null) {
				result.setText("id is missing.");
				result.setIcon(null);
				return;
			}
			if (element.getLabel() != null)
				result.setText(element.getLabel());
			else
				result.setText("");

			if (element.getIcon() != null) {
				ImageIcon img = getImageIcon(element.getIcon());
				if (img == null)
					System.err.println("couldnt find image " + element.getIcon());

				result.setIcon(img);
			} else
				result.setIcon(null);

			setToolTip(element.getToolTip(), result);
			setShortcut(element.getShortcut(), result);
			setActionCommand(element.getActionCommand(), result);
		} else if (component instanceof JTextField) {
			JTextField result = (JTextField) component;
			Element element = ressources.getElement(identifier, locale);
			if (element == null) {
				return;
			}

			setToolTip(element.getToolTip(), result);
		} else if (component instanceof JPanel) {
			Element element = ressources.getElement(identifier, locale);

			if (((JPanel) component).getBorder() instanceof TitledBorder) {
				TitledBorder b = (TitledBorder) ((JPanel) component).getBorder();
				if (element == null) {
					b.setTitle("id is missing.");
					return;
				}
				b.setTitle(element.getLabel());
			}

			if ((JPanel) component instanceof JPanel)
				setToolTip(element.getToolTip(), (JPanel) component);
		} else if (component instanceof JLabel) {
			JLabel result = (JLabel) component;
			Element element = ressources.getElement(identifier, this.locale);
			if (element == null) {
				result.setText("id is missing.");
				result.setIcon(null);
				return;
			}
			if (element.getLabel() != null)
				result.setText(element.getLabel());
			else
				result.setText("");

			if (element.getIcon() != null) {
				ImageIcon img = getImageIcon(element.getIcon());
				if (img == null)
					System.err.println("couldnt find image " + element.getIcon());

				result.setIcon(img);
			} else
				result.setIcon(null);

			setToolTip(element.getToolTip(), result);
		} else if (component instanceof JComboBox) {
			JComboBox<?> result = (JComboBox<?>) component;
			Element element = ressources.getElement(identifier, this.locale);
			if (element == null) {
				return;
			}

			setToolTip(element.getToolTip(), result);
			setActionCommand(element.getActionCommand(), result);
		} else if (component instanceof JCheckBox) {
			JCheckBox result = (JCheckBox) component;
			Element element = ressources.getElement(identifier, this.locale);
			if (element == null) {
				result.setText("id is missing.");
				result.setIcon(null);
				return;
			}

			if (element.getLabel() != null)
				result.setText(element.getLabel());

			if (element.getIcon() != null) {
				ImageIcon img = getImageIcon(element.getIcon());
				if (img == null)
					System.err.println("couldnt find image " + element.getIcon());

				result.setIcon(img);
			} else
				result.setIcon(null);

			setToolTip(element.getToolTip(), result);
			setShortcut(element.getShortcut(), result);
			setActionCommand(element.getActionCommand(), result);
		} else if (component instanceof JRadioButton) {
			JRadioButton result = (JRadioButton) component;
			Element element = ressources.getElement(identifier, this.locale);
			if (element == null) {
				result.setText("id is missing.");
				result.setIcon(null);
				return;
			}

			if (element.getLabel() != null)
				result.setText(element.getLabel());

			if (element.getIcon() != null) {
				ImageIcon img = getImageIcon(element.getIcon());
				if (img == null)
					System.err.println("couldnt find image " + element.getIcon());

				result.setIcon(img);
			} else
				result.setIcon(null);

			setToolTip(element.getToolTip(), result);
			setShortcut(element.getShortcut(), result);
			setActionCommand(element.getActionCommand(), result);
		} else if (component instanceof JTextArea) {
			JTextArea result = (JTextArea) component;
			Element element = ressources.getElement(identifier, this.locale);
			if (element == null) {
				return;
			}

			setToolTip(element.getToolTip(), result);
		} else if (component instanceof JPasswordField) {
			JPasswordField result = (JPasswordField) component;
			Element element = ressources.getElement(identifier, this.locale);
			if (element == null) {
				return;
			}

			setToolTip(element.getToolTip(), result);
		} else if (component instanceof JTextPane) {
			JTextPane result = (JTextPane) component;
			Element element = ressources.getElement(identifier, this.locale);
			if (element == null) {
				return;
			}

			setToolTip(element.getToolTip(), result);
		} else if (component instanceof JEditorPane) {
			JEditorPane result = (JEditorPane) component;
			Element element = ressources.getElement(identifier, this.locale);
			if (element == null) {
				return;
			}

			setToolTip(element.getToolTip(), result);
		} else if (component instanceof JList) {
			JList<?> result = (JList<?>) component;
			Element element = ressources.getElement(identifier, this.locale);
			if (element == null) {
				return;
			}

			setToolTip(element.getToolTip(), result);
		} else if (component instanceof JMenuBar) {
			JMenuBar result = (JMenuBar) component;
			Element element = ressources.getElement(identifier, this.locale);
			if (element == null) {
				return;
			}

			setToolTip(element.getToolTip(), result);
		} else if (component instanceof JMenu) {
			JMenu result = (JMenu) component;
			Element element = ressources.getElement(identifier, this.locale);
			if (element == null) {
				result.setText("id is missing.");
				return;
			}

			if (element.getIcon() != null) {
				ImageIcon img = getImageIcon(element.getIcon());
				if (img == null)
					System.err.println("couldnt find image " + element.getIcon());

				result.setIcon(img);
			} else
				result.setIcon(null);

			if (element.getLabel() != null)
				result.setText(element.getLabel());

			setToolTip(element.getToolTip(), result);
			setShortcut(element.getShortcut(), result);
			setActionCommand(element.getActionCommand(), result);
		} else if (component instanceof JPopupMenu) {
			JPopupMenu result = (JPopupMenu) component;
			Element element = ressources.getElement(identifier, this.locale);
			if (element == null) {
				return;
			}

			setToolTip(element.getToolTip(), result);
		} else if (component instanceof JMenuItem) {
			JMenuItem result = (JMenuItem) component;
			Element element = ressources.getElement(identifier, this.locale);
			if (element == null) {
				result.setText("id is missing.");
				result.setIcon(null);
				return;
			}

			if (element.getIcon() != null) {
				ImageIcon img = getImageIcon(element.getIcon());
				if (img == null)
					System.err.println("couldnt find image " + element.getIcon());

				result.setIcon(img);
			} else
				result.setIcon(null);

			if (element.getLabel() != null)
				result.setText(element.getLabel());

			setToolTip(element.getToolTip(), result);
			setShortcut(element.getShortcut(), result);
			setActionCommand(element.getActionCommand(), result);
		} else if (component instanceof JCheckBoxMenuItem) {
			JCheckBoxMenuItem result = (JCheckBoxMenuItem) component;
			Element element = ressources.getElement(identifier, this.locale);
			if (element == null) {
				result.setText("id is missing.");
				result.setIcon(null);
				return;
			}

			if (element.getIcon() != null) {
				ImageIcon img = getImageIcon(element.getIcon());
				if (img == null)
					System.err.println("couldnt find image " + element.getIcon());

				result.setIcon(img);
			} else
				result.setIcon(null);

			if (element.getLabel() != null)
				result.setText(element.getLabel());

			setToolTip(element.getToolTip(), result);
			setShortcut(element.getShortcut(), result);
			setActionCommand(element.getActionCommand(), result);
		} else if (component instanceof JRadioButtonMenuItem) {
			JRadioButtonMenuItem result = (JRadioButtonMenuItem) component;
			Element element = ressources.getElement(identifier, this.locale);
			if (element == null) {
				result.setText("id is missing.");
				result.setIcon(null);
				return;
			}

			if (element.getIcon() != null) {
				ImageIcon img = getImageIcon(element.getIcon());
				if (img == null)
					System.err.println("couldnt find image " + element.getIcon());

				result.setIcon(img);
			} else
				result.setIcon(null);

			if (element.getLabel() != null)
				result.setText(element.getLabel());

			setToolTip(element.getToolTip(), result);
			setShortcut(element.getShortcut(), result);
			setActionCommand(element.getActionCommand(), result);
		} else if (component instanceof JFrame) {
			JFrame result = (JFrame) component;
			Element element = ressources.getElement(identifier, this.locale);
			if (element == null)
				result.setTitle("id is missing.");
			else
				result.setTitle(element.getLabel());
		} else if (component instanceof JFormattedTextField) {
			JFormattedTextField result = (JFormattedTextField) component;
			Element element = ressources.getElement(identifier, this.locale);
			if (element == null) {
				return;
			}

			setToolTip(element.getToolTip(), result);
		} else if (component instanceof JTable) {
			JTable result = (JTable) component;

			Element element = ressources.getElement(identifier, this.locale);
			if (element == null) {
				return;
			}

			setToolTip(element.getToolTip(), result);
		} else if (component instanceof TabComponent) {
			TabComponent comp = (TabComponent) component;
			JTabbedPane pane = comp.getPane();

			for (DataDouble<Integer, String> data : comp.getTabs()) {
				Element element = ressources.getElement(data.getSecond(), this.locale);
				if (element == null) {
					pane.setTitleAt(data.getFirst(), "id is missing.");
					continue;
				}

				pane.setTitleAt(data.getFirst(), element.getLabel());

				if (element.getIcon() != null) {
					ImageIcon img = getImageIcon(element.getIcon());
					if (img == null) {
						System.err.println("couldnt find image " + element.getIcon());
					} else
						pane.setIconAt(data.getFirst(), img);
				} else
					pane.setIconAt(data.getFirst(), null);

				if (element.getShortcut() != null)
					pane.setMnemonicAt(data.getFirst(), element.getShortcut());

				pane.setToolTipTextAt(data.getFirst(), element.getToolTip());
			}
		} else if (component instanceof JTabbedPane) {
			JTabbedPane result = (JTabbedPane) component;

			Element element = ressources.getElement(identifier, this.locale);
			if (element == null) {
				return;
			}

			setToolTip(element.getToolTip(), result);
		} else if (component instanceof JTree) {
			JTree result = (JTree) component;

			Element element = ressources.getElement(identifier, this.locale);
			if (element == null) {
				return;
			}

			setToolTip(element.getToolTip(), result);
		} else if (component instanceof JSlider) {
			JSlider result = (JSlider) component;

			Element element = ressources.getElement(identifier, this.locale);
			if (element == null) {
				return;
			}

			setToolTip(element.getToolTip(), result);
		}
	}
}