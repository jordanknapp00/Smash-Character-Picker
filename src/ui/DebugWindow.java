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
	
	/**
	 * Constructs a <code>DebugWindow</code> with the specified parameters.
	 * Because the class extends <code>JFrame</code>, it is up to the caller
	 * to show the debug window.
	 * <br><br>
	 * The <code>DebugWindow</code> has a notion of a "parent window" -- the
	 * window that was responsible for creating it. In the context of the
	 * Smash Character Picker, that is the <code>MainWindow</code> class.
	 * <br><br>
	 * The <Code>DebugWindow</code> will have the same width as its parent
	 * window. The height will be 200 pixels. The position of the
	 * <code>DebugWindow</code> will be based on the x and y coordinates of
	 * the parent, as well as the height of the parent. Ultimately, it should
	 * be placed exactly below the parent window.
	 * <br><br>
	 * Logging info to the <code>DebugWindow</code> is done via the
	 * <code>Util</code> class' <code>log()</code> and <code>error()</code>
	 * methods.
	 * 
	 * @param parentWidth	The width of the parent window creating this
	 * 						<code>DebugWindow</code>.
	 * @param parentHeight	The height of the parent window.
	 * @param parentX		The x position of the parent window.
	 * @param parentY		The y position of the parent window.
	 */
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
