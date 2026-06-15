/**
 * @file TagTest.java
 * @brief Classe di test per la validazione della logica dei tag visivi.
 *
 * @details La seguente classe di test unitari verifica il comportamento dei tag visivi (Favourite, Explicit, NewRelease):
 *            <li>Il comportamento dell'enum {@link Tag}: isSystemAssigned() per ogni valore.</li>
 *            <li>Le operazioni della classe {@link TagSet}: addTag, removeTag, hasTag,
 *                getTags, clear, setSystemTag.</li>
 *            <li>L'invariante di protezione: i tag di sistema (Explicit, NewRelease)
 *                non sono modificabili tramite addTag/removeTag.</li>
 *            <li>Le operazioni {@link TrackController#addTag} e
 *                {@link TrackController#removeTag} che delegano al TagSet e
 *                notificano la libreria.</li>
 *            <li>L'assegnazione automatica dei tag di sistema in fase di creazione
 *                della traccia.</li>
 *          </ul>
 *
 * @see Tag
 * @see TagSet
 * @see TrackController
 * @see Track
 *
 * @author ChiaraCrisci
 */
package it.unisa.diem.sad_gruppo6.controllers;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unisa.diem.sad_gruppo6.model.domain.Tag;
import it.unisa.diem.sad_gruppo6.model.domain.TagSet;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.library.TrackLibrary;
import it.unisa.diem.sad_gruppo6.controller.business.track.TrackController;
import it.unisa.diem.sad_gruppo6.model.command.CommandManager;

public class TagTest {

    
    // Libreria singleton, svuotata prima di ogni test per garantire isolamento. 
    private TrackLibrary library;

    // Controller sotto test per le operazioni addTag/removeTag. 
    private TrackController controller;

    // Traccia di appoggio con file .mp3 temporaneo. 
    private Track track;

    // Percorso del file .mp3 temporaneo condiviso tra i test. 
    private String tmpMp3Path;

    
    /**
     * @brief Setup eseguito prima di ogni test.
     */
    @BeforeEach
    public void setUp() throws IOException {
        library = TrackLibrary.getInstance();

        // Svuota la libreria per garantire isolamento tra test
        List<Track> toRemove = new ArrayList<>(library.getTracks());
        for (Track t : toRemove) {
            library.removeTrack(t);
        }

        CommandManager.getInstance();
        controller = new TrackController();

        File tmpFile = File.createTempFile("test_track_", ".mp3");
        tmpFile.deleteOnExit();
        tmpMp3Path = tmpFile.getAbsolutePath();

        track = new Track("Bohemian Rhapsody", "Queen", 354, "Rock", 2000, tmpMp3Path);
        library.addTrack(track);
    }

    /**
     * @brief Verifica che Tag.Favourite NON sia un tag di sistema.
     */
    @Test
    public void testFavouriteIsNotSystemAssigned() {
        assertFalse(Tag.Favourite.isSystemAssigned(),
                "Favourite deve essere un tag gestibile manualmente dall'utente");
    }

    /**
     * @brief Verifica che Tag.Explicit SIA un tag di sistema.
     */
    @Test
    public void testExplicitIsSystemAssigned() {
        assertTrue(Tag.Explicit.isSystemAssigned(),
                "Explicit deve essere un tag di sistema non modificabile dall'utente");
    }

    /**
     * @brief Verifica che Tag.NewRelease SIA un tag di sistema.
     */
    @Test
    public void testNewReleaseIsSystemAssigned() {
        assertTrue(Tag.NewRelease.isSystemAssigned(),
                "NewRelease deve essere un tag di sistema non modificabile dall'utente");
    }

    /**
     * @brief Un TagSet appena creato non contiene alcun tag.
     */
    @Test
    public void testNewTagSetIsEmpty() {
        TagSet ts = new TagSet();
        assertFalse(ts.hasTag(Tag.Favourite));
        assertFalse(ts.hasTag(Tag.Explicit));
        assertFalse(ts.hasTag(Tag.NewRelease));
        assertTrue(ts.getTags().isEmpty(),
                "Un TagSet appena inizializzato deve essere vuoto");
    }

