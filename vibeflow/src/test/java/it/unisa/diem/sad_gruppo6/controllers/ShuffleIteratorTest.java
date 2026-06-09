/**
 * @file ShuffleIteratorTest.java
 * @brief Classe di test per la validazione del comportamento di {@link ShuffleIterator}.
 *
 * @details Verifica che la modalità di riproduzione casuale rispetti tutti gli
 *          acceptance criteria definiti per il task ID_13:
 *          <ul>
 *            <li>La traccia successiva viene scelta casualmente senza alterare
 *                l'ordine originale della playlist.</li>
 *            <li>La disattivazione dello shuffle non interrompe il brano
 *                corrente e i successivi tornano sequenziali.</li>
 *          </ul>
 *          Poiché {@link ShuffleIterator} non dipende da JavaFX, i test vengono
 *          eseguiti direttamente senza bisogno di inizializzare il Toolkit.
 *
 * @see ShuffleIterator
 * @see ShuffleMode
 * @author ChiaraCrisci
 */
package it.unisa.diem.sad_gruppo6.controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.playback.iterators.ShuffleIterator;
import it.unisa.diem.sad_gruppo6.model.playback.strategies.ShuffleMode;
import it.unisa.diem.sad_gruppo6.model.playback.strategies.SequentialMode;
import it.unisa.diem.sad_gruppo6.model.playback.iterators.PlaylistIterator;

public class ShuffleIteratorTest {

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
     * Verifica che il costruttore lanci {@link IllegalArgumentException}
     * quando la lista di tracce è null (guardia di robustezza).
     */
    @Test
    public void testConstructorThrowsOnNullList() {
        assertThrows(IllegalArgumentException.class,
            () -> new ShuffleIterator(null, null),
            "Il costruttore deve rifiutare una lista null");
    }

    /**
     * Verifica che il costruttore lanci {@link IllegalArgumentException}
     * quando la lista di tracce è vuota.
     */
    @Test
    public void testConstructorThrowsOnEmptyList() {
        assertThrows(IllegalArgumentException.class,
            () -> new ShuffleIterator(List.of(), null),
            "Il costruttore deve rifiutare una lista vuota");
    }

    /**
     * Verifica che {@code hasNext()} restituisca {@code true} appena creato
     * l'iteratore con una startTrack, poiché ci sono ancora brani in remaining.
     */
    @Test
    public void testHasNextReturnsTrueWhenTracksRemain() {
        ShuffleIterator iterator = new ShuffleIterator(tracks, t1);
        assertTrue(iterator.hasNext(),
            "hasNext() deve essere true se ci sono tracce non ancora riprodotte");
    }

    /**
     * Verifica che {@code hasNext()} restituisca {@code false} dopo aver
     * consumato tutte le tracce con next().
     */
    @Test
    public void testHasNextReturnsFalseWhenAllConsumed() {
        ShuffleIterator iterator = new ShuffleIterator(tracks, t1);
        // Consuma tutte le tracce rimanenti
        while (iterator.hasNext()) {
            iterator.next();
        }
        assertFalse(iterator.hasNext(),
            "hasNext() deve essere false dopo aver consumato tutte le tracce");
    }

    /**
     * Verifica che {@code next()} restituisca {@code null} quando non ci sono
     * più tracce disponibili (fine shuffle).
     */
    @Test
    public void testNextReturnsNullWhenExhausted() {
        ShuffleIterator iterator = new ShuffleIterator(tracks, t1);
        while (iterator.hasNext()) {
            iterator.next();
        }
        assertNull(iterator.next(),
            "next() deve restituire null quando tutte le tracce sono state riprodotte");
    }

    /**
     * Verifica che tutte le tracce vengano riprodotte esattamente una volta
     * durante uno shuffle completo (nessuna traccia saltata o duplicata).
     */
    @Test
    public void testNextProducesEachTrackExactlyOnce() {
        ShuffleIterator iterator = new ShuffleIterator(tracks, null);
        Set<Track> seen = new HashSet<>();

        while (iterator.hasNext()) {
            Track t = iterator.next();
            assertNotNull(t, "next() non deve restituire null se hasNext() è true");
            assertFalse(seen.contains(t),
                "La traccia '" + t.getTitle() + "' non deve essere riprodotta due volte");
            seen.add(t);
        }

        assertEquals(tracks.size(), seen.size(),
            "Tutte e " + tracks.size() + " le tracce devono essere riprodotte esattamente una volta");
    }

    /**
     * Verifica che l'ordine originale della lista NON venga modificato dopo
     * lo shuffle (nessuna traccia saltata o duplicata).
     */
    @Test
    public void testOriginalListOrderIsPreserved() {
        List<Track> original = Arrays.asList(t1, t2, t3, t4, t5);
        ShuffleIterator iterator = new ShuffleIterator(tracks, null);

        while (iterator.hasNext()) {
            iterator.next();
        }

        assertEquals(original, tracks,
            "L'ordine originale della lista non deve essere alterato dallo shuffle");
    }

    /**
     * Verifica che la startTrack non venga riprodotta due volte: deve essere
     * già nella history e non comparire di nuovo nelle successive chiamate a next().
     */
    @Test
    public void testStartTrackNotRepeatedByNext() {
        ShuffleIterator iterator = new ShuffleIterator(tracks, t1);
        Set<Track> played = new HashSet<>();
        played.add(t1);

        while (iterator.hasNext()) {
            Track t = iterator.next();
            assertFalse(played.contains(t),
                "La startTrack non deve essere riprodotta di nuovo da next()");
            played.add(t);
        }
    }

