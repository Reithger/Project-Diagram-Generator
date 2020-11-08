package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Ellipse2D;

import visual.frame.WindowFrame;
import visual.panel.CanvasPanel;

public class LoadingAnimation {
	
	private WindowFrame fra;
	
	public LoadingAnimation() {
		run();
	}
	
	public int getWidth() {
		return fra.getWidth() + fra.BULLSHIT_OFFSET_X;
	}
	
	public int getHeight() {
		return fra.getHeight() + fra.BULLSHIT_OFFSET_Y;
	}

	public void setLocation(int x, int y) {
		fra.getFrame().setLocation(x, y);
	}
	
	public void dispose() {
		fra.disposeFrame();
	}
	
	public void run() {
		preset1(32, 32, 150, 1, 5, 10, 15);
		//preset3(32, 32, 210, 1, 10, 10, 10);
		//preset2(500, 500, 150, 1, 5, 10, 15);
	}
	
	private void preset1(int wid, int hei, int maxPeriod, int zoomScale, int rChange, int gChange, int bChange) {
		int x = wid / 2 / zoomScale;
		int y = hei / 2 / zoomScale;
		int maxSize = wid / 2 / zoomScale;
		int minSize = wid / 4 / zoomScale;
		CanvasPanel ca = new CanvasPanel(0, 0, wid, hei, zoomScale) {

			@Override
			public void initialize() {
				super.initialize();
				for(int i = x - maxSize; i < x + maxSize; i++) {
					for(int j = y - maxSize; j < y + maxSize; j++) {
						double dist = Math.sqrt(Math.pow(i - x, 2) + Math.pow(j - y, 2));
						if(dist >= maxSize) {
							setPixelColor(i, j, new Color(0, 0, 0, 0));
						}
					}
				}
			}

			int counter = 0;
			@Override
			public void paintComponent(Graphics g2) {
				super.paintComponent(g2);
				double prop = Math.sin((double)(counter) / maxPeriod * (2 * Math.PI));
				int distMax = (int)(maxSize * prop);
				int distMin = (int)(minSize * prop);
				for(int i = x - distMax; i < x + distMax; i++) {
					for(int j = y - distMax; j < y + distMax; j++) {
						double dist = Math.sqrt(Math.pow(i - x, 2) + Math.pow(j - y, 2));
						if(dist <= distMax && dist >= distMin) {
							Color c = getPixelColor(i, j);
							int r = c.getRed() + (int)(rChange * prop);
							int g = c.getGreen() + (int)(gChange * prop);
							int b = c.getBlue() + (int)(bChange / prop);
							setPixelColor(i, j, new Color(r % 255, g % 255, b % 255));
						}
					}
				}
				counter = (counter + 1) % maxPeriod;
			}
		
			boolean dragged = false;
			int lastX;
			int lastY;
			
			@Override
			public void clickPressEvent(int code, int x2, int y2) {
				lastX = x2;
				lastY = y2;
				dragged = true;
			}
			
			@Override
			public void clickReleaseEvent(int code, int x2, int y2) {
				dragged = false;
			}
			
			@Override
			public void dragEvent(int code, int x2, int y2) {
				if(dragged) {
					Point p = getParentFrame().getFrame().getLocationOnScreen();
					getParentFrame().getFrame().setLocation((int)(p.getX() + (x2 - lastX)), (int)(p.getY() + (y2 - lastY)));
				}
			}
			
		};
		fra = new WindowFrame(wid, hei);
		//fra.getFrame().
		fra.getFrame().setVisible(false);
		fra.getFrame().dispose();
		fra.getFrame().setUndecorated(true);
		fra.getFrame().setShape(new Ellipse2D.Double(0, 0, wid, hei));
		fra.getFrame().setSize(wid, hei);
		//fra.getFrame().setOpacity(.25f);
		fra.getFrame().setVisible(true);
		
		fra.setName("Ada's Algorithm Art");
		fra.addPanel("ca", ca);
	}
}
	