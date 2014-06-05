package jops;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;

public class PlayslaveOutput implements Runnable {

    private Process playslave;
    private BlockingQueue<Request> outQueue;

    public PlayslaveOutput(Process inPlayslave, BlockingQueue<Request> inOutQueue) {
	this.playslave = inPlayslave;
	this.outQueue = inOutQueue;
    }

    @Override
    public void run() {
	try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
		this.playslave.getOutputStream()))) {
	    for (;;) {
		pw.println(this.outQueue.take());
		pw.flush();
	    }
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

}
