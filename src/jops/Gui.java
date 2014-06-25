package jops;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.UIManager;


public class Gui implements Listener {
    private static final String FA_LOAD = "\uf15b";

    private static final String FA_EJECT = "\uf052";

    private static final String FA_STOP = "\uf04d";

    private static final String FA_PLAY = "\uf04b";

    private Model model;

    private JLabel state = new JLabel("EJECTED");
    private JLabel name = new JLabel("<No connection>");
    private JLabel duration = new JLabel("--");
    private JLabel position = new JLabel("--");
    private JFrame frame;
    private JSlider seeker = new JSlider();
    private JLabel songAlbum = new JLabel("--");
    private JLabel songArtist = new JLabel("--");
    private JLabel songTitle = new JLabel("--");
    private JButton playButton = new JButton("Play");
    private JButton stopButton = new JButton("Stop");
    private JButton ejctButton = new JButton("Eject");
    private JButton loadButton = new JButton("Load");
    
    private Font fontAwesome = null;

    public Gui(Model inModel) {
	this.model = inModel;

	this.model.registerListener(this);
    }

    public void start() {
	System.out.println("Showing GUI");
	javax.swing.SwingUtilities.invokeLater(() -> createAndShowGui());
    }

    private void createAndShowGui() {
	setLookAndFeel();
	try {
	    InputStream is = this.getClass().getResourceAsStream("FontAwesome.ttf");
	    this.fontAwesome = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, 24);
	} catch (FontFormatException | IOException e) {
	}
	
	this.frame = new JFrame();

	JPanel controlPanel = initControlPanel();
	JPanel positionPanel = initPositionPanel();
	JPanel metadataPanel = initMetadataPanel();

	this.frame.getContentPane().add(controlPanel, BorderLayout.PAGE_END);
	this.frame.getContentPane().add(this.name, BorderLayout.PAGE_START);
	this.frame.getContentPane().add(this.state, BorderLayout.LINE_START);
	this.frame.getContentPane().add(metadataPanel, BorderLayout.CENTER);
	this.frame.getContentPane().add(positionPanel, BorderLayout.LINE_END);

	// Set up player for the initial ejected state.
	handleEject();
	
	this.frame.pack();
	this.frame.setVisible(true);
    }

    private JPanel initControlPanel() {
	JPanel controlPanel = new JPanel();
	controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.PAGE_AXIS));
	
	controlPanel.add(this.seeker);
	initSeeker();
	
	JPanel buttonPanel = initButtonPanel();
	controlPanel.add(buttonPanel);
	
	return controlPanel;
    }

    private JPanel initButtonPanel() {
	JPanel buttonPanel = new JPanel(new FlowLayout());

	buttonPanel.add(this.playButton);
	buttonPanel.add(this.stopButton);
	buttonPanel.add(this.ejctButton);
	buttonPanel.add(this.loadButton);

	initButtons();
	
	return buttonPanel;
    }

    private JPanel initPositionPanel() {
	JPanel positionPanel = new JPanel();
	positionPanel.setLayout(new BoxLayout(positionPanel, BoxLayout.PAGE_AXIS));
	positionPanel.add(this.duration);
	positionPanel.add(this.position);
	return positionPanel;
    }

    private JPanel initMetadataPanel() {
	JPanel metadataPanel = new JPanel();
	
	metadataPanel.setLayout(new BoxLayout(metadataPanel, BoxLayout.PAGE_AXIS));
	metadataPanel.setBorder(BorderFactory.createLoweredBevelBorder());
	
	metadataPanel.add(this.songTitle);
	metadataPanel.add(this.songArtist);
	metadataPanel.add(this.songAlbum);
	
	return metadataPanel;
    }

    private void initButtons() {
	this.playButton.addActionListener((e) -> {
	    this.model.play();
	});
	this.stopButton.addActionListener((e) -> {
	    this.model.stop();
	});
	this.ejctButton.addActionListener((e) -> {
	    this.model.eject();
	});
	this.loadButton.addActionListener((e) -> {
	    loadFile();
	});
	
	setFontAwesomeIcon(this.playButton, FA_PLAY);
	setFontAwesomeIcon(this.stopButton, FA_STOP);
	setFontAwesomeIcon(this.ejctButton, FA_EJECT);
	setFontAwesomeIcon(this.loadButton, FA_LOAD);
    }
    
    private void setFontAwesomeIcon(JButton component, String icon) {
	if (this.fontAwesome != null) {
	    component.setFont(this.fontAwesome);
	    component.setText(icon);
	}
    }
    private void setFontAwesomeIcon(JLabel component, String icon) {
	if (this.fontAwesome != null) {
	    component.setFont(this.fontAwesome);
	    component.setText(icon);
	}
    }

    private void initSeeker() {
	this.seeker.setEnabled(false);
	this.seeker.addChangeListener((e) -> {
	    if (!this.seeker.getValueIsAdjusting()) {
		// This change may have happened as part of a position update
		// from the player, in which case the new duration will be the
		// same as that in the model.  Ignore this case.
		if (this.seeker.getValue() != this.model.time()) {
		    this.model.seek(this.seeker.getValue());
		}
	    }
	});
    }

    private static void setLookAndFeel() {
	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception e) {
	    // Use default look and feel
	}
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
	
	switch (to) {
	case EJECTED:
	    handleEject();
	    break;
	case PLAYING:
	    handlePlay();
	    break;
	case STOPPED:
	    handleStop();
	    break;
	default:
	    break;
	}
    }

    private void handleEject() {
	clearMetadata();
	this.seeker.setEnabled(false);
	this.stopButton.setEnabled(false);
	this.playButton.setEnabled(false);
	this.ejctButton.setEnabled(false);
	
	setFontAwesomeIcon(this.state, FA_EJECT);
	this.state.setForeground(Color.BLUE);
    }

    private void handlePlay() {
	this.seeker.setEnabled(true);
	this.stopButton.setEnabled(true);
	this.playButton.setEnabled(false);
	this.ejctButton.setEnabled(true);
	
	setFontAwesomeIcon(this.state, FA_PLAY);
	this.state.setForeground(Color.GREEN);
    }

    private void handleStop() {
	this.seeker.setEnabled(true);
	this.stopButton.setEnabled(false);
	this.playButton.setEnabled(true);
	this.ejctButton.setEnabled(true);
	
	setFontAwesomeIcon(this.state, FA_STOP);
	this.state.setForeground(Color.RED);
    }

    @Override
    public void setTime(long micros) {
	this.position.setText(formatTime(micros));
	
	// Only update the seek bar if it isn't being used.
	if (!this.seeker.getValueIsAdjusting()) {
	    this.seeker.setValue((int) micros);
	}
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
	this.seeker.setMaximum((int) micros);
    }

    @Override
    public void loadFailed() {
	clearMetadata();
	setSongTitle("(Load Failed)");
    }
    
    private void clearMetadata() {
	setSongAlbum("No Album");
	setSongArtist("No Artist");
	setSongTitle("No Title");
	
	this.duration.setText("0:00:00");
	this.position.setText("0:00:00");
	
	this.seeker.setValue(0);
	this.seeker.setMaximum(0);
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
