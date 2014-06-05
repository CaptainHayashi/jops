/**
 * 
 */
package jops;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author mattbw
 *
 */
public class Jops {
    private String[] args;
    private Process playslave;

    public Jops(String inArgs[]) {
	this.args = inArgs;
    }

    public static void main(String args[]) {
	new Jops(args).run();
    }

    private void run() {
	if (this.args.length == 0) {
	    System.err.println("Need a command line for invoking playslave.");
	} else {
	    try {
		this.playslave = new ProcessBuilder(this.args).start();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}

	if (this.playslave != null) {
	    BlockingQueue<Response> inQueue = new LinkedBlockingQueue<>();
	    BlockingQueue<Request> outQueue = new LinkedBlockingQueue<>();
	    ResponseParser parser = new SimpleResponseParser();

	    new Thread(new PlayslaveInput(this.playslave, inQueue, parser))
		    .start();
	    new Thread(new PlayslaveOutput(this.playslave, outQueue)).start();
	    Model model = new Model(inQueue, outQueue);
	    new Gui(model).start();

	    model.run();
	}
    }
}
