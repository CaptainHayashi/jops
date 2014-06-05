package jops;

public class TimeResponse implements Response {
    private long micros;

    public TimeResponse(long inMicros) {
	this.micros = inMicros;
    }

    @Override
    public void handle(Model model) {
	model.setTime(this.micros);
    }
}
