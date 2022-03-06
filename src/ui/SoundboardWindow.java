package ui;

import java.awt.AWTEvent;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.concurrent.ThreadLocalRandom;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import data.ProgramState;
import util.Util;

public class SoundboardWindow {
	
	private JFrame frame;
	private JPanel panel;
	
	private JButton doItAgainButton;
	private JButton neutralAerialButton;
	private JButton trashManButton;
	private JButton yodaNoButton;
	private JButton bruhSoundEffectButton;
	private JButton angerousNowButton;
	private JButton theWorstButton;
	private JButton sureManButton;
	private JButton bscuseMeButton;
	private JButton ghoulButton;
	private JButton whatButton;
	private JButton thatMakesMeFeelAngryButton;
	private JButton daringTodayButton;
	private JButton vsauceButton;
	private JButton despiseHimButton;
	private JButton whatDuhHeckButton;
	private JButton cheaterButton;
	private JButton ohMyGodButton;
	private JButton doingStringsButton;
	private JButton churlishButton;
	private JButton chicanerousButton;
	
	private ProgramState state;
	
	public SoundboardWindow(MainWindow parent, ProgramState state) {		
		frame = new JFrame("Soundboard");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(325, 435);
		frame.setResizable(false);
		
		this.state = state;
		
		frame.setLocation(parent.getX() - frame.getWidth(), parent.getY());
		frame.addWindowListener(new SoundboardWindowListener());
		
		//attempt to set look and feel, catching any errors
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			Util.error(e);
		} catch (InstantiationException e) {
			Util.error(e);
		} catch (IllegalAccessException e) {
			Util.error(e);
		} catch (UnsupportedLookAndFeelException e) {
			Util.error(e);
		}
		
		panel = new JPanel();
		panel.setLayout(new GridLayout(0, 3));
		
		doItAgainButton = new JButton("Do it again");
		doItAgainButton.addActionListener(new SingleSoundButton("/sounds/doitagain.wav"));
		neutralAerialButton = new JButton("Neutral aerial");
		neutralAerialButton.addActionListener(new SingleSoundButton("/sounds/neutralaerial.wav"));
		trashManButton = new JButton("Trash man");
		trashManButton.addActionListener(new SingleSoundButton("/sounds/trashman.wav"));
		yodaNoButton = new JButton("Yoda no");
		yodaNoButton.addActionListener(new SingleSoundButton("/sounds/yodano.wav"));
		bruhSoundEffectButton = new JButton("Bruh");
		bruhSoundEffectButton.addActionListener(new SingleSoundButton("/sounds/bruh.wav"));
		angerousNowButton = new JButton("<html><center>I am be<br>angerous now</center></html>");
		angerousNowButton.addActionListener(new SingleSoundButton("/sounds/angerousnow.wav"));
		theWorstButton = new JButton("<html><center>This is... the<br>worst</center></html>");
		theWorstButton.addActionListener(new SingleSoundButton("/sounds/thisistheworst.wav"));
		sureManButton = new JButton("<html><center>Yeah, sure<br>man</center></html>");
		sureManButton.addActionListener(new SingleSoundButton("/sounds/sureman.wav"));
		bscuseMeButton = new JButton("B'scuse me");
		bscuseMeButton.addActionListener(new SingleSoundButton("/sounds/bscuseme.wav"));
		whatButton = new JButton("What");
		whatButton.addActionListener(new SingleSoundButton("/sounds/what.wav"));
		thatMakesMeFeelAngryButton = new JButton("<html><center>That makes me<br>feel angry!</center></html>");
		thatMakesMeFeelAngryButton.addActionListener(new SingleSoundButton("/sounds/makesmefeelangry.wav"));
		daringTodayButton = new JButton("<html><center>Daring today,<br>aren't we?</center></html>");
		daringTodayButton.addActionListener(new SingleSoundButton("/sounds/daringtoday.wav"));
		vsauceButton = new JButton("<html><center>Vsauce<br>uhhowhaaa</center></html>");
		vsauceButton.addActionListener(new SingleSoundButton("/sounds/pancake.wav"));
		despiseHimButton = new JButton("I despise him!");
		despiseHimButton.addActionListener(new SingleSoundButton("/sounds/despisehim.wav"));
		whatDuhHeckButton = new JButton("<html><center>What duh heck is up with dis game</center></html>");
		whatDuhHeckButton.addActionListener(new SingleSoundButton("/sounds/whatduhheck.wav"));
		ohMyGodButton = new JButton("Oh my GOD");
		ohMyGodButton.addActionListener(new SingleSoundButton("/sounds/ohmygod.wav"));
		doingStringsButton = new JButton("<html>This dude's<br>doing strings</html>");
		doingStringsButton.addActionListener(new SingleSoundButton("/sounds/doingstrings.wav"));
		churlishButton = new JButton("<html>Insubordinate<br>and churlish</html>");
		churlishButton.addActionListener(new SingleSoundButton("/sounds/churlish.wav"));
		chicanerousButton = new JButton("<html><center>Mischievous and deceitful, chicanerous and deplorable</center></html>");
		chicanerousButton.addActionListener(new SingleSoundButton("/sounds/chicanerous.wav"));
		
		String[] ghoulSounds = new String[] {"/sounds/ghoul1.wav", "/sounds/ghoul2.wav",
				"/sounds/ghoul3.wav", "/sounds/ghoul4.wav"
		};
		String[] ghoulNames = new String[] {"Ghoul 1", "Ghoul 2", "Ghoul 3", "Ghoul 4"};
		ghoulButton = new JButton("Ghoul");
		ghoulButton.addMouseListener(new MultiSoundButton(ghoulSounds, ghoulNames));
		
