/**
 * Classe concreta che rappresenta lo stato di pausa del player.
 * Implementa l'interfaccia 'PlayerState' e definisce il comportamento specifico per lo stato di pausa.
 * 
 * @pattern State
 * 
 * @author EmanuelChirico, LuigiAutorino
 */

package it.unisa.diem.sad_gruppo6.model.playback.states;
import it.unisa.diem.sad_gruppo6.model.playback.iterators.PlaylistIterator;

public class PausedState implements PlayerState {

    /**
     * Restituisce il nome dello stato attuale del player, in questo caso "Paused".
     * @return "Paused" come nome dello stato attuale del player.
    */

    @Override
    public String getStatusName() {
        return "Paused";
    }

    /**
     * Gestisce l'azione di attivazione del player quando è in pausa. 
     * In questo caso, cambia lo stato del player a "PlayingState" per riprodurre la traccia.
     * 
     * @param ctx Il contesto del player, utilizzato per cambiare lo stato del player a "PlayingState".
    */

    @Override
    public void play(PlaybackState ctx) {
        ctx.changeState(new PlayingState());
    }

    /**
     * Gestisce l'azione di pause quando il player è già in stato di pausa. 
     * In questo caso, non è necessario eseguire alcuna azione poiché il player è già in pausa.
     * 
     * @param ctx Il contesto del player
    */

    @Override
    public void pause(PlaybackState ctx) {
        return;
    }
    
    /**
    * Esegue lo skip in avanti mentre è in pausa.
     * Cambia la traccia e la riporta a 0, ma NON avvia la riproduzione.
     * @param ctx Il contesto globale dello stato di riproduzione.
     */
    @Override
    public void next(PlaybackState ctx) {
        PlaylistIterator iterator = ctx.getIterator();
        if (iterator != null && iterator.hasNext()) {
            ctx.setCurrentTrack(iterator.next());
            ctx.seekTo(0);
        }
    }

    /**
     * Esegue lo skip all'indietro mentre è in pausa.
     * Applica la regola dei 10 secondi e resetta a 0, mantenendo lo stato di pausa.
     * @param ctx Il contesto globale dello stato di riproduzione.
     */
    @Override
    public void previous(PlaybackState ctx) {
        if (ctx.getCurrentPosition() >= 10) {
            ctx.seekTo(0);
        } else {
            PlaylistIterator iterator = ctx.getIterator();
            if (iterator != null && iterator.hasPrevious()) {
                ctx.setCurrentTrack(iterator.previous());
            }
            ctx.seekTo(0);
        }
    }
}
