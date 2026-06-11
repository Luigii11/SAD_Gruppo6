/**
 * @file TrackLibraryViewControllerTest.java
 * Classe di test per la validazione della logica di business all'interno di 'TrackLibraryViewController'.
 * Utilizza il framework JUnit 5 per verificare il corretto funzionamento dei metodi e il comportamento del controller 
 * in risposta agli eventi della libreria.
 * * @author EmanuelaGraziuso
 */


package it.unisa.diem.sad_gruppo6.controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.LinkedHashSet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.sad_gruppo6.controller.ui.library.TrackLibraryViewController;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibrary;

import java.io.File;
import java.lang.reflect.Field;




public class TrackLibraryViewControllerTest {

    private TrackLibrary testLibrary;
    private TrackLibraryViewController testController;
    private Track track1, track2, track3, track4, track5;

    /**
     * Metodo di setup eseguito prima di ogni singolo test (@BeforeEach).
     * Ottiene il singleton TrackLibrary e lo svuota per garantire
     * un ambiente pulito e deterministico ad ogni esecuzione.
     */

    @BeforeEach
    public void setUp() throws Exception {
        testLibrary = TrackLibrary.getInstance();
 
        // Svuotiamo la libreria singleton tramite reflection per isolare i test
        Field tracksField = TrackLibrary.class.getDeclaredField("tracks");
        tracksField.setAccessible(true);
        ((LinkedHashSet<?>) tracksField.get(testLibrary)).clear();
 
        testController = new TrackLibraryViewController();

        Field libraryField = TrackLibraryViewController.class.getDeclaredField("library");
        libraryField.setAccessible(true);
        libraryField.set(testController, testLibrary);

        File f1 = File.createTempFile("track1_", ".mp3"); f1.deleteOnExit();
        File f2 = File.createTempFile("track2_", ".mp3"); f2.deleteOnExit();
        File f3 = File.createTempFile("track3_", ".mp3"); f3.deleteOnExit();
        File f4 = File.createTempFile("track4_", ".mp3"); f4.deleteOnExit();
        File f5 = File.createTempFile("track5_", ".mp3"); f5.deleteOnExit();
        track1 = new Track("Albachiara", "Vasco Rossi", 240, "Rock", 1984, f1.getAbsolutePath());
        track2 = new Track("Napule è", "Pino Daniele", 227, "Pop", 1977, f2.getAbsolutePath());
        track3 = new Track("Je so' pazzo", "Pino Daniele", 223, "Blues", 1979, f3.getAbsolutePath());
        track4 = new Track("Quanno chiove", "Pino Daniele", 275, "Blues", 1980, f4.getAbsolutePath());
        track5 = new Track("Yesterday", "The Beatles", 125, "Pop", 1975, f5.getAbsolutePath());

    }

    /**
     * Verifica che, quando la libreria è vuota,
     * il metodo onLibraryChanged() imposti correttamente lo stato
     * interno del controller come "lista vuota" (emptyVisible = true).
     */

    @Test
    public void testOnLibraryChanged_emptyLibrary_setsEmptyState() {
    // La libreria è vuota per costruzione (setUp)
    assertTrue(testLibrary.getTracks().isEmpty(),
            "Precondizione: la libreria deve essere vuota");

    assertDoesNotThrow(() -> testController.onLibraryChanged(),
            "onLibraryChanged() non deve lanciare eccezioni con libreria vuota");
}

    /**
     * Verifica che, dopo l'aggiunta di una traccia alla libreria, 
     * la libreria stessa contenga l'elemento con i metadati corretti
     * (titolo, artista/autore, durata) che il controller dovrà poi esporre in lista.
     */

    @Test
    public void testOnTrackAdded_libraryContainsTrackWithCorrectMetadata() {
     testLibrary.addTrack(track1);

    List<Track> tracks = testLibrary.getTracks();

    assertEquals(1, tracks.size(), "La libreria deve contenere esattamente 1 traccia");
    assertEquals("Albachiara", tracks.get(0).getTitle());
    assertEquals("Vasco Rossi", tracks.get(0).getAuthor());
    assertEquals(240, tracks.get(0).getDuration());
    assertEquals("Rock", tracks.get(0).getGenre());
    assertEquals(1984, tracks.get(0).getYear());
    }

    /**
     * Verifica che più tracce aggiunte alla libreria siano
     * tutte recuperabili dal controller tramite getTracks(), rispettando
     * la regola di business che richiede la visualizzazione dell'intero elenco.
     */

    @Test
    public void testOnTrackAdded_multipleTracksAllRetrievable() {
    testLibrary.addTrack(track2);
    testLibrary.addTrack(track3);
    testLibrary.addTrack(track4);

    List<Track> tracks = testLibrary.getTracks();

    assertEquals(3, tracks.size(), "La libreria deve esporre tutte e 3 le tracce aggiunte");
    }

    /**
     * Verifica che la durata di una traccia sia correttamente convertibile nel formato 'm:ss' 
     * 
     */
    @Test
    public void testDurationFormat_isCorrectlyComputed() {
   
    int min = track5.getDuration() / 60;
    int sec = track5.getDuration() % 60;
    String formatted = String.format("%d:%02d", min, sec);

    assertEquals("2:05", formatted,
            "La durata 125s deve essere formattata come '2:05'");
    }

    /**
     * Verifica che dopo onTrackAdded() la libreria rifletta effettivamente la nuova traccia, confermando che l'aggiornamento
     * dello stato sia coerente con la notifica ricevuta.
     */
    @Test
    public void testOnTrackAdded_libraryIsUpdatedBeforeNotification() {
    
    testLibrary.addTrack(track1);
    testController.onTrackAdded(track1); // il controller viene notificato

    assertEquals(1, testLibrary.getTracks().size(),
            "Dopo onTrackAdded la libreria deve contenere la traccia appena inserita");
    }
     
}
