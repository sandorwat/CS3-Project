import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.TimerTask;

import javax.swing.*;

@SuppressWarnings({ "serial" })
class Stage extends JLabel implements ActionListener {
	private javax.swing.Timer timer;
	private Music music;
	private Ship ship;
	private int stageNum, stageType, lifeCount, enemyCount, origEnemyCount, moveNum, score, scoreNum, hiScore, rank;
	private boolean inStage, beforeFirst, needDraw, inGame, gameOver, muted, justDied, justDied2, lifeTimer, bossTime,
			bossDefeated, minionTimer, invasion, finishedInvasion, showRanking, enterScore, scoreAdded, inPlay,
			spaceNotAdded;
	private String name;
	private JLabel j, n, mover;
	private ScoreNode scoreNode, bst;
	private Enemy[] left, right;
	private Boss boss;
	private Minion minions;
	private final int ifw = JComponent.WHEN_IN_FOCUSED_WINDOW;
	private final int[][][] pos = {
			{ { 25, 50 }, { 95, 50 }, { 165, 50 }, { 295, 50 }, { 385, 50 }, { 515, 50 }, { 585, 50 }, { 655, 50 } },
			{ { 25, 150 }, { 95, 150 }, { 165, 150 }, { 295, 150 }, { 385, 150 }, { 515, 150 }, { 585, 150 },
					{ 655, 150 } },
			{ { 25, 250 }, { 95, 250 }, { 165, 250 }, { 295, 250 }, { 385, 250 }, { 515, 250 }, { 585, 250 },
					{ 655, 250 } },
			{ { 25, 350 }, { 95, 350 }, { 165, 350 }, { 295, 350 }, { 385, 350 }, { 515, 350 }, { 585, 350 },
					{ 655, 350 } } };

	public Stage(boolean muted, ScoreNode bst) {

		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(765, 800));
		setFocusable(true);

		inGame = true;
		gameOver = false;
		this.muted = muted;
		this.bst = bst;
		justDied = false;
		justDied2 = false;

		try {
			music = new Music("src/monodyGame.wav");
			if (muted)
				music.pause();
		} catch (Exception e) {
			e.printStackTrace();
		}

