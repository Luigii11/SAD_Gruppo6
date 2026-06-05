/**
 * @file PlaybackController.java
 * @brief Controller di business responsabile della gestione globale della riproduzione audio.
 * @details Coordina l'interazione tra lo stato dell'applicazione (PlaybackState) e il servizio 
 * fisico di riproduzione (PlaybackService), garantendo che ci sia sempre un unico flusso audio attivo.
 * @see PlaybackState
 * @see PlaybackService
 * @author EmanuelChirico, LuigiAutorino
 */

package it.unisa.diem.sad_gruppo6.controller.business.playback;

import it.unisa.diem.sad_gruppo6.model.domain.Playlist;
import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackState;
import it.unisa.diem.sad_gruppo6.model.playback.states.PlayingState;
import it.unisa.diem.sad_gruppo6.model.service.PlaybackService;

public class PlaybackController {

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
     * @brief Costruttore parametrizzato per il Dependency Injection (usato principalmente nei Test).
     * @param playbackState Lo stato logico della riproduzione da utilizzare.
     * @param playbackService Il gestore del flusso audio fisico da utilizzare.
     */
    public PlaybackController(PlaybackState playbackState, PlaybackService playbackService) {
        this.playbackState = playbackState;
        this.playbackService = playbackService;
    }

    /**
     * @brief Avvia l'ascolto di un'intera playlist partendo dalla prima traccia.
     * @param p La playlist da riprodurre.
     * @throws IllegalArgumentException Se la playlist passata risulta vuota.
     */
    public void play(Playlist p) {
        if (p.getTracks().isEmpty()) {
            throw new IllegalArgumentException("La playlist è vuota, impossibile avviare la riproduzione.");
        }
        playbackState.setCurrentPlaylist(p);
        Track first = p.getTracks().get(0); 
        startPlayback(first);
    }

    /**
     * @brief Avvia l'ascolto di un singolo brano specifico.
     * @param t La traccia musicale da riprodurre.
     */
    public void play(Track t) {
        startPlayback(t);
    }

    /**
     * @brief Logica interna unificata per iniziare l'esecuzione di una traccia.
     * @details Interrompe ogni eventuale audio in corso, aggiorna la traccia corrente nel Singleton,
     * commuta lo State logico su "Playing" e avvia fisicamente il motore di riproduzione.
     * @param t La traccia da cui avviare il flusso.
     */
    private void startPlayback(Track t) {
        playbackService.stop();                                  
        playbackState.setCurrentTrack(t);                
        playbackState.changeState(new PlayingState());   
        playbackService.start();                                 
    }

    /**
     * @brief Ferma temporaneamente la riproduzione corrente.
     * @details Trasmette il cambio di stato al pattern State logico e interrompe il flusso audio.
     */
    public void pause() {
        playbackState.pause();
        playbackService.stop();
    }

    /**
     * @brief Riprende la riproduzione musicale precedentemente messa in pausa.
     */
    public void resume() {
        playbackState.play();        
        playbackService.start();     
    }

    /**
     * @brief Salta al brano successivo nella playlist.
     */
    public void next() {
        if (playbackState != null) {
            playbackState.next(); 
        }
    }

    /**
     * @brief Torna al brano precedente nella playlist.
     */
    public void previous() {
        if (playbackState != null) {
            playbackState.previous();
        }
    }
}