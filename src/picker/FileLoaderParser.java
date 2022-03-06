package picker;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import data.ProgramState;
import ui.MainWindow;
import util.Util;

/**
 * This class is responsible for loading and parsing tier list files.
 * 
 * @author Jordan Knapp
 */
public class FileLoaderParser {
	
	private ProgramState state;
	private MainWindow parent;
	
	public FileLoaderParser(MainWindow parent, ProgramState state) {
		this.parent = parent;
		this.state = state;
	}
	
	/**
	 * This method chooses the file to be loaded, then calls the
	 * <code>parseFile()</code> method to actually parse it.
	 */
	public void loadFile() {
		JFileChooser fileChooser = new JFileChooser(".");
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Documents (*.txt)", "txt");
		fileChooser.setFileFilter(filter);
		int willLoad = fileChooser.showOpenDialog(null);
		
		if (willLoad == JFileChooser.APPROVE_OPTION) {
			parseFile(fileChooser.getSelectedFile());
		}
	}
	
	/**
	 * This method is responsible for actually parsing the given tier list file.
	 * list file. All data read from the file will be stored in fields found in
	 * the <code>ProgramState</code> class.
	 * 
	 * @param tierListFile	The file to be parsed.
	 */
	public void parseFile(File tierListFile) {
		boolean needToUpdateChances = false;
		
		try {
			BufferedReader in = new BufferedReader(new FileReader(tierListFile));
			
			//file structure is as follows:
			//upper double S 	index 0
			//mid double S		index 1
			//lower double S	index 2
			//upper S			index 3
			//mid S				index 4
			//lower S			index 5
			//upper A			index 6
			//mid A				index 7
			//lower A			index 8
			//upper B			index 9
			//mid B				index 10
			//lower B			index 11
			//upper C			index 12
			//mid C				index 13
			//lower C			index 14
			//upper D			index 15
			//mid D				index 16
			//lower D			index 17
			//upper E			index 18
			//mid E				index 19
			//lower E			index 20
			//upper F			index 21
			//mid F				index 22
			//lower F			index 23
			//p1exc				index 24
			//p2exc				index 25
			//p3exc				index 26
			//p4exc				index 27
			//p5exc				index 28
			//p6exc				index 29
			//p7exc				index 30
			//p8exc				index 31
			//p1fav				index 32
			//p2fav				index 33
			//p3fav				index 34
			//p4fav				index 35
			//p5fav				index 36
			//p6fav				index 37
			//p7fav				index 38
			//p8fav				index 39
			
			//read first line
			String lineAt = in.readLine();
			//continue reading all lines as long as they exist
			while(lineAt != null) {
				//scroll through the chars in the read line, if one is an equals
				//then check for which tier it is
				String next = "";
				boolean foundEqual = false;
				for(int at = 0; at < lineAt.length(); at++) {
					if(lineAt.charAt(at) == '=') {
						//remove space before equals sign and check name
						foundEqual = true;
						next = next.substring(0, next.length() - 1);
						next = next.toLowerCase();
						switch(next) {
							case "upper double s":
								readLine(0, at, lineAt);
								break;
							case "mid double s":
								readLine(1, at, lineAt);
								break;
							case "lower double s":
								readLine(2, at, lineAt);
								break;
							case "upper s":
								readLine(3, at, lineAt);
								break;
							case "mid s":
								readLine(4, at, lineAt);
								break;
							case "lower s":
								readLine(5, at, lineAt);
								break;
							case "upper a":
								readLine(6, at, lineAt);
								break;
							case "mid a":
								readLine(7, at, lineAt);
								break;
							case "lower a":
								readLine(8, at, lineAt);
								break;
							case "upper b":
								readLine(9, at, lineAt);
								break;
							case "mid b":
								readLine(10, at, lineAt);
								break;
							case "lower b":
								readLine(11, at, lineAt);
								break;
							case "upper c":
								readLine(12, at, lineAt);
								break;
							case "mid c":
								readLine(13, at, lineAt);
								break;
							case "lower c":
								readLine(14, at, lineAt);
								break;
							case "upper d":
								readLine(15, at, lineAt);
								break;
							case "mid d":
								readLine(16, at, lineAt);
								break;
							case "lower d":
								readLine(17, at, lineAt);
								break;
							case "upper e":
								readLine(18, at, lineAt);
								break;
							case "mid e":
								readLine(19, at, lineAt);
								break;
							case "lower e":
								readLine(20, at, lineAt);
								break;
							case "upper f":
								readLine(21, at, lineAt);
								break;
							case "mid f":
								readLine(22, at, lineAt);
								break;
							case "lower f":
								readLine(23, at, lineAt);
								break;
							case "p1 exclude":
								readLine(24, at, lineAt);
								break;
							case "p2 exclude":
								readLine(25, at, lineAt);
								break;
							case "p3 exclude":
								readLine(26, at, lineAt);
								break;
							case "p4 exclude":
								readLine(27, at, lineAt);
								break;
							case "p5 exclude":
								readLine(28, at, lineAt);
								break;
							case "p6 exclude":
								readLine(29, at, lineAt);
								break;
							case "p7 exclude":
								readLine(30, at, lineAt);
								break;
							case "p8 exclude":
								readLine(31, at, lineAt);
								break;
							case "p1 favorite":
								readLine(32, at, lineAt);
								break;
							case "p2 favorite":
								readLine(33, at, lineAt);
								break;
							case "p3 favorite":
								readLine(34, at, lineAt);
								break;
							case "p4 favorite":
								readLine(35, at, lineAt);
								break;
							case "p5 favorite":
								readLine(36, at, lineAt);
								break;
							case "p6 favorite":
								readLine(37, at, lineAt);
								break;
							case "p7 favorite":
								readLine(38, at, lineAt);
								break;
							case "p8 favorite":
								readLine(39, at, lineAt);
								break;
							case "tier chances":
								readSetting(1, at, lineAt);
								needToUpdateChances = true;
								break;
							case "cannot get size":
								readSetting(2, at, lineAt);
								break;
							case "allow ss in cannot get":
								readSetting(3, at, lineAt);
								break;
							case "allow s in cannot get":
								readSetting(4, at, lineAt);
								break;
							case "players":
								readSetting(5, at, lineAt);
								break;
							case "bump chances":
								readSetting(6, at, lineAt);
								needToUpdateChances = true;
								break;
							default:
								in.close();
								throw new IOException(next);
						}
					}
					else {
						next += lineAt.charAt(at);
					}
				}
				//if any lines are found that aren't valid, stop reading
				//file and throw an error
				//should allow for comments to be added as long as they
				//start with a #
				if(!foundEqual && !next.equals("") && !(next.charAt(0) == '#')) {
					in.close();
					throw new IOException();
				}
				lineAt = in.readLine();
			}
			in.close();
		} catch (FileNotFoundException e) {
			Util.error(e);
			parent.printToResult("File " + tierListFile.getName()
					+ " not found!");
			state.fileLoaded = false;
			return;
		} catch(IOException ioe) {
			Util.error(ioe);
			parent.printToResult("IOException in reading "
					+ tierListFile.getName() + ".\nThis means it is not a "
					+ "valid tier list file.\nPlease load a valid tier list "
					+ "file.\nMore details can be found in the debug menu.\n");
			state.fileLoaded = false;
			return;
		} catch(NumberFormatException nfe) {
			Util.error(nfe);
			parent.printToResult("NumberFormatException in reading "
					+ tierListFile.getName() + ".\nThis means it is not a "
					+ "valid tier list file.\nPlease load a valid tier list "
					+ "file.\nMore details can be found in the debug menu.\n");
			state.fileLoaded = false;
			return;
		}
		
		if(needToUpdateChances) {
			state.applyTierChances();
		}
		
		//printing out debug info in case anything ever goes wrong
		Util.log("The following data has been loaded as the current tier list:");
		for(int at = 0; at < 24; at++) {
			String tierAt = Util.tierToString(at);
			if(tierAt.equals("Upper Double S tier") || tierAt.equals("Lower Double S tier")) {
				Util.log(tierAt + ":\t" + state.linesOfFile.get(at));
			}
			else {
				Util.log(tierAt + ":\t\t" + state.linesOfFile.get(at));
			}
		}
		for(int at = 24; at < 32; at++) {
			Util.log("Player " + (at - 23) + " exclude:\t" + state.linesOfFile.get(at));
		}
		for(int at = 32; at < 40; at++) {
			Util.log("Player " + (at - 31) + " favorites:\t" + state.linesOfFile.get(at));
		}
		
		state.fileLoaded = true;
		parent.printToResult("Loaded file: " + tierListFile.getName());
	}
	
