package it.unisa.diem.sad_gruppo6.controllers;

import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.playback.iterators.SequentialIterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @brief Test unitari per SequentialIterator.
 */
public class SequentialIteratorTest {

    private List<Track> tracks;
    private Track t1, t2, t3;

    @BeforeEach
    public void setUp() {
        int annoValido = 2024; // Un valore compreso tra 1970 e 2026
        
        t1 = new Track("Titolo1", "Autore1", 100, "Genere1", annoValido);
        t2 = new Track("Titolo2", "Autore2", 200, "Genere2", annoValido);
        t3 = new Track("Titolo3", "Autore3", 300, "Genere3", annoValido);
        
        tracks = Arrays.asList(t1, t2, t3);
    }

    @Test
    public void testSequentialNavigation() {
        SequentialIterator it = new SequentialIterator(tracks, t1);
        
        assertEquals(t2, it.next(), "Il primo next dopo t1 deve essere t2");
        assertEquals(t3, it.next(), "Il prossimo deve essere t3");
        assertFalse(it.hasNext(), "Alla fine non dovrebbe esserci un prossimo");
    }

    @Test
    public void testPreviousNavigation() {
        SequentialIterator it = new SequentialIterator(tracks, t3);
        
        assertTrue(it.hasPrevious());
        assertEquals(t2, it.previous());
        assertEquals(t1, it.previous());
        assertFalse(it.hasPrevious(), "All'inizio non dovrebbe esserci un precedente");
    }

    @Test
    public void testInvalidTrackThrowsException() {
        Track nonEsistente = new Track("Fake", "Fake", 100, "Genere", 2000);
        assertThrows(IllegalArgumentException.class, () -> new SequentialIterator(tracks, nonEsistente));
    }

    @Test
    public void testTrackYearValidation() {
    // Verifica che anno troppo basso lanci eccezione
    assertThrows(IllegalArgumentException.class, () -> 
        new Track("Test", "Autore", 100, "Genere", 1969));
        
    // Verifica che anno troppo alto lanci eccezione
    assertThrows(IllegalArgumentException.class, () -> 
        new Track("Test", "Autore", 100, "Genere", 2027));

    // Verifica che i limiti siano accettati
    assertDoesNotThrow(() -> new Track("Test", "Autore", 100, "Genere", 1970));
    assertDoesNotThrow(() -> new Track("Test", "Autore", 100, "Genere", 2026));
    }

    @Test
    public void testEmptyList() {
        List<Track> emptyList = Arrays.asList();
        // Verifica che l'iteratore gestisca una lista vuota (o lanci l'eccezione corretta)
        assertThrows(IllegalArgumentException.class, () -> new SequentialIterator(emptyList, null));
    }

    @Test
    public void testSingleElement() {
        Track t1 = new Track("T1", "A1", 100, "Genere", 2024);
        List<Track> singleList = Arrays.asList(t1);
        SequentialIterator it = new SequentialIterator(singleList, t1);
        
        // Se c'è solo T1, dopo T1 non c'è nulla, quindi il risultato deve essere null
        assertNull(it.next(), "Su una lista con un solo elemento, next() deve restituire null");
        assertFalse(it.hasNext());
        assertFalse(it.hasPrevious());
    }

    @Test
    public void testBounds() {
        SequentialIterator it = new SequentialIterator(tracks, t3);
        assertNull(it.next(), "Se sono alla fine, next() deve restituire null");
    }
}