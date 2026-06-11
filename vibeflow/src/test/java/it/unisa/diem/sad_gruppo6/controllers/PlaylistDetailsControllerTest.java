/**
 * @file PlyalistDetailsControllerTest.java
 * Classe di test per la validazione della logica all'interno della classe PlaylistDetailsController
 * 
 * Scenari coperti:
 * - aggiunta traccia a una playlist
 * - aggiunta traccia già presente
 * - rimozione traccia presente nella playlist
 * - rimozione traccia non presente nella playlist
 * - verifica contatore delle tracce dopo aggiunta/rimozione
 * 
 * @author EmanuelaGraziuso
 */



package it.unisa.diem.sad_gruppo6.controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.sad_gruppo6.controller.business.playlist.PlaylistController;
import it.unisa.diem.sad_gruppo6.model.command.CommandManager;
import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibrary;
import it.unisa.diem.sad_gruppo6.model.library.PlaylistLibraryObserver;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibrary;



public class PlaylistDetailsControllerTest {

    private TrackLibrary testTrackLibrary;
    private PlaylistLibrary testPlaylistLibrary;
    private CommandManager testCommandManager;
    private PlaylistController testPlaylistController; 
    
    private Playlist currentPlaylist;

    private Track existingTrack;
    private Track newTrack;       
    private Track secondTrack;    
    private Track notInPlaylist; 
    private Track t2, t3; 

    /**
     * Metodo di setup eseguito prima di ogni singolo test.
     *
     * Inizializza un ambiente pulito.
     */
    @BeforeEach
    public void setUp() throws Exception{
        testPlaylistLibrary = PlaylistLibrary.getInstance();
        testPlaylistLibrary.clear();

        testTrackLibrary = TrackLibrary.getInstance();
        List<Track> currentTracks = new ArrayList<>(testTrackLibrary.getTracks());
        for (Track t : currentTracks) {
            testTrackLibrary.removeTrack(t);
        }

        testCommandManager = CommandManager.getInstance();
        testPlaylistController = new PlaylistController(
                testTrackLibrary, testPlaylistLibrary, testCommandManager);

        testPlaylistController.createPlaylist("La mia playlist");
        currentPlaylist = testPlaylistLibrary.getPlaylists().get(0);

        File f = File.createTempFile("track1_", ".mp3"); 
        f.deleteOnExit();
        existingTrack = new Track("Napule è", "Pino Daniele", 227, "Pop", 1977, f.getAbsolutePath());
        testPlaylistController.addTrackToPlaylist(existingTrack, currentPlaylist);

        File fNew = File.createTempFile("track_new_", ".mp3"); fNew.deleteOnExit();
        newTrack = new Track("Je so' pazzo", "Pino Daniele", 223, "Blues", 1979, fNew.getAbsolutePath());

        File fSecond = File.createTempFile("track_second_", ".mp3"); fSecond.deleteOnExit();
        secondTrack = new Track("Quanno chiove", "Pino Daniele", 275, "Blues", 1980, fSecond.getAbsolutePath());

        File fNot = File.createTempFile("track_not_", ".mp3"); fNot.deleteOnExit();
        notInPlaylist = new Track("Albachiara", "Vasco Rossi", 240, "Rock", 1984, fNot.getAbsolutePath());

        File ft2 = File.createTempFile("track_t2_", ".mp3"); ft2.deleteOnExit();
        t2 = new Track("Je so' pazzo", "Pino Daniele", 223, "Blues", 1979, ft2.getAbsolutePath());

        File ft3 = File.createTempFile("track_t3_", ".mp3"); ft3.deleteOnExit();
        t3 = new Track("Quanno chiove", "Pino Daniele", 275, "Blues", 1980, ft3.getAbsolutePath());
    }

    /**
     * Testa l'aggiunta di una nuova traccia alla playlist corrente.
     * Verifica che la playlist contenga la traccia aggiunta e che il contatore
     * aumenti di conseguenza.
     */
    @Test
    public void testAddTrackToPlaylist_success() {
        testPlaylistController.addTrackToPlaylist(newTrack, currentPlaylist);

        assertEquals(2, currentPlaylist.getTracks().size(),
                "La playlist dovrebbe contenere 2 tracce dopo l'aggiunta");
        assertTrue(currentPlaylist.getTracks().contains(newTrack),
                "La playlist dovrebbe contenere la traccia appena aggiunta");
    }

