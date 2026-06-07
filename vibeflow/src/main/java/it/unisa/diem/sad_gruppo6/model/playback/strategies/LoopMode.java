/**
 * @file LoopMode.java
 * @brief Implementazione concreta dell'interfaccia PlaybackMode per la
 *        modalità di riproduzione ciclica (Loop).
 *
 * @details Funge da Strategy concreta: quando viene richiesto un iteratore,
 *          produce un'istanza di LoopIterator, delegando ad essa
 *          tutta la logica di riproduzione circolare. 
 *
 * @pattern Strategy
 * @see PlaybackMode
 * @see LoopIterator
 * @author EmanuelaGraziuso
 */
package it.unisa.diem.sad_gruppo6.model.playback.strategies;

import it.unisa.diem.sad_gruppo6.model.domain.Track;
import it.unisa.diem.sad_gruppo6.model.playback.iterators.LoopIterator;
import it.unisa.diem.sad_gruppo6.model.playback.iterators.PlaylistIterator;

import java.util.List;

public class LoopMode implements PlaybackMode {

    /**
     * @brief Crea e restituisce un iteratore in modalità ciclica.
     *
     * @param tracks     La lista dei brani su cui applicare il loop.
     * @param startTrack La traccia attualmente in riproduzione al momento
     *                   dell'attivazione del loop; può essere null.
     * @return Un'istanza configurata di LoopIterator.
     */
    @Override
    public PlaylistIterator getIterator(List<Track> tracks, Track startTrack) {
        return new LoopIterator(tracks, startTrack);
    }
}