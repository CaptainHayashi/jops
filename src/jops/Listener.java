package jops;

public interface Listener {
    void setName(String name);

    void setState(State to);

    void setTime(long seconds);

    void setTotalDuration(long micros);
    
    void setSongAlbum(String album);
    void setSongArtist(String artist);
    void setSongTitle(String title);
    
    
    void loadFailed();
}
