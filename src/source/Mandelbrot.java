package source;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Scanner;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import vectors.Point2D;

public class Mandelbrot extends JPanel
		implements Runnable, KeyListener, MouseMotionListener, MouseListener, MouseWheelListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9020477412984598531L;
	JFrame frame;
	BufferedImage screen;
	BufferedImage comblayers;
	BufferedImage fscreen;
	Graphics2D graphics;
	Graphics2D copier;
	Graphics2D layerer;
	int screenwidth;
	int screenheight;
	int screenarea;
	int fps = 0;
	int threadnum = 0;
	int[] wait = new int[1000];
	int timemilis;
	boolean[] ispressed = new boolean[500];
	boolean[] topress = new boolean[500];
	static BufferedImage[] images;
	Point2D Mouse = Point2D.Origin();
	Point2D MousePos = Point2D.Origin();
	Point2D PrevMousePos = Point2D.Origin();
	boolean[] isheld = new boolean[4];
	int[] set1;
	int[] set2;
	public int steps = 8196;
	Point2D viewstart = new Point2D(-2, -2);
	Point2D viewend = new Point2D(2, 2);

	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Mandelbrot a = new Mandelbrot(1440,1440);
	}

	public Mandelbrot(int width, int height) {
		loadimages();
		screenwidth = width;
		screenheight = height;
		screenarea = screenwidth * screenheight;
		set1 = new int[screenarea];
		set2 = new int[screenarea];
		frame = new JFrame();
		frame.setDefaultCloseOperation(3);
		frame.setResizable(false);
		frame.add(this);
		this.setPreferredSize(new Dimension(720, 720));
		addKeyListener(this);
		addMouseMotionListener(this);
		addMouseListener(this);
		addMouseWheelListener(this);
		frame.addKeyListener(this);
		frame.addMouseMotionListener(this);
		frame.addMouseListener(this);
		frame.addMouseWheelListener(this);
		setSize(screenwidth, screenheight);
		frame.pack();
		frame.setLocationRelativeTo(null);
		screen = new BufferedImage(screenwidth, screenheight, BufferedImage.TYPE_INT_ARGB);
		fscreen = new BufferedImage(720, 720, BufferedImage.TYPE_INT_ARGB);
		comblayers = new BufferedImage(screenwidth, screenheight, BufferedImage.TYPE_INT_ARGB);
		graphics = screen.createGraphics();
		copier = fscreen.createGraphics();
		layerer = comblayers.createGraphics();
		new Thread(this).start();
		new Thread(this).start();
		new Thread(this).start();
		frame.setVisible(true);
	}

	private void loadimages() {
		URL txt = getClass().getClassLoader().getResource("images/meta.txt");
		int imagecount = 0;
		try {
			Scanner in = new Scanner(txt.openStream());
			imagecount = Integer.parseInt(in.nextLine());
			in.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		images = new BufferedImage[imagecount];
		for (int i = 0; i < imagecount; i++) {
			URL image = getClass().getClassLoader().getResource("images/" + i + ".png");
			try {
				images[i] = ImageIO.read(image);
				System.out.println("images/" + i + ".png");
			} catch (IOException e) {
			}
		}

	}

	public synchronized void paint(Graphics g) {
		g.drawImage(fscreen, 0, 0, null);
	}

	protected void graphicsupdate() throws ConcurrentModificationException {
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, screenwidth, screenheight);
		maingraphics();
		copier.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		copier.drawImage(screen, 0, 0,720,720, null);

	}

	protected void maingraphics() {
		for (int i = 0; i < screenarea; i++) {
			int x = i % screenwidth;
			int y = i / screenwidth;
			if (set1[i] < steps) {
				graphics.setColor(Color.getHSBColor(set1[i] / 16.0f, 1, 1));
			} else {
				graphics.setColor(Color.black);
			}
			graphics.drawRect(x, y, 0, 0);

		}
	}

	protected void contentupdate() {
		for (int i = 0; i < screenarea; i++) {
			double x = (double) (i % screenwidth) / screenwidth;
			double y = (double) (i / screenwidth) / screenheight;
			double re = (viewend.x - viewstart.x) * x + viewstart.x;
			double im = (viewend.y - viewstart.y) * y + viewstart.y;
			ComplexNumber c = new ComplexNumber(re,im);
			ComplexNumber z = new ComplexNumber(re,im);
			int a = 0;
			for (; a < steps; a++) {
			z=ComplexNumber.Add(ComplexNumber.Multiply(z, z), c);
				if (z.Abs()>2) {
					break;
				}
			}
			set1[i] = a;
		}
		Arrays.fill(topress, false);
		
	}

	public void run() {
		threadnum++;
		if (threadnum == 1) {
			while (true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
				try {
					graphicsupdate();
					repaint();
				} catch (ConcurrentModificationException e) {
				}

			}
		}
		if (threadnum == 2) {
			while (true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
				contentupdate();
				for (int i = 0; i < 1000; i++) {
					wait[i]++;
				}
			}
		}
		if (threadnum == 3) {
			while (true) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
				fps = wait[1];
				for (int i = 1; i < 1000; i++) {
					wait[i - 1] = wait[i];
				}
				wait[999] = 0;
				timemilis++;
			}

		}
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
		ispressed[e.getKeyCode()] = true;
		topress[e.getKeyCode()] = true;
	}

	public void keyReleased(KeyEvent e) {
		ispressed[e.getKeyCode()] = false;
	}

	public void mouseDragged(MouseEvent e) {
		viewstart = Point2D.add(viewstart, Point2D.add(MousePos.scale(-1), PrevMousePos));
		viewend = Point2D.add(viewend, Point2D.add(MousePos.scale(-1), PrevMousePos));
		mouseMoved(e);
	}

	public void mouseMoved(MouseEvent e) {
		PrevMousePos = MousePos;
		MousePos = Point2D.add(viewstart, new Point2D((Mouse.x / screenwidth) * (viewend.x - viewstart.x),
				(Mouse.y / screenheight) * (viewend.x - viewstart.x)));
		Mouse = new Point2D(e.getX(), e.getY());
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
		isheld[e.getButton()] = true;
	}

	public void mouseReleased(MouseEvent e) {
		isheld[e.getButton()] = false;
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		double scale = Math.pow(Math.E, e.getWheelRotation()/8.0);
		viewstart = Point2D.add(MousePos, Point2D.add(viewstart, MousePos.scale(-1)).scale(scale));
		viewend = Point2D.add(MousePos, Point2D.add(viewend, MousePos.scale(-1)).scale(scale));
	}
}