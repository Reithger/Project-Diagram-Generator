package ui;

import java.awt.Image;

import visual.panel.ElementPanel;

public class ImageDisplay {

//---  Constant Values   ----------------------------------------------------------------------
	
	private final static double DEFAULT_ZOOM = 1.0;
	private static final double MOVEMENT_FACTOR = .1;
	private static final double ZOOM_FACTOR = 1.1;
	
	private static final double UI_BOX_RATIO_Y = 3 / 4.0;
	private static final double UI_BOX_RATIO_X = 4 / 5.0;
	public static final String IMAGE_NAME = "img";
	
	//-- Codes  -----------------------------------------------
	private static final int CODE_MOVE_RIGHT = 10;
	private static final int CODE_MOVE_DOWN = 11;
	private static final int CODE_MOVE_LEFT = 12;
	private static final int CODE_MOVE_UP = 13;
	private static final int CODE_ZOOM_IN = 14;
	private static final int CODE_ZOOM_OUT = 15;
	private static final int CODE_RESET_POSITION = 17;
	private static final char KEY_MOVE_RIGHT = 'd';
	private static final char KEY_MOVE_DOWN = 's';
	private static final char KEY_MOVE_LEFT = 'a';
	private static final char KEY_MOVE_UP = 'w';
	private static final char KEY_ZOOM_IN = 'q';
	private static final char KEY_ZOOM_OUT = 'e';
	private static final char KEY_RESET_POSITION = 'h';
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private String imageName;
	private String imagePath;
	private Image reference;
	private double zoom;
	private ElementPanel p;
	
	private int dragStartX;
	private int dragStartY;
	private boolean dragState;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public ImageDisplay(String path, ElementPanel in) {
		imagePath = path;
		p = in;
		zoom = 1;
		refresh();
		zoom = (double)in.getWidth() / reference.getWidth(null);
		zoom = zoom > (double)in.getHeight() / reference.getHeight(null) ? zoom : (double)in.getHeight() / reference.getHeight(null);
	}

//---  Operations   ---------------------------------------------------------------------------
	
	public void processClickInput(int code) {
		switch(code) {
			case CODE_MOVE_RIGHT:
				decreaseOriginX();
				break;
			case CODE_MOVE_LEFT:
				increaseOriginX();
				break;
			case CODE_MOVE_UP:
				increaseOriginY();
				break;
			case CODE_MOVE_DOWN:
				decreaseOriginY();
				break;
			case CODE_ZOOM_IN:
				increaseZoom();
				break;
			case CODE_ZOOM_OUT:
				decreaseZoom();
				break;
			case CODE_RESET_POSITION:
				resetPosition();
				break;
			}
	}
	
	public void processKeyInput(char code) {
		switch(code) {
			case KEY_MOVE_RIGHT:
				decreaseOriginX();
				break;
			case KEY_MOVE_LEFT:
				increaseOriginX();
				break;
			case KEY_MOVE_UP:
				increaseOriginY();
				break;
			case KEY_MOVE_DOWN:
				decreaseOriginY();
				break;
			case KEY_ZOOM_IN:
				increaseZoom();
				break;
			case KEY_ZOOM_OUT:
				decreaseZoom();
				break;
			case KEY_RESET_POSITION:
				resetPosition();
				break;
			}
	}
	
	public void processPressInput(int code, int x, int y) {
		dragStartX = x;
		dragStartY = y;
		dragState = true;
	}
	
	public void processReleaseInput(int code, int x, int y) {
		dragState = false;
	}
	
	public void processMouseWheelInput(int scroll) {
		if(scroll < 0) {
			increaseZoom();
		}
		else {
			decreaseZoom();
		}
		drawPage();
	}
	
	public void processDragInput(int code, int x, int y) {
		if(dragState) {
			dragOriginX(x - dragStartX);
			dragOriginY(y - dragStartY);
			dragStartX = x;
			dragStartY = y;
		}
	}
	
	public void drawPage() {
		p.addImage(IMAGE_NAME, 10, false, 0, 0, false, getImage(), getZoom());
	}

	public void refresh() {
		clear();
		reference = p.retrieveImage(imagePath);
		imageName = formatImageName(imagePath);
	}
	
	public void clear() {
		p.removeCachedImage(imagePath);
		p.removeElement(IMAGE_NAME);
		p.removeElementPrefixed("");
	}
	
	public void resetPosition() {
		p.setOffsetX(0);
		p.setOffsetY(0);
		zoom = DEFAULT_ZOOM;
		p.removeElement(IMAGE_NAME);
		drawPage();
	}
		
//---  Setter Methods   -----------------------------------------------------------------------
	
	public void setImagePath(String in) {
		p.removeCachedImage(imagePath);
		imagePath = in;
		refresh();
	}
	
	public void increaseOriginX() {
		p.setOffsetX((int)(p.getOffsetX() + reference.getWidth(null) * zoom * MOVEMENT_FACTOR));
	}

	public void increaseOriginY() {
		p.setOffsetY((int)(p.getOffsetY() + reference.getHeight(null) * zoom * MOVEMENT_FACTOR));
	}
	
	public void decreaseOriginX() {
		p.setOffsetX((int)(p.getOffsetX() - reference.getWidth(null) * zoom * MOVEMENT_FACTOR));
	}
	
	public void decreaseOriginY() {
		p.setOffsetY((int)(p.getOffsetY() - reference.getHeight(null) * zoom * MOVEMENT_FACTOR));
	}
	
	public void dragOriginX(int amount) {
		p.setOffsetX(p.getOffsetX() + amount);
	}
	
	public void dragOriginY(int amount) {
		p.setOffsetY(p.getOffsetY() + amount);
	}
	
	public void increaseZoom() {
		zoom *= ZOOM_FACTOR;
		p.removeElement(IMAGE_NAME);
	}
	
	public void decreaseZoom() {
		zoom /= ZOOM_FACTOR;
		p.removeElement(IMAGE_NAME);
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public String getImageName() {
		return imageName;
	}
	
	public String getImagePath() {
		return imagePath;
	}
	
	public Image getImage() {
		return reference;
	}
	
	public double getZoom() {
		return zoom;
	}
	
//---  Mechanics   ----------------------------------------------------------------------------
	
	private String formatImageName(String in) {
		return in.substring(in.lastIndexOf("\\") + 1).substring(in.lastIndexOf("/") + 1);
	}
	
}
