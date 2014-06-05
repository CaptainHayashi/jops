package jops;

public class StateChangeRequest implements Request {
    private State state;

    public StateChangeRequest(State inState) {
	this.state = inState;
    }

    @Override
    public String toString() {
	String string = null;

	switch (this.state) {
	case PLAYING:
	    string = "play";
	    break;
	case STOPPED:
	    string = "stop";
	    break;
	case EJECTED:
	    string = "ejct";
	    break;
	default:
	    string = "????";
	    break;
	}
	assert string != null;
	System.out.println(string);
	return string;
    }
}
