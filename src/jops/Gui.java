package jops;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.Duration;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Gui implements Listener {
    private Model model;

    private JLabel state = new JLabel("EJECTED");
    private JLabel name = new JLabel("<No connection>");
    private JLabel position = new JLabel("--");
    private JFrame frame;

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

	JLabel track = new JLabel("No Track");

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

	this.frame.getContentPane().add(buttonPanel, BorderLayout.PAGE_END);
	this.frame.getContentPane().add(this.name, BorderLayout.PAGE_START);
	this.frame.getContentPane().add(this.state, BorderLayout.LINE_START);
	this.frame.getContentPane().add(track, BorderLayout.CENTER);
	this.frame.getContentPane().add(this.position, BorderLayout.LINE_END);

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
    }

    @Override
    public void setTime(long micros) {
	Duration dur = Duration.ofNanos(micros * 1000);

	long hours = dur.toHours();
	long mins = dur.minusHours(hours).toMinutes();
	long secs = dur.minusHours(hours).minusMinutes(mins).getSeconds();
	this.position.setText(String.format("%d:%02d:%02d",
		Long.valueOf(hours), Long.valueOf(mins), Long.valueOf(secs)));
    }

}