	private void readLine(int index, int startAt, String line) {
		//skip over the equals sign and space
		startAt += 2;
		
		String next = "";
		ArrayList<String> currentLine = new ArrayList<String>();
		for(int at = startAt; at < line.length(); at++) {
			if(line.charAt(at) == ',') {
				currentLine.add(next);
				next = "";
				at++;
			}
			else {
				next += line.charAt(at);
			}
		}
		currentLine.add(next);
		state.linesOfFile.add(index, currentLine);
	}
	
	private void readSetting(int id, int startAt, String line) throws IOException, NumberFormatException {
		//settings id's
		//1 = tier chances (comma-separated list; if they're valid, applied automatically)
		//2 = cannot get size (integer between 0 and 15)
		//3 = allow SS in cannot get buffer (true or false, 1 or 0)
		//4 = allow S in cannot get buffer (true or false, 1 or 0)
		//5 = number of players (integer between 2 and 8)
		//6 = bump chances (comma-separated list; if they're valid, applied automatically)
		String toRead = line.substring(startAt + 2);
		
		//a couple variables here are used in reading two different settings
		//so we'll define them before the switch statement
		String next = "";
		int numAt = 0;
		
		switch(id) {
			case 2:
				int newCannotGetSize = -1;
				
				newCannotGetSize = Integer.parseInt(toRead);
				
				if(newCannotGetSize >= 0 && newCannotGetSize <= 15) {
					state.cannotGetSize = newCannotGetSize;
					parent.updateCannotGetSpinner();
				}
				else {
					throw new IOException("New 'cannot get size' of " + toRead
							+ " is invalid. Please give a value between 0 and"
							+ " 15.");
				}
				break;
			case 3:
				boolean newSSAllowedInCannotGet = false;
				
				if(toRead.equals("true")) {
					newSSAllowedInCannotGet = true;
				}
				else if(toRead.equals("false")) {
					newSSAllowedInCannotGet = false;
				}
				else if(Integer.parseInt(toRead) == 1) {
					newSSAllowedInCannotGet = true;
				}
				else if(Integer.parseInt(toRead) == 0) {
					newSSAllowedInCannotGet = false;
				}
				else {
					throw new IOException("New 'ss allowed in cannot get' "
							+ "value of " + toRead + " is invalid. Please "
							+ "give a value of either true, false, 0, or "
							+ "1.");
				}
				
				state.allowSSInCannotGetBuffer = newSSAllowedInCannotGet;
				parent.updateSSAllowedInCannotGet();
				break;
			case 4:
				boolean newSAllowedInCannotGet = false;
				
				if(toRead.equals("true")) {
					newSAllowedInCannotGet = true;
				}
				else if(toRead.equals("false")) {
					newSAllowedInCannotGet = false;
				}
				else if(Integer.parseInt(toRead) == 1) {
					newSAllowedInCannotGet = true;
				}
				else if(Integer.parseInt(toRead) == 0) {
					newSAllowedInCannotGet = false;
				}
				else {
					throw new IOException("New 's allowed in cannot get' "
							+ "value of " + toRead + " is invalid. Please "
							+ "give a value of either true, false, 0, or "
							+ "1.");
				}

				state.allowSInCannotGetBuffer = newSAllowedInCannotGet;
				parent.updateSAllowedInCannotGet();
				break;
			case 5:
				int newNumPlayers = -1;
				
				newNumPlayers = Integer.parseInt(toRead);

				
				if(newNumPlayers >= 2 && newNumPlayers <= 8) {
					state.numPlayers = newNumPlayers;
					parent.updateNumPlayersSpinner();
				}
				else {
					Util.error("New 'players' value of " + toRead + " is "
							+ "invalid. Please give a value between 2 and 8.");
				}
				
				break;
			case 1:
				next = "";
				int[] fileTierChances = new int[8];
				numAt = 0;
				
				for(int at = 0; at < toRead.length(); at++) {
					if(toRead.charAt(at) == ',') {
						fileTierChances[numAt] = Integer.parseInt(next);
						next = "";
						numAt++;
						at++;
					}
					else {
						next += toRead.charAt(at);
					}
				}
				try {
					fileTierChances[numAt] = Integer.parseInt(next);
				} catch(ArrayIndexOutOfBoundsException e) {
					Util.error("Error in parsing custom tier chances. Please "
							+ "specify exactly 8 comma-separated values.");
					break;
				}
				
				for(int at = 0; at < 8; at++) {
					state.newTierChances[at] = fileTierChances[at];
				}
				
				break;
			case 6:
				next = "";
				int[] fileBumpChances = new int[3];
				numAt = 0;
				
				for(int at = 0; at < toRead.length(); at++) {
					if(toRead.charAt(at) == ',') {
						fileBumpChances[numAt] = Integer.parseInt(next);
						next = "";
						numAt++;
						at++;
					}
					else {
						next += toRead.charAt(at);
					}
				}
				try {
					fileBumpChances[numAt] = Integer.parseInt(next);
				} catch(ArrayIndexOutOfBoundsException e) {
					Util.error("Error in parsing custom bump chances. Please "
							+ "specify exactly 3 comma-separated values.");
					break;
				}
				
				
				for(int at = 0; at < 3; at++) {
					state.newBumpChances[at] = fileBumpChances[at];
				}
				
				break;
			default:
				//this is impossible to get to, still including it just in case
				Util.error(id + " is not a valid setting id. Something went "
						+ "very wrong for this to be reached.");
		}
	}

}
