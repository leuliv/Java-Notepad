package notepad;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;

public class MainFrame extends JFrame implements ActionListener {

	// Content pane includes all the components
	private JPanel contentPane;

	// The four main components
	JMenuBar menuBar;
	JTabbedPane tabbedPane;
	JToolBar statusBar;
	JToolBar toolBar;

	// JLabels for the status bar
	JLabel filenameLabel = new JLabel("");
	JLabel rowLabel = new JLabel("Row : ");
	JLabel colLabel = new JLabel("Col : ");

	// counts the number of tabs created
	static int count = 1;

	// Classes that control the undo and re-do actions
	UndoManager undoManager = new UndoManager();
	Action undoAction = new PerformUndoAction(undoManager);
	Action redoAction = new PerformRedoAction(undoManager);

	// Gets the system clip-board(Used in cut,copy & paste)
	Clipboard clip = getToolkit().getSystemClipboard();

	// pop-up menu for the right click action
	JPopupMenu popupMenu;

	// Components for the find and replace actions
	JTextField findText;
	JTextField replaceText;
	JButton replaceButton;
	JButton cancelButton;
	JDialog jd;
	int _lineCount;

	// booleans that control the visibility of both the tool-bar and status-bar
	boolean toolbarVisible = true;
	boolean statusbarVisible = true;

