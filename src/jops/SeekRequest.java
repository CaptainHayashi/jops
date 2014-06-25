package jops;

public class SeekRequest implements Request {
    long position;
    
    public SeekRequest(long inPosition) {
	this.position = inPosition;
    }
    
    @Override
    public String toString() {
	return "seek " + this.position;
    }
}
