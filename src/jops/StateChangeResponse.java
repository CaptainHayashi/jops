package jops;

public class StateChangeResponse implements Response {
    private State to;

    public StateChangeResponse(State inFrom, State inTo) {
	this.to = inTo;
    }

    @Override
    public void handle(Model model) {
	model.setState(this.to);
    }

}
