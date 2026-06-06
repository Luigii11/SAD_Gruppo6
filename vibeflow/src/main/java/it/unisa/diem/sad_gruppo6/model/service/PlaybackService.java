/**
 * @file PlaybackService.java
 * @brief Servizio responsabile della gestione fisica del flusso audio.
 * @details Utilizza una 'Timeline' di JavaFX per simulare la riproduzione, avanzando
 * di un secondo alla volta. Interagisce con PlaybackState per aggiornare la UI e 
 * per gestire lo scorrimento automatico delle tracce tramite PlaylistIterator.
 * @pattern Service
 * @pattern Singleton
 * @author EmanuelChirico
 * @author ChiaraCrisci
 */
package it.unisa.diem.sad_gruppo6.model.service;

import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

import it.unisa.diem.sad_gruppo6.model.playback.states.PlaybackState;
import it.unisa.diem.sad_gruppo6.model.playback.iterators.PlaylistIterator;
import it.unisa.diem.sad_gruppo6.model.domain.Track;

public class PlaybackService {

    /* Attributi */
    private PlaybackState playbackState;
    private static PlaybackService instance;
    private Timeline timeline;

    /**
     * @brief Costruttore privato per implementare il pattern Singleton. 
     * @details Inizializza il riferimento allo stato di riproduzione.
     */
    private PlaybackService() {
        this.playbackState = PlaybackState.getInstance();  
    }
    
    /**
     * @brief Restituisce l'unica istanza di PlaybackService.
     * @pattern Singleton
     * @return L'istanza singleton di PlaybackService.
     */
    public static PlaybackService getInstance() {
        if (instance == null) {
            instance = new PlaybackService();
        }
        return instance;
    }

    /**
     * @brief Avvia il flusso audio simulato ripartendo dalla posizione corrente.
     * @details Ferma eventuali flussi attivi prima di avviarne uno nuovo.
     * Non avvia la Timeline se non è presente alcuna traccia in riproduzione.
     */
    public void start() {
        stop();
        if (playbackState.getCurrentTrack() == null) {
            return; // Nessuna traccia: non ha senso avviare il timer
        }
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> tick()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * @brief Ferma il flusso audio simulato (Timer JavaFX).
     * @details Viene chiamato quando si avvia un nuovo flusso, si mette in pausa 
     * la riproduzione manualmente, o quando la playlist giunge al termine.
     */
    public void stop() {
        if (timeline != null) {
            timeline.stop();
            timeline = null;
        }
    }

    /**
     * @brief Eseguito ogni secondo: aggiorna la posizione e gestisce l'auto-scorrimento.
     * @details Controlla se la traccia corrente è finita. Se non è finita, incrementa il
     * timer. Se è finita, utilizza l'iteratore (se disponibile) per passare alla traccia
     * successiva. Se non ci sono più tracce, ferma il player.
     *
     * @see PlaybackState#seekTo(int)
     * @see PlaybackState#getCurrentPosition()
     */
    private void tick() {
        Track currentTrack = playbackState.getCurrentTrack();
        if (currentTrack == null) {
            stop();
            return;
        }

        int currentPos = playbackState.getCurrentPosition();
        int totalDuration = currentTrack.getDuration();

        if (currentPos < totalDuration) {
            // La traccia è ancora in corso: avanza di 1 secondo
            playbackState.seekTo(currentPos + 1);
        } else {
            // La traccia corrente è terminata. Controlliamo se c'è un brano successivo.
            PlaylistIterator iterator = playbackState.getIterator();
            
            if (iterator != null && iterator.hasNext()) {
                // C'è un'altra traccia. Auto-scorrimento in avanti.
                Track nextTrack = iterator.next();
                playbackState.setCurrentTrack(nextTrack);
                playbackState.seekTo(0);
                // Non serve chiamare start(), la timeline è "INDEFINITE" e continuerà a ticchettare
            } else {
                // La playlist è finita. Nessun brano successivo.
                stop();
                playbackState.pause();
                playbackState.seekTo(0);
            }
        }
    }
}