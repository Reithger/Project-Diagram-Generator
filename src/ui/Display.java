package ui;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import filemeta.FileChooser;
import filemeta.config.Config;
import image.ConvertVisual;
import input.CustomEventReceiver;
import input.NestedEventReceiver;
import visual.composite.popout.PopoutAlert;
import visual.composite.HandlePanel;
import visual.composite.ImageDisplay;
import visual.frame.WindowFrame;

public class Display {
	
	private final static int DEFAULT_WIDTH = 800;
	private final static int DEFAULT_HEIGHT = 600;
	private final static double VERTICAL_RATIO = 1.0 / 4;
	public final static String DOT_ADDRESS_VAR = "dotAddress";
	public final static String ADDRESS_SETTINGS = "./Diagram/settings/";
	public final static String ADDRESS_IMAGES = "./Diagram/images/";
	public final static String ADDRESS_SOURCES = "./Diagram/sources/";
	public final static String ADDRESS_CONFIG = ADDRESS_SETTINGS + "/config.txt";
	private final static String DEFAULT_CONFIG_PATH = "/assets/config.properties";
	
	private final static String DEFAULT_CONFIG_COMMENT = "##############################################################\r\n" + 
			"#                       Configurations                       #\r\n" + 
			"##############################################################\r\n" + 
			"# Format as 'name = address', the \" = \" spacing is necessary\r\n" + 
			"# It's awkward but it makes the file reading easier and I'm telling you this directly";
	
