package jops;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

public class Model implements Listener {
    private BlockingQueue<Response> inQueue;
    private BlockingQueue<Request> outQueue;
    private Set<Listener> listeners = new HashSet<>();

    public Model(BlockingQueue<Response> inInQueue,
	    BlockingQueue<Request> inOutQueue) {
	this.inQueue = inInQueue;
	this.outQueue = inOutQueue;
    }

    public void registerListener(Listener l) {
	this.listeners.add(l);
    }

    @Override
    public void setName(String name) {
	this.listeners.forEach((listener) -> {
	    listener.setName(name);
	});
    }

    @Override
    public void setState(State to) {
	this.listeners.forEach((listener) -> {
	    listener.setState(to);
	});
    }

    @Override
    public void setTime(long micros) {
	this.listeners.forEach((listener) -> {
	    listener.setTime(micros);
	});
    }

    @Override
    public void setTotalDuration(long micros) {
	this.listeners.forEach((listener) -> {
	    listener.setTotalDuration(micros);
	});
    }

    @Override
    public void loadFailed() {
	this.listeners.forEach((listener) -> {
	    listener.loadFailed();
	});
    }

    @Override
    public void setSongAlbum(String album) {
	this.listeners.forEach((listener) -> {
	    listener.setSongAlbum(album);
	});
    }
    
    @Override
    public void setSongArtist(String artist) {
	this.listeners.forEach((listener) -> {
	    listener.setSongArtist(artist);
	});
    }
    
    @Override
    public void setSongTitle(String title) {
	this.listeners.forEach((listener) -> {
	    listener.setSongTitle(title);
	});
    }
    
    
    public void run() {
	for (;;) {
	    try {
		this.inQueue.take().handle(this);
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }

    public void play() {
	stateChange(State.PLAYING);
    }

    public void stop() {
	stateChange(State.STOPPED);
    }

    public void eject() {
	stateChange(State.EJECTED);
    }

    private void stateChange(State state) {
	try {
	    System.out.println("Requesting state change to " + state);
	    this.outQueue.put(new StateChangeRequest(state));
	} catch (InterruptedException e) {
	    // Ignore
	}
    }

    public void load(File selectedFile) {
	boolean fileWorks = FileProbe.probe(selectedFile, this);
	if (fileWorks) {
	    try {
		this.outQueue.put(new LoadRequest(selectedFile));
	    } catch (InterruptedException e) {
		// Ignore
	    }
	} else {
	    loadFailed();
	}
    }
}
