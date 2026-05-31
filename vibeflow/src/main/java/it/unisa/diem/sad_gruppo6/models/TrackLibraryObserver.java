package it.unisa.diem.sad_gruppo6.models;

/**
 * Interfaccia per l'osservazione delle modifiche alla TrackLibrary.
 * 
 * @author EmanuelChirico
 */
public interface TrackLibraryObserver 
{
    void onTrackAdded(Track track);

}