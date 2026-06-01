/**
 * @file TrackLibrary.java
 * Classe di definizione di un oggetto di tipo 'TrackLibrary', collezione di oggetti di tipo 'Track'.
 * 
 * @author EmanuelChirico
 * @author EmanuelaGraziuso
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
        notifyObserver();
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

    /**
     * Restituisce la lista di tutte le tracce presenti nella libreria.

    * @return Lista tracce presenti nella libreria.
     */

    public List<Track> getTracks() 
    {
        return new ArrayList<>(tracks);
    }

    /**
     * Registra un nuovo observer che verrà notificato ad ogni modifica della libreria
     * 
     * @param o: L'observer da registrare.
     */
    public void registerObserver(TrackLibraryObserver o)
    {
        if(o!= null && !observers.contains(o)){
            observers.add(o);
        }
    }

    /**
     * Rimuove un observer precedentemente registrato.
     * 
     * @param o: L'observer da rimuovere.
     */
    public void removeObserver(TrackLibraryObserver o)
    {
        observers.remove(o);
    }

    /**
     * Notifica a tutti gli observer registrati che la libreria è stata modificata, in modo che possano 
     * aggiornare le loro viste di conseguenza.
     * 
     */
    private void notifyObserver()
    {
    for (TrackLibraryObserver observer : observers) 
            {
                observer.onLibraryChanged();
            }
    }
}