		j = new JLabel();
		j.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_M, 0, false), "mute");
		j.getActionMap().put("mute", new Mute());
		add(j);

		n = new JLabel();
		for (int c = 65; c <= 90; c++)
			n.getInputMap(ifw).put(KeyStroke.getKeyStroke(c, 0, false), "" + (char) c);
		for (int c = 97; c <= 122; c++)
			n.getInputMap(ifw).put(KeyStroke.getKeyStroke(c, 0, false), "" + (char) (c - 32));
		n.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0, false), "backspace");
		n.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "enter");
		for (int c = 65; c <= 90; c++)
			n.getActionMap().put("" + (char) c, new Key(c));
		n.getActionMap().put("backspace", new Key(8));
		n.getActionMap().put("enter", new Key(10));

		ship = new Ship();
		mover = ship.getMover();
		add(mover);

		name = "";
		stageNum = 0;
		stageType = 0;
		lifeCount = 3;
		left = new Enemy[4];
		right = new Enemy[4];
		minions = null;
		enemyCount = 0;
		origEnemyCount = 0;
		bossTime = false;
		bossDefeated = false;
		minionTimer = false;
		moveNum = 0;
		score = 0;
		scoreNode = null;
		rank = 0;
		inStage = false;
		inPlay = true;
		beforeFirst = true;
		needDraw = true;
		invasion = false;
		finishedInvasion = false;
		showRanking = false;
		enterScore = false;
		spaceNotAdded = true;

		if (bst == null)
			hiScore = 0;
		else {
			ScoreNode temp = bst;
			while (temp.getRightChild() != null)
				temp = temp.getRightChild();
			hiScore = temp.getScore();
		}

		timer = new javax.swing.Timer(10, this);
		timer.start();
	}

	public boolean gameOver() {
		return gameOver;
	}

	public boolean isMuted() {
		return muted;
	}

	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;

		Font font = new Font("Helvetica", Font.BOLD, 20);
		g2d.setColor(Color.WHITE);
		g2d.setFont(font);
		g2d.drawString("SCORE", 100, 25);
		g2d.drawString("HI-SCORE", 420, 25);
		g2d.drawString(String.format("%07d", score), 200, 25);
		g2d.drawString(String.format("%07d", hiScore), 550, 25);

		if (needDraw && stageNum != 0) {
			String msg = "STAGE " + stageNum;
			Font stage = new Font("Helvetica", Font.BOLD, 50);
			FontMetrics stageFM = getFontMetrics(stage);

			g2d.setColor(Color.WHITE);
			g2d.setFont(stage);
			g2d.drawString(msg, (750 - stageFM.stringWidth(msg)) / 2, 300);
		}

		if (enterScore) {
			enterScore(g2d);
			return;
		}

		if (showRanking) {
			showRanking(g2d);
			return;
		}

		if (!inStage && !beforeFirst) {
			if (ship != null && inGame && ship.isVisible())
				g2d.drawImage(ship.getImage(), ship.getX(), ship.getY(), this);
			return;
		}

		if (invasion)
			drawInvasion(g2d);

		if (ship != null && inGame && !beforeFirst) {
			for (int k = 0; k < lifeCount; k++)
				g2d.drawImage(ship.getImage(), k * 40 + 30, 680, this);
			if (ship.isVisible())
				g2d.drawImage(ship.getImage(), ship.getX(), ship.getY(), this);

			java.util.List<Missile> missiles = ship.getMissiles();
			for (Missile missile : missiles)
				if (missile.isVisible())
					g2d.drawImage(missile.getImage(), missile.getX(), missile.getY(), this);

			if (bossTime) {
				if (boss != null && boss.isVisible()) {
					g2d.drawImage(boss.getImage(), boss.getX(), boss.getY(), this);
					g2d.drawRect(50, 50, 630, 20);
					g2d.fillRect(50, 50, boss.getHealth() * 630 / 50, 20);
				}
				if (minions != null) {
					Minion temp = minions;
					while (temp != null) {
						if (temp.isVisible())
							g2d.drawImage(temp.getImage(), temp.getX(), temp.getY(), this);
						temp = temp.getRight();
					}
				}
			} else if (!bossTime) {
				for (Enemy enemy : left) {
					Enemy temp = enemy;
					while (temp != null) {
						if (temp.isVisible())
							g2d.drawImage(temp.getImage(), temp.getX(), temp.getY(), this);
						if (temp.shotMissile()) {
							g2d.drawImage(temp.getEnemyMissile().getImage(), temp.getEnemyMissile().getX(),
									temp.getEnemyMissile().getY(), this);
						}
						temp = temp.getRight();
					}
				}
			}

			if (justDied) {
				justDied = false;
				justDied2 = true;
			} else if (justDied2) {
				justDied2 = false;
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (justDied) {
			if (lifeCount != 0) {
				ship = new Ship();
				ship.setInStage(true);
				add(ship.getMover());
				if (!lifeTimer) {
					lifeTimer = true;
					lifeCount--;
					java.util.Timer timer = new java.util.Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							lifeTimer = false;
						}
					}, 500);
				}
				inGame = true;
			} else {
				if (spaceNotAdded) {
					j.getInputMap(ifw).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "space");
					j.getActionMap().put("space", new Space());
					spaceNotAdded = false;
				}
				drawGameOver(g2d);
			}
		} else if (invasion) {

		} else if (finishedInvasion) {
			drawGameOver(g2d);
		} else if (stageType != 5) {
			inGame = true;
			inStage = false;
			if (ship != null)
				ship.setInStage(false);
			ship.getMissiles().clear();
			nextStage(g2d);
		} else if (!bossDefeated) {
			bossTime = true;
			inGame = true;
			boss = new Boss(200, 75);
			minions = new Minion(pos[0][0][0], pos[0][0][1]);
			Minion temp = minions;
			for (int k = 1; k < 8; k++) {
				Minion temp2 = temp;
				temp = new Minion(pos[0][k][0], pos[0][k][1]);
				temp2.setRight(temp);
				temp.setLeft(temp2);
			}
		} else {
			inGame = true;
			inStage = false;
			if (ship != null)
				ship.setInStage(false);
			bossDefeated = false;
			ship.getMissiles().clear();
			nextStage(g2d);
		}
		Toolkit.getDefaultToolkit().sync();
	}

	public void drawGameOver(Graphics2D g2d) {

		inPlay = false;
		music.pause();
		String msg = "Game Over";
		Font small = new Font("Helvetica", Font.BOLD, 50);
		FontMetrics fm = getFontMetrics(small);

		g2d.setColor(Color.WHITE);
		g2d.setFont(small);
		g2d.drawString(msg, (750 - fm.stringWidth(msg)) / 2, 300);
		;
		try {
			Thread.sleep(1000);
		} catch (Exception e) {

		}
		remove(mover);
		if (!scoreAdded) {
			scoreNode = new ScoreNode("", score, 1, null, null, null);
			ScoreNode temp1 = bst;
			ScoreNode temp2 = bst;
			if (bst == null) {
				bst = scoreNode;
			} else {
				while (temp1 != null) {
					temp2 = temp1;
					if (scoreNode.compareTo(temp1) > 0)
						temp1 = temp1.getRightChild();
					else {
						scoreNode.setRank(temp1.getRank());
						temp1 = temp1.getLeftChild();
					}
				}
				if (scoreNode.compareTo(temp2) > 0)
					temp2.setRightChild(scoreNode);
				else {
					temp2.setLeftChild(scoreNode);
					scoreNode.setRank(temp2.getRank());
				}
				scoreNode.setParent(temp2);
			}
			scoreAdded = true;
			rank(bst, score);
		}
	}

	public void drawInvasion(Graphics2D g2d) {
		String msg = "Invasion!";
		Font small = new Font("Helvetica", Font.BOLD, 50);
		FontMetrics fm = getFontMetrics(small);

		g2d.setColor(Color.WHITE);
		g2d.setFont(small);
		g2d.drawString(msg, (750 - fm.stringWidth(msg)) / 2, 300);
	}

	public void enterScore(Graphics2D g2d) {
		String msg = "HIGH SCORES";
		Font small = new Font("Helvetica", Font.BOLD, 50);
		FontMetrics fm = getFontMetrics(small);

		g2d.setColor(Color.WHITE);
		g2d.setFont(small);
		g2d.drawString(msg, (750 - fm.stringWidth(msg)) / 2, 100);

		scoreNum = 0;
		showScores(g2d, bst);
		g2d.drawString("" + rank, 50, 670);
		g2d.drawString(name, 250, 670);
		g2d.drawString("" + score, 500, 670);
	}

	public void showRanking(Graphics2D g2d) {
		String msg = "HIGH SCORES";
		Font small = new Font("Helvetica", Font.BOLD, 50);
		FontMetrics fm = getFontMetrics(small);

		g2d.setColor(Color.WHITE);
		g2d.setFont(small);
		g2d.drawString(msg, (750 - fm.stringWidth(msg)) / 2, 100);

		scoreNum = 0;
		showScores(g2d, bst);
		g2d.drawString("" + rank, 50, 670);
		g2d.drawString(name, 250, 670);
		g2d.drawString("" + score, 500, 670);
	}

	public void showScores(Graphics2D g2d, ScoreNode s) {
		if (s != null) {
			showScores(g2d, s.getRightChild());
			if (scoreNum == 10)
				return;
			Font font = new Font("Helvetica", Font.BOLD, 30);
			g2d.setColor(Color.WHITE);
			g2d.setFont(font);
			g2d.drawString("" + (scoreNum + 1), 50, scoreNum * 50 + 150);
			g2d.drawString(s.getName(), 250, scoreNum * 50 + 150);
			g2d.drawString("" + s.getScore(), 500, scoreNum * 50 + 150);
			scoreNum++;
			if (scoreNum != 10)
				showScores(g2d, s.getLeftChild());
		}
	}

	public void rank(ScoreNode s, int score) {
		if (s != null && s.getScore() < score) {
			rank(s.getRightChild(), score);
			return;
		}
		if (s != null) {
			rank(s.getRightChild(), score);
			rank(s.getLeftChild(), score);
			rank++;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (ship != null && ship.isVisible())
			ship.move();
		if (inStage && ship != null && !invasion && !finishedInvasion) {

			updateMissiles();
			updateEnemies();

			checkCollisions();
		}

		revalidate();
		repaint();
	}

	private void updateMissiles() {

		java.util.List<Missile> missiles = ship.getMissiles();

		for (int i = 0; i < missiles.size(); i++) {
			Missile missile = missiles.get(i);
			if (missile.isVisible())
				missile.move();
			else
				missiles.remove(i);
		}
	}

	private void updateEnemies() {

		if (bossTime && boss != null) {
			if (!boss.isVisible() && minions == null) {
				inGame = false;
				bossTime = false;
				bossDefeated = true;
				score += boss.getScoreValue();
				return;
			}
			if (boss.isVisible()) {
				boss.move();
				if (!minionTimer) {
					java.util.Timer timer = new java.util.Timer();
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							minions = new Minion(pos[0][0][0], pos[0][0][1]);
							Minion temp = minions;
							for (int k = 1; k < 8; k++) {
								Minion temp2 = temp;
								temp = new Minion(pos[0][k][0], pos[0][k][1]);
								temp2.setRight(temp);
								temp.setLeft(temp2);
							}
							minionTimer = false;
						}
					}, 7000);
					minionTimer = true;
				}
			}
			while (minions != null && !minions.isVisible()) {
				score += minions.getScoreValue();
				minions = minions.getRight();
			}
			Minion temp = minions;
			while (temp != null) {
				if (temp.isVisible())
					temp.move();
				else {
					score += temp.getScoreValue();
					if (temp.getLeft() != null)
						temp.getLeft().setRight(temp.getRight());
					if (temp.getRight() != null)
						temp.getRight().setLeft(temp.getLeft());
				}
				temp = temp.getRight();
			}
			if (minions != null && minions.getY() > 680)
				minions = null;
			return;
		}

		if (enemyCount == 0) {
			if (stageType == 5 && !bossDefeated) {
				bossTime = true;
			}
			inGame = false;
			return;
		}

		if (boss != null && !boss.isVisible() && minions == null)
			return;

		boolean move = moveNum == 3;
		if (move)
			moveNum = 0;
		moveNum++;

		for (int row = 0; row < 4; row++) {

			while (left[row] != null && !left[row].isVisible()) {
				score += left[row].getScoreValue();
				left[row] = left[row].getRight();
				if (left[row] != null)
					left[row].setLeft(null);
				enemyCount--;
			}

			Enemy temp = left[row];
			while (temp != null) {
				if (temp.isVisible()) {
					if (move)
						temp.move();
					if (temp.getY() >= 720) {
						invasion = true;
						java.util.Timer timer = new java.util.Timer();
						timer.schedule(new TimerTask() {
							@Override
							public void run() {
								inGame = false;
								invasion = false;
								finishedInvasion = true;
							}
						}, 1000);
					}
					right[row] = temp;
				} else {
					score += temp.getScoreValue();
					if (temp.getLeft() != null)
						temp.getLeft().setRight(temp.getRight());
					if (temp.getRight() != null)
						temp.getRight().setLeft(temp.getLeft());
					enemyCount--;
				}
				temp = temp.getRight();
			}
		}

		int ratio = 1;
		if (enemyCount != 0)
			ratio = origEnemyCount / enemyCount;
		if (left[0] != null)
			left[0].setSpeed(ratio);
		if (left[1] != null)
			left[1].setSpeed(ratio);
		if (left[2] != null)
			left[2].setSpeed(ratio);
		if (left[3] != null)
			left[3].setSpeed(ratio);

		if (enemyCount == origEnemyCount / 3) {
			if (left[0] != null)
				left[0].setSpeed(3);
			if (left[1] != null)
				left[1].setSpeed(3);
			if (left[2] != null)
				left[2].setSpeed(3);
			if (left[3] != null)
				left[3].setSpeed(3);
		}

		for (int row = 0; row < 4; row++) {

			if (left[row] != null && left[row].getX() < 2 && left[row].getVelocity() < 0) {

				if (left[0] != null) {
					left[0].setDirec("right");
					left[0].goDownRow(true);
				}
				if (left[1] != null) {
					left[1].setDirec("right");
					left[1].goDownRow(true);
				}
				if (left[2] != null) {
					left[2].setDirec("right");
					left[2].goDownRow(true);
				}
				if (left[3] != null) {
					left[3].setDirec("right");
					left[3].goDownRow(true);
				}
				break;
			} else if (right[row] != null && right[row].getX() > 680 && right[row].getVelocity() > 0) {

				if (right[0] != null) {
					right[0].setDirec("left");
					right[0].goDownRow(false);
				}
				if (right[1] != null) {
					right[1].setDirec("left");
					right[1].goDownRow(false);
				}
				if (right[2] != null) {
					right[2].setDirec("left");
					right[2].goDownRow(false);
				}
				if (right[3] != null) {
					right[3].setDirec("left");
					right[3].goDownRow(false);
				}
				break;
			}
		}
	}

	private void checkCollisions() {

		Rectangle r3 = ship.getBounds();
		if (bossTime && boss != null) {
			Rectangle r2 = boss.getBounds();
			if (r3.intersects(r2)) {
				ship = null;
				inGame = false;
				justDied = true;
			}
			if (minions != null) {
				Minion temp = minions;
				while (temp != null) {
					r2 = temp.getBounds();
					if (r3.intersects(r2)) {
						ship = null;
						temp.loseLife();
						inGame = false;
						justDied = true;
					}
					temp = temp.getRight();
				}
			}
		} else {
			for (Enemy l : left) {
				Enemy temp = l;
				while (temp != null) {
					Rectangle r2 = temp.getBounds();
					if (r3.intersects(r2)) {
						ship = null;
						temp.setVisible(false);
						inGame = false;
						justDied = true;
					}
					if (temp.shotMissile()) {
						Rectangle r4 = temp.getEnemyMissile().getBounds();
						if (r3.intersects(r4)) {
							ship = null;
							temp.getEnemyMissile().setVisible(false);
							inGame = false;
							justDied = true;
						}
					}
					temp = temp.getRight();
				}
			}
		}

		if (!justDied) {
			java.util.List<Missile> missiles = ship.getMissiles();
			for (Missile m : missiles) {
				Rectangle r1 = m.getBounds();

				if (bossTime) {
					if (boss != null) {
						Rectangle r2 = boss.getBounds();
						if (r1.intersects(r2)) {
							m.setVisible(false);
							boss.loseLife();
						}
					}
					if (minions != null) {
						Minion temp = minions;
						while (temp != null) {
							Rectangle r2 = temp.getBounds();
							if (r1.intersects(r2)) {
								m.setVisible(false);
								temp.loseLife();
							}
							temp = temp.getRight();
						}
					}
				} else {
					for (Enemy enemy : left) {
						Enemy temp = enemy;
						while (temp != null) {
							Rectangle r2 = temp.getBounds();
							if (r1.intersects(r2)) {
								m.setVisible(false);
								temp.loseLife();
							}
							if (temp.shotMissile()) {
								Rectangle r4 = temp.getEnemyMissile().getBounds();
								if (r1.intersects(r4)) {
									m.setVisible(false);
									temp.getEnemyMissile().setVisible(false);
								}
							}
							temp = temp.getRight();
						}
					}
				}
			}
		}
	}

	private void nextStage(Graphics2D g2d) {

		beforeFirst = false;
		needDraw = true;
		java.util.Timer timer = new java.util.Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				needDraw = false;
				inStage = true;
				if (ship != null)
					ship.setInStage(true);
			}
		}, 2000);

		stageNum++;
		stageType = (stageNum - 1) % 5 + 1;

		for (int row = 0; row < 4; row++) {
			if (stageType == 1)
				left[row] = new Enemy1(pos[row][0][0], pos[row][0][1]);
			else if (stageType == 2) {
				if (row % 2 == 0)
					left[row] = new Enemy1(pos[row][0][0], pos[row][0][1]);
				else
					left[row] = new Enemy2(pos[row][0][0], pos[row][0][1]);
			} else if (stageType == 3)
				left[row] = new Enemy2(pos[row][0][0], pos[row][0][1]);
			else if (stageType == 4) {
				if (row % 3 == 0)
					left[row] = new Enemy1(pos[row][0][0], pos[row][0][1]);
				else if (row % 3 == 1)
					left[row] = new Enemy2(pos[row][0][0], pos[row][0][1]);
				else
					left[row] = new Enemy3(pos[row][0][0], pos[row][0][1]);
			} else if (stageType == 5) {
				if (row % 2 == 0)
					left[row] = new Enemy2(pos[row][0][0], pos[row][0][1]);
				else
					left[row] = new Enemy3(pos[row][0][0], pos[row][0][1]);
			}
			enemyCount++;
			Enemy temp = left[row];
			for (int col = 1; col < 8; col++) {
				Enemy temp2 = null;
				if (stageType == 1)
					temp2 = new Enemy1(pos[row][col][0], pos[row][col][1]);
				else if (stageType == 2) {
					if ((row + col) % 2 == 0)
						temp2 = new Enemy1(pos[row][col][0], pos[row][col][1]);
					else
						temp2 = new Enemy2(pos[row][col][0], pos[row][col][1]);
				} else if (stageType == 3)
					temp2 = new Enemy2(pos[row][col][0], pos[row][col][1]);
				else if (stageType == 4) {
					if ((row + col) % 3 == 0)
						temp2 = new Enemy1(pos[row][col][0], pos[row][col][1]);
					else if ((row + col) % 3 == 1)
						temp2 = new Enemy2(pos[row][col][0], pos[row][col][1]);
					else if ((row + col) % 3 == 2)
						temp2 = new Enemy3(pos[row][col][0], pos[row][col][1]);
				} else if (stageType == 5) {
					if ((row + col) % 2 == 0)
						temp2 = new Enemy2(pos[row][col][0], pos[row][col][1]);
					else
						temp2 = new Enemy3(pos[row][col][0], pos[row][col][1]);
				}
				enemyCount++;
				temp.setRight(temp2);
				temp2.setLeft(temp);
				temp = temp2;
			}
			right[row] = temp;
		}
		origEnemyCount = enemyCount;
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

	private class Space extends AbstractAction {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (!inPlay && !showRanking) {
				remove(j);
				add(n);
				enterScore = true;
			}
			if (showRanking) {
				gameOver = true;
			}
		}
	}

	private class Key extends AbstractAction {
		private int key;

		public Key(int c) {
			key = c;
		}

		@Override
		public void actionPerformed(ActionEvent e) {

			if (key == 8) {
				if (!name.isEmpty())
					name = name.substring(0, name.length() - 1);
			} else if (key == 10 && !name.isEmpty()) {
				showRanking = true;
				enterScore = false;
				scoreNode.setName(name);
				remove(n);
				add(j);
				PrintWriter pw = null;
				try {
					pw = new PrintWriter(new FileWriter(new File("src/scores.txt"), true));
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				pw.println(scoreNode.getName() + " " + scoreNode.getScore());
				pw.close();
			} else if (name.length() != 3)
				name += (char) key;
		}
	}
}