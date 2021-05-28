import java.awt.*;
import javax.swing.*;

@SuppressWarnings("serial")
class Enemy extends JPanel {
	private int x, y, w, h;
	private int livesLeft;
	private boolean visible;
	private boolean shotMissile;
	private int velocity;
	private int scoreValue;
	private Image image;
	private Enemy left, right;
	private EnemyMissile em;

	public Enemy(int x, int y, int lives, String imageLoc) {
		this.x = x;
		this.y = y;
		visible = true;
		velocity = 1;
		livesLeft = lives;
		scoreValue = lives * 20;
		shotMissile = false;

		ImageIcon ii = new ImageIcon(imageLoc);
		image = ii.getImage();

		em = null;

		w = image.getWidth(null);
		h = image.getHeight(null);
	}

	public Enemy(int x, int y, int lives, int points, String imageLoc) {
		this(x, y, lives, imageLoc);
		scoreValue = points;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
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

	public int getLives() {
		return livesLeft;
	}

	public int getVelocity() {
		return velocity;
	}

	public int getScoreValue() {
		return scoreValue;
	}

	public Image getImage() {
		return image;
	}

	public EnemyMissile getEnemyMissile() {
		return em;
	}

	public boolean shotMissile() {
		return shotMissile;
	}

	public void setDirec(String s) {
		if (s.equals("left")) {
			velocity = Math.abs(velocity) * -1;
			if (left != null)
				left.setDirec("left");
		} else if (s.equals("right")) {
			velocity = Math.abs(velocity);
			if (right != null)
				right.setDirec("right");
		}
	}

	public void setLeft(Enemy e) {
		left = e;
	}

	public void setRight(Enemy e) {
		right = e;
	}

	public Enemy getLeft() {
		return left;
	}

	public Enemy getRight() {
		return right;
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, w, h);
	}

	public void loseLife() {
		livesLeft--;
		switch (livesLeft) {
		case (0):
			setVisible(false);
			break;
		case (1):
			ImageIcon ii = new ImageIcon("src/enemy1.png");
			image = ii.getImage();
			break;
		case (2):
			ii = new ImageIcon("src/enemy2.png");
			image = ii.getImage();
		}

		if (livesLeft == 0)
			setVisible(false);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean v) {
		visible = v;
	}

	public void move() {
		x += velocity;
		if (em != null) {
			em.move();
			if (!em.isVisible()) {
				shotMissile = false;
				em = null;
			}
		}
		if (!shotMissile) {
			if (Math.random() * 350 <= 1) {
				em = new EnemyMissile(x + w / 2, y + h);
				shotMissile = true;
			}
		}
	}

	public void goDownRow(boolean direc) {
		// true = toward right, false = toward left
		y += 20;
		if (direc && right != null)
			right.goDownRow(true);
		else if (!direc && left != null)
			left.goDownRow(false);
	}

	public void setSpeed(int m) {
		if (velocity > 0)
			velocity = m;
		else
			velocity = m * -1;
		if (right != null)
			right.setSpeed(m);
	}

	public String toString() {
		return "Enemy: (" + x + ", " + y + ") -> " + right;
	}
}

@SuppressWarnings("serial")
class Enemy1 extends Enemy {

	public Enemy1(int x, int y) {
		super(x, y, 1, "src/enemy1.png");
	}
}

@SuppressWarnings("serial")
class Enemy2 extends Enemy {

	public Enemy2(int x, int y) {
		super(x, y, 2, "src/enemy2.png");
	}
}

@SuppressWarnings("serial")
class Enemy3 extends Enemy {

	public Enemy3(int x, int y) {
		super(x, y, 3, "src/enemy3.png");
	}
}

@SuppressWarnings("serial")
class Boss extends Enemy {
	private int health;
	private int velocity;
	private int count;

	public Boss(int x, int y) {
		super(x, y, 50, "src/boss.png");
		health = 50;
		velocity = 1;
		count = 0;
	}

	public int getLives() {
		return health;
	}

	public void setDirec(String s) {
		if (s.equals("left"))
			velocity = Math.abs(velocity) * -1;
		else if (s.equals("right"))
			velocity = Math.abs(velocity);
	}

	public void loseLife() {
		health--;
		if (health == 0)
			setVisible(false);
	}

	public void move() {
		if (getX() < 0)
			velocity = Math.abs(velocity);
		if (getX() > 300)
			velocity = Math.abs(velocity) * -1;
		if (count == 0)
			setX(getX() + velocity);
		count++;
		count = count % 7;
	}

	public int getHealth() {
		return health;
	}
}

@SuppressWarnings("serial")
class Minion extends Enemy {
	private Minion left, right;

	public Minion(int x, int y) {
		super(x, y, 1, 10, "src/minion.png");
	}

	public void move() {
		setY(getY() + 2);
	}

	public void setLeft(Minion m) {
		left = m;
	}

	public void setRight(Minion m) {
		right = m;
	}

	public Minion getLeft() {
		return left;
	}

	public Minion getRight() {
		return right;
	}
}