	private final static String ENTRY_LABEL_PROJECT_ROOT = "text_entry_root";
	private final static String ENTRY_LABEL_SUB_PACKAGE = "text_entry_sub";
	private final static String ENTRY_LABEL_SAVE_NAME = "text_entry_name";
	private final static Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 14);
	
	private final static int CODE_SHOW_INSTANCE = 50;
	private final static int CODE_SHOW_FUNCTION = 51;
	private final static int CODE_SHOW_PRIVATE = 52;
	private final static int CODE_GENERATE_UML = 53;
	private final static int CODE_NAVIGATE_SRC = 54;
	private final static int CODE_NAVIGATE_SUB_PKG = 55;
	
	private final static String DEFAULT_SRC_TEXT = "Path to Project src";
	private final static String DEFAULT_PKG_TEXT = "Packages to ignore";
	
	private final static String[][] BOOLEAN_SELECTION = new String[][] {{"Show Instance Variables?"}, 
																		{"Show Functions?"},
																		{"Show Private Entities?"}
																		};
	private final static int[][] SELECTION_CODES = new int[][] {{CODE_SHOW_INSTANCE},
																{CODE_SHOW_FUNCTION},
																{CODE_SHOW_PRIVATE}
																};
	
	private String GENERATE_RECT_NAME = "generate_rect";
																
	private WindowFrame frame;
	private HandlePanel panel;
	private HandlePanel image;
	private ImageDisplay display;
	private boolean[][] state;
	
	private boolean testing = true;
	
	private String path;
	private String ignore;
	private String nom;
	
	public Display() {
		fileConfiguration();
		
		path = testing ? "C:/Users/Borinor/eclipse-workspace/Project Diagram Generator/src/" : DEFAULT_SRC_TEXT;
		nom = "Name";
		ignore = DEFAULT_PKG_TEXT;
		
		ConvertVisual.assignPaths(ADDRESS_IMAGES, ADDRESS_SOURCES, ADDRESS_SETTINGS);
		frame = new WindowFrame(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		frame.setName("Test");
		int panelX = 0;
		int panelY = (int)(DEFAULT_HEIGHT * (1 - VERTICAL_RATIO));
		panel = new HandlePanel(panelX, panelY, DEFAULT_WIDTH, (int)(DEFAULT_HEIGHT * VERTICAL_RATIO));
		panel.setEventReceiver(new CustomEventReceiver() {
			
			@Override
			public void clickEvent(int code, int x, int y, int clickType) {
				nom = panel.getElementStoredText(ENTRY_LABEL_SAVE_NAME);
				for(int i = 0; i < SELECTION_CODES.length; i++) {
					for(int j = 0; j < SELECTION_CODES[i].length; j++) {
						if(code == SELECTION_CODES[i][j]) {
							state[i][j] = !state[i][j];
						}
					}
				}
				switch(code) {
					case CODE_GENERATE_UML:
						String rootPath = path;
						if(!(new File(rootPath)).exists()){
							new PopoutAlert(300, 250, "Source folder for project not found.");
						}
						else {
							ArrayList<String> igno = processPackagesIgnore(ignore);
							String path = ConvertVisual.generateUMLDiagram(rootPath, igno, nom, state[0][0], state[1][0], state[2][0]);
							showImage(path);
							new PopoutAlert(300, 250, "UML generated to: \"" + path + "\".");
						}
						break;
					case CODE_NAVIGATE_SRC:
						path = FileChooser.promptSelectFile("C://", true, true).getAbsolutePath();
						break;
					case CODE_NAVIGATE_SUB_PKG:
						String rootPath2 = panel.getElementStoredText(ENTRY_LABEL_PROJECT_ROOT);
						if((new File(rootPath2)).exists()) {
							PopoutPackageNavigator ppn = new PopoutPackageNavigator(rootPath2);
							ignore = ppn.getPackageCode();
							ppn.dispose();
						}
						else {
							new PopoutAlert(300, 250, "Source folder for project not found.");
						}
						break;
					default:
						break;
				}
				drawPanel();
			}
			
		});
		image = new HandlePanel(0, 0, DEFAULT_WIDTH, (int)(DEFAULT_HEIGHT * (1 - VERTICAL_RATIO)));
		
		display = new ImageDisplay("/assets/Complexity.jpg", image);
		
		image.setEventReceiver(new NestedEventReceiver(display.generateEventReceiver()));
		
		panel.setScrollBarHorizontal(false);
		panel.setScrollBarVertical(false);
		
		display.autofitImage();
		display.refresh();
		
		image.setScrollBarHorizontal(false);
		image.setScrollBarVertical(false);
		
		frame.reserveWindow("display");
		frame.showActiveWindow("display");
		frame.addPanelToWindow("display", "panel", panel);
		frame.addPanelToWindow("display", "image", image);
		
		state = new boolean[SELECTION_CODES.length][SELECTION_CODES[0].length];
		
		drawPanel();
		drawImage();
	}
	
	private ArrayList<String> processPackagesIgnore(String sub){
		ArrayList<String> out = new ArrayList<String>();
		String[] use = sub.split(";");
		for(String s : use) {
			out.add(s);
		}
		return out;
	}
	
	private void showImage(String in) {
		display.setImage(in);
		display.autofitImage();
		display.refresh();
	}
	
	private void drawPanel() {
		int wid = panel.getWidth();
		int hei = panel.getHeight();
		int iconSize = wid / 30;
		
		panel.handleLine("line_1", false, 5, 0, 0, wid - 1, 0, 3, Color.black);
		panel.handleLine("line_2", false, 5, 1, 0, 1, hei, 2, Color.black);
		panel.handleLine("line_3", false, 5, wid - 1, hei, wid - 1, 0, 2, Color.black);
		panel.handleLine("line_4", false, 5, wid - 1, hei - 1, 0, hei - 1, 2, Color.black);
		
		int subCode = -51;
		
		int posX = wid / 5;
		int posY = hei / 5 + hei/20;
		int chngY = hei / 4;
		int horzWid = wid / 3;
		int vertHei = hei / 6;
		int extend = wid / 6;
		
		panel.handleRectangle("rect_entry_root", false, 10, posX + extend / 2, posY, horzWid + extend, vertHei, Color.white, Color.black);
		panel.handleTextEntry(ENTRY_LABEL_PROJECT_ROOT, false, posX + extend / 2, posY, horzWid + extend, vertHei, subCode--, DEFAULT_FONT, path);
		panel.handleImageButton("filepath_src_button", false, posX + wid * 9 / 48 + extend, posY, iconSize, iconSize, "src/assets/plus_icon.png", CODE_NAVIGATE_SRC);
		panel.handleRectangle("filepath_src_rect", false, 5, posX + wid * 9 / 48 + extend, posY, iconSize, iconSize, Color.white, Color.black);
		posY += chngY;
		panel.handleRectangle("rect_entry_sub", false, 10, posX + extend / 2, posY, horzWid + extend, vertHei, Color.white, Color.black);
		panel.handleTextEntry(ENTRY_LABEL_SUB_PACKAGE, false, posX + extend / 2, posY, horzWid + extend, vertHei, subCode--, DEFAULT_FONT, ignore);
		panel.handleImageButton("pkg_nvg_button", false, posX + wid * 9 / 48 + extend, posY, iconSize, iconSize, "src/assets/plus_icon.png", CODE_NAVIGATE_SUB_PKG);
		panel.handleRectangle("pkg_nvg_rect", false, 5, posX + wid * 9 / 48 + extend, posY, iconSize, iconSize, Color.white, Color.black);
		posY += chngY;
		panel.handleRectangle("rect_entry_name", false, 10, posX, posY, horzWid, vertHei,Color.white, Color.black);
		panel.handleTextEntry(ENTRY_LABEL_SAVE_NAME, false, posX, posY, horzWid, vertHei, subCode--, DEFAULT_FONT, nom);
	
		posX += wid * 7 / 24;
		
		panel.handleRectangle(GENERATE_RECT_NAME, false, 10, posX, posY, wid / 6, hei / 6, Color.gray, Color.black);
		panel.handleText("butt_text", false, posX, posY, wid / 6, hei / 6, DEFAULT_FONT, "Generate");
		panel.handleButton("butt_generate", false, posX, posY, wid / 6, hei / 6, CODE_GENERATE_UML);
		
		int acro = SELECTION_CODES.length;
		int upd = SELECTION_CODES[0].length;
		
		int widChng = wid / 3 / (acro + 1);
		int widStrt = wid * 13 / 24;
		int heiChng = hei / 6;
		int heiStrt = hei / 5;
		int size = wid / 40;
				
		panel.removeElementPrefixed("checkbox");
		for(int i = 0; i < acro; i++) {
			for(int j = 0; j < upd; j++) {
				if(SELECTION_CODES[i][j] == -1) {
					continue;
				}
				posX = (acro / 2 - j) * widChng + widStrt;
				posY = i * heiChng + heiStrt;
				panel.handleRectangle("checkbox_" + i + "_" + j, false, 10, posX, posY, size, size, state[i][j] ? Color.gray : Color.white, Color.black);
				panel.handleButton("checkbox_butt_" + i  + " " + j, false, posX, posY, size, size, SELECTION_CODES[i][j]);
				panel.handleText("checkbox_text_" + i + "_" + j, false, posX + wid / 6, posY, wid / 3, size, DEFAULT_FONT, BOOLEAN_SELECTION[i][j]);
			}
		}
	}
	
	private void drawImage() {
		display.drawPage();
	}
	
	//-- File Configuration  ----------------------------------
	
	private void fileConfiguration() {
		Config c = new Config("", new UMLConfigValidation());
		c.addFilePath("Diagram");
		c.addFilePath("Diagram/settings");
		c.addFilePath("Diagram/images");
		c.addFilePath("Diagram/sources");
		c.addFile("Diagram/settings", "config.txt", DEFAULT_CONFIG_COMMENT);
		c.addFileEntry("Diagram/settings", "config.txt", DOT_ADDRESS_VAR, "Where is your dot program located? It will be called externally.", "?");
		
		c.softWriteConfig();
		
		while(!c.verifyConfig()) {
			switch(c.getErrorCode()) {
				case UMLConfigValidation.CODE_FAILURE_DOT_ADDRESS:
					PopoutAlert pA = new PopoutAlert(400, 250, "Please navigate to and select the path for your graphviz/bin/dot.exe file in the following navigation tool");
					c.setConfigFileEntry("Diagram/settings/config.txt", DOT_ADDRESS_VAR, FileChooser.promptSelectFile("C:/", true, true).getAbsolutePath());
					pA.dispose();
					break;
				case UMLConfigValidation.CODE_FAILURE_FILE_MISSING:
					c.initializeDefaultConfig();
					break;
				default:
					break;
			}
		}
	}
	
}