    /**
     * Verifica che {@code hasPrevious()} sia {@code false} all'inizio, prima
     * di qualsiasi chiamata a {@code next()}.
     */
    @Test
    public void testHasPreviousFalseAtStart() {
        ShuffleIterator iterator = new ShuffleIterator(tracks, null);
        assertFalse(iterator.hasPrevious(),
            "hasPrevious() deve essere false prima di qualsiasi next()");
    }

    /**
     * Verifica che dopo una chiamata a {@code next()}, {@code hasPrevious()}
     * restituisca {@code true}.
     */
    @Test
    public void testHasPreviousTrueAfterNext() {
        ShuffleIterator iterator = new ShuffleIterator(tracks, null);
        iterator.next(); // primo brano
        iterator.next(); // secondo brano
        assertTrue(iterator.hasPrevious(),
            "hasPrevious() deve essere true dopo almeno due chiamate a next()");
    }

    /**
     * Verifica che {@code previous()} ritorni la traccia precedente nella
     * cronologia senza estrarne una nuova da remaining (navigazione a ritroso
     * nella history).
     */
    @Test
    public void testPreviousReturnsHistoryTrack() {
        ShuffleIterator iterator = new ShuffleIterator(tracks, null);
        Track first = iterator.next();
        Track second = iterator.next();

        Track backToFirst = iterator.previous();
        assertEquals(first, backToFirst,
            "previous() deve restituire la prima traccia della history dopo due next()");
    }

    /**
     * Verifica che {@code previous()} restituisca {@code null} quando non
     * ci sono tracce precedenti nella cronologia.
     */
    @Test
    public void testPreviousReturnsNullAtStart() {
        ShuffleIterator iterator = new ShuffleIterator(tracks, null);
        assertNull(iterator.previous(),
            "previous() deve restituire null se non c'è nessuna traccia precedente");
    }

    /**
     * Verifica che dopo un {@code previous()}, la successiva chiamata a
     * {@code next()} riprenda dalla posizione corretta nella history (senza
     * aggiungere nuova casualità).
     */
    @Test
    public void testNextAfterPreviousResumesHistory() {
        ShuffleIterator iterator = new ShuffleIterator(tracks, null);
        Track first = iterator.next();
        Track second = iterator.next();

        iterator.previous(); // torna a first
        Track resumedSecond = iterator.next(); // deve tornare second, non una nuova traccia

        assertEquals(second, resumedSecond,
            "next() dopo previous() deve riprendere dalla history, non scegliere casualmente");
    }

    /**
     * Verifica che dopo {@code reset()}, l'iteratore torni allo stato iniziale:
     * {@code hasNext()} deve essere {@code true} e {@code hasPrevious()} {@code false}.
     */
    @Test
    public void testResetRestoresInitialState() {
        ShuffleIterator iterator = new ShuffleIterator(tracks, null);
        // Consuma tutte le tracce
        while (iterator.hasNext()) {
            iterator.next();
        }
        assertFalse(iterator.hasNext(), "Prima del reset non devono esserci tracce");

        iterator.reset();

        assertTrue(iterator.hasNext(),
            "Dopo reset() hasNext() deve essere true");
        assertFalse(iterator.hasPrevious(),
            "Dopo reset() hasPrevious() deve essere false");
    }

    /**
     * Verifica che dopo {@code reset()}, tutte le tracce tornino disponibili
     * per una nuova sessione shuffle completa.
     */
    @Test
    public void testResetAllowsFullShuffleAgain() {
        ShuffleIterator iterator = new ShuffleIterator(tracks, null);
        while (iterator.hasNext()) iterator.next();

        iterator.reset();

        Set<Track> seen = new HashSet<>();
        while (iterator.hasNext()) {
            seen.add(iterator.next());
        }
        assertEquals(tracks.size(), seen.size(),
            "Dopo reset() devono essere disponibili di nuovo tutte le tracce");
    }

    /**
     * Verifica che {@link ShuffleMode#getIterator(List, Track)} restituisca
     * un'istanza di {@link ShuffleIterator} non nulla.
     */
    @Test
    public void testShuffleModeReturnsShuffleIterator() {
        ShuffleMode mode = new ShuffleMode();
        PlaylistIterator iterator = mode.getIterator(tracks, t1);
        assertNotNull(iterator,
            "ShuffleMode.getIterator() non deve restituire null");
        assertInstanceOf(ShuffleIterator.class, iterator,
            "ShuffleMode.getIterator() deve restituire un'istanza di ShuffleIterator");
    }

    /**
     * Verifica che {@link SequentialMode#getIterator(List, Track)} restituisca
     * un iteratore che NON è uno ShuffleIterator (controllo di tipo).
     */
    @Test
    public void testSequentialModeDoesNotReturnShuffleIterator() {
        SequentialMode mode = new SequentialMode();
        PlaylistIterator iterator = mode.getIterator(tracks, t1);
        assertNotNull(iterator,
            "SequentialMode.getIterator() non deve restituire null");
        assertFalse(iterator instanceof ShuffleIterator,
            "SequentialMode.getIterator() non deve restituire uno ShuffleIterator");
    }
}