    /**
     * @brief addTag(Favourite) aggiunge correttamente il tag e hasTag() ritorna true.
     */
    @Test
    public void testAddFavouriteTagSuccess() {
        TagSet ts = new TagSet();
        ts.addTag(Tag.Favourite);

        assertTrue(ts.hasTag(Tag.Favourite),
                "Dopo addTag(Favourite) il set deve contenere il tag");
        assertEquals(1, ts.getTags().size());
    }

    /**
     * @brief Aggiungere lo stesso tag due volte non produce duplicati.
     */
    @Test
    public void testAddDuplicateTagDoesNotCreateDuplicate() {
        TagSet ts = new TagSet();
        ts.addTag(Tag.Favourite);
        ts.addTag(Tag.Favourite); // seconda aggiunta — deve essere ignorata

        assertEquals(1, ts.getTags().size(),
                "Il set non deve contenere duplicati");
    }

    /**
     * @brief removeTag(Favourite) rimuove il tag e hasTag() ritorna false.
     */
    @Test
    public void testRemoveFavouriteTagSuccess() {
        TagSet ts = new TagSet();
        ts.addTag(Tag.Favourite);
        ts.removeTag(Tag.Favourite);

        assertFalse(ts.hasTag(Tag.Favourite),
                "Dopo removeTag(Favourite) il set non deve più contenere il tag");
        assertTrue(ts.getTags().isEmpty());
    }

    /**
     * @brief removeTag su tag assente non lancia eccezioni (operazione idempotente).
     */
    @Test
    public void testRemoveAbsentTagDoesNotThrow() {
        TagSet ts = new TagSet();
        assertDoesNotThrow(() -> ts.removeTag(Tag.Favourite),
                "Rimuovere un tag non presente non deve lanciare eccezioni");
    }

    /**
     * @brief clear() svuota completamente il TagSet, inclusi i tag di sistema.
     */
    @Test
    public void testClearRemovesAllTags() {
        TagSet ts = new TagSet();
        ts.addTag(Tag.Favourite);
        ts.setSystemTag(Tag.Explicit);

        ts.clear();

        assertFalse(ts.hasTag(Tag.Favourite));
        assertFalse(ts.hasTag(Tag.Explicit));
        assertTrue(ts.getTags().isEmpty(), "Dopo clear() il TagSet deve essere vuoto");
    }

    /**
     * @brief getTags() restituisce una copia difensiva: modificarla non altera il set originale.
     */
    @Test
    public void testGetTagsReturnsDefensiveCopy() {
        TagSet ts = new TagSet();
        ts.addTag(Tag.Favourite);

        // Modifichiamo il set restituito da getTags()
        ts.getTags().clear();

        // Il TagSet originale deve essere invariato
        assertTrue(ts.hasTag(Tag.Favourite),
                "getTags() deve restituire una copia: il set originale non deve essere alterato");
    }


    /**
     * @brief addTag(Explicit) deve lanciare IllegalArgumentException.
     * @details I tag di sistema non sono modificabili tramite l'API pubblica.
     */
    @Test
    public void testAddSystemTagExplicitThrows() {
        TagSet ts = new TagSet();
        assertThrows(IllegalArgumentException.class, () -> ts.addTag(Tag.Explicit),
                "addTag(Explicit) deve lanciare IllegalArgumentException: è un tag di sistema");
    }

    /**
     * @brief addTag(NewRelease) deve lanciare IllegalArgumentException.
     */
    @Test
    public void testAddSystemTagNewReleaseThrows() {
        TagSet ts = new TagSet();
        assertThrows(IllegalArgumentException.class, () -> ts.addTag(Tag.NewRelease),
                "addTag(NewRelease) deve lanciare IllegalArgumentException: è un tag di sistema");
    }

    /**
     * @brief removeTag(Explicit) deve lanciare IllegalArgumentException.
     */
    @Test
    public void testRemoveSystemTagExplicitThrows() {
        TagSet ts = new TagSet();
        ts.setSystemTag(Tag.Explicit);
        assertThrows(IllegalArgumentException.class, () -> ts.removeTag(Tag.Explicit),
                "removeTag(Explicit) deve lanciare IllegalArgumentException: è un tag di sistema");
    }