	// Launch the application(Main Method)
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Create the frame(Constructor)
	public MainFrame() {
		setIconImage(
				Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/notepad/images/notebook_icon.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 646, 380);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		setTitle("Notepad Java Assignment");

		menuBar = new JMenuBar();

		JMenu file = new JMenu("  File  ");

		JMenuItem file_new = new JMenuItem("  New                                      ");
		file_new.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/new_file.png")));
		file_new.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));

		JMenuItem file_open = new JMenuItem("  Open ");
		file_open.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/open_file.png")));
		file_open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));

		JMenuItem file_save = new JMenuItem("  Save ");
		file_save.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/save_file.png")));
		file_save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));

		JMenuItem file_saveas = new JMenuItem("  Save As");
		file_saveas.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/save_as.png")));
		file_saveas.setAccelerator(KeyStroke.getKeyStroke("F2"));

		JMenuItem file_saveall = new JMenuItem("  Save All");
		file_saveall.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/save_all.png")));
		file_saveall
				.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));

		JMenuItem file_close = new JMenuItem("  Close");
		file_close.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/close_tab.png")));
		file_close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));

		JMenuItem file_closeall = new JMenuItem("  Close All");
		file_closeall.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/close_all.png")));
		file_closeall
				.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK));

		JMenuItem file_exit = new JMenuItem("  Exit");
		file_exit.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/exit_window.png")));
		file_exit.setAccelerator(KeyStroke.getKeyStroke("F4"));

		file_new.addActionListener(this);
		file_open.addActionListener(this);
		file_save.addActionListener(this);
		file_saveas.addActionListener(this);
		file_saveall.addActionListener(this);
		file_close.addActionListener(this);
		file_closeall.addActionListener(this);
		file_exit.addActionListener(this);

		JMenu edit = new JMenu("  Edit  ");

		// creating edit menu items
		JMenuItem edit_cut = new JMenuItem("  Cut                                      ");
		edit_cut.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/cut.png")));
		edit_cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));

		JMenuItem edit_copy = new JMenuItem("  Copy");
		edit_copy.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/copy.png")));
		edit_copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));

		JMenuItem edit_paste = new JMenuItem("  Paste");
		edit_paste.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/paste.png")));
		edit_paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));

		JMenuItem edit_undo = new JMenuItem("  Undo");
		edit_undo.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/undo.png")));
		edit_undo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));

		JMenuItem edit_redo = new JMenuItem("  Redo");
		edit_redo.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/redo.png")));
		edit_redo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));

		JMenuItem edit_find = new JMenuItem("  Find");
		edit_find.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/find.png")));
		edit_find.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));

		JMenuItem edit_replace = new JMenuItem("  Replace");
		edit_replace.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/replace.png")));
		edit_replace.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));

		JMenuItem edit_goto = new JMenuItem("  GoTo");
		edit_goto.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/goto.png")));
		edit_goto.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_G, ActionEvent.CTRL_MASK));

		JMenuItem edit_selectall = new JMenuItem("  Select All");
		edit_selectall.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/select_all.png")));

		JMenu edit_changecase = new JMenu("  Change Case");
		edit_changecase.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/change_case.png")));

		JMenuItem edit_changecase_upper = new JMenuItem("  Upper Case   ");
		edit_changecase_upper.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/uppercase.png")));
		JMenuItem edit_changecase_lower = new JMenuItem("  Lower Case   ");
		edit_changecase_lower.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/lowercase.png")));
		JMenuItem edit_changecase_sentence = new JMenuItem("  Sentence Case   ");
		edit_changecase_sentence.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/sentence_c.png")));

		edit_cut.addActionListener(this);
		edit_copy.addActionListener(this);
		edit_paste.addActionListener(this);
		edit_goto.addActionListener(this);
		edit_find.addActionListener(this);
		edit_replace.addActionListener(this);
		edit_selectall.addActionListener(this);
		edit_changecase_upper.addActionListener(this);
		edit_changecase_lower.addActionListener(this);
		edit_changecase_sentence.addActionListener(this);

		edit_undo.addActionListener(undoAction);
		edit_redo.addActionListener(redoAction);

		JMenu view = new JMenu("  View  ");

		// creating view menu items
		JMenuItem view_font = new JMenuItem("  Font                               ");
		view_font.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/font.png")));
		view_font.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.ALT_MASK));

		JMenuItem view_textcolor = new JMenuItem("  Text Color");
		view_textcolor.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/text_color.png")));
		JMenuItem view_backcolor = new JMenuItem("  Background Color");
		view_backcolor.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/textArea_color.png")));

		JCheckBoxMenuItem viewtoolBar = new JCheckBoxMenuItem("  Hide ToolBar");

		if (toolbarVisible) {
			viewtoolBar.setSelected(false);
		} else {
			viewtoolBar.setSelected(true);
		}

		JCheckBoxMenuItem view_statusstrip = new JCheckBoxMenuItem("  Hide Statusbar");

		if (statusbarVisible) {
			view_statusstrip.setSelected(false);
		} else {
			view_statusstrip.setSelected(true);
		}

		view_font.addActionListener(this);
		view_textcolor.addActionListener(this);
		view_backcolor.addActionListener(this);
		viewtoolBar.addActionListener(this);
		view_statusstrip.addActionListener(this);

		JMenu help = new JMenu("  Help  ");

		JMenuItem explainSourceCode = new JMenuItem("Explain Source code");
		explainSourceCode.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/explain.png")));
		JMenuItem seeGroupMembers = new JMenuItem("See Group members");
		seeGroupMembers.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/group_members.png")));
		JMenuItem softwareDetails = new JMenuItem("Software Details");
		softwareDetails.setIcon(new ImageIcon(MainFrame.class.getResource("/notepad/images/software_details.png")));

		// adding actions to help menu items
		explainSourceCode.addActionListener(this);
		seeGroupMembers.addActionListener(this);
		softwareDetails.addActionListener(this);

		file.add(file_new);
		file.addSeparator();
		file.add(file_open);
		file.addSeparator();
		file.add(file_save);
		file.add(file_saveas);
		file.add(file_saveall);
		file.addSeparator();
		file.add(file_close);
		file.add(file_closeall);
		file.addSeparator();
		file.add(file_exit);

		// add file menu to menu bar mb
		menuBar.add(file);

		// adding edit menuitems to edit menu
		edit.add(edit_cut);
		edit.add(edit_copy);
		edit.add(edit_paste);
		edit.addSeparator();
		edit.add(edit_undo);
		edit.add(edit_redo);
		edit.addSeparator();
		edit.add(edit_find);
		edit.add(edit_replace);
		edit.add(edit_goto);
		edit.addSeparator();
		edit.add(edit_selectall);
		edit.addSeparator();
		edit.add(edit_changecase);
		edit_changecase.add(edit_changecase_upper);
		edit_changecase.add(edit_changecase_lower);
		edit_changecase.addSeparator();
		edit_changecase.add(edit_changecase_sentence);

		// add edit menu to mb
		menuBar.add(edit);

		// adding view menuitems to view menu
		view.add(view_font);
		view.addSeparator();
		view.add(view_textcolor);
		view.add(view_backcolor);
		view.addSeparator();
		view.add(viewtoolBar);
		view.add(view_statusstrip);

		menuBar.add(view);

		help.add(explainSourceCode);
		help.addSeparator();
		help.add(seeGroupMembers);
		help.add(softwareDetails);

		menuBar.add(help);

		setJMenuBar(menuBar);

		tabbedPane = new JTabbedPane();
		tabbedPane.setFont(new Font("Calibri", Font.PLAIN, 14));

		toolBar = new JToolBar();
		toolBar.setFloatable(false);

		// creating toolbar buttons
		JButton toolbar_new = new JButton(new ImageIcon(MainFrame.class.getResource("/notepad/images/new_file.png")));
		toolbar_new.setToolTipText("New");
		toolbar_new.addActionListener(new ToolBarButtonsAction("new"));

		JButton toolbar_open = new JButton(new ImageIcon(MainFrame.class.getResource("/notepad/images/open_file.png")));
		toolbar_open.setToolTipText("Open");
		toolbar_open.addActionListener(new ToolBarButtonsAction("open"));

		JButton toolbar_save = new JButton(new ImageIcon(this.getClass().getResource("/notepad/images/save_file.png")));
		toolbar_save.setToolTipText("Save");
		toolbar_save.addActionListener(new ToolBarButtonsAction("save"));

		JButton toolbar_saveas = new JButton(new ImageIcon(MainFrame.class.getResource("/notepad/images/save_as.png")));
		toolbar_saveas.setToolTipText("Save As");
		toolbar_saveas.addActionListener(new ToolBarButtonsAction("saveas"));

		JButton toolbar_saveall = new JButton(
				new ImageIcon(MainFrame.class.getResource("/notepad/images/save_all.png")));
		toolbar_saveall.setToolTipText("Save All");
		toolbar_saveall.addActionListener(new ToolBarButtonsAction("saveall"));

		JButton toolbar_cut = new JButton(new ImageIcon(this.getClass().getResource("/notepad/images/cut.png")));
		toolbar_cut.setToolTipText("Cut");
		toolbar_cut.addActionListener(new ToolBarButtonsAction("cut"));

		JButton toolbar_copy = new JButton(new ImageIcon(this.getClass().getResource("/notepad/images/copy.png")));
		toolbar_copy.setToolTipText("Copy");
		toolbar_copy.addActionListener(new ToolBarButtonsAction("copy"));

		JButton toolbar_paste = new JButton(new ImageIcon(this.getClass().getResource("/notepad/images/paste.png")));
		toolbar_paste.setToolTipText("Paste");
		toolbar_paste.addActionListener(new ToolBarButtonsAction("paste"));

		JButton toolbar_undo = new JButton(new ImageIcon(this.getClass().getResource("/notepad/images/undo.png")));
		toolbar_undo.setToolTipText("Undo");
		toolbar_undo.addActionListener(new ToolBarButtonsAction("undo"));

		JButton toolbar_redu = new JButton(new ImageIcon(this.getClass().getResource("/notepad/images/redo.png")));
		toolbar_redu.setToolTipText("Redo");
		toolbar_redu.addActionListener(new ToolBarButtonsAction("redo"));

		JButton toolbar_goto = new JButton(new ImageIcon(this.getClass().getResource("/notepad/images/goto.png")));
		toolbar_goto.setToolTipText("GoTo");
		toolbar_goto.addActionListener(new ToolBarButtonsAction("goto"));

		JButton toolbar_font = new JButton(new ImageIcon(this.getClass().getResource("/notepad/images/font.png")));
		toolbar_font.setToolTipText("Set Font");
		toolbar_font.addActionListener(new ToolBarButtonsAction("font"));

		JButton toolbar_close = new JButton(
				new ImageIcon(MainFrame.class.getResource("/notepad/images/close_tab.png")));
		toolbar_close.setToolTipText("Close Tab");
		toolbar_close.addActionListener(new ToolBarButtonsAction("close"));

		// adding toolbar buttons to toolBar object
		toolBar.add(toolbar_new);
		toolBar.addSeparator(new Dimension(4, 4));
		toolBar.add(toolbar_open);
		toolBar.addSeparator(new Dimension(4, 4));
		toolBar.add(toolbar_save);
		toolBar.add(toolbar_saveas);
		toolBar.add(toolbar_saveall);
		toolBar.addSeparator(new Dimension(6, 6));
		toolBar.add(toolbar_cut);
		toolBar.add(toolbar_copy);
		toolBar.add(toolbar_paste);
		toolBar.addSeparator(new Dimension(6, 6));
		toolBar.add(toolbar_undo);
		toolBar.add(toolbar_redu);
		toolBar.addSeparator(new Dimension(6, 6));
		toolBar.add(toolbar_goto);
		toolBar.add(toolbar_font);
		toolBar.addSeparator(new Dimension(6, 6));
		toolBar.add(toolbar_close);

		toolbar_undo.addActionListener(undoAction);
		toolbar_redu.addActionListener(redoAction);

		statusBar = new JToolBar();
		statusBar.setFloatable(false);

		JLabel readylabel;
		readylabel = new JLabel("Java Assignment");
		readylabel.setFont(new Font("Calibri", Font.PLAIN, 15));
		filenameLabel.setFont(new Font("Calibri", Font.PLAIN, 15));
		statusBar.add(readylabel);
		statusBar.add(new JLabel("                          "));
		statusBar.add(filenameLabel);
		statusBar.add(new JLabel("                                                            "));
		statusBar.add(rowLabel);
		statusBar.add(new JLabel("     "));
		statusBar.add(colLabel);

		popupMenu = new JPopupMenu();

		JMenuItem popup_edit_cut = new JMenuItem("  Cut                                     ");
		popup_edit_cut.setIcon(new ImageIcon(this.getClass().getResource("/notepad/images/cut.png")));
		popup_edit_cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));

		JMenuItem popup_edit_copy = new JMenuItem("  Copy");
		popup_edit_copy.setIcon(new ImageIcon(this.getClass().getResource("/notepad/images/copy.png")));
		popup_edit_copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));

		JMenuItem popup_edit_paste = new JMenuItem("  Paste");
		popup_edit_paste.setIcon(new ImageIcon(this.getClass().getResource("/notepad/images/paste.png")));
		popup_edit_paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));

		JMenuItem popup_edit_selectall = new JMenuItem("  Select All");
		popup_edit_selectall.setIcon(new ImageIcon(this.getClass().getResource("/notepad/images/select_all.png")));

		JMenu popup_edit_changecase = new JMenu("  Change Case");
		popup_edit_changecase.setIcon(new ImageIcon(this.getClass().getResource("/notepad/images/change_case.png")));
		JMenuItem popup_edit_changecase_upper = new JMenuItem("  Upper Case   ");
		popup_edit_changecase_upper
				.setIcon(new ImageIcon(this.getClass().getResource("/notepad/images/uppercase.png")));
		JMenuItem popup_edit_changecase_lower = new JMenuItem("  Lower Case   ");
		popup_edit_changecase_lower
				.setIcon(new ImageIcon(this.getClass().getResource("/notepad/images/lowercase.png")));
		JMenuItem popup_edit_changecase_sentence = new JMenuItem("  Sentence Case   ");
		popup_edit_changecase_sentence
				.setIcon(new ImageIcon(this.getClass().getResource("/notepad/images/sentence_c.png")));

		popup_edit_changecase.add(popup_edit_changecase_upper);
		popup_edit_changecase.add(popup_edit_changecase_lower);
		popup_edit_changecase.add(popup_edit_changecase_sentence);

		JMenuItem popup_view_font = new JMenuItem("  Font ");
		popup_view_font.setIcon(new ImageIcon(this.getClass().getResource("/notepad/images/new_file.png")));
		popup_view_font.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.ALT_MASK));

		// add actions to popup menu items
		popup_edit_cut.addActionListener(this);
		popup_edit_copy.addActionListener(this);
		popup_edit_paste.addActionListener(this);
		popup_edit_selectall.addActionListener(this);
		popup_edit_changecase_upper.addActionListener(this);
		popup_edit_changecase_lower.addActionListener(this);
		popup_edit_changecase_sentence.addActionListener(this);
		popup_view_font.addActionListener(this);

		// adding pop-up menu items to popupMenu
		popupMenu.add(popup_edit_cut);
		popupMenu.add(popup_edit_copy);
		popupMenu.add(popup_edit_paste);
		popupMenu.addSeparator();
		popupMenu.add(popup_edit_selectall);
		popupMenu.addSeparator();
		popupMenu.add(popup_edit_changecase);
		popupMenu.addSeparator();
		popupMenu.add(popup_view_font);

		// add window listener to the j-frame
		addWindowListener(new closeAction());

		contentPane.add(toolBar, BorderLayout.NORTH);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		contentPane.add(statusBar, BorderLayout.SOUTH);

	}

	// Includes the actions for the menu items
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() instanceof JMenuItem) {
			String item = e.getActionCommand().trim();

			switch (item) {
			case "New":
				newFile_Action();
				break;
			case "Open":
				openFile_Action();
				break;
			case "Save":
				saveFile_Action();
				break;
			case "Save As":
				savesAs_Action();
				break;
			case "Save All":
				saveallAction();
				break;
			case "Close":
				closeAction();
				break;
			case "Close All":
				closeallAction();
				break;
			case "Exit":
				exitAction();
				break;
			// Edit
			case "Cut":
				cutAction();
				break;
			case "Copy":
				copyAction();
				break;
			case "Paste":
				pasteAction();
				break;
			case "GoTo":
				gotoAction();
				break;
			case "Find":
				findAction();
				break;
			case "Replace":
				replaceAction();
				break;
			case "Select All":
				selectallAction();
				break;
			case "Upper Case":
				changeToUpperCase();
				break;
			case "Lower Case":
				changeToLowerCase();
				break;
			case "Sentence Case":
				changeToSentenceCase();
				break;
			// View
			case "Font":
				changeFont();
				break;
			case "Text Color":
				changeTextColor();
				break;
			case "Background Color":
				changeTextAreaColor();
				break;
			case "Hide ToolBar":
				if (toolbarVisible) {
					toolBar.setVisible(false);
					toolbarVisible = false;
				} else {
					toolBar.setVisible(true);
					toolbarVisible = true;
				}
				break;
			case "Hide Statusbar":
				if (statusbarVisible) {
					statusBar.setVisible(false);
					statusbarVisible = false;
				} else {
					statusBar.setVisible(true);
					statusbarVisible = true;
				}
				break;
			// Help
			case "Explain Source code":
				ExplainCode ec = new ExplainCode();
				ec.setVisible(true);
				break;
			case "See Group members":
				String groupMembers = "1) Leul Woldegabriel (00919/09) \n" + "2) Haftom Takele (0688/06) \n"
						+ "3) Mezgebe Woldekiros (01023/09) \n" + "4) Daniel Hailay (0315/09) \n"
						+ "5) Bizunesh Abera (00270/09) \n" + "6) Angesom Berhane (00147/09) \n"
						+ "7) Haftay Wolday (00671/09) \n" + "8) Kibrom Wolday (00863/09) \n" + "9) Ermias Tekleberhan (00391/09) \n"
						+ "10) Gebremedhen Gebreziher (00525/09) \n" + "11) Kbret Tajebe (00823/09)\n"
						+ "12) Atsede Meresa (00186/09) \n";
				JOptionPane.showMessageDialog(rootPane, groupMembers, "Group Members", 1);
				break;
			case "Software Details":
				String softwareDetails = "Notepad - Java Assignment \n" + "	 > Made on Eclipse 2018 \n"
						+ "	 > JFrame Design on WindowBuilder's Swing Designer\n"
						+ "	 > Installer generated by Excellsior \n"
						+ "	 > Icons are form https://icons8.com/web-app/12775/ \n"
						+ "	 > Zipped Version of the source code is included in the install directory \n"
						+ "	 > This software can be set as your default text editor \n";
				JOptionPane.showMessageDialog(rootPane, softwareDetails, "Software Details", 1);
				break;
			}
		}
	}

	// Includes the actions for the tool-bar button
	class ToolBarButtonsAction implements ActionListener {
		String type = "";

		public ToolBarButtonsAction(String s) {
			type = s;
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			switch (type) {
			case "new":
				newFile_Action();
				break;
			case "open":
				openFile_Action();
				break;
			case "save":
				saveFile_Action();
				break;
			case "saveas":
				savesAs_Action();
				break;
			case "saveall":
				saveallAction();
				break;
			case "cut":
				cutAction();
				break;
			case "copy":
				copyAction();
				break;
			case "paste":
				pasteAction();
				break;
			case "goto":
				gotoAction();
				break;
			case "font":
				changeFont();
				break;
			case "close":
				closeAction();
				break;
			}
		}
	}

	// Shows the pop-up menu
	class TextPane_MouseAction extends MouseAdapter {
		@Override
		public void mouseReleased(MouseEvent evt) {
			if (evt.isPopupTrigger()) {
				popupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
			}
		}
	}

	// Gets the number of rows and columns to be displayed on the status bar
	class CaretAction implements CaretListener {
		public int getRow(int pos, JTextPane textpane) {
			int rn = (pos == 0) ? 1 : 0;
			try {
				int offs = pos;
				while (offs > 0) {
					offs = Utilities.getRowStart(textpane, offs) - 1;
					rn++;
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}

			return rn;
		}

		public int getColumn(int pos, JTextPane textpane) {
			try {
				return pos - Utilities.getRowStart(textpane, pos) + 1;
			} catch (BadLocationException e) {
				e.printStackTrace();
			}

			return -1;
		}

		@Override
		public void caretUpdate(CaretEvent evt) {
			JTextPane textpane = (JTextPane) evt.getSource();
			int row = getRow(evt.getDot(), textpane);
			int col = getColumn(evt.getDot(), textpane);
			rowLabel.setText("Row : " + row);
			colLabel.setText("Col : " + col);
		}
	}

	// Replace action
	class ReplaceText_Action implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			Object source = evt.getSource();
			if (source == replaceButton) {
				int sel = tabbedPane.getSelectedIndex();
				JTextPane textPane = (JTextPane) (((JScrollPane) tabbedPane.getComponentAt(sel)).getViewport())
						.getComponent(0);

				String find = findText.getText();
				String replace = replaceText.getText();

				textPane.setText(textPane.getText().replaceAll(find, replace));

				String tabtext = tabbedPane.getTitleAt(sel);
				if (tabtext.contains("*")) {
				} else {
					tabbedPane.setTitleAt(sel, tabbedPane.getTitleAt(sel) + "*");
					tabbedPane.setIconAt(sel, new ImageIcon(this.getClass().getResource("resources/unsaved.png")));
				}
			} else if (source == cancelButton) {
				jd.dispose();
			}
		}
	}

	// On key typed performs an actions
	class KeyTypedAction implements KeyListener {
		@Override
		public void keyPressed(KeyEvent evt) {
			int keycode = evt.getKeyCode();
			boolean is_ControlDown = evt.isControlDown();

			if (keycode == KeyEvent.VK_X && is_ControlDown) {
				cutAction();
			} else if (keycode == KeyEvent.VK_C && is_ControlDown) {
				copyAction();
			} else if (keycode == KeyEvent.VK_V && is_ControlDown) {
				int sel = tabbedPane.getSelectedIndex();
				String tabtext = tabbedPane.getTitleAt(sel);
				if (tabtext.contains("*")) {
				} else {
					tabbedPane.setTitleAt(sel, tabbedPane.getTitleAt(sel) + "*");
					tabbedPane.setIconAt(sel,
							new ImageIcon(this.getClass().getResource("/notepad/images/not_saved.png")));
				}
			} else if (keycode == KeyEvent.VK_S && is_ControlDown) {
				saveFile_Action();
			}
		}

		@Override
		public void keyReleased(KeyEvent evt) {
		}

		@Override
		public void keyTyped(KeyEvent evt) {
			if (!evt.isControlDown()) {
				int sel = tabbedPane.getSelectedIndex();
				String tabtext = tabbedPane.getTitleAt(sel);
				if (tabtext.contains("*")) {
				} else {
					tabbedPane.setTitleAt(sel, tabbedPane.getTitleAt(sel) + "*");
					tabbedPane.setIconAt(sel,
							new ImageIcon(this.getClass().getResource("/notepad/images/not_saved.png")));
				}
			}
		}
	}

	// Performs the undo action
	public class PerformUndoAction extends AbstractAction {
		/**
		 * 
		 */

		UndoManager manager;

		public PerformUndoAction(UndoManager manager) {
			this.manager = manager;
		}

		public void actionPerformed(ActionEvent evt) {
			try {
				manager.undo();
			} catch (CannotUndoException e) {
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}

	// Performs the re-do action
	public class PerformRedoAction extends AbstractAction {
		/**
		 * 
		 */

		UndoManager _manager;

		public PerformRedoAction(UndoManager manager) {
			this._manager = manager;
		}

		public void actionPerformed(ActionEvent evt) {
			try {
				_manager.redo();
			} catch (CannotRedoException e) {
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}

	// Gets the numbers of lines in the text-pane
	public int getLineCount(JTextPane textPane) {
		_lineCount = 0;
		Scanner scanner = new Scanner(textPane.getText());
		while (scanner.hasNextLine()) {
			scanner.nextLine();
			_lineCount++;
		}
		scanner.close();
		return _lineCount;
	}

	// move the cursor to a new line
	public int setCursor(int newlineno, JTextPane textPane) {
		int pos = 0;
		int i = 0;
		String line = "";
		Scanner scanner = new Scanner(textPane.getText());
		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			i++;
			if (newlineno > i) {
				pos = pos + line.length() + 1;
			}
		}
		scanner.close();
		return pos;
	}

	// Creates and a new tab (File)
	public void newFile_Action() {

		JTextPane textPane = new JTextPane();
		textPane.setFont(new Font("Calibri", Font.PLAIN, 14));

		JScrollPane jsp = new JScrollPane(textPane);

		// Add key listener & Undo-able edit listener to text-pane
		textPane.addKeyListener(new KeyTypedAction());
		textPane.getDocument().addUndoableEditListener(undoManager);

		// add tab to tabbedPane with control text-pane
		tabbedPane.addTab("Doc " + count + " ", jsp);

		// add caret listener & mouse listener to text pane
		textPane.addCaretListener(new CaretAction());
		textPane.addMouseListener(new TextPane_MouseAction());
		int index = tabbedPane.getTabCount() - 1;

		tabbedPane.setSelectedIndex(index);

		// set save icon to added tab
		tabbedPane.setIconAt(index, new ImageIcon(this.getClass().getResource("/notepad/images/save_file.png")));

		// change the title
		setTitle("Notpad Java Assignment - [ Doc " + count + " ]");
		filenameLabel.setText("Doc " + count);

		count++;
	}

	// Opens a saved file
	@SuppressWarnings("deprecation")
	public void openFile_Action() {

		FileDialog fd = new FileDialog(new JFrame(), "Select File", FileDialog.LOAD);
		fd.setMultipleMode(true);
		fd.setSize(100, 100);
		fd.show();

		if (fd.getFiles() != null) {
			File[] files = fd.getFiles();
			for (File item : files) {
				String filename = item.toString();
				String file = filename;
				if (filename.contains("\\")) {
					file = filename.substring(filename.lastIndexOf("\\") + 1);
				} else if (filename.contains("/")) {
					file = filename.substring(filename.lastIndexOf("/") + 1);
				}

				int count = tabbedPane.getTabCount();

				JTextPane _textPane = new JTextPane();
				_textPane.setFont(new Font("Calibri", Font.PLAIN, 14));

				JScrollPane jsp = new JScrollPane(_textPane);
				_textPane.addKeyListener(new KeyTypedAction());
				_textPane.getDocument().addUndoableEditListener(undoManager);
				_textPane.addCaretListener(new CaretAction());
				_textPane.addMouseListener(new TextPane_MouseAction());

				tabbedPane.addTab(file, jsp);
				tabbedPane.setSelectedIndex(count);
				tabbedPane.setIconAt(count,
						new ImageIcon(this.getClass().getResource("/notepad/images/save_file.png")));

				setTitle("Notpad Java Assignment - [ " + file + " ]");
				filenameLabel.setText(filename);

				BufferedReader d;
				StringBuffer sb = new StringBuffer();
				try {
					d = new BufferedReader(new FileReader(filename));
					String line;
					while ((line = d.readLine()) != null)
						sb.append(line + "\n");
					_textPane.setText(sb.toString());
					d.close();
				} catch (FileNotFoundException fe) {
					System.out.println("File not Found");
				} catch (IOException ioe) {
				}

				_textPane.requestFocus();

			}
		}

	}

	// Save changes to a file
	public void saveFile_Action() {

		if (tabbedPane.getTabCount() > 0) {

			String filename = filenameLabel.getText();
			int sel = tabbedPane.getSelectedIndex();
			JTextPane textPane = (JTextPane) (((JScrollPane) tabbedPane.getComponentAt(sel)).getViewport())
					.getComponent(0);

			if (filename.contains("\\") || filename.contains("/")) {
				File f = new File(filename);
				if (f.exists()) {
					try {
						DataOutputStream d = new DataOutputStream(new FileOutputStream(filename));
						String line = textPane.getText();
						d.writeBytes(line);
						d.close();

						String tabtext = tabbedPane.getTitleAt(sel);
						if (tabtext.contains("*")) {
							tabtext = tabtext.replace("*", "");
							tabbedPane.setTitleAt(sel, tabtext);
							setTitle("Notepad Java Assignment - [ " + tabtext + " ]");
							tabbedPane.setIconAt(sel,
									new ImageIcon(this.getClass().getResource("/notepad/images/save_file.png")));
						}

					} catch (Exception ex) {
						System.err.println("File not found");
					}
					textPane.requestFocus();
				}
			}

			else if (filename.contains("Doc ")) {
				savesAs_Action();
			}

		}
	}

	// Save a file with custom extensions
	@SuppressWarnings("deprecation")
	public void savesAs_Action() {
		if (tabbedPane.getTabCount() > 0) {
			FileDialog fd = new FileDialog(new JFrame(), "Save File", FileDialog.SAVE);
			fd.show();
			if (fd.getFile() != null) {
				String filename = fd.getDirectory() + fd.getFile();
				int sel = tabbedPane.getSelectedIndex();
				JTextPane textPane = (JTextPane) (((JScrollPane) tabbedPane.getComponentAt(sel)).getViewport())
						.getComponent(0);
				try {
					DataOutputStream d = new DataOutputStream(new FileOutputStream(filename));
					String line = textPane.getText();
					d.writeBytes(line);
					d.close();

					filenameLabel.setText(filename);

					String file = filename.substring(filename.lastIndexOf("\\") + 1);
					tabbedPane.setTitleAt(sel, file);

					tabbedPane.setIconAt(sel,
							new ImageIcon(this.getClass().getResource("/notepad/images/save_file.png")));

					setTitle("Notepad Java Assignment - [ " + file + " ]");

				} catch (Exception ex) {
					System.out.println("File not found");
				}
				textPane.requestFocus();

			}
		}
	}

	// Save changes to all files
	public void saveallAction() {
		if (tabbedPane.getTabCount() > 0) {

			int maxindex = tabbedPane.getTabCount() - 1;
			for (int i = 0; i <= maxindex; i++) {
				tabbedPane.setSelectedIndex(i);
				String filename = filenameLabel.getText();
				int sel = tabbedPane.getSelectedIndex();
				JTextPane textPane = (JTextPane) (((JScrollPane) tabbedPane.getComponentAt(sel)).getViewport())
						.getComponent(0);
				if (filename.contains("\\") || filename.contains("/")) {
					File f = new File(filename);
					if (f.exists()) {
						try {
							DataOutputStream d = new DataOutputStream(new FileOutputStream(filename));
							String line = textPane.getText();
							d.writeBytes(line);
							d.close();

							String tabtext = tabbedPane.getTitleAt(sel);
							if (tabtext.contains("*")) {
								tabtext = tabtext.replace("*", "");
								tabbedPane.setTitleAt(sel, tabtext);
								setTitle("Notepad Java Assignment - [ " + tabtext + " ]");
								tabbedPane.setIconAt(sel,
										new ImageIcon(this.getClass().getResource("/notepad/images/save_file.png")));
							}

						} catch (Exception ex) {
							System.err.println("File not found");
						}
						textPane.requestFocus();
					}
				}

			}
		}
	}

	// close a tab
	public void closeAction() {
		if (tabbedPane.getTabCount() > 0) {
			int sel = tabbedPane.getSelectedIndex();
			String tabtext = tabbedPane.getTitleAt(sel);

			if (tabtext.contains("*")) {
				int n = JOptionPane.showConfirmDialog(null, "Do you want to save " + tabtext + " before close ?",
						"Save or Not", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

				tabtext.replace("*", "");

				if (n == 0) {
					String filename = filenameLabel.getText();

					if (filename.contains("\\") || filename.contains("/")) {
						saveFile_Action();

						tabbedPane.remove(sel);

						// adding all elements to list after removing the tab
						for (int i = 0; i < tabbedPane.getTabCount(); i++) {
							String item = tabbedPane.getTitleAt(i);
							if (item.contains("*")) {
								item = item.replace("*", "").trim();
							}

						}

						rowLabel.setText("Row :");
						colLabel.setText("Col :");

						if (tabbedPane.getTabCount() == 0) {
							setTitle("Notepad Java Assignment");
							filenameLabel.setText("");
							rowLabel.setText("Row :");
							colLabel.setText("Col :");
						}

					}

					else if (filename.contains("Document ")) {
						savesAs_Action();

						tabbedPane.remove(sel);
						// adding all elements to list after removing the tab
						for (int i = 0; i < tabbedPane.getTabCount(); i++) {
							String item = tabbedPane.getTitleAt(i);
							if (item.contains("*")) {
								item = item.replace("*", "").trim();
							}

						}

						rowLabel.setText("Row :");
						colLabel.setText("Col :");

						if (tabbedPane.getTabCount() == 0) {
							setTitle("Notepad Java Assignment");
							filenameLabel.setText("");
							rowLabel.setText("Row :");
							colLabel.setText("Col :");
						}
					}

				}

				if (n == 1) {
					tabbedPane.remove(sel);

					// adding all elements to list after removing the tab
					for (int i = 0; i < tabbedPane.getTabCount(); i++) {
						String item = tabbedPane.getTitleAt(i);
						if (item.contains("*")) {
							item = item.replace("*", "").trim();
						}

					}

					rowLabel.setText("Row :");
					colLabel.setText("Col :");

					if (tabbedPane.getTabCount() == 0) {
						setTitle("Notepad Java Assignment");
						filenameLabel.setText("");
						rowLabel.setText("Row :");
						colLabel.setText("Col :");
					}
				}
			}

			else {
				tabbedPane.remove(sel);

				// adding all elements to list after removing the tab
				for (int i = 0; i < tabbedPane.getTabCount(); i++) {
					String item = tabbedPane.getTitleAt(i);
					if (item.contains("*")) {
						item = item.replace("*", "").trim();
					}

				}

				rowLabel.setText("Row :");
				colLabel.setText("Col :");

				if (tabbedPane.getTabCount() == 0) {
					setTitle("Notepad Java Assignment");
					filenameLabel.setText("");
					rowLabel.setText("Row :");
					colLabel.setText("Col :");
				}

			}
		}

		else {
			setTitle("Notepad Java Assignment");
			filenameLabel.setText("");
			rowLabel.setText("Row :");
			colLabel.setText("Col :");

		}
	}

	// close all tabs
	public void closeallAction() throws IndexOutOfBoundsException {
		if (tabbedPane.getTabCount() > 0) {
			for (int j = 0; j < tabbedPane.getTabCount(); j++) {
				tabbedPane.setSelectedIndex(j);
				int sel = tabbedPane.getSelectedIndex();
				String tabtext = tabbedPane.getTitleAt(sel);

				if (tabtext.contains("*")) {
					int n = JOptionPane.showConfirmDialog(null, "Do you want to save " + tabtext + " before closing ?",
							"Warning!", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

					tabtext.replace("*", "");

					if (n == 0) {
						String filename = filenameLabel.getText();

						if (filename.contains("\\") || filename.contains("/")) {
							saveFile_Action();

							tabbedPane.remove(sel);

							// adding all elements to list after removing the tab
							for (int i = 0; i < tabbedPane.getTabCount(); i++) {
								String item = tabbedPane.getTitleAt(i);
								if (item.contains("*")) {
									item = item.replace("*", "").trim();
								}

							}

							closeallAction();

							rowLabel.setText("Row :");
							colLabel.setText("Col :");
						} else if (filename.contains("Doc ")) {
							savesAs_Action();

							tabbedPane.remove(sel);

							// adding all elements to list after removing the tab
							for (int i = 0; i < tabbedPane.getTabCount(); i++) {
								String item = tabbedPane.getTitleAt(i);
								if (item.contains("*")) {
									item = item.replace("*", "").trim();
								}

							}

							closeallAction();

							rowLabel.setText("Row :");
							colLabel.setText("Col :");
						}

					} else if (n == 1) {
						tabbedPane.remove(sel);

						// adding all elements to list after removing the tab
						for (int i = 0; i < tabbedPane.getTabCount(); i++) {
							String item = tabbedPane.getTitleAt(i);
							if (item.contains("*")) {
								item = item.replace("*", "").trim();
							}

						}

						closeallAction();

						rowLabel.setText("Row :");
						colLabel.setText("Col :");
					}
				}

				else {
					tabbedPane.remove(sel);
					// adding all elements to list after removing the tab
					for (int i = 0; i < tabbedPane.getTabCount(); i++) {
						String item = tabbedPane.getTitleAt(i);
						if (item.contains("*")) {
							item = item.replace("*", "").trim();
						}

					}

					// _list.setSelectedIndex(tabbedPane.getTabCount() - 1);

					closeallAction();

					rowLabel.setText("Row :");
					colLabel.setText("Col :");
				}
			}
		}

		else {
			setTitle("Notepad Java Assignment");
			filenameLabel.setText("");

			rowLabel.setText("Row :");
			colLabel.setText("Col :");
		}
	}

	// exit window
	public void exitAction() {
		closeallAction();
		if (tabbedPane.getTabCount() == 0) {
			System.exit(0);
		}
	}

	// cut text
	public void cutAction() {
		if (tabbedPane.getTabCount() > 0) {
			int sel = tabbedPane.getSelectedIndex();
			JTextPane textPane = (JTextPane) (((JScrollPane) tabbedPane.getComponentAt(sel)).getViewport())
					.getComponent(0);
			String selected_text = textPane.getSelectedText();
			StringSelection ss = new StringSelection(selected_text);
			clip.setContents(ss, ss);
			textPane.replaceSelection("");

			String tabtext = tabbedPane.getTitleAt(sel);
			if (tabtext.contains("*")) {
			} else {
				tabbedPane.setTitleAt(sel, tabbedPane.getTitleAt(sel) + "*");
				tabbedPane.setIconAt(sel, new ImageIcon(this.getClass().getResource("resources/unsaved.png")));
			}
		}
	}

	// copy text
	public void copyAction() {
		if (tabbedPane.getTabCount() > 0) {
			int sel = tabbedPane.getSelectedIndex();
			JTextPane textPane = (JTextPane) (((JScrollPane) tabbedPane.getComponentAt(sel)).getViewport())
					.getComponent(0);
			String selected_text = textPane.getSelectedText();
			StringSelection ss = new StringSelection(selected_text);
			clip.setContents(ss, ss);

		}
	}

	// paste text
	public void pasteAction() {
		if (tabbedPane.getTabCount() > 0) {
			int sel = tabbedPane.getSelectedIndex();
			JTextPane textPane = (JTextPane) (((JScrollPane) tabbedPane.getComponentAt(sel)).getViewport())
					.getComponent(0);
			Transferable cliptran = clip.getContents(MainFrame.this);
			try {
				String selected_text = (String) cliptran.getTransferData(DataFlavor.stringFlavor);
				textPane.replaceSelection(selected_text);

				// here you can direct use textPane.paste();

				String tabtext = tabbedPane.getTitleAt(sel);
				if (tabtext.contains("*")) {
				} else {
					tabbedPane.setTitleAt(sel, tabbedPane.getTitleAt(sel) + "*");
					tabbedPane.setIconAt(sel, new ImageIcon(this.getClass().getResource("resources/unsaved.png")));
				}
			} catch (Exception exc) {
				System.out.println("error to paste");
			}
		}
	}

	// goto a line
	public void gotoAction() {
		if (tabbedPane.getTabCount() > 0) {
			int sel = tabbedPane.getSelectedIndex();
			JTextPane textPane = (JTextPane) (((JScrollPane) tabbedPane.getComponentAt(sel)).getViewport())
					.getComponent(0);

			do {
				try {
					String str = (String) JOptionPane.showInputDialog(null,
							"Enter Line number :  " + "(1 - " + getLineCount(textPane) + " )", "GoTo Line",
							JOptionPane.PLAIN_MESSAGE, null, null, null);
					if (str == null) {
						break;
					}

					int lineNumber = Integer.parseInt(str);
					_lineCount = getLineCount(textPane);
					if (lineNumber > _lineCount) {
						JOptionPane.showMessageDialog(null, "Line number out of range", "Error....",
								JOptionPane.ERROR_MESSAGE);
						continue;
					}
					textPane.setCaretPosition(0);
					textPane.setCaretPosition(setCursor(lineNumber, textPane));
					return;
				} catch (Exception e) {
				}
			} while (true);
		}
	}

	// find text action
	public void findAction() {
		if (tabbedPane.getTabCount() > 0) {
			int sel = tabbedPane.getSelectedIndex();
			JTextPane textPane = (JTextPane) (((JScrollPane) tabbedPane.getComponentAt(sel)).getViewport())
					.getComponent(0);

			String input = (String) JOptionPane.showInputDialog(null, "Enter Text to Find :  ", "Find",
					JOptionPane.PLAIN_MESSAGE, null, null, null);
			if (input != null) {
				int start = textPane.getText().indexOf(input);
				int end = input.length();
				if (start >= 0 && end > 0) {
					textPane.select(start, start + end);
				}
			}
		}
	}

	// find and replace text action
	@SuppressWarnings("deprecation")
	public void replaceAction() {
		if (tabbedPane.getTabCount() > 0) {
			jd = new JDialog(new JDialog(), true);
			jd.setSize(360, 120);
			jd.setLocation(this.getCenterPoints().x + 150, this.getCenterPoints().y + 130);
			jd.setResizable(false);
			jd.setTitle("Replace");

			JPanel jp1 = new JPanel();
			JPanel jp2 = new JPanel();
			JLabel findwhat = new JLabel("Find What    :    ");
			JLabel replacewith = new JLabel("Replace With : ");
			findText = new JTextField(20);
			replaceText = new JTextField(20);

			replaceButton = new JButton("Replace All");
			cancelButton = new JButton("Cancel");

			replaceButton.addActionListener(new ReplaceText_Action());
			cancelButton.addActionListener(new ReplaceText_Action());

			jp1.add(findwhat);
			jp1.add(findText);
			jp1.add(replacewith);
			jp1.add(replaceText);
			jp2.add(replaceButton);
			jp2.add(cancelButton);

			jd.getContentPane().add(jp1, BorderLayout.CENTER);
			jd.getContentPane().add(jp2, BorderLayout.SOUTH);

			jd.show();
		}
	}

	// select all text in the text-pane
	public void selectallAction() {
		if (tabbedPane.getTabCount() > 0) {
			int sel = tabbedPane.getSelectedIndex();
			JTextPane textPane = (JTextPane) (((JScrollPane) tabbedPane.getComponentAt(sel)).getViewport())
					.getComponent(0);

			textPane.selectAll();
		}
	}

	// change selected text to upper case
	public void changeToUpperCase() {
		if (tabbedPane.getTabCount() > 0) {
			int sel = tabbedPane.getSelectedIndex();
			JTextPane textPane = (JTextPane) (((JScrollPane) tabbedPane.getComponentAt(sel)).getViewport())
					.getComponent(0);

			if (textPane.getSelectedText() != null) {
				textPane.replaceSelection(textPane.getSelectedText().toUpperCase());

				String tabtext = tabbedPane.getTitleAt(sel);
				if (tabtext.contains("*")) {
				} else {
					tabbedPane.setTitleAt(sel, tabbedPane.getTitleAt(sel) + "*");
					tabbedPane.setIconAt(sel, new ImageIcon(this.getClass().getResource("resources/unsaved.png")));
				}
			}
		}
	}

	// change selected text to lower case
	public void changeToLowerCase() {
		if (tabbedPane.getTabCount() > 0) {
			int sel = tabbedPane.getSelectedIndex();
			JTextPane textPane = (JTextPane) (((JScrollPane) tabbedPane.getComponentAt(sel)).getViewport())
					.getComponent(0);

			if (textPane.getSelectedText() != null) {
				textPane.replaceSelection(textPane.getSelectedText().toLowerCase());

				String tabtext = tabbedPane.getTitleAt(sel);
				if (tabtext.contains("*")) {
				} else {
					tabbedPane.setTitleAt(sel, tabbedPane.getTitleAt(sel) + "*");
					tabbedPane.setIconAt(sel, new ImageIcon(this.getClass().getResource("resources/unsaved.png")));
				}
			}
		}
	}

	// change the first letter of the selected text
	public void changeToSentenceCase() {
		if (tabbedPane.getTabCount() > 0) {
			int sel = tabbedPane.getSelectedIndex();
			JTextPane textPane = (JTextPane) (((JScrollPane) tabbedPane.getComponentAt(sel)).getViewport())
					.getComponent(0);

			if (textPane.getSelectedText() != null) {
				String s = textPane.getSelectedText();
				char ch = s.charAt(0);
				String ss = String.valueOf(ch).toUpperCase();
				String str = s.substring(1);
				str = ss + str;
				textPane.replaceSelection(str);

				String tabtext = tabbedPane.getTitleAt(sel);
				if (tabtext.contains("*")) {
				} else {
					tabbedPane.setTitleAt(sel, tabbedPane.getTitleAt(sel) + "*");
					tabbedPane.setIconAt(sel, new ImageIcon(this.getClass().getResource("resources/unsaved.png")));
				}
			}
		}
	}

	// change the font of the text
	public void changeFont() {
		if (tabbedPane.getTabCount() > 0) {
			int sel = tabbedPane.getSelectedIndex();
			JTextPane textPane = (JTextPane) (((JScrollPane) tabbedPane.getComponentAt(sel)).getViewport())
					.getComponent(0);

			JDialog fa = new ChangeFontAction(textPane);
			fa.setTitle("Set Font");
			fa.setSize(400, 300);
			fa.setModal(true);
			fa.setLocation(this.getCenterPoints());
			fa.setResizable(false);
			fa.setAlwaysOnTop(true);
			fa.setVisible(true);
		}
	}

	// change the color of the text
	public void changeTextColor() {
		if (tabbedPane.getTabCount() > 0) {
			int sel = tabbedPane.getSelectedIndex();
			JTextPane textPane = (JTextPane) (((JScrollPane) tabbedPane.getComponentAt(sel)).getViewport())
					.getComponent(0);

			ColorChange.ForeColor_Action fra = new ColorChange.ForeColor_Action(textPane);
			fra.setTitle("Set text Color");
			fra.setModal(true);
			fra.setSize(540, 300);
			fra.setLocation(this.getCenterPoints());
			fra.setResizable(false);
			fra.setAlwaysOnTop(true);
			fra.setVisible(true);
		}
	}

	// change the color of the text-pane
	public void changeTextAreaColor() {
		if (tabbedPane.getTabCount() > 0) {
			int sel = tabbedPane.getSelectedIndex();
			JTextPane textPane = (JTextPane) (((JScrollPane) tabbedPane.getComponentAt(sel)).getViewport())
					.getComponent(0);

			ColorChange.BackColor_Action bra = new ColorChange.BackColor_Action(textPane);
			bra.setTitle("Set Background Color");
			bra.setModal(true);
			bra.setSize(540, 300);
			bra.setLocation(this.getCenterPoints());
			bra.setResizable(false);
			bra.setAlwaysOnTop(true);
			bra.setVisible(true);
		}
	}

	// Action that runs when close button pressed
	class closeAction extends WindowAdapter {
		@Override
		public void windowOpened(WindowEvent evt) {
			newFile_Action();
		}

		@Override
		public void windowClosing(WindowEvent evt) {
			closeallAction();
			if (tabbedPane.getTabCount() == 0) {
				System.exit(0);
			}
		}
	}

	// get the center points
	public Point getCenterPoints() {
		Point pt = new Point(0, 0);
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension d = toolkit.getScreenSize();
		pt.x = d.width / 3;
		pt.y = d.height / 4;

		return pt;
	}

}
