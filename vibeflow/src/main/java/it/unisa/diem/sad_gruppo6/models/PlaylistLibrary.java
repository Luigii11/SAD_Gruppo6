package it.unisa.diem.sad_gruppo6.models;

import java.util.List;
import java.util.ArrayList;

public class PlaylistLibrary {
    // Attributi
    private List<Playlist> playlists;
    private List<PlaylistLibraryObserver> observers;

    // Metodo Costruttore
    public PlaylistLibrary() {
        this.playlists = new ArrayList<>();
        this.observers = new ArrayList<>();
    }

    // Metodi CRUD
    public void addPlaylist(Playlist p) {
        playlists.add(p);
        notifyObservers();
    }

    public void removePlaylist(Playlist p) {
        playlists.remove(p);
        notifyObservers();
    }

    public void updatePlaylist(Playlist p) {
        // Da implementare
        notifyObservers();
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    // Gestione Observer
    public void registerObserver(PlaylistLibraryObserver o) {
        observers.add(o);
    }

    public void removeObserver(PlaylistLibraryObserver o) {
        observers.remove(o);
    }

    private void notifyObservers() {
        for (PlaylistLibraryObserver observer : observers) {
            observer.onPlaylistLibraryChanged();
        }
    }

}

