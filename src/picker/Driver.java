package picker;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ui.MainWindow;

public class Driver {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new MainWindow();
				} catch(Exception e) {
					JOptionPane.showMessageDialog(null, e, "Smash Character Picker", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
}
