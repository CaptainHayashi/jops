package jops;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.Duration;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Gui implements Listener {
    private Model model;

    private JLabel state = new JLabel("EJECTED");
    private JLabel name = new JLabel("<No connection>");
    private JLabel duration = new JLabel("--");
    private JLabel position = new JLabel("--");
    private JFrame frame;

    private JLabel songAlbum = new JLabel("--");
    private JLabel songArtist = new JLabel("--");
    private JLabel songTitle = new JLabel("--");

    public Gui(Model inModel) {
	this.model = inModel;

	this.model.registerListener(this);
    }

    public void start() {
	System.out.println("Showing GUI");
	javax.swing.SwingUtilities.invokeLater(() -> createAndShowGui());
    }

    private void createAndShowGui() {
	this.frame = new JFrame();

	JPanel buttonPanel = new JPanel(new FlowLayout());
	JButton playButton = new JButton("Play");
	JButton stopButton = new JButton("Stop");
	JButton ejctButton = new JButton("Eject");
	JButton loadButton = new JButton("Load");

	buttonPanel.add(playButton);
	playButton.addActionListener((e) -> {
	    this.model.play();
	});

	buttonPanel.add(stopButton);
	stopButton.addActionListener((e) -> {
	    this.model.stop();
	});

	buttonPanel.add(ejctButton);
	ejctButton.addActionListener((e) -> {
	    this.model.eject();
	});

	buttonPanel.add(loadButton);
	loadButton.addActionListener((e) -> {
	    loadFile();
	});
	
	JPanel positionPanel = new JPanel();
	positionPanel.setLayout(new BoxLayout(positionPanel, BoxLayout.PAGE_AXIS));
	positionPanel.add(this.duration);
	positionPanel.add(this.position);
	
	JPanel metadataPanel = new JPanel();
	metadataPanel.setLayout(new BoxLayout(metadataPanel, BoxLayout.PAGE_AXIS));
	metadataPanel.add(this.songTitle);
	metadataPanel.add(this.songArtist);
	metadataPanel.add(this.songAlbum);

	this.frame.getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
	this.frame.getContentPane().add(this.name, BorderLayout.PAGE_START);
	this.frame.getContentPane().add(this.state, BorderLayout.LINE_START);
	this.frame.getContentPane().add(metadataPanel, BorderLayout.CENTER);
	this.frame.getContentPane().add(positionPanel, BorderLayout.LINE_END);

	this.frame.pack();
	this.frame.setVisible(true);

	System.out.println("GUI shown");
    }

    private void loadFile() {
	JFileChooser fc = new JFileChooser();

	int returnVal = fc.showOpenDialog(this.frame);

	if (returnVal == JFileChooser.APPROVE_OPTION) {
	    this.model.load(fc.getSelectedFile());
	}
    }

    @Override
    public void setName(String inName) {
	this.name.setText(inName);
    }

    @Override
    public void setState(State to) {
	this.state.setText(to.toString());
	
	if (to == State.EJECTED) {
	    clearMetadata();
	}
    }

    @Override
    public void setTime(long micros) {
	this.position.setText(formatTime(micros));
    }
    
    private static String formatTime(long micros) {
	Duration dur = Duration.ofNanos(micros * 1000);

	long hours = dur.toHours();
	long mins = dur.minusHours(hours).toMinutes();
	long secs = dur.minusHours(hours).minusMinutes(mins).getSeconds();
	return String.format("%d:%02d:%02d",
		Long.valueOf(hours), Long.valueOf(mins), Long.valueOf(secs));
    }

    @Override
    public void setTotalDuration(long micros) {
	this.duration.setText(formatTime(micros));
    }

    @Override
    public void loadFailed() {
	clearMetadata();
	setSongAlbum("Load failed");
    }
    
    private void clearMetadata() {
	setSongAlbum("--");
	setSongArtist("--");
	setSongTitle("--");
	
	this.duration.setText("--");
	this.position.setText("--");
    }

    @Override
    public void setSongAlbum(String album) {
	this.songAlbum.setText(album);
    }
    
    @Override
    public void setSongArtist(String artist) {
	this.songArtist.setText(artist);
    }
    
    @Override
    public void setSongTitle(String title) {
	this.songTitle.setText(title);
    }
}
