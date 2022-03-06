package ui;

import java.awt.BorderLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import data.ProgramState;
import util.Util;

/**
 * A class responsible for holding the window that contains the
 * <i><code>debug</code></i> field in the <code>Util</code> class. Simply allows
 * debug information to be read when the program is being run outside of an IDE.
 * 
 * @author Jordan Knapp
 */
public class DebugWindow {
	
	private JFrame frame;
	private JPanel panel;
	
	private ProgramState state;
	
	public DebugWindow(MainWindow parent, ProgramState state) {
		frame = new JFrame("Smash Character Picker Debug");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(800, 200);
		frame.setResizable(true);
		
		this.state = state;
		
		frame.setLocation(parent.getX(), (int) (parent.getY() + parent.getHeight()));
		frame.addWindowListener(new DebugWindowListener());
		
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(Util.debug);
		
		JScrollPane scrollPane = new JScrollPane(panel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		frame.add(scrollPane);
		
		frame.setVisible(true);
	}
	
	private class DebugWindowListener implements WindowListener {

		public void windowOpened(WindowEvent e) {
			Util.log("You've opened the debug panel.");
			state.openedDebug = true;
		}

		public void windowClosing(WindowEvent e) {

		}

		public void windowClosed(WindowEvent e) {
			Util.log("You've closed the debug panel.");
			state.openedDebug = false;
		}

		public void windowIconified(WindowEvent e) {

		}

		public void windowDeiconified(WindowEvent e) {

		}

		public void windowActivated(WindowEvent e) {

		}

		public void windowDeactivated(WindowEvent e) {

		}
	}

}
