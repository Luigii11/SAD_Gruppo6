/**
 * @file LoopIteratorTest.java
 * @brief Classe di test per la validazione del comportamento di LoopIterator.
 *
 * @details Verifica che la modalità di riproduzione ciclica rispetti tutti gli
 *          acceptance criteria definiti per la modalità Loop.
 *
 * @see LoopIterator
 * @see LoopMode
 * @author EmanuelaGraziuso
 */
package it.unisa.diem.sad_gruppo6.controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.playback.iterators.LoopIterator;
import it.unisa.diem.sad_gruppo6.model.playback.strategies.LoopMode;
import it.unisa.diem.sad_gruppo6.model.playback.strategies.SequentialMode;
import it.unisa.diem.sad_gruppo6.model.playback.iterators.PlaylistIterator;

public class LoopIteratorTest {

    private Track t1, t2, t3, t4, t5;
    private List<Track> tracks;

    /**
     * Setup eseguito prima di ogni test.
     * Crea una lista di 5 tracce con durate diverse per distinguerle chiaramente.
     */
    @BeforeEach
    public void setUp() throws Exception {
        java.io.File f1 = java.io.File.createTempFile("track1_", ".mp3");
        java.io.File f2 = java.io.File.createTempFile("track2_", ".mp3");
        java.io.File f3 = java.io.File.createTempFile("track3_", ".mp3");
        java.io.File f4 = java.io.File.createTempFile("track4_", ".mp3");
        java.io.File f5 = java.io.File.createTempFile("track5_", ".mp3");
        f1.deleteOnExit(); f2.deleteOnExit(); f3.deleteOnExit();
        f4.deleteOnExit(); f5.deleteOnExit();

        t1 = new Track("Track 1", "Artist A", 180, "Pop",  2001, f1.getAbsolutePath());
        t2 = new Track("Track 2", "Artist B", 200, "Rock", 2002, f2.getAbsolutePath());
        t3 = new Track("Track 3", "Artist C", 220, "Jazz", 2003, f3.getAbsolutePath());
        t4 = new Track("Track 4", "Artist D", 240, "Soul", 2004, f4.getAbsolutePath());
        t5 = new Track("Track 5", "Artist E", 260, "R&B",  2005, f5.getAbsolutePath());
        tracks = Arrays.asList(t1, t2, t3, t4, t5);
    }

    /**
     * Verifica che il costruttore lanci IllegalArgumentException
     * quando la lista di tracce è null.
     */
    @Test
    public void testConstructorThrowsOnNullList() {
        assertThrows(IllegalArgumentException.class,
            () -> new LoopIterator(null, null),
            "Il costruttore deve rifiutare una lista null");
    }

    /**
     * Verifica che il costruttore lanci IllegalArgumentException
     * quando la lista di tracce è vuota.
     */
    @Test
    public void testConstructorThrowsOnEmptyList() {
        assertThrows(IllegalArgumentException.class,
            () -> new LoopIterator(List.of(), null),
            "Il costruttore deve rifiutare una lista vuota");
    }

    /**
     * Verifica che hasNext() restituisca sempre true in
     * modalità loop, anche prima di qualsiasi chiamata a next().
     */
    @Test
    public void testHasNextAlwaysReturnsTrue() {
        LoopIterator iterator = new LoopIterator(tracks, t1);
        assertTrue(iterator.hasNext(),
            "hasNext() deve essere sempre true in modalità loop");
    }

    /**
     * Verifica che hasNext() rimanga true anche dopo aver
     * navigato tutta la lista con next(): la riproduzione non si esaurisce.
     */
    @Test
    public void testHasNextRemainsAfterFullCycle() {
        LoopIterator iterator = new LoopIterator(tracks, t1);
        // Scorre un numero di tracce pari alla dimensione della lista
        for (int i = 0; i < tracks.size(); i++) {
            iterator.next();
        }
        assertTrue(iterator.hasNext(),
            "hasNext() deve rimanere true dopo aver percorso tutte le tracce in loop");
    }

