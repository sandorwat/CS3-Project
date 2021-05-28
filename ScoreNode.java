
class ScoreNode implements Comparable<ScoreNode> {
	private String name;
	private int score;
	private int rank;
	private ScoreNode parent;
	private ScoreNode leftChild;
	private ScoreNode rightChild;

	public ScoreNode(String s, int i, int r, ScoreNode parent, ScoreNode left, ScoreNode right) {
		name = s;
		score = i;
		rank = r;
		this.parent = parent;
		leftChild = left;
		rightChild = right;
	}

	public void setName(String s) {
		name = s;
	}

	public void setRank(int i) {
		rank += i;
	}

	public String getName() {
		return name;
	}

	public int getRank() {
		return rank;
	}

	public int getScore() {
		return score;
	}

	public void setParent(ScoreNode node) {
		parent = node;
	}

	public void setLeftChild(ScoreNode node) {
		leftChild = node;
	}

	public void setRightChild(ScoreNode node) {
		rightChild = node;
	}

	public ScoreNode getParent() {
		return parent;
	}

	public ScoreNode getLeftChild() {
		return leftChild;
	}

	public ScoreNode getRightChild() {
		return rightChild;
	}

	@Override
	public int compareTo(ScoreNode other) {
		if (score == other.getScore())
			return name.compareTo(other.getName());
		else
			return score - other.getScore();
	}

	public String toString() {
		return "Name: " + name + "\nScore: " + score + "\nRank: " + rank;
	}
}