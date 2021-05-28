import java.io.*;
import javax.sound.sampled.*;
import javax.swing.JLabel;

@SuppressWarnings("serial")
class Music extends JLabel {

	Long currentFrame;
	Clip clip;
	AudioInputStream audioInputStream;
	static String filePath;

	public Music(String path) throws UnsupportedAudioFileException, IOException, LineUnavailableException {

		try {
			filePath = path;
		} catch (Exception ex) {
			System.out.println("Error with playing sound.");
			ex.printStackTrace();
		}
		audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
		clip = AudioSystem.getClip();
		clip.open(audioInputStream);
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}

	public void play() {

		clip.start();
	}

	public void pause() {
		this.currentFrame = this.clip.getMicrosecondPosition();
		clip.stop();
	}

	public void resume() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		clip.close();
		resetAudioStream();
		clip.setMicrosecondPosition(currentFrame);
		this.play();
	}

	public void restart() throws IOException, LineUnavailableException, UnsupportedAudioFileException {
		clip.stop();
		clip.close();
		resetAudioStream();
		currentFrame = 0L;
		clip.setMicrosecondPosition(0);
		this.play();
	}

	public void stop() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		currentFrame = 0L;
		clip.stop();
		clip.close();
	}

	public void resetAudioStream() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
		clip.open(audioInputStream);
		clip.loop(Clip.LOOP_CONTINUOUSLY);
	}

}