package it.unisa.diem.sad_gruppo6.models;

public interface PlaylistIterator {
    boolean hasNext();
    Track next();
    boolean hasPrevious();
    Track previous();
    void reset();
}
