package it.unisa.diem.sad_gruppo6.model.library;

import it.unisa.diem.sad_gruppo6.model.domain.Track;

/**
 * Interfaccia per l'osservazione delle modifiche alla TrackLibrary.
 * Le classi che implementano questa interfaccia vengono notificate ogni volta che la 
 * TrackLibrary subisce una modifica.
 * 
 * @author EmanuelChirico
 * @author EmanuelaGraziuso
 */
public interface TrackLibraryObserver 
{
    void onTrackAdded(Track track);


    /**
     * Le viste che mostrano l'intera libreria devono implementare questo metodo 
     * per aggiornarsi automaticamente.
     */
    void onLibraryChanged();

}