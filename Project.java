import java.awt.EventQueue;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class Project extends JFrame {

	public Project() {

		add(new Game());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(765, 800);
		setLocationRelativeTo(null);
		setTitle("CompSci 3 Project");
		setResizable(false);
		setVisible(true);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		EventQueue.invokeLater(() -> {
			Project p = new Project();
			p.setVisible(true);
		});
	}

}