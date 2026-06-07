/**
 * @file PlaybackController.java
 * @brief Controller di business responsabile della gestione globale della riproduzione audio.
 * @details Coordina l'interazione tra lo stato dell'applicazione (PlaybackState) e il servizio 
 * fisico di riproduzione (PlaybackService), garantendo che ci sia sempre un unico flusso audio attivo.
 * @see PlaybackState
 * @see PlaybackService
 * @author EmanuelChirico, LuigiAutorino, ChiaraCrisci
 */

package it.unisa.diem.sad_gruppo6.controller.business.playback;

import java.io.FileNotFoundException;
import java.util.List;

import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.playback.iterators.PlaylistIterator;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackState;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlayingState;
import it.unisa.diem.sad_gruppo6.model.playback.strategies.PlaybackMode;
import it.unisa.diem.sad_gruppo6.model.playback.strategies.SequentialMode;
import it.unisa.diem.sad_gruppo6.model.playback.strategies.ShuffleMode;
import it.unisa.diem.sad_gruppo6.model.service.PlaybackService;

public class PlaybackController {

    /* Attributi */
    private PlaybackState playbackState;
    private PlaybackService playbackService;

    /**
     * @brief Costruttore di default.
     * @details Recupera le istanze Singleton dello stato e del servizio di riproduzione.
     */
    public PlaybackController() {
        this.playbackState = PlaybackState.getInstance();
        this.playbackService = PlaybackService.getInstance();
    }

    /**
     * @brief Costruttore parametrizzato per Dependency Injection (usato nei test).
     * @param playbackState Lo stato logico della riproduzione da utilizzare.
     * @param playbackService Il gestore del flusso audio fisico da utilizzare.
     */
    public PlaybackController(PlaybackState playbackState, PlaybackService playbackService) {
        this.playbackState = playbackState;
        this.playbackService = playbackService;
    }

    /**
     * @brief Avvia la riproduzione di una singola traccia.
     * @param selectedTrack La traccia da riprodurre.
     * @throws FileNotFoundException Se il file audio della traccia non esiste.
     */
    public void play(Track selectedTrack) throws FileNotFoundException {
        if (selectedTrack == null) {
            throw new IllegalArgumentException("Track cannot be null.");
        }
        startPlayback(selectedTrack);
    }

    /**
     * @brief Avvia l'ascolto di un'intera playlist partendo dalla prima traccia.
     * @param p La playlist da riprodurre.
     * @throws FileNotFoundException Se il file audio della prima traccia non esiste.
     */
    public void play(Playlist p) throws FileNotFoundException {
        if (p == null || p.getTracks().isEmpty()) {
            throw new IllegalArgumentException("Empty playlist, impossible to play it.");
        }
        play(p, p.getTracks().get(0));
    }

    /**
     * @brief Avvia l'ascolto di una playlist partendo da una traccia specifica.
     * @param p La playlist da riprodurre come contesto.
     * @param startTrack La traccia da cui iniziare l'ascolto.
     * @throws FileNotFoundException Se il file audio della traccia non esiste.
     */
    public void play(Playlist p, Track startTrack) throws FileNotFoundException {
        if (p == null || p.getTracks().isEmpty()) {
            throw new IllegalArgumentException("Empty playlist, impossible to play.");
        }
        playbackState.setCurrentPlaylist(p);
        play(p.getTracks(), startTrack);
    }

    /**
     * @brief Avvia l'ascolto di una lista generica di brani partendo da uno specifico.
     * @details Salva la lista corrente in {@link PlaybackState} tramite
     *          {@code setCurrentTrackList}, così {@link #setMode(PlaybackMode)} può
     *          sempre ricostruire l'iteratore correttamente sia da Playlist che da
     *          TrackLibrary. Configura poi l'iteratore in base alla modalità attiva.
     * @param tracks La lista dei brani da usare come contesto.
     * @param startTrack La traccia da cui iniziare la riproduzione.
     * @throws FileNotFoundException Se il file audio della traccia non esiste.
     */
    public void play(List<Track> tracks, Track startTrack) throws FileNotFoundException {
        if (tracks == null || tracks.isEmpty()) {
            throw new IllegalArgumentException("Empty list, impossible to play it.");
        }
        playbackState.setCurrentTrackList(tracks);
        PlaylistIterator iterator = playbackState.getMode().getIterator(tracks, startTrack);
        playbackState.setIterator(iterator);
        startPlayback(startTrack);
    }

