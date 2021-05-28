import java.awt.*;
import javax.swing.*;

@SuppressWarnings("serial")
class EnemyMissile extends JPanel {
	private int x, y, w, h;
	private boolean visible;
	private Image image;

	public EnemyMissile(int x, int y) {

		this.x = x;
		this.y = y;
		visible = true;

		ImageIcon ii = new ImageIcon("src/missile.png");
		image = ii.getImage();

		w = image.getWidth(null);
		h = image.getHeight(null);
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

	public Rectangle getBounds() {
		return new Rectangle(x, y, w, h);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean v) {
		visible = v;
	}

	public void move() {

		y += 5;

		if (y > 720) {
			visible = false;
		}
	}
}