    /**
     * Verifica che next() non restituisca mai null in modalità
     * loop, nemmeno dopo aver cicliato più volte sull'intera playlist.
     */
    @Test
    public void testNextNeverReturnsNull() {
        LoopIterator iterator = new LoopIterator(tracks, null);
        for (int i = 0; i < tracks.size() * 3; i++) {
            assertNotNull(iterator.next(),
                "next() non deve mai restituire null in modalità loop");
        }
    }

    /**
     * Verifica il comportamento circolare di next(): dopo l'ultima
     * traccia, il ciclo successivo deve ricominciare dalla prima traccia (indice 0).
     */
    @Test
    public void testNextWrapsAroundToFirstTrack() {
        // Parte da t5 (ultima traccia), il prossimo next() deve tornare a t1
        LoopIterator iterator = new LoopIterator(tracks, t5);
        Track afterLast = iterator.next();
        assertEquals(t1, afterLast,
            "next() dopo l'ultima traccia deve ritornare alla prima (avanzamento circolare)");
    }

    /**
     * Verifica che next() esegua un ciclo completo nella sequenza corretta:
     * partendo da t1, dopo 5 chiamate a next() si deve tornare a t1.
     */
    @Test
    public void testNextCycleOrderIsCorrect() {
        LoopIterator iterator = new LoopIterator(tracks, t1);
        // Un ciclo completo: t2, t3, t4, t5, poi t1 di nuovo
        assertEquals(t2, iterator.next(), "1° next() da t1 deve restituire t2");
        assertEquals(t3, iterator.next(), "2° next() deve restituire t3");
        assertEquals(t4, iterator.next(), "3° next() deve restituire t4");
        assertEquals(t5, iterator.next(), "4° next() deve restituire t5");
        assertEquals(t1, iterator.next(), "5° next() deve ritornare a t1 (wrap-around)");
    }

    /**
     * Verifica che hasPrevious() restituisca sempre true in
     * modalità loop, analogamente a hasNext().
     */
    @Test
    public void testHasPreviousAlwaysReturnsTrue() {
        LoopIterator iterator = new LoopIterator(tracks, null);
        assertTrue(iterator.hasPrevious(),
            "hasPrevious() deve essere sempre true in modalità loop");
    }

    /**
     * Verifica che previous() non restituisca mai null in
     * modalità loop, nemmeno quando si naviga a ritroso più volte.
     */
    @Test
    public void testPreviousNeverReturnsNull() {
        LoopIterator iterator = new LoopIterator(tracks, null);
        for (int i = 0; i < tracks.size() * 3; i++) {
            assertNotNull(iterator.previous(),
                "previous() non deve mai restituire null in modalità loop");
        }
    }

    /**
     * Verifica il comportamento circolare di previous(): dalla prima
     * traccia (indice 0), previous() deve ritornare all'ultima.
     */
    @Test
    public void testPreviousWrapsAroundToLastTrack() {
        // Parte da t1 (prima traccia); previous() deve andare a t5
        LoopIterator iterator = new LoopIterator(tracks, t1);
        Track beforeFirst = iterator.previous();
        assertEquals(t5, beforeFirst,
            "previous() dalla prima traccia deve ritornare all'ultima (arretramento circolare)");
    }

    /**
     * Verifica che previous() esegua un arretramento nella sequenza corretta:
     * partendo da t1, tre chiamate a previous() devono restituire t5, t4, t3.
     */
    @Test
    public void testPreviousCycleOrderIsCorrect() {
        LoopIterator iterator = new LoopIterator(tracks, t1);
        assertEquals(t5, iterator.previous(), "1° previous() da t1 deve restituire t5");
        assertEquals(t4, iterator.previous(), "2° previous() deve restituire t4");
        assertEquals(t3, iterator.previous(), "3° previous() deve restituire t3");
    }

    /**
     * Verifica che dopo una sequenza di next() e previous(),
     * il risultato sia coerente con la posizione attesa nella lista.
     */
    @Test
    public void testNextThenPreviousReturnsCorrectTrack() {
        LoopIterator iterator = new LoopIterator(tracks, t1);
        iterator.next();   // ora su t2
        iterator.next();   // ora su t3
        Track goBack = iterator.previous(); // deve tornare a t2
        assertEquals(t2, goBack,
            "previous() dopo due next() partendo da t1 deve restituire t2");
    }

