package notepad;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Toolkit;
import java.awt.Color;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.Font;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;

public class ExplainCode extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ExplainCode frame = new ExplainCode();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ExplainCode() {
		setResizable(false);
		setAlwaysOnTop(true);
		setIconImage(Toolkit.getDefaultToolkit().getImage(ExplainCode.class.getResource("/notepad/images/notebook_icon.png")));
		setTitle("ExplainCode");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 302, 374);
		setUndecorated(true);
		contentPane = new JPanel();
		contentPane.setBackground(Color.DARK_GRAY);
		contentPane.setBorder(new TitledBorder(new LineBorder(new Color(192, 192, 192), 1, true), "Explain Code", TitledBorder.LEADING, TitledBorder.TOP, null, Color.WHITE));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(Color.WHITE, 1, true));
		panel.setBackground(Color.DARK_GRAY);
		contentPane.add(panel, BorderLayout.NORTH);
		
		JButton btnGoBack = new JButton("Close");
		btnGoBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				close();
			}
		});
		btnGoBack.setForeground(Color.BLACK);
		btnGoBack.setBackground(Color.WHITE);
		btnGoBack.setFont(new Font("Arial", Font.PLAIN, 14));
		panel.add(btnGoBack);
		
		JButton btnOpenPdf = new JButton("Open PDF");
		btnOpenPdf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/C", "c:\\Program Files\\Adigrat University, ECE\\Notepad (Java Assignment)\\files\\rs.pdf");
				try {
					processBuilder.start();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnOpenPdf.setForeground(Color.BLACK);
		btnOpenPdf.setBackground(Color.WHITE);
		btnOpenPdf.setFont(new Font("Arial", Font.PLAIN, 14));
		panel.add(btnOpenPdf);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		JTextPane txtpn = new JTextPane();
		txtpn.setFont(txtpn.getFont().deriveFont(txtpn.getFont().getStyle() & ~Font.BOLD, 15f));
		txtpn.setForeground(Color.BLACK);
		txtpn.setEditable(false);
		String ftext = "Notepad- Source Code \n"
     	             + "- The code has to 4 independent Classes MainFrame,ExplainCode(This Frame),ChangeFontAction,ColorChange \n"
				     + "- MainFrame,ExplainCode extend the JFrame class \n"
				     + "- ChangeFontAction extend the JDialog class \n"
				     + "> MainFrame \n"
				     + "- MainFrame in addition to extending JFrame it implements ActionListners \n"
				     + "- MainFrame has 27 Methods including the main method and constructors \n"
				     + "- MainFrame also has 8 classes that extend different kinds of listeners \n"
				     + "- MainFrame is the frame that contains the text editor \n"
				     + "> ExplainCode \n"
				     + "- ExplainCode has 2 methods main method and a custom method 'close()' \n"
				     + "-  \n"
				     + "\n --OPEN PDF FOR MORE-- \n";
		
		txtpn.setText(ftext);
		scrollPane.setViewportView(txtpn);
	}
	
	public void close() {
		this.dispose();
	}

}
