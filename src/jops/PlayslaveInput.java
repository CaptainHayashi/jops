package jops;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

public class PlayslaveInput implements Runnable {
    private BlockingQueue<Response> responseQueue;
    private Process playslave;
    private ResponseParser responseParser;

    public PlayslaveInput(Process inPlayslave,
	    BlockingQueue<Response> inResponseQueue,
	    ResponseParser inResponseParser) {
	this.playslave = inPlayslave;
	this.responseQueue = inResponseQueue;
	this.responseParser = inResponseParser;
    }

    @Override
    public void run() {
	try (BufferedReader br = new BufferedReader(new InputStreamReader(
		this.playslave.getInputStream()))) {
	    for (;;) {
		enqueue(parse(br.readLine()));
	    }
	} catch (IOException | InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    private Response parse(String input) {
	System.out.println("Got " + input);
	return this.responseParser.parse(input);
    }

    private void enqueue(Response response) throws InterruptedException {
	this.responseQueue.put(response);
    }
}
