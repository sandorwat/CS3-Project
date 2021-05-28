import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

@SuppressWarnings("serial")
class Game extends JPanel implements ActionListener {

	private boolean inGame, justLost, muted;
	private boolean information;
	private Music music;
	private JLabel j;
	private JButton info, mute;
	private Stage stage;
	private javax.swing.Timer timer;
	private ScoreNode bst;
	private ScoreNode temp1, temp2, temp3;
	private int bstSize;
	private int hiScore;
	private LayoutManager original;
	private Ship ship;
	private Image title;
	private Image pressStart;
	private int ifw = JComponent.WHEN_IN_FOCUSED_WINDOW;

	public Game() {

		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(765, 800));
		setFocusable(true);
		original = getLayout();
		setLayout(null);
		inGame = false;
		justLost = false;
		information = false;
		muted = false;

		title = new ImageIcon("src/galactica.png").getImage();
		pressStart = new ImageIcon("src/pressStart.png").getImage();

		ship = new Ship();
		add(ship.getMover());

		try {
			music = new Music("src/monodyTitleScreen.wav");
		} catch (Exception e) {
			e.printStackTrace();
		}
		add(music);
		music.play();

		info = new JButton(new ImageIcon("src/info.png"));
		info.setBounds(620, 680, 50, 50);
		info.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				information = !information;
			}
		});
		info.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
		add(info);
		mute = new JButton(new ImageIcon("src/unmuted.png"));
		mute.setBounds(680, 680, 50, 50);
		mute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (muted) {
					music.play();
					muted = false;
					mute.setIcon(new ImageIcon("src/unmuted.png"));
				} else {
					music.pause();
					muted = true;
					mute.setIcon(new ImageIcon("src/mute.png"));
				}
			}
		});
		mute.getInputMap().put(KeyStroke.getKeyStroke("SPACE"), "none");
		add(mute);

		BufferedReader br;
		temp1 = temp2 = temp3 = null;
		try {
			br = new BufferedReader(new FileReader(new File("src/scores.txt")));
			String line = "";
			while ((line = br.readLine()) != null)
				addScoreNode(line);
			while (bstSize < 10)
				addScoreNode("AAA " + 0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (bst == null)
			hiScore = 0;
		else {
			ScoreNode temp = bst;
			while (temp.getRightChild() != null)
				temp = temp.getRightChild();
			hiScore = temp.getScore();
		}

		j = new JLabel();
		j.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "space");
		j.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_M, 0, false), "mute");
		j.getActionMap().put("space", new Play());
		j.getActionMap().put("mute", new Mute());
		j.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, false), "up");
		j.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0, true), "upRelease");
		j.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, false), "down");
		j.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0, true), "downRelease");
		j.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "left");
		j.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "leftRelease");
		j.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "right");
		j.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "rightRelease");
		j.getActionMap().put("up", ship.new Move(1, false));
		j.getActionMap().put("upRelease", ship.new Move(1, true));
		j.getActionMap().put("down", ship.new Move(2, false));
		j.getActionMap().put("downRelease", ship.new Move(2, true));
		j.getActionMap().put("left", ship.new Move(3, false));
		j.getActionMap().put("leftRelease", ship.new Move(3, true));
		j.getActionMap().put("right", ship.new Move(4, false));
		j.getActionMap().put("rightRelease", ship.new Move(4, true));
		add(j);

		timer = new javax.swing.Timer(10, this);
		timer.start();
	}

	public void addScoreNode(String line) {
		String[] s = line.split("[ ]");
		if (s.length < 2)
			return;
		if (bst == null)
			bst = new ScoreNode(s[0], Integer.parseInt(s[1]), 1, null, null, null);
		else {
			temp1 = new ScoreNode(s[0], Integer.parseInt(s[1]), 1, null, null, null);
			temp2 = bst;
			temp3 = bst;
			while (temp2 != null) {
				temp3 = temp2;
				if (temp1.compareTo(temp2) > 0)
					temp2 = temp2.getRightChild();
				else {
					temp1.setRank(temp2.getRank());
					temp2 = temp2.getLeftChild();
				}
			}
			if (temp1.compareTo(temp3) > 0)
				temp3.setRightChild(temp1);
			else {
				temp3.setLeftChild(temp1);
				temp1.setRank(temp3.getRank());
			}
			temp1.setParent(temp3);
		}
		bstSize++;
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		if (!information && !inGame) {

			g2d.drawImage(title, 120, 100, this);
			g2d.drawImage(pressStart, 160, 450, this);
			g2d.drawImage(ship.getImage(), ship.getX(), ship.getY(), this);

			Font font = new Font("Helvetica", Font.BOLD, 20);
			g2d.setColor(Color.WHITE);
			g2d.setFont(font);
			g2d.drawString("SCORE", 100, 25);
			g2d.drawString("HI-SCORE", 420, 25);
			g2d.drawString(String.format("%07d", 0), 200, 25);
			g2d.drawString(String.format("%07d", hiScore), 550, 25);

		} else if (!inGame) {
			Font font = new Font("Helvetica", Font.BOLD, 30);
			FontMetrics fm = getFontMetrics(font);
			g2d.setColor(Color.WHITE);
			g2d.setFont(font);
			g2d.drawString("HOW TO PLAY", (750 - fm.stringWidth("HOW TO PLAY")) / 2, 65);

			g2d.drawString("CREDITS", (750 - fm.stringWidth("CREDITS")) / 2, 350);

			font = new Font("Helvetica", Font.BOLD, 20);
			g2d.setFont(font);
			g2d.drawString("↑", 174, 110);
			g2d.drawString("← ↓ →", 150, 130);
			g2d.drawString("Use the arrow keys to move.", 250, 120);

			g2d.drawImage(new ImageIcon("src/spacebar.png").getImage(), 155, 162, this);
			g2d.drawString("Press the space bar to shoot.", 250, 180);

			g2d.drawImage(new ImageIcon("src/enemy1.png").getImage(), 157, 215, this);
			g2d.drawString("Kill the enemies to gain points!", 250, 240);

			g2d.drawString("Designer", 200, 400);
			g2d.drawString("Sandor Wat", 440, 400);
			g2d.drawString("Programmer", 200, 450);
			g2d.drawString("Sandor Wat", 440, 450);
			g2d.drawString("Artists", 200, 500);
			g2d.drawString("Sandor Wat", 440, 500);
			g2d.drawString("Lucas Wat", 440, 535);
			g2d.drawString("Music", 200, 585);
			g2d.drawString("Monody - TheFatRat", 440, 585);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (stage != null && stage.gameOver()) {
			muted = stage.isMuted();
			remove(stage);
			stage = null;
			inGame = false;
			justLost = true;
			setLayout(null);
			info.setVisible(true);
			mute.setVisible(true);
		} else if (ship != null) {
			ship.move();
		}
		if (!inGame)
			repaint();
	}

	private class Play extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {

			if (!justLost && !inGame) {
				inGame = true;
				try {
					music.pause();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				info.setVisible(false);
				mute.setVisible(false);
				information = false;
				remove(ship.getMover());
				setLayout(original);
				stage = new Stage(muted, bst);
				add(stage);
			}
			if (justLost) {
				if (!muted)
					music.play();
				justLost = false;
				add(ship.getMover());
			}
		}
	}

	private class Mute extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {

			if (muted) {
				music.play();
				muted = false;
			} else {
				music.pause();
				muted = true;
			}
		}
	}
}