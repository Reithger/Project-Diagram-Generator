package ui;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.Scanner;

import image.ConvertVisual;
import image.GraphViz;
import input.Communication;
import ui.popups.PopoutConfig;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public class Display {
	
	private final static int DEFAULT_WIDTH = 800;
	private final static int DEFAULT_HEIGHT = 600;
	private final static double VERTICAL_RATIO = 1.0 / 4;
	private final static String DOT_ADDRESS_VAR = "dotAddress";
	public final static String ADDRESS_SETTINGS = "./Diagram/settings/";
	public final static String ADDRESS_IMAGES = "./Diagram/images/";
	public final static String ADDRESS_SOURCES = "./Diagram/sources/";
	public final static String ADDRESS_CONFIG = ADDRESS_SETTINGS + "/config.txt";
	private final static String DEFAULT_CONFIG_PATH = "/assets/config.properties";
	
	private final static String ENTRY_LABEL_PROJECT_ROOT = "text_entry_root";
	private final static String ENTRY_LABEL_SUB_PACKAGE = "text_entry_sub";
	private final static String ENTRY_LABEL_SAVE_NAME = "text_entry_name";
	private final static Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 14);
	
	private final static int CODE_SHOW_INSTANCE = 50;
	private final static int CODE_SHOW_FUNCTION = 51;
	private final static int CODE_SHOW_PRIVATE = 52;
	private final static int CODE_GENERATE_UML = 53;
	
	private final static String[][] BOOLEAN_SELECTION = new String[][] {{"Show Instance Variables?"}, 
																		{"Show Functions?"},
																		{"Show Private Entities?"}
																		};
	private final static int[][] SELECTION_CODES = new int[][] {{CODE_SHOW_INSTANCE},
																{CODE_SHOW_FUNCTION},
																{CODE_SHOW_PRIVATE}
																};
	
	private WindowFrame frame;
	private ElementPanel panel;
	private ElementPanel image;
	private ImageDisplay display;
	private String dotAddress;
	private boolean[][] state;
	
	//TODO: Automatically detect package hierarchy and give option to only view a subset of them; has to provide root path to /src/ folder then designate a package separately
	
	public Display() {
		fileConfiguration();
		ConvertVisual.assignPaths(ADDRESS_IMAGES, ADDRESS_SOURCES, ADDRESS_SETTINGS);
		frame = new WindowFrame(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		panel = new ElementPanel(0, (int)(DEFAULT_HEIGHT * (1 - VERTICAL_RATIO)), DEFAULT_WIDTH, (int)(DEFAULT_HEIGHT * VERTICAL_RATIO)) {
			
			@Override
			public void clickBehaviour(int code, int x, int y) {
				for(int i = 0; i < SELECTION_CODES.length; i++) {
					for(int j = 0; j < SELECTION_CODES[i].length; j++) {
						if(code == SELECTION_CODES[i][j]) {
							state[i][j] = !state[i][j];
						}
					}
				}
				switch(code) {
					case CODE_GENERATE_UML:
						String rootPath = this.getElementStoredText(ENTRY_LABEL_PROJECT_ROOT);
						String sub = this.getElementStoredText(ENTRY_LABEL_SUB_PACKAGE);
						String name = this.getElementStoredText(ENTRY_LABEL_SAVE_NAME);
						String path = ConvertVisual.generateUMLDiagram(rootPath, sub, name, state[0][0], state[1][0], state[2][0]);
						System.out.println(path);
						showImage(path);
						break;
					default:
						break;
				}
				drawPanel();
			}
			
			@Override
			public void keyBehaviour(char code) {
				System.out.println(code);
			}
			
		};
		image = new ElementPanel(0, 0, DEFAULT_WIDTH, (int)(DEFAULT_HEIGHT * (1 - VERTICAL_RATIO))) {
			
			public void keyBehaviour(char code) {
				display.processKeyInput(code);
				drawImage();
			}
			
			public void clickBehaviour(int code, int x, int y) {
				display.processClickInput(code);
				drawImage();
			}

			@Override
			public void mouseWheelBehaviour(int scroll) {
				display.processMouseWheelInput(scroll);
				drawImage();
			}
			
			public void clickPressBehaviour(int code, int x, int y) {
				display.processPressInput(code, x, y);
				drawImage();
			}
			
			public void clickReleaseBehaviour(int code, int x, int y) {
				display.processReleaseInput(code, x, y);
				drawImage();
			}
			
			@Override
			public void dragBehaviour(int code, int x, int y) {
				display.processDragInput(code, x, y);
				drawImage();
			}
		};
		
		panel.setScrollBarHorizontal(false);
		panel.setScrollBarVertical(false);
		
		display = new ImageDisplay("/assets/fun.jpg", image);
		
		image.setScrollBarHorizontal(false);
		image.setScrollBarVertical(false);
		
		frame.reserveWindow("display");
		frame.showActiveWindow("display");
		frame.reservePanel("display", "panel", panel);
		frame.reservePanel("display", "image", image);
		
		state = new boolean[SELECTION_CODES.length][SELECTION_CODES[0].length];
		
		drawPanel();
		drawImage();
	}
	
	private void showImage(String in) {
		display = new ImageDisplay(in, image);
	}
	
	private void drawPanel() {
		int wid = panel.getWidth();
		int hei = panel.getHeight();
		
		if(!panel.moveElement("line_1", 0, 0))
			panel.addLine("line_1", 5, false, 0, 0, wid - 1, 0, 3, Color.black);
		if(!panel.moveElement("line_2", 1, 0))
			panel.addLine("line_2", 5, false, 1, 0, 1, hei, 2, Color.black);
		if(!panel.moveElement("line_3", wid - 1, hei))
			panel.addLine("line_3", 5, false, wid - 1, hei, wid - 1, 0, 2, Color.black);
		if(!panel.moveElement("line_4", wid - 1, hei - 1))
			panel.addLine("line_4", 5, false, wid - 1, hei - 1, 0, hei - 1, 2, Color.black);
		
		int subCode = -51;
		
		int posX = wid / 5;
		int posY = hei / 5;
		int chngY = hei / 4;
		int horzWid = wid / 3;
		int vertHei = hei / 6;
		
		if(!panel.moveElement("rect_entry_root", posX, posY))
			panel.addRectangle("rect_entry_root", 10, false, posX, posY, horzWid, vertHei, true, Color.white, Color.black);
		if(!panel.moveElement(ENTRY_LABEL_PROJECT_ROOT, posX, posY))
			panel.addTextEntry(ENTRY_LABEL_PROJECT_ROOT, 15, false, posX, posY, horzWid, vertHei, subCode--, "", DEFAULT_FONT, true, true, true);
		posY += chngY;
		if(!panel.moveElement("rect_entry_sub", posX, posY))
			panel.addRectangle("rect_entry_sub", 10, false, posX, posY, horzWid, vertHei, true, Color.white, Color.black);
		if(!panel.moveElement(ENTRY_LABEL_SUB_PACKAGE, posX, posY))
			panel.addTextEntry(ENTRY_LABEL_SUB_PACKAGE, 15, false, posX, posY, horzWid, vertHei, subCode--, "", DEFAULT_FONT, true, true, true);
		posY += chngY;
		if(!panel.moveElement("rect_entry_name", posX, posY))
			panel.addRectangle("rect_entry_name", 10, false, posX, posY, horzWid, vertHei, true, Color.white, Color.black);
		if(!panel.moveElement(ENTRY_LABEL_SAVE_NAME, posX, posY))
			panel.addTextEntry(ENTRY_LABEL_SAVE_NAME, 15, false, posX, posY, horzWid, vertHei, subCode--, "", DEFAULT_FONT, true, true, true);
		
		posX += wid * 7 / 24;
		
		if(!panel.moveElement("rect_generate", posX, posY))
			panel.addRectangle("rect_generate", 10, false, posX, posY, wid / 6, hei / 6, true, Color.gray, Color.black);
		if(!panel.moveElement("butt_text", posX, posY))
			panel.addText("butt_text", 20, false, posX, posY, wid / 6, hei / 6, "Generate", DEFAULT_FONT, true, true, true);
		if(!panel.moveElement("butt_generate", posX, posY))
			panel.addButton("butt_generate", 10, false, posX, posY, wid / 6, hei / 6, null, CODE_GENERATE_UML, true);
		
		int acro = SELECTION_CODES.length;
		int upd = SELECTION_CODES[0].length;
		
		int widChng = wid / 3 / (acro + 1);
		int widStrt = wid * 13 / 24;
		int heiChng = hei / 6;
		int heiStrt = hei / 5;
		int size = wid / 40;
				
		for(int i = 0; i < acro; i++) {
			for(int j = 0; j < upd; j++) {
				if(SELECTION_CODES[i][j] == -1) {
					continue;
				}
				posX = (acro / 2 - j) * widChng + widStrt;
				posY = i * heiChng + heiStrt;
				if(!panel.moveElement("checkbox_" + i + "_" + j, posX, posY)) 
						panel.addRectangle("checkbox_" + i + "_" + j, 10, false, posX, posY, size, size, true, state[i][j] ? Color.gray : Color.white, Color.black);
				if(!panel.moveElement("checkbox_butt_" + i  + " " + j, posX, posY)) 
						panel.addButton("checkbox_butt_" + i  + " " + j, 10, false,  posX, posY, size, size, null, SELECTION_CODES[i][j], true);
				if(!panel.moveElement("checkbox_text_" + i + "_" + j, posX + wid / 6, posY)) 
						panel.addText("checkbox_text_" + i + "_" + j, 20, false,  posX + wid / 6, posY, wid / 3, size, BOOLEAN_SELECTION[i][j], DEFAULT_FONT, true, true, true);
			}
		}
	}
	
	private void drawImage() {
		display.drawPage();
	}
	
	//-- File Configuration  ----------------------------------
	
	private void fileConfiguration() {
		File settings = new File(ADDRESS_SETTINGS);
		File images = new File(ADDRESS_IMAGES);
		File source = new File(ADDRESS_SOURCES);
		images.mkdirs();
		source.mkdirs();
		settings.mkdirs();
		File config = new File(ADDRESS_CONFIG);
		if(!config.exists() || !verifyConfigFile(config)){
			createConfigurationFile(config);
		}
		readDirectories(config);
	}
	
	private boolean verifyConfigFile(File f) {
		try {
			Scanner sc = new Scanner(f);
			String line;
			while(sc.hasNextLine()) {
				line = sc.nextLine();
				if(!line.matches("#.*")) { //TODO: This is a bad verification, doesn't extend
					if(!line.matches(DOT_ADDRESS_VAR + " = .*")) {
						sc.close();
						return false;
					}
				}
			}
			sc.close();
		}
		catch(Exception e) {
			e.printStackTrace();
			//TODO: Error window popup
		}
		return true;
	}
	
	private void createConfigurationFile(File config) {
		try {
			if(config.exists()) {
				config.delete();
			}
			config.createNewFile();
			BufferedReader defaultConfig = retrieveFileReader(DEFAULT_CONFIG_PATH);
			RandomAccessFile write = new RandomAccessFile(config, "rw");
			int c = defaultConfig.read();
			while(c != -1) {
				write.write(c);
				c = defaultConfig.read();
			}
			write.close();
		}
		catch(Exception e) {
			e.printStackTrace();
			//TODO: Error window popup
		}
	}
	
	private void readDirectories(File config) {
		try {
			Scanner sc = new Scanner(config);
			String line;
			while(sc.hasNextLine()) {
				line = sc.nextLine();
				if(line.matches(DOT_ADDRESS_VAR + " = .*")) {
					dotAddress = line.substring((DOT_ADDRESS_VAR + " = ").length());
					if(dotAddress.contentEquals("?") || !verifyDotAddress(dotAddress)) {
						PopoutConfig pop = new PopoutConfig();
						while(!verifyDotAddress(dotAddress)) {
							dotAddress = null;
							while(Communication.get("dot") == null) {}
							dotAddress = Communication.get("dot");
							Communication.set("dot", null);
							pop.failure();
						}
						pop.success();
						writeConfigEntry(DOT_ADDRESS_VAR, dotAddress);
					}
				}
			}
			sc.close();
		}
		catch(Exception e) {
			e.printStackTrace();
			//TODO: Error window popup
		}
	}
	
	private void writeConfigEntry(String entry, String contents) {
		try {
			File config = new File(ADDRESS_CONFIG);
			Scanner sc = new Scanner(config);
			StringBuilder sb = new StringBuilder();
			String line;
			while(sc.hasNextLine()) {
				line = sc.nextLine();
				if(line.matches(entry + ".*")) {
					sb.append(entry + " = " + contents + "\n");
				}
				else {
					sb.append(line + "\n");
				}
			}
			sc.close();
			config.delete();
			config.createNewFile();
			RandomAccessFile write = new RandomAccessFile(config, "rw");
			write.writeBytes(sb.toString());
			write.close();
		}
		catch(Exception e) {
			e.printStackTrace();
			//TODO: Error window popup
		}
	}
	
	private boolean verifyDotAddress(String path) {
		return GraphViz.verifyDotPath(path);
	}
	
//---  Mechanical   ---------------------------------------------------------------------------

	public BufferedReader retrieveFileReader(String pathIn) {
		String path = pathIn.replace("\\", "/");
		InputStream is = Display.class.getResourceAsStream(path); 
		if(is == null) {
			try {
				is = new FileInputStream(new File(path));
			}
			catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return new BufferedReader(new InputStreamReader(is));
	}
	
}
