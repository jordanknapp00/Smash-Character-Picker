package picker;

import javax.swing.SwingUtilities;

import ui.MainWindow;

public class Driver {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new MainWindow();
			}
		});
	}
}