    /**
     * Testa il tentativo di aggiungere una traccia già presente nella playlist.
     * Verifica che venga lanciata IllegalArgumentException e che
     * il numero di tracce rimanga invariato (no duplicati).
     *
     */
    @Test
    public void testAddTrackToPlaylist_duplicateThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                testPlaylistController.addTrackToPlaylist(existingTrack, currentPlaylist),
                "Dovrebbe lanciare un'eccezione per traccia duplicata nella playlist");

        assertEquals(1, currentPlaylist.getTracks().size(),
                "Il numero di tracce non deve cambiare in caso di duplicato");
    }

    /**
     * Testa la rimozione corretta di una traccia dalla playlist corrente.
     * Verifica che la playlist sia vuota dopo la rimozione (dopo la conferma).
     */
    @Test
    public void testRemoveTrackFromPlaylist_success() {
        testPlaylistController.removeTrackFromPlaylist(existingTrack, currentPlaylist);

        assertEquals(0, currentPlaylist.getTracks().size(),
                "La playlist dovrebbe essere vuota dopo la rimozione dell'unica traccia");
        assertFalse(currentPlaylist.getTracks().contains(existingTrack),
                "La traccia rimossa non dovrebbe più essere nella playlist");
    }

    /**
     * Verifica che, dopo la rimozione di una traccia, le altre tracce
     * della playlist rimangano intatte.
     */
    @Test
    public void testRemoveTrackFromPlaylist_otherTracksUntouched()  {
        testPlaylistController.addTrackToPlaylist(secondTrack, currentPlaylist);

        testPlaylistController.removeTrackFromPlaylist(existingTrack, currentPlaylist);

        assertEquals(1, currentPlaylist.getTracks().size(),
                "Dopo la rimozione deve rimanere esattamente 1 traccia");
        assertTrue(currentPlaylist.getTracks().contains(secondTrack),
                "La traccia non rimossa deve ancora essere presente");
    }

    /**
     * Testa il tentativo di rimuovere una traccia che non è presente nella playlist.
     * Verifica che venga lanciata IllegalArgumentException e che
     * la playlist rimanga invariata.
     * 
     */
    @Test
    public void testRemoveTrackFromPlaylist_trackNotPresentThrowsException(){
        assertThrows(IllegalArgumentException.class, () ->
                testPlaylistController.removeTrackFromPlaylist(notInPlaylist, currentPlaylist),
                "Dovrebbe lanciare un'eccezione per traccia non presente nella playlist");

        assertEquals(1, currentPlaylist.getTracks().size(),
                "La playlist deve rimanere invariata");
    }

    /**
     * Testa il tentativo di rimuovere una traccia null dalla playlist.
     * Verifica che venga lanciata IllegalArgumentException.
     *
     */
    @Test
    public void testRemoveTrackFromPlaylist_nullTrackThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                testPlaylistController.removeTrackFromPlaylist(null, currentPlaylist),
                "Dovrebbe lanciare un'eccezione per traccia null");
    }

    /**
     * Verifica che il numero di tracce nella playlist sia correttamente
     * aggiornato dopo una sequenza di aggiunte e rimozioni.
     *
     */
    @Test
    public void testTrackCount_afterAddAndRemove() {
        testPlaylistController.addTrackToPlaylist(t2, currentPlaylist);
        testPlaylistController.addTrackToPlaylist(t3, currentPlaylist);
        assertEquals(3, currentPlaylist.getTracks().size(),
                "Dopo 2 aggiunte il contatore deve essere 3");

        testPlaylistController.removeTrackFromPlaylist(t2, currentPlaylist);
        assertEquals(2, currentPlaylist.getTracks().size(),
                "Dopo 1 rimozione il contatore deve essere 2");
    }

    /**
     * Verifica che un observer registrato sulla PlaylistLibrary
     * riceva la notifica quando la libreria viene modificata (nuova playlist creata).
     *
     */
    @Test
    public void testObserver_notifiedOnPlaylistLibraryChange() {
        int[] notificationCount = {0};
        PlaylistLibraryObserver testObserver = () -> notificationCount[0]++;

        testPlaylistLibrary.registerObserver(testObserver);

        testPlaylistController.createPlaylist("Seconda playlist");

        assertEquals(1, notificationCount[0],
                "L'observer deve ricevere esattamente 1 notifica dopo la modifica della libreria");

        testPlaylistLibrary.removeObserver(testObserver);
    }

    /**
     * Verifica che la playlist corrente abbia il nome corretto e contenga
     * esattamente la traccia inserita nel setUp().
     *
     */
    @Test
    public void testPlaylistInitialState() {
        assertEquals("La mia playlist", currentPlaylist.getName(),
                "Il nome della playlist deve corrispondere a quello impostato");
        assertEquals(1, currentPlaylist.getTracks().size(),
                "La playlist deve contenere la traccia aggiunta nel setUp");
        assertEquals(existingTrack, currentPlaylist.getTracks().get(0),
                "La traccia nella playlist deve essere quella inserita nel setUp");
    }

    
}
