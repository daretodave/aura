package com.aura.impl;

import java.awt.Canvas;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.aura.AWindow;
import com.aura.Aura;
import com.aura.Brush;
import com.aura.Context;
import com.aura.cache.Asset;
import com.aura.cache.Cache;
import com.aura.input.MouseEvent.Mouse;
import com.aura.model.Bounds;
import com.aura.model.Color;
import com.aura.model.Element;
import com.aura.model.Location;
import com.aura.model.Texture;

public class G2DWindow extends AWindow implements MouseMotionListener,
		MouseListener, KeyListener {

	private JFrame window;
	private boolean built;
	private BufferStrategy strategy;
	private Canvas canvas;
	private boolean control;

	public static class G2DImageRenderer implements Texture {

		private BufferedImage image;

		public G2DImageRenderer(BufferedImage image) {
			this.image = image;
		}

		@Override
		public float width() {
			return image.getWidth();
		}

		@Override
		public float height() {
			return image.getHeight();
		}

		@Override
		public void render(Object g, float x, float y, float w, float h) {
			Graphics2D graphics = (Graphics2D) g;
			graphics.drawImage(image, (int) x, (int) y, (int) w, (int) h, null);
		}

	}

	public static Object getClipboardContents() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable contents = clipboard.getContents(null);
		if (contents == null)
			return null;
		DataFlavor[] flavors = contents.getTransferDataFlavors();
		if (flavors.length == 0) {
			return 0;
		}
		DataFlavor resolve = DataFlavor.selectBestTextFlavor(flavors);
		if(resolve.getMimeType() != null && resolve.getMimeType().startsWith("text")) {
			resolve = DataFlavor.stringFlavor;
		} else if (contents.isDataFlavorSupported(DataFlavor.imageFlavor)) {
			resolve = DataFlavor.imageFlavor;
		}
		Object o;
		try {
			o = contents.getTransferData(resolve);
		} catch (Exception e) {
			return null;
		}
		if (o instanceof BufferedImage) {
			Texture texture = new G2DImageRenderer((BufferedImage) o);
			return texture;
		}
		return o;
	}

	public static class SimpleTransfer implements Transferable {

		private Object object;
		private DataFlavor[] supported;

		public SimpleTransfer(Object object, DataFlavor... flavors) {
			this.object = object;
			this.supported = flavors;
		}
		@Override
		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
			if (isDataFlavorSupported(flavor))
				return object;
			throw new UnsupportedFlavorException(flavor);
		}
		@Override
		public DataFlavor[] getTransferDataFlavors() {
			return supported;
		}
		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			for(DataFlavor support : supported) {
				if(support.equals(flavor)) {
					return true;
				}
			}
			return false;
		}
	}

	public static void setClipboardContents(Object o) {
		Transferable resolve = null;
		if (o instanceof Texture) {
			o = ((G2DImageRenderer) o).image;
			resolve = new SimpleTransfer(resolve, DataFlavor.imageFlavor);
		} else if (o instanceof String) {
			resolve = new StringSelection(o.toString());
		} else {
			throw new RuntimeException("Unsure of how to handle object: " + o + " for clipboard.");
		}
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(resolve, null);
	}

	static {
		try {
			UIManager
					.setLookAndFeel(javax.swing.plaf.synth.SynthLookAndFeel.class
							.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			Graphics2D g = null;
			try {
				g = (Graphics2D) strategy.getDrawGraphics();
				brush.build(g);
				try {
					render(g);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(-1);
				}
			} finally {
				g.dispose();
			}
			strategy.show();
		}
	}

	@Override
	public void open() {
		if (window == null || window.isVisible()) {
			return;
		}
		window.setVisible(true);
		if (!built) {
			canvas.setIgnoreRepaint(true);
			canvas.createBufferStrategy(2);
			strategy = canvas.getBufferStrategy();
			Aura.submit(this);
			built = true;
		}
	}

	@Override
	public void close() {
		window.dispose();
	}

	@Override
	public float getWidthOnScreen() {
		return canvas.getWidth();
	}

	@Override
	public float getHeightOnScreen() {
		return canvas.getHeight();
	}

	@Override
	public void build(Context context) {
		window = new JFrame();
		canvas = new Canvas();
		window.add(canvas);
		window.setResizable(context.is(RESIZABLE, true));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas.addMouseListener(this);
		canvas.addMouseMotionListener(this);
		canvas.addKeyListener(this);
		window.setSize(context.get(WIDTH, Integer.class, 500),
				context.get(HEIGHT, Integer.class, 500));
		window.setTitle(context.get(TITLE, String.class, ""));
		window.setUndecorated(context.get(DECOR, Integer.class, Decor.FRAMED) != Decor.FRAMED);
		window.setLocationRelativeTo(null);
		setIcon(context.asset(ICON));
		setBackground(context.asset(BACKGROUND));
		clear = context.get(CLEAR_COLOR, Integer.class, Color.WHITE);
	}

	@Override
	public void display() {
	}

	@Override
	public Texture getImage(Asset asset) {
		return grabImage(asset);
	}

	@Override
	public Object getFont(Asset asset) {
		return grabFont(asset);
	}

	public static Texture grabImage(Asset asset) {
		if (asset == null) {
			return null;
		}
		return new G2DImageRenderer(Cache.asset(asset, BufferedImage.class));
	}

	public static Object grabFont(Asset asset) {
		if (asset == null) {
			return null;
		}
		return Cache.asset(asset, java.awt.Font.class);
	}

	@Override
	public void setIcon(Texture image) {
		if (window != null && image instanceof G2DImageRenderer) {
			window.setIconImage(((G2DImageRenderer) image).image);
		}
	}

	@Override
	public float getXOnScreen() {
		return 0;
	}

	@Override
	public float getYOnScreen() {
		return 0;
	}

	@Override
	public String style() {
		return "window";
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
			control = true;
		}
		keyboard(Type.PRESS, e.getKeyCode(), e.isControlDown(), e.isShiftDown());
	}

	@Override
	public void keyReleased(KeyEvent e) {
		keyboard(Type.RELEASE, e.getKeyCode(), control,
				e.isShiftDown());
		if(e.getKeyCode() == KeyEvent.VK_CONTROL) {
			control = true;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mouse(Type.PRESS, e.getButton(), e.getX(), e.getY(), e.isControlDown(),
				e.isShiftDown());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouse(Type.RELEASE, e.getButton(), e.getX(), e.getY(),
				e.isControlDown(), e.isShiftDown());
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouse(Type.DRAG, e.getButton(), e.getX(), e.getY(), e.isControlDown(),
				e.isShiftDown());
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouse(Type.MOVE, e.getButton(), e.getX(), e.getY(), e.isControlDown(),
				e.isShiftDown());
	}

	@Override
	public boolean contains(float x, float y) {
		return true;
	}

	@Override
	public boolean mouse(com.aura.input.MouseEvent event) {
		return true;
	}

	@Override
	public void setPointer(int type) {
		switch (type) {
		case Mouse.NORMAL:
			window.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			break;
		case Mouse.CROSSHAIRS:
			window.setCursor(Cursor
					.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			break;
		case Mouse.HAND:
			window.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			break;
		case Mouse.TYPE:
			window.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
			break;
		}
	}

	@Override
	public void state(int state, boolean inState) {
	}

	@Override
	public int getCursor(com.aura.input.MouseEvent mouseEvent) {
		return com.aura.input.MouseEvent.Mouse.NORMAL;
	}

	@Override
	public void onChange(Brush brush, String style, Object before, Object after) {
	}

	@Override
	public void overlay() {
	}

	@Override
	public float width() {
		return canvas.getWidth();
	}

	@Override
	public float height() {
		return canvas.getHeight();
	}

	@Override
	public float x() {
		return 0;
	}

	@Override
	public float y() {
		return 0;
	}

	@Override
	public void onFPSUpdate(int fps) {
		window.setTitle("FPS:" + fps);
	}

	@Override
	public void setClearColor(int color) {
		clear = color;
	}

	@Override
	public void bound(Bounds bounds) {
	}

	@Override
	public void locate(Location location) {
	}



	@Override
	public void logic() {
	}

	@Override
	public Bounds getBounds() {
		return null;
	}

	@Override
	public boolean draggedIn(Element what, float x, float y) {
		return false;
	}

	@Override
	public float getWindowX() {
		return window.getX();
	}

	@Override
	public float getWindowY() {
		return window.getY();
	}

	@Override
	public void setLocation(float x, float y) {
		window.setLocation((int) x, (int) y);
	}

	@Override
	public void resize(float width, float height) {
		window.setSize((int)width, (int)height);
	}

	@Override
	public float getWindowWidth() {
		return window.getWidth();
	}

	@Override
	public float getWindowHeight() {
		return window.getHeight();
	}

}
