package it.unisa.diem.sad_gruppo6.controllers;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import it.unisa.diem.sad_gruppo6.model.domain.Track;

import java.io.File;

public class TrackTest {

    @Test
    public void testValidTrackCreation() throws Exception {
        File f = File.createTempFile("track1_", ".mp3");
        f.deleteOnExit();
        Track track = new Track("Bohemian Rhapsody", "Queen", 354, "Rock", 1975, f.getAbsolutePath());
        assertEquals("Bohemian Rhapsody", track.getTitle());
        assertEquals(354, track.getDuration());
        assertEquals(1975, track.getYear());
    }

    @Test
    public void testEmptyTitleThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Track("   ", "Queen", 354, "Rock", 1975, null));
    }

    @Test
    public void testYearOutOfRangeThrows() {
        assertThrows(IllegalArgumentException.class, () ->
            new Track("Title", "Author", 200, "Pop", 2050, null));
    }

    @Test
    public void testNegativeDurationThrows() throws Exception {
        assertThrows(IllegalArgumentException.class, () ->
            new Track("Title", "Author", -5, "Pop", 2000, null));
    }

    @Test
    public void testEqualsSameTitleAndAuthor() throws Exception{
        File f1 = File.createTempFile("track1_", ".mp3"); 
        f1.deleteOnExit();
        File f2 = File.createTempFile("track2_", ".mp3"); 
        f2.deleteOnExit();
        Track t1 = new Track("Title", "Author", 200, "Pop", 2000, f1.getAbsolutePath());
        Track t2 = new Track("Title", "Author", 999, "Jazz", 1990, f2.getAbsolutePath());
        assertEquals(t1, t2);   // stesso titolo+autore → stessa traccia
        assertEquals(t1.hashCode(), t2.hashCode());
    }
}