		String[] cheatSounds = new String[] {"/sounds/cheat1.wav", "/sounds/cheat2.wav",
				"/sounds/cheat3.wav", "/sounds/cheat4.wav", "/sounds/cheat5.wav"
		};
		String[] cheatNames = new String[] {"Cheater, don't you do that!", "Cheater!",
				"Cheeeet", "Cheater! 2", "Cheat"
		};
		cheaterButton = new JButton("Cheater!");
		cheaterButton.addMouseListener(new MultiSoundButton(cheatSounds, cheatNames));
		
		panel.add(doItAgainButton);
		panel.add(neutralAerialButton);
		panel.add(trashManButton);
		panel.add(yodaNoButton);
		panel.add(bruhSoundEffectButton);
		panel.add(angerousNowButton);
		panel.add(theWorstButton);
		panel.add(sureManButton);
		panel.add(bscuseMeButton);
		panel.add(whatDuhHeckButton);
		panel.add(ghoulButton);
		panel.add(whatButton);
		panel.add(thatMakesMeFeelAngryButton);
		panel.add(daringTodayButton);
		panel.add(vsauceButton);
		panel.add(despiseHimButton);
		panel.add(cheaterButton);
		panel.add(ohMyGodButton);
		panel.add(doingStringsButton);
		panel.add(churlishButton);
		panel.add(chicanerousButton);
		
		frame.getContentPane().add(panel);
		
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/img/Icon.png")));
		
		frame.setVisible(true);
	}
	
	private class SingleSoundButton implements ActionListener {
		private String soundLocation;
		
		public SingleSoundButton(String sound) {
			soundLocation = sound;
		}
		
		public void actionPerformed(ActionEvent e) {
			try {
				InputStream is = getClass().getResourceAsStream(soundLocation);
				AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
				Clip clip = AudioSystem.getClip();
				clip.open(stream);
				clip.start();
			} catch(Exception ex) {
				Util.error(ex);
			}
		}
	}
	
	private class MultiSoundButton implements MouseListener {
		private int soundSelected;
		private String[] soundList;
		
		private JPopupMenu menu;
		
		public MultiSoundButton(String[] soundList, String[] soundNames) {
			this.soundList = soundList;
			
			soundSelected = -1;
			
			menu = new JPopupMenu();
			
			JRadioButtonMenuItem rand = new JRadioButtonMenuItem("Random");
			rand.addActionListener(new MenuItemActionListener(-1));
			rand.setSelected(true);
			
			ButtonGroup bg = new ButtonGroup();
			bg.add(rand);
			menu.add(rand);
			
			for(int at = 0; at < soundList.length; at++) {
				JRadioButtonMenuItem button = new JRadioButtonMenuItem(soundNames[at]);
				button.addActionListener(new MenuItemActionListener(at));
				bg.add(button);
				menu.add(button);
			}
		}
		
		private class MenuItemActionListener implements ActionListener {
			private int setTo;
			
			public MenuItemActionListener(int setTo) {
				this.setTo = setTo;
			}
			
			public void actionPerformed(ActionEvent e) {
				soundSelected = setTo;
			}
		}
		
		public void mouseClicked(MouseEvent e) {
			if(SwingUtilities.isLeftMouseButton(e)) {
				int soundToPlay;
				if(soundSelected == -1) {
					soundToPlay = ThreadLocalRandom.current().nextInt(0, soundList.length);
				}
				else {
					soundToPlay = soundSelected;
				}
				
				try {
					InputStream is;
					is = getClass().getResourceAsStream(soundList[soundToPlay]);
					AudioInputStream stream = AudioSystem.getAudioInputStream(new BufferedInputStream(is));
					Clip clip = AudioSystem.getClip();
					clip.open(stream);
					clip.start();
				} catch(Exception ex) {
					Util.error(ex);
				}
			}
			else if(SwingUtilities.isRightMouseButton(e)) {
				armPopup();
				Point mousePos = MouseInfo.getPointerInfo().getLocation();
				menu.setLocation(mousePos.x, mousePos.y);
				menu.setVisible(true);
			}
		}
		
		private void armPopup() {
		    if(menu != null) {
		        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
		            public void eventDispatched(AWTEvent event) {
		                if(event instanceof MouseEvent) {
		                    MouseEvent m = (MouseEvent)event;
		                    if(m.getID() == MouseEvent.MOUSE_CLICKED) {
		                        menu.setVisible(false);
		                        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
		                    }
		                }
		                if(event instanceof WindowEvent) {
		                    WindowEvent we = (WindowEvent)event;
		                    if(we.getID() == WindowEvent.WINDOW_DEACTIVATED || we.getID() == WindowEvent.WINDOW_STATE_CHANGED) {
		                        menu.setVisible(false);
		                        Toolkit.getDefaultToolkit().removeAWTEventListener(this);
		                    }
		                }
		            }
		        }, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.WINDOW_EVENT_MASK);
		    }
		}

		public void mousePressed(MouseEvent e) {
		}

		public void mouseReleased(MouseEvent e) {
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
	}
	
	private class SoundboardWindowListener implements WindowListener {

		public void windowOpened(WindowEvent e) {
			Util.log("You've opened the soundboard panel.");
			state.openedSoundboard = true;
		}

		public void windowClosing(WindowEvent e) {

		}

		public void windowClosed(WindowEvent e) {
			Util.log("You've closed the soundboard panel.");
			state.openedSoundboard = false;
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