    /**
     * @brief Logica interna unificata per iniziare l'esecuzione di una traccia.
     * @details Interrompe ogni eventuale audio in corso, aggiorna la traccia corrente,
     *          commuta lo State logico su "Playing", avvia fisicamente il motore audio
     *          e registra il callback di fine traccia per l'auto-scorrimento.
     * @param t La traccia da avviare.
     * @throws FileNotFoundException Se il file audio della traccia non esiste.
     */
    private void startPlayback(Track t) throws FileNotFoundException {
        playbackService.stop();
        playbackState.setCurrentTrack(t);
        playbackState.seekTo(0);
        playbackState.changeState(new PlayingState());
        playbackService.start();
        playbackService.setOnEndOfTrack(() -> onTrackEnded());
    }

    /**
     * @brief Ferma temporaneamente la riproduzione corrente.
     * @details Trasmette il cambio di stato al pattern State e sospende il flusso audio.
     */
    public void pause() {
        playbackState.pause();
        playbackService.pause();
    }

    /**
     * @brief Riprende la riproduzione musicale precedentemente messa in pausa.
     */
    public void resume() {
        playbackState.play();
        playbackService.resume();
    }

    /**
     * @brief Salta al brano successivo nella playlist.
     * @details Interroga il PlaybackState per il cambio di traccia (che usa l'iteratore
     *          attivo, sia SequentialIterator che ShuffleIterator) e avvia il nuovo flusso audio.
     * @throws FileNotFoundException Se il file audio della traccia successiva non esiste.
     */
    public void next() throws FileNotFoundException {
        Track previousTrack = playbackState.getCurrentTrack();
        playbackState.next();
        Track currentTrack = playbackState.getCurrentTrack();

        if (currentTrack != null && currentTrack != previousTrack) {
            playbackService.start();
            playbackService.setOnEndOfTrack(() -> onTrackEnded());
        }
    }

    /**
     * @brief Torna al brano precedente nella playlist.
     * @throws FileNotFoundException Se il file audio della traccia precedente non esiste.
     */
    public void previous() throws FileNotFoundException {
        if (playbackState != null) {
            playbackState.previous();
        }
    }

    /**
     * @brief Imposta la modalità di riproduzione attiva e aggiorna l'iteratore corrente.
     *
     * @details Sostituisce la {@link PlaybackMode} nel {@link PlaybackState} e ricrea
     *          immediatamente l'iteratore a partire dalla traccia attualmente in ascolto,
     *          così la modalità ha effetto dal brano successivo senza interrompere né
     *          riavviare la riproduzione corrente. Usa {@code currentTrackList} (sempre
     *          aggiornata all'avvio) per funzionare sia con Playlist che con TrackLibrary.
     *          L'ordine originale della playlist non viene mai alterato (AC3, AC5).
     *
     * @param mode La nuova {@link PlaybackMode} da adottare
     *             (es. {@link ShuffleMode} o {@link SequentialMode}).
     * @author ChiaraCrisci
     */
    public void setMode(PlaybackMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("PlaybackMode cannot be null.");
        }

        playbackState.setMode(mode);

        List<Track> tracks = playbackState.getCurrentTrackList();
        Track currentTrack = playbackState.getCurrentTrack();

        if (tracks != null && !tracks.isEmpty()) {
            PlaylistIterator newIterator = mode.getIterator(tracks, currentTrack);
            playbackState.setIterator(newIterator);
        }
    }

    /**
     * @brief Callback invocata automaticamente alla fine della traccia corrente.
     * @details Interroga l'iteratore attivo (Sequential o Shuffle) tramite
     *          {@code playbackState.next()} per determinare il brano successivo
     *          e ne avvia la riproduzione. Se non ci sono più tracce, il player si ferma.
     */
    private void onTrackEnded() {
        Track previousTrack = playbackState.getCurrentTrack();
        playbackState.next();
        Track currentTrack = playbackState.getCurrentTrack();

        if (currentTrack != null && currentTrack != previousTrack) {
            try {
                playbackService.start();
                playbackService.setOnEndOfTrack(() -> onTrackEnded());
            } catch (FileNotFoundException e) {
                System.err.println("Impossibile riprodurre la traccia: " + e.getMessage());
            }
        }
    }
}