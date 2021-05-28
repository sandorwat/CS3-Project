import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

@SuppressWarnings("serial")
class Ship extends JPanel {
	private int x, y, w, h, dx, dy;
	private boolean up, down, left, right;
	private boolean leftPriority, upPriority;
	private boolean inStage;
	private Image image;
	private java.util.List<Missile> missiles;
	private JLabel j;
	private static final int ifw = JComponent.WHEN_IN_FOCUSED_WINDOW;

	public Ship() {

		x = 300;
		y = 650;
		dx = 0;
		dy = 0;
		inStage = true;

		ImageIcon ii = new ImageIcon("src/ship.png");
		image = ii.getImage();

		w = image.getWidth(null);
		h = image.getHeight(null);

		missiles = new ArrayList<Missile>();

		j = new JLabel();
		j.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "up");
		j.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "upRelease");
		j.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "down");
		j.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "downRelease");
		j.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "left");
		j.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "leftRelease");
		j.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "right");
		j.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "rightRelease");
		j.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "space");
		j.getActionMap().put("up", new Move(1, false));
		j.getActionMap().put("upRelease", new Move(1, true));
		j.getActionMap().put("down", new Move(2, false));
		j.getActionMap().put("downRelease", new Move(2, true));
		j.getActionMap().put("left", new Move(3, false));
		j.getActionMap().put("leftRelease", new Move(3, true));
		j.getActionMap().put("right", new Move(4, false));
		j.getActionMap().put("rightRelease", new Move(4, true));
		j.getActionMap().put("space", new Fire());
	}

	public void move() {
		if ((x > 0 || dx >= 0) && (x < 744 - w || dx <= 0))
			x += dx;
		if ((y > 0 || dy >= 0) && (y < 720 - h || dy <= 0))
			y += dy;
	}

	public void setInStage(boolean b) {
		inStage = b;
	}

	public int getX() {

		return x;
	}

	public int getY() {

		return y;
	}

	public int getWidth() {

		return w;
	}

	public int getHeight() {

		return h;
	}

	public Image getImage() {

		return image;
	}

	public JLabel getMover() {
		return j;
	}

	public java.util.List<Missile> getMissiles() {
		return missiles;
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, w, h);
	}

	public class Move extends AbstractAction {
		private int direction;
		private boolean release;

		public Move(int d, boolean r) {
			direction = d;
			release = r;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!release) {
				switch (direction) {
				case (1):
					up = true;
					upPriority = true;
					break;
				case (2):
					down = true;
					upPriority = false;
					break;
				case (3):
					left = true;
					leftPriority = true;
					break;
				case (4):
					right = true;
					leftPriority = false;
					break;
				}
			} else {
				switch (direction) {
				case (1):
					up = false;
					upPriority = false;
					break;
				case (2):
					down = false;
					break;
				case (3):
					left = false;
					leftPriority = false;
					break;
				case (4):
					right = false;
					break;
				}
			}
			if (up == down) {
				if (upPriority)
					dy = -3;
				else if (down)
					dy = 3;
				else
					dy = 0;
			} else if (up)
				dy = -3;
			else if (down)
				dy = 3;
			if (left == right) {
				if (leftPriority)
					dx = -3;
				else if (right)
					dx = 3;
				else
					dx = 0;
			} else if (left)
				dx = -3;
			else if (right)
				dx = 3;
		}
	}

	private class Fire extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {

			if (inStage)
				missiles.add(new Missile(x + w / 2 - 1, y));
		}
	}
}