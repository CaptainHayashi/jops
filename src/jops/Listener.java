package jops;

public interface Listener {
    void setName(String name);

    void setState(State to);

    void setTime(long seconds);
}
