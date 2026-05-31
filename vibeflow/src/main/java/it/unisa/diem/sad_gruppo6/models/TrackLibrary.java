/**
 * @file TrackLibrary.java
 * Classe di definizione di un oggetto di tipo 'TrackLibrary', collezione di oggetti di tipo 'Track'.
 * 
 * @author EmanuelChirico
 */

package it.unisa.diem.sad_gruppo6.models;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class TrackLibrary 
{

    private static TrackLibrary instance;
    private LinkedHashSet<Track> tracks;
    private List<TrackLibraryObserver> observers;

    private TrackLibrary() 
    {
        tracks = new LinkedHashSet<>();
        observers = new ArrayList<>();
    }

    /**
      * Metodo per ottenere l'istanza singleton di TrackLibrary.
      * Se l'istanza non esiste, viene creata una nuova istanza.
      * 
      * @pattern Singleton
      * @return L'istanza singleton di TrackLibrary.
      * 
     */

    public static TrackLibrary getInstance() 
    {
        if (instance == null) 
        {
            instance = new TrackLibrary();
        }
        return instance;
    }

    /**
      * Metodo per aggiungere una traccia alla libreria.
      * 
      * @param track La traccia da aggiungere alla libreria.
     */
    public void addTrack(Track track) 
    {
        tracks.add(track); 
        notifyTrackAdded(track);
    }

     /**
      * Metodo per registrare un osservatore alla libreria.
      * 
      * @param Track TrackLibraryObserver da registrare alla libreria.
     */
    private void notifyTrackAdded(Track track) 
    {
        for (TrackLibraryObserver observer : observers) 
        {
            observer.onTrackAdded(track);
        }
    }

}
