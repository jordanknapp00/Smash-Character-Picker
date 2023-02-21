package ui;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import util.Util;

/**
 * A class responsible for holding the window that contains the
 * <i><code>debug</code></i> field in the <code>Util</code> class. Simply allows
 * debug information to be read when the program is being run outside of an IDE.
 * 
 * @author Jordan Knapp
 */
public class DebugWindow extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private JPanel panel;
	
	public DebugWindow(int parentWidth, int parentHeight, int parentX, int parentY) {
		super("Smash Character Picker Debug");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(parentWidth, 200);
		setResizable(true);
		
		setLocation(parentX, parentY + parentHeight);
		
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		Util.debug.setEditable(false);
		Util.debug.setFont(new Font("Monospaced", Font.PLAIN, Util.debug.getFont().getSize()));
		panel.add(Util.debug);
		
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		add(scrollPane);
	}
}
