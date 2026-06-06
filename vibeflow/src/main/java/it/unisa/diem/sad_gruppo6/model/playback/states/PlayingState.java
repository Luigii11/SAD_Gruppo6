/**
 * Classe concreta che rappresenta lo stato di riproduzione del player.
 * Implementa l'interfaccia 'PlayerState' e definisce il comportamento specifico per lo stato di riproduzione.
 * 
 * @pattern State
 * 
 * @author EmanuelChirico, LuigiAutorino
 */

package it.unisa.diem.sad_gruppo6.model.playback.states;
import it.unisa.diem.sad_gruppo6.model.playback.iterators.PlaylistIterator;

public class PlayingState implements PlayerState {

    /**
     * Restituisce il nome dello stato attuale del player, in questo caso "Playing".
     * @return "Playing" come nome dello stato attuale del player.
    */
    @Override
    public String getStatusName() {
        return "Playing";
    }

    /**
     * Gestisce l'azione di play quando il player è già in stato di riproduzione. 
     * In questo caso, non è necessario eseguire alcuna azione poiché il player è già in riproduzione.
     * 
     * @param ctx Il contesto del player
    */
   
    @Override
    public void play(PlaybackState ctx) {
        return;
    }

    /**
     * Gestisce l'azione di pausa quando il player è in stato di riproduzione. 
     * In questo caso, cambia lo stato del player a "PausedState" per mettere in pausa la riproduzione.
     * 
     * @param ctx Il contesto del player, utilizzato per cambiare lo stato del player a "PausedState".
    */

    @Override
    public void pause(PlaybackState ctx) {
        ctx.changeState(new PausedState());
    }

    /**
     * Esegue lo skip in avanti interrogando il PlaylistIterator.
     * Se è presente una traccia successiva, aggiorna la traccia corrente 
     * e resetta il contatore di riproduzione a 0.
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
     * Esegue lo skip all'indietro o il riavvio della traccia attuale.
     * Se la traccia in esecuzione ha superato i 10 secondi, viene semplicemente riavviata da 0.
     * Se si trova prima dei 10 secondi, tenta di caricare la traccia precedente dalla coda.
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
