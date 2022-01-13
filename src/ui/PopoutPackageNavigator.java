package ui;

import java.awt.Color;
import java.awt.Font;
import java.io.File;

import visual.composite.HandlePanel;
import visual.composite.popout.PopoutWindow;

public class PopoutPackageNavigator extends PopoutWindow{

	private final static int DEFAULT_WIDTH = 300;
	private final static int DEFAULT_HEIGHT = 600;
	private final static String ICON_PATH_OPEN = "src/assets/chest_open.png";
	private final static String ICON_PATH_CLOSED = "src/assets/closed_chest.png";
	private final static String ICON_BEND = "src/assets/l-bend.png";
	private final static int CODE_SUBMIT = -15;
	public final static String COMM_CODE = "Arbitrary";
	
	private final static Font DEFAULT_FONT = new Font("Serif", Font.BOLD, 18);
	
	private String rootPath;
	private Node root;
	private HandlePanel p;
	private int codeCounter;
	private volatile boolean ready;
	
	public PopoutPackageNavigator(String inRoot) {
		super(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		p = getHandlePanel();
		rootPath = inRoot;
		File f = new File(rootPath);
		root = new Node(f.getName(), codeCounter++);
		explore(f, root);
		drawPage();
	}
	
	public String getPackageCode() {
		while(!ready) {}
		String out = root.getDisabled("");
		return out;
	}

	private void explore(File in, Node n) {
		for(String f : in.list()) {
			File ref = new File(in.getAbsolutePath() + "/" + f);
			if(ref.isDirectory()) {
				Node c = new Node(f, codeCounter++);
				n.addChild(c);
				explore(ref, c);
			}
		}
	}
	
	private void drawPage() {
		int maxY = drawNode(root, p.getWidth() / 20, p.getHeight() / 20);
		p.handleRectangle("spacer", "no_move", 15, p.getWidth() / 2, maxY + p.getHeight() / 15, 5, 5, Color.white, Color.white);
		p.handleTextButton("submit", "move", 5, p.getWidth() - p.getWidth() / 6, p.getHeight() / 20, p.getWidth() / 5, p.getHeight() / 16, DEFAULT_FONT, "Submit", CODE_SUBMIT, Color.green, Color.black);
		p.addScrollbar("scrollbar", 20, "no_move", p.getWidth() - p.getWidth() / 20, 0, p.getWidth() / 20, p.getHeight(), 0, p.getHeight(), "move", true);
	}
	
	private int drawNode(Node n, int x, int y) {
		int iconSize = p.getHeight() / 20;
		p.handleImageButton("node_butt_" + n.getName() + "_" + x + "_" + y, "no_move", 10, x, y, iconSize, iconSize, n.getStatus() ? ICON_PATH_OPEN : ICON_PATH_CLOSED, n.getCode());
		p.addText("node_text_" + n.getName() + "_" + x + "_" + y, 15, "no_move", x + iconSize, y, p.getWidth() / 3, p.getHeight() / 15, n.getName() + "/", DEFAULT_FONT, false, true, false);
		y += p.getHeight() / 15;
		if(n.getStatus() && n.getChildren().size() > 0)
			p.handleImage("lbend_" + n.getName() + "_" + x + "_" + y, "no_move", 5, x, y, ICON_BEND, 1);
		x += p.getWidth() / 10;
		if(n.getStatus()) {
			for(Node n2 : n.getChildren()) {
				y = drawNode(n2, x, y);
			}
		}
		return y;
	}
	
	@Override
	public void clickAction(int code, int x, int y) {
		if(code == CODE_SUBMIT) {
			ready = true;
		} else if(code != -1) {
			Node found = root.findCode(code);
			found.toggle();
			p.removeElementPrefixed("");
			drawPage();
		}
	}

}
