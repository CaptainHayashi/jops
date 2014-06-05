package jops;

public class FailResponse implements Response {
    private String string;

    public FailResponse(String inString) {
	this.string = inString;
    }

    @Override
    public void handle(Model model) {
	// TODO Auto-generated method stub
	System.err.println("Failed response: " + this.string);
    }

}