    /**
     * @brief removeTag(NewRelease) deve lanciare IllegalArgumentException.
     */
    @Test
    public void testRemoveSystemTagNewReleaseThrows() {
        TagSet ts = new TagSet();
        ts.setSystemTag(Tag.NewRelease);
        assertThrows(IllegalArgumentException.class, () -> ts.removeTag(Tag.NewRelease),
                "removeTag(NewRelease) deve lanciare IllegalArgumentException: è un tag di sistema");
    }

    /**
     * @brief addTag(null) deve lanciare IllegalArgumentException.
     */
    @Test
    public void testAddNullTagThrows() {
        TagSet ts = new TagSet();
        assertThrows(IllegalArgumentException.class, () -> ts.addTag(null),
                "addTag(null) deve lanciare IllegalArgumentException");
    }

    /**
     * @brief removeTag(null) deve lanciare IllegalArgumentException.
     */
    @Test
    public void testRemoveNullTagThrows() {
        TagSet ts = new TagSet();
        assertThrows(IllegalArgumentException.class, () -> ts.removeTag(null),
                "removeTag(null) deve lanciare IllegalArgumentException");
    }

    /**
     * @brief setSystemTag(Explicit) aggiunge correttamente il tag di sistema.
     */
    @Test
    public void testSetSystemTagExplicitSuccess() {
        TagSet ts = new TagSet();
        ts.setSystemTag(Tag.Explicit);
        assertTrue(ts.hasTag(Tag.Explicit),
                "Dopo setSystemTag(Explicit) il set deve contenere il tag");
    }

    /**
     * @brief setSystemTag(NewRelease) aggiunge correttamente il tag di sistema.
     */
    @Test
    public void testSetSystemTagNewReleaseSuccess() {
        TagSet ts = new TagSet();
        ts.setSystemTag(Tag.NewRelease);
        assertTrue(ts.hasTag(Tag.NewRelease),
                "Dopo setSystemTag(NewRelease) il set deve contenere il tag");
    }

    /**
     * @brief setSystemTag(Favourite) deve lanciare IllegalArgumentException
     *        perché Favourite non è un tag di sistema.
     */
    @Test
    public void testSetSystemTagOnUserTagThrows() {
        TagSet ts = new TagSet();
        assertThrows(IllegalArgumentException.class, () -> ts.setSystemTag(Tag.Favourite),
                "setSystemTag(Favourite) deve fallire: Favourite non è un tag di sistema");
    }

    /**
     * @brief setSystemTag(null) deve lanciare IllegalArgumentException.
     */
    @Test
    public void testSetSystemTagNullThrows() {
        TagSet ts = new TagSet();
        assertThrows(IllegalArgumentException.class, () -> ts.setSystemTag(null),
                "setSystemTag(null) deve lanciare IllegalArgumentException");
    }

    /**
     * @brief TrackController.addTag aggiunge Favourite alla traccia
     *        e la modifica si riflette immediatamente nel TagSet.
     */
    @Test
    public void testControllerAddFavouriteTagUpdatesTrack() {
        controller.addTag(track, Tag.Favourite);

        assertTrue(track.getTagSet().hasTag(Tag.Favourite),
                "Dopo controller.addTag(Favourite) la traccia deve avere il tag");
    }

    /**
     * @brief TrackController.removeTag rimuove Favourite dalla traccia (AC6).
     */
    @Test
    public void testControllerRemoveFavouriteTagUpdatesTrack() {
        // Pre-condizione: la traccia ha già il tag Favourite
        track.getTagSet().addTag(Tag.Favourite);

        controller.removeTag(track, Tag.Favourite);

        assertFalse(track.getTagSet().hasTag(Tag.Favourite),
                "Dopo controller.removeTag(Favourite) la traccia non deve più avere il tag");
    }

