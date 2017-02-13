import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class GameFrame extends Frame implements KeyListener, Runnable {

	private int speed = 40;
	private Thread thread;

	private Graphics2D bg;
	private BufferedImage buffer;

	private int display = 0;

	private final double stepSize = 2d;
	private final int turnSize = 3;

	private boolean forward = false, backward = false, left = false, right = false;
	private double px = 0d, py = 0d, pz = 0d;
	private int degree = 45;

	private ArrayList<AudibleObject> objects = new ArrayList<AudibleObject>();
	private Sample ambient, walking, turning;

	public GameFrame() {
		super();
		thread = new Thread(this);

		buffer = new BufferedImage(420, 440, BufferedImage.TYPE_INT_RGB);
		bg = buffer.createGraphics();
		bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		bg.setStroke(new BasicStroke(1.5f));
	}

	public void start() {

        ambient = new Sample("ambient.wav");
        ambient.loadAudio();
        ambient.setVolume(-20.0f);
        ambient.loop();

		walking = new Sample("walking.wav");
		walking.loadAudio();
		walking.setVolume(-25.0f);

		turning = new Sample("turning.wav");
		turning.loadAudio();
		turning.setVolume(-20.0f);

		objects.add(new AudibleObject("fire.wav", Color.blue, 400 * Math.random(), 400 * Math.random(), 0d));
        objects.add(new AudibleObject("beep.wav", Color.green, 400 * Math.random(), 400 * Math.random(), 0d));
		for (int i = 0; i < objects.size(); i++) {
			objects.get(i).getSample().loadAudio();
			objects.get(i).getSample().loop();
		}

		addKeyListener(this);
		thread.start();
	}

	public void paint(Graphics g) {
		bg.clearRect(0, 0, 420, 440);

		if (display == 0) {
			bg.setColor(Color.WHITE);
			bg.fillRect(0, 0, 420, 440);

			bg.setColor(Color.BLACK);
			bg.drawString("CONTROLS", 150, 115);
			bg.drawString("Turn Left: A", 150, 150);
			bg.drawString("Turn Right: D", 150, 170);
			bg.drawString("Forward: W", 150, 190);
			bg.drawString("Backward: S", 150, 210);
			bg.drawString("CLICK SPACEBAR", 140, 260);
			bg.drawString("TO ENABLE VISUALS", 140, 280);
		} else if (display == 1) {

			bg.setColor(Color.WHITE);
			bg.fillRect(0, 0, 420, 440);

			/* Display Walls */
			if (px < 10 && degree > 135 && degree < 225) {
				int diff = (int) (100 - 10 * px);
				bg.setColor(Color.gray);
				bg.drawRect(10, 220 - diff, 400, 2 * diff);
			} else if (px > 390 && (degree < 45 || degree > 315)) {
				int diff = (int) (100 - 10 * (400 - px));
				bg.setColor(Color.gray);
				bg.drawRect(10, 220 - diff, 400, 2 * diff);
			} else if (py < 10 && degree > 225 && degree < 315) {
				int diff = (int) (100 - 10 * py);
				bg.setColor(Color.gray);
				bg.drawRect(10, 220 - diff, 400, 2 * diff);
			} else if (py > 390 && degree < 135 && degree > 45) {
				int diff = (int) (100 - 10 * (400 - py));
				bg.setColor(Color.gray);
				bg.drawRect(10, 220 - diff, 400, 2 * diff);
			}

			for (int i = 0; i < objects.size(); i++) {

				double x = objects.get(i).getX(),
						y = objects.get(i).getY(),
						z = objects.get(i).getZ();

				// Translate Angle
				double angle = Math.toDegrees(Math.atan2((x - px), (y - py)));
				angle = -(angle - 90d);
				if (angle < 0d) angle += 360d;

				// Angle of Sight
				double dAngle = Math.abs(degree - angle);
				if (dAngle >= 180d) dAngle = 360d - dAngle;

				double opDegree = (180d + degree) % 360d;
				if ((degree >= angle && angle >= opDegree) || (degree <= opDegree && (angle <= degree || angle >= opDegree))) {
					dAngle = -dAngle;
				}

				// Distance
				double distance = Math.pow(x - px, 2) + Math.pow(y - py, 2) + Math.pow(z - pz, 2);
				distance = Math.pow(distance, 0.5d);

				if (distance < 1d) distance = 1d;

				double horizontal = 200 + 200 * dAngle / 60;
				double size = Math.max(500 - 700 * distance / 400, 1);

				//System.out.println(horizontal + " " + size);

				bg.setColor(objects.get(i).getColor());
				bg.drawOval((int) horizontal - (int) (size / 2), 220 - (int) (size / 2), (int) size, (int) size);
			}
		} else if (display == 2) {
			bg.setColor(Color.WHITE);
			bg.fillRect(0, 0, 420, 440);

			bg.setColor(Color.red);
			bg.drawOval((int) px, (int) py + 20, 20, 20);
			bg.drawLine((int) px + 10, (int) py + 30, (int) (px + 10 + 10 * Math.cos(Math.toRadians(degree))), (int) (py + 30 + 10 * Math.sin(Math.toRadians(degree))));

			for (int i = 0; i < objects.size(); i++) {
				bg.setColor(objects.get(i).getColor());
				bg.drawOval((int) objects.get(i).getX(), (int) objects.get(i).getY() + 20, 20, 20);
			}
		}

		g.drawImage(buffer, 0, 0, this);
	}

	public void update(Graphics p) {
		paint(p);
	}

	@Override
	public void keyPressed(KeyEvent e) {
//        System.out.println("key pressed: " + e.getKeyChar());
		if (e.getKeyChar() == 'w' && !forward) {
			forward = true;
			walking.loop();
		} else if (e.getKeyChar() == 's' && !backward) {
			backward = true;
			walking.loop();
		} else if (e.getKeyChar() == 'a' && !left) {
			left = true;
			turning.loop();
		} else if (e.getKeyChar() == 'd' && !right) {
			right = true;
			turning.loop();
		} else if (e.getKeyCode() == KeyEvent.VK_SPACE) display = (display + 1) % 3;
	}

	@Override
	public void keyReleased(KeyEvent e) {
//        System.out.println("key released: " + e.getKeyChar());
		if (e.getKeyChar() == 'w') {
			forward = false;
			walking.stop();
		} else if (e.getKeyChar() == 's') {
			backward = false;
			walking.stop();
		} else if (e.getKeyChar() == 'a') {
			left = false;
			turning.stop();
		} else if (e.getKeyChar() == 'd') {
			right = false;
			turning.stop();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	public void moveUser() {
		if (forward) {
			px += stepSize * Math.cos(Math.toRadians(degree));
			py += stepSize * Math.sin(Math.toRadians(degree));
		} else if (backward) {
			px -= stepSize * Math.cos(Math.toRadians(degree));
			py -= stepSize * Math.sin(Math.toRadians(degree));
		}
		if (right) {
			degree = (degree + turnSize) % 360;
		} else if (left) {
			degree = (degree - turnSize) % 360;
			if (degree < 0) degree += 360;
		}

		if (px < 0) px = 0d;
		else if (px > 400) px = 400d;
		if (py < 0) py = 0d;
		else if (py > 400) py = 400d;
	}

	public void adjustAudio() {
		for (int i = 0; i < objects.size(); i++) {
			objects.get(i).adjust(px, py, pz, (double) degree);
		}
	}

	public void moveObjects() {
		double safetyThreshold = 40.0;
		AudibleObject goodAO = objects.get(1);
		AudibleObject evilAO = objects.get(0);

		double dx = Math.abs(px - goodAO.getX());
		double dy = Math.abs(py - goodAO.getY());
		double distance = Math.sqrt(Math.pow(dx, 2.0) + Math.pow(dy, 2.0));

//        System.out.println("distance: " + distance);

		double interval = distance < safetyThreshold ? -0.2 : 0.2;

		if (evilAO.getX() < px) {
			evilAO.setX(evilAO.getX() + interval);
		} else {
			evilAO.setX(evilAO.getX() - interval);
		}

		if (evilAO.getY() < py) {
			evilAO.setY(evilAO.getY() + interval);
		} else {
			evilAO.setY(evilAO.getY() - interval);
		}
	}


	public void run() {
		while (true) {

//			System.out.println("forward: " + forward + "  backward: " + backward + "  left: " + left + "  right: " + right);

			// move
			moveUser();

            moveObjects();

			// graphics
			repaint();

			// sounds
			adjustAudio();


			try {
				Thread.sleep(speed);
			} catch (InterruptedException e) {
				System.out.println("Sleep Failed.");
			}
		}
	}
}
