package notepad;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public final class ChangeFontAction extends JDialog implements ListSelectionListener, ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
	String[] fontStyles = { "  Plain  ", "  Bold  ", "  Italic  ", "  Bold+Italic  " };

	List lst = new List();
	JList fontsList;
	JList fontStyleList;
	JList fontSizeList;
	JPanel jp1, jp2;
	DefaultListModel model;
	JLabel displayLabel;
	JButton ok, cancel;
	JTextPane textPane;

	public ChangeFontAction(JTextPane tx) {
		textPane = tx;

		Container cp = getContentPane();

		fontsList = new JList(fontNames);
		fontStyleList = new JList(fontStyles);

		fontsList.setFont(new Font("Calibri", Font.PLAIN, 14));
		fontStyleList.setFont(new Font("Calibri", Font.PLAIN, 14));

		model = new DefaultListModel();
		fontSizeList = new JList(model);
		fontSizeList.setFont(new Font("Calibri", Font.PLAIN, 14));

		for (int i = 8; i <= 74; i++) {
			model.addElement("  " + i + "   ");
		}

		fontsList.setSelectedIndex(8);
		fontStyleList.setSelectedIndex(0);
		fontSizeList.setSelectedIndex(21);

		fontsList.addListSelectionListener(this);
		fontStyleList.addListSelectionListener(this);
		fontSizeList.addListSelectionListener(this);

		jp1 = new JPanel();
		jp2 = new JPanel();
		JPanel jp3 = new JPanel();
		jp3.add(new JScrollPane(fontsList));

		JPanel jp4 = new JPanel();
		jp4.setLayout(new GridLayout(0, 2));
		jp4.add(new JScrollPane(fontStyleList));
		jp4.add(new JScrollPane(fontSizeList));

		jp1.add(jp3, BorderLayout.WEST);
		jp1.add(jp4, BorderLayout.EAST);

		displayLabel = new JLabel("Java Assignment", JLabel.CENTER);
		displayLabel.setFont(new Font("Arial", Font.PLAIN, 21));
		displayLabel.setSize(50, 300);

		jp1.add(displayLabel);

		ok = new JButton("  OK  ");
		cancel = new JButton("  Cancel  ");

		ok.addActionListener(this);
		cancel.addActionListener(this);

		jp2.add(ok);
		jp2.add(cancel);

		cp.add(jp1, BorderLayout.CENTER);
		cp.add(jp2, BorderLayout.SOUTH);

	}

	@Override
	public void valueChanged(ListSelectionEvent evt) {
		String fontname = fontsList.getSelectedValue().toString();
		String fontstyle = fontStyleList.getSelectedValue().toString().trim();
		int fontsize = Integer.parseInt(fontSizeList.getSelectedValue().toString().trim());

		switch (fontstyle) {
		case "Plain":
			displayLabel.setFont(new Font(fontname, Font.PLAIN, fontsize));
			break;

		case "Bold":
			displayLabel.setFont(new Font(fontname, Font.BOLD, fontsize));
			break;

		case "Italic":
			displayLabel.setFont(new Font(fontname, Font.ITALIC, fontsize));
			break;

		case "Bold+Italic":
			displayLabel.setFont(new Font(fontname, Font.BOLD + Font.ITALIC, fontsize));
			break;
		}
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		Object source = evt.getSource();
		if (source == ok) {
			String fontname = fontsList.getSelectedValue().toString();
			String fontstyle = fontStyleList.getSelectedValue().toString().trim();
			int fontsize = Integer.parseInt(fontSizeList.getSelectedValue().toString().trim());

			switch (fontstyle) {
			case "Plain":
				textPane.setFont(new Font(fontname, Font.PLAIN, fontsize));
				break;

			case "Bold":
				textPane.setFont(new Font(fontname, Font.BOLD, fontsize));
				break;

			case "Italic":
				textPane.setFont(new Font(fontname, Font.ITALIC, fontsize));
				break;

			case "Bold Italic":
				textPane.setFont(new Font(fontname, Font.BOLD + Font.ITALIC, fontsize));
				break;
			}

			this.dispose();
		} else if (source == cancel) {
			this.dispose();
		}
	}

}