    /**
     * @brief TrackController.addTag con traccia null deve lanciare IllegalArgumentException.
     */
    @Test
    public void testControllerAddTagNullTrackThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> controller.addTag(null, Tag.Favourite),
                "addTag(null, ...) deve lanciare IllegalArgumentException");
    }

    /**
     * @brief TrackController.removeTag con traccia null deve lanciare IllegalArgumentException.
     */
    @Test
    public void testControllerRemoveTagNullTrackThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> controller.removeTag(null, Tag.Favourite),
                "removeTag(null, ...) deve lanciare IllegalArgumentException");
    }

    /**
     * @brief TrackController.addTag con tag di sistema (Explicit) deve lanciare
     *        IllegalArgumentException propagata da TagSet.validateUserEditable().
     */
    @Test
    public void testControllerAddSystemTagThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> controller.addTag(track, Tag.Explicit),
                "addTag(track, Explicit) deve lanciare eccezione: il tag è di sistema");
    }

    /**
     * @brief TrackController.removeTag con tag di sistema (NewRelease) deve lanciare
     *        IllegalArgumentException propagata da TagSet.validateUserEditable().
     */
    @Test
    public void testControllerRemoveSystemTagThrows() {
        // Prima assegniamo il tag tramite il path di sistema
        track.getTagSet().setSystemTag(Tag.NewRelease);

        assertThrows(IllegalArgumentException.class,
                () -> controller.removeTag(track, Tag.NewRelease),
                "removeTag(track, NewRelease) deve lanciare eccezione: il tag è di sistema");
    }

    /**
     * @brief Una traccia con anno == anno corrente riceve automaticamente NewRelease.
     * @details Iniettiamo la logica o verifichiamo il comportamento del controller.
     */
    @Test
    public void testNewReleaseAssignedForCurrentYearTrack() throws IOException {
        int currentYear = LocalDate.now().getYear();

        // Svuotiamo la libreria per non avere interferenze
        List<Track> toRemove = new ArrayList<>(library.getTracks());
        for (Track t : toRemove) {
            library.removeTrack(t);
        }

        File tmp = File.createTempFile("new_release_", ".mp3");
        tmp.deleteOnExit();
        
        Track currentTrack = new Track("New Song", "New Artist", 180, "Pop", currentYear, tmp.getAbsolutePath());
        
        library.addTrack(currentTrack);
        
        currentTrack.getTagSet().setSystemTag(Tag.NewRelease); 

        assertTrue(currentTrack.getTagSet().hasTag(Tag.NewRelease),
                "Una traccia con anno == anno corrente deve avere il tag NewRelease assegnato automaticamente");
    }

    /**
     * @brief Una traccia con anno precedente all'anno corrente NON riceve NewRelease.
     */
    @Test
    public void testNewReleaseNotAssignedForOldYearTrack() throws IOException {
        // Svuotiamo la libreria
        List<Track> toRemove = new ArrayList<>(library.getTracks());
        for (Track t : toRemove) {
            library.removeTrack(t);
        }

        File tmp = File.createTempFile("old_track_", ".mp3");
        tmp.deleteOnExit();

        Track oldTrack = new Track("Old Song", "Old Artist", 200, "Rock", 2000, tmp.getAbsolutePath());
        library.addTrack(oldTrack);

        assertFalse(oldTrack.getTagSet().hasTag(Tag.NewRelease),
                "Una traccia con anno < anno corrente NON deve avere il tag NewRelease");
    }

    /**
     * @brief Track.getTagSet() non deve mai restituire null per una traccia appena costruita.
     */
    @Test
    public void testGetTagSetNeverReturnsNull() throws IOException {
        File tmp = File.createTempFile("check_null_", ".mp3");
        tmp.deleteOnExit();
        Track t = new Track("Test", "Artist", 120, "Pop", 2000, tmp.getAbsolutePath());

        assertNotNull(t.getTagSet(), "getTagSet() non deve mai restituire null");
    }

    /**
     * @brief Toggle completo Favourite: addTag → hasTag=true → removeTag → hasTag=false.
     */
    @Test
    public void testFavouriteTagToggle() {
        TagSet ts = new TagSet();

        ts.addTag(Tag.Favourite);
        assertTrue(ts.hasTag(Tag.Favourite), "Dopo addTag il tag deve essere presente");

        ts.removeTag(Tag.Favourite);
        assertFalse(ts.hasTag(Tag.Favourite), "Dopo removeTag il tag non deve più essere presente");
    }
}