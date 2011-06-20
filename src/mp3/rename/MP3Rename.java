package mp3.rename;

import java.awt.EventQueue;

public class MP3Rename {

	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				MainPanel.Graphics();
			}
		});

	}

}