    /**
     * Verifica che l'ordine originale della lista NON venga modificato dopo
     * la navigazione in loop (ne avanti ne indietro).
     */
    @Test
    public void testOriginalListOrderIsPreserved() {
        List<Track> original = Arrays.asList(t1, t2, t3, t4, t5);
        LoopIterator iterator = new LoopIterator(tracks, null);

        // Naviga avanti un ciclo completo + 2
        for (int i = 0; i < tracks.size() + 2; i++) {
            iterator.next();
        }
        // Naviga indietro
        for (int i = 0; i < 3; i++) {
            iterator.previous();
        }

        assertEquals(original, tracks,
            "L'ordine originale della lista non deve essere alterato dalla navigazione loop");
    }

    /**
     * Verifica che il costruttore con startTrack non null posizioni
     * correttamente l'iteratore: il primo next() deve restituire
     * la traccia immediatamente successiva a startTrack.
     */
    @Test
    public void testStartTrackPositionsIteratorCorrectly() {
        // Parte da t3: il prossimo next() deve essere t4
        LoopIterator iterator = new LoopIterator(tracks, t3);
        Track nextAfterStart = iterator.next();
        assertEquals(t4, nextAfterStart,
            "Con startTrack=t3, il primo next() deve restituire t4");
    }

    /**
     * Verifica che con startTrack null l'iteratore parta dall'indice 0:
     * il primo next() deve restituire la seconda traccia della lista.
     */
    @Test
    public void testNullStartTrackStartsFromIndexZero() {
        LoopIterator iterator = new LoopIterator(tracks, null);
        Track first = iterator.next();
        assertEquals(t2, first,
            "Con startTrack=null il primo next() deve restituire t2 (partenza da indice 0)");
    }

    /**
     * Verifica che dopo reset(), l'iteratore torni all'indice 0:
     * la successiva chiamata a next() deve restituire t2.
     */
    @Test
    public void testResetRestoresIndexZero() {
        LoopIterator iterator = new LoopIterator(tracks, t3);
        iterator.next();
        iterator.next();

        iterator.reset();

        Track afterReset = iterator.next();
        assertEquals(t2, afterReset,
            "Dopo reset() il primo next() deve restituire t2 (indice 0 → avanzamento a 1)");
    }

    /**
     * Verifica che dopo reset(), hasNext() e
     * hasPrevious() rimangano entrambi true.
     */
    @Test
    public void testResetKeepsLoopInvariants() {
        LoopIterator iterator = new LoopIterator(tracks, t5);
        for (int i = 0; i < tracks.size(); i++) {
            iterator.next();
        }

        iterator.reset();

        assertTrue(iterator.hasNext(),
            "Dopo reset() hasNext() deve essere true");
        assertTrue(iterator.hasPrevious(),
            "Dopo reset() hasPrevious() deve essere true");
    }

    /**
     * Verifica che LoopMode#getIterator(List, Track) restituisca
     * un'istanza di LoopIterator non nulla.
     */
    @Test
    public void testLoopModeReturnsLoopIterator() {
        LoopMode mode = new LoopMode();
        PlaylistIterator iterator = mode.getIterator(tracks, t1);
        assertNotNull(iterator,
            "LoopMode.getIterator() non deve restituire null");
        assertInstanceOf(LoopIterator.class, iterator,
            "LoopMode.getIterator() deve restituire un'istanza di LoopIterator");
    }

    /**
     * Verifica che SequentialMode#getIterator(List, Track) restituisca
     * un iteratore che NON è un LoopIterator.
     */
    @Test
    public void testSequentialModeDoesNotReturnLoopIterator() {
        SequentialMode mode = new SequentialMode();
        PlaylistIterator iterator = mode.getIterator(tracks, t1);
        assertNotNull(iterator,
            "SequentialMode.getIterator() non deve restituire null");
        assertFalse(iterator instanceof LoopIterator,
            "SequentialMode.getIterator() non deve restituire un LoopIterator");
    }
}
